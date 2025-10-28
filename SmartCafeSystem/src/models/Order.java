package models;

import java.sql.Timestamp;

public class Order {
    private int orderId;
    private int userId;
    private Timestamp orderDate;
    private double totalAmount;
    private String paymentMethod;
    private String status;
    private Timestamp readyTime;
    private String username;
    
    public Order() {}
    
    public Order(int userId, double totalAmount, String paymentMethod) {
        this.userId = userId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.status = "PENDING";
    }
    
    // Getters and Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }
    
    public double getTotalAmount() { return totalAmount; }
    public void setTotalAmount(double totalAmount) { this.totalAmount = totalAmount; }
    
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public Timestamp getReadyTime() { return readyTime; }
    public void setReadyTime(Timestamp readyTime) { this.readyTime = readyTime; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}