package com.jdgame.db;

import com.jdgame.model.Question;
import com.jdgame.model.User;
import com.jdgame.util.Config;
import java.sql.*;
import java.util.*;

public class DBManager {
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(Config.JDBC_URL, Config.DB_USER, Config.DB_PASS);
    }

    public User loadUser(String username) {
        String sql = "SELECT id,username,email,high_level_reached,score,last_saved_state FROM users WHERE username = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User u = new User(rs.getInt("id"), rs.getString("username"), rs.getString("email"));
                    u.setHighLevelReached(rs.getInt("high_level_reached"));
                    u.setScore(rs.getInt("score"));
                    u.setLastSavedState(rs.getString("last_saved_state"));
                    return u;
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public List<Question> fetchQuestionsForLevel(int levelIndex) {
        String sql = "SELECT id, question_text, choice_a, choice_b, choice_c, choice_d, correct_choice FROM questions WHERE level_index = ?";
        List<Question> list = new ArrayList<>();
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, levelIndex);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Question q = new Question(
                        rs.getInt("id"), levelIndex, rs.getString("question_text"),
                        rs.getString("choice_a"), rs.getString("choice_b"),
                        rs.getString("choice_c"), rs.getString("choice_d"),
                        rs.getString("correct_choice").charAt(0));
                    list.add(q);
                }
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public void saveProgress(int userId, int currentLevel, int score, String jsonState) {
        String sql = "UPDATE users SET high_level_reached = GREATEST(high_level_reached, ?), score = ?, last_saved_state = ? WHERE id = ?";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, currentLevel);
            ps.setInt(2, score);
            ps.setString(3, jsonState);
            ps.setInt(4, userId);
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }

    public void logAttempt(int userId, int levelIndex, int timeSpentMs, int questionId, boolean correct) {
        String sql = "INSERT INTO gameplay_logs (user_id, level_index, time_spent_ms, question_id, correct) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, levelIndex);
            ps.setInt(3, timeSpentMs);
            ps.setInt(4, questionId);
            ps.setBoolean(5, correct);
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
}
