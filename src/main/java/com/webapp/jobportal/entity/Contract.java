package com.webapp.jobportal.entity;

import jakarta.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "contracts")
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "job_application_id", referencedColumnName = "id")
    private JobSeekerApply jobApplication;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "user_id")
    private Users client;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", referencedColumnName = "user_id")
    private Users freelancer;

    @Column(name = "contract_title")
    private String title;

    @Column(name = "total_amount")
    private Double totalAmount;

    @Column(name = "status")
    private String status; // DRAFT, ACTIVE, COMPLETED, CANCELLED

    @Column(name = "created_date")
    private Date createdDate;

    @OneToMany(mappedBy = "contract", cascade = CascadeType.ALL)
    private List<Milestone> milestones = new java.util.ArrayList<>();

    public Contract() {
    }

    public Contract(JobSeekerApply jobApplication, Users client, Users freelancer, String title, Double totalAmount,
            String status) {
        this.jobApplication = jobApplication;
        this.client = client;
        this.freelancer = freelancer;
        this.title = title;
        this.totalAmount = totalAmount;
        this.status = status;
        this.createdDate = new Date();
    }

    // Getters and Setters

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobSeekerApply getJobApplication() {
        return jobApplication;
    }

    public void setJobApplication(JobSeekerApply jobApplication) {
        this.jobApplication = jobApplication;
    }

    public Users getClient() {
        return client;
    }

    public void setClient(Users client) {
        this.client = client;
    }

    public Users getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(Users freelancer) {
        this.freelancer = freelancer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }
}
