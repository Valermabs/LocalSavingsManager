package com.moscat.views;

import com.moscat.App;
import com.moscat.controllers.AdminController;
import com.moscat.controllers.AuthController;
import com.moscat.controllers.MemberController;
import com.moscat.views.components.CustomButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Base dashboard view with common functionality for all user roles
 */
public class DashboardView extends JPanel {
    
    protected JFrame parentFrame;
    protected JPanel contentPanel;
    protected JPanel sidebarPanel;
    protected JPanel statsPanel;
    protected final DecimalFormat currencyFormatter = new DecimalFormat("#,##0.00");
    protected final SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM dd, yyyy");
    
    /**
     * Constructor for DashboardView
     * 
     * @param parentFrame Parent JFrame
     */
    public DashboardView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
        loadData();
    }
    
    /**
     * Initializes the UI components
     */
    protected void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);
        
        // Create sidebar
        sidebarPanel = createSidebarPanel();
        JScrollPane sidebarScrollPane = new JScrollPane(sidebarPanel);
        sidebarScrollPane.setBorder(null);
        sidebarScrollPane.setPreferredSize(new Dimension(200, 0));
        
        // Create main content area
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create dashboard home content
        JPanel dashboardHome = createDashboardHome();
        contentPanel.add(dashboardHome, BorderLayout.CENTER);
        
        // Add sidebar and content to main panel
        add(sidebarScrollPane, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);
        
        // Create footer
        JPanel footerPanel = createFooterPanel();
        add(footerPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the header panel with application title and user info
     * 
     * @return Header panel
     */
    protected JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(60, 63, 65));
        panel.setPreferredSize(new Dimension(0, 50));
        panel.setBorder(new EmptyBorder(0, 10, 0, 10));
        
        // Application title
        JLabel titleLabel = new JLabel("MOSCAT Multipurpose Cooperative");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // User info and logout
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        String username = "Guest";
        String role = "";
        
        if (AuthController.getCurrentUser() != null) {
            username = AuthController.getCurrentUser().getUsername();
            role = AuthController.getCurrentUser().getRole();
        }
        
        JLabel userLabel = new JLabel(username + " (" + role + ")");
        userLabel.setForeground(Color.WHITE);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFocusPainted(false);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        
        userPanel.add(userLabel);
        userPanel.add(logoutButton);
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(userPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates the sidebar panel with navigation options
     * 
     * @return Sidebar panel
     */
    protected JPanel createSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Common menu items for all users
        addMenuButton(panel, "Dashboard", e -> showDashboardHome());
        
        // Add role-specific options
        if (AuthController.isSuperAdmin() || AuthController.isTreasurer()) {
            addMenuButton(panel, "Members", e -> showMemberList());
            addMenuButton(panel, "Register Member", e -> showMemberRegistration());
            addMenuButton(panel, "Transactions", e -> showTransactions());
            addMenuButton(panel, "Loans", e -> showLoans());
        }
        
        if (AuthController.isBookkeeper()) {
            addMenuButton(panel, "Members", e -> showMemberList());
            addMenuButton(panel, "Transactions", e -> showTransactions());
            addMenuButton(panel, "Loans", e -> showLoans());
        }
        
        // Admin-specific options
        if (AuthController.isSuperAdmin()) {
            addMenuButton(panel, "User Management", e -> showUserManagement());
            addMenuButton(panel, "Savings Settings", e -> showSavingsSettings());
        }
        
        // Reports available to all
        addMenuButton(panel, "Reports", e -> showReports());
        
        // Add a glue component to push everything to the top
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    /**
     * Creates and adds a sidebar menu button
     * 
     * @param panel Panel to add button to
     * @param text Button text
     * @param listener Action listener for button
     */
    protected void addMenuButton(JPanel panel, String text, ActionListener listener) {
        JButton button = new JButton(text);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.setMaximumSize(new Dimension(180, 35));
        button.setFocusPainted(false);
        button.addActionListener(listener);
        
        panel.add(button);
        panel.add(Box.createVerticalStrut(5));
    }
    
    /**
     * Creates the dashboard home content with statistics
     * 
     * @return Dashboard home panel
     */
    protected JPanel createDashboardHome() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Welcome message
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String fullName = "";
        if (AuthController.getCurrentUser() != null && AuthController.getCurrentUser().getFullName() != null) {
            fullName = AuthController.getCurrentUser().getFullName();
        }
        
        JLabel welcomeLabel = new JLabel("Welcome, " + 
                (fullName.isEmpty() ? AuthController.getCurrentUser().getUsername() : fullName));
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomePanel.add(welcomeLabel);
        
        // Date label
        JLabel dateLabel = new JLabel("Today is " + dateFormatter.format(new Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        welcomePanel.add(dateLabel);
        
        // Stats panel
        statsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        statsPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Content scrollpane
        JScrollPane scrollPane = new JScrollPane(statsPanel);
        scrollPane.setBorder(null);
        
        panel.add(welcomePanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates the footer panel
     * 
     * @return Footer panel
     */
    protected JPanel createFooterPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 30));
        panel.setBorder(new EmptyBorder(5, 10, 5, 10));
        panel.setBackground(new Color(240, 240, 240));
        
        JLabel copyrightLabel = new JLabel("© 2025 MOSCAT Multipurpose Cooperative");
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        
        panel.add(copyrightLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    /**
     * Creates a statistic card with title and value
     * 
     * @param title Statistic title
     * @param value Statistic value
     * @param color Card color
     * @return JPanel containing statistic card
     */
    protected JPanel createStatisticCard(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(color);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color.darker(), 1),
                new EmptyBorder(15, 15, 15, 15)));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Loads dashboard data
     */
    protected void loadData() {
        statsPanel.removeAll();
        
        SwingWorker<AdminController.SystemStatistics, Void> worker = new SwingWorker<AdminController.SystemStatistics, Void>() {
            @Override
            protected AdminController.SystemStatistics doInBackground() throws Exception {
                return AdminController.getSystemStatistics();
            }
            
            @Override
            protected void done() {
                try {
                    AdminController.SystemStatistics stats = get();
                    
                    // Add statistic cards based on role
                    if (AuthController.isSuperAdmin() || AuthController.isTreasurer()) {
                        statsPanel.add(createStatisticCard("Total Members", String.valueOf(stats.totalMembers), new Color(220, 220, 255)));
                        statsPanel.add(createStatisticCard("Active Members", String.valueOf(stats.activeMembers), new Color(220, 255, 220)));
                        statsPanel.add(createStatisticCard("Dormant Members", String.valueOf(stats.dormantMembers), new Color(255, 220, 220)));
                        statsPanel.add(createStatisticCard("Total Savings", "₱ " + currencyFormatter.format(stats.totalSavingsBalance), new Color(255, 255, 220)));
                        statsPanel.add(createStatisticCard("Interest Earned", "₱ " + currencyFormatter.format(stats.totalInterestEarned), new Color(220, 255, 255)));
                        statsPanel.add(createStatisticCard("Active Loans", String.valueOf(stats.activeLoans), new Color(255, 220, 255)));
                        statsPanel.add(createStatisticCard("Loan Portfolio", "₱ " + currencyFormatter.format(stats.totalLoanPortfolio), new Color(220, 240, 240)));
                        statsPanel.add(createStatisticCard("Pending Applications", String.valueOf(stats.pendingLoanApplications), new Color(255, 240, 220)));
                    }
                    
                    // Bookkeeper sees less financial data
                    if (AuthController.isBookkeeper()) {
                        statsPanel.add(createStatisticCard("Total Members", String.valueOf(stats.totalMembers), new Color(220, 220, 255)));
                        statsPanel.add(createStatisticCard("Active Members", String.valueOf(stats.activeMembers), new Color(220, 255, 220)));
                        statsPanel.add(createStatisticCard("Dormant Members", String.valueOf(stats.dormantMembers), new Color(255, 220, 220)));
                    }
                    
                    // Today's transaction data (all roles)
                    statsPanel.add(createStatisticCard("Today's Transactions", String.valueOf(stats.transactionsToday), new Color(235, 235, 235)));
                    statsPanel.add(createStatisticCard("Today's Deposits", "₱ " + currencyFormatter.format(stats.depositsToday), new Color(220, 255, 220)));
                    statsPanel.add(createStatisticCard("Today's Withdrawals", "₱ " + currencyFormatter.format(stats.withdrawalsToday), new Color(255, 220, 220)));
                    
                    // Check dormant accounts
                    int dormantUpdated = MemberController.checkAndUpdateDormantAccounts();
                    if (dormantUpdated > 0) {
                        JOptionPane.showMessageDialog(parentFrame, 
                                dormantUpdated + " accounts have been marked as dormant due to inactivity.",
                                "Dormant Accounts Updated", 
                                JOptionPane.INFORMATION_MESSAGE);
                    }
                    
                    // Refresh UI
                    statsPanel.revalidate();
                    statsPanel.repaint();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error loading statistics: " + e.getMessage(),
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Shows the dashboard home view
     */
    protected void showDashboardHome() {
        contentPanel.removeAll();
        
        JPanel dashboardHome = createDashboardHome();
        contentPanel.add(dashboardHome, BorderLayout.CENTER);
        
        loadData();
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the member list view
     */
    protected void showMemberList() {
        contentPanel.removeAll();
        
        MemberListView memberListView = new MemberListView(parentFrame);
        contentPanel.add(memberListView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the member registration view
     */
    protected void showMemberRegistration() {
        if (!AuthController.isSuperAdmin() && !AuthController.isTreasurer()) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "You don't have permission to register members.",
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        contentPanel.removeAll();
        
        MemberRegistrationView registrationView = new MemberRegistrationView(parentFrame);
        contentPanel.add(registrationView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the transactions view
     */
    protected void showTransactions() {
        contentPanel.removeAll();
        
        TransactionView transactionView = new TransactionView(parentFrame);
        contentPanel.add(transactionView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the loans view
     */
    protected void showLoans() {
        contentPanel.removeAll();
        
        LoanApplicationView loanView = new LoanApplicationView(parentFrame);
        contentPanel.add(loanView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the user management view
     */
    protected void showUserManagement() {
        if (!AuthController.isSuperAdmin()) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Only super administrators can manage users.",
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        contentPanel.removeAll();
        
        UserManagementView userManagementView = new UserManagementView(parentFrame);
        contentPanel.add(userManagementView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the savings settings view
     */
    protected void showSavingsSettings() {
        if (!AuthController.isSuperAdmin()) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Only super administrators can change savings settings.",
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        contentPanel.removeAll();
        
        SavingsSettingsView savingsSettingsView = new SavingsSettingsView(parentFrame);
        contentPanel.add(savingsSettingsView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the reports view
     */
    protected void showReports() {
        contentPanel.removeAll();
        
        ReportView reportView = new ReportView(parentFrame);
        contentPanel.add(reportView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Gets display text for transaction type
     * 
     * @param transactionType Transaction type code
     * @return Human-readable transaction type
     */
    protected String getTransactionTypeDisplay(String transactionType) {
        switch (transactionType) {
            case "DEPOSIT":
                return "Deposit";
            case "WITHDRAWAL":
                return "Withdrawal";
            case "INTEREST_EARNING":
                return "Interest Earning";
            case "LOAN_RELEASE":
                return "Loan Release";
            case "LOAN_PAYMENT":
                return "Loan Payment";
            case "FEE":
                return "Fee";
            default:
                return transactionType;
        }
    }
    
    /**
     * Logs out the current user
     */
    protected void logout() {
        int option = JOptionPane.showConfirmDialog(
                parentFrame,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            AuthController.logout();
            App.changeView(new LoginView(parentFrame));
        }
    }
}
