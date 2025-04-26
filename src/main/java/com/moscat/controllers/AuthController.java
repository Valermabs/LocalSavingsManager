package com.moscat.controllers;

import com.moscat.models.User;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Controller for authentication and user management
 */
public class AuthController {
    
    private static User currentUser;
    
    /**
     * Attempts to log in a user
     * 
     * @param username Username
     * @param password Password
     * @return User object if login successful, null otherwise
     */
    public static User login(String username, String password) {
        // Log login attempt (for debugging only)
        System.out.println("Login attempt with username: '" + username + "' and password length: " + password.length());
        
        // Check database for user
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                boolean active = rs.getBoolean("active");
                
                // Check if user is active
                if (!active) {
                    return null;
                }
                
                // Verify password
                if (PasswordHasher.verify(password, storedPassword)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setPassword(storedPassword); // Store hashed password
                    user.setFirstName(rs.getString("first_name"));
                    user.setLastName(rs.getString("last_name"));
                    user.setEmail(rs.getString("email"));
                    user.setRole(rs.getString("role"));
                    user.setLastLogin(rs.getTimestamp("last_login"));
                    user.setActive(active);
                    
                    // Update last login time
                    updateLastLogin(user.getId());
                    // Update last login time in the User object too
                    user.setLastLogin(new Date());
                    
                    // Set as current user
                    currentUser = user;
                    return user;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Logs out the current user
     */
    public static void logout() {
        currentUser = null;
    }
    
    /**
     * Gets the current logged-in user
     * 
     * @return Current user or null if not logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if the current user is a super admin
     * 
     * @return true if super admin, false otherwise
     */
    public static boolean isSuperAdmin() {
        return currentUser != null && currentUser.isSuperAdmin();
    }
    
    /**
     * Checks if the current user is a treasurer
     * 
     * @return true if treasurer, false otherwise
     */
    public static boolean isTreasurer() {
        return currentUser != null && currentUser.isTreasurer();
    }
    
    /**
     * Checks if the current user is a bookkeeper
     * 
     * @return true if bookkeeper, false otherwise
     */
    public static boolean isBookkeeper() {
        return currentUser != null && currentUser.isBookkeeper();
    }
    
    /**
     * Checks if the current user has access to the specified feature based on their role
     * 
     * @param feature The feature to check access for
     * @return true if the user has access, false otherwise
     */
    public static boolean hasRoleBasedAccess(String feature) {
        if (currentUser == null) {
            return false;
        }
        
        // SuperAdmin has access to everything
        if (isSuperAdmin()) {
            return true;
        }
        
        // Check role-specific permissions
        switch (feature) {
            // Features available to both Treasurer and Bookkeeper
            case "VIEW_MEMBERS":
            case "VIEW_TRANSACTIONS":
            case "VIEW_LOANS":
            case "GENERATE_REPORTS":
                return isTreasurer() || isBookkeeper();
                
            // Features available only to Treasurer
            case "REGISTER_MEMBERS":
            case "DEPOSIT_WITHDRAWAL":
            case "LOAN_CREATION":
            case "LOAN_PAYMENTS":
                return isTreasurer();
                
            // Features available only to SuperAdmin
            case "INTEREST_RATE_SETTING":
            case "USER_MANAGEMENT":
            case "SYSTEM_SETTINGS":
                return isSuperAdmin();
                
            default:
                return false;
        }
    }
    
    /**
     * Checks if the current user has permission for specific operations
     * This is a more granular check that uses the permission system
     * 
     * @param permissionCode The permission code to check
     * @return true if the user has the permission, false otherwise
     */
    public static boolean hasPermission(String permissionCode) {
        if (currentUser == null) {
            return false;
        }
        
        // SuperAdmin has all permissions
        if (isSuperAdmin()) {
            return true;
        }
        
        // Use the PermissionController to check if this user has the specific permission
        return PermissionController.currentUserHasPermission(permissionCode);
    }
    
    /**
     * Gets a user by ID
     * 
     * @param userId User ID
     * @return User or null if not found
     */
    public static User getUserById(int userId) {
        String query = "SELECT * FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setLastLogin(rs.getTimestamp("last_login"));
                user.setActive(rs.getBoolean("active"));
                
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Gets all users
     * 
     * @return List of all users
     */
    public static List<User> getAllUsers() {
        String query = "SELECT * FROM users ORDER BY username";
        List<User> users = new ArrayList<>();
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setLastLogin(rs.getTimestamp("last_login"));
                user.setActive(rs.getBoolean("active"));
                
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Creates a new user
     * 
     * @param user User to create
     * @param plainPassword Plain text password
     * @return true if creation successful, false otherwise
     */
    public static boolean createUser(User user, String plainPassword) {
        // Validate input
        if (user.getUsername() == null || user.getFirstName() == null || user.getLastName() == null || 
                user.getEmail() == null || user.getRole() == null || plainPassword == null) {
            return false;
        }
        
        // Check if username already exists
        if (getUserByUsernameInternal(user.getUsername()) != null) {
            return false;
        }
        
        // Hash password
        String hashedPassword = PasswordHasher.hash(plainPassword);
        
        String query = "INSERT INTO users (username, password, first_name, last_name, email, role, active) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, hashedPassword);
            stmt.setString(3, user.getFirstName());
            stmt.setString(4, user.getLastName());
            stmt.setString(5, user.getEmail());
            stmt.setString(6, user.getRole());
            stmt.setBoolean(7, user.isActive());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates a user
     * 
     * @param user User to update
     * @return true if update successful, false otherwise
     */
    public static boolean updateUser(User user) {
        // Validate input
        if (user.getId() <= 0 || user.getUsername() == null || user.getFirstName() == null || 
                user.getLastName() == null || user.getEmail() == null || user.getRole() == null) {
            return false;
        }
        
        String query = "UPDATE users SET username = ?, first_name = ?, last_name = ?, " +
                "email = ?, role = ?, active = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getFirstName());
            stmt.setString(3, user.getLastName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getRole());
            stmt.setBoolean(6, user.isActive());
            stmt.setInt(7, user.getId());
            
            int rowsAffected = stmt.executeUpdate();
            
            // Update current user if updating the logged-in user
            if (rowsAffected > 0 && currentUser != null && currentUser.getId() == user.getId()) {
                currentUser.setUsername(user.getUsername());
                currentUser.setFirstName(user.getFirstName());
                currentUser.setLastName(user.getLastName());
                currentUser.setEmail(user.getEmail());
                currentUser.setRole(user.getRole());
                currentUser.setActive(user.isActive());
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Changes a user's password
     * 
     * @param userId User ID
     * @param currentPassword Current password
     * @param newPassword New password
     * @return true if change successful, false otherwise
     */
    public static boolean changePassword(int userId, String currentPassword, String newPassword) {
        // Get user
        User user = getUserById(userId);
        
        if (user == null) {
            return false;
        }
        
        // Verify current password
        if (!PasswordHasher.verify(currentPassword, user.getPassword())) {
            return false;
        }
        
        // Hash new password
        String hashedPassword = PasswordHasher.hash(newPassword);
        
        // Update password
        String query = "UPDATE users SET password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Resets a user's password to a new random password
     * 
     * @param userId User ID
     * @return New password or null if reset failed
     */
    public static String resetPassword(int userId) {
        // Get user
        User user = getUserById(userId);
        
        if (user == null) {
            return null;
        }
        
        // Generate random password
        String newPassword = generateRandomPassword();
        
        // Hash new password
        String hashedPassword = PasswordHasher.hash(newPassword);
        
        // Update password
        String query = "UPDATE users SET password = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0 ? newPassword : null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // The method was moved to line 437
    
    /**
     * Activates a user
     * 
     * @param userId User ID
     * @return true if activation successful, false otherwise
     */
    public static boolean activateUser(int userId) {
        String query = "UPDATE users SET active = TRUE WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deactivates a user
     * 
     * @param userId User ID
     * @return true if deactivation successful, false otherwise
     */
    public static boolean deactivateUser(int userId) {
        // Check if this is the last active super admin
        if (isLastActiveSuperAdmin(userId)) {
            return false; // Can't deactivate the last active super admin
        }
        
        String query = "UPDATE users SET active = FALSE WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates the last login timestamp for a user
     * 
     * @param userId User ID
     * @return true if update successful, false otherwise
     */
    private static boolean updateLastLogin(int userId) {
        String query = "UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets a user by username
     * 
     * @param username Username
     * @return User or null if not found
     */
    // This method is used internally, but there's a public version with the same name
    private static User getUserByUsernameInternal(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setLastLogin(rs.getTimestamp("last_login"));
                user.setActive(rs.getBoolean("active"));
                
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Checks if a user is the last active super admin
     * 
     * @param userId User ID
     * @return true if user is the last active super admin, false otherwise
     */
    private static boolean isLastActiveSuperAdmin(int userId) {
        String query = "SELECT COUNT(*) FROM users WHERE role = ? AND active = TRUE AND id != ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, Constants.ROLE_SUPER_ADMIN);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int count = rs.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Deletes a user
     * 
     * @param userId User ID
     * @return true if deletion successful, false otherwise
     */
    public static boolean deleteUser(int userId) {
        // Get user
        User user = getUserById(userId);
        
        if (user == null) {
            return false;
        }
        
        // Don't allow deleting the last SuperAdmin
        if (user.isSuperAdmin() && isLastActiveSuperAdmin(userId)) {
            return false;
        }
        
        // Delete user
        String query = "DELETE FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Gets a user by username (public version)
     * 
     * @param username Username
     * @return User object or null if not found
     */
    public static User getUserByUsername(String username) {
        String query = "SELECT * FROM users WHERE username = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setFirstName(rs.getString("first_name"));
                user.setLastName(rs.getString("last_name"));
                user.setEmail(rs.getString("email"));
                user.setRole(rs.getString("role"));
                user.setLastLogin(rs.getTimestamp("last_login"));
                user.setActive(rs.getBoolean("active"));
                user.setStatus(rs.getString("status"));
                
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Generates a random password
     */
    private static String generateRandomPassword() {
        // Define character sets
        String uppercase = "ABCDEFGHJKLMNPQRSTUVWXYZ"; // Excluded I and O as they can be confused with 1 and 0
        String lowercase = "abcdefghijkmnopqrstuvwxyz"; // Excluded l as it can be confused with 1
        String digits = "23456789"; // Excluded 0 and 1 as they can be confused with O and l/I
        String specialChars = "@#$%&*!?";
        
        StringBuilder password = new StringBuilder();
        
        // Add at least one character from each set
        password.append(uppercase.charAt((int) (Math.random() * uppercase.length())));
        password.append(lowercase.charAt((int) (Math.random() * lowercase.length())));
        password.append(digits.charAt((int) (Math.random() * digits.length())));
        password.append(specialChars.charAt((int) (Math.random() * specialChars.length())));
        
        // Add 4 more random characters
        String allChars = uppercase + lowercase + digits + specialChars;
        for (int i = 0; i < 4; i++) {
            password.append(allChars.charAt((int) (Math.random() * allChars.length())));
        }
        
        // Shuffle the password
        char[] passArray = password.toString().toCharArray();
        for (int i = 0; i < passArray.length; i++) {
            int j = (int) (Math.random() * passArray.length);
            char temp = passArray[i];
            passArray[i] = passArray[j];
            passArray[j] = temp;
        }
        
        return new String(passArray);
    }
}