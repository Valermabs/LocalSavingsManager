package com.moscat.models;

import java.util.Date;

/**
 * Model for user permission data
 * Stores the relationship between a user and a permission
 */
public class UserPermission {
    
    private int id;
    private int userId;
    private int permissionId;
    private boolean active;
    private Date grantedDate;
    private Date expiryDate;
    private int grantedBy;
    private String notes;
    private Date createdAt;
    private Date updatedAt;
    
    // For caching permission details
    private transient Permission permission;
    
    /**
     * Default constructor
     */
    public UserPermission() {
    }
    
    /**
     * Constructor with basic info
     * 
     * @param userId User ID
     * @param permissionId Permission ID
     */
    public UserPermission(int userId, int permissionId) {
        this.userId = userId;
        this.permissionId = permissionId;
        this.active = true;
        this.grantedDate = new Date();
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
     * Gets the user ID
     * 
     * @return User ID
     */
    public int getUserId() {
        return userId;
    }
    
    /**
     * Sets the user ID
     * 
     * @param userId User ID
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    /**
     * Gets the permission ID
     * 
     * @return Permission ID
     */
    public int getPermissionId() {
        return permissionId;
    }
    
    /**
     * Sets the permission ID
     * 
     * @param permissionId Permission ID
     */
    public void setPermissionId(int permissionId) {
        this.permissionId = permissionId;
    }
    
    /**
     * Checks if the user permission is active
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
     * Gets the granted date
     * 
     * @return Granted date
     */
    public Date getGrantedDate() {
        return grantedDate;
    }
    
    /**
     * Sets the granted date
     * 
     * @param grantedDate Granted date
     */
    public void setGrantedDate(Date grantedDate) {
        this.grantedDate = grantedDate;
    }
    
    /**
     * Gets the expiry date
     * 
     * @return Expiry date
     */
    public Date getExpiryDate() {
        return expiryDate;
    }
    
    /**
     * Sets the expiry date
     * 
     * @param expiryDate Expiry date
     */
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    /**
     * Gets the ID of the user who granted this permission
     * 
     * @return Granted by user ID
     */
    public int getGrantedBy() {
        return grantedBy;
    }
    
    /**
     * Sets the ID of the user who granted this permission
     * 
     * @param grantedBy Granted by user ID
     */
    public void setGrantedBy(int grantedBy) {
        this.grantedBy = grantedBy;
    }
    
    /**
     * Gets the notes
     * 
     * @return Notes
     */
    public String getNotes() {
        return notes;
    }
    
    /**
     * Sets the notes
     * 
     * @param notes Notes
     */
    public void setNotes(String notes) {
        this.notes = notes;
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
    
    /**
     * Gets the updated at date
     * 
     * @return Updated at date
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the updated at date
     * 
     * @param updatedAt Updated at date
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Gets the permission associated with this user permission
     * 
     * @return Permission
     */
    public Permission getPermission() {
        return permission;
    }
    
    /**
     * Sets the permission associated with this user permission
     * 
     * @param permission Permission
     */
    public void setPermission(Permission permission) {
        this.permission = permission;
    }
    
    /**
     * Checks if this permission is expired
     * 
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        if (expiryDate == null) {
            return false;
        }
        return expiryDate.before(new Date());
    }
}