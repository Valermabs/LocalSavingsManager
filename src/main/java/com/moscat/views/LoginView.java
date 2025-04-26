package com.moscat.views;

import com.moscat.controllers.AuthController;
import com.moscat.models.User;
import com.moscat.utils.Constants;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Login dialog for the application
 */
public class LoginView extends JDialog {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private JLabel messageLabel;
    
    /**
     * Creates a new login dialog
     * 
     * @param parent Parent frame
     */
    public LoginView(JFrame parent) {
        super(parent, "Login", true);
        
        // Set up the layout
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.HORIZONTAL;
        constraints.insets = new Insets(10, 10, 10, 10);
        
        // Title label
        JLabel titleLabel = new JLabel("MOSCAT Cooperative System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        constraints.gridx = 0;
        constraints.gridy = 0;
        constraints.gridwidth = 2;
        panel.add(titleLabel, constraints);
        
        // Username label
        JLabel usernameLabel = new JLabel("Username:");
        constraints.gridx = 0;
        constraints.gridy = 1;
        constraints.gridwidth = 1;
        panel.add(usernameLabel, constraints);
        
        // Username field
        usernameField = new JTextField(20);
        constraints.gridx = 1;
        constraints.gridy = 1;
        panel.add(usernameField, constraints);
        
        // Password label
        JLabel passwordLabel = new JLabel("Password:");
        constraints.gridx = 0;
        constraints.gridy = 2;
        panel.add(passwordLabel, constraints);
        
        // Password field
        passwordField = new JPasswordField(20);
        constraints.gridx = 1;
        constraints.gridy = 2;
        panel.add(passwordField, constraints);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // Login button
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        buttonPanel.add(loginButton);
        
        // Cancel button
        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                System.exit(0);
            }
        });
        buttonPanel.add(cancelButton);
        
        constraints.gridx = 0;
        constraints.gridy = 3;
        constraints.gridwidth = 2;
        panel.add(buttonPanel, constraints);
        
        // Message label
        messageLabel = new JLabel(" ");
        messageLabel.setForeground(Color.RED);
        constraints.gridx = 0;
        constraints.gridy = 4;
        constraints.gridwidth = 2;
        panel.add(messageLabel, constraints);
        
        // Set default button
        getRootPane().setDefaultButton(loginButton);
        
        // Set content pane
        setContentPane(panel);
        
        // Pack and center
        pack();
        setLocationRelativeTo(parent);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    /**
     * Handles the login action
     */
    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        
        // Validate input
        if (username.isEmpty() || password.isEmpty()) {
            messageLabel.setText("Please enter username and password");
            return;
        }
        
        // Attempt login
        User user = AuthController.login(username, password);
        
        if (user != null) {
            // Successful login
            dispose();
            
            // Open appropriate dashboard based on user role
            if (user.isSuperAdmin()) {
                // Open admin dashboard
                JOptionPane.showMessageDialog(
                    null,
                    "Welcome Admin " + user.getFirstName() + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
                // TODO: Add AdminDashboard
                // new AdminDashboard(user).setVisible(true);
            } else if (user.isTreasurer()) {
                // Open treasurer dashboard
                JOptionPane.showMessageDialog(
                    null,
                    "Welcome Treasurer " + user.getFirstName() + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
                // TODO: Add TreasurerDashboard
                // new TreasurerDashboard(user).setVisible(true);
            } else if (user.isBookkeeper()) {
                // Open bookkeeper dashboard
                JOptionPane.showMessageDialog(
                    null,
                    "Welcome Bookkeeper " + user.getFirstName() + "!",
                    "Login Successful",
                    JOptionPane.INFORMATION_MESSAGE
                );
                // TODO: Add BookkeeperDashboard
                // new BookkeeperDashboard(user).setVisible(true);
            } else {
                // Unrecognized role
                JOptionPane.showMessageDialog(
                    this,
                    "Unknown user role: " + user.getRole(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
                AuthController.logout();
            }
        } else {
            // Failed login
            messageLabel.setText("Invalid username or password");
            passwordField.setText("");
            usernameField.requestFocus();
        }
    }
}