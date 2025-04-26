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
 * Controller for permission management
 */
public class PermissionController {
    
    /**
     * Checks if the current user has a specific permission
     * 
     * @param permissionCode The permission code to check
     * @return true if user has permission, false otherwise
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
        
        // Check user's specific permissions
        return hasPermission(currentUser.getId(), permissionCode);
    }
    
    /**
     * Checks if a user has a specific permission
     * 
     * @param userId User ID
     * @param permissionCode Permission code
     * @return true if user has permission, false otherwise
     */
    public static boolean hasPermission(int userId, String permissionCode) {
        String query = "SELECT COUNT(*) FROM user_permissions up "
                + "JOIN permissions p ON up.permission_id = p.id "
                + "WHERE up.user_id = ? AND p.code = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, permissionCode);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Gets a list of all available permissions
     * 
     * @return List of permission maps
     */
    public static List<Map<String, Object>> getAllPermissions() {
        String query = "SELECT id, name, code, description FROM permissions ORDER BY id";
        List<Map<String, Object>> permissions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Map<String, Object> permission = new HashMap<>();
                permission.put("id", rs.getInt("id"));
                permission.put("name", rs.getString("name"));
                permission.put("code", rs.getString("code"));
                permission.put("description", rs.getString("description"));
                
                permissions.add(permission);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Gets all permissions grouped by module
     * 
     * @return Map of permissions grouped by module
     */
    public static Map<String, List<Map<String, Object>>> getPermissionsByModule() {
        Map<String, List<Map<String, Object>>> modules = new HashMap<>();
        
        // Initialize standard modules
        modules.put("User Management", new ArrayList<>());
        modules.put("Member Management", new ArrayList<>());
        modules.put("Account Management", new ArrayList<>());
        modules.put("Transaction Management", new ArrayList<>());
        modules.put("Loan Management", new ArrayList<>());
        modules.put("Report Generation", new ArrayList<>());
        modules.put("System Settings", new ArrayList<>());
        modules.put("Other", new ArrayList<>());
        
        // Get all permissions
        List<Map<String, Object>> allPermissions = getAllPermissions();
        
        // Group permissions by module
        for (Map<String, Object> permission : allPermissions) {
            String code = (String) permission.get("code");
            String module = getPermissionModule(code);
            
            if (!modules.containsKey(module)) {
                modules.put(module, new ArrayList<>());
            }
            
            modules.get(module).add(permission);
        }
        
        return modules;
    }
    
    /**
     * Gets a permission's module based on its code
     * 
     * @param code Permission code
     * @return Module name
     */
    private static String getPermissionModule(String code) {
        if (code.startsWith("USER_")) {
            return "User Management";
        } else if (code.startsWith("MEMBER_")) {
            return "Member Management";
        } else if (code.startsWith("ACCOUNT_")) {
            return "Account Management";
        } else if (code.startsWith("TRANSACTION_")) {
            return "Transaction Management";
        } else if (code.startsWith("LOAN_")) {
            return "Loan Management";
        } else if (code.startsWith("REPORT_")) {
            return "Report Generation";
        } else if (code.startsWith("SYSTEM_")) {
            return "System Settings";
        } else {
            return "Other";
        }
    }
    
    /**
     * Gets the list of permission codes assigned to a user
     * 
     * @param userId User ID
     * @param includeInherited Whether to include inherited permissions based on user role
     * @return List of permission codes
     */
    public static List<String> getUserPermissions(int userId, boolean includeInherited) {
        // First get the user to check their role
        User user = AuthController.getUserById(userId);
        
        if (user == null) {
            return new ArrayList<>();
        }
        
        // SuperAdmin has all permissions
        if (includeInherited && user.isSuperAdmin()) {
            List<Map<String, Object>> allPermissions = getAllPermissions();
            List<String> permissionCodes = new ArrayList<>();
            
            for (Map<String, Object> permission : allPermissions) {
                permissionCodes.add((String) permission.get("code"));
            }
            
            return permissionCodes;
        }
        
        // Get explicitly assigned permissions
        return getExplicitUserPermissions(userId);
    }
    
    /**
     * Gets the list of permission codes directly assigned to a user
     * 
     * @param userId User ID
     * @return List of permission codes
     */
    public static List<String> getUserPermissions(int userId) {
        return getUserPermissions(userId, false);
    }
    
    /**
     * Gets the list of permission codes explicitly assigned to a user
     * 
     * @param userId User ID
     * @return List of permission codes
     */
    private static List<String> getExplicitUserPermissions(int userId) {
        String query = "SELECT p.code FROM user_permissions up "
                + "JOIN permissions p ON up.permission_id = p.id "
                + "WHERE up.user_id = ?";
        
        List<String> permissions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                permissions.add(rs.getString("code"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Sets the permissions for a user
     * 
     * @param userId User ID
     * @param permissionCodes List of permission codes
     * @return true if successful, false otherwise
     */
    public static boolean setUserPermissions(int userId, List<String> permissionCodes) {
        // First, get the user to check their role
        User user = AuthController.getUserById(userId);
        
        if (user == null) {
            return false;
        }
        
        // SuperAdmin users automatically have all permissions
        if (user.isSuperAdmin()) {
            return true;
        }
        
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Start transaction
            conn.setAutoCommit(false);
            
            try {
                // Delete existing permissions
                String deleteQuery = "DELETE FROM user_permissions WHERE user_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(deleteQuery)) {
                    stmt.setInt(1, userId);
                    stmt.executeUpdate();
                }
                
                // Insert new permissions
                if (!permissionCodes.isEmpty()) {
                    // Get permission IDs
                    Map<String, Integer> permissionIds = getPermissionIds(conn);
                    
                    // Insert each permission
                    String insertQuery = "INSERT INTO user_permissions (user_id, permission_id) VALUES (?, ?)";
                    try (PreparedStatement stmt = conn.prepareStatement(insertQuery)) {
                        for (String code : permissionCodes) {
                            Integer permissionId = permissionIds.get(code);
                            if (permissionId != null) {
                                stmt.setInt(1, userId);
                                stmt.setInt(2, permissionId);
                                stmt.executeUpdate();
                            }
                        }
                    }
                }
                
                // Commit transaction
                conn.commit();
                return true;
                
            } catch (SQLException e) {
                // Rollback transaction
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
     * Gets a map of permission codes to IDs
     * 
     * @param conn Database connection
     * @return Map of permission codes to IDs
     * @throws SQLException If a database error occurs
     */
    private static Map<String, Integer> getPermissionIds(Connection conn) throws SQLException {
        String query = "SELECT id, code FROM permissions";
        Map<String, Integer> permissionIds = new HashMap<>();
        
        try (PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                permissionIds.put(rs.getString("code"), rs.getInt("id"));
            }
        }
        
        return permissionIds;
    }
    
    /**
     * Creates the permissions table if it doesn't exist
     */
    public static void createPermissionsTableIfNotExists() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Create permissions table
            String createPermissionsTable = "CREATE TABLE IF NOT EXISTS permissions ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "code VARCHAR(50) UNIQUE NOT NULL, "
                    + "name VARCHAR(100) NOT NULL, "
                    + "description TEXT"
                    + ")";
            
            try (PreparedStatement stmt = conn.prepareStatement(createPermissionsTable)) {
                stmt.executeUpdate();
            }
            
            // Create user_permissions table
            String createUserPermissionsTable = "CREATE TABLE IF NOT EXISTS user_permissions ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "user_id INT NOT NULL, "
                    + "permission_id INT NOT NULL, "
                    + "UNIQUE (user_id, permission_id), "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE"
                    + ")";
            
            try (PreparedStatement stmt = conn.prepareStatement(createUserPermissionsTable)) {
                stmt.executeUpdate();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Assigns a permission to a user with a specified module ID
     *
     * @param userId User ID
     * @param permissionId Permission ID
     * @param moduleId Module ID
     * @return true if successful, false otherwise
     */
    public static boolean assignPermissionToUser(int userId, int permissionId, int moduleId) {
        // First check user role
        User user = AuthController.getUserById(userId);
        if (user == null || user.isSuperAdmin()) {
            // SuperAdmin already has all permissions
            return user != null && user.isSuperAdmin();
        }
        
        String query = "INSERT INTO user_permissions (user_id, permission_id, module_id) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, permissionId);
            stmt.setInt(3, moduleId);
            
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Assigns a permission to a user
     *
     * @param userId User ID
     * @param permissionId Permission ID
     * @return true if successful, false otherwise
     */
    public static boolean assignPermissionToUser(int userId, int permissionId) {
        String query = "INSERT INTO user_permissions (user_id, permission_id) VALUES (?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, permissionId);
            
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Revokes a permission from a user
     *
     * @param userId User ID
     * @param permissionId Permission ID
     * @return true if successful, false otherwise
     */
    public static boolean revokePermissionFromUser(int userId, int permissionId) {
        String query = "DELETE FROM user_permissions WHERE user_id = ? AND permission_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, permissionId);
            
            stmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Creates the user permissions table if it doesn't exist
     */
    public static void createUserPermissionsTableIfNotExists() {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            // Create user_permissions table
            String createUserPermissionsTable = "CREATE TABLE IF NOT EXISTS user_permissions ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT, "
                    + "user_id INT NOT NULL, "
                    + "permission_id INT NOT NULL, "
                    + "UNIQUE (user_id, permission_id), "
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, "
                    + "FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE"
                    + ")";
            
            try (PreparedStatement stmt = conn.prepareStatement(createUserPermissionsTable)) {
                stmt.executeUpdate();
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize default permissions in the database
     */
    public static void initializeDefaultPermissions() {
        createPermissionsTableIfNotExists();
        ensureStandardPermissionsExist();
    }
    
    /**
     * Ensures all standard permissions exist in the database
     */
    public static void ensureStandardPermissionsExist() {
        // Define standard permissions
        Map<String, String[]> standardPermissions = new HashMap<>();
        
        // User Management permissions
        standardPermissions.put(Constants.PERMISSION_USER_MANAGEMENT, 
                new String[]{"User Management", "Allows creating, editing, and deleting system users"});
        
        // Member Management permissions
        standardPermissions.put(Constants.PERMISSION_MEMBER_MANAGEMENT, 
                new String[]{"Member Management", "Allows creating, editing, and deleting cooperative members"});
        
        // Account Management permissions
        standardPermissions.put(Constants.PERMISSION_ACCOUNT_MANAGEMENT, 
                new String[]{"Account Management", "Allows managing savings accounts and time deposits"});
        
        // Transaction Management permissions
        standardPermissions.put(Constants.PERMISSION_TRANSACTION_MANAGEMENT, 
                new String[]{"Transaction Management", "Allows processing deposits, withdrawals, and transfers"});
        
        // Loan Management permissions
        standardPermissions.put(Constants.PERMISSION_LOAN_MANAGEMENT, 
                new String[]{"Loan Management", "Allows creating, approving, and managing loans"});
        
        // Report Generation permissions
        standardPermissions.put(Constants.PERMISSION_REPORT_GENERATION, 
                new String[]{"Report Generation", "Allows generating and viewing reports"});
        
        // System Settings permissions
        standardPermissions.put(Constants.PERMISSION_SYSTEM_SETTINGS, 
                new String[]{"System Settings", "Allows configuring system-wide settings"});
        
        // Create permissions that don't exist yet
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            for (Map.Entry<String, String[]> entry : standardPermissions.entrySet()) {
                String code = entry.getKey();
                String name = entry.getValue()[0];
                String description = entry.getValue()[1];
                
                createPermissionIfNotExists(conn, code, name, description);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates a permission if it doesn't exist
     * 
     * @param conn Database connection
     * @param code Permission code
     * @param name Permission name
     * @param description Permission description
     * @throws SQLException If a database error occurs
     */
    private static void createPermissionIfNotExists(Connection conn, String code, String name, 
            String description) throws SQLException {
        
        // Check if permission exists
        String checkQuery = "SELECT COUNT(*) FROM permissions WHERE code = ?";
        try (PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) == 0) {
                // Permission doesn't exist, create it
                String insertQuery = "INSERT INTO permissions (code, name, description) VALUES (?, ?, ?)";
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setString(1, code);
                    insertStmt.setString(2, name);
                    insertStmt.setString(3, description);
                    insertStmt.executeUpdate();
                }
            }
        }
    }
}