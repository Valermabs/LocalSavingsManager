package com.moscat.models;

import com.moscat.utils.Constants;
import java.util.Date;

/**
 * Model for savings account data
 */
public class SavingsAccount {
    
    private int id;
    private String accountNumber;
    private int memberId;
    private String accountType;
    private double balance;
    private double interestAccrued; // Accrued interest that doesn't add to principal
    private double interestRate;
    private double interestEarned;
    private double minimumBalance;
    private String status;
    private Date openedDate;
    private Date openDate;
    private Date lastTransactionDate;
    private Date lastInterestDate;
    private Date lastActivityDate;
    private Date dormantSince;
    private Date createdAt;
    private Date updatedAt;
    
    /**
     * Default constructor
     */
    public SavingsAccount() {
    }
    
    /**
     * Gets the ID
     * 
     * @return ID
     */
    public int getId() {
        return id;
    }
    
    /**
     * Sets the ID
     * 
     * @param id ID
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Gets the account number
     * 
     * @return Account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }
    
    /**
     * Sets the account number
     * 
     * @param accountNumber Account number
     */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    /**
     * Gets the member ID
     * 
     * @return Member ID
     */
    public int getMemberId() {
        return memberId;
    }
    
    /**
     * Sets the member ID
     * 
     * @param memberId Member ID
     */
    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }
    
    /**
     * Gets the account type
     * 
     * @return Account type
     */
    public String getAccountType() {
        return accountType;
    }
    
    /**
     * Sets the account type
     * 
     * @param accountType Account type
     */
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    /**
     * Gets the balance
     * 
     * @return Balance
     */
    public double getBalance() {
        return balance;
    }
    
    /**
     * Sets the balance
     * 
     * @param balance Balance
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    /**
     * Gets the interest rate
     * 
     * @return Interest rate
     */
    public double getInterestRate() {
        return interestRate;
    }
    
    /**
     * Sets the interest rate
     * 
     * @param interestRate Interest rate
     */
    public void setInterestRate(double interestRate) {
        this.interestRate = interestRate;
    }
    
    /**
     * Gets the status
     * 
     * @return Status
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Sets the status
     * 
     * @param status Status
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Gets the opened date
     * 
     * @return Opened date
     */
    public Date getOpenedDate() {
        return openedDate;
    }
    
    /**
     * Sets the opened date
     * 
     * @param openedDate Opened date
     */
    public void setOpenedDate(Date openedDate) {
        this.openedDate = openedDate;
    }
    
    /**
     * Gets the last transaction date
     * 
     * @return Last transaction date
     */
    public Date getLastTransactionDate() {
        return lastTransactionDate;
    }
    
    /**
     * Sets the last transaction date
     * 
     * @param lastTransactionDate Last transaction date
     */
    public void setLastTransactionDate(Date lastTransactionDate) {
        this.lastTransactionDate = lastTransactionDate;
    }
    
    /**
     * Gets the created at date
     * 
     * @return Created at date
     */
    public Date getCreatedAt() {
        return createdAt;
    }
    
    /**
     * Sets the created at date
     * 
     * @param createdAt Created at date
     */
    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Gets the updated at date
     * 
     * @return Updated at date
     */
    public Date getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Sets the updated at date
     * 
     * @param updatedAt Updated at date
     */
    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Checks if the account is active
     * 
     * @return true if active, false otherwise
     */
    public boolean isActive() {
        return Constants.SAVINGS_STATUS_ACTIVE.equals(status);
    }
    
    /**
     * Checks if the account is dormant
     * 
     * @return true if dormant, false otherwise
     */
    public boolean isDormant() {
        return Constants.SAVINGS_STATUS_DORMANT.equals(status);
    }
    
    /**
     * Gets the opening date (alias for opened date)
     * 
     * @return Opening date
     */
    public Date getOpenDate() {
        return openedDate;
    }
    
    /**
     * Sets the open date
     * 
     * @param openDate Open date
     */
    public void setOpenDate(Date openDate) {
        this.openedDate = openDate;
        this.openDate = openDate;
    }
    
    /**
     * Gets the last activity date
     * 
     * @return Last activity date
     */
    public Date getLastActivityDate() {
        return lastActivityDate != null ? lastActivityDate : lastTransactionDate;
    }
    
    /**
     * Sets the last activity date
     * 
     * @param lastActivityDate Last activity date
     */
    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
    
    /**
     * Gets the interest earned
     * 
     * @return Interest earned
     */
    public double getInterestEarned() {
        return interestEarned;
    }
    
    /**
     * Sets the interest earned
     * 
     * @param interestEarned Interest earned
     */
    public void setInterestEarned(double interestEarned) {
        this.interestEarned = interestEarned;
    }
    
    /**
     * Gets the total balance (alias for balance)
     * 
     * @return Total balance
     */
    public double getTotalBalance() {
        return balance;
    }
    
    /**
     * Gets the interest accrued
     * 
     * @return Interest accrued
     */
    public double getInterestAccrued() {
        return interestAccrued;
    }
    
    /**
     * Sets the interest accrued
     * 
     * @param interestAccrued Interest accrued
     */
    public void setInterestAccrued(double interestAccrued) {
        this.interestAccrued = interestAccrued;
    }
    
    /**
     * Gets the minimum balance
     * 
     * @return Minimum balance
     */
    public double getMinimumBalance() {
        return minimumBalance;
    }
    
    /**
     * Sets the minimum balance
     * 
     * @param minimumBalance Minimum balance
     */
    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
    }
    
    /**
     * Gets the last interest calculation date
     * 
     * @return Last interest calculation date
     */
    public Date getLastInterestDate() {
        return lastInterestDate;
    }
    
    /**
     * Sets the last interest calculation date
     * 
     * @param lastInterestDate Last interest calculation date
     */
    public void setLastInterestDate(Date lastInterestDate) {
        this.lastInterestDate = lastInterestDate;
    }
    
    /**
     * Gets the dormant since date
     * 
     * @return Dormant since date
     */
    public Date getDormantSince() {
        return dormantSince;
    }
    
    /**
     * Sets the dormant since date
     * 
     * @param dormantSince Dormant since date
     */
    public void setDormantSince(Date dormantSince) {
        this.dormantSince = dormantSince;
    }
    
    /**
     * Gets the total available balance (principal + accrued interest)
     * 
     * @return Total available balance
     */
    public double getAvailableBalance() {
        return balance + interestAccrued;
    }
    
    /**
     * Checks if account is dormant for X months
     * 
     * @param monthsInactive Number of months to check for dormancy
     * @return true if account is dormant for specified months
     */
    public boolean isDormantFor(int monthsInactive) {
        if (lastActivityDate == null && lastTransactionDate == null) {
            return false;
        }
        
        Date lastActivity = getLastActivityDate();
        if (lastActivity == null) {
            return false;
        }
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.add(java.util.Calendar.MONTH, -monthsInactive);
        Date dormancyThreshold = cal.getTime();
        
        return lastActivity.before(dormancyThreshold);
    }
}