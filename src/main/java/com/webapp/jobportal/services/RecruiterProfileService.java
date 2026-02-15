package com.webapp.jobportal.services;

import com.webapp.jobportal.entity.RecruiterProfile;
import com.webapp.jobportal.entity.Users;
import com.webapp.jobportal.repository.RecruiterProfileRepository;
import com.webapp.jobportal.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RecruiterProfileService {

    private final RecruiterProfileRepository recruiterRepository;
    private final UsersRepository usersRepository;

    @Autowired
    public RecruiterProfileService(RecruiterProfileRepository recruiterRepository, UsersRepository usersRepository) {
        this.recruiterRepository = recruiterRepository;
        this.usersRepository = usersRepository;
    }

    public Optional<RecruiterProfile> getOne(Integer id) {
        return recruiterRepository.findById(id);
    }

    public RecruiterProfile addNew(RecruiterProfile recruiterProfile) {
        return recruiterRepository.save(recruiterProfile);
    }

    public RecruiterProfile getCurrentRecruiterProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Users users = usersRepository.findByEmail(currentUsername).orElseThrow(() -> new UsernameNotFoundException("User not found"));
            Optional<RecruiterProfile> recruiterProfile = getOne(users.getUserId());
            return recruiterProfile.orElse(null);
        } else return null;
    }
    
    public RecruiterProfile verifyProfile(int userId) {
        Optional<RecruiterProfile> profile = getOne(userId);
        if (profile.isPresent()) {
            RecruiterProfile p = profile.get();
            p.setIsVerified(true);
            p.setDocumentStatus("approved");
            p.setBusinessLicenseStatus("approved");
            return recruiterRepository.save(p);
        }
        throw new RuntimeException("Profile not found");
    }

    public RecruiterProfile rejectProfile(int userId) {
        Optional<RecruiterProfile> profile = getOne(userId);
        if (profile.isPresent()) {
            RecruiterProfile p = profile.get();
            p.setIsVerified(false);
            p.setDocumentStatus("rejected");
            p.setBusinessLicenseStatus("rejected");
            return recruiterRepository.save(p);
        }
        throw new RuntimeException("Profile not found");
    }
}
