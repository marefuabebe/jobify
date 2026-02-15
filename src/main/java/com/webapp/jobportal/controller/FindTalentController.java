package com.webapp.jobportal.controller;

import com.webapp.jobportal.entity.*;
import com.webapp.jobportal.repository.JobSeekerProfileRepository;
import com.webapp.jobportal.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/find-talent")
public class FindTalentController {

    private final UsersService usersService;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final RatingService ratingService;

    @Autowired
    public FindTalentController(UsersService usersService,
            JobSeekerProfileRepository jobSeekerProfileRepository,
            RatingService ratingService) {
        this.usersService = usersService;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.ratingService = ratingService;
    }

    @GetMapping("/")
    public String findTalent(Model model,
            @RequestParam(value = "search", required = false) String search,
            @RequestParam(value = "skill", required = false) String skill,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "experienceLevel", required = false) String experienceLevel,
            @RequestParam(value = "minRating", required = false) Double minRating,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "8") int size) {

        Users currentUser = usersService.getCurrentUser();

        // Check if user is logged in
        boolean isLoggedIn = currentUser != null;
        model.addAttribute("isLoggedIn", isLoggedIn);
        model.addAttribute("activePage", "find-talent");

        if (isLoggedIn) {
            // Verify user is a Client for logged-in users
            if (currentUser.getUserTypeId() == null || currentUser.getUserTypeId().getUserTypeId() != 1) { // Client
                                                                                                           // user type
                                                                                                           // is 1
                return "redirect:/dashboard/";
            }
            model.addAttribute("currentUser", currentUser);
        }

        if (isLoggedIn) {
            RecruiterProfile clientProfile = (RecruiterProfile) usersService.getCurrentUserProfile();
            if (clientProfile == null) {
                return "redirect:/recruiter-profile/";
            }
            model.addAttribute("user", clientProfile);
            model.addAttribute("clientProfile", clientProfile);
        }

        // Get all verified freelancers
        List<JobSeekerProfile> allFreelancers = jobSeekerProfileRepository.findAll().stream()
                .filter(profile -> {
                    if (profile.getUserId() == null) {
                        System.out.println(
                                "Profile: " + profile.getFirstName() + " " + profile.getLastName() + " - No user ID");
                        return false;
                    }
                    Users user = profile.getUserId();
                    if (user.getUserTypeId() == null || user.getUserTypeId().getUserTypeId() != 2) {
                        System.out.println("Profile: " + profile.getFirstName() + " " + profile.getLastName()
                                + " - Not a freelancer type");
                        return false; // Must be a freelancer (type 2)
                    }
                    boolean isUserApproved = user.isApproved();
                    boolean isProfileVerified = Boolean.TRUE.equals(profile.getIsVerified());

                    // Debug logging (remove in production)
                    System.out.println("Freelancer: " + profile.getFirstName() + " " + profile.getLastName() +
                            " - Type: 2" +
                            " - Approved: " + isUserApproved +
                            " - Verified: " + isProfileVerified);

                    return isUserApproved && isProfileVerified;
                })
                .collect(Collectors.toList());

        System.out.println("Found " + allFreelancers.size() + " verified freelancers");

        // Apply filters
        List<JobSeekerProfile> filteredFreelancers = allFreelancers.stream()
                .filter(profile -> {
                    // Search filter
                    if (StringUtils.hasText(search)) {
                        String searchLower = search.toLowerCase();
                        boolean matchesSearch = (profile.getFirstName() != null
                                && profile.getFirstName().toLowerCase().contains(searchLower)) ||
                                (profile.getLastName() != null
                                        && profile.getLastName().toLowerCase().contains(searchLower))
                                ||
                                (profile.getCity() != null && profile.getCity().toLowerCase().contains(searchLower)) ||
                                (profile.getState() != null && profile.getState().toLowerCase().contains(searchLower))
                                ||
                                (profile.getCountry() != null
                                        && profile.getCountry().toLowerCase().contains(searchLower));
                        if (!matchesSearch)
                            return false;
                    }

                    // Skill filter
                    if (StringUtils.hasText(skill)) {
                        boolean hasSkill = profile.getSkills() != null &&
                                profile.getSkills().stream()
                                        .anyMatch(s -> s.getName() != null &&
                                                s.getName().toLowerCase().contains(skill.toLowerCase()));
                        if (!hasSkill)
                            return false;
                    }

                    // Location filter
                    if (StringUtils.hasText(location)) {
                        String locationLower = location.toLowerCase();
                        boolean matchesLocation = (profile.getCity() != null
                                && profile.getCity().toLowerCase().contains(locationLower)) ||
                                (profile.getState() != null && profile.getState().toLowerCase().contains(locationLower))
                                ||
                                (profile.getCountry() != null
                                        && profile.getCountry().toLowerCase().contains(locationLower));
                        if (!matchesLocation)
                            return false;
                    }

                    // Experience level filter
                    if (StringUtils.hasText(experienceLevel)) {
                        boolean hasExperienceLevel = profile.getSkills() != null &&
                                profile.getSkills().stream()
                                        .anyMatch(s -> s.getExperienceLevel() != null &&
                                                s.getExperienceLevel().equalsIgnoreCase(experienceLevel));
                        if (!hasExperienceLevel)
                            return false;
                    }

                    return true;
                })
                .collect(Collectors.toList());

        System.out.println("After filtering, " + filteredFreelancers.size() + " freelancers remain");

        // Pagination Logic (Standard 6 items for 3-column grid)
        int totalItems = filteredFreelancers.size();
        size = 6; // Set uniform size

        int totalPages = (int) Math.ceil((double) totalItems / size);

        // Clamp page number
        if (page < 0)
            page = 0;
        if (totalPages > 0 && page >= totalPages)
            page = totalPages - 1;

        int start = page * size;
        int end = Math.min(start + size, totalItems);

        List<JobSeekerProfile> paginatedFreelancers;
        if (start < totalItems) {
            paginatedFreelancers = filteredFreelancers.subList(start, end);
        } else {
            paginatedFreelancers = List.of();
        }

        // Calculate ratings for each freelancer (only for paginated items)
        Map<Integer, Double> freelancerRatings = new HashMap<>();
        Map<Integer, Long> freelancerRatingCounts = new HashMap<>();
        for (JobSeekerProfile freelancer : paginatedFreelancers) {
            Double avgRating = ratingService.getAverageRating(freelancer);
            Long ratingCount = ratingService.getRatingCount(freelancer);
            freelancerRatings.put(freelancer.getUserAccountId(), avgRating != null ? avgRating : 0.0);
            freelancerRatingCounts.put(freelancer.getUserAccountId(), ratingCount != null ? ratingCount : 0L);
        }

        // Set model attributes
        RecruiterProfile clientProfile = null;
        if (isLoggedIn) {
            clientProfile = (RecruiterProfile) usersService.getCurrentUserProfile();
        }

        model.addAttribute("user", clientProfile);
        model.addAttribute("freelancers", paginatedFreelancers);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("size", size);
        model.addAttribute("freelancerRatings", freelancerRatings);
        model.addAttribute("freelancerRatingCounts", freelancerRatingCounts);
        model.addAttribute("search", search);
        model.addAttribute("skill", skill);
        model.addAttribute("location", location);
        model.addAttribute("experienceLevel", experienceLevel);
        model.addAttribute("minRating", minRating);

        // Set verification status only for logged-in users
        boolean isVerifiedResult = isLoggedIn && clientProfile != null &&
                Boolean.TRUE.equals(clientProfile.getIsVerified()) &&
                currentUser != null && currentUser.isApproved();
        model.addAttribute("isVerified", isVerifiedResult);

        return "find-talent";
    }
}
