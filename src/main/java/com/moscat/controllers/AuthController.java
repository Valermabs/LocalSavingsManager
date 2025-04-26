package com.moscat.controllers;

import com.moscat.models.User;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.PasswordHasher;
import com.moscat.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for authentication and user management
 */
public class AuthController {
    
    private static User currentUser;
    
    /**
     * Authenticates a user with the given credentials
     * 
     * @param username Username
     * @param password Password
     * @return true if authentication successful, false otherwise
     */
    public static boolean authenticate(String username, String password) {
        String query = "SELECT * FROM users WHERE username = ? AND status = 'ACTIVE'";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
                if (PasswordHasher.verifyPassword(password, storedPassword)) {
                    // Create user object
                    currentUser = new User();
                    currentUser.setId(rs.getInt("id"));
                    currentUser.setUsername(rs.getString("username"));
                    currentUser.setRole(rs.getString("role"));
                    currentUser.setStatus(rs.getString("status"));
                    currentUser.setFullName(rs.getString("full_name"));
                    currentUser.setEmail(rs.getString("email"));
                    currentUser.setContactNumber(rs.getString("contact_number"));
                    currentUser.setCreatedAt(rs.getDate("created_at"));
                    currentUser.setLastLogin(rs.getDate("last_login"));
                    
                    // Update last login time
                    updateLastLogin(currentUser.getId());
                    
                    return true;
                }
            }
            
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Updates the last login time for a user
     * 
     * @param userId User ID
     * @throws SQLException If database operation fails
     */
    private static void updateLastLogin(int userId) throws SQLException {
        String query = "UPDATE users SET last_login = ? WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setDate(1, DateUtils.getCurrentDate());
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    /**
     * Creates a new user
     * 
     * @param user User object
     * @param rawPassword Raw password
     * @return true if user created successfully, false otherwise
     */
    public static boolean createUser(User user, String rawPassword) {
        String query = "INSERT INTO users (username, password, role, status, full_name, " +
                       "email, contact_number, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setString(1, user.getUsername());
            stmt.setString(2, PasswordHasher.hashPassword(rawPassword));
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
     * Updates an existing user
     * 
     * @param user User object
     * @param newPassword New password (null if not changing)
     * @return true if user updated successfully, false otherwise
     */
    public static boolean updateUser(User user, String newPassword) {
        StringBuilder query = new StringBuilder("UPDATE users SET role = ?, status = ?, " +
                "full_name = ?, email = ?, contact_number = ?");
        
        if (newPassword != null && !newPassword.isEmpty()) {
            query.append(", password = ?");
        }
        
        query.append(" WHERE id = ?");
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query.toString())) {
            
            stmt.setString(1, user.getRole());
            stmt.setString(2, user.getStatus());
            stmt.setString(3, user.getFullName());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getContactNumber());
            
            int paramIndex = 6;
            if (newPassword != null && !newPassword.isEmpty()) {
                stmt.setString(paramIndex++, PasswordHasher.hashPassword(newPassword));
            }
            
            stmt.setInt(paramIndex, user.getId());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Deletes a user
     * 
     * @param userId User ID
     * @return true if user deleted successfully, false otherwise
     */
    public static boolean deleteUser(int userId) {
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
     * Gets a user by ID
     * 
     * @param userId User ID
     * @return User object, or null if not found
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
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setContactNumber(rs.getString("contact_number"));
                user.setCreatedAt(rs.getDate("created_at"));
                user.setLastLogin(rs.getDate("last_login"));
                
                return user;
            }
            
            return null;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
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
                user.setRole(rs.getString("role"));
                user.setStatus(rs.getString("status"));
                user.setFullName(rs.getString("full_name"));
                user.setEmail(rs.getString("email"));
                user.setContactNumber(rs.getString("contact_number"));
                user.setCreatedAt(rs.getDate("created_at"));
                user.setLastLogin(rs.getDate("last_login"));
                
                users.add(user);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    /**
     * Gets the currently logged-in user
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
     * Logs out the current user
     */
    public static void logout() {
        currentUser = null;
    }
    
    /**
     * Changes the password for the current user
     * 
     * @param currentPassword Current password
     * @param newPassword New password
     * @return true if password changed successfully, false otherwise
     */
    public static boolean changePassword(String currentPassword, String newPassword) {
        if (currentUser == null) {
            return false;
        }
        
        String query = "SELECT password FROM users WHERE id = ?";
        
        try (Connection conn = DatabaseManager.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, currentUser.getId());
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                
                if (PasswordHasher.verifyPassword(currentPassword, storedPassword)) {
                    // Update password
                    String updateQuery = "UPDATE users SET password = ? WHERE id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setString(1, PasswordHasher.hashPassword(newPassword));
                    updateStmt.setInt(2, currentUser.getId());
                    
                    int rowsAffected = updateStmt.executeUpdate();
                    updateStmt.close();
                    
                    return rowsAffected > 0;
                }
            }
            
            return false;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
