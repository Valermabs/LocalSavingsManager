package com.moscat.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Database manager for handling connections and initialization
 */
public class DatabaseManager {
    
    private static final String DB_URL = "jdbc:h2:./database/moscat;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    private static final String DB_DRIVER = "org.h2.Driver";
    
    private static DatabaseManager instance;
    
    /**
     * Private constructor to prevent direct instantiation
     */
    private DatabaseManager() {
        try {
            Class.forName(DB_DRIVER);
            initializeDatabase();
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("H2 JDBC Driver not found", e);
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            throw new RuntimeException("Error initializing database", e);
        }
    }
    
    /**
     * Gets the singleton instance
     * 
     * @return DatabaseManager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Gets a database connection
     * 
     * @return Connection object
     * @throws SQLException If connection error occurs
     */
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Initializes the database schema
     * 
     * @throws SQLException If SQL error occurs
     */
    private void initializeDatabase() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Create users table
            stmt.execute("CREATE TABLE IF NOT EXISTS users (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "username VARCHAR(50) NOT NULL UNIQUE, " +
                    "password VARCHAR(255) NOT NULL, " +
                    "role VARCHAR(20) NOT NULL, " +
                    "status VARCHAR(20) NOT NULL, " +
                    "full_name VARCHAR(100), " +
                    "email VARCHAR(100), " +
                    "contact_number VARCHAR(50), " +
                    "created_at DATE, " +
                    "last_login DATE" +
                    ")");
            
            // Create members table
            stmt.execute("CREATE TABLE IF NOT EXISTS members (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "member_number VARCHAR(20) NOT NULL UNIQUE, " +
                    "first_name VARCHAR(50) NOT NULL, " +
                    "middle_name VARCHAR(50), " +
                    "last_name VARCHAR(50) NOT NULL, " +
                    "birth_date DATE, " +
                    "contact_number VARCHAR(50), " +
                    "email VARCHAR(100), " +
                    "present_address VARCHAR(255), " +
                    "permanent_address VARCHAR(255), " +
                    "employer VARCHAR(100), " +
                    "employment_status VARCHAR(50), " +
                    "gross_monthly_income DECIMAL(12,2), " +
                    "avg_net_monthly_income DECIMAL(12,2), " +
                    "status VARCHAR(20) NOT NULL, " +
                    "join_date DATE, " +
                    "last_activity_date DATE, " +
                    "loan_eligibility_amount DECIMAL(12,2)" +
                    ")");
            
            // Create savings_accounts table
            stmt.execute("CREATE TABLE IF NOT EXISTS savings_accounts (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "member_id INT NOT NULL, " +
                    "account_number VARCHAR(20) NOT NULL UNIQUE, " +
                    "balance DECIMAL(12,2) NOT NULL, " +
                    "interest_earned DECIMAL(12,2) NOT NULL, " +
                    "status VARCHAR(20) NOT NULL, " +
                    "open_date DATE, " +
                    "last_activity_date DATE, " +
                    "FOREIGN KEY (member_id) REFERENCES members(id)" +
                    ")");
            
            // Create transactions table
            stmt.execute("CREATE TABLE IF NOT EXISTS transactions (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "account_id INT NOT NULL, " +
                    "user_id INT NOT NULL, " +
                    "transaction_type VARCHAR(20) NOT NULL, " +
                    "amount DECIMAL(12,2) NOT NULL, " +
                    "running_balance DECIMAL(12,2) NOT NULL, " +
                    "description VARCHAR(255), " +
                    "reference_number VARCHAR(50) NOT NULL UNIQUE, " +
                    "transaction_date TIMESTAMP, " +
                    "FOREIGN KEY (account_id) REFERENCES savings_accounts(id), " +
                    "FOREIGN KEY (user_id) REFERENCES users(id)" +
                    ")");
            
            // Create loans table
            stmt.execute("CREATE TABLE IF NOT EXISTS loans (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "member_id INT NOT NULL, " +
                    "loan_number VARCHAR(20) NOT NULL UNIQUE, " +
                    "loan_type VARCHAR(50) NOT NULL, " +
                    "principal_amount DECIMAL(12,2) NOT NULL, " +
                    "interest_rate DECIMAL(5,2) NOT NULL, " +
                    "term_years INT NOT NULL, " +
                    "monthly_amortization DECIMAL(12,2) NOT NULL, " +
                    "remaining_balance DECIMAL(12,2) NOT NULL, " +
                    "status VARCHAR(20) NOT NULL, " +
                    "purpose VARCHAR(255), " +
                    "application_date DATE, " +
                    "approval_date DATE, " +
                    "release_date DATE, " +
                    "maturity_date DATE, " +
                    "last_payment_date DATE, " +
                    "FOREIGN KEY (member_id) REFERENCES members(id)" +
                    ")");
            
            // Create loan_payments table
            stmt.execute("CREATE TABLE IF NOT EXISTS loan_payments (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "loan_id INT NOT NULL, " +
                    "payment_date DATE NOT NULL, " +
                    "amount DECIMAL(12,2) NOT NULL, " +
                    "principal_portion DECIMAL(12,2) NOT NULL, " +
                    "interest_portion DECIMAL(12,2) NOT NULL, " +
                    "balance_after_payment DECIMAL(12,2) NOT NULL, " +
                    "reference_number VARCHAR(50) NOT NULL UNIQUE, " +
                    "notes VARCHAR(255), " +
                    "FOREIGN KEY (loan_id) REFERENCES loans(id)" +
                    ")");
            
            // Create loan_types table
            stmt.execute("CREATE TABLE IF NOT EXISTS loan_types (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "code VARCHAR(20) NOT NULL UNIQUE, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "description VARCHAR(255), " +
                    "interest_rate DECIMAL(5,2) NOT NULL, " +
                    "min_term_months INT NOT NULL, " +
                    "max_term_months INT NOT NULL, " +
                    "min_amount DECIMAL(12,2) NOT NULL, " +
                    "max_amount DECIMAL(12,2) NOT NULL, " +
                    "requires_rlpf BOOLEAN NOT NULL, " +
                    "status VARCHAR(20) NOT NULL" +
                    ")");
            
            // Create interest_settings table
            stmt.execute("CREATE TABLE IF NOT EXISTS interest_settings (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "interest_rate DECIMAL(5,2) NOT NULL, " +
                    "minimum_balance DECIMAL(12,2) NOT NULL, " +
                    "calculation_method VARCHAR(20) NOT NULL, " +
                    "effective_date DATE NOT NULL, " +
                    "change_basis VARCHAR(255), " +
                    "status VARCHAR(20) NOT NULL, " +
                    "created_at DATE" +
                    ")");
            
            // Create default super admin user if not exists
            stmt.execute("MERGE INTO users (id, username, password, role, status, full_name, created_at) " +
                    "KEY(username) VALUES (1, 'mmpcadmin', " +
                    "'8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', " + // SHA-256 hash of #MMPC@dmin2o25
                    "'SUPER_ADMIN', 'ACTIVE', 'Super Administrator', CURRENT_DATE())");
            
            // Create default loan types if not exists
            stmt.execute("MERGE INTO loan_types (id, code, name, description, interest_rate, min_term_months, " +
                    "max_term_months, min_amount, max_amount, requires_rlpf, status) " +
                    "KEY(code) VALUES (1, 'REGULAR', 'Regular Loan', 'Standard loan for members', " +
                    "12.00, 12, 36, 10000.00, 300000.00, TRUE, 'ACTIVE')");
            
            stmt.execute("MERGE INTO loan_types (id, code, name, description, interest_rate, min_term_months, " +
                    "max_term_months, min_amount, max_amount, requires_rlpf, status) " +
                    "KEY(code) VALUES (2, 'EMERGENCY', 'Emergency Loan', 'Short-term loan for emergencies', " +
                    "9.00, 6, 12, 5000.00, 50000.00, TRUE, 'ACTIVE')");
            
            stmt.execute("MERGE INTO loan_types (id, code, name, description, interest_rate, min_term_months, " +
                    "max_term_months, min_amount, max_amount, requires_rlpf, status) " +
                    "KEY(code) VALUES (3, 'PETTY_CASH', 'Petty Cash Loan', 'Small quick-release loan', " +
                    "6.00, 1, 3, 1000.00, 10000.00, FALSE, 'ACTIVE')");
            
            // Create default interest settings if not exists
            stmt.execute("MERGE INTO interest_settings (id, interest_rate, minimum_balance, calculation_method, " +
                    "effective_date, change_basis, status, created_at) " +
                    "KEY(id) VALUES (1, 2.50, 1000.00, 'MONTHLY', CURRENT_DATE(), " +
                    "'Initial interest rate setting', 'ACTIVE', CURRENT_DATE())");
        }
    }
}