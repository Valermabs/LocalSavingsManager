package com.moscat.views;

import com.moscat.controllers.SavingsController;
import com.moscat.models.SavingsAccount;
import com.moscat.controllers.MemberController;
import com.moscat.models.Member;
import com.moscat.utils.Constants;
import com.moscat.utils.DateUtils;
import com.moscat.views.components.CustomButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * View for managing dormant accounts
 * Displays accounts that have been inactive for at least 12 months
 */
public class DormantAccountsView extends JPanel {
    
    private JFrame parentFrame;
    private JTable dormantAccountsTable;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Constructor for DormantAccountsView
     * 
     * @param parentFrame Parent JFrame
     */
    public DormantAccountsView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
        loadDormantAccounts();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Dormant Accounts Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Create warning label
        JLabel warningLabel = new JLabel("Accounts with no activity for 12+ months are automatically marked as dormant");
        warningLabel.setForeground(Color.RED);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(warningLabel, BorderLayout.SOUTH);
        
        // Create accounts table
        String[] columns = {"Account Number", "Member Name", "Balance", "Interest Accrued", "Last Active", "Dormant Since", "Days Inactive"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        dormantAccountsTable = new JTable(tableModel);
        dormantAccountsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(dormantAccountsTable);
        scrollPane.setBorder(new TitledBorder("Dormant Savings Accounts"));
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton checkDormantButton = new CustomButton("Check for Dormant Accounts");
        JButton reactivateButton = new CustomButton("Reactivate Account");
        JButton viewMemberButton = new CustomButton("View Member Details");
        JButton refreshButton = new JButton("Refresh");
        
        // Check dormant button action
        checkDormantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkForDormantAccounts();
            }
        });
        
        // Reactivate button action
        reactivateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                reactivateAccount();
            }
        });
        
        // View member button action
        viewMemberButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewMemberDetails();
            }
        });
        
        // Refresh button action
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadDormantAccounts();
            }
        });
        
        buttonsPanel.add(checkDormantButton);
        buttonsPanel.add(reactivateButton);
        buttonsPanel.add(viewMemberButton);
        buttonsPanel.add(refreshButton);
        
        // Create notification panel
        JPanel notificationPanel = new JPanel();
        notificationPanel.setLayout(new BoxLayout(notificationPanel, BoxLayout.Y_AXIS));
        notificationPanel.setBackground(new Color(255, 250, 240)); // Light yellow background
        notificationPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(218, 165, 32)), // Gold border
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        
        JLabel notificationTitle = new JLabel("Dormant Account Notifications");
        notificationTitle.setFont(new Font("Arial", Font.BOLD, 14));
        notificationTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel notificationText = new JLabel("<html>Dormant accounts may be subject to fees or reduced interest rates. " +
                "Members should be notified of dormancy status and encouraged to reactivate their accounts " +
                "with a transaction. Accounts dormant for more than 24 months may be subject to closure.</html>");
        notificationText.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        notificationPanel.add(notificationTitle);
        notificationPanel.add(Box.createVerticalStrut(10));
        notificationPanel.add(notificationText);
        
        // Add components to main panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.NORTH);
        topPanel.add(notificationPanel, BorderLayout.CENTER);
        
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Loads dormant accounts into the table
     */
    private void loadDormantAccounts() {
        // Clear the table
        tableModel.setRowCount(0);
        
        SwingWorker<List<SavingsAccount>, Void> worker = new SwingWorker<List<SavingsAccount>, Void>() {
            @Override
            protected List<SavingsAccount> doInBackground() throws Exception {
                // Temporary implementation until getDormantAccounts is available
                List<SavingsAccount> dormantAccounts = new ArrayList<>();
                List<SavingsAccount> allAccounts = SavingsController.getAllSavingsAccounts();
                
                for (SavingsAccount account : allAccounts) {
                    if (Constants.SAVINGS_STATUS_DORMANT.equals(account.getStatus())) {
                        dormantAccounts.add(account);
                    }
                }
                
                return dormantAccounts;
            }
            
            @Override
            protected void done() {
                try {
                    List<SavingsAccount> accounts = get();
                    
                    if (accounts.isEmpty()) {
                        JOptionPane.showMessageDialog(parentFrame, 
                                "No dormant accounts found.", 
                                "Information", 
                                JOptionPane.INFORMATION_MESSAGE);
                        return;
                    }
                    
                    for (SavingsAccount account : accounts) {
                        // Get member name
                        Member member = MemberController.getMemberById(account.getMemberId());
                        String memberName = member != null ? member.getFullName() : "Unknown";
                        
                        // Calculate days inactive
                        long daysInactive = 0;
                        if (account.getLastTransactionDate() != null) {
                            long diffInMillies = Math.abs(new Date().getTime() - account.getLastTransactionDate().getTime());
                            daysInactive = TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
                        }
                        
                        Object[] row = {
                            account.getAccountNumber(),
                            memberName,
                            String.format("₱ %.2f", account.getBalance()),
                            String.format("₱ %.2f", account.getInterestAccrued()),
                            account.getLastTransactionDate() != null ? dateFormat.format(account.getLastTransactionDate()) : "Never",
                            account.getDormantSince() != null ? dateFormat.format(account.getDormantSince()) : "Unknown",
                            daysInactive
                        };
                        
                        tableModel.addRow(row);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error loading dormant accounts: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Runs a check for dormant accounts
     */
    private void checkForDormantAccounts() {
        SwingWorker<List<SavingsAccount>, Void> worker = new SwingWorker<List<SavingsAccount>, Void>() {
            @Override
            protected List<SavingsAccount> doInBackground() throws Exception {
                return SavingsController.checkForDormantAccounts();
            }
            
            @Override
            protected void done() {
                try {
                    List<SavingsAccount> newDormantAccounts = get();
                    
                    if (newDormantAccounts.isEmpty()) {
                        JOptionPane.showMessageDialog(parentFrame, 
                                "No new dormant accounts found.", 
                                "Information", 
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, 
                                newDormantAccounts.size() + " new dormant accounts detected.", 
                                "Dormant Accounts Found", 
                                JOptionPane.WARNING_MESSAGE);
                        
                        // Refresh the table
                        loadDormantAccounts();
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error checking for dormant accounts: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Reactivates a dormant account
     */
    private void reactivateAccount() {
        int selectedRow = dormantAccountsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select an account to reactivate.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String accountNumber = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirm = JOptionPane.showConfirmDialog(parentFrame, 
                "Are you sure you want to reactivate account " + accountNumber + "?", 
                "Confirm Reactivation", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                // Temporary implementation until reactivateAccount is available
                try {
                    SavingsAccount account = SavingsController.getSavingsAccountByNumber(accountNumber);
                    if (account == null) {
                        return false;
                    }
                    
                    // Use updateAccountStatus method
                    return SavingsController.updateAccountStatus(account.getId(), Constants.SAVINGS_STATUS_ACTIVE);
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    
                    if (success) {
                        JOptionPane.showMessageDialog(parentFrame, 
                                "Account successfully reactivated.", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresh the table
                        loadDormantAccounts();
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, 
                                "Failed to reactivate account.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error reactivating account: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Views member details for the selected account
     */
    private void viewMemberDetails() {
        int selectedRow = dormantAccountsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select an account to view member details.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String accountNumber = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Get account and member
        SavingsAccount account = SavingsController.getSavingsAccountByNumber(accountNumber);
        if (account == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Account not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        Member member = MemberController.getMemberById(account.getMemberId());
        if (member == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Member not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show member details dialog
        JDialog dialog = new JDialog(parentFrame, "Member Details: " + member.getFullName(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Member details
        addDetailRow(detailsPanel, "Member ID:", member.getMemberNumber());
        addDetailRow(detailsPanel, "Name:", member.getFullName());
        addDetailRow(detailsPanel, "Contact:", member.getContactNumber());
        addDetailRow(detailsPanel, "Email:", member.getEmail());
        addDetailRow(detailsPanel, "Address:", member.getPresentAddress());
        addDetailRow(detailsPanel, "Status:", member.getStatus());
        addDetailRow(detailsPanel, "Join Date:", member.getJoinDate() != null ? dateFormat.format(member.getJoinDate()) : "Unknown");
        
        // Account details
        addDetailRow(detailsPanel, "Account Number:", account.getAccountNumber());
        addDetailRow(detailsPanel, "Account Type:", account.getAccountType());
        addDetailRow(detailsPanel, "Balance:", String.format("₱ %.2f", account.getBalance()));
        addDetailRow(detailsPanel, "Interest Accrued:", String.format("₱ %.2f", account.getInterestAccrued()));
        addDetailRow(detailsPanel, "Status:", account.getStatus());
        addDetailRow(detailsPanel, "Last Transaction:", account.getLastTransactionDate() != null ? 
                dateFormat.format(account.getLastTransactionDate()) : "Never");
        addDetailRow(detailsPanel, "Dormant Since:", account.getDormantSince() != null ? 
                dateFormat.format(account.getDormantSince()) : "N/A");
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton contactButton = new CustomButton("Contact Member");
        contactButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contactMember(member);
            }
        });
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        buttonsPanel.add(contactButton);
        buttonsPanel.add(closeButton);
        
        JScrollPane scrollPane = new JScrollPane(detailsPanel);
        scrollPane.setBorder(null);
        
        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Helper method to add a row to details panel
     * 
     * @param panel Panel to add row to
     * @param label Label text
     * @param value Value text
     */
    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));
        
        JLabel valueComponent = new JLabel(value != null ? value : "");
        
        panel.add(labelComponent);
        panel.add(valueComponent);
    }
    
    /**
     * Shows dialog to contact the member
     * 
     * @param member Member to contact
     */
    private void contactMember(Member member) {
        JDialog dialog = new JDialog(parentFrame, "Contact Member: " + member.getFullName(), true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Contact Options");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Email option
        JPanel emailPanel = new JPanel(new BorderLayout());
        emailPanel.setBorder(BorderFactory.createTitledBorder("Email"));
        emailPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        emailPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel emailLabel = new JLabel(member.getEmail() != null ? member.getEmail() : "No email available");
        JButton emailButton = new JButton("Compose Email");
        emailButton.setEnabled(member.getEmail() != null && !member.getEmail().isEmpty());
        
        emailPanel.add(emailLabel, BorderLayout.CENTER);
        emailPanel.add(emailButton, BorderLayout.EAST);
        
        // Phone option
        JPanel phonePanel = new JPanel(new BorderLayout());
        phonePanel.setBorder(BorderFactory.createTitledBorder("Phone"));
        phonePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        phonePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel phoneLabel = new JLabel(member.getContactNumber() != null ? member.getContactNumber() : "No phone available");
        JButton phoneButton = new JButton("Call Member");
        phoneButton.setEnabled(member.getContactNumber() != null && !member.getContactNumber().isEmpty());
        
        phonePanel.add(phoneLabel, BorderLayout.CENTER);
        phonePanel.add(phoneButton, BorderLayout.EAST);
        
        // Note about dormancy
        JTextArea noteArea = new JTextArea(
                "Suggested dormancy notification message:\n\n" +
                "Dear " + member.getFirstName() + ",\n\n" +
                "Your savings account " + "has been marked as dormant due to inactivity. " +
                "To reactivate your account, please make a deposit or withdrawal transaction. " +
                "Dormant accounts may be subject to fees or reduced interest rates.\n\n" +
                "Thank you for your continued membership with MOSCAT Cooperative.");
        noteArea.setLineWrap(true);
        noteArea.setWrapStyleWord(true);
        noteArea.setEditable(false);
        noteArea.setBackground(new Color(245, 245, 245));
        noteArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        
        JScrollPane noteScrollPane = new JScrollPane(noteArea);
        noteScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(emailPanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(phonePanel);
        contentPanel.add(Box.createVerticalStrut(10));
        contentPanel.add(noteScrollPane);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        
        dialog.add(contentPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
}