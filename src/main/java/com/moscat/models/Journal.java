package com.moscat.models;

import java.time.LocalDateTime;

/**
 * Represents a monthly journal record
 */
public class Journal {
    private int id;
    private String month; // In format YYYY-MM
    private String transactionsSummary;
    private String remarks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructor
    public Journal() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getMonth() {
        return month;
    }
    
    public void setMonth(String month) {
        this.month = month;
    }
    
    public String getTransactionsSummary() {
        return transactionsSummary;
    }
    
    public void setTransactionsSummary(String transactionsSummary) {
        this.transactionsSummary = transactionsSummary;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getRemarks() {
        return remarks;
    }
    
    public void setRemarks(String remarks) {
        this.remarks = remarks;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Extract the year from the month string
     * 
     * @return The year
     */
    public int getYear() {
        if (month != null && month.length() >= 4) {
            try {
                return Integer.parseInt(month.substring(0, 4));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Extract the month number from the month string
     * 
     * @return The month number (1-12)
     */
    public int getMonthNumber() {
        if (month != null && month.length() >= 7) {
            try {
                return Integer.parseInt(month.substring(5, 7));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Get the month name
     * 
     * @return The month name
     */
    public String getMonthName() {
        int monthNum = getMonthNumber();
        switch (monthNum) {
            case 1: return "January";
            case 2: return "February";
            case 3: return "March";
            case 4: return "April";
            case 5: return "May";
            case 6: return "June";
            case 7: return "July";
            case 8: return "August";
            case 9: return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: return "Unknown";
        }
    }
}