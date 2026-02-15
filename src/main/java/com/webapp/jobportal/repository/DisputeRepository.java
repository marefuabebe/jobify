package com.webapp.jobportal.repository;

import com.webapp.jobportal.entity.Dispute;
import com.webapp.jobportal.entity.JobPostActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisputeRepository extends JpaRepository<Dispute, Integer> {
    List<Dispute> findByJob(JobPostActivity job);

    List<Dispute> findByStatus(String status);
}
