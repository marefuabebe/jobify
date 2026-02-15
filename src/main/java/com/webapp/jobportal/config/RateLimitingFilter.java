package com.webapp.jobportal.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.lang.NonNull;

@Component
public class RateLimitingFilter extends OncePerRequestFilter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    // Limits
    private static final int LOGIN_CAPACITY = 5;
    private static final int PAYMENT_CAPACITY = 10;
    private static final Duration REFILL_DURATION = Duration.ofMinutes(1);

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = request.getRemoteAddr();

        if (path.startsWith("/login") || path.startsWith("/payment/")) {
            Bucket bucket = buckets.computeIfAbsent(ip + ":" + getEndpointType(path), k -> createBucket(path));

            if (!bucket.tryConsume()) {
                response.setStatus(429); // Too Many Requests
                response.getWriter().write("Too many requests. Please try again later.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String getEndpointType(String path) {
        if (path.startsWith("/login"))
            return "LOGIN";
        return "PAYMENT";
    }

    private Bucket createBucket(String path) {
        int capacity = path.startsWith("/login") ? LOGIN_CAPACITY : PAYMENT_CAPACITY;
        return new Bucket(capacity, capacity, REFILL_DURATION);
    }

    // Simple Token Bucket implementation
    private static class Bucket {
        private final int capacity;
        private int tokens;
        private final Duration refillDuration;
        private Instant lastRefill;

        public Bucket(int capacity, int tokens, Duration refillDuration) {
            this.capacity = capacity;
            this.tokens = tokens;
            this.refillDuration = refillDuration;
            this.lastRefill = Instant.now();
        }

        public synchronized boolean tryConsume() {
            refill();
            if (tokens > 0) {
                tokens--;
                return true;
            }
            return false;
        }

        private void refill() {
            Instant now = Instant.now();
            // Better logic: standard token bucket refills X tokens per period.
            // Simplified: Refill to full if duration passed.
            // Even better: Proportional refill.

            if (Duration.between(lastRefill, now).compareTo(refillDuration) > 0) {
                tokens = capacity;
                lastRefill = now;
            }
        }
    }
}
