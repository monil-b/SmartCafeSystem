package models;

public class OrderDetail {
    private int detailId;
    private int orderId;
    private int itemId;
    private int quantity;
    private double subtotal;
    private String itemName;
    private double itemPrice;
    
    public OrderDetail() {}
    
    public OrderDetail(int orderId, int itemId, int quantity, double subtotal) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = quantity;
        this.subtotal = subtotal;
    }
    
    // Getters and Setters
    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }
    
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    
    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }
    
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    
    public double getItemPrice() { return itemPrice; }
    public void setItemPrice(double itemPrice) { this.itemPrice = itemPrice; }
}