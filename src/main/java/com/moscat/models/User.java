package com.moscat.models;

import java.time.LocalDateTime;

/**
 * Represents a user in the system
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String contactNumber;
    private LocalDateTime lastLogin;
    private LocalDateTime createdAt;
    
    // Default constructor
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
     * Checks if this user is a super admin
     * 
     * @return True if user is a super admin, false otherwise
     */
    public boolean isSuperAdmin() {
        // In the simplified model, all users are super admin
        return true;
    }
    
    /**
     * Get the first name from the full name
     * 
     * @return The first name
     */
    public String getFirstName() {
        if (fullName != null && fullName.contains(" ")) {
            return fullName.substring(0, fullName.indexOf(" "));
        } else {
            return fullName;
        }
    }
}