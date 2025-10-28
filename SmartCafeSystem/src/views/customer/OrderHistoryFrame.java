package views.customer;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.List;
import controllers.OrderController;
import models.*;

public class OrderHistoryFrame extends JFrame {
    
    // Modern coffee theme colors
    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color SOFT_WHITE = new Color(255, 253, 250);
    
    private User currentUser;
    private MenuFrame menuFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Order> orders;
    
    public OrderHistoryFrame(User user, MenuFrame menu) {
        this.currentUser = user;
        this.menuFrame = menu;
        
        setTitle("SmartCafe - Your Orders");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CREAM);
        
        initComponents();
        loadOrders();
    }
    
    private void initComponents() {
        setLayout(null);
        
        // Header with gradient
        JPanel header = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                GradientPaint gp = new GradientPaint(0, 0, DARK_BROWN, 
                                                     getWidth(), 0, COFFEE_BROWN);
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        header.setBounds(0, 0, 1000, 100);
        header.setLayout(null);
        
        JLabel lblIcon = new JLabel("📋");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 45));
        lblIcon.setBounds(30, 25, 55, 50);
        header.add(lblIcon);
        
        JLabel lblTitle = new JLabel("Your Orders");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 34));
        lblTitle.setForeground(WHITE_SMOKE);
        lblTitle.setBounds(95, 24, 300, 42);
        header.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("View your order history");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(255, 255, 255, 220));
        lblSubtitle.setBounds(95, 66, 300, 22);
        header.add(lblSubtitle);
        
        add(header);
        
        // Orders panel with rounded corners
        JPanel ordersPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setColor(LIGHT_COFFEE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 20, 20);
            }
        };
        ordersPanel.setOpaque(false);
        ordersPanel.setBackground(SOFT_WHITE);
        ordersPanel.setBounds(40, 130, 920, 480);
        ordersPanel.setLayout(new BorderLayout(10, 10));
        ordersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel lblOrdersTitle = new JLabel("Order History");
        lblOrdersTitle.setFont(new Font("Arial", Font.BOLD, 22));
        lblOrdersTitle.setForeground(DARK_BROWN);
        lblOrdersTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        ordersPanel.add(lblOrdersTitle, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"Order ID", "Date", "Items", "Total", "Payment", "Status", "Action"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(52);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 10));
        table.setBackground(SOFT_WHITE);
        table.setSelectionBackground(CREAM);
        table.setSelectionForeground(DARK_BROWN);
        
        // Header styling
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));
        tableHeader.setBackground(COFFEE_BROWN);
        tableHeader.setForeground(WHITE_SMOKE);
        tableHeader.setBorder(BorderFactory.createEmptyBorder());
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 45));
        
        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(180);
        table.getColumnModel().getColumn(2).setPreferredWidth(80);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);
        
        // Custom renderer for status column
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                
                if (status.equals("READY")) {
                    c.setForeground(new Color(76, 175, 80));
                } else if (status.equals("PREPARING")) {
                    c.setForeground(new Color(255, 152, 0));
                } else if (status.equals("PENDING")) {
                    c.setForeground(COFFEE_BROWN);
                } else {
                    c.setForeground(DARK_BROWN);
                }
                
                if (isSelected) {
                    c.setBackground(CREAM);
                } else {
                    c.setBackground(SOFT_WHITE);
                }
                
                setHorizontalAlignment(CENTER);
                setFont(new Font("Arial", Font.BOLD, 13));
                return c;
            }
        });
        
        // Center align numeric columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SOFT_WHITE);
        ordersPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(ordersPanel);
        
        // Rounded button for View Receipt
        JButton btnViewReceipt = new RoundedButton("View Receipt", 18);
        btnViewReceipt.setBounds(300, 620, 180, 52);
        btnViewReceipt.setFont(new Font("Arial", Font.BOLD, 16));
        btnViewReceipt.setBackground(COFFEE_BROWN);
        btnViewReceipt.setForeground(WHITE_SMOKE);
        btnViewReceipt.setFocusPainted(false);
        btnViewReceipt.setBorderPainted(false);
        btnViewReceipt.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnViewReceipt.addActionListener(e -> viewReceipt());
        
        btnViewReceipt.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnViewReceipt.setBackground(DARK_BROWN);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnViewReceipt.setBackground(COFFEE_BROWN);
            }
        });
        
        add(btnViewReceipt);
        
        // Rounded button for Back
        JButton btnBack = new RoundedButton("← Back to Menu", 18);
        btnBack.setBounds(520, 620, 180, 52);
        btnBack.setFont(new Font("Arial", Font.BOLD, 16));
        btnBack.setBackground(SOFT_WHITE);
        btnBack.setForeground(COFFEE_BROWN);
        btnBack.setFocusPainted(false);
        btnBack.setBorderPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> backToMenu());
        
        btnBack.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btnBack.setBackground(CREAM);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btnBack.setBackground(SOFT_WHITE);
            }
        });
        
        add(btnBack);
    }
    
    private void loadOrders() {
        orders = OrderController.getUserOrders(currentUser.getUserId());
        tableModel.setRowCount(0);
        
        if (orders.isEmpty()) {
            // Show empty state
            String[] emptyRow = {"", "", "No orders yet", "", "", "", ""};
            tableModel.addRow(emptyRow);
        } else {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
            
            for (Order order : orders) {
                // Get order details to count items
                List<OrderDetail> details = OrderController.getOrderDetails(order.getOrderId());
                int totalItems = 0;
                for (OrderDetail detail : details) {
                    totalItems += detail.getQuantity();
                }
                
                tableModel.addRow(new Object[]{
                    "#" + String.format("%04d", order.getOrderId()),
                    dateFormat.format(order.getOrderDate()),
                    totalItems,
                    "₹" + String.format("%.2f", order.getTotalAmount()),
                    order.getPaymentMethod(),
                    order.getStatus(),
                    "View"
                });
            }
        }
    }
    
    private void viewReceipt() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to view receipt!",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        if (orders.isEmpty()) {
            return;
        }
        
        Order selectedOrder = orders.get(selectedRow);
        new ReceiptFrame(currentUser, selectedOrder.getOrderId()).setVisible(true);
    }
    
    private void backToMenu() {
        this.dispose();
        menuFrame.setVisible(true);
    }
    
    // Custom rounded button class
    class RoundedButton extends JButton {
        private int radius;
        
        public RoundedButton(String text, int radius) {
            super(text);
            this.radius = radius;
            setContentAreaFilled(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Paint shadow
            g2.setColor(new Color(0, 0, 0, 30));
            g2.fillRoundRect(2, 4, getWidth() - 4, getHeight() - 4, radius, radius);
            
            // Paint button background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth() - 2, getHeight() - 4, radius, radius);
            
            // Paint border if background is light
            if (getBackground().equals(SOFT_WHITE) || getBackground().equals(CREAM)) {
                g2.setColor(COFFEE_BROWN);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(1, 1, getWidth() - 4, getHeight() - 6, radius, radius);
            }
            
            g2.dispose();
            super.paintComponent(g);
        }
        
        @Override
        protected void paintBorder(Graphics g) {
            // Border is painted in paintComponent
        }
    }
}