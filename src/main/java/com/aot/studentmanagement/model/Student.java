package com.aot.studentmanagement.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Student model class demonstrating encapsulation.
 * All fields are private with public getters and setters.
 */
public class Student {

    private int id;
    private String rollNumber;
    private String name;
    private String course;
    private String academicYear;
    private String email;
    private String phone;
    private String gender;
    private LocalDateTime createdAt;

    /**
     * Full constructor with all fields.
     */
    public Student(int id, String rollNumber, String name, String course, String academicYear,
                   String email, String phone, String gender, LocalDateTime createdAt) {
        this.id = id;
        this.rollNumber = rollNumber;
        this.name = name;
        this.course = course;
        this.academicYear = academicYear;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.createdAt = createdAt;
    }

    /**
     * Default constructor — used by DAO for mapping database rows.
     */
    public Student() {
    }

    /**
     * Constructor without id and createdAt — for creating new students.
     */
    public Student(String rollNumber, String name, String course, String academicYear,
                   String email, String phone, String gender) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.course = course;
        this.academicYear = academicYear;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
    }

    // ======================== Getters and Setters ========================

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // ======================== Object Overrides ========================

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String createdAtStr = (createdAt != null) ? createdAt.format(formatter) : "N/A";
        return "Student{" +
                "id=" + id +
                ", rollNumber='" + rollNumber + '\'' +
                ", name='" + name + '\'' +
                ", course='" + course + '\'' +
                ", academicYear='" + academicYear + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", gender='" + gender + '\'' +
                ", createdAt=" + createdAtStr +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(rollNumber, student.rollNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(rollNumber);
    }
}
