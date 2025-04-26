package com.moscat.views;

import com.moscat.controllers.AuthController;
import com.moscat.utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Base dashboard view with common functionality for all user roles
 */
public class DashboardView extends JPanel {
    
    protected JFrame parentFrame;
    protected String title;
    protected JPanel menuPanel;
    protected JPanel contentPanel;
    protected Map<String, JPanel> menuItems;
    protected JPanel selectedMenuItem;
    
    /**
     * Constructs a new DashboardView
     * 
     * @param parentFrame Parent JFrame
     */
    public DashboardView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        this.title = "Dashboard";
        this.menuItems = new HashMap<>();
        initializeUI();
    }
    
    /**
     * Sets the dashboard title
     * 
     * @param title Dashboard title
     */
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        
        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        
        // Create main content area with menu and content
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create menu panel
        menuPanel = createMenuPanel();
        
        // Create content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        // Add panels to main panel
        mainPanel.add(menuPanel, BorderLayout.WEST);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add components to main view
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
        
        // Show dashboard by default
        showDashboard();
    }
    
    /**
     * Creates the header panel
     * 
     * @return JPanel with header
     */
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(new Color(0, 87, 146)); // Dark blue color
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Create logo/title
        JLabel titleLabel = new JLabel("MOSCAT Cooperative");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        
        // Create user info and logout button
        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setOpaque(false);
        
        JLabel userLabel = new JLabel("Welcome, " + getUserDisplayName());
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        
        JLabel logoutLabel = new JLabel("Logout");
        logoutLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutLabel.setForeground(Color.WHITE);
        logoutLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                logout();
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                logoutLabel.setText("<html><u>Logout</u></html>");
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                logoutLabel.setText("Logout");
            }
        });
        
        userPanel.add(userLabel);
        userPanel.add(new JLabel(" | "));
        userPanel.add(logoutLabel);
        
        // Add components to panel
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(userPanel, BorderLayout.EAST);
        
        return panel;
    }
    
    /**
     * Creates the menu panel
     * 
     * @return JPanel with menu
     */
    private JPanel createMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(new Color(245, 245, 245));
        panel.setPreferredSize(new Dimension(200, 0));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Color.LIGHT_GRAY));
        
        // Add title
        JLabel menuTitleLabel = new JLabel(title);
        menuTitleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        menuTitleLabel.setBorder(new EmptyBorder(20, 15, 20, 15));
        menuTitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(menuTitleLabel);
        
        // Add menu items
        String[] items = getMenuItems();
        for (String item : items) {
            JPanel menuItem = createMenuItem(item);
            menuItems.put(item, menuItem);
            panel.add(menuItem);
        }
        
        // Add filler to push menu items to the top
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    /**
     * Creates a menu item
     * 
     * @param text Menu item text
     * @return JPanel with menu item
     */
    private JPanel createMenuItem(String text) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(new EmptyBorder(10, 15, 10, 15));
        panel.setMaximumSize(new Dimension(200, 40));
        
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 14));
        
        panel.add(label, BorderLayout.CENTER);
        
        // Add hover effect and click handler
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectMenuItem(panel, text);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (panel != selectedMenuItem) {
                    panel.setBackground(new Color(230, 230, 230));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (panel != selectedMenuItem) {
                    panel.setBackground(new Color(245, 245, 245));
                }
            }
        });
        
        return panel;
    }
    
    /**
     * Selects a menu item
     * 
     * @param panel Menu item panel
     * @param text Menu item text
     */
    private void selectMenuItem(JPanel panel, String text) {
        // Deselect current selection
        if (selectedMenuItem != null) {
            selectedMenuItem.setBackground(new Color(245, 245, 245));
            selectedMenuItem.setBorder(new EmptyBorder(10, 15, 10, 15));
        }
        
        // Select new item
        panel.setBackground(new Color(220, 220, 220));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 5, 0, 0, new Color(0, 87, 146)),
                new EmptyBorder(10, 10, 10, 15)));
        selectedMenuItem = panel;
        
        // Handle menu item selection
        handleMenuItemSelection(text);
    }
    
    /**
     * Gets available menu items based on role
     * 
     * @return Array of menu items
     */
    protected String[] getMenuItems() {
        return new String[] {
            "Dashboard",
            "Member Management",
            "Savings Management",
            "Loan Management",
            "Transaction History",
            "Reports",
            "Settings",
            "My Account"
        };
    }
    
    /**
     * Handles menu item selection
     * 
     * @param selectedItem Selected menu item
     */
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
            case "Loan Management":
                showLoanManagement();
                break;
            case "Transaction History":
                showTransactionHistory();
                break;
            case "Reports":
                showReports();
                break;
            case "Settings":
                showSettings();
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
    protected void showDashboard() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Dashboard content will be displayed here.");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the member management screen
     */
    protected void showMemberManagement() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Member management content will be displayed here.");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the savings management screen
     */
    protected void showSavingsManagement() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Savings management content will be displayed here.");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the loan management screen
     */
    protected void showLoanManagement() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Loan management content will be displayed here.");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the transaction history screen
     */
    protected void showTransactionHistory() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Transaction history content will be displayed here.");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the reports screen
     */
    protected void showReports() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Reports content will be displayed here.");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the settings screen
     */
    protected void showSettings() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("Settings content will be displayed here.");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Shows the user account screen
     */
    protected void showMyAccount() {
        contentPanel.removeAll();
        
        JLabel placeholderLabel = new JLabel("User account content will be displayed here.");
        placeholderLabel.setHorizontalAlignment(JLabel.CENTER);
        contentPanel.add(placeholderLabel, BorderLayout.CENTER);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Gets the current user's display name
     * 
     * @return User display name
     */
    protected String getUserDisplayName() {
        if (AuthController.getCurrentUser() != null) {
            String fullName = AuthController.getCurrentUser().getFullName();
            if (fullName != null && !fullName.isEmpty()) {
                return fullName;
            }
            return AuthController.getCurrentUser().getUsername();
        }
        return "User";
    }
    
    /**
     * Gets display text for transaction type
     * 
     * @param transactionType Transaction type code
     * @return Human-readable transaction type
     */
    protected String getTransactionTypeDisplay(String transactionType) {
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
    
    /**
     * Logs out the current user
     */
    protected void logout() {
        int option = JOptionPane.showConfirmDialog(
                parentFrame,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            AuthController.logout();
            com.moscat.App.changeView(new LoginView(parentFrame));
        }
    }
}