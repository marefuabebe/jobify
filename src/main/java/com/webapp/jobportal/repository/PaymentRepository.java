package com.webapp.jobportal.repository;

import com.webapp.jobportal.entity.Payment;
import com.webapp.jobportal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findByPayer(Users payer);

    List<Payment> findByPayee(Users payee);

    List<Payment> findByJob(com.webapp.jobportal.entity.JobPostActivity job);

    Payment findByStripeSessionId(String stripeSessionId);
}
