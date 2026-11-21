package com.aot.studentmanagement;

import com.aot.studentmanagement.config.AppConfig;
import com.aot.studentmanagement.dao.StudentDAO;
import com.aot.studentmanagement.database.DatabaseInitializer;
import com.aot.studentmanagement.database.DatabaseInitializer.InitResult;
import com.aot.studentmanagement.model.Student;
import com.aot.studentmanagement.service.AuthenticationService;
import com.aot.studentmanagement.service.StudentService;

import java.util.List;

/**
 * End-to-End Live Database & Service Integration Verification.
 * Verifies automatic database creation, table creation, CRUD operations,
 * duplicate constraints, search filters, and statistics directly on MySQL.
 * 
 * PCC-CS503 Object Oriented Programming | Academy of Technology
 */
public class DatabaseIntegrationTest {

    public static void main(String[] args) {
        System.out.println("============================================================");
        System.out.println("  LIVE MYSQL DATABASE INTEGRATION TEST SUITE               ");
        System.out.println("  Institution: Academy of Technology | Department of CSE    ");
        System.out.println("============================================================\n");

        AppConfig.loadConfiguration();
        System.out.println("Target Database: " + AppConfig.getDbHost() + ":" + AppConfig.getDbPort() + "/" + AppConfig.getDbName());
        System.out.println("Database User:   " + AppConfig.getDbUser());

        int passed = 0;
        int failed = 0;

        // 1. Test Database & Table Auto-Initialization
        System.out.print("Test 1: Database & Table Auto-Initialization ... ");
        InitResult initRes = DatabaseInitializer.initialize();
        if (initRes.isSuccess()) {
            System.out.println("[PASS] ✓ " + initRes.getMessage());
            passed++;
        } else {
            System.out.println("[FAIL] ✗ " + initRes.getMessage());
            failed++;
        }

        StudentService service = new StudentService(new StudentDAO());
        String testRoll = "CSE2024999";

        // Cleanup any previous test artifact
        Student existing = service.getStudentByRollNumber(testRoll);
        if (existing != null) {
            service.deleteStudent(existing.getId());
        }

        // 2. Test Add Student
        System.out.print("Test 2: Add Student to MySQL ... ");
        Student s1 = new Student(testRoll, "Test Integration Student", "Computer Science & Engineering",
                "2026-27", "integration.test@aot.edu.in", "9876543210", "Male");
        String addErr = service.addStudent(s1);
        if (addErr == null) {
            System.out.println("[PASS] ✓ Successfully saved to MySQL");
            passed++;
        } else {
            System.out.println("[FAIL] ✗ Error: " + addErr);
            failed++;
        }

        // 3. Test Duplicate Roll Number Rejection
        System.out.print("Test 3: Reject Duplicate Roll Number ... ");
        Student duplicate = new Student(testRoll, "Another Student", "Information Technology",
                "2026-27", "another@aot.edu.in", "9876543211", "Female");
        String dupErr = service.addStudent(duplicate);
        if (dupErr != null && dupErr.toLowerCase().contains("already")) {
            System.out.println("[PASS] ✓ Correctly rejected duplicate roll number");
            passed++;
        } else {
            System.out.println("[FAIL] ✗ Did not reject duplicate. Result: " + dupErr);
            failed++;
        }

        // 4. Test View / Retrieve Student
        System.out.print("Test 4: Retrieve Student by Roll Number ... ");
        Student fetched = service.getStudentByRollNumber(testRoll);
        if (fetched != null && fetched.getName().equals("Test Integration Student")) {
            System.out.println("[PASS] ✓ Found student with ID: " + fetched.getId());
            passed++;
        } else {
            System.out.println("[FAIL] ✗ Student not found in MySQL");
            failed++;
        }

        // 5. Test Update Student Details
        System.out.print("Test 5: Update Student in MySQL ... ");
        if (fetched != null) {
            fetched.setName("Updated Integration Student");
            fetched.setPhone("9123456789");
            String updateErr = service.updateStudent(fetched);
            Student refetched = service.getStudentById(fetched.getId());
            if (updateErr == null && refetched != null && refetched.getName().equals("Updated Integration Student")) {
                System.out.println("[PASS] ✓ Successfully modified student in MySQL");
                passed++;
            } else {
                System.out.println("[FAIL] ✗ Update failed: " + updateErr);
                failed++;
            }
        } else {
            System.out.println("[SKIPPED] ✗ Previous step failed");
            failed++;
        }

        // 6. Test Search by Roll Number, Name, and Course
        System.out.print("Test 6: Search Queries (Roll, Name, Course) ... ");
        List<Student> searchByRoll = service.searchStudents("CSE2024", "roll_number");
        List<Student> searchByName = service.searchStudents("Updated", "name");
        List<Student> searchByCourse = service.searchStudents("Computer Science", "course");
        if (!searchByRoll.isEmpty() && !searchByName.isEmpty() && !searchByCourse.isEmpty()) {
            System.out.println("[PASS] ✓ Partial search matched across all criteria");
            passed++;
        } else {
            System.out.println("[FAIL] ✗ Search failed to return expected results");
            failed++;
        }

        // 7. Test Dashboard Statistics
        System.out.print("Test 7: Dynamic Dashboard Statistics from MySQL ... ");
        int totalCount = service.getTotalStudentCount();
        int courseCount = service.getDistinctCourseCount();
        if (totalCount > 0 && courseCount > 0) {
            System.out.println("[PASS] ✓ Total Students: " + totalCount + ", Total Courses: " + courseCount);
            passed++;
        } else {
            System.out.println("[FAIL] ✗ Invalid count: Total=" + totalCount + ", Courses=" + courseCount);
            failed++;
        }

        // 8. Test Delete Student
        System.out.print("Test 8: Delete Student from MySQL ... ");
        if (fetched != null) {
            boolean deleted = service.deleteStudent(fetched.getId());
            Student verifyGone = service.getStudentById(fetched.getId());
            if (deleted && verifyGone == null) {
                System.out.println("[PASS] ✓ Successfully deleted test record from MySQL");
                passed++;
            } else {
                System.out.println("[FAIL] ✗ Record was not deleted");
                failed++;
            }
        } else {
            System.out.println("[SKIPPED] ✗ No test student to delete");
            failed++;
        }

        // 9. Test Authentication Service
        System.out.print("Test 9: Administrator Authentication Service ... ");
        AuthenticationService auth = new AuthenticationService();
        boolean validLogin = auth.authenticate("admin", "admin123");
        boolean invalidLogin = !auth.authenticate("admin", "wrong");
        auth.logout();
        boolean loggedOut = !auth.isLoggedIn();
        if (validLogin && invalidLogin && loggedOut) {
            System.out.println("[PASS] ✓ Login validation & logout verified");
            passed++;
        } else {
            System.out.println("[FAIL] ✗ Authentication logic failed");
            failed++;
        }

        System.out.println("\n------------------------------------------------------------");
        System.out.printf("  INTEGRATION SUMMARY: %d Total | %d Passed | %d Failed%n", (passed + failed), passed, failed);
        System.out.println("------------------------------------------------------------");

        if (failed == 0) {
            System.out.println("  >>> ALL LIVE MYSQL INTEGRATION TESTS PASSED 100%! <<<\n");
        } else {
            System.exit(1);
        }
    }
}
