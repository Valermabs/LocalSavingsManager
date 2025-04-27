package com.moscat.models;

/**
 * Represents a summary of transactions by type
 */
public class TransactionSummary {
    private String transactionType;
    private double totalAmount;
    private int count;
    
    // Default constructor
    public TransactionSummary() {
        this.totalAmount = 0.0;
        this.count = 0;
    }
    
    // Constructor with transaction type
    public TransactionSummary(String transactionType) {
        this.transactionType = transactionType;
        this.totalAmount = 0.0;
        this.count = 0;
    }
    
    // Constructor with all fields
    public TransactionSummary(String transactionType, double totalAmount, int count) {
        this.transactionType = transactionType;
        this.totalAmount = totalAmount;
        this.count = count;
    }
    
    // Getters and setters
    public String getTransactionType() {
        return transactionType;
    }
    
    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public int getCount() {
        return count;
    }
    
    public void setCount(int count) {
        this.count = count;
    }
    
    /**
     * Adds a transaction amount to the summary
     * 
     * @param amount The amount to add
     */
    public void addTransaction(double amount) {
        this.totalAmount += amount;
        this.count++;
    }
    
    /**
     * Convenience method to add a deposit transaction
     * 
     * @param amount The amount to add
     */
    public void addDeposit(double amount) {
        setTransactionType("SAVINGS_DEPOSIT");
        addTransaction(amount);
    }
    
    /**
     * Convenience method to add a withdrawal transaction
     * 
     * @param amount The amount to add
     */
    public void addWithdrawal(double amount) {
        setTransactionType("SAVINGS_WITHDRAWAL");
        addTransaction(amount);
    }
    
    /**
     * Convenience method to add an interest transaction
     * 
     * @param amount The amount to add
     */
    public void addInterest(double amount) {
        setTransactionType("INTEREST_EARNED");
        addTransaction(amount);
    }
    
    /**
     * Convenience method to add a loan disbursement transaction
     * 
     * @param amount The amount to add
     */
    public void addLoan(double amount) {
        setTransactionType("LOAN_DISBURSEMENT");
        addTransaction(amount);
    }
    
    /**
     * Convenience method to add a loan payment transaction
     * 
     * @param amount The amount to add
     */
    public void addLoanPayment(double amount) {
        setTransactionType("LOAN_PAYMENT");
        addTransaction(amount);
    }
    
    /**
     * Gets the average transaction amount
     * 
     * @return The average amount per transaction
     */
    public double getAverageAmount() {
        if (count == 0) {
            return 0.0;
        }
        return totalAmount / count;
    }
    
    /**
     * Gets a display name for the transaction type
     * 
     * @return The formatted transaction type display
     */
    public String getTransactionTypeDisplay() {
        if (transactionType == null) {
            return "Unknown";
        }
        
        switch (transactionType) {
            case "SAVINGS_DEPOSIT":
                return "Savings Deposit";
            case "SAVINGS_WITHDRAWAL":
                return "Savings Withdrawal";
            case "LOAN_PAYMENT":
                return "Loan Payment";
            case "LOAN_DISBURSEMENT":
                return "Loan Disbursement";
            case "INTEREST_EARNED":
                return "Interest Earned";
            case "SERVICE_FEE":
                return "Service Fee";
            case "MEMBERSHIP_FEE":
                return "Membership Fee";
            case "PENALTY":
                return "Penalty Payment";
            default:
                return transactionType;
        }
    }
}