package com.moscat.models;

import java.util.Date;

/**
 * Model for transaction data
 */
public class Transaction {
    
    private int id;
    private int accountId;
    private int userId;
    private String transactionType;
    private double amount;
    private double runningBalance;
    private String description;
    private String referenceNumber;
    private Date transactionDate;
    
    /**
     * Default constructor
     */
    public Transaction() {
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
     * Gets the account ID
     * 
     * @return Account ID
     */
    public int getAccountId() {
        return accountId;
    }
    
    /**
     * Sets the account ID
     * 
     * @param accountId Account ID
     */
    public void setAccountId(int accountId) {
        this.accountId = accountId;
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
     * Gets the transaction type
     * 
     * @return Transaction type
     */
    public String getTransactionType() {
        return transactionType;
    }
    
    /**
     * Sets the transaction type
     * 
     * @param transactionType Transaction type
     */
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    /**
     * Gets the amount
     * 
     * @return Amount
     */
    public double getAmount() {
        return amount;
    }
    
    /**
     * Sets the amount
     * 
     * @param amount Amount
     */
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    /**
     * Gets the running balance
     * 
     * @return Running balance
     */
    public double getRunningBalance() {
        return runningBalance;
    }
    
    /**
     * Sets the running balance
     * 
     * @param runningBalance Running balance
     */
    public void setRunningBalance(double runningBalance) {
        this.runningBalance = runningBalance;
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
     * Gets the reference number
     * 
     * @return Reference number
     */
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    /**
     * Sets the reference number
     * 
     * @param referenceNumber Reference number
     */
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    /**
     * Gets the transaction date
     * 
     * @return Transaction date
     */
    public Date getTransactionDate() {
        return transactionDate;
    }
    
    /**
     * Sets the transaction date
     * 
     * @param transactionDate Transaction date
     */
    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }
}