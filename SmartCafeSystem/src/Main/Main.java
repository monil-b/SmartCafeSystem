import javax.swing.*;
import database.DatabaseConnection;
import views.LoginFrame;

public class Main {
    
    public static void main(String[] args) {
        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Test database connection
        System.out.println("╔═══════════════════════════════════════╗");
        System.out.println("║     SmartCafe Management System      ║");
        System.out.println("║        Desktop Application           ║");
        System.out.println("╚═══════════════════════════════════════╝");
        System.out.println();
        
        if (DatabaseConnection.testConnection()) {
            System.out.println("✓ System ready!");
            System.out.println();
            
            // Launch application
            SwingUtilities.invokeLater(() -> {
                new LoginFrame().setVisible(true);
            });
        } else {
            System.err.println("✗ Failed to connect to database!");
            System.err.println("Please check your database configuration in DatabaseConnection.java");
            
            JOptionPane.showMessageDialog(null,
                "Failed to connect to database!\n" +
                "Please check:\n" +
                "1. MySQL is running\n" +
                "2. Database 'smartcafe' exists\n" +
                "3. Credentials in DatabaseConnection.java are correct",
                "Database Connection Error",
                JOptionPane.ERROR_MESSAGE);
            
            System.exit(1);
        }
    }
}