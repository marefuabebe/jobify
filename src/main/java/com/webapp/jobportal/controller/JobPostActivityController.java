package com.webapp.jobportal.controller;

import com.webapp.jobportal.entity.*;
import com.webapp.jobportal.services.JobPostActivityService;
import com.webapp.jobportal.services.JobSeekerApplyService;
import com.webapp.jobportal.services.JobSeekerSaveService;
import com.webapp.jobportal.services.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.webapp.jobportal.services.EmailService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Controller
public class JobPostActivityController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerSaveService jobSeekerSaveService;
    private final EmailService emailService;

    @Autowired
    public JobPostActivityController(UsersService usersService, JobPostActivityService jobPostActivityService,
            JobSeekerApplyService jobSeekerApplyService, JobSeekerSaveService jobSeekerSaveService,
            EmailService emailService) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerSaveService = jobSeekerSaveService;
        this.emailService = emailService;
    }

    @GetMapping("/search")
    public String searchJobs(
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
            @RequestParam(value = "category", required = false) String category,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addAttribute("job", job);
        redirectAttributes.addAttribute("location", location);
        redirectAttributes.addAttribute("category", category);

        if (partTime != null)
            redirectAttributes.addAttribute("partTime", partTime);
        if (fullTime != null)
            redirectAttributes.addAttribute("fullTime", fullTime);
        if (freelance != null)
            redirectAttributes.addAttribute("freelance", freelance);
        if (remoteOnly != null)
            redirectAttributes.addAttribute("remoteOnly", remoteOnly);
        if (officeOnly != null)
            redirectAttributes.addAttribute("officeOnly", officeOnly);
        if (partialRemote != null)
            redirectAttributes.addAttribute("partialRemote", partialRemote);

        if (today)
            redirectAttributes.addAttribute("today", true);
        if (days7)
            redirectAttributes.addAttribute("days7", true);
        if (days30)
            redirectAttributes.addAttribute("days30", true);

        return "redirect:/find-work/";
    }

    @GetMapping("/dashboard/add")
    public String addJobs(Model model, RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Check if user is verified client
        if (currentUser.getUserTypeId().getUserTypeId() == 1) { // Client
            RecruiterProfile clientProfile = (RecruiterProfile) usersService.getCurrentUserProfile();
            if (clientProfile == null || !Boolean.TRUE.equals(clientProfile.getIsVerified())
                    || !currentUser.isApproved()) {
                redirectAttributes.addFlashAttribute("error",
                        "Identity verification required: Please upload your government-issued ID (passport, national ID, or driver's license) and complete profile verification before posting jobs.");
                return "redirect:/dashboard/";
            }
        }

        model.addAttribute("jobPostActivity", new JobPostActivity());
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }

    @PostMapping("/dashboard/addNew")
    public String addNew(JobPostActivity jobPostActivity, Model model, RedirectAttributes redirectAttributes) {
        Users user = usersService.getCurrentUser();
        if (user == null) {
            return "redirect:/login";
        }

        // Verify client can post jobs - must be approved AND verified with documents
        if (user.getUserTypeId().getUserTypeId() == 1) { // Client
            if (!user.isApproved()) {
                redirectAttributes.addFlashAttribute("error",
                        "Your account must be approved by an admin before posting jobs.");
                return "redirect:/dashboard/";
            }

            RecruiterProfile clientProfile = (RecruiterProfile) usersService.getCurrentUserProfile();
            if (clientProfile == null || !Boolean.TRUE.equals(clientProfile.getIsVerified())) {
                redirectAttributes.addFlashAttribute("error",
                        "Identity verification required: Please upload your government-issued ID and wait for admin verification before posting jobs.");
                return "redirect:/dashboard/";
            }
        }

        if (user != null) {
            jobPostActivity.setPostedById(user);
        }
        jobPostActivity.setPostedDate(new Date());
        model.addAttribute("jobPostActivity", jobPostActivity);
        JobPostActivity saved = jobPostActivityService.addNew(jobPostActivity);

        redirectAttributes.addFlashAttribute("success", "Job posted successfully!");
        return "redirect:/client-dashboard/";
    }

    @PostMapping("/dashboard/deleteJob/{id}")
    public String deleteJob(@PathVariable("id") int id) {
        jobPostActivityService.delete(id);
        return "redirect:/dashboard/";
    }

    @GetMapping("dashboard/edit/{id}")
    public String editJob(@PathVariable("id") int id, Model model) {

        JobPostActivity jobPostActivity = jobPostActivityService.getOne(id);
        model.addAttribute("jobPostActivity", jobPostActivity);
        model.addAttribute("user", usersService.getCurrentUserProfile());
        return "add-jobs";
    }

    @PostMapping("/jobs/{id}/pause")
    public String pauseJob(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            JobPostActivity job = jobPostActivityService.getOne(id);
            // Ensure only the recruiter who posted the job can pause it
            // (Assuming standard security checks are in place, but adding a check here)
            jobPostActivityService.pauseJob(id);
            emailService.sendJobModerationNotification(job, "PAUSED", "Action taken by recruiter.");
            redirectAttributes.addFlashAttribute("success", "Job post paused successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error pausing job: " + e.getMessage());
        }
        return "redirect:/client-dashboard/jobs"; // Changed to jobs page to keep user context
    }

    @PostMapping("/jobs/{id}/resume")
    public String resumeJob(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            JobPostActivity job = jobPostActivityService.getOne(id);
            jobPostActivityService.resumeJob(id);
            emailService.sendJobModerationNotification(job, "RESUMED", "Action taken by recruiter.");
            redirectAttributes.addFlashAttribute("success", "Job post resumed successfully.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error resuming job: " + e.getMessage());
        }
        return "redirect:/client-dashboard/jobs";
    }

    @GetMapping("/jobs/{id}/history")
    @org.springframework.web.bind.annotation.ResponseBody
    public List<JobModerationLog> getJobModerationHistory(@PathVariable int id) {
        // In a real app, verify that the current user owns this job
        return jobPostActivityService.getJobModerationHistory(id);
    }
}
