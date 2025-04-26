package com.moscat.models;

import java.util.Date;

/**
 * Model for savings account interest settings
 */
public class InterestSettings {
    
    private int id;
    private double interestRate;
    private double minimumBalance;
    private String calculationMethod;
    private Date effectiveDate;
    private String changeBasis;
    private String status;
    private Date createdAt;
    
    /**
     * Default constructor
     */
    public InterestSettings() {
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
     * Gets the interest rate
     * 
     * @return Interest rate
     */
    public double getInterestRate() {
        return interestRate;
    }
    
    /**
     * Sets the interest rate
     * 
     * @param interestRate Interest rate
     */
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
    
    /**
     * Gets the minimum balance
     * 
     * @return Minimum balance
     */
    public double getMinimumBalance() {
        return minimumBalance;
    }
    
    /**
     * Sets the minimum balance
     * 
     * @param minimumBalance Minimum balance
     */
    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }
    
    /**
     * Gets the calculation method
     * 
     * @return Calculation method
     */
    public String getCalculationMethod() {
        return calculationMethod;
    }
    
    /**
     * Sets the calculation method
     * 
     * @param calculationMethod Calculation method
     */
    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }
    
    /**
     * Gets the effective date
     * 
     * @return Effective date
     */
    public Date getEffectiveDate() {
        return effectiveDate;
    }
    
    /**
     * Sets the effective date
     * 
     * @param effectiveDate Effective date
     */
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    /**
     * Gets the change basis
     * 
     * @return Change basis
     */
    public String getChangeBasis() {
        return changeBasis;
    }
    
    /**
     * Sets the change basis
     * 
     * @param changeBasis Change basis
     */
    public void setChangeBasis(String changeBasis) {
        this.changeBasis = changeBasis;
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