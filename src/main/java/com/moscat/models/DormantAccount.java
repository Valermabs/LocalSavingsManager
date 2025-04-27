package com.moscat.models;

import java.time.LocalDateTime;

/**
 * Represents a dormant account in the system
 */
public class DormantAccount {
    private int id;
    private int memberId;
    private LocalDateTime lastTransactionDate;
    private LocalDateTime dormantSince;
    private String dormantStatus;
    private boolean notificationSent;
    
    // Constructor
    public DormantAccount() {
        this.dormantSince = LocalDateTime.now();
        this.dormantStatus = "Dormant";
        this.notificationSent = false;
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getMemberId() {
        return memberId;
    }
    
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    public LocalDateTime getLastTransactionDate() {
        return lastTransactionDate;
    }
    
    public void setLastTransactionDate(LocalDateTime lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }
    
    public LocalDateTime getDormantSince() {
        return dormantSince;
    }
    
    public void setDormantSince(LocalDateTime dormantSince) {
        this.dormantSince = dormantSince;
    }
    
    public String getDormantStatus() {
        return dormantStatus;
    }
    
    public void setDormantStatus(String dormantStatus) {
        this.dormantStatus = dormantStatus;
    }
    
    public boolean isNotificationSent() {
        return notificationSent;
    }
    
    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }
    
    /**
     * Calculate the number of days the account has been dormant
     * 
     * @return The number of days the account has been dormant
     */
    public long getDaysDormant() {
        return java.time.Duration.between(dormantSince, LocalDateTime.now()).toDays();
    }
    
    /**
     * Calculate the number of months the account has been dormant
     * 
     * @return The number of months the account has been dormant
     */
    public long getMonthsDormant() {
        return java.time.Period.between(dormantSince.toLocalDate(), LocalDateTime.now().toLocalDate()).toTotalMonths();
    }
    
    /**
     * Reactivate the dormant account
     */
    public void reactivate() {
        this.dormantStatus = "Reactivated";
    }
}