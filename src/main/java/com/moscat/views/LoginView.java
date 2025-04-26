package com.moscat.views;

import com.moscat.App;
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
    private JButton loginButton;
    private JLabel statusLabel;
    
    /**
     * Constructs a new LoginView
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
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Create logo panel
        JPanel logoPanel = createLogoPanel();
        
        // Create login form panel
        JPanel loginPanel = createLoginPanel();
        
        // Add panels to main panel
        add(logoPanel, BorderLayout.NORTH);
        add(loginPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates the logo panel
     * 
     * @return JPanel with logo
     */
    private JPanel createLogoPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(Color.WHITE);
        panel.setBorder(new EmptyBorder(50, 0, 30, 0));
        
        // Create logo label
        JLabel logoLabel = new JLabel("MOSCAT Multipurpose Cooperative");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 24));
        logoLabel.setForeground(new Color(0, 87, 146)); // Dark blue color
        
        panel.add(logoLabel);
        return panel;
    }
    
    /**
     * Creates the login form panel
     * 
     * @return JPanel with login form
     */
    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Create login form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                new EmptyBorder(30, 30, 30, 30)));
        
        GridBagConstraints formGbc = new GridBagConstraints();
        formGbc.insets = new Insets(5, 5, 5, 5);
        formGbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Login form title
        JLabel titleLabel = new JLabel("Login to your account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        formGbc.gridx = 0;
        formGbc.gridy = 0;
        formGbc.gridwidth = 2;
        formPanel.add(titleLabel, formGbc);
        
        // Spacer
        JLabel spacerLabel = new JLabel(" ");
        formGbc.gridx = 0;
        formGbc.gridy = 1;
        formGbc.gridwidth = 2;
        formPanel.add(spacerLabel, formGbc);
        
        // Username label
        JLabel usernameLabel = new JLabel("Username:");
        formGbc.gridx = 0;
        formGbc.gridy = 2;
        formGbc.gridwidth = 2;
        formPanel.add(usernameLabel, formGbc);
        
        // Username field
        usernameField = new CustomTextField();
        usernameField.setPreferredSize(new Dimension(250, Constants.TEXT_FIELD_HEIGHT));
        formGbc.gridx = 0;
        formGbc.gridy = 3;
        formGbc.gridwidth = 2;
        formPanel.add(usernameField, formGbc);
        
        // Password label
        JLabel passwordLabel = new JLabel("Password:");
        formGbc.gridx = 0;
        formGbc.gridy = 4;
        formGbc.gridwidth = 2;
        formPanel.add(passwordLabel, formGbc);
        
        // Password field
        passwordField = new JPasswordField();
        passwordField.setPreferredSize(new Dimension(250, Constants.TEXT_FIELD_HEIGHT));
        formGbc.gridx = 0;
        formGbc.gridy = 5;
        formGbc.gridwidth = 2;
        formPanel.add(passwordField, formGbc);
        
        // Status label (for error messages)
        statusLabel = new JLabel(" ");
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(JLabel.CENTER);
        formGbc.gridx = 0;
        formGbc.gridy = 6;
        formGbc.gridwidth = 2;
        formPanel.add(statusLabel, formGbc);
        
        // Login button
        loginButton = new CustomButton("Login");
        loginButton.setPreferredSize(new Dimension(250, Constants.BUTTON_HEIGHT));
        formGbc.gridx = 0;
        formGbc.gridy = 7;
        formGbc.gridwidth = 2;
        formPanel.add(loginButton, formGbc);
        
        // Add the form panel to the main panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(formPanel, gbc);
        
        // Set up event listeners
        setupEventListeners();
        
        return panel;
    }
    
    /**
     * Sets up event listeners for interactive components
     */
    private void setupEventListeners() {
        // Login button action listener
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performLogin();
            }
        });
        
        // Password field key listener for Enter key
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    performLogin();
                }
            }
        });
    }
    
    /**
     * Performs the login process
     */
    private void performLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        // Basic validation
        if (username.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please enter both username and password.");
            return;
        }
        
        // Attempt to login
        boolean loginSuccess = AuthController.login(username, password);
        
        if (loginSuccess) {
            // Redirect to appropriate dashboard based on role
            User currentUser = AuthController.getCurrentUser();
            
            if (currentUser.isSuperAdmin()) {
                App.changeView(new SuperAdminDashboard(parentFrame));
            } else if (currentUser.isTreasurer()) {
                App.changeView(new TreasurerDashboard(parentFrame));
            } else if (currentUser.isBookkeeper()) {
                App.changeView(new BookkeeperDashboard(parentFrame));
            } else {
                // Fallback to generic dashboard
                App.changeView(new DashboardView(parentFrame));
            }
        } else {
            statusLabel.setText("Invalid username or password.");
        }
    }
}