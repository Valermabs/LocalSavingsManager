package com.moscat.controllers;

import com.moscat.models.Transaction;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.DateUtils;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for transaction management
 */
public class TransactionController {
    
    /**
     * Gets recent transactions
     * 
     * @param limit Maximum number of transactions to return
     * @return List of recent transactions
     */
    public static List<Transaction> getRecentTransactions(int limit) {
        String query = "SELECT * FROM transactions ORDER BY transaction_date DESC LIMIT ?";
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, limit);
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
     * Gets transactions for a specific account
     * 
     * @param accountId Account ID
     * @param limit Maximum number of transactions to return (0 for all)
     * @return List of account transactions
     */
    public static List<Transaction> getAccountTransactions(int accountId, int limit) {
        String query = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC";
        if (limit > 0) {
            query += " LIMIT ?";
        }
        
        List<Transaction> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, accountId);
            
            if (limit > 0) {
                stmt.setInt(2, limit);
            }
            
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
     * Gets transactions by date range
     * 
     * @param startDate Start date (yyyy-MM-dd)
     * @param endDate End date (yyyy-MM-dd)
     * @return List of transactions in date range
     */
    public static List<Transaction> getTransactionsByDateRange(String startDate, String endDate) {
        String query = "SELECT * FROM transactions WHERE DATE(transaction_date) BETWEEN ? AND ? ORDER BY transaction_date DESC";
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
     * Gets transactions by reference number
     * 
     * @param referenceNumber Transaction reference number
     * @return Transaction or null if not found
     */
    public static Transaction getTransactionByReference(String referenceNumber) {
        String query = "SELECT * FROM transactions WHERE reference_number = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, referenceNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToTransaction(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Searches for transactions matching the search term
     * 
     * @param searchTerm Search term (reference number, description, etc.)
     * @return List of matching transactions
     */
    public static List<Transaction> searchTransactions(String searchTerm) {
        String query = "SELECT t.* FROM transactions t " +
                "LEFT JOIN savings_accounts a ON t.account_id = a.id " +
                "LEFT JOIN members m ON a.member_id = m.id " +
                "WHERE t.reference_number LIKE ? OR t.description LIKE ? " +
                "OR a.account_number LIKE ? OR m.first_name LIKE ? OR m.last_name LIKE ? " +
                "ORDER BY t.transaction_date DESC";
        
        List<Transaction> transactions = new ArrayList<>();
        String searchPattern = "%" + searchTerm + "%";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            stmt.setString(5, searchPattern);
            
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
     * Gets summary of transactions for a specific date
     * 
     * @param date Date string (yyyy-MM-dd)
     * @return List of transaction summaries
     */
    public static List<TransactionSummary> getDailyTransactionSummary(String date) {
        String query = "SELECT transaction_type, SUM(amount) as total_amount " +
                "FROM transactions WHERE DATE(transaction_date) = ? " +
                "GROUP BY transaction_type ORDER BY transaction_type";
        
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
     * Exports a table to CSV file
     * 
     * @param table JTable to export
     * @param filePath Path to save CSV file
     * @return true if export successful, false otherwise
     */
    public static boolean exportTransactionsToCSV(JTable table, String filePath) {
        try {
            TableModel model = table.getModel();
            FileWriter csv = new FileWriter(filePath);
            PrintWriter out = new PrintWriter(csv);
            
            // Write headers
            for (int i = 0; i < model.getColumnCount(); i++) {
                out.write(model.getColumnName(i));
                if (i < model.getColumnCount() - 1) {
                    out.write(",");
                }
            }
            out.write("\n");
            
            // Write data
            for (int i = 0; i < model.getRowCount(); i++) {
                for (int j = 0; j < model.getColumnCount(); j++) {
                    String value = model.getValueAt(i, j) != null ? model.getValueAt(i, j).toString() : "";
                    // Escape commas in the value
                    if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
                        value = "\"" + value.replace("\"", "\"\"") + "\"";
                    }
                    out.write(value);
                    if (j < model.getColumnCount() - 1) {
                        out.write(",");
                    }
                }
                out.write("\n");
            }
            
            out.close();
            return true;
            
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Maps a ResultSet row to a Transaction object
     * 
     * @param rs ResultSet
     * @return Transaction object
     * @throws SQLException If database error occurs
     */
    private static Transaction mapResultSetToTransaction(ResultSet rs) throws SQLException {
        Transaction transaction = new Transaction();
        transaction.setId(rs.getInt("id"));
        transaction.setAccountId(rs.getInt("account_id"));
        transaction.setUserId(rs.getInt("user_id"));
        transaction.setTransactionType(rs.getString("transaction_type"));
        transaction.setAmount(rs.getDouble("amount"));
        transaction.setRunningBalance(rs.getDouble("running_balance"));
        transaction.setDescription(rs.getString("description"));
        transaction.setReferenceNumber(rs.getString("reference_number"));
        transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
        return transaction;
    }
    
    /**
     * Transaction summary class
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
        
        public String getTransactionTypeDisplay() {
            switch (transactionType) {
                case Constants.TRANSACTION_DEPOSIT:
                    return "Deposit";
                case Constants.TRANSACTION_WITHDRAWAL:
                    return "Withdrawal";
                case Constants.TRANSACTION_INTEREST_EARNING:
                    return "Interest Earning";
                case Constants.TRANSACTION_LOAN_RELEASE:
                    return "Loan Release";
                case Constants.TRANSACTION_LOAN_PAYMENT:
                    return "Loan Payment";
                case Constants.TRANSACTION_FEE:
                    return "Fee";
                default:
                    return transactionType;
            }
        }
    }
}