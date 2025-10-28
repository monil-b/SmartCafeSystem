package views.owner;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import controllers.MenuController;
import controllers.OwnerController;
import models.*;
import models.MenuItem;

public class MenuManagementFrame extends JFrame {
    
    // Coffee theme colors
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
    private List<MenuItem> menuItems;
    
    public MenuManagementFrame(Owner owner, OwnerDashboardFrame dashboard) {
        this.currentOwner = owner;
        this.dashboardFrame = dashboard;
        
        setTitle("SmartCafe - Menu Management");
        setSize(1100, 750);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CREAM);
        
        initComponents();
        loadMenuItems();
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
        
        JLabel lblIcon = new JLabel("🍽️️");
        lblIcon.setFont(new Font("Arial", Font.PLAIN, 45));
        lblIcon.setBounds(30, 25, 55, 50);
        header.add(lblIcon);
        
        JLabel lblTitle = new JLabel("Menu Management");
        lblTitle.setFont(new Font("Arial", Font.BOLD, 32));
        lblTitle.setForeground(WHITE_SMOKE);
        lblTitle.setBounds(95, 20, 500, 38);
        header.add(lblTitle);
        
        JLabel lblSubtitle = new JLabel("Add, edit, and manage menu items");
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 15));
        lblSubtitle.setForeground(new Color(255, 255, 255, 220));
        lblSubtitle.setBounds(95, 60, 400, 22);
        header.add(lblSubtitle);
        
        add(header);
        
        // Menu table panel with Rounded Border
        JPanel menuPanel = new RoundedPanel(20, SOFT_WHITE, LIGHT_COFFEE);
        menuPanel.setBounds(40, 130, 1020, 490);
        menuPanel.setLayout(new BorderLayout(10, 10));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Table
        String[] columns = {"Item ID", "Name", "Category", "Price", "Prep Time", "Available", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        table = new JTable(tableModel);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(45);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 8));
        table.setBackground(SOFT_WHITE);
        table.setSelectionBackground(CREAM);
        table.setSelectionForeground(DARK_BROWN);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Header styling
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Arial", Font.BOLD, 13));
        tableHeader.setBackground(COFFEE_BROWN);
        tableHeader.setForeground(WHITE_SMOKE);
        tableHeader.setBorder(BorderFactory.createEmptyBorder());
        tableHeader.setPreferredSize(new Dimension(tableHeader.getWidth(), 40));
        tableHeader.setReorderingAllowed(false);
        
        // Column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(70);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(80);
        table.getColumnModel().getColumn(4).setPreferredWidth(90);
        table.getColumnModel().getColumn(5).setPreferredWidth(80);
        table.getColumnModel().getColumn(6).setPreferredWidth(250);
        
        // Available column renderer with better styling
        table.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                if (value.toString().equals("Yes")) {
                    c.setForeground(new Color(76, 175, 80));
                    setText("✓ Available");
                } else {
                    c.setForeground(new Color(244, 67, 54));
                    setText("✗ Unavailable");
                }
                
                if (isSelected) {
                    c.setBackground(CREAM);
                } else {
                    c.setBackground(SOFT_WHITE);
                }
                
                setHorizontalAlignment(CENTER);
                setFont(new Font("Arial", Font.BOLD, 12));
                return c;
            }
        });
        
        // Apply strikethrough style to unavailable items
        DefaultTableCellRenderer unavailableRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                // Check if item is unavailable (column 5)
                String availability = (String) table.getValueAt(row, 5);
                boolean isAvailable = availability.equals("Yes");
                
                if (!isAvailable) {
                    setForeground(new Color(150, 150, 150)); // Gray out unavailable items
                } else {
                    setForeground(DARK_BROWN);
                }
                
                if (isSelected) {
                    setBackground(CREAM);
                } else {
                    setBackground(SOFT_WHITE);
                }
                
                return this;
            }
        };
        
        // Apply to name and description columns
        table.getColumnModel().getColumn(1).setCellRenderer(unavailableRenderer);
        table.getColumnModel().getColumn(6).setCellRenderer(unavailableRenderer);
        
        // Center align ID column
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
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(SOFT_WHITE);
        menuPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(menuPanel);
        
        // Action buttons
        JButton btnAdd = createStyledButton("Add Item", 140, 645, 180, 50);
        btnAdd.addActionListener(e -> addMenuItem());
        add(btnAdd);
        
        JButton btnEdit = createStyledButton("Edit Item", 340, 645, 180, 50);
        btnEdit.addActionListener(e -> editMenuItem());
        add(btnEdit);
        
        JButton btnToggle = createStyledButton("Toggle Availability", 540, 645, 200, 50);
        btnToggle.addActionListener(e -> toggleAvailability());
        add(btnToggle);
        
        JButton btnBack = createStyledButton("Back", 760, 645, 180, 50);
        btnBack.addActionListener(e -> this.dispose());
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
                int tx = (getWidth() - textWidth) / 2;
                int ty = (getHeight() + textHeight) / 2 - 2;
                
                g.setColor(WHITE_SMOKE);
                g.setFont(getFont());
                g.drawString(getText(), tx, ty);
            }
        };
        
        btn.setBounds(x, y, width, height);
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(WHITE_SMOKE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
    
    private void loadMenuItems() {
        // Store current selection
        int selectedRow = table.getSelectedRow();
        int selectedItemId = -1;
        if (selectedRow >= 0 && menuItems != null && selectedRow < menuItems.size()) {
            selectedItemId = menuItems.get(selectedRow).getItemId();
        }
        
        tableModel.setRowCount(0);
        
        // IMPORTANT: Use getAllMenuItemsForManagement() to see ALL items
        try {
            menuItems = MenuController.getAllMenuItemsForManagement();
        } catch (Exception e) {
            // Fallback if method doesn't exist yet
            System.err.println("getAllMenuItemsForManagement() not found, using getAllMenuItems()");
            menuItems = MenuController.getAllMenuItems();
        }
        
        int newSelectedRow = -1;
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem item = menuItems.get(i);
            
            // Check if this was the previously selected item
            if (item.getItemId() == selectedItemId) {
                newSelectedRow = i;
            }
            
            tableModel.addRow(new Object[]{
                item.getItemId(),
                item.getName(),
                item.getCategory(),
                "₹" + String.format("%.2f", item.getPrice()),
                "N/A", // Prep Time not in current model
                item.isAvailable() ? "Yes" : "No",
                item.getDescription() != null ? item.getDescription().substring(0, Math.min(40, item.getDescription().length())) + "..." : "No description"
            });
        }
        
        System.out.println("Loaded " + menuItems.size() + " items in Menu Management");
        
        // Restore selection if the item still exists
        if (newSelectedRow >= 0) {
            table.setRowSelectionInterval(newSelectedRow, newSelectedRow);
        }
    }
    
    private void addMenuItem() {
        JDialog dialog = new JDialog(this, "Add Menu Item", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(CREAM);
        
        // Header Panel
        JPanel headerPanel = new JPanel() {
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
        headerPanel.setBounds(0, 0, 550, 60);
        headerPanel.setLayout(null);
        
        JLabel lblHeader = new JLabel("Add New Menu Item");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 22));
        lblHeader.setForeground(WHITE_SMOKE);
        lblHeader.setBounds(20, 15, 300, 30);
        headerPanel.add(lblHeader);
        
        dialog.add(headerPanel);
        
        // Input fields
        int yPos = 90;
        
        JLabel lblName = new JLabel("Item Name:");
        lblName.setBounds(40, yPos, 100, 25);
        lblName.setForeground(DARK_BROWN);
        lblName.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(lblName);
        JTextField txtName = new JTextField();
        txtName.setBounds(180, yPos, 320, 38);
        txtName.setFont(new Font("Arial", Font.PLAIN, 14));
        txtName.setBackground(WHITE_SMOKE);
        txtName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        dialog.add(txtName);
        yPos += 60;
        
        JLabel lblPrice = new JLabel("Price (₹):");
        lblPrice.setBounds(40, yPos, 100, 25);
        lblPrice.setForeground(DARK_BROWN);
        lblPrice.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(lblPrice);
        JTextField txtPrice = new JTextField();
        txtPrice.setBounds(180, yPos, 320, 38);
        txtPrice.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPrice.setBackground(WHITE_SMOKE);
        txtPrice.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        dialog.add(txtPrice);
        yPos += 60;
        
        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setBounds(40, yPos, 100, 25);
        lblCategory.setForeground(DARK_BROWN);
        lblCategory.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(lblCategory);
        String[] categories = {"Beverages", "Food", "Desserts"};
        JComboBox<String> cmbCategory = new JComboBox<>(categories);
        cmbCategory.setBounds(180, yPos, 320, 38);
        cmbCategory.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbCategory.setBackground(WHITE_SMOKE);
        dialog.add(cmbCategory);
        yPos += 60;
        
        JLabel lblPrepTime = new JLabel("Prep Time (sec):");
        lblPrepTime.setBounds(40, yPos, 130, 25);
        lblPrepTime.setForeground(DARK_BROWN);
        lblPrepTime.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(lblPrepTime);
        JTextField txtPrepTime = new JTextField("300");
        txtPrepTime.setBounds(180, yPos, 320, 38);
        txtPrepTime.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPrepTime.setBackground(WHITE_SMOKE);
        txtPrepTime.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        dialog.add(txtPrepTime);
        yPos += 60;
        
        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setBounds(40, yPos, 100, 25);
        lblDesc.setForeground(DARK_BROWN);
        lblDesc.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(lblDesc);
        JTextArea txtDesc = new JTextArea();
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setFont(new Font("Arial", Font.PLAIN, 14));
        txtDesc.setBackground(WHITE_SMOKE);
        txtDesc.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setBounds(180, yPos, 320, 90);
        scrollDesc.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
        dialog.add(scrollDesc);
        yPos += 110;
        
        JButton btnSave = createDialogButton("Save Item", 120, yPos, 150, 45);
        btnSave.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                String category = cmbCategory.getSelectedItem().toString();
                int prepTime = Integer.parseInt(txtPrepTime.getText().trim());
                String description = txtDesc.getText().trim();
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please enter item name!");
                    return;
                }
                
                boolean success = OwnerController.addMenuItem(name, price, category, 
                    name.toLowerCase().replace(" ", "") + ".jpg", description, prepTime);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Item added successfully!");
                    dialog.dispose();
                    loadMenuItems();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add item!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid price or prep time!");
            }
        });
        dialog.add(btnSave);
        
        JButton btnCancel = createDialogButton("Cancel", 290, yPos, 150, 45);
        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.add(btnCancel);
        
        dialog.setVisible(true);
    }
    
    private void editMenuItem() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit!");
            return;
        }
        
        MenuItem item = menuItems.get(selectedRow);
        
        JDialog dialog = new JDialog(this, "Edit Menu Item", true);
        dialog.setSize(550, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(null);
        dialog.getContentPane().setBackground(CREAM);
        
        // Header Panel
        JPanel headerPanel = new JPanel() {
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
        headerPanel.setBounds(0, 0, 550, 60);
        headerPanel.setLayout(null);
        
        JLabel lblHeader = new JLabel("Edit Menu Item");
        lblHeader.setFont(new Font("Arial", Font.BOLD, 22));
        lblHeader.setForeground(WHITE_SMOKE);
        lblHeader.setBounds(20, 15, 300, 30);
        headerPanel.add(lblHeader);
        
        dialog.add(headerPanel);
        
        int yPos = 90;
        
        JLabel lblName = new JLabel("Item Name:");
        lblName.setBounds(40, yPos, 100, 25);
        lblName.setForeground(DARK_BROWN);
        lblName.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(lblName);
        JTextField txtName = new JTextField(item.getName());
        txtName.setBounds(180, yPos, 320, 38);
        txtName.setFont(new Font("Arial", Font.PLAIN, 14));
        txtName.setBackground(WHITE_SMOKE);
        txtName.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        dialog.add(txtName);
        yPos += 60;
        
        JLabel lblPrice = new JLabel("Price (₹):");
        lblPrice.setBounds(40, yPos, 100, 25);
        lblPrice.setForeground(DARK_BROWN);
        lblPrice.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(lblPrice);
        JTextField txtPrice = new JTextField(String.valueOf(item.getPrice()));
        txtPrice.setBounds(180, yPos, 320, 38);
        txtPrice.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPrice.setBackground(WHITE_SMOKE);
        txtPrice.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        dialog.add(txtPrice);
        yPos += 60;
        
        JLabel lblCategory = new JLabel("Category:");
        lblCategory.setBounds(40, yPos, 100, 25);
        lblCategory.setForeground(DARK_BROWN);
        lblCategory.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(lblCategory);
        String[] categories = {"Beverages", "Food", "Desserts"};
        JComboBox<String> cmbCategory = new JComboBox<>(categories);
        cmbCategory.setSelectedItem(item.getCategory());
        cmbCategory.setBounds(180, yPos, 320, 38);
        cmbCategory.setFont(new Font("Arial", Font.PLAIN, 14));
        cmbCategory.setBackground(WHITE_SMOKE);
        dialog.add(cmbCategory);
        yPos += 60;
        
        JLabel lblPrepTime = new JLabel("Prep Time (sec):");
        lblPrepTime.setBounds(40, yPos, 130, 25);
        lblPrepTime.setForeground(DARK_BROWN);
        lblPrepTime.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(lblPrepTime);
        JTextField txtPrepTime = new JTextField("300");
        txtPrepTime.setBounds(180, yPos, 320, 38);
        txtPrepTime.setFont(new Font("Arial", Font.PLAIN, 14));
        txtPrepTime.setBackground(WHITE_SMOKE);
        txtPrepTime.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        dialog.add(txtPrepTime);
        yPos += 60;
        
        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setBounds(40, yPos, 100, 25);
        lblDesc.setForeground(DARK_BROWN);
        lblDesc.setFont(new Font("Arial", Font.BOLD, 14));
        dialog.add(lblDesc);
        JTextArea txtDesc = new JTextArea(item.getDescription());
        txtDesc.setLineWrap(true);
        txtDesc.setWrapStyleWord(true);
        txtDesc.setFont(new Font("Arial", Font.PLAIN, 14));
        txtDesc.setBackground(WHITE_SMOKE);
        txtDesc.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        JScrollPane scrollDesc = new JScrollPane(txtDesc);
        scrollDesc.setBounds(180, yPos, 320, 90);
        scrollDesc.setBorder(BorderFactory.createLineBorder(LIGHT_COFFEE, 2));
        dialog.add(scrollDesc);
        yPos += 110;
        
        JButton btnUpdate = createDialogButton("Update Item", 120, yPos, 150, 45);
        btnUpdate.addActionListener(e -> {
            try {
                String name = txtName.getText().trim();
                double price = Double.parseDouble(txtPrice.getText().trim());
                String category = cmbCategory.getSelectedItem().toString();
                int prepTime = Integer.parseInt(txtPrepTime.getText().trim());
                String description = txtDesc.getText().trim();
                
                boolean success = OwnerController.updateMenuItem(item.getItemId(), name, 
                    price, category, description, prepTime);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, "Item updated successfully!");
                    dialog.dispose();
                    loadMenuItems();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update item!");
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid price or prep time!");
            }
        });
        dialog.add(btnUpdate);
        
        JButton btnCancel = createDialogButton("Cancel", 290, yPos, 150, 45);
        btnCancel.addActionListener(e -> dialog.dispose());
        dialog.add(btnCancel);
        
        dialog.setVisible(true);
    }
    
    private JButton createDialogButton(String text, int x, int y, int width, int height) {
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
                
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
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
        btn.setFont(new Font("Arial", Font.BOLD, 14));
        btn.setForeground(WHITE_SMOKE);
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return btn;
    }
    
    private void toggleAvailability() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item!");
            return;
        }
        
        MenuItem item = menuItems.get(selectedRow);
        boolean newAvailability = !item.isAvailable();
        
        // Confirm action
        String message = newAvailability 
            ? "Make \"" + item.getName() + "\" available to customers?" 
            : "Hide \"" + item.getName() + "\" from customers?\n(You can re-enable it anytime)";
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            message,
            "Toggle Item Availability", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        boolean success = OwnerController.toggleItemAvailability(item.getItemId(), newAvailability);
        
        if (success) {
            String statusMessage = newAvailability 
                ? "✓ \"" + item.getName() + "\" is now AVAILABLE to customers!"
                : "✓ \"" + item.getName() + "\" is now HIDDEN from customers.\nYou can make it available again by selecting it and clicking Toggle Availability.";
            
            JOptionPane.showMessageDialog(this, 
                statusMessage,
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            loadMenuItems();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to update item availability!",
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}