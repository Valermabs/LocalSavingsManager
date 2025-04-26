package com.moscat.views;

import com.moscat.App;
import com.moscat.controllers.AuthController;
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
    private JButton loginButton;
    
    /**
     * Constructor for LoginView
     * 
     * @param parentFrame Parent JFrame
     */
    public LoginView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        // Set layout
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Create components
        JPanel centerPanel = new JPanel(new GridBagLayout());
        JPanel loginPanel = createLoginPanel();
        
        centerPanel.add(loginPanel);
        add(centerPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates the login panel with input fields and buttons
     * 
     * @return JPanel containing login components
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEtchedBorder(),
                new EmptyBorder(20, 20, 20, 20)));
        panel.setMaximumSize(new Dimension(400, 300));
        
        // Logo or header
        JLabel logoLabel = new JLabel("MOSCAT Multipurpose Cooperative");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel subtitleLabel = new JLabel("Savings and Loan System");
        subtitleLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Login form
        JPanel formPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        formPanel.setMaximumSize(new Dimension(350, 200));
        formPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        JLabel usernameLabel = new JLabel("Username:");
        usernameField = new CustomTextField();
        usernameField.setPreferredSize(new Dimension(200, Constants.TEXT_FIELD_HEIGHT));
        
        JLabel passwordLabel = new JLabel("Password:");
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(200, Constants.TEXT_FIELD_HEIGHT));
        
        // Error label
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Login button
        loginButton = new CustomButton("Login");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(120, Constants.BUTTON_HEIGHT));
        
        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                attemptLogin();
            }
        });
        
        // Add key listeners
        KeyAdapter enterKeyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    attemptLogin();
                }
            }
        };
        
        usernameField.addKeyListener(enterKeyAdapter);
        passwordField.addKeyListener(enterKeyAdapter);
        
        // Add components to form panel
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        
        // Add all components to main panel
        panel.add(logoLabel);
        panel.add(Box.createVerticalStrut(5));
        panel.add(subtitleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(formPanel);
        panel.add(errorLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loginButton);
        
        return panel;
    }
    
    /**
     * Attempts to authenticate the user with provided credentials
     */
    private void attemptLogin() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Input validation
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Username and password are required");
            return;
        }
        
        // Show loading indicator
        loginButton.setEnabled(false);
        loginButton.setText("Logging in...");
        errorLabel.setText("");
        
        // Perform login in a separate thread to keep UI responsive
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return AuthController.authenticate(username, password);
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    
                    if (success) {
                        // Navigate to appropriate dashboard based on role
                        navigateToDashboard();
                    } else {
                        errorLabel.setText("Invalid username or password");
                        loginButton.setEnabled(true);
                        loginButton.setText("Login");
                        passwordField.setText("");
                    }
                } catch (Exception e) {
                    errorLabel.setText("An error occurred while logging in");
                    loginButton.setEnabled(true);
                    loginButton.setText("Login");
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Navigates to the appropriate dashboard based on user role
     */
    private void navigateToDashboard() {
        if (AuthController.isSuperAdmin()) {
            App.changeView(new SuperAdminDashboard(parentFrame));
        } else if (AuthController.isTreasurer()) {
            App.changeView(new TreasurerDashboard(parentFrame));
        } else if (AuthController.isBookkeeper()) {
            App.changeView(new BookkeeperDashboard(parentFrame));
        } else {
            // Fallback to general dashboard
            App.changeView(new DashboardView(parentFrame));
        }
    }
}
