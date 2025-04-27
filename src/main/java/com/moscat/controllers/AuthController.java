package com.moscat.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import com.moscat.models.User;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.PasswordHasher;

/**
 * Handles authentication and user session management
 */
public class AuthController {
    private static User currentUser = null;
    
    /**
     * Attempts to authenticate a user with the given credentials
     * 
     * @param username Username
     * @param password Password
     * @return True if authentication successful, false otherwise
     */
    public static boolean authenticate(String username, String password) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "SELECT * FROM users WHERE username = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        String storedPassword = rs.getString("password");
                        
                        if (PasswordHasher.verify(password, storedPassword)) {
                            // Authentication successful, create user session
                            User user = new User();
                            user.setId(rs.getInt("id"));
                            user.setUsername(rs.getString("username"));
                            user.setFullName(rs.getString("full_name"));
                            user.setEmail(rs.getString("email"));
                            user.setContactNumber(rs.getString("contact_number"));
                            
                            // Set as current user
                            currentUser = user;
                            
                            // Update last login time
                            updateLastLogin(user.getId());
                            
                            return true;
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Database error during authentication: " + e.getMessage());
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Updates the last login time for a user
     * 
     * @param userId User ID
     */
    private static void updateLastLogin(int userId) {
        try (Connection conn = DatabaseManager.getInstance().getConnection()) {
            String query = "UPDATE users SET last_login = ? WHERE id = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                stmt.setInt(2, userId);
                
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Error updating last login time: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Gets the currently authenticated user
     * 
     * @return The current user, or null if no user is authenticated
     */
    public static User getCurrentUser() {
        return currentUser;
    }
    
    /**
     * Checks if a user is authenticated
     * 
     * @return True if a user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        return currentUser != null;
    }
    
    /**
     * Logs out the current user
     */
    public static void logout() {
        currentUser = null;
    }
    
    /**
     * Checks if the current user has super admin privileges
     * 
     * @return True if current user is a super admin, false otherwise
     */
    public static boolean isSuperAdmin() {
        // In the simplified model with just a SuperAdmin, all authenticated users are SuperAdmin
        return isAuthenticated();
    }
}