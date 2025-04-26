package com.moscat.views;

import com.moscat.controllers.MemberController;
import com.moscat.models.Member;
import com.moscat.utils.DateUtils;
import com.moscat.views.components.CustomButton;
import com.moscat.views.components.CustomTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * View for registering new members
 */
public class MemberRegistrationView extends JPanel {
    
    private JFrame parentFrame;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    // Form fields
    private CustomTextField firstNameField;
    private CustomTextField middleNameField;
    private CustomTextField lastNameField;
    private CustomTextField birthDateField;
    private CustomTextField contactNumberField;
    private CustomTextField emailField;
    private CustomTextField presentAddressField;
    private CustomTextField permanentAddressField;
    private CustomTextField employerField;
    private JComboBox<String> employmentStatusCombo;
    private CustomTextField grossMonthlyIncomeField;
    private CustomTextField avgNetMonthlyIncomeField;
    
    /**
     * Constructor for MemberRegistrationView
     * 
     * @param parentFrame Parent JFrame
     */
    public MemberRegistrationView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel titleLabel = new JLabel("Member Registration");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel);
        
        // Create form panel
        JPanel formPanel = new JPanel(new BorderLayout());
        
        // Personal information panel
        JPanel personalInfoPanel = createPersonalInfoPanel();
        
        // Employment information panel
        JPanel employmentInfoPanel = createEmploymentInfoPanel();
        
        // Combine form sections
        JPanel formContentPanel = new JPanel(new GridLayout(2, 1, 0, 10));
        formContentPanel.add(personalInfoPanel);
        formContentPanel.add(employmentInfoPanel);
        
        // Add to form panel
        formPanel.add(formContentPanel, BorderLayout.CENTER);
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton clearButton = new JButton("Clear");
        JButton registerButton = new CustomButton("Register Member");
        
        // Clear button action
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearForm();
            }
        });
        
        // Register button action
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                registerMember();
            }
        });
        
        buttonsPanel.add(clearButton);
        buttonsPanel.add(registerButton);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(formPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the personal information panel
     * 
     * @return JPanel with personal information fields
     */
    private JPanel createPersonalInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Personal Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // First Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("First Name:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        firstNameField = new CustomTextField();
        panel.add(firstNameField, gbc);
        
        // Middle Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Middle Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        middleNameField = new CustomTextField();
        panel.add(middleNameField, gbc);
        
        // Last Name
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Last Name:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        lastNameField = new CustomTextField();
        panel.add(lastNameField, gbc);
        
        // Birth Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Birth Date (yyyy-MM-dd):*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        birthDateField = new CustomTextField();
        panel.add(birthDateField, gbc);
        
        // Contact Number
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Contact Number:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        contactNumberField = new CustomTextField();
        panel.add(contactNumberField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        emailField = new CustomTextField();
        panel.add(emailField, gbc);
        
        // Present Address
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Present Address:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        presentAddressField = new CustomTextField();
        panel.add(presentAddressField, gbc);
        
        // Permanent Address
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Permanent Address:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        permanentAddressField = new CustomTextField();
        panel.add(permanentAddressField, gbc);
        
        return panel;
    }
    
    /**
     * Creates the employment information panel
     * 
     * @return JPanel with employment information fields
     */
    private JPanel createEmploymentInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(new TitledBorder("Employment Information"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Employer
        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Employer:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        employerField = new CustomTextField();
        panel.add(employerField, gbc);
        
        // Employment Status
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Employment Status:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        String[] employmentStatuses = {"Regular", "Contract of Service", "Job Order"};
        employmentStatusCombo = new JComboBox<>(employmentStatuses);
        panel.add(employmentStatusCombo, gbc);
        
        // Gross Monthly Income
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Gross Monthly Income:*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        grossMonthlyIncomeField = new CustomTextField();
        panel.add(grossMonthlyIncomeField, gbc);
        
        // Average Net Monthly Income
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
        panel.add(new JLabel("Avg. Net Monthly Income (3 months):*"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        avgNetMonthlyIncomeField = new CustomTextField();
        panel.add(avgNetMonthlyIncomeField, gbc);
        
        return panel;
    }
    
    /**
     * Clears all form fields
     */
    private void clearForm() {
        firstNameField.setText("");
        middleNameField.setText("");
        lastNameField.setText("");
        birthDateField.setText("");
        contactNumberField.setText("");
        emailField.setText("");
        presentAddressField.setText("");
        permanentAddressField.setText("");
        employerField.setText("");
        employmentStatusCombo.setSelectedIndex(0);
        grossMonthlyIncomeField.setText("");
        avgNetMonthlyIncomeField.setText("");
    }
    
    /**
     * Registers a new member with form data
     */
    private void registerMember() {
        // Validate required fields
        if (firstNameField.getText().trim().isEmpty() ||
                lastNameField.getText().trim().isEmpty() ||
                birthDateField.getText().trim().isEmpty() ||
                contactNumberField.getText().trim().isEmpty() ||
                presentAddressField.getText().trim().isEmpty() ||
                permanentAddressField.getText().trim().isEmpty() ||
                employerField.getText().trim().isEmpty() ||
                grossMonthlyIncomeField.getText().trim().isEmpty() ||
                avgNetMonthlyIncomeField.getText().trim().isEmpty()) {
            
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please fill in all required fields marked with *", 
                    "Missing Information", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate birth date format
        Date birthDate = null;
        try {
            birthDate = dateFormat.parse(birthDateField.getText().trim());
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Invalid birth date format. Please use yyyy-MM-dd format.", 
                    "Invalid Date", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate numeric fields
        double grossMonthlyIncome = 0;
        double avgNetMonthlyIncome = 0;
        
        try {
            grossMonthlyIncome = Double.parseDouble(grossMonthlyIncomeField.getText().trim());
            if (grossMonthlyIncome <= 0) {
                throw new NumberFormatException("Gross monthly income must be positive");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Invalid gross monthly income. Please enter a valid number.", 
                    "Invalid Input", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            avgNetMonthlyIncome = Double.parseDouble(avgNetMonthlyIncomeField.getText().trim());
            if (avgNetMonthlyIncome <= 0) {
                throw new NumberFormatException("Average net monthly income must be positive");
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Invalid average net monthly income. Please enter a valid number.", 
                    "Invalid Input", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create member object
        Member member = new Member();
        member.setFirstName(firstNameField.getText().trim());
        member.setMiddleName(middleNameField.getText().trim());
        member.setLastName(lastNameField.getText().trim());
        member.setBirthDate(birthDate);
        member.setContactNumber(contactNumberField.getText().trim());
        member.setEmail(emailField.getText().trim());
        member.setPresentAddress(presentAddressField.getText().trim());
        member.setPermanentAddress(permanentAddressField.getText().trim());
        member.setEmployer(employerField.getText().trim());
        member.setEmploymentStatus((String) employmentStatusCombo.getSelectedItem());
        member.setGrossMonthlyIncome(grossMonthlyIncome);
        member.setAvgNetMonthlyIncome(avgNetMonthlyIncome);
        member.setStatus("ACTIVE");
        member.setJoinDate(DateUtils.getCurrentDate());
        member.setLastActivityDate(DateUtils.getCurrentDate());
        
        // Register the member
        try {
            boolean success = MemberController.registerMember(member);
            
            if (success) {
                JOptionPane.showMessageDialog(parentFrame, 
                        "Member registered successfully!", 
                        "Registration Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
                
                // Clear form after successful registration
                clearForm();
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                        "Failed to register member.", 
                        "Registration Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(parentFrame, 
                    "Error registering member: " + e.getMessage(), 
                    "Registration Error", 
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}