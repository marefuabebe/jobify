package com.webapp.jobportal.config;

import com.webapp.jobportal.entity.Users;
import com.webapp.jobportal.entity.UsersType;
import com.webapp.jobportal.repository.UsersRepository;
import com.webapp.jobportal.repository.UsersTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsersTypeRepository usersTypeRepository;
    
    @Autowired
    private UsersRepository usersRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Check if data already exists
        if (usersTypeRepository.count() > 0) {
            System.out.println("Data already initialized");
            return;
        }
        
        // Create user types
        UsersType client = new UsersType();
        client.setUserTypeName("Client");
        usersTypeRepository.save(client);

        UsersType freelancer = new UsersType();
        freelancer.setUserTypeName("Freelancer");
        usersTypeRepository.save(freelancer);

        UsersType admin = new UsersType();
        admin.setUserTypeName("Admin");
        usersTypeRepository.save(admin);

        // Create admin user using reference
        Users adminUser = new Users();
        adminUser.setEmail("marefu@gmail.com");
        adminUser.setPassword(passwordEncoder.encode("marefu@@3854"));
        adminUser.setIsActive(true);
        adminUser.setApproved(true);
        adminUser.setRegistrationDate(new Date());
        adminUser.setUserTypeId(usersTypeRepository.getReferenceById(3)); // Admin ID = 3
        usersRepository.save(adminUser);

        System.out.println("Jobify platform initialized:");
        System.out.println("- User types: Client, Freelancer, Admin");
        System.out.println("- Admin user: marefu@gmail.com");
        System.out.println("- Database ready for freelancing platform");
    }
}