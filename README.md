# JD Quest - My Semester 1 Java Project 🎮

Hi, this is my Advanced Java Programming project for Semester 1 at Nehru MCA. I've named it **JD Quest**. It's a level-based quiz game that uses Java Swing for the design and MySQL to save user data.

## What it does
- **Login:** Users can log in to keep track of their scores.
- **Levels:** There are 15 levels. As you clear one, you move to the next.
- **Save Progress:** The game saves your high score and which level you reached so you don't lose progress.
- **Performance Logs:** It even tracks how much time you take for each question.
- **Easy Config:** I've put settings like JDBC URL and number of levels in one file so I can change them easily.

## Tech Used
- **Language:** Java 17
- **Tools:** Maven (for managing JAR files like the MySQL connector)
- **GUI:** Swing
- **Database:** MySQL
- **Data storage:** Using JSON to save some of the user states.

## How the code is organized
- `com.jdgame.db`: Has all the SQL connection and query code.
- `com.jdgame.model`: Simple classes for User and Question data.
- `com.jdgame.ui`: Where the screens are designed.
- `com.jdgame.util`: Just a simple config file for DB settings.
- `sql/`: The schema file I used to create my tables.

## Database Tables
- `users`: Stores who is playing and their top scores.
- `questions`: The bank of questions for each level.
- `gameplay_logs`: This is for tracking if answers were correct and the time taken.

## How to setup
1. Open MySQL and run everything inside `sql/schema.sql`.
2. Check `Config.java` to make sure the database username and password match your local PC settings.
3. Run the `Main.java` file.

**Author:** SIVAPRAKASH M
**College:** NIITM MCA
