import java.sql.*;

public class DBScript {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/?useSSL=false&allowPublicKeyRetrieval=true";
        String user = "root";
        String pass = "1525";

        try (Connection con = DriverManager.getConnection(url, user, pass);
             Statement stmt = con.createStatement()) {
            
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS leveldevil");
            stmt.executeUpdate("USE leveldevil");
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS players (" +
                               "username VARCHAR(50) PRIMARY KEY, " +
                               "password VARCHAR(50), " +
                               "gender VARCHAR(20), " +
                               "level INT DEFAULT 0, " +
                               "deaths INT DEFAULT 0)");
            System.out.println("Database 'leveldevil' and table 'players' are ready!");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
