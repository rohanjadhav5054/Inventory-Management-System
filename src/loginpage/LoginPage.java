//LoginPage.java
package loginpage;

import dashboard.InventoryDashboard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Login Page Implementation for the Inventory Management System
 * 
 * This class provides a user-friendly login interface that authenticates users
 * before granting access to the Inventory Dashboard. It serves as the entry point
 * to the application's UI after the database initialization.
 * 
 * Features:
 * - Modern UI with custom rounded components
 * - Username and password authentication
 * - Password visibility toggle
 * - Error handling for invalid credentials
 * - Smooth transition to the main dashboard on successful login
 * 
 * Design Principles:
 * - Uses custom UI components (RoundedTextField, RoundedPasswordField, RoundedButton)
 * - Simple, clean layout with appropriate spacing and alignment
 * - Proper error handling and user feedback
 * - Secure password handling
 * 
 * @author Inventory Management System Team
 * @version 1.0
 */
public class LoginPage extends JFrame {

    /**
     * Constructs and displays the login page.
     * 
     * This constructor initializes the JFrame and all UI components, sets up event listeners,
     * and makes the form visible. The login form includes:
     * - Application title
     * - User icon (custom drawn)
     * - Username and password fields
     * - Show/hide password checkbox
     * - Login button
     * 
     * Authentication is hardcoded for demonstration purposes (username: admin, password: 1234).
     * In a production environment, this would be replaced with a proper authentication system.
     */
    public LoginPage() {
        // Frame settings
        setTitle("Login Page");
        setSize(500, 550);
        setLocationRelativeTo(null); // Center the window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(Color.decode("#E3F2FD")); // Light blue background

        // Fonts
        Font titleFont = new Font("Segoe UI", Font.BOLD, 22);
        Font labelFont = new Font("Segoe UI", Font.PLAIN, 16);
        Font fieldFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font buttonFont = new Font("Segoe UI", Font.BOLD, 14);

        // Title Label
        JLabel lblTitle = new JLabel("Inventory Management System");
        lblTitle.setFont(titleFont);
        lblTitle.setBounds(70, 20, 400, 30);
        lblTitle.setForeground(new Color(60, 60, 60));
        add(lblTitle);

        // User Icon
        try {
            // Create a panel with a user icon using text
            JPanel iconPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Draw circle background
                    g2d.setColor(new Color(70, 130, 180)); // Steel blue
                    g2d.fillOval(10, 10, 80, 80);
                    
                    // Draw user silhouette
                    g2d.setColor(Color.WHITE);
                    // Head
                    g2d.fillOval(35, 25, 30, 30);
                    // Body
                    g2d.fillOval(25, 55, 50, 30);
                    
                    g2d.dispose();
                }
            };
            iconPanel.setPreferredSize(new Dimension(100, 100));
            iconPanel.setBounds(200, 80, 100, 100);
            iconPanel.setOpaque(false);
            add(iconPanel);
            
            System.out.println("Using custom drawn user icon");
        } catch (Exception e) {
            System.out.println("Error creating user icon: " + e.getMessage());
            // Create a placeholder panel for the missing image
            JPanel placeholderIcon = new JPanel();
            placeholderIcon.setBackground(new Color(200, 200, 200));
            placeholderIcon.setBounds(200, 80, 100, 100);
            add(placeholderIcon);
        }

        // USER label
        JLabel userLabel = new JLabel("ADMIN");
        userLabel.setFont(buttonFont);
        userLabel.setBounds(220, 190, 80, 30);
        add(userLabel);

        // Username Label
        JLabel lblUser = new JLabel("Username :");
        lblUser.setFont(labelFont);
        lblUser.setBounds(130, 230, 100, 25);
        add(lblUser);

        // Username Field (custom rounded)
        RoundedTextField txtUser = new RoundedTextField();
        txtUser.setBounds(225, 230, 150, 25);
        txtUser.setFont(fieldFont);
        add(txtUser);

        // Password Label
        JLabel lblPass = new JLabel("Password :");
        lblPass.setFont(labelFont);
        lblPass.setBounds(130, 280, 100, 25);
        add(lblPass);

        // Password Field (custom rounded)
        RoundedPasswordField txtPass = new RoundedPasswordField();
        txtPass.setBounds(225, 280, 150, 25);
        txtPass.setFont(fieldFont);
        add(txtPass);

        // Login Button (custom rounded)
        RoundedButton btnLogin = new RoundedButton("Login");
        btnLogin.setFont(buttonFont);
        btnLogin.setBackground(new Color(255, 182, 193)); // Soft pink color
        btnLogin.setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY));
        btnLogin.setBounds(170, 350, 150, 35);
        btnLogin.setFocusPainted(false);
        add(btnLogin);
        
        // Show/Hide Password Checkbox
        JCheckBox Jc = new JCheckBox("Show Password");
        Jc.setBounds(220,320,150,15);
        add(Jc);

        // Add action listener for the checkbox
        Jc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (Jc.isSelected()) {
                    // Show password
                    txtPass.setEchoChar((char) 0);
                } else {
                    // Hide password
                    txtPass.setEchoChar('•'); // Use your preferred echo character
                }
            }
        });

        // Add action listener for the login button
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String username = txtUser.getText();
                String password = new String(txtPass.getPassword());

                // Authenticate user (hardcoded for demonstration)
                if (username.equals("admin") && password.equals("1234")) {
                    try {
                        JOptionPane.showMessageDialog(null, "Login Successful!");
                        System.out.println("Creating dashboard...");
                        InventoryDashboard dashboard = new InventoryDashboard();
                        System.out.println("Setting dashboard visible...");
                        dashboard.setVisible(true);
                        System.out.println("Disposing login page...");
                        dispose();
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        JOptionPane.showMessageDialog(null,
                                "Error opening dashboard: " + ex.getMessage(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid Credentials!");
                }
            }
        });
        // Show Frame
        setVisible(true);
    }


    /**
     * Main method to launch the Login Page.
     * Note: This is commented out as the application is now launched from Main.java
     */
//    public static void main(String[] args) {
//        new LoginPage();
//    }
}