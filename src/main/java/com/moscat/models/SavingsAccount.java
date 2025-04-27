package com.moscat.models;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * Represents a savings account
 * Note: In this updated model, savings information is stored directly in the Member object,
 * but this class is maintained for compatibility with existing code.
 */
public class SavingsAccount {
    private int id;
    private int memberId;
    private String accountNumber;
    private double balance;
    private double interestEarned;
    private String status;
    private Date lastActivityDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructor
    public SavingsAccount() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Static factory method to create from a Member object
    public static SavingsAccount fromMember(Member member) {
        SavingsAccount account = new SavingsAccount();
        account.setId(member.getId());  // Use member ID as account ID for simplicity
        account.setMemberId(member.getId());
        account.setAccountNumber(member.getMemberNumber());
        account.setBalance(member.getSavingsBalance());
        account.setInterestEarned(member.getInterestEarned());
        account.setStatus(member.getStatus());
        account.setLastActivityDate(member.getLastActivityDate());
        
        if (member.getCreatedAt() != null) {
            account.setCreatedAt(member.getCreatedAt());
        }
        
        if (member.getUpdatedAt() != null) {
            account.setUpdatedAt(member.getUpdatedAt());
        }
        
        return account;
    }
    
    // Static factory method to create from a map
    public static SavingsAccount fromMap(java.util.Map<String, Object> map) {
        SavingsAccount account = new SavingsAccount();
        
        if (map.containsKey("memberId")) {
            account.setId((Integer) map.get("memberId"));
            account.setMemberId((Integer) map.get("memberId"));
        }
        
        if (map.containsKey("memberNumber")) {
            account.setAccountNumber((String) map.get("memberNumber"));
        }
        
        if (map.containsKey("balance")) {
            account.setBalance((Double) map.get("balance"));
        }
        
        if (map.containsKey("interestEarned")) {
            account.setInterestEarned((Double) map.get("interestEarned"));
        }
        
        if (map.containsKey("status")) {
            account.setStatus((String) map.get("status"));
        }
        
        if (map.containsKey("lastActivity")) {
            account.setLastActivityDate((Date) map.get("lastActivity"));
        }
        
        return account;
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
    
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public double getBalance() {
        return balance;
    }
    
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    public double getInterestEarned() {
        return interestEarned;
    }
    
    public void setInterestEarned(double interestEarned) {
        this.interestEarned = interestEarned;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Date getLastActivityDate() {
        return lastActivityDate;
    }
    
    public void setLastActivityDate(Date lastActivityDate) {
        this.lastActivityDate = lastActivityDate;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    /**
     * Gets the total balance (savings + interest)
     * 
     * @return The total balance
     */
    public double getTotalBalance() {
        return balance + interestEarned;
    }
    
    /**
     * Gets the date the account was opened
     * For backward compatibility - uses createdAt
     * 
     * @return The opening date
     */
    public Date getOpenDate() {
        if (createdAt != null) {
            return com.moscat.utils.DateUtils.convertToDate(createdAt);
        }
        return null;
    }
    
    /**
     * Check if account is dormant
     * 
     * @return True if the account is dormant, false otherwise
     */
    public boolean isDormant() {
        return com.moscat.utils.Constants.ACCOUNT_DORMANT.equals(status) || 
               com.moscat.utils.Constants.SAVINGS_STATUS_DORMANT.equals(status);
    }
    
    /**
     * Check if account is active
     * 
     * @return True if the account is active, false otherwise
     */
    public boolean isActive() {
        return com.moscat.utils.Constants.ACCOUNT_ACTIVE.equals(status) || 
               com.moscat.utils.Constants.SAVINGS_STATUS_ACTIVE.equals(status);
    }
    
    /**
     * Check if account is inactive
     * 
     * @return True if the account is inactive, false otherwise
     */
    public boolean isInactive() {
        return com.moscat.utils.Constants.ACCOUNT_INACTIVE.equals(status);
    }
    
    /**
     * Check if account is closed
     * 
     * @return True if the account is closed, false otherwise
     */
    public boolean isClosed() {
        return com.moscat.utils.Constants.ACCOUNT_CLOSED.equals(status) || 
               com.moscat.utils.Constants.SAVINGS_STATUS_CLOSED.equals(status);
    }
    
    /**
     * Converts this account to a Member object
     * 
     * @return A Member object with savings data from this account
     */
    public Member toMember() {
        Member member = new Member();
        member.setId(this.memberId);
        member.setMemberNumber(this.accountNumber);
        member.setSavingsBalance(this.balance);
        member.setInterestEarned(this.interestEarned);
        member.setStatus(this.status);
        member.setLastActivityDate(this.lastActivityDate);
        
        return member;
    }
}