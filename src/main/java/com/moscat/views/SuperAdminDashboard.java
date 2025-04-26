package com.moscat.views;

import com.moscat.controllers.AdminController;
import com.moscat.controllers.AuthController;
import com.moscat.controllers.LoanController;
import com.moscat.controllers.SavingsController;
import com.moscat.models.InterestSettings;
import com.moscat.models.LoanType;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Dashboard view for super admin users with extended functionality
 */
public class SuperAdminDashboard extends DashboardView {
    
    /**
     * Constructor for SuperAdminDashboard
     * 
     * @param parentFrame Parent JFrame
     */
    public SuperAdminDashboard(JFrame parentFrame) {
        super(parentFrame);
        
        // Add additional admin actions to the dashboard
        JPanel adminActionsPanel = createAdminActionsPanel();
        
        // Add to content panel
        contentPanel.add(adminActionsPanel, BorderLayout.SOUTH);
        
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    /**
     * Creates a panel with quick admin actions
     * 
     * @return JPanel with admin actions
     */
    private JPanel createAdminActionsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new TitledBorder("Super Admin Actions"));
        
        JPanel buttonsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        buttonsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create admin user button
        JButton createUserButton = new JButton("Create Admin User");
        createUserButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showCreateAdminUserDialog();
            }
        });
        
        // Update interest settings button
        JButton interestSettingsButton = new JButton("Update Interest Settings");
        interestSettingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSavingsSettings();
            }
        });
        
        // Manage loan types button
        JButton loanTypesButton = new JButton("Manage Loan Types");
        loanTypesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoanTypeManagementDialog();
            }
        });
        
        buttonsPanel.add(createUserButton);
        buttonsPanel.add(interestSettingsButton);
        buttonsPanel.add(loanTypesButton);
        
        panel.add(buttonsPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    /**
     * Shows a dialog to create a new admin user
     */
    private void showCreateAdminUserDialog() {
        JDialog dialog = new JDialog(parentFrame, "Create Admin User", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form fields
        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(20);
        
        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(20);
        
        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField(20);
        
        JLabel roleLabel = new JLabel("Role:");
        String[] roles = {"TREASURER", "BOOKKEEPER"};
        JComboBox<String> roleComboBox = new JComboBox<>(roles);
        
        JLabel fullNameLabel = new JLabel("Full Name:");
        JTextField fullNameField = new JTextField(20);
        
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(20);
        
        JLabel contactLabel = new JLabel("Contact Number:");
        JTextField contactField = new JTextField(20);
        
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
        
        JButton createButton = new JButton("Create User");
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
                    final JDialog progressDialog = new JDialog(dialog, "Creating User", true);
                    progressDialog.setLayout(new FlowLayout());
                    progressDialog.add(new JLabel("Creating user account..."));
                    progressDialog.setSize(250, 100);
                    progressDialog.setLocationRelativeTo(dialog);
                    
                    SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
                        @Override
                        protected Boolean doInBackground() throws Exception {
                            com.moscat.models.User newUser = new com.moscat.models.User();
                            newUser.setUsername(username);
                            newUser.setRole(role);
                            newUser.setStatus("ACTIVE");
                            newUser.setFullName(fullName);
                            newUser.setEmail(email);
                            newUser.setContactNumber(contact);
                            
                            return AdminController.createAdmin(newUser, password);
                        }
                        
                        @Override
                        protected void done() {
                            progressDialog.dispose();
                            
                            try {
                                boolean success = get();
                                
                                if (success) {
                                    JOptionPane.showMessageDialog(dialog, 
                                            "User created successfully!", 
                                            "Success", 
                                            JOptionPane.INFORMATION_MESSAGE);
                                    dialog.dispose();
                                } else {
                                    JOptionPane.showMessageDialog(dialog, 
                                            "Failed to create user.", 
                                            "Error", 
                                            JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (Exception ex) {
                                JOptionPane.showMessageDialog(dialog, 
                                        "Error creating user: " + ex.getMessage(), 
                                        "Error", 
                                        JOptionPane.ERROR_MESSAGE);
                                ex.printStackTrace();
                            }
                        }
                    };
                    
                    worker.execute();
                    progressDialog.setVisible(true);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Error creating user: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
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
     * Shows a dialog to manage loan types
     */
    private void showLoanTypeManagementDialog() {
        JDialog dialog = new JDialog(parentFrame, "Manage Loan Types", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(parentFrame);
        
        // Loan types list panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        DefaultListModel<LoanType> loanTypeListModel = new DefaultListModel<>();
        JList<LoanType> loanTypeList = new JList<>(loanTypeListModel);
        loanTypeList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        JScrollPane scrollPane = new JScrollPane(loanTypeList);
        scrollPane.setPreferredSize(new Dimension(200, 0));
        
        // Loan type details panel
        JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        detailsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        JLabel codeLabel = new JLabel("Code:");
        JTextField codeField = new JTextField(20);
        
        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(20);
        
        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField(20);
        
        JLabel interestRateLabel = new JLabel("Interest Rate (%):");
        JTextField interestRateField = new JTextField(20);
        
        JLabel minTermLabel = new JLabel("Min Term (months):");
        JTextField minTermField = new JTextField(20);
        
        JLabel maxTermLabel = new JLabel("Max Term (months):");
        JTextField maxTermField = new JTextField(20);
        
        JLabel minAmountLabel = new JLabel("Min Amount:");
        JTextField minAmountField = new JTextField(20);
        
        JLabel maxAmountLabel = new JLabel("Max Amount:");
        JTextField maxAmountField = new JTextField(20);
        
        JLabel requiresRLPFLabel = new JLabel("Requires RLPF:");
        JCheckBox requiresRLPFCheckbox = new JCheckBox();
        
        // Add components to details panel
        detailsPanel.add(codeLabel);
        detailsPanel.add(codeField);
        detailsPanel.add(nameLabel);
        detailsPanel.add(nameField);
        detailsPanel.add(descriptionLabel);
        detailsPanel.add(descriptionField);
        detailsPanel.add(interestRateLabel);
        detailsPanel.add(interestRateField);
        detailsPanel.add(minTermLabel);
        detailsPanel.add(minTermField);
        detailsPanel.add(maxTermLabel);
        detailsPanel.add(maxTermField);
        detailsPanel.add(minAmountLabel);
        detailsPanel.add(minAmountField);
        detailsPanel.add(maxAmountLabel);
        detailsPanel.add(maxAmountField);
        detailsPanel.add(requiresRLPFLabel);
        detailsPanel.add(requiresRLPFCheckbox);
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton newButton = new JButton("New");
        JButton saveButton = new JButton("Save");
        JButton closeButton = new JButton("Close");
        
        // Set up loan type list selection listener
        loanTypeList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                LoanType selectedLoanType = loanTypeList.getSelectedValue();
                if (selectedLoanType != null) {
                    codeField.setText(selectedLoanType.getCode());
                    codeField.setEditable(false); // Code cannot be changed once created
                    nameField.setText(selectedLoanType.getName());
                    descriptionField.setText(selectedLoanType.getDescription());
                    interestRateField.setText(String.valueOf(selectedLoanType.getInterestRate()));
                    minTermField.setText(String.valueOf(selectedLoanType.getMinTermMonths()));
                    maxTermField.setText(String.valueOf(selectedLoanType.getMaxTermMonths()));
                    minAmountField.setText(String.valueOf(selectedLoanType.getMinAmount()));
                    maxAmountField.setText(String.valueOf(selectedLoanType.getMaxAmount()));
                    requiresRLPFCheckbox.setSelected(selectedLoanType.isRequiresRLPF());
                }
            }
        });
        
        // Load loan types
        SwingWorker<List<LoanType>, Void> loadWorker = new SwingWorker<List<LoanType>, Void>() {
            @Override
            protected List<LoanType> doInBackground() throws Exception {
                return LoanController.getLoanTypes();
            }
            
            @Override
            protected void done() {
                try {
                    List<LoanType> loanTypes = get();
                    loanTypeListModel.clear();
                    for (LoanType loanType : loanTypes) {
                        loanTypeListModel.addElement(loanType);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Error loading loan types: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                    ex.printStackTrace();
                }
            }
        };
        
        loadWorker.execute();
        
        // New button action
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear fields and enable code field for new entry
                codeField.setText("");
                codeField.setEditable(true);
                nameField.setText("");
                descriptionField.setText("");
                interestRateField.setText("12.0");
                minTermField.setText("12");
                maxTermField.setText("60");
                minAmountField.setText("10000");
                maxAmountField.setText("500000");
                requiresRLPFCheckbox.setSelected(true);
                
                // Deselect list
                loanTypeList.clearSelection();
            }
        });
        
        // Save button action
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                String code = codeField.getText().trim();
                String name = nameField.getText().trim();
                String description = descriptionField.getText().trim();
                
                if (code.isEmpty() || name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Code and name are required fields.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    double interestRate = Double.parseDouble(interestRateField.getText().trim());
                    int minTerm = Integer.parseInt(minTermField.getText().trim());
                    int maxTerm = Integer.parseInt(maxTermField.getText().trim());
                    double minAmount = Double.parseDouble(minAmountField.getText().trim());
                    double maxAmount = Double.parseDouble(maxAmountField.getText().trim());
                    boolean requiresRLPF = requiresRLPFCheckbox.isSelected();
                    
                    // Create or update loan type
                    LoanType loanType = new LoanType();
                    loanType.setCode(code);
                    loanType.setName(name);
                    loanType.setDescription(description);
                    loanType.setInterestRate(interestRate);
                    loanType.setMinTermMonths(minTerm);
                    loanType.setMaxTermMonths(maxTerm);
                    loanType.setMinAmount(minAmount);
                    loanType.setMaxAmount(maxAmount);
                    loanType.setRequiresRLPF(requiresRLPF);
                    
                    boolean success = LoanController.saveLoanType(loanType);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, 
                                "Loan type saved successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        
                        // Reload loan types
                        loadWorker.execute();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "Failed to save loan type.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please enter valid numeric values for interest rate, terms, and amounts.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        // Close button action
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        buttonsPanel.add(newButton);
        buttonsPanel.add(saveButton);
        buttonsPanel.add(closeButton);
        
        // Layout components
        listPanel.add(new JLabel("Loan Types:"), BorderLayout.NORTH);
        listPanel.add(scrollPane, BorderLayout.CENTER);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(listPanel, BorderLayout.WEST);
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
}
