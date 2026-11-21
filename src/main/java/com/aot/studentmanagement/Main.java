package com.aot.studentmanagement;

import com.aot.studentmanagement.config.AppConfig;
import com.aot.studentmanagement.database.DatabaseInitializer;
import com.aot.studentmanagement.database.DatabaseInitializer.InitResult;
import com.aot.studentmanagement.service.AuthenticationService;
import com.aot.studentmanagement.ui.LoginFrame;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Master Application Entry Point for the Student Record Management System.
 * 
 * Course: PCC-CS503 Object Oriented Programming
 * Institution: Academy of Technology
 * Department: Computer Science & Engineering
 * Faculty Evaluator: Dr. Krishna Bhowal
 * Academic Session: 2026–27
 */
public class Main {

    public static void main(String[] args) {
        // Step 1: Initialize Modern FlatLaf Look and Feel
        try {
            FlatLightLaf.setup();
            UIManager.put("Button.arc", 10);
            UIManager.put("Component.arc", 10);
            UIManager.put("TextComponent.arc", 8);
            UIManager.put("ScrollBar.width", 10);
            UIManager.put("ScrollBar.thumbArc", 999);
            UIManager.put("ScrollBar.trackArc", 999);
            UIManager.put("TabbedPane.selectedBackground", Color.WHITE);
            UIManager.put("Table.selectionBackground", new Color(0xe3, 0xf2, 0xfd));
            UIManager.put("Table.selectionForeground", new Color(0x1a, 0x23, 0x7e));
        } catch (Exception e) {
            System.err.println("[Main] FlatLaf setup notice: " + e.getMessage());
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {
            }
        }

        // Step 2: Initialize Database automatically and launch GUI
        SwingUtilities.invokeLater(() -> {
            System.out.println("=============================================================");
            System.out.println("  " + AppConfig.getAppName() + " v" + AppConfig.getAppVersion());
            System.out.println("  Institution: " + AppConfig.getInstitution());
            System.out.println("  Department:  " + AppConfig.getDepartment());
            System.out.println("  Course:      " + AppConfig.getCourseCode() + " — " + AppConfig.getCourseName());
            System.out.println("  Evaluator:   " + AppConfig.getFacultyEvaluator());
            System.out.println("  Session:     " + AppConfig.getAcademicSession());
            System.out.println("=============================================================");

            performDatabaseInitializationWithRetry();

            // Launch Administrator Authentication Frame
            AuthenticationService authService = new AuthenticationService();
            LoginFrame loginFrame = new LoginFrame(authService);
            loginFrame.setVisible(true);
        });
    }

    /**
     * Executes automatic database and table initialization with graceful recovery options.
     */
    private static void performDatabaseInitializationWithRetry() {
        InitResult result = DatabaseInitializer.initialize();

        if (result.isSuccess()) {
            System.out.println("[Main] ✓ Automatic Database Management: " + result.getMessage());
        } else {
            System.err.println("[Main] ✗ Database initialization encountered an issue: " + result.getMessage());

            Object[] options = {"Retry Connection", "Continue to Sign In"};
            int choice = JOptionPane.showOptionDialog(null,
                    "<html><body>"
                            + "<h3 style='color:#b71c1c;'>MySQL Database Initialization Notice</h3>"
                            + "<p>The application could not automatically connect to MySQL server at: <b>"
                            + AppConfig.getDbHost() + ":" + AppConfig.getDbPort() + "</b></p>"
                            + "<p><b>Diagnostic Details:</b><br><pre style='color:#555;'>" + result.getMessage() + "</pre></p>"
                            + "<hr>"
                            + "<p><b>How to resolve:</b><br>"
                            + "1. Ensure MySQL Server service is running on your machine.<br>"
                            + "2. Verify your credentials in <b>database.properties</b>.<br>"
                            + "3. Click <b>'Retry Connection'</b> once MySQL is active.</p>"
                            + "<p><i>Note: The application will automatically create the database and tables upon successful connection.</i></p>"
                            + "</body></html>",
                    "Database Connectivity Notice",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    options,
                    options[0]);

            if (choice == JOptionPane.YES_OPTION) {
                AppConfig.loadConfiguration();
                performDatabaseInitializationWithRetry();
            }
        }
    }
}
