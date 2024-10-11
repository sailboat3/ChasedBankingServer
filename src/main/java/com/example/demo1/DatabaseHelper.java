package com.example.demo1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseHelper {
    private static final String DB_URL = "jdbc:sqlite:users.db";

    public static Connection connect() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL);
            System.out.println("SQL is connected");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    public static void createUsersTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS users (
                 username TEXT PRIMARY KEY,
                 password TEXT NOT NULL,
                 balance REAL NOT NULL
                );""";

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            System.out.println(e.getMessage());

        }
    }

    public static void addUser(User User) {
        String sql = "INSERT INTO users(username, password, balance) VALUES(?, ?, ?)";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, User.getUsername());
            pstmt.setString(2, User.getPassword());
            pstmt.setDouble(3, User.getBalance());
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    public static User getUser(String username) {
        String sql = "SELECT username, password, balance FROM users WHERE username = ?";
        User user = null;

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                user = new User(rs.getString("username"), rs.getString("password"), rs.getDouble("balance"));
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return user;
    }

    public static void updateBalance(String username, double newBalance) {
        String sql = "UPDATE users SET balance = ? WHERE username = ?";

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, username);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}