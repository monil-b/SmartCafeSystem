package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Semaphore;

/**
 * OS Concept: Connection Pooling with Semaphore for resource management
 * Implements thread-safe database connection pool
 */
public class DatabaseConnection {
    
    // Database credentials - CHANGE PASSWORD HERE!
	private static final String URL = "jdbc:mysql://localhost:3306/smartcafe";
	private static final String USER = "root";
	private static final String PASSWORD = "Monil0!73#"; 
    
    // Connection pool configuration
    private static final int POOL_SIZE = 5;
    private static BlockingQueue<Connection> connectionPool;
    private static Semaphore semaphore;
    
    // Singleton instance
    private static DatabaseConnection instance;
    
    // Private constructor for singleton
    private DatabaseConnection() {
        initializePool();
    }
    
    /**
     * Get singleton instance
     */
    public static synchronized DatabaseConnection getInstance() {
        if (instance == null) {
            instance = new DatabaseConnection();
        }
        return instance;
    }
    
    /**
     * Initialize connection pool with semaphore (OS Concept)
     */
    private void initializePool() {
        connectionPool = new ArrayBlockingQueue<>(POOL_SIZE);
        semaphore = new Semaphore(POOL_SIZE);
        
        try {
            // Load MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create connections
            for (int i = 0; i < POOL_SIZE; i++) {
                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                connectionPool.offer(conn);
            }
            System.out.println("✓ Database connection pool initialized with " + POOL_SIZE + " connections");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("✗ Failed to initialize connection pool: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Get connection from pool (OS Concept: Semaphore)
     * Thread will wait if no connection available
     */
    public Connection getConnection() throws InterruptedException {
        semaphore.acquire(); // Wait for available connection
        Connection conn = connectionPool.take();
        System.out.println("→ Connection acquired. Available: " + semaphore.availablePermits());
        return conn;
    }
    
    /**
     * Return connection to pool (OS Concept: Resource release)
     */
    public void releaseConnection(Connection conn) {
        if (conn != null) {
            connectionPool.offer(conn);
            semaphore.release();
            System.out.println("← Connection released. Available: " + semaphore.availablePermits());
        }
    }
    
    /**
     * Get simple connection (for quick operations)
     */
    public static Connection getSimpleConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
    
    /**
     * Close all connections in pool
     */
    public void closePool() {
        for (Connection conn : connectionPool) {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        System.out.println("✓ Connection pool closed");
    }
    
    /**
     * Test database connection
     */
    public static boolean testConnection() {
        try (Connection conn = getSimpleConnection()) {
            System.out.println("✓ Database connection successful!");
            return true;
        } catch (SQLException e) {
            System.err.println("✗ Database connection failed: " + e.getMessage());
            return false;
        }
    }
}