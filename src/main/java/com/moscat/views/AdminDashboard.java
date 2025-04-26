package com.moscat.views;

import com.moscat.controllers.AuthController;
import com.moscat.models.User;
import com.moscat.utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Dashboard for SuperAdmin users
 */
public class AdminDashboard extends JFrame {
    
    private User currentUser;
    private JLabel welcomeLabel;
    private JLabel lastLoginLabel;
    private JButton userManagementButton;
    private JButton systemSettingsButton;
    private JButton transactionsButton;
    private JButton loansButton;
    private JButton membersButton;
    private JButton reportsButton;
    private JButton interestRatesButton;
    private JButton logoutButton;
    
    /**
     * Constructs a new AdminDashboard
     * 
     * @param user Current user
     */
    public AdminDashboard(User user) {
        this.currentUser = user;
        
        // Set up frame
        setTitle("SuperAdmin Dashboard - MOSCAT Cooperative");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(51, 102, 153));
        headerPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Add welcome labels
        JPanel labelsPanel = new JPanel(new GridLayout(2, 1));
        labelsPanel.setOpaque(false);
        
        welcomeLabel = new JLabel("Welcome, " + user.getFullName() + " (SuperAdmin)");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(Color.WHITE);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM d, yyyy h:mm a");
        String lastLoginStr = user.getLastLogin() != null ?
                dateFormat.format(user.getLastLogin()) : "First login";
        
        lastLoginLabel = new JLabel("Last login: " + lastLoginStr);
        lastLoginLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        lastLoginLabel.setForeground(Color.WHITE);
        
        labelsPanel.add(welcomeLabel);
        labelsPanel.add(lastLoginLabel);
        
        headerPanel.add(labelsPanel, BorderLayout.WEST);
        
        // Create date label
        JLabel dateLabel = new JLabel(dateFormat.format(new Date()));
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(Color.WHITE);
        headerPanel.add(dateLabel, BorderLayout.EAST);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Create content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
        
        // Create menu panel
        JPanel menuPanel = new JPanel(new GridLayout(8, 1, 0, 10));
        menuPanel.setBorder(new EmptyBorder(0, 0, 0, 15));
        
        // User Management
        userManagementButton = createMenuButton("User Management", "Manage system users");
        userManagementButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openUserManagement();
            }
        });
        menuPanel.add(userManagementButton);
        
        // System Settings
        systemSettingsButton = createMenuButton("System Settings", "Configure system settings");
        systemSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSystemSettings();
            }
        });
        menuPanel.add(systemSettingsButton);
        
        // Transactions
        transactionsButton = createMenuButton("Transactions", "Manage deposits and withdrawals");
        transactionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openTransactions();
            }
        });
        menuPanel.add(transactionsButton);
        
        // Loans
        loansButton = createMenuButton("Loans", "Manage loan applications and payments");
        loansButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openLoans();
            }
        });
        menuPanel.add(loansButton);
        
        // Members
        membersButton = createMenuButton("Members", "Manage cooperative members");
        membersButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openMembers();
            }
        });
        menuPanel.add(membersButton);
        
        // Reports
        reportsButton = createMenuButton("Reports", "Generate and view reports");
        reportsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openReports();
            }
        });
        menuPanel.add(reportsButton);
        
        // Interest Rates
        interestRatesButton = createMenuButton("Interest Rates", "Set and manage interest rates");
        interestRatesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openInterestRates();
            }
        });
        menuPanel.add(interestRatesButton);
        
        // Logout
        logoutButton = createMenuButton("Logout", "Exit the system");
        logoutButton.setBackground(new Color(217, 83, 79));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                logout();
            }
        });
        menuPanel.add(logoutButton);
        
        contentPanel.add(menuPanel, BorderLayout.WEST);
        
        // Create dashboard panel (main content)
        JPanel dashboardPanel = new JPanel(new BorderLayout());
        dashboardPanel.setBorder(BorderFactory.createTitledBorder("SuperAdmin Dashboard"));
        
        // Add dashboard content here
        JPanel summaryPanel = new JPanel(new GridLayout(3, 2, 20, 20));
        summaryPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        summaryPanel.add(createSummaryPanel("Total Members", "0", new Color(51, 122, 183)));
        summaryPanel.add(createSummaryPanel("Active Accounts", "0", new Color(92, 184, 92)));
        summaryPanel.add(createSummaryPanel("Pending Loans", "0", new Color(240, 173, 78)));
        summaryPanel.add(createSummaryPanel("Total Savings", "₱0.00", new Color(91, 192, 222)));
        summaryPanel.add(createSummaryPanel("Total Loans", "₱0.00", new Color(217, 83, 79)));
        summaryPanel.add(createSummaryPanel("System Users", "1", new Color(153, 84, 187)));
        
        dashboardPanel.add(summaryPanel, BorderLayout.CENTER);
        
        contentPanel.add(dashboardPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Create footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setBorder(new EmptyBorder(10, 15, 10, 15));
        footerPanel.setBackground(new Color(240, 240, 240));
        
        JLabel versionLabel = new JLabel("MOSCAT Cooperative System v" + Constants.APP_VERSION);
        versionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerPanel.add(versionLabel, BorderLayout.WEST);
        
        JLabel copyrightLabel = new JLabel("© " + java.time.Year.now().getValue() + " MOSCAT Cooperative");
        copyrightLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerPanel.add(copyrightLabel, BorderLayout.EAST);
        
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Set content pane
        setContentPane(mainPanel);
    }
    
    /**
     * Creates a menu button
     * 
     * @param text Button text
     * @param tooltip Button tooltip
     * @return Button
     */
    private JButton createMenuButton(String text, String tooltip) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setToolTipText(tooltip);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }
    
    /**
     * Creates a summary panel
     * 
     * @param title Panel title
     * @param value Panel value
     * @param color Panel color
     * @return Panel
     */
    private JPanel createSummaryPanel(String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createLineBorder(color, 2));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(color);
        titleLabel.setBorder(new EmptyBorder(10, 10, 0, 10));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setBorder(new EmptyBorder(0, 10, 10, 10));
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Opens the user management view
     */
    private void openUserManagement() {
        // We need to use the parent frame as the owner for the dialog
        Window windowAncestor = SwingUtilities.getWindowAncestor(this);
        Frame owner = null;
        
        // Check if window ancestor is a Frame
        if (windowAncestor instanceof Frame) {
            owner = (Frame) windowAncestor;
        } else {
            // Get all frames if no direct ancestor frame
            Frame[] frames = Frame.getFrames();
            if (frames.length > 0) {
                owner = frames[0]; // Use the first frame
            }
        }
        
        UserManagementView userManagementView = new UserManagementView(owner);
        
        // Add to a dialog to display
        JDialog dialog = new JDialog(owner, "User Management", true);
        dialog.setContentPane(userManagementView);
        dialog.pack();
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
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
     * Logs out the current user
     */
    private void logout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            AuthController.logout();
            dispose();
            
            // Show login dialog
            new LoginView(null).setVisible(true);
        }
    }
}