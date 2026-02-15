package com.webapp.jobportal.services;

import com.webapp.jobportal.entity.Notification;
import com.webapp.jobportal.entity.Users;
import com.webapp.jobportal.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public NotificationService(NotificationRepository notificationRepository,
            SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.messagingTemplate = messagingTemplate;
    }

    public Notification createNotification(Users user, String title, String message, String type, Integer relatedId) {
        Notification notification = new Notification();
        notification.setUserId(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRelatedId(relatedId);
        notification.setIsRead(false);
        notification.setCreatedAt(new Date());

        Notification saved = notificationRepository.save(notification);

        // Send real-time notification to user
        messagingTemplate.convertAndSendToUser(
                String.valueOf(user.getUserId()),
                "/queue/notifications",
                saved);

        return saved;
    }

    public List<Notification> getUserNotifications(Users user) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user);
    }

    public List<Notification> getUnreadNotifications(Users user) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(Users user) {
        return notificationRepository.countByUserIdAndIsReadFalse(user);
    }

    public List<Notification> getRecentNotifications(Users user, int limit) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user).stream()
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    public void markAsRead(Integer notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        notification.setIsRead(true);
        notificationRepository.save(notification);
    }

    public void markAllAsRead(Users user) {
        List<Notification> notifications = notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(user);
        notifications.forEach(notification -> {
            notification.setIsRead(true);
            notificationRepository.save(notification);
        });
    }

    public void deleteNotification(Integer notificationId) {
        notificationRepository.deleteById(notificationId);
    }

    public void createAdminNotification(String userEmail, String userType) {
        // Create notification for admin user (assuming admin has userId = 1)
        Users adminUser = new Users();
        adminUser.setUserId(1); // Admin user ID

        createNotification(
                adminUser,
                "New User Verification Required",
                "A new " + userType + " (" + userEmail
                        + ") has uploaded verification documents and is ready for review.",
                "VERIFICATION",
                null);
    }

    public Notification getOne(Integer id) {
        return notificationRepository.findById(id).orElse(null);
    }
}
