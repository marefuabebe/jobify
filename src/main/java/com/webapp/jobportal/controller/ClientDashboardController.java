package com.webapp.jobportal.controller;

import com.webapp.jobportal.entity.*;
import com.webapp.jobportal.repository.JobSeekerProfileRepository;
import com.webapp.jobportal.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/client-dashboard")
public class ClientDashboardController {

    private final UsersService usersService;
    private final JobPostActivityService jobPostActivityService;
    private final JobSeekerApplyService jobSeekerApplyService;
    private final ChatService chatService;
    private final RatingService ratingService;
    private final JobSeekerProfileRepository jobSeekerProfileRepository;
    private final com.webapp.jobportal.repository.PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Autowired
    public ClientDashboardController(UsersService usersService,
            JobPostActivityService jobPostActivityService,
            JobSeekerApplyService jobSeekerApplyService,
            ChatService chatService,
            RatingService ratingService,
            JobSeekerProfileRepository jobSeekerProfileRepository,
            com.webapp.jobportal.repository.PaymentRepository paymentRepository,
            NotificationService notificationService) {
        this.usersService = usersService;
        this.jobPostActivityService = jobPostActivityService;
        this.jobSeekerApplyService = jobSeekerApplyService;
        this.chatService = chatService;
        this.ratingService = ratingService;
        this.jobSeekerProfileRepository = jobSeekerProfileRepository;
        this.paymentRepository = paymentRepository;
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public String clientDashboard(Model model, @RequestParam(value = "q", required = false) String query) {
        Users currentUser = usersService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        // Verify user is a Client
        if (currentUser.getUserTypeId().getUserTypeId() != 1) {
            return "redirect:/dashboard/";
        }

        RecruiterProfile clientProfile = (RecruiterProfile) usersService.getCurrentUserProfile();
        if (clientProfile == null) {
            return "redirect:/recruiter-profile/";
        }

        // Get posted jobs
        List<RecruiterJobsDto> postedJobs = jobPostActivityService.getRecruiterJobs(clientProfile.getUserAccountId());

        // Filter Posted Jobs if query exists
        if (query != null && !query.trim().isEmpty()) {
            String lowerQ = query.toLowerCase().trim();
            postedJobs = postedJobs.stream()
                    .filter(job -> job.getJobTitle().toLowerCase().contains(lowerQ) ||
                            (job.getDescriptionOfJob() != null
                                    && job.getDescriptionOfJob().toLowerCase().contains(lowerQ)))
                    .collect(Collectors.toList());
        }

        // Get all proposals (candidates) for displayed jobs
        List<JobSeekerApply> allProposals = postedJobs.stream()
                .flatMap(job -> {
                    JobPostActivity jobPost = jobPostActivityService.getOne(job.getJobPostId());
                    return jobSeekerApplyService.getJobCandidates(jobPost).stream();
                })
                .collect(Collectors.toList());

        // Get ongoing projects (HIRED)
        // We get ALL ongoing projects regardless of filter, usually ongoing is
        // important to see
        // Or should we filter them too?
        // Let's keep logic: Proposals follow filtered jobs. Ongoing projects could be
        // filtered by name?
        // For now, let's keep ongoing projects linked to the jobs we see, OR just ALL
        // ongoing projects.
        // Let's stick to: Proposals = derived from *filtered* jobs.
        // Ongoing Projects = derived from *filtered* jobs AND hired.

        List<JobSeekerApply> ongoingProjects = allProposals.stream()
                .filter(proposal -> "HIRED".equals(proposal.getApplicationStatus()))
                .collect(Collectors.toList());

        // Get unread messages
        List<ChatMessage> unreadMessages = chatService.getUnreadMessages(currentUser);

        // Get verified freelancers
        List<JobSeekerProfile> verifiedFreelancers = jobSeekerProfileRepository.findAll().stream()
                .filter(profile -> {
                    if (profile.getUserId() == null)
                        return false;
                    Users user = profile.getUserId();
                    if (user.getUserTypeId() == null || user.getUserTypeId().getUserTypeId() != 2)
                        return false;
                    return Boolean.TRUE.equals(profile.getIsVerified());
                })
                .collect(Collectors.toList());

        // Filter Freelancers if query exists
        if (query != null && !query.trim().isEmpty()) {
            String lowerQ = query.toLowerCase().trim();
            verifiedFreelancers = verifiedFreelancers.stream()
                    .filter(f -> (f.getFirstName() != null && f.getFirstName().toLowerCase().contains(lowerQ)) ||
                            (f.getLastName() != null && f.getLastName().toLowerCase().contains(lowerQ)) ||
                            (f.getSkills() != null && f.getSkills().stream()
                                    .anyMatch(s -> s.getName() != null && s.getName().toLowerCase().contains(lowerQ))))
                    .collect(Collectors.toList());
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", clientProfile);
        model.addAttribute("postedJobs", postedJobs);
        model.addAttribute("proposals", allProposals.stream()
                .filter(p -> !"WORK_SUBMITTED".equals(p.getApplicationStatus()))
                .collect(Collectors.toList()));
        model.addAttribute("ongoingProjects", ongoingProjects);
        model.addAttribute("unreadMessagesCount", unreadMessages.size());
        model.addAttribute("isVerified", Boolean.TRUE.equals(clientProfile.getIsVerified()));
        model.addAttribute("verifiedFreelancers", verifiedFreelancers);
        model.addAttribute("query", query);

        // Header Attributes
        long unseenCount = allProposals.stream()
                .filter(p -> !"WORK_SUBMITTED".equals(p.getApplicationStatus()))
                .count();
        long activeCount = allProposals.stream()
                .filter(p -> "HIRED".equals(p.getApplicationStatus()))
                .count();
        model.addAttribute("unseenProposals", (int) unseenCount);
        model.addAttribute("activeProjects", (int) activeCount);

        // Add ratings
        Map<Integer, Double> freelancerRatings = new HashMap<>();
        Map<Integer, Long> freelancerRatingCounts = new HashMap<>();
        List<JobSeekerProfile> allRelevantFreelancers = allProposals.stream()
                .map(JobSeekerApply::getUserId)
                .collect(Collectors.toList());
        allRelevantFreelancers.addAll(ongoingProjects.stream()
                .map(JobSeekerApply::getUserId)
                .collect(Collectors.toList()));
        allRelevantFreelancers.addAll(verifiedFreelancers);
        for (JobSeekerProfile freelancer : allRelevantFreelancers) {
            if (freelancer != null && !freelancerRatings.containsKey(freelancer.getUserAccountId())) {
                Double avgRating = ratingService.getAverageRating(freelancer);
                Long ratingCount = ratingService.getRatingCount(freelancer);
                freelancerRatings.put(freelancer.getUserAccountId(), avgRating != null ? avgRating : 0.0);
                freelancerRatingCounts.put(freelancer.getUserAccountId(), ratingCount != null ? ratingCount : 0L);
            }
        }
        model.addAttribute("freelancerRatings", freelancerRatings);
        model.addAttribute("freelancerRatingCounts", freelancerRatingCounts);

        return "client-dashboard";
    }

    @GetMapping("/jobs")
    public String viewJobs(Model model) {
        Users currentUser = usersService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (currentUser.getUserTypeId().getUserTypeId() != 1) {
            return "redirect:/dashboard/";
        }

        RecruiterProfile clientProfile = (RecruiterProfile) usersService.getCurrentUserProfile();
        if (clientProfile == null) {
            return "redirect:/recruiter-profile/";
        }

        List<RecruiterJobsDto> postedJobs = jobPostActivityService.getRecruiterJobs(clientProfile.getUserAccountId());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", clientProfile);
        model.addAttribute("postedJobs", postedJobs);
        model.addAttribute("isVerified", Boolean.TRUE.equals(clientProfile.getIsVerified()));
        model.addAttribute("backUrl", "/client-dashboard/");

        // Header Attributes
        List<JobSeekerApply> allProposals = postedJobs.stream()
                .flatMap(job -> {
                    JobPostActivity jobPost = jobPostActivityService.getOne(job.getJobPostId());
                    return jobSeekerApplyService.getJobCandidates(jobPost).stream();
                })
                .collect(Collectors.toList());
        long unseenCount = allProposals.stream()
                .filter(p -> !"WORK_SUBMITTED".equals(p.getApplicationStatus()))
                .count();
        long activeCount = allProposals.stream()
                .filter(p -> "HIRED".equals(p.getApplicationStatus()))
                .count();
        model.addAttribute("unseenProposals", (int) unseenCount);
        model.addAttribute("activeProjects", (int) activeCount);

        return "client-jobs";
    }

    @GetMapping("/proposals")
    public String viewProposals(Model model) {
        Users currentUser = usersService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (currentUser.getUserTypeId().getUserTypeId() != 1) {
            return "redirect:/dashboard/";
        }

        RecruiterProfile clientProfile = (RecruiterProfile) usersService.getCurrentUserProfile();
        if (clientProfile == null) {
            return "redirect:/recruiter-profile/";
        }

        List<RecruiterJobsDto> postedJobs = jobPostActivityService.getRecruiterJobs(clientProfile.getUserAccountId());
        List<JobSeekerApply> allProposals = postedJobs.stream()
                .flatMap(job -> {
                    JobPostActivity jobPost = jobPostActivityService.getOne(job.getJobPostId());
                    return jobSeekerApplyService.getJobCandidates(jobPost).stream();
                })
                .collect(Collectors.toList());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", clientProfile);
        model.addAttribute("proposals", allProposals);
        model.addAttribute("isVerified", Boolean.TRUE.equals(clientProfile.getIsVerified()));
        model.addAttribute("backUrl", "/client-dashboard/");

        // Header Attributes
        long unseenCount = allProposals.stream()
                .filter(p -> !"WORK_SUBMITTED".equals(p.getApplicationStatus()))
                .count();
        long activeCount = allProposals.stream()
                .filter(p -> "HIRED".equals(p.getApplicationStatus()))
                .count();
        model.addAttribute("unseenProposals", (int) unseenCount);
        model.addAttribute("activeProjects", (int) activeCount);

        return "client-proposals";
    }

    @GetMapping("/projects")
    public String viewProjects(Model model) {
        Users currentUser = usersService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (currentUser.getUserTypeId().getUserTypeId() != 1) {
            return "redirect:/dashboard/";
        }

        RecruiterProfile clientProfile = (RecruiterProfile) usersService.getCurrentUserProfile();
        if (clientProfile == null) {
            return "redirect:/recruiter-profile/";
        }

        List<RecruiterJobsDto> postedJobs = jobPostActivityService.getRecruiterJobs(clientProfile.getUserAccountId());
        List<JobSeekerApply> ongoingProjects = postedJobs.stream()
                .flatMap(job -> {
                    JobPostActivity jobPost = jobPostActivityService.getOne(job.getJobPostId());
                    return jobSeekerApplyService.getJobCandidates(jobPost).stream()
                            .filter(proposal -> "HIRED".equals(proposal.getApplicationStatus()));
                })
                .collect(Collectors.toList());

        // Check which projects have been rated
        Map<Integer, Boolean> projectRatedMap = new HashMap<>();
        Users currentUserObj = usersService.getCurrentUser();
        for (JobSeekerApply project : ongoingProjects) {
            if (currentUserObj != null) {
                projectRatedMap.put(project.getId(), ratingService.hasClientRatedProject(currentUserObj, project));
            } else {
                projectRatedMap.put(project.getId(), false);
            }
        }

        // Calculate total payments per project
        Map<Integer, Double> projectTotalPaid = new HashMap<>();
        for (JobSeekerApply project : ongoingProjects) {
            Double totalPaid = paymentRepository.findByJob(project.getJob()).stream()
                    .mapToDouble(Payment::getTotalAmount)
                    .sum();
            projectTotalPaid.put(project.getId(), totalPaid);
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", clientProfile);
        model.addAttribute("ongoingProjects", ongoingProjects);
        model.addAttribute("projectRatedMap", projectRatedMap);
        model.addAttribute("projectTotalPaid", projectTotalPaid);
        model.addAttribute("isVerified", Boolean.TRUE.equals(clientProfile.getIsVerified()));
        model.addAttribute("backUrl", "/client-dashboard/");

        // Header Attributes
        long activeCount = ongoingProjects.size();
        List<RecruiterJobsDto> allJobs_h = jobPostActivityService.getRecruiterJobs(clientProfile.getUserAccountId());
        long unseenCount = allJobs_h.stream()
                .flatMap(
                        job -> jobPostActivityService.getOne(job.getJobPostId()) != null
                                ? jobSeekerApplyService
                                        .getJobCandidates(jobPostActivityService.getOne(job.getJobPostId())).stream()
                                : java.util.stream.Stream.empty())
                .filter(p -> !"WORK_SUBMITTED".equals(p.getApplicationStatus()))
                .count();

        model.addAttribute("unseenProposals", (int) unseenCount);
        model.addAttribute("activeProjects", (int) activeCount);

        return "client-projects";
    }

    @GetMapping("/payments")
    public String viewPayments(Model model) {
        Users currentUser = usersService.getCurrentUser();
        if (currentUser == null) {
            return "redirect:/login";
        }

        if (currentUser.getUserTypeId().getUserTypeId() != 1)

        {
            return "redirect:/dashboard/";
        }

        RecruiterProfile clientProfile = (RecruiterProfile) usersService.getCurrentUserProfile();
        if (clientProfile == null) {
            return "redirect:/recruiter-profile/";
        }

        // Get ongoing projects for payment tracking
        List<RecruiterJobsDto> postedJobs = jobPostActivityService.getRecruiterJobs(clientProfile.getUserAccountId());
        List<JobSeekerApply> ongoingProjects = postedJobs.stream()
                .flatMap(job -> {
                    JobPostActivity jobPost = jobPostActivityService.getOne(job.getJobPostId());
                    return jobSeekerApplyService.getJobCandidates(jobPost).stream()
                            .filter(proposal -> "HIRED".equals(proposal.getApplicationStatus()));
                })
                .collect(Collectors.toList());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", clientProfile);
        model.addAttribute("ongoingProjects", ongoingProjects);

        // Calculate total project value
        double totalProjectValue = ongoingProjects.stream()
                .filter(p -> p.getProposedRate() != null)
                .mapToDouble(JobSeekerApply::getProposedRate)
                .sum();
        model.addAttribute("totalProjectValue", totalProjectValue);

        // Calculate total paid
        List<Payment> payments = paymentRepository.findByPayer(currentUser);
        double totalPaid = payments.stream()
                .mapToDouble(Payment::getTotalAmount)
                .sum();
        model.addAttribute("totalPaid", totalPaid);

        model.addAttribute("isVerified", Boolean.TRUE.equals(clientProfile.getIsVerified()));

        // Header Attributes
        model.addAttribute("activeProjects", (int) ongoingProjects.size());
        List<RecruiterJobsDto> allJobs = jobPostActivityService.getRecruiterJobs(clientProfile.getUserAccountId());
        long realUnseenCount = allJobs.stream()
                .flatMap(job -> {
                    JobPostActivity jp = jobPostActivityService.getOne(job.getJobPostId());
                    return jp != null ? jobSeekerApplyService.getJobCandidates(jp).stream()
                            : java.util.stream.Stream.empty();
                })
                .filter(p -> !"WORK_SUBMITTED".equals(p.getApplicationStatus()))
                .count();
        model.addAttribute("unseenProposals", (int) realUnseenCount);

        return "client-payments";
    }

    @PostMapping("/process-payment")
    public String processPayment(@RequestParam("projectId") int projectId,
            @RequestParam("paymentMethod") String paymentMethod,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        Users currentUser = usersService.getCurrentUser();
        if (currentUser == null || currentUser.getUserTypeId().getUserTypeId() != 1) {
            return "redirect:/login";
        }

        JobPostActivity job = jobPostActivityService.getOne(projectId);
        if (job == null) {
            redirectAttributes.addFlashAttribute("error", "Project not found.");
            return "redirect:/client-dashboard/payments";
        }

        // Verify ownership
        if (job.getPostedById().getUserId() != currentUser.getUserId()) {
            redirectAttributes.addFlashAttribute("error", "Unauthorized access.");
            return "redirect:/client-dashboard/payments";
        }

        // Find the hired application
        List<JobSeekerApply> hiredApps = jobSeekerApplyService.getJobCandidates(job).stream()
                .filter(app -> "HIRED".equals(app.getApplicationStatus()))
                .collect(Collectors.toList());

        if (hiredApps.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No active freelancer found for this project.");
            return "redirect:/client-dashboard/payments";
        }

        JobSeekerApply application = hiredApps.get(0); // Assuming one freelancer per job for now
        Users freelancer = application.getUserId().getUserId();

        // Calculate amounts
        Double rate = application.getProposedRate();
        if (rate == null)
            rate = 0.0;
        Double serviceFee = rate * 0.05; // 5% fee
        Double totalAmount = rate + serviceFee;

        // Create Payment Record
        Payment payment = new Payment(
                currentUser,
                freelancer,
                job,
                rate,
                serviceFee,
                totalAmount,
                paymentMethod,
                "COMPLETED");
        paymentRepository.save(payment);

        // Update Application Status (optional - could effectively 'close' the project
        // or just mark verified)
        // application.setApplicationStatus("PAID"); // Or keep as HIRED if project
        // continues
        // jobSeekerApplyService.addNew(application);

        // Notifications
        try {
            // To Freelancer
            notificationService.createNotification(
                    freelancer,
                    "Payment Received: " + job.getJobTitle(),
                    "You have received a payment of $" + String.format("%.2f", rate) + " for " + job.getJobTitle(),
                    "PAYMENT",
                    payment.getId());

            // To Client
            notificationService.createNotification(
                    currentUser,
                    "Payment Successful: " + job.getJobTitle(),
                    "Your payment of $" + String.format("%.2f", totalAmount) + " was processed successfully.",
                    "PAYMENT",
                    payment.getId());

        } catch (Exception e) {
            System.err.println("Failed to send notification: " + e.getMessage());
        }

        redirectAttributes.addFlashAttribute("success",
                "Payment of $" + String.format("%.2f", totalAmount) + " processed successfully!");
        return "redirect:/client-dashboard/payments";
    }
}
