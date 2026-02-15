package com.webapp.jobportal.controller;

import com.webapp.jobportal.entity.RecruiterProfile;
import com.webapp.jobportal.entity.Users;
import com.webapp.jobportal.repository.UsersRepository;
import com.webapp.jobportal.services.RecruiterProfileService;
import com.webapp.jobportal.util.FileUploadUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Controller
@RequestMapping("/recruiter-profile")
public class RecruiterProfileController {

    private final UsersRepository usersRepository;
    private final RecruiterProfileService recruiterProfileService;
    private final com.webapp.jobportal.services.EmailService emailService;
    private final com.webapp.jobportal.services.NotificationService notificationService;

    @Autowired
    public RecruiterProfileController(UsersRepository usersRepository,
            RecruiterProfileService recruiterProfileService,
            com.webapp.jobportal.services.EmailService emailService,
            com.webapp.jobportal.services.NotificationService notificationService) {
        this.usersRepository = usersRepository;
        this.recruiterProfileService = recruiterProfileService;
        this.emailService = emailService;
        this.notificationService = notificationService;
    }

    @GetMapping("/")
    public String recruiterProfile(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            String currentUsername = authentication.getName();
            Optional<Users> userOpt = usersRepository.findByEmail(currentUsername);
            if (userOpt.isPresent()) {
                Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(userOpt.get().getUserId());
                if (recruiterProfile.isPresent()) {
                    RecruiterProfile profile = recruiterProfile.get();
                    if (profile.getIsVerified() == null) {
                        profile.setIsVerified(false);
                    }
                    // Only consider verified if user is also approved
                    if (!userOpt.get().isApproved()) {
                        profile.setIsVerified(false);
                    }
                    model.addAttribute("profile", profile);
                    model.addAttribute("user", profile);
                } else {
                    RecruiterProfile newProfile = new RecruiterProfile();
                    newProfile.setUserId(userOpt.get());
                    newProfile.setUserAccountId(userOpt.get().getUserId());
                    model.addAttribute("profile", newProfile);
                    model.addAttribute("user", newProfile);
                }
            }
        }
        return "recruiter_profile";
    }

    @GetMapping("/view/{id}")
    public String viewProfile(@org.springframework.web.bind.annotation.PathVariable("id") int id, Model model) {
        Optional<RecruiterProfile> recruiterProfile = recruiterProfileService.getOne(id);

        // If profile exists, use it. If not, check if User exists and is a Client to
        // show placeholder.
        if (recruiterProfile.isPresent() || usersRepository.findById(id).isPresent()) {

            RecruiterProfile profile;
            if (recruiterProfile.isPresent()) {
                profile = recruiterProfile.get();
            } else {
                // Profile missing but User exists - create placeholder
                Users user = usersRepository.findById(id).get();
                profile = new RecruiterProfile(user);
                profile.setUserAccountId(user.getUserId());
                profile.setFirstName("Unknown"); // Placeholder
                profile.setLastName("Client"); // Placeholder
                profile.setCity("Unknown City");
            }

            model.addAttribute("profile", profile);

            // Add current logged-in user for the Navbar
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
                String currentUsername = authentication.getName();
                Optional<Users> userOpt = usersRepository.findByEmail(currentUsername);
                if (userOpt.isPresent()) {
                    Users currentUser = userOpt.get();
                    // Try to get profile if exists, to get proper name
                    if (currentUser.getUserTypeId().getUserTypeId() == 1) {
                        Optional<RecruiterProfile> currentProfile = recruiterProfileService
                                .getOne(currentUser.getUserId());
                        model.addAttribute("user",
                                currentProfile.orElse((RecruiterProfile) new RecruiterProfile(currentUser)));
                    } else {
                        model.addAttribute("user", currentUser);
                    }
                }
            }
            return "recruiter-profile-view";
        }
        return "redirect:/dashboard/";
    }

    @PostMapping("/addNew")
    public String addNew(RecruiterProfile recruiterProfile,
            @RequestParam("image") MultipartFile multipartFile,
            @RequestParam(value = "verificationFrontFile", required = false) MultipartFile verificationFront,
            @RequestParam(value = "verificationBackFile", required = false) MultipartFile verificationBack,
            @RequestParam(value = "businessLicenseFile", required = false) MultipartFile businessLicense,
            Model model,
            org.springframework.web.servlet.mvc.support.RedirectAttributes redirectAttributes) {

        if (!setUserForProfile(recruiterProfile)) {
            model.addAttribute("error", "Authentication failed");
            return "recruiter_profile";
        }

        RecruiterProfile existingProfile = recruiterProfileService.getOne(recruiterProfile.getUserAccountId())
                .orElse(null);

        // Preserve existing verification status if not changing
        if (existingProfile != null) {
            if (recruiterProfile.getVerificationFront() == null) {
                recruiterProfile.setVerificationFront(existingProfile.getVerificationFront());
            }
            if (recruiterProfile.getVerificationBack() == null) {
                recruiterProfile.setVerificationBack(existingProfile.getVerificationBack());
            }
            // Preserve boolean isVerified if files are not re-uploaded?
            // Logic: If documents are uploaded, reset verification to false?
            // Standard practice: changing docs resets verify.
        }

        model.addAttribute("profile", recruiterProfile);
        setFileNames(recruiterProfile, multipartFile, verificationFront, verificationBack, businessLicense);

        try {
            // Reset verification if ANY new verification document is uploaded
            if ((verificationFront != null && !verificationFront.isEmpty()) ||
                    (verificationBack != null && !verificationBack.isEmpty()) ||
                    (businessLicense != null && !businessLicense.isEmpty())) {
                recruiterProfile.setIsVerified(false);
                recruiterProfile.setDocumentStatus("UNDER_REVIEW");
            } else if (existingProfile != null) {
                // Keep existing status if no new docs
                recruiterProfile.setIsVerified(existingProfile.getIsVerified());
                recruiterProfile.setDocumentStatus(existingProfile.getDocumentStatus());
            }

            RecruiterProfile savedUser = recruiterProfileService.addNew(recruiterProfile);
            saveFiles(savedUser, multipartFile, verificationFront, verificationBack, businessLicense);

            if ((verificationFront != null && !verificationFront.isEmpty()) ||
                    (verificationBack != null && !verificationBack.isEmpty()) ||
                    (businessLicense != null && !businessLicense.isEmpty())) {
                redirectAttributes.addFlashAttribute("verificationSuccess",
                        "Verification documents submitted successfully!");
                try {
                    emailService.sendAdminVerificationNotification(savedUser.getUserId().getEmail(), "Recruiter");
                    notificationService.createAdminNotification(savedUser.getUserId().getEmail(), "Recruiter");
                } catch (Exception e) {
                    System.err.println("Failed to send admin notification: " + e.getMessage());
                }
            }
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");

        } catch (Exception ex) {
            model.addAttribute("error", "Failed to save profile. Please try again.");
            return "recruiter_profile";
        }

        return "redirect:/client-dashboard/";
    }

    private boolean setUserForProfile(RecruiterProfile recruiterProfile) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken) {
            return false;
        }

        Optional<Users> userOpt = usersRepository.findByEmail(authentication.getName());
        if (userOpt.isPresent()) {
            Users users = userOpt.get();
            recruiterProfile.setUserId(users);
            recruiterProfile.setUserAccountId(users.getUserId());
            return true;
        }
        return false;
    }

    private void setFileNames(RecruiterProfile recruiterProfile, MultipartFile multipartFile,
            MultipartFile verificationFront, MultipartFile verificationBack, MultipartFile businessLicense) {
        if (isValidFile(multipartFile)) {
            recruiterProfile.setProfilePhoto(StringUtils.cleanPath(multipartFile.getOriginalFilename()));
        }

        if (isValidFile(verificationFront)) {
            recruiterProfile.setVerificationFront(StringUtils.cleanPath(verificationFront.getOriginalFilename()));
        }

        if (isValidFile(verificationBack)) {
            recruiterProfile.setVerificationBack(StringUtils.cleanPath(verificationBack.getOriginalFilename()));
        }

        if (isValidFile(businessLicense)) {
            recruiterProfile.setBusinessLicense(StringUtils.cleanPath(businessLicense.getOriginalFilename()));
        }
    }

    private boolean isValidFile(MultipartFile file) {
        return file != null && file.getOriginalFilename() != null && !file.getOriginalFilename().isEmpty();
    }

    private void saveFiles(RecruiterProfile savedUser, MultipartFile multipartFile,
            MultipartFile verificationFront, MultipartFile verificationBack, MultipartFile businessLicense)
            throws Exception {
        String uploadDir = "photos/recruiter/" + savedUser.getUserAccountId();

        if (multipartFile != null && !multipartFile.isEmpty()) {
            FileUploadUtil.saveFile(uploadDir, savedUser.getProfilePhoto(), multipartFile);
        }
        if (verificationFront != null && !verificationFront.isEmpty()) {
            FileUploadUtil.saveFile(uploadDir, savedUser.getVerificationFront(), verificationFront);
        }
        if (verificationBack != null && !verificationBack.isEmpty()) {
            FileUploadUtil.saveFile(uploadDir, savedUser.getVerificationBack(), verificationBack);
        }
        if (businessLicense != null && !businessLicense.isEmpty()) {
            FileUploadUtil.saveFile(uploadDir, savedUser.getBusinessLicense(), businessLicense);
        }
    }
}