package com.moscat.controllers;

import com.moscat.models.Permission;
import com.moscat.models.User;
import com.moscat.models.UserPermission;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.DateUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller for permission operations
 */
public class PermissionController {

    /**
     * Gets all permissions
     * 
     * @return List of all permissions
     */
    public static List<Permission> getAllPermissions() {
        List<Permission> permissions = new ArrayList<>();
        String query = "SELECT * FROM permissions WHERE active = ? ORDER BY module, name";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setBoolean(1, true);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Permission permission = mapResultSetToPermission(rs);
                permissions.add(permission);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Gets a permission by ID
     * 
     * @param permissionId Permission ID
     * @return Permission or null if not found
     */
    public static Permission getPermissionById(int permissionId) {
        String query = "SELECT * FROM permissions WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, permissionId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPermission(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets a permission by code
     * 
     * @param code Permission code
     * @return Permission or null if not found
     */
    public static Permission getPermissionByCode(String code) {
        if (code == null || code.isEmpty()) {
            return null;
        }
        
        String query = "SELECT * FROM permissions WHERE code = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, code);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToPermission(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets permissions by module
     * 
     * @param module Module name
     * @return List of permissions for the module
     */
    public static List<Permission> getPermissionsByModule(String module) {
        List<Permission> permissions = new ArrayList<>();
        
        if (module == null || module.isEmpty()) {
            return permissions;
        }
        
        String query = "SELECT * FROM permissions WHERE module = ? AND active = ? ORDER BY name";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, module);
            stmt.setBoolean(2, true);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Permission permission = mapResultSetToPermission(rs);
                permissions.add(permission);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Gets all available modules
     * 
     * @return List of module names
     */
    public static List<String> getAllModules() {
        List<String> modules = new ArrayList<>();
        String query = "SELECT DISTINCT module FROM permissions WHERE active = ? ORDER BY module";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setBoolean(1, true);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                modules.add(rs.getString("module"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return modules;
    }
    
    /**
     * Creates a new permission
     * 
     * @param permission Permission to create
     * @return true if creation successful, false otherwise
     */
    public static boolean createPermission(Permission permission) {
        if (permission == null || permission.getCode() == null || permission.getCode().isEmpty()) {
            return false;
        }
        
        // Check if permission with this code already exists
        if (getPermissionByCode(permission.getCode()) != null) {
            return false;
        }
        
        String query = "INSERT INTO permissions (name, code, description, module, active, created_at, updated_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            
            // Set permission properties
            stmt.setString(1, permission.getName());
            stmt.setString(2, permission.getCode());
            stmt.setString(3, permission.getDescription());
            stmt.setString(4, permission.getModule());
            stmt.setBoolean(5, permission.isActive());
            
            // Set timestamps
            Timestamp now = DateUtils.getCurrentTimestamp();
            stmt.setTimestamp(6, now);
            stmt.setTimestamp(7, now);
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                // Get generated ID
                ResultSet rs = stmt.getGeneratedKeys();
                if (rs.next()) {
                    permission.setId(rs.getInt(1));
                }
                
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Updates a permission
     * 
     * @param permission Permission to update
     * @return true if update successful, false otherwise
     */
    public static boolean updatePermission(Permission permission) {
        if (permission == null || permission.getId() <= 0) {
            return false;
        }
        
        String query = "UPDATE permissions SET name = ?, description = ?, module = ?, active = ?, " +
                "updated_at = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            // Set permission properties
            stmt.setString(1, permission.getName());
            stmt.setString(2, permission.getDescription());
            stmt.setString(3, permission.getModule());
            stmt.setBoolean(4, permission.isActive());
            
            // Set timestamp
            stmt.setTimestamp(5, DateUtils.getCurrentTimestamp());
            
            // Set ID
            stmt.setInt(6, permission.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Deletes a permission
     * 
     * @param permissionId Permission ID
     * @return true if deletion successful, false otherwise
     */
    public static boolean deletePermission(int permissionId) {
        if (permissionId <= 0) {
            return false;
        }
        
        String query = "DELETE FROM permissions WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, permissionId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Gets all permissions for a user
     * 
     * @param userId User ID
     * @return List of permissions for the user
     */
    public static List<Permission> getUserPermissions(int userId) {
        List<Permission> permissions = new ArrayList<>();
        
        if (userId <= 0) {
            return permissions;
        }
        
        String query = "SELECT p.* FROM permissions p " +
                "JOIN user_permissions up ON p.id = up.permission_id " +
                "WHERE up.user_id = ? AND up.active = ? AND p.active = ? " +
                "AND (up.expiry_date IS NULL OR up.expiry_date > ?) " +
                "ORDER BY p.module, p.name";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setBoolean(2, true);
            stmt.setBoolean(3, true);
            stmt.setTimestamp(4, DateUtils.getCurrentTimestamp());
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Permission permission = mapResultSetToPermission(rs);
                permissions.add(permission);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return permissions;
    }
    
    /**
     * Gets all user permissions (with details) for a user
     * 
     * @param userId User ID
     * @return List of user permissions for the user
     */
    public static List<UserPermission> getUserPermissionDetails(int userId) {
        List<UserPermission> userPermissions = new ArrayList<>();
        
        if (userId <= 0) {
            return userPermissions;
        }
        
        String query = "SELECT up.*, p.name, p.code, p.description, p.module " +
                "FROM user_permissions up " +
                "JOIN permissions p ON up.permission_id = p.id " +
                "WHERE up.user_id = ? " +
                "ORDER BY up.active DESC, p.module, p.name";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                UserPermission userPermission = mapResultSetToUserPermission(rs);
                
                // Create and attach permission object
                Permission permission = new Permission();
                permission.setId(rs.getInt("permission_id"));
                permission.setName(rs.getString("name"));
                permission.setCode(rs.getString("code"));
                permission.setDescription(rs.getString("description"));
                permission.setModule(rs.getString("module"));
                
                userPermission.setPermission(permission);
                userPermissions.add(userPermission);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return userPermissions;
    }
    
    /**
     * Checks if a user has a specific permission
     * 
     * @param userId User ID
     * @param permissionCode Permission code
     * @return true if user has permission, false otherwise
     */
    public static boolean hasPermission(int userId, String permissionCode) {
        if (userId <= 0 || permissionCode == null || permissionCode.isEmpty()) {
            return false;
        }
        
        // First, check if the user is a SuperAdmin (they have all permissions)
        User user = AuthController.getUserById(userId);
        if (user != null && Constants.ROLE_SUPER_ADMIN.equals(user.getRole())) {
            return true;
        }
        
        String query = "SELECT COUNT(*) FROM user_permissions up " +
                "JOIN permissions p ON up.permission_id = p.id " +
                "WHERE up.user_id = ? AND p.code = ? " +
                "AND up.active = ? AND p.active = ? " +
                "AND (up.expiry_date IS NULL OR up.expiry_date > ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            stmt.setString(2, permissionCode);
            stmt.setBoolean(3, true);
            stmt.setBoolean(4, true);
            stmt.setTimestamp(5, DateUtils.getCurrentTimestamp());
            
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
     * Grants a permission to a user
     * 
     * @param userId User ID
     * @param permissionId Permission ID
     * @param grantedById ID of user granting the permission
     * @param expiryDate Expiry date (optional)
     * @param notes Notes (optional)
     * @return true if grant successful, false otherwise
     */
    public static boolean grantPermission(int userId, int permissionId, int grantedById, 
            Date expiryDate, String notes) {
        if (userId <= 0 || permissionId <= 0 || grantedById <= 0) {
            return false;
        }
        
        // Check if this user already has this permission
        String checkQuery = "SELECT id FROM user_permissions " +
                "WHERE user_id = ? AND permission_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement checkStmt = conn.prepareStatement(checkQuery)) {
            
            checkStmt.setInt(1, userId);
            checkStmt.setInt(2, permissionId);
            
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                // Permission already exists, update it instead
                int userPermissionId = rs.getInt("id");
                
                String updateQuery = "UPDATE user_permissions SET active = ?, " +
                        "granted_date = ?, expiry_date = ?, granted_by = ?, " +
                        "notes = ?, updated_at = ? WHERE id = ?";
                
                try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                    updateStmt.setBoolean(1, true);
                    updateStmt.setTimestamp(2, DateUtils.getCurrentTimestamp());
                    updateStmt.setTimestamp(3, expiryDate != null ? new Timestamp(expiryDate.getTime()) : null);
                    updateStmt.setInt(4, grantedById);
                    updateStmt.setString(5, notes);
                    updateStmt.setTimestamp(6, DateUtils.getCurrentTimestamp());
                    updateStmt.setInt(7, userPermissionId);
                    
                    return updateStmt.executeUpdate() > 0;
                }
            } else {
                // Create new user permission
                String insertQuery = "INSERT INTO user_permissions " +
                        "(user_id, permission_id, active, granted_date, expiry_date, " +
                        "granted_by, notes, created_at, updated_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                
                try (PreparedStatement insertStmt = conn.prepareStatement(insertQuery)) {
                    insertStmt.setInt(1, userId);
                    insertStmt.setInt(2, permissionId);
                    insertStmt.setBoolean(3, true);
                    
                    Timestamp now = DateUtils.getCurrentTimestamp();
                    insertStmt.setTimestamp(4, now);
                    insertStmt.setTimestamp(5, expiryDate != null ? new Timestamp(expiryDate.getTime()) : null);
                    insertStmt.setInt(6, grantedById);
                    insertStmt.setString(7, notes);
                    insertStmt.setTimestamp(8, now);
                    insertStmt.setTimestamp(9, now);
                    
                    return insertStmt.executeUpdate() > 0;
                }
            }
            
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
     * @return true if revocation successful, false otherwise
     */
    public static boolean revokePermission(int userId, int permissionId) {
        if (userId <= 0 || permissionId <= 0) {
            return false;
        }
        
        String query = "UPDATE user_permissions SET active = ?, updated_at = ? " +
                "WHERE user_id = ? AND permission_id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setBoolean(1, false);
            stmt.setTimestamp(2, DateUtils.getCurrentTimestamp());
            stmt.setInt(3, userId);
            stmt.setInt(4, permissionId);
            
            return stmt.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Maps a result set to a Permission object
     * 
     * @param rs Result set
     * @return Permission object
     * @throws SQLException if an error occurs
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
     * Maps a result set to a UserPermission object
     * 
     * @param rs Result set
     * @return UserPermission object
     * @throws SQLException if an error occurs
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
    
    /**
     * Creates the permissions table if it doesn't exist
     */
    public static void createPermissionsTableIfNotExists() {
        String query = "CREATE TABLE IF NOT EXISTS permissions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "name VARCHAR(100) NOT NULL, " +
                "code VARCHAR(50) NOT NULL UNIQUE, " +
                "description TEXT, " +
                "module VARCHAR(50) NOT NULL, " +
                "active BOOLEAN DEFAULT TRUE, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(query);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creates the user permissions table if it doesn't exist
     */
    public static void createUserPermissionsTableIfNotExists() {
        String query = "CREATE TABLE IF NOT EXISTS user_permissions (" +
                "id INT AUTO_INCREMENT PRIMARY KEY, " +
                "user_id INT NOT NULL, " +
                "permission_id INT NOT NULL, " +
                "active BOOLEAN DEFAULT TRUE, " +
                "granted_date TIMESTAMP, " +
                "expiry_date TIMESTAMP, " +
                "granted_by INT, " +
                "notes TEXT, " +
                "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "FOREIGN KEY (user_id) REFERENCES users(id), " +
                "FOREIGN KEY (permission_id) REFERENCES permissions(id), " +
                "FOREIGN KEY (granted_by) REFERENCES users(id), " +
                "UNIQUE (user_id, permission_id)" +
                ")";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.executeUpdate(query);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes default permissions if they don't exist
     */
    public static void initializeDefaultPermissions() {
        // Check if permissions already exist
        String query = "SELECT COUNT(*) FROM permissions";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            
            if (rs.next() && rs.getInt(1) > 0) {
                // Permissions already exist, no need to initialize
                return;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }
        
        // Define default permissions
        List<Permission> defaultPermissions = new ArrayList<>();
        
        // Member permissions
        defaultPermissions.add(new Permission("View Members", "MEMBER_VIEW", "View member information", "Members"));
        defaultPermissions.add(new Permission("Create Members", "MEMBER_CREATE", "Create new members", "Members"));
        defaultPermissions.add(new Permission("Edit Members", "MEMBER_EDIT", "Edit member information", "Members"));
        defaultPermissions.add(new Permission("Delete Members", "MEMBER_DELETE", "Delete members", "Members"));
        
        // Savings permissions
        defaultPermissions.add(new Permission("View Savings Accounts", "SAVINGS_VIEW", "View savings accounts", "Savings"));
        defaultPermissions.add(new Permission("Create Savings Accounts", "SAVINGS_CREATE", "Create new savings accounts", "Savings"));
        defaultPermissions.add(new Permission("Edit Savings Accounts", "SAVINGS_EDIT", "Edit savings accounts", "Savings"));
        defaultPermissions.add(new Permission("Delete Savings Accounts", "SAVINGS_DELETE", "Delete savings accounts", "Savings"));
        defaultPermissions.add(new Permission("Deposit", "SAVINGS_DEPOSIT", "Make deposits", "Savings"));
        defaultPermissions.add(new Permission("Withdraw", "SAVINGS_WITHDRAW", "Make withdrawals", "Savings"));
        defaultPermissions.add(new Permission("Manage Interest", "SAVINGS_INTEREST", "Manage interest rates and calculations", "Savings"));
        defaultPermissions.add(new Permission("Manage Dormant Accounts", "SAVINGS_DORMANT", "Manage dormant accounts", "Savings"));
        
        // Loan permissions
        defaultPermissions.add(new Permission("View Loans", "LOAN_VIEW", "View loans", "Loans"));
        defaultPermissions.add(new Permission("Create Loans", "LOAN_CREATE", "Create new loans", "Loans"));
        defaultPermissions.add(new Permission("Edit Loans", "LOAN_EDIT", "Edit loans", "Loans"));
        defaultPermissions.add(new Permission("Delete Loans", "LOAN_DELETE", "Delete loans", "Loans"));
        defaultPermissions.add(new Permission("Approve Loans", "LOAN_APPROVE", "Approve loan applications", "Loans"));
        defaultPermissions.add(new Permission("Disburse Loans", "LOAN_DISBURSE", "Disburse loan funds", "Loans"));
        defaultPermissions.add(new Permission("Collect Payments", "LOAN_PAYMENT", "Collect loan payments", "Loans"));
        
        // Report permissions
        defaultPermissions.add(new Permission("View Reports", "REPORT_VIEW", "View reports", "Reports"));
        defaultPermissions.add(new Permission("Generate Reports", "REPORT_GENERATE", "Generate new reports", "Reports"));
        defaultPermissions.add(new Permission("Export Reports", "REPORT_EXPORT", "Export reports", "Reports"));
        
        // User management permissions
        defaultPermissions.add(new Permission("View Users", "USER_VIEW", "View system users", "Users"));
        defaultPermissions.add(new Permission("Create Users", "USER_CREATE", "Create new system users", "Users"));
        defaultPermissions.add(new Permission("Edit Users", "USER_EDIT", "Edit system users", "Users"));
        defaultPermissions.add(new Permission("Delete Users", "USER_DELETE", "Delete system users", "Users"));
        defaultPermissions.add(new Permission("Manage Permissions", "USER_PERMISSIONS", "Manage user permissions", "Users"));
        
        // System permissions
        defaultPermissions.add(new Permission("View Settings", "SYSTEM_SETTINGS_VIEW", "View system settings", "System"));
        defaultPermissions.add(new Permission("Edit Settings", "SYSTEM_SETTINGS_EDIT", "Edit system settings", "System"));
        defaultPermissions.add(new Permission("Backup Database", "SYSTEM_BACKUP", "Backup the database", "System"));
        defaultPermissions.add(new Permission("Restore Database", "SYSTEM_RESTORE", "Restore the database from backup", "System"));
        
        // Insert the permissions
        for (Permission permission : defaultPermissions) {
            createPermission(permission);
        }
    }
}