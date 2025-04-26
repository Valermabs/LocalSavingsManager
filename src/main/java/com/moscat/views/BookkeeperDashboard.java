package com.moscat.views;

import com.moscat.controllers.AdminController;
import com.moscat.controllers.LoanController;
import com.moscat.controllers.MemberController;
import com.moscat.controllers.TransactionController;
import com.moscat.models.Member;
import com.moscat.models.Transaction;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Dashboard view for bookkeeper users with report generation and transaction viewing capabilities
 */
public class BookkeeperDashboard extends DashboardView {
    
    private JTable recentTransactionsTable;
    private DefaultTableModel transactionsModel;
    
    /**
     * Constructor for BookkeeperDashboard
     * 
     * @param parentFrame Parent JFrame
     */
    public BookkeeperDashboard(JFrame parentFrame) {
        super(parentFrame);
        
        // Add bookkeeper-specific panels
        JPanel recentTransactionsPanel = createRecentTransactionsPanel();
        
        // Create a split pane for the content
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                statsPanel,
                recentTransactionsPanel);
        splitPane.setResizeWeight(0.4);
        
        // Replace stats panel with split pane
        contentPanel.remove(statsPanel);
        contentPanel.add(splitPane, BorderLayout.CENTER);
        
        // Add bookkeeper actions panel
        JPanel bookkeeperActionsPanel = createBookkeeperActionsPanel();
        contentPanel.add(bookkeeperActionsPanel, BorderLayout.SOUTH);
        
        // Load recent transactions
        loadRecentTransactions();
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Creates a panel with recent transactions
     * 
     * @return JPanel with transactions table
     */
    private JPanel createRecentTransactionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Recent Transactions"));
        
        // Create table model for transactions
        String[] columns = {"Date/Time", "Type", "Account", "Member", "Amount", "Balance", "Reference"};
        transactionsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        recentTransactionsTable = new JTable(transactionsModel);
        JScrollPane scrollPane = new JScrollPane(recentTransactionsTable);
        
        // Create filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JLabel dateLabel = new JLabel("Date:");
        JTextField dateField = new JTextField(10);
        dateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date())); // Today's date
        
        JButton filterButton = new JButton("Filter");
        JButton refreshButton = new JButton("Refresh");
        
        // Filter button action
        filterButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String dateFilter = dateField.getText().trim();
                if (!dateFilter.isEmpty()) {
                    loadTransactionsByDate(dateFilter);
                }
            }
        });
        
        // Refresh button action
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRecentTransactions();
            }
        });
        
        filterPanel.add(dateLabel);
        filterPanel.add(dateField);
        filterPanel.add(filterButton);
        filterPanel.add(refreshButton);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a panel with bookkeeper actions
     * 
     * @return JPanel with bookkeeper actions
     */
    private JPanel createBookkeeperActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Bookkeeper Actions"));
        
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Daily journal button
        JButton journalButton = new JButton("Generate Daily Journal");
        journalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = JOptionPane.showInputDialog(
                        parentFrame,
                        "Enter date (yyyy-MM-dd):",
                        new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                
                if (date != null && !date.isEmpty()) {
                    showDailyJournal(date);
                }
            }
        });
        
        // Transaction history button
        JButton historyButton = new JButton("Member Transaction History");
        historyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMemberTransactionHistory();
            }
        });
        
        // Generate report button
        JButton reportButton = new JButton("Generate Reports");
        reportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showReports();
            }
        });
        
        buttonsPanel.add(journalButton);
        buttonsPanel.add(historyButton);
        buttonsPanel.add(reportButton);
        
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Loads recent transactions into the table
     */
    private void loadRecentTransactions() {
        SwingWorker<List<Transaction>, Void> worker = new SwingWorker<List<Transaction>, Void>() {
            @Override
            protected List<Transaction> doInBackground() throws Exception {
                // Get today's date
                String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                return TransactionController.getTransactionsByDateRange(today, today);
            }
            
            @Override
            protected void done() {
                try {
                    List<Transaction> transactions = get();
                    
                    // Clear the table
                    transactionsModel.setRowCount(0);
                    
                    // Add transactions to the table
                    for (Transaction transaction : transactions) {
                        // Get account and member info
                        com.moscat.models.SavingsAccount account = 
                                com.moscat.controllers.SavingsController.getAccountById(transaction.getAccountId());
                        
                        Member member = null;
                        if (account != null) {
                            member = MemberController.getMemberById(account.getMemberId());
                        }
                        
                        String accountNumber = (account != null) ? account.getAccountNumber() : "";
                        String memberName = (member != null) ? member.getFullName() : "";
                        
                        Object[] row = {
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(transaction.getTransactionDate()),
                            getTransactionTypeDisplay(transaction.getTransactionType()),
                            accountNumber,
                            memberName,
                            "₱ " + currencyFormatter.format(transaction.getAmount()),
                            "₱ " + currencyFormatter.format(transaction.getRunningBalance()),
                            transaction.getReferenceNumber()
                        };
                        
                        transactionsModel.addRow(row);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error loading transactions: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Loads transactions for a specific date
     * 
     * @param date Date string (yyyy-MM-dd)
     */
    private void loadTransactionsByDate(String date) {
        SwingWorker<List<Transaction>, Void> worker = new SwingWorker<List<Transaction>, Void>() {
            @Override
            protected List<Transaction> doInBackground() throws Exception {
                return TransactionController.getTransactionsByDateRange(date, date);
            }
            
            @Override
            protected void done() {
                try {
                    List<Transaction> transactions = get();
                    
                    // Clear the table
                    transactionsModel.setRowCount(0);
                    
                    // Add transactions to the table
                    for (Transaction transaction : transactions) {
                        // Get account and member info
                        com.moscat.models.SavingsAccount account = 
                                com.moscat.controllers.SavingsController.getAccountById(transaction.getAccountId());
                        
                        Member member = null;
                        if (account != null) {
                            member = MemberController.getMemberById(account.getMemberId());
                        }
                        
                        String accountNumber = (account != null) ? account.getAccountNumber() : "";
                        String memberName = (member != null) ? member.getFullName() : "";
                        
                        Object[] row = {
                            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(transaction.getTransactionDate()),
                            getTransactionTypeDisplay(transaction.getTransactionType()),
                            accountNumber,
                            memberName,
                            "₱ " + currencyFormatter.format(transaction.getAmount()),
                            "₱ " + currencyFormatter.format(transaction.getRunningBalance()),
                            transaction.getReferenceNumber()
                        };
                        
                        transactionsModel.addRow(row);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error loading transactions: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Shows the daily journal for a specific date
     * 
     * @param date Date string (yyyy-MM-dd)
     */
    private void showDailyJournal(String date) {
        JDialog dialog = new JDialog(parentFrame, "Daily Journal - " + date, true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(parentFrame);
        
        // Create panel to hold the journal
        JPanel journalPanel = new JPanel(new BorderLayout());
        journalPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Get the daily transaction report
        JPanel reportPanel = com.moscat.controllers.ReportController.generateDailyTransactionReport(date);
        
        // Create export button
        JButton exportButton = new JButton("Export to CSV");
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Export Journal");
                fileChooser.setSelectedFile(new java.io.File("journal_" + date + ".csv"));
                
                int userSelection = fileChooser.showSaveDialog(dialog);
                
                if (userSelection == JFileChooser.APPROVE_OPTION) {
                    java.io.File fileToSave = fileChooser.getSelectedFile();
                    
                    // Ensure file has .csv extension
                    String filePath = fileToSave.getAbsolutePath();
                    if (!filePath.toLowerCase().endsWith(".csv")) {
                        filePath += ".csv";
                    }
                    
                    // Get the table from the report panel
                    JTable table = null;
                    for (Component component : reportPanel.getComponents()) {
                        if (component instanceof JScrollPane) {
                            JScrollPane scrollPane = (JScrollPane) component;
                            if (scrollPane.getViewport().getView() instanceof JTable) {
                                table = (JTable) scrollPane.getViewport().getView();
                                break;
                            }
                        }
                    }
                    
                    if (table != null) {
                        boolean success = com.moscat.controllers.ReportController.exportToCSV(table, filePath);
                        
                        if (success) {
                            JOptionPane.showMessageDialog(dialog, 
                                    "Journal exported successfully.", 
                                    "Export Complete", 
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(dialog, 
                                    "Failed to export journal.", 
                                    "Export Error", 
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "No data table found to export.", 
                                "Export Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(exportButton);
        
        JScrollPane scrollPane = new JScrollPane(reportPanel);
        
        journalPanel.add(scrollPane, BorderLayout.CENTER);
        journalPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(journalPanel, BorderLayout.CENTER);
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows the transaction history for a specific member
     */
    private void showMemberTransactionHistory() {
        String memberNumber = JOptionPane.showInputDialog(
                parentFrame,
                "Enter Member Number:",
                "Member Transaction History",
                JOptionPane.QUESTION_MESSAGE);
        
        if (memberNumber != null && !memberNumber.isEmpty()) {
            Member member = MemberController.getMemberByNumber(memberNumber);
            
            if (member != null) {
                JDialog dialog = new JDialog(parentFrame, "Transaction History - " + member.getFullName(), true);
                dialog.setLayout(new BorderLayout());
                dialog.setSize(800, 600);
                dialog.setLocationRelativeTo(parentFrame);
                
                // Get the member report
                JPanel reportPanel = com.moscat.controllers.ReportController.generateMemberReport(member);
                
                JScrollPane scrollPane = new JScrollPane(reportPanel);
                
                dialog.add(scrollPane, BorderLayout.CENTER);
                
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                        "Member not found.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Gets a display name for a transaction type
     * 
     * @param transactionType Transaction type code
     * @return Display name
     */
    private String getTransactionTypeDisplay(String transactionType) {
        switch (transactionType) {
            case "DEPOSIT":
                return "Deposit";
            case "WITHDRAWAL":
                return "Withdrawal";
            case "LOAN_PAYMENT":
                return "Loan Payment";
            case "LOAN_RELEASE":
                return "Loan Release";
            case "INTEREST_EARNING":
                return "Interest Earning";
            default:
                return transactionType;
        }
    }
}
