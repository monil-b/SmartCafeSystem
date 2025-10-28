package views.customer;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import controllers.OrderController;
import models.*;
import threads.OrderProcessor;

public class PaymentFrame extends JFrame {
    
    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color SOFT_WHITE = new Color(255, 253, 250);
    
    private User currentUser;
    private List<CartItem> cartItems;
    private MenuFrame menuFrame;
    private double totalAmount;
    private JRadioButton rbCash, rbCard, rbUPI;
    private JPanel cashPanel, cardPanel, upiPanel;
    
    public PaymentFrame(User user, List<CartItem> items, MenuFrame menu) {
        this.currentUser = user;
        this.cartItems = items;
        this.menuFrame = menu;
        calculateTotal();
        
        setTitle("SmartCafe - Payment");
        setSize(850, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CREAM);
        
        initComponents();
    }
    
    private void calculateTotal() {
        totalAmount = 0.0;
        for (CartItem item : cartItems) {
            totalAmount += item.getSubtotal();
        }
    }
    
    private void initComponents() {
        setLayout(null);
        
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, DARK_BROWN, 
                                                     getWidth(), 0, COFFEE_BROWN);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setBounds(0, 0, 850, 100);
        header.setLayout(null);
        
        JLabel lblIcon = new JLabel("💳");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 45));
        lblIcon.setBounds(40, 25, 55, 50);
        header.add(lblIcon);
        
        JLabel lblTitle = new JLabel("Payment & Checkout");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 34));
        lblTitle.setForeground(WHITE_SMOKE);
        lblTitle.setBounds(105, 22, 450, 42);
        header.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Complete your order securely");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(255, 255, 255, 220));
        lblSubtitle.setBounds(105, 65, 300, 22);
        header.add(lblSubtitle);
        
        add(header);
        
        // Order summary card 
        JPanel summaryCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        summaryCard.setBackground(SOFT_WHITE);
        summaryCard.setBounds(50, 130, 750, 190);
        summaryCard.setLayout(null);
        summaryCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        summaryCard.setOpaque(false);
        
        JLabel lblSummaryTitle = new JLabel("📋 Order Summary");
        lblSummaryTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblSummaryTitle.setForeground(DARK_BROWN);
        lblSummaryTitle.setBounds(20, 15, 300, 30);
        summaryCard.add(lblSummaryTitle);
        
        int totalItems = 0;
        for (CartItem item : cartItems) {
            totalItems += item.getQuantity();
        }
        
        JLabel lblItems = new JLabel("Total Items: " + totalItems);
        lblItems.setFont(new Font("Arial", Font.PLAIN, 17));
        lblItems.setForeground(COFFEE_BROWN);
        lblItems.setBounds(30, 60, 300, 25);
        summaryCard.add(lblItems);
        
        JLabel lblSubtotal = new JLabel("Subtotal: ₹" + String.format("%.2f", totalAmount));
        lblSubtotal.setFont(new Font("Arial", Font.PLAIN, 17));
        lblSubtotal.setForeground(COFFEE_BROWN);
        lblSubtotal.setBounds(30, 90, 300, 25);
        summaryCard.add(lblSubtotal);
        
        double tax = totalAmount * 0.05;
        double grandTotal = totalAmount + tax;
        
        JLabel lblTax = new JLabel("Tax (GST 5%): ₹" + String.format("%.2f", tax));
        lblTax.setFont(new Font("Arial", Font.PLAIN, 17));
        lblTax.setForeground(COFFEE_BROWN);
        lblTax.setBounds(30, 120, 300, 25);
        summaryCard.add(lblTax);
        
        JSeparator sep1 = new JSeparator();
        sep1.setBounds(430, 20, 2, 150);
        sep1.setOrientation(SwingConstants.VERTICAL);
        sep1.setForeground(LIGHT_COFFEE);
        summaryCard.add(sep1);
        
        JLabel lblTotalLabel = new JLabel("Grand Total");
        lblTotalLabel.setFont(new Font("Arial", Font.BOLD, 19));
        lblTotalLabel.setForeground(COFFEE_BROWN);
        lblTotalLabel.setBounds(460, 55, 250, 30);
        summaryCard.add(lblTotalLabel);
        
        JLabel lblGrandTotal = new JLabel("₹" + String.format("%.2f", grandTotal));
        lblGrandTotal.setFont(new Font("Arial", Font.BOLD, 38));
        lblGrandTotal.setForeground(COFFEE_BROWN);
        lblGrandTotal.setBounds(460, 90, 250, 50);
        summaryCard.add(lblGrandTotal);
        
        add(summaryCard);
        
        // Payment method card
        JPanel paymentCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        paymentCard.setBackground(SOFT_WHITE);
        paymentCard.setBounds(50, 340, 750, 180);
        paymentCard.setLayout(null);
        paymentCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        paymentCard.setOpaque(false);
        
        JLabel lblPaymentTitle = new JLabel("💰 Select Payment Method");
        lblPaymentTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblPaymentTitle.setForeground(DARK_BROWN);
        lblPaymentTitle.setBounds(20, 15, 400, 30);
        paymentCard.add(lblPaymentTitle);
        
        ButtonGroup paymentGroup = new ButtonGroup();
        
        // Cash option
        cashPanel = createPaymentOption("💵", "Cash", "Pay on delivery");
        cashPanel.setBounds(20, 65, 230, 95);
        rbCash = (JRadioButton) cashPanel.getComponent(0);
        rbCash.setSelected(true);
        paymentGroup.add(rbCash);
        paymentCard.add(cashPanel);
        
        // Card option
        cardPanel = createPaymentOption("💳", "Card", "Credit/Debit Card");
        cardPanel.setBounds(265, 65, 230, 95);
        rbCard = (JRadioButton) cardPanel.getComponent(0);
        paymentGroup.add(rbCard);
        paymentCard.add(cardPanel);
        
        // UPI option
        upiPanel = createPaymentOption("📱", "UPI", "GPay, PhonePe");
        upiPanel.setBounds(510, 65, 230, 95);
        rbUPI = (JRadioButton) upiPanel.getComponent(0);
        paymentGroup.add(rbUPI);
        paymentCard.add(upiPanel);
        
        // Update initial selection appearance
        cashPanel.setBorder(BorderFactory.createLineBorder(COFFEE_BROWN, 3));
        
        add(paymentCard);
        
        // Timer info card 
        JPanel timerCard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            }
        };
        timerCard.setBackground(new Color(255, 248, 230));
        timerCard.setBounds(50, 540, 750, 95);
        timerCard.setLayout(null);
        timerCard.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
        timerCard.setOpaque(false);
        
        JLabel lblTimerIcon = new JLabel("⏱️");
        lblTimerIcon.setFont(new Font("Arial", Font.PLAIN, 40));
        lblTimerIcon.setBounds(35, 25, 50, 45);
        timerCard.add(lblTimerIcon);
        
        JLabel lblTimerTitle = new JLabel("Estimated Preparation Time");
        lblTimerTitle.setFont(new Font("Arial", Font.BOLD, 19));
        lblTimerTitle.setForeground(DARK_BROWN);
        lblTimerTitle.setBounds(110, 20, 400, 26);
        timerCard.add(lblTimerTitle);
        
        JLabel lblTimerText = new JLabel("Your order will be ready in approximately 5 minutes");
        lblTimerText.setFont(new Font("Arial", Font.PLAIN, 15));
        lblTimerText.setForeground(COFFEE_BROWN);
        lblTimerText.setBounds(110, 50, 500, 22);
        timerCard.add(lblTimerText);
        
        add(timerCard);
        
        // Buttons 
        JButton btnCancel = new JButton("← Cancel") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(LIGHT_COFFEE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 20, 20);
                super.paintComponent(g);
            }
        };
        btnCancel.setBounds(50, 660, 220, 58);
        btnCancel.setFont(new Font("Arial", Font.BOLD, 18));
        btnCancel.setBackground(SOFT_WHITE);
        btnCancel.setForeground(COFFEE_BROWN);
        btnCancel.setFocusPainted(false);
        btnCancel.setContentAreaFilled(false);
        btnCancel.setBorderPainted(false);
        btnCancel.setOpaque(false);
        btnCancel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCancel.addActionListener(e -> cancel());
        
        btnCancel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCancel.setBackground(CREAM);
                btnCancel.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCancel.setBackground(SOFT_WHITE);
                btnCancel.repaint();
            }
        });
        
        add(btnCancel);
        
        JButton btnConfirm = new JButton("Confirm & Place Order 🎉") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        btnConfirm.setBounds(290, 660, 510, 58);
        btnConfirm.setFont(new Font("Arial", Font.BOLD, 20));
        btnConfirm.setBackground(COFFEE_BROWN);
        btnConfirm.setForeground(WHITE_SMOKE);
        btnConfirm.setFocusPainted(false);
        btnConfirm.setContentAreaFilled(false);
        btnConfirm.setBorderPainted(false);
        btnConfirm.setOpaque(false);
        btnConfirm.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnConfirm.addActionListener(e -> confirmPayment());
        
        btnConfirm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnConfirm.setBackground(DARK_BROWN);
                btnConfirm.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnConfirm.setBackground(COFFEE_BROWN);
                btnConfirm.repaint();
            }
        });
        
        add(btnConfirm);
    }
    
    private JPanel createPaymentOption(String icon, String title, String subtitle) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        panel.setLayout(null);
        panel.setBackground(CREAM);
        panel.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
        panel.setOpaque(false);
        
        JRadioButton rb = new JRadioButton();
        rb.setBounds(15, 35, 24, 24);
        rb.setBackground(CREAM);
        rb.setOpaque(false);
        panel.add(rb);
        
        JLabel lblIcon = new JLabel(icon);
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 36));
        lblIcon.setBounds(50, 10, 50, 45);
        panel.add(lblIcon);
        
        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(DARK_BROWN);
        lblTitle.setBounds(110, 15, 110, 24);
        panel.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel(subtitle);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitle.setForeground(COFFEE_BROWN);
        lblSubtitle.setBounds(110, 42, 160, 18);
        panel.add(lblSubtitle);
        
        // Checkmark when selected
        JLabel lblCheck = new JLabel("✓");
        lblCheck.setFont(new Font("Arial", Font.BOLD, 24));
        lblCheck.setForeground(new Color(76, 175, 80));
        lblCheck.setBounds(195, 8, 30, 30);
        lblCheck.setVisible(false);
        panel.add(lblCheck);
        
        rb.addActionListener(e -> {
            // Update all panels
            cashPanel.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
            cardPanel.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
            upiPanel.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
            
            // Hide all checkmarks
            for (Component c : cashPanel.getComponents()) {
                if (c instanceof JLabel && ((JLabel)c).getText().equals("✓")) c.setVisible(false);
            }
            for (Component c : cardPanel.getComponents()) {
                if (c instanceof JLabel && ((JLabel)c).getText().equals("✓")) c.setVisible(false);
            }
            for (Component c : upiPanel.getComponents()) {
                if (c instanceof JLabel && ((JLabel)c).getText().equals("✓")) c.setVisible(false);
            }
            
            // Highlight selected
            panel.setBorder(BorderFactory.createLineBorder(COFFEE_BROWN, 3));
            lblCheck.setVisible(true);
        });
        
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                if (!rb.isSelected()) {
                    panel.setBorder(BorderFactory.createLineBorder(COFFEE_BROWN, 2));
                }
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                if (!rb.isSelected()) {
                    panel.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
                }
            }
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                rb.doClick();
            }
        });
        
        return panel;
    }
    
    private void confirmPayment() {
        // Determine payment method before threading
        final String paymentMethod;
        if (rbCard.isSelected()) {
            paymentMethod = "Card";
        } else if (rbUPI.isSelected()) {
            paymentMethod = "UPI";
        } else {
            paymentMethod = "Cash";
        }
        
        // Show processing dialog
        JDialog processingDialog = new JDialog(this, "Processing Payment", true);
        processingDialog.setSize(400, 180);
        processingDialog.setLocationRelativeTo(this);
        processingDialog.setUndecorated(true);
        
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
        
        JLabel lblProcessing = new JLabel("⏳ Processing Payment...");
        lblProcessing.setFont(new Font("Arial", Font.BOLD, 20));
        lblProcessing.setForeground(DARK_BROWN);
        lblProcessing.setBounds(80, 50, 300, 30);
        dialogPanel.add(lblProcessing);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setBounds(50, 100, 300, 25);
        progressBar.setForeground(COFFEE_BROWN);
        dialogPanel.add(progressBar);
        
        processingDialog.add(dialogPanel);
        
        // Process payment in background
        new Thread(() -> {
            try {
                Thread.sleep(1500); // Simulate processing
                
                SwingUtilities.invokeLater(() -> {
                    processingDialog.dispose();
                    
                    int orderId = OrderController.createOrder(currentUser.getUserId(), cartItems, paymentMethod);
                    
                    if (orderId > 0) {
                        // Start order processing with timer
                        OrderProcessor.submitOrder(orderId, currentUser.getUsername());
                        
                        // Custom success dialog
                        JDialog successDialog = new JDialog(this, "Order Placed", true);
                        successDialog.setSize(450, 280);
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
                        
                        JLabel lblSuccess = new JLabel("✅");
                        lblSuccess.setFont(new Font("Arial", Font.PLAIN, 60));
                        lblSuccess.setBounds(195, 20, 60, 60);
                        successPanel.add(lblSuccess);
                        
                        JLabel lblTitle = new JLabel("Payment Successful!");
                        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
                        lblTitle.setForeground(new Color(76, 175, 80));
                        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
                        lblTitle.setBounds(50, 90, 350, 30);
                        successPanel.add(lblTitle);
                        
                        JLabel lblOrderId = new JLabel("Order ID: #" + String.format("%04d", orderId));
                        lblOrderId.setFont(new Font("Arial", Font.BOLD, 18));
                        lblOrderId.setForeground(DARK_BROWN);
                        lblOrderId.setHorizontalAlignment(SwingConstants.CENTER);
                        lblOrderId.setBounds(50, 130, 350, 25);
                        successPanel.add(lblOrderId);
                        
                        JLabel lblMethod = new JLabel("Payment Method: " + paymentMethod);
                        lblMethod.setFont(new Font("Arial", Font.PLAIN, 14));
                        lblMethod.setForeground(COFFEE_BROWN);
                        lblMethod.setHorizontalAlignment(SwingConstants.CENTER);
                        lblMethod.setBounds(50, 160, 350, 20);
                        successPanel.add(lblMethod);
                        
                        JButton btnOk = new JButton("Track My Order") {
                            @Override
                            protected void paintComponent(Graphics g) {
                                Graphics2D g2d = (Graphics2D) g;
                                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                g2d.setColor(getBackground());
                                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                                super.paintComponent(g);
                            }
                        };
                        btnOk.setBounds(125, 200, 200, 45);
                        btnOk.setFont(new Font("Arial", Font.BOLD, 16));
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
                        
                        // Add order to menu frame tracking
                        menuFrame.addActiveOrder(orderId);
                        
                        // Clear the cart
                        menuFrame.clearCart();
                        
                        // Go back to menu with order tracking
                        PaymentFrame.this.dispose();
                        menuFrame.setVisible(true);
                        
                    } else {
                        JOptionPane.showMessageDialog(PaymentFrame.this,
                            "Failed to create order!\nPlease try again.",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }).start();
        
        processingDialog.setVisible(true);
    }
    
    private void cancel() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to cancel payment?\nYour cart items will be preserved.",
            "Cancel Payment",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            this.dispose();
            menuFrame.setVisible(true);
        }
    }
}
