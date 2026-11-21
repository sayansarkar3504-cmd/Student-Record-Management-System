-- ============================================================
-- Student Record Management System - Database Setup Script
-- PCC-CS503 Object Oriented Programming Mini Project
-- Academy of Technology | CSE Department
-- ============================================================

-- Create database (if not exists)
CREATE DATABASE IF NOT EXISTS student_record_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Use the database
USE student_record_db;

-- Create students table (if not exists)
CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    roll_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    course VARCHAR(100) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Verify table creation
DESCRIBE students;

-- ============================================================
-- Optional: Insert sample records for testing
-- Uncomment the lines below if you want test data
-- ============================================================
-- INSERT INTO students (roll_number, name, course, academic_year, email, phone, gender)
-- VALUES
--     ('CSE2024001', 'Aarav Sharma', 'Computer Science & Engineering', '2026-27', 'aarav.sharma@aot.edu.in', '9876543210', 'Male'),
--     ('CSE2024002', 'Priya Patel', 'Computer Science & Engineering', '2026-27', 'priya.patel@aot.edu.in', '9876543211', 'Female'),
--     ('ECE2024001', 'Rahul Das', 'Electronics & Communication Engineering', '2026-27', 'rahul.das@aot.edu.in', '9876543212', 'Male'),
--     ('ME2024001', 'Sneha Roy', 'Mechanical Engineering', '2026-27', 'sneha.roy@aot.edu.in', '9876543213', 'Female'),
--     ('CE2024001', 'Amit Kumar', 'Civil Engineering', '2026-27', 'amit.kumar@aot.edu.in', '9876543214', 'Male');

SELECT 'Database setup completed successfully!' AS Status;

