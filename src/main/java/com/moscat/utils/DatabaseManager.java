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
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }
    
    /**
     * Initializes the database
     */
    public void initializeDatabase() {
        DatabaseInitializer.initialize();
    }
}