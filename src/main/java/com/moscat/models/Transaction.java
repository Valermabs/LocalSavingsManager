package com.moscat.models;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Model class for transactions
 */
public class Transaction {
    private int id;
    private int accountId;
    private String transactionType; // DEPOSIT, WITHDRAWAL, LOAN_PAYMENT, LOAN_RELEASE, INTEREST_EARNING
    private double amount;
    private double runningBalance;
    private String referenceNumber;
    private String description;
    private int userId; // User who processed the transaction
    private Timestamp transactionDate;
    
    /**
     * Default constructor
     */
    public Transaction() {
    }
    
    /**
     * Parameterized constructor
     * 
     * @param id Transaction ID
     * @param accountId Savings account ID
     * @param transactionType Transaction type
     * @param amount Transaction amount
     * @param runningBalance Running balance after transaction
     * @param referenceNumber Reference number
     * @param description Transaction description
     * @param userId User ID who processed the transaction
     * @param transactionDate Transaction date and time
     */
    public Transaction(int id, int accountId, String transactionType, double amount, 
            double runningBalance, String referenceNumber, String description, 
            int userId, Timestamp transactionDate) {
        this.id = id;
        this.accountId = accountId;
        this.transactionType = transactionType;
        this.amount = amount;
        this.runningBalance = runningBalance;
        this.referenceNumber = referenceNumber;
        this.description = description;
        this.userId = userId;
        this.transactionDate = transactionDate;
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getAccountId() {
        return accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    public String getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public double getRunningBalance() {
        return runningBalance;
    }
    
    public void setRunningBalance(double runningBalance) {
        this.runningBalance = runningBalance;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public Timestamp getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(Timestamp transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    /**
     * Checks if the transaction is a deposit
     * 
     * @return true if deposit, false otherwise
     */
    public boolean isDeposit() {
        return "DEPOSIT".equals(transactionType);
    }
    
    /**
     * Checks if the transaction is a withdrawal
     * 
     * @return true if withdrawal, false otherwise
     */
    public boolean isWithdrawal() {
        return "WITHDRAWAL".equals(transactionType);
    }
    
    /**
     * Checks if the transaction is a loan payment
     * 
     * @return true if loan payment, false otherwise
     */
    public boolean isLoanPayment() {
        return "LOAN_PAYMENT".equals(transactionType);
    }
    
    /**
     * Checks if the transaction is a loan release
     * 
     * @return true if loan release, false otherwise
     */
    public boolean isLoanRelease() {
        return "LOAN_RELEASE".equals(transactionType);
    }
    
    /**
     * Checks if the transaction is an interest earning
     * 
     * @return true if interest earning, false otherwise
     */
    public boolean isInterestEarning() {
        return "INTEREST_EARNING".equals(transactionType);
    }
}
