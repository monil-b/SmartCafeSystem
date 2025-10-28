package views.owner;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;
import threads.KitchenManager;
import models.Owner;

public class KitchenDisplayFrame extends JFrame {
    
    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color SOFT_WHITE = new Color(255, 253, 250);
    private static final Color BUSY_ORANGE = new Color(255, 152, 0);
    private static final Color IDLE_GREEN = new Color(76, 175, 80);
    private static final Color QUEUE_BLUE = new Color(33, 150, 243);
    
    private Owner currentOwner;
    private OwnerDashboardFrame dashboardFrame;
    private JLabel[] stationLabels;
    private JLabel[] orderLabels;
    private JLabel[] statusLabels;
    private JLabel[] iconLabels;
    private JLabel[] itemLabels;
    private JLabel[] timerLabels;
    private JLabel queueCountLabel;
    private Timer refreshTimer;
    private int cookingAnimationState = 0;
    
    public KitchenDisplayFrame(Owner owner, OwnerDashboardFrame dashboard) {
        this.currentOwner = owner;
        this.dashboardFrame = dashboard;
        
        setTitle("SmartCafe - Kitchen Display");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CREAM);
        
        // Initialize kitchen manager
        KitchenManager.initialize();
        
        initComponents();
        
        // Auto-refresh every 1 second for real-time updates
        refreshTimer = new Timer(1000, e -> {
            updateStationStatus();
            cookingAnimationState = (cookingAnimationState + 1) % 4;
        });
        refreshTimer.start();
    }
    
    private void initComponents() {
        setLayout(null);
        
        // Header Panel with Gradient
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
        
        JLabel lblIcon = new JLabel("‍🍳");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 45));
        lblIcon.setBounds(30, 25, 55, 50);
        header.add(lblIcon);
        
        JLabel lblTitle = new JLabel("Kitchen Display System");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setForeground(WHITE_SMOKE);
        lblTitle.setBounds(95, 20, 500, 38);
        header.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Real-time kitchen station monitoring");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(255, 255, 255, 220));
        lblSubtitle.setBounds(95, 60, 400, 22);
        header.add(lblSubtitle);
        
        add(header);
        
        // Info panel
        JPanel infoPanel = new RoundedPanel(20, new Color(255, 248, 230), LIGHT_COFFEE);
        infoPanel.setBounds(40, 120, 1020, 90);
        infoPanel.setLayout(null);
        
        JLabel lblInfo = new JLabel("🔥 3 Parallel Kitchen Stations Active");
        lblInfo.setFont(new Font("Arial", Font.BOLD, 20));
        lblInfo.setForeground(DARK_BROWN);
        lblInfo.setBounds(30, 18, 500, 28);
        infoPanel.add(lblInfo);
        
        JLabel lblSubInfo = new JLabel("Orders are automatically distributed across available stations for faster processing");
        lblSubInfo.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubInfo.setForeground(COFFEE_BROWN);
        lblSubInfo.setBounds(30, 50, 700, 22);
        infoPanel.add(lblSubInfo);
        
        // Queue counter
        queueCountLabel = new JLabel("📋 Queue: 0");
        queueCountLabel.setFont(new Font("Arial", Font.BOLD, 16));
        queueCountLabel.setForeground(QUEUE_BLUE);
        queueCountLabel.setBounds(820, 30, 180, 28);
        infoPanel.add(queueCountLabel);
        
        add(infoPanel);
        
        // Station panels
        stationLabels = new JLabel[3];
        orderLabels = new JLabel[3];
        statusLabels = new JLabel[3];
        iconLabels = new JLabel[3];
        itemLabels = new JLabel[3];
        timerLabels = new JLabel[3];
        
        int xPos = 40;
        for (int i = 0; i < 3; i++) {
            JPanel stationPanel = createStationPanel(i + 1, xPos, 230);
            add(stationPanel);
            xPos += 360;
        }
        
        // Control buttons
        JButton btnBack = createStyledButton(" Back to Dashboard", 420, 710, 260, 50);
        btnBack.addActionListener(e -> {
            refreshTimer.stop();
            this.dispose();
        });
        add(btnBack);
        
        // Initial status update
        updateStationStatus();
    }
    
    // Rounded Panel Helper Class
    private class RoundedPanel extends JPanel {
        private int radius;
        private Color bgColor;
        private Color borderColor;
        
        public RoundedPanel(int radius, Color bgColor, Color borderColor) {
            this.radius = radius;
            this.bgColor = bgColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Fill background
            g2d.setColor(bgColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
            
            // Draw border
            g2d.setColor(borderColor);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, radius, radius);
        }
    }
    
    private JPanel createStationPanel(int stationNum, int x, int y) {
        JPanel panel = new RoundedPanel(20, SOFT_WHITE, LIGHT_COFFEE);
        panel.setBounds(x, y, 330, 430);
        panel.setLayout(null);
        
        // Station header with gradient
        JPanel headerPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, COFFEE_BROWN, getWidth(), 0, DARK_BROWN);
                g2d.setPaint(gp);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        headerPanel.setBounds(0, 0, 330, 70);
        headerPanel.setLayout(null);
        headerPanel.setOpaque(false);
        
        JLabel lblStationName = new JLabel("STATION " + stationNum, SwingConstants.CENTER);
        lblStationName.setFont(new Font("Arial", Font.BOLD, 26));
        lblStationName.setForeground(WHITE_SMOKE);
        lblStationName.setBounds(0, 20, 330, 35);
        headerPanel.add(lblStationName);
        
        panel.add(headerPanel);
        
        // Smaller station icon with shadow effect
        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(new Color(255, 253, 250));
        iconPanel.setBounds(115, 95, 100, 100);
        iconPanel.setLayout(new BorderLayout());
        iconPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 3),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        
        JLabel lblIcon = new JLabel("🔥", SwingConstants.CENTER);
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 50));
        iconLabels[stationNum - 1] = lblIcon;
        iconPanel.add(lblIcon, BorderLayout.CENTER);
        
        panel.add(iconPanel);
        
        // Status indicator with styled background
        JPanel statusPanel = new JPanel();
        statusPanel.setBackground(new Color(240, 248, 255));
        statusPanel.setBounds(50, 215, 230, 45);
        statusPanel.setLayout(new BorderLayout());
        statusPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel lblStatus = new JLabel("✓ READY", SwingConstants.CENTER);
        lblStatus.setFont(new Font("Arial", Font.BOLD, 20));
        lblStatus.setForeground(IDLE_GREEN);
        statusLabels[stationNum - 1] = lblStatus;
        statusPanel.add(lblStatus, BorderLayout.CENTER);
        
        panel.add(statusPanel);
        
        // Timer label
        JLabel lblTimer = new JLabel("", SwingConstants.CENTER);
        lblTimer.setFont(new Font("Arial", Font.BOLD, 15));
        lblTimer.setForeground(BUSY_ORANGE);
        lblTimer.setBounds(20, 270, 290, 23);
        timerLabels[stationNum - 1] = lblTimer;
        panel.add(lblTimer);
        
        // Current order
        JLabel lblOrder = new JLabel("Ready for next order", SwingConstants.CENTER);
        lblOrder.setFont(new Font("Arial", Font.PLAIN, 14));
        lblOrder.setForeground(COFFEE_BROWN);
        lblOrder.setBounds(20, 300, 290, 23);
        orderLabels[stationNum - 1] = lblOrder;
        panel.add(lblOrder);
        
        // Item details 
        JLabel lblItems = new JLabel("", SwingConstants.CENTER);
        lblItems.setFont(new Font("Arial", Font.PLAIN, 12));
        lblItems.setForeground(new Color(100, 100, 100));
        lblItems.setBounds(15, 330, 300, 85);
        lblItems.setVerticalAlignment(SwingConstants.TOP);
        itemLabels[stationNum - 1] = lblItems;
        panel.add(lblItems);
        
        // Station label for reference
        stationLabels[stationNum - 1] = lblStationName;
        
        return panel;
    }
    
    private JButton createStyledButton(String text, int x, int y, int width, int height) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
                
                if (getModel().isPressed()) {
                    g2d.setColor(DARK_BROWN);
                } else if (getModel().isRollover()) {
                    g2d.setColor(DARK_BROWN);
                } else {
                    g2d.setColor(COFFEE_BROWN);
                }
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                g2d.dispose();
                
                // Paint text manually
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                int tx = (getWidth() - textWidth) / 2;
                int ty = (getHeight() + textHeight) / 2 - 2;
                
                g.setColor(WHITE_SMOKE);
                g.setFont(getFont());
                g.drawString(getText(), tx, ty);
            }
        };
        
        btn.setBounds(x, y, width, height);
        btn.setFont(new Font("Arial", Font.BOLD, 16));
        btn.setForeground(WHITE_SMOKE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
    
    private String removeEmojisFromText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        return text.replaceAll("[\\p{So}\\p{Sk}]", "").replaceAll("\\s+", " ").trim();
    }
    
    private String getCookingAnimation() {
        String[] animations = {"🔥", "🍳", "👨‍🍳", "💨"};
        return animations[cookingAnimationState];
    }
    
    private void updateStationStatus() {
        List<Map<String, Object>> stationStatuses = KitchenManager.getStationStatus();
        
        // Update queue count
        int queueSize = KitchenManager.getQueueSize();
        if (queueSize > 0) {
            queueCountLabel.setText("📋 Queue: " + queueSize + " order" + (queueSize > 1 ? "s" : ""));
            queueCountLabel.setForeground(BUSY_ORANGE);
        } else {
            queueCountLabel.setText("📋 Queue: Empty");
            queueCountLabel.setForeground(IDLE_GREEN);
        }
        
        for (int i = 0; i < stationStatuses.size() && i < 3; i++) {
            Map<String, Object> status = stationStatuses.get(i);
            boolean busy = (Boolean) status.get("busy");
            int currentOrder = (Integer) status.get("currentOrder");
            String itemDetails = (String) status.get("itemDetails");
            long timeRemaining = (Long) status.get("timeRemaining");
            
            if (busy && currentOrder != -1) {
                // BUSY state - Cooking with animation
                statusLabels[i].setText("⚡ COOKING");
                statusLabels[i].setForeground(BUSY_ORANGE);
                orderLabels[i].setText("Order #" + String.format("%04d", currentOrder));
                
                // Animated cooking emoji
                iconLabels[i].setText(getCookingAnimation());
                
                // Show item details
                if (itemDetails != null && !itemDetails.isEmpty()) {
                    String cleanedDetails = removeEmojisFromText(itemDetails);
                    // Split into multiple lines if too long
                    if (cleanedDetails.length() > 40) {
                        String[] parts = cleanedDetails.split(",");
                        StringBuilder formatted = new StringBuilder("<html><center>");
                        for (int j = 0; j < parts.length; j++) {
                            formatted.append(parts[j].trim());
                            if (j < parts.length - 1) {
                                formatted.append("<br>");
                            }
                        }
                        formatted.append("</center></html>");
                        itemLabels[i].setText(formatted.toString());
                    } else {
                        itemLabels[i].setText(cleanedDetails);
                    }
                } else {
                    itemLabels[i].setText("");
                }
                
                // Show timer
                if (timeRemaining > 0) {
                    long minutes = timeRemaining / 60;
                    long seconds = timeRemaining % 60;
                    timerLabels[i].setText(String.format("⏱ %02d:%02d remaining", minutes, seconds));
                } else {
                    timerLabels[i].setText("⏱ Almost done!");
                }
                
            } else {
                // IDLE state - Ready for orders
                statusLabels[i].setText("✓ READY");
                statusLabels[i].setForeground(IDLE_GREEN);
                orderLabels[i].setText("Waiting for order...");
                iconLabels[i].setText("😊");
                itemLabels[i].setText("");
                timerLabels[i].setText("");
            }
        }
    }
    
    @Override
    public void dispose() {
        if (refreshTimer != null) {
            refreshTimer.stop();
        }
        super.dispose();
    }
}
