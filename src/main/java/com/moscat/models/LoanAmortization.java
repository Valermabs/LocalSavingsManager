package com.moscat.models;

/**
 * Model class for loan amortization schedule
 */
public class LoanAmortization {
    private int id;
    private int loanId;
    private int year;
    private double remainingPrincipal;
    private double annualInterest;
    private double annualPrincipalPayment;
    private double monthlyAmortization;
    
    /**
     * Default constructor
     */
    public LoanAmortization() {
    }
    
    /**
     * Parameterized constructor
     * 
     * @param id Amortization ID
     * @param loanId Loan ID
     * @param year Year number
     * @param remainingPrincipal Remaining principal at the start of the year
     * @param annualInterest Annual interest amount
     * @param annualPrincipalPayment Annual principal payment
     * @param monthlyAmortization Monthly amortization amount
     */
    public LoanAmortization(int id, int loanId, int year, double remainingPrincipal,
            double annualInterest, double annualPrincipalPayment, double monthlyAmortization) {
        this.id = id;
        this.loanId = loanId;
        this.year = year;
        this.remainingPrincipal = remainingPrincipal;
        this.annualInterest = annualInterest;
        this.annualPrincipalPayment = annualPrincipalPayment;
        this.monthlyAmortization = monthlyAmortization;
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
    
    public int getYear() {
        return year;
    }
    
    public void setYear(int year) {
        this.year = year;
    }
    
    public double getRemainingPrincipal() {
        return remainingPrincipal;
    }
    
    public void setRemainingPrincipal(double remainingPrincipal) {
        this.remainingPrincipal = remainingPrincipal;
    }
    
    public double getAnnualInterest() {
        return annualInterest;
    }
    
    public void setAnnualInterest(double annualInterest) {
        this.annualInterest = annualInterest;
    }
    
    public double getAnnualPrincipalPayment() {
        return annualPrincipalPayment;
    }
    
    public void setAnnualPrincipalPayment(double annualPrincipalPayment) {
        this.annualPrincipalPayment = annualPrincipalPayment;
    }
    
    public double getMonthlyAmortization() {
        return monthlyAmortization;
    }
    
    public void setMonthlyAmortization(double monthlyAmortization) {
        this.monthlyAmortization = monthlyAmortization;
    }
    
    /**
     * Gets the monthly principal payment
     * 
     * @return Monthly principal payment
     */
    public double getMonthlyPrincipalPayment() {
        return annualPrincipalPayment / 12;
    }
    
    /**
     * Gets the monthly interest payment
     * 
     * @return Monthly interest payment
     */
    public double getMonthlyInterestPayment() {
        return annualInterest / 12;
    }
    
    /**
     * Gets the total annual payment (principal + interest)
     * 
     * @return Total annual payment
     */
    public double getAnnualPayment() {
        return annualPrincipalPayment + annualInterest;
    }
}
