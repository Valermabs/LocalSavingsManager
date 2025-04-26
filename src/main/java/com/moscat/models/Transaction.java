package com.moscat.models;

import java.util.Date;

/**
 * Model for transaction data
 */
public class Transaction {
    
    private int id;
    private String transactionId;
    private int accountId;
    private String transactionType;
    private double amount;
    private Date transactionDate;
    private String description;
    private double balanceAfter;
    private int performedById;
    private String performedByUsername;
    private Date createdAt;
    
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
     * Gets the transaction ID
     * 
     * @return Transaction ID
     */
    public String getTransactionId() {
        return transactionId;
    }
    
    /**
     * Sets the transaction ID
     * 
     * @param transactionId Transaction ID
     */
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
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
     * Gets the balance after transaction
     * 
     * @return Balance after transaction
     */
    public double getBalanceAfter() {
        return balanceAfter;
    }
    
    /**
     * Sets the balance after transaction
     * 
     * @param balanceAfter Balance after transaction
     */
    public void setBalanceAfter(double balanceAfter) {
        this.balanceAfter = balanceAfter;
    }
    
    /**
     * Gets the performed by user ID
     * 
     * @return Performed by user ID
     */
    public int getPerformedById() {
        return performedById;
    }
    
    /**
     * Sets the performed by user ID
     * 
     * @param performedById Performed by user ID
     */
    public void setPerformedById(int performedById) {
        this.performedById = performedById;
    }
    
    /**
     * Gets the performed by username
     * 
     * @return Performed by username
     */
    public String getPerformedByUsername() {
        return performedByUsername;
    }
    
    /**
     * Sets the performed by username
     * 
     * @param performedByUsername Performed by username
     */
    public void setPerformedByUsername(String performedByUsername) {
        this.performedByUsername = performedByUsername;
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