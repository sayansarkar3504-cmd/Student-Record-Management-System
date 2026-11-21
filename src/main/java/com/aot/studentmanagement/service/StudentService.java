package com.aot.studentmanagement.service;

import com.aot.studentmanagement.dao.StudentDAO;
import com.aot.studentmanagement.model.Student;
import com.aot.studentmanagement.util.ValidationUtil;

import java.util.List;

/**
 * Service Layer coordinating business logic, validation, and data persistence.
 * 
 * PCC-CS503 Object Oriented Programming | Academy of Technology
 */
public class StudentService {

    private final StudentDAO dao;

    public StudentService() {
        this.dao = new StudentDAO();
    }

    public StudentService(StudentDAO dao) {
        this.dao = dao;
    }

    /**
     * Validates and registers a new student.
     * 
     * @param student The student to persist
     * @return null on success, or user-friendly error message
     */
    public String addStudent(Student student) {
        List<String> validationErrors = ValidationUtil.validateStudent(student);
        if (!validationErrors.isEmpty()) {
            return String.join("\n", validationErrors);
        }

        if (dao.isRollNumberExists(student.getRollNumber())) {
            return "Roll Number '" + student.getRollNumber() + "' is already registered.";
        }

        boolean success = dao.addStudent(student);
        if (success) {
            return null; // Success
        } else {
            if (dao.isRollNumberExists(student.getRollNumber())) {
                return "Roll Number '" + student.getRollNumber() + "' is already registered.";
            }
            return "Failed to save student record to MySQL. Please check database connectivity.";
        }
    }

    /**
     * Retrieves all student records.
     */
    public List<Student> getAllStudents() {
        return dao.getAllStudents();
    }

    /**
     * Retrieves a student by database ID.
     */
    public Student getStudentById(int id) {
        return dao.getStudentById(id);
    }

    /**
     * Retrieves a student by roll number.
     */
    public Student getStudentByRollNumber(String rollNumber) {
        return dao.getStudentByRollNumber(rollNumber);
    }

    /**
     * Validates and updates an existing student profile.
     * 
     * @param student The student with updated values
     * @return null on success, or user-friendly error message
     */
    public String updateStudent(Student student) {
        List<String> validationErrors = ValidationUtil.validateStudent(student);
        if (!validationErrors.isEmpty()) {
            return String.join("\n", validationErrors);
        }

        if (dao.isRollNumberExistsExcluding(student.getRollNumber(), student.getId())) {
            return "Roll Number '" + student.getRollNumber() + "' is already assigned to another student.";
        }

        boolean success = dao.updateStudent(student);
        if (success) {
            return null; // Success
        } else {
            return "Failed to update student record in MySQL. Please check database connectivity.";
        }
    }

    /**
     * Deletes a student by database ID.
     */
    public boolean deleteStudent(int id) {
        return dao.deleteStudent(id);
    }

    /**
     * Searches students by keyword and filter category.
     */
    public List<Student> searchStudents(String keyword, String searchBy) {
        return dao.searchStudents(keyword, searchBy);
    }

    /**
     * Total student count.
     */
    public int getTotalStudentCount() {
        return dao.getTotalStudentCount();
    }

    /**
     * Distinct course count.
     */
    public int getDistinctCourseCount() {
        return dao.getDistinctCourseCount();
    }

    /**
     * Recent student records.
     */
    public List<Student> getRecentStudents(int limit) {
        return dao.getRecentStudents(limit);
    }
}
