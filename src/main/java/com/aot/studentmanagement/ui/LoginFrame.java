package com.aot.studentmanagement.ui;

import com.aot.studentmanagement.config.AppConfig;
import com.aot.studentmanagement.database.DatabaseConnection;
import com.aot.studentmanagement.database.DatabaseInitializer;
import com.aot.studentmanagement.service.AuthenticationService;
import com.aot.studentmanagement.service.StudentService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Modern Administrator Authentication Window.
 * Split-panel layout with institutional branding and secure credential
 * verification.
 * 
 * PCC-CS503 Object Oriented Programming | Academy of Technology
 */
public class LoginFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    // Theme Colors
    private static final Color PRIMARY_DARK = new Color(0x1a, 0x23, 0x7e);
    private static final Color GRADIENT_END = new Color(0x0d, 0x47, 0xa1);
    private static final Color BUTTON_BLUE = new Color(0x15, 0x65, 0xc0);
    private static final Color ACCENT_TEAL = new Color(0x00, 0x89, 0x7b);
    private static final Color ERROR_RED = new Color(0xd3, 0x2f, 0x2f);
    private static final Color TEXT_MUTED = new Color(0x75, 0x75, 0x75);
    private static final Color CARD_BG = Color.WHITE;
    private static final String FONT_FAMILY = "Segoe UI";

    private final AuthenticationService authService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton showHideButton;
    private JButton loginButton;
    private JLabel errorLabel;
    private JLabel dbStatusLabel;
    private boolean passwordVisible = false;

    public LoginFrame(AuthenticationService authService) {
        this.authService = authService;
        initializeFrame();
        buildUI();
        setupKeyBindings();
        checkDatabaseStatus();
    }

    private void initializeFrame() {
        setTitle("Student Record Management System — Administrator Login");
        setSize(960, 580);
        setMinimumSize(new Dimension(850, 520));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createBrandingPanel(), BorderLayout.WEST);
        mainPanel.add(createFormPanel(), BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    private JPanel createBrandingPanel() {
        JPanel panel = new JPanel() {
            private static final long serialVersionUID = 1L;

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, PRIMARY_DARK, 0, getHeight(), GRADIENT_END);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        panel.setPreferredSize(new Dimension(420, 580));
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(40, 35, 40, 35));

        // Graduation cap icon
        JLabel logoIcon = new JLabel("🎓", SwingConstants.CENTER);
        logoIcon.setFont(new Font(FONT_FAMILY, Font.PLAIN, 65));
        logoIcon.setForeground(Color.WHITE);
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(logoIcon);

        panel.add(Box.createVerticalStrut(15));

        // System Title
        JLabel title = new JLabel("SRMS Portal", SwingConstants.CENTER);
        title.setFont(new Font(FONT_FAMILY, Font.BOLD, 26));
        title.setForeground(Color.WHITE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(title);

        JLabel subtitle = new JLabel("Student Record Management System", SwingConstants.CENTER);
        subtitle.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        subtitle.setForeground(new Color(0xbb, 0xde, 0xfb));
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(subtitle);

        panel.add(Box.createVerticalStrut(25));

        // Separator
        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 60));
        sep.setMaximumSize(new Dimension(300, 1));
        panel.add(sep);

        panel.add(Box.createVerticalStrut(25));

        // Institutional Details
        panel.add(createMetaLabel("Institution:", AppConfig.getInstitution()));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createMetaLabel("Department:", AppConfig.getDepartment()));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createMetaLabel("Course:", AppConfig.getCourseCode() + " — " + AppConfig.getCourseName()));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createMetaLabel("Faculty Evaluator:", AppConfig.getFacultyEvaluator()));
        panel.add(Box.createVerticalStrut(8));
        panel.add(createMetaLabel("Academic Session:", AppConfig.getAcademicSession()));

        panel.add(Box.createVerticalGlue());

        // Footer notice
        JLabel footerNote = new JLabel(
                "<html><center><font color='#90caf9' size='2'>Academy of Technology<br>CA3 Application-Based Assessment</font></center></html>",
                SwingConstants.CENTER);
        footerNote.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(footerNote);

        return panel;
    }

    private JPanel createMetaLabel(String header, String val) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setMaximumSize(new Dimension(340, 24));

        JLabel h = new JLabel(header);
        h.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        h.setForeground(new Color(0xbb, 0xde, 0xfb));

        JLabel v = new JLabel(val);
        v.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        v.setForeground(Color.WHITE);

        p.add(h, BorderLayout.WEST);
        p.add(v, BorderLayout.EAST);
        return p;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(CARD_BG);
        panel.setBorder(new EmptyBorder(35, 45, 35, 45));

        // Top Status Bar: DB indicator
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setOpaque(false);

        dbStatusLabel = new JLabel("Checking MySQL...");
        dbStatusLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        dbStatusLabel.setForeground(TEXT_MUTED);
        topBar.add(dbStatusLabel, BorderLayout.EAST);

        panel.add(topBar, BorderLayout.NORTH);

        // Center Content
        JPanel formCenter = new JPanel();
        formCenter.setLayout(new BoxLayout(formCenter, BoxLayout.Y_AXIS));
        formCenter.setOpaque(false);
        formCenter.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel headerTitle = new JLabel("Administrator Sign In");
        headerTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 24));
        headerTitle.setForeground(PRIMARY_DARK);
        formCenter.add(headerTitle);

        JLabel headerSub = new JLabel("Enter your credentials to access the student database.");
        headerSub.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        headerSub.setForeground(TEXT_MUTED);
        formCenter.add(headerSub);

        formCenter.add(Box.createVerticalStrut(25));

        // Username Field
        JLabel lblUser = new JLabel("Username");
        lblUser.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        lblUser.setForeground(new Color(0x33, 0x33, 0x33));
        formCenter.add(lblUser);

        formCenter.add(Box.createVerticalStrut(5));

        usernameField = new JTextField();
        usernameField.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        usernameField.setPreferredSize(new Dimension(380, 40));
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        usernameField.putClientProperty("JTextField.placeholderText", "Enter admin username...");
        formCenter.add(usernameField);

        formCenter.add(Box.createVerticalStrut(15));

        // Password Field with Show/Hide button
        JLabel lblPass = new JLabel("Password");
        lblPass.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        lblPass.setForeground(new Color(0x33, 0x33, 0x33));
        formCenter.add(lblPass);

        formCenter.add(Box.createVerticalStrut(5));

        JPanel passPanel = new JPanel(new BorderLayout(5, 0));
        passPanel.setOpaque(false);
        passPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));

        passwordField = new JPasswordField();
        passwordField.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        passwordField.setPreferredSize(new Dimension(300, 40));
        passwordField.putClientProperty("JTextField.placeholderText", "Enter password...");
        passPanel.add(passwordField, BorderLayout.CENTER);

        showHideButton = new JButton("👁");
        showHideButton.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        showHideButton.setPreferredSize(new Dimension(48, 40));
        showHideButton.setFocusPainted(false);
        showHideButton.setToolTipText("Show / Hide Password");
        showHideButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        showHideButton.addActionListener(e -> togglePasswordVisibility());
        passPanel.add(showHideButton, BorderLayout.EAST);

        formCenter.add(passPanel);

        formCenter.add(Box.createVerticalStrut(10));

        // Error message label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        errorLabel.setForeground(ERROR_RED);
        errorLabel.setVisible(false);
        formCenter.add(errorLabel);

        formCenter.add(Box.createVerticalStrut(10));

        // Sign In Button
        loginButton = new JButton("Sign In ➔");
        loginButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        loginButton.setBackground(BUTTON_BLUE);
        loginButton.setForeground(Color.WHITE);
        loginButton.setPreferredSize(new Dimension(380, 44));
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginButton.setFocusPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> performLogin());
        formCenter.add(loginButton);

        formCenter.add(Box.createVerticalStrut(15));

        // Demo Credentials Auto-Fill Chip Button
        JButton demoChip = new JButton("🔑 Use Demo Credentials (admin )");
        demoChip.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        demoChip.setForeground(PRIMARY_DARK);
        demoChip.setBackground(new Color(0xf0, 0xf4, 0xf8));
        demoChip.setBorder(new CompoundBorder(
                new LineBorder(new Color(0x90, 0xca, 0xf9), 1, true),
                new EmptyBorder(6, 12, 6, 12)));
        demoChip.setFocusPainted(false);
        demoChip.setCursor(new Cursor(Cursor.HAND_CURSOR));
        demoChip.setAlignmentX(Component.CENTER_ALIGNMENT);
        demoChip.addActionListener(e -> {
            usernameField.setText(AppConfig.getDefaultAdminUsername());
            passwordField.setText(AppConfig.getDefaultAdminPassword());
            hideError();
        });
        formCenter.add(demoChip);

        panel.add(formCenter, BorderLayout.CENTER);

        return panel;
    }

    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        if (passwordVisible) {
            passwordField.setEchoChar((char) 0);
            showHideButton.setText("🔒");
        } else {
            passwordField.setEchoChar('•');
            showHideButton.setText("👁");
        }
    }

    private void checkDatabaseStatus() {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return DatabaseConnection.getInstance().testConnection();
            }

            @Override
            protected void done() {
                try {
                    boolean online = get();
                    if (online) {
                        dbStatusLabel.setText("🟢 MySQL Online (" + AppConfig.getDbName() + ")");
                        dbStatusLabel.setForeground(new Color(0x2e, 0x7d, 0x32));
                    } else {
                        dbStatusLabel.setText("🟡 MySQL Disconnected (Check database.properties)");
                        dbStatusLabel.setForeground(new Color(0xc6, 0x28, 0x28));
                    }
                } catch (Exception ignored) {
                    dbStatusLabel.setText("⚪ MySQL Status Unknown");
                }
            }
        };
        worker.execute();
    }

    private void setupKeyBindings() {
        ActionListener loginAction = e -> performLogin();
        usernameField.addActionListener(loginAction);
        passwordField.addActionListener(loginAction);
    }

    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            shakeWindow();
            return;
        }

        loginButton.setEnabled(false);
        loginButton.setText("Authenticating...");

        SwingUtilities.invokeLater(() -> {
            try {
                boolean success = authService.authenticate(username, password);
                if (success) {
                    hideError();
                    dispose();
                    StudentService studentService = new StudentService();
                    DashboardFrame dashboard = new DashboardFrame(authService, studentService);
                    dashboard.setVisible(true);
                } else {
                    showError("Invalid username or password. Please try again.");
                    shakeWindow();
                    passwordField.setText("");
                    passwordField.requestFocusInWindow();
                }
            } catch (Exception ex) {
                showError("Authentication error: " + ex.getMessage());
            } finally {
                loginButton.setEnabled(true);
                loginButton.setText("Sign In ➔");
            }
        });
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void hideError() {
        errorLabel.setText(" ");
        errorLabel.setVisible(false);
    }

    private void shakeWindow() {
        final Point origin = getLocation();
        final int distance = 8;
        final int count = 6;
        Timer timer = new Timer(35, null);
        final int[] step = { 0 };
        timer.addActionListener(e -> {
            if (step[0] >= count) {
                setLocation(origin);
                timer.stop();
            } else {
                int dx = (step[0] % 2 == 0) ? distance : -distance;
                setLocation(origin.x + dx, origin.y);
                step[0]++;
            }
        });
        timer.start();
    }
}
