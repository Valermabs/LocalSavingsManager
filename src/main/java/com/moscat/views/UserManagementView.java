package com.moscat.views;

import com.moscat.controllers.AdminController;
import com.moscat.controllers.AuthController;
import com.moscat.models.User;
import com.moscat.views.components.CustomButton;
import com.moscat.views.components.CustomTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * View for managing system users
 */
public class UserManagementView extends JPanel {
    
    private JFrame parentFrame;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Constructor for UserManagementView
     * 
     * @param parentFrame Parent JFrame
     */
    public UserManagementView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
        loadUsers();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Create users table
        String[] columns = {"Username", "Name", "Role", "Email", "Contact", "Status", "Last Login"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(new TitledBorder("User Accounts"));
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton createButton = new CustomButton("Create User");
        JButton editButton = new CustomButton("Edit User");
        JButton resetPasswordButton = new CustomButton("Reset Password");
        JButton permissionsButton = new CustomButton("Manage Permissions");
        JButton activateButton = new CustomButton("Activate");
        JButton deactivateButton = new CustomButton("Deactivate");
        JButton refreshButton = new CustomButton("Refresh");
        
        // Create user button action
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCreateUserDialog();
            }
        });
        
        // Edit user button action
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editUser();
            }
        });
        
        // Reset password button action
        resetPasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetPassword();
            }
        });
        
        // Permissions button action
        permissionsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                managePermissions();
            }
        });
        
        // Activate button action
        activateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeUserStatus(true);
            }
        });
        
        // Deactivate button action
        deactivateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeUserStatus(false);
            }
        });
        
        // Refresh button action
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadUsers();
            }
        });
        
        buttonsPanel.add(createButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(resetPasswordButton);
        buttonsPanel.add(permissionsButton);
        buttonsPanel.add(activateButton);
        buttonsPanel.add(deactivateButton);
        buttonsPanel.add(refreshButton);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Loads users into the table
     */
    private void loadUsers() {
        // Clear the table
        tableModel.setRowCount(0);
        
        SwingWorker<List<User>, Void> worker = new SwingWorker<List<User>, Void>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                return AdminController.getAllAdmins();
            }
            
            @Override
            protected void done() {
                try {
                    List<User> users = get();
                    
                    for (User user : users) {
                        Object[] row = {
                            user.getUsername(),
                            user.getFullName(),
                            user.getRole(),
                            user.getEmail(),
                            user.getContactNumber(),
                            user.getStatus(),
                            user.getLastLogin() != null ? dateFormat.format(user.getLastLogin()) : ""
                        };
                        
                        tableModel.addRow(row);
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
     * Shows dialog to create a new user
     */
    private void showCreateUserDialog() {
        JDialog dialog = new JDialog(parentFrame, "Create User", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form fields
        JLabel usernameLabel = new JLabel("Username:*");
        CustomTextField usernameField = new CustomTextField();
        
        JLabel passwordLabel = new JLabel("Password:*");
        JPasswordField passwordField = new JPasswordField();
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:*");
        JPasswordField confirmPasswordField = new JPasswordField();
        
        JLabel roleLabel = new JLabel("Role:*");
        String[] roles = {"TREASURER", "BOOKKEEPER"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        
        JLabel fullNameLabel = new JLabel("Full Name:*");
        CustomTextField fullNameField = new CustomTextField();
        
        JLabel emailLabel = new JLabel("Email:");
        CustomTextField emailField = new CustomTextField();
        
        JLabel contactLabel = new JLabel("Contact Number:");
        CustomTextField contactField = new CustomTextField();
        
        // Add components to form panel
        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);
        formPanel.add(roleLabel);
        formPanel.add(roleComboBox);
        formPanel.add(fullNameLabel);
        formPanel.add(fullNameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JButton createButton = new CustomButton("Create User");
        createButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());
                String role = (String) roleComboBox.getSelectedItem();
                String fullName = fullNameField.getText().trim();
                String email = emailField.getText().trim();
                String contact = contactField.getText().trim();
                
                if (username.isEmpty() || password.isEmpty() || fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Username, password, and full name are required fields.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Passwords do not match.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if username is available
                if (!AdminController.isUsernameAvailable(username)) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Username is already taken. Please choose another.", 
                            "Username Unavailable", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create the user
                try {
                    User newUser = new User();
                    newUser.setUsername(username);
                    newUser.setRole(role);
                    newUser.setStatus("ACTIVE");
                    newUser.setFullName(fullName);
                    newUser.setEmail(email);
                    newUser.setContactNumber(contact);
                    
                    boolean success = AdminController.createAdmin(newUser, password);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, 
                                "User created successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        
                        // Refresh user list
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "Failed to create user.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, 
                            "Error creating user: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(createButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Shows dialog to edit selected user
     */
    private void editUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select a user to edit.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String username = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Find selected user
        List<User> users = AdminController.getAllAdmins();
        User tempUser = null;
        
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                tempUser = user;
                break;
            }
        }
        
        if (tempUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "User not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Cannot edit super admin
        if (tempUser.isSuperAdmin()) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Cannot edit super admin account.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create a final reference to be used in inner classes
        final User selectedUser = tempUser;
        
        // Show edit dialog
        JDialog dialog = new JDialog(parentFrame, "Edit User", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form fields
        JLabel usernameLabel = new JLabel("Username:");
        JLabel usernameValueLabel = new JLabel(selectedUser.getUsername());
        
        JLabel roleLabel = new JLabel("Role:*");
        String[] roles = {"TREASURER", "BOOKKEEPER"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        
        // Select current role
        for (int i = 0; i < roleComboBox.getItemCount(); i++) {
            if (roleComboBox.getItemAt(i).equals(selectedUser.getRole())) {
                roleComboBox.setSelectedIndex(i);
                break;
            }
        }
        
        JLabel fullNameLabel = new JLabel("Full Name:*");
        CustomTextField fullNameField = new CustomTextField(selectedUser.getFullName());
        
        JLabel emailLabel = new JLabel("Email:");
        CustomTextField emailField = new CustomTextField(selectedUser.getEmail());
        
        JLabel contactLabel = new JLabel("Contact Number:");
        CustomTextField contactField = new CustomTextField(selectedUser.getContactNumber());
        
        // Add components to form panel
        formPanel.add(usernameLabel);
        formPanel.add(usernameValueLabel);
        formPanel.add(roleLabel);
        formPanel.add(roleComboBox);
        formPanel.add(fullNameLabel);
        formPanel.add(fullNameField);
        formPanel.add(emailLabel);
        formPanel.add(emailField);
        formPanel.add(contactLabel);
        formPanel.add(contactField);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JButton saveButton = new CustomButton("Save Changes");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                final String role = (String) roleComboBox.getSelectedItem();
                final String fullName = fullNameField.getText().trim();
                final String email = emailField.getText().trim();
                final String contact = contactField.getText().trim();
                
                if (fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Full name is required.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create a final reference to selectedUser for use in the inner class
                final User finalSelectedUser = selectedUser;
                
                // Update the user
                try {
                    // Update user object with final variables
                    finalSelectedUser.setRole(role);
                    finalSelectedUser.setFullName(fullName);
                    finalSelectedUser.setEmail(email);
                    finalSelectedUser.setContactNumber(contact);
                    
                    final boolean roleSuccess = AdminController.updateAdminRole(finalSelectedUser.getId(), role);
                    final boolean infoSuccess = AdminController.updateAdmin(finalSelectedUser);
                    
                    if (roleSuccess && infoSuccess) {
                        JOptionPane.showMessageDialog(dialog, 
                                "User updated successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        
                        // Refresh user list
                        loadUsers();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "Failed to update user.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, 
                            "Error updating user: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Resets password for the selected user
     */
    private void resetPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select a user to reset password.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String username = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Find selected user
        List<User> users = AdminController.getAllAdmins();
        User tempUser = null;
        
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                tempUser = user;
                break;
            }
        }
        
        if (tempUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "User not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Cannot reset super admin password (for security)
        if (tempUser.isSuperAdmin()) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Cannot reset super admin password.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create a final reference to be used in inner classes
        final User selectedUser = tempUser;
        
        // Show reset password dialog
        JDialog dialog = new JDialog(parentFrame, "Reset Password", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form fields
        JLabel usernameLabel = new JLabel("Username:");
        JLabel usernameValueLabel = new JLabel(selectedUser.getUsername());
        
        JLabel newPasswordLabel = new JLabel("New Password:*");
        JPasswordField newPasswordField = new JPasswordField();
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:*");
        JPasswordField confirmPasswordField = new JPasswordField();
        
        // Add components to form panel
        formPanel.add(usernameLabel);
        formPanel.add(usernameValueLabel);
        formPanel.add(newPasswordLabel);
        formPanel.add(newPasswordField);
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JButton resetButton = new CustomButton("Reset Password");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                final String newPassword = new String(newPasswordField.getPassword());
                final String confirmPassword = new String(confirmPasswordField.getPassword());
                
                if (newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "New password is required.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (!newPassword.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Passwords do not match.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create a final reference to selectedUser for the inner class
                final User finalSelectedUser = selectedUser;
                
                // Reset the password
                try {
                    final boolean success = AdminController.resetAdminPassword(finalSelectedUser.getId(), newPassword);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, 
                                "Password reset successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "Failed to reset password.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, 
                            "Error resetting password: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(resetButton);
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    /**
     * Changes the status of the selected user
     * 
     * @param activate true to activate, false to deactivate
     */
    private void changeUserStatus(boolean activate) {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select a user to " + (activate ? "activate" : "deactivate") + ".", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String username = (String) tableModel.getValueAt(selectedRow, 0);
        String currentStatus = (String) tableModel.getValueAt(selectedRow, 5);
        
        // Check if status already matches desired state
        if ((activate && currentStatus.equals("ACTIVE")) || 
                (!activate && currentStatus.equals("INACTIVE"))) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "User is already " + (activate ? "active" : "inactive") + ".", 
                    "Status Info", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Find selected user
        List<User> users = AdminController.getAllAdmins();
        User tempUser = null;
        
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                tempUser = user;
                break;
            }
        }
        
        if (tempUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "User not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Cannot deactivate super admin
        if (!activate && tempUser.isSuperAdmin()) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Cannot deactivate super admin account.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Cannot deactivate current user
        if (!activate && AuthController.getCurrentUser().getUsername().equals(username)) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Cannot deactivate your own account.", 
                    "Access Denied", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create a final reference for inner classes
        final User selectedUser = tempUser;
        
        int confirmResult = JOptionPane.showConfirmDialog(parentFrame, 
                "Are you sure you want to " + (activate ? "activate" : "deactivate") + " this user?", 
                "Confirm Status Change", 
                JOptionPane.YES_NO_OPTION);
        
        if (confirmResult == JOptionPane.YES_OPTION) {
            boolean success = false;
            
            try {
                if (activate) {
                    success = AdminController.activateAdmin(selectedUser.getId());
                } else {
                    success = AdminController.deactivateAdmin(selectedUser.getId());
                }
                
                if (success) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "User " + (activate ? "activated" : "deactivated") + " successfully.", 
                            "Status Updated", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Failed to " + (activate ? "activate" : "deactivate") + " user.", 
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
    
    /**
     * Manages permissions for the selected user
     */
    private void managePermissions() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select a user to manage permissions.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        String username = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Find selected user
        List<User> users = AdminController.getAllAdmins();
        User selectedUser = null;
        
        for (User user : users) {
            if (user.getUsername().equals(username)) {
                selectedUser = user;
                break;
            }
        }
        
        if (selectedUser == null) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "User not found.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Show permissions management view
        UserPermissionsView permissionsView = new UserPermissionsView(parentFrame, selectedUser);
        permissionsView.setVisible(true);
    }
}