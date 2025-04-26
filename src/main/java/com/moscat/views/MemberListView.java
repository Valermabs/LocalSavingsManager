package com.moscat.views;

import com.moscat.controllers.MemberController;
import com.moscat.models.Member;
import com.moscat.utils.Constants;
import com.moscat.utils.DateUtils;
import com.moscat.views.components.CustomButton;
import com.moscat.views.components.CustomTextField;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * View for member listing and management
 */
public class MemberListView extends JPanel {
    
    private JFrame parentFrame;
    private JTable memberTable;
    private DefaultTableModel tableModel;
    private CustomTextField searchField;
    private JComboBox<String> statusFilter;
    private List<Member> currentMembers;
    
    /**
     * Constructor
     * 
     * @param parentFrame Parent frame
     */
    public MemberListView(JFrame parentFrame) {
        this.parentFrame = parentFrame;
        initializeUI();
        loadMembers();
    }
    
    /**
     * Initializes UI components
     */
    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Create header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Member Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        CustomButton addButton = new CustomButton("Add New Member");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showAddMemberDialog();
            }
        });
        buttonPanel.add(addButton);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(buttonPanel, BorderLayout.EAST);
        
        // Create search and filter panel
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBorder(new EmptyBorder(0, 0, 20, 0));
        
        JPanel searchInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchField = new CustomTextField();
        searchField.setPreferredSize(new Dimension(250, Constants.TEXT_FIELD_HEIGHT));
        searchField.setPlaceholder("Search members...");
        
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchMembers();
            }
        });
        
        searchInputPanel.add(searchField);
        searchInputPanel.add(searchButton);
        
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        filterPanel.add(new JLabel("Status:"));
        
        statusFilter = new JComboBox<>(new String[]{"All", "Active", "Inactive", "Pending"});
        statusFilter.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filterMembers();
            }
        });
        filterPanel.add(statusFilter);
        
        searchPanel.add(searchInputPanel, BorderLayout.WEST);
        searchPanel.add(filterPanel, BorderLayout.EAST);
        
        // Create table
        createMemberTable();
        JScrollPane tableScrollPane = new JScrollPane(memberTable);
        
        // Add components to main panel
        add(headerPanel, BorderLayout.NORTH);
        add(searchPanel, BorderLayout.CENTER);
        add(tableScrollPane, BorderLayout.SOUTH);
    }
    
    /**
     * Creates member table
     */
    private void createMemberTable() {
        String[] columnNames = {
            "Member ID", "Name", "Contact Number", "Email", "Join Date", "Status", "Actions"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only allow editing action column
            }
        };
        
        memberTable = new JTable(tableModel);
        memberTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        memberTable.setRowHeight(35);
        memberTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        memberTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        memberTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        memberTable.getColumnModel().getColumn(2).setPreferredWidth(120);
        memberTable.getColumnModel().getColumn(3).setPreferredWidth(180);
        memberTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        memberTable.getColumnModel().getColumn(5).setPreferredWidth(80);
        memberTable.getColumnModel().getColumn(6).setPreferredWidth(120);
        
        // Add cell renderer for action column
        memberTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer());
        
        // Add cell editor for action column
        memberTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JCheckBox()));
        
        // Add row click listener
        memberTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = memberTable.rowAtPoint(e.getPoint());
                int col = memberTable.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col == 6) {
                    // Action column clicked, handled by cell editor
                } else if (row >= 0 && e.getClickCount() == 2) {
                    // Double-click on row, show member details
                    showMemberDetails(row);
                }
            }
        });
    }
    
    /**
     * Loads members into table
     */
    private void loadMembers() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get members
        currentMembers = MemberController.getAllMembers();
        
        // Add members to table
        for (Member member : currentMembers) {
            addMemberToTable(member);
        }
    }
    
    /**
     * Adds a member to the table
     * 
     * @param member Member to add
     */
    private void addMemberToTable(Member member) {
        Object[] rowData = {
            member.getMemberNumber(),
            member.getFullName(),
            member.getContactNumber(),
            member.getEmail(),
            DateUtils.formatDateForDisplay(member.getJoinDate()),
            member.getStatus(),
            "Actions"
        };
        tableModel.addRow(rowData);
    }
    
    /**
     * Shows member details
     * 
     * @param row Table row
     */
    private void showMemberDetails(int row) {
        if (row >= 0 && row < currentMembers.size()) {
            Member member = currentMembers.get(row);
            // For now just show a message dialog
            JOptionPane.showMessageDialog(parentFrame,
                    "Member Details:\n" +
                    "ID: " + member.getMemberNumber() + "\n" +
                    "Name: " + member.getFullName() + "\n" +
                    "Contact: " + member.getContactNumber() + "\n" +
                    "Email: " + member.getEmail() + "\n" +
                    "Status: " + member.getStatus(),
                    "Member Details",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Shows add member dialog
     */
    private void showAddMemberDialog() {
        // For now just show a placeholder dialog
        JOptionPane.showMessageDialog(parentFrame,
                "Add Member functionality will be implemented in a future update.",
                "Add Member",
                JOptionPane.INFORMATION_MESSAGE);
        
        // In a real implementation, we would show a MemberRegistrationView
        // MemberRegistrationView registrationView = new MemberRegistrationView(parentFrame);
        // registrationView.setVisible(true);
    }
    
    /**
     * Searches members based on search field
     */
    private void searchMembers() {
        String searchTerm = searchField.getText().trim();
        
        if (searchTerm.isEmpty()) {
            loadMembers();
            return;
        }
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Search members
        currentMembers = MemberController.searchMembers(searchTerm);
        
        // Apply filter
        filterMembers(currentMembers);
    }
    
    /**
     * Filters members based on status filter
     */
    private void filterMembers() {
        // Get all members first
        List<Member> allMembers = MemberController.getAllMembers();
        
        // Apply filter
        filterMembers(allMembers);
    }
    
    /**
     * Filters a list of members based on status filter
     * 
     * @param members List of members to filter
     */
    private void filterMembers(List<Member> members) {
        String selectedFilter = (String) statusFilter.getSelectedItem();
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Reset current members
        currentMembers.clear();
        
        // Apply filter
        for (Member member : members) {
            boolean addMember = false;
            
            if (selectedFilter.equals("All")) {
                addMember = true;
            } else if (selectedFilter.equals("Active") && member.isActive()) {
                addMember = true;
            } else if (selectedFilter.equals("Inactive") && member.isInactive()) {
                addMember = true;
            } else if (selectedFilter.equals("Pending") && member.isPending()) {
                addMember = true;
            }
            
            if (addMember) {
                currentMembers.add(member);
                addMemberToTable(member);
            }
        }
    }
    
    /**
     * Activates a member
     * 
     * @param memberNumber Member number
     */
    private void activateMember(String memberNumber) {
        boolean success = MemberController.activateMember(memberNumber);
        
        if (success) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Member activated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadMembers();
        } else {
            JOptionPane.showMessageDialog(parentFrame,
                    "Failed to activate member.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Deactivates a member
     * 
     * @param memberNumber Member number
     */
    private void deactivateMember(String memberNumber) {
        boolean success = MemberController.deactivateMember(memberNumber);
        
        if (success) {
            JOptionPane.showMessageDialog(parentFrame,
                    "Member deactivated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            loadMembers();
        } else {
            JOptionPane.showMessageDialog(parentFrame,
                    "Failed to deactivate member.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Custom renderer for buttons in table
     */
    private class ButtonRenderer extends JPanel implements javax.swing.table.TableCellRenderer {
        private JButton viewButton;
        private JButton actionButton;
        
        public ButtonRenderer() {
            setLayout(new GridLayout(1, 2, 5, 0));
            
            viewButton = new JButton("View");
            viewButton.setFocusPainted(false);
            
            actionButton = new JButton("Action");
            actionButton.setFocusPainted(false);
            
            add(viewButton);
            add(actionButton);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            // Get member status
            String status = (String) table.getValueAt(row, 5);
            
            if (Constants.STATUS_ACTIVE.equals(status)) {
                actionButton.setText("Deactivate");
            } else if (Constants.STATUS_INACTIVE.equals(status)) {
                actionButton.setText("Activate");
            } else {
                actionButton.setText("Action");
            }
            
            return this;
        }
    }
    
    /**
     * Custom editor for buttons in table
     */
    private class ButtonEditor extends DefaultCellEditor {
        private JButton viewButton;
        private JButton actionButton;
        private JPanel panel;
        private String memberNumber;
        private String memberStatus;
        
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            
            panel = new JPanel(new GridLayout(1, 2, 5, 0));
            
            viewButton = new JButton("View");
            viewButton.setFocusPainted(false);
            viewButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    int row = memberTable.getSelectedRow();
                    showMemberDetails(row);
                    fireEditingStopped();
                }
            });
            
            actionButton = new JButton("Action");
            actionButton.setFocusPainted(false);
            actionButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (Constants.STATUS_ACTIVE.equals(memberStatus)) {
                        int result = JOptionPane.showConfirmDialog(parentFrame,
                                "Are you sure you want to deactivate this member?",
                                "Confirm Deactivation",
                                JOptionPane.YES_NO_OPTION);
                        
                        if (result == JOptionPane.YES_OPTION) {
                            deactivateMember(memberNumber);
                        }
                    } else if (Constants.STATUS_INACTIVE.equals(memberStatus)) {
                        int result = JOptionPane.showConfirmDialog(parentFrame,
                                "Are you sure you want to activate this member?",
                                "Confirm Activation",
                                JOptionPane.YES_NO_OPTION);
                        
                        if (result == JOptionPane.YES_OPTION) {
                            activateMember(memberNumber);
                        }
                    }
                    fireEditingStopped();
                }
            });
            
            panel.add(viewButton);
            panel.add(actionButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            memberNumber = (String) table.getValueAt(row, 0);
            memberStatus = (String) table.getValueAt(row, 5);
            
            if (Constants.STATUS_ACTIVE.equals(memberStatus)) {
                actionButton.setText("Deactivate");
            } else if (Constants.STATUS_INACTIVE.equals(memberStatus)) {
                actionButton.setText("Activate");
            } else {
                actionButton.setText("Action");
            }
            
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "Actions";
        }
    }
}