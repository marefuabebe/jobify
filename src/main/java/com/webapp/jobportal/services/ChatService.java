package com.webapp.jobportal.services;

import com.webapp.jobportal.entity.ChatMessage;
import com.webapp.jobportal.entity.JobPostActivity;
import com.webapp.jobportal.entity.Users;
import com.webapp.jobportal.repository.ChatMessageRepository;
import com.webapp.jobportal.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationService notificationService;
    private final UsersRepository usersRepository;
    private final UsersService usersService;

    @Autowired
    public ChatService(ChatMessageRepository chatMessageRepository,
            SimpMessagingTemplate messagingTemplate,
            NotificationService notificationService,
            UsersRepository usersRepository,
            UsersService usersService) {
        this.chatMessageRepository = chatMessageRepository;
        this.messagingTemplate = messagingTemplate;
        this.notificationService = notificationService;
        this.usersRepository = usersRepository;
        this.usersService = usersService;
    }

    public List<Users> getVerifiedChatPartners(Users currentUser) {
        // Fail-safe approach: Get ALL users and filter in memory using strict
        // verification
        int currentTypeId = currentUser.getUserTypeId().getUserTypeId();

        List<Users> allUsers = usersRepository.findAll();

        if (currentTypeId == 1) { // Client -> show Verified Freelancers (Type 2)
            return allUsers.stream()
                    .filter(u -> u.getUserTypeId().getUserTypeId() == 2)
                    .filter(u -> u.getUserId() != currentUser.getUserId())
                    .filter(u -> usersService.isUserFullyVerified(u)) // Strict check
                    .collect(java.util.stream.Collectors.toList());
        } else if (currentTypeId == 2) { // Freelancer -> show Verified Clients (Type 1)
            return allUsers.stream()
                    .filter(u -> u.getUserTypeId().getUserTypeId() == 1)
                    .filter(u -> u.getUserId() != currentUser.getUserId())
                    .filter(u -> usersService.isUserFullyVerified(u)) // Strict check
                    .collect(java.util.stream.Collectors.toList());
        }

        return List.of();
    }

    public ChatMessage saveMessage(ChatMessage chatMessage) {
        chatMessage.setTimestamp(new Date());
        chatMessage.setIsRead(false);
        ChatMessage saved = chatMessageRepository.save(chatMessage);

        // Send real-time message to receiver using EMAIL as Principal
        // This acts as the centralized sending logic for both HTTP and WS controller
        // methods
        if (saved.getReceiverId() != null && saved.getReceiverId().getEmail() != null) {
            // Convert to DTO to avoid serialization issues (Circular references, Lazy
            // loading)
            com.webapp.jobportal.dto.RealtimeChatDTO dto = new com.webapp.jobportal.dto.RealtimeChatDTO();
            dto.setId(saved.getId());
            dto.setMessage(saved.getMessage());
            dto.setTimestamp(saved.getTimestamp());
            dto.setIsRead(saved.getIsRead());

            dto.setSenderId(new com.webapp.jobportal.dto.RealtimeChatDTO.ChatUserDTO(
                    saved.getSenderId().getUserId(),
                    saved.getSenderId().getEmail()));

            dto.setReceiverId(new com.webapp.jobportal.dto.RealtimeChatDTO.ChatUserDTO(
                    saved.getReceiverId().getUserId(),
                    saved.getReceiverId().getEmail()));

            if (saved.getJobId() != null) {
                dto.setJobId(saved.getJobId().getJobPostId());
            }

            dto.setAttachmentPath(saved.getAttachmentPath());
            dto.setAttachmentType(saved.getAttachmentType());

            // USE TOPIC-BASED ROUTING (Specific to Receiver ID)
            // This avoids Principal/User mismatch issues entirely
            messagingTemplate.convertAndSend(
                    "/topic/messages/" + saved.getReceiverId().getUserId(),
                    dto);

            // Also send to Sender to confirm delivery and sync other devices
            messagingTemplate.convertAndSend(
                    "/topic/messages/" + saved.getSenderId().getUserId(),
                    dto);

            // Create notification for receiver
            notificationService.createNotification(
                    chatMessage.getReceiverId(),
                    "New Message",
                    "You have a new message from " + getSenderName(chatMessage.getSenderId()),
                    "MESSAGE",
                    chatMessage.getId());
        }

        return saved;
    }

    public List<ChatMessage> getConversation(Users user1, Users user2, JobPostActivity job) {
        if (job != null) {
            return chatMessageRepository.findConversationBetweenUsersForJob(user1, user2, job);
        }
        return chatMessageRepository.findConversationBetweenUsers(user1, user2);
    }

    public List<ChatMessage> getUnreadMessages(Users user) {
        return chatMessageRepository.findByReceiverIdAndIsReadFalse(user);
    }

    public void markAsRead(Integer messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setIsRead(true);
        chatMessageRepository.save(message);
    }

    public void markConversationAsRead(Users sender, Users receiver) {
        List<ChatMessage> messages = chatMessageRepository.findBySenderIdAndReceiverIdOrderByTimestampAsc(sender,
                receiver);
        messages.forEach(msg -> {
            boolean wasUnread = !msg.getIsRead();
            if (wasUnread) {
                msg.setIsRead(true);
                chatMessageRepository.save(msg);
                broadcastUpdate(msg);
            }
        });
    }

    public List<Users> getChatPartners(Users user) {
        return chatMessageRepository.findChatPartners(user);
    }

    private String getSenderName(Users user) {
        return usersService.getUserFullName(user);
    }

    public ChatMessage editMessage(Integer messageId, String newContent) {
        ChatMessage message = chatMessageRepository.findById(messageId).orElse(null);
        if (message != null) {
            message.setMessage(newContent);
            message.setIsEdited(true);
            chatMessageRepository.save(message);
            broadcastUpdate(message);
        }
        return message;
    }

    public ChatMessage deleteMessage(Integer messageId) {
        ChatMessage message = chatMessageRepository.findById(messageId).orElse(null);
        if (message != null) {
            message.setIsDeleted(true);
            message.setMessage("This message was deleted");
            message.setAttachmentPath(null); // Clear attachment
            message.setAttachmentType(null);
            chatMessageRepository.save(message);
            broadcastUpdate(message);
        }
        return message;
    }

    private void broadcastUpdate(ChatMessage message) {
        com.webapp.jobportal.dto.RealtimeChatDTO dto = new com.webapp.jobportal.dto.RealtimeChatDTO();
        dto.setId(message.getId());
        dto.setMessage(message.getMessage());
        dto.setTimestamp(message.getTimestamp());
        dto.setIsRead(message.getIsRead());
        dto.setIsEdited(message.getIsEdited());
        dto.setIsDeleted(message.getIsDeleted());

        dto.setSenderId(new com.webapp.jobportal.dto.RealtimeChatDTO.ChatUserDTO(
                message.getSenderId().getUserId(),
                message.getSenderId().getEmail()));

        dto.setReceiverId(new com.webapp.jobportal.dto.RealtimeChatDTO.ChatUserDTO(
                message.getReceiverId().getUserId(),
                message.getReceiverId().getEmail()));

        if (message.getJobId() != null) {
            dto.setJobId(message.getJobId().getJobPostId());
        }

        dto.setAttachmentPath(message.getAttachmentPath());
        dto.setAttachmentType(message.getAttachmentType());

        // Notify Receiver
        messagingTemplate.convertAndSend(
                "/topic/messages/" + message.getReceiverId().getUserId(),
                dto);

        // Notify Sender
        messagingTemplate.convertAndSend(
                "/topic/messages/" + message.getSenderId().getUserId(),
                dto);
    }

    public java.util.List<com.webapp.jobportal.dto.ChatConversationDTO> getConversations(Users currentUser) {
        List<Users> partners = getVerifiedChatPartners(currentUser);
        java.util.List<com.webapp.jobportal.dto.ChatConversationDTO> conversations = new java.util.ArrayList<>();

        for (Users partner : partners) {
            String lastMsg = "";
            Date time = null;
            int unread = chatMessageRepository.countUnreadMessages(partner, currentUser);

            List<ChatMessage> history = chatMessageRepository.findLastMessageBetweenUsers(currentUser, partner);
            if (!history.isEmpty()) {
                ChatMessage latest = history.get(0);
                lastMsg = latest.getMessage();
                time = latest.getTimestamp();
                if (lastMsg.length() > 30) {
                    lastMsg = lastMsg.substring(0, 27) + "...";
                }
            }

            conversations.add(new com.webapp.jobportal.dto.ChatConversationDTO(partner, lastMsg, time, unread));
        }

        // Populate photosImagePath for each conversation
        for (com.webapp.jobportal.dto.ChatConversationDTO conv : conversations) {
            conv.setPhotosImagePath(usersService.getUserProfileImagePath(conv.getChatPartner()));
        }

        return conversations;
    }
}
