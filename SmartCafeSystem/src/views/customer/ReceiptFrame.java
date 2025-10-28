package views.customer;

import javax.swing.*;
import java.awt.*;
import utils.ReceiptGenerator;
import models.User;

public class ReceiptFrame extends JFrame {

    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color SOFT_WHITE = new Color(255, 253, 250);
    
    private User currentUser;
    private int orderId;
    
    public ReceiptFrame(User user, int orderId) {
        this.currentUser = user;
        this.orderId = orderId;
        
        setTitle("SmartCafe - Receipt");
        setSize(700, 820);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CREAM);
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(null);
        
        // Header
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, DARK_BROWN, 
                                                     getWidth(), 0, COFFEE_BROWN);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setBounds(0, 0, 700, 110);
        header.setLayout(null);
        
        JLabel lblIcon = new JLabel("🧾");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 55));
        lblIcon.setBounds(40, 25, 65, 60);
        header.add(lblIcon);
        
        JLabel lblTitle = new JLabel("Order Receipt");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 38));
        lblTitle.setForeground(WHITE_SMOKE);
        lblTitle.setBounds(115, 24, 350, 42);
        header.add(lblTitle);
        
        JLabel lblOrderId = new JLabel("Order #" + String.format("%04d", orderId));
        lblOrderId.setFont(new Font("Arial", Font.BOLD, 19));
        lblOrderId.setForeground(new Color(255, 255, 255, 220));
        lblOrderId.setBounds(115, 68, 300, 26);
        header.add(lblOrderId);
        
        // Success badge 
        JPanel successBadge = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
            }
        };
        successBadge.setBackground(new Color(76, 175, 80));
        successBadge.setBounds(555, 32, 125, 45);
        successBadge.setLayout(null);
        successBadge.setBorder(BorderFactory.createEmptyBorder());
        successBadge.setOpaque(false);
        
        JLabel lblSuccess = new JLabel("✓ Paid");
        lblSuccess.setFont(new Font("Arial", Font.BOLD, 17));
        lblSuccess.setForeground(WHITE_SMOKE);
        lblSuccess.setBounds(0, 0, 125, 45);
        lblSuccess.setHorizontalAlignment(SwingConstants.CENTER);
        successBadge.add(lblSuccess);
        
        header.add(successBadge);
        add(header);
        
        // Decorative elements
        JLabel lblCoffeeIcon1 = new JLabel("☕");
        lblCoffeeIcon1.setFont(new Font("Arial", Font.PLAIN, 24));
        lblCoffeeIcon1.setBounds(50, 125, 30, 30);
        add(lblCoffeeIcon1);
        
        JLabel lblCoffeeIcon2 = new JLabel("☕");
        lblCoffeeIcon2.setFont(new Font("Arial", Font.PLAIN, 24));
        lblCoffeeIcon2.setBounds(620, 125, 30, 30);
        add(lblCoffeeIcon2);
        
        // Receipt container
        JPanel receiptContainer = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw shadow
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillRoundRect(4, 4, getWidth()-4, getHeight()-4, 20, 20);
                
                // Draw main background
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth()-4, getHeight()-4, 20, 20);
            }
        };
        receiptContainer.setBackground(SOFT_WHITE);
        receiptContainer.setBounds(40, 165, 620, 540);
        receiptContainer.setLayout(null);
        receiptContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        receiptContainer.setOpaque(false);
        
        // Decorative header line in receipt
        JPanel decorLine = new JPanel();
        decorLine.setBackground(COFFEE_BROWN);
        decorLine.setBounds(20, 0, 560, 3);
        receiptContainer.add(decorLine);
        
        // Receipt text area with custom styling
        JTextArea txtReceipt = new JTextArea();
        txtReceipt.setFont(new Font("Courier New", Font.PLAIN, 13));
        txtReceipt.setEditable(false);
        txtReceipt.setBackground(SOFT_WHITE);
        txtReceipt.setForeground(DARK_BROWN);
        txtReceipt.setLineWrap(false);
        txtReceipt.setMargin(new Insets(10, 10, 10, 10));
        
        // Generate receipt
        String receiptText = ReceiptGenerator.generateReceipt(orderId);
        txtReceipt.setText(receiptText);
        txtReceipt.setCaretPosition(0);
        
        JScrollPane scrollPane = new JScrollPane(txtReceipt);
        scrollPane.setBounds(0, 15, 580, 485);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        receiptContainer.add(scrollPane);
        
        add(receiptContainer);
        
        // Action buttons 
        JButton btnPrint = new JButton("🖨️ Print Receipt") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(COFFEE_BROWN);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
                super.paintComponent(g);
            }
        };
        btnPrint.setBounds(40, 725, 195, 55);
        btnPrint.setFont(new Font("Arial", Font.BOLD, 15));
        btnPrint.setBackground(SOFT_WHITE);
        btnPrint.setForeground(COFFEE_BROWN);
        btnPrint.setFocusPainted(false);
        btnPrint.setContentAreaFilled(false);
        btnPrint.setBorderPainted(false);
        btnPrint.setOpaque(false);
        btnPrint.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPrint.addActionListener(e -> printReceipt(txtReceipt));
        
        btnPrint.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnPrint.setBackground(CREAM);
                btnPrint.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnPrint.setBackground(SOFT_WHITE);
                btnPrint.repaint();
            }
        });
        
        add(btnPrint);
        
        JButton btnSave = new JButton("💾 Save as PDF") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(COFFEE_BROWN);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
                super.paintComponent(g);
            }
        };
        btnSave.setBounds(250, 725, 195, 55);
        btnSave.setFont(new Font("Arial", Font.BOLD, 15));
        btnSave.setBackground(SOFT_WHITE);
        btnSave.setForeground(COFFEE_BROWN);
        btnSave.setFocusPainted(false);
        btnSave.setContentAreaFilled(false);
        btnSave.setBorderPainted(false);
        btnSave.setOpaque(false);
        btnSave.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnSave.addActionListener(e -> saveReceipt());
        
        btnSave.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnSave.setBackground(CREAM);
                btnSave.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnSave.setBackground(SOFT_WHITE);
                btnSave.repaint();
            }
        });
        
        add(btnSave);
        
        JButton btnClose = new JButton("✓ Close") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        btnClose.setBounds(460, 725, 200, 55);
        btnClose.setFont(new Font("Arial", Font.BOLD, 17));
        btnClose.setBackground(COFFEE_BROWN);
        btnClose.setForeground(WHITE_SMOKE);
        btnClose.setFocusPainted(false);
        btnClose.setContentAreaFilled(false);
        btnClose.setBorderPainted(false);
        btnClose.setOpaque(false);
        btnClose.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnClose.addActionListener(e -> closeReceipt());
        
        btnClose.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnClose.setBackground(DARK_BROWN);
                btnClose.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnClose.setBackground(COFFEE_BROWN);
                btnClose.repaint();
            }
        });
        
        add(btnClose);
        
        // Info label at bottom
        JLabel lblInfo = new JLabel("Thank you for your order! ☕");
        lblInfo.setFont(new Font("Arial", Font.ITALIC, 13));
        lblInfo.setForeground(COFFEE_BROWN);
        lblInfo.setHorizontalAlignment(SwingConstants.CENTER);
        lblInfo.setBounds(0, 790, 700, 20);
        add(lblInfo);
    }
    
    private void printReceipt(JTextArea txtReceipt) {
        // Show printing dialog
        JDialog printDialog = new JDialog(this, "Printing", true);
        printDialog.setSize(350, 150);
        printDialog.setLocationRelativeTo(this);
        printDialog.setUndecorated(true);
        
        JPanel dialogPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        dialogPanel.setBackground(SOFT_WHITE);
        dialogPanel.setLayout(null);
        dialogPanel.setBorder(BorderFactory.createLineBorder(COFFEE_BROWN, 3));
        dialogPanel.setOpaque(false);
        
        JLabel lblPrinting = new JLabel("🖨️ Preparing to print...");
        lblPrinting.setFont(new Font("Arial", Font.BOLD, 16));
        lblPrinting.setForeground(DARK_BROWN);
        lblPrinting.setBounds(60, 40, 250, 30);
        dialogPanel.add(lblPrinting);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBounds(40, 85, 270, 20);
        progressBar.setForeground(COFFEE_BROWN);
        dialogPanel.add(progressBar);
        
        printDialog.add(dialogPanel);
        
        // Print in background
        new Thread(() -> {
            try {
                Thread.sleep(500);
                boolean complete = txtReceipt.print();
                
                SwingUtilities.invokeLater(() -> {
                    printDialog.dispose();
                    
                    if (complete) {
                        showSuccessDialog("Receipt sent to printer successfully!", "Print Success");
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Printing was cancelled.",
                            "Print Cancelled",
                            JOptionPane.WARNING_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    printDialog.dispose();
                    JOptionPane.showMessageDialog(this,
                        "Failed to print receipt: " + e.getMessage(),
                        "Print Error",
                        JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
        
        printDialog.setVisible(true);
    }
    
    private void saveReceipt() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Receipt");
        fileChooser.setSelectedFile(new java.io.File(
            "SmartCafe_Receipt_" + String.format("%04d", orderId) + ".txt"
        ));
        
        // Add file filter
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }
            public String getDescription() {
                return "Text Files (*.txt)";
            }
        });
        
        int result = fileChooser.showSaveDialog(this);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            
            // Ensure .txt extension
            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath += ".txt";
            }
            
            boolean saved = ReceiptGenerator.saveReceiptToFile(orderId, filePath);
            
            if (saved) {
                showSuccessDialog(
                    "Receipt saved successfully!\n\nLocation: " + filePath,
                    "Save Success"
                );
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to save receipt!",
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showSuccessDialog(String message, String title) {
        JDialog successDialog = new JDialog(this, title, true);
        successDialog.setSize(450, 220);
        successDialog.setLocationRelativeTo(this);
        successDialog.setUndecorated(true);
        
        JPanel successPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        successPanel.setBackground(SOFT_WHITE);
        successPanel.setLayout(null);
        successPanel.setBorder(BorderFactory.createLineBorder(new Color(76, 175, 80), 3));
        successPanel.setOpaque(false);
        
        JLabel lblSuccess = new JLabel("✓");
        lblSuccess.setFont(new Font("Arial", Font.PLAIN, 50));
        lblSuccess.setForeground(new Color(76, 175, 80));
        lblSuccess.setBounds(195, 20, 60, 60);
        successPanel.add(lblSuccess);
        
        JTextArea lblMessage = new JTextArea(message);
        lblMessage.setFont(new Font("Arial", Font.PLAIN, 14));
        lblMessage.setForeground(DARK_BROWN);
        lblMessage.setEditable(false);
        lblMessage.setOpaque(false);
        lblMessage.setLineWrap(true);
        lblMessage.setWrapStyleWord(true);
        lblMessage.setBounds(30, 90, 390, 60);
        successPanel.add(lblMessage);
        
        JButton btnOk = new JButton("OK") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };
        btnOk.setBounds(150, 165, 150, 40);
        btnOk.setFont(new Font("Arial", Font.BOLD, 15));
        btnOk.setBackground(new Color(76, 175, 80));
        btnOk.setForeground(Color.WHITE);
        btnOk.setFocusPainted(false);
        btnOk.setContentAreaFilled(false);
        btnOk.setBorderPainted(false);
        btnOk.setOpaque(false);
        btnOk.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOk.addActionListener(e -> successDialog.dispose());
        successPanel.add(btnOk);
        
        successDialog.add(successPanel);
        successDialog.setVisible(true);
    }
    
    private void closeReceipt() {
        this.dispose();
    }
}
