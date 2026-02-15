package com.webapp.jobportal.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "disputes")
public class Dispute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "user_id")
    private Users reporter;

    @ManyToOne
    @JoinColumn(name = "against_id", referencedColumnName = "user_id")
    private Users against; // Optional, can be derived from Job

    @ManyToOne
    @JoinColumn(name = "job_post_id", referencedColumnName = "job_post_id")
    private JobPostActivity job;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String type; // STATUS_ISSUE, PAYMENT_ISSUE, BEHAVIOR, OTHER
    private String status; // PENDING, IN_REVIEW, RESOLVED, DISMISSED

    @Temporal(TemporalType.TIMESTAMP)
    private Date postedDate;

    @Column(columnDefinition = "TEXT")
    private String resolutionNotes;

    public Dispute() {
    }

    public Dispute(Users reporter, Users against, JobPostActivity job, String description, String type, String status) {
        this.reporter = reporter;
        this.against = against;
        this.job = job;
        this.description = description;
        this.type = type;
        this.status = status;
        this.postedDate = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Users getReporter() {
        return reporter;
    }

    public void setReporter(Users reporter) {
        this.reporter = reporter;
    }

    public Users getAgainst() {
        return against;
    }

    public void setAgainst(Users against) {
        this.against = against;
    }

    public JobPostActivity getJob() {
        return job;
    }

    public void setJob(JobPostActivity job) {
        this.job = job;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getResolutionNotes() {
        return resolutionNotes;
    }

    public void setResolutionNotes(String resolutionNotes) {
        this.resolutionNotes = resolutionNotes;
    }
}
