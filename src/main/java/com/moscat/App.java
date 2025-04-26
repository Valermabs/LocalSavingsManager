package com.moscat;

import com.moscat.utils.DatabaseManager;
import com.moscat.views.LoginView;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Main application class
 */
public class App {
    
    /**
     * Main entry point
     * 
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        try {
            // Set look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // Initialize database
        initializeDatabase();
        
        // Create main frame
        JFrame mainFrame = new JFrame("MOSCAT Cooperative");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(400, 300);
        mainFrame.setLocationRelativeTo(null);
        
        // Show splash screen
        showSplashScreen(mainFrame);
        
        // Show login dialog
        SwingUtilities.invokeLater(() -> {
            new LoginView(mainFrame).setVisible(true);
        });
    }
    
    /**
     * Initializes the database
     */
    private static void initializeDatabase() {
        try {
            // Get database connection
            Connection conn = DatabaseManager.getInstance().getConnection();
            
            // Create tables
            createTables(conn);
            
            System.out.println("Database initialized successfully.");
        } catch (Exception e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Creates database tables
     * 
     * @param conn Database connection
     * @throws SQLException if an error occurs
     */
    private static void createTables(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            // Create users table
            String usersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "username VARCHAR(50) NOT NULL UNIQUE,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "first_name VARCHAR(50) NOT NULL,"
                    + "last_name VARCHAR(50) NOT NULL,"
                    + "email VARCHAR(100) NOT NULL,"
                    + "contact_number VARCHAR(20),"
                    + "role VARCHAR(20) NOT NULL,"
                    + "status VARCHAR(20) DEFAULT 'Active',"
                    + "last_login TIMESTAMP,"
                    + "active BOOLEAN DEFAULT TRUE,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ")";
            stmt.execute(usersTable);
            
            // Create members table
            String membersTable = "CREATE TABLE IF NOT EXISTS members ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "member_id VARCHAR(20) NOT NULL UNIQUE,"
                    + "first_name VARCHAR(50) NOT NULL,"
                    + "last_name VARCHAR(50) NOT NULL,"
                    + "gender VARCHAR(10),"
                    + "birth_date DATE,"
                    + "address VARCHAR(255),"
                    + "city VARCHAR(50),"
                    + "state VARCHAR(50),"
                    + "postal_code VARCHAR(20),"
                    + "country VARCHAR(50),"
                    + "phone VARCHAR(20),"
                    + "email VARCHAR(100),"
                    + "id_type VARCHAR(50),"
                    + "id_number VARCHAR(50),"
                    + "occupation VARCHAR(100),"
                    + "employer VARCHAR(100),"
                    + "monthly_income DECIMAL(12,2),"
                    + "status VARCHAR(20) DEFAULT 'Active',"
                    + "join_date DATE NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "notes TEXT"
                    + ")";
            stmt.execute(membersTable);
            
            // Create savings_accounts table
            String savingsAccountsTable = "CREATE TABLE IF NOT EXISTS savings_accounts ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "account_number VARCHAR(20) NOT NULL UNIQUE,"
                    + "member_id INTEGER NOT NULL,"
                    + "account_type VARCHAR(20) NOT NULL,"
                    + "balance DECIMAL(12,2) NOT NULL DEFAULT 0.0,"
                    + "interest_rate DECIMAL(5,3) NOT NULL,"
                    + "status VARCHAR(20) NOT NULL DEFAULT 'Active',"
                    + "opened_date DATE NOT NULL,"
                    + "last_transaction_date TIMESTAMP,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (member_id) REFERENCES members(id)"
                    + ")";
            stmt.execute(savingsAccountsTable);
            
            // Create transactions table
            String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "transaction_id VARCHAR(20) NOT NULL UNIQUE,"
                    + "account_id INTEGER NOT NULL,"
                    + "transaction_type VARCHAR(20) NOT NULL,"
                    + "amount DECIMAL(12,2) NOT NULL,"
                    + "transaction_date TIMESTAMP NOT NULL,"
                    + "description VARCHAR(255),"
                    + "balance_after DECIMAL(12,2) NOT NULL,"
                    + "performed_by INTEGER,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (account_id) REFERENCES savings_accounts(id),"
                    + "FOREIGN KEY (performed_by) REFERENCES users(id)"
                    + ")";
            stmt.execute(transactionsTable);
            
            // Create loans table
            String loansTable = "CREATE TABLE IF NOT EXISTS loans ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "loan_number VARCHAR(20) NOT NULL UNIQUE,"
                    + "member_id INTEGER NOT NULL,"
                    + "loan_type VARCHAR(20) NOT NULL,"
                    + "amount DECIMAL(12,2) NOT NULL,"
                    + "interest_rate DECIMAL(5,3) NOT NULL,"
                    + "term_months INTEGER NOT NULL,"
                    + "payment_frequency VARCHAR(20) NOT NULL,"
                    + "monthly_payment DECIMAL(12,2) NOT NULL,"
                    + "total_interest DECIMAL(12,2) NOT NULL,"
                    + "total_amount DECIMAL(12,2) NOT NULL,"
                    + "balance DECIMAL(12,2) NOT NULL,"
                    + "disbursed_date DATE,"
                    + "status VARCHAR(20) NOT NULL DEFAULT 'Pending',"
                    + "purpose VARCHAR(255),"
                    + "application_date DATE NOT NULL,"
                    + "approved_date DATE,"
                    + "approved_by INTEGER,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (member_id) REFERENCES members(id),"
                    + "FOREIGN KEY (approved_by) REFERENCES users(id)"
                    + ")";
            stmt.execute(loansTable);
            
            // Create loan_payments table
            String loanPaymentsTable = "CREATE TABLE IF NOT EXISTS loan_payments ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "payment_id VARCHAR(20) NOT NULL UNIQUE,"
                    + "loan_id INTEGER NOT NULL,"
                    + "payment_date DATE NOT NULL,"
                    + "due_date DATE NOT NULL,"
                    + "amount DECIMAL(12,2) NOT NULL,"
                    + "principal DECIMAL(12,2) NOT NULL,"
                    + "interest DECIMAL(12,2) NOT NULL,"
                    + "balance_after DECIMAL(12,2) NOT NULL,"
                    + "status VARCHAR(20) NOT NULL DEFAULT 'Unpaid',"
                    + "received_by INTEGER,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (loan_id) REFERENCES loans(id),"
                    + "FOREIGN KEY (received_by) REFERENCES users(id)"
                    + ")";
            stmt.execute(loanPaymentsTable);
            
            // Create system_settings table
            String settingsTable = "CREATE TABLE IF NOT EXISTS system_settings ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "setting_key VARCHAR(50) NOT NULL UNIQUE,"
                    + "setting_value VARCHAR(255) NOT NULL,"
                    + "description VARCHAR(255),"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ")";
            stmt.execute(settingsTable);
        }
    }
    
    /**
     * Shows a splash screen
     * 
     * @param parent Parent component
     */
    private static void showSplashScreen(Component parent) {
        JWindow splashScreen = new JWindow();
        splashScreen.setSize(400, 300);
        splashScreen.setLocationRelativeTo(parent);
        
        JPanel content = new JPanel(new BorderLayout());
        content.setBorder(BorderFactory.createLineBorder(new Color(51, 102, 153), 2));
        
        JLabel titleLabel = new JLabel("MOSCAT Cooperative", JLabel.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel subtitleLabel = new JLabel("Savings & Loan Management System", JLabel.CENTER);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JPanel labelPanel = new JPanel(new GridLayout(2, 1));
        labelPanel.add(titleLabel);
        labelPanel.add(subtitleLabel);
        
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        
        content.add(Box.createVerticalStrut(50), BorderLayout.NORTH);
        content.add(labelPanel, BorderLayout.CENTER);
        content.add(progressBar, BorderLayout.SOUTH);
        
        splashScreen.setContentPane(content);
        splashScreen.setVisible(true);
        
        // Close splash screen after 2 seconds
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                splashScreen.dispose();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}