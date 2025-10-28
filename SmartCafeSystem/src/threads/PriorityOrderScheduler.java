package threads;

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import database.DatabaseConnection;

/**
 * OS CONCEPT: Priority Queue Scheduling
 * Orders are prioritized based on waiting time and preparation complexity
 */
public class PriorityOrderScheduler {
    
    // Priority queue with custom comparator
    private static PriorityQueue<OrderTask> orderPriorityQueue = new PriorityQueue<>(
        new OrderPriorityComparator()
    );
    
    // Thread pool for processing
    private static ExecutorService schedulerExecutor = Executors.newFixedThreadPool(3);
    
    // Active flag
    private static volatile boolean isRunning = false;
    
    /**
     * Start the scheduler
     */
    public static void startScheduler() {
        if (!isRunning) {
            isRunning = true;
            schedulerExecutor.submit(new SchedulerTask());
            System.out.println("✓ Priority Order Scheduler started");
        }
    }
    
    /**
     * Stop the scheduler
     */
    public static void stopScheduler() {
        isRunning = false;
        schedulerExecutor.shutdown();
        System.out.println("✓ Priority Order Scheduler stopped");
    }
    
    /**
     * Add order to priority queue
     */
    public static synchronized void addOrder(int orderId, int prepTime, int priority) {
        OrderTask task = new OrderTask(orderId, prepTime, priority);
        orderPriorityQueue.offer(task);
        System.out.println("→ Order #" + orderId + " added to priority queue (Priority: " + priority + ")");
    }
    
    /**
     * Get next order from priority queue
     */
    public static synchronized OrderTask getNextOrder() {
        return orderPriorityQueue.poll();
    }
    
    /**
     * Get queue size
     */
    public static int getQueueSize() {
        return orderPriorityQueue.size();
    }
    
    /**
     * Order Task class
     */
    static class OrderTask {
        int orderId;
        int prepTime;
        int priority;
        long queuedTime;
        
        public OrderTask(int orderId, int prepTime, int priority) {
            this.orderId = orderId;
            this.prepTime = prepTime;
            this.priority = priority;
            this.queuedTime = System.currentTimeMillis();
        }
        
        public int calculateDynamicPriority() {
            // Higher priority for orders waiting longer
            long waitingTime = System.currentTimeMillis() - queuedTime;
            int waitingMinutes = (int)(waitingTime / 60000);
            return priority + waitingMinutes;
        }
    }
    
    /**
     * Comparator for priority queue
     * OS CONCEPT: Priority-based scheduling
     */
    static class OrderPriorityComparator implements Comparator<OrderTask> {
        @Override
        public int compare(OrderTask o1, OrderTask o2) {
            // Higher priority value = higher priority in queue
            return Integer.compare(o2.calculateDynamicPriority(), o1.calculateDynamicPriority());
        }
    }
    
    /**
     * Scheduler task that processes queue
     */
    static class SchedulerTask implements Runnable {
        @Override
        public void run() {
            System.out.println("⚙ Scheduler task started");
            
            while (isRunning) {
                try {
                    OrderTask task = getNextOrder();
                    
                    if (task != null) {
                        System.out.println("⚡ Processing Order #" + task.orderId + 
                                         " (Priority: " + task.calculateDynamicPriority() + ")");
                        
                        // Process the order using OrderProcessor
                        OrderProcessor.submitOrder(task.orderId, "Owner");
                    }
                    
                    Thread.sleep(2000); // Check every 2 seconds
                    
                } catch (InterruptedException e) {
                    System.err.println("✗ Scheduler interrupted: " + e.getMessage());
                    break;
                }
            }
            
            System.out.println("⚙ Scheduler task stopped");
        }
    }
    
    /**
     * Calculate order priority based on various factors
     */
    public static int calculateOrderPriority(int orderId) {
        Connection conn = null;
        int priority = 0;
        
        try {
            conn = DatabaseConnection.getSimpleConnection();
            
            // Get order details
            String sql = "SELECT od.*, m.preparation_time FROM order_details od " +
                        "JOIN menu_items m ON od.item_id = m.item_id " +
                        "WHERE od.order_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            int totalItems = 0;
            int totalPrepTime = 0;
            
            while (rs.next()) {
                int quantity = rs.getInt("quantity");
                int prepTime = rs.getInt("preparation_time");
                totalItems += quantity;
                totalPrepTime += (prepTime * quantity);
            }
            
            rs.close();
            pstmt.close();
            
            // Priority calculation:
            // - Fewer items = higher priority
            // - Less prep time = higher priority
            priority = Math.max(0, 100 - totalItems - (totalPrepTime / 60));
            
        } catch (SQLException e) {
            System.err.println("✗ Error calculating priority: " + e.getMessage());
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException e) {}
        }
        
        return priority;
    }
}