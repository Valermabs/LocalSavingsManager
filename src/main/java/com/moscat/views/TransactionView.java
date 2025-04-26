package com.moscat.views;

import com.moscat.controllers.MemberController;
import com.moscat.controllers.SavingsController;
import com.moscat.controllers.TransactionController;
import com.moscat.models.Member;
import com.moscat.models.SavingsAccount;
import com.moscat.models.Transaction;
import com.moscat.views.components.CustomButton;
import com.moscat.views.components.CustomTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * View for managing and viewing transactions
 */
public class TransactionView extends JPanel {
    
    private JFrame parentFrame;
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private DecimalFormat currencyFormatter = new DecimalFormat("#,##0.00");
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Constructor for TransactionView
     * 
     * @param parentFrame Parent JFrame
     */
    public TransactionView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
        loadRecentTransactions();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create header with title and search
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Transaction Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(15);
        JButton searchButton = new CustomButton("Search");
        
        String[] filterOptions = {"All", "Today", "This Week", "This Month", "Member Number", "Account Number"};
        filterComboBox = new JComboBox<>(filterOptions);
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchTransactions();
            }
        });
        
        searchPanel.add(new JLabel("Filter:"));
        searchPanel.add(filterComboBox);
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Create transaction table
        String[] columns = {"Date/Time", "Type", "Reference", "Account", "Member", "Description", "Amount", "Balance"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        transactionTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(new TitledBorder("Transaction List"));
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton viewButton = new CustomButton("View Details");
        JButton exportButton = new CustomButton("Export");
        JButton refreshButton = new CustomButton("Refresh");
        
        // View button action
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewTransactionDetails();
            }
        });
        
        // Export button action
        exportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exportTransactions();
            }
        });
        
        // Refresh button action
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadRecentTransactions();
            }
        });
        
        buttonsPanel.add(viewButton);
        buttonsPanel.add(exportButton);
        buttonsPanel.add(refreshButton);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Loads recent transactions into the table
     */
    private void loadRecentTransactions() {
        // Clear the table
        tableModel.setRowCount(0);
        
        SwingWorker<List<Transaction>, Void> worker = new SwingWorker<List<Transaction>, Void>() {
            @Override
            protected List<Transaction> doInBackground() throws Exception {
                return TransactionController.getRecentTransactions(100);
            }
            
            @Override
            protected void done() {
                try {
                    List<Transaction> transactions = get();
                    addTransactionsToTable(transactions);
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
     * Searches for transactions based on filter and search term
     */
    private void searchTransactions() {
        String filterType = (String) filterComboBox.getSelectedItem();
        String searchTerm = searchField.getText().trim();
        
        // Clear the table
        tableModel.setRowCount(0);
        
        SwingWorker<List<Transaction>, Void> worker = new SwingWorker<List<Transaction>, Void>() {
            @Override
            protected List<Transaction> doInBackground() throws Exception {
                List<Transaction> transactions = null;
                
                switch (filterType) {
                    case "All":
                        if (searchTerm.isEmpty()) {
                            transactions = TransactionController.getRecentTransactions(100);
                        } else {
                            transactions = TransactionController.searchTransactions(searchTerm);
                        }
                        break;
                    case "Today":
                        String today = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
                        transactions = TransactionController.getTransactionsByDateRange(today, today);
                        break;
                    case "This Week":
                        // Simplified for implementation
                        transactions = TransactionController.getRecentTransactions(50);
                        break;
                    case "This Month":
                        // Simplified for implementation
                        transactions = TransactionController.getRecentTransactions(100);
                        break;
                    case "Member Number":
                        if (!searchTerm.isEmpty()) {
                            Member member = MemberController.getMemberByNumber(searchTerm);
                            if (member != null) {
                                SavingsAccount account = MemberController.getMemberSavingsAccount(member.getId());
                                if (account != null) {
                                    transactions = TransactionController.getAccountTransactions(account.getId(), 100);
                                }
                            }
                        }
                        break;
                    case "Account Number":
                        if (!searchTerm.isEmpty()) {
                            SavingsAccount account = SavingsController.getAccountByNumber(searchTerm);
                            if (account != null) {
                                transactions = TransactionController.getAccountTransactions(account.getId(), 100);
                            }
                        }
                        break;
                }
                
                return transactions;
            }
            
            @Override
            protected void done() {
                try {
                    List<Transaction> transactions = get();
                    addTransactionsToTable(transactions);
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error searching transactions: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Adds transactions to the table
     * 
     * @param transactions List of transactions to add
     */
    private void addTransactionsToTable(List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            tableModel.addRow(new Object[]{"No transactions found", "", "", "", "", "", "", ""});
            return;
        }
        
        for (Transaction transaction : transactions) {
            // Get account and member info
            SavingsAccount account = SavingsController.getAccountById(transaction.getAccountId());
            
            Member member = null;
            if (account != null) {
                member = MemberController.getMemberById(account.getMemberId());
            }
            
            String accountNumber = (account != null) ? account.getAccountNumber() : "";
            String memberName = (member != null) ? member.getFullName() : "";
            
            Object[] row = {
                dateFormatter.format(transaction.getTransactionDate()),
                getTransactionTypeDisplay(transaction.getTransactionType()),
                transaction.getReferenceNumber(),
                accountNumber,
                memberName,
                transaction.getDescription(),
                "₱ " + currencyFormatter.format(transaction.getAmount()),
                "₱ " + currencyFormatter.format(transaction.getRunningBalance())
            };
            
            tableModel.addRow(row);
        }
    }
    
    /**
     * Views details of the selected transaction
     */
    private void viewTransactionDetails() {
        int selectedRow = transactionTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select a transaction to view details.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String referenceNumber = (String) tableModel.getValueAt(selectedRow, 2);
        Transaction transaction = TransactionController.getTransactionByReference(referenceNumber);
        
        if (transaction == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Transaction details not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get account and member info
        SavingsAccount account = SavingsController.getAccountById(transaction.getAccountId());
        
        Member member = null;
        if (account != null) {
            member = MemberController.getMemberById(account.getMemberId());
        }
        
        // Create details dialog
        JDialog dialog = new JDialog(parentFrame, "Transaction Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel titleLabel = new JLabel("Transaction Details");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        
        // Content panel
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        contentPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Add transaction info
        addDetailRow(contentPanel, "Reference Number:", transaction.getReferenceNumber());
        addDetailRow(contentPanel, "Date:", dateFormatter.format(transaction.getTransactionDate()));
        addDetailRow(contentPanel, "Type:", getTransactionTypeDisplay(transaction.getTransactionType()));
        addDetailRow(contentPanel, "Amount:", "₱ " + currencyFormatter.format(transaction.getAmount()));
        addDetailRow(contentPanel, "Running Balance:", "₱ " + currencyFormatter.format(transaction.getRunningBalance()));
        addDetailRow(contentPanel, "Description:", transaction.getDescription());
        
        // Add account info
        if (account != null) {
            addDetailRow(contentPanel, "Account Number:", account.getAccountNumber());
            addDetailRow(contentPanel, "Account Status:", account.getStatus());
        }
        
        // Add member info
        if (member != null) {
            addDetailRow(contentPanel, "Member:", member.getFullName());
            addDetailRow(contentPanel, "Member Number:", member.getMemberNumber());
            addDetailRow(contentPanel, "Contact:", member.getContactNumber());
        }
        
        detailsPanel.add(headerPanel, BorderLayout.NORTH);
        detailsPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonsPanel.add(closeButton);
        
        detailsPanel.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.add(detailsPanel);
        dialog.setVisible(true);
    }
    
    /**
     * Helper method to add a row to the details panel
     * 
     * @param panel Panel to add to
     * @param label Label text
     * @param value Value text
     */
    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel valueComponent = new JLabel(value);
        
        panel.add(labelComponent);
        panel.add(valueComponent);
    }
    
    /**
     * Exports transactions to a CSV file
     */
    private void exportTransactions() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "No transactions to export.", 
                    "Export Error", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Transactions");
        fileChooser.setSelectedFile(new java.io.File("transactions_" + 
                new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".csv"));
        
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            java.io.File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure file has .csv extension
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            
            boolean success = TransactionController.exportTransactionsToCSV(transactionTable, filePath);
            
            if (success) {
                JOptionPane.showMessageDialog(parentFrame, 
                        "Transactions exported successfully.", 
                        "Export Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                        "Failed to export transactions.", 
                        "Export Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Gets display text for transaction type
     * 
     * @param transactionType Transaction type code
     * @return Human-readable transaction type
     */
    private String getTransactionTypeDisplay(String transactionType) {
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
}