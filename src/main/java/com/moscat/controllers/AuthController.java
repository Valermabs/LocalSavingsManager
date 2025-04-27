package com.moscat.controllers;

import com.moscat.models.User;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.DateUtils;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller for authentication-related operations
 */
public class AuthController {
    
    private static User currentUser = null;
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /**
     * Alias for login method for backward compatibility
     * 
     * @param username The username
     * @param password The password
     * @return True if authentication was successful, false otherwise
     */
    public static boolean authenticate(String username, String password) {
        Map<String, Object> result = login(username, password);
        return (boolean) result.get("success");
    }
    
    /**
     * Authenticate a user with username and password
     * 
     * @param username The username
     * @param password The password
     * @return Map containing result (success/failure) and user data if successful
     */
    public static Map<String, Object> login(String username, String password) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                int id = rs.getInt("id");
                String storedPassword = rs.getString("password");
                String salt = rs.getString("salt");
                String fullName = rs.getString("full_name");
                String email = rs.getString("email");
                String contactNumber = rs.getString("contact_number");
                String role = rs.getString("role");
                boolean isActive = rs.getBoolean("is_active");
                
                // For backward compatibility, check if salt is null (plain text password)
                boolean passwordMatch = false;
                
                if (salt == null || salt.isEmpty()) {
                    // Legacy authentication (plain text)
                    passwordMatch = storedPassword.equals(password);
                    
                    // If match, upgrade the password storage to secure hash
                    if (passwordMatch) {
                        upgradePassword(id, password);
                    }
                } else {
                    // Secure authentication
                    passwordMatch = validatePassword(password, storedPassword, salt);
                }
                
                if (passwordMatch) {
                    if (isActive) {
                        // Create user object
                        User user = new User();
                        user.setId(id);
                        user.setUsername(username);
                        user.setFullName(fullName);
                        user.setEmail(email);
                        user.setContactNumber(contactNumber);
                        user.setRole(role);
                        user.setActive(isActive);
                        user.setLastLogin(LocalDateTime.now());
                        
                        // Update last login time
                        updateLastLogin(id);
                        
                        // Set as current user
                        currentUser = user;
                        
                        result.put("success", true);
                        result.put("user", user);
                        result.put("message", "Login successful");
                    } else {
                        result.put("message", "Account is inactive. Please contact the administrator.");
                    }
                } else {
                    result.put("message", "Invalid username or password");
                }
            } else {
                result.put("message", "Invalid username or password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("message", "Database error: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Create a new user
     * 
     * @param username The username
     * @param password The password
     * @param fullName The full name
     * @param email The email
     * @param contactNumber The contact number
     * @param role The role
     * @return Map containing result (success/failure) and any error message
     */
    public static Map<String, Object> createUser(String username, String password, String fullName, 
            String email, String contactNumber, String role) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        
        // Validate inputs
        if (username == null || username.trim().isEmpty()) {
            result.put("message", "Username is required");
            return result;
        }
        
        if (password == null || password.trim().isEmpty()) {
            result.put("message", "Password is required");
            return result;
        }
        
        if (fullName == null || fullName.trim().isEmpty()) {
            result.put("message", "Full name is required");
            return result;
        }
        
        // Check if username already exists
        if (userExists(username)) {
            result.put("message", "Username already exists");
            return result;
        }
        
        // Generate salt and hash password
        byte[] salt = generateSalt();
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashedPassword = hashPassword(password, salt);
        
        // Insert into database
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO users (username, password, salt, full_name, email, contact_number, role, is_active, created_at) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, hashedPassword);
            stmt.setString(3, saltBase64);
            stmt.setString(4, fullName);
            stmt.setString(5, email);
            stmt.setString(6, contactNumber);
            stmt.setString(7, role);
            stmt.setBoolean(8, true); // Active by default
            stmt.setString(9, DateUtils.formatLocalDateTime(LocalDateTime.now()));
            
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                result.put("success", true);
                result.put("message", "User created successfully");
            } else {
                result.put("message", "Failed to create user");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("message", "Database error: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Change a user's password
     * 
     * @param userId The user ID
     * @param currentPassword The current password
     * @param newPassword The new password
     * @return Map containing result (success/failure) and any error message
     */
    public static Map<String, Object> changePassword(int userId, String currentPassword, String newPassword) {
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        
        // Validate inputs
        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            result.put("message", "Current password is required");
            return result;
        }
        
        if (newPassword == null || newPassword.trim().isEmpty()) {
            result.put("message", "New password is required");
            return result;
        }
        
        try (Connection conn = DatabaseManager.getConnection()) {
            // Get current password info
            String sql = "SELECT password, salt FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String salt = rs.getString("salt");
                
                boolean passwordMatch = false;
                
                if (salt == null || salt.isEmpty()) {
                    // Legacy authentication (plain text)
                    passwordMatch = storedPassword.equals(currentPassword);
                } else {
                    // Secure authentication
                    passwordMatch = validatePassword(currentPassword, storedPassword, salt);
                }
                
                if (passwordMatch) {
                    // Generate new salt and hash new password
                    byte[] newSalt = generateSalt();
                    String newSaltBase64 = Base64.getEncoder().encodeToString(newSalt);
                    String newHashedPassword = hashPassword(newPassword, newSalt);
                    
                    // Update password
                    sql = "UPDATE users SET password = ?, salt = ? WHERE id = ?";
                    stmt = conn.prepareStatement(sql);
                    stmt.setString(1, newHashedPassword);
                    stmt.setString(2, newSaltBase64);
                    stmt.setInt(3, userId);
                    
                    int rowsAffected = stmt.executeUpdate();
                    
                    if (rowsAffected > 0) {
                        result.put("success", true);
                        result.put("message", "Password changed successfully");
                    } else {
                        result.put("message", "Failed to change password");
                    }
                } else {
                    result.put("message", "Current password is incorrect");
                }
            } else {
                result.put("message", "User not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            result.put("message", "Database error: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * Get a user by ID
     * 
     * @param userId The user ID
     * @return The user object, or null if not found
     */
    public static User getUserById(int userId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT * FROM users WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setContactNumber(rs.getString("contact_number"));
                user.setRole(rs.getString("role"));
                user.setActive(rs.getBoolean("is_active"));
                
                // Parse last login if available
                String lastLoginStr = rs.getString("last_login");
                if (lastLoginStr != null && !lastLoginStr.isEmpty()) {
                    user.setLastLogin(DateUtils.parseLocalDateTime(lastLoginStr));
                }
                
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get the currently logged in user
     * 
     * @return The current user, or null if no user is logged in
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Log out the current user
     */
    public static void logout() {
        currentUser = null;
    }
    
    /**
     * Checks if the current user is a Super Admin
     * 
     * @return True if the current user is a Super Admin, false otherwise
     */
    public static boolean isSuperAdmin() {
        if (currentUser == null) {
            return false;
        }
        return "SuperAdmin".equals(currentUser.getRole());
    }
    
    /**
     * Check if a username already exists
     * 
     * @param username The username to check
     * @return True if the username exists, false otherwise
     */
    private static boolean userExists(String username) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            
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
     * Update a user's last login time
     * 
     * @param userId The user ID
     */
    private static void updateLastLogin(int userId) {
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE users SET last_login = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, DateUtils.formatLocalDateTime(LocalDateTime.now()));
            stmt.setInt(2, userId);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Upgrade a user's password from plain text to secure hash
     * 
     * @param userId The user ID
     * @param plainPassword The plain text password
     */
    private static void upgradePassword(int userId, String plainPassword) {
        byte[] salt = generateSalt();
        String saltBase64 = Base64.getEncoder().encodeToString(salt);
        String hashedPassword = hashPassword(plainPassword, salt);
        
        try (Connection conn = DatabaseManager.getConnection()) {
            String sql = "UPDATE users SET password = ?, salt = ? WHERE id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, hashedPassword);
            stmt.setString(2, saltBase64);
            stmt.setInt(3, userId);
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Generate a random salt for password hashing
     * 
     * @return The generated salt
     */
    private static byte[] generateSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        return salt;
    }
    
    /**
     * Hash a password with a given salt
     * 
     * @param password The password to hash
     * @param salt The salt to use
     * @return The hashed password
     */
    private static String hashPassword(String password, byte[] salt) {
        try {
            KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = factory.generateSecret(spec).getEncoded();
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
            // Fall back to plain text if hashing fails
            return password;
        }
    }
    
    /**
     * Validate a password against a stored hash and salt
     * 
     * @param password The password to validate
     * @param storedHash The stored hash
     * @param storedSalt The stored salt (Base64 encoded)
     * @return True if the password is valid, false otherwise
     */
    private static boolean validatePassword(String password, String storedHash, String storedSalt) {
        try {
            byte[] salt = Base64.getDecoder().decode(storedSalt);
            String calculatedHash = hashPassword(password, salt);
            return calculatedHash.equals(storedHash);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}