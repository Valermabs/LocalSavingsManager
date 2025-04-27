package com.moscat.models;

import java.time.LocalDateTime;

/**
 * Represents a financial transaction in the system
 */
public class Transaction {
    private int id;
    private int memberId;
    private int accountId; // Savings account ID
    private String referenceNumber; // Unique reference number for tracking
    private String transactionType;
    private double amount;
    private double runningBalance; // Balance after transaction
    private LocalDateTime transactionDate;
    private String description;
    private String processedBy;
    private int transactionBy; // User ID of person making the transaction
    
    // Constructor
    public Transaction() {
        this.transactionDate = LocalDateTime.now();
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
    
    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }
    
    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getProcessedBy() {
        return processedBy;
    }
    
    public void setProcessedBy(String processedBy) {
        this.processedBy = processedBy;
    }
    
    public int getAccountId() {
        return accountId;
    }
    
    public void setAccountId(int accountId) {
        this.accountId = accountId;
    }
    
    public String getReferenceNumber() {
        return referenceNumber;
    }
    
    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
    
    public double getRunningBalance() {
        return runningBalance;
    }
    
    public void setRunningBalance(double runningBalance) {
        this.runningBalance = runningBalance;
    }
    
    public int getTransactionBy() {
        return transactionBy;
    }
    
    public void setTransactionBy(int transactionBy) {
        this.transactionBy = transactionBy;
    }
    
    /**
     * Check if this is a deposit transaction
     * 
     * @return True if deposit, false otherwise
     */
    public boolean isDeposit() {
        return "Deposit".equals(transactionType);
    }
    
    /**
     * Check if this is a withdrawal transaction
     * 
     * @return True if withdrawal, false otherwise
     */
    public boolean isWithdrawal() {
        return "Withdrawal".equals(transactionType);
    }
    
    /**
     * Check if this is an interest transaction
     * 
     * @return True if interest, false otherwise
     */
    public boolean isInterest() {
        return "Interest".equals(transactionType);
    }
    
    /**
     * Check if this is a loan-related transaction
     * 
     * @return True if loan-related, false otherwise
     */
    public boolean isLoanRelated() {
        return "Loan Release".equals(transactionType) || "Loan Payment".equals(transactionType);
    }
}