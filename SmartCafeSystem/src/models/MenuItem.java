package models;

public class MenuItem {
    private int itemId;
    private String name;
    private double price;
    private String category;
    private String imagePath;
    private String description;
    private boolean available;
    
    public MenuItem() {}
    
    public MenuItem(int itemId, String name, double price, String category, 
                   String imagePath, String description) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.category = category;
        this.imagePath = imagePath;
        this.description = description;
        this.available = true;
    }
    
    // Getters and Setters
    public int getItemId() { return itemId; }
    public void setItemId(int itemId) { this.itemId = itemId; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }
}