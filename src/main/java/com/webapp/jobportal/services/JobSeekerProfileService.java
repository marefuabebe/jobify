package com.webapp.jobportal.services;

import com.webapp.jobportal.entity.JobSeekerProfile;
import com.webapp.jobportal.entity.Users;
import com.webapp.jobportal.repository.JobSeekerProfileRepository;
import com.webapp.jobportal.repository.UsersRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class JobSeekerProfileService {

    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final UsersRepository usersRepository;

    public JobSeekerProfileService(JobSeekerProfileRepository jobSeekerProfileRepository,
            UsersRepository usersRepository) {
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.usersRepository = usersRepository;
    }

    public java.util.List<JobSeekerProfile> getRecentFreelancers() {
        return jobSeekerProfileRepository.getRecentProfiles();
    }

    public Optional<JobSeekerProfile> getOne(Integer id) {
        return jobSeekerProfileRepository.findById(id);
    }

    public Optional<JobSeekerProfile> getOneWithSkills(Integer id) {
        return jobSeekerProfileRepository.findByIdWithSkills(id);
    }

    public JobSeekerProfile addNew(JobSeekerProfile jobSeekerProfile) {
        return jobSeekerProfileRepository.save(jobSeekerProfile);
    }

    public JobSeekerProfile getCurrentSeekerProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Optional<JobSeekerProfile> seekerProfile = getOne(users.getUserId());
            return seekerProfile.orElse(null);
        } else
            return null;

    }

    public JobSeekerProfile verifyProfile(int userId) {
        Optional<JobSeekerProfile> profile = getOne(userId);
        if (profile.isPresent()) {
            JobSeekerProfile p = profile.get();
            p.setIsVerified(true);
            p.setDocumentStatus("approved");
            return jobSeekerProfileRepository.save(p);
        }
        throw new RuntimeException("Profile not found");
    }

    public JobSeekerProfile rejectProfile(int userId) {
        Optional<JobSeekerProfile> profile = getOne(userId);
        if (profile.isPresent()) {
            JobSeekerProfile p = profile.get();
            p.setIsVerified(false);
            p.setDocumentStatus("rejected");
            return jobSeekerProfileRepository.save(p);
        }
        throw new RuntimeException("Profile not found");
    }

    public long getTotalVerifiedFreelancers() {
        return jobSeekerProfileRepository.countByIsVerifiedTrue();
    }
}
