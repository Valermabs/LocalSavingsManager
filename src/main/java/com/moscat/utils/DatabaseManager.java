package com.moscat.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Database Manager class using Singleton pattern
 * Manages database connection and initialization
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private final String dbUrl = "jdbc:h2:./data/moscat_db;AUTO_SERVER=TRUE";
    private final String dbUser = "sa";
    private final String dbPassword = "";
    
    // Private constructor to enforce Singleton pattern
    private DatabaseManager() {
    }
    
    /**
     * Gets the singleton instance of DatabaseManager
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
     * Initializes the database and creates required tables
     * 
     * @throws SQLException If database initialization fails
     */
    public void initializeDatabase() throws SQLException {
        try {
            // Load the H2 database driver
            Class.forName("org.h2.Driver");
            
            // Establish connection
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            
            // Create tables from schema.sql
            executeSchemaScript();
            
            // Check if super admin exists, if not, create default super admin
            createDefaultSuperAdmin();
            
            System.out.println("Database initialized successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("H2 JDBC driver not found");
            throw new SQLException("Database driver not found", e);
        }
    }
    
    /**
     * Executes the SQL schema script
     * 
     * @throws SQLException If executing the schema script fails
     */
    private void executeSchemaScript() throws SQLException {
        try {
            // Read schema.sql file from resources
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            getClass().getResourceAsStream("/schema.sql")));
            
            String schemaScript = reader.lines().collect(Collectors.joining("\n"));
            reader.close();
            
            // Split script by semicolons to execute each statement separately
            String[] statements = schemaScript.split(";");
            
            Statement stmt = connection.createStatement();
            for (String statementText : statements) {
                if (!statementText.trim().isEmpty()) {
                    stmt.execute(statementText);
                }
            }
            stmt.close();
        } catch (Exception e) {
            throw new SQLException("Failed to execute schema script", e);
        }
    }
    
    /**
     * Creates the default super admin account if it doesn't exist
     * 
     * @throws SQLException If creating the super admin fails
     */
    private void createDefaultSuperAdmin() throws SQLException {
        PreparedStatement checkStmt = null;
        PreparedStatement insertStmt = null;
        ResultSet rs = null;
        
        try {
            // Check if super admin exists
            checkStmt = connection.prepareStatement(
                    "SELECT COUNT(*) FROM users WHERE username = ?");
            checkStmt.setString(1, "mmpcadmin");
            rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            
            // If super admin doesn't exist, create it
            if (count == 0) {
                insertStmt = connection.prepareStatement(
                        "INSERT INTO users (username, password, role, status) VALUES (?, ?, ?, ?)");
                insertStmt.setString(1, "mmpcadmin");
                insertStmt.setString(2, PasswordHasher.hashPassword("#MMPC@dmin2o25"));
                insertStmt.setString(3, "SUPER_ADMIN");
                insertStmt.setString(4, "ACTIVE");
                insertStmt.executeUpdate();
                System.out.println("Super admin account created");
            }
        } finally {
            if (rs != null) rs.close();
            if (checkStmt != null) checkStmt.close();
            if (insertStmt != null) insertStmt.close();
        }
    }
    
    /**
     * Gets a connection to the database
     * 
     * @return Connection object
     * @throws SQLException If getting connection fails
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
        }
        return connection;
    }
    
    /**
     * Executes an update SQL statement with parameters
     * 
     * @param sql SQL statement
     * @param params Parameters for the SQL statement
     * @return Number of rows affected
     * @throws SQLException If executing the statement fails
     */
    public int executeUpdate(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = null;
        try {
            stmt = getConnection().prepareStatement(sql);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        } finally {
            if (stmt != null) stmt.close();
        }
    }
    
    /**
     * Executes a query SQL statement with parameters
     * 
     * @param sql SQL query
     * @param params Parameters for the SQL query
     * @return ResultSet with query results
     * @throws SQLException If executing the query fails
     */
    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = getConnection().prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }
    
    /**
     * Closes the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Executes an insert statement and returns the generated key
     * 
     * @param sql SQL insert statement
     * @param params Parameters for the SQL statement
     * @return Generated key or -1 if error
     * @throws SQLException If executing the statement fails
     */
    public int executeInsert(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            stmt.executeUpdate();
            rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return -1;
        } finally {
            if (rs != null) rs.close();
            if (stmt != null) stmt.close();
        }
    }
    
    /**
     * Executes a batch insert statement
     * 
     * @param sql SQL insert statement
     * @param batchParams List of parameter arrays for batch insert
     * @return Array of row counts
     * @throws SQLException If executing the batch fails
     */
    public int[] executeBatch(String sql, List<Object[]> batchParams) throws SQLException {
        PreparedStatement stmt = null;
        try {
            connection.setAutoCommit(false);
            stmt = getConnection().prepareStatement(sql);
            
            for (Object[] params : batchParams) {
                for (int i = 0; i < params.length; i++) {
                    stmt.setObject(i + 1, params[i]);
                }
                stmt.addBatch();
            }
            
            int[] results = stmt.executeBatch();
            connection.commit();
            return results;
        } catch (SQLException e) {
            if (connection != null) {
                connection.rollback();
            }
            throw e;
        } finally {
            if (connection != null) {
                connection.setAutoCommit(true);
            }
            if (stmt != null) stmt.close();
        }
    }
}
