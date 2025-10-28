package views.customer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import controllers.MenuController;
import models.User;
import models.MenuItem;
import models.CartItem;
import threads.OrderProcessor;
import views.LoginFrame;

public class MenuFrame extends JFrame {
    
    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color SOFT_WHITE = new Color(255, 253, 250);
    private static final Color BADGE_RED = new Color(220, 53, 69);
    
    private User currentUser;
    private java.util.List<MenuItem> menuItems;
    private Map<Integer, CartItem> cart;
    private JPanel menuPanel;
    private JLabel lblCartCount;
    private JLabel lblCartTotal;
    
    // Order tracking fields
    private java.util.List<Integer> activeOrders;
    private JPanel orderTrackingPanel;
    private Timer orderRefreshTimer;
    
    public MenuFrame(User user) {
        this.currentUser = user;
        this.cart = new HashMap<>();
        this.activeOrders = new ArrayList<>();
        
        setTitle("SmartCafe - Menu");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CREAM);
        
        loadMenuItems();
        initComponents();
    }
    
    private void loadMenuItems() {
        menuItems = MenuController.getAllMenuItems();
        System.out.println("Loaded " + menuItems.size() + " menu items");
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
                GradientPaint gp = new GradientPaint(0, 0, DARK_BROWN, 
                                                     getWidth(), 0, COFFEE_BROWN);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        topBar.setBounds(0, 0, 1100, 90);
        topBar.setLayout(null);
        
        JLabel lblIcon = new JLabel("☕");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 40));
        lblIcon.setBounds(30, 20, 50, 50);
        topBar.add(lblIcon);
        
        JLabel lblWelcome = new JLabel("Welcome, " + currentUser.getUsername() + "!");
        lblWelcome.setFont(new Font("Arial", Font.BOLD, 24));
        lblWelcome.setForeground(WHITE_SMOKE);
        lblWelcome.setBounds(90, 18, 400, 30);
        topBar.add(lblWelcome);
        
        JLabel lblSubtext = new JLabel("Order your favorite coffee & treats");
        lblSubtext.setFont(new Font("Arial", Font.PLAIN, 14));
        lblSubtext.setForeground(new Color(255, 255, 255, 220));
        lblSubtext.setBounds(90, 48, 300, 20);
        topBar.add(lblSubtext);
        
        // Cart button 
        JButton btnCart = new JButton("🛒 Cart") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        btnCart.setBounds(750, 25, 105, 42);
        btnCart.setFont(new Font("Arial", Font.BOLD, 15));
        btnCart.setBackground(WHITE_SMOKE);
        btnCart.setForeground(COFFEE_BROWN);
        btnCart.setFocusPainted(false);
        btnCart.setContentAreaFilled(false);
        btnCart.setBorderPainted(false);
        btnCart.setOpaque(false);
        btnCart.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCart.addActionListener(e -> openCart());
        
        btnCart.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCart.setBackground(CREAM);
                btnCart.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCart.setBackground(WHITE_SMOKE);
                btnCart.repaint();
            }
        });
        
        topBar.add(btnCart);
        
        // Orders button
        JButton btnOrderHistory = new JButton("📋 Orders") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        btnOrderHistory.setBounds(870, 25, 110, 42);
        btnOrderHistory.setFont(new Font("Arial", Font.BOLD, 15));
        btnOrderHistory.setBackground(WHITE_SMOKE);
        btnOrderHistory.setForeground(COFFEE_BROWN);
        btnOrderHistory.setFocusPainted(false);
        btnOrderHistory.setContentAreaFilled(false);
        btnOrderHistory.setBorderPainted(false);
        btnOrderHistory.setOpaque(false);
        btnOrderHistory.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnOrderHistory.addActionListener(e -> openOrderHistory());
        
        btnOrderHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnOrderHistory.setBackground(CREAM);
                btnOrderHistory.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnOrderHistory.setBackground(WHITE_SMOKE);
                btnOrderHistory.repaint();
            }
        });
        
        topBar.add(btnOrderHistory);
        
        // Logout button 
        JButton btnLogout = new JButton("Logout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        btnLogout.setBounds(985, 25, 95, 42);
        btnLogout.setFont(new Font("Arial", Font.BOLD, 15));
        btnLogout.setBackground(WHITE_SMOKE);
        btnLogout.setForeground(COFFEE_BROWN);
        btnLogout.setFocusPainted(false);
        btnLogout.setContentAreaFilled(false);
        btnLogout.setBorderPainted(false);
        btnLogout.setOpaque(false);
        btnLogout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> logout());

        btnLogout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(CREAM);
                btnLogout.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnLogout.setBackground(WHITE_SMOKE);
                btnLogout.repaint();
            }
        });

        topBar.add(btnLogout);
        
        // Cart badge 
        lblCartCount = new JLabel("0", SwingConstants.CENTER) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Only draw if count > 0
                int count = 0;
                try {
                    count = Integer.parseInt(getText());
                } catch (NumberFormatException e) {
                    count = 0;
                }
                
                if (count > 0) {
                    // Draw red circle background
                    g2d.setColor(BADGE_RED);
                    g2d.fillOval(0, 0, getWidth(), getHeight());
                }
                
                super.paintComponent(g);
            }
        };
        lblCartCount.setBounds(830, 20, 28, 28);
        lblCartCount.setFont(new Font("Arial", Font.BOLD, 12));
        lblCartCount.setForeground(Color.WHITE);
        lblCartCount.setOpaque(false);
        topBar.add(lblCartCount);
        
        add(topBar);
        
        // Menu title section
        JLabel lblMenuTitle = new JLabel("Our Menu", SwingConstants.CENTER);
        lblMenuTitle.setFont(new Font("Arial", Font.BOLD, 34));
        lblMenuTitle.setForeground(DARK_BROWN);
        lblMenuTitle.setBounds(0, 105, 1100, 45);
        add(lblMenuTitle);
        
        // Decorative coffee bean line
        JPanel decorLine = new JPanel();
        decorLine.setBackground(COFFEE_BROWN);
        decorLine.setBounds(460, 155, 180, 3);
        add(decorLine);
        
        // Initialize order tracking
        activeOrders = new ArrayList<>();
        createOrderTrackingPanel();
        
        // Menu scroll pane
        menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(0, 3, 20, 20));
        menuPanel.setBackground(CREAM);
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JScrollPane scrollPane = new JScrollPane(menuPanel);
        scrollPane.setBounds(30, 175, 1040, 485);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setName("menuScrollPane");
        add(scrollPane);
        
        displayMenuItems();
        
        // Bottom bar with cart info
        JPanel bottomBar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                GradientPaint gp = new GradientPaint(0, 0, WHITE_SMOKE, 
                                                     0, getHeight(), CREAM);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        bottomBar.setBounds(0, 665, 1100, 85);
        bottomBar.setLayout(null);
        bottomBar.setBorder(BorderFactory.createMatteBorder(2, 0, 0, 0, LIGHT_COFFEE));
        
        // Cart icon and total
        JLabel lblCartIcon = new JLabel("🛒");
        lblCartIcon.setFont(new Font("Arial", Font.PLAIN, 30));
        lblCartIcon.setBounds(40, 25, 40, 35);
        bottomBar.add(lblCartIcon);
        
        lblCartTotal = new JLabel("Cart Total: ₹0.00");
        lblCartTotal.setFont(new Font("Arial", Font.BOLD, 22));
        lblCartTotal.setForeground(DARK_BROWN);
        lblCartTotal.setBounds(85, 28, 300, 30);
        bottomBar.add(lblCartTotal);
        
        // Promo info
        JLabel lblPromo = new JLabel("🎁 Free delivery on orders above ₹500");
        lblPromo.setFont(new Font("Arial", Font.PLAIN, 13));
        lblPromo.setForeground(COFFEE_BROWN);
        lblPromo.setBounds(820, 32, 270, 25);
        bottomBar.add(lblPromo);
        
        add(bottomBar);
    }
    
    private void createOrderTrackingPanel() {
        orderTrackingPanel = new JPanel();
        orderTrackingPanel.setLayout(null);
        orderTrackingPanel.setBackground(new Color(255, 248, 230));
        orderTrackingPanel.setBounds(30, 170, 1040, 0);
        orderTrackingPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        add(orderTrackingPanel);
        orderTrackingPanel.setVisible(false);
    }
    
    public void addActiveOrder(int orderId) {
        if (!activeOrders.contains(orderId)) {
            activeOrders.add(orderId);
            updateOrderTracking();
            
            if (orderRefreshTimer == null) {
                orderRefreshTimer = new Timer(1000, e -> updateOrderTracking());
                orderRefreshTimer.start();
            }
        }
    }
    
    private void updateOrderTracking() {
        if (activeOrders == null || activeOrders.isEmpty()) {
            if (orderTrackingPanel != null) {
                orderTrackingPanel.setVisible(false);
                orderTrackingPanel.setBounds(30, 170, 1040, 0);
                
                Component[] components = getContentPane().getComponents();
                for (Component comp : components) {
                    if (comp instanceof JScrollPane && "menuScrollPane".equals(comp.getName())) {
                        comp.setBounds(30, 175, 1040, 485);
                        break;
                    }
                }
            }
            return;
        }
        
        orderTrackingPanel.removeAll();
        orderTrackingPanel.setVisible(true);
        
        JLabel lblTrackingTitle = new JLabel("📦 Active Orders");
        lblTrackingTitle.setFont(new Font("Arial", Font.BOLD, 18));
        lblTrackingTitle.setForeground(DARK_BROWN);
        lblTrackingTitle.setBounds(15, 10, 200, 25);
        orderTrackingPanel.add(lblTrackingTitle);
        
        java.util.List<Integer> completedOrders = new ArrayList<>();
        int yPos = 45;
        
        for (Integer orderId : activeOrders) {
            String status = OrderProcessor.getOrderStatus(orderId);
            
            if (status.equals("COMPLETED") || status.equals("READY")) {
                completedOrders.add(orderId);
            }
            
            JPanel orderCard = createOrderStatusCard(orderId, status);
            orderCard.setBounds(15, yPos, 1000, 70);
            orderTrackingPanel.add(orderCard);
            
            yPos += 80;
        }
        
        for (Integer completedOrder : completedOrders) {
            Timer removeTimer = new Timer(10000, e -> {
                activeOrders.remove(completedOrder);
                updateOrderTracking();
                if (activeOrders.isEmpty() && orderRefreshTimer != null) {
                    orderRefreshTimer.stop();
                    orderRefreshTimer = null;
                }
            });
            removeTimer.setRepeats(false);
            removeTimer.start();
        }
        
        int panelHeight = Math.max(100, yPos + 15);
        orderTrackingPanel.setPreferredSize(new Dimension(1040, panelHeight));
        orderTrackingPanel.setBounds(30, 170, 1040, panelHeight);
        
        int menuScrollYPos = 170 + panelHeight + 10;
        int menuScrollHeight = 665 - menuScrollYPos;
        
        Component[] components = getContentPane().getComponents();
        for (Component comp : components) {
            if (comp instanceof JScrollPane && "menuScrollPane".equals(comp.getName())) {
                comp.setBounds(30, menuScrollYPos, 1040, menuScrollHeight);
                break;
            }
        }
        
        orderTrackingPanel.revalidate();
        orderTrackingPanel.repaint();
        revalidate();
        repaint();
    }
    
    private JPanel createOrderStatusCard(int orderId, String status) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        card.setLayout(null);
        card.setBackground(SOFT_WHITE);
        card.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
        card.setOpaque(false);
        
        JLabel lblOrderId = new JLabel("Order #" + String.format("%04d", orderId));
        lblOrderId.setFont(new Font("Arial", Font.BOLD, 18));
        lblOrderId.setForeground(DARK_BROWN);
        lblOrderId.setBounds(20, 15, 150, 25);
        card.add(lblOrderId);
        
        JLabel lblStatusDot = new JLabel("●");
        lblStatusDot.setBounds(20, 42, 15, 15);
        lblStatusDot.setFont(new Font("Arial", Font.PLAIN, 16));
        
        JLabel lblStatus = new JLabel();
        lblStatus.setBounds(40, 40, 600, 20);
        lblStatus.setFont(new Font("Arial", Font.PLAIN, 14));
        
        if (status.equals("READY")) {
            lblStatusDot.setForeground(new Color(76, 175, 80));
            lblStatus.setText("✓ Your order is ready for pickup!");
            lblStatus.setForeground(new Color(76, 175, 80));
        } else if (status.contains("PREPARING")) {
            lblStatusDot.setForeground(new Color(255, 152, 0));
            lblStatus.setText("🔥 Preparing your order... " + status.substring(status.indexOf("(")));
            lblStatus.setForeground(COFFEE_BROWN);
        } else {
            lblStatusDot.setForeground(LIGHT_COFFEE);
            lblStatus.setText("⏳ Order received and processing...");
            lblStatus.setForeground(COFFEE_BROWN);
        }
        
        card.add(lblStatusDot);
        card.add(lblStatus);
        
        // View Receipt button
        JButton btnDetails = new JButton("View Receipt") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        btnDetails.setBounds(850, 15, 130, 40);
        btnDetails.setFont(new Font("Arial", Font.BOLD, 13));
        btnDetails.setBackground(COFFEE_BROWN);
        btnDetails.setForeground(WHITE_SMOKE);
        btnDetails.setFocusPainted(false);
        btnDetails.setContentAreaFilled(false);
        btnDetails.setBorderPainted(false);
        btnDetails.setOpaque(false);
        btnDetails.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDetails.addActionListener(e -> {
            new ReceiptFrame(currentUser, orderId).setVisible(true);
        });
        
        btnDetails.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnDetails.setBackground(DARK_BROWN);
                btnDetails.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnDetails.setBackground(COFFEE_BROWN);
                btnDetails.repaint();
            }
        });
        
        card.add(btnDetails);
        
        return card;
    }
    
    private void displayMenuItems() {
        menuPanel.removeAll();
        
        if (menuItems.isEmpty()) {
            JLabel lblNoItems = new JLabel("No menu items available", SwingConstants.CENTER);
            lblNoItems.setFont(new Font("Arial", Font.PLAIN, 18));
            lblNoItems.setForeground(COFFEE_BROWN);
            menuPanel.add(lblNoItems);
        } else {
            for (MenuItem item : menuItems) {
                menuPanel.add(createMenuItemCard(item));
            }
        }
        
        menuPanel.revalidate();
        menuPanel.repaint();
    }
    
    private JPanel createMenuItemCard(MenuItem item) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        card.setLayout(null);
        card.setBackground(SOFT_WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        card.setPreferredSize(new Dimension(320, 260));
        card.setOpaque(false);
        
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COFFEE_BROWN, 2),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_COFFEE, 1),
                    BorderFactory.createEmptyBorder(12, 12, 12, 12)
                ));
            }
        });
        
        // Item image 
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        imagePanel.setBounds(8, 8, 300, 95);
        imagePanel.setLayout(new BorderLayout());
        imagePanel.setBackground(CREAM);
        imagePanel.setOpaque(false);
        
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setVerticalAlignment(SwingConstants.CENTER);

        try {
            // Try to load from JAR resources
            String imagePath = "/images/" + item.getImagePath();
            java.net.URL imageURL = getClass().getResource(imagePath);
            
            if (imageURL != null) {
                ImageIcon icon = new ImageIcon(imageURL);
                Image img = icon.getImage().getScaledInstance(300, 95, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
            } else {
                // Fallback to emoji if image not found
                lblImage.setText(getCategoryEmoji(item.getCategory(), item.getName()));
                lblImage.setFont(new Font("Arial", Font.PLAIN, 55));
            }
        } catch (Exception e) {
            // Fallback to emoji on any error
            lblImage.setText(getCategoryEmoji(item.getCategory(), item.getName()));
            lblImage.setFont(new Font("Arial", Font.PLAIN, 55));
        }
        
        imagePanel.add(lblImage);
        card.add(imagePanel);
        
        // Item name
        JLabel lblName = new JLabel(item.getName(), SwingConstants.CENTER);
        lblName.setBounds(8, 110, 300, 26);
        lblName.setFont(new Font("Arial", Font.BOLD, 17));
        lblName.setForeground(DARK_BROWN);
        card.add(lblName);
        
        // Item DESCRIPTION (from database)
        String description = item.getDescription();
        if (description != null && !description.isEmpty()) {
            if (description.length() > 40) {
                description = description.substring(0, 37) + "...";
            }
        } else {
            description = "Delicious item";
        }
        
        JLabel lblDescription = new JLabel(description, SwingConstants.CENTER);
        lblDescription.setBounds(8, 138, 300, 18);
        lblDescription.setFont(new Font("Arial", Font.PLAIN, 11));
        lblDescription.setForeground(new Color(120, 120, 120));
        card.add(lblDescription);
        
        // Category badge
        JLabel lblCategory = new JLabel(item.getCategory(), SwingConstants.CENTER);
        lblCategory.setBounds(110, 160, 100, 20);
        lblCategory.setFont(new Font("Arial", Font.PLAIN, 10));
        lblCategory.setForeground(COFFEE_BROWN);
        lblCategory.setOpaque(true);
        lblCategory.setBackground(new Color(245, 237, 220));
        lblCategory.setBorder(BorderFactory.createEmptyBorder(3, 8, 3, 8));
        card.add(lblCategory);
        
        // Price
        JLabel lblPrice = new JLabel("₹" + String.format("%.2f", item.getPrice()));
        lblPrice.setBounds(15, 188, 100, 30);
        lblPrice.setFont(new Font("Arial", Font.BOLD, 20));
        lblPrice.setForeground(COFFEE_BROWN);
        card.add(lblPrice);
        
        // Control panel
        JPanel controlPanel = new JPanel();
        controlPanel.setLayout(null);
        controlPanel.setBounds(130, 188, 175, 40);
        controlPanel.setBackground(Color.WHITE);
        controlPanel.setOpaque(false);
        
        boolean inCart = cart.containsKey(item.getItemId());
        int currentQty = inCart ? cart.get(item.getItemId()).getQuantity() : 0;
        
        if (inCart) {
            JButton btnMinus = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(WHITE_SMOKE);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2d.setColor(LIGHT_COFFEE);
                    g2d.setStroke(new BasicStroke(2));
                    g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 15, 15);
                    g2d.setColor(COFFEE_BROWN);
                    g2d.setStroke(new BasicStroke(3));
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    g2d.drawLine(centerX - 8, centerY, centerX + 8, centerY);
                }
            };
            btnMinus.setBounds(0, 0, 50, 40);
            btnMinus.setFocusPainted(false);
            btnMinus.setContentAreaFilled(false);
            btnMinus.setBorderPainted(false);
            btnMinus.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnMinus.addActionListener(e -> {
                removeFromCart(item);
                menuPanel.removeAll();
                displayMenuItems();
            });
            controlPanel.add(btnMinus);
            
            // Quantity
            JLabel lblQty = new JLabel(String.valueOf(currentQty), SwingConstants.CENTER);
            lblQty.setBounds(55, 0, 65, 40);
            lblQty.setFont(new Font("Arial", Font.BOLD, 19));
            lblQty.setOpaque(true);
            lblQty.setBackground(CREAM);
            lblQty.setForeground(DARK_BROWN);
            lblQty.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, LIGHT_COFFEE));
            controlPanel.add(lblQty);
            
            JButton btnPlus = new JButton() {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(COFFEE_BROWN);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                    g2d.setColor(WHITE_SMOKE);
                    g2d.setStroke(new BasicStroke(3));
                    int centerX = getWidth() / 2;
                    int centerY = getHeight() / 2;
                    g2d.drawLine(centerX - 8, centerY, centerX + 8, centerY);
                    g2d.drawLine(centerX, centerY - 8, centerX, centerY + 8);
                }
            };
            btnPlus.setBounds(125, 0, 50, 40);
            btnPlus.setFocusPainted(false);
            btnPlus.setContentAreaFilled(false);
            btnPlus.setBorderPainted(false);
            btnPlus.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnPlus.addActionListener(e -> {
                addToCart(item);
                menuPanel.removeAll();
                displayMenuItems();
            });
            controlPanel.add(btnPlus);
            
        } else {
            JButton btnAdd = new JButton("+ Add") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setColor(getBackground());
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                    super.paintComponent(g);
                }
            };
            btnAdd.setBounds(0, 0, 175, 40);
            btnAdd.setFont(new Font("Arial", Font.BOLD, 15));
            btnAdd.setBackground(COFFEE_BROWN);
            btnAdd.setForeground(WHITE_SMOKE);
            btnAdd.setFocusPainted(false);
            btnAdd.setContentAreaFilled(false);
            btnAdd.setBorderPainted(false);
            btnAdd.setOpaque(false);
            btnAdd.setCursor(new Cursor(Cursor.HAND_CURSOR));
            
            btnAdd.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    btnAdd.setBackground(DARK_BROWN);
                    btnAdd.repaint();
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    btnAdd.setBackground(COFFEE_BROWN);
                    btnAdd.repaint();
                }
            });
            
            btnAdd.addActionListener(e -> {
                addToCart(item);
                menuPanel.removeAll();
                displayMenuItems();
            });
            controlPanel.add(btnAdd);
        }
        
        card.add(controlPanel);
        return card;
    }
    
    private String getCategoryEmoji(String category, String name) {
        String nameLower = name.toLowerCase();
        
        switch (category.toLowerCase()) {
            case "beverages":
                if (nameLower.contains("coffee")) return "☕";
                if (nameLower.contains("tea")) return "🍵";
                if (nameLower.contains("shake")) return "🥤";
                return "🍹";
            case "food":
                if (nameLower.contains("sandwich")) return "🥪";
                if (nameLower.contains("pasta")) return "🍝";
                if (nameLower.contains("pizza")) return "🍕";
                if (nameLower.contains("burger")) return "🍔";
                if (nameLower.contains("fries")) return "🍟";
                return "🍴";
            case "desserts":
                if (nameLower.contains("ice")) return "🍦";
                if (nameLower.contains("cake")) return "🍰";
                if (nameLower.contains("brownie")) return "🧁";
                if (nameLower.contains("waffle")) return "🧇";
                return "🍮";
            default:
                return "🍽️";
        }
    }
    
    private void addToCart(MenuItem item) {
        if (cart.containsKey(item.getItemId())) {
            CartItem cartItem = cart.get(item.getItemId());
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cart.put(item.getItemId(), new CartItem(item, 1));
        }
        updateCartDisplay();
    }
    
    private void removeFromCart(MenuItem item) {
        if (cart.containsKey(item.getItemId())) {
            CartItem cartItem = cart.get(item.getItemId());
            if (cartItem.getQuantity() > 1) {
                cartItem.setQuantity(cartItem.getQuantity() - 1);
            } else {
                cart.remove(item.getItemId());
            }
            updateCartDisplay();
        }
    }
    
    private void updateCartDisplay() {
        int totalItems = 0;
        double totalAmount = 0.0;
        
        for (CartItem item : cart.values()) {
            totalItems += item.getQuantity();
            totalAmount += item.getSubtotal();
        }
        
        lblCartCount.setText(String.valueOf(totalItems));
        lblCartTotal.setText("Cart Total: ₹" + String.format("%.2f", totalAmount));
    }
    
    private void openCart() {
        if (cart.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Your cart is empty!\nAdd items from the menu.",
                "Empty Cart", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        this.setVisible(false);
        new CartFrame(currentUser, new ArrayList<>(cart.values()), this).setVisible(true);
    }
    
    public void clearCart() {
        cart.clear();
        updateCartDisplay();
        menuPanel.removeAll();
        displayMenuItems();
    }
    
    private void openOrderHistory() {
        new OrderHistoryFrame(currentUser, this).setVisible(true);
    }
    
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout", JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (orderRefreshTimer != null) {
                orderRefreshTimer.stop();
            }
            this.dispose();
            new LoginFrame().setVisible(true);
        }
    }
    
    public void refreshCart(Map<Integer, CartItem> updatedCart) {
        this.cart = updatedCart;
        updateCartDisplay();
        menuPanel.removeAll();
        displayMenuItems();
    }
}
