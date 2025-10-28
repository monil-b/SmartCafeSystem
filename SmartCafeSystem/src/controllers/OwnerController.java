package controllers;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import database.DatabaseConnection;
import models.*;

public class OwnerController {
    
    /**
     * Owner login
     */
    public static Owner loginOwner(String username, String password) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getSimpleConnection();
            
            String sql = "SELECT * FROM owners WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Owner owner = new Owner();
                owner.setOwnerId(rs.getInt("owner_id"));
                owner.setUsername(rs.getString("username"));
                owner.setFullName(rs.getString("full_name"));
                owner.setEmail(rs.getString("email"));
                owner.setPhone(rs.getString("phone"));
                owner.setCreatedAt(rs.getTimestamp("created_at"));
                
                System.out.println("✓ Owner login successful: " + username);
                rs.close();
                pstmt.close();
                return owner;
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("✗ Owner login failed: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return null;
    }
    
    /**
     * Get all orders (for owner dashboard)
     */
    public static List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "SELECT o.*, u.username FROM orders o " +
                        "JOIN users u ON o.user_id = u.user_id " +
                        "ORDER BY o.order_date DESC";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setStatus(rs.getString("status"));
                order.setReadyTime(rs.getTimestamp("ready_time"));
                order.setUsername(rs.getString("username"));
                
                orders.add(order);
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting all orders: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return orders;
    }
    
    /**
     * Get orders by status
     */
    public static List<Order> getOrdersByStatus(String status) {
        List<Order> orders = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "SELECT o.*, u.username FROM orders o " +
                        "JOIN users u ON o.user_id = u.user_id " +
                        "WHERE o.status = ? ORDER BY o.order_date ASC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setStatus(rs.getString("status"));
                order.setReadyTime(rs.getTimestamp("ready_time"));
                order.setUsername(rs.getString("username"));
                
                orders.add(order);
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting orders by status: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return orders;
    }
    
    /**
     * Get today's analytics
     */
    public static Map<String, Object> getTodayAnalytics() {
        Map<String, Object> analytics = new HashMap<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            
            // Total orders today
            String ordersSql = "SELECT COUNT(*) as count FROM orders WHERE DATE(order_date) = CURDATE()";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(ordersSql);
            if (rs.next()) {
                analytics.put("totalOrders", rs.getInt("count"));
            }
            rs.close();
            
            // Total revenue today
            String revenueSql = "SELECT SUM(total_amount) as revenue FROM orders WHERE DATE(order_date) = CURDATE()";
            rs = stmt.executeQuery(revenueSql);
            if (rs.next()) {
                analytics.put("totalRevenue", rs.getDouble("revenue"));
            }
            rs.close();
            
            // Pending orders
            String pendingSql = "SELECT COUNT(*) as count FROM orders WHERE status = 'PENDING'";
            rs = stmt.executeQuery(pendingSql);
            if (rs.next()) {
                analytics.put("pendingOrders", rs.getInt("count"));
            }
            rs.close();
            
            // Preparing orders
            String preparingSql = "SELECT COUNT(*) as count FROM orders WHERE status = 'PREPARING'";
            rs = stmt.executeQuery(preparingSql);
            if (rs.next()) {
                analytics.put("preparingOrders", rs.getInt("count"));
            }
            rs.close();
            
            // Ready orders
            String readySql = "SELECT COUNT(*) as count FROM orders WHERE status = 'READY'";
            rs = stmt.executeQuery(readySql);
            if (rs.next()) {
                analytics.put("readyOrders", rs.getInt("count"));
            }
            rs.close();
            
            stmt.close();
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting analytics: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return analytics;
    }
    
    /**
     * Add new menu item
     */
    public static boolean addMenuItem(String name, double price, String category, 
                                     String imagePath, String description, int prepTime) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "INSERT INTO menu_items (name, price, category, image_path, description, preparation_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setString(3, category);
            pstmt.setString(4, imagePath);
            pstmt.setString(5, description);
            pstmt.setInt(6, prepTime);
            
            int rows = pstmt.executeUpdate();
            pstmt.close();
            
            if (rows > 0) {
                System.out.println("✓ Menu item added: " + name);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error adding menu item: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }
    
    /**
     * Update menu item
     */
    public static boolean updateMenuItem(int itemId, String name, double price, 
                                        String category, String description, int prepTime) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "UPDATE menu_items SET name = ?, price = ?, category = ?, " +
                        "description = ?, preparation_time = ? WHERE item_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setDouble(2, price);
            pstmt.setString(3, category);
            pstmt.setString(4, description);
            pstmt.setInt(5, prepTime);
            pstmt.setInt(6, itemId);
            
            int rows = pstmt.executeUpdate();
            pstmt.close();
            
            if (rows > 0) {
                System.out.println("✓ Menu item updated: " + name);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error updating menu item: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }
    
    /**
     * Delete menu item
     */
    public static boolean deleteMenuItem(int itemId) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "DELETE FROM menu_items WHERE item_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemId);
            
            int rows = pstmt.executeUpdate();
            pstmt.close();
            
            if (rows > 0) {
                System.out.println("✓ Menu item deleted");
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error deleting menu item: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }
    
    /**
     * Toggle menu item availability
     */
    public static boolean toggleItemAvailability(int itemId, boolean available) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "UPDATE menu_items SET available = ? WHERE item_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBoolean(1, available);
            pstmt.setInt(2, itemId);
            
            int rows = pstmt.executeUpdate();
            pstmt.close();
            
            return rows > 0;
            
        } catch (SQLException e) {
            System.err.println("✗ Error toggling availability: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        return false;
    }
}