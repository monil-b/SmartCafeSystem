package views.owner;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import controllers.OwnerController;
import models.Owner;

public class AnalyticsFrame extends JFrame {
    
    // Coffee theme colors
    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color SOFT_WHITE = new Color(255, 253, 250);
    
    // Coffee theme colors for metric cards
    private static final Color ESPRESSO_BLUE = new Color(77, 93, 83);
    private static final Color MOCHA_GREEN = new Color(109, 131, 84);
    private static final Color CARAMEL_ORANGE = new Color(196, 140, 71);
    private static final Color CINNAMON_RED = new Color(165, 94, 68);
    private static final Color MATCHA_GREEN = new Color(130, 158, 93);
    
    private Owner currentOwner;
    private OwnerDashboardFrame dashboardFrame;
    
    public AnalyticsFrame(Owner owner, OwnerDashboardFrame dashboard) {
        this.currentOwner = owner;
        this.dashboardFrame = dashboard;
        
        setTitle("SmartCafe - Analytics & Reports");
        setSize(1100, 750);
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
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, DARK_BROWN, getWidth(), 0, COFFEE_BROWN);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setBounds(0, 0, 1100, 100);
        header.setLayout(null);
        
        JLabel lblTitle = new JLabel("Analytics & Reports");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setForeground(WHITE_SMOKE);
        lblTitle.setBounds(50, 20, 500, 38);
        header.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Business insights and performance metrics");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(255, 255, 255, 220));
        lblSubtitle.setBounds(50, 60, 400, 22);
        header.add(lblSubtitle);
        
        add(header);
        
        // Today's Summary Panel
        JPanel summaryPanel = createSummaryPanel();
        summaryPanel.setBounds(40, 130, 1020, 250);
        add(summaryPanel);
        
        // Charts info panel
        JPanel chartsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        chartsPanel.setBackground(SOFT_WHITE);
        chartsPanel.setBounds(40, 400, 1020, 240);
        chartsPanel.setLayout(null);
        chartsPanel.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
        chartsPanel.setOpaque(false);
        
        JLabel lblChartsTitle = new JLabel("Performance Insights");
        lblChartsTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblChartsTitle.setForeground(DARK_BROWN);
        lblChartsTitle.setBounds(30, 20, 400, 30);
        chartsPanel.add(lblChartsTitle);
        
        String[] insights = {
            "✓ Peak hours: 12:00 PM - 2:00 PM, 7:00 PM - 9:00 PM",
            "✓ Most popular category: Beverages (45% of orders)",
            "✓ Average order value: ₹250.00",
            "✓ Customer satisfaction: 4.5/5.0",
            "✓ Average preparation time: 8 minutes",
            "✓ Order completion rate: 98%"
        };
        
        int yPos = 70;
        for (String insight : insights) {
            JLabel lbl = new JLabel(insight);
            lbl.setFont(new Font("Arial", Font.PLAIN, 15));
            lbl.setForeground(COFFEE_BROWN);
            lbl.setBounds(40, yPos, 950, 22);
            chartsPanel.add(lbl);
            yPos += 28;
        }
        
        add(chartsPanel);
        
        // Back button - repositioned to be visible
        JButton btnBack = createStyledButton("← Back to Dashboard", 420, 660, 250, 50);
        btnBack.setBackground(COFFEE_BROWN);
        btnBack.setForeground(WHITE_SMOKE);
        btnBack.addActionListener(e -> this.dispose());
        add(btnBack);
    }
    
    private JPanel createSummaryPanel() {
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
        panel.setBackground(SOFT_WHITE);
        panel.setLayout(null);
        panel.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
        panel.setOpaque(false);
        
        JLabel lblTitle = new JLabel("Today's Summary");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblTitle.setForeground(DARK_BROWN);
        lblTitle.setBounds(30, 20, 400, 30);
        panel.add(lblTitle);
        
        // Get analytics data
        Map<String, Object> analytics = OwnerController.getTodayAnalytics();
        
        int totalOrders = (Integer) analytics.getOrDefault("totalOrders", 0);
        double revenue = (Double) analytics.getOrDefault("totalRevenue", 0.0);
        int pending = (Integer) analytics.getOrDefault("pendingOrders", 0);
        int preparing = (Integer) analytics.getOrDefault("preparingOrders", 0);
        int ready = (Integer) analytics.getOrDefault("readyOrders", 0);
        
        // Metric cards with proper spacing to fit all 5 cards
        int xPos = 30;
        int cardWidth = 182;
        int cardSpacing = 16;
        
        createMetricCard(panel, "Total Orders", String.valueOf(totalOrders), "📊", 
            xPos, 70, cardWidth, 130, ESPRESSO_BLUE);
        xPos += cardWidth + cardSpacing;
        
        createMetricCard(panel, "Revenue", "₹" + String.format("%.2f", revenue), "💰", 
            xPos, 70, cardWidth, 130, MOCHA_GREEN);
        xPos += cardWidth + cardSpacing;
        
        createMetricCard(panel, "Pending", String.valueOf(pending), "⏳", 
            xPos, 70, cardWidth, 130, CARAMEL_ORANGE);
        xPos += cardWidth + cardSpacing;
        
        createMetricCard(panel, "Preparing", String.valueOf(preparing), "🔥", 
            xPos, 70, cardWidth, 130, CINNAMON_RED);
        xPos += cardWidth + cardSpacing;
        
        createMetricCard(panel, "Ready", String.valueOf(ready), "✅", 
            xPos, 70, cardWidth, 130, MATCHA_GREEN);
        
        return panel;
    }
    
    private void createMetricCard(JPanel parent, String label, String value, String icon, 
                                  int x, int y, int width, int height, Color color) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Vibrant gradient background
                Color lighterColor = new Color(
                    Math.min(255, color.getRed() + 30),
                    Math.min(255, color.getGreen() + 30),
                    Math.min(255, color.getBlue() + 30)
                );
                GradientPaint gp = new GradientPaint(0, 0, lighterColor, 0, getHeight(), color);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                // Add subtle shadow effect
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 15, 15);
            }
        };
        card.setBounds(x, y, width, height);
        card.setLayout(null);
        card.setOpaque(false);
        
        JLabel lblIcon = new JLabel(icon, SwingConstants.CENTER);
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 35));
        lblIcon.setBounds(0, 15, width, 40);
        card.add(lblIcon);
        
        JLabel lblValue = new JLabel(value, SwingConstants.CENTER);
        lblValue.setFont(new Font("Arial", Font.BOLD, 28));
        lblValue.setForeground(Color.WHITE);
        lblValue.setBounds(0, 60, width, 30);
        card.add(lblValue);
        
        JLabel lblLabel = new JLabel(label, SwingConstants.CENTER);
        lblLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        lblLabel.setForeground(new Color(255, 255, 255, 240));
        lblLabel.setBounds(0, 95, width, 20);
        card.add(lblLabel);
        
        parent.add(card);
    }
    
    private JButton createStyledButton(String text, int x, int y, int width, int height) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Gradient background
                GradientPaint gp = new GradientPaint(0, 0, getBackground(), 0, getHeight(), 
                    getBackground().darker());
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                
                super.paintComponent(g);
            }
        };
        btn.setBounds(x, y, width, height);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setBackground(COFFEE_BROWN);
        btn.setForeground(WHITE_SMOKE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
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
}