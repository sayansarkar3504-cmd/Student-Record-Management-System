package com.aot.studentmanagement.config;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

/**
 * Application configuration manager.
 * Reads settings from database.properties file, system properties, or environment variables.
 * Provides sensible defaults for local development.
 * 
 * PCC-CS503 Object Oriented Programming | Academy of Technology
 */
public class AppConfig {

    // Application metadata constants
    public static final String APP_NAME = "Student Record Management System";
    public static final String APP_VERSION = "1.0.0";
    public static final String INSTITUTION = "Academy of Technology";
    public static final String DEPARTMENT = "Computer Science & Engineering";
    public static final String COURSE_CODE = "PCC-CS503";
    public static final String COURSE_NAME = "Object Oriented Programming";
    public static final String FACULTY_EVALUATOR = "Dr. Krishna Bhowal";
    public static final String ACADEMIC_SESSION = "2026-27";

    // Default admin credentials (for academic demonstration)
    public static final String DEFAULT_ADMIN_USERNAME = "admin";
    public static final String DEFAULT_ADMIN_PASSWORD = "admin123";

    // Database configuration fields
    private static String dbHost;
    private static int dbPort;
    private static String dbName;
    private static String dbUser;
    private static String dbPassword;

    static {
        loadConfiguration();
    }

    private AppConfig() {
        // Prevent instantiation
    }

    /**
     * Loads or reloads database configuration from database.properties or environment.
     */
    public static synchronized void loadConfiguration() {
        Properties props = new Properties();
        File propFile = new File("database.properties");
        if (propFile.exists()) {
            try (FileInputStream fis = new FileInputStream(propFile)) {
                props.load(fis);
                System.out.println("[AppConfig] Loaded database configuration from " + propFile.getAbsolutePath());
            } catch (Exception e) {
                System.err.println("[AppConfig] Warning: Failed to load database.properties: " + e.getMessage());
            }
        } else {
            System.out.println("[AppConfig] database.properties not found; using environment variables and defaults.");
        }

        dbHost = getConfigValue(props, "DB_HOST", "localhost");
        dbPort = Integer.parseInt(getConfigValue(props, "DB_PORT", "3306"));
        dbName = getConfigValue(props, "DB_NAME", "student_record_db");
        dbUser = getConfigValue(props, "DB_USER", "root");
        dbPassword = getConfigValue(props, "DB_PASSWORD", "");
    }

    /**
     * Reads a configuration value following precedence:
     * 1. System property (-Dkey=value)
     * 2. Environment variable (KEY=value)
     * 3. database.properties file
     * 4. Provided default value
     */
    private static String getConfigValue(Properties props, String key, String defaultValue) {
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.trim().isEmpty()) {
            return sysProp.trim();
        }

        String env = System.getenv(key);
        if (env != null && !env.trim().isEmpty()) {
            return env.trim();
        }

        String propVal = props.getProperty(key);
        if (propVal != null) {
            return propVal.trim();
        }

        return defaultValue;
    }

    public static String getAppName() { return APP_NAME; }
    public static String getAppVersion() { return APP_VERSION; }
    public static String getInstitution() { return INSTITUTION; }
    public static String getDepartment() { return DEPARTMENT; }
    public static String getCourseCode() { return COURSE_CODE; }
    public static String getCourseName() { return COURSE_NAME; }
    public static String getFacultyEvaluator() { return FACULTY_EVALUATOR; }
    public static String getAcademicSession() { return ACADEMIC_SESSION; }
    public static String getDefaultAdminUsername() { return DEFAULT_ADMIN_USERNAME; }
    public static String getDefaultAdminPassword() { return DEFAULT_ADMIN_PASSWORD; }

    public static String getDbHost() { return dbHost; }
    public static int getDbPort() { return dbPort; }
    public static String getDbName() { return dbName; }
    public static String getDbUser() { return dbUser; }
    public static String getDbPassword() { return dbPassword; }

    /**
     * Full MySQL JDBC connection URL for student_record_db.
     */
    public static String getJdbcUrl() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort + "/" + dbName
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    }

    /**
     * Root MySQL JDBC connection URL without database name (used for initial CREATE DATABASE).
     */
    public static String getJdbcUrlWithoutDatabase() {
        return "jdbc:mysql://" + dbHost + ":" + dbPort
                + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&characterEncoding=UTF-8";
    }
}
