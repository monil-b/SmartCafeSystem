package controllers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;
import models.MenuItem;

public class MenuController {
    
    /**
     * Get all menu items (ONLY AVAILABLE - for customers)
     */
    public static List<MenuItem> getAllMenuItems() {
        List<MenuItem> menuItems = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "SELECT * FROM menu_items WHERE available = TRUE ORDER BY category, name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                MenuItem item = new MenuItem();
                item.setItemId(rs.getInt("item_id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setCategory(rs.getString("category"));
                item.setImagePath(rs.getString("image_path"));
                item.setDescription(rs.getString("description"));
                item.setAvailable(rs.getBoolean("available"));
                
                menuItems.add(item);
            }
            
            rs.close();
            stmt.close();
            System.out.println("✓ Loaded " + menuItems.size() + " available menu items");
            
        } catch (SQLException e) {
            System.err.println("✗ Error loading menu items: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return menuItems;
    }
    
    /**
     * Get ALL menu items including unavailable ones (for owner management)
     * This allows owners to see and recover toggled items
     */
    public static List<MenuItem> getAllMenuItemsForManagement() {
        List<MenuItem> menuItems = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            // NO WHERE clause - gets ALL items (available AND unavailable)
            // Orders by: available items first, then by category, then by name
            String sql = "SELECT * FROM menu_items ORDER BY available DESC, category, name";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                MenuItem item = new MenuItem();
                item.setItemId(rs.getInt("item_id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setCategory(rs.getString("category"));
                item.setImagePath(rs.getString("image_path"));
                item.setDescription(rs.getString("description"));
                item.setAvailable(rs.getBoolean("available"));
                
                menuItems.add(item);
            }
            
            rs.close();
            stmt.close();
            
            // Count available and unavailable items
            long availableCount = menuItems.stream().filter(MenuItem::isAvailable).count();
            long unavailableCount = menuItems.size() - availableCount;
            
            System.out.println("✓ Loaded " + menuItems.size() + " menu items for management");
            System.out.println("  - Available: " + availableCount);
            System.out.println("  - Unavailable: " + unavailableCount);
            
        } catch (SQLException e) {
            System.err.println("✗ Error loading menu items: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return menuItems;
    }
    
    /**
     * Get menu items by category (ONLY AVAILABLE - for customers)
     */
    public static List<MenuItem> getMenuItemsByCategory(String category) {
        List<MenuItem> menuItems = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "SELECT * FROM menu_items WHERE category = ? AND available = TRUE ORDER BY name";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, category);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                MenuItem item = new MenuItem();
                item.setItemId(rs.getInt("item_id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setCategory(rs.getString("category"));
                item.setImagePath(rs.getString("image_path"));
                item.setDescription(rs.getString("description"));
                item.setAvailable(rs.getBoolean("available"));
                
                menuItems.add(item);
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("✗ Error loading menu items: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return menuItems;
    }
    
    /**
     * Get menu item by ID
     */
    public static MenuItem getMenuItemById(int itemId) {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "SELECT * FROM menu_items WHERE item_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, itemId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                MenuItem item = new MenuItem();
                item.setItemId(rs.getInt("item_id"));
                item.setName(rs.getString("name"));
                item.setPrice(rs.getDouble("price"));
                item.setCategory(rs.getString("category"));
                item.setImagePath(rs.getString("image_path"));
                item.setDescription(rs.getString("description"));
                item.setAvailable(rs.getBoolean("available"));
                
                rs.close();
                pstmt.close();
                return item;
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("✗ Error loading menu item: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return null;
    }
}