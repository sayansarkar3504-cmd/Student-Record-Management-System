package com.aot.studentmanagement.model;

/**
 * Admin model class representing an administrator user.
 */
public class Admin {

    private String username;
    private String password;
    private String fullName;
    private String role;

    /**
     * Default constructor.
     */
    public Admin() {
        this.username = "";
        this.password = "";
        this.fullName = "Administrator";
        this.role = "admin";
    }

    /**
     * Constructor with all fields.
     */
    public Admin(String username, String password, String fullName, String role) {
        this.username = username;
        this.password = password;
        this.fullName = fullName;
        this.role = role;
    }

    /**
     * Constructor with just username and password.
     */
    public Admin(String username, String password) {
        this.username = username;
        this.password = password;
        this.fullName = "";
        this.role = "admin";
    }

    // ======================== Getters and Setters ========================

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    // ======================== Object Overrides ========================

    /**
     * Returns a string representation of the Admin.
     * Password is intentionally excluded for security.
     */
    @Override
    public String toString() {
        return "Admin{" +
                "username='" + username + '\'' +
                ", fullName='" + fullName + '\'' +
                ", role='" + role + '\'' +
                '}';
    }
}
