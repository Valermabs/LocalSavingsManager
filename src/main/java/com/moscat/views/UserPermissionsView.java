package com.moscat.views;

import com.moscat.controllers.AuthController;
import com.moscat.controllers.PermissionController;
import com.moscat.models.User;
import com.moscat.utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * View for managing user permissions
 */
public class UserPermissionsView extends JDialog {
    
    private final User user;
    private final Map<String, JCheckBox> permissionCheckboxes = new HashMap<>();
    
    /**
     * Constructor for the UserPermissionsView
     * 
     * @param parent The parent frame
     * @param user The user whose permissions are being edited
     */
    public UserPermissionsView(Frame parent, User user) {
        super(parent, "User Permissions: " + user.getUsername(), true);
        this.user = user;
        
        setSize(500, 500);
        setLocationRelativeTo(parent);
        
        initComponents();
    }
    
    /**
     * Initializes the UI components
     */
    private void initComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Add title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Editing permissions for " + user.getFullName() + " (" + user.getRole() + ")");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel, BorderLayout.NORTH);
        
        // Add description
        JPanel descriptionPanel = new JPanel();
        descriptionPanel.setLayout(new BoxLayout(descriptionPanel, BoxLayout.Y_AXIS));
        
        String descriptionText;
        boolean hasFullAccess = false;
        
        if (user.isSuperAdmin()) {
            descriptionText = "SuperAdmin users automatically have all permissions and cannot be modified.";
            hasFullAccess = true;
        } else if (user.isTreasurer()) {
            descriptionText = "Treasurers typically need permissions for transactions, loans, and member management.";
        } else if (user.isBookkeeper()) {
            descriptionText = "Bookkeepers typically need read-only access to transaction history and reports.";
        } else {
            descriptionText = "Select the permissions for this user:";
        }
        
        JLabel descLabel = new JLabel("<html>" + descriptionText + "</html>");
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descriptionPanel.add(descLabel);
        descriptionPanel.add(Box.createVerticalStrut(10));
        
        // Add permissions panel (scrollable)
        JPanel permissionsPanel = new JPanel();
        permissionsPanel.setLayout(new BoxLayout(permissionsPanel, BoxLayout.Y_AXIS));
        
        // Get all permissions
        List<Map<String, Object>> permissions = PermissionController.getAllPermissions();
        List<String> userPermissions = PermissionController.getUserPermissions(user.getId(), true);
        
        // Add permission checkboxes, grouped by categories
        addPermissionCheckboxes(permissionsPanel, permissions, userPermissions, hasFullAccess);
        
        JScrollPane scrollPane = new JScrollPane(permissionsPanel);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Permissions"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                savePermissions();
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Disable save button for SuperAdmin users
        if (hasFullAccess) {
            saveButton.setEnabled(false);
        }
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // Set content pane
        setContentPane(mainPanel);
    }
    
    /**
     * Adds permission checkboxes to the panel
     * 
     * @param panel The panel to add checkboxes to
     * @param permissions The list of all permissions
     * @param userPermissions The list of permissions the user has
     * @param hasFullAccess Whether the user has full access (SuperAdmin)
     */
    private void addPermissionCheckboxes(JPanel panel, List<Map<String, Object>> permissions, 
                                        List<String> userPermissions, boolean hasFullAccess) {
        
        // Sort permissions into categories
        Map<String, List<Map<String, Object>>> categorizedPermissions = new HashMap<>();
        
        // Add standard categories
        categorizedPermissions.put("User Management", new ArrayList<>());
        categorizedPermissions.put("Member Management", new ArrayList<>());
        categorizedPermissions.put("Account Management", new ArrayList<>());
        categorizedPermissions.put("Transaction Management", new ArrayList<>());
        categorizedPermissions.put("Loan Management", new ArrayList<>());
        categorizedPermissions.put("Report Generation", new ArrayList<>());
        categorizedPermissions.put("System Settings", new ArrayList<>());
        categorizedPermissions.put("Other", new ArrayList<>());
        
        // Categorize permissions
        for (Map<String, Object> permission : permissions) {
            String code = (String) permission.get("code");
            String category = getPermissionCategory(code);
            
            if (!categorizedPermissions.containsKey(category)) {
                categorizedPermissions.put(category, new ArrayList<>());
            }
            
            categorizedPermissions.get(category).add(permission);
        }
        
        // Add categories and permissions
        for (Map.Entry<String, List<Map<String, Object>>> entry : categorizedPermissions.entrySet()) {
            if (entry.getValue().isEmpty()) {
                continue;
            }
            
            // Add category label
            JLabel categoryLabel = new JLabel(entry.getKey());
            categoryLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
            categoryLabel.setBorder(new EmptyBorder(10, 0, 5, 0));
            categoryLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            panel.add(categoryLabel);
            
            // Add permissions in this category
            for (Map<String, Object> permission : entry.getValue()) {
                String code = (String) permission.get("code");
                String name = (String) permission.get("name");
                String description = (String) permission.get("description");
                
                JCheckBox checkBox = new JCheckBox(name);
                checkBox.setToolTipText(description);
                checkBox.setSelected(hasFullAccess || userPermissions.contains(code));
                checkBox.setEnabled(!hasFullAccess);
                
                // Store checkbox for later retrieval
                permissionCheckboxes.put(code, checkBox);
                
                // Add to panel
                JPanel checkboxPanel = new JPanel(new BorderLayout());
                checkboxPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
                checkboxPanel.add(checkBox, BorderLayout.WEST);
                
                // Add tooltip icon
                if (description != null && !description.isEmpty()) {
                    JLabel infoLabel = new JLabel(" (?)");
                    infoLabel.setToolTipText(description);
                    infoLabel.setForeground(Color.GRAY);
                    checkboxPanel.add(infoLabel, BorderLayout.CENTER);
                }
                
                panel.add(checkboxPanel);
            }
            
            // Add separator
            panel.add(new JSeparator());
        }
    }
    
    /**
     * Gets the permission category based on its code
     * 
     * @param code The permission code
     * @return The category name
     */
    private String getPermissionCategory(String code) {
        if (code.startsWith("USER_")) {
            return "User Management";
        } else if (code.startsWith("MEMBER_")) {
            return "Member Management";
        } else if (code.startsWith("ACCOUNT_")) {
            return "Account Management";
        } else if (code.startsWith("TRANSACTION_")) {
            return "Transaction Management";
        } else if (code.startsWith("LOAN_")) {
            return "Loan Management";
        } else if (code.startsWith("REPORT_")) {
            return "Report Generation";
        } else if (code.startsWith("SYSTEM_")) {
            return "System Settings";
        } else {
            return "Other";
        }
    }
    
    /**
     * Saves the user permissions
     */
    private void savePermissions() {
        // Skip for SuperAdmin
        if (user.isSuperAdmin()) {
            dispose();
            return;
        }
        
        // Get selected permissions
        List<String> selectedPermissions = new ArrayList<>();
        for (Map.Entry<String, JCheckBox> entry : permissionCheckboxes.entrySet()) {
            if (entry.getValue().isSelected()) {
                selectedPermissions.add(entry.getKey());
            }
        }
        
        // Save permissions
        boolean success = PermissionController.setUserPermissions(user.getId(), selectedPermissions);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                "Permissions updated successfully.", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Failed to update permissions.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
}