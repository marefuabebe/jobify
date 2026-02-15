package com.webapp.jobportal.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Entity
@Table(name = "job_moderation_log")
public class JobModerationLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "job_id", referencedColumnName = "job_post_id")
    private JobPostActivity job;

    @Column(name = "old_status")
    private String oldStatus;

    @Column(name = "new_status")
    private String newStatus;

    @ManyToOne
    @JoinColumn(name = "action_by", referencedColumnName = "user_id")
    private Users actionBy;

    @Column(name = "reason", length = 1000)
    private String reason;

    @Column(name = "category")
    private String category;

    @Column(name = "severity")
    private String severity;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "timestamp")
    private Date timestamp;

    public JobModerationLog() {
    }

    public JobModerationLog(JobPostActivity job, String oldStatus, String newStatus, Users actionBy, String reason,
            String category, String severity, Date timestamp) {
        this.job = job;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
        this.actionBy = actionBy;
        this.reason = reason;
        this.category = category;
        this.severity = severity;
        this.timestamp = timestamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobPostActivity getJob() {
        return job;
    }

    public void setJob(JobPostActivity job) {
        this.job = job;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public void setOldStatus(String oldStatus) {
        this.oldStatus = oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(String newStatus) {
        this.newStatus = newStatus;
    }

    public Users getActionBy() {
        return actionBy;
    }

    public void setActionBy(Users actionBy) {
        this.actionBy = actionBy;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
