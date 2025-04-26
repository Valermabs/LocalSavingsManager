package com.moscat.controllers;

import com.moscat.models.InterestSettings;
import com.moscat.models.SavingsAccount;
import com.moscat.models.Transaction;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

/**
 * Controller for savings account management
 */
public class SavingsController {
    
    /**
     * Gets a savings account by ID
     * 
     * @param accountId Account ID
     * @return SavingsAccount or null if not found
     */
    public static SavingsAccount getAccountById(int accountId) {
        String query = "SELECT * FROM savings_accounts WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSavingsAccount(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets a savings account by account number
     * 
     * @param accountNumber Account number
     * @return SavingsAccount or null if not found
     */
    public static SavingsAccount getAccountByNumber(String accountNumber) {
        String query = "SELECT * FROM savings_accounts WHERE account_number = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToSavingsAccount(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Opens a new savings account for a member
     * 
     * @param memberId Member ID
     * @param initialDeposit Initial deposit amount
     * @return true if account opened successfully, false otherwise
     */
    public static boolean openAccount(int memberId, double initialDeposit) {
        if (memberId <= 0 || initialDeposit < 0) {
            return false;
        }
        
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Generate account number
            String accountNumber = generateAccountNumber();
            
            // Create savings account
            String insertAccount = "INSERT INTO savings_accounts (member_id, account_number, " +
                    "balance, interest_earned, status, open_date) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(insertAccount, 
                    PreparedStatement.RETURN_GENERATED_KEYS)) {
                
                stmt.setInt(1, memberId);
                stmt.setString(2, accountNumber);
                stmt.setDouble(3, initialDeposit);
                stmt.setDouble(4, 0.0);
                stmt.setString(5, Constants.ACCOUNT_ACTIVE);
                stmt.setDate(6, DateUtils.getCurrentDate());
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected <= 0) {
                    conn.rollback();
                    return false;
                }
                
                ResultSet generatedKeys = stmt.getGeneratedKeys();
                int accountId;
                
                if (generatedKeys.next()) {
                    accountId = generatedKeys.getInt(1);
                } else {
                    conn.rollback();
                    return false;
                }
                
                // If initial deposit is greater than 0, create initial deposit transaction
                if (initialDeposit > 0) {
                    String insertTransaction = "INSERT INTO transactions (account_id, user_id, " +
                            "transaction_type, amount, running_balance, description, reference_number, " +
                            "transaction_date) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    
                    try (PreparedStatement txnStmt = conn.prepareStatement(insertTransaction)) {
                        
                        txnStmt.setInt(1, accountId);
                        txnStmt.setInt(2, AuthController.getCurrentUser().getId());
                        txnStmt.setString(3, Constants.TRANSACTION_DEPOSIT);
                        txnStmt.setDouble(4, initialDeposit);
                        txnStmt.setDouble(5, initialDeposit);
                        txnStmt.setString(6, "Initial deposit");
                        txnStmt.setString(7, generateReferenceNumber(Constants.TRANSACTION_DEPOSIT));
                        txnStmt.setTimestamp(8, DateUtils.getCurrentTimestamp());
                        
                        int txnRowsAffected = txnStmt.executeUpdate();
                        
                        if (txnRowsAffected <= 0) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
                
                conn.commit();
                return true;
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
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
     * Processes a deposit transaction
     * 
     * @param accountId Account ID
     * @param amount Deposit amount
     * @param description Transaction description
     * @param userId User ID of the user performing the transaction
     * @return true if deposit processed successfully, false otherwise
     */
    public static boolean processDeposit(int accountId, double amount, String description, int userId) {
        if (accountId <= 0 || amount <= 0 || userId <= 0) {
            return false;
        }
        
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Get current account balance
            String getBalance = "SELECT balance FROM savings_accounts WHERE id = ? FOR UPDATE";
            
            double currentBalance = 0;
            
            try (PreparedStatement stmt = conn.prepareStatement(getBalance)) {
                stmt.setInt(1, accountId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    currentBalance = rs.getDouble("balance");
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
            // Calculate new balance
            double newBalance = currentBalance + amount;
            
            // Update account balance
            String updateBalance = "UPDATE savings_accounts SET balance = ?, last_activity_date = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateBalance)) {
                stmt.setDouble(1, newBalance);
                stmt.setDate(2, DateUtils.getCurrentDate());
                stmt.setInt(3, accountId);
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected <= 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Create transaction record
            String insertTransaction = "INSERT INTO transactions (account_id, user_id, transaction_type, " +
                    "amount, running_balance, description, reference_number, transaction_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(insertTransaction)) {
                stmt.setInt(1, accountId);
                stmt.setInt(2, userId);
                stmt.setString(3, Constants.TRANSACTION_DEPOSIT);
                stmt.setDouble(4, amount);
                stmt.setDouble(5, newBalance);
                stmt.setString(6, description);
                stmt.setString(7, generateReferenceNumber(Constants.TRANSACTION_DEPOSIT));
                stmt.setTimestamp(8, DateUtils.getCurrentTimestamp());
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected <= 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
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
     * @param userId User ID of the user performing the transaction
     * @return true if withdrawal processed successfully, false otherwise
     */
    public static boolean processWithdrawal(int accountId, double amount, String description, int userId) {
        if (accountId <= 0 || amount <= 0 || userId <= 0) {
            return false;
        }
        
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Get current account balance
            String getBalance = "SELECT balance FROM savings_accounts WHERE id = ? FOR UPDATE";
            
            double currentBalance = 0;
            
            try (PreparedStatement stmt = conn.prepareStatement(getBalance)) {
                stmt.setInt(1, accountId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    currentBalance = rs.getDouble("balance");
                } else {
                    conn.rollback();
                    return false;
                }
            }
            
            // Check if sufficient balance
            if (currentBalance < amount) {
                conn.rollback();
                return false;
            }
            
            // Calculate new balance
            double newBalance = currentBalance - amount;
            
            // Update account balance
            String updateBalance = "UPDATE savings_accounts SET balance = ?, last_activity_date = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(updateBalance)) {
                stmt.setDouble(1, newBalance);
                stmt.setDate(2, DateUtils.getCurrentDate());
                stmt.setInt(3, accountId);
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected <= 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            // Create transaction record
            String insertTransaction = "INSERT INTO transactions (account_id, user_id, transaction_type, " +
                    "amount, running_balance, description, reference_number, transaction_date) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(insertTransaction)) {
                stmt.setInt(1, accountId);
                stmt.setInt(2, userId);
                stmt.setString(3, Constants.TRANSACTION_WITHDRAWAL);
                stmt.setDouble(4, amount);
                stmt.setDouble(5, newBalance);
                stmt.setString(6, description);
                stmt.setString(7, generateReferenceNumber(Constants.TRANSACTION_WITHDRAWAL));
                stmt.setTimestamp(8, DateUtils.getCurrentTimestamp());
                
                int rowsAffected = stmt.executeUpdate();
                
                if (rowsAffected <= 0) {
                    conn.rollback();
                    return false;
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
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
     * Gets the current interest settings
     * 
     * @return InterestSettings object or null if not found
     */
    public static InterestSettings getCurrentInterestSettings() {
        String query = "SELECT * FROM interest_settings WHERE status = ? ORDER BY effective_date DESC LIMIT 1";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.STATUS_ACTIVE);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToInterestSettings(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets the interest settings history
     * 
     * @return List of interest settings
     */
    public static List<InterestSettings> getInterestSettingsHistory() {
        String query = "SELECT * FROM interest_settings ORDER BY effective_date DESC";
        List<InterestSettings> settingsList = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                settingsList.add(mapResultSetToInterestSettings(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return settingsList;
    }
    
    /**
     * Creates a new interest settings record
     * 
     * @param settings InterestSettings object
     * @return true if created successfully, false otherwise
     */
    public static boolean createInterestSettings(InterestSettings settings) {
        // Deactivate current interest settings first
        deactivateCurrentInterestSettings();
        
        // Insert new interest settings
        String query = "INSERT INTO interest_settings (interest_rate, minimum_balance, calculation_method, " +
                "effective_date, change_basis, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDouble(1, settings.getInterestRate());
            stmt.setDouble(2, settings.getMinimumBalance());
            stmt.setString(3, settings.getCalculationMethod());
            stmt.setDate(4, new java.sql.Date(settings.getEffectiveDate().getTime()));
            stmt.setString(5, settings.getChangeBasis());
            stmt.setString(6, settings.getStatus());
            stmt.setDate(7, DateUtils.getCurrentDate());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deactivates the current interest settings
     * 
     * @return true if deactivated successfully, false otherwise
     */
    private static boolean deactivateCurrentInterestSettings() {
        String query = "UPDATE interest_settings SET status = ? WHERE status = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.STATUS_INACTIVE);
            stmt.setString(2, Constants.STATUS_ACTIVE);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Calculates interest for all accounts
     * 
     * @return true if calculations successful, false otherwise
     */
    public static boolean calculateInterest() {
        InterestSettings settings = getCurrentInterestSettings();
        
        if (settings == null) {
            return false;
        }
        
        Connection conn = null;
        
        try {
            conn = DatabaseManager.getInstance().getConnection();
            conn.setAutoCommit(false);
            
            // Get all active accounts with balance above minimum
            String query = "SELECT * FROM savings_accounts WHERE status = ? AND balance >= ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, Constants.ACCOUNT_ACTIVE);
                stmt.setDouble(2, settings.getMinimumBalance());
                
                ResultSet rs = stmt.executeQuery();
                
                while (rs.next()) {
                    SavingsAccount account = mapResultSetToSavingsAccount(rs);
                    
                    // Calculate interest amount
                    double interestAmount = calculateInterestAmount(account.getBalance(), settings);
                    
                    // Update interest earned
                    double newInterestEarned = account.getInterestEarned() + interestAmount;
                    
                    String updateQuery = "UPDATE savings_accounts SET interest_earned = ? WHERE id = ?";
                    
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setDouble(1, newInterestEarned);
                        updateStmt.setInt(2, account.getId());
                        
                        int rowsAffected = updateStmt.executeUpdate();
                        
                        if (rowsAffected <= 0) {
                            conn.rollback();
                            return false;
                        }
                    }
                    
                    // Record interest transaction
                    String insertTransaction = "INSERT INTO transactions (account_id, user_id, transaction_type, " +
                            "amount, running_balance, description, reference_number, transaction_date) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                    
                    try (PreparedStatement txnStmt = conn.prepareStatement(insertTransaction)) {
                        txnStmt.setInt(1, account.getId());
                        txnStmt.setInt(2, 1); // System user ID (assuming ID 1 is system)
                        txnStmt.setString(3, Constants.TRANSACTION_INTEREST_EARNING);
                        txnStmt.setDouble(4, interestAmount);
                        txnStmt.setDouble(5, account.getBalance() + newInterestEarned);
                        txnStmt.setString(6, "Interest earned");
                        txnStmt.setString(7, generateReferenceNumber(Constants.TRANSACTION_INTEREST_EARNING));
                        txnStmt.setTimestamp(8, DateUtils.getCurrentTimestamp());
                        
                        int rowsAffected = txnStmt.executeUpdate();
                        
                        if (rowsAffected <= 0) {
                            conn.rollback();
                            return false;
                        }
                    }
                }
            }
            
            conn.commit();
            return true;
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
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
     * Calculates interest amount based on balance and settings
     * 
     * @param balance Account balance
     * @param settings Interest settings
     * @return Interest amount
     */
    private static double calculateInterestAmount(double balance, InterestSettings settings) {
        double annualRate = settings.getInterestRate() / 100; // Convert percentage to decimal
        
        if (settings.getCalculationMethod().equals("DAILY")) {
            // Daily interest calculation (annual rate / 365)
            return balance * (annualRate / 365);
        } else {
            // Monthly interest calculation (annual rate / 12)
            return balance * (annualRate / 12);
        }
    }
    
    /**
     * Generates a unique account number
     * 
     * @return Generated account number
     */
    private static String generateAccountNumber() {
        // Format: SA-YYYYMMDD-XXXX (where XXXX is a random 4-digit number)
        String prefix = "SA-";
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String randomPart = String.format("%04d", new Random().nextInt(10000));
        
        return prefix + datePart + "-" + randomPart;
    }
    
    /**
     * Generates a unique reference number for transactions
     * 
     * @param transactionType Transaction type code
     * @return Generated reference number
     */
    public static String generateReferenceNumber(String transactionType) {
        // Format: [TYPE]-YYYYMMDD-HHMMSS-XXX (where XXX is a random 3-digit number)
        String prefix = "";
        
        switch (transactionType) {
            case Constants.TRANSACTION_DEPOSIT:
                prefix = "DEP";
                break;
            case Constants.TRANSACTION_WITHDRAWAL:
                prefix = "WDW";
                break;
            case Constants.TRANSACTION_INTEREST_EARNING:
                prefix = "INT";
                break;
            case Constants.TRANSACTION_LOAN_RELEASE:
                prefix = "LNR";
                break;
            case Constants.TRANSACTION_LOAN_PAYMENT:
                prefix = "LNP";
                break;
            case Constants.TRANSACTION_FEE:
                prefix = "FEE";
                break;
            default:
                prefix = "TXN";
        }
        
        String datePart = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String timePart = new SimpleDateFormat("HHmmss").format(new Date());
        String randomPart = String.format("%03d", new Random().nextInt(1000));
        
        return prefix + "-" + datePart + "-" + timePart + "-" + randomPart;
    }
    
    /**
     * Maps a ResultSet row to a SavingsAccount object
     * 
     * @param rs ResultSet
     * @return SavingsAccount object
     * @throws SQLException If database error occurs
     */
    private static SavingsAccount mapResultSetToSavingsAccount(ResultSet rs) throws SQLException {
        SavingsAccount account = new SavingsAccount();
        account.setId(rs.getInt("id"));
        account.setMemberId(rs.getInt("member_id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setBalance(rs.getDouble("balance"));
        account.setInterestEarned(rs.getDouble("interest_earned"));
        account.setStatus(rs.getString("status"));
        account.setOpenDate(rs.getDate("open_date"));
        account.setLastActivityDate(rs.getDate("last_activity_date"));
        return account;
    }
    
    /**
     * Maps a ResultSet row to an InterestSettings object
     * 
     * @param rs ResultSet
     * @return InterestSettings object
     * @throws SQLException If database error occurs
     */
    private static InterestSettings mapResultSetToInterestSettings(ResultSet rs) throws SQLException {
        InterestSettings settings = new InterestSettings();
        settings.setId(rs.getInt("id"));
        settings.setInterestRate(rs.getDouble("interest_rate"));
        settings.setMinimumBalance(rs.getDouble("minimum_balance"));
        settings.setCalculationMethod(rs.getString("calculation_method"));
        settings.setEffectiveDate(rs.getDate("effective_date"));
        settings.setChangeBasis(rs.getString("change_basis"));
        settings.setStatus(rs.getString("status"));
        settings.setCreatedAt(rs.getDate("created_at"));
        return settings;
    }
}