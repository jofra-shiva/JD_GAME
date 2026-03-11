# Project Report: JD Quest Quiz Engine
**Subject:** Advanced Java Programming
**Submitted by:** [Your Name]
**Project Name:** JD_GAME_Maven_Project

## 1. About the Project
JD Quest is a simple desktop application I built to learn how Java connects with databases. It's a quiz game where questions come from a MySQL database. I used Maven to manage the project and keep all the libraries organized.

## 2. Why I built this
The main goal was to:
- Learn how to use JDBC to talk to MySQL.
- Practice making GUIs using Java Swing.
- Understand how to save user progress so they don't have to start over every time.

## 3. How it works
This project is split into three main parts:
- **Design (UI):** I used Java Swing to make the windows and buttons.
- **Logic:** I wrote code to check if an answer is right and manage the levels.
- **Database:** I used a MySQL database to store all the user info and questions.

### Important Java Classes I wrote:
- **Main.java:** This is where the program starts. It opens the login window.
- **DBManager.java:** All my SQL code is here. It handles things like loading users and saving scores.
- **User & Question.java:** These are simple classes to hold data while the program is running.
- **Config.java:** I put all my database passwords and settings here so they are easy to change.

## 4. Database Setup
I created three tables for this project:
1. `users`: To keep track of who is playing and their high scores.
2. `questions`: To store all the quiz questions and the correct answers.
3. `gameplay_logs`: To keep a record of how users are performing.

## 5. What I learned
While making this, I got a better understanding of:
- **JDBC:** Using `PreparedStatement` to run SQL queries safely.
- **Swing:** How to use basic layouts and handle button clicks.
- **Maven:** How to easily add JAR files like the MySQL driver to a project.
- **Exceptions:** Using try-catch blocks to handle database errors without crashing the program.

## 6. Conclusion
Building JD Quest helped me see how Advanced Java concepts work in a real application. In the future, I plan to add more levels and maybe some animations to make the UI look even better.
