-- Jobify Verification System Update
-- This script enhances the existing database with proper verification constraints and procedures

USE `jobportal`;

-- Add verification status tracking table for admin audit trail
CREATE TABLE IF NOT EXISTS `verification_audit` (
  `audit_id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `profile_type` varchar(20) NOT NULL COMMENT 'recruiter or job_seeker',
  `action` varchar(20) NOT NULL COMMENT 'verified, rejected, pending',
  `admin_id` int NOT NULL,
  `notes` text,
  `timestamp` datetime(6) DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`audit_id`),
  KEY `FK_audit_user` (`user_id`),
  KEY `FK_audit_admin` (`admin_id`),
  CONSTRAINT `FK_audit_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`user_id`),
  CONSTRAINT `FK_audit_admin` FOREIGN KEY (`admin_id`) REFERENCES `users` (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- Add verification document status tracking
ALTER TABLE `recruiter_profile` 
ADD COLUMN IF NOT EXISTS `document_status` varchar(20) DEFAULT 'pending' COMMENT 'pending, approved, rejected',
ADD COLUMN IF NOT EXISTS `business_license_status` varchar(20) DEFAULT 'pending' COMMENT 'pending, approved, rejected',
ADD COLUMN IF NOT EXISTS `verification_notes` text;

ALTER TABLE `job_seeker_profile` 
ADD COLUMN IF NOT EXISTS `document_status` varchar(20) DEFAULT 'pending' COMMENT 'pending, approved, rejected',
ADD COLUMN IF NOT EXISTS `verification_notes` text;

-- Add indexes for better performance on verification queries
CREATE INDEX IF NOT EXISTS `idx_users_approval_status` ON `users` (`is_approved`, `user_type_id`);
CREATE INDEX IF NOT EXISTS `idx_recruiter_verification` ON `recruiter_profile` (`is_verified`, `document_status`);
CREATE INDEX IF NOT EXISTS `idx_job_seeker_verification` ON `job_seeker_profile` (`is_verified`, `document_status`);
CREATE INDEX IF NOT EXISTS `idx_job_approval` ON `job_post_activity` (`is_approved`, `is_active`);

-- Create verification procedures for admins

-- Procedure to verify recruiter profile
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS `VerifyRecruiter`(
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
CREATE PROCEDURE IF NOT EXISTS `VerifyJobSeeker`(
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
CREATE PROCEDURE IF NOT EXISTS `ApproveJobPosting`(
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
CREATE OR REPLACE VIEW `pending_recruiter_verifications` AS
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
CREATE OR REPLACE VIEW `pending_job_seeker_verifications` AS
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
CREATE OR REPLACE VIEW `pending_job_approvals` AS
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
CREATE TRIGGER IF NOT EXISTS `before_job_insert_check_verification`
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
CREATE TRIGGER IF NOT EXISTS `before_application_check_verification`
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
CREATE FUNCTION IF NOT EXISTS `CanUserPostJobs`(p_user_id INT) 
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
CREATE FUNCTION IF NOT EXISTS `CanUserApplyForJobs`(p_user_id INT) 
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

-- Insert sample admin user if not exists
INSERT IGNORE INTO `users` (`user_id`, `email`, `is_active`, `is_approved`, `password`, `registration_date`, `user_type_id`)
VALUES (999, 'admin@jobify.com', 1, 1, '$2a$10$dummy.hash.for.admin', NOW(), 3);

-- Create notification procedures for verification status updates
DELIMITER //
CREATE PROCEDURE IF NOT EXISTS `SendVerificationNotification`(
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

-- Create enhanced chat constraints to ensure only verified users can chat
ALTER TABLE `chat_message` 
ADD CONSTRAINT IF NOT EXISTS `chk_chat_sender_verified` 
CHECK (
    -- Allow admin users to chat anytime
    EXISTS (SELECT 1 FROM users WHERE user_id = sender_id AND user_type_id = 3) OR
    -- Allow verified recruiters
    EXISTS (SELECT 1 FROM users u JOIN recruiter_profile rp ON u.user_id = rp.user_account_id 
            WHERE u.user_id = sender_id AND u.is_approved = 1 AND rp.is_verified = 1) OR
    -- Allow verified job seekers
    EXISTS (SELECT 1 FROM users u JOIN job_seeker_profile jsp ON u.user_id = jsp.user_account_id 
            WHERE u.user_id = sender_id AND u.is_approved = 1 AND jsp.is_verified = 1)
);

ALTER TABLE `chat_message` 
ADD CONSTRAINT IF NOT EXISTS `chk_chat_receiver_verified` 
CHECK (
    -- Allow admin users to receive chats anytime
    EXISTS (SELECT 1 FROM users WHERE user_id = receiver_id AND user_type_id = 3) OR
    -- Allow verified recruiters
    EXISTS (SELECT 1 FROM users u JOIN recruiter_profile rp ON u.user_id = rp.user_account_id 
            WHERE u.user_id = receiver_id AND u.is_approved = 1 AND rp.is_verified = 1) OR
    -- Allow verified job seekers
    EXISTS (SELECT 1 FROM users u JOIN job_seeker_profile jsp ON u.user_id = jsp.user_account_id 
            WHERE u.user_id = receiver_id AND u.is_approved = 1 AND jsp.is_verified = 1)
);

COMMIT;
