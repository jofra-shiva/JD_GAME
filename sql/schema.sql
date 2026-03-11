CREATE DATABASE IF NOT EXISTS jdgame;
USE jdgame;

CREATE TABLE IF NOT EXISTS users (
  id INT AUTO_INCREMENT PRIMARY KEY,
  username VARCHAR(50) UNIQUE NOT NULL,
  email VARCHAR(100),
  password_hash VARCHAR(255),
  high_level_reached INT DEFAULT 0,
  score INT DEFAULT 0,
  last_saved_state JSON,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS questions (
  id INT AUTO_INCREMENT PRIMARY KEY,
  level_index INT NOT NULL,
  question_text TEXT NOT NULL,
  choice_a VARCHAR(255),
  choice_b VARCHAR(255),
  choice_c VARCHAR(255),
  choice_d VARCHAR(255),
  correct_choice CHAR(1)
);

CREATE TABLE IF NOT EXISTS gameplay_logs (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  level_index INT NOT NULL,
  time_spent_ms INT,
  question_id INT,
  correct BOOLEAN,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
