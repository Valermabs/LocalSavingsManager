package com.moscat.models;

import java.util.Date;

/**
 * Model for user data
 */
public class User {
    
    private int id;
    private String username;
    private String role;
    private String status;
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
     * Gets the ID
     * 
     * @return ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the ID
     * 
     * @param id ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets the username
     * 
     * @return Username
     */
    public String getUsername() {
        return username;
    }
    
    /**
     * Sets the username
     * 
     * @param username Username
     */
    public void setUsername(String username) {
        this.username = username;
    }
    
    /**
     * Gets the role
     * 
     * @return Role
     */
    public String getRole() {
        return role;
    }
    
    /**
     * Sets the role
     * 
     * @param role Role
     */
    public void setRole(String role) {
        this.role = role;
    }
    
    /**
     * Gets the status
     * 
     * @return Status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the status
     * 
     * @param status Status
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Gets the full name
     * 
     * @return Full name
     */
    public String getFullName() {
        return fullName;
    }
    
    /**
     * Sets the full name
     * 
     * @param fullName Full name
     */
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    /**
     * Gets the email
     * 
     * @return Email
     */
    public String getEmail() {
        return email;
    }
    
    /**
     * Sets the email
     * 
     * @param email Email
     */
    public void setEmail(String email) {
        this.email = email;
    }
    
    /**
     * Gets the contact number
     * 
     * @return Contact number
     */
    public String getContactNumber() {
        return contactNumber;
    }
    
    /**
     * Sets the contact number
     * 
     * @param contactNumber Contact number
     */
    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
    
    /**
     * Gets the creation date
     * 
     * @return Creation date
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the creation date
     * 
     * @param createdAt Creation date
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the last login date
     * 
     * @return Last login date
     */
    public Date getLastLogin() {
        return lastLogin;
    }
    
    /**
     * Sets the last login date
     * 
     * @param lastLogin Last login date
     */
    public void setLastLogin(Date lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    /**
     * Checks if the user is a super admin
     * 
     * @return true if the user is a super admin, false otherwise
     */
    public boolean isSuperAdmin() {
        return role != null && role.equals(com.moscat.utils.Constants.ROLE_SUPER_ADMIN);
    }
    
    /**
     * Checks if the user is a treasurer
     * 
     * @return true if the user is a treasurer, false otherwise
     */
    public boolean isTreasurer() {
        return role != null && role.equals(com.moscat.utils.Constants.ROLE_TREASURER);
    }
    
    /**
     * Checks if the user is a bookkeeper
     * 
     * @return true if the user is a bookkeeper, false otherwise
     */
    public boolean isBookkeeper() {
        return role != null && role.equals(com.moscat.utils.Constants.ROLE_BOOKKEEPER);
    }
}