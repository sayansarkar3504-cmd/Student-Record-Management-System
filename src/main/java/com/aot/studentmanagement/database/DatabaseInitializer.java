package com.aot.studentmanagement.database;

import com.aot.studentmanagement.config.AppConfig;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Enterprise Database Initializer for Student Record Management System.
 * 
 * Automatically checks MySQL connectivity, creates the database ('student_record_db')
 * if not present, creates the 'students' table with constraints, creates secondary
 * indexes for rapid searching, and verifies schema integrity.
 * 
 * Never drops existing tables or modifies existing student records.
 * PCC-CS503 Object Oriented Programming | Academy of Technology
 */
public class DatabaseInitializer {

    private static boolean lastInitSuccess = false;
    private static String lastStatusMessage = "Not initialized";
    private static int existingRecordCount = 0;

    private DatabaseInitializer() {
        // Prevent instantiation
    }

    /**
     * Diagnostic result container for database initialization.
     */
    public static class InitResult {
        private final boolean success;
        private final String message;
        private final int recordCount;
        private final boolean dbCreated;
        private final boolean tableCreated;

        public InitResult(boolean success, String message, int recordCount, boolean dbCreated, boolean tableCreated) {
            this.success = success;
            this.message = message;
            this.recordCount = recordCount;
            this.dbCreated = dbCreated;
            this.tableCreated = tableCreated;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public int getRecordCount() { return recordCount; }
        public boolean isDbCreated() { return dbCreated; }
        public boolean isTableCreated() { return tableCreated; }

        @Override
        public String toString() {
            return (success ? "✓ " : "✗ ") + message + " (Records: " + recordCount + ")";
        }
    }

    /**
     * Initializes the database environment automatically.
     * 
     * 1. Verifies MySQL server connectivity on configured host and port.
     * 2. Automatically creates database 'student_record_db' if it does not exist.
     * 3. Automatically creates table 'students' with required columns and constraints.
     * 4. Automatically creates performance indexes on 'name' and 'course'.
     * 5. Validates complete schema integrity.
     * 
     * @return InitResult containing detailed status
     */
    public static synchronized InitResult initialize() {
        boolean dbCreated = false;
        boolean tableCreated = false;

        // Step 1: Check JDBC Driver
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            lastInitSuccess = false;
            lastStatusMessage = "MySQL Connector/J Driver not found on classpath: " + e.getMessage();
            System.err.println("[DatabaseInitializer] " + lastStatusMessage);
            return new InitResult(false, lastStatusMessage, 0, false, false);
        }

        // Step 2: Connect to MySQL Server root instance and create database if not present
        String rootUrl = AppConfig.getJdbcUrlWithoutDatabase();
        String user = AppConfig.getDbUser();
        String password = AppConfig.getDbPassword();
        String dbName = AppConfig.getDbName();

        System.out.println("[DatabaseInitializer] Connecting to MySQL server at " + AppConfig.getDbHost() + ":" + AppConfig.getDbPort() + "...");

        try (Connection rootConn = DriverManager.getConnection(rootUrl, user, password);
             Statement stmt = rootConn.createStatement()) {

            String createDbSql = "CREATE DATABASE IF NOT EXISTS " + dbName
                    + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            int rows = stmt.executeUpdate(createDbSql);
            if (rows > 0) {
                dbCreated = true;
                System.out.println("[DatabaseInitializer] Database '" + dbName + "' created successfully.");
            } else {
                System.out.println("[DatabaseInitializer] Database '" + dbName + "' already exists.");
            }

        } catch (SQLException e) {
            lastInitSuccess = false;
            String hint = getTroubleshootingHint(e);
            lastStatusMessage = "Cannot connect to MySQL server at " + AppConfig.getDbHost() + ":" + AppConfig.getDbPort()
                    + "\nError: " + e.getMessage() + "\n" + hint;
            System.err.println("[DatabaseInitializer] " + lastStatusMessage);
            return new InitResult(false, lastStatusMessage, 0, false, false);
        }

        // Step 3: Connect to the application database and create the 'students' table if not present
        String createStudentsTableSql = "CREATE TABLE IF NOT EXISTS students ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "roll_number VARCHAR(20) UNIQUE NOT NULL, "
                + "name VARCHAR(100) NOT NULL, "
                + "course VARCHAR(100) NOT NULL, "
                + "academic_year VARCHAR(20) NOT NULL, "
                + "email VARCHAR(100) NOT NULL, "
                + "phone VARCHAR(20) NOT NULL, "
                + "gender VARCHAR(10) NOT NULL, "
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement()) {

            // Execute table creation
            stmt.executeUpdate(createStudentsTableSql);
            tableCreated = true;
            System.out.println("[DatabaseInitializer] Table 'students' verified/created successfully.");

            // Create secondary indexes for fast searching (ignoring duplicate index warnings)
            createIndexSafely(stmt, "students", "idx_student_name", "name");
            createIndexSafely(stmt, "students", "idx_student_course", "course");

            // Step 4: Verify schema columns
            List<String> missingCols = verifySchemaColumns(conn);
            if (!missingCols.isEmpty()) {
                lastInitSuccess = false;
                lastStatusMessage = "Table 'students' exists but is missing required columns: " + missingCols;
                System.err.println("[DatabaseInitializer] " + lastStatusMessage);
                return new InitResult(false, lastStatusMessage, 0, dbCreated, false);
            }

            // Step 5: Read current record count and seed initial demo data if empty
            try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students")) {
                if (rs.next()) {
                    existingRecordCount = rs.getInt(1);
                }
            }

            if (existingRecordCount == 0) {
                seedSampleDataIfEmpty(conn);
                try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM students")) {
                    if (rs.next()) {
                        existingRecordCount = rs.getInt(1);
                    }
                }
            }

            lastInitSuccess = true;
            lastStatusMessage = "Database and tables verified. (" + existingRecordCount + " student records available)";
            System.out.println("[DatabaseInitializer] ✓ " + lastStatusMessage);
            return new InitResult(true, lastStatusMessage, existingRecordCount, dbCreated, tableCreated);

        } catch (SQLException e) {
            lastInitSuccess = false;
            lastStatusMessage = "Failed to initialize table 'students': " + e.getMessage();
            System.err.println("[DatabaseInitializer] " + lastStatusMessage);
            return new InitResult(false, lastStatusMessage, 0, dbCreated, false);
        }
    }

    /**
     * Seeds initial academic demonstration records if the table is completely empty.
     */
    private static void seedSampleDataIfEmpty(Connection conn) {
        String insertSql = "INSERT INTO students (roll_number, name, course, academic_year, email, phone, gender) "
                         + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        String[][] samples = {
            {"CSE2024001", "Aarav Sharma", "Computer Science & Engineering", "2026-27", "aarav.sharma@aot.edu.in", "9876543210", "Male"},
            {"CSE2024002", "Priya Patel", "Computer Science & Engineering", "2026-27", "priya.patel@aot.edu.in", "9876543211", "Female"},
            {"ECE2024001", "Rahul Das", "Electronics & Communication Engineering", "2026-27", "rahul.das@aot.edu.in", "9876543212", "Male"},
            {"ME2024001", "Sneha Roy", "Mechanical Engineering", "2026-27", "sneha.roy@aot.edu.in", "9876543213", "Female"},
            {"IT2024001", "Amit Kumar Ghosh", "Information Technology", "2026-27", "amit.ghosh@aot.edu.in", "9876543214", "Male"}
        };

        try (PreparedStatement pstmt = conn.prepareStatement(insertSql)) {
            for (String[] row : samples) {
                pstmt.setString(1, row[0]);
                pstmt.setString(2, row[1]);
                pstmt.setString(3, row[2]);
                pstmt.setString(4, row[3]);
                pstmt.setString(5, row[4]);
                pstmt.setString(6, row[5]);
                pstmt.setString(7, row[6]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            System.out.println("[DatabaseInitializer] Initialized " + samples.length + " academic demonstration records in MySQL.");
        } catch (SQLException e) {
            System.err.println("[DatabaseInitializer] Note on seeding sample records: " + e.getMessage());
        }
    }

    /**
     * Safely creates an index on a table if it does not already exist.
     */
    private static void createIndexSafely(Statement stmt, String table, String indexName, String column) {
        try {
            stmt.executeUpdate("CREATE INDEX " + indexName + " ON " + table + " (" + column + ")");
            System.out.println("[DatabaseInitializer] Index '" + indexName + "' created on " + table + "(" + column + ").");
        } catch (SQLException e) {
            // Error code 1061 = Duplicate key name (index already exists), which is expected
            if (e.getErrorCode() != 1061 && !e.getMessage().toLowerCase().contains("duplicate")) {
                System.err.println("[DatabaseInitializer] Note on index " + indexName + ": " + e.getMessage());
            }
        }
    }

    /**
     * Verifies that all 9 required columns exist in the 'students' table.
     */
    private static List<String> verifySchemaColumns(Connection conn) throws SQLException {
        List<String> required = List.of("id", "roll_number", "name", "course", "academic_year", "email", "phone", "gender", "created_at");
        List<String> present = new ArrayList<>();

        DatabaseMetaData meta = conn.getMetaData();
        try (ResultSet rs = meta.getColumns(AppConfig.getDbName(), null, "students", null)) {
            while (rs.next()) {
                present.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }

        List<String> missing = new ArrayList<>();
        for (String req : required) {
            if (!present.contains(req.toLowerCase())) {
                missing.add(req);
            }
        }
        return missing;
    }

    /**
     * Generates a helpful troubleshooting hint based on SQL exception codes.
     */
    private static String getTroubleshootingHint(SQLException e) {
        int code = e.getErrorCode();
        String sqlState = (e.getSQLState() != null) ? e.getSQLState() : "";

        if (code == 1045 || sqlState.equals("28000")) {
            return "Hint: Authentication failed. Please verify 'DB_USER' and 'DB_PASSWORD' in database.properties.";
        }
        if (code == 0 || sqlState.startsWith("08")) {
            return "Hint: MySQL Server is not responding on port " + AppConfig.getDbPort() + ". Please ensure MySQL service is started.";
        }
        return "Hint: Check database.properties in project root.";
    }

    public static boolean isInitialized() {
        return lastInitSuccess;
    }

    public static String getLastStatusMessage() {
        return lastStatusMessage;
    }

    public static int getExistingRecordCount() {
        return existingRecordCount;
    }
}
