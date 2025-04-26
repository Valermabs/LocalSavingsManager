package com.moscat.views;

import com.moscat.controllers.AuthController;
import com.moscat.controllers.PermissionController;
import com.moscat.models.Permission;
import com.moscat.models.User;
import com.moscat.models.UserPermission;
import com.moscat.views.components.CustomButton;

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
 * View for managing user permissions
 */
public class UserPermissionsView extends JDialog {
    
    private JFrame parentFrame;
    private User user;
    private JTable permissionsTable;
    private DefaultTableModel permissionsTableModel;
    private JTable grantedPermissionsTable;
    private DefaultTableModel grantedPermissionsTableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Constructor for UserPermissionsView
     * 
     * @param parentFrame Parent JFrame
     * @param user User whose permissions are being managed
     */
    public UserPermissionsView(JFrame parentFrame, User user) {
        super(parentFrame, "User Permissions - " + user.getUsername(), true);
        this.parentFrame = parentFrame;
        this.user = user;
        
        // Basic dialog setup
        setSize(800, 600);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(parentFrame);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        
        initializeUI();
        loadData();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // User info panel
        JPanel userInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        userInfoPanel.setBorder(new TitledBorder("User Information"));
        
        JLabel usernameLabel = new JLabel("Username: " + user.getUsername());
        JLabel nameLabel = new JLabel("Name: " + user.getFullName());
        JLabel roleLabel = new JLabel("Role: " + user.getRole());
        
        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(Box.createHorizontalStrut(20));
        userInfoPanel.add(nameLabel);
        userInfoPanel.add(Box.createHorizontalStrut(20));
        userInfoPanel.add(roleLabel);
        
        // Create permissions table
        String[] permissionColumns = {"ID", "Name", "Module", "Description"};
        permissionsTableModel = new DefaultTableModel(permissionColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        permissionsTable = new JTable(permissionsTableModel);
        permissionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        permissionsTable.getColumnModel().getColumn(0).setMaxWidth(40);
        
        JScrollPane permissionsScrollPane = new JScrollPane(permissionsTable);
        permissionsScrollPane.setBorder(new TitledBorder("Available Permissions"));
        
        // Create granted permissions table
        String[] grantedColumns = {"ID", "Name", "Module", "Granted Date", "Expiry Date", "Status"};
        grantedPermissionsTableModel = new DefaultTableModel(grantedColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        grantedPermissionsTable = new JTable(grantedPermissionsTableModel);
        grantedPermissionsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        grantedPermissionsTable.getColumnModel().getColumn(0).setMaxWidth(40);
        
        JScrollPane grantedPermissionsScrollPane = new JScrollPane(grantedPermissionsTable);
        grantedPermissionsScrollPane.setBorder(new TitledBorder("Granted Permissions"));
        
        // Create buttons panels
        JPanel permissionsButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton grantButton = new CustomButton("Grant Permission ▼");
        permissionsButtonsPanel.add(grantButton);
        
        JPanel grantedButtonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton revokeButton = new CustomButton("Revoke Permission ▲");
        grantedButtonsPanel.add(revokeButton);
        
        // Create tables panel
        JPanel tablesPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        
        JPanel permissionsPanel = new JPanel(new BorderLayout());
        permissionsPanel.add(permissionsScrollPane, BorderLayout.CENTER);
        permissionsPanel.add(permissionsButtonsPanel, BorderLayout.SOUTH);
        
        JPanel grantedPanel = new JPanel(new BorderLayout());
        grantedPanel.add(grantedPermissionsScrollPane, BorderLayout.CENTER);
        grantedPanel.add(grantedButtonsPanel, BorderLayout.SOUTH);
        
        tablesPanel.add(permissionsPanel);
        tablesPanel.add(grantedPanel);
        
        // Create bottom panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });
        bottomPanel.add(closeButton);
        
        // Add action listeners
        grantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                grantPermission();
            }
        });
        
        revokeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                revokePermission();
            }
        });
        
        // Add components to main panel
        mainPanel.add(userInfoPanel, BorderLayout.NORTH);
        mainPanel.add(tablesPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add main panel to dialog
        setContentPane(mainPanel);
    }
    
    /**
     * Loads permission data
     */
    private void loadData() {
        loadAvailablePermissions();
        loadGrantedPermissions();
    }
    
    /**
     * Loads available permissions
     */
    private void loadAvailablePermissions() {
        // Clear the table
        permissionsTableModel.setRowCount(0);
        
        SwingWorker<List<Permission>, Void> worker = new SwingWorker<List<Permission>, Void>() {
            @Override
            protected List<Permission> doInBackground() throws Exception {
                return PermissionController.getAllPermissions();
            }
            
            @Override
            protected void done() {
                try {
                    List<Permission> permissions = get();
                    
                    for (Permission permission : permissions) {
                        Object[] row = {
                            permission.getId(),
                            permission.getName(),
                            permission.getModule(),
                            permission.getDescription()
                        };
                        
                        permissionsTableModel.addRow(row);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error loading permissions: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Loads granted permissions
     */
    private void loadGrantedPermissions() {
        // Clear the table
        grantedPermissionsTableModel.setRowCount(0);
        
        SwingWorker<List<UserPermission>, Void> worker = new SwingWorker<List<UserPermission>, Void>() {
            @Override
            protected List<UserPermission> doInBackground() throws Exception {
                return PermissionController.getUserPermissionDetails(user.getId());
            }
            
            @Override
            protected void done() {
                try {
                    List<UserPermission> userPermissions = get();
                    
                    for (UserPermission userPermission : userPermissions) {
                        Permission permission = userPermission.getPermission();
                        
                        String status = userPermission.isActive() ? "Active" : "Inactive";
                        if (userPermission.isExpired()) {
                            status = "Expired";
                        }
                        
                        Object[] row = {
                            permission.getId(),
                            permission.getName(),
                            permission.getModule(),
                            userPermission.getGrantedDate() != null ? 
                                dateFormat.format(userPermission.getGrantedDate()) : "",
                            userPermission.getExpiryDate() != null ? 
                                dateFormat.format(userPermission.getExpiryDate()) : "",
                            status
                        };
                        
                        grantedPermissionsTableModel.addRow(row);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error loading user permissions: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Grants a permission to the user
     */
    private void grantPermission() {
        int selectedRow = permissionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a permission to grant.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int permissionId = (int) permissionsTableModel.getValueAt(selectedRow, 0);
        String permissionName = (String) permissionsTableModel.getValueAt(selectedRow, 1);
        
        // Show grant dialog
        JDialog dialog = new JDialog(this, "Grant Permission", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form fields
        JLabel permissionLabel = new JLabel("Permission:");
        JLabel permissionValueLabel = new JLabel(permissionName);
        
        JLabel expiryLabel = new JLabel("Expiry Date (Optional):");
        JTextField expiryField = new JTextField();
        expiryField.setToolTipText("Format: YYYY-MM-DD");
        
        JLabel notesLabel = new JLabel("Notes (Optional):");
        JTextField notesField = new JTextField();
        
        // Add components to form panel
        formPanel.add(permissionLabel);
        formPanel.add(permissionValueLabel);
        formPanel.add(expiryLabel);
        formPanel.add(expiryField);
        formPanel.add(notesLabel);
        formPanel.add(notesField);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JButton grantButton = new CustomButton("Grant Permission");
        grantButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Parse expiry date if provided
                Date expiryDate = null;
                String expiryStr = expiryField.getText().trim();
                
                if (!expiryStr.isEmpty()) {
                    try {
                        expiryDate = dateFormat.parse(expiryStr);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(dialog, 
                                "Invalid expiry date format. Please use YYYY-MM-DD.", 
                                "Input Error", 
                                JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                String notes = notesField.getText().trim();
                User currentUser = AuthController.getCurrentUser();
                
                boolean success = PermissionController.grantPermission(
                        user.getId(), permissionId, currentUser.getId(), expiryDate, notes);
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Permission granted successfully!", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    
                    // Refresh permissions
                    loadGrantedPermissions();
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                            "Failed to grant permission.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(grantButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Revokes a permission from the user
     */
    private void revokePermission() {
        int selectedRow = grantedPermissionsTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                    "Please select a permission to revoke.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int permissionId = (int) grantedPermissionsTableModel.getValueAt(selectedRow, 0);
        String permissionName = (String) grantedPermissionsTableModel.getValueAt(selectedRow, 1);
        
        // Confirm revocation
        int choice = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to revoke the permission '" + permissionName + "'?", 
                "Confirm Revocation", 
                JOptionPane.YES_NO_OPTION);
        
        if (choice != JOptionPane.YES_OPTION) {
            return;
        }
        
        boolean success = PermissionController.revokePermission(user.getId(), permissionId);
        
        if (success) {
            JOptionPane.showMessageDialog(this, 
                    "Permission revoked successfully!", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh permissions
            loadGrantedPermissions();
        } else {
            JOptionPane.showMessageDialog(this, 
                    "Failed to revoke permission.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}