package com.webapp.jobportal.repository;

import com.webapp.jobportal.entity.Users;
import com.webapp.jobportal.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Integer> {
    List<Withdrawal> findByUser(Users user);

    List<Withdrawal> findByJob(com.webapp.jobportal.entity.JobPostActivity job);
}
