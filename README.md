# Student Record Management System (SRMS)

**Institution:** Academy of Technology  
**Department:** Computer Science & Engineering  
**Course Name:** Object Oriented Programming  
**Course Code:** PCC-CS503  
**Semester:** 5th Semester (B.Tech)  
**Academic Session:** 2026–27  
**Assessment:** CA3 Application-Based Assessment / Mini Project  
**Faculty Evaluator:** Dr. Krishna Bhowal  
**Maximum Marks:** 25  
**Submission Date:** 21 November 2026  

---

## 📋 Project Overview

The **Student Record Management System (SRMS)** is an enterprise-grade desktop software application developed using pure Java, Java Swing, and MySQL through JDBC. Conforming strictly to Object-Oriented Programming (OOP) paradigms and a layered architecture, the software empowers academic administrators to register students, perform high-speed searches, manage departmental enrolments, inspect live analytics, and guarantee relational data persistence.

The system features **Fully Automatic Database Management**: it automatically connects to the local MySQL server, provisions the database (`student_record_db`), creates the relational schema (`students`), builds secondary performance indexes, and validates data integrity—**without requiring manual SQL execution in MySQL Workbench**.

---

## ✨ Key Upgrades & Highlights

### 1. Fully Automatic Database Management
- **Zero Manual SQL Setup:** The application autonomously executes `CREATE DATABASE IF NOT EXISTS student_record_db` and creates the `students` table.
- **Auto-Indexing:** Automatically creates indexes on `name` and `course` for fast searching.
- **Integrity Validation:** Verifies schema structure on startup and prevents accidental data deletion or table drops.
- **Automatic Recovery:** If MySQL is offline, a non-blocking diagnostic dialog appears with a **"Retry Connection"** action that allows instant reconnection once MySQL is started.

### 2. Multi-Criteria Student Management (CRUD)
- **Add Student:** Validates Roll Number (alphanumeric/hyphen/underscore), Full Name, Department Stream, Academic Session, Email (RFC syntax), Phone (10 digits), and Gender.
- **View Students:** High-performance `JTable` inside `JScrollPane` with **interactive column sorting**, alternating zebra stripes, custom cell renderers, and an empty-state screen.
- **Real-Time Quick Filter:** Filter records instantly on every keystroke directly within the table toolbar.
- **Update Student:** Safe model-index conversion (`convertRowIndexToModel`) ensures sorting never modifies the wrong record. Modal dialog allows editing with full re-validation.
- **Delete Student:** Detailed confirmation warning displaying student roll number and full name before committing deletion to MySQL.
- **Search Engine:** Filter by **Roll Number**, **Student Name**, **Course**, or **Global Search (All Fields)** with partial string matching (`LIKE %keyword%`).

### 3. Modern UI/UX Redesign (Java Swing + FlatLaf)
- **Theme:** Academic Navy Blue (`#1a237e`), Accent Teal (`#00897b`), Clean White Cards, and Crisp Typography.
- **Split-Panel Sign In:** Institutional branding on the left, masked password with show/hide toggle (`👁`), caps-lock detection, animated shake on error, and a **"Use Demo Credentials"** one-click chip.
- **Live Connection Badge:** Real-time indicator (`🟢 MySQL Online` / `🟡 MySQL Disconnected`).
- **Dynamic Executive Dashboard:** Real-time statistics cards (Total Students, Total Courses, Academic Session) dynamically queried from MySQL, plus a *Recently Registered Students* table.

---

## 🏛️ Layered Architecture & OOP Principles

```
src/main/java/com/aot/studentmanagement/
├── Main.java                          # Entry point, FlatLaf styling & DB bootstrapping
├── config/
│   └── AppConfig.java                 # Config manager (database.properties, env, system properties)
├── database/
│   ├── DatabaseConnection.java        # Thread-safe Singleton JDBC connection factory
│   └── DatabaseInitializer.java       # Automatic DDL runner (CREATE DB, TABLE, INDEXES, VERIFY)
├── dao/
│   └── StudentDAO.java                # Data Access Object executing parameterized SQL queries
├── service/
│   ├── StudentService.java            # Business logic, validation coordination, duplicate checks
│   └── AuthenticationService.java     # Admin authentication & session management
├── model/
│   ├── Student.java                   # Encapsulated student domain entity
│   └── Admin.java                     # Admin entity with credential shielding
├── ui/
│   ├── LoginFrame.java                # Branded split-panel login window
│   ├── DashboardFrame.java            # Master navigation frame with sidebar & CardLayout
│   ├── StudentFormPanel.java          # Add / Update student input form with live validation
│   └── StudentTablePanel.java         # JTable records browser with live filter & sorting
└── util/
    ├── ValidationUtil.java            # Static regex and constraint validation methods
    └── PasswordUtil.java              # Password verification utilities
```

### OOP Implementation Highlights

| OOP Principle | Implementation in Project |
| :--- | :--- |
| **Encapsulation** | Private attributes in `Student` and `Admin`, exposed via strongly-typed getters/setters with validation. |
| **Abstraction** | GUI panels interact purely via `StudentService` and `AuthenticationService` APIs, completely shielding UI from SQL syntax. |
| **Singleton Pattern** | `DatabaseConnection` enforces a single instance managing driver loading and connection pooling. |
| **Polymorphism** | Overridden `toString()`, `equals()`, and `hashCode()` based on institutional Roll Numbers; overloaded constructors. |
| **Collections** | Extensive use of `ArrayList<Student>` and `List<String>` for batch data retrieval and error accumulation. |
| **Exception Handling** | `try-with-resources` ensuring automated closure of `Connection`, `PreparedStatement`, and `ResultSet`; custom handling of `SQLIntegrityConstraintViolationException`. |

---

## 🗄️ Database Design

- **Database Name:** `student_record_db`
- **Table Name:** `students`
- **Engine:** `InnoDB` (ACID Compliant)
- **Charset:** `utf8mb4`

| Column | Type | Constraints | Description |
| :--- | :--- | :--- | :--- |
| `id` | `INT` | `PRIMARY KEY`, `AUTO_INCREMENT` | Unique internal surrogate identifier. |
| `roll_number` | `VARCHAR(20)` | `UNIQUE`, `NOT NULL` | Institutional roll identifier. |
| `name` | `VARCHAR(100)` | `NOT NULL` | Full student legal name. |
| `course` | `VARCHAR(100)` | `NOT NULL` | Departmental degree stream. |
| `academic_year` | `VARCHAR(20)` | `NOT NULL` | Enrolled academic session (e.g. 2026-27). |
| `email` | `VARCHAR(100)` | `NOT NULL` | Valid email address. |
| `phone` | `VARCHAR(20)` | `NOT NULL` | 10-digit telephone number. |
| `gender` | `VARCHAR(10)` | `NOT NULL` | Demographic gender (Male/Female/Other). |
| `created_at` | `TIMESTAMP` | `DEFAULT CURRENT_TIMESTAMP` | Registration timestamp. |

---

## 🚀 Easy One-Click Execution (Windows & VS Code)

### Option 1: Double-Click Batch File (Fastest)
Double-click [run.bat](file:///d:/java%20program/Student%20Record%20Management%20System/run.bat) in the project folder, or run in Command Prompt:
```cmd
run.bat
```

### Option 2: Windows PowerShell
```powershell
.\run.ps1
```

### Option 3: Direct Executable Fat JAR
```powershell
java -jar target/student-record-management-1.0.0.jar
```

### Option 4: Rebuild the Application
To recompile all sources and reassemble the standalone 3.3 MB Fat JAR:
- Using Batch: `build.bat`
- Using PowerShell: `.\build.ps1`
- Using Maven: `mvn clean package`

---

## 🔑 Administrative Demo Credentials

- **Username:** `admin`
- **Password:** `admin123`
*(A quick-fill chip button is also available on the login screen for instant sign-in).*

---

## ⚙️ Database Configuration

Configuration is automatically read from `database.properties` in the project root:
```properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=student_record_db
DB_USER=root
DB_PASSWORD=your_mysql_password
```
If `database.properties` is omitted, the application seamlessly falls back to environment variables (`DB_HOST`, `DB_PORT`, `DB_USER`, `DB_PASSWORD`) or sensible local defaults.

---

## 🧪 Automated Testing Verification

The project includes both **Unit Tests** and **Live Database Integration Tests**:

### 1. Unit Test Suite (29 Tests)
Tests encapsulation, validation patterns, authentication logic, and collection filtering:
```powershell
# Run using the automated TestRunner
& "C:\Program Files\Java\jdk-21\bin\java.exe" -cp "target\test-classes;target\classes;C:\Users\sayan\.m2\repository\org\junit\jupiter\junit-jupiter-api\5.10.2\junit-jupiter-api-5.10.2.jar;C:\Users\sayan\.m2\repository\org\junit\platform\junit-platform-commons\1.10.2\junit-platform-commons-1.10.2.jar;C:\Users\sayan\.m2\repository\org\opentest4j\opentest4j\1.3.0\opentest4j-1.3.0.jar;C:\Users\sayan\.m2\repository\com\formdev\flatlaf\3.4\flatlaf-3.4.jar;C:\Users\sayan\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar" com.aot.studentmanagement.TestRunner
```
*Result: 29 Total | 29 Passed | 0 Failed (100% Pass Rate).*

### 2. Live Database Integration Test Suite (9 Tests)
Tests real schema auto-creation, INSERT, SELECT, UPDATE, DELETE, duplicate rejection, and search queries directly against MySQL:
```powershell
& "C:\Program Files\Java\jdk-21\bin\java.exe" -cp "target\test-classes;target\classes;C:\Users\sayan\.m2\repository\com\formdev\flatlaf\3.4\flatlaf-3.4.jar;C:\Users\sayan\.m2\repository\com\mysql\mysql-connector-j\8.3.0\mysql-connector-j-8.3.0.jar" com.aot.studentmanagement.DatabaseIntegrationTest
```
*Result: 9 Total | 9 Passed | 0 Failed (100% Pass Rate).*

---

## 🎓 Faculty Demonstration Checklist

| Step | Action | Expected Behavior |
| :---: | :--- | :--- |
| 1 | Launch via `run.bat` | Login screen opens; top right displays `🟢 MySQL Online (student_record_db)`. |
| 2 | Click "🔑 Use Demo Credentials" | Auto-fills `admin` and `admin123`. |
| 3 | Click "Sign In" | Authenticates session; Dashboard opens with live KPI metric cards. |
| 4 | Click "➕ Add Student" | Fill registration form; click "Save Student Record". Student persisted in MySQL. |
| 5 | Test Duplicate Roll | Enter the same roll number; system displays duplicate roll alert. |
| 6 | Click "📋 View Students" | `JTable` displays all students. Test header click for sorting; type in quick filter box to search. |
| 7 | Edit Student | Select a row, click "✏️ Edit Selected" (or double-click row). Update fields and save. |
| 8 | Delete Student | Select a row, click "🗑️ Delete Selected". Confirm warning prompt. Record removed from MySQL and dashboard counters refresh. |
| 9 | Click "🔍 Search Records" | Search by Name or Course with partial match; view filtered results. |
| 10 | Persistence Test | Close the app completely and reopen with `run.bat`. All records remain permanently stored in MySQL! |

---

## 📜 Academic Attribution
Developed for **PCC-CS503 Object Oriented Programming**  
**Institution:** Academy of Technology  
**Department:** Computer Science & Engineering  
**Academic Session:** 2026–27  
**Faculty Evaluator:** Dr. Krishna Bhowal  
