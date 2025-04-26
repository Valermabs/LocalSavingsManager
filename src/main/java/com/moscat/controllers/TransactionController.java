package com.moscat.controllers;

import com.moscat.models.Transaction;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for transaction operations
 */
public class TransactionController {
    
    /**
     * Gets a transaction by ID
     * 
     * @param transactionId Transaction ID
     * @return Transaction object, or null if not found
     */
    public static Transaction getTransactionById(int transactionId) {
        String query = "SELECT * FROM transactions WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets all transactions for a specific date range
     * 
     * @param startDate Start date (yyyy-MM-dd)
     * @param endDate End date (yyyy-MM-dd)
     * @return List of transactions
     */
    public static List<Transaction> getTransactionsByDateRange(String startDate, String endDate) {
        String query = "SELECT * FROM transactions WHERE " +
                "DATE(transaction_date) BETWEEN ? AND ? " +
                "ORDER BY transaction_date DESC";
        
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, startDate);
            stmt.setString(2, endDate);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Gets all transactions for a specific account
     * 
     * @param accountId Account ID
     * @return List of transactions
     */
    public static List<Transaction> getTransactionsByAccount(int accountId) {
        String query = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC";
        
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Gets all transactions for a specific member
     * 
     * @param memberId Member ID
     * @return List of transactions
     */
    public static List<Transaction> getTransactionsByMember(int memberId) {
        String query = "SELECT t.* FROM transactions t " +
                "JOIN savings_accounts s ON t.account_id = s.id " +
                "WHERE s.member_id = ? " +
                "ORDER BY t.transaction_date DESC";
        
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Gets all transactions for a specific transaction type
     * 
     * @param transactionType Transaction type
     * @return List of transactions
     */
    public static List<Transaction> getTransactionsByType(String transactionType) {
        String query = "SELECT * FROM transactions WHERE transaction_type = ? ORDER BY transaction_date DESC";
        
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, transactionType);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Gets all transactions processed by a specific user
     * 
     * @param userId User ID
     * @return List of transactions
     */
    public static List<Transaction> getTransactionsByUser(int userId) {
        String query = "SELECT * FROM transactions WHERE user_id = ? ORDER BY transaction_date DESC";
        
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(mapResultSetToTransaction(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Gets daily transaction summary
     * 
     * @param date Date (yyyy-MM-dd)
     * @return List of transaction summary objects with type and total amount
     */
    public static List<TransactionSummary> getDailyTransactionSummary(String date) {
        String query = "SELECT transaction_type, SUM(amount) as total_amount " +
                "FROM transactions WHERE DATE(transaction_date) = ? " +
                "GROUP BY transaction_type";
        
        List<TransactionSummary> summaries = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, date);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                TransactionSummary summary = new TransactionSummary();
                summary.setTransactionType(rs.getString("transaction_type"));
                summary.setTotalAmount(rs.getDouble("total_amount"));
                
                summaries.add(summary);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return summaries;
    }
    
    /**
     * Gets monthly transaction summary
     * 
     * @param year Year
     * @param month Month (1-12)
     * @return List of transaction summary objects with type and total amount
     */
    public static List<TransactionSummary> getMonthlyTransactionSummary(int year, int month) {
        String query = "SELECT transaction_type, SUM(amount) as total_amount " +
                "FROM transactions WHERE YEAR(transaction_date) = ? AND MONTH(transaction_date) = ? " +
                "GROUP BY transaction_type";
        
        List<TransactionSummary> summaries = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, year);
            stmt.setInt(2, month);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                TransactionSummary summary = new TransactionSummary();
                summary.setTransactionType(rs.getString("transaction_type"));
                summary.setTotalAmount(rs.getDouble("total_amount"));
                
                summaries.add(summary);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return summaries;
    }
    
    /**
     * Gets yearly transaction summary
     * 
     * @param year Year
     * @return List of transaction summary objects with type and total amount
     */
    public static List<TransactionSummary> getYearlyTransactionSummary(int year) {
        String query = "SELECT transaction_type, SUM(amount) as total_amount " +
                "FROM transactions WHERE YEAR(transaction_date) = ? " +
                "GROUP BY transaction_type";
        
        List<TransactionSummary> summaries = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, year);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                TransactionSummary summary = new TransactionSummary();
                summary.setTransactionType(rs.getString("transaction_type"));
                summary.setTotalAmount(rs.getDouble("total_amount"));
                
                summaries.add(summary);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return summaries;
    }
    
    /**
     * Gets the user who processed a transaction
     * 
     * @param transactionId Transaction ID
     * @return User ID who processed the transaction
     */
    public static int getTransactionProcessor(int transactionId) {
        String query = "SELECT user_id FROM transactions WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, transactionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("user_id");
            }
            
            return -1;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }
    
    /**
     * Gets the savings account balance for a specific date
     * 
     * @param accountId Account ID
     * @param date Date (yyyy-MM-dd)
     * @return Account balance as of the specified date
     */
    public static double getAccountBalanceAsOfDate(int accountId, String date) {
        String query = "SELECT running_balance FROM transactions " +
                "WHERE account_id = ? AND DATE(transaction_date) <= ? " +
                "ORDER BY transaction_date DESC LIMIT 1";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, accountId);
            stmt.setString(2, date);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble("running_balance");
            }
            
            return 0; // No transactions found
            
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
    
    /**
     * Maps a ResultSet row to a Transaction object
     * 
     * @param rs ResultSet
     * @return Transaction object
     * @throws SQLException If database operation fails
     */
    private static Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setAccountId(rs.getInt("account_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setRunningBalance(rs.getDouble("running_balance"));
        transaction.setReferenceNumber(rs.getString("reference_number"));
        transaction.setDescription(rs.getString("description"));
        transaction.setUserId(rs.getInt("user_id"));
        transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
        
        return transaction;
    }
    
    /**
     * Inner class for transaction summary
     */
    public static class TransactionSummary {
        private String transactionType;
        private double totalAmount;
        
        public String getTransactionType() {
            return transactionType;
        }
        
        public void setTransactionType(String transactionType) {
            this.transactionType = transactionType;
        }
        
        public double getTotalAmount() {
            return totalAmount;
        }
        
        public void setTotalAmount(double totalAmount) {
            this.totalAmount = totalAmount;
        }
        
        /**
         * Gets the transaction type display name
         * 
         * @return Display name for the transaction type
         */
        public String getTransactionTypeDisplay() {
            switch (transactionType) {
                case Constants.TRANSACTION_DEPOSIT:
                    return "Deposit";
                case Constants.TRANSACTION_WITHDRAWAL:
                    return "Withdrawal";
                case Constants.TRANSACTION_LOAN_PAYMENT:
                    return "Loan Payment";
                case Constants.TRANSACTION_LOAN_RELEASE:
                    return "Loan Release";
                case Constants.TRANSACTION_INTEREST_EARNING:
                    return "Interest Earning";
                default:
                    return transactionType;
            }
        }
    }
}
