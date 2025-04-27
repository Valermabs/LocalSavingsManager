package com.moscat.views;

import com.moscat.controllers.AuthController;
import com.moscat.models.User;
import com.moscat.utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Base class for all dashboard views
 */
public abstract class DashboardView extends JDialog {
    
    protected JPanel mainPanel;
    protected JPanel menuPanel;
    protected JPanel contentPanel;
    protected JLabel userInfoLabel;
    
    /**
     * Constructs a new DashboardView
     * 
     * @param parentFrame Parent JFrame
     */
    public DashboardView(JFrame parentFrame) {
        super(parentFrame, true);
        
        // Basic dialog setup
        setSize(1000, 700);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        // Create main panel with BorderLayout
        mainPanel = new JPanel(new BorderLayout());
        
        // Create top bar
        JPanel topBar = createTopBar();
        mainPanel.add(topBar, BorderLayout.NORTH);
        
        // Create menu panel
        menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.WEST);
        
        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Set content pane
        setContentPane(mainPanel);
        
        // Show dashboard by default
        showDashboard();
    }
    
    /**
     * Creates the top bar
     * 
     * @return JPanel with top bar
     */
    private JPanel createTopBar() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(51, 102, 153));
        panel.setPreferredSize(new Dimension(0, 50));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        
        // Application name
        JLabel appNameLabel = new JLabel("MOSCAT Cooperative");
        appNameLabel.setFont(new Font("Arial", Font.BOLD, 18));
        appNameLabel.setForeground(Color.WHITE);
        
        // User info
        User currentUser = AuthController.getCurrentUser();
        String userInfo = currentUser != null 
                ? currentUser.getFullName() + " (" + currentUser.getRole() + ")" 
                : "Guest";
        
        userInfoLabel = new JLabel(userInfo);
        userInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userInfoLabel.setForeground(Color.WHITE);
        
        // Logout button
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(e -> logout());
        
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setOpaque(false);
        rightPanel.add(userInfoLabel);
        rightPanel.add(logoutButton);
        
        panel.add(appNameLabel, BorderLayout.WEST);
        panel.add(rightPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates the menu panel
     * 
     * @return JPanel with menu
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(240, 240, 240));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(new EmptyBorder(20, 15, 20, 15));
        
        // Get menu items
        String[] menuItems = getMenuItems();
        
        // Create menu buttons
        for (String menuItem : menuItems) {
            JButton menuButton = new JButton(menuItem);
            menuButton.setAlignmentX(Component.LEFT_ALIGNMENT);
            menuButton.setMaximumSize(new Dimension(170, 35));
            menuButton.setFocusPainted(false);
            
            menuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleMenuItemSelection(menuItem);
                }
            });
            
            panel.add(menuButton);
            panel.add(Box.createVerticalStrut(10));
        }
        
        return panel;
    }
    
    /**
     * Logs out the current user
     */
    private void logout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            AuthController.logout();
            dispose();
            
            // Show login dialog
            JFrame parentFrame = (JFrame) getParent();
            new LoginView(parentFrame).setVisible(true);
        }
    }
    
    /**
     * Gets menu items based on role
     * 
     * @return Array of menu items
     */
    protected abstract String[] getMenuItems();
    
    /**
     * Handles menu item selection
     * 
     * @param selectedItem Selected menu item
     */
    protected abstract void handleMenuItemSelection(String selectedItem);
    
    /**
     * Shows the dashboard screen
     */
    protected abstract void showDashboard();
    
    /**
     * Shows the member management screen
     */
    protected void showMemberManagement() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Member management functionality will be implemented in future updates.");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the savings management screen
     */
    protected void showSavingsManagement() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Savings management functionality will be implemented in future updates.");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the loan management screen
     */
    protected void showLoanManagement() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Loan management functionality will be implemented in future updates.");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the transaction history screen
     */
    protected void showTransactionHistory() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Transaction history functionality will be implemented in future updates.");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the reports screen
     */
    protected void showReports() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Reports functionality will be implemented in future updates.");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the settings screen
     */
    protected void showSettings() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Settings functionality will be implemented in future updates.");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the account screen
     */
    protected void showMyAccount() {
        contentPanel.removeAll();
        
        User currentUser = AuthController.getCurrentUser();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("My Account");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel nameLabel = new JLabel("Name: " + currentUser.getFullName());
        nameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel usernameLabel = new JLabel("Username: " + currentUser.getUsername());
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel emailLabel = new JLabel("Email: " + currentUser.getEmail());
        emailLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel roleLabel = new JLabel("Role: " + currentUser.getRole());
        roleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        changePasswordButton.addActionListener(e -> showChangePasswordDialog());
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(usernameLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(emailLabel);
        panel.add(Box.createVerticalStrut(10));
        panel.add(roleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(changePasswordButton);
        
        contentPanel.add(panel, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the change password dialog
     */
    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JPasswordField currentPasswordField = new JPasswordField();
        JPasswordField newPasswordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        
        panel.add(new JLabel("Current Password:"));
        panel.add(currentPasswordField);
        panel.add(new JLabel("New Password:"));
        panel.add(newPasswordField);
        panel.add(new JLabel("Confirm Password:"));
        panel.add(confirmPasswordField);
        
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Change Password",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
        
        if (result == JOptionPane.OK_OPTION) {
            String currentPassword = new String(currentPasswordField.getPassword());
            String newPassword = new String(newPasswordField.getPassword());
            String confirmPassword = new String(confirmPasswordField.getPassword());
            
            // Validate input
            if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "All fields are required.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            if (!newPassword.equals(confirmPassword)) {
                JOptionPane.showMessageDialog(
                        this,
                        "New password and confirmation do not match.",
                        "Input Error",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Attempt to change password
            User currentUser = AuthController.getCurrentUser();
            Map<String, Object> passwordResult = AuthController.changePassword(
                    currentUser.getId(),
                    currentPassword,
                    newPassword);
            
            boolean success = (boolean) passwordResult.get("success");
            if (success) {
                JOptionPane.showMessageDialog(
                        this,
                        "Password changed successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "Failed to change password. Please verify your current password.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}