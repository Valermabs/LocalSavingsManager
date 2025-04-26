package com.moscat.controllers;

import com.moscat.models.User;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for managing permissions
 */
public class PermissionController {
    
    // Cache for permission IDs
    private static Map<String, Integer> permissionIdCache = new HashMap<>();
    
    /**
     * Creates permissions table if it doesn't exist
     */
    public static void createPermissionsTableIfNotExists() {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            String query = "CREATE TABLE IF NOT EXISTS permissions ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "code VARCHAR(50) NOT NULL UNIQUE,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "description VARCHAR(255),"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                    + ")";
            
            stmt.execute(query);
            System.out.println("Permissions table created or already exists.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates user permissions table if it doesn't exist
     */
    public static void createUserPermissionsTableIfNotExists() {
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             java.sql.Statement stmt = conn.createStatement()) {
            
            String query = "CREATE TABLE IF NOT EXISTS user_permissions ("
                    + "id INTEGER PRIMARY KEY AUTO_INCREMENT,"
                    + "user_id INTEGER NOT NULL,"
                    + "permission_id INTEGER NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,"
                    + "FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE,"
                    + "UNIQUE(user_id, permission_id)"
                    + ")";
            
            stmt.execute(query);
            System.out.println("User permissions table created or already exists.");
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes default permissions
     */
    public static void initializeDefaultPermissions() {
        // Define standard permissions
        createPermissionIfNotExists(Constants.PERMISSION_USER_MANAGEMENT, "User Management", "Manage system users");
        createPermissionIfNotExists(Constants.PERMISSION_MEMBER_MANAGEMENT, "Member Management", "Manage cooperative members");
        createPermissionIfNotExists(Constants.PERMISSION_ACCOUNT_MANAGEMENT, "Account Management", "Manage savings accounts");
        createPermissionIfNotExists(Constants.PERMISSION_TRANSACTION_MANAGEMENT, "Transaction Management", "Manage financial transactions");
        createPermissionIfNotExists(Constants.PERMISSION_LOAN_MANAGEMENT, "Loan Management", "Manage loans");
        createPermissionIfNotExists(Constants.PERMISSION_REPORT_GENERATION, "Report Generation", "Generate reports");
        createPermissionIfNotExists(Constants.PERMISSION_SYSTEM_SETTINGS, "System Settings", "Manage system settings");
    }
    
    /**
     * Creates a permission if it doesn't exist
     * 
     * @param code Permission code
     * @param name Permission name
     * @param description Permission description
     * @return Permission ID
     */
    private static int createPermissionIfNotExists(String code, String name, String description) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Check if permission exists
            String checkQuery = "SELECT id FROM permissions WHERE code = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
                checkStmt.setString(1, code);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next()) {
                    int id = rs.getInt("id");
                    permissionIdCache.put(code, id);
                    return id;
                }
            }
            
            // Create permission
            String insertQuery = "INSERT INTO permissions (code, name, description) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery, PreparedStatement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, code);
                insertStmt.setString(2, name);
                insertStmt.setString(3, description);
                
                int rowsAffected = insertStmt.executeUpdate();
                
                if (rowsAffected > 0) {
                    ResultSet generatedKeys = insertStmt.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);
                        permissionIdCache.put(code, id);
                        return id;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Checks if the current user has a specific permission
     * 
     * @param permissionCode Permission code
     * @return true if the user has the permission, false otherwise
     */
    public static boolean currentUserHasPermission(String permissionCode) {
        User currentUser = AuthController.getCurrentUser();
        
        if (currentUser == null) {
            return false;
        }
        
        // SuperAdmin has all permissions
        if (currentUser.isSuperAdmin()) {
            return true;
        }
        
        // Check if the user has this specific permission
        int userId = currentUser.getId();
        return userHasPermission(userId, permissionCode);
    }
    
    /**
     * Checks if a user has a specific permission
     * 
     * @param userId User ID
     * @param permissionCode Permission code
     * @return true if the user has the permission, false otherwise
     */
    public static boolean userHasPermission(int userId, String permissionCode) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT 1 FROM user_permissions up "
                    + "JOIN permissions p ON up.permission_id = p.id "
                    + "WHERE up.user_id = ? AND p.code = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setString(2, permissionCode);
                
                ResultSet rs = stmt.executeQuery();
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets all permissions
     * 
     * @return List of permission codes and names
     */
    public static List<Map<String, Object>> getAllPermissions() {
        List<Map<String, Object>> permissions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT id, code, name, description FROM permissions ORDER BY name");
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> permission = new HashMap<>();
                permission.put("id", rs.getInt("id"));
                permission.put("code", rs.getString("code"));
                permission.put("name", rs.getString("name"));
                permission.put("description", rs.getString("description"));
                
                permissions.add(permission);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Gets permissions for a specific user
     * 
     * @param userId User ID
     * @return List of permission codes
     */
    public static List<String> getUserPermissions(int userId) {
        List<String> permissions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT p.code FROM user_permissions up "
                    + "JOIN permissions p ON up.permission_id = p.id "
                    + "WHERE up.user_id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    permissions.add(rs.getString("code"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Grants a permission to a user
     * 
     * @param userId User ID
     * @param permissionCode Permission code
     * @return true if granted successfully, false otherwise
     */
    public static boolean grantPermission(int userId, String permissionCode) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Get permission ID
            int permissionId = getPermissionId(permissionCode);
            if (permissionId == -1) {
                return false;
            }
            
            // Check if already granted
            if (userHasPermission(userId, permissionCode)) {
                return true;
            }
            
            // Grant permission
            String query = "INSERT INTO user_permissions (user_id, permission_id) VALUES (?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, permissionId);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Revokes a permission from a user
     * 
     * @param userId User ID
     * @param permissionCode Permission code
     * @return true if revoked successfully, false otherwise
     */
    public static boolean revokePermission(int userId, String permissionCode) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Get permission ID
            int permissionId = getPermissionId(permissionCode);
            if (permissionId == -1) {
                return false;
            }
            
            // Revoke permission
            String query = "DELETE FROM user_permissions WHERE user_id = ? AND permission_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setInt(1, userId);
                stmt.setInt(2, permissionId);
                
                int rowsAffected = stmt.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Sets permissions for a user (removes existing ones and adds new ones)
     * 
     * @param userId User ID
     * @param permissionCodes List of permission codes
     * @return true if set successfully, false otherwise
     */
    public static boolean setUserPermissions(int userId, List<String> permissionCodes) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Begin transaction
            conn.setAutoCommit(false);
            
            try {
                // Remove all existing permissions
                String deleteQuery = "DELETE FROM user_permissions WHERE user_id = ?";
                try (PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery)) {
                    deleteStmt.setInt(1, userId);
                    deleteStmt.executeUpdate();
                }
                
                // Add new permissions
                if (permissionCodes != null && !permissionCodes.isEmpty()) {
                    String insertQuery = "INSERT INTO user_permissions (user_id, permission_id) VALUES (?, ?)";
                    try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                        for (String code : permissionCodes) {
                            int permissionId = getPermissionId(code);
                            if (permissionId != -1) {
                                insertStmt.setInt(1, userId);
                                insertStmt.setInt(2, permissionId);
                                insertStmt.addBatch();
                            }
                        }
                        insertStmt.executeBatch();
                    }
                }
                
                // Commit transaction
                conn.commit();
                return true;
                
            } catch (SQLException e) {
                // Rollback transaction on error
                conn.rollback();
                e.printStackTrace();
                return false;
            } finally {
                // Restore auto-commit
                conn.setAutoCommit(true);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets the ID of a permission
     * 
     * @param permissionCode Permission code
     * @return Permission ID or -1 if not found
     */
    private static int getPermissionId(String permissionCode) {
        // Check cache first
        if (permissionIdCache.containsKey(permissionCode)) {
            return permissionIdCache.get(permissionCode);
        }
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT id FROM permissions WHERE code = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, permissionCode);
                
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    int id = rs.getInt("id");
                    permissionIdCache.put(permissionCode, id);
                    return id;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return -1;
    }
}