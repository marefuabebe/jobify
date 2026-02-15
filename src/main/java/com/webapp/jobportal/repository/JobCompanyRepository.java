package com.webapp.jobportal.repository;

import com.webapp.jobportal.entity.JobCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobCompanyRepository extends JpaRepository<JobCompany, Integer> {

    @Query(value = "SELECT * FROM job_company LIMIT 3", nativeQuery = true)
    List<JobCompany> findTop5();
}
