package com.moscat.models;

import java.sql.Date;

/**
 * Model class for savings accounts
 */
public class SavingsAccount {
    private int id;
    private int memberId;
    private String accountNumber;
    private double balance;
    private double interestEarned;
    private Date lastInterestComputationDate;
    private String status; // ACTIVE, DORMANT, CLOSED
    private Date openDate;
    private Date lastActivityDate;
    
    /**
     * Default constructor
     */
    public SavingsAccount() {
    }
    
    /**
     * Parameterized constructor
     * 
     * @param id Account ID
     * @param memberId Member ID
     * @param accountNumber Account number
     * @param balance Current balance
     * @param interestEarned Total interest earned
     * @param lastInterestComputationDate Date of last interest computation
     * @param status Account status
     * @param openDate Account opening date
     * @param lastActivityDate Date of last activity
     */
    public SavingsAccount(int id, int memberId, String accountNumber, double balance, 
            double interestEarned, Date lastInterestComputationDate, String status, 
            Date openDate, Date lastActivityDate) {
        this.id = id;
        this.memberId = memberId;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.interestEarned = interestEarned;
        this.lastInterestComputationDate = lastInterestComputationDate;
        this.status = status;
        this.openDate = openDate;
        this.lastActivityDate = lastActivityDate;
    }
    
    // Getters and Setters
    
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
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public double getInterestEarned() {
        return interestEarned;
    }
    
    public void setInterestEarned(double interestEarned) {
        this.interestEarned = interestEarned;
    }
    
    public Date getLastInterestComputationDate() {
        return lastInterestComputationDate;
    }
    
    public void setLastInterestComputationDate(Date lastInterestComputationDate) {
        this.lastInterestComputationDate = lastInterestComputationDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getOpenDate() {
        return openDate;
    }
    
    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }
    
    public Date getLastActivityDate() {
        return lastActivityDate;
    }
    
    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
    
    /**
     * Gets the total balance including earned interest
     * 
     * @return Total balance with interest
     */
    public double getTotalBalance() {
        return balance + interestEarned;
    }
    
    /**
     * Checks if the account is active
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    /**
     * Checks if the account is dormant
     * 
     * @return true if dormant, false otherwise
     */
    public boolean isDormant() {
        return "DORMANT".equals(status);
    }
    
    /**
     * Checks if the account is closed
     * 
     * @return true if closed, false otherwise
     */
    public boolean isClosed() {
        return "CLOSED".equals(status);
    }
}
