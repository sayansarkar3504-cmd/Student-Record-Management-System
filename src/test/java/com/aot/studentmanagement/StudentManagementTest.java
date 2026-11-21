package com.aot.studentmanagement;

import com.aot.studentmanagement.config.AppConfig;
import com.aot.studentmanagement.model.Admin;
import com.aot.studentmanagement.model.Student;
import com.aot.studentmanagement.service.AuthenticationService;
import com.aot.studentmanagement.util.PasswordUtil;
import com.aot.studentmanagement.util.ValidationUtil;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Student Record Management System.
 * 
 * Tests that do NOT require MySQL connection are marked and will always pass.
 * Tests that require MySQL connection will fail gracefully if DB is not
 * available.
 * 
 * PCC-CS503 Object Oriented Programming Mini Project
 * Academy of Technology
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StudentManagementTest {

    // ==========================================
    // 1. Model Tests (No DB Required)
    // ==========================================

    @Test
    @Order(1)
    @DisplayName("Student Model - Encapsulation Test")
    void testStudentModelEncapsulation() {
        Student student = new Student("CSE001", "Rahul Sharma", "Computer Science & Engineering",
                "2026-27", "rahul@aot.edu.in", "9876543210", "Male");

        assertEquals("CSE001", student.getRollNumber());
        assertEquals("Rahul Sharma", student.getName());
        assertEquals("Computer Science & Engineering", student.getCourse());
        assertEquals("2026-27", student.getAcademicYear());
        assertEquals("rahul@aot.edu.in", student.getEmail());
        assertEquals("9876543210", student.getPhone());
        assertEquals("Male", student.getGender());

        // Test setter
        student.setName("Rahul K. Sharma");
        assertEquals("Rahul K. Sharma", student.getName());
    }

    @Test
    @Order(2)
    @DisplayName("Student Model - Equals and HashCode based on Roll Number")
    void testStudentEqualsHashCode() {
        Student s1 = new Student("CSE001", "Student A", "CSE", "2026-27",
                "a@test.com", "1234567890", "Male");
        Student s2 = new Student("CSE001", "Student B", "ECE", "2025-26",
                "b@test.com", "0987654321", "Female");
        Student s3 = new Student("CSE002", "Student C", "CSE", "2026-27",
                "c@test.com", "1111111111", "Male");

        assertEquals(s1, s2, "Students with same roll number should be equal");
        assertNotEquals(s1, s3, "Students with different roll numbers should not be equal");
        assertEquals(s1.hashCode(), s2.hashCode(), "Hash codes should match for equal students");
    }

    @Test
    @Order(3)
    @DisplayName("Student Model - toString Override")
    void testStudentToString() {
        Student student = new Student("CSE001", "Test Student", "CSE",
                "2026-27", "test@test.com", "1234567890", "Male");
        String str = student.toString();
        assertNotNull(str);
        assertTrue(str.contains("CSE001"));
        assertTrue(str.contains("Test Student"));
    }

    @Test
    @Order(4)
    @DisplayName("Admin Model - Constructor and Getters")
    void testAdminModel() {
        Admin admin = new Admin("admin", "admin123", "Administrator", "ADMIN");
        assertEquals("admin", admin.getUsername());
        assertEquals("admin123", admin.getPassword());
        assertEquals("Administrator", admin.getFullName());
        assertEquals("ADMIN", admin.getRole());
    }

    @Test
    @Order(5)
    @DisplayName("Admin Model - toString does not expose password")
    void testAdminToStringNoPassword() {
        Admin admin = new Admin("admin", "secretPassword123", "Admin User", "ADMIN");
        String str = admin.toString();
        assertFalse(str.contains("secretPassword123"),
                "toString should not expose the password");
    }

    // ==========================================
    // 2. Validation Tests (No DB Required)
    // ==========================================

    @Test
    @Order(10)
    @DisplayName("Validation - Valid Email Addresses")
    void testValidEmails() {
        assertTrue(ValidationUtil.isValidEmail("test@example.com"));
        assertTrue(ValidationUtil.isValidEmail("user.name@domain.co.in"));
        assertTrue(ValidationUtil.isValidEmail("student@aot.edu.in"));
    }

    @Test
    @Order(11)
    @DisplayName("Validation - Invalid Email Addresses")
    void testInvalidEmails() {
        assertFalse(ValidationUtil.isValidEmail(""));
        assertFalse(ValidationUtil.isValidEmail(null));
        assertFalse(ValidationUtil.isValidEmail("invalid"));
        assertFalse(ValidationUtil.isValidEmail("@domain.com"));
        assertFalse(ValidationUtil.isValidEmail("user@"));
    }

    @Test
    @Order(12)
    @DisplayName("Validation - Valid Phone Numbers")
    void testValidPhones() {
        assertTrue(ValidationUtil.isValidPhone("9876543210"));
        assertTrue(ValidationUtil.isValidPhone("+919876543210"));
    }

    @Test
    @Order(13)
    @DisplayName("Validation - Invalid Phone Numbers")
    void testInvalidPhones() {
        assertFalse(ValidationUtil.isValidPhone(""));
        assertFalse(ValidationUtil.isValidPhone(null));
        assertFalse(ValidationUtil.isValidPhone("123"));
        assertFalse(ValidationUtil.isValidPhone("abcdefghij"));
    }

    @Test
    @Order(14)
    @DisplayName("Validation - Valid Names")
    void testValidNames() {
        assertTrue(ValidationUtil.isValidName("Rahul Sharma"));
        assertTrue(ValidationUtil.isValidName("Priya Patel"));
        assertTrue(ValidationUtil.isValidName("AB"));
    }

    @Test
    @Order(15)
    @DisplayName("Validation - Invalid Names")
    void testInvalidNames() {
        assertFalse(ValidationUtil.isValidName(""));
        assertFalse(ValidationUtil.isValidName(null));
        assertFalse(ValidationUtil.isValidName("A"));
        assertFalse(ValidationUtil.isValidName("123abc"));
    }

    @Test
    @Order(16)
    @DisplayName("Validation - Valid Roll Numbers")
    void testValidRollNumbers() {
        assertTrue(ValidationUtil.isValidRollNumber("CSE2024001"));
        assertTrue(ValidationUtil.isValidRollNumber("ECE001"));
        assertTrue(ValidationUtil.isValidRollNumber("12345"));
    }

    @Test
    @Order(17)
    @DisplayName("Validation - Invalid Roll Numbers")
    void testInvalidRollNumbers() {
        assertFalse(ValidationUtil.isValidRollNumber(""));
        assertFalse(ValidationUtil.isValidRollNumber(null));
    }

    @Test
    @Order(18)
    @DisplayName("Validation - Null or Empty Check")
    void testNullOrEmpty() {
        assertTrue(ValidationUtil.isNullOrEmpty(null));
        assertTrue(ValidationUtil.isNullOrEmpty(""));
        assertTrue(ValidationUtil.isNullOrEmpty("   "));
        assertFalse(ValidationUtil.isNullOrEmpty("text"));
    }

    @Test
    @Order(19)
    @DisplayName("Validation - Complete Student Validation (Valid)")
    void testValidStudentValidation() {
        Student valid = new Student("CSE001", "Rahul Sharma", "Computer Science & Engineering",
                "2026-27", "rahul@aot.edu.in", "9876543210", "Male");
        List<String> errors = ValidationUtil.validateStudent(valid);
        assertTrue(errors.isEmpty(), "Valid student should have no validation errors. Errors: " + errors);
    }

    @Test
    @Order(20)
    @DisplayName("Validation - Complete Student Validation (Invalid - Empty Fields)")
    void testInvalidStudentValidation() {
        Student invalid = new Student("", "", "", "", "", "", "");
        List<String> errors = ValidationUtil.validateStudent(invalid);
        assertFalse(errors.isEmpty(), "Invalid student should have validation errors");
        assertTrue(errors.size() >= 5, "Should have multiple validation errors");
    }

    @Test
    @Order(21)
    @DisplayName("Validation - Invalid Email in Student")
    void testStudentInvalidEmail() {
        Student student = new Student("CSE001", "Valid Name", "CSE",
                "2026-27", "invalid-email", "9876543210", "Male");
        List<String> errors = ValidationUtil.validateStudent(student);
        assertTrue(errors.stream().anyMatch(e -> e.toLowerCase().contains("email")),
                "Should have email validation error");
    }

    @Test
    @Order(22)
    @DisplayName("Validation - Invalid Phone in Student")
    void testStudentInvalidPhone() {
        Student student = new Student("CSE001", "Valid Name", "CSE",
                "2026-27", "valid@email.com", "123", "Male");
        List<String> errors = ValidationUtil.validateStudent(student);
        assertTrue(errors.stream().anyMatch(e -> e.toLowerCase().contains("phone")),
                "Should have phone validation error");
    }

    // ==========================================
    // 3. Authentication Tests (No DB Required)
    // ==========================================

    @Test
    @Order(30)
    @DisplayName("Authentication - Successful Login")
    void testSuccessfulLogin() {
        AuthenticationService authService = new AuthenticationService();
        boolean result = authService.authenticate("admin", "admin123");
        assertTrue(result, "Login should succeed with correct credentials");
        assertTrue(authService.isLoggedIn());
        assertNotNull(authService.getCurrentAdmin());
        assertEquals("admin", authService.getCurrentAdmin().getUsername());
    }

    @Test
    @Order(31)
    @DisplayName("Authentication - Failed Login (Wrong Password)")
    void testFailedLoginWrongPassword() {
        AuthenticationService authService = new AuthenticationService();
        boolean result = authService.authenticate("admin", "wrongpassword");
        assertFalse(result, "Login should fail with wrong password");
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentAdmin());
    }

    @Test
    @Order(32)
    @DisplayName("Authentication - Failed Login (Wrong Username)")
    void testFailedLoginWrongUsername() {
        AuthenticationService authService = new AuthenticationService();
        boolean result = authService.authenticate("wronguser", "admin123");
        assertFalse(result, "Login should fail with wrong username");
        assertFalse(authService.isLoggedIn());
    }

    @Test
    @Order(33)
    @DisplayName("Authentication - Logout")
    void testLogout() {
        AuthenticationService authService = new AuthenticationService();
        authService.authenticate("admin", "admin123");
        assertTrue(authService.isLoggedIn());

        authService.logout();
        assertFalse(authService.isLoggedIn());
        assertNull(authService.getCurrentAdmin());
    }

    // ==========================================
    // 4. Password Utility Tests (No DB Required)
    // ==========================================

    @Test
    @Order(40)
    @DisplayName("PasswordUtil - Correct Password Verification")
    void testCorrectPasswordVerification() {
        assertTrue(PasswordUtil.verifyPassword("admin123", "admin123"));
    }

    @Test
    @Order(41)
    @DisplayName("PasswordUtil - Incorrect Password Verification")
    void testIncorrectPasswordVerification() {
        assertFalse(PasswordUtil.verifyPassword("wrong", "admin123"));
    }

    // ==========================================
    // 5. Configuration Tests (No DB Required)
    // ==========================================

    @Test
    @Order(50)
    @DisplayName("AppConfig - Default Values")
    void testAppConfigDefaults() {
        assertEquals("Student Record Management System", AppConfig.getAppName());
        assertEquals("1.0.0", AppConfig.getAppVersion());
        assertEquals("Academy of Technology", AppConfig.getInstitution());
        assertEquals("admin", AppConfig.getDefaultAdminUsername());
        assertEquals("admin123", AppConfig.getDefaultAdminPassword());
        assertNotNull(AppConfig.getDbHost());
        assertNotNull(AppConfig.getJdbcUrl());
    }

    @Test
    @Order(51)
    @DisplayName("AppConfig - JDBC URL Format")
    void testJdbcUrlFormat() {
        String url = AppConfig.getJdbcUrl();
        assertTrue(url.startsWith("jdbc:mysql://"), "URL should start with jdbc:mysql://");
        assertTrue(url.contains("student_record_db"), "URL should contain database name");
    }

    // ==========================================
    // 6. Collections and OOP Concept Tests
    // ==========================================

    @Test
    @Order(60)
    @DisplayName("OOP - Student Polymorphism via Object")
    void testPolymorphism() {
        Object student = new Student("CSE001", "Test", "CSE", "2026-27",
                "t@t.com", "9876543210", "Male");
        assertTrue(student instanceof Student);
        assertNotEquals("", student.toString()); // Polymorphic toString
    }

    @Test
    @Order(61)
    @DisplayName("Collections - ArrayList of Students")
    void testStudentCollection() {
        java.util.ArrayList<Student> students = new java.util.ArrayList<>();
        students.add(new Student("CSE001", "A", "CSE", "2026-27", "a@t.com", "9876543210", "Male"));
        students.add(new Student("CSE002", "B", "CSE", "2026-27", "b@t.com", "9876543211", "Female"));
        students.add(new Student("ECE001", "C", "ECE", "2026-27", "c@t.com", "9876543212", "Male"));

        assertEquals(3, students.size());

        // Filter by course using stream (demonstrates Collections usage)
        long cseCount = students.stream()
                .filter(s -> s.getCourse().equals("CSE"))
                .count();
        assertEquals(2, cseCount);
    }

    @Test
    @Order(62)
    @DisplayName("OOP - Method Overloading in Admin Constructor")
    void testMethodOverloading() {
        // Two-arg constructor
        Admin admin1 = new Admin("user1", "pass1");
        assertEquals("user1", admin1.getUsername());

        // Four-arg constructor (overloaded)
        Admin admin2 = new Admin("user2", "pass2", "Full Name", "ADMIN");
        assertEquals("Full Name", admin2.getFullName());
    }
}
