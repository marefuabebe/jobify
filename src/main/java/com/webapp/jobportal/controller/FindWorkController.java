package com.webapp.jobportal.controller;

import com.webapp.jobportal.entity.*;
import com.webapp.jobportal.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/find-work")
public class FindWorkController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final RecruiterProfileService recruiterProfileService;
    private final ChatService chatService;

    @Autowired
    public FindWorkController(UsersService usersService,
            JobPostActivityService jobPostActivityService,
            JobSeekerApplyService jobSeekerApplyService,
            JobSeekerSaveService jobSeekerSaveService,
            RecruiterProfileService recruiterProfileService,
            ChatService chatService) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.recruiterProfileService = recruiterProfileService;
        this.chatService = chatService;
    }

    @GetMapping(value = { "/", "" })
    public String findWork(Model model,
            @RequestParam(value = "job", required = false) String job,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "partTime", required = false) String partTime,
            @RequestParam(value = "fullTime", required = false) String fullTime,
            @RequestParam(value = "freelance", required = false) String freelance,
            @RequestParam(value = "remoteOnly", required = false) String remoteOnly,
            @RequestParam(value = "officeOnly", required = false) String officeOnly,
            @RequestParam(value = "partialRemote", required = false) String partialRemote,
            @RequestParam(value = "today", required = false) boolean today,
            @RequestParam(value = "days7", required = false) boolean days7,
            @RequestParam(value = "days30", required = false) boolean days30,
            @RequestParam(value = "minBudget", required = false) Double minBudget,
            @RequestParam(value = "maxBudget", required = false) Double maxBudget,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "6") int size) {

        Users currentUser = usersService.getCurrentUser();
        JobSeekerProfile freelancerProfile = null;

        // Check if user is logged in
        boolean isLoggedIn = currentUser != null;
        model.addAttribute("isLoggedIn", isLoggedIn);

        if (isLoggedIn) {
            // Verify user is a Freelancer for logged-in users
            if (currentUser.getUserTypeId() != null && currentUser.getUserTypeId().getUserTypeId() == 2) {
                freelancerProfile = (JobSeekerProfile) usersService.getCurrentUserProfile();
                if (freelancerProfile != null) {
                    model.addAttribute("currentUser", currentUser);
                    model.addAttribute("user", freelancerProfile);
                    model.addAttribute("freelancerProfile", freelancerProfile);

                    // Populate Header Badges
                    List<JobSeekerApply> appliedJobs = jobSeekerApplyService.getCandidatesJobs(freelancerProfile);
                    List<JobSeekerApply> ongoingProjects = appliedJobs.stream()
                            .filter(application -> "HIRED".equals(application.getApplicationStatus()))
                            .collect(Collectors.toList());
                    List<ChatMessage> unreadMessages = chatService.getUnreadMessages(currentUser);

                    model.addAttribute("appliedJobs", appliedJobs);
                    model.addAttribute("ongoingProjects", ongoingProjects);
                    model.addAttribute("unreadMessagesCount", unreadMessages.size());
                    boolean isUserVerified = Boolean.TRUE.equals(freelancerProfile.getIsVerified())
                            && currentUser.isApproved();
                    model.addAttribute("isVerified", isUserVerified);
                }
            }
        }

        // Search for jobs
        model.addAttribute("partTime", Objects.equals(partTime, "Part-Time"));
        model.addAttribute("fullTime", Objects.equals(fullTime, "Full-Time"));
        model.addAttribute("freelance", Objects.equals(freelance, "Freelance"));
        model.addAttribute("remoteOnly", Objects.equals(remoteOnly, "Remote-Only"));
        model.addAttribute("officeOnly", Objects.equals(officeOnly, "Office-Only"));
        model.addAttribute("partialRemote", Objects.equals(partialRemote, "Partial-Remote"));
        model.addAttribute("today", today);
        model.addAttribute("days7", days7);
        model.addAttribute("days30", days30);
        model.addAttribute("job", job);
        model.addAttribute("location", location);
        model.addAttribute("minBudget", minBudget);
        model.addAttribute("maxBudget", maxBudget);
        model.addAttribute("category", category);

        LocalDate searchDate = null;
        List<JobPostActivity> jobPost = null;
        boolean dateSearchFlag = true;
        boolean remote = true;
        boolean type = true;

        if (days30) {
            searchDate = LocalDate.now().minusDays(30);
        } else if (days7) {
            searchDate = LocalDate.now().minusDays(7);
        } else if (today) {
            searchDate = LocalDate.now();
        } else {
            dateSearchFlag = false;
        }

        List<String> typeList = new ArrayList<>();
        if (partTime != null)
            typeList.add("Part-Time");
        if (fullTime != null)
            typeList.add("Full-Time");
        if (freelance != null)
            typeList.add("Freelance");

        List<String> remoteList = new ArrayList<>();
        if (remoteOnly != null) {
            remoteList.add("Remote-Only");
            remoteList.add("Remote");
        }
        if (officeOnly != null) {
            remoteList.add("Office-Only");
            remoteList.add("Office");
            remoteList.add("Onsite");
        }
        if (partialRemote != null) {
            remoteList.add("Partial-Remote");
            remoteList.add("Hybrid");
        }

        if (typeList.isEmpty()) {
            typeList.addAll(Arrays.asList("Part-Time", "Full-Time", "Freelance", "Contract", "Temporary"));
            remote = false;
        }

        if (remoteList.isEmpty()) {
            remoteList.addAll(Arrays.asList("Remote-Only", "Remote", "Office-Only", "Office", "Onsite",
                    "Partial-Remote", "Hybrid"));
            type = false;
        }

        // Search for jobs
        if (!dateSearchFlag && !remote && !type && !StringUtils.hasText(job) && !StringUtils.hasText(location)
                && !StringUtils.hasText(category)) {
            jobPost = jobPostActivityService.getAll();
        } else {
            jobPost = jobPostActivityService.search(job, location, typeList, remoteList, searchDate, category);
        }

        if (jobPost == null) {
            jobPost = new ArrayList<>();
        }

        // Apply budget filters
        if (minBudget != null || maxBudget != null) {
            jobPost = jobPost.stream()
                    .filter(j -> {
                        if (j == null)
                            return false;
                        if (j.getSalaryMin() == null && j.getSalaryMax() == null)
                            return false;
                        if (minBudget != null && j.getSalaryMax() != null && j.getSalaryMax() < minBudget)
                            return false;
                        if (maxBudget != null && j.getSalaryMin() != null && j.getSalaryMin() > maxBudget)
                            return false;
                        return true;
                    })
                    .collect(Collectors.toList());
        }

        // Sort jobs by postedDate descending (Recent First)
        if (jobPost != null) {
            jobPost.sort(Comparator.comparing(JobPostActivity::getPostedDate,
                    Comparator.nullsLast(Comparator.reverseOrder())));
        }

        // Pagination Logic
        int totalItems = jobPost.size();
        int totalPages = (int) Math.ceil((double) totalItems / size);

        // Ensure page is within bounds
        if (page < 0)
            page = 0;
        if (page >= totalPages && totalPages > 0)
            page = totalPages - 1;

        int startIdx = page * size;
        int endIdx = Math.min(startIdx + size, totalItems);

        List<JobPostActivity> pagedJobs = new ArrayList<>();
        if (startIdx < totalItems) {
            pagedJobs = jobPost.subList(startIdx, endIdx);
        }

        // Mark applied/saved only for paginated results
        if (isLoggedIn) {
            if (freelancerProfile != null) {
                List<JobSeekerApply> jobSeekerApplyList = jobSeekerApplyService.getCandidatesJobs(freelancerProfile);
                List<JobSeekerSave> jobSeekerSaveList = jobSeekerSaveService.getCandidatesJob(freelancerProfile);

                for (JobPostActivity jobActivity : pagedJobs) {
                    // Check for applied jobs
                    boolean exist = jobSeekerApplyList.stream().anyMatch(a -> a.getJob() != null
                            && Objects.equals(jobActivity.getJobPostId(), a.getJob().getJobPostId()));
                    jobActivity.setIsActive(exist);

                    // Check for saved jobs
                    boolean saved = jobSeekerSaveList.stream().anyMatch(s -> s.getJob() != null
                            && Objects.equals(jobActivity.getJobPostId(), s.getJob().getJobPostId()));
                    jobActivity.setIsSaved(saved);
                }
            }
        }

        // Set client verification for paginated results
        for (JobPostActivity jobActivity : pagedJobs) {
            boolean isClientVerified = false;
            if (jobActivity.getPostedById() != null) {
                try {
                    RecruiterProfile clientProfile = recruiterProfileService
                            .getOne(jobActivity.getPostedById().getUserId()).orElse(null);
                    if (clientProfile != null) {
                        isClientVerified = Boolean.TRUE.equals(clientProfile.getIsVerified());
                        jobActivity.setRecruiterFirstName(
                                clientProfile.getFirstName() != null ? clientProfile.getFirstName() : "");
                        jobActivity.setRecruiterLastName(
                                clientProfile.getLastName() != null ? clientProfile.getLastName() : "");
                        jobActivity.setRecruiterCity(clientProfile.getCity() != null ? clientProfile.getCity() : "");
                        jobActivity.setRecruiterCountry(
                                clientProfile.getCountry() != null ? clientProfile.getCountry() : "");
                        jobActivity.setRecruiterCompany(
                                clientProfile.getCompany() != null ? clientProfile.getCompany() : "");
                        jobActivity.setRecruiterProfilePhoto(clientProfile.getPhotosImagePath());
                        jobActivity.setRecruiterUserAccountId(clientProfile.getUserAccountId());
                    }
                } catch (Exception e) {
                    System.err.println("Error retrieving recruiter profile: " + e.getMessage());
                }
            }
            jobActivity.setPostedByVerified(isClientVerified);
        }

        model.addAttribute("jobPost", pagedJobs);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("pageSize", size);

        // Ensure user profile is handled properly for public access
        if (isLoggedIn && freelancerProfile != null) {
            model.addAttribute("user", freelancerProfile);
            boolean isUserVerified = Boolean.TRUE.equals(freelancerProfile.getIsVerified()) &&
                    currentUser != null && currentUser.isApproved();
            model.addAttribute("isVerified", isUserVerified);
        } else {
            // For public users, no user profile or verification needed
            model.addAttribute("isVerified", false);
        }

        return "find-work";
    }
}
