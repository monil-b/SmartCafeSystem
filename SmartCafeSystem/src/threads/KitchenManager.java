package threads;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import database.DatabaseConnection;

/**
 * OS CONCEPT: Parallel Processing with Multiple Kitchen Stations
 * Simulates 3 parallel kitchen stations processing orders concurrently
 */
public class KitchenManager {
    
    private static final int NUM_STATIONS = 3;
    private static List<KitchenStation> stations = new ArrayList<>();
    private static ExecutorService kitchenExecutor = null;
    private static volatile boolean isRunning = false;
    private static volatile boolean isInitialized = false;
    private static BlockingQueue<Integer> globalQueue = new LinkedBlockingQueue<>();
    
    /**
     * Initialize kitchen stations
     */
    public static synchronized void initialize() {
        if (isInitialized) {
            System.out.println("⚠ Kitchen Manager already initialized");
            return;
        }
        
        stations.clear();
        globalQueue.clear();
        kitchenExecutor = Executors.newFixedThreadPool(NUM_STATIONS);
        
        for (int i = 1; i <= NUM_STATIONS; i++) {
            KitchenStation station = new KitchenStation(i, "Station " + i, globalQueue);
            stations.add(station);
            kitchenExecutor.submit(station);
        }
        
        isRunning = true;
        isInitialized = true;
        System.out.println("✓ Kitchen Manager initialized with " + NUM_STATIONS + " stations");
    }
    
    /**
     * Assign order to available station
     */
    public static synchronized boolean assignOrderToStation(int orderId) {
        // Auto-initialize if not already done
        if (!isInitialized) {
            System.out.println("⚠ Kitchen Manager not initialized, initializing now...");
            initialize();
        }
        
        try {
            globalQueue.put(orderId);
            System.out.println("📋 Order #" + orderId + " added to kitchen queue");
            return true;
        } catch (InterruptedException e) {
            System.err.println("✗ Error queuing order: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get queue size
     */
    public static int getQueueSize() {
        return globalQueue.size();
    }
    
    /**
     * Get station status with detailed information
     */
    public static List<Map<String, Object>> getStationStatus() {
        List<Map<String, Object>> statusList = new ArrayList<>();
        
        // Auto-initialize if not already done
        if (!isInitialized) {
            initialize();
        }
        
        for (KitchenStation station : stations) {
            Map<String, Object> status = new HashMap<>();
            status.put("stationId", station.getId());
            status.put("name", station.getName());
            status.put("busy", station.isBusy());
            status.put("currentOrder", station.getCurrentOrderId());
            status.put("itemDetails", station.getItemDetails());
            status.put("timeRemaining", station.getTimeRemaining());
            statusList.add(status);
        }
        
        return statusList;
    }
    
    /**
     * Shutdown kitchen
     */
    public static synchronized void shutdown() {
        if (!isInitialized) {
            return;
        }
        
        isRunning = false;
        for (KitchenStation station : stations) {
            station.stop();
        }
        
        if (kitchenExecutor != null) {
            kitchenExecutor.shutdown();
            try {
                if (!kitchenExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    kitchenExecutor.shutdownNow();
                }
            } catch (InterruptedException e) {
                kitchenExecutor.shutdownNow();
            }
        }
        
        globalQueue.clear();
        isInitialized = false;
        System.out.println("✓ Kitchen Manager shutdown");
    }
    
    /**
     * Kitchen Station class - represents one parallel processing unit
     */
    static class KitchenStation implements Runnable {
        private int id;
        private String name;
        private volatile boolean busy;
        private volatile int currentOrderId;
        private volatile boolean running;
        private volatile String itemDetails;
        private volatile long startTime;
        private volatile long totalTime;
        private BlockingQueue<Integer> orderQueue;
        
        public KitchenStation(int id, String name, BlockingQueue<Integer> sharedQueue) {
            this.id = id;
            this.name = name;
            this.busy = false;
            this.currentOrderId = -1;
            this.running = true;
            this.itemDetails = "";
            this.orderQueue = sharedQueue;
        }
        
        @Override
        public void run() {
            System.out.println("🔥 " + name + " started and ready");
            
            while (running) {
                try {
                    // Wait for order with timeout
                    Integer orderId = orderQueue.poll(1, TimeUnit.SECONDS);
                    
                    if (orderId != null) {
                        currentOrderId = orderId;
                        busy = true;
                        
                        // Get order details
                        itemDetails = getOrderItemDetails(orderId);
                        int prepTime = getOrderPrepTime(orderId);
                        totalTime = prepTime;
                        startTime = System.currentTimeMillis();
                        
                        updateStationInDatabase(orderId);
                        
                        System.out.println("👨‍🍳 " + name + " started processing Order #" + orderId);
                        System.out.println("   Items: " + itemDetails);
                        
                        // Process the order
                        processOrder(orderId, prepTime);
                        
                        System.out.println("✓ " + name + " completed Order #" + orderId);
                        
                        // Reset station
                        currentOrderId = -1;
                        busy = false;
                        itemDetails = "";
                        updateStationInDatabase(null);
                    }
                    
                } catch (InterruptedException e) {
                    if (running) {
                        System.err.println("✗ " + name + " interrupted");
                    }
                    break;
                }
            }
            
            System.out.println("🔥 " + name + " stopped");
        }
        
        private void processOrder(int orderId, int prepTime) {
            try {
                // Sleep for preparation time (in seconds)
                Thread.sleep(prepTime * 1000L);
                
                // Update order status to completed
                updateOrderStatus(orderId, "completed");
                
                System.out.println("✓ " + name + " completed Order #" + orderId);
                
            } catch (InterruptedException e) {
                System.err.println("✗ " + name + " order processing interrupted");
            }
        }
        
        private String getOrderItemDetails(int orderId) {
            Connection conn = null;
            StringBuilder details = new StringBuilder();
            
            try {
                conn = DatabaseConnection.getSimpleConnection();
                String sql = "SELECT m.name, od.quantity " +
                            "FROM order_details od " +
                            "JOIN menu_items m ON od.item_id = m.item_id " +
                            "WHERE od.order_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId);
                ResultSet rs = pstmt.executeQuery();
                
                boolean first = true;
                while (rs.next()) {
                    if (!first) details.append(", ");
                    
                    String itemName = rs.getString("name");
                    int qty = rs.getInt("quantity");
                    
                    // Add emoji based on item type
                    String emoji = getItemEmoji(itemName);
                    details.append(emoji).append(" ").append(qty).append("x ").append(itemName);
                    first = false;
                }
                
                rs.close();
                pstmt.close();
                
            } catch (SQLException e) {
                System.err.println("✗ Error getting item details: " + e.getMessage());
            } finally {
                try { if (conn != null) conn.close(); } catch (SQLException e) {}
            }
            
            return details.toString();
        }
        
        private String getItemEmoji(String itemName) {
            String lower = itemName.toLowerCase();
            if (lower.contains("espresso") || lower.contains("coffee")) return "☕";
            if (lower.contains("cappuccino") || lower.contains("latte")) return "☕";
            if (lower.contains("tea")) return "🍵";
            if (lower.contains("juice")) return "🥤";
            if (lower.contains("cake") || lower.contains("pastry")) return "🍰";
            if (lower.contains("croissant") || lower.contains("bagel")) return "🥐";
            if (lower.contains("sandwich")) return "🥪";
            if (lower.contains("cookie")) return "🍪";
            if (lower.contains("muffin")) return "🧁";
            if (lower.contains("smoothie")) return "🥤";
            return "🍽️";
        }
        
        private int getOrderPrepTime(int orderId) {
            Connection conn = null;
            int totalTime = 30; // Default 30 seconds for demo
            
            try {
                conn = DatabaseConnection.getSimpleConnection();
                String sql = "SELECT SUM(m.preparation_time * od.quantity) as total_time " +
                            "FROM order_details od " +
                            "JOIN menu_items m ON od.item_id = m.item_id " +
                            "WHERE od.order_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId);
                ResultSet rs = pstmt.executeQuery();
                
                if (rs.next()) {
                    int dbTime = rs.getInt("total_time");
                    if (dbTime > 0) {
                        totalTime = dbTime;
                    }
                }
                
                rs.close();
                pstmt.close();
                
            } catch (SQLException e) {
                System.err.println("✗ Error getting prep time: " + e.getMessage());
            } finally {
                try { if (conn != null) conn.close(); } catch (SQLException e) {}
            }
            
            return totalTime;
        }
        
        private void updateOrderStatus(int orderId, String status) {
            Connection conn = null;
            try {
                conn = DatabaseConnection.getSimpleConnection();
                String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, status);
                pstmt.setInt(2, orderId);
                pstmt.executeUpdate();
                pstmt.close();
                
                System.out.println("✓ Order #" + orderId + " status updated to: " + status);
                
            } catch (SQLException e) {
                System.err.println("✗ Error updating order status: " + e.getMessage());
            } finally {
                try { if (conn != null) conn.close(); } catch (SQLException e) {}
            }
        }
        
        private void updateStationInDatabase(Integer orderId) {
            Connection conn = null;
            try {
                conn = DatabaseConnection.getSimpleConnection();
                
                // First, ensure the station exists in the database
                String checkSql = "SELECT station_id FROM kitchen_stations WHERE station_id = ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkSql);
                checkStmt.setInt(1, id);
                ResultSet rs = checkStmt.executeQuery();
                
                if (!rs.next()) {
                    // Insert station if it doesn't exist
                    String insertSql = "INSERT INTO kitchen_stations (station_id, station_name, current_order_id) VALUES (?, ?, ?)";
                    PreparedStatement insertStmt = conn.prepareStatement(insertSql);
                    insertStmt.setInt(1, id);
                    insertStmt.setString(2, name);
                    if (orderId != null) {
                        insertStmt.setInt(3, orderId);
                    } else {
                        insertStmt.setNull(3, Types.INTEGER);
                    }
                    insertStmt.executeUpdate();
                    insertStmt.close();
                } else {
                    // Update existing station
                    String updateSql = "UPDATE kitchen_stations SET current_order_id = ?, station_name = ? WHERE station_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateSql);
                    
                    if (orderId != null) {
                        updateStmt.setInt(1, orderId);
                    } else {
                        updateStmt.setNull(1, Types.INTEGER);
                    }
                    updateStmt.setString(2, name);
                    updateStmt.setInt(3, id);
                    
                    updateStmt.executeUpdate();
                    updateStmt.close();
                }
                
                rs.close();
                checkStmt.close();
                
            } catch (SQLException e) {
                System.err.println("✗ Error updating station: " + e.getMessage());
                e.printStackTrace();
            } finally {
                try { if (conn != null) conn.close(); } catch (SQLException e) {}
            }
        }
        
        public void stop() {
            running = false;
        }
        
        public int getId() { return id; }
        public String getName() { return name; }
        public synchronized boolean isBusy() { return busy; }
        public synchronized int getCurrentOrderId() { return currentOrderId; }
        public synchronized String getItemDetails() { return itemDetails; }
        
        public synchronized long getTimeRemaining() {
            if (!busy || currentOrderId == -1) {
                return 0;
            }
            long elapsed = (System.currentTimeMillis() - startTime) / 1000;
            long remaining = totalTime - elapsed;
            return remaining > 0 ? remaining : 0;
        }
    }
}