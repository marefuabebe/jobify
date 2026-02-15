package com.webapp.jobportal.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(CustomAuthenticationSuccessHandler.class);

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String username = userDetails.getUsername();

        // Log only safe information
        logger.info("User logged in successfully: {}", username);

        boolean hasAdminRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("Admin"));
        boolean hasFreelancerRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("Freelancer"));
        boolean hasClientRole = authentication.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals("Client"));

        if (hasAdminRole) {
            response.sendRedirect("/admin/dashboard");
        } else if (hasClientRole) {
            response.sendRedirect("/client-dashboard/");
        } else if (hasFreelancerRole) {
            response.sendRedirect("/freelancer-dashboard/");
        } else {
            response.sendRedirect("/");
        }
    }
}
