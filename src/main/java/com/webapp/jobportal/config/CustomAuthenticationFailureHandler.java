package com.webapp.jobportal.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory
            .getLogger(CustomAuthenticationFailureHandler.class);

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String username = request.getParameter("username");
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String failureReason = exception.getMessage();

        logger.warn("Login Failed | User: {} | IP: {} | Reason: {} | UA: {}",
                username, ipAddress, failureReason, userAgent);

        boolean isDisabled = false;

        if (exception instanceof DisabledException) {
            isDisabled = true;
        } else if (exception.getCause() instanceof DisabledException) {
            isDisabled = true;
        } else if (exception.getMessage() != null && exception.getMessage().toLowerCase().contains("disabled")) {
            isDisabled = true;
        }

        if (isDisabled) {
            logger.info("Redirecting disabled user {} to /login?disabled=true", username);
            response.sendRedirect("/login?disabled=true");
        } else {
            response.sendRedirect("/login?error=true");
        }
    }
}
