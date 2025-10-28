package views.owner;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import controllers.OwnerController;
import models.Owner;

public class OwnerDashboardFrame extends JFrame {

    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color SOFT_WHITE = new Color(255, 253, 250);
    private static final Color CARD_TOTAL_ORDERS = new Color(88, 105, 103);
    private static final Color CARD_REVENUE = new Color(119, 140, 103);
    private static final Color CARD_PENDING = new Color(204, 153, 102);
    private static final Color CARD_PREPARING = new Color(168, 119, 110);
    
    private Owner currentOwner;
    private JLabel lblTotalOrders, lblRevenue, lblPending, lblPreparing;
    private Timer refreshTimer;
    
    public OwnerDashboardFrame(Owner owner) {
        this.currentOwner = owner;
        
        setTitle("SmartCafe - Owner Dashboard");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CREAM);
        
        initComponents();
        loadAnalytics();
        
        // Auto-refresh every 5 seconds
        refreshTimer = new Timer(5000, e -> loadAnalytics());
        refreshTimer.start();
    }
    
    private void initComponents() {
        setLayout(null);
        
        // Top bar 
        JPanel topBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, DARK_BROWN, getWidth(), 0, COFFEE_BROWN);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topBar.setBounds(0, 0, 1200, 100);
        topBar.setLayout(null);
        
        JLabel lblIcon = new JLabel("👨");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 45));
        lblIcon.setBounds(40, 25, 55, 50);
        topBar.add(lblIcon);
        
        JLabel lblWelcome = new JLabel("Owner Dashboard");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 32));
        lblWelcome.setForeground(WHITE_SMOKE);
        lblWelcome.setBounds(105, 20, 500, 38);
        topBar.add(lblWelcome);
        
        JLabel lblSubtext = new JLabel("Welcome, " + currentOwner.getFullName());
        lblSubtext.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtext.setForeground(new Color(255, 255, 255, 220));
        lblSubtext.setBounds(105, 60, 400, 22);
        topBar.add(lblSubtext);
        
        // Logout button
        JButton btnLogout = createRoundedButton("Logout", 1080, 30, 100, 40);
        btnLogout.addActionListener(e -> logout());
        topBar.add(btnLogout);
        
        add(topBar);
        
        // Analytics Cards
        int cardY = 130;
        int cardSpacing = 290;
        
        // Card 1: Total Orders
        JPanel card1 = createAnalyticsCard("Total Orders", "0", 40, cardY, CARD_TOTAL_ORDERS);
        lblTotalOrders = (JLabel) card1.getComponent(2);
        add(card1);
        
        // Card 2: Revenue
        JPanel card2 = createAnalyticsCard("Revenue", "₹0.00", 40 + cardSpacing, cardY, CARD_REVENUE);
        lblRevenue = (JLabel) card2.getComponent(2);
        add(card2);
        
        // Card 3: Pending Orders
        JPanel card3 = createAnalyticsCard("Pending", "0", 40 + cardSpacing * 2, cardY, CARD_PENDING);
        lblPending = (JLabel) card3.getComponent(2);
        add(card3);
        
        // Card 4: Preparing Orders
        JPanel card4 = createAnalyticsCard("Preparing", "0", 40 + cardSpacing * 3, cardY, CARD_PREPARING);
        lblPreparing = (JLabel) card4.getComponent(2);
        add(card4);
        
        // Quick Actions Panel
        JPanel actionsPanel = createActionsPanel();
        actionsPanel.setBounds(40, 360, 1120, 200);
        add(actionsPanel);
        
        // Management Buttons
        int btnY = 590;
        int btnSpacing = 290;
        
        JButton btnOrderQueue = createManagementButton("Order Queue", "View & manage orders", 40, btnY);
        btnOrderQueue.addActionListener(e -> openOrderQueue());
        add(btnOrderQueue);
        
        JButton btnMenu = createManagementButton("Menu Management", "Add, edit, delete items", 40 + btnSpacing, btnY);
        btnMenu.addActionListener(e -> openMenuManagement());
        add(btnMenu);
        
        JButton btnKitchen = createManagementButton("Kitchen Display", "Monitor kitchen stations", 40 + btnSpacing * 2, btnY);
        btnKitchen.addActionListener(e -> openKitchenDisplay());
        add(btnKitchen);
        
        JButton btnAnalytics = createManagementButton("Analytics", "View detailed reports", 40 + btnSpacing * 3, btnY);
        btnAnalytics.addActionListener(e -> openAnalytics());
        add(btnAnalytics);
    }
    
    private JPanel createAnalyticsCard(String title, String value, int x, int y, Color bgColor) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setBackground(bgColor);
        card.setBounds(x, y, 270, 180);
        card.setLayout(null);
        card.setOpaque(false);
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.PLAIN, 18));
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBounds(20, 30, 230, 25);
        card.add(lblTitle);
        
        JSeparator sep = new JSeparator();
        sep.setBounds(60, 65, 150, 2);
        sep.setForeground(new Color(255, 255, 255, 100));
        card.add(sep);
        
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 48));
        lblValue.setForeground(Color.WHITE);
        lblValue.setBounds(20, 85, 230, 70);
        card.add(lblValue);
        
        return card;
    }
    
    private JPanel createActionsPanel() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        panel.setBackground(new Color(255, 248, 230));
        panel.setLayout(null);
        panel.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
        panel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Quick Actions");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblTitle.setForeground(DARK_BROWN);
        lblTitle.setBounds(30, 20, 250, 30);
        panel.add(lblTitle);
        
        String[] actions = {
            "Today: " + new java.text.SimpleDateFormat("EEEE, MMMM dd, yyyy").format(new java.util.Date()),
            "Auto-refresh every 5 seconds",
            "Real-time order notifications enabled",
            "All systems operational"
        };
        
        int yPos = 70;
        for (String action : actions) {
            JLabel lbl = new JLabel(action);
            lbl.setFont(new Font("Arial", Font.PLAIN, 15));
            lbl.setForeground(COFFEE_BROWN);
            lbl.setBounds(40, yPos, 1040, 22);
            panel.add(lbl);
            yPos += 28;
        }
        
        return panel;
    }
    
    private JButton createManagementButton(String title, String subtitle, int x, int y) {
        JButton btn = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        btn.setBounds(x, y, 270, 120);
        btn.setLayout(null);
        btn.setBackground(COFFEE_BROWN);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        JLabel lblTitle = new JLabel(title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTitle.setForeground(WHITE_SMOKE);
        lblTitle.setBounds(0, 30, 270, 28);
        btn.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel(subtitle, SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSubtitle.setForeground(new Color(255, 255, 255, 200));
        lblSubtitle.setBounds(0, 65, 270, 20);
        btn.add(lblSubtitle);
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(DARK_BROWN);
                btn.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(COFFEE_BROWN);
                btn.repaint();
            }
        });
        
        return btn;
    }
    
    private JButton createRoundedButton(String text, int x, int y, int width, int height) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(WHITE_SMOKE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 15, 15);
                super.paintComponent(g);
            }
        };
        button.setBounds(x, y, width, height);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(WHITE_SMOKE);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    private void loadAnalytics() {
        Map<String, Object> analytics = OwnerController.getTodayAnalytics();
        
        int totalOrders = (Integer) analytics.getOrDefault("totalOrders", 0);
        double revenue = (Double) analytics.getOrDefault("totalRevenue", 0.0);
        int pending = (Integer) analytics.getOrDefault("pendingOrders", 0);
        int preparing = (Integer) analytics.getOrDefault("preparingOrders", 0);
        
        lblTotalOrders.setText(String.valueOf(totalOrders));
        lblRevenue.setText("₹" + String.format("%.2f", revenue));
        lblPending.setText(String.valueOf(pending));
        lblPreparing.setText(String.valueOf(preparing));
    }
    
    private void openOrderQueue() {
        new OrderQueueFrame(currentOwner, this).setVisible(true);
    }
    
    private void openMenuManagement() {
        new MenuManagementFrame(currentOwner, this).setVisible(true);
    }
    
    private void openKitchenDisplay() {
        new KitchenDisplayFrame(currentOwner, this).setVisible(true);
    }
    
    private void openAnalytics() {
        new AnalyticsFrame(currentOwner, this).setVisible(true);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (refreshTimer != null) {
                refreshTimer.stop();
            }
            this.dispose();
            new OwnerLoginFrame().setVisible(true);
        }
    }
}
