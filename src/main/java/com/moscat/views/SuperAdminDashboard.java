package com.moscat.views;

import com.moscat.utils.DateUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard view for super admin users
 */
public class SuperAdminDashboard extends DashboardView {
    
    /**
     * Constructs a new SuperAdminDashboard
     * 
     * @param parentFrame Parent JFrame
     */
    public SuperAdminDashboard(JFrame parentFrame) {
        super(parentFrame);
        setTitle("Super Admin Dashboard");
    }
    
    /**
     * Gets available menu items based on role
     * 
     * @return Array of menu items
     */
    @Override
    protected String[] getMenuItems() {
        return new String[] {
            "Dashboard",
            "Member Management",
            "Savings Management",
            "Loan Management",
            "Transaction History",
            "Dormant Accounts",
            "Reports",
            "Users",
            "Settings",
            "My Account"
        };
    }
    
    /**
     * Handles menu item selection
     * 
     * @param selectedItem Selected menu item
     */
    @Override
    protected void handleMenuItemSelection(String selectedItem) {
        switch (selectedItem) {
            case "Dashboard":
                showDashboard();
                break;
            case "Member Management":
                showMemberManagement();
                break;
            case "Savings Management":
                showSavingsManagement();
                break;
            case "Loan Management":
                showLoanManagement();
                break;
            case "Transaction History":
                showTransactionHistory();
                break;
            case "Dormant Accounts":
                showDormantAccounts();
                break;
            case "Reports":
                showReports();
                break;
            case "Users":
                showUserManagement();
                break;
            case "Settings":
                showSettings();
                break;
            case "My Account":
                showMyAccount();
                break;
            default:
                showDashboard();
        }
    }
    
    /**
     * Shows the dashboard screen
     */
    @Override
    protected void showDashboard() {
        contentPanel.removeAll();
        
        // Create dashboard content
        JPanel dashboardContent = createDashboardContent();
        
        contentPanel.add(dashboardContent, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Creates the dashboard content
     * 
     * @return JPanel with dashboard content
     */
    private JPanel createDashboardContent() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel welcomeLabel = new JLabel("Welcome, Super Admin");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel dateLabel = new JLabel("Today: " + DateUtils.formatDateForDisplay(new java.util.Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // Create stats panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 15, 15));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Members card
        JPanel membersCard = createStatsCard("Total Members", "127", "View Members");
        statsPanel.add(membersCard);
        
        // Savings card
        JPanel savingsCard = createStatsCard("Total Savings", "₱2,450,890.75", "View Accounts");
        statsPanel.add(savingsCard);
        
        // Loans card
        JPanel loansCard = createStatsCard("Active Loans", "42", "View Loans");
        statsPanel.add(loansCard);
        
        // Transactions card
        JPanel transactionsCard = createStatsCard("Today's Transactions", "18", "View Transactions");
        statsPanel.add(transactionsCard);
        
        // Users card
        JPanel usersCard = createStatsCard("System Users", "8", "Manage Users");
        statsPanel.add(usersCard);
        
        // Interest card
        JPanel interestCard = createStatsCard("Interest Rate", "2.5%", "Adjust Settings");
        statsPanel.add(interestCard);
        
        // Create recent activities panel
        JPanel activitiesPanel = new JPanel(new BorderLayout());
        activitiesPanel.setBorder(BorderFactory.createTitledBorder("Recent System Activities"));
        
        // For now, just add a placeholder
        JLabel placeholderLabel = new JLabel("Recent system activities will be displayed here.");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        placeholderLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        activitiesPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        // Add panels to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(activitiesPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates a statistics card
     * 
     * @param title Card title
     * @param value Card value
     * @param actionText Action text
     * @return JPanel with stats card
     */
    private JPanel createStatsCard(String title, String value, String actionText) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 220, 220), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        panel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(Color.WHITE);
        centerPanel.add(valueLabel, BorderLayout.CENTER);
        
        JLabel actionLabel = new JLabel("<html><u>" + actionText + "</u></html>");
        actionLabel.setForeground(new Color(0, 102, 204));
        actionLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(actionLabel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Shows the user management screen
     */
    protected void showUserManagement() {
        contentPanel.removeAll();
        
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Add user management tab (placeholder for now)
        // Create and add user management panel
        UserManagementView userManagementView = new UserManagementView(getOwner() instanceof JFrame ? (JFrame) getOwner() : null);
        
        // Add tabs to tabbed pane
        tabbedPane.addTab("User Management", userManagementView);
        
        contentPanel.add(tabbedPane, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the settings screen
     */
    @Override
    protected void showSettings() {
        contentPanel.removeAll();
        
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("System Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton savingsSettingsButton = new JButton("Savings Account Settings");
        savingsSettingsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        savingsSettingsButton.addActionListener(e -> showSavingsSettings());
        
        JButton loanSettingsButton = new JButton("Loan Settings");
        loanSettingsButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton backupButton = new JButton("Database Backup");
        backupButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(20));
        panel.add(savingsSettingsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loanSettingsButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(backupButton);
        
        contentPanel.add(panel, BorderLayout.NORTH);
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the savings settings screen
     */
    protected void showSavingsSettings() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Savings account settings will be implemented in future updates.");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the dormant accounts management screen
     */
    protected void showDormantAccounts() {
        contentPanel.removeAll();
        
        DormantAccountsView dormantAccountsView = new DormantAccountsView(getOwner() instanceof JFrame ? (JFrame) getOwner() : null);
        contentPanel.add(dormantAccountsView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
}