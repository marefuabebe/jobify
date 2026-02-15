-- Drop the db first if they exist
DROP DATABASE  IF EXISTS `jobportal`;
CREATE DATABASE `jobportal`;
USE `jobportal`;

CREATE TABLE `users_type` (
  `user_type_id` int NOT NULL AUTO_INCREMENT,
  `user_type_name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`user_type_id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `users_type` VALUES (1,'Client'),(2,'Freelancer'),(3,'Admin');


CREATE TABLE `users` (
  `user_id` int NOT NULL AUTO_INCREMENT,
  `email` varchar(255) DEFAULT NULL,
  `is_active` bit(1) DEFAULT NULL,
  `is_approved` bit(1) DEFAULT b'0',
  `password` varchar(255) DEFAULT NULL,
  `registration_date` datetime(6) DEFAULT NULL,
  `user_type_id` int DEFAULT NULL,
  PRIMARY KEY (`user_id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  KEY `FK5snet2ikvi03wd4rabd40ckdl` (`user_type_id`),
  CONSTRAINT `FK5snet2ikvi03wd4rabd40ckdl` FOREIGN KEY (`user_type_id`) REFERENCES `users_type` (`user_type_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `job_company` (
  `id` int NOT NULL AUTO_INCREMENT,
  `logo` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `job_location` (
  `id` int NOT NULL AUTO_INCREMENT,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `job_seeker_profile` (
  `user_account_id` int NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `employment_type` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `profile_photo` varchar(255) DEFAULT NULL,
  `resume` varchar(255) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `work_authorization` varchar(255) DEFAULT NULL,
  `verification_document` varchar(255) DEFAULT NULL,
  `is_verified` bit(1) DEFAULT b'0',
  PRIMARY KEY (`user_account_id`),
  CONSTRAINT `FKohp1poe14xlw56yxbwu2tpdm7` FOREIGN KEY (`user_account_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `recruiter_profile` (
  `user_account_id` int NOT NULL,
  `city` varchar(255) DEFAULT NULL,
  `company` varchar(255) DEFAULT NULL,
  `country` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) DEFAULT NULL,
  `last_name` varchar(255) DEFAULT NULL,
  `profile_photo` varchar(64) DEFAULT NULL,
  `state` varchar(255) DEFAULT NULL,
  `verification_document` varchar(255) DEFAULT NULL,
  `business_license` varchar(255) DEFAULT NULL,
  `is_verified` bit(1) DEFAULT b'0',
  PRIMARY KEY (`user_account_id`),
  CONSTRAINT `FK42q4eb7jw1bvw3oy83vc05ft6` FOREIGN KEY (`user_account_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `job_post_activity` (
  `job_post_id` int NOT NULL AUTO_INCREMENT,
  `description_of_job` varchar(10000) DEFAULT NULL,
  `job_title` varchar(255) DEFAULT NULL,
  `job_type` varchar(255) DEFAULT NULL,
  `posted_date` datetime(6) DEFAULT NULL,
  `remote` varchar(255) DEFAULT NULL,
  `salary` varchar(255) DEFAULT NULL,
  `salary_min` decimal(10,2) DEFAULT NULL,
  `salary_max` decimal(10,2) DEFAULT NULL,
  `is_approved` bit(1) DEFAULT b'0',
  `is_active` bit(1) DEFAULT b'1',
  `job_company_id` int DEFAULT NULL,
  `job_location_id` int DEFAULT NULL,
  `posted_by_id` int DEFAULT NULL,
  PRIMARY KEY (`job_post_id`),
  KEY `FKpjpv059hollr4tk92ms09s6is` (`job_company_id`),
  KEY `FK44003mnvj29aiijhsc6ftsgxe` (`job_location_id`),
  KEY `FK62yqqbypsq2ik34ngtlw4m9k3` (`posted_by_id`),
  CONSTRAINT `FK44003mnvj29aiijhsc6ftsgxe` FOREIGN KEY (`job_location_id`) REFERENCES `job_location` (`id`),
  CONSTRAINT `FK62yqqbypsq2ik34ngtlw4m9k3` FOREIGN KEY (`posted_by_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FKpjpv059hollr4tk92ms09s6is` FOREIGN KEY (`job_company_id`) REFERENCES `job_company` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `job_seeker_save` (
  `id` int NOT NULL AUTO_INCREMENT,
  `job` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK1vn1w4dxfiavb5q2gu1n0whxo` (`user_id`,`job`),
  KEY `FKpb44x040gkdltxqy9m7jmvvf3` (`job`),
  CONSTRAINT `FK96dyvgd8hmdohqsfdpvyl89mg` FOREIGN KEY (`user_id`) REFERENCES `job_seeker_profile` (`user_account_id`),
  CONSTRAINT `FKpb44x040gkdltxqy9m7jmvvf3` FOREIGN KEY (`job`) REFERENCES `job_post_activity` (`job_post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `job_seeker_apply` (
  `id` int NOT NULL AUTO_INCREMENT,
  `apply_date` datetime(6) DEFAULT NULL,
  `cover_letter` varchar(255) DEFAULT NULL,
  `application_status` varchar(50) DEFAULT 'PENDING',
  `proposed_rate` decimal(10,2) DEFAULT NULL,
  `job` int DEFAULT NULL,
  `user_id` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK8v6qok40anljlhpkc486nsdmu` (`user_id`,`job`),
  KEY `FKmfhx9q4uclbb74vm49lv9dmf4` (`job`),
  CONSTRAINT `FKmfhx9q4uclbb74vm49lv9dmf4` FOREIGN KEY (`job`) REFERENCES `job_post_activity` (`job_post_id`),
  CONSTRAINT `FKs9fftlyxws2ak05q053vi57qv` FOREIGN KEY (`user_id`) REFERENCES `job_seeker_profile` (`user_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


CREATE TABLE `skills` (
  `id` int NOT NULL AUTO_INCREMENT,
  `experience_level` varchar(255) DEFAULT NULL,
  `name` varchar(255) DEFAULT NULL,
  `years_of_experience` varchar(255) DEFAULT NULL,
  `job_seeker_profile` int DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKsjdksau8sat30c00aqh5xf2wh` (`job_seeker_profile`),
  CONSTRAINT `FKsjdksau8sat30c00aqh5xf2wh` FOREIGN KEY (`job_seeker_profile`) REFERENCES `job_seeker_profile` (`user_account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Chat Messages Table
CREATE TABLE `chat_message` (
  `id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  `job_id` int DEFAULT NULL,
  `message` text NOT NULL,
  `timestamp` datetime(6) DEFAULT NULL,
  `is_read` bit(1) DEFAULT b'0',
  PRIMARY KEY (`id`),
  KEY `FK_chat_sender` (`sender_id`),
  KEY `FK_chat_receiver` (`receiver_id`),
  KEY `FK_chat_job` (`job_id`),
  CONSTRAINT `FK_chat_sender` FOREIGN KEY (`sender_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK_chat_receiver` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK_chat_job` FOREIGN KEY (`job_id`) REFERENCES `job_post_activity` (`job_post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Notifications Table
CREATE TABLE `notification` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(255) NOT NULL,
  `message` text NOT NULL,
  `type` varchar(50) DEFAULT NULL,
  `related_id` int DEFAULT NULL,
  `is_read` bit(1) DEFAULT b'0',
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_notification_user` (`user_id`),
  CONSTRAINT `FK_notification_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Add verification document status tracking for recruiters
ALTER TABLE `recruiter_profile` 
ADD COLUMN `document_status` varchar(20) DEFAULT 'pending' COMMENT 'pending, approved, rejected',
ADD COLUMN `business_license_status` varchar(20) DEFAULT 'pending' COMMENT 'pending, approved, rejected',
ADD COLUMN `verification_notes` text;

-- Add verification document status tracking for job seekers
ALTER TABLE `job_seeker_profile` 
ADD COLUMN `document_status` varchar(20) DEFAULT 'pending' COMMENT 'pending, approved, rejected',
ADD COLUMN `verification_notes` text;

-- Add verification status tracking table for admin audit trail
CREATE TABLE `verification_audit` (
  `audit_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `profile_type` varchar(20) NOT NULL COMMENT 'recruiter, job_seeker, or job_post',
  `action` varchar(20) NOT NULL COMMENT 'verified, rejected, pending, approved',
  `admin_id` int NOT NULL,
  `notes` text,
  `timestamp` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`audit_id`),
  KEY `FK_audit_user` (`user_id`),
  KEY `FK_audit_admin` (`admin_id`),
  CONSTRAINT `FK_audit_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK_audit_admin` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Disputes Table
CREATE TABLE `disputes` (
  `id` int NOT NULL AUTO_INCREMENT,
  `reporter_id` int DEFAULT NULL,
  `against_id` int DEFAULT NULL,
  `job_post_id` int DEFAULT NULL,
  `description` text,
  `type` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `posted_date` datetime(6) DEFAULT NULL,
  `resolution_notes` text,
  PRIMARY KEY (`id`),
  KEY `FK_dispute_reporter` (`reporter_id`),
  KEY `FK_dispute_against` (`against_id`),
  KEY `FK_dispute_job` (`job_post_id`),
  CONSTRAINT `FK_dispute_reporter` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK_dispute_against` FOREIGN KEY (`against_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK_dispute_job` FOREIGN KEY (`job_post_id`) REFERENCES `job_post_activity` (`job_post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Payments Table
CREATE TABLE `payments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `payer_id` int DEFAULT NULL,
  `payee_id` int DEFAULT NULL,
  `job_post_id` int DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `service_fee` double DEFAULT NULL,
  `total_amount` double DEFAULT NULL,
  `payment_date` datetime(6) DEFAULT NULL,
  `payment_method` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `stripe_session_id` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_payment_payer` (`payer_id`),
  KEY `FK_payment_payee` (`payee_id`),
  KEY `FK_payment_job` (`job_post_id`),
  CONSTRAINT `FK_payment_payer` FOREIGN KEY (`payer_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK_payment_payee` FOREIGN KEY (`payee_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK_payment_job` FOREIGN KEY (`job_post_id`) REFERENCES `job_post_activity` (`job_post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Rating Table
CREATE TABLE `rating` (
  `id` int NOT NULL AUTO_INCREMENT,
  `freelancer_id` int DEFAULT NULL,
  `client_id` int DEFAULT NULL,
  `project_id` int DEFAULT NULL,
  `rating_value` int DEFAULT NULL,
  `review` text,
  `created_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_rating_freelancer` (`freelancer_id`),
  KEY `FK_rating_client` (`client_id`),
  KEY `FK_rating_project` (`project_id`),
  CONSTRAINT `FK_rating_freelancer` FOREIGN KEY (`freelancer_id`) REFERENCES `job_seeker_profile` (`user_account_id`),
  CONSTRAINT `FK_rating_client` FOREIGN KEY (`client_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK_rating_project` FOREIGN KEY (`project_id`) REFERENCES `job_seeker_apply` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Withdrawals Table
CREATE TABLE `withdrawals` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int DEFAULT NULL,
  `job_post_id` int DEFAULT NULL,
  `amount` double DEFAULT NULL,
  `withdrawal_date` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `method` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FK_withdrawal_user` (`user_id`),
  KEY `FK_withdrawal_job` (`job_post_id`),
  CONSTRAINT `FK_withdrawal_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK_withdrawal_job` FOREIGN KEY (`job_post_id`) REFERENCES `job_post_activity` (`job_post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Add indexes for better performance on verification queries
CREATE INDEX `idx_users_approval_status` ON `users` (`is_approved`, `user_type_id`);
CREATE INDEX `idx_recruiter_verification` ON `recruiter_profile` (`is_verified`, `document_status`);
CREATE INDEX `idx_job_seeker_verification` ON `job_seeker_profile` (`is_verified`, `document_status`);
CREATE INDEX `idx_job_approval` ON `job_post_activity` (`is_approved`, `is_active`);

-- Create verification procedures for admins

-- Procedure to verify recruiter profile
DELIMITER //
CREATE PROCEDURE `VerifyRecruiter`(
    IN p_user_id INT,
    IN p_admin_id INT,
    IN p_approve BOOLEAN,
    IN p_notes TEXT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    IF p_approve = TRUE THEN
        UPDATE recruiter_profile 
        SET is_verified = 1, 
            document_status = 'approved',
            business_license_status = 'approved',
            verification_notes = p_notes
        WHERE user_account_id = p_user_id;
        
        UPDATE users 
        SET is_approved = 1 
        WHERE user_id = p_user_id;
        
        INSERT INTO verification_audit (user_id, profile_type, action, admin_id, notes)
        VALUES (p_user_id, 'recruiter', 'verified', p_admin_id, p_notes);
    ELSE
        UPDATE recruiter_profile 
        SET is_verified = 0, 
            document_status = 'rejected',
            business_license_status = 'rejected',
            verification_notes = p_notes
        WHERE user_account_id = p_user_id;
        
        INSERT INTO verification_audit (user_id, profile_type, action, admin_id, notes)
        VALUES (p_user_id, 'recruiter', 'rejected', p_admin_id, p_notes);
    END IF;
    
    COMMIT;
END //
DELIMITER ;

-- Procedure to verify job seeker profile
DELIMITER //
CREATE PROCEDURE `VerifyJobSeeker`(
    IN p_user_id INT,
    IN p_admin_id INT,
    IN p_approve BOOLEAN,
    IN p_notes TEXT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    IF p_approve = TRUE THEN
        UPDATE job_seeker_profile 
        SET is_verified = 1, 
            document_status = 'approved',
            verification_notes = p_notes
        WHERE user_account_id = p_user_id;
        
        UPDATE users 
        SET is_approved = 1 
        WHERE user_id = p_user_id;
        
        INSERT INTO verification_audit (user_id, profile_type, action, admin_id, notes)
        VALUES (p_user_id, 'job_seeker', 'verified', p_admin_id, p_notes);
    ELSE
        UPDATE job_seeker_profile 
        SET is_verified = 0, 
            document_status = 'rejected',
            verification_notes = p_notes
        WHERE user_account_id = p_user_id;
        
        INSERT INTO verification_audit (user_id, profile_type, action, admin_id, notes)
        VALUES (p_user_id, 'job_seeker', 'rejected', p_admin_id, p_notes);
    END IF;
    
    COMMIT;
END //
DELIMITER ;

-- Procedure to approve job posting
DELIMITER //
CREATE PROCEDURE `ApproveJobPosting`(
    IN p_job_id INT,
    IN p_admin_id INT,
    IN p_approve BOOLEAN,
    IN p_notes TEXT
)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;
    
    START TRANSACTION;
    
    IF p_approve = TRUE THEN
        UPDATE job_post_activity 
        SET is_approved = 1 
        WHERE job_post_id = p_job_id;
        
        INSERT INTO verification_audit (user_id, profile_type, action, admin_id, notes)
        VALUES ((SELECT posted_by_id FROM job_post_activity WHERE job_post_id = p_job_id), 
                'job_post', 'approved', p_admin_id, p_notes);
    ELSE
        UPDATE job_post_activity 
        SET is_approved = 0, 
            is_active = 0
        WHERE job_post_id = p_job_id;
        
        INSERT INTO verification_audit (user_id, profile_type, action, admin_id, notes)
        VALUES ((SELECT posted_by_id FROM job_post_activity WHERE job_post_id = p_job_id), 
                'job_post', 'rejected', p_admin_id, p_notes);
    END IF;
    
    COMMIT;
END //
DELIMITER ;

-- Create views for admin dashboard

-- View for pending recruiter verifications
CREATE VIEW `pending_recruiter_verifications` AS
SELECT 
    u.user_id,
    u.email,
    u.registration_date,
    rp.first_name,
    rp.last_name,
    rp.company,
    rp.verification_document,
    rp.business_license,
    rp.document_status,
    rp.business_license_status
FROM users u
JOIN recruiter_profile rp ON u.user_id = rp.user_account_id
WHERE u.user_type_id = 1 AND u.is_approved = 0 AND rp.is_verified = 0
ORDER BY u.registration_date DESC;

-- View for pending job seeker verifications
CREATE VIEW `pending_job_seeker_verifications` AS
SELECT 
    u.user_id,
    u.email,
    u.registration_date,
    jsp.first_name,
    jsp.last_name,
    jsp.verification_document,
    jsp.document_status,
    jsp.work_authorization
FROM users u
JOIN job_seeker_profile jsp ON u.user_id = jsp.user_account_id
WHERE u.user_type_id = 2 AND u.is_approved = 0 AND jsp.is_verified = 0
ORDER BY u.registration_date DESC;

-- View for pending job approvals
CREATE VIEW `pending_job_approvals` AS
SELECT 
    jpa.job_post_id,
    jpa.job_title,
    jpa.salary,
    jpa.posted_date,
    u.email as posted_by_email,
    rp.company,
    jpa.description_of_job
FROM job_post_activity jpa
JOIN users u ON jpa.posted_by_id = u.user_id
LEFT JOIN recruiter_profile rp ON u.user_id = rp.user_account_id
WHERE jpa.is_approved = 0 AND jpa.is_active = 1
ORDER BY jpa.posted_date DESC;

-- Add constraints to ensure business rules

-- Ensure only verified recruiters can post jobs (trigger)
DELIMITER //
CREATE TRIGGER `before_job_insert_check_verification`
BEFORE INSERT ON `job_post_activity`
FOR EACH ROW
BEGIN
    DECLARE recruiter_verified BOOLEAN;
    
    -- Check if the posting user is a verified recruiter
    SELECT rp.is_verified INTO recruiter_verified
    FROM users u
    JOIN recruiter_profile rp ON u.user_id = rp.user_account_id
    WHERE u.user_id = NEW.posted_by_id AND u.user_type_id = 1;
    
    -- If recruiter exists but is not verified, prevent job posting
    IF recruiter_verified IS NOT NULL AND recruiter_verified = FALSE THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Only verified recruiters can post jobs';
    END IF;
END //
DELIMITER ;

-- Ensure only verified job seekers can apply for jobs (trigger)
DELIMITER //
CREATE TRIGGER `before_application_check_verification`
BEFORE INSERT ON `job_seeker_apply`
FOR EACH ROW
BEGIN
    DECLARE job_seeker_verified BOOLEAN;
    
    -- Check if the applying user is a verified job seeker
    SELECT jsp.is_verified INTO job_seeker_verified
    FROM users u
    JOIN job_seeker_profile jsp ON u.user_id = jsp.user_account_id
    WHERE u.user_id = NEW.user_id AND u.user_type_id = 2;
    
    -- If job seeker exists but is not verified, prevent application
    IF job_seeker_verified IS NOT NULL AND job_seeker_verified = FALSE THEN
        SIGNAL SQLSTATE '45000' 
        SET MESSAGE_TEXT = 'Only verified job seekers can apply for jobs';
    END IF;
END //
DELIMITER ;

-- Create function to check if user can perform actions
DELIMITER //
CREATE FUNCTION `CanUserPostJobs`(p_user_id INT) 
RETURNS BOOLEAN
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE can_post BOOLEAN DEFAULT FALSE;
    
    SELECT COUNT(*) > 0 INTO can_post
    FROM users u
    JOIN recruiter_profile rp ON u.user_id = rp.user_account_id
    WHERE u.user_id = p_user_id 
      AND u.user_type_id = 1 
      AND u.is_approved = 1 
      AND rp.is_verified = 1;
    
    RETURN can_post;
END //
DELIMITER ;

DELIMITER //
CREATE FUNCTION `CanUserApplyForJobs`(p_user_id INT) 
RETURNS BOOLEAN
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE can_apply BOOLEAN DEFAULT FALSE;
    
    SELECT COUNT(*) > 0 INTO can_apply
    FROM users u
    JOIN job_seeker_profile jsp ON u.user_id = jsp.user_account_id
    WHERE u.user_id = p_user_id 
      AND u.user_type_id = 2 
      AND u.is_approved = 1 
      AND jsp.is_verified = 1;
    
    RETURN can_apply;
END //
DELIMITER ;

-- Create notification procedures for verification status updates
DELIMITER //
CREATE PROCEDURE `SendVerificationNotification`(
    IN p_user_id INT,
    IN p_status VARCHAR(20),
    IN p_message TEXT
)
BEGIN
    INSERT INTO `notification` (`user_id`, `title`, `message`, `type`, `created_at`)
    VALUES (p_user_id, 
            CONCAT('Verification ', p_status),
            p_message,
            'verification',
            NOW(6));
END //
DELIMITER ;

-- Insert sample admin user if not exists
INSERT IGNORE INTO users
(user_id, email, is_active, is_approved, password, registration_date, user_type_id)
VALUES
    (999, 'marefu@gmail.com', 1, 1,
     '$2a$10$5eeGFToNkR1bTnuFAZj9J.1U0jmL4l4tDhUDp/7noT.H..v3AohjC',
     NOW(), 3);


-- Testimonials Table
CREATE TABLE IF NOT EXISTS `testimonials` (
  `id` int NOT NULL AUTO_INCREMENT,
  `image_url` varchar(255) DEFAULT NULL,
  `message` text,
  `name` varchar(255) DEFAULT NULL,
  `rating` int DEFAULT NULL,
  `role` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

INSERT INTO `testimonials` (id, name, role, message, rating, image_url) VALUES 
(1, 'John Doe', 'CEO, TechStart', 'Jobify has completely transformed how match with talent. The process is seamless and the quality of candidates is outstanding. Highly recommended!', 5, NULL),
(2, 'Sarah Johnson', 'UX Designer', 'As a freelancer, I''ve found my best long-term clients here. The platform is intuitive, reliable, and actually cares about its users.', 5, NULL),
(3, 'Michael Ross', 'HR Manager', 'The support team is incredible. Any issue I faced was resolved within minutes. Validated users mean I can trust who I work with.', 5, NULL);


