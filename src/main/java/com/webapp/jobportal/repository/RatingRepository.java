package com.webapp.jobportal.repository;

import com.webapp.jobportal.entity.JobSeekerApply;
import com.webapp.jobportal.entity.JobSeekerProfile;
import com.webapp.jobportal.entity.Rating;
import com.webapp.jobportal.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Integer> {

    List<Rating> findByFreelancer(JobSeekerProfile freelancer);

    List<Rating> findByClient(Users client);

    Optional<Rating> findByProject(JobSeekerApply project);

    @Query("SELECT AVG(r.ratingValue) FROM Rating r WHERE r.freelancer = :freelancer")
    Double getAverageRating(@Param("freelancer") JobSeekerProfile freelancer);

    @Query("SELECT COUNT(r) FROM Rating r WHERE r.freelancer = :freelancer")
    Long getRatingCount(@Param("freelancer") JobSeekerProfile freelancer);

    @Query("SELECT r FROM Rating r WHERE r.freelancer = :freelancer ORDER BY r.createdAt DESC")
    List<Rating> findRecentRatings(@Param("freelancer") JobSeekerProfile freelancer);

    @Query("SELECT AVG(r.ratingValue) FROM Rating r")
    Double getGlobalAverageRating();
}
