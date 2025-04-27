package com.moscat.models;

import java.time.LocalDate;

/**
 * Represents a single amortization payment in a loan repayment schedule
 */
public class LoanAmortization {
    private int id;
    private int loanId;
    private int paymentNumber;
    private LocalDate paymentDate;
    private double principalAmount;
    private double interestAmount;
    private double totalPayment;
    private double remainingBalance;
    private String paymentStatus;
    private LocalDate actualPaymentDate;
    
    // Constructor
    public LoanAmortization() {
        this.paymentStatus = "Unpaid";
    }
    
    // Getters and setters
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
    
    public int getPaymentNumber() {
        return paymentNumber;
    }
    
    public void setPaymentNumber(int paymentNumber) {
        this.paymentNumber = paymentNumber;
    }
    
    public LocalDate getPaymentDate() {
        return paymentDate;
    }
    
    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }
    
    public double getPrincipalAmount() {
        return principalAmount;
    }
    
    public void setPrincipalAmount(double principalAmount) {
        this.principalAmount = principalAmount;
    }
    
    public double getInterestAmount() {
        return interestAmount;
    }
    
    public void setInterestAmount(double interestAmount) {
        this.interestAmount = interestAmount;
    }
    
    public double getTotalPayment() {
        return totalPayment;
    }
    
    public void setTotalPayment(double totalPayment) {
        this.totalPayment = totalPayment;
    }
    
    public double getRemainingBalance() {
        return remainingBalance;
    }
    
    public void setRemainingBalance(double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
    
    public String getPaymentStatus() {
        return paymentStatus;
    }
    
    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }
    
    public LocalDate getActualPaymentDate() {
        return actualPaymentDate;
    }
    
    public void setActualPaymentDate(LocalDate actualPaymentDate) {
        this.actualPaymentDate = actualPaymentDate;
    }
    
    /**
     * Checks if the payment is overdue
     * 
     * @return true if payment is overdue, false otherwise
     */
    public boolean isOverdue() {
        if ("Paid".equals(paymentStatus)) {
            return false;
        }
        
        return paymentDate.isBefore(LocalDate.now());
    }
    
    /**
     * Gets the number of days overdue
     * 
     * @return The number of days overdue, or 0 if not overdue
     */
    public long getDaysOverdue() {
        if (!isOverdue()) {
            return 0;
        }
        
        return java.time.temporal.ChronoUnit.DAYS.between(paymentDate, LocalDate.now());
    }
    
    /**
     * Calculates the penalty amount if payment is overdue
     * 
     * @param penaltyRatePercentage The penalty rate in percentage (e.g., 2 for 2%)
     * @return The calculated penalty amount
     */
    public double calculatePenalty(double penaltyRatePercentage) {
        if (!isOverdue()) {
            return 0.0;
        }
        
        // Penalty = TotalPayment × PenaltyRate
        return totalPayment * (penaltyRatePercentage / 100.0);
    }
}