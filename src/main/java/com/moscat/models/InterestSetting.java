package com.moscat.models;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an interest rate setting configuration
 */
public class InterestSetting {
    private int id;
    private double interestRate;
    private double minimumBalanceRequired;
    private String computationBasis;
    private LocalDate effectiveDate;
    private String reasonForChange;
    private String setBy;
    private LocalDateTime createdAt;
    
    // Constructor
    public InterestSetting() {
        this.createdAt = LocalDateTime.now();
        this.effectiveDate = LocalDate.now();
    }
    
    // Getters and setters
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
    
    public double getMinimumBalanceRequired() {
        return minimumBalanceRequired;
    }
    
    public void setMinimumBalanceRequired(double minimumBalanceRequired) {
        this.minimumBalanceRequired = minimumBalanceRequired;
    }
    
    public String getComputationBasis() {
        return computationBasis;
    }
    
    public void setComputationBasis(String computationBasis) {
        this.computationBasis = computationBasis;
    }
    
    public LocalDate getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(LocalDate effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public String getReasonForChange() {
        return reasonForChange;
    }
    
    public void setReasonForChange(String reasonForChange) {
        this.reasonForChange = reasonForChange;
    }
    
    public String getSetBy() {
        return setBy;
    }
    
    public void setSetBy(String setBy) {
        this.setBy = setBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Calculate daily interest factor
     * 
     * @return The daily interest factor
     */
    public double getDailyInterestFactor() {
        return interestRate / 365.0;
    }
    
    /**
     * Calculate monthly interest factor
     * 
     * @return The monthly interest factor
     */
    public double getMonthlyInterestFactor() {
        return interestRate / 12.0;
    }
    
    /**
     * Check if this interest setting is currently effective
     * 
     * @return True if effective now, false otherwise
     */
    public boolean isEffectiveNow() {
        return !LocalDate.now().isBefore(effectiveDate);
    }
    
    /**
     * Check if a member's balance qualifies for interest
     * 
     * @param balance The member's balance
     * @return True if the balance qualifies for interest, false otherwise
     */
    public boolean qualifiesForInterest(double balance) {
        return balance >= minimumBalanceRequired;
    }
    
    /**
     * Calculate interest for a given balance
     * 
     * @param balance The balance to calculate interest on
     * @return The calculated interest amount
     */
    public double calculateInterest(double balance) {
        if (!qualifiesForInterest(balance)) {
            return 0.0;
        }
        
        if ("Daily".equals(computationBasis)) {
            return balance * (interestRate / 100.0 / 365.0);
        } else { // Monthly
            return balance * (interestRate / 100.0 / 12.0);
        }
    }
}