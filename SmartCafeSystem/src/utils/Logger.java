package utils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Logger {
    
    private static final String LOG_FILE = "smartcafe.log";
    private static final SimpleDateFormat dateFormat = 
        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Log info message
     */
    public static void info(String message) {
        log("INFO", message);
        System.out.println("ℹ " + message);
    }
    
    /**
     * Log error message
     */
    public static void error(String message) {
        log("ERROR", message);
        System.err.println("✗ " + message);
    }
    
    /**
     * Log warning message
     */
    public static void warning(String message) {
        log("WARNING", message);
        System.out.println("⚠ " + message);
    }
    
    /**
     * Log success message
     */
    public static void success(String message) {
        log("SUCCESS", message);
        System.out.println("✓ " + message);
    }
    
    /**
     * Write log to file
     */
    private static synchronized void log(String level, String message) {
        try (FileWriter fw = new FileWriter(LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            
            String timestamp = dateFormat.format(new Date());
            out.println(String.format("[%s] [%s] %s", timestamp, level, message));
            
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
    
    /**
     * Clear log file
     */
    public static void clearLog() {
        try (FileWriter fw = new FileWriter(LOG_FILE, false)) {
            fw.write("");
            System.out.println("✓ Log file cleared");
        } catch (IOException e) {
            System.err.println("Failed to clear log file: " + e.getMessage());
        }
    }
}