# JD Quest - My Semester 1 Java Project 🎮

Hi, this is my Advanced Java Programming project for Semester 1 at Nehru MCA. I've named it **JD Quest**. It's a level-based quiz game that uses Java Swing for the design and MySQL to save user data.

## 🚀 How to Run the Program

The project currently uses a merged single-file entry point for easier execution.

### Prerequisites
1.  **MySQL Database**: Ensure MySQL is running on your machine.
2.  **Database Configuration**: Default settings are `root` for user and `1525` for password.
3.  **JAR Dependencies**: The project uses `mysql-connector-j-9.5.0.jar` (found in `src/`).

### Setup & Launch
1.  **Initialize Database**:
    Run the provided `DBScript.java` once to create the `leveldevil` database and `players` table automatically:
    ```bash
    javac -cp ".;src/mysql-connector-j-9.5.0.jar" DBScript.java
    java -cp ".;src/mysql-connector-j-9.5.0.jar" DBScript
    ```

2.  **Compile & Run the Game**:
    Use the following command from the root folder:
    ```bash
    javac -cp ".;src/mysql-connector-j-9.5.0.jar" LevelDevilAllInOne.java
    java -cp ".;src/mysql-connector-j-9.5.0.jar" LevelDevilAllInOne
    ```

## 📸 Screenshots

Here are some glimpses of the game in action!

| Game Level 1 | Quiz Challenge | Game Level 2 |
| :---: | :---: | :---: |
| ![Level 1](src/main/java/com/jdgame/assets/Screenshot%202026-03-29%20154853.png) | ![Quiz Challenge](src/main/java/com/jdgame/assets/Screenshot%202026-03-29%20154909.png) | ![Level 2](src/main/java/com/jdgame/assets/Screenshot%202026-03-29%20154944.png) |

## ✨ What it does
- **Login:** Users can log in to keep track of their scores.
- **Levels:** There are 15 levels. As you clear one, you move to the next.
- **Save Progress:** The game saves your high score and which level you reached so you don't lose progress.
- **Performance Logs:** It even tracks how much time you take for each question.
- **Easy Config:** I've put settings like JDBC URL and number of levels in one file so I can change them easily.

## 🛠 Tech Used
- **Language:** Java 17
- **Tools:** Maven (for managing JAR files like the MySQL connector)
- **GUI:** Swing
- **Database:** MySQL
- **Data storage:** Using JDBC to manage user data in `leveldevil` database.

## 📁 How the code is organized
- `LevelDevilAllInOne.java`: The primary merged project file containing UI, Logic, and DB connections.
- `DBScript.java`: A helper script for quick database setup.
- `src/main/java/com/jdgame/assets`: Multimedia assets like screenshots.
- `sql/`: Original schema file used for project planning.

## 🗄 Database Tables
- `players`: Stores who is playing, their top scores (level) and death counts.

**Author:** SIVAPRAKASH M
**College:** NIITM MCA
