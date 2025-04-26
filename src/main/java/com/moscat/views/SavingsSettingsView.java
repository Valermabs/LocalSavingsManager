package com.moscat.views;

import com.moscat.controllers.SavingsController;
import com.moscat.models.InterestSettings;
import com.moscat.utils.DateUtils;
import com.moscat.views.components.CustomButton;
import com.moscat.views.components.CustomTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * View for managing savings account interest settings
 */
public class SavingsSettingsView extends JPanel {
    
    private JFrame parentFrame;
    private JTable settingsTable;
    private DefaultTableModel tableModel;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    /**
     * Constructor for SavingsSettingsView
     * 
     * @param parentFrame Parent JFrame
     */
    public SavingsSettingsView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
        loadSettings();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Savings Interest Settings");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Create settings table
        String[] columns = {"ID", "Interest Rate (%)", "Minimum Balance", "Calculation Method", "Effective Date", "Basis", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        settingsTable = new JTable(tableModel);
        settingsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Hide ID column
        settingsTable.getColumnModel().getColumn(0).setMinWidth(0);
        settingsTable.getColumnModel().getColumn(0).setMaxWidth(0);
        settingsTable.getColumnModel().getColumn(0).setWidth(0);
        
        JScrollPane scrollPane = new JScrollPane(settingsTable);
        scrollPane.setBorder(new TitledBorder("Interest Settings History"));
        
        // Create current settings panel
        JPanel currentSettingsPanel = createCurrentSettingsPanel();
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton newButton = new CustomButton("New Interest Setting");
        JButton refreshButton = new JButton("Refresh");
        
        // New button action
        newButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showNewSettingDialog();
            }
        });
        
        // Refresh button action
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadSettings();
            }
        });
        
        buttonsPanel.add(newButton);
        buttonsPanel.add(refreshButton);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(currentSettingsPanel, BorderLayout.NORTH);
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(centerPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates a panel showing current interest settings
     * 
     * @return JPanel with current settings
     */
    private JPanel createCurrentSettingsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                new TitledBorder("Current Settings"),
                new EmptyBorder(10, 10, 10, 10)));
        
        JPanel ratePanel = new JPanel(new BorderLayout());
        JLabel rateTitle = new JLabel("Interest Rate");
        JLabel rateValue = new JLabel("0.00%");
        rateTitle.setHorizontalAlignment(JLabel.CENTER);
        rateValue.setHorizontalAlignment(JLabel.CENTER);
        rateValue.setFont(new Font("Arial", Font.BOLD, 16));
        ratePanel.add(rateTitle, BorderLayout.NORTH);
        ratePanel.add(rateValue, BorderLayout.CENTER);
        
        JPanel minBalancePanel = new JPanel(new BorderLayout());
        JLabel minBalanceTitle = new JLabel("Minimum Balance");
        JLabel minBalanceValue = new JLabel("₱ 0.00");
        minBalanceTitle.setHorizontalAlignment(JLabel.CENTER);
        minBalanceValue.setHorizontalAlignment(JLabel.CENTER);
        minBalanceValue.setFont(new Font("Arial", Font.BOLD, 16));
        minBalancePanel.add(minBalanceTitle, BorderLayout.NORTH);
        minBalancePanel.add(minBalanceValue, BorderLayout.CENTER);
        
        JPanel methodPanel = new JPanel(new BorderLayout());
        JLabel methodTitle = new JLabel("Calculation Method");
        JLabel methodValue = new JLabel("MONTHLY");
        methodTitle.setHorizontalAlignment(JLabel.CENTER);
        methodValue.setHorizontalAlignment(JLabel.CENTER);
        methodValue.setFont(new Font("Arial", Font.BOLD, 16));
        methodPanel.add(methodTitle, BorderLayout.NORTH);
        methodPanel.add(methodValue, BorderLayout.CENTER);
        
        JPanel datePanel = new JPanel(new BorderLayout());
        JLabel dateTitle = new JLabel("Effective Since");
        JLabel dateValue = new JLabel(dateFormat.format(new Date()));
        dateTitle.setHorizontalAlignment(JLabel.CENTER);
        dateValue.setHorizontalAlignment(JLabel.CENTER);
        dateValue.setFont(new Font("Arial", Font.BOLD, 16));
        datePanel.add(dateTitle, BorderLayout.NORTH);
        datePanel.add(dateValue, BorderLayout.CENTER);
        
        panel.add(ratePanel);
        panel.add(minBalancePanel);
        panel.add(methodPanel);
        panel.add(datePanel);
        
        // Load current settings
        SwingWorker<InterestSettings, Void> worker = new SwingWorker<InterestSettings, Void>() {
            @Override
            protected InterestSettings doInBackground() throws Exception {
                return SavingsController.getCurrentInterestSettings();
            }
            
            @Override
            protected void done() {
                try {
                    InterestSettings settings = get();
                    
                    if (settings != null) {
                        rateValue.setText(decimalFormat.format(settings.getInterestRate()) + "%");
                        minBalanceValue.setText("₱ " + decimalFormat.format(settings.getMinimumBalance()));
                        methodValue.setText(settings.getCalculationMethod());
                        dateValue.setText(dateFormat.format(settings.getEffectiveDate()));
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        
        worker.execute();
        
        return panel;
    }
    
    /**
     * Loads interest settings into the table
     */
    private void loadSettings() {
        // Clear the table
        tableModel.setRowCount(0);
        
        SwingWorker<List<InterestSettings>, Void> worker = new SwingWorker<List<InterestSettings>, Void>() {
            @Override
            protected List<InterestSettings> doInBackground() throws Exception {
                return SavingsController.getInterestSettingsHistory();
            }
            
            @Override
            protected void done() {
                try {
                    List<InterestSettings> settingsList = get();
                    
                    for (InterestSettings settings : settingsList) {
                        Object[] row = {
                            settings.getId(),
                            decimalFormat.format(settings.getInterestRate()),
                            "₱ " + decimalFormat.format(settings.getMinimumBalance()),
                            settings.getCalculationMethod(),
                            dateFormat.format(settings.getEffectiveDate()),
                            settings.getChangeBasis(),
                            settings.getStatus()
                        };
                        
                        tableModel.addRow(row);
                    }
                    
                    // Refresh current settings panel
                    remove(((BorderLayout)getLayout()).getLayoutComponent(BorderLayout.CENTER));
                    JPanel centerPanel = new JPanel(new BorderLayout());
                    centerPanel.add(createCurrentSettingsPanel(), BorderLayout.NORTH);
                    centerPanel.add(new JScrollPane(settingsTable), BorderLayout.CENTER);
                    add(centerPanel, BorderLayout.CENTER);
                    
                    revalidate();
                    repaint();
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error loading interest settings: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Shows dialog to create a new interest setting
     */
    private void showNewSettingDialog() {
        JDialog dialog = new JDialog(parentFrame, "New Interest Setting", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(450, 400);
        dialog.setLocationRelativeTo(parentFrame);
        
        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form fields
        JLabel interestRateLabel = new JLabel("Interest Rate (%):*");
        CustomTextField interestRateField = new CustomTextField();
        
        JLabel minBalanceLabel = new JLabel("Minimum Balance:*");
        CustomTextField minBalanceField = new CustomTextField();
        
        JLabel calculationMethodLabel = new JLabel("Calculation Method:*");
        String[] methods = {"DAILY", "MONTHLY"};
        JComboBox<String> methodComboBox = new JComboBox<>(methods);
        
        JLabel effectiveDateLabel = new JLabel("Effective Date:*");
        CustomTextField effectiveDateField = new CustomTextField(dateFormat.format(new Date()));
        
        JLabel basisLabel = new JLabel("Change Basis:*");
        CustomTextField basisField = new CustomTextField();
        
        // Add components to form panel
        formPanel.add(interestRateLabel);
        formPanel.add(interestRateField);
        formPanel.add(minBalanceLabel);
        formPanel.add(minBalanceField);
        formPanel.add(calculationMethodLabel);
        formPanel.add(methodComboBox);
        formPanel.add(effectiveDateLabel);
        formPanel.add(effectiveDateField);
        formPanel.add(basisLabel);
        formPanel.add(basisField);
        
        // Add information text area
        JTextArea infoArea = new JTextArea(
                "Notes:\n" +
                "- New interest settings take effect from the specified effective date\n" +
                "- Change basis should explain the reason for the change (e.g. 'Board Resolution 2025-001')\n" +
                "- Daily calculation divides the annual rate by 365 days\n" +
                "- Monthly calculation divides the annual rate by 12 months");
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setBackground(formPanel.getBackground());
        infoArea.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JButton saveButton = new CustomButton("Save Setting");
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Validate input
                String interestRateText = interestRateField.getText().trim();
                String minBalanceText = minBalanceField.getText().trim();
                String calculationMethod = (String) methodComboBox.getSelectedItem();
                String effectiveDateText = effectiveDateField.getText().trim();
                String basis = basisField.getText().trim();
                
                if (interestRateText.isEmpty() || minBalanceText.isEmpty() || 
                        effectiveDateText.isEmpty() || basis.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, 
                            "All fields marked with * are required.", 
                            "Input Error", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                try {
                    double interestRate = Double.parseDouble(interestRateText);
                    double minBalance = Double.parseDouble(minBalanceText);
                    Date effectiveDate = dateFormat.parse(effectiveDateText);
                    
                    if (interestRate < 0) {
                        throw new NumberFormatException("Interest rate must be non-negative");
                    }
                    
                    if (minBalance < 0) {
                        throw new NumberFormatException("Minimum balance must be non-negative");
                    }
                    
                    // Create settings object
                    InterestSettings settings = new InterestSettings();
                    settings.setInterestRate(interestRate);
                    settings.setMinimumBalance(minBalance);
                    settings.setCalculationMethod(calculationMethod);
                    settings.setEffectiveDate(effectiveDate);
                    settings.setChangeBasis(basis);
                    settings.setStatus("ACTIVE");
                    settings.setCreatedAt(DateUtils.getCurrentDate());
                    
                    boolean success = SavingsController.createInterestSettings(settings);
                    
                    if (success) {
                        JOptionPane.showMessageDialog(dialog, 
                                "Interest settings saved successfully!", 
                                "Success", 
                                JOptionPane.INFORMATION_MESSAGE);
                        dialog.dispose();
                        
                        // Refresh settings list
                        loadSettings();
                    } else {
                        JOptionPane.showMessageDialog(dialog, 
                                "Failed to save interest settings.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, 
                            "Please enter valid numeric values for interest rate and minimum balance.", 
                            "Input Error", 
                            JOptionPane.WARNING_MESSAGE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(dialog, 
                            "Error: " + ex.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(saveButton);
        
        dialog.add(formPanel, BorderLayout.NORTH);
        dialog.add(new JScrollPane(infoArea), BorderLayout.CENTER);
        dialog.add(buttonsPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
}