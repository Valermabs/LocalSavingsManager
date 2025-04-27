package com.moscat.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a loan in the system
 */
public class Loan {
    private int id;
    private int memberId;
    private String loanType;
    private double loanAmount;
    private double interestRate;
    private double previousLoanBalance;
    private double deductions;
    private double rlpf; // Risk Loss Provision Fund
    private double netProceeds;
    private int termMonths;
    private LocalDateTime applicationDate;
    private LocalDateTime approvalDate;
    private String status;
    private List<LoanAmortization> amortizationSchedule;
    
    // Constructor
    public Loan() {
        this.applicationDate = LocalDateTime.now();
        this.amortizationSchedule = new ArrayList<>();
        this.status = "Pending";
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
    
    public String getLoanType() {
        return loanType;
    }
    
    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }
    
    public double getLoanAmount() {
        return loanAmount;
    }
    
    public void setLoanAmount(double loanAmount) {
        this.loanAmount = loanAmount;
    }
    
    public double getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
    
    public double getPreviousLoanBalance() {
        return previousLoanBalance;
    }
    
    public void setPreviousLoanBalance(double previousLoanBalance) {
        this.previousLoanBalance = previousLoanBalance;
    }
    
    public double getDeductions() {
        return deductions;
    }
    
    public void setDeductions(double deductions) {
        this.deductions = deductions;
    }
    
    public double getRlpf() {
        return rlpf;
    }
    
    public void setRlpf(double rlpf) {
        this.rlpf = rlpf;
    }
    
    public double getNetProceeds() {
        return netProceeds;
    }
    
    public void setNetProceeds(double netProceeds) {
        this.netProceeds = netProceeds;
    }
    
    public int getTermMonths() {
        return termMonths;
    }
    
    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }
    
    public LocalDateTime getApplicationDate() {
        return applicationDate;
    }
    
    public void setApplicationDate(LocalDateTime applicationDate) {
        this.applicationDate = applicationDate;
    }
    
    public LocalDateTime getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(LocalDateTime approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<LoanAmortization> getAmortizationSchedule() {
        return amortizationSchedule;
    }
    
    public void setAmortizationSchedule(List<LoanAmortization> amortizationSchedule) {
        this.amortizationSchedule = amortizationSchedule;
    }
    
    /**
     * Calculate the RLPF (Risk Loss Provision Fund) for this loan
     * 
     * @return The calculated RLPF value
     */
    public double calculateRLPF() {
        // RLPF = (LoanAmount ÷ 1000) × 1 × Number of Term Months
        // Not applied to Petty Cash or Bonus Loans
        if ("Petty Cash".equals(loanType) || "Bonus".equals(loanType)) {
            return 0.0;
        }
        
        return (loanAmount / 1000.0) * 1 * termMonths;
    }
    
    /**
     * Calculate the net proceeds of the loan
     * 
     * @return The calculated net proceeds
     */
    public double calculateNetProceeds() {
        return loanAmount - previousLoanBalance - deductions - rlpf;
    }
    
    /**
     * Generate the amortization schedule for this loan
     */
    public void generateAmortizationSchedule() {
        amortizationSchedule.clear();
        
        // Number of years in the term
        double termYears = termMonths / 12.0;
        
        // Annual principal payment
        double annualPrincipalPayment = loanAmount / termYears;
        
        // Convert monthly value to yearly for the calculation
        LocalDate currentDate = LocalDate.now();
        
        // Generate for each year
        for (int year = 0; year < Math.ceil(termYears); year++) {
            // Calculate remaining principal
            double remainingPrincipal = loanAmount - (annualPrincipalPayment * year);
            if (remainingPrincipal < 0) {
                remainingPrincipal = 0;
            }
            
            // Calculate annual interest
            double annualInterest = remainingPrincipal * (interestRate / 100);
            
            // Monthly amortization = (annual interest / 12) + (annual principal payment / 12)
            double monthlyInterest = annualInterest / 12;
            double monthlyPrincipal = annualPrincipalPayment / 12;
            double monthlyAmortization = monthlyInterest + monthlyPrincipal;
            
            // Generate monthly amortization entries
            for (int month = 0; month < 12; month++) {
                // Skip if we've exceeded the term months
                if ((year * 12) + month >= termMonths) {
                    break;
                }
                
                LocalDate paymentDate = currentDate.plusMonths((year * 12) + month);
                double remainingBalance = remainingPrincipal - (monthlyPrincipal * month);
                if (remainingBalance < 0) {
                    remainingBalance = 0;
                }
                
                LoanAmortization amortization = new LoanAmortization();
                amortization.setPaymentNumber((year * 12) + month + 1);
                amortization.setPaymentDate(paymentDate);
                amortization.setPrincipalAmount(monthlyPrincipal);
                amortization.setInterestAmount(monthlyInterest);
                amortization.setTotalPayment(monthlyAmortization);
                amortization.setRemainingBalance(remainingBalance);
                amortization.setPaymentStatus("Unpaid");
                
                amortizationSchedule.add(amortization);
            }
        }
    }
}