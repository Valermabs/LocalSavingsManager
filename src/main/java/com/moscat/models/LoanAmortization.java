package com.moscat.models;

import java.time.LocalDate;

/**
 * Represents a loan amortization payment schedule
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
     * Check if payment is due
     * 
     * @return True if payment is due, false otherwise
     */
    public boolean isPaymentDue() {
        return LocalDate.now().isAfter(paymentDate) && "Unpaid".equals(paymentStatus);
    }
    
    /**
     * Check if payment is late
     * 
     * @return True if payment is late, false otherwise
     */
    public boolean isPaymentLate() {
        return LocalDate.now().isAfter(paymentDate.plusDays(5)) && "Unpaid".equals(paymentStatus);
    }
}