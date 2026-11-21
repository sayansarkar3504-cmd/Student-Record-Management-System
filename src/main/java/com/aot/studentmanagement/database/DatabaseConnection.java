package com.aot.studentmanagement.database;

import com.aot.studentmanagement.config.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton database connection manager.
 * Provides methods to obtain JDBC connections using application configuration.
 */
public class DatabaseConnection {

    private static DatabaseConnection instance;

    /**
     * Private constructor — loads the MySQL JDBC driver.
     */
    private DatabaseConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("[DatabaseConnection] MySQL JDBC Driver not found. "
                    + "Ensure mysql-connector-java is on the classpath.");
            System.err.println("[DatabaseConnection] Error: " + e.getMessage());
        }
    }

    /**
     * Returns the singleton instance of DatabaseConnection.
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    /**
     * Creates and returns a new database connection using AppConfig credentials.
     * Static convenience method for DAO classes.
     *
     * @return a new Connection to the configured database
     * @throws SQLException if a connection cannot be established
     */
    public static Connection getConnection() throws SQLException {
        getInstance(); // ensure driver is loaded
        try {
            return DriverManager.getConnection(
                    AppConfig.getJdbcUrl(),
                    AppConfig.getDbUser(),
                    AppConfig.getDbPassword()
            );
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Tests whether a connection to the database can be established.
     *
     * @return true if the connection is successful, false otherwise
     */
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            if (connection != null && !connection.isClosed()) {
                System.out.println("[DatabaseConnection] Database connection test successful.");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[DatabaseConnection] Database connection test failed: " + e.getMessage());
        }
        return false;
    }
}
