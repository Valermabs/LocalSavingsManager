package com.moscat.controllers;

import com.moscat.models.User;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.DateUtils;
import com.moscat.utils.PasswordHasher;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Controller for authentication and user management
 */
public class AuthController {
    
    private static User currentUser;
    
    /**
     * Authenticates a user
     * 
     * @param username Username
     * @param password Password
     * @return true if authentication successful, false otherwise
     */
    public static boolean login(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND status = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            stmt.setString(2, Constants.STATUS_ACTIVE);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                
                if (PasswordHasher.verifyPassword(password, storedHash)) {
                    // Create user object
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUsername(rs.getString("username"));
                    user.setRole(rs.getString("role"));
                    user.setStatus(rs.getString("status"));
                    user.setFullName(rs.getString("full_name"));
                    user.setEmail(rs.getString("email"));
                    user.setContactNumber(rs.getString("contact_number"));
                    user.setCreatedAt(rs.getDate("created_at"));
                    user.setLastLogin(rs.getDate("last_login"));
                    
                    // Update last login
                    updateLastLogin(user.getId());
                    
                    // Set current user
                    currentUser = user;
                    
                    return true;
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Logs out the current user
     */
    public static void logout() {
        currentUser = null;
    }
    
    /**
     * Gets the current user
     * 
     * @return Current user
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is logged in
     * 
     * @return true if user is logged in, false otherwise
     */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
    
    /**
     * Changes a user's password
     * 
     * @param userId User ID
     * @param currentPassword Current password
     * @param newPassword New password
     * @return true if password changed successfully, false otherwise
     */
    public static boolean changePassword(int userId, String currentPassword, String newPassword) {
        // First verify current password
        String checkQuery = "SELECT password FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(checkQuery)) {
            
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedHash = rs.getString("password");
                
                if (PasswordHasher.verifyPassword(currentPassword, storedHash)) {
                    // Current password is correct, update to new password
                    String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
                    
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, PasswordHasher.hashPassword(newPassword));
                        updateStmt.setInt(2, userId);
                        
                        int rowsAffected = updateStmt.executeUpdate();
                        return rowsAffected > 0;
                    }
                }
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Creates a new user
     * 
     * @param user User object
     * @param password Password
     * @return true if user created successfully, false otherwise
     */
    public static boolean createUser(User user, String password) {
        String query = "INSERT INTO users (username, password, role, status, full_name, " +
                "email, contact_number, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, PasswordHasher.hashPassword(password));
            stmt.setString(3, user.getRole());
            stmt.setString(4, user.getStatus());
            stmt.setString(5, user.getFullName());
            stmt.setString(6, user.getEmail());
            stmt.setString(7, user.getContactNumber());
            stmt.setDate(8, DateUtils.getCurrentDate());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates a user's last login date
     * 
     * @param userId User ID
     */
    private static void updateLastLogin(int userId) {
        String query = "UPDATE users SET last_login = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDate(1, DateUtils.getCurrentDate());
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}