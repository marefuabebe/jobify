package com.webapp.jobportal.repository;

import com.webapp.jobportal.entity.ChatMessage;
import com.webapp.jobportal.entity.JobPostActivity;
import com.webapp.jobportal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {

    List<ChatMessage> findBySenderIdAndReceiverIdOrderByTimestampAsc(Users senderId, Users receiverId);

    List<ChatMessage> findByJobIdOrderByTimestampAsc(JobPostActivity jobId);

    // Filter out deleted messages
    @Query("SELECT cm FROM ChatMessage cm WHERE ((cm.senderId = :user1 AND cm.receiverId = :user2) OR (cm.senderId = :user2 AND cm.receiverId = :user1)) AND (cm.isDeleted IS NULL OR cm.isDeleted = false) ORDER BY cm.timestamp ASC")
    List<ChatMessage> findConversationBetweenUsers(@Param("user1") Users user1, @Param("user2") Users user2);

    @Query("SELECT cm FROM ChatMessage cm WHERE ((cm.senderId = :user1 AND cm.receiverId = :user2) OR (cm.senderId = :user2 AND cm.receiverId = :user1)) AND cm.jobId = :job AND (cm.isDeleted IS NULL OR cm.isDeleted = false) ORDER BY cm.timestamp ASC")
    List<ChatMessage> findConversationBetweenUsersForJob(@Param("user1") Users user1, @Param("user2") Users user2,
            @Param("job") JobPostActivity job);

    List<ChatMessage> findByReceiverIdAndIsReadFalse(Users receiverId);

    @Query("SELECT DISTINCT cm.senderId FROM ChatMessage cm WHERE cm.receiverId = :user UNION SELECT DISTINCT cm.receiverId FROM ChatMessage cm WHERE cm.senderId = :user")
    List<Users> findChatPartners(@Param("user") Users user);

    @Query("SELECT cm FROM ChatMessage cm WHERE ((cm.senderId = :user1 AND cm.receiverId = :user2) OR (cm.senderId = :user2 AND cm.receiverId = :user1)) AND (cm.isDeleted IS NULL OR cm.isDeleted = false) ORDER BY cm.timestamp DESC")
    List<ChatMessage> findLastMessageBetweenUsers(@Param("user1") Users user1, @Param("user2") Users user2);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.senderId = :sender AND cm.receiverId = :receiver AND cm.isRead = false AND (cm.isDeleted IS NULL OR cm.isDeleted = false)")
    int countUnreadMessages(@Param("sender") Users sender, @Param("receiver") Users receiver);
}
