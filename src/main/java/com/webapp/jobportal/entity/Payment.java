package com.webapp.jobportal.entity;

import jakarta.persistence.*;
import java.util.Date;

@Entity
@Table(name = "payments")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "payer_id", referencedColumnName = "user_id")
    private Users payer;

    @ManyToOne
    @JoinColumn(name = "payee_id", referencedColumnName = "user_id")
    private Users payee;

    @ManyToOne
    @JoinColumn(name = "job_post_id", referencedColumnName = "job_post_id")
    private JobPostActivity job;

    @ManyToOne
    @JoinColumn(name = "milestone_id", referencedColumnName = "id")
    private Milestone milestone;

    private Double amount;
    private Double serviceFee;
    private Double totalAmount;

    private Date paymentDate;
    private String paymentMethod; // e.g., "Credit Card (Demo)"
    private String status; // "COMPLETED", "FAILED"
    private String stripeSessionId; // Stripe Checkout Session ID

    public Payment() {
    }

    public Payment(Users payer, Users payee, JobPostActivity job, Double amount, Double serviceFee, Double totalAmount,
            String paymentMethod, String status) {
        this.payer = payer;
        this.payee = payee;
        this.job = job;
        this.amount = amount;
        this.serviceFee = serviceFee;
        this.totalAmount = totalAmount;
        this.paymentDate = new Date();
        this.paymentMethod = paymentMethod;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Users getPayer() {
        return payer;
    }

    public void setPayer(Users payer) {
        this.payer = payer;
    }

    public Users getPayee() {
        return payee;
    }

    public void setPayee(Users payee) {
        this.payee = payee;
    }

    public JobPostActivity getJob() {
        return job;
    }

    public void setJob(JobPostActivity job) {
        this.job = job;
    }

    public Milestone getMilestone() {
        return milestone;
    }

    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public Double getServiceFee() {
        return serviceFee;
    }

    public void setServiceFee(Double serviceFee) {
        this.serviceFee = serviceFee;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Date getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(Date paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStripeSessionId() {
        return stripeSessionId;
    }

    public void setStripeSessionId(String stripeSessionId) {
        this.stripeSessionId = stripeSessionId;
    }
}
