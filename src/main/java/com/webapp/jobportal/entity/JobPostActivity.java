package com.webapp.jobportal.entity;

import jakarta.persistence.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "job_post_activity")
public class JobPostActivity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_post_id")
    private Integer jobPostId;

    @ManyToOne
    @JoinColumn(name = "posted_by_id", referencedColumnName = "user_id")
    private Users postedById;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_location_id", referencedColumnName = "id")
    private JobLocation jobLocationId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "job_company_id", referencedColumnName = "id")
    private JobCompany jobCompanyId;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "is_approved")
    private Boolean isApproved = false;

    @Transient
    private Boolean isSaved;

    @Transient
    private Boolean postedByVerified;

    @Transient
    private String userApplicationStatus; // Open, Applied, HIRED (In Progress), COMPLETED

    @Length(max = 10000)
    @Column(name = "description_of_job", columnDefinition = "TEXT")
    private String descriptionOfJob;

    @Column(name = "job_type")
    private String jobType;
    private String salary;

    @Column(name = "salary_min")
    private Double salaryMin;

    @Column(name = "salary_max")
    private Double salaryMax;

    private String remote;

    @Column(name = "job_category")
    private String jobCategory;

    @DateTimeFormat(pattern = "dd-MM-yyyy")
    @Column(name = "posted_date")
    private Date postedDate;

    @Column(name = "job_title")
    private String jobTitle;

    @Column(name = "required_skills", length = 1000)
    private String requiredSkills;

    @Column(name = "project_duration")
    private String projectDuration;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "application_deadline")
    private Date applicationDeadline;

    @Column(name = "project_type")
    private String projectType; // Fixed-Price or Hourly

    @Column(name = "work_mode")
    private String workMode; // Remote or Onsite

    @Column(name = "status")
    private String status = "OPEN"; // OPEN, PAUSED, CLOSED, REJECTED, VIOLATION, UNDER_REVIEW

    @Column(name = "severity")
    private String severity; // LOW (Warning), MEDIUM (Temporary Block), HIGH (Permanent)

    @Column(name = "violation_category")
    private String violationCategory; // SCAM, SPAM, INAPPROPRIATE_CONTENT, TOS_VIOLATION

    @Column(name = "moderation_note", length = 1000)
    private String moderationNote;

    public JobPostActivity() {
    }

    public JobPostActivity(Integer jobPostId, Users postedById, JobLocation jobLocationId, JobCompany jobCompanyId,
            Boolean isActive, Boolean isApproved, Boolean isSaved, String descriptionOfJob, String jobType,
            String salary, Double salaryMin, Double salaryMax, String remote, Date postedDate, String jobTitle,
            String status) {
        this.jobPostId = jobPostId;
        this.postedById = postedById;
        this.jobLocationId = jobLocationId;
        this.jobCompanyId = jobCompanyId;
        this.isActive = isActive;
        this.isApproved = isApproved;
        this.isSaved = isSaved;
        this.descriptionOfJob = descriptionOfJob;
        this.jobType = jobType;
        this.salary = salary;
        this.salaryMin = salaryMin;
        this.salaryMax = salaryMax;
        this.remote = remote;
        this.postedDate = postedDate;
        this.jobTitle = jobTitle;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getModerationNote() {
        return moderationNote;
    }

    public void setModerationNote(String moderationNote) {
        this.moderationNote = moderationNote;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getViolationCategory() {
        return violationCategory;
    }

    public void setViolationCategory(String violationCategory) {
        this.violationCategory = violationCategory;
    }

    public Integer getJobPostId() {
        return jobPostId;
    }

    public void setJobPostId(Integer jobPostId) {
        this.jobPostId = jobPostId;
    }

    public Users getPostedById() {
        return postedById;
    }

    public void setPostedById(Users postedById) {
        this.postedById = postedById;
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

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean active) {
        isActive = active;
    }

    public Boolean getIsSaved() {
        return isSaved;
    }

    public void setIsSaved(Boolean saved) {
        isSaved = saved;
    }

    public Boolean getIsApproved() {
        return isApproved;
    }

    public void setIsApproved(Boolean approved) {
        isApproved = approved;
    }

    public String getDescriptionOfJob() {
        return descriptionOfJob;
    }

    public void setDescriptionOfJob(String descriptionOfJob) {
        this.descriptionOfJob = descriptionOfJob;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }

    public Double getSalaryMin() {
        return salaryMin;
    }

    public void setSalaryMin(Double salaryMin) {
        this.salaryMin = salaryMin;
    }

    public Double getSalaryMax() {
        return salaryMax;
    }

    public void setSalaryMax(Double salaryMax) {
        this.salaryMax = salaryMax;
    }

    public String getRemote() {
        return remote;
    }

    public void setRemote(String remote) {
        this.remote = remote;
    }

    public String getJobCategory() {
        return jobCategory;
    }

    public void setJobCategory(String jobCategory) {
        this.jobCategory = jobCategory;
    }

    public Date getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(Date postedDate) {
        this.postedDate = postedDate;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public String getProjectDuration() {
        return projectDuration;
    }

    public void setProjectDuration(String projectDuration) {
        this.projectDuration = projectDuration;
    }

    public Date getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(Date applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }

    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }

    public String getWorkMode() {
        return workMode;
    }

    public void setWorkMode(String workMode) {
        this.workMode = workMode;
    }

    public Boolean getPostedByVerified() {
        return postedByVerified;
    }

    public void setPostedByVerified(Boolean postedByVerified) {
        this.postedByVerified = postedByVerified;
    }

    public String getUserApplicationStatus() {
        return userApplicationStatus;
    }

    public void setUserApplicationStatus(String userApplicationStatus) {
        this.userApplicationStatus = userApplicationStatus;
    }

    // Transient fields to hold recruiter profile data for template access
    @Transient
    private String recruiterFirstName;

    @Transient
    private String recruiterLastName;

    @Transient
    private String recruiterCity;

    @Transient
    private String recruiterCountry;

    @Transient
    private String recruiterCompany;

    @Transient
    private String recruiterProfilePhoto;

    // Getters and setters for the transient fields
    public String getRecruiterFirstName() {
        return recruiterFirstName;
    }

    public void setRecruiterFirstName(String recruiterFirstName) {
        this.recruiterFirstName = recruiterFirstName;
    }

    public String getRecruiterLastName() {
        return recruiterLastName;
    }

    public void setRecruiterLastName(String recruiterLastName) {
        this.recruiterLastName = recruiterLastName;
    }

    public String getRecruiterCity() {
        return recruiterCity;
    }

    public void setRecruiterCity(String recruiterCity) {
        this.recruiterCity = recruiterCity;
    }

    public String getRecruiterCountry() {
        return recruiterCountry;
    }

    public void setRecruiterCountry(String recruiterCountry) {
        this.recruiterCountry = recruiterCountry;
    }

    public String getRecruiterCompany() {
        return recruiterCompany;
    }

    public void setRecruiterCompany(String recruiterCompany) {
        this.recruiterCompany = recruiterCompany;
    }

    @Transient
    private Integer recruiterUserAccountId;

    public String getRecruiterProfilePhoto() {
        return recruiterProfilePhoto;
    }

    public void setRecruiterProfilePhoto(String recruiterProfilePhoto) {
        this.recruiterProfilePhoto = recruiterProfilePhoto;
    }

    public Integer getRecruiterUserAccountId() {
        return recruiterUserAccountId;
    }

    public void setRecruiterUserAccountId(Integer recruiterUserAccountId) {
        this.recruiterUserAccountId = recruiterUserAccountId;
    }

    @Override
    public String toString() {
        return "JobPostActivity{" +
                "jobPostId=" + jobPostId +
                ", postedById=" + postedById +
                ", jobLocationId=" + jobLocationId +
                ", jobCompanyId=" + jobCompanyId +
                ", isActive=" + isActive +
                ", isSaved=" + isSaved +
                ", descriptionOfJob='" + descriptionOfJob + '\'' +
                ", jobType='" + jobType + '\'' +
                ", salary='" + salary + '\'' +
                ", remote='" + remote + '\'' +
                ", postedDate=" + postedDate +
                ", jobTitle='" + jobTitle + '\'' +
                '}';
    }
}