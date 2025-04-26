package com.moscat.controllers;

import com.moscat.models.Permission;
import com.moscat.models.UserPermission;
import com.moscat.models.User;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for permission-related operations
 */
public class PermissionController {
    
    /**
     * Creates the permissions table if it doesn't exist
     */
    public static void createPermissionsTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS permissions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "code VARCHAR(50) NOT NULL UNIQUE, " +
                "description VARCHAR(255), " +
                "module VARCHAR(50), " +
                "active BOOLEAN DEFAULT TRUE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("Permissions table created or already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating permissions table: " + e.getMessage());
        }
    }
    
    /**
     * Creates the user permissions table if it doesn't exist
     */
    public static void createUserPermissionsTableIfNotExists() {
        String sql = "CREATE TABLE IF NOT EXISTS user_permissions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "permission_id INT NOT NULL, " +
                "active BOOLEAN DEFAULT TRUE, " +
                "granted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "expiry_date TIMESTAMP NULL, " +
                "granted_by INT, " +
                "notes VARCHAR(255), " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, " +
                "FOREIGN KEY (permission_id) REFERENCES permissions(id) ON DELETE CASCADE, " +
                "UNIQUE(user_id, permission_id)" +
                ")";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("User permissions table created or already exists.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Error creating user permissions table: " + e.getMessage());
        }
    }
    
    /**
     * Initializes default permissions if they don't exist
     */
    public static void initializeDefaultPermissions() {
        // Define default permissions
        List<Permission> defaultPermissions = new ArrayList<>();
        
        // Member management permissions
        defaultPermissions.add(new Permission("View Members", "MEMBER_VIEW", "Can view member information", "MEMBERS"));
        defaultPermissions.add(new Permission("Add Members", "MEMBER_ADD", "Can add new members", "MEMBERS"));
        defaultPermissions.add(new Permission("Edit Members", "MEMBER_EDIT", "Can edit member information", "MEMBERS"));
        defaultPermissions.add(new Permission("Delete Members", "MEMBER_DELETE", "Can delete members", "MEMBERS"));
        defaultPermissions.add(new Permission("Reactivate Members", "MEMBER_REACTIVATE", "Can reactivate dormant members", "MEMBERS"));
        
        // Savings account permissions
        defaultPermissions.add(new Permission("View Savings Accounts", "SAVINGS_VIEW", "Can view savings accounts", "SAVINGS"));
        defaultPermissions.add(new Permission("Create Savings Accounts", "SAVINGS_CREATE", "Can create new savings accounts", "SAVINGS"));
        defaultPermissions.add(new Permission("Process Deposits", "SAVINGS_DEPOSIT", "Can process deposits", "SAVINGS"));
        defaultPermissions.add(new Permission("Process Withdrawals", "SAVINGS_WITHDRAW", "Can process withdrawals", "SAVINGS"));
        defaultPermissions.add(new Permission("Adjust Interest Rates", "SAVINGS_ADJUST_INTEREST", "Can adjust interest rates", "SAVINGS"));
        defaultPermissions.add(new Permission("Close Accounts", "SAVINGS_CLOSE", "Can close savings accounts", "SAVINGS"));
        
        // Loan permissions
        defaultPermissions.add(new Permission("View Loans", "LOAN_VIEW", "Can view loan information", "LOANS"));
        defaultPermissions.add(new Permission("Create Loan Applications", "LOAN_CREATE", "Can create loan applications", "LOANS"));
        defaultPermissions.add(new Permission("Approve Loans", "LOAN_APPROVE", "Can approve loan applications", "LOANS"));
        defaultPermissions.add(new Permission("Reject Loans", "LOAN_REJECT", "Can reject loan applications", "LOANS"));
        defaultPermissions.add(new Permission("Disburse Loans", "LOAN_DISBURSE", "Can disburse approved loans", "LOANS"));
        defaultPermissions.add(new Permission("Adjust Loan Terms", "LOAN_ADJUST", "Can adjust loan terms", "LOANS"));
        defaultPermissions.add(new Permission("Process Loan Payments", "LOAN_PAYMENT", "Can process loan payments", "LOANS"));
        defaultPermissions.add(new Permission("Handle Delinquent Loans", "LOAN_DELINQUENT", "Can manage delinquent loans", "LOANS"));
        
        // Report permissions
        defaultPermissions.add(new Permission("View Reports", "REPORT_VIEW", "Can view all reports", "REPORTS"));
        defaultPermissions.add(new Permission("Generate Member Reports", "REPORT_MEMBER", "Can generate member reports", "REPORTS"));
        defaultPermissions.add(new Permission("Generate Savings Reports", "REPORT_SAVINGS", "Can generate savings reports", "REPORTS"));
        defaultPermissions.add(new Permission("Generate Loan Reports", "REPORT_LOAN", "Can generate loan reports", "REPORTS"));
        defaultPermissions.add(new Permission("Generate Financial Reports", "REPORT_FINANCIAL", "Can generate financial reports", "REPORTS"));
        
        // Admin permissions
        defaultPermissions.add(new Permission("Manage Users", "ADMIN_USERS", "Can manage system users", "ADMIN"));
        defaultPermissions.add(new Permission("Manage Permissions", "ADMIN_PERMISSIONS", "Can manage permissions", "ADMIN"));
        defaultPermissions.add(new Permission("System Settings", "ADMIN_SETTINGS", "Can modify system settings", "ADMIN"));
        defaultPermissions.add(new Permission("View Audit Logs", "ADMIN_AUDIT", "Can view audit logs", "ADMIN"));
        defaultPermissions.add(new Permission("Database Backup", "ADMIN_BACKUP", "Can perform database backups", "ADMIN"));
        
        // Insert permissions if they don't exist
        for (Permission permission : defaultPermissions) {
            try {
                if (!permissionExists(permission.getCode())) {
                    createPermission(permission);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                System.err.println("Error checking/creating permission: " + e.getMessage());
            }
        }
    }
    
    /**
     * Checks if a permission with the given code exists
     * 
     * @param code Permission code
     * @return true if exists, false otherwise
     * @throws SQLException if database error occurs
     */
    private static boolean permissionExists(String code) throws SQLException {
        String sql = "SELECT COUNT(*) FROM permissions WHERE code = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Creates a new permission
     * 
     * @param permission Permission object
     * @return true if created successfully, false otherwise
     */
    public static boolean createPermission(Permission permission) {
        String sql = "INSERT INTO permissions (name, code, description, module, active, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, permission.getName());
            stmt.setString(2, permission.getCode());
            stmt.setString(3, permission.getDescription());
            stmt.setString(4, permission.getModule());
            stmt.setBoolean(5, permission.isActive());
            stmt.setTimestamp(6, DateUtils.getCurrentTimestamp());
            stmt.setTimestamp(7, DateUtils.getCurrentTimestamp());
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        permission.setId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Gets a permission by its ID
     * 
     * @param id Permission ID
     * @return Permission object if found, null otherwise
     */
    public static Permission getPermissionById(int id) {
        String sql = "SELECT * FROM permissions WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPermission(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets a permission by its code
     * 
     * @param code Permission code
     * @return Permission object if found, null otherwise
     */
    public static Permission getPermissionByCode(String code) {
        String sql = "SELECT * FROM permissions WHERE code = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, code);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToPermission(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets all permissions
     * 
     * @return List of permissions
     */
    public static List<Permission> getAllPermissions() {
        String sql = "SELECT * FROM permissions ORDER BY module, name";
        List<Permission> permissions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                permissions.add(mapResultSetToPermission(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Gets all permissions grouped by module
     * 
     * @return Map of module name to list of permissions
     */
    public static Map<String, List<Permission>> getPermissionsByModule() {
        List<Permission> allPermissions = getAllPermissions();
        Map<String, List<Permission>> permissionsByModule = new HashMap<>();
        
        for (Permission permission : allPermissions) {
            String module = permission.getModule();
            if (!permissionsByModule.containsKey(module)) {
                permissionsByModule.put(module, new ArrayList<>());
            }
            permissionsByModule.get(module).add(permission);
        }
        
        return permissionsByModule;
    }
    
    /**
     * Updates a permission
     * 
     * @param permission Permission object
     * @return true if updated successfully, false otherwise
     */
    public static boolean updatePermission(Permission permission) {
        String sql = "UPDATE permissions SET name = ?, description = ?, module = ?, " +
                "active = ?, updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, permission.getName());
            stmt.setString(2, permission.getDescription());
            stmt.setString(3, permission.getModule());
            stmt.setBoolean(4, permission.isActive());
            stmt.setTimestamp(5, DateUtils.getCurrentTimestamp());
            stmt.setInt(6, permission.getId());
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Deletes a permission
     * 
     * @param id Permission ID
     * @return true if deleted successfully, false otherwise
     */
    public static boolean deletePermission(int id) {
        String sql = "DELETE FROM permissions WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Assigns a permission to a user
     * 
     * @param userId User ID
     * @param permissionId Permission ID
     * @param grantedBy ID of the user granting the permission
     * @return true if assigned successfully, false otherwise
     */
    public static boolean assignPermissionToUser(int userId, int permissionId, int grantedBy) {
        String sql = "INSERT INTO user_permissions (user_id, permission_id, granted_by, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE active = TRUE, granted_by = ?, updated_at = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            java.sql.Timestamp now = DateUtils.getCurrentTimestamp();
            
            stmt.setInt(1, userId);
            stmt.setInt(2, permissionId);
            stmt.setInt(3, grantedBy);
            stmt.setTimestamp(4, now);
            stmt.setTimestamp(5, now);
            stmt.setInt(6, grantedBy);
            stmt.setTimestamp(7, now);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Revokes a permission from a user
     * 
     * @param userId User ID
     * @param permissionId Permission ID
     * @return true if revoked successfully, false otherwise
     */
    public static boolean revokePermissionFromUser(int userId, int permissionId) {
        String sql = "UPDATE user_permissions SET active = FALSE, updated_at = ? " +
                "WHERE user_id = ? AND permission_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setTimestamp(1, DateUtils.getCurrentTimestamp());
            stmt.setInt(2, userId);
            stmt.setInt(3, permissionId);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Gets all permissions assigned to a user
     * 
     * @param userId User ID
     * @param activeOnly If true, only return active permissions
     * @return List of user permissions
     */
    public static List<UserPermission> getUserPermissions(int userId, boolean activeOnly) {
        String sql = "SELECT up.*, p.* FROM user_permissions up " +
                "JOIN permissions p ON up.permission_id = p.id " +
                "WHERE up.user_id = ?";
        
        if (activeOnly) {
            sql += " AND up.active = TRUE AND p.active = TRUE";
        }
        
        sql += " ORDER BY p.module, p.name";
        
        List<UserPermission> userPermissions = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    UserPermission userPermission = mapResultSetToUserPermission(rs);
                    Permission permission = mapResultSetToPermission(rs);
                    userPermission.setPermission(permission);
                    userPermissions.add(userPermission);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return userPermissions;
    }
    
    /**
     * Gets permission codes for a user (used for checking permissions)
     * 
     * @param userId User ID
     * @return List of permission codes
     */
    public static List<String> getUserPermissionCodes(int userId) {
        String sql = "SELECT p.code FROM user_permissions up " +
                "JOIN permissions p ON up.permission_id = p.id " +
                "WHERE up.user_id = ? AND up.active = TRUE AND p.active = TRUE";
        
        List<String> permissionCodes = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    permissionCodes.add(rs.getString("code"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissionCodes;
    }
    
    /**
     * Checks if a user has a specific permission
     * 
     * @param userId User ID
     * @param permissionCode Permission code
     * @return true if user has the permission, false otherwise
     */
    public static boolean hasPermission(int userId, String permissionCode) {
        // Super Admin has all permissions
        User user = AuthController.getUserById(userId);
        if (user != null && Constants.ROLE_SUPER_ADMIN.equals(user.getRole())) {
            return true;
        }
        
        String sql = "SELECT COUNT(*) FROM user_permissions up " +
                "JOIN permissions p ON up.permission_id = p.id " +
                "WHERE up.user_id = ? AND p.code = ? AND up.active = TRUE AND p.active = TRUE";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, permissionCode);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Checks if the currently logged-in user has a specific permission
     * 
     * @param permissionCode Permission code
     * @return true if user has the permission, false otherwise
     */
    public static boolean currentUserHasPermission(String permissionCode) {
        User currentUser = AuthController.getCurrentUser();
        if (currentUser == null) {
            return false;
        }
        
        return hasPermission(currentUser.getId(), permissionCode);
    }
    
    /**
     * Maps a ResultSet row to a Permission object
     * 
     * @param rs ResultSet
     * @return Permission object
     * @throws SQLException if mapping fails
     */
    private static Permission mapResultSetToPermission(ResultSet rs) throws SQLException {
        Permission permission = new Permission();
        permission.setId(rs.getInt("id"));
        permission.setName(rs.getString("name"));
        permission.setCode(rs.getString("code"));
        permission.setDescription(rs.getString("description"));
        permission.setModule(rs.getString("module"));
        permission.setActive(rs.getBoolean("active"));
        permission.setCreatedAt(rs.getTimestamp("created_at"));
        permission.setUpdatedAt(rs.getTimestamp("updated_at"));
        return permission;
    }
    
    /**
     * Maps a ResultSet row to a UserPermission object
     * 
     * @param rs ResultSet
     * @return UserPermission object
     * @throws SQLException if mapping fails
     */
    private static UserPermission mapResultSetToUserPermission(ResultSet rs) throws SQLException {
        UserPermission userPermission = new UserPermission();
        userPermission.setId(rs.getInt("id"));
        userPermission.setUserId(rs.getInt("user_id"));
        userPermission.setPermissionId(rs.getInt("permission_id"));
        userPermission.setActive(rs.getBoolean("active"));
        userPermission.setGrantedDate(rs.getTimestamp("granted_date"));
        userPermission.setExpiryDate(rs.getTimestamp("expiry_date"));
        userPermission.setGrantedBy(rs.getInt("granted_by"));
        userPermission.setNotes(rs.getString("notes"));
        userPermission.setCreatedAt(rs.getTimestamp("created_at"));
        userPermission.setUpdatedAt(rs.getTimestamp("updated_at"));
        return userPermission;
    }
}