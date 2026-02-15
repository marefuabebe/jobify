package com.webapp.jobportal.repository;

import com.webapp.jobportal.entity.RecruiterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RecruiterProfileRepository extends JpaRepository<RecruiterProfile, Integer> {

    @Query("SELECT DISTINCT p.country FROM RecruiterProfile p WHERE p.country IS NOT NULL")
    List<String> findDistinctCountries();

    @Modifying
    @Transactional
    @Query(value = "CALL VerifyRecruiter(:userId, :adminId, :approve, :notes)", nativeQuery = true)
    void verifyRecruiter(@Param("userId") int userId, @Param("adminId") int adminId,
            @Param("approve") boolean approve, @Param("notes") String notes);

    long countByIsVerifiedTrue();
}
