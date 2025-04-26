package com.moscat.views;

import com.moscat.controllers.AuthController;
import com.moscat.models.User;
import com.moscat.utils.Constants;
import com.moscat.views.components.CustomButton;
import com.moscat.views.components.CustomTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Login view for user authentication
 */
public class LoginView extends JPanel {
    
    private JFrame parentFrame;
    private CustomTextField usernameField;
    private JPasswordField passwordField;
    private JLabel errorLabel;
    
    /**
     * Constructor
     * 
     * @param parentFrame Parent frame
     */
    public LoginView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
    }
    
    /**
     * Initializes UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create logo panel
        JPanel logoPanel = createLogoPanel();
        
        // Create login form panel
        JPanel loginPanel = createLoginFormPanel();
        
        // Add panels to main view
        add(logoPanel, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates the logo panel
     * 
     * @return JPanel with logo
     */
    private JPanel createLogoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(0, 87, 146)); // Dark blue
        panel.setPreferredSize(new Dimension(0, 150));
        
        JLabel titleLabel = new JLabel("MOSCAT Cooperative");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel subtitleLabel = new JLabel("Multipurpose Cooperative Savings and Loan System");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(subtitleLabel);
        
        panel.add(titlePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the login form panel
     * 
     * @return JPanel with login form
     */
    private JPanel createLoginFormPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(50, 100, 50, 100));
        
        // Create form title
        JLabel formTitleLabel = new JLabel("Login");
        formTitleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        formTitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create error label
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        errorLabel.setVisible(false);
        
        // Create username field
        usernameField = new CustomTextField();
        usernameField.setPlaceholder("Username");
        usernameField.setPreferredSize(new Dimension(300, Constants.TEXT_FIELD_HEIGHT));
        usernameField.setMaximumSize(new Dimension(300, Constants.TEXT_FIELD_HEIGHT));
        
        // Create password field
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(300, Constants.TEXT_FIELD_HEIGHT));
        passwordField.setMaximumSize(new Dimension(300, Constants.TEXT_FIELD_HEIGHT));
        
        // Add key listener to password field for enter key
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });
        
        // Create login button
        CustomButton loginButton = new CustomButton("Login");
        loginButton.setPreferredSize(new Dimension(300, 40));
        loginButton.setMaximumSize(new Dimension(300, 40));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        // Add components to panel
        panel.add(formTitleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(errorLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(new JLabel("Username:"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(15));
        panel.add(new JLabel("Password:"));
        panel.add(Box.createVerticalStrut(5));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(25));
        panel.add(loginButton);
        
        return panel;
    }
    
    /**
     * Performs login
     */
    private void login() {
        // Clear error message
        errorLabel.setVisible(false);
        
        // Get username and password
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Validate inputs
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password.");
            return;
        }
        
        // Attempt login
        User user = AuthController.login(username, password);
        
        if (user != null) {
            // Login successful, determine dashboard based on role
            JPanel dashboard;
            
            switch (user.getRole()) {
                case Constants.ROLE_SUPER_ADMIN:
                    dashboard = new SuperAdminDashboard(parentFrame);
                    break;
                case Constants.ROLE_TREASURER:
                    dashboard = new TreasurerDashboard(parentFrame);
                    break;
                case Constants.ROLE_BOOKKEEPER:
                    dashboard = new BookkeeperDashboard(parentFrame);
                    break;
                default:
                    dashboard = new DashboardView(parentFrame);
            }
            
            // Change view to dashboard
            com.moscat.App.changeView(dashboard);
        } else {
            // Login failed
            showError("Invalid username or password. Please try again.");
        }
    }
    
    /**
     * Shows an error message
     * 
     * @param message Error message
     */
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}