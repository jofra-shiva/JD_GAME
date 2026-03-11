# JD Quest: Level-Based Quiz Adventure 🎮
*Advanced Java Programming - Semester 1 Project*

## 📌 Project Overview
**JD Quest** is a robust, interactive quiz game developed as part of the MCA Semester 1 "Advanced Java Programming" curriculum. The project demonstrates the integration of **Java Swing** for GUI development and **MySQL** for persistent data management.

The game challenges users with level-based questions, tracks progress through high scores, and provides a persistent experience by saving game states directly to a centralized database.

---

## 🚀 Key Features
- **User Authentication:** Secure login system allowing users to track their individual progress.
- **Level-Based Progression:** 15 distinct levels of increasing difficulty.
- **Dynamic Question Engine:** Fetches multiple-choice questions (MCQs) from the database in real-time.
- **Progress Persistence:** Automatically saves high scores and current level indices.
- **Gameplay Analytics:** Logs setiap attempt (ms spent, correctness) for performance tracking.
- **Custom Configuration:** Easily adjustable game settings (No. of levels, questions per level) via a central configuration utility.

---

## 🛠️ Technology Stack
- **Language:** Java 17+
- **Framework:** Maven (Dependency & Build Management)
- **GUI:** Java Swing (Desktop Interface)
- **Database:** MySQL 8.0
- **Driver:** MySQL Connector/J
- **Data Format:** JSON (for complex state storage)

---

## 📂 Project Structure & Analysis
The project follows a modular architecture to ensure separation of concerns:

- **`com.jdgame.db`**: Handles all JDBC operations, connection pooling, and SQL execution.
- **`com.jdgame.model`**: Contains Data Access Objects (DAOs) for `User` and `Question` entities.
- **`com.jdgame.ui`**: (Work-in-progress) Manages the Swing-based graphical user interface.
- **`com.jdgame.util`**: Centralized configuration and helper utilities.
- **`sql/`**: Contains the database schema and initialization scripts.

---

## 💾 Database Design
The backend is powered by a relational schema optimized for speed and logging:
- **`users`**: Stores credentials, high scores, and a JSON blob of the last saved state.
- **`questions`**: A repository of MCQs categorized by level index.
- **`gameplay_logs`**: Tracks user performance metrics for every question answered.

---

## 🔧 Setup & Installation

### Prerequisites
- JDK 17
- Apache Maven
- MySQL Server

### Database Setup
1. Execute the provided SQL script:
   ```bash
   mysql -u root -p < sql/schema.sql
   ```
2. Configure credentials in `com.jdgame.util.Config.java`.

### Build & Run
```bash
mvn clean install
mvn exec:java -Dexec.mainClass="com.jdgame.Main"
```

---

## 📈 Future Roadmap
- [ ] Complete the UI implementation with custom themes.
- [ ] Add timer-based challenges for difficulty spikes.
- [ ] Implement a Global Leaderboard feature.
- [ ] Add sound effects and animations using Java AWT/Swing timers.

---
**Developed by:** [Your Name]
**Institutional Context:** Nehru MCA - SEM 1 Project
