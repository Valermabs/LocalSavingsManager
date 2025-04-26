package com.moscat.models;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Model class for loans
 */
public class Loan {
    private int id;
    private int memberId;
    private String loanNumber;
    private String loanType; // REGULAR, PETTY_CASH, BONUS
    private String purpose;
    private double principalAmount;
    private double interestRate;
    private int termYears;
    private double previousLoanBalance;
    private double totalDeductions;
    private double netLoanProceeds;
    private double monthlyAmortization;
    private double remainingBalance;
    private Date applicationDate;
    private Date approvalDate;
    private Date releaseDate;
    private Date maturityDate;
    private Date lastPaymentDate;
    private String status; // PENDING, APPROVED, REJECTED, ACTIVE, PAID
    private int processedBy; // User ID who processed the loan
    
    private List<LoanDeduction> deductions;
    private List<LoanAmortization> amortizationSchedule;
    
    /**
     * Default constructor
     */
    public Loan() {
        this.deductions = new ArrayList<>();
        this.amortizationSchedule = new ArrayList<>();
    }
    
    /**
     * Parameterized constructor
     * 
     * @param id Loan ID
     * @param memberId Member ID
     * @param loanNumber Loan number
     * @param loanType Loan type
     * @param principalAmount Principal amount
     * @param interestRate Interest rate
     * @param termYears Term in years
     * @param previousLoanBalance Previous loan balance
     * @param totalDeductions Total deductions
     * @param netLoanProceeds Net loan proceeds
     * @param monthlyAmortization Monthly amortization
     * @param remainingBalance Remaining balance
     * @param applicationDate Application date
     * @param approvalDate Approval date
     * @param releaseDate Release date
     * @param maturityDate Maturity date
     * @param status Loan status
     * @param processedBy User ID who processed the loan
     */
    public Loan(int id, int memberId, String loanNumber, String loanType, double principalAmount,
            double interestRate, int termYears, double previousLoanBalance, double totalDeductions,
            double netLoanProceeds, double monthlyAmortization, double remainingBalance,
            Date applicationDate, Date approvalDate, Date releaseDate, Date maturityDate,
            String status, int processedBy) {
        this.id = id;
        this.memberId = memberId;
        this.loanNumber = loanNumber;
        this.loanType = loanType;
        this.principalAmount = principalAmount;
        this.interestRate = interestRate;
        this.termYears = termYears;
        this.previousLoanBalance = previousLoanBalance;
        this.totalDeductions = totalDeductions;
        this.netLoanProceeds = netLoanProceeds;
        this.monthlyAmortization = monthlyAmortization;
        this.remainingBalance = remainingBalance;
        this.applicationDate = applicationDate;
        this.approvalDate = approvalDate;
        this.releaseDate = releaseDate;
        this.maturityDate = maturityDate;
        this.status = status;
        this.processedBy = processedBy;
        this.deductions = new ArrayList<>();
        this.amortizationSchedule = new ArrayList<>();
    }
    
    // Getters and Setters
    
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
    
    public String getLoanNumber() {
        return loanNumber;
    }
    
    public void setLoanNumber(String loanNumber) {
        this.loanNumber = loanNumber;
    }
    
    public String getLoanType() {
        return loanType;
    }
    
    public void setLoanType(String loanType) {
        this.loanType = loanType;
    }
    
    public String getPurpose() {
        return purpose;
    }
    
    public void setPurpose(String purpose) {
        this.purpose = purpose;
    }
    
    public double getPrincipalAmount() {
        return principalAmount;
    }
    
    public void setPrincipalAmount(double principalAmount) {
        this.principalAmount = principalAmount;
    }
    
    public double getInterestRate() {
        return interestRate;
    }
    
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
    
    public int getTermYears() {
        return termYears;
    }
    
    public void setTermYears(int termYears) {
        this.termYears = termYears;
    }
    
    public double getPreviousLoanBalance() {
        return previousLoanBalance;
    }
    
    public void setPreviousLoanBalance(double previousLoanBalance) {
        this.previousLoanBalance = previousLoanBalance;
    }
    
    public double getTotalDeductions() {
        return totalDeductions;
    }
    
    public void setTotalDeductions(double totalDeductions) {
        this.totalDeductions = totalDeductions;
    }
    
    public double getNetLoanProceeds() {
        return netLoanProceeds;
    }
    
    public void setNetLoanProceeds(double netLoanProceeds) {
        this.netLoanProceeds = netLoanProceeds;
    }
    
    public double getMonthlyAmortization() {
        return monthlyAmortization;
    }
    
    public void setMonthlyAmortization(double monthlyAmortization) {
        this.monthlyAmortization = monthlyAmortization;
    }
    
    public double getRemainingBalance() {
        return remainingBalance;
    }
    
    public void setRemainingBalance(double remainingBalance) {
        this.remainingBalance = remainingBalance;
    }
    
    public Date getApplicationDate() {
        return applicationDate;
    }
    
    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }
    
    public Date getApprovalDate() {
        return approvalDate;
    }
    
    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    public Date getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(Date releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public Date getMaturityDate() {
        return maturityDate;
    }
    
    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }
    
    public Date getLastPaymentDate() {
        return lastPaymentDate;
    }
    
    public void setLastPaymentDate(Date lastPaymentDate) {
        this.lastPaymentDate = lastPaymentDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public int getProcessedBy() {
        return processedBy;
    }
    
    public void setProcessedBy(int processedBy) {
        this.processedBy = processedBy;
    }
    
    public List<LoanDeduction> getDeductions() {
        return deductions;
    }
    
    public void setDeductions(List<LoanDeduction> deductions) {
        this.deductions = deductions;
    }
    
    public List<LoanAmortization> getAmortizationSchedule() {
        return amortizationSchedule;
    }
    
    public void setAmortizationSchedule(List<LoanAmortization> amortizationSchedule) {
        this.amortizationSchedule = amortizationSchedule;
    }
    
    /**
     * Adds a deduction to the loan
     * 
     * @param deduction Deduction to add
     */
    public void addDeduction(LoanDeduction deduction) {
        this.deductions.add(deduction);
        this.totalDeductions += deduction.getAmount();
        recalculateNetProceeds();
    }
    
    /**
     * Adds an amortization schedule entry
     * 
     * @param amortization Amortization schedule entry
     */
    public void addAmortization(LoanAmortization amortization) {
        this.amortizationSchedule.add(amortization);
    }
    
    /**
     * Recalculates the net loan proceeds
     */
    public void recalculateNetProceeds() {
        this.netLoanProceeds = this.principalAmount - this.totalDeductions - this.previousLoanBalance;
    }
    
    /**
     * Calculates the RLPF amount (₱1 per ₱1,000 computed monthly)
     * 
     * @return RLPF amount
     */
    public double calculateRLPF() {
        // RLPF is not applicable for Petty Cash and Bonus Loan
        if ("PETTY_CASH".equals(loanType) || "BONUS".equals(loanType)) {
            return 0;
        }
        
        // RLPF = (Principal / 1000) * 1 * (termYears * 12)
        return (principalAmount / 1000) * 1 * (termYears * 12);
    }
    
    /**
     * Calculates loan amortization schedule using straight-line diminishing principal method
     */
    public void calculateAmortizationSchedule() {
        amortizationSchedule.clear();
        
        double annualPrincipalPayment = principalAmount / termYears;
        double remainingPrincipal = principalAmount;
        
        for (int year = 1; year <= termYears; year++) {
            double annualInterest = remainingPrincipal * (interestRate / 100);
            double monthlyAmortization = (annualInterest / 12) + (annualPrincipalPayment / 12);
            
            LoanAmortization amortization = new LoanAmortization();
            amortization.setLoanId(id);
            amortization.setYear(year);
            amortization.setRemainingPrincipal(remainingPrincipal);
            amortization.setAnnualInterest(annualInterest);
            amortization.setAnnualPrincipalPayment(annualPrincipalPayment);
            amortization.setMonthlyAmortization(monthlyAmortization);
            
            amortizationSchedule.add(amortization);
            
            remainingPrincipal -= annualPrincipalPayment;
        }
        
        // Set the monthly amortization for the first year
        if (!amortizationSchedule.isEmpty()) {
            this.monthlyAmortization = amortizationSchedule.get(0).getMonthlyAmortization();
        }
    }
    
    /**
     * Checks if the loan is pending
     * 
     * @return true if pending, false otherwise
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }
    
    /**
     * Checks if the loan is approved
     * 
     * @return true if approved, false otherwise
     */
    public boolean isApproved() {
        return "APPROVED".equals(status);
    }
    
    /**
     * Checks if the loan is rejected
     * 
     * @return true if rejected, false otherwise
     */
    public boolean isRejected() {
        return "REJECTED".equals(status);
    }
    
    /**
     * Checks if the loan is active
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }
    
    /**
     * Checks if the loan is paid
     * 
     * @return true if paid, false otherwise
     */
    public boolean isPaid() {
        return "PAID".equals(status);
    }
}
