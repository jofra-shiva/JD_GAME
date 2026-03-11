# Project Implementation Report: JD Quest Quiz Engine
**Subject:** Advanced Java Programming (MCA - SEM 1)
**Project Title:** JD_GAME_Maven_Project

## 1. Introduction
The **JD Quest** project is a desktop-based application designed to facilitate level-triggered quiz sessions. It is built using the Java programming language, utilizing Maven for life-cycle management and MySQL for data persistence. This project serves as a practical implementation of Advanced Java concepts including JDBC, Swing, and the Model-View-Controller (MVC) design pattern.

## 2. Objectives
- To create a scalable level-based quiz system.
- To implement persistent user storage and progress tracking.
- To demonstrate proficiency in database connectivity using JDBC.
- To provide an interactive User Interface for better engagement.

## 3. System Analysis

### 3.1 Architecture Overview
The system follows a 3-tier architecture:
1.  **Presentation Tier:** Built using Java Swing (JFrame, JPanel, etc.).
2.  **Logic Tier:** Process flow management, level validation, and score calculation.
3.  **Data Tier:** MySQL database for storing user profiles, questions, and session logs.

### 3.2 Component Analysis
- **Main Entry Point (`Main.java`):** Initializes the database connection and launches the GUI on the Event Dispatch Thread (EDT).
- **Data Access Object (`DBManager.java`):** Centralizes all SQL queries. It uses `PreparedStatement` to prevent SQL injection and manages `ResultSet` objects efficiently.
- **Model Entities (`User.java`, `Question.java`):** Encapsulate data in POJO (Plain Old Java Objects) format, making the data flow clean and readable.
- **Configuration Management (`Config.java`):** Decouples system constants (DB URL, Levels) from the core logic, allowing for easy platform migration.

### 3.3 Database Table Structure
| Table Name | Description | Key Fields |
| :--- | :--- | :--- |
| `users` | Stores user profiles and scores. | `id`, `username`, `high_level_reached` |
| `questions` | Contains the level-wise bank of MCQs. | `level_index`, `correct_choice` |
| `gameplay_logs` | Audit trail for performance tracking. | `user_id`, `time_spent_ms`, `correct` |

## 4. Advanced Java Concepts Used

### A. JDBC (Java Database Connectivity)
The project utilizes JDBC to bridge the Java application with the MySQL server. It implements:
- `DriverManager.getConnection()` for session management.
- `PreparedStatement` for parameterized queries.
- `ResultSet` for data retrieval.

### B. Java Swing (GUI)
The UI is built on the Swing library, focusing on:
- **Event Handling:** Button clicks and window transitions.
- **Layout Management:** Organizing components for a responsive desktop view.
- **Thread Management:** Using `SwingConstants` and `invokeLater` to ensure thread-safe UI updates.

### C. Exception Handling
Robust try-with-resources blocks are used in `DBManager` to ensure database connections are closed automatically, preventing resource leaks.

### D. Collections Framework
`List<Question>` and `ArrayList` are used extensively to manage and iterate through question sets retrieved from the database.

## 5. Conclusion
JD Quest demonstrates a complete end-to-end flow from user input to database storage. The modular nature of the code allows for future scaling, such as adding more levels, implementing timer-based sessions, or migrating to a cloud-based database.

---
**Prepared for:** MCA Semester 1 Viva-Voce
**Project Codebase:** [JD_GAME_Maven_Project]
