package com.aot.studentmanagement.util;

import com.aot.studentmanagement.model.Student;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Utility class providing static validation methods for student data.
 */
public class ValidationUtil {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^(\\+91)?\\d{10}$"
    );

    private static final Pattern NAME_PATTERN = Pattern.compile(
            "^[A-Za-z ]{2,}$"
    );

    private static final Pattern ROLL_NUMBER_PATTERN = Pattern.compile(
            "^[A-Za-z0-9_-]+$"
    );

    private ValidationUtil() {
        // Prevent instantiation
    }

    /**
     * Checks if a string is null or empty (after trimming).
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.trim().isEmpty();
    }

    /**
     * Validates an email address against a standard pattern.
     */
    public static boolean isValidEmail(String email) {
        if (isNullOrEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates a phone number. Accepts 10 digits, optionally prefixed with +91.
     */
    public static boolean isValidPhone(String phone) {
        if (isNullOrEmpty(phone)) return false;
        return PHONE_PATTERN.matcher(phone.trim()).matches();
    }

    /**
     * Validates a name. Must be at least 2 characters, only letters and spaces.
     */
    public static boolean isValidName(String name) {
        if (isNullOrEmpty(name)) return false;
        return NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validates a roll number. Must not be empty and must be alphanumeric.
     */
    public static boolean isValidRollNumber(String roll) {
        if (isNullOrEmpty(roll)) return false;
        return ROLL_NUMBER_PATTERN.matcher(roll.trim()).matches();
    }

    /**
     * Validates all fields of a Student object.
     *
     * @param student the Student to validate
     * @return a list of validation error messages; empty if the student is valid
     */
    public static List<String> validateStudent(Student student) {
        List<String> errors = new ArrayList<>();

        if (student == null) {
            errors.add("Student object cannot be null.");
            return errors;
        }

        // Roll number validation
        if (isNullOrEmpty(student.getRollNumber())) {
            errors.add("Roll number is required.");
        } else if (!isValidRollNumber(student.getRollNumber())) {
            errors.add("Roll number must be alphanumeric.");
        }

        // Name validation
        if (isNullOrEmpty(student.getName())) {
            errors.add("Name is required.");
        } else if (!isValidName(student.getName())) {
            errors.add("Name must contain at least 2 characters and only letters and spaces.");
        }

        // Course validation
        if (isNullOrEmpty(student.getCourse())) {
            errors.add("Course is required.");
        }

        // Academic year validation
        if (isNullOrEmpty(student.getAcademicYear())) {
            errors.add("Academic year is required.");
        }

        // Email validation
        if (isNullOrEmpty(student.getEmail())) {
            errors.add("Email is required.");
        } else if (!isValidEmail(student.getEmail())) {
            errors.add("Email format is invalid.");
        }

        // Phone validation
        if (isNullOrEmpty(student.getPhone())) {
            errors.add("Phone number is required.");
        } else if (!isValidPhone(student.getPhone())) {
            errors.add("Phone number must be 10 digits, optionally prefixed with +91.");
        }

        // Gender validation
        if (isNullOrEmpty(student.getGender())) {
            errors.add("Gender is required.");
        } else {
            String gender = student.getGender().trim().toLowerCase();
            if (!gender.equals("male") && !gender.equals("female") && !gender.equals("other")) {
                errors.add("Gender must be Male, Female, or Other.");
            }
        }

        return errors;
    }
}
