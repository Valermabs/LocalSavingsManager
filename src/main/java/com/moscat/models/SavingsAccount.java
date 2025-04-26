package com.moscat.models;

import java.util.Date;

/**
 * Model for savings account data
 */
public class SavingsAccount {
    
    private int id;
    private String accountNumber;
    private int memberId;
    private String accountType;
    private double balance;
    private double interestRate;
    private String status;
    private Date openedDate;
    private Date lastTransactionDate;
    private Date createdAt;
    private Date updatedAt;
    
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
     * Gets the account type
     * 
     * @return Account type
     */
    public String getAccountType() {
        return accountType;
    }
    
    /**
     * Sets the account type
     * 
     * @param accountType Account type
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
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
     * Gets the opened date
     * 
     * @return Opened date
     */
    public Date getOpenedDate() {
        return openedDate;
    }
    
    /**
     * Sets the opened date
     * 
     * @param openedDate Opened date
     */
    public void setOpenedDate(Date openedDate) {
        this.openedDate = openedDate;
    }
    
    /**
     * Gets the last transaction date
     * 
     * @return Last transaction date
     */
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }
    
    /**
     * Sets the last transaction date
     * 
     * @param lastTransactionDate Last transaction date
     */
    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
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
}