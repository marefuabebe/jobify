package com.webapp.jobportal.repository;

import com.webapp.jobportal.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends JpaRepository<Contract, Integer> {
    @org.springframework.data.jpa.repository.Query("SELECT c FROM Contract c WHERE c.client.userId = :userId")
    List<Contract> findByClientUserId(@org.springframework.data.repository.query.Param("userId") Integer userId);

    @org.springframework.data.jpa.repository.Query("SELECT c FROM Contract c WHERE c.freelancer.userId = :userId")
    List<Contract> findByFreelancerUserId(@org.springframework.data.repository.query.Param("userId") Integer userId);

    List<Contract> findByClient(com.webapp.jobportal.entity.Users client);

    List<Contract> findByFreelancer(com.webapp.jobportal.entity.Users freelancer);

    // Find contract by job application ID to prevent duplicates
    java.util.Optional<Contract> findByJobApplicationId(Integer jobApplicationId);

    java.util.Optional<Contract> findByJobApplication_Job_JobPostId(Integer jobPostId);
}
