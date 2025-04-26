package com.moscat.views;

import com.moscat.controllers.MemberController;
import com.moscat.controllers.ReportController;
import com.moscat.models.Member;
import com.moscat.views.components.CustomButton;
import com.moscat.views.components.CustomTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * View for generating and exporting reports
 */
public class ReportView extends JPanel {
    
    private JFrame parentFrame;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private JPanel reportContentPanel;
    
    /**
     * Constructor for ReportView
     * 
     * @param parentFrame Parent JFrame
     */
    public ReportView(JFrame parentFrame) {
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
        JPanel headerPanel = new JPanel(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Create main panel with sidebar and content
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        // Create sidebar with report types
        JPanel sidebarPanel = createSidebarPanel();
        
        // Create report content panel
        reportContentPanel = new JPanel(new BorderLayout());
        reportContentPanel.setBorder(new TitledBorder("Report Output"));
        
        // Add welcome message
        JLabel welcomeLabel = new JLabel("Select a report type from the left sidebar to generate a report.");
        welcomeLabel.setHorizontalAlignment(JLabel.CENTER);
        welcomeLabel.setBorder(new EmptyBorder(50, 0, 0, 0));
        reportContentPanel.add(welcomeLabel, BorderLayout.CENTER);
        
        // Add components to main panel
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        mainPanel.add(reportContentPanel, BorderLayout.CENTER);
        
        // Add components to main view
        add(headerPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates the sidebar panel with report options
     * 
     * @return JPanel with report options
     */
    private JPanel createSidebarPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new TitledBorder("Report Types"));
        panel.setPreferredSize(new Dimension(200, 0));
        
        // Member report button
        JButton memberReportButton = new CustomButton("Member Report");
        memberReportButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        memberReportButton.setMaximumSize(new Dimension(180, 35));
        memberReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMemberReportOptions();
            }
        });
        
        // Daily transaction report button
        JButton dailyReportButton = new CustomButton("Daily Transaction Report");
        dailyReportButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        dailyReportButton.setMaximumSize(new Dimension(180, 35));
        dailyReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showDailyReportOptions();
            }
        });
        
        // Monthly transaction report button
        JButton monthlyReportButton = new CustomButton("Monthly Transaction Report");
        monthlyReportButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        monthlyReportButton.setMaximumSize(new Dimension(180, 35));
        monthlyReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showMonthlyReportOptions();
            }
        });
        
        // Loan summary report button
        JButton loanReportButton = new CustomButton("Loan Summary Report");
        loanReportButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loanReportButton.setMaximumSize(new Dimension(180, 35));
        loanReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showLoanReportOptions();
            }
        });
        
        // Savings summary report button
        JButton savingsReportButton = new CustomButton("Savings Summary Report");
        savingsReportButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        savingsReportButton.setMaximumSize(new Dimension(180, 35));
        savingsReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showSavingsReportOptions();
            }
        });
        
        // Add buttons to panel with spacing
        panel.add(Box.createVerticalStrut(10));
        panel.add(memberReportButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(dailyReportButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(monthlyReportButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(loanReportButton);
        panel.add(Box.createVerticalStrut(10));
        panel.add(savingsReportButton);
        panel.add(Box.createVerticalGlue());
        
        return panel;
    }
    
    /**
     * Shows options for member report
     */
    private void showMemberReportOptions() {
        // Clear content panel
        reportContentPanel.removeAll();
        
        // Create options panel
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Member Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        optionsPanel.add(titleLabel, gbc);
        
        // Member search
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        optionsPanel.add(new JLabel("Member Number:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        CustomTextField memberNumberField = new CustomTextField();
        memberNumberField.setPreferredSize(new Dimension(150, 25));
        optionsPanel.add(memberNumberField, gbc);
        
        gbc.gridx = 2;
        gbc.gridy = 1;
        JButton searchButton = new JButton("Search");
        optionsPanel.add(searchButton, gbc);
        
        // Member name (read-only)
        gbc.gridx = 0;
        gbc.gridy = 2;
        optionsPanel.add(new JLabel("Member Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel memberNameLabel = new JLabel("");
        optionsPanel.add(memberNameLabel, gbc);
        
        // Generate button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton generateButton = new CustomButton("Generate Report");
        generateButton.setEnabled(false);
        optionsPanel.add(generateButton, gbc);
        
        // Create result panel to hold the generated report
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("Report Preview"));
        
        // Search button action
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String memberNumber = memberNumberField.getText().trim();
                if (!memberNumber.isEmpty()) {
                    Member member = MemberController.getMemberByNumber(memberNumber);
                    if (member != null) {
                        memberNameLabel.setText(member.getFullName());
                        generateButton.setEnabled(true);
                    } else {
                        memberNameLabel.setText("Member not found");
                        generateButton.setEnabled(false);
                    }
                }
            }
        });
        
        // Generate button action
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String memberNumber = memberNumberField.getText().trim();
                if (!memberNumber.isEmpty()) {
                    Member member = MemberController.getMemberByNumber(memberNumber);
                    if (member != null) {
                        // Generate report
                        JPanel reportPanel = ReportController.generateMemberReport(member);
                        
                        // Clear result panel and add report
                        resultPanel.removeAll();
                        
                        // Add export button
                        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                        JButton exportButton = new JButton("Export to PDF");
                        exportButton.addActionListener(new ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                exportReport(reportPanel, "member_" + member.getMemberNumber() + "_report");
                            }
                        });
                        buttonPanel.add(exportButton);
                        
                        // Add to result panel
                        resultPanel.add(new JScrollPane(reportPanel), BorderLayout.CENTER);
                        resultPanel.add(buttonPanel, BorderLayout.SOUTH);
                        
                        resultPanel.revalidate();
                        resultPanel.repaint();
                    }
                }
            }
        });
        
        // Combine panels
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(optionsPanel, BorderLayout.NORTH);
        combinedPanel.add(resultPanel, BorderLayout.CENTER);
        
        reportContentPanel.add(combinedPanel, BorderLayout.CENTER);
        reportContentPanel.revalidate();
        reportContentPanel.repaint();
    }
    
    /**
     * Shows options for daily transaction report
     */
    private void showDailyReportOptions() {
        // Clear content panel
        reportContentPanel.removeAll();
        
        // Create options panel
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Daily Transaction Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        optionsPanel.add(titleLabel, gbc);
        
        // Date selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        optionsPanel.add(new JLabel("Select Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        CustomTextField dateField = new CustomTextField(dateFormat.format(new Date()));
        dateField.setPreferredSize(new Dimension(150, 25));
        optionsPanel.add(dateField, gbc);
        
        // Generate button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton generateButton = new CustomButton("Generate Report");
        optionsPanel.add(generateButton, gbc);
        
        // Create result panel to hold the generated report
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("Report Preview"));
        
        // Generate button action
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String date = dateField.getText().trim();
                
                try {
                    // Validate date format
                    dateFormat.parse(date);
                    
                    // Generate report
                    JPanel reportPanel = ReportController.generateDailyTransactionReport(date);
                    
                    // Clear result panel and add report
                    resultPanel.removeAll();
                    
                    // Add export button
                    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                    JButton exportButton = new JButton("Export to PDF");
                    JButton exportCsvButton = new JButton("Export to CSV");
                    
                    exportButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            exportReport(reportPanel, "daily_transactions_" + date);
                        }
                    });
                    
                    exportCsvButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            exportToCsv(reportPanel, "daily_transactions_" + date);
                        }
                    });
                    
                    buttonPanel.add(exportCsvButton);
                    buttonPanel.add(exportButton);
                    
                    // Add to result panel
                    resultPanel.add(new JScrollPane(reportPanel), BorderLayout.CENTER);
                    resultPanel.add(buttonPanel, BorderLayout.SOUTH);
                    
                    resultPanel.revalidate();
                    resultPanel.repaint();
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Invalid date format. Please use yyyy-MM-dd format.", 
                            "Date Error", 
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        });
        
        // Combine panels
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(optionsPanel, BorderLayout.NORTH);
        combinedPanel.add(resultPanel, BorderLayout.CENTER);
        
        reportContentPanel.add(combinedPanel, BorderLayout.CENTER);
        reportContentPanel.revalidate();
        reportContentPanel.repaint();
    }
    
    /**
     * Shows options for monthly transaction report
     */
    private void showMonthlyReportOptions() {
        // Clear content panel
        reportContentPanel.removeAll();
        
        // Create options panel
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 3;
        JLabel titleLabel = new JLabel("Monthly Transaction Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        optionsPanel.add(titleLabel, gbc);
        
        // Month selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        optionsPanel.add(new JLabel("Select Month:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        String[] months = {"January", "February", "March", "April", "May", "June", 
                "July", "August", "September", "October", "November", "December"};
        JComboBox<String> monthComboBox = new JComboBox<>(months);
        
        // Set current month as default
        Calendar cal = Calendar.getInstance();
        monthComboBox.setSelectedIndex(cal.get(Calendar.MONTH));
        
        optionsPanel.add(monthComboBox, gbc);
        
        // Year selection
        gbc.gridx = 0;
        gbc.gridy = 2;
        optionsPanel.add(new JLabel("Select Year:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        
        // Create year options (current year and 5 years back)
        int currentYear = cal.get(Calendar.YEAR);
        String[] years = new String[6];
        for (int i = 0; i < 6; i++) {
            years[i] = String.valueOf(currentYear - i);
        }
        
        JComboBox<String> yearComboBox = new JComboBox<>(years);
        optionsPanel.add(yearComboBox, gbc);
        
        // Generate button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton generateButton = new CustomButton("Generate Report");
        optionsPanel.add(generateButton, gbc);
        
        // Create result panel to hold the generated report
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("Report Preview"));
        
        // Generate button action
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int month = monthComboBox.getSelectedIndex() + 1; // Month is 1-indexed
                int year = Integer.parseInt((String) yearComboBox.getSelectedItem());
                
                // Generate report
                JPanel reportPanel = ReportController.generateMonthlyTransactionReport(year, month);
                
                // Clear result panel and add report
                resultPanel.removeAll();
                
                // Add export button
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton exportButton = new JButton("Export to PDF");
                JButton exportCsvButton = new JButton("Export to CSV");
                
                exportButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        exportReport(reportPanel, "monthly_transactions_" + year + "_" + month);
                    }
                });
                
                exportCsvButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        exportToCsv(reportPanel, "monthly_transactions_" + year + "_" + month);
                    }
                });
                
                buttonPanel.add(exportCsvButton);
                buttonPanel.add(exportButton);
                
                // Add to result panel
                resultPanel.add(new JScrollPane(reportPanel), BorderLayout.CENTER);
                resultPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                resultPanel.revalidate();
                resultPanel.repaint();
            }
        });
        
        // Combine panels
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(optionsPanel, BorderLayout.NORTH);
        combinedPanel.add(resultPanel, BorderLayout.CENTER);
        
        reportContentPanel.add(combinedPanel, BorderLayout.CENTER);
        reportContentPanel.revalidate();
        reportContentPanel.repaint();
    }
    
    /**
     * Shows options for loan summary report
     */
    private void showLoanReportOptions() {
        // Clear content panel
        reportContentPanel.removeAll();
        
        // Create options panel
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Loan Summary Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        optionsPanel.add(titleLabel, gbc);
        
        // Report type selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        optionsPanel.add(new JLabel("Report Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        String[] reportTypes = {"All Loans", "Active Loans", "Pending Loans", "Completed Loans", "Overdue Loans"};
        JComboBox<String> reportTypeComboBox = new JComboBox<>(reportTypes);
        optionsPanel.add(reportTypeComboBox, gbc);
        
        // Generate button
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton generateButton = new CustomButton("Generate Report");
        optionsPanel.add(generateButton, gbc);
        
        // Create result panel to hold the generated report
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("Report Preview"));
        
        // Generate button action
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String reportType = (String) reportTypeComboBox.getSelectedItem();
                
                // Generate report
                JPanel reportPanel = ReportController.generateLoanSummaryReport(reportType);
                
                // Clear result panel and add report
                resultPanel.removeAll();
                
                // Add export button
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton exportButton = new JButton("Export to PDF");
                JButton exportCsvButton = new JButton("Export to CSV");
                
                exportButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String filename = "loan_summary_" + 
                                reportType.toLowerCase().replace(" ", "_") + "_" + 
                                dateFormat.format(new Date());
                        exportReport(reportPanel, filename);
                    }
                });
                
                exportCsvButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String filename = "loan_summary_" + 
                                reportType.toLowerCase().replace(" ", "_") + "_" + 
                                dateFormat.format(new Date());
                        exportToCsv(reportPanel, filename);
                    }
                });
                
                buttonPanel.add(exportCsvButton);
                buttonPanel.add(exportButton);
                
                // Add to result panel
                resultPanel.add(new JScrollPane(reportPanel), BorderLayout.CENTER);
                resultPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                resultPanel.revalidate();
                resultPanel.repaint();
            }
        });
        
        // Combine panels
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(optionsPanel, BorderLayout.NORTH);
        combinedPanel.add(resultPanel, BorderLayout.CENTER);
        
        reportContentPanel.add(combinedPanel, BorderLayout.CENTER);
        reportContentPanel.revalidate();
        reportContentPanel.repaint();
    }
    
    /**
     * Shows options for savings summary report
     */
    private void showSavingsReportOptions() {
        // Clear content panel
        reportContentPanel.removeAll();
        
        // Create options panel
        JPanel optionsPanel = new JPanel(new GridBagLayout());
        optionsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Title
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        JLabel titleLabel = new JLabel("Savings Summary Report");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        optionsPanel.add(titleLabel, gbc);
        
        // Report type selection
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        optionsPanel.add(new JLabel("Report Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        String[] reportTypes = {"All Accounts", "Active Accounts", "Dormant Accounts", "Interest Earnings"};
        JComboBox<String> reportTypeComboBox = new JComboBox<>(reportTypes);
        optionsPanel.add(reportTypeComboBox, gbc);
        
        // Time period (only for Interest Earnings)
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel periodLabel = new JLabel("Time Period:");
        periodLabel.setVisible(false);
        optionsPanel.add(periodLabel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        String[] periods = {"Current Month", "Previous Month", "Current Year", "Previous Year"};
        JComboBox<String> periodComboBox = new JComboBox<>(periods);
        periodComboBox.setVisible(false);
        optionsPanel.add(periodComboBox, gbc);
        
        // Update visibility of period options based on report type
        reportTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String reportType = (String) reportTypeComboBox.getSelectedItem();
                boolean showPeriod = reportType.equals("Interest Earnings");
                periodLabel.setVisible(showPeriod);
                periodComboBox.setVisible(showPeriod);
            }
        });
        
        // Generate button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton generateButton = new CustomButton("Generate Report");
        optionsPanel.add(generateButton, gbc);
        
        // Create result panel to hold the generated report
        JPanel resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(new TitledBorder("Report Preview"));
        
        // Generate button action
        generateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String reportType = (String) reportTypeComboBox.getSelectedItem();
                String period = (String) periodComboBox.getSelectedItem();
                
                // Generate report
                JPanel reportPanel = ReportController.generateSavingsSummaryReport(reportType, period);
                
                // Clear result panel and add report
                resultPanel.removeAll();
                
                // Add export button
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton exportButton = new JButton("Export to PDF");
                JButton exportCsvButton = new JButton("Export to CSV");
                
                exportButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String filename = "savings_summary_" + 
                                reportType.toLowerCase().replace(" ", "_") + "_" + 
                                dateFormat.format(new Date());
                        exportReport(reportPanel, filename);
                    }
                });
                
                exportCsvButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String filename = "savings_summary_" + 
                                reportType.toLowerCase().replace(" ", "_") + "_" + 
                                dateFormat.format(new Date());
                        exportToCsv(reportPanel, filename);
                    }
                });
                
                buttonPanel.add(exportCsvButton);
                buttonPanel.add(exportButton);
                
                // Add to result panel
                resultPanel.add(new JScrollPane(reportPanel), BorderLayout.CENTER);
                resultPanel.add(buttonPanel, BorderLayout.SOUTH);
                
                resultPanel.revalidate();
                resultPanel.repaint();
            }
        });
        
        // Combine panels
        JPanel combinedPanel = new JPanel(new BorderLayout());
        combinedPanel.add(optionsPanel, BorderLayout.NORTH);
        combinedPanel.add(resultPanel, BorderLayout.CENTER);
        
        reportContentPanel.add(combinedPanel, BorderLayout.CENTER);
        reportContentPanel.revalidate();
        reportContentPanel.repaint();
    }
    
    /**
     * Exports a report panel to PDF
     * 
     * @param reportPanel Panel to export
     * @param baseFilename Base filename (without extension)
     */
    private void exportReport(JPanel reportPanel, String baseFilename) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to PDF");
        fileChooser.setSelectedFile(new File(baseFilename + ".pdf"));
        
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure file has .pdf extension
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            
            boolean success = ReportController.exportToPDF(reportPanel, filePath);
            
            if (success) {
                JOptionPane.showMessageDialog(parentFrame, 
                        "Report exported successfully to PDF.", 
                        "Export Complete", 
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                        "Failed to export report to PDF.", 
                        "Export Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Exports a report panel to CSV
     * 
     * @param reportPanel Panel to export
     * @param baseFilename Base filename (without extension)
     */
    private void exportToCsv(JPanel reportPanel, String baseFilename) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export to CSV");
        fileChooser.setSelectedFile(new File(baseFilename + ".csv"));
        
        int userSelection = fileChooser.showSaveDialog(parentFrame);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure file has .csv extension
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
            }
            
            // Find the JTable in the report panel
            JTable table = null;
            for (Component component : reportPanel.getComponents()) {
                if (component instanceof JScrollPane) {
                    JScrollPane scrollPane = (JScrollPane) component;
                    if (scrollPane.getViewport().getView() instanceof JTable) {
                        table = (JTable) scrollPane.getViewport().getView();
                        break;
                    }
                }
            }
            
            if (table != null) {
                boolean success = ReportController.exportToCSV(table, filePath);
                
                if (success) {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Report exported successfully to CSV.", 
                            "Export Complete", 
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Failed to export report to CSV.", 
                            "Export Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                        "No table data found to export.", 
                        "Export Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}