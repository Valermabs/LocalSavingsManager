package com.moscat.views;

import com.moscat.controllers.MemberController;
import com.moscat.models.Member;
import com.moscat.views.components.CustomButton;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.List;

/**
 * View for displaying and managing members
 */
public class MemberListView extends JPanel {
    
    private JFrame parentFrame;
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private DecimalFormat currencyFormatter = new DecimalFormat("#,##0.00");
    
    /**
     * Constructor for MemberListView
     * 
     * @param parentFrame Parent JFrame
     */
    public MemberListView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
        loadMembers();
    }
    
    /**
     * Initializes the UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Create header with title and search
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Member Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        searchField = new JTextField(20);
        JButton searchButton = new CustomButton("Search");
        
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchMembers(searchField.getText().trim());
            }
        });
        
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(searchPanel, BorderLayout.EAST);
        
        // Create member table
        String[] columns = {"Member #", "Name", "Contact Number", "Email", "Status", "Join Date"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        
        JScrollPane scrollPane = new JScrollPane(memberTable);
        scrollPane.setBorder(new TitledBorder("Member List"));
        
        // Create buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton viewButton = new CustomButton("View Details");
        JButton editButton = new CustomButton("Edit Member");
        JButton activateButton = new CustomButton("Activate");
        JButton deactivateButton = new CustomButton("Deactivate");
        JButton refreshButton = new CustomButton("Refresh");
        
        // View button action
        viewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                viewMemberDetails();
            }
        });
        
        // Edit button action
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editMember();
            }
        });
        
        // Activate button action
        activateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeMemberStatus(true);
            }
        });
        
        // Deactivate button action
        deactivateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changeMemberStatus(false);
            }
        });
        
        // Refresh button action
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMembers();
            }
        });
        
        buttonsPanel.add(viewButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(activateButton);
        buttonsPanel.add(deactivateButton);
        buttonsPanel.add(refreshButton);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Loads all members into the table
     */
    private void loadMembers() {
        // Clear the table
        tableModel.setRowCount(0);
        
        SwingWorker<List<Member>, Void> worker = new SwingWorker<List<Member>, Void>() {
            @Override
            protected List<Member> doInBackground() throws Exception {
                return MemberController.getAllMembers();
            }
            
            @Override
            protected void done() {
                try {
                    List<Member> members = get();
                    
                    for (Member member : members) {
                        Object[] row = {
                            member.getMemberNumber(),
                            member.getFullName(),
                            member.getContactNumber(),
                            member.getEmail(),
                            member.getStatus(),
                            member.getJoinDate() != null ? member.getJoinDate().toString() : ""
                        };
                        
                        tableModel.addRow(row);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error loading members: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Searches for members matching the search term
     * 
     * @param searchTerm Text to search for
     */
    private void searchMembers(String searchTerm) {
        if (searchTerm.isEmpty()) {
            loadMembers();
            return;
        }
        
        // Clear the table
        tableModel.setRowCount(0);
        
        SwingWorker<List<Member>, Void> worker = new SwingWorker<List<Member>, Void>() {
            @Override
            protected List<Member> doInBackground() throws Exception {
                return MemberController.searchMembers(searchTerm);
            }
            
            @Override
            protected void done() {
                try {
                    List<Member> members = get();
                    
                    for (Member member : members) {
                        Object[] row = {
                            member.getMemberNumber(),
                            member.getFullName(),
                            member.getContactNumber(),
                            member.getEmail(),
                            member.getStatus(),
                            member.getJoinDate() != null ? member.getJoinDate().toString() : ""
                        };
                        
                        tableModel.addRow(row);
                    }
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(parentFrame, 
                            "Error searching members: " + e.getMessage(), 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        
        worker.execute();
    }
    
    /**
     * Views details of the selected member
     */
    private void viewMemberDetails() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow >= 0) {
            String memberNumber = (String) tableModel.getValueAt(selectedRow, 0);
            Member member = MemberController.getMemberByNumber(memberNumber);
            
            if (member != null) {
                // Create and display member details dialog
                JDialog dialog = new JDialog(parentFrame, "Member Details - " + member.getFullName(), true);
                dialog.setLayout(new BorderLayout());
                dialog.setSize(800, 600);
                dialog.setLocationRelativeTo(parentFrame);
                
                // Get member details panel from report controller
                JPanel detailsPanel = com.moscat.controllers.ReportController.generateMemberReport(member);
                JScrollPane scrollPane = new JScrollPane(detailsPanel);
                
                dialog.add(scrollPane, BorderLayout.CENTER);
                
                // Add close button
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
                JButton closeButton = new JButton("Close");
                closeButton.addActionListener(e -> dialog.dispose());
                buttonPanel.add(closeButton);
                dialog.add(buttonPanel, BorderLayout.SOUTH);
                
                dialog.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(parentFrame, 
                        "Error loading member details.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select a member to view details.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Opens dialog to edit selected member
     */
    private void editMember() {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow >= 0) {
            String memberNumber = (String) tableModel.getValueAt(selectedRow, 0);
            Member member = MemberController.getMemberByNumber(memberNumber);
            
            if (member != null) {
                // Create edit member dialog
                // Simplified for this implementation
                JOptionPane.showMessageDialog(parentFrame, 
                        "Edit Member functionality would open a form to edit " + member.getFullName(), 
                        "Edit Member", 
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select a member to edit.", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Changes the status of the selected member
     * 
     * @param activate true to activate, false to deactivate
     */
    private void changeMemberStatus(boolean activate) {
        int selectedRow = memberTable.getSelectedRow();
        if (selectedRow >= 0) {
            String memberNumber = (String) tableModel.getValueAt(selectedRow, 0);
            
            int confirmResult = JOptionPane.showConfirmDialog(parentFrame, 
                    "Are you sure you want to " + (activate ? "activate" : "deactivate") + " this member?", 
                    "Confirm Status Change", 
                    JOptionPane.YES_NO_OPTION);
            
            if (confirmResult == JOptionPane.YES_OPTION) {
                boolean success = false;
                
                try {
                    if (activate) {
                        success = MemberController.activateMember(memberNumber);
                    } else {
                        success = MemberController.deactivateMember(memberNumber);
                    }
                    
                    if (success) {
                        JOptionPane.showMessageDialog(parentFrame, 
                                "Member " + (activate ? "activated" : "deactivated") + " successfully.", 
                                "Status Updated", 
                                JOptionPane.INFORMATION_MESSAGE);
                        
                        loadMembers();
                    } else {
                        JOptionPane.showMessageDialog(parentFrame, 
                                "Failed to " + (activate ? "activate" : "deactivate") + " member.", 
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
        } else {
            JOptionPane.showMessageDialog(parentFrame, 
                    "Please select a member to " + (activate ? "activate" : "deactivate") + ".", 
                    "Selection Required", 
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}