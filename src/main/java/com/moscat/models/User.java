package com.moscat.models;

import com.moscat.utils.Constants;
import java.util.Date;

/**
 * Model class for User
 */
public class User {
    
    private int id;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String email;
    private String contactNumber;
    private String role;
    private String status;
    private boolean active;
    private Date lastLogin;
    private Date createdAt;
    
    /**
     * Default constructor
     */
    public User() {
        // Default constructor
    }
    
    /**
     * Gets the user ID
     * 
     * @return User ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the user ID
     * 
     * @param id User ID
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
     * Gets the password (hashed)
     * 
     * @return Password
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password (hashed)
     * 
     * @param password Password
     */
    public void setPassword(String password) {
        this.password = password;
    }
    
    /**
     * Gets the first name
     * 
     * @return First name
     */
    public String getFirstName() {
        return firstName;
    }
    
    /**
     * Sets the first name
     * 
     * @param firstName First name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Gets the last name
     * 
     * @return Last name
     */
    public String getLastName() {
        return lastName;
    }
    
    /**
     * Sets the last name
     * 
     * @param lastName Last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    /**
     * Gets the full name (first name + last name)
     * 
     * @return Full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Sets the full name by splitting it into first and last name
     * 
     * @param fullName Full name in "FirstName LastName" format
     */
    public void setFullName(String fullName) {
        if (fullName != null && !fullName.isEmpty()) {
            String[] parts = fullName.split(" ", 2);
            if (parts.length > 0) {
                this.firstName = parts[0];
                
                if (parts.length > 1) {
                    this.lastName = parts[1];
                } else {
                    this.lastName = ""; // Set empty last name if not provided
                }
            }
        }
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
     * Checks if user is active
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets whether user is active
     * 
     * @param active Active status
     */
    public void setActive(boolean active) {
        this.active = active;
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
     * Checks if user is a SuperAdmin
     * 
     * @return true if SuperAdmin, false otherwise
     */
    public boolean isSuperAdmin() {
        return role != null && role.equals(Constants.ROLE_SUPER_ADMIN);
    }
    
    /**
     * Checks if user is a Treasurer
     * 
     * @return true if Treasurer, false otherwise
     */
    public boolean isTreasurer() {
        return role != null && role.equals(Constants.ROLE_TREASURER);
    }
    
    /**
     * Checks if user is a Bookkeeper
     * 
     * @return true if Bookkeeper, false otherwise
     */
    public boolean isBookkeeper() {
        return role != null && role.equals(Constants.ROLE_BOOKKEEPER);
    }
    
    @Override
    public String toString() {
        return getFullName() + " (" + username + " - " + role + ")";
    }
}