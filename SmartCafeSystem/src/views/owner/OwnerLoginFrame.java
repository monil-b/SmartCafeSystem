package views.owner;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import controllers.OwnerController;
import models.Owner;

public class OwnerLoginFrame extends JFrame {
    
    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color ACCENT_COFFEE = new Color(160, 110, 80);
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private boolean passwordVisible = false;
    
    public OwnerLoginFrame() {
        setTitle("SmartCafe - Owner Login");
        setSize(550, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CREAM);
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(null);
        
        // Logo panel 
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillOval(5, 5, 100, 100);
                g2d.setColor(DARK_BROWN);
                g2d.fillOval(0, 0, 100, 100);
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillOval(20, 15, 40, 40);
            }
        };
        logoPanel.setBounds(225, 50, 105, 105);
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        
        JLabel lblCoffee = new JLabel("👨‍💼", SwingConstants.CENTER);
        lblCoffee.setFont(new Font("Arial", Font.PLAIN, 55));
        lblCoffee.setForeground(WHITE_SMOKE);
        logoPanel.add(lblCoffee);
        add(logoPanel);
        
        // Title
        JLabel lblTitle = new JLabel("Owner Access", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitle.setForeground(DARK_BROWN);
        lblTitle.setBounds(50, 175, 450, 45);
        add(lblTitle);
        
        // Subtitle
        JLabel lblSubtitle = new JLabel("Manage your cafe operations", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtitle.setForeground(ACCENT_COFFEE);
        lblSubtitle.setBounds(50, 225, 450, 25);
        add(lblSubtitle);
        
        // Username field
        JPanel usernamePanel = createModernInputPanel("Username", 280);
        txtUsername = (JTextField) usernamePanel.getComponent(1);
        add(usernamePanel);
        
        // Password field
        JPanel passwordPanel = createModernPasswordPanel("Password", 380);
        txtPassword = (JPasswordField) passwordPanel.getComponent(1);
        add(passwordPanel);
        
        // Login button
        btnLogin = createRoundedButton("Sign In as Owner", DARK_BROWN, WHITE_SMOKE);
        btnLogin.setBounds(100, 490, 350, 55);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.addActionListener(e -> handleLogin());
        add(btnLogin);
        
        // Back to customer login
        JButton btnBack = new JButton("← Back to Customer Login");
        btnBack.setBounds(150, 565, 250, 30);
        btnBack.setFont(new Font("Arial", Font.PLAIN, 14));
        btnBack.setForeground(COFFEE_BROWN);
        btnBack.setContentAreaFilled(false);
        btnBack.setBorderPainted(false);
        btnBack.setFocusPainted(false);
        btnBack.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBack.addActionListener(e -> backToCustomerLogin());
        
        btnBack.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnBack.setForeground(DARK_BROWN);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnBack.setForeground(COFFEE_BROWN);
            }
        });
        
        add(btnBack);
        
        // Enter key login
        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
    }
    
    private JButton createRoundedButton(String text, Color bgColor, Color fgColor) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 40));
                g2.fill(new RoundRectangle2D.Float(2, 4, getWidth()-2, getHeight()-2, 15, 15));
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-2, getHeight()-4, 15, 15));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        button.setBackground(bgColor);
        button.setForeground(fgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(COFFEE_BROWN);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private JPanel createModernInputPanel(String label, int yPos) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(100, yPos, 350, 85);
        panel.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(DARK_BROWN);
        lbl.setBounds(5, 0, 150, 25);
        panel.add(lbl);
        
        JTextField txt = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth()-2, getHeight()-2, 12, 12));
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-2, getHeight()-2, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txt.setBounds(0, 30, 350, 50);
        txt.setFont(new Font("Arial", Font.PLAIN, 16));
        txt.setBackground(WHITE_SMOKE);
        txt.setForeground(DARK_BROWN);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        txt.setOpaque(false);
        
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DARK_BROWN, 2),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });
        
        panel.add(txt);
        return panel;
    }
    
    private JPanel createModernPasswordPanel(String label, int yPos) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(100, yPos, 350, 85);
        panel.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 14));
        lbl.setForeground(DARK_BROWN);
        lbl.setBounds(5, 0, 150, 25);
        panel.add(lbl);
        
        JPasswordField txt = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 20));
                g2.fill(new RoundRectangle2D.Float(2, 2, getWidth()-2, getHeight()-2, 12, 12));
                g2.setColor(getBackground());
                g2.fill(new RoundRectangle2D.Float(0, 0, getWidth()-2, getHeight()-2, 12, 12));
                g2.dispose();
                super.paintComponent(g);
            }
        };
        txt.setBounds(0, 30, 350, 50);
        txt.setFont(new Font("Arial", Font.PLAIN, 16));
        txt.setBackground(WHITE_SMOKE);
        txt.setForeground(DARK_BROWN);
        txt.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 50)
        ));
        txt.setOpaque(false);
        
        txt.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(DARK_BROWN, 2),
                    BorderFactory.createEmptyBorder(5, 15, 5, 50)
                ));
            }
            @Override
            public void focusLost(FocusEvent e) {
                txt.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
                    BorderFactory.createEmptyBorder(5, 15, 5, 50)
                ));
            }
        });
        
        panel.add(txt);
        
        // Eye button
        JLabel btnEye = new JLabel("👁");
        btnEye.setBounds(310, 42, 30, 30);
        btnEye.setFont(new Font("Arial Unicode MS", Font.PLAIN, 20));
        btnEye.setForeground(COFFEE_BROWN);
        btnEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btnEye.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                passwordVisible = !passwordVisible;
                if (passwordVisible) {
                    txt.setEchoChar((char) 0);
                    btnEye.setText("🙈");
                } else {
                    txt.setEchoChar('•');
                    btnEye.setText("👁");
                }
            }
            @Override
            public void mouseEntered(MouseEvent e) {
                btnEye.setForeground(DARK_BROWN);
            }
            @Override
            public void mouseExited(MouseEvent e) {
                btnEye.setForeground(COFFEE_BROWN);
            }
        });
        
        panel.add(btnEye);
        return panel;
    }
    
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter username and password!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Owner owner = OwnerController.loginOwner(username, password);
        
        if (owner != null) {
            JOptionPane.showMessageDialog(this,
                "Welcome, " + owner.getFullName() + "!",
                "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            
            this.dispose();
            new OwnerDashboardFrame(owner).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid owner credentials!",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }
    
    private void backToCustomerLogin() {
        this.dispose();
        new views.LoginFrame().setVisible(true);
    }
}
