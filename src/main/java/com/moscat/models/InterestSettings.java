package com.moscat.models;

import java.sql.Date;

/**
 * Model class for savings interest settings
 */
public class InterestSettings {
    private int id;
    private double interestRate;
    private double minimumBalanceForInterest;
    private String computationMethod; // DAILY, MONTHLY
    private Date effectiveDate;
    private String changeReason;
    private int createdBy; // User ID who created the setting
    
    /**
     * Default constructor
     */
    public InterestSettings() {
    }
    
    /**
     * Parameterized constructor
     * 
     * @param id Settings ID
     * @param interestRate Interest rate percentage
     * @param minimumBalanceForInterest Minimum balance required to earn interest
     * @param computationMethod Computation method (DAILY, MONTHLY)
     * @param effectiveDate Date when the settings take effect
     * @param changeReason Reason for the change
     * @param createdBy User ID who created the setting
     */
    public InterestSettings(int id, double interestRate, double minimumBalanceForInterest,
            String computationMethod, Date effectiveDate, String changeReason, int createdBy) {
        this.id = id;
        this.interestRate = interestRate;
        this.minimumBalanceForInterest = minimumBalanceForInterest;
        this.computationMethod = computationMethod;
        this.effectiveDate = effectiveDate;
        this.changeReason = changeReason;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public double getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
    
    public double getMinimumBalanceForInterest() {
        return minimumBalanceForInterest;
    }
    
    public void setMinimumBalanceForInterest(double minimumBalanceForInterest) {
        this.minimumBalanceForInterest = minimumBalanceForInterest;
    }
    
    public String getComputationMethod() {
        return computationMethod;
    }
    
    public void setComputationMethod(String computationMethod) {
        this.computationMethod = computationMethod;
    }
    
    public Date getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public String getChangeReason() {
        return changeReason;
    }
    
    public void setChangeReason(String changeReason) {
        this.changeReason = changeReason;
    }
    
    public int getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    /**
     * Calculates the daily interest rate
     * 
     * @param year Year for days calculation
     * @return Daily interest rate
     */
    public double getDailyInterestRate(int year) {
        int daysInYear = (year % 4 == 0 && (year % 100 != 0 || year % 400 == 0)) ? 366 : 365;
        return interestRate / daysInYear;
    }
    
    /**
     * Calculates the monthly interest rate
     * 
     * @return Monthly interest rate
     */
    public double getMonthlyInterestRate() {
        return interestRate / 12;
    }
    
    /**
     * Checks if the computation method is daily
     * 
     * @return true if daily, false otherwise
     */
    public boolean isDailyComputation() {
        return "DAILY".equals(computationMethod);
    }
    
    /**
     * Checks if the computation method is monthly
     * 
     * @return true if monthly, false otherwise
     */
    public boolean isMonthlyComputation() {
        return "MONTHLY".equals(computationMethod);
    }
}
