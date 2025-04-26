package com.moscat.views;

import com.moscat.utils.DateUtils;

import javax.swing.*;
import java.awt.*;

/**
 * Dashboard view for treasurer users
 */
public class TreasurerDashboard extends DashboardView {
    
    /**
     * Constructs a new TreasurerDashboard
     * 
     * @param parentFrame Parent JFrame
     */
    public TreasurerDashboard(JFrame parentFrame) {
        super(parentFrame);
        setTitle("Treasurer Dashboard");
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
            "Reports",
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
            case "Reports":
                showReports();
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
        
        JLabel welcomeLabel = new JLabel("Welcome, Treasurer");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel dateLabel = new JLabel("Today: " + DateUtils.formatDateForDisplay(new java.util.Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // Create stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create cards
        JPanel totalCashCard = createStatsCard("Total Cash", "₱1,245,890.50", "View Details");
        JPanel todayDepositCard = createStatsCard("Today's Deposits", "₱45,780.25", "View Transactions");
        JPanel todayWithdrawalCard = createStatsCard("Today's Withdrawals", "₱32,450.75", "View Transactions");
        
        statsPanel.add(totalCashCard);
        statsPanel.add(todayDepositCard);
        statsPanel.add(todayWithdrawalCard);
        
        // Create action buttons panel
        JPanel actionsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        actionsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JButton depositButton = new JButton("New Deposit");
        JButton withdrawalButton = new JButton("New Withdrawal");
        JButton loanPaymentButton = new JButton("Loan Payment");
        
        actionsPanel.add(depositButton);
        actionsPanel.add(withdrawalButton);
        actionsPanel.add(loanPaymentButton);
        
        // Create recent transactions panel
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        
        JLabel placeholderLabel = new JLabel("Recent transactions will be displayed here.");
        placeholderLabel.setHorizontalAlignment(SwingConstants.CENTER);
        placeholderLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        transactionsPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        // Add panels to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(statsPanel, BorderLayout.NORTH);
        centerPanel.add(actionsPanel, BorderLayout.CENTER);
        
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(transactionsPanel, BorderLayout.SOUTH);
        
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
        valueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        
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
}