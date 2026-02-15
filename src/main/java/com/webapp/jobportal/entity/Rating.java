package com.webapp.jobportal.entity;

import jakarta.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.Date;

@Entity
@Table(name = "rating")
public class Rating {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "freelancer_id", referencedColumnName = "user_account_id")
    private JobSeekerProfile freelancer;

    @ManyToOne
    @JoinColumn(name = "client_id", referencedColumnName = "user_id")
    private Users client;

    @ManyToOne
    @JoinColumn(name = "project_id", referencedColumnName = "id")
    private JobSeekerApply project;

    @Column(name = "rating_value")
    private Integer ratingValue; // 1-5 stars

    @Column(columnDefinition = "TEXT")
    private String review;

    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm:ss")
    @Column(name = "created_at")
    private Date createdAt;

    public Rating() {
    }

    public Rating(Integer id, JobSeekerProfile freelancer, Users client, JobSeekerApply project, 
                  Integer ratingValue, String review, Date createdAt) {
        this.id = id;
        this.freelancer = freelancer;
        this.client = client;
        this.project = project;
        this.ratingValue = ratingValue;
        this.review = review;
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public JobSeekerProfile getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(JobSeekerProfile freelancer) {
        this.freelancer = freelancer;
    }

    public Users getClient() {
        return client;
    }

    public void setClient(Users client) {
        this.client = client;
    }

    public JobSeekerApply getProject() {
        return project;
    }

    public void setProject(JobSeekerApply project) {
        this.project = project;
    }

    public Integer getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(Integer ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}