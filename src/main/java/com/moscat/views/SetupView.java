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
 * First-time setup dialog for creating the initial admin user
 */
public class SetupView extends JDialog {
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField emailField;
    private JTextField contactNumberField;
    private JButton createButton;
    private JLabel errorLabel;
    
    /**
     * Constructs a new SetupView
     * 
     * @param parentFrame Parent JFrame
     */
    public SetupView(JFrame parentFrame) {
        super(parentFrame, "MOSCAT System Setup", true);
        
        // Basic dialog setup
        setSize(500, 500);
        setResizable(false);
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 15, 0));
        
        JLabel titleLabel = new JLabel("MOSCAT Cooperative - Initial Setup");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel subtitleLabel = new JLabel("Create the first SuperAdmin account");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(subtitleLabel, BorderLayout.CENTER);
        
        JLabel instructionLabel = new JLabel("<html>Welcome to MOSCAT! Before you can use the system, " +
                "you need to create a SuperAdmin account. This account will have full access to all system features.</html>");
        instructionLabel.setBorder(new EmptyBorder(0, 0, 15, 0));
        headerPanel.add(instructionLabel, BorderLayout.SOUTH);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        
        JLabel usernameLabel = new JLabel("Username*:");
        usernameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password*:");
        passwordField = new JPasswordField(20);
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password*:");
        confirmPasswordField = new JPasswordField(20);
        
        JLabel firstNameLabel = new JLabel("First Name*:");
        firstNameField = new JTextField(20);
        
        JLabel lastNameLabel = new JLabel("Last Name*:");
        lastNameField = new JTextField(20);
        
        JLabel emailLabel = new JLabel("Email*:");
        emailField = new JTextField(20);
        
        JLabel contactNumberLabel = new JLabel("Contact Number:");
        contactNumberField = new JTextField(20);
        
        errorLabel = new JLabel("");
        errorLabel.setForeground(Color.RED);
        
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);
        formPanel.add(firstNameLabel);
        formPanel.add(firstNameField);
        formPanel.add(lastNameLabel);
        formPanel.add(lastNameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(contactNumberLabel);
        formPanel.add(contactNumberField);
        formPanel.add(new JLabel("* Required fields"));
        formPanel.add(errorLabel);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        createButton = new JButton("Create Admin Account");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createAdminAccount();
            }
        });
        
        buttonPanel.add(createButton);
        
        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set content pane
        setContentPane(mainPanel);
        
        // Set default button
        getRootPane().setDefaultButton(createButton);
    }
    
    /**
     * Creates the initial admin account
     */
    private void createAdminAccount() {
        // Get input values
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String email = emailField.getText().trim();
        String contactNumber = contactNumberField.getText().trim();
        
        // Validate input
        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || 
                firstName.isEmpty() || lastName.isEmpty() || email.isEmpty()) {
            errorLabel.setText("All required fields must be filled.");
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }
        
        if (password.length() < 8) {
            errorLabel.setText("Password must be at least 8 characters long.");
            return;
        }
        
        // Create user object
        User adminUser = new User();
        adminUser.setUsername(username);
        adminUser.setFirstName(firstName);
        adminUser.setLastName(lastName);
        adminUser.setEmail(email);
        adminUser.setContactNumber(contactNumber);
        adminUser.setRole(Constants.ROLE_SUPER_ADMIN);
        adminUser.setActive(true);
        
        // Create user in database
        boolean success = AuthController.createUser(adminUser, password);
        
        if (success) {
            JOptionPane.showMessageDialog(
                    this,
                    "SuperAdmin account created successfully! You can now log in.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Close setup dialog
            dispose();
            
            // Show login dialog
            JFrame parentFrame = (JFrame) getParent();
            new LoginView(parentFrame).setVisible(true);
        } else {
            errorLabel.setText("Failed to create SuperAdmin account. Please try again.");
        }
    }
}