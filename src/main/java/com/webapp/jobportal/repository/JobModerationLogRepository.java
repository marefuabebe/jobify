package com.webapp.jobportal.repository;

import com.webapp.jobportal.entity.JobModerationLog;
import com.webapp.jobportal.entity.JobPostActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobModerationLogRepository extends JpaRepository<JobModerationLog, Integer> {
    List<JobModerationLog> findByJobOrderByTimestampDesc(JobPostActivity job);

    List<JobModerationLog> findAllByOrderByTimestampDesc();

    org.springframework.data.domain.Page<JobModerationLog> findAllByOrderByTimestampDesc(
            org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT l FROM JobModerationLog l WHERE " +
            "(:status IS NULL OR l.newStatus = :status) AND " +
            "(:severity IS NULL OR l.severity = :severity) AND " +
            "(:category IS NULL OR l.category = :category) AND " +
            "(:moderatorId IS NULL OR l.actionBy.userId = :moderatorId) AND " +
            "(:startDate IS NULL OR l.timestamp >= :startDate) AND " +
            "(:endDate IS NULL OR l.timestamp <= :endDate)")
    org.springframework.data.domain.Page<JobModerationLog> findWithFilters(
            @org.springframework.data.repository.query.Param("status") String status,
            @org.springframework.data.repository.query.Param("severity") String severity,
            @org.springframework.data.repository.query.Param("category") String category,
            @org.springframework.data.repository.query.Param("moderatorId") Integer moderatorId,
            @org.springframework.data.repository.query.Param("startDate") java.util.Date startDate,
            @org.springframework.data.repository.query.Param("endDate") java.util.Date endDate,
            org.springframework.data.domain.Pageable pageable);
}
