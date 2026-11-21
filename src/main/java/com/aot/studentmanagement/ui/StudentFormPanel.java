package com.aot.studentmanagement.ui;

import com.aot.studentmanagement.model.Student;
import com.aot.studentmanagement.service.StudentService;
import com.aot.studentmanagement.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.util.List;

/**
 * Registration and modification form for student profiles.
 * Fully validated inputs, responsive layout, and seamless Add/Edit modes.
 * 
 * PCC-CS503 Object Oriented Programming | Academy of Technology
 */
public class StudentFormPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // Theme Colors
    private static final Color PRIMARY_DARK = new Color(0x1a, 0x23, 0x7e);
    private static final Color BUTTON_BLUE = new Color(0x15, 0x65, 0xc0);
    private static final Color SAVE_GREEN = new Color(0x2e, 0x7d, 0x32);
    private static final Color ACCENT_TEAL = new Color(0x00, 0x89, 0x7b);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xe0, 0xe0, 0xe0);
    private static final String FONT_FAMILY = "Segoe UI";

    private final StudentService studentService;
    private final Runnable refreshCallback;

    // Form Components
    private JLabel formTitleLabel;
    private JLabel subtitleLabel;
    private JTextField txtRollNumber;
    private JTextField txtName;
    private JComboBox<String> comboCourse;
    private JComboBox<String> comboYear;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JRadioButton rbMale;
    private JRadioButton rbFemale;
    private JRadioButton rbOther;
    private ButtonGroup genderGroup;

    private JButton btnSave;
    private JButton btnReset;
    private JButton btnCancelEdit;

    // Edit State Tracking
    private boolean editMode = false;
    private int currentEditingStudentId = -1;

    public StudentFormPanel(StudentService studentService, Runnable refreshCallback) {
        this.studentService = studentService;
        this.refreshCallback = refreshCallback;

        setLayout(new BorderLayout());
        setBackground(new Color(0xf8, 0xf9, 0xfa));
        setBorder(new EmptyBorder(20, 30, 20, 30));

        initComponents();
    }

    private void initComponents() {
        // Main container card
        JPanel cardPanel = new JPanel(new BorderLayout(0, 20));
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(25, 40, 25, 40)
        ));

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        formTitleLabel = new JLabel("➕ Student Registration Form");
        formTitleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 22));
        formTitleLabel.setForeground(PRIMARY_DARK);

        subtitleLabel = new JLabel("Enter student details below. Fields marked with (*) are required and strictly validated.");
        subtitleLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        subtitleLabel.setForeground(new Color(0x66, 0x66, 0x66));

        headerPanel.add(formTitleLabel, BorderLayout.NORTH);
        headerPanel.add(Box.createVerticalStrut(5), BorderLayout.CENTER);
        headerPanel.add(subtitleLabel, BorderLayout.SOUTH);
        cardPanel.add(headerPanel, BorderLayout.NORTH);

        // Form fields layout
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Roll Number
        txtRollNumber = createTextField(20, "e.g., CSE2024001");
        addFormField(formGrid, gbc, 0, "Roll Number *:", txtRollNumber, "Unique alphanumeric institutional identifier");

        // Student Name
        txtName = createTextField(20, "e.g., Sayan Sarkar");
        addFormField(formGrid, gbc, 1, "Full Name *:", txtName, "Student's full legal name (minimum 2 characters)");

        // Course
        String[] courses = {
                "-- Select Departmental Stream --",
                "Computer Science & Engineering",
                "Electronics & Communication Engineering",
                "Electrical Engineering",
                "Mechanical Engineering",
                "Civil Engineering",
                "Information Technology"
        };
        comboCourse = new JComboBox<>(courses);
        comboCourse.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        comboCourse.setPreferredSize(new Dimension(340, 38));
        addFormField(formGrid, gbc, 2, "Course / Stream *:", comboCourse, "Departmental B.Tech degree program");

        // Academic Year
        String[] years = {
                "-- Select Academic Year --",
                "2023-24",
                "2024-25",
                "2025-26",
                "2026-27",
                "2027-28"
        };
        comboYear = new JComboBox<>(years);
        comboYear.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        comboYear.setPreferredSize(new Dimension(340, 38));
        comboYear.setSelectedItem("2026-27");
        addFormField(formGrid, gbc, 3, "Academic Session *:", comboYear, "Enrolled academic session");

        // Email
        txtEmail = createTextField(20, "e.g., student@aot.edu.in");
        addFormField(formGrid, gbc, 4, "Email Address *:", txtEmail, "Valid RFC format email address");

        // Phone
        txtPhone = createTextField(20, "e.g., 9876543210");
        addFormField(formGrid, gbc, 5, "Contact Phone *:", txtPhone, "10-digit mobile number (optionally with +91)");

        // Gender (Radio buttons)
        JPanel genderPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        genderPanel.setOpaque(false);
        rbMale = new JRadioButton("Male");
        rbFemale = new JRadioButton("Female");
        rbOther = new JRadioButton("Other");

        Font radioFont = new Font(FONT_FAMILY, Font.PLAIN, 14);
        rbMale.setFont(radioFont);
        rbFemale.setFont(radioFont);
        rbOther.setFont(radioFont);
        rbMale.setOpaque(false);
        rbFemale.setOpaque(false);
        rbOther.setOpaque(false);

        genderGroup = new ButtonGroup();
        genderGroup.add(rbMale);
        genderGroup.add(rbFemale);
        genderGroup.add(rbOther);

        genderPanel.add(rbMale);
        genderPanel.add(rbFemale);
        genderPanel.add(rbOther);
        addFormField(formGrid, gbc, 6, "Gender *:", genderPanel, "Demographic gender classification");

        cardPanel.add(formGrid, BorderLayout.CENTER);

        // Buttons Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        buttonPanel.setOpaque(false);

        btnReset = new JButton("🔄 Reset Form");
        btnReset.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        btnReset.setPreferredSize(new Dimension(135, 40));
        btnReset.setFocusPainted(false);
        btnReset.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnReset.addActionListener(e -> clearForm());

        btnCancelEdit = new JButton("✖ Cancel Edit");
        btnCancelEdit.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        btnCancelEdit.setPreferredSize(new Dimension(135, 40));
        btnCancelEdit.setForeground(new Color(0xd3, 0x2f, 0x2f));
        btnCancelEdit.setFocusPainted(false);
        btnCancelEdit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancelEdit.setVisible(false);
        btnCancelEdit.addActionListener(e -> cancelEdit());

        btnSave = new JButton("💾 Save Student Record");
        btnSave.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        btnSave.setPreferredSize(new Dimension(190, 40));
        btnSave.setBackground(SAVE_GREEN);
        btnSave.setForeground(Color.WHITE);
        btnSave.setFocusPainted(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> saveStudent());

        buttonPanel.add(btnCancelEdit);
        buttonPanel.add(btnReset);
        buttonPanel.add(btnSave);

        cardPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Put card inside a scroll pane for smooth resizing
        JScrollPane scrollPane = new JScrollPane(cardPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(new Color(0xf8, 0xf9, 0xfa));
        add(scrollPane, BorderLayout.CENTER);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, Component comp, String tooltip) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.25;
        JLabel label = new JLabel(labelText);
        label.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        label.setForeground(new Color(0x22, 0x22, 0x22));
        panel.add(label, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.75;
        if (tooltip != null && comp instanceof JComponent) {
            ((JComponent) comp).setToolTipText(tooltip);
        }
        panel.add(comp, gbc);
    }

    private JTextField createTextField(int columns, String placeholder) {
        JTextField tf = new JTextField(columns);
        tf.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        tf.setPreferredSize(new Dimension(340, 38));
        if (placeholder != null) {
            tf.putClientProperty("JTextField.placeholderText", placeholder);
        }
        return tf;
    }

    private void saveStudent() {
        String roll = txtRollNumber.getText().trim();
        String name = txtName.getText().trim();
        String course = (comboCourse.getSelectedIndex() > 0) ? (String) comboCourse.getSelectedItem() : "";
        String year = (comboYear.getSelectedIndex() > 0) ? (String) comboYear.getSelectedItem() : "";
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String gender = "";
        if (rbMale.isSelected()) gender = "Male";
        else if (rbFemale.isSelected()) gender = "Female";
        else if (rbOther.isSelected()) gender = "Other";

        Student student = new Student(roll, name, course, year, email, phone, gender);
        if (editMode) {
            student.setId(currentEditingStudentId);
        }

        List<String> validationErrors = ValidationUtil.validateStudent(student);
        if (!validationErrors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Please resolve the following input issues:\n\n");
            for (String err : validationErrors) {
                sb.append("• ").append(err).append("\n");
            }
            JOptionPane.showMessageDialog(this, sb.toString(), "Validation Alert", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (editMode) {
            String error = studentService.updateStudent(student);
            if (error == null) {
                JOptionPane.showMessageDialog(this,
                        "Student profile (" + roll + ") updated successfully in MySQL!",
                        "Update Successful",
                        JOptionPane.INFORMATION_MESSAGE);
                cancelEdit();
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        error,
                        "Update Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            String error = studentService.addStudent(student);
            if (error == null) {
                JOptionPane.showMessageDialog(this,
                        "Student successfully registered into MySQL database!\n\n"
                                + "• Roll Number: " + roll + "\n"
                                + "• Name: " + name + "\n"
                                + "• Course: " + course + "\n"
                                + "• Session: " + year,
                        "Registration Confirmed",
                        JOptionPane.INFORMATION_MESSAGE);
                clearForm();
                if (refreshCallback != null) {
                    refreshCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        error,
                        "Registration Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void setStudentForEdit(Student s) {
        if (s == null) return;
        this.editMode = true;
        this.currentEditingStudentId = s.getId();

        formTitleLabel.setText("✏️ Update Student Profile");
        formTitleLabel.setForeground(new Color(0xef, 0x6c, 0x00));
        subtitleLabel.setText("Modifying record for Roll Number: " + s.getRollNumber() + " (ID: " + s.getId() + ")");
        btnSave.setText("💾 Save Changes");
        btnSave.setBackground(BUTTON_BLUE);
        btnCancelEdit.setVisible(true);

        txtRollNumber.setText(s.getRollNumber());
        txtName.setText(s.getName());
        comboCourse.setSelectedItem(s.getCourse());
        comboYear.setSelectedItem(s.getAcademicYear());
        txtEmail.setText(s.getEmail());
        txtPhone.setText(s.getPhone());

        if ("Male".equalsIgnoreCase(s.getGender())) rbMale.setSelected(true);
        else if ("Female".equalsIgnoreCase(s.getGender())) rbFemale.setSelected(true);
        else if ("Other".equalsIgnoreCase(s.getGender())) rbOther.setSelected(true);
        else genderGroup.clearSelection();
    }

    public void clearForm() {
        txtRollNumber.setText("");
        txtName.setText("");
        comboCourse.setSelectedIndex(0);
        comboYear.setSelectedItem("2026-27");
        txtEmail.setText("");
        txtPhone.setText("");
        genderGroup.clearSelection();
    }

    public void cancelEdit() {
        this.editMode = false;
        this.currentEditingStudentId = -1;
        formTitleLabel.setText("➕ Student Registration Form");
        formTitleLabel.setForeground(PRIMARY_DARK);
        subtitleLabel.setText("Enter student details below. Fields marked with (*) are required and strictly validated.");
        btnSave.setText("💾 Save Student Record");
        btnSave.setBackground(SAVE_GREEN);
        btnCancelEdit.setVisible(false);
        clearForm();

        Window window = SwingUtilities.getWindowAncestor(this);
        if (window instanceof JDialog) {
            window.dispose();
        }
    }
}
