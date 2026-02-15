package com.webapp.jobportal.services;

import com.webapp.jobportal.entity.JobSeekerApply;
import com.webapp.jobportal.entity.JobSeekerProfile;
import com.webapp.jobportal.entity.Rating;
import com.webapp.jobportal.entity.Users;
import com.webapp.jobportal.repository.RatingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    @Autowired
    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating createRating(Rating rating) {
        rating.setCreatedAt(new Date());
        return ratingRepository.save(rating);
    }

    public List<Rating> getFreelancerRatings(JobSeekerProfile freelancer) {
        return ratingRepository.findByFreelancer(freelancer);
    }

    public Double getAverageRating(JobSeekerProfile freelancer) {
        Double avg = ratingRepository.getAverageRating(freelancer);
        return avg != null ? avg : 0.0;
    }

    public Long getRatingCount(JobSeekerProfile freelancer) {
        return ratingRepository.getRatingCount(freelancer);
    }

    public Optional<Rating> getRatingByProject(JobSeekerApply project) {
        return ratingRepository.findByProject(project);
    }

    public List<Rating> getRecentRatings(JobSeekerProfile freelancer) {
        return ratingRepository.findRecentRatings(freelancer);
    }

    public boolean hasClientRatedProject(Users client, JobSeekerApply project) {
        Optional<Rating> existingRating = ratingRepository.findByProject(project);
        return existingRating.isPresent() &&
                existingRating.get().getClient().getUserId() == client.getUserId();
    }

    public int getGlobalSuccessRate() {
        Double avg = ratingRepository.getGlobalAverageRating();
        if (avg == null)
            return 0;
        return (int) Math.round((avg / 5.0) * 100);
    }
}
