-- 1. Create the database (if it doesn't already exist)
CREATE DATABASE IF NOT EXISTS electricity_billing;

-- 2. Tell MySQL to use this database for the following commands
USE electricity_billing;

-- 3. Create the table for storing user login info
-- We store a username (which must be unique) and a password
CREATE TABLE IF NOT EXISTS users (
    username VARCHAR(50) PRIMARY KEY,
    password VARCHAR(50) NOT NULL
);

-- 4. Insert a default administrator account so we can log in
-- Username: 'admin', Password: '12345'
-- If a user named 'admin' already exists, it just updates the password.
INSERT INTO users (username, password) 
VALUES ('admin', '12345')
ON DUPLICATE KEY UPDATE password = '12345';