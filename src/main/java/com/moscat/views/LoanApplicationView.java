package com.moscat.views;

import com.moscat.controllers.LoanController;
import com.moscat.controllers.MemberController;
import com.moscat.models.Loan;
import com.moscat.models.LoanType;
import com.moscat.models.Member;
import com.moscat.models.AmortizationEntry;
import com.moscat.utils.DateUtils;
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
import java.util.List;

/**
 * View for loan applications and loan management
 */
public class LoanApplicationView extends JPanel {
    
    private JFrame parentFrame;
    private JTabbedPane tabbedPane;
    private JTable loanTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private DecimalFormat currencyFormatter = new DecimalFormat("#,##0.00");
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Constructor for LoanApplicationView
     * 
     * @param parentFrame Parent JFrame
     */
    public LoanApplicationView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
        loadLoans();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create loan list panel
        JPanel loanListPanel = createLoanListPanel();
        
        // Create application form panel
        JPanel applicationPanel = createLoanApplicationPanel();
        
        // Add tabs
        tabbedPane.addTab("Loan List", loanListPanel);
        tabbedPane.addTab("Apply for Loan", applicationPanel);
        
        // Add components to main panel
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    /**
     * Creates the loan list panel
     * 
     * @return JPanel with loan list
     */
    private JPanel createLoanListPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create header with title and search
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Loan Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        JButton searchButton = new CustomButton("Search");
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchLoans(searchField.getText().trim());
            }
        });
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Create loan table
        String[] columns = {"Loan #", "Member", "Type", "Principal", "Term", "Monthly Payment", "Balance", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        loanTable = new JTable(tableModel);
        loanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(loanTable);
        scrollPane.setBorder(new TitledBorder("Loan List"));
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton viewButton = new CustomButton("View Details");
        JButton paymentButton = new CustomButton("Make Payment");
        JButton approveButton = new CustomButton("Approve");
        JButton rejectButton = new CustomButton("Reject");
        JButton refreshButton = new CustomButton("Refresh");
        
        // View button action
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewLoanDetails();
            }
        });
        
        // Payment button action
        paymentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                makeLoanPayment();
            }
        });
        
        // Approve button action
        approveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeLoanStatus(true);
            }
        });
        
        // Reject button action
        rejectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeLoanStatus(false);
            }
        });
        
        // Refresh button action
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadLoans();
            }
        });
        
        buttonsPanel.add(viewButton);
        buttonsPanel.add(paymentButton);
        buttonsPanel.add(approveButton);
        buttonsPanel.add(rejectButton);
        buttonsPanel.add(refreshButton);
        
        // Add components to panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the loan application panel
     * 
     * @return JPanel with loan application form
     */
    private JPanel createLoanApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header
        JLabel titleLabel = new JLabel("Loan Application Form");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Member search
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Member Number:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        CustomTextField memberNumberField = new CustomTextField();
        formPanel.add(memberNumberField, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        JButton searchMemberButton = new JButton("Search");
        formPanel.add(searchMemberButton, gbc);
        
        // Member info labels
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Member Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JLabel memberNameLabel = new JLabel("");
        formPanel.add(memberNameLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Loan Eligibility:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel eligibilityLabel = new JLabel("");
        formPanel.add(eligibilityLabel, gbc);
        
        // Loan type
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Loan Type:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JComboBox<LoanType> loanTypeComboBox = new JComboBox<>();
        formPanel.add(loanTypeComboBox, gbc);
        
        // Load loan types
        List<LoanType> loanTypes = LoanController.getLoanTypes();
        for (LoanType loanType : loanTypes) {
            loanTypeComboBox.addItem(loanType);
        }
        loanTypeComboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                if (value instanceof LoanType) {
                    value = ((LoanType) value).getName();
                }
                return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            }
        });
        
        // Principal amount
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Loan Amount:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        CustomTextField amountField = new CustomTextField();
        formPanel.add(amountField, gbc);
        
        // Term in years
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Term (Years):*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        CustomTextField termField = new CustomTextField();
        formPanel.add(termField, gbc);
        
        // Purpose
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Purpose:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.gridwidth = 2;
        CustomTextField purposeField = new CustomTextField();
        formPanel.add(purposeField, gbc);
        
        // Calculate button
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        JButton calculateButton = new CustomButton("Calculate");
        formPanel.add(calculateButton, gbc);
        
        // Results section
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        JSeparator separator = new JSeparator();
        formPanel.add(separator, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 3;
        JLabel resultsLabel = new JLabel("Loan Calculation Results");
        resultsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(resultsLabel, gbc);
        
        // Results details
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Interest Rate:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        JLabel interestRateLabel = new JLabel("");
        formPanel.add(interestRateLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 11;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Previous Loan Balance:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 11;
        gbc.gridwidth = 2;
        JLabel prevLoanBalanceLabel = new JLabel("");
        formPanel.add(prevLoanBalanceLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 12;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("RLPF Amount:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 12;
        gbc.gridwidth = 2;
        JLabel rlpfLabel = new JLabel("");
        formPanel.add(rlpfLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 13;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Total Deductions:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 13;
        gbc.gridwidth = 2;
        JLabel totalDeductionsLabel = new JLabel("");
        formPanel.add(totalDeductionsLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 14;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Net Loan Proceeds:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 14;
        gbc.gridwidth = 2;
        JLabel netProceedsLabel = new JLabel("");
        formPanel.add(netProceedsLabel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 15;
        gbc.gridwidth = 1;
        formPanel.add(new JLabel("Monthly Payment:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 15;
        gbc.gridwidth = 2;
        JLabel monthlyPaymentLabel = new JLabel("");
        formPanel.add(monthlyPaymentLabel, gbc);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearButton = new JButton("Clear");
        JButton applyButton = new CustomButton("Apply for Loan");
        
        // Search member button action
        searchMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String memberNumber = memberNumberField.getText().trim();
                if (!memberNumber.isEmpty()) {
                    Member member = MemberController.getMemberByNumber(memberNumber);
                    if (member != null) {
                        memberNameLabel.setText(member.getFullName());
                        eligibilityLabel.setText("₱ " + currencyFormatter.format(member.getLoanEligibilityAmount()));
                    } else {
                        memberNameLabel.setText("Member not found");
                        eligibilityLabel.setText("");
                    }
                }
            }
        });
        
        // Calculate button action
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate inputs
                String memberNumber = memberNumberField.getText().trim();
                if (memberNumber.isEmpty() || memberNameLabel.getText().isEmpty() || memberNameLabel.getText().equals("Member not found")) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Please enter a valid member number and search for the member.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                LoanType loanType = (LoanType) loanTypeComboBox.getSelectedItem();
                if (loanType == null) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Please select a loan type.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String amountText = amountField.getText().trim();
                String termText = termField.getText().trim();
                
                if (amountText.isEmpty() || termText.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Please enter loan amount and term.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    double amount = Double.parseDouble(amountText);
                    int termYears = Integer.parseInt(termText);
                    
                    if (amount <= 0 || termYears <= 0) {
                        throw new NumberFormatException("Positive values required");
                    }
                    
                    // Get member
                    Member member = MemberController.getMemberByNumber(memberNumber);
                    
                    // Calculate loan details
                    Loan loanCalculation = LoanController.calculateLoan(member.getId(), loanType.getCode(), amount, termYears);
                    
                    // Display results
                    interestRateLabel.setText(loanCalculation.getInterestRate() + "%");
                    prevLoanBalanceLabel.setText("₱ " + currencyFormatter.format(loanCalculation.getPreviousLoanBalance()));
                    
                    double rlpfAmount = 0;
                    if (loanType.isRequiresRLPF()) {
                        rlpfAmount = (amount / 1000) * 1 * (termYears * 12);
                    }
                    rlpfLabel.setText("₱ " + currencyFormatter.format(rlpfAmount));
                    
                    double totalDeductions = loanCalculation.getPreviousLoanBalance() + rlpfAmount;
                    totalDeductionsLabel.setText("₱ " + currencyFormatter.format(totalDeductions));
                    
                    double netProceeds = amount - totalDeductions;
                    netProceedsLabel.setText("₱ " + currencyFormatter.format(netProceeds));
                    
                    monthlyPaymentLabel.setText("₱ " + currencyFormatter.format(loanCalculation.getMonthlyAmortization()));
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Please enter valid numeric values for amount and term.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        // Clear button action
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                memberNumberField.setText("");
                memberNameLabel.setText("");
                eligibilityLabel.setText("");
                amountField.setText("");
                termField.setText("");
                purposeField.setText("");
                
                interestRateLabel.setText("");
                prevLoanBalanceLabel.setText("");
                rlpfLabel.setText("");
                totalDeductionsLabel.setText("");
                netProceedsLabel.setText("");
                monthlyPaymentLabel.setText("");
            }
        });
        
        // Apply button action
        applyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate inputs
                String memberNumber = memberNumberField.getText().trim();
                if (memberNumber.isEmpty() || memberNameLabel.getText().isEmpty() || memberNameLabel.getText().equals("Member not found")) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Please enter a valid member number and search for the member.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                LoanType loanType = (LoanType) loanTypeComboBox.getSelectedItem();
                if (loanType == null) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Please select a loan type.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                String amountText = amountField.getText().trim();
                String termText = termField.getText().trim();
                String purpose = purposeField.getText().trim();
                
                if (amountText.isEmpty() || termText.isEmpty() || purpose.isEmpty()) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Please enter loan amount, term, and purpose.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                try {
                    double amount = Double.parseDouble(amountText);
                    int termYears = Integer.parseInt(termText);
                    
                    if (amount <= 0 || termYears <= 0) {
                        throw new NumberFormatException("Positive values required");
                    }
                    
                    // Check if loan amount is already calculated
                    if (monthlyPaymentLabel.getText().isEmpty()) {
                        JOptionPane.showMessageDialog(parentFrame, 
                                "Please calculate the loan details first.", 
                                "Input Error", 
                                JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    
                    // Get member
                    Member member = MemberController.getMemberByNumber(memberNumber);
                    
                    // Submit loan application
                    Loan loan = new Loan();
                    loan.setMemberId(member.getId());
                    loan.setLoanType(loanType.getCode());
                    loan.setPrincipalAmount(amount);
                    loan.setTermYears(termYears);
                    loan.setInterestRate(loanType.getInterestRate());
                    loan.setPurpose(purpose);
                    loan.setApplicationDate(DateUtils.getCurrentSqlDate());
                    loan.setStatus("PENDING");
                    
                    boolean success = LoanController.applyForLoan(loan);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(parentFrame, 
                                "Loan application submitted successfully!", 
                                "Application Complete", 
                                JOptionPane.INFORMATION_MESSAGE);
                        
                        // Clear form
                        clearButton.doClick();
                        
                        // Switch to loan list tab
                        tabbedPane.setSelectedIndex(0);
                        
                        // Refresh loan list
                        loadLoans();
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, 
                                "Failed to submit loan application.", 
                                "Application Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Please enter valid numeric values for amount and term.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        buttonsPanel.add(clearButton);
        buttonsPanel.add(applyButton);
        
        // Add components to panel
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(formPanel), BorderLayout.CENTER);
        panel.add(buttonsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Loads all loans into the table
     */
    private void loadLoans() {
        // Clear the table
        tableModel.setRowCount(0);
        
        SwingWorker<List<Loan>, Void> worker = new SwingWorker<List<Loan>, Void>() {
            @Override
            protected List<Loan> doInBackground() throws Exception {
                return LoanController.getAllLoans();
            }
            
            @Override
            protected void done() {
                try {
                    List<Loan> loans = get();
                    
                    for (Loan loan : loans) {
                        Member member = MemberController.getMemberById(loan.getMemberId());
                        String memberName = (member != null) ? member.getFullName() : "Unknown";
                        
                        Object[] row = {
                            loan.getLoanNumber(),
                            memberName,
                            loan.getLoanType(),
                            "₱ " + currencyFormatter.format(loan.getPrincipalAmount()),
                            loan.getTermYears() + " years",
                            "₱ " + currencyFormatter.format(loan.getMonthlyAmortization()),
                            "₱ " + currencyFormatter.format(loan.getRemainingBalance()),
                            loan.getStatus()
                        };
                        
                        tableModel.addRow(row);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error loading loans: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Searches for loans based on search term
     * 
     * @param searchTerm Text to search for
     */
    private void searchLoans(String searchTerm) {
        if (searchTerm.isEmpty()) {
            loadLoans();
            return;
        }
        
        // Clear the table
        tableModel.setRowCount(0);
        
        SwingWorker<List<Loan>, Void> worker = new SwingWorker<List<Loan>, Void>() {
            @Override
            protected List<Loan> doInBackground() throws Exception {
                return LoanController.searchLoans(searchTerm);
            }
            
            @Override
            protected void done() {
                try {
                    List<Loan> loans = get();
                    
                    for (Loan loan : loans) {
                        Member member = MemberController.getMemberById(loan.getMemberId());
                        String memberName = (member != null) ? member.getFullName() : "Unknown";
                        
                        Object[] row = {
                            loan.getLoanNumber(),
                            memberName,
                            loan.getLoanType(),
                            "₱ " + currencyFormatter.format(loan.getPrincipalAmount()),
                            loan.getTermYears() + " years",
                            "₱ " + currencyFormatter.format(loan.getMonthlyAmortization()),
                            "₱ " + currencyFormatter.format(loan.getRemainingBalance()),
                            loan.getStatus()
                        };
                        
                        tableModel.addRow(row);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error searching loans: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Views details of the selected loan
     */
    private void viewLoanDetails() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select a loan to view details.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String loanNumber = (String) tableModel.getValueAt(selectedRow, 0);
        Loan loan = LoanController.getLoanByNumber(loanNumber);
        
        if (loan == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Loan details not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create details dialog
        JDialog dialog = new JDialog(parentFrame, "Loan Details", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel titleLabel = new JLabel("Loan Details - " + loanNumber);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Get member info
        Member member = MemberController.getMemberById(loan.getMemberId());
        String memberInfo = "Unknown Member";
        if (member != null) {
            memberInfo = member.getFullName() + " (" + member.getMemberNumber() + ")";
        }
        
        JLabel memberLabel = new JLabel("Member: " + memberInfo);
        memberLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        memberLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.add(titleLabel);
        headerPanel.add(memberLabel);
        
        // Content panel
        JPanel contentPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        contentPanel.setBorder(new EmptyBorder(20, 0, 20, 0));
        
        // Add loan info
        addDetailRow(contentPanel, "Loan Number:", loan.getLoanNumber());
        addDetailRow(contentPanel, "Loan Type:", loan.getLoanType());
        addDetailRow(contentPanel, "Principal Amount:", "₱ " + currencyFormatter.format(loan.getPrincipalAmount()));
        addDetailRow(contentPanel, "Interest Rate:", loan.getInterestRate() + "%");
        addDetailRow(contentPanel, "Term:", loan.getTermYears() + " years");
        addDetailRow(contentPanel, "Monthly Amortization:", "₱ " + currencyFormatter.format(loan.getMonthlyAmortization()));
        addDetailRow(contentPanel, "Remaining Balance:", "₱ " + currencyFormatter.format(loan.getRemainingBalance()));
        addDetailRow(contentPanel, "Status:", loan.getStatus());
        addDetailRow(contentPanel, "Purpose:", loan.getPurpose());
        addDetailRow(contentPanel, "Application Date:", dateFormatter.format(loan.getApplicationDate()));
        
        if (loan.getApprovalDate() != null) {
            addDetailRow(contentPanel, "Approval Date:", dateFormatter.format(loan.getApprovalDate()));
        }
        
        if (loan.getReleaseDate() != null) {
            addDetailRow(contentPanel, "Release Date:", dateFormatter.format(loan.getReleaseDate()));
        }
        
        if (loan.getLastPaymentDate() != null) {
            addDetailRow(contentPanel, "Last Payment Date:", dateFormatter.format(loan.getLastPaymentDate()));
        }
        
        // Amortization schedule
        JPanel schedulePanel = new JPanel(new BorderLayout());
        schedulePanel.setBorder(new TitledBorder("Amortization Schedule"));
        
        String[] columns = {"Year", "Month", "Principal", "Interest", "Monthly Payment", "Balance"};
        DefaultTableModel scheduleModel = new DefaultTableModel(columns, 0);
        
        List<AmortizationEntry> schedule = LoanController.getAmortizationSchedule(loan);
        if (schedule != null) {
            for (AmortizationEntry entry : schedule) {
                Object[] row = {
                    entry.getYear(),
                    entry.getMonth(),
                    "₱ " + currencyFormatter.format(entry.getPrincipalPayment()),
                    "₱ " + currencyFormatter.format(entry.getInterestPayment()),
                    "₱ " + currencyFormatter.format(entry.getMonthlyPayment()),
                    "₱ " + currencyFormatter.format(entry.getRemainingBalance())
                };
                
                scheduleModel.addRow(row);
            }
        }
        
        JTable scheduleTable = new JTable(scheduleModel);
        scheduleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scheduleScrollPane = new JScrollPane(scheduleTable);
        schedulePanel.add(scheduleScrollPane, BorderLayout.CENTER);
        
        // Create a tabbed pane for details and schedule
        JTabbedPane detailsTabs = new JTabbedPane();
        detailsTabs.addTab("Loan Details", new JScrollPane(contentPanel));
        detailsTabs.addTab("Amortization Schedule", schedulePanel);
        
        // Add buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        buttonsPanel.add(closeButton);
        
        detailsPanel.add(headerPanel, BorderLayout.NORTH);
        detailsPanel.add(detailsTabs, BorderLayout.CENTER);
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
     * Opens dialog to make a loan payment
     */
    private void makeLoanPayment() {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select a loan to make a payment.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String loanNumber = (String) tableModel.getValueAt(selectedRow, 0);
        Loan loan = LoanController.getLoanByNumber(loanNumber);
        
        if (loan == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Loan not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!loan.getStatus().equals("ACTIVE")) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Can only make payments on active loans.", 
                    "Status Error", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create payment dialog
        JDialog dialog = new JDialog(parentFrame, "Make Loan Payment", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Get member info
        Member member = MemberController.getMemberById(loan.getMemberId());
        String memberInfo = "Unknown Member";
        if (member != null) {
            memberInfo = member.getFullName() + " (" + member.getMemberNumber() + ")";
        }
        
        // Form fields
        formPanel.add(new JLabel("Loan Number:"));
        formPanel.add(new JLabel(loan.getLoanNumber()));
        
        formPanel.add(new JLabel("Member:"));
        formPanel.add(new JLabel(memberInfo));
        
        formPanel.add(new JLabel("Monthly Amortization:"));
        formPanel.add(new JLabel("₱ " + currencyFormatter.format(loan.getMonthlyAmortization())));
        
        formPanel.add(new JLabel("Remaining Balance:"));
        formPanel.add(new JLabel("₱ " + currencyFormatter.format(loan.getRemainingBalance())));
        
        formPanel.add(new JLabel("Payment Amount:*"));
        JTextField amountField = new JTextField();
        formPanel.add(amountField);
        
        formPanel.add(new JLabel("Notes:"));
        JTextField notesField = new JTextField();
        formPanel.add(notesField);
        
        // Pre-fill with monthly amortization
        amountField.setText(currencyFormatter.format(loan.getMonthlyAmortization()));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton payButton = new CustomButton("Make Payment");
        payButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String amountText = amountField.getText().trim().replace(",", "");
                    double amount = Double.parseDouble(amountText);
                    
                    if (amount <= 0) {
                        throw new NumberFormatException("Amount must be positive");
                    }
                    
                    String notes = notesField.getText().trim();
                    
                    boolean success = LoanController.processLoanPayment(
                            loan.getLoanNumber(), 
                            amount, 
                            notes);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, 
                                "Loan payment processed successfully!", 
                                "Payment Complete", 
                                JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        
                        // Refresh loan list
                        loadLoans();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "Failed to process loan payment.", 
                                "Payment Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please enter a valid payment amount.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(payButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Changes the status of the selected loan
     * 
     * @param approve true to approve, false to reject
     */
    private void changeLoanStatus(boolean approve) {
        int selectedRow = loanTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select a loan to " + (approve ? "approve" : "reject") + ".", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String loanNumber = (String) tableModel.getValueAt(selectedRow, 0);
        Loan loan = LoanController.getLoanByNumber(loanNumber);
        
        if (loan == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Loan not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!loan.getStatus().equals("PENDING")) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Can only " + (approve ? "approve" : "reject") + " pending loans.", 
                    "Status Error", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int confirmResult = JOptionPane.showConfirmDialog(parentFrame, 
                "Are you sure you want to " + (approve ? "approve" : "reject") + " this loan?", 
                "Confirm Status Change", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirmResult == JOptionPane.YES_OPTION) {
            boolean success = false;
            
            try {
                if (approve) {
                    success = LoanController.approveLoan(loanNumber);
                } else {
                    success = LoanController.rejectLoan(loanNumber);
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Loan " + (approve ? "approved" : "rejected") + " successfully.", 
                            "Status Updated", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    loadLoans();
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Failed to " + (approve ? "approve" : "reject") + " loan.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(parentFrame, 
                        "Error: " + e.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}