package com.moscat.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class for managing database connections
 */
public class DatabaseManager {
    
    private static DatabaseManager instance;
    private String jdbcUrl;
    private String username;
    private String password;
    
    /**
     * Private constructor
     */
    private DatabaseManager() {
        // Configure H2 database
        String dbName = "moscat_db";
        this.jdbcUrl = "jdbc:h2:./data/" + dbName;
        this.username = "moscat";
        this.password = "moscat";
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
     * @return Database connection
     * @throws SQLException if connection fails
     */
    public Connection getConnection() throws SQLException {
        try {
            // Load driver
            Class.forName("org.h2.Driver");
            
            // Get connection
            return DriverManager.getConnection(jdbcUrl, username, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("H2 JDBC driver not found", e);
        }
    }
}