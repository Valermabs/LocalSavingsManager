package com.moscat.models;

/**
 * Model class for loan deductions
 */
public class LoanDeduction {
    private int id;
    private int loanId;
    private String name;
    private double amount;
    private double percentage;
    private boolean isRLPF;
    
    /**
     * Default constructor
     */
    public LoanDeduction() {
    }
    
    /**
     * Parameterized constructor
     * 
     * @param id Deduction ID
     * @param loanId Loan ID
     * @param name Deduction name
     * @param amount Deduction amount
     * @param percentage Percentage of loan amount
     * @param isRLPF Whether the deduction is RLPF
     */
    public LoanDeduction(int id, int loanId, String name, double amount, double percentage, boolean isRLPF) {
        this.id = id;
        this.loanId = loanId;
        this.name = name;
        this.amount = amount;
        this.percentage = percentage;
        this.isRLPF = isRLPF;
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getLoanId() {
        return loanId;
    }
    
    public void setLoanId(int loanId) {
        this.loanId = loanId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public double getPercentage() {
        return percentage;
    }
    
    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
    
    public boolean isRLPF() {
        return isRLPF;
    }
    
    public void setRLPF(boolean isRLPF) {
        this.isRLPF = isRLPF;
    }
    
    /**
     * Calculates the deduction amount based on the loan principal and term
     * 
     * @param loanPrincipal Loan principal amount
     * @param termMonths Loan term in months
     */
    public void calculateAmount(double loanPrincipal, int termMonths) {
        if (isRLPF) {
            // RLPF = (Principal / 1000) * 1 * termMonths
            this.amount = (loanPrincipal / 1000) * 1 * termMonths;
        } else if (percentage > 0) {
            // Percentage-based deduction
            this.amount = loanPrincipal * (percentage / 100);
        }
        // Otherwise amount is already set
    }
}
