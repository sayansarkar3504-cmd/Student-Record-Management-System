package com.aot.studentmanagement.ui;

import com.aot.studentmanagement.config.AppConfig;
import com.aot.studentmanagement.database.DatabaseConnection;
import com.aot.studentmanagement.model.Student;
import com.aot.studentmanagement.service.AuthenticationService;
import com.aot.studentmanagement.service.StudentService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Modern Executive Dashboard for Student Record Management System.
 * Features sidebar navigation, dynamic KPI metric cards, recent records,
 * and integrated search and CRUD management modules.
 * 
 * PCC-CS503 Object Oriented Programming | Academy of Technology
 */
public class DashboardFrame extends JFrame {

    private static final long serialVersionUID = 1L;

    // Theme Colors
    private static final Color PRIMARY_DARK = new Color(0x1a, 0x23, 0x7e);
    private static final Color SIDEBAR_DARK = new Color(0x0f, 0x17, 0x5e);
    private static final Color BUTTON_BLUE = new Color(0x15, 0x65, 0xc0);
    private static final Color ACCENT_TEAL = new Color(0x00, 0x89, 0x7b);
    private static final Color AMBER_ORANGE = new Color(0xef, 0x6c, 0x00);
    private static final Color LIGHT_GRAY_BG = new Color(0xf4, 0xf6, 0xf9);
    private static final Color CARD_WHITE = Color.WHITE;
    private static final Color BORDER_COLOR = new Color(0xe0, 0xe0, 0xe0);
    private static final String FONT_FAMILY = "Segoe UI";

    private final AuthenticationService authService;
    private final StudentService studentService;

    // Layout
    private CardLayout cardLayout;
    private JPanel contentPanel;

    // Sidebar navigation buttons
    private JButton navDashboard;
    private JButton navAddStudent;
    private JButton navViewStudents;
    private JButton navSearchStudents;
    private JButton activeNavButton;

    // Dashboard card components
    private JLabel totalStudentsLabel;
    private JLabel totalCoursesLabel;
    private JLabel academicYearLabel;
    private DefaultTableModel recentTableModel;
    private JLabel recentCountBadge;

    // Search card components
    private JComboBox<String> searchTypeCombo;
    private JTextField searchField;
    private DefaultTableModel searchTableModel;
    private JLabel searchResultLabel;

    // Child panels
    private StudentFormPanel studentFormPanel;
    private StudentTablePanel studentTablePanel;

    public DashboardFrame(AuthenticationService authService, StudentService studentService) {
        this.authService = authService;
        this.studentService = studentService;
        initializeFrame();
        buildUI();
        refreshDashboard();
    }

    private void initializeFrame() {
        setTitle("Student Record Management System — " + AppConfig.getInstitution() + " [" + AppConfig.getCourseCode() + "]");
        setSize(1240, 780);
        setMinimumSize(new Dimension(1050, 680));
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private void buildUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createSidebar(), BorderLayout.WEST);
        mainPanel.add(createContentArea(), BorderLayout.CENTER);
        setContentPane(mainPanel);
    }

    // ================================================================
    //                         SIDEBAR
    // ================================================================

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setPreferredSize(new Dimension(240, 0));
        sidebar.setBackground(PRIMARY_DARK);

        // Header Panel: Logo + Institution
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(new EmptyBorder(25, 20, 20, 20));

        JLabel logoIcon = new JLabel("🎓", SwingConstants.CENTER);
        logoIcon.setFont(new Font(FONT_FAMILY, Font.PLAIN, 40));
        logoIcon.setForeground(Color.WHITE);
        logoIcon.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(logoIcon);

        logoPanel.add(Box.createVerticalStrut(8));

        JLabel titleLabel = new JLabel("SRMS Admin", SwingConstants.CENTER);
        titleLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(titleLabel);

        JLabel deptLabel = new JLabel("CSE Department", SwingConstants.CENTER);
        deptLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        deptLabel.setForeground(new Color(0x90, 0xca, 0xf9));
        deptLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoPanel.add(deptLabel);

        logoPanel.add(Box.createVerticalStrut(15));

        JSeparator sep = new JSeparator();
        sep.setForeground(new Color(255, 255, 255, 40));
        sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        logoPanel.add(sep);

        sidebar.add(logoPanel, BorderLayout.NORTH);

        // Navigation Menu
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new BoxLayout(navPanel, BoxLayout.Y_AXIS));
        navPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        navDashboard = createNavButton("📊   Dashboard");
        navAddStudent = createNavButton("➕   Add Student");
        navViewStudents = createNavButton("📋   View Students");
        navSearchStudents = createNavButton("🔍   Search Records");

        navDashboard.addActionListener(e -> showCard("dashboard", navDashboard));
        navAddStudent.addActionListener(e -> showCard("add", navAddStudent));
        navViewStudents.addActionListener(e -> {
            showCard("view", navViewStudents);
            studentTablePanel.loadStudents();
        });
        navSearchStudents.addActionListener(e -> showCard("search", navSearchStudents));

        navPanel.add(navDashboard);
        navPanel.add(navAddStudent);
        navPanel.add(navViewStudents);
        navPanel.add(navSearchStudents);

        sidebar.add(navPanel, BorderLayout.CENTER);

        // Bottom Footer: User Info & Logout
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.setBorder(new EmptyBorder(10, 15, 20, 15));

        // DB Status Indicator
        JLabel dbHealth = new JLabel("● MySQL: " + AppConfig.getDbName());
        dbHealth.setFont(new Font(FONT_FAMILY, Font.PLAIN, 11));
        dbHealth.setForeground(new Color(0x81, 0xc7, 0x84)); // Green
        dbHealth.setAlignmentX(Component.CENTER_ALIGNMENT);
        bottomPanel.add(dbHealth);

        bottomPanel.add(Box.createVerticalStrut(12));

        JButton logoutButton = new JButton("🚪   Sign Out");
        logoutButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setBackground(new Color(0x39, 0x49, 0xab));
        logoutButton.setBorder(new EmptyBorder(10, 15, 10, 15));
        logoutButton.setFocusPainted(false);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        logoutButton.addActionListener(e -> performLogout());
        bottomPanel.add(logoutButton);

        sidebar.add(bottomPanel, BorderLayout.SOUTH);

        setActiveNavButton(navDashboard);
        return sidebar;
    }

    private JButton createNavButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        button.setForeground(new Color(0xcc, 0xcc, 0xcc));
        button.setBackground(PRIMARY_DARK);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
        button.setPreferredSize(new Dimension(240, 46));
        button.setBorder(new EmptyBorder(10, 24, 10, 10));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button != activeNavButton) {
                    button.setBackground(SIDEBAR_DARK);
                    button.setForeground(Color.WHITE);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (button != activeNavButton) {
                    button.setBackground(PRIMARY_DARK);
                    button.setForeground(new Color(0xcc, 0xcc, 0xcc));
                }
            }
        });

        return button;
    }

    private void setActiveNavButton(JButton button) {
        if (activeNavButton != null) {
            activeNavButton.setBackground(PRIMARY_DARK);
            activeNavButton.setForeground(new Color(0xcc, 0xcc, 0xcc));
            activeNavButton.setFont(new Font(FONT_FAMILY, Font.PLAIN, 14));
        }
        activeNavButton = button;
        activeNavButton.setBackground(ACCENT_TEAL);
        activeNavButton.setForeground(Color.WHITE);
        activeNavButton.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
    }

    private void showCard(String cardName, JButton navButton) {
        cardLayout.show(contentPanel, cardName);
        setActiveNavButton(navButton);
        if ("dashboard".equals(cardName)) {
            refreshDashboard();
        }
    }

    // ================================================================
    //                       CONTENT AREA
    // ================================================================

    private JPanel createContentArea() {
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(LIGHT_GRAY_BG);

        // 1. Dashboard card
        contentPanel.add(createDashboardCard(), "dashboard");

        // 2. Add Student card
        studentFormPanel = new StudentFormPanel(studentService, this::refreshDashboard);
        contentPanel.add(studentFormPanel, "add");

        // 3. View Students card
        studentTablePanel = new StudentTablePanel(studentService, this::refreshDashboard);
        contentPanel.add(studentTablePanel, "view");

        // 4. Search Records card
        contentPanel.add(createSearchCard(), "search");

        return contentPanel;
    }

    // ================================================================
    //                       DASHBOARD CARD
    // ================================================================

    private JPanel createDashboardCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(LIGHT_GRAY_BG);

        // Top Header Bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CARD_WHITE);
        topBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(18, 30, 18, 30)
        ));

        JPanel titleGrp = new JPanel();
        titleGrp.setOpaque(false);
        titleGrp.setLayout(new BoxLayout(titleGrp, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Executive Administration Dashboard");
        title.setFont(new Font(FONT_FAMILY, Font.BOLD, 22));
        title.setForeground(PRIMARY_DARK);

        JLabel sub = new JLabel("Academy of Technology | Department of Computer Science & Engineering");
        sub.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        sub.setForeground(Color.GRAY);

        titleGrp.add(title);
        titleGrp.add(Box.createVerticalStrut(2));
        titleGrp.add(sub);
        topBar.add(titleGrp, BorderLayout.WEST);

        // Right side: Admin Badge & Date
        JPanel metaPanel = new JPanel();
        metaPanel.setOpaque(false);
        metaPanel.setLayout(new BoxLayout(metaPanel, BoxLayout.Y_AXIS));

        String dateStr = LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy"));
        JLabel dateLabel = new JLabel("📅 " + dateStr, SwingConstants.RIGHT);
        dateLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        dateLabel.setForeground(new Color(0x55, 0x55, 0x55));
        dateLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);

        JLabel userBadge = new JLabel("👤 Logged in as: Administrator (admin)", SwingConstants.RIGHT);
        userBadge.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        userBadge.setForeground(PRIMARY_DARK);
        userBadge.setAlignmentX(Component.RIGHT_ALIGNMENT);

        metaPanel.add(dateLabel);
        metaPanel.add(Box.createVerticalStrut(3));
        metaPanel.add(userBadge);
        topBar.add(metaPanel, BorderLayout.EAST);

        card.add(topBar, BorderLayout.NORTH);

        // Center Content: Stats + Recent Students + Quick Actions
        JPanel centerContent = new JPanel();
        centerContent.setLayout(new BoxLayout(centerContent, BoxLayout.Y_AXIS));
        centerContent.setBackground(LIGHT_GRAY_BG);
        centerContent.setBorder(new EmptyBorder(25, 30, 25, 30));

        // 1. KPI Statistics Cards Row
        JPanel statsRow = new JPanel(new GridLayout(1, 3, 20, 0));
        statsRow.setOpaque(false);
        statsRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 110));

        totalStudentsLabel = new JLabel("0");
        totalCoursesLabel = new JLabel("0");
        academicYearLabel = new JLabel(AppConfig.getAcademicSession());

        statsRow.add(createStatCard("👥", "Total Registered Students", totalStudentsLabel, BUTTON_BLUE));
        statsRow.add(createStatCard("📚", "Active Academic Courses", totalCoursesLabel, ACCENT_TEAL));
        statsRow.add(createStatCard("🎓", "Current Academic Session", academicYearLabel, AMBER_ORANGE));

        centerContent.add(statsRow);
        centerContent.add(Box.createVerticalStrut(25));

        // 2. Recent Students Table Section
        JPanel recentSection = new JPanel(new BorderLayout(0, 10));
        recentSection.setBackground(CARD_WHITE);
        recentSection.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(18, 22, 18, 22)
        ));

        JPanel recentHeader = new JPanel(new BorderLayout());
        recentHeader.setOpaque(false);

        JLabel recentTitle = new JLabel("🕒 Recently Enrolled Students");
        recentTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 16));
        recentTitle.setForeground(PRIMARY_DARK);

        recentCountBadge = new JLabel("Last 5 Registrations from MySQL");
        recentCountBadge.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        recentCountBadge.setForeground(Color.GRAY);

        recentHeader.add(recentTitle, BorderLayout.WEST);
        recentHeader.add(recentCountBadge, BorderLayout.EAST);
        recentSection.add(recentHeader, BorderLayout.NORTH);

        String[] recentColumns = {"ID", "Roll Number", "Full Name", "Course / Department", "Academic Year"};
        recentTableModel = new DefaultTableModel(recentColumns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable recentTable = new JTable(recentTableModel);
        styleRecentTable(recentTable);
        JScrollPane recentScrollPane = new JScrollPane(recentTable);
        recentScrollPane.setBorder(new LineBorder(new Color(0xf0, 0xf0, 0xf0), 1));
        recentScrollPane.getViewport().setBackground(CARD_WHITE);
        recentSection.add(recentScrollPane, BorderLayout.CENTER);

        centerContent.add(recentSection);
        centerContent.add(Box.createVerticalStrut(20));

        // 3. Quick Actions Toolbar
        JPanel quickActionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        quickActionsPanel.setOpaque(false);
        quickActionsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel quickActionsTitle = new JLabel("Quick Actions:");
        quickActionsTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 14));
        quickActionsTitle.setForeground(PRIMARY_DARK);
        quickActionsPanel.add(quickActionsTitle);

        JButton addBtn = createActionButton("➕ Add New Student", BUTTON_BLUE);
        addBtn.addActionListener(e -> showCard("add", navAddStudent));
        quickActionsPanel.add(addBtn);

        JButton viewBtn = createActionButton("📋 View All Records", ACCENT_TEAL);
        viewBtn.addActionListener(e -> {
            showCard("view", navViewStudents);
            studentTablePanel.loadStudents();
        });
        quickActionsPanel.add(viewBtn);

        JButton searchBtn = createActionButton("🔍 Search Database", AMBER_ORANGE);
        searchBtn.addActionListener(e -> showCard("search", navSearchStudents));
        quickActionsPanel.add(searchBtn);

        centerContent.add(quickActionsPanel);

        card.add(centerContent, BorderLayout.CENTER);
        return card;
    }

    private JPanel createStatCard(String icon, String title, JLabel valueLabel, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(CARD_WHITE);
        card.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(16, 18, 16, 18)
        ));

        // Left color accent strip
        JPanel colorStrip = new JPanel() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(accentColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2d.dispose();
            }
        };
        colorStrip.setPreferredSize(new Dimension(54, 54));
        colorStrip.setOpaque(false);
        colorStrip.setLayout(new GridBagLayout());

        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 26));
        iconLabel.setForeground(Color.WHITE);
        colorStrip.add(iconLabel);

        card.add(colorStrip, BorderLayout.WEST);

        // Right Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setOpaque(false);
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(new EmptyBorder(2, 16, 2, 5));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        titleLbl.setForeground(new Color(0x66, 0x66, 0x66));
        infoPanel.add(titleLbl);

        infoPanel.add(Box.createVerticalStrut(4));

        valueLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 26));
        valueLabel.setForeground(PRIMARY_DARK);
        infoPanel.add(valueLabel);

        card.add(infoPanel, BorderLayout.CENTER);
        return card;
    }

    private void styleRecentTable(JTable table) {
        table.setRowHeight(34);
        table.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        table.setShowGrid(true);
        table.setGridColor(new Color(0xf0, 0xf0, 0xf0));
        table.setSelectionBackground(new Color(0xe3, 0xf2, 0xfd));
        table.setSelectionForeground(PRIMARY_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        header.setBackground(PRIMARY_DARK);
        header.setForeground(Color.WHITE);
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 36));

        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(center);
        table.getColumnModel().getColumn(1).setCellRenderer(center);
        table.getColumnModel().getColumn(4).setCellRenderer(center);
    }

    // ================================================================
    //                       SEARCH CARD
    // ================================================================

    private JPanel createSearchCard() {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(LIGHT_GRAY_BG);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(CARD_WHITE);
        topBar.setBorder(new CompoundBorder(
                new MatteBorder(0, 0, 1, 0, BORDER_COLOR),
                new EmptyBorder(18, 30, 18, 30)
        ));

        JLabel searchTitle = new JLabel("🔍 Multi-Criteria Student Search Engine");
        searchTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 22));
        searchTitle.setForeground(PRIMARY_DARK);
        topBar.add(searchTitle, BorderLayout.WEST);

        card.add(topBar, BorderLayout.NORTH);

        // Center Content
        JPanel centerPanel = new JPanel(new BorderLayout(0, 15));
        centerPanel.setBackground(LIGHT_GRAY_BG);
        centerPanel.setBorder(new EmptyBorder(20, 30, 20, 30));

        // Search Controls Box
        JPanel searchControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        searchControls.setBackground(CARD_WHITE);
        searchControls.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(10, 15, 10, 15)
        ));

        JLabel searchByLabel = new JLabel("Search By Criteria:");
        searchByLabel.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        searchByLabel.setForeground(PRIMARY_DARK);
        searchControls.add(searchByLabel);

        searchTypeCombo = new JComboBox<>(new String[]{"All Fields (Global)", "Roll Number", "Student Name", "Course"});
        searchTypeCombo.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        searchTypeCombo.setPreferredSize(new Dimension(180, 36));
        searchControls.add(searchTypeCombo);

        searchField = new JTextField(24);
        searchField.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        searchField.setPreferredSize(new Dimension(300, 36));
        searchField.putClientProperty("JTextField.placeholderText", "Enter keyword to search MySQL...");
        searchField.addActionListener(e -> performSearch());
        searchControls.add(searchField);

        JButton searchButton = createActionButton("🔍 Search", BUTTON_BLUE);
        searchButton.addActionListener(e -> performSearch());
        searchControls.add(searchButton);

        JButton clearButton = createActionButton("✖ Clear Results", new Color(0x75, 0x75, 0x75));
        clearButton.addActionListener(e -> clearSearch());
        searchControls.add(clearButton);

        centerPanel.add(searchControls, BorderLayout.NORTH);

        // Results Table Panel
        JPanel resultsPanel = new JPanel(new BorderLayout(0, 10));
        resultsPanel.setBackground(CARD_WHITE);
        resultsPanel.setBorder(new CompoundBorder(
                new LineBorder(BORDER_COLOR, 1, true),
                new EmptyBorder(15, 20, 15, 20)
        ));

        JPanel resultsHeader = new JPanel(new BorderLayout());
        resultsHeader.setOpaque(false);

        JLabel resultsTitle = new JLabel("Search Results");
        resultsTitle.setFont(new Font(FONT_FAMILY, Font.BOLD, 15));
        resultsTitle.setForeground(PRIMARY_DARK);

        searchResultLabel = new JLabel("Enter a keyword and click 'Search' to query database");
        searchResultLabel.setFont(new Font(FONT_FAMILY, Font.PLAIN, 12));
        searchResultLabel.setForeground(Color.GRAY);

        resultsHeader.add(resultsTitle, BorderLayout.WEST);
        resultsHeader.add(searchResultLabel, BorderLayout.EAST);
        resultsPanel.add(resultsHeader, BorderLayout.NORTH);

        String[] searchColumns = {"ID", "Roll Number", "Full Name", "Course", "Year", "Email", "Phone", "Gender"};
        searchTableModel = new DefaultTableModel(searchColumns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int col) { return false; }
        };

        JTable searchTable = new JTable(searchTableModel);
        searchTable.setRowHeight(36);
        searchTable.setFont(new Font(FONT_FAMILY, Font.PLAIN, 13));
        searchTable.setShowGrid(true);
        searchTable.setGridColor(new Color(0xf0, 0xf0, 0xf0));
        searchTable.setSelectionBackground(new Color(0xe3, 0xf2, 0xfd));
        searchTable.setRowSorter(new TableRowSorter<>(searchTableModel));

        JTableHeader searchHeader = searchTable.getTableHeader();
        searchHeader.setFont(new Font(FONT_FAMILY, Font.BOLD, 12));
        searchHeader.setBackground(PRIMARY_DARK);
        searchHeader.setForeground(Color.WHITE);
        searchHeader.setPreferredSize(new Dimension(searchHeader.getPreferredSize().width, 38));

        JScrollPane searchScrollPane = new JScrollPane(searchTable);
        searchScrollPane.setBorder(new LineBorder(new Color(0xf0, 0xf0, 0xf0), 1));
        searchScrollPane.getViewport().setBackground(CARD_WHITE);
        resultsPanel.add(searchScrollPane, BorderLayout.CENTER);

        centerPanel.add(resultsPanel, BorderLayout.CENTER);
        card.add(centerPanel, BorderLayout.CENTER);

        return card;
    }

    private void performSearch() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a search keyword.",
                    "Search Warning", JOptionPane.WARNING_MESSAGE);
            searchField.requestFocusInWindow();
            return;
        }

        String searchType = (String) searchTypeCombo.getSelectedItem();
        String searchBy;
        if (searchType != null && searchType.startsWith("Roll")) {
            searchBy = "roll_number";
        } else if (searchType != null && searchType.startsWith("Student")) {
            searchBy = "name";
        } else if (searchType != null && searchType.startsWith("Course")) {
            searchBy = "course";
        } else {
            searchBy = "all";
        }

        try {
            List<Student> results = studentService.searchStudents(keyword, searchBy);
            searchTableModel.setRowCount(0);
            for (Student s : results) {
                searchTableModel.addRow(new Object[]{
                        s.getId(), s.getRollNumber(), s.getName(), s.getCourse(),
                        s.getAcademicYear(), s.getEmail(), s.getPhone(), s.getGender()
                });
            }

            int count = results.size();
            searchResultLabel.setText("Found " + count + " record" + (count == 1 ? "" : "s") + " matching '" + keyword + "'");

            if (results.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "No student records found matching keyword: \"" + keyword + "\"",
                        "No Matches", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Search query error: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearSearch() {
        searchField.setText("");
        searchTableModel.setRowCount(0);
        searchResultLabel.setText("Results cleared. Enter a keyword to search again.");
    }

    // ================================================================
    //                    REFRESH DASHBOARD
    // ================================================================

    public void refreshDashboard() {
        try {
            int totalStudents = studentService.getTotalStudentCount();
            int totalCourses = studentService.getDistinctCourseCount();

            totalStudentsLabel.setText(String.valueOf(totalStudents));
            totalCoursesLabel.setText(String.valueOf(totalCourses));
            academicYearLabel.setText(AppConfig.getAcademicSession());

            // Recent Students Table
            recentTableModel.setRowCount(0);
            List<Student> recent = studentService.getRecentStudents(5);
            for (Student s : recent) {
                recentTableModel.addRow(new Object[]{
                        s.getId(), s.getRollNumber(), s.getName(), s.getCourse(), s.getAcademicYear()
                });
            }
            recentCountBadge.setText(recent.isEmpty() ? "No recent records" : "Showing latest " + recent.size() + " of " + totalStudents + " total");

        } catch (Exception ex) {
            System.err.println("[DashboardFrame] Error refreshing dashboard stats: " + ex.getMessage());
        }
    }

    // ================================================================
    //                       LOGOUT
    // ================================================================

    private void performLogout() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to sign out from the Administrative session?",
                "Confirm Sign Out",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            authService.logout();
            dispose();
            LoginFrame loginFrame = new LoginFrame(new AuthenticationService());
            loginFrame.setVisible(true);
        }
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font(FONT_FAMILY, Font.BOLD, 13));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(170, 38));
        return button;
    }
}
