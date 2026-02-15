-- FIXED: Insert sample admin user
-- PROBLEMS IDENTIFIED:
-- 1. Column name mismatch: Backend uses 'userId' but SQL uses 'user_id' 
-- 2. Boolean values: MySQL expects 0/1 for bit fields, not true/false
-- 3. AUTO_INCREMENT: Cannot insert explicit value for AUTO_INCREMENT column
-- 4. Data type consistency: Ensure proper formatting

-- SOLUTION 1: Use INSERT IGNORE with proper column mapping
INSERT IGNORE INTO users 
(email, is_active, is_approved, password, registration_date, user_type_id)
VALUES
    ('marefu@gmail.com', 1, 1,
     '$2a$10$5eeGFToNkR1bTnuFAZj9J.1U0jmL4l4tDhUDp/7noT.H..v3AohjC',
     NOW(), 3);

-- SOLUTION 2: Use INSERT ... ON DUPLICATE KEY UPDATE (Recommended)
INSERT INTO users 
(email, is_active, is_approved, password, registration_date, user_type_id)
VALUES
    ('marefu@gmail.com', 1, 1,
     '$2a$10$5eeGFToNkR1bTnuFAZj9J.1U0jmL4l4tDhUDp/7noT.H..v3AohjC',
     NOW(), 3)
ON DUPLICATE KEY UPDATE 
    email = VALUES(email),
    is_active = VALUES(is_active),
    is_approved = VALUES(is_approved),
    password = VALUES(password),
    registration_date = VALUES(registration_date),
    user_type_id = VALUES(user_type_id);

-- SOLUTION 3: Remove user_id and let AUTO_INCREMENT work (Best Practice)
INSERT INTO users 
(email, is_active, is_approved, password, registration_date, user_type_id)
VALUES
    ('marefu@gmail.com', 1, 1,
     '$2a$10$5eeGFToNkR1bTnuFAZj9J.1U0jmL4l4tDhUDp/7noT.H..v3AohjC',
     NOW(), 3);

-- VERIFICATION QUERY: Check if admin user exists
SELECT * FROM users WHERE email = 'marefu@gmail.com' AND user_type_id = 3;

-- CLEANUP: Remove existing admin user if needed (Use with caution)
-- DELETE FROM users WHERE email = 'marefu@gmail.com' AND user_type_id = 3;
