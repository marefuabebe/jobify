package com.webapp.jobportal.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "job_seeker_apply", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "job" })
})
public class JobSeekerApply implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_account_id")
    private JobSeekerProfile userId;

    @ManyToOne
    @JoinColumn(name = "job", referencedColumnName = "job_post_id")
    private JobPostActivity job;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "apply_date")
    private Date applyDate;

    @Column(name = "cover_letter_text", columnDefinition = "TEXT")
    private String coverLetter;

    @Column(name = "application_status")
    private String applicationStatus = "PENDING";

    @Column(name = "proposed_rate")
    private Double proposedRate;

    @Column(name = "understanding_text", columnDefinition = "TEXT")
    private String understanding;

    @Column(name = "relevant_skills_text", columnDefinition = "TEXT")
    private String relevantSkills;

    @Column(name = "expected_completion_time")
    private String expectedCompletionTime;

    public JobSeekerApply() {
    }

    public JobSeekerApply(Integer id, JobSeekerProfile userId, JobPostActivity job, Date applyDate, String coverLetter,
            String applicationStatus, Double proposedRate) {
        this.id = id;
        this.userId = userId;
        this.job = job;
        this.applyDate = applyDate;
        this.coverLetter = coverLetter;
        this.applicationStatus = applicationStatus;
        this.proposedRate = proposedRate;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobSeekerProfile getUserId() {
        return userId;
    }

    public void setUserId(JobSeekerProfile userId) {
        this.userId = userId;
    }

    public JobPostActivity getJob() {
        return job;
    }

    public void setJob(JobPostActivity job) {
        this.job = job;
    }

    public Date getApplyDate() {
        return applyDate;
    }

    public void setApplyDate(Date applyDate) {
        this.applyDate = applyDate;
    }

    public String getCoverLetter() {
        return coverLetter;
    }

    public void setCoverLetter(String coverLetter) {
        this.coverLetter = coverLetter;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public Double getProposedRate() {
        return proposedRate;
    }

    public void setProposedRate(Double proposedRate) {
        this.proposedRate = proposedRate;
    }

    public String getUnderstanding() {
        return understanding;
    }

    public void setUnderstanding(String understanding) {
        this.understanding = understanding;
    }

    public String getRelevantSkills() {
        return relevantSkills;
    }

    public void setRelevantSkills(String relevantSkills) {
        this.relevantSkills = relevantSkills;
    }

    public String getExpectedCompletionTime() {
        return expectedCompletionTime;
    }

    public void setExpectedCompletionTime(String expectedCompletionTime) {
        this.expectedCompletionTime = expectedCompletionTime;
    }

    @Override
    public String toString() {
        return "JobSeekerApply{" +
                "id=" + id +
                ", userId=" + userId +
                ", job=" + job +
                ", applyDate=" + applyDate +
                ", coverLetter='" + coverLetter + '\'' +
                ", understanding='" + understanding + '\'' +
                ", relevantSkills='" + relevantSkills + '\'' +
                ", expectedCompletionTime='" + expectedCompletionTime + '\'' +
                '}';
    }
}