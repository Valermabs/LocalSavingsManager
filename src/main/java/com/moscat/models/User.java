package com.moscat.models;

import java.time.LocalDateTime;

/**
 * Represents a system user
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String contactNumber;
    private String role;
    private boolean isActive;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    
    // Constructor
    public User() {
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getContactNumber() {
        return contactNumber;
    }
    
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the user's first name (extracted from full name)
     * 
     * @return The first name
     */
    public String getFirstName() {
        if (fullName != null && !fullName.isEmpty()) {
            String[] parts = fullName.split(" ");
            return parts[0];
        }
        return "";
    }
    
    /**
     * Gets the user's last name (extracted from full name)
     * 
     * @return The last name
     */
    public String getLastName() {
        if (fullName != null && !fullName.isEmpty()) {
            String[] parts = fullName.split(" ");
            if (parts.length > 1) {
                return parts[parts.length - 1];
            }
        }
        return "";
    }
    
    /**
     * Gets the status of the user
     * 
     * @return String representation of user status
     */
    public String getStatus() {
        return isActive ? "Active" : "Inactive";
    }
    
    /**
     * Sets the status of the user
     * 
     * @param status The status string
     */
    public void setStatus(String status) {
        this.isActive = "Active".equalsIgnoreCase(status);
    }
    
    /**
     * Checks if this user is a Super Administrator
     * 
     * @return True if the user is a Super Administrator, false otherwise
     */
    public boolean isSuperAdmin() {
        return com.moscat.utils.Constants.ROLE_SUPER_ADMIN.equals(role);
    }
    
    /**
     * Checks if this user is a Treasurer
     * 
     * @return True if the user is a Treasurer, false otherwise
     */
    public boolean isTreasurer() {
        return com.moscat.utils.Constants.ROLE_TREASURER.equals(role);
    }
    
    /**
     * Checks if this user is a Bookkeeper
     * 
     * @return True if the user is a Bookkeeper, false otherwise
     */
    public boolean isBookkeeper() {
        return com.moscat.utils.Constants.ROLE_BOOKKEEPER.equals(role);
    }
}