package com.webapp.jobportal.config;

import com.webapp.jobportal.entity.Users;
import com.webapp.jobportal.entity.UsersType;
import com.webapp.jobportal.repository.UsersRepository;
import com.webapp.jobportal.repository.UsersTypeRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UsersTypeRepository usersTypeRepository;

    @Autowired
    private com.webapp.jobportal.repository.JobSeekerProfileRepository jobSeekerProfileRepository;

    @Autowired
    private com.webapp.jobportal.services.EmailService emailService;

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        try {
            CustomOAuth2User oauth2User = (CustomOAuth2User) authentication.getPrincipal();
            String email = oauth2User.getEmail();
            String name = oauth2User.getName();

            Optional<Users> userOptional = usersRepository.findByEmail(email);
            Users user;

            if (userOptional.isEmpty()) {
                // Register new user
                Users newUser = new Users();
                newUser.setEmail(email);
                newUser.setPassword(java.util.UUID.randomUUID().toString());
                newUser.setActive(true);
                newUser.setApproved(true);
                newUser.setRegistrationDate(new Date());

                // Default to Job Seeker (TypeId 2)
                Optional<UsersType> usersType = usersTypeRepository.findById(2);
                if (usersType.isPresent()) {
                    newUser.setUserTypeId(usersType.get());
                }

                user = usersRepository.save(newUser);

                // Create minimal JobSeekerProfile to avoid redirect loops
                // Similar to UsersService.addNew logic
                System.out.println("Creating default JobSeekerProfile for Google User: " + email);
                com.webapp.jobportal.entity.JobSeekerProfile profile = new com.webapp.jobportal.entity.JobSeekerProfile(
                        user);
                // Set defaults if constructor doesn't
                profile.setFirstName(name);
                profile.setLastName("");
                // We'd need to autowire JobSeekerProfileRepository
                jobSeekerProfileRepository.save(profile);

                // Send Welcome Email
                emailService.sendWelcomeNotification(email, name);

            } else {
                user = userOptional.get();
                if (!user.isActive()) {
                    // CRITICAL: Clear the security context so the user is NOT logged in.
                    SecurityContextHolder.clearContext();
                    if (request.getSession(false) != null) {
                        request.getSession(false).invalidate();
                    }
                    response.sendRedirect("/login?disabled=true");
                    return;
                }
            }

            // --- 2. Inject Authorities into SecurityContext ---
            // Fetch the user's role (e.g. "Freelancer")
            String roleName = user.getUserTypeId().getUserTypeName();
            List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
            authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority(roleName));

            // Create a NEW Authentication token with these authorities
            // We use UsernamePasswordAuthenticationToken or similar, preserving the
            // Principal
            org.springframework.security.authentication.UsernamePasswordAuthenticationToken newAuth = new org.springframework.security.authentication.UsernamePasswordAuthenticationToken(
                    authentication.getPrincipal(),
                    authentication.getCredentials(),
                    authorities);

            // --- 3. Save to Session ---
            // Ensure session is created and context is saved properly for Spring Security
            // 6+
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            // Use HttpSessionSecurityContextRepository to save the context explicitly
            org.springframework.security.web.context.SecurityContextRepository securityContextRepository = new org.springframework.security.web.context.HttpSessionSecurityContextRepository();

            securityContextRepository.saveContext(SecurityContextHolder.getContext(), request, response);

            response.sendRedirect("/dashboard/");

        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("/login?error");
        }
    }
}
