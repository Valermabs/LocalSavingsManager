package com.moscat.utils;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

/**
 * Initializes the database with required tables and default data
 */
public class DatabaseInitializer {
    
    /**
     * Initializes the database
     */
    public static void initialize() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            createTables(conn);
            createDefaultSuperAdmin(conn);
            createDefaultInterestSettings(conn);
            
            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates the required tables
     * 
     * @param conn Database connection
     * @throws SQLException If a database error occurs
     */
    private static void createTables(Connection conn) throws SQLException {
        // Create users table
        String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "username VARCHAR(50) UNIQUE NOT NULL, "
                + "password VARCHAR(255) NOT NULL, "
                + "full_name VARCHAR(100) NOT NULL, "
                + "email VARCHAR(100), "
                + "contact_number VARCHAR(20), "
                + "last_login DATETIME, "
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createUsersTable);
        }
        
        // Create members table
        String createMembersTable = "CREATE TABLE IF NOT EXISTS members ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "first_name VARCHAR(50) NOT NULL, "
                + "middle_name VARCHAR(50), "
                + "last_name VARCHAR(50) NOT NULL, "
                + "age INT NOT NULL, "
                + "birthdate DATE NOT NULL, "
                + "present_address TEXT NOT NULL, "
                + "permanent_address TEXT NOT NULL, "
                + "contact_number VARCHAR(20) NOT NULL, "
                + "email_address VARCHAR(100), "
                + "employer VARCHAR(100), "
                + "employment_status VARCHAR(50) NOT NULL, "
                + "gross_monthly_income DECIMAL(15,2) NOT NULL, "
                + "average_net_monthly_income DECIMAL(15,2) NOT NULL, "
                + "savings_balance DECIMAL(15,2) DEFAULT 0.00, "
                + "interest_earned DECIMAL(15,2) DEFAULT 0.00, "
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, "
                + "status VARCHAR(20) DEFAULT 'Active'"
                + ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createMembersTable);
        }
        
        // Create transactions table
        String createTransactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "member_id INT NOT NULL, "
                + "transaction_type VARCHAR(20) NOT NULL, "
                + "amount DECIMAL(15,2) NOT NULL, "
                + "transaction_date DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "description TEXT, "
                + "processed_by VARCHAR(50), "
                + "FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE"
                + ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createTransactionsTable);
        }
        
        // Create loans table
        String createLoansTable = "CREATE TABLE IF NOT EXISTS loans ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "member_id INT NOT NULL, "
                + "loan_type VARCHAR(50) NOT NULL, "
                + "loan_amount DECIMAL(15,2) NOT NULL, "
                + "interest_rate DECIMAL(5,2) NOT NULL, "
                + "previous_loan_balance DECIMAL(15,2) DEFAULT 0.00, "
                + "deductions DECIMAL(15,2) DEFAULT 0.00, "
                + "rlpf DECIMAL(15,2) DEFAULT 0.00, "
                + "net_proceeds DECIMAL(15,2) NOT NULL, "
                + "term_months INT NOT NULL, "
                + "application_date DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "approval_date DATETIME, "
                + "status VARCHAR(20) DEFAULT 'Pending', "
                + "FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE"
                + ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createLoansTable);
        }
        
        // Create loan amortization table
        String createLoanAmortizationTable = "CREATE TABLE IF NOT EXISTS loan_amortization ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "loan_id INT NOT NULL, "
                + "payment_number INT NOT NULL, "
                + "payment_date DATE NOT NULL, "
                + "principal_amount DECIMAL(15,2) NOT NULL, "
                + "interest_amount DECIMAL(15,2) NOT NULL, "
                + "total_payment DECIMAL(15,2) NOT NULL, "
                + "remaining_balance DECIMAL(15,2) NOT NULL, "
                + "payment_status VARCHAR(20) DEFAULT 'Unpaid', "
                + "actual_payment_date DATE, "
                + "FOREIGN KEY (loan_id) REFERENCES loans(id) ON DELETE CASCADE"
                + ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createLoanAmortizationTable);
        }
        
        // Create interest settings table
        String createInterestSettingsTable = "CREATE TABLE IF NOT EXISTS interest_settings ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "interest_rate DECIMAL(5,2) NOT NULL, "
                + "minimum_balance_required DECIMAL(15,2) NOT NULL, "
                + "computation_basis VARCHAR(20) NOT NULL, "
                + "effective_date DATE NOT NULL, "
                + "reason_for_change TEXT, "
                + "set_by VARCHAR(50) NOT NULL, "
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP"
                + ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createInterestSettingsTable);
        }
        
        // Create dormant accounts table
        String createDormantAccountsTable = "CREATE TABLE IF NOT EXISTS dormant_accounts ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "member_id INT NOT NULL, "
                + "last_transaction_date DATETIME, "
                + "dormant_since DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "dormant_status VARCHAR(20) DEFAULT 'Dormant', "
                + "notification_sent BOOLEAN DEFAULT FALSE, "
                + "FOREIGN KEY (member_id) REFERENCES members(id) ON DELETE CASCADE"
                + ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createDormantAccountsTable);
        }
        
        // Create journals table
        String createJournalsTable = "CREATE TABLE IF NOT EXISTS journals ("
                + "id INT PRIMARY KEY AUTO_INCREMENT, "
                + "month VARCHAR(7) NOT NULL, " // Format: YYYY-MM
                + "transactions_summary TEXT, "
                + "remarks TEXT, "
                + "created_at DATETIME DEFAULT CURRENT_TIMESTAMP, "
                + "updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                + ")";
        
        try (Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(createJournalsTable);
        }
    }
    
    /**
     * Creates the default SuperAdmin user
     * 
     * @param conn Database connection
     * @throws SQLException If a database error occurs
     */
    private static void createDefaultSuperAdmin(Connection conn) throws SQLException {
        // Check if SuperAdmin user already exists
        String query = "SELECT COUNT(*) FROM users WHERE username = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, "mmpcadmin");
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // User already exists, no need to create
                return;
            }
        }
        
        // Create SuperAdmin user
        String insertQuery = "INSERT INTO users (username, password, full_name, email, contact_number, created_at) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setString(1, "mmpcadmin");
            stmt.setString(2, PasswordHasher.hash("#MMPC@dmin2o25"));
            stmt.setString(3, "MOSCAT Super Administrator");
            stmt.setString(4, "admin@moscat.coop");
            stmt.setString(5, "N/A");
            stmt.setDate(6, Date.valueOf(LocalDate.now()));
            
            stmt.executeUpdate();
            System.out.println("Default SuperAdmin user created.");
        }
    }
    
    /**
     * Creates default interest settings
     * 
     * @param conn Database connection
     * @throws SQLException If a database error occurs
     */
    private static void createDefaultInterestSettings(Connection conn) throws SQLException {
        // Check if interest settings already exist
        String query = "SELECT COUNT(*) FROM interest_settings";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next() && rs.getInt(1) > 0) {
                // Settings already exist, no need to create
                return;
            }
        }
        
        // Create default interest settings
        String insertQuery = "INSERT INTO interest_settings (interest_rate, minimum_balance_required, computation_basis, effective_date, reason_for_change, set_by) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
            stmt.setDouble(1, 2.5); // 2.5% annual interest
            stmt.setDouble(2, 500.00); // 500 minimum balance
            stmt.setString(3, "Monthly"); // Monthly computation
            stmt.setDate(4, Date.valueOf(LocalDate.now()));
            stmt.setString(5, "Initial interest rate setting");
            stmt.setString(6, "System Initialization");
            
            stmt.executeUpdate();
            System.out.println("Default interest settings created.");
        }
    }
}