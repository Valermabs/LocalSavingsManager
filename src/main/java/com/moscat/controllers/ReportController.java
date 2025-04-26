package com.moscat.controllers;

import com.moscat.models.Loan;
import com.moscat.models.Member;
import com.moscat.models.SavingsAccount;
import com.moscat.models.Transaction;
import com.moscat.models.User;
import com.moscat.utils.DateUtils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Controller for generating and exporting reports
 */
public class ReportController {
    
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATETIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Generates a member information report
     * 
     * @param member Member object
     * @return JPanel containing the report
     */
    public static JPanel generateMemberReport(Member member) {
        if (member == null) {
            return new JPanel();
        }
        
        JPanel reportPanel = new JPanel(new BorderLayout(10, 10));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel titleLabel = new JLabel("Member Information Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Date
        JLabel dateLabel = new JLabel("Date: " + DATE_FORMAT.format(new Date()));
        dateLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // Member details
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        detailsPanel.setBorder(BorderFactory.createTitledBorder("Member Details"));
        
        addLabelValuePair(detailsPanel, "Member Number:", member.getMemberNumber());
        addLabelValuePair(detailsPanel, "Name:", member.getFullName());
        addLabelValuePair(detailsPanel, "Age:", String.valueOf(member.getAge()));
        addLabelValuePair(detailsPanel, "Birth Date:", DateUtils.formatDateForDisplay(member.getBirthDate()));
        addLabelValuePair(detailsPanel, "Contact Number:", member.getContactNumber());
        addLabelValuePair(detailsPanel, "Email:", member.getEmail());
        addLabelValuePair(detailsPanel, "Present Address:", member.getPresentAddress());
        addLabelValuePair(detailsPanel, "Permanent Address:", member.getPermanentAddress());
        addLabelValuePair(detailsPanel, "Employer:", member.getEmployer());
        addLabelValuePair(detailsPanel, "Employment Status:", member.getEmploymentStatus());
        addLabelValuePair(detailsPanel, "Gross Monthly Income:", "₱ " + CURRENCY_FORMAT.format(member.getGrossMonthlyIncome()));
        addLabelValuePair(detailsPanel, "Avg. Net Monthly Income:", "₱ " + CURRENCY_FORMAT.format(member.getAvgNetMonthlyIncome()));
        addLabelValuePair(detailsPanel, "Status:", member.getStatus());
        addLabelValuePair(detailsPanel, "Join Date:", DateUtils.formatDateForDisplay(member.getJoinDate()));
        addLabelValuePair(detailsPanel, "Last Activity Date:", DateUtils.formatDateForDisplay(member.getLastActivityDate()));
        addLabelValuePair(detailsPanel, "Loan Eligibility Amount:", "₱ " + CURRENCY_FORMAT.format(member.getLoanEligibilityAmount()));
        
        // Savings account information
        SavingsAccount account = MemberController.getMemberSavingsAccount(member.getId());
        
        JPanel accountPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        accountPanel.setBorder(BorderFactory.createTitledBorder("Savings Account"));
        
        if (account != null) {
            addLabelValuePair(accountPanel, "Account Number:", account.getAccountNumber());
            addLabelValuePair(accountPanel, "Balance:", "₱ " + CURRENCY_FORMAT.format(account.getBalance()));
            addLabelValuePair(accountPanel, "Interest Earned:", "₱ " + CURRENCY_FORMAT.format(account.getInterestEarned()));
            addLabelValuePair(accountPanel, "Total Balance:", "₱ " + CURRENCY_FORMAT.format(account.getTotalBalance()));
            addLabelValuePair(accountPanel, "Status:", account.getStatus());
            addLabelValuePair(accountPanel, "Open Date:", DateUtils.formatDateForDisplay(account.getOpenDate()));
        } else {
            addLabelValuePair(accountPanel, "Account:", "No savings account found");
        }
        
        // Transaction history
        List<Transaction> transactions = null;
        if (account != null) {
            transactions = TransactionController.getAccountTransactions(account.getId(), 10);
        }
        
        JPanel transactionPanel = new JPanel(new BorderLayout());
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Recent Transactions"));
        
        String[] columns = {"Date", "Type", "Reference", "Description", "Amount", "Balance"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        if (transactions != null && !transactions.isEmpty()) {
            for (Transaction transaction : transactions) {
                Object[] row = {
                    DATETIME_FORMAT.format(transaction.getTransactionDate()),
                    getTransactionTypeDisplay(transaction.getTransactionType()),
                    transaction.getReferenceNumber(),
                    transaction.getDescription(),
                    "₱ " + CURRENCY_FORMAT.format(transaction.getAmount()),
                    "₱ " + CURRENCY_FORMAT.format(transaction.getRunningBalance())
                };
                model.addRow(row);
            }
        } else {
            Object[] row = {"No transactions found", "", "", "", "", ""};
            model.addRow(row);
        }
        
        JTable transactionTable = new JTable(model);
        transactionTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        transactionPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Loans
        List<Loan> loans = LoanController.getMemberLoans(member.getId());
        
        JPanel loanPanel = new JPanel(new BorderLayout());
        loanPanel.setBorder(BorderFactory.createTitledBorder("Loans"));
        
        String[] loanColumns = {"Loan Number", "Type", "Principal", "Interest", "Term", "Status", "Balance"};
        DefaultTableModel loanModel = new DefaultTableModel(loanColumns, 0);
        
        if (loans != null && !loans.isEmpty()) {
            for (Loan loan : loans) {
                Object[] row = {
                    loan.getLoanNumber(),
                    loan.getLoanType(),
                    "₱ " + CURRENCY_FORMAT.format(loan.getPrincipalAmount()),
                    loan.getInterestRate() + "%",
                    loan.getTermYears() + " years",
                    loan.getStatus(),
                    "₱ " + CURRENCY_FORMAT.format(loan.getRemainingBalance())
                };
                loanModel.addRow(row);
            }
        } else {
            Object[] row = {"No loans found", "", "", "", "", "", ""};
            loanModel.addRow(row);
        }
        
        JTable loanTable = new JTable(loanModel);
        loanTable.setFillsViewportHeight(true);
        JScrollPane loanScrollPane = new JScrollPane(loanTable);
        loanPanel.add(loanScrollPane, BorderLayout.CENTER);
        
        // Combine all panels
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        
        JPanel topPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        topPanel.add(detailsPanel);
        topPanel.add(accountPanel);
        
        contentPanel.add(topPanel, BorderLayout.NORTH);
        contentPanel.add(transactionPanel, BorderLayout.CENTER);
        contentPanel.add(loanPanel, BorderLayout.SOUTH);
        
        reportPanel.add(headerPanel, BorderLayout.NORTH);
        reportPanel.add(contentPanel, BorderLayout.CENTER);
        
        JLabel footerLabel = new JLabel("Generated by MOSCAT Multipurpose Cooperative System");
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        
        reportPanel.add(footerLabel, BorderLayout.SOUTH);
        
        return reportPanel;
    }
    
    /**
     * Generates a daily transaction report
     * 
     * @param date Date to generate report for
     * @return JPanel containing the report
     */
    public static JPanel generateDailyTransactionReport(String date) {
        JPanel reportPanel = new JPanel(new BorderLayout(10, 10));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel titleLabel = new JLabel("Daily Transaction Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel dateLabel = new JLabel("Report Date: " + date);
        dateLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // Transaction list
        List<Transaction> transactions = TransactionController.getTransactionsByDateRange(date, date);
        
        JPanel transactionPanel = new JPanel(new BorderLayout());
        transactionPanel.setBorder(BorderFactory.createTitledBorder("Transactions"));
        
        String[] columns = {"Time", "Type", "Account", "Member", "Reference", "Description", "Amount", "Balance", "Processed By"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        if (transactions != null && !transactions.isEmpty()) {
            for (Transaction transaction : transactions) {
                // Get additional information
                SavingsAccount account = SavingsController.getAccountById(transaction.getAccountId());
                Member member = null;
                User user = null;
                
                if (account != null) {
                    member = MemberController.getMemberById(account.getMemberId());
                }
                
                if (transaction.getUserId() > 0) {
                    user = AuthController.getUserById(transaction.getUserId());
                }
                
                Object[] row = {
                    new SimpleDateFormat("HH:mm:ss").format(transaction.getTransactionDate()),
                    getTransactionTypeDisplay(transaction.getTransactionType()),
                    account != null ? account.getAccountNumber() : "",
                    member != null ? member.getFullName() : "",
                    transaction.getReferenceNumber(),
                    transaction.getDescription(),
                    "₱ " + CURRENCY_FORMAT.format(transaction.getAmount()),
                    "₱ " + CURRENCY_FORMAT.format(transaction.getRunningBalance()),
                    user != null ? user.getUsername() : ""
                };
                model.addRow(row);
            }
        } else {
            Object[] row = {"No transactions found", "", "", "", "", "", "", "", ""};
            model.addRow(row);
        }
        
        JTable transactionTable = new JTable(model);
        transactionTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        transactionPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Summary
        List<TransactionController.TransactionSummary> summaries = TransactionController.getDailyTransactionSummary(date);
        
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        String[] summaryColumns = {"Transaction Type", "Total Amount"};
        DefaultTableModel summaryModel = new DefaultTableModel(summaryColumns, 0);
        
        if (summaries != null && !summaries.isEmpty()) {
            double totalDeposits = 0;
            double totalWithdrawals = 0;
            
            for (TransactionController.TransactionSummary summary : summaries) {
                Object[] row = {
                    summary.getTransactionTypeDisplay(),
                    "₱ " + CURRENCY_FORMAT.format(summary.getTotalAmount())
                };
                summaryModel.addRow(row);
                
                // Track deposits and withdrawals
                if (summary.getTransactionType().equals("DEPOSIT") || 
                    summary.getTransactionType().equals("LOAN_RELEASE") ||
                    summary.getTransactionType().equals("INTEREST_EARNING")) {
                    totalDeposits += summary.getTotalAmount();
                } else if (summary.getTransactionType().equals("WITHDRAWAL") || 
                           summary.getTransactionType().equals("LOAN_PAYMENT")) {
                    totalWithdrawals += summary.getTotalAmount();
                }
            }
            
            // Add total row
            Object[] totalRow = {"TOTAL", "₱ " + CURRENCY_FORMAT.format(totalDeposits - totalWithdrawals)};
            summaryModel.addRow(totalRow);
            
        } else {
            Object[] row = {"No transactions", "₱ 0.00"};
            summaryModel.addRow(row);
        }
        
        JTable summaryTable = new JTable(summaryModel);
        summaryTable.setFillsViewportHeight(true);
        JScrollPane summarySP = new JScrollPane(summaryTable);
        summaryPanel.add(summarySP, BorderLayout.CENTER);
        
        // Combine all panels
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(transactionPanel, BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        reportPanel.add(headerPanel, BorderLayout.NORTH);
        reportPanel.add(contentPanel, BorderLayout.CENTER);
        
        JLabel footerLabel = new JLabel("Generated by MOSCAT Multipurpose Cooperative System on " + 
                new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        
        reportPanel.add(footerLabel, BorderLayout.SOUTH);
        
        return reportPanel;
    }
    
    /**
     * Generates a monthly transaction report
     * 
     * @param year Year
     * @param month Month (1-12)
     * @return JPanel containing the report
     */
    public static JPanel generateMonthlyTransactionReport(int year, int month) {
        JPanel reportPanel = new JPanel(new BorderLayout(10, 10));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        String monthName = new SimpleDateFormat("MMMM").format(getDateFromYearMonth(year, month));
        JLabel titleLabel = new JLabel("Monthly Transaction Report: " + monthName + " " + year);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel dateLabel = new JLabel("Generated on: " + DATE_FORMAT.format(new Date()));
        dateLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // Get first and last day of month
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1); // Month is 0-based in Calendar
        String firstDay = DATE_FORMAT.format(cal.getTime());
        
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String lastDay = DATE_FORMAT.format(cal.getTime());
        
        // Transaction summary
        List<TransactionController.TransactionSummary> summaries = 
                TransactionController.getMonthlyTransactionSummary(year, month);
        
        JPanel summaryPanel = new JPanel(new BorderLayout());
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Monthly Summary"));
        
        String[] summaryColumns = {"Transaction Type", "Total Amount"};
        DefaultTableModel summaryModel = new DefaultTableModel(summaryColumns, 0);
        
        double totalDeposits = 0;
        double totalWithdrawals = 0;
        
        if (summaries != null && !summaries.isEmpty()) {
            for (TransactionController.TransactionSummary summary : summaries) {
                Object[] row = {
                    summary.getTransactionTypeDisplay(),
                    "₱ " + CURRENCY_FORMAT.format(summary.getTotalAmount())
                };
                summaryModel.addRow(row);
                
                // Track deposits and withdrawals
                if (summary.getTransactionType().equals("DEPOSIT") || 
                    summary.getTransactionType().equals("LOAN_RELEASE") ||
                    summary.getTransactionType().equals("INTEREST_EARNING")) {
                    totalDeposits += summary.getTotalAmount();
                } else if (summary.getTransactionType().equals("WITHDRAWAL") || 
                           summary.getTransactionType().equals("LOAN_PAYMENT")) {
                    totalWithdrawals += summary.getTotalAmount();
                }
            }
        } else {
            Object[] row = {"No transactions", "₱ 0.00"};
            summaryModel.addRow(row);
        }
        
        // Add total row
        Object[] netRow = {"NET MOVEMENT", "₱ " + CURRENCY_FORMAT.format(totalDeposits - totalWithdrawals)};
        summaryModel.addRow(netRow);
        
        JTable summaryTable = new JTable(summaryModel);
        summaryTable.setFillsViewportHeight(true);
        JScrollPane summarySP = new JScrollPane(summaryTable);
        summaryPanel.add(summarySP, BorderLayout.CENTER);
        
        // Daily breakdown
        JPanel dailyPanel = new JPanel(new BorderLayout());
        dailyPanel.setBorder(BorderFactory.createTitledBorder("Daily Breakdown"));
        
        String[] dailyColumns = {"Day", "Deposits", "Withdrawals", "Net"};
        DefaultTableModel dailyModel = new DefaultTableModel(dailyColumns, 0);
        
        // Get days in month
        int daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        for (int day = 1; day <= daysInMonth; day++) {
            cal.set(year, month - 1, day);
            String currentDate = DATE_FORMAT.format(cal.getTime());
            
            List<TransactionController.TransactionSummary> dailySummary = 
                    TransactionController.getDailyTransactionSummary(currentDate);
            
            double dailyDeposits = 0;
            double dailyWithdrawals = 0;
            
            for (TransactionController.TransactionSummary summary : dailySummary) {
                if (summary.getTransactionType().equals("DEPOSIT") || 
                    summary.getTransactionType().equals("LOAN_RELEASE") ||
                    summary.getTransactionType().equals("INTEREST_EARNING")) {
                    dailyDeposits += summary.getTotalAmount();
                } else if (summary.getTransactionType().equals("WITHDRAWAL") || 
                           summary.getTransactionType().equals("LOAN_PAYMENT")) {
                    dailyWithdrawals += summary.getTotalAmount();
                }
            }
            
            // Only add days with transactions
            if (dailyDeposits > 0 || dailyWithdrawals > 0) {
                Object[] row = {
                    day,
                    "₱ " + CURRENCY_FORMAT.format(dailyDeposits),
                    "₱ " + CURRENCY_FORMAT.format(dailyWithdrawals),
                    "₱ " + CURRENCY_FORMAT.format(dailyDeposits - dailyWithdrawals)
                };
                dailyModel.addRow(row);
            }
        }
        
        // If no daily transactions were added
        if (dailyModel.getRowCount() == 0) {
            Object[] row = {"No daily transactions", "", "", ""};
            dailyModel.addRow(row);
        }
        
        JTable dailyTable = new JTable(dailyModel);
        dailyTable.setFillsViewportHeight(true);
        JScrollPane dailySP = new JScrollPane(dailyTable);
        dailyPanel.add(dailySP, BorderLayout.CENTER);
        
        // Combine all panels
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(summaryPanel, BorderLayout.NORTH);
        contentPanel.add(dailyPanel, BorderLayout.CENTER);
        
        reportPanel.add(headerPanel, BorderLayout.NORTH);
        reportPanel.add(contentPanel, BorderLayout.CENTER);
        
        JLabel footerLabel = new JLabel("Generated by MOSCAT Multipurpose Cooperative System. Period: " + 
                firstDay + " to " + lastDay);
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        
        reportPanel.add(footerLabel, BorderLayout.SOUTH);
        
        return reportPanel;
    }
    
    /**
     * Generates a member list report
     * 
     * @param members List of members
     * @param title Report title
     * @return JPanel containing the report
     */
    public static JPanel generateMemberListReport(List<Member> members, String title) {
        JPanel reportPanel = new JPanel(new BorderLayout(10, 10));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel dateLabel = new JLabel("Generated on: " + DATE_FORMAT.format(new Date()));
        dateLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // Member list
        JPanel memberPanel = new JPanel(new BorderLayout());
        memberPanel.setBorder(BorderFactory.createTitledBorder("Members"));
        
        String[] columns = {"Member #", "Name", "Contact", "Email", "Join Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        if (members != null && !members.isEmpty()) {
            for (Member member : members) {
                Object[] row = {
                    member.getMemberNumber(),
                    member.getFullName(),
                    member.getContactNumber(),
                    member.getEmail(),
                    DateUtils.formatDateForDisplay(member.getJoinDate()),
                    member.getStatus()
                };
                model.addRow(row);
            }
        } else {
            Object[] row = {"No members found", "", "", "", "", ""};
            model.addRow(row);
        }
        
        JTable memberTable = new JTable(model);
        memberTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(memberTable);
        memberPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Summary
        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        int activeCount = 0;
        int dormantCount = 0;
        
        if (members != null) {
            for (Member member : members) {
                if (member.isActive()) {
                    activeCount++;
                } else if (member.isDormant()) {
                    dormantCount++;
                }
            }
        }
        
        addLabelValuePair(summaryPanel, "Total Members:", String.valueOf(members != null ? members.size() : 0));
        addLabelValuePair(summaryPanel, "Active Members:", String.valueOf(activeCount));
        addLabelValuePair(summaryPanel, "Dormant Members:", String.valueOf(dormantCount));
        
        // Combine all panels
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(memberPanel, BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        reportPanel.add(headerPanel, BorderLayout.NORTH);
        reportPanel.add(contentPanel, BorderLayout.CENTER);
        
        JLabel footerLabel = new JLabel("Generated by MOSCAT Multipurpose Cooperative System");
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        
        reportPanel.add(footerLabel, BorderLayout.SOUTH);
        
        return reportPanel;
    }
    
    /**
     * Generates a loan report
     * 
     * @param loans List of loans
     * @param title Report title
     * @return JPanel containing the report
     */
    public static JPanel generateLoanReport(List<Loan> loans, String title) {
        JPanel reportPanel = new JPanel(new BorderLayout(10, 10));
        reportPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel dateLabel = new JLabel("Generated on: " + DATE_FORMAT.format(new Date()));
        dateLabel.setHorizontalAlignment(JLabel.RIGHT);
        
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        // Loan list
        JPanel loanPanel = new JPanel(new BorderLayout());
        loanPanel.setBorder(BorderFactory.createTitledBorder("Loans"));
        
        String[] columns = {"Loan #", "Member", "Type", "Principal", "Interest", "Term", "Net Proceeds", "Status", "Balance"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        if (loans != null && !loans.isEmpty()) {
            for (Loan loan : loans) {
                Member member = MemberController.getMemberById(loan.getMemberId());
                
                Object[] row = {
                    loan.getLoanNumber(),
                    member != null ? member.getFullName() : "",
                    loan.getLoanType(),
                    "₱ " + CURRENCY_FORMAT.format(loan.getPrincipalAmount()),
                    loan.getInterestRate() + "%",
                    loan.getTermYears() + " years",
                    "₱ " + CURRENCY_FORMAT.format(loan.getNetLoanProceeds()),
                    loan.getStatus(),
                    "₱ " + CURRENCY_FORMAT.format(loan.getRemainingBalance())
                };
                model.addRow(row);
            }
        } else {
            Object[] row = {"No loans found", "", "", "", "", "", "", "", ""};
            model.addRow(row);
        }
        
        JTable loanTable = new JTable(model);
        loanTable.setFillsViewportHeight(true);
        JScrollPane scrollPane = new JScrollPane(loanTable);
        loanPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Summary
        JPanel summaryPanel = new JPanel(new GridLayout(0, 2, 10, 5));
        summaryPanel.setBorder(BorderFactory.createTitledBorder("Summary"));
        
        int pendingCount = 0;
        int approvedCount = 0;
        int activeCount = 0;
        int paidCount = 0;
        double totalPrincipal = 0;
        double totalRemainingBalance = 0;
        
        if (loans != null) {
            for (Loan loan : loans) {
                if (loan.isPending()) {
                    pendingCount++;
                } else if (loan.isApproved()) {
                    approvedCount++;
                } else if (loan.isActive()) {
                    activeCount++;
                    totalPrincipal += loan.getPrincipalAmount();
                    totalRemainingBalance += loan.getRemainingBalance();
                } else if (loan.isPaid()) {
                    paidCount++;
                }
            }
        }
        
        addLabelValuePair(summaryPanel, "Total Loans:", String.valueOf(loans != null ? loans.size() : 0));
        addLabelValuePair(summaryPanel, "Pending Loans:", String.valueOf(pendingCount));
        addLabelValuePair(summaryPanel, "Approved Loans:", String.valueOf(approvedCount));
        addLabelValuePair(summaryPanel, "Active Loans:", String.valueOf(activeCount));
        addLabelValuePair(summaryPanel, "Paid Loans:", String.valueOf(paidCount));
        addLabelValuePair(summaryPanel, "Total Principal (Active):", "₱ " + CURRENCY_FORMAT.format(totalPrincipal));
        addLabelValuePair(summaryPanel, "Total Remaining Balance:", "₱ " + CURRENCY_FORMAT.format(totalRemainingBalance));
        
        // Combine all panels
        JPanel contentPanel = new JPanel(new BorderLayout(10, 10));
        contentPanel.add(loanPanel, BorderLayout.CENTER);
        contentPanel.add(summaryPanel, BorderLayout.SOUTH);
        
        reportPanel.add(headerPanel, BorderLayout.NORTH);
        reportPanel.add(contentPanel, BorderLayout.CENTER);
        
        JLabel footerLabel = new JLabel("Generated by MOSCAT Multipurpose Cooperative System");
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.ITALIC, 10));
        
        reportPanel.add(footerLabel, BorderLayout.SOUTH);
        
        return reportPanel;
    }
    
    /**
     * Exports a report to a CSV file
     * 
     * @param table JTable containing the report data
     * @param filePath File path to save the CSV
     * @return true if export successful, false otherwise
     */
    public static boolean exportToCSV(JTable table, String filePath) {
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(filePath)))) {
            // Write header
            for (int i = 0; i < table.getColumnCount(); i++) {
                writer.print(table.getColumnName(i));
                if (i < table.getColumnCount() - 1) {
                    writer.print(",");
                }
            }
            writer.println();
            
            // Write data
            for (int i = 0; i < table.getRowCount(); i++) {
                for (int j = 0; j < table.getColumnCount(); j++) {
                    String value = table.getValueAt(i, j) != null ? table.getValueAt(i, j).toString() : "";
                    // Escape commas
                    if (value.contains(",")) {
                        value = "\"" + value + "\"";
                    }
                    writer.print(value);
                    if (j < table.getColumnCount() - 1) {
                        writer.print(",");
                    }
                }
                writer.println();
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Adds a label-value pair to a panel
     * 
     * @param panel Panel to add to
     * @param label Label text
     * @param value Value text
     */
    private static void addLabelValuePair(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel valueComponent = new JLabel(value);
        
        panel.add(labelComponent);
        panel.add(valueComponent);
    }
    
    /**
     * Gets a display name for a transaction type
     * 
     * @param transactionType Transaction type code
     * @return Display name
     */
    private static String getTransactionTypeDisplay(String transactionType) {
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
    
    /**
     * Gets a Date object from year and month
     * 
     * @param year Year
     * @param month Month (1-12)
     * @return Date object
     */
    private static Date getDateFromYearMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(year, month - 1, 1); // Month is 0-based in Calendar
        return cal.getTime();
    }
}
