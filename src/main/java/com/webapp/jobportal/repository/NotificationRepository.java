package com.webapp.jobportal.repository;

import com.webapp.jobportal.entity.Notification;
import com.webapp.jobportal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    
    List<Notification> findByUserIdOrderByCreatedAtDesc(Users userId);
    
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(Users userId);
    
    long countByUserIdAndIsReadFalse(Users userId);
    
    @Query("SELECT n FROM Notification n WHERE n.userId = :user ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("user") Users user);
}

