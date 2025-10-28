package controllers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import database.DatabaseConnection;
import models.*;
import threads.OrderProcessor;

public class OrderController {
    
    /**
     * Create new order with order details
     * OS Concept: Transaction management (ACID properties)
     */
    public static int createOrder(int userId, List<CartItem> cartItems, String paymentMethod) {
        Connection conn = null;
        int orderId = -1;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            
            // Start transaction (OS Concept: Atomicity)
            conn.setAutoCommit(false);
            
            // Calculate total amount
            double totalAmount = 0;
            for (CartItem item : cartItems) {
                totalAmount += item.getSubtotal();
            }
            
            // Insert order
            String orderSql = "INSERT INTO orders (user_id, total_amount, payment_method, status) VALUES (?, ?, ?, ?)";
            PreparedStatement orderStmt = conn.prepareStatement(orderSql, Statement.RETURN_GENERATED_KEYS);
            orderStmt.setInt(1, userId);
            orderStmt.setDouble(2, totalAmount);
            orderStmt.setString(3, paymentMethod);
            orderStmt.setString(4, "PENDING");
            
            int rowsInserted = orderStmt.executeUpdate();
            
            if (rowsInserted > 0) {
                // Get generated order ID
                ResultSet rs = orderStmt.getGeneratedKeys();
                if (rs.next()) {
                    orderId = rs.getInt(1);
                }
                rs.close();
            }
            orderStmt.close();
            
            // Insert order details
            String detailSql = "INSERT INTO order_details (order_id, item_id, quantity, subtotal) VALUES (?, ?, ?, ?)";
            PreparedStatement detailStmt = conn.prepareStatement(detailSql);
            
            for (CartItem item : cartItems) {
                detailStmt.setInt(1, orderId);
                detailStmt.setInt(2, item.getMenuItem().getItemId());
                detailStmt.setInt(3, item.getQuantity());
                detailStmt.setDouble(4, item.getSubtotal());
                detailStmt.addBatch();
            }
            
            detailStmt.executeBatch();
            detailStmt.close();
            
            // Commit transaction (OS Concept: Atomicity - all or nothing)
            conn.commit();
            
            System.out.println("✓ Order #" + orderId + " created successfully!");
            System.out.println("  Total Amount: ₹" + totalAmount);
            System.out.println("  Payment Method: " + paymentMethod);
            System.out.println("  Items: " + cartItems.size());
            
        } catch (SQLException e) {
            // Rollback on error (OS Concept: Atomicity)
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            System.err.println("✗ Error creating order: " + e.getMessage());
            e.printStackTrace();
            orderId = -1;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        return orderId;
    }
    
    /**
     * Get order by ID
     */
    public static Order getOrderById(int orderId) {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "SELECT o.*, u.username FROM orders o " +
                        "JOIN users u ON o.user_id = u.user_id " +
                        "WHERE o.order_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Order order = new Order();
                order.setOrderId(rs.getInt("order_id"));
                order.setUserId(rs.getInt("user_id"));
                order.setOrderDate(rs.getTimestamp("order_date"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setPaymentMethod(rs.getString("payment_method"));
                order.setStatus(rs.getString("status"));
                order.setReadyTime(rs.getTimestamp("ready_time"));
                order.setUsername(rs.getString("username"));
                
                rs.close();
                pstmt.close();
                return order;
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting order: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return null;
    }
    
    /**
     * Get order details (items)
     */
    public static List<OrderDetail> getOrderDetails(int orderId) {
        List<OrderDetail> details = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "SELECT od.*, m.name, m.price FROM order_details od " +
                        "JOIN menu_items m ON od.item_id = m.item_id " +
                        "WHERE od.order_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderDetail detail = new OrderDetail();
                detail.setDetailId(rs.getInt("detail_id"));
                detail.setOrderId(rs.getInt("order_id"));
                detail.setItemId(rs.getInt("item_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setSubtotal(rs.getDouble("subtotal"));
                detail.setItemName(rs.getString("name"));
                detail.setItemPrice(rs.getDouble("price"));
                
                details.add(detail);
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("✗ Error getting order details: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return details;
    }
    
    /**
     * Get user's order history
     */
    public static List<Order> getUserOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "SELECT o.*, u.username FROM orders o " +
                        "JOIN users u ON o.user_id = u.user_id " +
                        "WHERE o.user_id = ? ORDER BY o.order_date DESC";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
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
            System.err.println("✗ Error getting user orders: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return orders;
    }
    
    /**
     * Process order with timer (OS Concept: Thread processing)
     */
    public static void processOrder(int orderId, String username) {
        // Submit to OrderProcessor thread pool
        OrderProcessor.submitOrder(orderId, username);
    }
    
    /**
     * Update order status
     */
    public static boolean updateOrderStatus(int orderId, String status) {
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, status);
            pstmt.setInt(2, orderId);
            
            int rowsUpdated = pstmt.executeUpdate();
            pstmt.close();
            
            if (rowsUpdated > 0) {
                System.out.println("✓ Order #" + orderId + " status updated to: " + status);
                return true;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error updating order status: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return false;
    }
}