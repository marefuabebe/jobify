package com.webapp.jobportal.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "withdrawals")
public class Withdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "job_post_id", referencedColumnName = "job_post_id")
    private JobPostActivity job;

    private Double amount;
    private Date withdrawalDate;
    private String status; // "COMPLETED", "PENDING"
    private String method; // "PayPal", "Bank Transfer"

    public Withdrawal() {
    }

    public Withdrawal(Users user, JobPostActivity job, Double amount, String status, String method) {
        this.user = user;
        this.job = job;
        this.amount = amount;
        this.withdrawalDate = new Date();
        this.status = status;
        this.method = method;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Users getUser() {
        return user;
    }

    public void setUser(Users user) {
        this.user = user;
    }

    public JobPostActivity getJob() {
        return job;
    }

    public void setJob(JobPostActivity job) {
        this.job = job;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Date getWithdrawalDate() {
        return withdrawalDate;
    }

    public void setWithdrawalDate(Date withdrawalDate) {
        this.withdrawalDate = withdrawalDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }
}
