package com.moscat.controllers;

import com.moscat.models.InterestSettings;
import com.moscat.models.SavingsAccount;
import com.moscat.models.Transaction;
import com.moscat.utils.Constants;
import com.moscat.utils.DateUtils;
import com.moscat.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Controller for savings account operations and interest calculations
 */
public class SavingsController {
    
    /**
     * Gets a savings account by ID
     * 
     * @param accountId Account ID
     * @return SavingsAccount object, or null if not found
     */
    public static SavingsAccount getAccountById(int accountId) {
        String query = "SELECT * FROM savings_accounts WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets a savings account by account number
     * 
     * @param accountNumber Account number
     * @return SavingsAccount object, or null if not found
     */
    public static SavingsAccount getAccountByNumber(String accountNumber) {
        String query = "SELECT * FROM savings_accounts WHERE account_number = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToAccount(rs);
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Gets the current interest settings
     * 
     * @return InterestSettings object, or null if not found
     */
    public static InterestSettings getCurrentInterestSettings() {
        String query = "SELECT * FROM interest_settings WHERE effective_date <= CURRENT_DATE " +
                "ORDER BY effective_date DESC, id DESC LIMIT 1";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                InterestSettings settings = new InterestSettings();
                settings.setId(rs.getInt("id"));
                settings.setInterestRate(rs.getDouble("interest_rate"));
                settings.setMinimumBalanceForInterest(rs.getDouble("minimum_balance_for_interest"));
                settings.setComputationMethod(rs.getString("computation_method"));
                settings.setEffectiveDate(rs.getDate("effective_date"));
                settings.setChangeReason(rs.getString("change_reason"));
                settings.setCreatedBy(rs.getInt("created_by"));
                
                return settings;
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Creates new interest settings
     * 
     * @param settings InterestSettings object
     * @return true if settings created successfully, false otherwise
     */
    public static boolean createInterestSettings(InterestSettings settings) {
        String query = "INSERT INTO interest_settings (interest_rate, minimum_balance_for_interest, " +
                "computation_method, effective_date, change_reason, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDouble(1, settings.getInterestRate());
            stmt.setDouble(2, settings.getMinimumBalanceForInterest());
            stmt.setString(3, settings.getComputationMethod());
            stmt.setDate(4, settings.getEffectiveDate());
            stmt.setString(5, settings.getChangeReason());
            stmt.setInt(6, settings.getCreatedBy());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets the interest settings history
     * 
     * @return List of interest settings
     */
    public static List<InterestSettings> getInterestSettingsHistory() {
        String query = "SELECT * FROM interest_settings ORDER BY effective_date DESC, id DESC";
        List<InterestSettings> settingsList = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                InterestSettings settings = new InterestSettings();
                settings.setId(rs.getInt("id"));
                settings.setInterestRate(rs.getDouble("interest_rate"));
                settings.setMinimumBalanceForInterest(rs.getDouble("minimum_balance_for_interest"));
                settings.setComputationMethod(rs.getString("computation_method"));
                settings.setEffectiveDate(rs.getDate("effective_date"));
                settings.setChangeReason(rs.getString("change_reason"));
                settings.setCreatedBy(rs.getInt("created_by"));
                
                settingsList.add(settings);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return settingsList;
    }
    
    /**
     * Processes a deposit transaction
     * 
     * @param accountId Account ID
     * @param amount Deposit amount
     * @param description Transaction description
     * @param userId User ID who processed the transaction
     * @return true if deposit successful, false otherwise
     */
    public static boolean processDeposit(int accountId, double amount, String description, int userId) {
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Get current account balance
            SavingsAccount account = getAccountById(accountId);
            if (account == null) {
                return false;
            }
            
            // Calculate new balance
            double newBalance = account.getBalance() + amount;
            
            // Update account balance
            String updateQuery = "UPDATE savings_accounts SET balance = ?, last_activity_date = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setDouble(1, newBalance);
                stmt.setDate(2, DateUtils.getCurrentDate());
                stmt.setInt(3, accountId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Create transaction record
            String transactionQuery = "INSERT INTO transactions (account_id, transaction_type, amount, " +
                    "running_balance, reference_number, description, user_id, transaction_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(transactionQuery)) {
                stmt.setInt(1, accountId);
                stmt.setString(2, Constants.TRANSACTION_DEPOSIT);
                stmt.setDouble(3, amount);
                stmt.setDouble(4, newBalance);
                stmt.setString(5, generateReferenceNumber(Constants.TRANSACTION_DEPOSIT));
                stmt.setString(6, description);
                stmt.setInt(7, userId);
                stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Update member's last activity date
            MemberController.updateLastActivityDate(account.getMemberId());
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Processes a withdrawal transaction
     * 
     * @param accountId Account ID
     * @param amount Withdrawal amount
     * @param description Transaction description
     * @param userId User ID who processed the transaction
     * @return true if withdrawal successful, false otherwise
     */
    public static boolean processWithdrawal(int accountId, double amount, String description, int userId) {
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Get current account balance
            SavingsAccount account = getAccountById(accountId);
            if (account == null) {
                return false;
            }
            
            // Check if sufficient balance
            if (account.getBalance() < amount) {
                return false;
            }
            
            // Calculate new balance
            double newBalance = account.getBalance() - amount;
            
            // Update account balance
            String updateQuery = "UPDATE savings_accounts SET balance = ?, last_activity_date = ? WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                stmt.setDouble(1, newBalance);
                stmt.setDate(2, DateUtils.getCurrentDate());
                stmt.setInt(3, accountId);
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Create transaction record
            String transactionQuery = "INSERT INTO transactions (account_id, transaction_type, amount, " +
                    "running_balance, reference_number, description, user_id, transaction_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(transactionQuery)) {
                stmt.setInt(1, accountId);
                stmt.setString(2, Constants.TRANSACTION_WITHDRAWAL);
                stmt.setDouble(3, amount);
                stmt.setDouble(4, newBalance);
                stmt.setString(5, generateReferenceNumber(Constants.TRANSACTION_WITHDRAWAL));
                stmt.setString(6, description);
                stmt.setInt(7, userId);
                stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                
                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected == 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Update member's last activity date
            MemberController.updateLastActivityDate(account.getMemberId());
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Computes interest for all eligible savings accounts
     * 
     * @param userId User ID who initiated the computation
     * @return Number of accounts for which interest was computed
     */
    public static int computeInterestForAllAccounts(int userId) {
        int accountsProcessed = 0;
        
        // Get current interest settings
        InterestSettings settings = getCurrentInterestSettings();
        if (settings == null) {
            return 0;
        }
        
        // Get all active savings accounts
        String query = "SELECT * FROM savings_accounts WHERE status = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.ACCOUNT_ACTIVE);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                SavingsAccount account = mapResultSetToAccount(rs);
                
                // Check if account meets minimum balance requirement
                if (account.getBalance() >= settings.getMinimumBalanceForInterest()) {
                    boolean success = computeInterestForAccount(account, settings, userId);
                    if (success) {
                        accountsProcessed++;
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return accountsProcessed;
    }
    
    /**
     * Computes interest for a specific savings account
     * 
     * @param account SavingsAccount object
     * @param settings InterestSettings object
     * @param userId User ID who initiated the computation
     * @return true if interest computed successfully, false otherwise
     */
    public static boolean computeInterestForAccount(SavingsAccount account, InterestSettings settings, int userId) {
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            double interestAmount = 0;
            java.sql.Date lastComputationDate = account.getLastInterestComputationDate();
            java.sql.Date currentDate = DateUtils.getCurrentDate();
            
            // If last computation date is null, use account open date
            if (lastComputationDate == null) {
                lastComputationDate = account.getOpenDate();
            }
            
            // Calculate interest based on computation method
            if (settings.isDailyComputation()) {
                // Daily computation
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(lastComputationDate);
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(currentDate);
                
                // Calculate days between
                long daysBetween = (endCal.getTimeInMillis() - startCal.getTimeInMillis()) / (24 * 60 * 60 * 1000);
                
                for (int i = 0; i < daysBetween; i++) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(lastComputationDate);
                    cal.add(Calendar.DAY_OF_YEAR, i);
                    
                    int year = cal.get(Calendar.YEAR);
                    double dailyRate = settings.getDailyInterestRate(year);
                    interestAmount += account.getBalance() * dailyRate / 100;
                }
            } else {
                // Monthly computation
                Calendar startCal = Calendar.getInstance();
                startCal.setTime(lastComputationDate);
                Calendar endCal = Calendar.getInstance();
                endCal.setTime(currentDate);
                
                // Calculate months between
                int monthsBetween = (endCal.get(Calendar.YEAR) - startCal.get(Calendar.YEAR)) * 12 + 
                                    (endCal.get(Calendar.MONTH) - startCal.get(Calendar.MONTH));
                
                // Adjust if we haven't reached the same day of month
                if (endCal.get(Calendar.DAY_OF_MONTH) < startCal.get(Calendar.DAY_OF_MONTH)) {
                    monthsBetween--;
                }
                
                double monthlyRate = settings.getMonthlyInterestRate();
                interestAmount = account.getBalance() * monthlyRate / 100 * monthsBetween;
            }
            
            // Round to 2 decimal places
            interestAmount = Math.round(interestAmount * 100.0) / 100.0;
            
            if (interestAmount > 0) {
                // Update account with new interest earned
                double newTotalInterest = account.getInterestEarned() + interestAmount;
                
                String updateQuery = "UPDATE savings_accounts SET interest_earned = ?, " +
                        "last_interest_computation_date = ? WHERE id = ?";
                
                try (PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
                    stmt.setDouble(1, newTotalInterest);
                    stmt.setDate(2, currentDate);
                    stmt.setInt(3, account.getId());
                    
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected == 0) {
                        conn.rollback();
                        return false;
                    }
                }
                
                // Create transaction record for interest earning
                String transactionQuery = "INSERT INTO transactions (account_id, transaction_type, amount, " +
                        "running_balance, reference_number, description, user_id, transaction_date) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement stmt = conn.prepareStatement(transactionQuery)) {
                    stmt.setInt(1, account.getId());
                    stmt.setString(2, Constants.TRANSACTION_INTEREST_EARNING);
                    stmt.setDouble(3, interestAmount);
                    stmt.setDouble(4, account.getBalance()); // Balance doesn't change, only interest earned
                    stmt.setString(5, generateReferenceNumber(Constants.TRANSACTION_INTEREST_EARNING));
                    stmt.setString(6, "Interest earning computation");
                    stmt.setInt(7, userId);
                    stmt.setTimestamp(8, new Timestamp(System.currentTimeMillis()));
                    
                    int rowsAffected = stmt.executeUpdate();
                    if (rowsAffected == 0) {
                        conn.rollback();
                        return false;
                    }
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return false;
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Gets transactions for a specific account
     * 
     * @param accountId Account ID
     * @param limit Maximum number of transactions to return (0 for all)
     * @return List of transactions
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
                
                transactions.add(transaction);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Generates a reference number for transactions
     * 
     * @param transactionType Transaction type
     * @return Generated reference number
     */
    private static String generateReferenceNumber(String transactionType) {
        String prefix;
        
        switch (transactionType) {
            case Constants.TRANSACTION_DEPOSIT:
                prefix = "DEP";
                break;
            case Constants.TRANSACTION_WITHDRAWAL:
                prefix = "WDW";
                break;
            case Constants.TRANSACTION_LOAN_PAYMENT:
                prefix = "LPY";
                break;
            case Constants.TRANSACTION_LOAN_RELEASE:
                prefix = "LRL";
                break;
            case Constants.TRANSACTION_INTEREST_EARNING:
                prefix = "INT";
                break;
            default:
                prefix = "TRX";
        }
        
        // Generate format: PREFIX-YYYYMMDD-RANDOM
        java.text.SimpleDateFormat dateFormat = new java.text.SimpleDateFormat("yyyyMMdd");
        String datePart = dateFormat.format(new Date());
        String randomPart = String.format("%06d", (int) (Math.random() * 1000000));
        
        return prefix + "-" + datePart + "-" + randomPart;
    }
    
    /**
     * Maps a ResultSet row to a SavingsAccount object
     * 
     * @param rs ResultSet
     * @return SavingsAccount object
     * @throws SQLException If database operation fails
     */
    private static SavingsAccount mapResultSetToAccount(ResultSet rs) throws SQLException {
        SavingsAccount account = new SavingsAccount();
        account.setId(rs.getInt("id"));
        account.setMemberId(rs.getInt("member_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setBalance(rs.getDouble("balance"));
        account.setInterestEarned(rs.getDouble("interest_earned"));
        account.setLastInterestComputationDate(rs.getDate("last_interest_computation_date"));
        account.setStatus(rs.getString("status"));
        account.setOpenDate(rs.getDate("open_date"));
        account.setLastActivityDate(rs.getDate("last_activity_date"));
        
        return account;
    }
}
