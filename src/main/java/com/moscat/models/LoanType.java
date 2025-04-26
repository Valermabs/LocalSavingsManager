package com.moscat.models;

/**
 * Model class for loan types
 */
public class LoanType {
    private int id;
    private String code;
    private String name;
    private String description;
    private double interestRate;
    private int minTermMonths;
    private int maxTermMonths;
    private double minAmount;
    private double maxAmount;
    private boolean requiresRLPF;
    
    /**
     * Default constructor
     */
    public LoanType() {
    }
    
    /**
     * Parameterized constructor
     * 
     * @param id Loan type ID
     * @param code Loan type code
     * @param name Loan type name
     * @param description Loan type description
     * @param interestRate Default interest rate
     * @param minTermMonths Minimum term in months
     * @param maxTermMonths Maximum term in months
     * @param minAmount Minimum loan amount
     * @param maxAmount Maximum loan amount
     * @param requiresRLPF Whether RLPF is required
     */
    public LoanType(int id, String code, String name, String description, double interestRate,
            int minTermMonths, int maxTermMonths, double minAmount, double maxAmount, boolean requiresRLPF) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.interestRate = interestRate;
        this.minTermMonths = minTermMonths;
        this.maxTermMonths = maxTermMonths;
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
        this.requiresRLPF = requiresRLPF;
    }
    
    // Getters and Setters
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public double getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
    
    public int getMinTermMonths() {
        return minTermMonths;
    }
    
    public void setMinTermMonths(int minTermMonths) {
        this.minTermMonths = minTermMonths;
    }
    
    public int getMaxTermMonths() {
        return maxTermMonths;
    }
    
    public void setMaxTermMonths(int maxTermMonths) {
        this.maxTermMonths = maxTermMonths;
    }
    
    public double getMinAmount() {
        return minAmount;
    }
    
    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }
    
    public double getMaxAmount() {
        return maxAmount;
    }
    
    public void setMaxAmount(double maxAmount) {
        this.maxAmount = maxAmount;
    }
    
    public boolean isRequiresRLPF() {
        return requiresRLPF;
    }
    
    public void setRequiresRLPF(boolean requiresRLPF) {
        this.requiresRLPF = requiresRLPF;
    }
    
    @Override
    public String toString() {
        return name;
    }
}
