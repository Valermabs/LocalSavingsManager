package com.moscat.views;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * View for user account management
 */
public class UserAccountView extends JPanel {
    
    private JFrame parentFrame;
    
    /**
     * Constructor
     * 
     * @param parentFrame Parent frame
     */
    public UserAccountView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
    }
    
    /**
     * Initializes UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("My Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Create content panel with placeholder message
        JPanel contentPanel = new JPanel(new BorderLayout());
        JLabel placeholderLabel = new JLabel("User account management functionality will be implemented in future updates.");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
}