package com.moscat.controllers;

import com.moscat.models.Member;
import com.moscat.models.SavingsAccount;
import com.moscat.models.Transaction;
import com.moscat.models.InterestSettings;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.DateUtils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller for savings account operations
 */
public class SavingsController {
    
    /**
     * Creates a new savings account
     * 
     * @param account Savings account to create
     * @param initialDeposit Initial deposit amount
     * @param performedById User ID who performed the operation
     * @return true if creation successful, false otherwise
     */
    public static boolean createSavingsAccount(SavingsAccount account, double initialDeposit, int performedById) {
        // Validate input
        if (account == null || account.getMemberId() <= 0 || account.getAccountType() == null || 
                account.getInterestRate() < 0 || initialDeposit < 0) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement accountStmt = null;
        PreparedStatement transactionStmt = null;
        
        try {
            // Get connection
            conn = DatabaseManager.getInstance().getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // Generate account number
            String accountNumber = generateAccountNumber(account.getMemberId());
            account.setAccountNumber(accountNumber);
            
            // Set other fields
            account.setBalance(initialDeposit);
            account.setStatus(Constants.SAVINGS_STATUS_ACTIVE);
            
            // Insert savings account
            String accountQuery = "INSERT INTO savings_accounts (account_number, member_id, account_type, " +
                    "balance, interest_rate, status, opened_date) VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            accountStmt = conn.prepareStatement(accountQuery, Statement.RETURN_GENERATED_KEYS);
            accountStmt.setString(1, account.getAccountNumber());
            accountStmt.setInt(2, account.getMemberId());
            accountStmt.setString(3, account.getAccountType());
            accountStmt.setDouble(4, account.getBalance());
            accountStmt.setDouble(5, account.getInterestRate());
            accountStmt.setString(6, account.getStatus());
            accountStmt.setDate(7, DateUtils.toSqlDate(account.getOpenedDate()));
            
            int rowsAffected = accountStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated account ID
                ResultSet generatedKeys = accountStmt.getGeneratedKeys();
                
                if (generatedKeys.next()) {
                    int accountId = generatedKeys.getInt(1);
                    account.setId(accountId);
                    
                    // Add initial deposit transaction if amount > 0
                    if (initialDeposit > 0) {
                        String transactionId = generateTransactionId();
                        
                        String transactionQuery = "INSERT INTO transactions (transaction_id, account_id, " +
                                "transaction_type, amount, transaction_date, description, balance_after, performed_by) " +
                                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                        
                        transactionStmt = conn.prepareStatement(transactionQuery);
                        transactionStmt.setString(1, transactionId);
                        transactionStmt.setInt(2, accountId);
                        transactionStmt.setString(3, Constants.TRANSACTION_DEPOSIT);
                        transactionStmt.setDouble(4, initialDeposit);
                        transactionStmt.setTimestamp(5, DateUtils.getCurrentTimestamp());
                        transactionStmt.setString(6, "Initial deposit");
                        transactionStmt.setDouble(7, initialDeposit);
                        transactionStmt.setInt(8, performedById);
                        
                        transactionStmt.executeUpdate();
                    }
                    
                    // Commit transaction
                    conn.commit();
                    return true;
                }
            }
            
            // Rollback on failure
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Rollback on exception
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            return false;
        } finally {
            // Close resources
            if (transactionStmt != null) {
                try {
                    transactionStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (accountStmt != null) {
                try {
                    accountStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        
        // This is never reached due to returns in try-catch blocks
        // but we need it for method signature compliance
        // return false;
    }
    
    /**
     * Gets a savings account by ID
     * 
     * @param accountId Account ID
     * @return SavingsAccount or null if not found
     */
    public static SavingsAccount getSavingsAccountById(int accountId) {
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
    public static SavingsAccount getSavingsAccountByNumber(String accountNumber) {
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
     * Gets all savings accounts for a member
     * 
     * @param memberId Member ID
     * @return List of savings accounts
     */
    public static List<SavingsAccount> getSavingsAccountsByMemberId(int memberId) {
        List<SavingsAccount> accounts = new ArrayList<>();
        String query = "SELECT * FROM savings_accounts WHERE member_id = ? ORDER BY id";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                SavingsAccount account = mapResultSetToSavingsAccount(rs);
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return accounts;
    }
    
    /**
     * Gets all active savings accounts
     * 
     * @return List of active savings accounts
     */
    public static List<SavingsAccount> getAllActiveSavingsAccounts() {
        List<SavingsAccount> accounts = new ArrayList<>();
        String query = "SELECT * FROM savings_accounts WHERE status = ? ORDER BY id";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.SAVINGS_STATUS_ACTIVE);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                SavingsAccount account = mapResultSetToSavingsAccount(rs);
                accounts.add(account);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return accounts;
    }
    
    /**
     * Makes a deposit to a savings account
     * 
     * @param accountId Account ID
     * @param amount Deposit amount
     * @param description Transaction description
     * @param performedById User ID who performed the operation
     * @return true if deposit successful, false otherwise
     */
    public static boolean deposit(int accountId, double amount, String description, int performedById) {
        // Validate input
        if (accountId <= 0 || amount <= 0) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement updateStmt = null;
        PreparedStatement transactionStmt = null;
        
        try {
            // Get connection
            conn = DatabaseManager.getInstance().getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // Get current account balance
            SavingsAccount account = getSavingsAccountById(accountId);
            
            if (account == null) {
                return false;
            }
            
            // Calculate new balance
            double newBalance = account.getBalance() + amount;
            
            // Update account balance
            String updateQuery = "UPDATE savings_accounts SET balance = ?, last_transaction_date = ? WHERE id = ?";
            
            updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setDouble(1, newBalance);
            updateStmt.setTimestamp(2, DateUtils.getCurrentTimestamp());
            updateStmt.setInt(3, accountId);
            
            int rowsAffected = updateStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Add transaction record
                String transactionId = generateTransactionId();
                
                String transactionQuery = "INSERT INTO transactions (transaction_id, account_id, " +
                        "transaction_type, amount, transaction_date, description, balance_after, performed_by) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                transactionStmt = conn.prepareStatement(transactionQuery);
                transactionStmt.setString(1, transactionId);
                transactionStmt.setInt(2, accountId);
                transactionStmt.setString(3, Constants.TRANSACTION_DEPOSIT);
                transactionStmt.setDouble(4, amount);
                transactionStmt.setTimestamp(5, DateUtils.getCurrentTimestamp());
                transactionStmt.setString(6, description);
                transactionStmt.setDouble(7, newBalance);
                transactionStmt.setInt(8, performedById);
                
                transactionStmt.executeUpdate();
                
                // Commit transaction
                conn.commit();
                return true;
            }
            
            // Rollback on failure
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Rollback on exception
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            return false;
        } finally {
            // Close resources
            if (transactionStmt != null) {
                try {
                    transactionStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (updateStmt != null) {
                try {
                    updateStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Makes a withdrawal from a savings account
     * 
     * @param accountId Account ID
     * @param amount Withdrawal amount
     * @param description Transaction description
     * @param performedById User ID who performed the operation
     * @return true if withdrawal successful, false otherwise
     */
    public static boolean withdraw(int accountId, double amount, String description, int performedById) {
        // Validate input
        if (accountId <= 0 || amount <= 0) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement updateStmt = null;
        PreparedStatement transactionStmt = null;
        
        try {
            // Get connection
            conn = DatabaseManager.getInstance().getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // Get current account balance
            SavingsAccount account = getSavingsAccountById(accountId);
            
            if (account == null) {
                return false;
            }
            
            // Check if balance is sufficient
            if (account.getBalance() < amount) {
                return false;
            }
            
            // Calculate new balance
            double newBalance = account.getBalance() - amount;
            
            // Update account balance
            String updateQuery = "UPDATE savings_accounts SET balance = ?, last_transaction_date = ? WHERE id = ?";
            
            updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setDouble(1, newBalance);
            updateStmt.setTimestamp(2, DateUtils.getCurrentTimestamp());
            updateStmt.setInt(3, accountId);
            
            int rowsAffected = updateStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Add transaction record
                String transactionId = generateTransactionId();
                
                String transactionQuery = "INSERT INTO transactions (transaction_id, account_id, " +
                        "transaction_type, amount, transaction_date, description, balance_after, performed_by) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                transactionStmt = conn.prepareStatement(transactionQuery);
                transactionStmt.setString(1, transactionId);
                transactionStmt.setInt(2, accountId);
                transactionStmt.setString(3, Constants.TRANSACTION_WITHDRAWAL);
                transactionStmt.setDouble(4, amount);
                transactionStmt.setTimestamp(5, DateUtils.getCurrentTimestamp());
                transactionStmt.setString(6, description);
                transactionStmt.setDouble(7, newBalance);
                transactionStmt.setInt(8, performedById);
                
                transactionStmt.executeUpdate();
                
                // Commit transaction
                conn.commit();
                return true;
            }
            
            // Rollback on failure
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Rollback on exception
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            return false;
        } finally {
            // Close resources
            if (transactionStmt != null) {
                try {
                    transactionStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (updateStmt != null) {
                try {
                    updateStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Updates a savings account
     * 
     * @param account Savings account to update
     * @return true if update successful, false otherwise
     */
    public static boolean updateSavingsAccount(SavingsAccount account) {
        // Validate input
        if (account == null || account.getId() <= 0) {
            return false;
        }
        
        String query = "UPDATE savings_accounts SET account_type = ?, interest_rate = ?, status = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, account.getAccountType());
            stmt.setDouble(2, account.getInterestRate());
            stmt.setString(3, account.getStatus());
            stmt.setInt(4, account.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets transactions for a savings account
     * 
     * @param accountId Account ID
     * @param limit Maximum number of transactions to return (0 for all)
     * @return List of transactions
     */
    public static List<Transaction> getTransactionsByAccountId(int accountId, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        
        String query = "SELECT t.*, u.username AS performed_by_username " +
                "FROM transactions t " +
                "LEFT JOIN users u ON t.performed_by = u.id " +
                "WHERE t.account_id = ? " +
                "ORDER BY t.transaction_date DESC";
        
        if (limit > 0) {
            query += " LIMIT ?";
        }
        
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
                transaction.setTransactionId(rs.getString("transaction_id"));
                transaction.setAccountId(rs.getInt("account_id"));
                transaction.setTransactionType(rs.getString("transaction_type"));
                transaction.setAmount(rs.getDouble("amount"));
                transaction.setTransactionDate(rs.getTimestamp("transaction_date"));
                transaction.setDescription(rs.getString("description"));
                transaction.setBalanceAfter(rs.getDouble("balance_after"));
                transaction.setPerformedById(rs.getInt("performed_by"));
                transaction.setPerformedByUsername(rs.getString("performed_by_username"));
                transaction.setCreatedAt(rs.getTimestamp("created_at"));
                
                transactions.add(transaction);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return transactions;
    }
    
    /**
     * Calculates and applies interest for a savings account
     * 
     * @param accountId Account ID
     * @param performedById User ID who performed the operation
     * @return true if interest application successful, false otherwise
     */
    public static boolean applyInterest(int accountId, int performedById) {
        // Validate input
        if (accountId <= 0) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement updateStmt = null;
        PreparedStatement transactionStmt = null;
        
        try {
            // Get connection
            conn = DatabaseManager.getInstance().getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // Get current account details
            SavingsAccount account = getSavingsAccountById(accountId);
            
            if (account == null || !Constants.STATUS_ACTIVE.equals(account.getStatus())) {
                return false;
            }
            
            // Calculate interest amount (simplified annual interest)
            double interestAmount = account.getBalance() * account.getInterestRate() / 12; // Monthly interest
            double newBalance = account.getBalance() + interestAmount;
            
            // Update account balance
            String updateQuery = "UPDATE savings_accounts SET balance = ?, last_transaction_date = ? WHERE id = ?";
            
            updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setDouble(1, newBalance);
            updateStmt.setTimestamp(2, DateUtils.getCurrentTimestamp());
            updateStmt.setInt(3, accountId);
            
            int rowsAffected = updateStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Add interest transaction record
                String transactionId = generateTransactionId();
                
                String transactionQuery = "INSERT INTO transactions (transaction_id, account_id, " +
                        "transaction_type, amount, transaction_date, description, balance_after, performed_by) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                transactionStmt = conn.prepareStatement(transactionQuery);
                transactionStmt.setString(1, transactionId);
                transactionStmt.setInt(2, accountId);
                transactionStmt.setString(3, Constants.TRANSACTION_INTEREST_EARNING);
                transactionStmt.setDouble(4, interestAmount);
                transactionStmt.setTimestamp(5, DateUtils.getCurrentTimestamp());
                transactionStmt.setString(6, "Interest earning");
                transactionStmt.setDouble(7, newBalance);
                transactionStmt.setInt(8, performedById);
                
                transactionStmt.executeUpdate();
                
                // Commit transaction
                conn.commit();
                return true;
            }
            
            // Rollback on failure
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Rollback on exception
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            return false;
        } finally {
            // Close resources
            if (transactionStmt != null) {
                try {
                    transactionStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (updateStmt != null) {
                try {
                    updateStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Closes a savings account
     * 
     * @param accountId Account ID
     * @param performedById User ID who performed the operation
     * @return true if closure successful, false otherwise
     */
    public static boolean closeSavingsAccount(int accountId, int performedById) {
        // Validate input
        if (accountId <= 0) {
            return false;
        }
        
        Connection conn = null;
        PreparedStatement updateStmt = null;
        PreparedStatement transactionStmt = null;
        
        try {
            // Get connection
            conn = DatabaseManager.getInstance().getConnection();
            
            // Start transaction
            conn.setAutoCommit(false);
            
            // Get current account details
            SavingsAccount account = getSavingsAccountById(accountId);
            
            if (account == null) {
                return false;
            }
            
            // Check if balance is zero
            if (account.getBalance() > 0) {
                // Withdraw all balance first
                double amount = account.getBalance();
                
                // Add withdrawal transaction
                String transactionId = generateTransactionId();
                
                String transactionQuery = "INSERT INTO transactions (transaction_id, account_id, " +
                        "transaction_type, amount, transaction_date, description, balance_after, performed_by) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
                
                transactionStmt = conn.prepareStatement(transactionQuery);
                transactionStmt.setString(1, transactionId);
                transactionStmt.setInt(2, accountId);
                transactionStmt.setString(3, Constants.TRANSACTION_WITHDRAWAL);
                transactionStmt.setDouble(4, amount);
                transactionStmt.setTimestamp(5, DateUtils.getCurrentTimestamp());
                transactionStmt.setString(6, "Final withdrawal for account closure");
                transactionStmt.setDouble(7, 0);
                transactionStmt.setInt(8, performedById);
                
                transactionStmt.executeUpdate();
            }
            
            // Update account status to closed
            String updateQuery = "UPDATE savings_accounts SET balance = 0, status = ?, last_transaction_date = ? WHERE id = ?";
            
            updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, Constants.SAVINGS_STATUS_CLOSED);
            updateStmt.setTimestamp(2, DateUtils.getCurrentTimestamp());
            updateStmt.setInt(3, accountId);
            
            int rowsAffected = updateStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Commit transaction
                conn.commit();
                return true;
            }
            
            // Rollback on failure
            conn.rollback();
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            
            // Rollback on exception
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            
            return false;
        } finally {
            // Close resources
            if (transactionStmt != null) {
                try {
                    transactionStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (updateStmt != null) {
                try {
                    updateStmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Gets a transaction type display name
     * 
     * @param transactionType Transaction type code
     * @return Display name
     */
    public static String getTransactionTypeDisplay(String transactionType) {
        if (transactionType == null) {
            return "";
        }
        
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
            default:
                return transactionType;
        }
    }
    
    /**
     * Maps a result set to a SavingsAccount object
     * 
     * @param rs Result set
     * @return SavingsAccount object
     * @throws SQLException if an error occurs
     */
    private static SavingsAccount mapResultSetToSavingsAccount(ResultSet rs) throws SQLException {
        SavingsAccount account = new SavingsAccount();
        account.setId(rs.getInt("id"));
        account.setAccountNumber(rs.getString("account_number"));
        account.setMemberId(rs.getInt("member_id"));
        account.setAccountType(rs.getString("account_type"));
        account.setBalance(rs.getDouble("balance"));
        account.setInterestRate(rs.getDouble("interest_rate"));
        account.setStatus(rs.getString("status"));
        account.setOpenedDate(rs.getDate("opened_date"));
        account.setLastTransactionDate(rs.getTimestamp("last_transaction_date"));
        account.setCreatedAt(rs.getTimestamp("created_at"));
        account.setUpdatedAt(rs.getTimestamp("updated_at"));
        return account;
    }
    
    /**
     * Generates a unique account number
     * 
     * @param memberId Member ID
     * @return Account number
     */
    private static String generateAccountNumber(int memberId) {
        // Generate timestamp-based account number
        long timestamp = System.currentTimeMillis();
        return "SA" + memberId + timestamp % 1000000;
    }
    
    /**
     * Generates a unique transaction ID
     * 
     * @return Transaction ID
     */
    private static String generateTransactionId() {
        // Generate timestamp-based transaction ID
        long timestamp = System.currentTimeMillis();
        int random = (int) (Math.random() * 1000);
        return "TXN" + timestamp % 10000000 + random;
    }
    
    /**
     * Generates a reference number for various types of transactions
     * 
     * @param prefix The prefix to use for the reference number (e.g., "LN" for loan)
     * @return Reference number
     */
    public static String generateReferenceNumber(String prefix) {
        String dateStr = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String randomStr = String.format("%06d", (int) (Math.random() * 999999));
        
        return prefix + dateStr + randomStr;
    }
    
    /**
     * Gets a savings account by ID (alias for getSavingsAccountById)
     * 
     * @param accountId Account ID
     * @return SavingsAccount or null if not found
     */
    public static SavingsAccount getAccountById(int accountId) {
        return getSavingsAccountById(accountId);
    }
    
    /**
     * Gets a savings account by account number (alias for getSavingsAccountByNumber)
     * 
     * @param accountNumber Account number
     * @return SavingsAccount or null if not found
     */
    public static SavingsAccount getAccountByNumber(String accountNumber) {
        return getSavingsAccountByNumber(accountNumber);
    }
    
    /**
     * Gets the current interest settings
     * 
     * @return The current interest settings
     */
    public static InterestSettings getCurrentInterestSettings() {
        String query = "SELECT * FROM interest_settings ORDER BY effective_date DESC LIMIT 1";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                return mapResultSetToInterestSettings(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets the history of interest settings
     * 
     * @return List of interest settings, ordered by effective date descending
     */
    public static List<InterestSettings> getInterestSettingsHistory() {
        List<InterestSettings> settingsList = new ArrayList<>();
        String query = "SELECT * FROM interest_settings ORDER BY effective_date DESC";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                InterestSettings settings = mapResultSetToInterestSettings(rs);
                settingsList.add(settings);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return settingsList;
    }
    
    /**
     * Creates new interest settings
     * 
     * @param settings The interest settings to create
     * @return true if creation successful, false otherwise
     */
    public static boolean createInterestSettings(InterestSettings settings) {
        String query = "INSERT INTO interest_settings (regular_savings_rate, time_deposit_rate, " +
                "share_capital_rate, effective_date, created_date, created_by) " +
                "VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDouble(1, settings.getRegularSavingsRate());
            stmt.setDouble(2, settings.getTimeDepositRate());
            stmt.setDouble(3, settings.getShareCapitalRate());
            stmt.setDate(4, DateUtils.toSqlDate(settings.getEffectiveDate()));
            stmt.setTimestamp(5, DateUtils.getCurrentTimestamp());
            stmt.setInt(6, settings.getCreatedBy());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Maps a ResultSet row to an InterestSettings object
     * 
     * @param rs ResultSet containing interest settings data
     * @return InterestSettings object
     * @throws SQLException if an error occurs while accessing the ResultSet
     */
    private static InterestSettings mapResultSetToInterestSettings(ResultSet rs) throws SQLException {
        InterestSettings settings = new InterestSettings();
        settings.setId(rs.getInt("id"));
        settings.setRegularSavingsRate(rs.getDouble("regular_savings_rate"));
        settings.setTimeDepositRate(rs.getDouble("time_deposit_rate"));
        settings.setShareCapitalRate(rs.getDouble("share_capital_rate"));
        settings.setEffectiveDate(rs.getDate("effective_date"));
        settings.setCreatedDate(rs.getTimestamp("created_date"));
        settings.setCreatedBy(rs.getInt("created_by"));
        return settings;
    }
}