package com.moscat.models;

import java.util.Date;

/**
 * Model for savings account data
 */
public class SavingsAccount {
    
    private int id;
    private int memberId;
    private String accountNumber;
    private double balance;
    private double interestEarned;
    private String status;
    private Date openDate;
    private Date lastActivityDate;
    
    /**
     * Default constructor
     */
    public SavingsAccount() {
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
     * Gets the member ID
     * 
     * @return Member ID
     */
    public int getMemberId() {
        return memberId;
    }
    
    /**
     * Sets the member ID
     * 
     * @param memberId Member ID
     */
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    /**
     * Gets the account number
     * 
     * @return Account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }
    
    /**
     * Sets the account number
     * 
     * @param accountNumber Account number
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    /**
     * Gets the balance
     * 
     * @return Balance
     */
    public double getBalance() {
        return balance;
    }
    
    /**
     * Sets the balance
     * 
     * @param balance Balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    /**
     * Gets the interest earned
     * 
     * @return Interest earned
     */
    public double getInterestEarned() {
        return interestEarned;
    }
    
    /**
     * Sets the interest earned
     * 
     * @param interestEarned Interest earned
     */
    public void setInterestEarned(double interestEarned) {
        this.interestEarned = interestEarned;
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
     * Gets the open date
     * 
     * @return Open date
     */
    public Date getOpenDate() {
        return openDate;
    }
    
    /**
     * Sets the open date
     * 
     * @param openDate Open date
     */
    public void setOpenDate(Date openDate) {
        this.openDate = openDate;
    }
    
    /**
     * Gets the last activity date
     * 
     * @return Last activity date
     */
    public Date getLastActivityDate() {
        return lastActivityDate;
    }
    
    /**
     * Sets the last activity date
     * 
     * @param lastActivityDate Last activity date
     */
    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
    
    /**
     * Gets the total balance (balance + interest earned)
     * 
     * @return Total balance
     */
    public double getTotalBalance() {
        return balance + interestEarned;
    }
}