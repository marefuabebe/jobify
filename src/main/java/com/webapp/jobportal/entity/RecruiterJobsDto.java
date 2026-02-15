package com.webapp.jobportal.entity;

public class RecruiterJobsDto {

    private Long totalCandidates;
    private Integer jobPostId;
    private String jobTitle;
    private JobLocation jobLocationId;
    private JobCompany jobCompanyId;

    private String descriptionOfJob;
    private java.util.Date postedDate;
    private String status;
    private String severity;
    private String moderationNote;

    public RecruiterJobsDto(Long totalCandidates, Integer jobPostId, String jobTitle, JobLocation jobLocationId,
            JobCompany jobCompanyId, String descriptionOfJob, java.util.Date postedDate, String status,
            String severity) {
        this(totalCandidates, jobPostId, jobTitle, jobLocationId, jobCompanyId, descriptionOfJob, postedDate, status,
                severity, null);
    }

    public RecruiterJobsDto(Long totalCandidates, Integer jobPostId, String jobTitle, JobLocation jobLocationId,
            JobCompany jobCompanyId, String descriptionOfJob, java.util.Date postedDate, String status,
            String severity, String moderationNote) {
        this.totalCandidates = totalCandidates;
        this.jobPostId = jobPostId;
        this.jobTitle = jobTitle;
        this.jobLocationId = jobLocationId;
        this.jobCompanyId = jobCompanyId;
        this.descriptionOfJob = descriptionOfJob;
        this.postedDate = postedDate;
        this.status = status;
        this.severity = severity;
        this.moderationNote = moderationNote;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getModerationNote() {
        return moderationNote;
    }

    public void setModerationNote(String moderationNote) {
        this.moderationNote = moderationNote;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTotalCandidates() {
        return totalCandidates;
    }

    public void setTotalCandidates(Long totalCandidates) {
        this.totalCandidates = totalCandidates;
    }

    public Integer getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Integer jobPostId) {
        this.jobPostId = jobPostId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public JobLocation getJobLocationId() {
        return jobLocationId;
    }

    public void setJobLocationId(JobLocation jobLocationId) {
        this.jobLocationId = jobLocationId;
    }

    public JobCompany getJobCompanyId() {
        return jobCompanyId;
    }

    public void setJobCompanyId(JobCompany jobCompanyId) {
        this.jobCompanyId = jobCompanyId;
    }

    public String getDescriptionOfJob() {
        return descriptionOfJob;
    }

    public void setDescriptionOfJob(String descriptionOfJob) {
        this.descriptionOfJob = descriptionOfJob;
    }

    public java.util.Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(java.util.Date postedDate) {
        this.postedDate = postedDate;
    }
}
