package controllers;

import java.sql.*;
import database.DatabaseConnection;
import models.User;

public class AuthController {
    
    /**
     * Register new user
     */
    public static boolean registerUser(String username, String password, String email, String phone) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getSimpleConnection();
            
            // Check if username already exists
            String checkSql = "SELECT username FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, username);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                System.out.println("✗ Username already exists!");
                rs.close();
                checkStmt.close();
                return false;
            }
            rs.close();
            checkStmt.close();
            
            // Insert new user
            String insertSql = "INSERT INTO users (username, password, email, phone) VALUES (?, ?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertSql);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password); // In real app, hash the password!
            insertStmt.setString(3, email);
            insertStmt.setString(4, phone);
            
            int rowsInserted = insertStmt.executeUpdate();
            insertStmt.close();
            
            if (rowsInserted > 0) {
                System.out.println("✓ User registered successfully: " + username);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Registration failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }
    
    /**
     * Login user
     */
    public static User loginUser(String username, String password) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getSimpleConnection();
            
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                // User found - create User object
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setRegistrationDate(rs.getTimestamp("registration_date"));
                
                System.out.println("✓ Login successful: " + username);
                rs.close();
                pstmt.close();
                return user;
            } else {
                System.out.println("✗ Invalid username or password!");
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("✗ Login failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return null;
    }
    
    /**
     * Check if username exists
     */
    public static boolean usernameExists(String username) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "SELECT username FROM users WHERE username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            
            boolean exists = rs.next();
            rs.close();
            pstmt.close();
            return exists;
            
        } catch (SQLException e) {
            System.err.println("✗ Error checking username: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }
}