package views.owner;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import controllers.OwnerController;
import controllers.OrderController;
import models.*;

public class OrderQueueFrame extends JFrame {
    
    // Coffee theme colors - EXACT FROM ORIGINAL FILE
    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color SOFT_WHITE = new Color(255, 253, 250);
    
    private Owner currentOwner;
    private OwnerDashboardFrame dashboardFrame;
    private JTable table;
    private DefaultTableModel tableModel;
    private List<Order> orders;
    private Timer refreshTimer;
    private JComboBox<String> filterCombo;
    
    public OrderQueueFrame(Owner owner, OwnerDashboardFrame dashboard) {
        this.currentOwner = owner;
        this.dashboardFrame = dashboard;
        
        setTitle("SmartCafe - Order Queue");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CREAM);
        
        initComponents();
        loadOrders("ALL");
        
        // Auto-refresh every 3 seconds
        refreshTimer = new Timer(3000, e -> loadOrders(filterCombo.getSelectedItem().toString()));
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
        
        JLabel lblIcon = new JLabel("📋");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 45));
        lblIcon.setBounds(30, 25, 55, 50);
        header.add(lblIcon);
        
        JLabel lblTitle = new JLabel("Order Queue Management");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setForeground(WHITE_SMOKE);
        lblTitle.setBounds(95, 20, 500, 38);
        header.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Monitor and manage all orders");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(255, 255, 255, 220));
        lblSubtitle.setBounds(95, 60, 400, 22);
        header.add(lblSubtitle);
        
        add(header);
        
        // Filter Panel with Rounded Border
        JPanel filterPanel = new RoundedPanel(20, SOFT_WHITE, LIGHT_COFFEE);
        filterPanel.setBounds(40, 120, 1020, 70);
        filterPanel.setLayout(null);
        
        JLabel lblFilter = new JLabel("Filter by Status:");
        lblFilter.setFont(new Font("Arial", Font.BOLD, 15));
        lblFilter.setForeground(DARK_BROWN);
        lblFilter.setBounds(25, 20, 130, 30);
        filterPanel.add(lblFilter);
        
        String[] statuses = {"ALL", "PENDING", "PREPARING", "READY", "COMPLETED"};
        filterCombo = new JComboBox<>(statuses);
        filterCombo.setBounds(155, 20, 150, 35);
        filterCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        filterCombo.setBackground(WHITE_SMOKE);
        filterCombo.addActionListener(e -> loadOrders(filterCombo.getSelectedItem().toString()));
        filterPanel.add(filterCombo);
        
        JButton btnRefresh = createStyledButton("Refresh", 840, 18, 160, 38);
        btnRefresh.addActionListener(e -> loadOrders(filterCombo.getSelectedItem().toString()));
        filterPanel.add(btnRefresh);
        
        add(filterPanel);
        
        // Orders Table Panel with Rounded Border
        JPanel ordersPanel = new RoundedPanel(20, SOFT_WHITE, LIGHT_COFFEE);
        ordersPanel.setBounds(40, 210, 1020, 420);
        ordersPanel.setLayout(new BorderLayout(10, 10));
        ordersPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Table
        String[] columns = {"Order ID", "Customer", "Date & Time", "Items", "Total", "Payment", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(48);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 8));
        table.setBackground(SOFT_WHITE);
        table.setSelectionBackground(CREAM);
        table.setSelectionForeground(DARK_BROWN);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Header styling
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 14));
        tableHeader.setBackground(COFFEE_BROWN);
        tableHeader.setForeground(WHITE_SMOKE);
        tableHeader.setBorder(BorderFactory.createEmptyBorder());
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 40));
        tableHeader.setReorderingAllowed(false);
        
        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(90);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(180);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(120);
        
        // Status column renderer
        table.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                String status = value.toString();
                
                switch (status) {
                    case "READY":
                        c.setForeground(new Color(76, 175, 80));
                        break;
                    case "PREPARING":
                        c.setForeground(new Color(255, 152, 0));
                        break;
                    case "PENDING":
                        c.setForeground(COFFEE_BROWN);
                        break;
                    case "COMPLETED":
                        c.setForeground(new Color(100, 100, 100));
                        break;
                    default:
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
        
        // Center align columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                setHorizontalAlignment(JLabel.CENTER);
                
                if (isSelected) {
                    setBackground(CREAM);
                    setForeground(DARK_BROWN);
                } else {
                    setBackground(SOFT_WHITE);
                    setForeground(DARK_BROWN);
                }
                return this;
            }
        };
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SOFT_WHITE);
        ordersPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(ordersPanel);
        
        // Action buttons
        JButton btnViewDetails = createStyledButton("View Details", 250, 650, 180, 50);
        btnViewDetails.addActionListener(e -> viewOrderDetails());
        add(btnViewDetails);
        
        JButton btnUpdateStatus = createStyledButton("Update Status", 450, 650, 180, 50);
        btnUpdateStatus.addActionListener(e -> updateOrderStatus());
        add(btnUpdateStatus);
        
        JButton btnBack = createStyledButton("Back", 650, 650, 180, 50);
        btnBack.addActionListener(e -> {
            refreshTimer.stop();
            this.dispose();
        });
        add(btnBack);
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
                
                // Paint text manually to avoid issues
                FontMetrics fm = g.getFontMetrics();
                int textWidth = fm.stringWidth(getText());
                int textHeight = fm.getAscent();
                int x = (getWidth() - textWidth) / 2;
                int y = (getHeight() + textHeight) / 2 - 2;
                
                g.setColor(WHITE_SMOKE);
                g.setFont(getFont());
                g.drawString(getText(), x, y);
            }
        };
        
        btn.setBounds(x, y, width, height);
        btn.setFont(new Font("Arial", Font.BOLD, 15));
        btn.setForeground(WHITE_SMOKE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
    
    private void loadOrders(String statusFilter) {
        // Store current selection
        int selectedRow = table.getSelectedRow();
        int selectedOrderId = -1;
        if (selectedRow >= 0 && selectedRow < orders.size()) {
            selectedOrderId = orders.get(selectedRow).getOrderId();
        }
        
        tableModel.setRowCount(0);
        
        if (statusFilter.equals("ALL")) {
            orders = OwnerController.getAllOrders();
        } else {
            orders = OwnerController.getOrdersByStatus(statusFilter);
        }
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy, hh:mm a");
        
        int newSelectedRow = -1;
        for (int i = 0; i < orders.size(); i++) {
            Order order = orders.get(i);
            
            // Check if this was the previously selected order
            if (order.getOrderId() == selectedOrderId) {
                newSelectedRow = i;
            }
            
            // Get order details to count items
            List<OrderDetail> details = OrderController.getOrderDetails(order.getOrderId());
            int totalItems = 0;
            for (OrderDetail detail : details) {
                totalItems += detail.getQuantity();
            }
            
            tableModel.addRow(new Object[]{
                "#" + String.format("%04d", order.getOrderId()),
                order.getUsername(),
                dateFormat.format(order.getOrderDate()),
                totalItems,
                "₹" + String.format("%.2f", order.getTotalAmount()),
                order.getPaymentMethod(),
                order.getStatus()
            });
        }
        
        // Restore selection if the order still exists
        if (newSelectedRow >= 0) {
            table.setRowSelectionInterval(newSelectedRow, newSelectedRow);
        }
    }
    
    private void viewOrderDetails() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to view details!",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Order selectedOrder = orders.get(selectedRow);
        
        // Create details dialog
        JDialog detailsDialog = new JDialog(this, "Order Details", true);
        detailsDialog.setSize(600, 500);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setLayout(new BorderLayout());
        
        JTextArea txtDetails = new JTextArea();
        txtDetails.setFont(new Font("Courier New", Font.PLAIN, 12));
        txtDetails.setEditable(false);
        txtDetails.setMargin(new Insets(10, 10, 10, 10));
        
        // Get order details
        StringBuilder details = new StringBuilder();
        details.append("═══════════════════════════════════════════════\n");
        details.append("           ORDER DETAILS\n");
        details.append("═══════════════════════════════════════════════\n\n");
        details.append(String.format("Order ID      : #%04d\n", selectedOrder.getOrderId()));
        details.append(String.format("Customer      : %s\n", selectedOrder.getUsername()));
        details.append(String.format("Status        : %s\n", selectedOrder.getStatus()));
        details.append(String.format("Payment       : %s\n", selectedOrder.getPaymentMethod()));
        details.append(String.format("Total Amount  : ₹%.2f\n\n", selectedOrder.getTotalAmount()));
        
        details.append("ITEMS:\n");
        details.append("───────────────────────────────────────────────\n");
        
        List<OrderDetail> orderDetails = OrderController.getOrderDetails(selectedOrder.getOrderId());
        for (OrderDetail detail : orderDetails) {
            details.append(String.format("• %s x%d - ₹%.2f\n", 
                detail.getItemName(), detail.getQuantity(), detail.getSubtotal()));
        }
        
        details.append("═══════════════════════════════════════════════\n");
        
        txtDetails.setText(details.toString());
        
        JScrollPane scrollPane = new JScrollPane(txtDetails);
        detailsDialog.add(scrollPane, BorderLayout.CENTER);
        
        JButton btnClose = new JButton("Close");
        btnClose.addActionListener(e -> detailsDialog.dispose());
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnClose);
        detailsDialog.add(btnPanel, BorderLayout.SOUTH);
        
        detailsDialog.setVisible(true);
    }
    
    private void updateOrderStatus() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Please select an order to update!",
                "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        Order selectedOrder = orders.get(selectedRow);
        
        String[] statuses = {"PENDING", "PREPARING", "READY", "COMPLETED"};
        String newStatus = (String) JOptionPane.showInputDialog(this,
            "Select new status for Order #" + selectedOrder.getOrderId(),
            "Update Order Status",
            JOptionPane.QUESTION_MESSAGE,
            null,
            statuses,
            selectedOrder.getStatus());
        
        if (newStatus != null && !newStatus.equals(selectedOrder.getStatus())) {
            boolean success = OrderController.updateOrderStatus(selectedOrder.getOrderId(), newStatus);
            
            if (success) {
                JOptionPane.showMessageDialog(this,
                    "Order status updated successfully!",
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                loadOrders(filterCombo.getSelectedItem().toString());
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to update order status!",
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}