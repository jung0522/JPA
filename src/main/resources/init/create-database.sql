-- Create Database
CREATE DATABASE IF NOT EXISTS goorm_jpa
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Create User
CREATE USER IF NOT EXISTS 'goorm-jpa'@'localhost' IDENTIFIED BY '1234';
CREATE USER IF NOT EXISTS 'goorm-jpa'@'%' IDENTIFIED BY '1234';

-- Grant Privileges
GRANT ALL PRIVILEGES ON goorm_jpa.* TO 'goorm-jpa'@'localhost';
GRANT ALL PRIVILEGES ON goorm_jpa.* TO 'goorm-jpa'@'%';

-- Apply Changes
FLUSH PRIVILEGES;
