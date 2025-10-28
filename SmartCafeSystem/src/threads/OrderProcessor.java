package threads;

import java.sql.*;
import java.util.concurrent.*;
import javax.swing.JOptionPane;
import database.DatabaseConnection;

/**
 * OS CONCEPTS DEMONSTRATED:
 * 1. Thread Pool (ExecutorService) - Multiple orders processed concurrently
 * 2. Semaphore - Order queue management (max 10 concurrent orders)
 * 3. Timer Thread - 5-minute countdown per order
 * 4. Synchronization - Thread-safe order status updates
 * 5. Producer-Consumer Pattern - Order submission and processing
 */
public class OrderProcessor {
    
    // Thread pool for processing multiple orders (OS Concept: Multithreading)
    private static ExecutorService orderExecutor = Executors.newFixedThreadPool(5);
    
    // Semaphore to limit concurrent orders (OS Concept: Resource Management)
    private static Semaphore orderSemaphore = new Semaphore(10);
    
    // Order queue (OS Concept: Scheduling)
    private static BlockingQueue<OrderTask> orderQueue = new LinkedBlockingQueue<>();
    
    // Active orders map (thread-safe)
    private static ConcurrentHashMap<Integer, OrderTask> activeOrders = new ConcurrentHashMap<>();
    
    /**
     * Submit order for processing
     */
    public static void submitOrder(int orderId, String username) {
        OrderTask task = new OrderTask(orderId, username);
        activeOrders.put(orderId, task);
        orderExecutor.submit(task);
        
        // *** Assign order to kitchen station ***
        KitchenManager.assignOrderToStation(orderId);
        
        System.out.println("✓ Order #" + orderId + " submitted to processing queue");
    }
    
    /**
     * Get order status
     */
    public static String getOrderStatus(int orderId) {
        OrderTask task = activeOrders.get(orderId);
        if (task != null) {
            return task.getStatus();
        }
        return "COMPLETED";
    }
    
    /**
     * Cancel order
     */
    public static void cancelOrder(int orderId) {
        OrderTask task = activeOrders.get(orderId);
        if (task != null) {
            task.cancel();
        }
    }
    
    /**
     * Shutdown processor
     */
    public static void shutdown() {
        orderExecutor.shutdown();
        System.out.println("✓ Order processor shutdown");
    }
    
    /**
     * OrderTask - Runnable that processes each order
     * OS Concept: Thread execution with timer
     */
    static class OrderTask implements Runnable {
        private int orderId;
        private String username;
        private volatile String status;
        private volatile boolean cancelled;
        private int remainingSeconds;
        private static final int PREPARATION_TIME = 300; // 5 minutes = 300 seconds
        
        public OrderTask(int orderId, String username) {
            this.orderId = orderId;
            this.username = username;
            this.status = "PREPARING";
            this.cancelled = false;
            this.remainingSeconds = PREPARATION_TIME;
        }
        
        @Override
        public void run() {
            try {
                // Acquire semaphore (OS Concept: Resource allocation)
                orderSemaphore.acquire();
                System.out.println("→ Processing Order #" + orderId + " | Active orders: " + 
                                 (10 - orderSemaphore.availablePermits()));
                
                // Update order status in database
                updateOrderStatus("PREPARING");
                
                // Start 5-minute countdown timer (OS Concept: Timer thread)
                startCountdownTimer();
                
                // Simulate order preparation with countdown
                while (remainingSeconds > 0 && !cancelled) {
                    Thread.sleep(1000); // 1 second
                    remainingSeconds--;
                    
                    // Update status every 30 seconds
                    if (remainingSeconds % 30 == 0 && remainingSeconds > 0) {
                        System.out.println("⏱ Order #" + orderId + " | Remaining: " + 
                                         formatTime(remainingSeconds));
                    }
                }
                
                if (!cancelled) {
                    // Order is ready!
                    status = "READY";
                    updateOrderStatus("READY");
                    
                    // Set ready time
                    updateReadyTime();
                    
                    // Notify customer (OS Concept: Inter-process communication)
                    notifyCustomer();
                    
                    System.out.println("✓ Order #" + orderId + " is READY!");
                } else {
                    status = "CANCELLED";
                    updateOrderStatus("CANCELLED");
                    System.out.println("✗ Order #" + orderId + " was cancelled");
                }
                
            } catch (InterruptedException e) {
                status = "CANCELLED";
                System.err.println("✗ Order #" + orderId + " interrupted: " + e.getMessage());
            } finally {
                // Release semaphore (OS Concept: Resource deallocation)
                orderSemaphore.release();
                activeOrders.remove(orderId);
                System.out.println("← Order #" + orderId + " completed | Available slots: " + 
                                 orderSemaphore.availablePermits());
            }
        }
        
        /**
         * Start countdown timer thread
         */
        private void startCountdownTimer() {
            Thread timerThread = new Thread(() -> {
                System.out.println("⏱ Timer started for Order #" + orderId + " (5 minutes)");
            });
            timerThread.start();
        }
        
        /**
         * Update order status in database (synchronized)
         */
        private synchronized void updateOrderStatus(String newStatus) {
            Connection conn = null;
            try {
                conn = DatabaseConnection.getSimpleConnection();
                String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setString(1, newStatus);
                pstmt.setInt(2, orderId);
                pstmt.executeUpdate();
                pstmt.close();
            } catch (SQLException e) {
                System.err.println("✗ Failed to update order status: " + e.getMessage());
            } finally {
                try { if (conn != null) conn.close(); } catch (SQLException e) {}
            }
        }
        
        /**
         * Update ready time in database
         */
        private void updateReadyTime() {
            Connection conn = null;
            try {
                conn = DatabaseConnection.getSimpleConnection();
                String sql = "UPDATE orders SET ready_time = NOW() WHERE order_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, orderId);
                pstmt.executeUpdate();
                pstmt.close();
            } catch (SQLException e) {
                System.err.println("✗ Failed to update ready time: " + e.getMessage());
            } finally {
                try { if (conn != null) conn.close(); } catch (SQLException e) {}
            }
        }
        
        /**
         * Notify customer that order is ready
         */
        private void notifyCustomer() {
            // Show notification dialog
            javax.swing.SwingUtilities.invokeLater(() -> {
                JOptionPane.showMessageDialog(null,
                    "🎉 Order #" + orderId + " is ready!\n\n" +
                    "Dear " + username + ",\n" +
                    "Your order is ready for pickup.\n" +
                    "Thank you for your patience!",
                    "Order Ready - SmartCafe",
                    JOptionPane.INFORMATION_MESSAGE);
            });
            
            // You can add sound notification here
            java.awt.Toolkit.getDefaultToolkit().beep();
        }
        
        /**
         * Format seconds to MM:SS
         */
        private String formatTime(int seconds) {
            int mins = seconds / 60;
            int secs = seconds % 60;
            return String.format("%02d:%02d", mins, secs);
        }
        
        /**
         * Cancel this order
         */
        public void cancel() {
            cancelled = true;
        }
        
        /**
         * Get current status
         */
        public String getStatus() {
            if (cancelled) return "CANCELLED";
            if (remainingSeconds > 0) return "PREPARING (" + formatTime(remainingSeconds) + ")";
            return "READY";
        }
        
        /**
         * Get remaining time
         */
        public int getRemainingSeconds() {
            return remainingSeconds;
        }
    }
}
