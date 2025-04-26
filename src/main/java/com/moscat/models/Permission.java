package com.moscat.models;

import java.util.Date;

/**
 * Model for permission data
 */
public class Permission {
    
    private int id;
    private String name;
    private String code;
    private String description;
    private String module;
    private boolean active;
    private Date createdAt;
    private Date updatedAt;
    
    /**
     * Default constructor
     */
    public Permission() {
        this.active = true;
    }
    
    /**
     * Constructor with basic info
     * 
     * @param name Name
     * @param code Code
     * @param description Description
     * @param module Module
     */
    public Permission(String name, String code, String description, String module) {
        this.name = name;
        this.code = code;
        this.description = description;
        this.module = module;
        this.active = true;
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
     * Gets the name
     * 
     * @return Name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name
     * 
     * @param name Name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the code
     * 
     * @return Code
     */
    public String getCode() {
        return code;
    }
    
    /**
     * Sets the code
     * 
     * @param code Code
     */
    public void setCode(String code) {
        this.code = code;
    }
    
    /**
     * Gets the description
     * 
     * @return Description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description
     * 
     * @param description Description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the module
     * 
     * @return Module
     */
    public String getModule() {
        return module;
    }
    
    /**
     * Sets the module
     * 
     * @param module Module
     */
    public void setModule(String module) {
        this.module = module;
    }
    
    /**
     * Checks if the permission is active
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
     * Returns a string representation of the permission
     * 
     * @return String representation
     */
    @Override
    public String toString() {
        return name + " (" + code + ")";
    }
}