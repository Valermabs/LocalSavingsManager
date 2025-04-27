package com.moscat.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class for managing database connections
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private static final String DB_URL = "jdbc:h2:./data/moscatdb;AUTO_SERVER=TRUE";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";
    
    // Private constructor to enforce singleton pattern
    private DatabaseManager() {
        try {
            // Load the H2 JDBC driver
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC Driver not found: " + e.getMessage());
            throw new RuntimeException("H2 JDBC Driver not found", e);
        }
    }
    
    /**
     * Gets the singleton instance of the DatabaseManager
     * 
     * @return The DatabaseManager instance
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }
    
    /**
     * Gets a connection to the database
     * 
     * @return A database connection
     * @throws SQLException If a database error occurs
     */
    public static Connection getConnection() throws SQLException {
        // Ensure the instance is initialized
        getInstance();
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Initializes the database
     */
    public void initializeDatabase() {
        DatabaseInitializer.initialize();
    }
    
    /**
     * Safely closes database resources: ResultSet, PreparedStatement, and Connection
     * 
     * @param rs The ResultSet to close
     * @param stmt The PreparedStatement to close
     * @param conn The Connection to close
     */
    public static void closeResources(java.sql.ResultSet rs, java.sql.PreparedStatement stmt, java.sql.Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.err.println("Error closing PreparedStatement: " + e.getMessage());
            }
        }
        
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing Connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Safely closes database resources: ResultSet and PreparedStatement
     * 
     * @param rs The ResultSet to close
     * @param stmt The PreparedStatement to close
     */
    public static void closeResources(java.sql.ResultSet rs, java.sql.PreparedStatement stmt) {
        closeResources(rs, stmt, null);
    }
    
    /**
     * Safely closes database resources: PreparedStatement and Connection
     * 
     * @param stmt The PreparedStatement to close
     * @param conn The Connection to close
     */
    public static void closeResources(java.sql.PreparedStatement stmt, java.sql.Connection conn) {
        closeResources(null, stmt, conn);
    }
}