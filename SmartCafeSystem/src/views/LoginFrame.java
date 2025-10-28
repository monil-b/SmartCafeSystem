package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import controllers.AuthController;
import models.User;
import views.customer.MenuFrame;

public class LoginFrame extends JFrame {
    
    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color ACCENT_COFFEE = new Color(160, 110, 80);
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin;
    private JButton btnRegister;
    private JCheckBox chkShowPassword;
    private boolean passwordVisible = false;
    
    public LoginFrame() {
        setTitle("SmartCafe - Login");
        setSize(550, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        getContentPane().setBackground(CREAM);
        
        initComponents();
    }
    
    private void initComponents() {
        setLayout(null);
        
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(0, 0, 0, 30));
                g2d.fillOval(5, 5, 100, 100);
                g2d.setColor(COFFEE_BROWN);
                g2d.fillOval(0, 0, 100, 100);
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillOval(20, 15, 40, 40);
            }
        };
        logoPanel.setBounds(225, 50, 105, 105);
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        
        JLabel lblCoffee = new JLabel("☕", SwingConstants.CENTER);
        lblCoffee.setFont(new Font("Arial", Font.PLAIN, 55));
        lblCoffee.setForeground(WHITE_SMOKE);
        logoPanel.add(lblCoffee);
        add(logoPanel);
        
        // Title with gradient effect
        JLabel lblTitle = new JLabel("Welcome Back", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 36));
        lblTitle.setForeground(DARK_BROWN);
        lblTitle.setBounds(50, 175, 450, 45);
        add(lblTitle);
        
        // Subtitle
        JLabel lblSubtitle = new JLabel("Sign in to order your favorite coffee", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtitle.setForeground(ACCENT_COFFEE);
        lblSubtitle.setBounds(50, 225, 450, 25);
        add(lblSubtitle);
        
        int yPos = 280;
        
        // Username field
        JPanel usernamePanel = createModernInputPanel("Username", yPos);
        txtUsername = (JTextField) usernamePanel.getComponent(1);
        add(usernamePanel);
        yPos += 100;
        
        // Password field
        JPanel passwordPanel = createModernInputPanel("Password", yPos);
        txtPassword = new JPasswordField() {
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
        txtPassword.setBounds(0, 30, 350, 50);
        txtPassword.setFont(new Font("Arial", Font.PLAIN, 16));
        txtPassword.setBackground(WHITE_SMOKE);
        txtPassword.setForeground(DARK_BROWN);
        txtPassword.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
            BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        txtPassword.setOpaque(false);
        
        txtPassword.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                txtPassword.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(COFFEE_BROWN, 2),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                txtPassword.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(LIGHT_COFFEE, 2),
                    BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));
            }
        });
        
        passwordPanel.add(txtPassword, 1);
        add(passwordPanel);
        yPos += 90;
        
        // Show Password Checkbox
        chkShowPassword = new JCheckBox("Show Password");
        chkShowPassword.setBounds(105, yPos, 200, 25);
        chkShowPassword.setFont(new Font("Arial", Font.PLAIN, 14));
        chkShowPassword.setForeground(DARK_BROWN);
        chkShowPassword.setBackground(CREAM);
        chkShowPassword.setFocusPainted(false);
        chkShowPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        chkShowPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (chkShowPassword.isSelected()) {
                    txtPassword.setEchoChar((char) 0);  // Show password
                } else {
                    txtPassword.setEchoChar('•');  // Hide password
                }
            }
        });
        
        add(chkShowPassword);
        yPos += 40;
        
        // Login button with shadow
        btnLogin = createRoundedButton("Sign In", COFFEE_BROWN, WHITE_SMOKE);
        btnLogin.setBounds(100, yPos, 350, 50);
        btnLogin.setFont(new Font("Arial", Font.BOLD, 18));
        btnLogin.addActionListener(e -> handleLogin());
        add(btnLogin);
        yPos += 65;
        
        // Divider with "or"
        JPanel dividerPanel = new JPanel();
        dividerPanel.setLayout(null);
        dividerPanel.setBounds(100, yPos, 350, 30);
        dividerPanel.setOpaque(false);
        
        JSeparator leftLine = new JSeparator();
        leftLine.setBounds(0, 15, 150, 2);
        leftLine.setForeground(LIGHT_COFFEE);
        dividerPanel.add(leftLine);
        
        JLabel lblOr = new JLabel("or", SwingConstants.CENTER);
        lblOr.setBounds(150, 5, 50, 20);
        lblOr.setFont(new Font("Arial", Font.PLAIN, 14));
        lblOr.setForeground(COFFEE_BROWN);
        dividerPanel.add(lblOr);
        
        JSeparator rightLine = new JSeparator();
        rightLine.setBounds(200, 15, 150, 2);
        rightLine.setForeground(LIGHT_COFFEE);
        dividerPanel.add(rightLine);
        
        add(dividerPanel);
        yPos += 45;
        
        // Register section
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        registerPanel.setBounds(100, yPos, 350, 35);
        registerPanel.setOpaque(false);
        
        JLabel lblDontHave = new JLabel("Don't have an account?");
        lblDontHave.setFont(new Font("Arial", Font.PLAIN, 15));
        lblDontHave.setForeground(COFFEE_BROWN);
        registerPanel.add(lblDontHave);
        
        btnRegister = new JButton("Sign up");
        btnRegister.setFont(new Font("Arial", Font.BOLD, 15));
        btnRegister.setForeground(COFFEE_BROWN);
        btnRegister.setContentAreaFilled(false);
        btnRegister.setBorderPainted(false);
        btnRegister.setFocusPainted(false);
        btnRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegister.setMargin(new Insets(0, 0, 0, 0));
        btnRegister.addActionListener(e -> openRegisterFrame());
        
        btnRegister.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnRegister.setForeground(DARK_BROWN);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnRegister.setForeground(COFFEE_BROWN);
            }
        });
        
        registerPanel.add(btnRegister);
        add(registerPanel);
        yPos += 40;
        
        // Divider before owner login
        JSeparator divider = new JSeparator();
        divider.setBounds(100, yPos, 350, 2);
        divider.setForeground(LIGHT_COFFEE);
        add(divider);
        yPos += 15;
        
        // Owner Login button
        JButton btnOwnerLogin = createRoundedButton("Owner Login", DARK_BROWN, WHITE_SMOKE);
        btnOwnerLogin.setBounds(125, yPos, 300, 50);
        btnOwnerLogin.setFont(new Font("Arial", Font.BOLD, 16));
        btnOwnerLogin.addActionListener(e -> {
            this.dispose();
            new views.owner.OwnerLoginFrame().setVisible(true);
        });
        add(btnOwnerLogin);
        
        // Enter key login
        txtPassword.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        });
        
        txtUsername.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    txtPassword.requestFocus();
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
        
        Color hoverColor = bgColor.equals(COFFEE_BROWN) ? DARK_BROWN : 
                          bgColor.equals(DARK_BROWN) ? new Color(60, 40, 35) : DARK_BROWN;
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(hoverColor);
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
                    BorderFactory.createLineBorder(COFFEE_BROWN, 2),
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
    
    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please enter username and password!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        User user = AuthController.loginUser(username, password);
        
        if (user != null) {
            JOptionPane.showMessageDialog(this,
                "Welcome, " + user.getUsername() + "!",
                "Login Successful", JOptionPane.INFORMATION_MESSAGE);
            
            this.dispose();
            new MenuFrame(user).setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password!",
                "Login Failed", JOptionPane.ERROR_MESSAGE);
            txtPassword.setText("");
        }
    }
    
    private void openRegisterFrame() {
        this.dispose();
        new RegisterFrame().setVisible(true);
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
