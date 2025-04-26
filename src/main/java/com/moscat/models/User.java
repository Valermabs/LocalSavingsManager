package com.moscat.models;

import com.moscat.utils.Constants;
import java.util.Date;

/**
 * Model for user data
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
    private Date lastLogin;
    private boolean active;
    private Date createdAt;
    
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
     * Gets the password hash
     * 
     * @return Password hash
     */
    public String getPassword() {
        return password;
    }
    
    /**
     * Sets the password hash
     * 
     * @param password Password hash
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
     * Checks if the user is active
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return active;
    }
    
    /**
     * Sets the active status
     * 
     * @param active Active status
     */
    public void setActive(boolean active) {
        this.active = active;
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
        return firstName + " " + lastName;
    }
    
    /**
     * Sets the full name by splitting it into first and last name
     * 
     * @param fullName Full name
     */
    public void setFullName(String fullName) {
        if (fullName != null && !fullName.isEmpty()) {
            String[] nameParts = fullName.trim().split("\\s+");
            if (nameParts.length > 0) {
                this.firstName = nameParts[0];
                
                if (nameParts.length > 1) {
                    // Last name is everything after the first name
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < nameParts.length; i++) {
                        if (i > 1) {
                            sb.append(" ");
                        }
                        sb.append(nameParts[i]);
                    }
                    this.lastName = sb.toString();
                } else {
                    this.lastName = "";
                }
            }
        }
    }
    
    /**
     * Checks if the user is a super admin
     * 
     * @return true if super admin, false otherwise
     */
    public boolean isSuperAdmin() {
        return Constants.ROLE_SUPER_ADMIN.equals(role);
    }
    
    /**
     * Gets the created at date
     * 
     * @return Created at date
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the created at date
     * 
     * @param createdAt Created at date
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}