package com.moscat.views;

import com.moscat.models.Transaction;
import com.moscat.controllers.TransactionController;
import com.moscat.utils.Constants;
import com.moscat.utils.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Dashboard view for bookkeeper users
 */
public class BookkeeperDashboard extends DashboardView {
    
    /**
     * Constructs a new BookkeeperDashboard
     * 
     * @param parentFrame Parent JFrame
     */
    public BookkeeperDashboard(JFrame parentFrame) {
        super(parentFrame);
        setTitle("Bookkeeper Dashboard");
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
        
        JLabel welcomeLabel = new JLabel("Welcome, Bookkeeper");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel dateLabel = new JLabel("Today: " + DateUtils.formatDateForDisplay(new java.util.Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        headerPanel.add(welcomeLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // Create stats panel
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        statsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Transactions today card
        JPanel todayTransactionsCard = createStatsCard("Transactions Today", "25", "View Details");
        statsPanel.add(todayTransactionsCard);
        
        // New members card
        JPanel newMembersCard = createStatsCard("New Members This Month", "5", "View Details");
        statsPanel.add(newMembersCard);
        
        // Active loans card
        JPanel activeLoansCard = createStatsCard("Active Loans", "42", "View Details");
        statsPanel.add(activeLoansCard);
        
        // Create recent transactions panel
        JPanel recentTransactionsPanel = createRecentTransactionsPanel();
        
        // Add panels to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(statsPanel, BorderLayout.CENTER);
        panel.add(recentTransactionsPanel, BorderLayout.SOUTH);
        
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
     * Creates the recent transactions panel
     * 
     * @return JPanel with recent transactions
     */
    private JPanel createRecentTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel titleLabel = new JLabel("Recent Transactions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Create table
        String[] columnNames = {"Date", "Reference", "Type", "Amount", "Balance", "Description"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add recent transactions
        List<Transaction> recentTransactions = TransactionController.getRecentTransactions(10);
        for (Transaction transaction : recentTransactions) {
            Object[] rowData = {
                DateUtils.formatDateForDisplay(transaction.getTransactionDate()),
                transaction.getReferenceNumber(),
                getTransactionTypeDisplay(transaction.getTransactionType()),
                String.format("₱%.2f", transaction.getAmount()),
                String.format("₱%.2f", transaction.getRunningBalance()),
                transaction.getDescription()
            };
            model.addRow(rowData);
        }
        
        JTable table = new JTable(model);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setRowHeight(30);
        table.getTableHeader().setReorderingAllowed(false);
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(0, 250));
        
        // Add components to panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Shows the member management screen
     */
    @Override
    protected void showMemberManagement() {
        contentPanel.removeAll();
        
        MemberListView memberListView = new MemberListView(parentFrame);
        contentPanel.add(memberListView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the savings management screen
     */
    @Override
    protected void showSavingsManagement() {
        contentPanel.removeAll();
        
        SavingsView savingsView = new SavingsView(parentFrame);
        contentPanel.add(savingsView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the transaction history screen
     */
    @Override
    protected void showTransactionHistory() {
        contentPanel.removeAll();
        
        TransactionHistoryView transactionHistoryView = new TransactionHistoryView(parentFrame);
        contentPanel.add(transactionHistoryView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the user account screen
     */
    @Override
    protected void showMyAccount() {
        contentPanel.removeAll();
        
        UserAccountView userAccountView = new UserAccountView(parentFrame);
        contentPanel.add(userAccountView, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Gets display text for transaction type
     * 
     * @param transactionType Transaction type code
     * @return Human-readable transaction type
     */
    @Override
    protected String getTransactionTypeDisplay(String transactionType) {
        return super.getTransactionTypeDisplay(transactionType);
    }
}