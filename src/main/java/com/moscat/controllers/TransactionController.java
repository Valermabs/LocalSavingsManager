package com.moscat.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.moscat.models.Member;
import com.moscat.models.Transaction;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;

/**
 * Controller for transaction-related operations
 */
public class TransactionController {
    
    /**
     * Processes a deposit transaction
     * 
     * @param memberId The member ID
     * @param amount The deposit amount
     * @param description The transaction description
     * @param processedBy The username of the user who processed the transaction
     * @return True if successful, false otherwise
     */
    public static boolean processDeposit(int memberId, double amount, String description, String processedBy) {
        if (amount <= 0) {
            return false;
        }
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Start a transaction
            conn.setAutoCommit(false);
            
            try {
                // Get the current balance
                Member member = MemberController.getMemberById(memberId);
                if (member == null) {
                    return false;
                }
                
                double newBalance = member.getSavingsBalance() + amount;
                
                // Update the member's balance
                String updateQuery = "UPDATE members SET savings_balance = ?, updated_at = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                    stmt.setDouble(1, newBalance);
                    stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setInt(3, memberId);
                    
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected <= 0) {
                        conn.rollback();
                        return false;
                    }
                }
                
                // Create a transaction record
                String insertQuery = "INSERT INTO transactions (member_id, transaction_type, amount, transaction_date, description, processed_by) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setInt(1, memberId);
                    stmt.setString(2, Constants.TRANSACTION_DEPOSIT);
                    stmt.setDouble(3, amount);
                    stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setString(5, description);
                    stmt.setString(6, processedBy);
                    
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
            System.err.println("Error processing deposit: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Processes a withdrawal transaction
     * 
     * @param memberId The member ID
     * @param amount The withdrawal amount
     * @param description The transaction description
     * @param processedBy The username of the user who processed the transaction
     * @return True if successful, false otherwise
     */
    public static boolean processWithdrawal(int memberId, double amount, String description, String processedBy) {
        if (amount <= 0) {
            return false;
        }
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Start a transaction
            conn.setAutoCommit(false);
            
            try {
                // Get the current balance
                Member member = MemberController.getMemberById(memberId);
                if (member == null) {
                    return false;
                }
                
                // Check if the balance is sufficient
                if (member.getSavingsBalance() < amount) {
                    return false;
                }
                
                double newBalance = member.getSavingsBalance() - amount;
                
                // Update the member's balance
                String updateQuery = "UPDATE members SET savings_balance = ?, updated_at = ? WHERE id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                    stmt.setDouble(1, newBalance);
                    stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setInt(3, memberId);
                    
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected <= 0) {
                        conn.rollback();
                        return false;
                    }
                }
                
                // Create a transaction record
                String insertQuery = "INSERT INTO transactions (member_id, transaction_type, amount, transaction_date, description, processed_by) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setInt(1, memberId);
                    stmt.setString(2, Constants.TRANSACTION_WITHDRAWAL);
                    stmt.setDouble(3, amount);
                    stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setString(5, description);
                    stmt.setString(6, processedBy);
                    
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
            System.err.println("Error processing withdrawal: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Records an interest transaction
     * 
     * @param memberId The member ID
     * @param amount The interest amount
     * @param description The transaction description
     * @param processedBy The username of the user who processed the transaction
     * @return True if successful, false otherwise
     */
    public static boolean recordInterest(int memberId, double amount, String description, String processedBy) {
        if (amount <= 0) {
            return false;
        }
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Start a transaction
            conn.setAutoCommit(false);
            
            try {
                // Update the member's interest earned
                if (!MemberController.addInterestEarned(memberId, amount)) {
                    conn.rollback();
                    return false;
                }
                
                // Create a transaction record
                String insertQuery = "INSERT INTO transactions (member_id, transaction_type, amount, transaction_date, description, processed_by) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                    stmt.setInt(1, memberId);
                    stmt.setString(2, Constants.TRANSACTION_INTEREST);
                    stmt.setDouble(3, amount);
                    stmt.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                    stmt.setString(5, description);
                    stmt.setString(6, processedBy);
                    
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
            System.err.println("Error recording interest: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets transactions for a member
     * 
     * @param memberId The member ID
     * @return List of transactions for the member
     */
    public static List<Transaction> getMemberTransactions(int memberId) {
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM transactions WHERE member_id = ? ORDER BY transaction_date DESC";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, memberId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        transactions.add(extractTransactionFromResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting member transactions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Gets recent transactions
     * 
     * @param limit The maximum number of transactions to get
     * @return List of recent transactions
     */
    public static List<Transaction> getRecentTransactions(int limit) {
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM transactions ORDER BY transaction_date DESC LIMIT ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, limit);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        transactions.add(extractTransactionFromResultSet(rs));
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting recent transactions: " + e.getMessage());
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Gets the number of active loans
     * 
     * @return The active loan count
     */
    public static int getActiveLoanCount() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT COUNT(*) FROM loans WHERE status = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.LOAN_STATUS_ACTIVE);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting active loan count: " + e.getMessage());
            e.printStackTrace();
        }
        
        return 0;
    }
    
    /**
     * Checks if a member has any activity within a certain number of months
     * 
     * @param memberId The member ID
     * @param months The number of months
     * @return True if the member has activity, false otherwise
     */
    public static boolean hasMemberActivity(int memberId, int months) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT COUNT(*) FROM transactions WHERE member_id = ? AND transaction_date > ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, memberId);
                
                // Calculate the date that is 'months' months ago
                LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(months);
                stmt.setTimestamp(2, Timestamp.valueOf(cutoffDate));
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) > 0;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error checking member activity: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Gets the last transaction date for a member
     * 
     * @param memberId The member ID
     * @return The last transaction date, or null if no transactions
     */
    public static LocalDateTime getLastTransactionDate(int memberId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT MAX(transaction_date) FROM transactions WHERE member_id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, memberId);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        Timestamp timestamp = rs.getTimestamp(1);
                        if (timestamp != null) {
                            return timestamp.toLocalDateTime();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error getting last transaction date: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Extracts a Transaction object from a ResultSet
     * 
     * @param rs The ResultSet
     * @return The extracted Transaction
     * @throws SQLException If a database error occurs
     */
    private static Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setMemberId(rs.getInt("member_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getDouble("amount"));
        
        Timestamp transactionDate = rs.getTimestamp("transaction_date");
        if (transactionDate != null) {
            transaction.setTransactionDate(transactionDate.toLocalDateTime());
        }
        
        transaction.setDescription(rs.getString("description"));
        transaction.setProcessedBy(rs.getString("processed_by"));
        
        return transaction;
    }
}