package com.aot.studentmanagement.ui;

import com.aot.studentmanagement.model.Student;
import com.aot.studentmanagement.service.StudentService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Modern JTable records browser for Student entity.
 * Features instant filtering, column sorting, alternating rows,
 * selection-safe row-to-model conversion, and complete CRUD actions.
 * 
 * PCC-CS503 Object Oriented Programming | Academy of Technology
 */
public class StudentTablePanel extends JPanel {

    private static final long serialVersionUID = 1L;

    // Theme Colors
    private static final Color PRIMARY_DARK = new Color(0x1a, 0x23, 0x7e);
    private static final Color BUTTON_BLUE = new Color(0x15, 0x65, 0xc0);
    private static final Color ACCENT_TEAL = new Color(0x00, 0x89, 0x7b);
    private static final Color AMBER_ORANGE = new Color(0xef, 0x6c, 0x00);
    private static final Color DANGER_RED = new Color(0xc6, 0x28, 0x28);
    private static final Color CARD_BG = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xe0, 0xe0, 0xe0);
    private static final String FONT_FAMILY = "Segoe UI";

    private final StudentService studentService;
    private final Runnable onDataChangedCallback;

    private JTable studentTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> tableSorter;
    private JLabel countLabel;
    private JTextField txtFilter;
    private JLabel emptyStateLabel;
    private JPanel emptyStatePanel;
    private JScrollPane scrollPane;
    private List<Student> currentStudentList;

    public StudentTablePanel(StudentService studentService) {
        this(studentService, null);
    }

    public StudentTablePanel(StudentService studentService, Runnable onDataChangedCallback) {
        this.studentService = studentService;
        this.onDataChangedCallback = onDataChangedCallback;

        setLayout(new BorderLayout());
        setBackground(new Color(0xf8, 0xf9, 0xfa));
        setBorder(new EmptyBorder(20, 25, 20, 25));

        initComponents();
        loadStudents();
    }

    private void initComponents() {
        // Main container card
        JPanel cardPanel = new JPanel(new BorderLayout(0, 12));
        cardPanel.setBackground(CARD_BG);
        cardPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(20, 25, 20, 25)
        ));

        // Top Section: Title & Controls
        JPanel topSection = new JPanel(new BorderLayout(0, 10));
        topSection.setOpaque(false);

        // Header Title + Subtitle
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("📋 Student Records Registry");
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 22));
        titleLabel.setForeground(PRIMARY_DARK);

        countLabel = new JLabel("Loading records from MySQL...");
        countLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        countLabel.setForeground(new Color(0x55, 0x55, 0x55));

        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(3));
        titlePanel.add(countLabel);

        topSection.add(titlePanel, BorderLayout.WEST);

        // Action Buttons Panel
        JPanel actionButtonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionButtonsPanel.setOpaque(false);

        JButton btnRefresh = createStyledButton("🔄 Refresh", BUTTON_BLUE);
        btnRefresh.setToolTipText("Reload records from database");
        btnRefresh.addActionListener(e -> loadStudents());

        JButton btnEdit = createStyledButton("✏️ Edit Selected", AMBER_ORANGE);
        btnEdit.setToolTipText("Edit information of selected student");
        btnEdit.addActionListener(e -> editSelectedStudent());

        JButton btnDelete = createStyledButton("🗑️ Delete Selected", DANGER_RED);
        btnDelete.setToolTipText("Permanently remove selected student record");
        btnDelete.addActionListener(e -> deleteSelectedStudent());

        actionButtonsPanel.add(btnRefresh);
        actionButtonsPanel.add(btnEdit);
        actionButtonsPanel.add(btnDelete);

        topSection.add(actionButtonsPanel, BorderLayout.EAST);

        // Filter / Search Toolbar directly above Table
        JPanel filterToolbar = new JPanel(new BorderLayout(10, 0));
        filterToolbar.setOpaque(false);
        filterToolbar.setBorder(new EmptyBorder(5, 0, 5, 0));

        JPanel searchBoxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        searchBoxPanel.setOpaque(false);

        JLabel filterIcon = new JLabel("🔎 Quick Filter:");
        filterIcon.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        filterIcon.setForeground(PRIMARY_DARK);
        searchBoxPanel.add(filterIcon);

        txtFilter = new JTextField(24);
        txtFilter.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        txtFilter.setPreferredSize(new Dimension(280, 34));
        txtFilter.putClientProperty("JTextField.placeholderText", "Type name, roll, or course to filter...");
        txtFilter.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) { applyTableFilter(); }
            @Override
            public void removeUpdate(DocumentEvent e) { applyTableFilter(); }
            @Override
            public void changedUpdate(DocumentEvent e) { applyTableFilter(); }
        });
        searchBoxPanel.add(txtFilter);

        JButton btnClearFilter = new JButton("✖ Clear");
        btnClearFilter.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        btnClearFilter.setPreferredSize(new Dimension(80, 34));
        btnClearFilter.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClearFilter.addActionListener(e -> txtFilter.setText(""));
        searchBoxPanel.add(btnClearFilter);

        JLabel sortHint = new JLabel("💡 Tip: Click any column header to sort. Double-click row to edit.");
        sortHint.setFont(new Font(FONT_FAMILY, Font.ITALIC, 12));
        sortHint.setForeground(Color.GRAY);

        filterToolbar.add(searchBoxPanel, BorderLayout.WEST);
        filterToolbar.add(sortHint, BorderLayout.EAST);

        topSection.add(filterToolbar, BorderLayout.SOUTH);
        cardPanel.add(topSection, BorderLayout.NORTH);

        // Table Setup
        String[] columnNames = {
                "ID", "Roll Number", "Full Name", "Course / Department",
                "Academic Year", "Email Address", "Phone Number", "Gender", "Date Registered"
        };

        tableModel = new DefaultTableModel(columnNames, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Cells are read-only
            }
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                if (columnIndex == 0) return Integer.class;
                return String.class;
            }
        };

        studentTable = new JTable(tableModel);
        tableSorter = new TableRowSorter<>(tableModel);
        studentTable.setRowSorter(tableSorter);
        styleTable(studentTable);

        // Double-click row to edit
        studentTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && studentTable.getSelectedRow() != -1) {
                    editSelectedStudent();
                }
            }
        });

        // Set column widths
        studentTable.getColumnModel().getColumn(0).setPreferredWidth(50);   // ID
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(120);  // Roll Number
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(170);  // Name
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(220);  // Course
        studentTable.getColumnModel().getColumn(4).setPreferredWidth(100);  // Year
        studentTable.getColumnModel().getColumn(5).setPreferredWidth(190);  // Email
        studentTable.getColumnModel().getColumn(6).setPreferredWidth(110);  // Phone
        studentTable.getColumnModel().getColumn(7).setPreferredWidth(80);   // Gender
        studentTable.getColumnModel().getColumn(8).setPreferredWidth(140);  // Date

        scrollPane = new JScrollPane(studentTable);
        scrollPane.setBorder(new LineBorder(BORDER_COLOR, 1));
        scrollPane.getViewport().setBackground(CARD_BG);

        // Empty state overlay
        emptyStatePanel = new JPanel(new GridBagLayout());
        emptyStatePanel.setBackground(CARD_BG);
        emptyStateLabel = new JLabel("<html><center><font size='5' color='#1a237e'><b>No Student Records Found</b></font><br><br>"
                + "<font size='3' color='#777777'>There are currently no students registered in the database.<br>"
                + "Click <b>'Add Student'</b> in the navigation sidebar to register your first student.</font></center></html>");
        emptyStateLabel.setHorizontalAlignment(SwingConstants.CENTER);
        emptyStatePanel.add(emptyStateLabel);

        JPanel tableContainer = new JPanel(new CardLayout());
        tableContainer.add(scrollPane, "table");
        tableContainer.add(emptyStatePanel, "empty");

        cardPanel.add(tableContainer, BorderLayout.CENTER);
        add(cardPanel, BorderLayout.CENTER);
    }

    private void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(true);
        table.setGridColor(new Color(0xf0, 0xf0, 0xf0));
        table.setSelectionBackground(new Color(0xe3, 0xf2, 0xfd));
        table.setSelectionForeground(new Color(0x1a, 0x23, 0x7e));
        table.setFillsViewportHeight(true);

        // Header Style
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        header.setBackground(PRIMARY_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 42));
        header.setReorderingAllowed(false);

        DefaultTableCellRenderer headerRenderer = new DefaultTableCellRenderer();
        headerRenderer.setBackground(PRIMARY_DARK);
        headerRenderer.setForeground(Color.WHITE);
        headerRenderer.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        headerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        headerRenderer.setBorder(new EmptyBorder(4, 10, 4, 10));
        header.setDefaultRenderer(headerRenderer);

        // Cell Alignment Renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(0xf9, 0xfa, 0xfb));
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                setBorder(new EmptyBorder(4, 8, 4, 8));
                return c;
            }
        };

        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? CARD_BG : new Color(0xf9, 0xfa, 0xfb));
                }
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(new EmptyBorder(4, 12, 4, 8));
                return c;
            }
        };

        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // ID
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Roll Number
        table.getColumnModel().getColumn(2).setCellRenderer(leftRenderer);   // Name
        table.getColumnModel().getColumn(3).setCellRenderer(leftRenderer);   // Course
        table.getColumnModel().getColumn(4).setCellRenderer(centerRenderer); // Year
        table.getColumnModel().getColumn(5).setCellRenderer(leftRenderer);   // Email
        table.getColumnModel().getColumn(6).setCellRenderer(centerRenderer); // Phone
        table.getColumnModel().getColumn(7).setCellRenderer(centerRenderer); // Gender
        table.getColumnModel().getColumn(8).setCellRenderer(centerRenderer); // Date
    }

    private void applyTableFilter() {
        String query = txtFilter.getText().trim();
        if (query.isEmpty()) {
            tableSorter.setRowFilter(null);
            updateCountLabel();
        } else {
            tableSorter.setRowFilter(RowFilter.regexFilter("(?i)" + Pattern.quote(query)));
            int matching = tableSorter.getViewRowCount();
            countLabel.setText("Showing " + matching + " of " + tableModel.getRowCount() + " records (Filter: \"" + query + "\")");
        }
    }

    private void updateCountLabel() {
        int total = tableModel.getRowCount();
        if (total == 0) {
            countLabel.setText("Total Records: 0 (No students registered yet)");
        } else {
            countLabel.setText("Total Records: " + total + " student" + (total > 1 ? "s" : "") + " enrolled");
        }
    }

    private JButton createStyledButton(String text, Color bg) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        button.setBackground(bg);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(145, 36));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    /**
     * Loads or reloads all student records from MySQL into the JTable.
     */
    public void loadStudents() {
        tableModel.setRowCount(0);
        try {
            currentStudentList = studentService.getAllStudents();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            for (Student s : currentStudentList) {
                String dateStr = (s.getCreatedAt() != null) ? s.getCreatedAt().format(formatter) : "N/A";
                tableModel.addRow(new Object[]{
                        s.getId(),
                        s.getRollNumber(),
                        s.getName(),
                        s.getCourse(),
                        s.getAcademicYear(),
                        s.getEmail(),
                        s.getPhone(),
                        s.getGender(),
                        dateStr
                });
            }

            int count = currentStudentList.size();
            updateCountLabel();

            // Toggle view between table and empty state
            JPanel parent = (JPanel) scrollPane.getParent();
            CardLayout cl = (CardLayout) parent.getLayout();
            if (count == 0) {
                cl.show(parent, "empty");
            } else {
                cl.show(parent, "table");
            }

            // Re-apply filter if text was present
            if (!txtFilter.getText().trim().isEmpty()) {
                applyTableFilter();
            }

        } catch (Exception e) {
            countLabel.setText("Failed to load records: " + e.getMessage());
            JOptionPane.showMessageDialog(this,
                    "Database Error while fetching records:\n" + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Edits the selected student safely using model index conversion.
     */
    private void editSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student from the table to edit.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Safe conversion from view index to model index (critical when sorted/filtered)
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        int studentId = (int) tableModel.getValueAt(modelRow, 0);

        Student selectedStudent = findStudentById(studentId);
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Student record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Open modal edit dialog
        Window parentWindow = SwingUtilities.getWindowAncestor(this);
        JDialog editDialog = new JDialog(parentWindow, "Edit Student — " + selectedStudent.getRollNumber(),
                Dialog.ModalityType.APPLICATION_MODAL);
        editDialog.setSize(750, 680);
        editDialog.setLocationRelativeTo(this);

        StudentFormPanel editForm = new StudentFormPanel(studentService, () -> {
            loadStudents();
            if (onDataChangedCallback != null) {
                onDataChangedCallback.run();
            }
            editDialog.dispose();
        });
        editForm.setStudentForEdit(selectedStudent);

        editDialog.setContentPane(editForm);
        editDialog.setVisible(true);
    }

    /**
     * Deletes the selected student record after confirmation.
     */
    private void deleteSelectedStudent() {
        int selectedRow = studentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                    "Please select a student from the table to delete.",
                    "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Safe conversion from view index to model index
        int modelRow = studentTable.convertRowIndexToModel(selectedRow);
        int studentId = (int) tableModel.getValueAt(modelRow, 0);

        Student selectedStudent = findStudentById(studentId);
        if (selectedStudent == null) {
            JOptionPane.showMessageDialog(this, "Student record not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete this student record from the database?\n\n"
                        + "• Roll Number: " + selectedStudent.getRollNumber() + "\n"
                        + "• Full Name: " + selectedStudent.getName() + "\n"
                        + "• Course: " + selectedStudent.getCourse() + "\n"
                        + "• Year: " + selectedStudent.getAcademicYear() + "\n\n"
                        + "This action will permanently remove the record from MySQL and cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = studentService.deleteStudent(selectedStudent.getId());
            if (success) {
                JOptionPane.showMessageDialog(this,
                        "Student record (" + selectedStudent.getRollNumber() + ") deleted successfully.",
                        "Deleted", JOptionPane.INFORMATION_MESSAGE);
                loadStudents();
                if (onDataChangedCallback != null) {
                    onDataChangedCallback.run();
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete student record. Please check database connection.",
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private Student findStudentById(int id) {
        if (currentStudentList != null) {
            for (Student s : currentStudentList) {
                if (s.getId() == id) return s;
            }
        }
        return studentService.getStudentById(id);
    }
}
