package com.moscat.models;

import java.sql.Date;

/**
 * Model class for system users
 */
public class User {
    private int id;
    private String username;
    private String password;
    private String role; // SUPER_ADMIN, TREASURER, BOOKKEEPER
    private String status; // ACTIVE, INACTIVE
    private String fullName;
    private String email;
    private String contactNumber;
    private Date createdAt;
    private Date lastLogin;
    
    /**
     * Default constructor
     */
    public User() {
    }
    
    /**
     * Parameterized constructor
     * 
     * @param id User ID
     * @param username Username
     * @param password Password (hashed)
     * @param role User role
     * @param status User status
     * @param fullName Full name
     * @param email Email address
     * @param contactNumber Contact number
     * @param createdAt Creation date
     * @param lastLogin Last login date
     */
    public User(int id, String username, String password, String role, String status,
            String fullName, String email, String contactNumber, Date createdAt, Date lastLogin) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.status = status;
        this.fullName = fullName;
        this.email = email;
        this.contactNumber = contactNumber;
        this.createdAt = createdAt;
        this.lastLogin = lastLogin;
    }
    
    // Getters and Setters
    
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
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
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
    
    public Date getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    public Date getLastLogin() {
        return lastLogin;
    }
    
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    /**
     * Checks if the user is a super admin
     * 
     * @return true if super admin, false otherwise
     */
    public boolean isSuperAdmin() {
        return "SUPER_ADMIN".equals(role);
    }
    
    /**
     * Checks if the user is a treasurer
     * 
     * @return true if treasurer, false otherwise
     */
    public boolean isTreasurer() {
        return "TREASURER".equals(role);
    }
    
    /**
     * Checks if the user is a bookkeeper
     * 
     * @return true if bookkeeper, false otherwise
     */
    public boolean isBookkeeper() {
        return "BOOKKEEPER".equals(role);
    }
    
    /**
     * Checks if the user is active
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
}
