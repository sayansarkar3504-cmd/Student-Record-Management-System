# TECHNICAL REPORT
## Design and Development of a Student Record Management System Using Java Swing

---

### ACADEMIC ASSESSMENT COVER PAGE

- **Course Code:** PCC-CS503  
- **Course Name:** Object Oriented Programming  
- **Assessment Type:** Application-Based Assessment / Mini Project  
- **Maximum Marks:** 25  
- **Academic Session:** 2026–27  
- **Institution:** Academy of Technology  
- **Department:** Computer Science & Engineering (CSE3)  
- **Programme:** Bachelor of Technology (B.Tech)  
- **Semester:** 5th Semester  
- **Faculty Evaluator:** Dr. Krishna Bhowal  
- **Date of Submission:** 21 November 2026  

---

## TABLE OF CONTENTS

1. [Introduction](#1-introduction)
2. [Problem Definition](#2-problem-definition)
3. [Objectives](#3-objectives)
4. [Scope of the Application](#4-scope-of-the-application)
5. [System Requirements](#5-system-requirements)
6. [Software and Hardware Requirements](#6-software-and-hardware-requirements)
7. [System Architecture](#7-system-architecture)
8. [Data Flow Diagram (DFD)](#8-data-flow-diagram-dfd)
9. [Database Design](#9-database-design)
10. [Java Swing Components Used](#10-java-swing-components-used)
11. [OOP Concepts Implemented](#11-oop-concepts-implemented)
12. [CRUD Operations](#12-crud-operations)
13. [JDBC Connectivity](#13-jdbc-connectivity)
14. [Input Validation](#14-input-validation)
15. [Exception Handling](#15-exception-handling)
16. [Testing Results](#16-testing-results)
17. [Demonstration & Screenshots Guide](#17-demonstration--screenshots-guide)
18. [Conclusion](#18-conclusion)
19. [Future Scope](#19-future-scope)

---

## 1. INTRODUCTION

The Student Record Management System (SRMS) is an interactive, multi-tiered desktop software solution developed as part of the curriculum for Object Oriented Programming (PCC-CS503) in the Department of Computer Science & Engineering at Academy of Technology.

Educational institutions process large volumes of student records across diverse engineering streams. Managing records manually or via isolated spreadsheet files introduces human errors, record duplication, concurrency issues, and data inconsistency. SRMS provides a modern graphical desktop interface backed by an industrial-strength MySQL relational database. The application highlights core software engineering methodologies including layered architecture, separation of concerns, defensive programming, and rigorous data validation.

---

## 2. PROBLEM DEFINITION

Traditional methods of tracking student demographics, departmental enrolments, and contact records suffer from several operational bottlenecks:
- **Redundancy & Inconsistency:** Lack of unique constraints allows duplicate student entries.
- **Data Entry Vulnerabilities:** Unvalidated inputs permit malformed email IDs, incomplete telephone numbers, and blank mandatory fields.
- **Inefficient Querying:** Searching records across thousands of students requires manual scanning or complex spreadsheet lookups.
- **Data Volatility:** File-based or in-memory systems risk complete record loss upon application restarts or unexpected power failures.
- **Unsecured Access:** Absence of role-based authentication permits unauthorized personnel to alter sensitive student profiles.

SRMS resolves these challenges by providing an authenticated, validated, and ACID-compliant relational persistence layer wrapped inside an intuitive Java Swing GUI.

---

## 3. OBJECTIVES

The primary goals of this project are:
1. **Design a Graphical User Interface (GUI):** Construct a responsive, modern desktop interface incorporating FlatLaf styling, dark navy/blue branding, and intuitive card-based layout transitions.
2. **Implement Object-Oriented Principles:** Apply Encapsulation, Abstraction, Inheritance, Polymorphism, and Collections in standard Java.
3. **Ensure Robust Relational Persistence:** Implement full CRUD (Create, Read, Update, Delete) capability using MySQL and JDBC `PreparedStatement` interfaces.
4. **Enforce Comprehensive Validation:** Perform client-side syntax checks on emails, telephone numbers, and unique student roll numbers.
5. **Guarantee Fault Tolerance:** Handle network latency, database connection drops, duplicate keys, and invalid inputs gracefully without application crashes.

---

## 4. SCOPE OF THE APPLICATION

The application is specifically designed for department administrators, academic registrars, and faculty coordinators:
- **Administrative Login:** Authenticates system administrators using secure credential verification.
- **Statistical Dashboard:** Real-time calculation of student enrolments, department counts, and recent admissions.
- **Registration & Profile Creation:** Structured forms for enrolling new students with instantaneous input feedback.
- **Tabular Record Visualization:** High-performance display of all records inside an alternating-row `JTable`.
- **In-Place Record Modification:** Dynamic loading of existing student data into modal edit panels.
- **Safe Record Deletion:** Confirmation prompts preventing accidental data loss.
- **Multi-Parametric Search Engine:** Rapid query execution using Roll Number (exact), Name (partial matching), or Course (filtered view).

---

## 5. SYSTEM REQUIREMENTS

### Functional Requirements
- **FR1 (Authentication):** The system shall verify credentials against configured administrative profiles and reject unauthorized logins.
- **FR2 (Add Student):** The system shall enforce non-null values for Roll Number, Name, Course, Year, Email, Phone, and Gender before writing to the database.
- **FR3 (Read Records):** The system shall retrieve all active student profiles from MySQL and populate a structured `JTable`.
- **FR4 (Update Profile):** The system shall permit administrators to modify student details without altering the primary key identifier.
- **FR5 (Delete Profile):** The system shall prompt the user with a confirmation warning before removing an entry from the database.
- **FR6 (Search):** The system shall support real-time querying by roll number, partial name, and academic course.

### Non-Functional Requirements
- **NFR1 (Performance):** SQL queries and UI rendering must complete within 200ms under standard operational conditions.
- **NFR2 (Security):** All SQL executions must utilize parameterized `PreparedStatement` objects to eliminate SQL Injection (CWE-89).
- **NFR3 (Usability):** High-contrast color palette (Dark Navy `#1a237e`, Button Blue `#1565c0`, Accent Teal `#00897b`) adhering to accessibility standards.
- **NFR4 (Reliability):** Application state and student data must persist across restarts.

---

## 6. SOFTWARE AND HARDWARE REQUIREMENTS

### Hardware Requirements
- **Processor:** Intel Core i3 / AMD Ryzen 3 or higher (x86_64 or ARM64)
- **RAM:** Minimum 4 GB (8 GB recommended)
- **Disk Space:** 250 MB free space for JDK, MySQL database, and application binaries
- **Display:** 1280 × 720 minimum screen resolution (1920 × 1080 recommended)

### Software Requirements
- **Operating System:** Windows 10/11, Ubuntu 20.04+, or macOS Monterey+
- **Java Development Kit:** JDK 21 LTS (or compatible JDK 17+)
- **Look and Feel:** FormDev FlatLaf 3.4
- **Database Engine:** MySQL Community Server 8.0 or higher
- **JDBC Driver:** MySQL Connector/J 8.3.0
- **Testing Framework:** JUnit 5 (Jupiter 5.10.2)
- **Build Tool:** Apache Maven 3.9+ / PowerShell Build Automation

---

## 7. SYSTEM ARCHITECTURE

The application follows a clean 4-tier Layered Architecture:

```
+-------------------------------------------------------------+
|                     PRESENTATION TIER                       |
|   LoginFrame   |   DashboardFrame   |   StudentFormPanel    |
|               StudentTablePanel     |   FlatLaf 3.4         |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                       BUSINESS TIER                         |
|   AuthenticationService   |   StudentService                |
|   ValidationUtil          |   PasswordUtil                  |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                    DATA ACCESS TIER (DAO)                   |
|   StudentDAO              |   DatabaseConnection (Singleton)|
|   DatabaseInitializer     |   AppConfig                     |
+-------------------------------------------------------------+
                              |
                              v
+-------------------------------------------------------------+
|                     PERSISTENCE TIER                        |
|   MySQL Relational Database Server 8.0                      |
|   Database: student_record_db | Table: students             |
+-------------------------------------------------------------+
```

---

## 8. DATA FLOW DIAGRAM (DFD)

### Level 0 DFD (Context Diagram)

```
                     +----------------------------------+
                     |                                  |
                     |         Administrator            |
                     |                                  |
                     +----------------------------------+
                        |                             ^
           Credentials /|                             | Dashboard Metrics /
           Student Data |                             | Tabular Records /
                        v                             | Notifications
                     +----------------------------------+
                     |               0.0                |
                     |      Student Record              |
                     |      Management System           |
                     +----------------------------------+
                        |                             ^
       SQL Insert /     |                             | Relational Tuples /
       Update / Delete  v                             | Query Results
                     +----------------------------------+
                     |           [ D1 ] MySQL           |
                     |         student_record_db        |
                     +----------------------------------+
```

### Level 1 DFD (Decomposed Functional Modules)

```
[Admin] ---> (1.0 Login Verification) ---> [Admin Model / Session]
               |
               v (Authenticated)
[Admin] ---> (2.0 Dashboard Processing) <---> [D1: students table]
               |
               +---> (3.0 Add Record & Validate) ---> [D1: students table]
               |
               +---> (4.0 Query & Browse Table) <---> [D1: students table]
               |
               +---> (5.0 Edit & Update Record) ---> [D1: students table]
               |
               +---> (6.0 Delete Record with Prompt) ---> [D1: students table]
               |
               +---> (7.0 Parametric Search) <---> [D1: students table]
```

---

## 9. DATABASE DESIGN

### Database Schema Specification
- **Database Name:** `student_record_db`
- **Table Name:** `students`
- **Character Encoding:** `utf8mb4`
- **Storage Engine:** `InnoDB` (ACID compliant, Row-level locking)

### Data Dictionary

| Column Name | Data Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique internal surrogate identifier. |
| `roll_number` | `VARCHAR(20)` | `UNIQUE`, `NOT NULL` | Institutional student roll identifier. |
| `name` | `VARCHAR(100)` | `NOT NULL` | Full legal name of the student. |
| `course` | `VARCHAR(100)` | `NOT NULL` | Departmental degree stream. |
| `academic_year` | `VARCHAR(20)` | `NOT NULL` | Enrolled academic year session (e.g. 2026-27). |
| `email` | `VARCHAR(100)` | `NOT NULL` | Institutional or personal email address. |
| `phone` | `VARCHAR(20)` | `NOT NULL` | Contact telephone number (10 digits). |
| `gender` | `VARCHAR(10)` | `NOT NULL` | Demographic gender classification. |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | System timestamp at the time of entry. |

### DDL Implementation Script
```sql
CREATE DATABASE IF NOT EXISTS student_record_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE student_record_db;

CREATE TABLE IF NOT EXISTS students (
    id INT PRIMARY KEY AUTO_INCREMENT,
    roll_number VARCHAR(20) UNIQUE NOT NULL,
    name VARCHAR(100) NOT NULL,
    course VARCHAR(100) NOT NULL,
    academic_year VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    gender VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
```

---

## 10. JAVA SWING COMPONENTS USED

The GUI is developed using pure standard Java Swing (`javax.swing.*`) and AWT (`java.awt.*`), enhanced with FormDev FlatLaf for styling:

| Component | Class Name | Function in Application |
| :--- | :--- | :--- |
| **Top-Level Frames** | `JFrame` | `LoginFrame` and `DashboardFrame` serving as master window containers. |
| **Containers & Cards** | `JPanel` | Content grouping, sidebar navigation, stat cards, and form layouts. |
| **Navigation Layout** | `CardLayout` | Instant swapping of panels (Dashboard, Add, View, Search) without window flickering. |
| **Flexible Grids** | `GridBagLayout` | Responsive alignment of labels, textfields, and comboboxes in forms. |
| **Directional Layout** | `BorderLayout` | Standardizing North headers, West sidebars, and Center viewports. |
| **Tabular Data** | `JTable` | Rendering tabular student rows with custom renderers and alternating colors. |
| **View Scrolling** | `JScrollPane` | Enabling vertical and horizontal scrolling across tables and forms. |
| **Input Fields** | `JTextField` | Roll number, student name, email, and telephone entry. |
| **Masked Input** | `JPasswordField`| Masking password entry during authentication. |
| **Dropdown Menus** | `JComboBox` | Enforcing predefined selections for Courses and Academic Years. |
| **Mutual Exclusion** | `JRadioButton` / `ButtonGroup` | Selecting demographic gender (Male, Female, Other). |
| **Interactive Controls**| `JButton` | Triggering actions (Login, Save, Update, Delete, Search, Reset). |
| **Dialog Prompts** | `JOptionPane` / `JDialog` | Modals for error alerts, success confirmations, and record editing. |

---

## 11. OOP CONCEPTS IMPLEMENTED

The system serves as a practical implementation of fundamental Object-Oriented Programming concepts:

### 1. Encapsulation
All attributes in domain model classes (`Student.java` and `Admin.java`) are declared `private`. Access and mutation are granted strictly through public getter and setter methods. Internal representation is hidden from consumers.
```java
public class Student {
    private int id;
    private String rollNumber;
    private String name;
    // ...
    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }
}
```

### 2. Abstraction
The presentation layer communicates with the service layer (`StudentService`) without exposure to SQL syntax, connection strings, or database schemas. All database querying logic is abstracted into `StudentDAO`.

### 3. Singleton Design Pattern
The `DatabaseConnection` class guarantees that only a single instance coordinates driver registration and connection parameters:
```java
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private DatabaseConnection() { ... }
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) instance = new DatabaseConnection();
        return instance;
    }
}
```

### 4. Polymorphism & Method Overriding
- Overrode `toString()` in `Student` to render formatted debugging representations.
- Overrode `equals()` and `hashCode()` in `Student` based on unique institutional Roll Numbers.
- Overrode `isCellEditable()` in `DefaultTableModel` to ensure read-only tabular presentation.
- Overrode `getTableCellRendererComponent()` in `DefaultTableCellRenderer` to create alternating zebra-stripes.

### 5. Method Overloading
Multiple constructors are defined in `Student` (full constructor for database mapping vs. 7-parameter constructor for new student creation) and `Admin` (full constructor vs. username/password constructor).

### 6. Collections Framework
- Extensively uses `ArrayList<Student>` and `List<Student>` for retrieving database records.
- Utilizes `List<String>` for accumulating validation error messages.
- Employs Java Stream API (`stream().filter(...).count()`) for analytical processing.

---

## 12. CRUD OPERATIONS

All four foundational database operations are implemented in `StudentDAO.java`:

### Create (Insert)
```sql
INSERT INTO students (roll_number, name, course, academic_year, email, phone, gender) 
VALUES (?, ?, ?, ?, ?, ?, ?);
```
- Inserts new student record using parameterized values.
- Catches duplicate roll number collisions gracefully.

### Read (Select)
```sql
SELECT * FROM students ORDER BY id DESC;
SELECT * FROM students WHERE roll_number = ?;
SELECT * FROM students WHERE name LIKE ?;
```
- Maps `ResultSet` tuples directly into strongly typed `Student` objects.

### Update (Modify)
```sql
UPDATE students SET roll_number = ?, name = ?, course = ?, academic_year = ?, 
email = ?, phone = ?, gender = ? WHERE id = ?;
```
- Modifies student profile attributes while preserving record ID and creation timestamp.

### Delete (Remove)
```sql
DELETE FROM students WHERE id = ?;
```
- Removes record upon explicit user confirmation from `JOptionPane`.

---

## 13. JDBC CONNECTIVITY

The system connects to MySQL using the official MySQL Connector/J driver (`com.mysql.cj.jdbc.Driver`).

### Connection String Specification
```
jdbc:mysql://localhost:3306/student_record_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
```
Key connection flags:
- `useSSL=false`: Disables SSL overhead in local testing environments.
- `allowPublicKeyRetrieval=true`: Supports SHA256 password authentication in MySQL 8.0+.
- `serverTimezone=UTC`: Eliminates timezone discrepancies between client JVM and database server.

### Automated Database Bootstrapping
`DatabaseInitializer.java` implements automated schema creation:
1. Connects to MySQL server without database specification (`jdbc:mysql://localhost:3306`).
2. Executes `CREATE DATABASE IF NOT EXISTS student_record_db`.
3. Connects to `student_record_db` and executes `CREATE TABLE IF NOT EXISTS students (...)`.

---

## 14. INPUT VALIDATION

Validation is performed in `ValidationUtil.java` before any SQL query is dispatched:

1. **Mandatory Field Check:** Ensures no field contains null or blank strings.
2. **Roll Number Format:** Must contain at least 2 alphanumeric characters (`^[a-zA-Z0-9_-]{2,20}$`).
3. **Student Name:** Restricts names to alphabetic characters, spaces, and periods (`^[a-zA-Z\\s.]{2,100}$`).
4. **Email Address:** Conforms to RFC standard email syntax (`^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$`).
5. **Phone Number:** Validates 10-digit Indian mobile numbers with optional country code (`^(\\+91)?[6-9]\\d{9}$`).
6. **Gender:** Enforces selection of one valid demographic option.

---

## 15. EXCEPTION HANDLING

The application implements defensive programming to ensure fault tolerance:

- **`SQLException`:** Trapped inside DAO methods; prints diagnostic stack information to error streams and returns structured boolean/error feedback to the UI.
- **`SQLIntegrityConstraintViolationException`:** Specifically identified during insert/update operations to present a user-friendly duplicate roll number notification.
- **`try-with-resources` Blocks:** Utilized on all `Connection`, `PreparedStatement`, and `ResultSet` instances, guaranteeing that database handles and sockets are closed even if runtime errors occur.
- **Null Safety:** Guard clauses protect against null pointer exceptions across models, services, and table renderers.

---

## 16. TESTING RESULTS

An automated JUnit 5 test suite (`StudentManagementTest.java`) containing 29 test cases was executed via `TestRunner.java`.

### Test Execution Log

```
============================================================
  PCC-CS503 STUDENT RECORD MANAGEMENT SYSTEM - TEST SUITE   
  Institution: Academy of Technology | CSE Department       
============================================================

  [PASS] ✓ Student Model - Encapsulation Test
  [PASS] ✓ Student Model - Equals and HashCode based on Roll Number
  [PASS] ✓ Student Model - toString Override
  [PASS] ✓ Admin Model - Constructor and Getters
  [PASS] ✓ Admin Model - toString does not expose password
  [PASS] ✓ Validation - Valid Email Addresses
  [PASS] ✓ Validation - Invalid Email Addresses
  [PASS] ✓ Validation - Valid Phone Numbers
  [PASS] ✓ Validation - Invalid Phone Numbers
  [PASS] ✓ Validation - Valid Names
  [PASS] ✓ Validation - Invalid Names
  [PASS] ✓ Validation - Valid Roll Numbers
  [PASS] ✓ Validation - Invalid Roll Numbers
  [PASS] ✓ Validation - Null or Empty Check
  [PASS] ✓ Validation - Complete Student Validation (Valid)
  [PASS] ✓ Validation - Complete Student Validation (Invalid - Empty Fields)
  [PASS] ✓ Validation - Invalid Email in Student
  [PASS] ✓ Validation - Invalid Phone in Student
  [PASS] ✓ Authentication - Successful Login
  [PASS] ✓ Authentication - Failed Login (Wrong Password)
  [PASS] ✓ Authentication - Failed Login (Wrong Username)
  [PASS] ✓ Authentication - Logout
  [PASS] ✓ PasswordUtil - Correct Password Verification
  [PASS] ✓ PasswordUtil - Incorrect Password Verification
  [PASS] ✓ AppConfig - Default Values
  [PASS] ✓ AppConfig - JDBC URL Format
  [PASS] ✓ OOP - Student Polymorphism via Object
  [PASS] ✓ Collections - ArrayList of Students
  [PASS] ✓ OOP - Method Overloading in Admin Constructor

------------------------------------------------------------
  TEST EXECUTION SUMMARY: 29 Total | 29 Passed | 0 Failed
------------------------------------------------------------
  >>> ALL UNIT TESTS PASSED SUCCESSFULLY! <<<
```

---

## 17. DEMONSTRATION & SCREENSHOTS GUIDE

To capture screenshots for the final submission report:

1. **Login Screen:**
   - Launch application via `run.bat` or `run.ps1`.
   - Capture the split-panel login screen showing institutional branding on the left and input fields on the right.
2. **Invalid Login Attempt:**
   - Enter `admin` / `wrongpassword` and click **Login**.
   - Capture the red error message and animated window state.
3. **Main Dashboard:**
   - Login with `admin` / `admin123`.
   - Capture the full window showing the dark navy navigation sidebar, dynamic statistic cards, recent students table, and quick action buttons.
4. **Add Student Form:**
   - Click **➕ Add Student** on the sidebar.
   - Capture the registration form with filled inputs before submission.
5. **Validation Error Notice:**
   - Clear the email field or enter an invalid phone number and click **Save Student**.
   - Capture the warning dialog enumerating validation errors.
6. **Student Records Table:**
   - Click **📋 View Students**.
   - Capture the `JTable` populated with registered student records, alternating row highlights, and record counter.
7. **Update Student Dialog:**
   - Select a row in the table and click **✏️ Edit Selected**.
   - Capture the modal update dialog with pre-populated student data.
8. **Delete Confirmation Dialog:**
   - Select a row and click **🗑️ Delete Selected**.
   - Capture the confirmation dialog showing student name and roll number.
9. **Search Results:**
   - Click **🔍 Search Students**, select search criteria by `Course` or `Name`, enter keyword, and click **Search**.
   - Capture the dynamically filtered table results.

---

## 18. CONCLUSION

The Design and Development of the Student Record Management System demonstrates the practical application of Object-Oriented Software Engineering principles taught in course PCC-CS503. By decoupling the presentation, business, and persistence layers, the software achieves high maintainability, testability, and structural clarity. The integration of Java Swing with modern FlatLaf styling bridges the gap between academic demonstrations and industrial-grade software design. The system satisfies all assessment criteria mandated by the Department of Computer Science & Engineering at Academy of Technology.

---

## 19. FUTURE SCOPE

Future enhancements planned for subsequent releases:
1. **Role-Based Access Control (RBAC):** Introducing multi-tiered user privileges (Super Admin, Faculty, Student Viewers).
2. **Export to PDF and Excel:** Integration of Apache POI or iText libraries for automated transcript and attendance sheet generation.
3. **Student Photograph Integration:** Support for student image uploads stored as BLOB or file-system paths.
4. **REST API Integration:** Exposing a lightweight Spring Boot backend allowing mobile app connectivity.
5. **Advanced Analytics:** Chart-based visualisations (e.g. enrolment distribution per department) using JFreeChart.

---

**Report Prepared By:** B.Tech 5th Semester Student (CSE3)  
**Academy of Technology**  
**Course Code:** PCC-CS503 (Object Oriented Programming)

