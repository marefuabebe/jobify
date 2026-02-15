package com.webapp.jobportal.controller;

import com.webapp.jobportal.entity.*;
import com.webapp.jobportal.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/ratings")
public class RatingController {

    private final RatingService ratingService;
    private final UsersService usersService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final JobSeekerProfileService jobSeekerProfileService;
    private final EmailService emailService;
    private final NotificationService notificationService;

    @Autowired
    public RatingController(RatingService ratingService, UsersService usersService,
                           JobSeekerApplyService jobSeekerApplyService,
                           JobSeekerProfileService jobSeekerProfileService,
                           EmailService emailService, NotificationService notificationService) {
        this.ratingService = ratingService;
        this.usersService = usersService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.jobSeekerProfileService = jobSeekerProfileService;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @GetMapping("/submit/{projectId}")
    public String showRatingForm(@PathVariable("projectId") int projectId, Model model) {
        Users currentUser = usersService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Only clients can rate freelancers
        if (currentUser.getUserTypeId().getUserTypeId() != 1) {
            return "redirect:/dashboard/";
        }

        JobSeekerApply project = jobSeekerApplyService.getById(projectId);
        if (project == null || !"HIRED".equals(project.getApplicationStatus())) {
            return "redirect:/client-dashboard/projects";
        }

        // Check if already rated
        if (ratingService.hasClientRatedProject(currentUser, project)) {
            return "redirect:/ratings/view/" + project.getUserId().getUserAccountId();
        }

        model.addAttribute("project", project);
        model.addAttribute("freelancer", project.getUserId());
        model.addAttribute("rating", new Rating());
        return "submit-rating";
    }

    @PostMapping("/submit/{projectId}")
    public String submitRating(@PathVariable("projectId") int projectId,
                              @RequestParam("ratingValue") Integer ratingValue,
                              @RequestParam(value = "review", required = false) String review,
                              RedirectAttributes redirectAttributes) {
        Users currentUser = usersService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (currentUser.getUserTypeId().getUserTypeId() != 1) {
            return "redirect:/dashboard/";
        }

        JobSeekerApply project = jobSeekerApplyService.getById(projectId);
        if (project == null || !"HIRED".equals(project.getApplicationStatus())) {
            redirectAttributes.addFlashAttribute("error", "Invalid project or project not completed.");
            return "redirect:/client-dashboard/projects";
        }

        // Check if already rated
        if (ratingService.hasClientRatedProject(currentUser, project)) {
            redirectAttributes.addFlashAttribute("error", "You have already rated this freelancer for this project.");
            return "redirect:/client-dashboard/projects";
        }

        // Validate rating value
        if (ratingValue == null || ratingValue < 1 || ratingValue > 5) {
            redirectAttributes.addFlashAttribute("error", "Rating must be between 1 and 5 stars.");
            return "redirect:/ratings/submit/" + projectId;
        }

        Rating rating = new Rating();
        rating.setFreelancer(project.getUserId());
        rating.setClient(currentUser);
        rating.setProject(project);
        rating.setRatingValue(ratingValue);
        rating.setReview(review);
        rating.setCreatedAt(new Date());

        ratingService.createRating(rating);

        // Send notification to freelancer
        if (project.getUserId() != null && project.getUserId().getUserId() != null) {
            notificationService.createNotification(
                project.getUserId().getUserId(),
                "New Rating Received",
                "You received a " + ratingValue + "-star rating from a client!",
                "SYSTEM",
                rating.getId()
            );

            // Send email notification
            if (project.getUserId().getUserId().getEmail() != null) {
                emailService.sendRatingNotification(
                    project.getUserId().getUserId().getEmail(),
                    ratingValue,
                    currentUser.getEmail()
                );
            }
        }

        redirectAttributes.addFlashAttribute("success", "Rating submitted successfully!");
        return "redirect:/client-dashboard/projects";
    }

    @GetMapping("/view/{freelancerId}")
    public String viewRatings(@PathVariable("freelancerId") int freelancerId, Model model) {
        JobSeekerProfile freelancer = jobSeekerProfileService.getOne(freelancerId).orElse(null);
        if (freelancer == null) {
            return "redirect:/find-talent/";
        }

        List<Rating> ratings = ratingService.getFreelancerRatings(freelancer);
        Double averageRating = ratingService.getAverageRating(freelancer);
        Long ratingCount = ratingService.getRatingCount(freelancer);

        model.addAttribute("freelancer", freelancer);
        model.addAttribute("ratings", ratings);
        model.addAttribute("averageRating", averageRating);
        model.addAttribute("ratingCount", ratingCount);

        return "view-ratings";
    }
}

