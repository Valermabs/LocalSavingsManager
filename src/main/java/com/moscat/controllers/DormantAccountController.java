package com.moscat.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.moscat.models.DormantAccount;
import com.moscat.models.Member;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;

/**
 * Controller for dormant account-related operations
 */
public class DormantAccountController {
    
    /**
     * Checks for dormant accounts and marks them
     * 
     * @return The number of accounts newly marked as dormant
     */
    public static int checkAndMarkDormantAccounts() {
        int newDormantCount = 0;
        
        // Get all active members
        List<Member> activeMembers = new ArrayList<>();
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM members WHERE status = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.STATUS_ACTIVE);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Member member = new Member();
                        member.setId(rs.getInt("id"));
                        member.setFirstName(rs.getString("first_name"));
                        member.setLastName(rs.getString("last_name"));
                        member.setStatus(rs.getString("status"));
                        
                        activeMembers.add(member);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting active members: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
        
        // Check each active member for dormancy
        for (Member member : activeMembers) {
            // Check if they have any activity in the last DORMANCY_PERIOD_MONTHS months
            boolean hasActivity = TransactionController.hasMemberActivity(
                member.getId(), Constants.DORMANCY_PERIOD_MONTHS);
            
            if (!hasActivity) {
                // Mark as dormant
                if (markAccountAsDormant(member.getId())) {
                    newDormantCount++;
                }
            }
        }
        
        return newDormantCount;
    }
    
    /**
     * Marks an account as dormant
     * 
     * @param memberId The member ID
     * @return True if successful, false otherwise
     */
    public static boolean markAccountAsDormant(int memberId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Start a transaction
            conn.setAutoCommit(false);
            
            try {
                // Update member status
                if (!MemberController.setMemberDormant(memberId)) {
                    conn.rollback();
                    return false;
                }
                
                // Get the last transaction date
                LocalDateTime lastTransactionDate = TransactionController.getLastTransactionDate(memberId);
                
                // Create dormant account record
                String insertQuery = "INSERT INTO dormant_accounts (member_id, last_transaction_date, dormant_since, dormant_status, notification_sent) VALUES (?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setInt(1, memberId);
                    
                    if (lastTransactionDate != null) {
                        stmt.setTimestamp(2, Timestamp.valueOf(lastTransactionDate));
                    } else {
                        stmt.setTimestamp(2, null);
                    }
                    
                    stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setString(4, Constants.STATUS_DORMANT);
                    stmt.setBoolean(5, false);
                    
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected <= 0) {
                        conn.rollback();
                        return false;
                    }
                }
                
                // Commit the transaction
                conn.commit();
                return true;
            } catch (Exception e) {
                // Rollback in case of an error
                conn.rollback();
                throw e;
            } finally {
                // Restore auto-commit
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error marking account as dormant: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Reactivates a dormant account
     * 
     * @param memberId The member ID
     * @return True if successful, false otherwise
     */
    public static boolean reactivateDormantAccount(int memberId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Start a transaction
            conn.setAutoCommit(false);
            
            try {
                // Update member status
                if (!MemberController.reactivateMember(memberId)) {
                    conn.rollback();
                    return false;
                }
                
                // Update dormant account record
                String updateQuery = "UPDATE dormant_accounts SET dormant_status = 'Reactivated' WHERE member_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                    stmt.setInt(1, memberId);
                    
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected <= 0) {
                        conn.rollback();
                        return false;
                    }
                }
                
                // Commit the transaction
                conn.commit();
                return true;
            } catch (Exception e) {
                // Rollback in case of an error
                conn.rollback();
                throw e;
            } finally {
                // Restore auto-commit
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            System.err.println("Error reactivating dormant account: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets dormant accounts
     * 
     * @return List of dormant accounts
     */
    public static List<DormantAccount> getDormantAccounts() {
        List<DormantAccount> dormantAccounts = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM dormant_accounts WHERE dormant_status = ? ORDER BY dormant_since DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.STATUS_DORMANT);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        dormantAccounts.add(extractDormantAccountFromResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting dormant accounts: " + e.getMessage());
            e.printStackTrace();
        }
        
        return dormantAccounts;
    }
    
    /**
     * Gets the dormant account count
     * 
     * @return The number of dormant accounts
     */
    public static int getDormantAccountCount() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT COUNT(*) FROM dormant_accounts WHERE dormant_status = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.STATUS_DORMANT);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting dormant account count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Marks a notification as sent for a dormant account
     * 
     * @param dormantAccountId The dormant account ID
     * @return True if successful, false otherwise
     */
    public static boolean markNotificationSent(int dormantAccountId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE dormant_accounts SET notification_sent = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setBoolean(1, true);
                stmt.setInt(2, dormantAccountId);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error marking notification sent: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Extracts a DormantAccount object from a ResultSet
     * 
     * @param rs The ResultSet
     * @return The extracted DormantAccount
     * @throws SQLException If a database error occurs
     */
    private static DormantAccount extractDormantAccountFromResultSet(ResultSet rs) throws SQLException {
        DormantAccount account = new DormantAccount();
        account.setId(rs.getInt("id"));
        account.setMemberId(rs.getInt("member_id"));
        
        Timestamp lastTransactionDate = rs.getTimestamp("last_transaction_date");
        if (lastTransactionDate != null) {
            account.setLastTransactionDate(lastTransactionDate.toLocalDateTime());
        }
        
        Timestamp dormantSince = rs.getTimestamp("dormant_since");
        if (dormantSince != null) {
            account.setDormantSince(dormantSince.toLocalDateTime());
        }
        
        account.setDormantStatus(rs.getString("dormant_status"));
        account.setNotificationSent(rs.getBoolean("notification_sent"));
        
        return account;
    }
}