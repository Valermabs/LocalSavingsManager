package com.moscat.controllers;

import com.moscat.models.Member;
import com.moscat.models.SavingsAccount;
import com.moscat.models.Transaction;
import com.moscat.models.User;
import com.moscat.utils.Constants;
import com.moscat.utils.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller for report generation and export
 */
public class ReportController {
    
    /**
     * Generates a member report panel
     * 
     * @param member Member to report on
     * @return JPanel containing report
     */
    public static JPanel generateMemberReport(Member member) {
        if (member == null) {
            JPanel errorPanel = new JPanel();
            errorPanel.add(new JLabel("Error: Member not found"));
            return errorPanel;
        }
        
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BorderLayout());
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create report header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Member Profile Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel dateLabel = new JLabel("Generated on: " + DateUtils.formatDateForDisplay(new Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // Create member details panel
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Member Information"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Add member details
        addLabelPair(detailsPanel, "Member Number:", member.getMemberNumber(), gbc, 0);
        addLabelPair(detailsPanel, "Name:", member.getFullName(), gbc, 1);
        addLabelPair(detailsPanel, "Age:", String.valueOf(member.getAge()), gbc, 2);
        addLabelPair(detailsPanel, "Birth Date:", DateUtils.formatDateForDisplay(member.getBirthDate()), gbc, 3);
        addLabelPair(detailsPanel, "Contact Number:", member.getContactNumber(), gbc, 4);
        addLabelPair(detailsPanel, "Email:", member.getEmail(), gbc, 5);
        addLabelPair(detailsPanel, "Present Address:", member.getPresentAddress(), gbc, 6);
        addLabelPair(detailsPanel, "Permanent Address:", member.getPermanentAddress(), gbc, 7);
        addLabelPair(detailsPanel, "Employer:", member.getEmployer(), gbc, 8);
        addLabelPair(detailsPanel, "Employment Status:", member.getEmploymentStatus(), gbc, 9);
        addLabelPair(detailsPanel, "Gross Monthly Income:", String.format("₱%.2f", member.getGrossMonthlyIncome()), gbc, 10);
        addLabelPair(detailsPanel, "Avg. Net Monthly Income:", String.format("₱%.2f", member.getAvgNetMonthlyIncome()), gbc, 11);
        addLabelPair(detailsPanel, "Status:", member.getStatus(), gbc, 12);
        addLabelPair(detailsPanel, "Join Date:", DateUtils.formatDateForDisplay(member.getJoinDate()), gbc, 13);
        addLabelPair(detailsPanel, "Last Activity Date:", DateUtils.formatDateForDisplay(member.getLastActivityDate()), gbc, 14);
        
        // Get savings accounts for member
        List<SavingsAccount> accounts = MemberController.getMemberSavingsAccounts(member.getId());
        
        // Create account summary panel
        JPanel accountsPanel = new JPanel(new BorderLayout());
        accountsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Savings Accounts"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        if (accounts.isEmpty()) {
            accountsPanel.add(new JLabel("No savings accounts found for this member."), BorderLayout.CENTER);
        } else {
            // Create table model
            String[] columnNames = {"Account Number", "Balance", "Interest Earned", "Total Balance", "Status", "Open Date"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Add account data
            for (SavingsAccount account : accounts) {
                Object[] rowData = {
                    account.getAccountNumber(),
                    String.format("₱%.2f", account.getBalance()),
                    String.format("₱%.2f", account.getInterestEarned()),
                    String.format("₱%.2f", account.getTotalBalance()),
                    account.getStatus(),
                    DateUtils.formatDateForDisplay(account.getOpenDate())
                };
                model.addRow(rowData);
            }
            
            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowHeight(25);
            table.getTableHeader().setReorderingAllowed(false);
            
            accountsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        }
        
        // Get recent transactions for member's accounts
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Recent Transactions"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        if (accounts.isEmpty()) {
            transactionsPanel.add(new JLabel("No accounts available for transaction history."), BorderLayout.CENTER);
        } else {
            // Create table model
            String[] columnNames = {"Date", "Account", "Type", "Amount", "Balance", "Description"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Add transaction data (last 10 transactions)
            for (SavingsAccount account : accounts) {
                List<Transaction> transactions = TransactionController.getAccountTransactions(account.getId(), 5);
                for (Transaction transaction : transactions) {
                    Object[] rowData = {
                        DateUtils.formatDateForDisplay(transaction.getTransactionDate()),
                        account.getAccountNumber(),
                        getTransactionTypeDisplay(transaction.getTransactionType()),
                        String.format("₱%.2f", transaction.getAmount()),
                        String.format("₱%.2f", transaction.getRunningBalance()),
                        transaction.getDescription()
                    };
                    model.addRow(rowData);
                }
            }
            
            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowHeight(25);
            table.getTableHeader().setReorderingAllowed(false);
            
            transactionsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        }
        
        // Combine panels
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(detailsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(accountsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(transactionsPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        
        // Add all to main panel
        reportPanel.add(headerPanel, BorderLayout.NORTH);
        reportPanel.add(scrollPane, BorderLayout.CENTER);
        
        return reportPanel;
    }
    
    /**
     * Generates a daily transaction report panel
     * 
     * @param date Date string (yyyy-MM-dd)
     * @return JPanel containing report
     */
    public static JPanel generateDailyTransactionReport(String date) {
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BorderLayout());
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create report header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Daily Transaction Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel dateLabel = new JLabel("Date: " + date + " | Generated on: " + DateUtils.formatDateForDisplay(new Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // Get transaction summaries
        List<TransactionController.TransactionSummary> summaries = TransactionController.getDailyTransactionSummary(date);
        
        // Create summary panel
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Transaction Summary"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        // Create table model
        String[] summaryColumns = {"Transaction Type", "Total Amount"};
        DefaultTableModel summaryModel = new DefaultTableModel(summaryColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Add summary data
        double grandTotal = 0;
        for (TransactionController.TransactionSummary summary : summaries) {
            Object[] rowData = {
                summary.getTransactionTypeDisplay(),
                String.format("₱%.2f", summary.getTotalAmount())
            };
            summaryModel.addRow(rowData);
            grandTotal += summary.getTotalAmount();
        }
        
        // Add grand total
        Object[] totalRow = {"GRAND TOTAL", String.format("₱%.2f", grandTotal)};
        summaryModel.addRow(totalRow);
        
        JTable summaryTable = new JTable(summaryModel);
        summaryTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        summaryTable.setRowHeight(25);
        summaryTable.getTableHeader().setReorderingAllowed(false);
        
        summaryPanel.add(new JScrollPane(summaryTable), BorderLayout.CENTER);
        
        // Get transactions for the day
        List<Transaction> transactions = TransactionController.getTransactionsByDateRange(date, date);
        
        // Create transactions panel
        JPanel transactionsPanel = new JPanel(new BorderLayout());
        transactionsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Transaction Details"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        if (transactions.isEmpty()) {
            transactionsPanel.add(new JLabel("No transactions found for this date."), BorderLayout.CENTER);
        } else {
            // Create table model
            String[] columnNames = {"Time", "Reference", "Account", "Type", "Amount", "Running Balance", "Description", "User"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            // Add transaction data
            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
            for (Transaction transaction : transactions) {
                SavingsAccount account = SavingsController.getAccountById(transaction.getAccountId());
                String accountNumber = account != null ? account.getAccountNumber() : "N/A";
                
                User user = null; // We'll implement getUserById later
                String userName = "System";
                
                Object[] rowData = {
                    timeFormat.format(transaction.getTransactionDate()),
                    transaction.getReferenceNumber(),
                    accountNumber,
                    getTransactionTypeDisplay(transaction.getTransactionType()),
                    String.format("₱%.2f", transaction.getAmount()),
                    String.format("₱%.2f", transaction.getRunningBalance()),
                    transaction.getDescription(),
                    userName
                };
                model.addRow(rowData);
            }
            
            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowHeight(25);
            table.getTableHeader().setReorderingAllowed(false);
            
            transactionsPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        }
        
        // Combine panels
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(summaryPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(transactionsPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        
        // Add all to main panel
        reportPanel.add(headerPanel, BorderLayout.NORTH);
        reportPanel.add(scrollPane, BorderLayout.CENTER);
        
        return reportPanel;
    }
    
    /**
     * Generates a monthly transaction report panel
     * 
     * @param year Year
     * @param month Month (1-12)
     * @return JPanel containing report
     */
    public static JPanel generateMonthlyTransactionReport(int year, int month) {
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BorderLayout());
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Format month name
        String[] monthNames = {"January", "February", "March", "April", "May", "June", 
                "July", "August", "September", "October", "November", "December"};
        String monthName = monthNames[month - 1];
        
        // Create report header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Monthly Transaction Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel dateLabel = new JLabel("Month: " + monthName + " " + year + " | Generated on: " + 
                DateUtils.formatDateForDisplay(new Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // Get month summary
        // NOTE: We need to implement getMonthlyTransactionSummary in TransactionController
        // This is a placeholder until we implement that method
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Monthly Summary"),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        // For now, just show a message
        JLabel placeholderLabel = new JLabel("Monthly transaction summary will be displayed here.");
        summaryPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        // Combine panels
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.add(summaryPanel);
        
        JScrollPane scrollPane = new JScrollPane(contentPanel);
        
        // Add all to main panel
        reportPanel.add(headerPanel, BorderLayout.NORTH);
        reportPanel.add(scrollPane, BorderLayout.CENTER);
        
        return reportPanel;
    }
    
    /**
     * Generates a loan summary report panel
     * 
     * @param reportType Type of loan report
     * @return JPanel containing report
     */
    public static JPanel generateLoanSummaryReport(String reportType) {
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BorderLayout());
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create report header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Loan Summary Report: " + reportType);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel dateLabel = new JLabel("Generated on: " + DateUtils.formatDateForDisplay(new Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // For now, just show a message
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        JLabel placeholderLabel = new JLabel("Loan summary report will be implemented in future updates.");
        placeholderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(placeholderLabel);
        
        // Add all to main panel
        reportPanel.add(headerPanel, BorderLayout.NORTH);
        reportPanel.add(contentPanel, BorderLayout.CENTER);
        
        return reportPanel;
    }
    
    /**
     * Generates a savings summary report panel
     * 
     * @param reportType Type of savings report
     * @param period Time period (for interest reports)
     * @return JPanel containing report
     */
    public static JPanel generateSavingsSummaryReport(String reportType, String period) {
        JPanel reportPanel = new JPanel();
        reportPanel.setLayout(new BorderLayout());
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create report header
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        String title = "Savings Summary Report: " + reportType;
        if (reportType.equals("Interest Earnings") && period != null) {
            title += " (" + period + ")";
        }
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel dateLabel = new JLabel("Generated on: " + DateUtils.formatDateForDisplay(new Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // For now, show a placeholder message
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        List<Member> members = MemberController.getActiveMembers();
        List<SavingsAccount> accounts = new ArrayList<>();
        
        // Get relevant accounts based on report type
        for (Member member : members) {
            List<SavingsAccount> memberAccounts = MemberController.getMemberSavingsAccounts(member.getId());
            for (SavingsAccount account : memberAccounts) {
                if (reportType.equals("All Accounts") || 
                        (reportType.equals("Active Accounts") && account.isActive()) ||
                        (reportType.equals("Dormant Accounts") && account.isDormant()) ||
                        reportType.equals("Interest Earnings")) {
                    accounts.add(account);
                }
            }
        }
        
        // Create report content based on report type
        if (accounts.isEmpty()) {
            JLabel noDataLabel = new JLabel("No accounts found matching the specified criteria.");
            noDataLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            contentPanel.add(noDataLabel);
        } else {
            // Create table model
            String[] columnNames = {"Account Number", "Member Name", "Balance", "Interest Earned", "Status"};
            DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            double totalBalance = 0;
            double totalInterest = 0;
            
            // Add account data
            for (SavingsAccount account : accounts) {
                Member member = MemberController.getMemberById(account.getMemberId());
                if (member != null) {
                    Object[] rowData = {
                        account.getAccountNumber(),
                        member.getFullName(),
                        String.format("₱%.2f", account.getBalance()),
                        String.format("₱%.2f", account.getInterestEarned()),
                        account.getStatus()
                    };
                    model.addRow(rowData);
                    
                    totalBalance += account.getBalance();
                    totalInterest += account.getInterestEarned();
                }
            }
            
            // Add total row
            Object[] totalRow = {
                "TOTALS",
                "(" + accounts.size() + " accounts)",
                String.format("₱%.2f", totalBalance),
                String.format("₱%.2f", totalInterest),
                ""
            };
            model.addRow(totalRow);
            
            JTable table = new JTable(model);
            table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            table.setRowHeight(25);
            table.getTableHeader().setReorderingAllowed(false);
            
            JScrollPane tableScrollPane = new JScrollPane(table);
            tableScrollPane.setPreferredSize(new Dimension(0, 300));
            
            contentPanel.add(tableScrollPane);
        }
        
        // Add all to main panel
        reportPanel.add(headerPanel, BorderLayout.NORTH);
        reportPanel.add(contentPanel, BorderLayout.CENTER);
        
        return reportPanel;
    }
    
    /**
     * Exports a JPanel to PDF
     * 
     * @param panel Panel to export
     * @param filePath Path to save PDF
     * @return true if export successful, false otherwise
     */
    public static boolean exportToPDF(JPanel panel, String filePath) {
        // This is a placeholder for PDF export functionality
        // In a real implementation, you would use a library like iText or Apache PDFBox
        
        try {
            // Simulate creating a PDF
            File file = new File(filePath);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write("PDF export placeholder".getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Exports a JTable to CSV
     * 
     * @param table Table to export
     * @param filePath Path to save CSV
     * @return true if export successful, false otherwise
     */
    public static boolean exportToCSV(JTable table, String filePath) {
        return TransactionController.exportTransactionsToCSV(table, filePath);
    }
    
    /**
     * Helper method to add a label pair to a panel
     * 
     * @param panel Panel to add to
     * @param labelText Label text
     * @param valueText Value text
     * @param gbc GridBagConstraints
     * @param row Grid row
     */
    private static void addLabelPair(JPanel panel, String labelText, String valueText, 
            GridBagConstraints gbc, int row) {
        
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.anchor = GridBagConstraints.EAST;
        gbc.gridwidth = 1;
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Arial", Font.BOLD, 12));
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1.0;
        
        JLabel value = new JLabel(valueText != null ? valueText : "");
        panel.add(value, gbc);
    }
    
    /**
     * Gets display text for transaction type
     * 
     * @param transactionType Transaction type code
     * @return Human-readable transaction type
     */
    private static String getTransactionTypeDisplay(String transactionType) {
        switch (transactionType) {
            case Constants.TRANSACTION_DEPOSIT:
                return "Deposit";
            case Constants.TRANSACTION_WITHDRAWAL:
                return "Withdrawal";
            case Constants.TRANSACTION_INTEREST_EARNING:
                return "Interest Earning";
            case Constants.TRANSACTION_LOAN_RELEASE:
                return "Loan Release";
            case Constants.TRANSACTION_LOAN_PAYMENT:
                return "Loan Payment";
            case Constants.TRANSACTION_FEE:
                return "Fee";
            default:
                return transactionType;
        }
    }
}