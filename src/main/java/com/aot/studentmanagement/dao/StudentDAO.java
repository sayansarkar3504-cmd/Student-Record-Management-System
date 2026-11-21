package com.aot.studentmanagement.dao;

import com.aot.studentmanagement.database.DatabaseConnection;
import com.aot.studentmanagement.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object (DAO) for Student entity.
 * Executes parameterized SQL queries with PreparedStatement and try-with-resources.
 * 
 * PCC-CS503 Object Oriented Programming | Academy of Technology
 */
public class StudentDAO {

    /**
     * Inserts a new student record into MySQL.
     * 
     * @param student The student to persist
     * @return true if inserted, false otherwise
     */
    public boolean addStudent(Student student) {
        String sql = "INSERT INTO students (roll_number, name, course, academic_year, email, phone, gender) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, student.getRollNumber().trim());
            pstmt.setString(2, student.getName().trim());
            pstmt.setString(3, student.getCourse().trim());
            pstmt.setString(4, student.getAcademicYear().trim());
            pstmt.setString(5, student.getEmail().trim().toLowerCase());
            pstmt.setString(6, student.getPhone().trim());
            pstmt.setString(7, student.getGender().trim());

            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        student.setId(generatedKeys.getInt(1));
                    }
                }
                return true;
            }
            return false;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("[StudentDAO] Duplicate roll number detected: " + student.getRollNumber());
            return false;
        } catch (SQLException e) {
            System.err.println("[StudentDAO] SQL Error adding student: " + e.getMessage());
            return false;
        }
    }

    /**
     * Retrieves all student records ordered by newest ID first.
     */
    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY id DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                students.add(mapRowToStudent(rs));
            }

        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error retrieving all students: " + e.getMessage());
        }
        return students;
    }

    /**
     * Finds a single student by unique ID.
     */
    public Student getStudentById(int id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error retrieving student by ID: " + e.getMessage());
        }
        return null;
    }

    /**
     * Finds a single student by unique roll number.
     */
    public Student getStudentByRollNumber(String rollNumber) {
        String sql = "SELECT * FROM students WHERE roll_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rollNumber.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToStudent(rs);
                }
            }

        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error retrieving student by roll number: " + e.getMessage());
        }
        return null;
    }

    /**
     * Updates an existing student record by primary key ID.
     */
    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET roll_number = ?, name = ?, course = ?, academic_year = ?, "
                   + "email = ?, phone = ?, gender = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, student.getRollNumber().trim());
            pstmt.setString(2, student.getName().trim());
            pstmt.setString(3, student.getCourse().trim());
            pstmt.setString(4, student.getAcademicYear().trim());
            pstmt.setString(5, student.getEmail().trim().toLowerCase());
            pstmt.setString(6, student.getPhone().trim());
            pstmt.setString(7, student.getGender().trim());
            pstmt.setInt(8, student.getId());

            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error updating student ID " + student.getId() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a student record permanently by ID.
     */
    public boolean deleteStudent(int id) {
        String sql = "DELETE FROM students WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();
            return rows > 0;

        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error deleting student ID " + id + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Multi-criteria search supporting Roll Number, Student Name, Course, or Global search.
     */
    public List<Student> searchStudents(String keyword, String searchBy) {
        List<Student> students = new ArrayList<>();
        if (keyword == null) keyword = "";
        keyword = keyword.trim();

        String sql;
        boolean isGlobal = false;

        switch (searchBy.toLowerCase()) {
            case "roll_number":
                sql = "SELECT * FROM students WHERE roll_number LIKE ? ORDER BY id DESC";
                break;
            case "name":
                sql = "SELECT * FROM students WHERE name LIKE ? ORDER BY id DESC";
                break;
            case "course":
                sql = "SELECT * FROM students WHERE course LIKE ? ORDER BY id DESC";
                break;
            case "all":
            default:
                sql = "SELECT * FROM students WHERE roll_number LIKE ? OR name LIKE ? OR course LIKE ? ORDER BY id DESC";
                isGlobal = true;
                break;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String pattern = "%" + keyword + "%";
            if (isGlobal) {
                pstmt.setString(1, pattern);
                pstmt.setString(2, pattern);
                pstmt.setString(3, pattern);
            } else {
                pstmt.setString(1, pattern);
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRowToStudent(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error searching students (" + searchBy + "): " + e.getMessage());
        }
        return students;
    }

    /**
     * Returns total count of students currently stored in MySQL.
     */
    public int getTotalStudentCount() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error getting total count: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Returns count of distinct courses enrolled.
     */
    public int getDistinctCourseCount() {
        String sql = "SELECT COUNT(DISTINCT course) FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error getting distinct courses: " + e.getMessage());
        }
        return 0;
    }

    /**
     * Retrieves the most recent N registered students.
     */
    public List<Student> getRecentStudents(int limit) {
        List<Student> students = new ArrayList<>();
        String sql = "SELECT * FROM students ORDER BY created_at DESC, id DESC LIMIT ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, limit);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    students.add(mapRowToStudent(rs));
                }
            }

        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error retrieving recent students: " + e.getMessage());
        }
        return students;
    }

    /**
     * Checks if a roll number is already assigned.
     */
    public boolean isRollNumberExists(String rollNumber) {
        String sql = "SELECT COUNT(*) FROM students WHERE roll_number = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rollNumber.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error checking roll existence: " + e.getMessage());
        }
        return false;
    }

    /**
     * Checks if a roll number exists for another student (excluding current record ID).
     */
    public boolean isRollNumberExistsExcluding(String rollNumber, int excludeId) {
        String sql = "SELECT COUNT(*) FROM students WHERE roll_number = ? AND id != ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, rollNumber.trim());
            pstmt.setInt(2, excludeId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("[StudentDAO] Error checking duplicate roll on update: " + e.getMessage());
        }
        return false;
    }

    /**
     * Maps a ResultSet row to a strongly typed Student object.
     */
    private Student mapRowToStudent(ResultSet rs) throws SQLException {
        Student student = new Student();
        student.setId(rs.getInt("id"));
        student.setRollNumber(rs.getString("roll_number"));
        student.setName(rs.getString("name"));
        student.setCourse(rs.getString("course"));
        student.setAcademicYear(rs.getString("academic_year"));
        student.setEmail(rs.getString("email"));
        student.setPhone(rs.getString("phone"));
        student.setGender(rs.getString("gender"));

        Timestamp ts = rs.getTimestamp("created_at");
        student.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);

        return student;
    }
}
