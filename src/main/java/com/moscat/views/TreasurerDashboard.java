package com.moscat.views;

import com.moscat.controllers.LoanController;
import com.moscat.controllers.MemberController;
import com.moscat.controllers.SavingsController;
import com.moscat.models.Loan;
import com.moscat.models.Member;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Dashboard view for treasurer users with transaction and loan management capabilities
 */
public class TreasurerDashboard extends DashboardView {
    
    private JTable pendingLoansTable;
    private DefaultTableModel pendingLoansModel;
    
    /**
     * Constructor for TreasurerDashboard
     * 
     * @param parentFrame Parent JFrame
     */
    public TreasurerDashboard(JFrame parentFrame) {
        super(parentFrame);
        
        // Add treasurer-specific panels
        JPanel treasurerActionsPanel = createTreasurerActionsPanel();
        JPanel pendingLoansPanel = createPendingLoansPanel();
        
        // Create a split pane for the content
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                statsPanel,
                pendingLoansPanel);
        splitPane.setResizeWeight(0.5);
        
        // Replace stats panel with split pane
        contentPanel.remove(statsPanel);
        contentPanel.add(splitPane, BorderLayout.CENTER);
        contentPanel.add(treasurerActionsPanel, BorderLayout.SOUTH);
        
        // Load pending loans
        loadPendingLoans();
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Creates a panel with quick treasurer actions
     * 
     * @return JPanel with treasurer actions
     */
    private JPanel createTreasurerActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Treasurer Actions"));
        
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Deposit button
        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDepositDialog();
            }
        });
        
        // Withdrawal button
        JButton withdrawalButton = new JButton("Withdrawal");
        withdrawalButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showWithdrawalDialog();
            }
        });
        
        // Loan payment button
        JButton loanPaymentButton = new JButton("Loan Payment");
        loanPaymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoanPaymentDialog();
            }
        });
        
        buttonsPanel.add(depositButton);
        buttonsPanel.add(withdrawalButton);
        buttonsPanel.add(loanPaymentButton);
        
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Creates a panel showing pending loan applications
     * 
     * @return JPanel with pending loans table
     */
    private JPanel createPendingLoansPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Pending Loan Applications"));
        
        // Create table model
        String[] columns = {"Loan #", "Member", "Type", "Amount", "Term", "Status", "Application Date"};
        pendingLoansModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        pendingLoansTable = new JTable(pendingLoansModel);
        JScrollPane scrollPane = new JScrollPane(pendingLoansTable);
        
        // Action buttons
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton viewButton = new JButton("View Details");
        JButton approveButton = new JButton("Approve");
        JButton rejectButton = new JButton("Reject");
        JButton refreshButton = new JButton("Refresh");
        
        // View button action
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = pendingLoansTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String loanNumber = (String) pendingLoansModel.getValueAt(selectedRow, 0);
                    showLoanDetailsDialog(loanNumber);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Please select a loan application to view.", 
                            "Selection Required", 
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        // Approve button action
        approveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = pendingLoansTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String loanNumber = (String) pendingLoansModel.getValueAt(selectedRow, 0);
                    approveLoan(loanNumber);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Please select a loan application to approve.", 
                            "Selection Required", 
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        // Reject button action
        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = pendingLoansTable.getSelectedRow();
                if (selectedRow >= 0) {
                    String loanNumber = (String) pendingLoansModel.getValueAt(selectedRow, 0);
                    rejectLoan(loanNumber);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Please select a loan application to reject.", 
                            "Selection Required", 
                            JOptionPane.INFORMATION_MESSAGE);
                }
            }
        });
        
        // Refresh button action
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadPendingLoans();
            }
        });
        
        buttonsPanel.add(viewButton);
        buttonsPanel.add(approveButton);
        buttonsPanel.add(rejectButton);
        buttonsPanel.add(refreshButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Loads pending loan applications into the table
     */
    private void loadPendingLoans() {
        SwingWorker<List<Loan>, Void> worker = new SwingWorker<List<Loan>, Void>() {
            @Override
            protected List<Loan> doInBackground() throws Exception {
                return LoanController.getPendingLoans();
            }
            
            @Override
            protected void done() {
                try {
                    List<Loan> pendingLoans = get();
                    
                    // Clear the table
                    pendingLoansModel.setRowCount(0);
                    
                    // Add pending loans to the table
                    for (Loan loan : pendingLoans) {
                        Member member = MemberController.getMemberById(loan.getMemberId());
                        String memberName = (member != null) ? member.getFullName() : "Unknown";
                        
                        Object[] row = {
                            loan.getLoanNumber(),
                            memberName,
                            loan.getLoanType(),
                            "₱ " + currencyFormatter.format(loan.getPrincipalAmount()),
                            loan.getTermYears() + " years",
                            loan.getStatus(),
                            dateFormatter.format(loan.getApplicationDate())
                        };
                        
                        pendingLoansModel.addRow(row);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error loading pending loans: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Shows a dialog to process a deposit
     */
    private void showDepositDialog() {
        JDialog dialog = new JDialog(parentFrame, "Process Deposit", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Search member
        JLabel memberLabel = new JLabel("Member Number:");
        JTextField memberField = new JTextField(20);
        
        JButton searchButton = new JButton("Search");
        JLabel memberNameLabel = new JLabel("");
        
        // Account info
        JLabel accountLabel = new JLabel("Account Number:");
        JTextField accountField = new JTextField(20);
        accountField.setEditable(false);
        
        JLabel balanceLabel = new JLabel("Current Balance:");
        JTextField balanceField = new JTextField(20);
        balanceField.setEditable(false);
        
        // Deposit amount
        JLabel amountLabel = new JLabel("Deposit Amount:");
        JTextField amountField = new JTextField(20);
        
        // Description
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField(20);
        
        // Add components to form panel
        formPanel.add(memberLabel);
        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(memberField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        formPanel.add(searchPanel);
        formPanel.add(new JLabel("Member Name:"));
        formPanel.add(memberNameLabel);
        formPanel.add(accountLabel);
        formPanel.add(accountField);
        formPanel.add(balanceLabel);
        formPanel.add(balanceField);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);
        
        // Search button action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String memberNumber = memberField.getText().trim();
                if (!memberNumber.isEmpty()) {
                    Member member = MemberController.getMemberByNumber(memberNumber);
                    if (member != null) {
                        memberNameLabel.setText(member.getFullName());
                        
                        // Get account info
                        com.moscat.models.SavingsAccount account = 
                                MemberController.getMemberSavingsAccount(member.getId());
                        
                        if (account != null) {
                            accountField.setText(account.getAccountNumber());
                            balanceField.setText("₱ " + currencyFormatter.format(account.getBalance()));
                        } else {
                            accountField.setText("No account found");
                            balanceField.setText("");
                        }
                    } else {
                        memberNameLabel.setText("Member not found");
                        accountField.setText("");
                        balanceField.setText("");
                    }
                }
            }
        });
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JButton depositButton = new JButton("Process Deposit");
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                String accountNumber = accountField.getText().trim();
                String amountText = amountField.getText().trim();
                String description = descriptionField.getText().trim();
                
                if (accountNumber.isEmpty() || accountNumber.equals("No account found")) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please select a valid member with an account.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (amountText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please enter a deposit amount.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    double amount = Double.parseDouble(amountText);
                    
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(dialog, 
                                "Deposit amount must be greater than zero.", 
                                "Input Error", 
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Get the account
                    com.moscat.models.SavingsAccount account = 
                            SavingsController.getAccountByNumber(accountNumber);
                    
                    if (account != null) {
                        // Process the deposit
                        boolean success = SavingsController.processDeposit(
                                account.getId(), 
                                amount, 
                                description, 
                                com.moscat.controllers.AuthController.getCurrentUser().getId());
                        
                        if (success) {
                            JOptionPane.showMessageDialog(dialog, 
                                    "Deposit processed successfully!", 
                                    "Success", 
                                    JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, 
                                    "Failed to process deposit.", 
                                    "Error", 
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "Account not found.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please enter a valid deposit amount.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(depositButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows a dialog to process a withdrawal
     */
    private void showWithdrawalDialog() {
        JDialog dialog = new JDialog(parentFrame, "Process Withdrawal", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Search member
        JLabel memberLabel = new JLabel("Member Number:");
        JTextField memberField = new JTextField(20);
        
        JButton searchButton = new JButton("Search");
        JLabel memberNameLabel = new JLabel("");
        
        // Account info
        JLabel accountLabel = new JLabel("Account Number:");
        JTextField accountField = new JTextField(20);
        accountField.setEditable(false);
        
        JLabel balanceLabel = new JLabel("Current Balance:");
        JTextField balanceField = new JTextField(20);
        balanceField.setEditable(false);
        
        // Withdrawal amount
        JLabel amountLabel = new JLabel("Withdrawal Amount:");
        JTextField amountField = new JTextField(20);
        
        // Description
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField(20);
        
        // Add components to form panel
        formPanel.add(memberLabel);
        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(memberField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        formPanel.add(searchPanel);
        formPanel.add(new JLabel("Member Name:"));
        formPanel.add(memberNameLabel);
        formPanel.add(accountLabel);
        formPanel.add(accountField);
        formPanel.add(balanceLabel);
        formPanel.add(balanceField);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);
        
        // Search button action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String memberNumber = memberField.getText().trim();
                if (!memberNumber.isEmpty()) {
                    Member member = MemberController.getMemberByNumber(memberNumber);
                    if (member != null) {
                        memberNameLabel.setText(member.getFullName());
                        
                        // Get account info
                        com.moscat.models.SavingsAccount account = 
                                MemberController.getMemberSavingsAccount(member.getId());
                        
                        if (account != null) {
                            accountField.setText(account.getAccountNumber());
                            balanceField.setText("₱ " + currencyFormatter.format(account.getBalance()));
                        } else {
                            accountField.setText("No account found");
                            balanceField.setText("");
                        }
                    } else {
                        memberNameLabel.setText("Member not found");
                        accountField.setText("");
                        balanceField.setText("");
                    }
                }
            }
        });
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JButton withdrawButton = new JButton("Process Withdrawal");
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                String accountNumber = accountField.getText().trim();
                String amountText = amountField.getText().trim();
                String description = descriptionField.getText().trim();
                
                if (accountNumber.isEmpty() || accountNumber.equals("No account found")) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please select a valid member with an account.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (amountText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please enter a withdrawal amount.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    double amount = Double.parseDouble(amountText);
                    
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(dialog, 
                                "Withdrawal amount must be greater than zero.", 
                                "Input Error", 
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Get the account
                    com.moscat.models.SavingsAccount account = 
                            SavingsController.getAccountByNumber(accountNumber);
                    
                    if (account != null) {
                        // Check if sufficient balance
                        if (account.getBalance() < amount) {
                            JOptionPane.showMessageDialog(dialog, 
                                    "Insufficient balance for withdrawal.", 
                                    "Error", 
                                    JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        
                        // Process the withdrawal
                        boolean success = SavingsController.processWithdrawal(
                                account.getId(), 
                                amount, 
                                description, 
                                com.moscat.controllers.AuthController.getCurrentUser().getId());
                        
                        if (success) {
                            JOptionPane.showMessageDialog(dialog, 
                                    "Withdrawal processed successfully!", 
                                    "Success", 
                                    JOptionPane.INFORMATION_MESSAGE);
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, 
                                    "Failed to process withdrawal.", 
                                    "Error", 
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "Account not found.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please enter a valid withdrawal amount.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(withdrawButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows a dialog to process a loan payment
     */
    private void showLoanPaymentDialog() {
        JDialog dialog = new JDialog(parentFrame, "Process Loan Payment", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Search loan
        JLabel loanLabel = new JLabel("Loan Number:");
        JTextField loanField = new JTextField(20);
        
        JButton searchButton = new JButton("Search");
        
        // Loan info
        JLabel memberNameLabel = new JLabel("Member:");
        JTextField memberNameField = new JTextField(20);
        memberNameField.setEditable(false);
        
        JLabel loanTypeLabel = new JLabel("Loan Type:");
        JTextField loanTypeField = new JTextField(20);
        loanTypeField.setEditable(false);
        
        JLabel principalLabel = new JLabel("Principal Amount:");
        JTextField principalField = new JTextField(20);
        principalField.setEditable(false);
        
        JLabel balanceLabel = new JLabel("Remaining Balance:");
        JTextField balanceField = new JTextField(20);
        balanceField.setEditable(false);
        
        JLabel monthlyLabel = new JLabel("Monthly Amortization:");
        JTextField monthlyField = new JTextField(20);
        monthlyField.setEditable(false);
        
        // Account info
        JLabel accountLabel = new JLabel("Savings Account:");
        JTextField accountField = new JTextField(20);
        accountField.setEditable(false);
        
        JLabel accountBalanceLabel = new JLabel("Account Balance:");
        JTextField accountBalanceField = new JTextField(20);
        accountBalanceField.setEditable(false);
        
        // Payment amount
        JLabel amountLabel = new JLabel("Payment Amount:");
        JTextField amountField = new JTextField(20);
        
        // Description
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField(20);
        
        // Add components to form panel
        formPanel.add(loanLabel);
        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(loanField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.EAST);
        
        formPanel.add(searchPanel);
        formPanel.add(memberNameLabel);
        formPanel.add(memberNameField);
        formPanel.add(loanTypeLabel);
        formPanel.add(loanTypeField);
        formPanel.add(principalLabel);
        formPanel.add(principalField);
        formPanel.add(balanceLabel);
        formPanel.add(balanceField);
        formPanel.add(monthlyLabel);
        formPanel.add(monthlyField);
        formPanel.add(accountLabel);
        formPanel.add(accountField);
        formPanel.add(accountBalanceLabel);
        formPanel.add(accountBalanceField);
        formPanel.add(amountLabel);
        formPanel.add(amountField);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);
        
        // Variables to store loan and account data
        final Loan[] selectedLoan = new Loan[1];
        final com.moscat.models.SavingsAccount[] selectedAccount = new com.moscat.models.SavingsAccount[1];
        
        // Search button action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String loanNumber = loanField.getText().trim();
                if (!loanNumber.isEmpty()) {
                    Loan loan = LoanController.getLoanByNumber(loanNumber);
                    if (loan != null && loan.isActive()) {
                        selectedLoan[0] = loan;
                        
                        // Get member info
                        Member member = MemberController.getMemberById(loan.getMemberId());
                        if (member != null) {
                            memberNameField.setText(member.getFullName());
                            
                            // Get account info
                            com.moscat.models.SavingsAccount account = 
                                    MemberController.getMemberSavingsAccount(member.getId());
                            
                            if (account != null) {
                                selectedAccount[0] = account;
                                accountField.setText(account.getAccountNumber());
                                accountBalanceField.setText("₱ " + currencyFormatter.format(account.getBalance()));
                            } else {
                                accountField.setText("No account found");
                                accountBalanceField.setText("");
                            }
                        } else {
                            memberNameField.setText("Member not found");
                        }
                        
                        // Set loan info
                        loanTypeField.setText(loan.getLoanType());
                        principalField.setText("₱ " + currencyFormatter.format(loan.getPrincipalAmount()));
                        balanceField.setText("₱ " + currencyFormatter.format(loan.getRemainingBalance()));
                        monthlyField.setText("₱ " + currencyFormatter.format(loan.getMonthlyAmortization()));
                        
                        // Set suggested payment amount
                        amountField.setText(String.valueOf(loan.getMonthlyAmortization()));
                        descriptionField.setText("Monthly loan payment for " + loanNumber);
                        
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "Loan not found or not active.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        
                        // Clear fields
                        selectedLoan[0] = null;
                        selectedAccount[0] = null;
                        memberNameField.setText("");
                        loanTypeField.setText("");
                        principalField.setText("");
                        balanceField.setText("");
                        monthlyField.setText("");
                        accountField.setText("");
                        accountBalanceField.setText("");
                        amountField.setText("");
                        descriptionField.setText("");
                    }
                }
            }
        });
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JButton payButton = new JButton("Process Payment");
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                if (selectedLoan[0] == null || selectedAccount[0] == null) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please select a valid loan with an associated account.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String amountText = amountField.getText().trim();
                String description = descriptionField.getText().trim();
                
                if (amountText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please enter a payment amount.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    double amount = Double.parseDouble(amountText);
                    
                    if (amount <= 0) {
                        JOptionPane.showMessageDialog(dialog, 
                                "Payment amount must be greater than zero.", 
                                "Input Error", 
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Check if sufficient balance
                    if (selectedAccount[0].getBalance() < amount) {
                        JOptionPane.showMessageDialog(dialog, 
                                "Insufficient balance in savings account for payment.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Process the payment
                    boolean success = LoanController.recordLoanPayment(
                            selectedLoan[0].getId(), 
                            selectedAccount[0].getId(), 
                            amount, 
                            description, 
                            com.moscat.controllers.AuthController.getCurrentUser().getId());
                    
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, 
                                "Loan payment processed successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "Failed to process loan payment.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please enter a valid payment amount.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(payButton);
        
        dialog.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows detailed information about a loan
     * 
     * @param loanNumber Loan number
     */
    private void showLoanDetailsDialog(String loanNumber) {
        JDialog dialog = new JDialog(parentFrame, "Loan Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(parentFrame);
        
        // Load loan data
        Loan loan = LoanController.getLoanByNumber(loanNumber);
        if (loan == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Loan not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get member info
        Member member = MemberController.getMemberById(loan.getMemberId());
        String memberName = (member != null) ? member.getFullName() : "Unknown";
        
        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Loan details panel
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(new TitledBorder("Loan Information"));
        
        detailsPanel.add(new JLabel("Loan Number:"));
        detailsPanel.add(new JLabel(loan.getLoanNumber()));
        
        detailsPanel.add(new JLabel("Member:"));
        detailsPanel.add(new JLabel(memberName));
        
        detailsPanel.add(new JLabel("Loan Type:"));
        detailsPanel.add(new JLabel(loan.getLoanType()));
        
        detailsPanel.add(new JLabel("Principal Amount:"));
        detailsPanel.add(new JLabel("₱ " + currencyFormatter.format(loan.getPrincipalAmount())));
        
        detailsPanel.add(new JLabel("Interest Rate:"));
        detailsPanel.add(new JLabel(loan.getInterestRate() + "%"));
        
        detailsPanel.add(new JLabel("Term:"));
        detailsPanel.add(new JLabel(loan.getTermYears() + " years"));
        
        detailsPanel.add(new JLabel("Previous Loan Balance:"));
        detailsPanel.add(new JLabel("₱ " + currencyFormatter.format(loan.getPreviousLoanBalance())));
        
        detailsPanel.add(new JLabel("Total Deductions:"));
        detailsPanel.add(new JLabel("₱ " + currencyFormatter.format(loan.getTotalDeductions())));
        
        detailsPanel.add(new JLabel("Net Proceeds:"));
        detailsPanel.add(new JLabel("₱ " + currencyFormatter.format(loan.getNetLoanProceeds())));
        
        detailsPanel.add(new JLabel("Monthly Amortization:"));
        detailsPanel.add(new JLabel("₱ " + currencyFormatter.format(loan.getMonthlyAmortization())));
        
        detailsPanel.add(new JLabel("Application Date:"));
        detailsPanel.add(new JLabel(dateFormatter.format(loan.getApplicationDate())));
        
        // Amortization schedule panel
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBorder(new TitledBorder("Amortization Schedule"));
        
        String[] columns = {"Year", "Remaining Principal", "Annual Interest", "Annual Principal", "Monthly Payment"};
        DefaultTableModel scheduleModel = new DefaultTableModel(columns, 0);
        
        for (com.moscat.models.LoanAmortization amortization : loan.getAmortizationSchedule()) {
            Object[] row = {
                amortization.getYear(),
                "₱ " + currencyFormatter.format(amortization.getRemainingPrincipal()),
                "₱ " + currencyFormatter.format(amortization.getAnnualInterest()),
                "₱ " + currencyFormatter.format(amortization.getAnnualPrincipalPayment()),
                "₱ " + currencyFormatter.format(amortization.getMonthlyAmortization())
            };
            
            scheduleModel.addRow(row);
        }
        
        JTable scheduleTable = new JTable(scheduleModel);
        JScrollPane scrollPane = new JScrollPane(scheduleTable);
        
        schedulePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add panels to content panel
        contentPanel.add(detailsPanel, BorderLayout.NORTH);
        contentPanel.add(schedulePanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        buttonsPanel.add(closeButton);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Approves a loan application
     * 
     * @param loanNumber Loan number
     */
    private void approveLoan(String loanNumber) {
        int option = JOptionPane.showConfirmDialog(
                parentFrame,
                "Are you sure you want to approve this loan application?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            Loan loan = LoanController.getLoanByNumber(loanNumber);
            if (loan != null) {
                boolean success = LoanController.approveLoanApplication(
                        loan.getId(), 
                        com.moscat.controllers.AuthController.getCurrentUser().getId());
                
                if (success) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Loan application approved successfully!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the loans table
                    loadPendingLoans();
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Failed to approve loan application.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                        "Loan not found.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Rejects a loan application
     * 
     * @param loanNumber Loan number
     */
    private void rejectLoan(String loanNumber) {
        String reason = JOptionPane.showInputDialog(
                parentFrame,
                "Enter reason for rejection:",
                "Reject Loan Application",
                JOptionPane.QUESTION_MESSAGE);
        
        if (reason != null) {
            Loan loan = LoanController.getLoanByNumber(loanNumber);
            if (loan != null) {
                boolean success = LoanController.rejectLoanApplication(
                        loan.getId(), 
                        com.moscat.controllers.AuthController.getCurrentUser().getId(),
                        reason);
                
                if (success) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Loan application rejected.", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Refresh the loans table
                    loadPendingLoans();
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Failed to reject loan application.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                        "Loan not found.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
