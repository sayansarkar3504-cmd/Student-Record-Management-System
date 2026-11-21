🎓 Student Record Management System (SRMS)

A Java Swing & MySQL-Based Desktop Application

<p align="center">
<b>Academy of Technology</b><br>
Department of Computer Science & Engineering
</p>

<p align="center">
<img src="https://img.shields.io/badge/Java-21-orange?logo=openjdk" />
<img src="https://img.shields.io/badge/Java%20Swing-GUI-blue" />
<img src="https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql" />
<img src="https://img.shields.io/badge/Maven-Build-red?logo=apachemaven" />
<img src="https://img.shields.io/badge/Status-Completed-success" />
</p>

📚 Academic Information
Particulars	Details
Institution	Academy of Technology
Department	Computer Science & Engineering
Course Name	Object Oriented Programming
Course Code	PCC-CS503
Semester	5th Semester (B.Tech)
Academic Session	2026–27
Assessment	CA3 Application-Based Assessment / Mini Project
Faculty Evaluator	Dr. Krishna Bhowal
Maximum Marks	25
Submission Date	21 November 2026
📋 Project Overview

The Student Record Management System (SRMS) is a desktop-based application developed using Java, Java Swing, JDBC, and MySQL.

The application provides an efficient platform for academic administrators to manage student information through a user-friendly graphical interface.

It supports student registration, record modification, deletion, searching, filtering, authentication, and real-time dashboard analytics.

The project follows Object-Oriented Programming principles and a layered software architecture to maintain modularity, code reusability, and maintainability.

⭐ Key Highlight: Automatic Database Management

One of the major features of this application is its automatic database initialization system.

On startup, the application can:

Connect to the local MySQL server.
Automatically create the student_record_db database if it does not exist.
Automatically create the students table.
Create secondary indexes for faster searching.
Verify database schema and connectivity.
Display connection diagnostics if MySQL is unavailable.
Allow users to retry the database connection.

No manual SQL execution in MySQL Workbench is required for initial database creation.

✨ Features
1. Automatic Database Management
Automatic database creation.
Automatic table initialization.
Automatic index creation.
Database connectivity validation.
MySQL connection retry support.
Persistent storage using MySQL.
2. Student Record Management (CRUD)
Create: Register new student records.
Read: Display all registered students.
Update: Edit existing student information.
Delete: Remove student records with confirmation.
Duplicate roll number prevention.
Input validation before database operations.
3. Advanced Search & Filtering

Search student records using:

Roll Number
Student Name
Course / Department
Global Search

Additional features:

Partial text matching.
Real-time table filtering.
Interactive column sorting.
Efficient database queries using JDBC.
4. Modern Graphical User Interface

Built using Java Swing with FlatLaf styling.

Academic Navy Blue theme.
Teal accent colors.
Clean dashboard layout.
Interactive navigation sidebar.
Custom table rendering.
Alternating row colors.
Responsive login and registration forms.
Password visibility toggle.
Demo credential autofill.
MySQL connection status indicator.
5. Executive Dashboard

The dashboard provides real-time statistics:

Total Students
Total Courses
Current Academic Session
Recently Registered Students
6. Authentication System
Administrator login.
Credential verification.
Session management.
Demo login functionality.
🛠️ Technologies Used
Technology	Purpose
Java 21	Core application development
Java Swing	Graphical User Interface
FlatLaf	Modern UI styling
MySQL 8.0	Relational database
JDBC	Database connectivity
Maven	Dependency management and build
SQL	Database operations
JUnit 5	Unit testing
🏛️ Project Architecture

The application follows a layered architecture to separate user interface, business logic, database operations, and data models.

text
src/main/java/com/aot/studentmanagement/
│
├── Main.java
│
├── config/
│   └── AppConfig.java
│
├── database/
│   ├── DatabaseConnection.java
│   └── DatabaseInitializer.java
│
├── dao/
│   └── StudentDAO.java
│
├── service/
│   ├── StudentService.java
│   └── AuthenticationService.java
│
├── model/
│   ├── Student.java
│   └── Admin.java
│
├── ui/
│   ├── LoginFrame.java
│   ├── DashboardFrame.java
│   ├── StudentFormPanel.java
│   └── StudentTablePanel.java
│
└── util/
    ├── ValidationUtil.java
    └── PasswordUtil.java

Architecture Components
Layer	Responsibility
UI Layer	Handles graphical interaction
Service Layer	Implements business logic
DAO Layer	Executes database operations
Model Layer	Represents application entities
Database Layer	Manages connections and initialization
Utility Layer	Provides validation and helper functions
🧠 Object-Oriented Programming Concepts

This project demonstrates the core principles of Object-Oriented Programming.

OOP Concept	Implementation
Encapsulation	Private fields in Student and Admin classes
Abstraction	Service classes hide database implementation details
Inheritance	Reuse of Java class functionality where applicable
Polymorphism	Method overriding and constructor overloading
Singleton Pattern	Centralized database connection management
Collections	ArrayList and List for record management
Exception Handling	Try-with-resources and SQL exception handling
🗄️ Database Design

Database Name: student_record_db

Table Name: students

Storage Engine: InnoDB

Character Set: utf8mb4

Students Table Structure
Column	Data Type	Constraint
id	INT	Primary Key, Auto Increment
roll_number	VARCHAR(20)	Unique, Not Null
name	VARCHAR(100)	Not Null
course	VARCHAR(100)	Not Null
academic_year	VARCHAR(20)	Not Null
email	VARCHAR(100)	Not Null
phone	VARCHAR(20)	Not Null
gender	VARCHAR(10)	Not Null
created_at	TIMESTAMP	Default Current Timestamp
Database Configuration

Create a database.properties file in the project root if custom credentials are required.

properties
DB_HOST=localhost
DB_PORT=3306
DB_NAME=student_record_db
DB_USER=root
DB_PASSWORD=your_mysql_password


Alternatively, environment variables can be used.

Security Note: Never upload your actual database password or private credentials to a public GitHub repository. Use database.properties.example as the configuration template.

🚀 Installation & Setup
Prerequisites

Install the following software:

Java JDK 21 or later.
MySQL Server 8.0 or compatible version.
Maven (if not using the Maven Wrapper).
Git (optional, for cloning the repository).
Step 1: Clone the Repository
bash
git clone https://github.com/sayansarkar3504-cmd/Student-Record-Management-System.git

Step 2: Open the Project
bash
cd Student-Record-Management-System


Open the project folder in VS Code or your preferred Java IDE.

Step 3: Configure MySQL

Make sure the MySQL server is running.

If necessary, create database.properties in the project root and configure your MySQL username and password.

The application automatically initializes the database and required table.

Step 4: Run the Application

Option 1: Windows Batch File

cmd
run.bat


Option 2: PowerShell

powershell
.\run.ps1


Option 3: Maven

bash
mvn clean package


Then execute:

bash
java -jar target/student-record-management-1.0.0.jar


Option 4: Build Scripts

Windows:

cmd
build.bat


PowerShell:

powershell
.\build.ps1

🔑 Demo Login Credentials
Field	Value
Username	admin
Password	admin123

The login screen also provides a demo credential autofill option.

Note: Demo credentials are intended for academic demonstration. Change default credentials before any real-world deployment.

🧪 Testing & Verification

The project includes unit testing and database integration testing.

1. Unit Testing

Test Coverage:

Student model validation.
Roll number validation.
Email validation.
Phone number validation.
Authentication logic.
Collection filtering.
Application utility methods.

Reported Test Results:

Metric	Result
Total Tests	29
Passed	29
Failed	0
Pass Rate	100%
2. Database Integration Testing

Tests database operations against a live MySQL instance.

Test Scenarios:

Automatic database initialization.
Table creation.
Student insertion.
Student retrieval.
Student updates.
Student deletion.
Duplicate roll number rejection.
Search functionality.
Database connectivity.

Reported Test Results:

Metric	Result
Total Tests	9
Passed	9
Failed	0
Pass Rate	100%

Testing results are based on the reported project test execution. Run the tests in your own environment to reproduce them.

🎓 Faculty Demonstration Guide

The following workflow can be used during the project presentation.

Step	Demonstration	Expected Result
1	Launch application	Login screen appears
2	Use demo credentials	Username and password are filled
3	Sign in	Dashboard opens
4	Add Student	New student is registered
5	Test Duplicate Roll	Duplicate warning appears
6	View Students	Student records appear in JTable
7	Sort Records	Table sorting works
8	Edit Student	Selected student information is updated
9	Delete Student	Confirmation appears before deletion
10	Search Records	Matching records are displayed
11	Restart Application	Previously saved records remain available
📸 Application Screenshots

Screenshots of the application can be added here.

Suggested screenshots:

Login Screen
Main Dashboard
Add Student Form
Student Records Table
Search and Filtering
Update Student Dialog
Delete Confirmation
MySQL Connection Status

To add screenshots, create a folder named screenshots in the project root.

Example:

text
screenshots/
├── login.png
├── dashboard.png
├── add-student.png
├── student-records.png
└── search.png


Then embed them using:

markdown
![Dashboard](screenshots/dashboard.png)

🔒 Security & Data Integrity

The application incorporates several practices to improve data safety:

Parameterized SQL queries.
PreparedStatement for database operations.
Input validation.
Unique roll number constraints.
Confirmation before deletion.
Try-with-resources for JDBC resource management.
No manual database deletion during initialization.

For production deployment, use stronger password hashing, secure credential storage, and a dedicated database user with restricted permissions.

🔮 Future Enhancements

Possible future improvements include:

Student profile photo upload.
Export student records to PDF and Excel.
Attendance management.
Fee management.
Role-based administrator permissions.
Advanced reporting and analytics.
Backup and restore functionality.
Cloud database integration.
📜 Academic Attribution

Developed as part of the PCC-CS503 – Object Oriented Programming Application-Based Assessment / Mini Project.

Institution: Academy of Technology
Department: Computer Science & Engineering
Academic Session: 2026–27
Semester: 5th Semester (B.Tech)
Faculty Evaluator: Dr. Krishna Bhowal

👨‍💻 Developer

GitHub: @sayansarkar3504-cmd

Project Repository: Student Record Management System

<p align="center">
<b>Student Record Management System</b><br>
Built with Java, Swing, JDBC & MySQL
<br><br>
⭐ If you find this project useful, consider giving it a star!
</p>
