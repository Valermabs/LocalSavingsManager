package com.moscat.models;

import java.util.Date;

/**
 * Model class for interest rate settings for savings accounts
 * Supports configurable interest rates with calculation methods and minimum balance requirements
 */
public class InterestSettings {
    
    // Calculation method constants
    public static final String CALCULATION_MONTHLY = "Monthly";
    public static final String CALCULATION_DAILY = "Daily";
    
    private int id;
    private double interestRate;
    private double regularSavingsRate;
    private double timeDepositRate;
    private double shareCapitalRate;
    private double minimumBalance;
    private String calculationMethod;
    private Date effectiveDate;
    private String changeBasis;
    private String status;
    private Date createdDate;
    private int createdBy;
    private String boardResolutionNumber;
    private Date approvalDate;
    
    /**
     * Default constructor
     */
    public InterestSettings() {
        // Initialize with default values
        this.calculationMethod = CALCULATION_MONTHLY;
        this.minimumBalance = 500.0; // Default minimum balance
        this.status = "Active";
    }
    
    /**
     * Constructor with all fields
     * 
     * @param id ID
     * @param regularSavingsRate Regular savings interest rate
     * @param timeDepositRate Time deposit interest rate
     * @param shareCapitalRate Share capital interest rate
     * @param effectiveDate Effective date
     * @param createdDate Created date
     * @param createdBy Created by user ID
     */
    public InterestSettings(int id, double regularSavingsRate, double timeDepositRate, 
            double shareCapitalRate, Date effectiveDate, Date createdDate, int createdBy) {
        this.id = id;
        this.regularSavingsRate = regularSavingsRate;
        this.timeDepositRate = timeDepositRate;
        this.shareCapitalRate = shareCapitalRate;
        this.effectiveDate = effectiveDate;
        this.createdDate = createdDate;
        this.createdBy = createdBy;
        this.calculationMethod = CALCULATION_MONTHLY;
        this.minimumBalance = 500.0; // Default minimum balance
        this.status = "Active";
    }

    // Getters and Setters
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the regularSavingsRate
     */
    public double getRegularSavingsRate() {
        return regularSavingsRate;
    }

    /**
     * @param regularSavingsRate the regularSavingsRate to set
     */
    public void setRegularSavingsRate(double regularSavingsRate) {
        this.regularSavingsRate = regularSavingsRate;
    }

    /**
     * @return the timeDepositRate
     */
    public double getTimeDepositRate() {
        return timeDepositRate;
    }

    /**
     * @param timeDepositRate the timeDepositRate to set
     */
    public void setTimeDepositRate(double timeDepositRate) {
        this.timeDepositRate = timeDepositRate;
    }

    /**
     * @return the shareCapitalRate
     */
    public double getShareCapitalRate() {
        return shareCapitalRate;
    }

    /**
     * @param shareCapitalRate the shareCapitalRate to set
     */
    public void setShareCapitalRate(double shareCapitalRate) {
        this.shareCapitalRate = shareCapitalRate;
    }

    /**
     * @return the effectiveDate
     */
    public Date getEffectiveDate() {
        return effectiveDate;
    }

    /**
     * @param effectiveDate the effectiveDate to set
     */
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    /**
     * @return the createdDate
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the createdBy
     */
    public int getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }
    
    /**
     * @return the interestRate
     */
    public double getInterestRate() {
        return interestRate;
    }

    /**
     * @param interestRate the interestRate to set
     */
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }

    /**
     * @return the minimumBalance
     */
    public double getMinimumBalance() {
        return minimumBalance;
    }

    /**
     * @param minimumBalance the minimumBalance to set
     */
    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    /**
     * @return the calculationMethod
     */
    public String getCalculationMethod() {
        return calculationMethod;
    }

    /**
     * @param calculationMethod the calculationMethod to set
     */
    public void setCalculationMethod(String calculationMethod) {
        this.calculationMethod = calculationMethod;
    }

    /**
     * @return the changeBasis
     */
    public String getChangeBasis() {
        return changeBasis;
    }

    /**
     * @param changeBasis the changeBasis to set
     */
    public void setChangeBasis(String changeBasis) {
        this.changeBasis = changeBasis;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Alias for setCreatedDate for compatibility
     * 
     * @param createdAt the creation date to set
     */
    public void setCreatedAt(Date createdAt) {
        this.createdDate = createdAt;
    }
    
    /**
     * @return the boardResolutionNumber
     */
    public String getBoardResolutionNumber() {
        return boardResolutionNumber;
    }

    /**
     * @param boardResolutionNumber the board resolution number to set
     */
    public void setBoardResolutionNumber(String boardResolutionNumber) {
        this.boardResolutionNumber = boardResolutionNumber;
    }

    /**
     * @return the approvalDate
     */
    public Date getApprovalDate() {
        return approvalDate;
    }

    /**
     * @param approvalDate the approval date to set
     */
    public void setApprovalDate(Date approvalDate) {
        this.approvalDate = approvalDate;
    }
    
    /**
     * Calculates daily interest rate if the calculation method is daily
     * 
     * @return Daily interest rate 
     */
    public double getDailyInterestRate() {
        if (CALCULATION_DAILY.equals(calculationMethod)) {
            // Divide by 365 days
            return interestRate / 365.0;
        }
        return interestRate;
    }
    
    /**
     * Checks if a savings account meets the minimum balance requirement
     * 
     * @param balance Account balance to check
     * @return true if the balance is sufficient to earn interest, false otherwise
     */
    public boolean meetsMinimumBalance(double balance) {
        return balance >= minimumBalance;
    }
}