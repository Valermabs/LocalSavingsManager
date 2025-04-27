package com.moscat.views;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import com.moscat.controllers.AuthController;
import com.moscat.utils.Constants;

/**
 * Login view for the application
 */
public class LoginView extends JDialog {
    private static final long serialVersionUID = 1L;
    
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    
    /**
     * Constructor
     * 
     * @param owner The owner frame
     */
    public LoginView(Frame owner) {
        super(owner, "Login - " + Constants.APP_NAME, true);
        
        initComponents();
        setupLayout();
        setupListeners();
        
        // Center the dialog on the screen
        setSize(400, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
    }
    
    /**
     * Initializes the components
     */
    private void initComponents() {
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        cancelButton = new JButton("Cancel");
        
        // Make login button the default button
        getRootPane().setDefaultButton(loginButton);
    }
    
    /**
     * Sets up the layout
     */
    private void setupLayout() {
        JPanel contentPane = new JPanel(new BorderLayout(10, 10));
        contentPane.setBorder(new EmptyBorder(20, 20, 20, 20));
        setContentPane(contentPane);
        
        // Title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("MOSCAT Cooperative");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(titleLabel);
        
        JLabel subtitleLabel = new JLabel("Savings and Loan System");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        titlePanel.add(subtitleLabel);
        
        contentPane.add(titlePanel, BorderLayout.NORTH);
        
        // Input panel
        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Username label and field
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(usernameField, gbc);
        
        // Password label and field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.EAST;
        inputPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.anchor = GridBagConstraints.WEST;
        inputPanel.add(passwordField, gbc);
        
        contentPane.add(inputPanel, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(loginButton);
        buttonPanel.add(cancelButton);
        
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Sets up the action listeners
     */
    private void setupListeners() {
        // Login button action
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        
        // Cancel button action
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeDialog();
            }
        });
        
        // Window close action
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                closeDialog();
            }
        });
    }
    
    /**
     * Attempts to log in with the provided credentials
     */
    private void login() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(
                this,
                "Please enter both username and password.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE
            );
            return;
        }
        
        if (AuthController.authenticate(username, password)) {
            // Authentication successful, open the main dashboard
            openDashboard();
        } else {
            // Authentication failed
            JOptionPane.showMessageDialog(
                this,
                "Invalid username or password.",
                "Login Error",
                JOptionPane.ERROR_MESSAGE
            );
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
    
    /**
     * Opens the main dashboard
     */
    private void openDashboard() {
        dispose();
        
        // Open the SuperAdmin dashboard
        SuperAdminDashboard dashboard = new SuperAdminDashboard();
        dashboard.setVisible(true);
    }
    
    /**
     * Closes the dialog
     */
    private void closeDialog() {
        dispose();
        
        // If this is the main login dialog (owner is null), exit the application
        if (getOwner() == null) {
            System.exit(0);
        }
    }
}