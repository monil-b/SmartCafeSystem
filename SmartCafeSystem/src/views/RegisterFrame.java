package views;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import controllers.AuthController;

public class RegisterFrame extends JFrame {

    private static final Color COFFEE_BROWN = new Color(139, 90, 60);
    private static final Color LIGHT_COFFEE = new Color(198, 156, 109);
    private static final Color CREAM = new Color(245, 237, 220);
    private static final Color DARK_BROWN = new Color(78, 52, 46);
    private static final Color WHITE_SMOKE = new Color(250, 248, 245);
    private static final Color ACCENT_COFFEE = new Color(160, 110, 80);
    
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JPasswordField txtConfirmPassword;
    private JTextField txtEmail;
    private JTextField txtPhone;
    private JButton btnRegister;
    private JButton btnBackToLogin;
    private boolean passwordVisible = false;
    private boolean confirmPasswordVisible = false;
    
    public RegisterFrame() {
        setTitle("SmartCafe - Register");
        setSize(550, 820);
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
                g2d.fillOval(4, 4, 85, 85);
                g2d.setColor(COFFEE_BROWN);
                g2d.fillOval(0, 0, 85, 85);
                g2d.setColor(new Color(255, 255, 255, 40));
                g2d.fillOval(15, 10, 35, 35);
            }
        };
        logoPanel.setBounds(232, 35, 90, 90);
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        
        JLabel lblCoffee = new JLabel("☕", SwingConstants.CENTER);
        lblCoffee.setFont(new Font("Arial", Font.PLAIN, 45));
        lblCoffee.setForeground(WHITE_SMOKE);
        logoPanel.add(lblCoffee);
        add(logoPanel);
        
        // Title
        JLabel lblTitle = new JLabel("Create Account", SwingConstants.CENTER);
        lblTitle.setFont(new Font("Arial", Font.BOLD, 34));
        lblTitle.setForeground(DARK_BROWN);
        lblTitle.setBounds(50, 135, 450, 40);
        add(lblTitle);
        
        // Subtitle
        JLabel lblSubtitle = new JLabel("Join SmartCafe today!", SwingConstants.CENTER);
        lblSubtitle.setFont(new Font("Arial", Font.PLAIN, 16));
        lblSubtitle.setForeground(ACCENT_COFFEE);
        lblSubtitle.setBounds(50, 175, 450, 25);
        add(lblSubtitle);
        
        int yPos = 220;
        
        // Username
        JPanel usernamePanel = createModernInputPanel("Username", yPos);
        txtUsername = (JTextField) usernamePanel.getComponent(1);
        add(usernamePanel);
        yPos += 85;
        
        // Email
        JPanel emailPanel = createModernInputPanel("Email Address", yPos);
        txtEmail = (JTextField) emailPanel.getComponent(1);
        add(emailPanel);
        yPos += 85;
        
        // Phone
        JPanel phonePanel = createModernInputPanel("Phone Number", yPos);
        txtPhone = (JTextField) phonePanel.getComponent(1);
        add(phonePanel);
        yPos += 85;
        
        // Password
        JPanel passwordPanel = createModernPasswordPanelWithEye("Password", yPos, true);
        txtPassword = (JPasswordField) passwordPanel.getComponent(1);
        add(passwordPanel);
        yPos += 85;
        
        // Confirm Password
        JPanel confirmPanel = createModernPasswordPanelWithEye("Confirm Password", yPos, false);
        txtConfirmPassword = (JPasswordField) confirmPanel.getComponent(1);
        add(confirmPanel);
        yPos += 95;
        
        // Register button
        btnRegister = createRoundedButton("Create Account", COFFEE_BROWN, WHITE_SMOKE);
        btnRegister.setBounds(100, yPos, 350, 55);
        btnRegister.setFont(new Font("Arial", Font.BOLD, 18));
        btnRegister.addActionListener(e -> handleRegister());
        add(btnRegister);
        
        // Already have account section
        yPos += 70;
        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 0));
        loginPanel.setBounds(100, yPos, 350, 35);
        loginPanel.setOpaque(false);
        
        JLabel lblHaveAccount = new JLabel("Already have an account?");
        lblHaveAccount.setFont(new Font("Arial", Font.PLAIN, 15));
        lblHaveAccount.setForeground(COFFEE_BROWN);
        loginPanel.add(lblHaveAccount);
        
        btnBackToLogin = new JButton("Sign in");
        btnBackToLogin.setFont(new Font("Arial", Font.BOLD, 15));
        btnBackToLogin.setForeground(COFFEE_BROWN);
        btnBackToLogin.setContentAreaFilled(false);
        btnBackToLogin.setBorderPainted(false);
        btnBackToLogin.setFocusPainted(false);
        btnBackToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnBackToLogin.setMargin(new Insets(0, 0, 0, 0));
        btnBackToLogin.addActionListener(e -> backToLogin());
        
        btnBackToLogin.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                btnBackToLogin.setForeground(DARK_BROWN);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                btnBackToLogin.setForeground(COFFEE_BROWN);
            }
        });
        
        loginPanel.add(btnBackToLogin);
        add(loginPanel);
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
                button.setBackground(DARK_BROWN);
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
        panel.setBounds(100, yPos, 350, 75);
        panel.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(DARK_BROWN);
        lbl.setBounds(5, 0, 200, 22);
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
        txt.setBounds(0, 25, 350, 48);
        txt.setFont(new Font("Arial", Font.PLAIN, 15));
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
    
    private JPanel createModernPasswordPanelWithEye(String label, int yPos, boolean isPassword) {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(100, yPos, 350, 75);
        panel.setOpaque(false);
        
        JLabel lbl = new JLabel(label);
        lbl.setFont(new Font("Arial", Font.BOLD, 13));
        lbl.setForeground(DARK_BROWN);
        lbl.setBounds(5, 0, 200, 22);
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
        txt.setBounds(0, 25, 350, 48);
        txt.setFont(new Font("Arial", Font.PLAIN, 15));
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
                    BorderFactory.createLineBorder(COFFEE_BROWN, 2),
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
        JButton btnEye = new JButton("👁");
        btnEye.setBounds(300, 29, 40, 40);
        btnEye.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        btnEye.setForeground(COFFEE_BROWN);
        btnEye.setBackground(WHITE_SMOKE);
        btnEye.setContentAreaFilled(false);
        btnEye.setBorderPainted(false);
        btnEye.setFocusPainted(false);
        btnEye.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEye.setToolTipText("Click to show/hide password");
        
        if (isPassword) {
            btnEye.addActionListener(e -> {
                passwordVisible = !passwordVisible;
                if (passwordVisible) {
                    txt.setEchoChar((char) 0);
                    btnEye.setText("🙈");
                } else {
                    txt.setEchoChar('•');
                    btnEye.setText("👁");
                }
            });
        } else {
            btnEye.addActionListener(e -> {
                confirmPasswordVisible = !confirmPasswordVisible;
                if (confirmPasswordVisible) {
                    txt.setEchoChar((char) 0);
                    btnEye.setText("🙈");
                } else {
                    txt.setEchoChar('•');
                    btnEye.setText("👁");
                }
            });
        }
        
        btnEye.addMouseListener(new MouseAdapter() {
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
    
    private void handleRegister() {
        String username = txtUsername.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirmPass = new String(txtConfirmPassword.getPassword());
        
        if (username.isEmpty() || email.isEmpty() || phone.isEmpty() || 
            password.isEmpty() || confirmPass.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill all fields!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPass)) {
            JOptionPane.showMessageDialog(this,
                "Passwords do not match!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this,
                "Password must be at least 4 characters!",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        boolean success = AuthController.registerUser(username, password, email, phone);
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "Registration successful!\nPlease login with your credentials.",
                "Success", JOptionPane.INFORMATION_MESSAGE);
            backToLogin();
        } else {
            JOptionPane.showMessageDialog(this,
                "Registration failed!\nUsername may already exist.",
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void backToLogin() {
        this.dispose();
        new LoginFrame().setVisible(true);
    }
}
