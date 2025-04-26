package com.moscat.views;

import com.moscat.controllers.AdminController;
import com.moscat.controllers.AuthController;
import com.moscat.controllers.PermissionController;
import com.moscat.models.User;
import com.moscat.utils.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * View for managing system users (Admins, Treasurers, Bookkeepers)
 */
public class UserManagementView extends JPanel {
    
    private final Frame owner;
    private DefaultTableModel tableModel;
    private JTable userTable;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton permissionsButton;
    private JButton refreshButton;
    
    /**
     * Constructor for UserManagementView
     * 
     * @param owner The owner frame
     */
    public UserManagementView(Frame owner) {
        this.owner = owner;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        initComponents();
        loadUsers();
    }
    
    /**
     * Initializes the UI components
     */
    private void initComponents() {
        // Add title panel
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 18));
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);
        
        // Create table model with columns
        String[] columns = {"ID", "Username", "Name", "Role", "Status", "Email", "Contact"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        // Create table
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(userTable);
        add(scrollPane, BorderLayout.CENTER);
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        addButton = new JButton("Add User");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddUserDialog();
            }
        });
        
        editButton = new JButton("Edit User");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showEditUserDialog();
            }
        });
        
        deleteButton = new JButton("Delete User");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteUser();
            }
        });
        
        permissionsButton = new JButton("Permissions");
        permissionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showPermissionsDialog();
            }
        });
        
        refreshButton = new JButton("Refresh");
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadUsers();
            }
        });
        
        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(permissionsButton);
        buttonPanel.add(refreshButton);
        
        add(buttonPanel, BorderLayout.SOUTH);
        
        // Initially disable buttons that require selection
        editButton.setEnabled(false);
        deleteButton.setEnabled(false);
        permissionsButton.setEnabled(false);
        
        // Add selection listener to enable/disable buttons
        userTable.getSelectionModel().addListSelectionListener(e -> {
            boolean hasSelection = userTable.getSelectedRow() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
            permissionsButton.setEnabled(hasSelection);
        });
    }
    
    /**
     * Loads users into the table
     */
    private void loadUsers() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Get all users
        List<User> users = AdminController.getAllAdmins();
        
        // Add users to table
        for (User user : users) {
            Object[] rowData = {
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole(),
                user.getStatus(),
                user.getEmail(),
                user.getContactNumber()
            };
            tableModel.addRow(rowData);
        }
    }
    
    /**
     * Shows the add user dialog
     */
    private void showAddUserDialog() {
        // Create dialog
        JDialog dialog = new JDialog(owner, "Add User", true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(owner);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(8, 2, 5, 5));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Add form fields
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField contactField = new JTextField();
        
        // Role combo box
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{
            Constants.ROLE_TREASURER,
            Constants.ROLE_BOOKKEEPER
        });
        
        // Status combo box
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{
            Constants.STATUS_ACTIVE,
            Constants.STATUS_INACTIVE
        });
        
        // Add fields to form
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        
        formPanel.add(new JLabel("Password:"));
        formPanel.add(passwordField);
        
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(contactField);
        
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleComboBox);
        
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                if (usernameField.getText().isEmpty() || 
                        passwordField.getPassword().length == 0 ||
                        firstNameField.getText().isEmpty() ||
                        lastNameField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please fill in all required fields.", 
                            "Validation Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if username is available
                if (!AdminController.isUsernameAvailable(usernameField.getText())) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Username is already taken.", 
                            "Validation Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create user object
                User user = new User();
                user.setUsername(usernameField.getText());
                user.setFirstName(firstNameField.getText());
                user.setLastName(lastNameField.getText());
                user.setEmail(emailField.getText());
                user.setContactNumber(contactField.getText());
                user.setRole((String) roleComboBox.getSelectedItem());
                user.setStatus((String) statusComboBox.getSelectedItem());
                
                // Add user
                boolean success = AdminController.createAdmin(user, new String(passwordField.getPassword()));
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                            "User added successfully.", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                            "Failed to add user.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setVisible(true);
    }
    
    /**
     * Shows the edit user dialog
     */
    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get selected user
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        User user = AuthController.getUserById(userId);
        
        if (user == null) {
            JOptionPane.showMessageDialog(this, 
                    "Failed to load user data.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create dialog
        JDialog dialog = new JDialog(owner, "Edit User: " + user.getUsername(), true);
        dialog.setSize(400, 400);
        dialog.setLocationRelativeTo(owner);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Add form fields
        JTextField usernameField = new JTextField(user.getUsername());
        usernameField.setEditable(false); // Cannot change username
        
        JTextField firstNameField = new JTextField(user.getFirstName());
        JTextField lastNameField = new JTextField(user.getLastName());
        JTextField emailField = new JTextField(user.getEmail());
        JTextField contactField = new JTextField(user.getContactNumber());
        
        // Role combo box
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{
            Constants.ROLE_TREASURER,
            Constants.ROLE_BOOKKEEPER
        });
        roleComboBox.setSelectedItem(user.getRole());
        
        // Status combo box
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{
            Constants.STATUS_ACTIVE,
            Constants.STATUS_INACTIVE,
            Constants.STATUS_DORMANT
        });
        statusComboBox.setSelectedItem(user.getStatus());
        
        // Password reset button
        JButton resetPasswordButton = new JButton("Reset Password");
        resetPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showResetPasswordDialog(user);
            }
        });
        
        // Add fields to form
        formPanel.add(new JLabel("Username:"));
        formPanel.add(usernameField);
        
        formPanel.add(new JLabel("First Name:"));
        formPanel.add(firstNameField);
        
        formPanel.add(new JLabel("Last Name:"));
        formPanel.add(lastNameField);
        
        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);
        
        formPanel.add(new JLabel("Contact:"));
        formPanel.add(contactField);
        
        formPanel.add(new JLabel("Role:"));
        formPanel.add(roleComboBox);
        
        formPanel.add(new JLabel("Status:"));
        formPanel.add(statusComboBox);
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        // SuperAdmin cannot be edited
        if (user.isSuperAdmin()) {
            roleComboBox.setEnabled(false);
            statusComboBox.setEnabled(false);
        }
        
        buttonPanel.add(resetPasswordButton);
        
        JButton saveButton = new JButton("Save");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                if (firstNameField.getText().isEmpty() || lastNameField.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please fill in all required fields.", 
                            "Validation Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Update user object
                user.setFirstName(firstNameField.getText());
                user.setLastName(lastNameField.getText());
                user.setEmail(emailField.getText());
                user.setContactNumber(contactField.getText());
                
                // Only update role and status if not SuperAdmin
                if (!user.isSuperAdmin()) {
                    user.setRole((String) roleComboBox.getSelectedItem());
                    user.setStatus((String) statusComboBox.getSelectedItem());
                }
                
                // Update user
                boolean success = AdminController.updateAdmin(user);
                if (success && !user.isSuperAdmin()) {
                    success = AdminController.updateAdminRole(user.getId(), user.getRole());
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                            "User updated successfully.", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                            "Failed to update user.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setVisible(true);
    }
    
    /**
     * Shows the reset password dialog
     * 
     * @param user The user to reset password for
     */
    private void showResetPasswordDialog(User user) {
        // Create dialog
        JDialog dialog = new JDialog(owner, "Reset Password: " + user.getUsername(), true);
        dialog.setSize(300, 150);
        dialog.setLocationRelativeTo(owner);
        
        // Create form panel
        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Add form fields
        JPasswordField passwordField = new JPasswordField();
        JPasswordField confirmPasswordField = new JPasswordField();
        
        // Add fields to form
        formPanel.add(new JLabel("New Password:"));
        formPanel.add(passwordField);
        
        formPanel.add(new JLabel("Confirm Password:"));
        formPanel.add(confirmPasswordField);
        
        // Add button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton saveButton = new JButton("Reset");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                if (passwordField.getPassword().length == 0) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please enter a password.", 
                            "Validation Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if passwords match
                if (!new String(passwordField.getPassword()).equals(
                        new String(confirmPasswordField.getPassword()))) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Passwords do not match.", 
                            "Validation Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Reset password
                boolean success = AdminController.resetAdminPassword(user.getId(), 
                        new String(passwordField.getPassword()));
                
                if (success) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Password reset successfully.", 
                            "Success", 
                            JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, 
                            "Failed to reset password.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        
        // Add panels to dialog
        dialog.setLayout(new BorderLayout());
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        // Show dialog
        dialog.setVisible(true);
    }
    
    /**
     * Deletes a user
     */
    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get selected user
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        String username = (String) tableModel.getValueAt(selectedRow, 1);
        String role = (String) tableModel.getValueAt(selectedRow, 3);
        
        // Cannot delete SuperAdmin
        if (role.equals(Constants.ROLE_SUPER_ADMIN)) {
            JOptionPane.showMessageDialog(this, 
                    "SuperAdmin account cannot be deleted.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete user '" + username + "'?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirm == JOptionPane.YES_OPTION) {
            boolean success = AdminController.deactivateAdmin(userId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "User deactivated successfully.", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to deactivate user.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Shows the permissions dialog for a user
     */
    private void showPermissionsDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        // Get selected user
        int userId = (int) tableModel.getValueAt(selectedRow, 0);
        User user = AuthController.getUserById(userId);
        
        if (user == null) {
            JOptionPane.showMessageDialog(this, 
                    "Failed to load user data.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show permissions dialog
        UserPermissionsView permissionsView = new UserPermissionsView(owner, user);
        permissionsView.setVisible(true);
    }
}