package com.moscat.views;

import com.moscat.controllers.AuthController;
import com.moscat.controllers.PermissionController;
import com.moscat.models.Permission;
import com.moscat.models.User;
import com.moscat.models.UserPermission;
import com.moscat.utils.ColorScheme;
import com.moscat.utils.Constants;
import com.moscat.utils.UIHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * View for managing user permissions
 */
public class UserPermissionsView extends JPanel {
    
    private final JFrame parentFrame;
    private User selectedUser;
    private Map<String, List<Permission>> permissionsByModule;
    private List<UserPermission> userPermissions;
    
    private JPanel contentPanel;
    private JComboBox<User> userSelector;
    private JPanel permissionsPanel;
    private JTable assignedTable;
    private DefaultTableModel assignedTableModel;
    
    /**
     * Constructor
     * 
     * @param parentFrame Parent frame
     */
    public UserPermissionsView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create components
        initComponents();
        
        // Load data
        loadUsers();
        loadPermissions();
    }
    
    /**
     * Initializes UI components
     */
    private void initComponents() {
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("User Permissions Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // User selection panel
        JPanel userSelectionPanel = new JPanel(new BorderLayout());
        userSelectionPanel.setBorder(new TitledBorder("Select User"));
        
        userSelector = new JComboBox<>();
        userSelector.addActionListener(e -> handleUserSelection());
        
        JButton refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(e -> loadUsers());
        
        userSelectionPanel.add(new JLabel("User: "), BorderLayout.WEST);
        userSelectionPanel.add(userSelector, BorderLayout.CENTER);
        userSelectionPanel.add(refreshButton, BorderLayout.EAST);
        
        // Set up the assigned permissions table
        String[] columns = {"Permission", "Module", "Granted Date", "Status", "Actions"};
        assignedTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                return col == 4; // Only the actions column is editable
            }
        };
        
        assignedTable = new JTable(assignedTableModel);
        assignedTable.getColumnModel().getColumn(4).setCellRenderer(new ButtonRenderer());
        assignedTable.getColumnModel().getColumn(4).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        JScrollPane assignedScrollPane = new JScrollPane(assignedTable);
        assignedScrollPane.setPreferredSize(new Dimension(600, 200));
        
        JPanel assignedPanel = new JPanel(new BorderLayout());
        assignedPanel.setBorder(new TitledBorder("Assigned Permissions"));
        assignedPanel.add(assignedScrollPane, BorderLayout.CENTER);
        
        // Create permissions panel (will be populated when a user is selected)
        permissionsPanel = new JPanel();
        permissionsPanel.setLayout(new BoxLayout(permissionsPanel, BoxLayout.Y_AXIS));
        permissionsPanel.setBorder(new TitledBorder("Available Permissions"));
        
        JScrollPane permissionsScrollPane = new JScrollPane(permissionsPanel);
        permissionsScrollPane.setPreferredSize(new Dimension(600, 300));
        
        // Content panel
        contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(userSelectionPanel, BorderLayout.NORTH);
        contentPanel.add(assignedPanel, BorderLayout.CENTER);
        contentPanel.add(permissionsScrollPane, BorderLayout.SOUTH);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Loads user list to the selector
     */
    private void loadUsers() {
        SwingWorker<List<User>, Void> worker = new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return AuthController.getAllUsers();
            }
            
            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    userSelector.removeAllItems();
                    
                    for (User user : users) {
                        userSelector.addItem(user);
                    }
                    
                    if (selectedUser != null) {
                        // Try to reselect the previously selected user
                        for (int i = 0; i < userSelector.getItemCount(); i++) {
                            User user = userSelector.getItemAt(i);
                            if (user.getId() == selectedUser.getId()) {
                                userSelector.setSelectedIndex(i);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error loading users: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Loads permissions grouped by module
     */
    private void loadPermissions() {
        SwingWorker<Map<String, List<Permission>>, Void> worker = new SwingWorker<Map<String, List<Permission>>, Void>() {
            @Override
            protected Map<String, List<Permission>> doInBackground() throws Exception {
                return PermissionController.getPermissionsByModule();
            }
            
            @Override
            protected void done() {
                try {
                    permissionsByModule = get();
                    if (selectedUser != null) {
                        updatePermissionsPanel();
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
     * Handles user selection from the dropdown
     */
    private void handleUserSelection() {
        User user = (User) userSelector.getSelectedItem();
        if (user == null) {
            return;
        }
        
        selectedUser = user;
        loadUserPermissions();
    }
    
    /**
     * Loads the selected user's permissions
     */
    private void loadUserPermissions() {
        if (selectedUser == null) {
            return;
        }
        
        SwingWorker<List<UserPermission>, Void> worker = new SwingWorker<List<UserPermission>, Void>() {
            @Override
            protected List<UserPermission> doInBackground() throws Exception {
                return PermissionController.getUserPermissions(selectedUser.getId(), false);
            }
            
            @Override
            protected void done() {
                try {
                    userPermissions = get();
                    updateAssignedPermissionsTable();
                    updatePermissionsPanel();
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
     * Updates the assigned permissions table
     */
    private void updateAssignedPermissionsTable() {
        // Clear the table
        assignedTableModel.setRowCount(0);
        
        if (userPermissions == null) {
            return;
        }
        
        for (UserPermission userPermission : userPermissions) {
            Permission permission = userPermission.getPermission();
            
            if (permission != null) {
                String formattedDate = userPermission.getGrantedDate() != null 
                        ? userPermission.getGrantedDate().toString() 
                        : "N/A";
                
                String status = userPermission.isActive() ? "Active" : "Inactive";
                
                Object[] row = {
                    permission.getName(),
                    permission.getModule(),
                    formattedDate,
                    status,
                    userPermission.isActive() ? "Revoke" : "Activate"
                };
                
                assignedTableModel.addRow(row);
            }
        }
    }
    
    /**
     * Updates the available permissions panel
     */
    private void updatePermissionsPanel() {
        permissionsPanel.removeAll();
        
        if (permissionsByModule == null || selectedUser == null) {
            permissionsPanel.revalidate();
            permissionsPanel.repaint();
            return;
        }
        
        // Create a set of permission IDs that the user already has
        java.util.Set<Integer> assignedPermissionIds = new java.util.HashSet<>();
        if (userPermissions != null) {
            for (UserPermission userPermission : userPermissions) {
                assignedPermissionIds.add(userPermission.getPermissionId());
            }
        }
        
        for (Map.Entry<String, List<Permission>> entry : permissionsByModule.entrySet()) {
            String module = entry.getKey();
            List<Permission> permissions = entry.getValue();
            
            JPanel modulePanel = new JPanel(new BorderLayout());
            modulePanel.setBorder(BorderFactory.createTitledBorder(module));
            
            JPanel permissionsGrid = new JPanel(new GridLayout(0, 3, 10, 5));
            
            for (Permission permission : permissions) {
                JPanel permItem = new JPanel(new BorderLayout());
                permItem.setBorder(BorderFactory.createEtchedBorder());
                permItem.setBackground(ColorScheme.BACKGROUND_LIGHT);
                
                JCheckBox checkBox = new JCheckBox(permission.getName());
                checkBox.setToolTipText(permission.getDescription());
                
                // Check if this permission is already assigned to the user
                boolean isAssigned = assignedPermissionIds.contains(permission.getId());
                checkBox.setSelected(isAssigned);
                
                // Disable checkbox for assigned permissions to prevent duplicate assignments
                checkBox.setEnabled(!isAssigned);
                
                checkBox.addActionListener(e -> {
                    if (checkBox.isSelected()) {
                        assignPermission(permission);
                    }
                });
                
                permItem.add(checkBox, BorderLayout.CENTER);
                permissionsGrid.add(permItem);
            }
            
            modulePanel.add(permissionsGrid, BorderLayout.CENTER);
            permissionsPanel.add(modulePanel);
        }
        
        permissionsPanel.revalidate();
        permissionsPanel.repaint();
    }
    
    /**
     * Assigns a permission to the selected user
     * 
     * @param permission Permission to assign
     */
    private void assignPermission(Permission permission) {
        if (selectedUser == null) {
            return;
        }
        
        // Get the current user (the one assigning the permission)
        User currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "You must be logged in to assign permissions.", 
                    "Authentication Required", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            @Override
            protected Boolean doInBackground() throws Exception {
                return PermissionController.assignPermissionToUser(
                        selectedUser.getId(), 
                        permission.getId(), 
                        currentUser.getId()
                );
            }
            
            @Override
            protected void done() {
                try {
                    boolean success = get();
                    if (success) {
                        JOptionPane.showMessageDialog(parentFrame, 
                                "Permission assigned successfully.", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresh user permissions
                        loadUserPermissions();
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, 
                                "Failed to assign permission.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error assigning permission: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Revokes or activates a permission for the selected user
     * 
     * @param row Table row index
     */
    private void togglePermission(int row) {
        if (selectedUser == null || userPermissions == null || row < 0 || row >= userPermissions.size()) {
            return;
        }
        
        UserPermission userPermission = userPermissions.get(row);
        boolean isActive = userPermission.isActive();
        
        String action = isActive ? "revoke" : "activate";
        String message = "Are you sure you want to " + action + " this permission?";
        
        int result = JOptionPane.showConfirmDialog(parentFrame, 
                message, 
                "Confirm " + (isActive ? "Revocation" : "Activation"), 
                JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                @Override
                protected Boolean doInBackground() throws Exception {
                    if (isActive) {
                        return PermissionController.revokePermissionFromUser(
                                selectedUser.getId(), 
                                userPermission.getPermissionId()
                        );
                    } else {
                        // For activation, we use assign method which handles reactivation
                        User currentUser = AuthController.getCurrentUser();
                        return PermissionController.assignPermissionToUser(
                                selectedUser.getId(), 
                                userPermission.getPermissionId(), 
                                currentUser.getId()
                        );
                    }
                }
                
                @Override
                protected void done() {
                    try {
                        boolean success = get();
                        if (success) {
                            JOptionPane.showMessageDialog(parentFrame, 
                                    "Permission " + (isActive ? "revoked" : "activated") + " successfully.", 
                                    "Success", 
                                    JOptionPane.INFORMATION_MESSAGE);
                            
                            // Refresh user permissions
                            loadUserPermissions();
                        } else {
                            JOptionPane.showMessageDialog(parentFrame, 
                                    "Failed to " + action + " permission.", 
                                    "Error", 
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(parentFrame, 
                                "Error " + action + "ing permission: " + e.getMessage(), 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            };
            
            worker.execute();
        }
    }
    
    /**
     * Button renderer for the permissions table
     */
    private class ButtonRenderer extends JButton implements javax.swing.table.TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText(value.toString());
            
            // Set different colors for revoke and activate buttons
            if ("Revoke".equals(value.toString())) {
                setBackground(ColorScheme.DANGER);
                setForeground(Color.WHITE);
            } else {
                setBackground(ColorScheme.SUCCESS);
                setForeground(Color.WHITE);
            }
            
            return this;
        }
    }
    
    /**
     * Button editor for the permissions table
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton button;
        private String label;
        private boolean isPushed;
        private int row;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.addActionListener(e -> fireEditingStopped());
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.row = row;
            label = value.toString();
            button.setText(label);
            
            // Set different colors for revoke and activate buttons
            if ("Revoke".equals(label)) {
                button.setBackground(ColorScheme.DANGER);
                button.setForeground(Color.WHITE);
            } else {
                button.setBackground(ColorScheme.SUCCESS);
                button.setForeground(Color.WHITE);
            }
            
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                togglePermission(row);
            }
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
    }
}