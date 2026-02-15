package com.webapp.jobportal;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordGenerator {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String password = "marefu@@3854";
        String encodedPassword = encoder.encode(password);

        System.out.println("Encoded password for 'marefu@@3854': " + encodedPassword);

        // Test if it matches
        boolean matches = encoder.matches("marefu@@3854", encodedPassword);
        System.out.println("Password matches: " + matches);
    }
}
