package com.moscat.views;

import com.moscat.controllers.AuthController;
import com.moscat.models.User;
import com.moscat.utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login dialog
 */
public class LoginView extends JDialog {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel errorLabel;
    
    /**
     * Constructs a new LoginView
     * 
     * @param parentFrame Parent JFrame
     */
    public LoginView(JFrame parentFrame) {
        super(parentFrame, "MOSCAT Login", true);
        
        // Basic dialog setup
        setSize(400, 300);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("MOSCAT Cooperative");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel subtitleLabel = new JLabel("Savings & Loan Management System");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        headerPanel.add(titleLabel);
        headerPanel.add(subtitleLabel);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField(20);
        
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(errorLabel);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        buttonPanel.add(loginButton);
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set content pane
        setContentPane(mainPanel);
        
        // Set default button
        getRootPane().setDefaultButton(loginButton);
    }
    
    /**
     * Attempts to log in the user
     */
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password are required.");
            return;
        }
        
        // Attempt to login
        User user = AuthController.login(username, password);
        
        if (user != null) {
            // Open dashboard based on role
            switch (user.getRole()) {
                case Constants.ROLE_SUPER_ADMIN:
                    openSuperAdminDashboard();
                    break;
                case Constants.ROLE_TREASURER:
                    openTreasurerDashboard();
                    break;
                case Constants.ROLE_BOOKKEEPER:
                    openBookkeeperDashboard();
                    break;
                default:
                    errorLabel.setText("Unknown user role.");
                    return;
            }
            
            // Close login dialog
            dispose();
        } else {
            errorLabel.setText("Invalid username or password.");
        }
    }
    
    /**
     * Opens the super admin dashboard
     */
    private void openSuperAdminDashboard() {
        JFrame parentFrame = (JFrame) getParent();
        SuperAdminDashboard dashboard = new SuperAdminDashboard(parentFrame);
        dashboard.setVisible(true);
    }
    
    /**
     * Opens the treasurer dashboard
     */
    private void openTreasurerDashboard() {
        JFrame parentFrame = (JFrame) getParent();
        TreasurerDashboard dashboard = new TreasurerDashboard(parentFrame);
        dashboard.setVisible(true);
    }
    
    /**
     * Opens the bookkeeper dashboard
     */
    private void openBookkeeperDashboard() {
        JFrame parentFrame = (JFrame) getParent();
        BookkeeperDashboard dashboard = new BookkeeperDashboard(parentFrame);
        dashboard.setVisible(true);
    }
}