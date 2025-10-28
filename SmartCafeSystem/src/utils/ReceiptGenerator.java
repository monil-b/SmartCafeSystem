package utils;

import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import database.DatabaseConnection;

public class ReceiptGenerator {
    
    private static final SimpleDateFormat dateFormat = 
        new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a");
    
    /**
     * Generate receipt text
     */
    public static String generateReceipt(int orderId) {
        StringBuilder receipt = new StringBuilder();
        Connection conn = null;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            
            // Get order details
            String orderSql = "SELECT o.*, u.username FROM orders o " +
                            "JOIN users u ON o.user_id = u.user_id " +
                            "WHERE o.order_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(orderSql);
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                String username = rs.getString("username");
                Timestamp orderDate = rs.getTimestamp("order_date");
                double totalAmount = rs.getDouble("total_amount");
                String paymentMethod = rs.getString("payment_method");
                String status = rs.getString("status");
                
                // Header
                receipt.append("═══════════════════════════════════════\n");
                receipt.append("           SMARTCAFE SYSTEM            \n");
                receipt.append("         Your Digital Cafe ☕          \n");
                receipt.append("═══════════════════════════════════════\n\n");
                
                // Order info
                receipt.append("RECEIPT\n");
                receipt.append("───────────────────────────────────────\n");
                receipt.append(String.format("Order ID      : #%04d\n", orderId));
                receipt.append(String.format("Customer      : %s\n", username));
                receipt.append(String.format("Date & Time   : %s\n", dateFormat.format(orderDate)));
                receipt.append(String.format("Status        : %s\n", status));
                receipt.append("───────────────────────────────────────\n\n");
                
                // Items
                receipt.append("ITEMS ORDERED\n");
                receipt.append("───────────────────────────────────────\n");
                receipt.append(String.format("%-20s %5s %8s %10s\n", 
                    "Item", "Qty", "Price", "Subtotal"));
                receipt.append("───────────────────────────────────────\n");
                
                // Get order items
                String itemsSql = "SELECT od.*, m.name, m.price FROM order_details od " +
                                "JOIN menu_items m ON od.item_id = m.item_id " +
                                "WHERE od.order_id = ?";
                PreparedStatement pstmt2 = conn.prepareStatement(itemsSql);
                pstmt2.setInt(1, orderId);
                ResultSet rs2 = pstmt2.executeQuery();
                
                double subtotalSum = 0;
                int itemCount = 0;
                
                while (rs2.next()) {
                    String itemName = rs2.getString("name");
                    int quantity = rs2.getInt("quantity");
                    double price = rs2.getDouble("price");
                    double subtotal = rs2.getDouble("subtotal");
                    subtotalSum += subtotal;
                    itemCount++;
                    
                    // Truncate long names
                    if (itemName.length() > 18) {
                        itemName = itemName.substring(0, 15) + "...";
                    }
                    
                    receipt.append(String.format("%-20s %5d ₹%7.2f ₹%9.2f\n",
                        itemName, quantity, price, subtotal));
                }
                rs2.close();
                pstmt2.close();
                
                receipt.append("───────────────────────────────────────\n");
                receipt.append(String.format("Total Items   : %d\n", itemCount));
                receipt.append(String.format("Subtotal      : ₹%.2f\n", subtotalSum));
                
                // Calculate taxes and charges
                double cgst = subtotalSum * 0.025; // 2.5% CGST
                double sgst = subtotalSum * 0.025; // 2.5% SGST
                double serviceCharge = subtotalSum * 0.05; // 5% Service Charge
                double grandTotal = subtotalSum + cgst + sgst + serviceCharge;
                
                receipt.append(String.format("CGST (2.5%%)   : ₹%.2f\n", cgst));
                receipt.append(String.format("SGST (2.5%%)   : ₹%.2f\n", sgst));
                receipt.append(String.format("Service (5%%)  : ₹%.2f\n", serviceCharge));
                receipt.append("───────────────────────────────────────\n");
                receipt.append(String.format("GRAND TOTAL   : ₹%.2f\n", grandTotal));
                receipt.append("═══════════════════════════════════════\n\n");
                
                // Payment info
                receipt.append("PAYMENT DETAILS\n");
                receipt.append("───────────────────────────────────────\n");
                receipt.append(String.format("Payment Method: %s\n", paymentMethod));
                receipt.append(String.format("Amount Paid   : ₹%.2f\n", grandTotal));
                receipt.append("Status        : PAID ✓\n");
                receipt.append("───────────────────────────────────────\n\n");
                
                // Footer
                receipt.append("═══════════════════════════════════════\n");
                receipt.append("      Thank you for your order!        \n");
                receipt.append("         Visit us again soon ❤️         \n");
                receipt.append("═══════════════════════════════════════\n");
                receipt.append("     Contact: smartcafe@email.com      \n");
                receipt.append("         Phone: +91 98765 43210        \n");
                receipt.append("═══════════════════════════════════════\n");
            }
            
            rs.close();
            pstmt.close();
            
        } catch (SQLException e) {
            System.err.println("✗ Error generating receipt: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return receipt.toString();
    }
    
    /**
     * Save receipt to file
     */
    public static boolean saveReceiptToFile(int orderId, String filePath) {
        try {
            String receipt = generateReceipt(orderId);
            FileWriter writer = new FileWriter(filePath);
            writer.write(receipt);
            writer.close();
            System.out.println("✓ Receipt saved to: " + filePath);
            return true;
        } catch (IOException e) {
            System.err.println("✗ Error saving receipt: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get receipt file name
     */
    public static String getReceiptFileName(int orderId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        return String.format("SmartCafe_Receipt_%04d_%s.txt", 
                           orderId, sdf.format(new Date()));
    }
}