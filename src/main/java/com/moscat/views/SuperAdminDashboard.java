package com.moscat.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import com.moscat.controllers.AuthController;
import com.moscat.controllers.DormantAccountController;
import com.moscat.controllers.MemberController;
import com.moscat.controllers.TransactionController;
import com.moscat.models.User;
import com.moscat.utils.Constants;

/**
 * Main dashboard for the Super Administrator
 */
public class SuperAdminDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    
    private JLabel dateTimeLabel;
    private JLabel userLabel;
    private JPanel mainContentPanel;
    
    /**
     * Constructor
     */
    public SuperAdminDashboard() {
        super(Constants.APP_NAME + " - Super Administrator Dashboard");
        
        initComponents();
        setupLayout();
        setupMenuBar();
        setupListeners();
        
        // Set size and position
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
    }
    
    /**
     * Initializes the components
     */
    private void initComponents() {
        // Date and time label, updated by a timer
        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        updateDateTime();
        
        // Timer to update date and time every second
        Timer timer = new Timer(1000, e -> updateDateTime());
        timer.start();
        
        // User label
        User currentUser = AuthController.getCurrentUser();
        String userName = (currentUser != null) ? currentUser.getFullName() : "Unknown User";
        userLabel = new JLabel("Welcome, " + userName);
        userLabel.setFont(new Font("Arial", Font.BOLD, 12));
        
        // Main content panel
        mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    }
    
    /**
     * Sets up the layout
     */
    private void setupLayout() {
        setLayout(new BorderLayout());
        
        // Header panel with logo and user info
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Logo and system title
        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel(Constants.APP_NAME, SwingConstants.LEFT);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoPanel.add(titleLabel);
        
        headerPanel.add(logoPanel, BorderLayout.WEST);
        
        // User info and date/time
        JPanel userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.add(userLabel);
        userPanel.add(dateTimeLabel);
        
        headerPanel.add(userPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Create the dashboard summary panel
        JPanel summaryPanel = createSummaryPanel();
        
        // Create the quick action buttons panel
        JPanel actionPanel = createQuickActionPanel();
        
        // Add panels to the main content
        mainContentPanel.add(summaryPanel, BorderLayout.CENTER);
        mainContentPanel.add(actionPanel, BorderLayout.SOUTH);
        
        add(mainContentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates the summary panel showing system statistics
     * 
     * @return The summary panel
     */
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "System Summary", 
            TitledBorder.LEFT, 
            TitledBorder.TOP
        ));
        
        // Get statistics from controllers
        int memberCount = MemberController.getMemberCount();
        double totalSavingsBalance = MemberController.getTotalSavingsBalance();
        int activeLoanCount = TransactionController.getActiveLoanCount();
        int dormantAccountsCount = DormantAccountController.getDormantAccountCount();
        
        // Members panel
        JPanel membersPanel = createSummaryCard("Total Members", String.valueOf(memberCount), new Color(41, 128, 185));
        panel.add(membersPanel);
        
        // Total Savings panel
        JPanel savingsPanel = createSummaryCard("Total Savings", String.format("₱%.2f", totalSavingsBalance), new Color(39, 174, 96));
        panel.add(savingsPanel);
        
        // Active Loans panel
        JPanel loansPanel = createSummaryCard("Active Loans", String.valueOf(activeLoanCount), new Color(211, 84, 0));
        panel.add(loansPanel);
        
        // Dormant Accounts panel
        JPanel dormantPanel = createSummaryCard("Dormant Accounts", String.valueOf(dormantAccountsCount), new Color(192, 57, 43));
        panel.add(dormantPanel);
        
        return panel;
    }
    
    /**
     * Creates a summary card with title, value, and color
     * 
     * @param title The card title
     * @param value The value to display
     * @param color The card color
     * @return The summary card panel
     */
    private JPanel createSummaryCard(String title, String value, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        valueLabel.setForeground(color);
        
        panel.add(titleLabel);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(valueLabel);
        
        return panel;
    }
    
    /**
     * Creates the quick action panel with buttons
     * 
     * @return The quick action panel
     */
    private JPanel createQuickActionPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createEtchedBorder(), 
            "Quick Actions", 
            TitledBorder.LEFT, 
            TitledBorder.TOP
        ));
        
        // Member Registration button
        JButton memberButton = createActionButton("Register Member", null);
        memberButton.addActionListener(e -> openMemberRegistration());
        panel.add(memberButton);
        
        // Transaction button
        JButton transactionButton = createActionButton("New Transaction", null);
        transactionButton.addActionListener(e -> openTransactions());
        panel.add(transactionButton);
        
        // Loan Application button
        JButton loanButton = createActionButton("New Loan", null);
        loanButton.addActionListener(e -> openLoans());
        panel.add(loanButton);
        
        // Reports button
        JButton reportsButton = createActionButton("Generate Reports", null);
        reportsButton.addActionListener(e -> openReports());
        panel.add(reportsButton);
        
        return panel;
    }
    
    /**
     * Creates an action button with icon and text
     * 
     * @param text The button text
     * @param icon The button icon
     * @return The configured button
     */
    private JButton createActionButton(String text, ImageIcon icon) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        if (icon != null) {
            button.setIcon(icon);
        }
        button.setFocusable(false);
        button.setVerticalTextPosition(SwingConstants.BOTTOM);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        return button;
    }
    
    /**
     * Sets up the menu bar
     */
    private void setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(e -> confirmExit());
        fileMenu.add(exitItem);
        
        // Members menu
        JMenu membersMenu = new JMenu("Members");
        JMenuItem registerMemberItem = new JMenuItem("Register New Member");
        registerMemberItem.addActionListener(e -> openMemberRegistration());
        JMenuItem viewMembersItem = new JMenuItem("View/Edit Members");
        viewMembersItem.addActionListener(e -> openMembers());
        membersMenu.add(registerMemberItem);
        membersMenu.add(viewMembersItem);
        
        // Transactions menu
        JMenu transactionsMenu = new JMenu("Transactions");
        JMenuItem newTransactionItem = new JMenuItem("New Transaction");
        newTransactionItem.addActionListener(e -> openTransactions());
        JMenuItem viewTransactionsItem = new JMenuItem("View Transactions");
        viewTransactionsItem.addActionListener(e -> openTransactions());
        transactionsMenu.add(newTransactionItem);
        transactionsMenu.add(viewTransactionsItem);
        
        // Loans menu
        JMenu loansMenu = new JMenu("Loans");
        JMenuItem newLoanItem = new JMenuItem("New Loan Application");
        newLoanItem.addActionListener(e -> openLoans());
        JMenuItem viewLoansItem = new JMenuItem("View/Manage Loans");
        viewLoansItem.addActionListener(e -> openLoans());
        loansMenu.add(newLoanItem);
        loansMenu.add(viewLoansItem);
        
        // Reports menu
        JMenu reportsMenu = new JMenu("Reports");
        JMenuItem memberReportItem = new JMenuItem("Member Reports");
        memberReportItem.addActionListener(e -> openReports());
        JMenuItem transactionReportItem = new JMenuItem("Transaction Reports");
        transactionReportItem.addActionListener(e -> openReports());
        JMenuItem loanReportItem = new JMenuItem("Loan Reports");
        loanReportItem.addActionListener(e -> openReports());
        JMenuItem dormantAccountsItem = new JMenuItem("Dormant Accounts");
        dormantAccountsItem.addActionListener(e -> openDormantAccounts());
        reportsMenu.add(memberReportItem);
        reportsMenu.add(transactionReportItem);
        reportsMenu.add(loanReportItem);
        reportsMenu.addSeparator();
        reportsMenu.add(dormantAccountsItem);
        
        // Settings menu
        JMenu settingsMenu = new JMenu("Settings");
        JMenuItem interestRatesItem = new JMenuItem("Interest Rates");
        interestRatesItem.addActionListener(e -> openInterestRates());
        JMenuItem systemSettingsItem = new JMenuItem("System Settings");
        systemSettingsItem.addActionListener(e -> openSystemSettings());
        settingsMenu.add(interestRatesItem);
        settingsMenu.add(systemSettingsItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(e -> showAboutDialog());
        helpMenu.add(aboutItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(membersMenu);
        menuBar.add(transactionsMenu);
        menuBar.add(loansMenu);
        menuBar.add(reportsMenu);
        menuBar.add(settingsMenu);
        menuBar.add(helpMenu);
        
        setJMenuBar(menuBar);
    }
    
    /**
     * Sets up the window listeners
     */
    private void setupListeners() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmExit();
            }
        });
    }
    
    /**
     * Updates the date and time label
     */
    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE, MMMM d, yyyy h:mm:ss a");
        dateTimeLabel.setText(sdf.format(new Date()));
    }
    
    /**
     * Confirms exit and closes the application
     */
    private void confirmExit() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to exit the application?",
            "Confirm Exit",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (result == JOptionPane.YES_OPTION) {
            dispose();
            System.exit(0);
        }
    }
    
    /**
     * Shows the about dialog
     */
    private void showAboutDialog() {
        JOptionPane.showMessageDialog(
            this,
            "MOSCAT Cooperative Savings and Loan System\n" +
            "Version " + Constants.APP_VERSION + "\n\n" +
            "© 2025 MOSCAT Cooperative",
            "About",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Opens the member registration screen
     */
    private void openMemberRegistration() {
        // TODO: Implement Member Registration view
        JOptionPane.showMessageDialog(
            this,
            "Member Registration view not implemented yet.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Opens the members view
     */
    private void openMembers() {
        // TODO: Implement Members view
        JOptionPane.showMessageDialog(
            this,
            "Members view not implemented yet.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Opens the transactions view
     */
    private void openTransactions() {
        // TODO: Implement Transactions view
        JOptionPane.showMessageDialog(
            this,
            "Transactions view not implemented yet.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Opens the loans view
     */
    private void openLoans() {
        // TODO: Implement Loans view
        JOptionPane.showMessageDialog(
            this,
            "Loans view not implemented yet.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Opens the reports view
     */
    private void openReports() {
        // TODO: Implement Reports view
        JOptionPane.showMessageDialog(
            this,
            "Reports view not implemented yet.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Opens the dormant accounts view
     */
    private void openDormantAccounts() {
        // TODO: Implement Dormant Accounts view
        JOptionPane.showMessageDialog(
            this,
            "Dormant Accounts view not implemented yet.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Opens the interest rates view
     */
    private void openInterestRates() {
        // TODO: Implement Interest Rates view
        JOptionPane.showMessageDialog(
            this,
            "Interest Rates view not implemented yet.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
    
    /**
     * Opens the system settings view
     */
    private void openSystemSettings() {
        // TODO: Implement System Settings view
        JOptionPane.showMessageDialog(
            this,
            "System Settings view not implemented yet.",
            "Information",
            JOptionPane.INFORMATION_MESSAGE
        );
    }
}