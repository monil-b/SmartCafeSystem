package views.customer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import models.*;

public class CartFrame extends JFrame {
    
    // Modern coffee theme colors
    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color SOFT_WHITE = new Color(255, 253, 250);
    
    private User currentUser;
    private java.util.List<CartItem> cartItems;
    private MenuFrame menuFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel lblTotal;
    private JLabel lblSubtotal;
    private JLabel lblTax;
    
    public CartFrame(User user, java.util.List<CartItem> items, MenuFrame menu) {
        this.currentUser = user;
        this.cartItems = items;
        this.menuFrame = menu;
        
        setTitle("SmartCafe - Shopping Cart");
        setSize(1000, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CREAM);
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(null);
        
        // Header with coffee gradient
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
        header.setBounds(0, 0, 1000, 110);
        header.setLayout(null);
        
        JLabel lblIcon = new JLabel("🛒");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 55));
        lblIcon.setBounds(40, 25, 70, 60);
        header.add(lblIcon);
        
        JLabel lblTitle = new JLabel("Your Shopping Cart");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 38));
        lblTitle.setForeground(WHITE_SMOKE);
        lblTitle.setBounds(120, 22, 500, 42);
        header.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Review your items before checkout");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtitle.setForeground(new Color(255, 255, 255, 220));
        lblSubtitle.setBounds(120, 65, 450, 25);
        header.add(lblSubtitle);
        
        // Item count badge - ROUNDED
        int totalItems = 0;
        for (CartItem item : cartItems) {
            totalItems += item.getQuantity();
        }
        
        JLabel lblItemCount = new JLabel(totalItems + " items") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                super.paintComponent(g);
            }
        };
        lblItemCount.setFont(new Font("Arial", Font.BOLD, 16));
        lblItemCount.setForeground(WHITE_SMOKE);
        lblItemCount.setOpaque(false);
        lblItemCount.setBackground(new Color(180, 60, 50));
        lblItemCount.setBounds(820, 37, 130, 38);
        lblItemCount.setHorizontalAlignment(SwingConstants.CENTER);
        header.add(lblItemCount);
        
        add(header);
        
        // Main content area
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(CREAM);
        contentPanel.setLayout(null);
        contentPanel.setBounds(0, 110, 1000, 640);
        
        // Left side - Cart items with ROUNDED corners
        JPanel cartItemsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        cartItemsPanel.setBackground(SOFT_WHITE);
        cartItemsPanel.setBounds(30, 25, 600, 480);
        cartItemsPanel.setLayout(new BorderLayout(10, 10));
        cartItemsPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        cartItemsPanel.setOpaque(false);
        
        JLabel lblItemsTitle = new JLabel("Cart Items (" + cartItems.size() + ")");
        lblItemsTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblItemsTitle.setForeground(DARK_BROWN);
        lblItemsTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        cartItemsPanel.add(lblItemsTitle, BorderLayout.NORTH);
        
        // Create custom item cards
        JPanel itemsContainer = new JPanel();
        itemsContainer.setLayout(new BoxLayout(itemsContainer, BoxLayout.Y_AXIS));
        itemsContainer.setBackground(SOFT_WHITE);
        
        for (int i = 0; i < cartItems.size(); i++) {
            CartItem item = cartItems.get(i);
            JPanel itemCard = createCartItemCard(item, i);
            itemsContainer.add(itemCard);
            if (i < cartItems.size() - 1) {
                itemsContainer.add(Box.createRigidArea(new Dimension(0, 12)));
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(itemsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        cartItemsPanel.add(scrollPane, BorderLayout.CENTER);
        
        contentPanel.add(cartItemsPanel);
        
        // Right side - Order summary with ROUNDED corners
        JPanel summaryPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
            }
        };
        summaryPanel.setBackground(SOFT_WHITE);
        summaryPanel.setBounds(650, 25, 320, 420);
        summaryPanel.setLayout(null);
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        summaryPanel.setOpaque(false);
        
        JLabel lblSummaryTitle = new JLabel("Order Summary");
        lblSummaryTitle.setFont(new Font("Arial", Font.BOLD, 24));
        lblSummaryTitle.setForeground(DARK_BROWN);
        lblSummaryTitle.setBounds(15, 15, 280, 30);
        summaryPanel.add(lblSummaryTitle);
        
        JSeparator sep1 = new JSeparator();
        sep1.setBounds(15, 55, 280, 2);
        sep1.setForeground(LIGHT_COFFEE);
        summaryPanel.add(sep1);
        
        // Subtotal
        JLabel lblSubtotalLabel = new JLabel("Subtotal:");
        lblSubtotalLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        lblSubtotalLabel.setForeground(COFFEE_BROWN);
        lblSubtotalLabel.setBounds(15, 75, 150, 25);
        summaryPanel.add(lblSubtotalLabel);
        
        lblSubtotal = new JLabel("₹0.00");
        lblSubtotal.setFont(new Font("Arial", Font.BOLD, 17));
        lblSubtotal.setForeground(DARK_BROWN);
        lblSubtotal.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSubtotal.setBounds(165, 75, 130, 25);
        summaryPanel.add(lblSubtotal);
        
        // Tax
        JLabel lblTaxLabel = new JLabel("Tax (GST 5%):");
        lblTaxLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        lblTaxLabel.setForeground(COFFEE_BROWN);
        lblTaxLabel.setBounds(15, 105, 150, 25);
        summaryPanel.add(lblTaxLabel);
        
        lblTax = new JLabel("₹0.00");
        lblTax.setFont(new Font("Arial", Font.BOLD, 17));
        lblTax.setForeground(DARK_BROWN);
        lblTax.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTax.setBounds(165, 105, 130, 25);
        summaryPanel.add(lblTax);
        
        // Delivery
        JLabel lblDeliveryLabel = new JLabel("Delivery:");
        lblDeliveryLabel.setFont(new Font("Arial", Font.PLAIN, 17));
        lblDeliveryLabel.setForeground(COFFEE_BROWN);
        lblDeliveryLabel.setBounds(15, 135, 150, 25);
        summaryPanel.add(lblDeliveryLabel);
        
        JLabel lblDelivery = new JLabel("FREE");
        lblDelivery.setFont(new Font("Arial", Font.BOLD, 17));
        lblDelivery.setForeground(new Color(76, 175, 80));
        lblDelivery.setHorizontalAlignment(SwingConstants.RIGHT);
        lblDelivery.setBounds(165, 135, 130, 25);
        summaryPanel.add(lblDelivery);
        
        JSeparator sep2 = new JSeparator();
        sep2.setBounds(15, 175, 280, 2);
        sep2.setForeground(LIGHT_COFFEE);
        summaryPanel.add(sep2);
        
        // Total
        JLabel lblTotalLabel = new JLabel("Total:");
        lblTotalLabel.setFont(new Font("Arial", Font.BOLD, 22));
        lblTotalLabel.setForeground(DARK_BROWN);
        lblTotalLabel.setBounds(15, 195, 150, 30);
        summaryPanel.add(lblTotalLabel);
        
        lblTotal = new JLabel("₹0.00");
        lblTotal.setFont(new Font("Arial", Font.BOLD, 30));
        lblTotal.setForeground(COFFEE_BROWN);
        lblTotal.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTotal.setBounds(115, 190, 180, 35);
        summaryPanel.add(lblTotal);
        
        updateTotal();
        
        // Checkout button - ROUNDED
        JButton btnCheckout = new JButton("Proceed to Checkout") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                super.paintComponent(g);
            }
        };
        btnCheckout.setBounds(15, 330, 280, 60);
        btnCheckout.setFont(new Font("Arial", Font.BOLD, 19));
        btnCheckout.setBackground(COFFEE_BROWN);
        btnCheckout.setForeground(WHITE_SMOKE);
        btnCheckout.setFocusPainted(false);
        btnCheckout.setContentAreaFilled(false);
        btnCheckout.setBorderPainted(false);
        btnCheckout.setOpaque(false);
        btnCheckout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCheckout.addActionListener(e -> proceedToPayment());
        
        btnCheckout.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnCheckout.setBackground(DARK_BROWN);
                btnCheckout.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnCheckout.setBackground(COFFEE_BROWN);
                btnCheckout.repaint();
            }
        });
        
        summaryPanel.add(btnCheckout);
        
        contentPanel.add(summaryPanel);
        
        // Bottom buttons - ROUNDED
        JButton btnContinue = new JButton("← Continue Shopping") {
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
        btnContinue.setBounds(30, 520, 280, 50);
        btnContinue.setFont(new Font("Arial", Font.BOLD, 16));
        btnContinue.setBackground(SOFT_WHITE);
        btnContinue.setForeground(COFFEE_BROWN);
        btnContinue.setFocusPainted(false);
        btnContinue.setContentAreaFilled(false);
        btnContinue.setBorderPainted(false);
        btnContinue.setOpaque(false);
        btnContinue.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnContinue.addActionListener(e -> backToMenu());
        
        btnContinue.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnContinue.setBackground(CREAM);
                btnContinue.repaint();
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnContinue.setBackground(SOFT_WHITE);
                btnContinue.repaint();
            }
        });
        
        contentPanel.add(btnContinue);
        
        // Security info - ROUNDED
        JPanel securityPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
            }
        };
        securityPanel.setBackground(new Color(255, 248, 230));
        securityPanel.setBounds(650, 465, 320, 65);
        securityPanel.setLayout(null);
        securityPanel.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
        securityPanel.setOpaque(false);
        
        JLabel lblSecurityIcon = new JLabel("🔒");
        lblSecurityIcon.setFont(new Font("Arial", Font.PLAIN, 26));
        lblSecurityIcon.setBounds(15, 17, 35, 30);
        securityPanel.add(lblSecurityIcon);
        
        JLabel lblSecurity = new JLabel("<html>Secure checkout<br/>Your data is protected</html>");
        lblSecurity.setFont(new Font("Arial", Font.PLAIN, 13));
        lblSecurity.setForeground(DARK_BROWN);
        lblSecurity.setBounds(60, 12, 240, 40);
        securityPanel.add(lblSecurity);
        
        contentPanel.add(securityPanel);
        
        add(contentPanel);
    }
    
    private JPanel createCartItemCard(CartItem item, int index) {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
            }
        };
        card.setLayout(null);
        card.setBackground(CREAM);
        card.setPreferredSize(new Dimension(540, 120));
        card.setMaximumSize(new Dimension(540, 120));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setOpaque(false);
        
        // Item image with ACTUAL IMAGE from resources (ROUNDED)
        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
            }
        };
        imagePanel.setBounds(10, 15, 90, 90);
        imagePanel.setLayout(new BorderLayout());
        imagePanel.setBackground(WHITE_SMOKE);
        imagePanel.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
        imagePanel.setOpaque(false);
        
        JLabel lblImage = new JLabel();
        lblImage.setHorizontalAlignment(SwingConstants.CENTER);
        lblImage.setVerticalAlignment(SwingConstants.CENTER);
        
        // Try to load actual image
        try {
            String imagePath = "/images/" + item.getMenuItem().getImagePath();
            java.net.URL imageURL = getClass().getResource(imagePath);
            
            if (imageURL != null) {
                ImageIcon icon = new ImageIcon(imageURL);
                Image img = icon.getImage().getScaledInstance(90, 90, Image.SCALE_SMOOTH);
                lblImage.setIcon(new ImageIcon(img));
            } else {
                // Fallback to emoji
                lblImage.setText(getCategoryEmoji(item.getMenuItem().getCategory(), 
                                                   item.getMenuItem().getName()));
                lblImage.setFont(new Font("Arial", Font.PLAIN, 50));
            }
        } catch (Exception e) {
            lblImage.setText(getCategoryEmoji(item.getMenuItem().getCategory(), 
                                               item.getMenuItem().getName()));
            lblImage.setFont(new Font("Arial", Font.PLAIN, 50));
        }
        
        imagePanel.add(lblImage);
        card.add(imagePanel);
        
        // Item name
        JLabel lblName = new JLabel(item.getMenuItem().getName());
        lblName.setFont(new Font("Arial", Font.BOLD, 18));
        lblName.setForeground(DARK_BROWN);
        lblName.setBounds(115, 18, 260, 28);
        card.add(lblName);
        
        // Item category badge - ROUNDED
        JLabel lblCategory = new JLabel(item.getMenuItem().getCategory()) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                super.paintComponent(g);
            }
        };
        lblCategory.setFont(new Font("Arial", Font.PLAIN, 11));
        lblCategory.setForeground(COFFEE_BROWN);
        lblCategory.setOpaque(false);
        lblCategory.setBackground(new Color(245, 237, 220));
        lblCategory.setBounds(115, 50, 90, 22);
        lblCategory.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(lblCategory);
        
        // Price per item
        JLabel lblPrice = new JLabel("₹" + String.format("%.2f", item.getMenuItem().getPrice()) + " each");
        lblPrice.setFont(new Font("Arial", Font.PLAIN, 14));
        lblPrice.setForeground(new Color(120, 120, 120));
        lblPrice.setBounds(115, 78, 150, 20);
        card.add(lblPrice);
        
        // Quantity controls - ROUNDED buttons
        JPanel qtyPanel = new JPanel();
        qtyPanel.setLayout(null);
        qtyPanel.setBounds(380, 30, 130, 42);
        qtyPanel.setOpaque(false);
        
        JButton btnMinus = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill background
                g2d.setColor(SOFT_WHITE);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Draw border
                g2d.setColor(LIGHT_COFFEE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth()-3, getHeight()-3, 12, 12);
                
                // Draw minus symbol
                g2d.setColor(COFFEE_BROWN);
                g2d.setStroke(new BasicStroke(3));
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                g2d.drawLine(centerX - 7, centerY, centerX + 7, centerY);
            }
        };
        btnMinus.setBounds(0, 0, 42, 42);
        btnMinus.setFocusPainted(false);
        btnMinus.setContentAreaFilled(false);
        btnMinus.setBorderPainted(false);
        btnMinus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMinus.addActionListener(e -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
            } else {
                cartItems.remove(index);
            }
            refreshDisplay();
        });
        qtyPanel.add(btnMinus);
        
        JLabel lblQty = new JLabel(String.valueOf(item.getQuantity()), SwingConstants.CENTER);
        lblQty.setBounds(46, 0, 38, 42);
        lblQty.setFont(new Font("Arial", Font.BOLD, 18));
        lblQty.setOpaque(true);
        lblQty.setBackground(SOFT_WHITE);
        lblQty.setForeground(DARK_BROWN);
        lblQty.setBorder(BorderFactory.createMatteBorder(2, 0, 2, 0, LIGHT_COFFEE));
        qtyPanel.add(lblQty);
        
        JButton btnPlus = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Fill background
                g2d.setColor(COFFEE_BROWN);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                
                // Draw plus symbol
                g2d.setColor(WHITE_SMOKE);
                g2d.setStroke(new BasicStroke(3));
                int centerX = getWidth() / 2;
                int centerY = getHeight() / 2;
                // Horizontal line
                g2d.drawLine(centerX - 7, centerY, centerX + 7, centerY);
                // Vertical line
                g2d.drawLine(centerX, centerY - 7, centerX, centerY + 7);
            }
        };
        btnPlus.setBounds(88, 0, 42, 42);
        btnPlus.setFocusPainted(false);
        btnPlus.setContentAreaFilled(false);
        btnPlus.setBorderPainted(false);
        btnPlus.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnPlus.addActionListener(e -> {
            item.setQuantity(item.getQuantity() + 1);
            refreshDisplay();
        });
        qtyPanel.add(btnPlus);
        
        card.add(qtyPanel);
        
        // Subtotal
        JLabel lblSubtotal = new JLabel("₹" + String.format("%.2f", item.getSubtotal()));
        lblSubtotal.setFont(new Font("Arial", Font.BOLD, 21));
        lblSubtotal.setForeground(DARK_BROWN);
        lblSubtotal.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSubtotal.setBounds(380, 80, 130, 28);
        card.add(lblSubtotal);
        
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
    
    private void refreshDisplay() {
        this.dispose();
        new CartFrame(currentUser, cartItems, menuFrame).setVisible(true);
        
        // Update menu frame cart
        Map<Integer, CartItem> updatedCart = new HashMap<>();
        for (CartItem item : cartItems) {
            updatedCart.put(item.getMenuItem().getItemId(), item);
        }
        menuFrame.refreshCart(updatedCart);
    }
    
    private void updateTotal() {
        double subtotal = 0.0;
        for (CartItem item : cartItems) {
            subtotal += item.getSubtotal();
        }
        
        double tax = subtotal * 0.05;
        double total = subtotal + tax;
        
        lblSubtotal.setText("₹" + String.format("%.2f", subtotal));
        lblTax.setText("₹" + String.format("%.2f", tax));
        lblTotal.setText("₹" + String.format("%.2f", total));
    }
    
    private void backToMenu() {
        this.dispose();
        menuFrame.setVisible(true);
    }
    
    private void proceedToPayment() {
        if (cartItems.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Your cart is empty!",
                "Empty Cart", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        this.dispose();
        new PaymentFrame(currentUser, cartItems, menuFrame).setVisible(true);
    }
}