package com.moscat.controllers;

import com.moscat.models.Member;
import com.moscat.models.SavingsAccount;
import com.moscat.models.Transaction;
import com.moscat.utils.Constants;
import com.moscat.utils.DatabaseManager;
import com.moscat.utils.DateUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller for savings account operations
 */
public class SavingsController {
    
    /**
     * Get a savings account by ID
     * 
     * @param accountId The account ID
     * @return The SavingsAccount, or null if not found
     */
    public static SavingsAccount getAccountById(int accountId) {
        // In our updated model, member ID is the same as the account ID
        Member member = MemberController.getMemberById(accountId);
        if (member != null) {
            return SavingsAccount.fromMember(member);
        }
        return null;
    }
    
    /**
     * Get all savings accounts in the system
     * 
     * @return List of savings accounts
     */
    public static List<SavingsAccount> getAllAccounts() {
        List<SavingsAccount> accounts = new ArrayList<>();
        List<Member> members = MemberController.getAllMembers();
        
        for (Member member : members) {
            accounts.add(SavingsAccount.fromMember(member));
        }
        
        return accounts;
    }
    
    /**
     * Get all active savings accounts
     * 
     * @return List of active savings accounts
     */
    public static List<SavingsAccount> getActiveAccounts() {
        List<SavingsAccount> accounts = new ArrayList<>();
        List<Member> members = MemberController.getActiveMembers();
        
        for (Member member : members) {
            SavingsAccount account = SavingsAccount.fromMember(member);
            if (account.isActive()) {
                accounts.add(account);
            }
        }
        
        return accounts;
    }
    
    /**
     * Get all dormant savings accounts
     * 
     * @return List of dormant savings accounts
     */
    public static List<SavingsAccount> getDormantAccounts() {
        List<SavingsAccount> accounts = new ArrayList<>();
        List<Member> members = MemberController.getAllMembers();
        
        for (Member member : members) {
            SavingsAccount account = SavingsAccount.fromMember(member);
            if (account.isDormant()) {
                accounts.add(account);
            }
        }
        
        return accounts;
    }
    
    /**
     * Create a new savings account
     * 
     * @param memberId The member ID
     * @param initialDeposit The initial deposit amount
     * @param transactionBy User ID of the person creating the account
     * @return The created SavingsAccount, or null if creation failed
     */
    public static SavingsAccount createAccount(int memberId, double initialDeposit, int transactionBy) {
        Member member = MemberController.getMemberById(memberId);
        if (member == null) {
            return null;
        }
        
        // Update the member's savings balance
        member.setSavingsBalance(initialDeposit);
        member.setStatus(Constants.ACCOUNT_ACTIVE);
        member.setLastActivityDate(new Date());
        
        boolean success = MemberController.updateMember(member);
        if (!success) {
            return null;
        }
        
        // Create a transaction record for the initial deposit
        if (initialDeposit > 0) {
            Transaction transaction = new Transaction();
            transaction.setAccountId(memberId);
            transaction.setTransactionType("SAVINGS_DEPOSIT");
            transaction.setAmount(initialDeposit);
            transaction.setRunningBalance(initialDeposit);
            transaction.setDescription("Initial deposit for new savings account");
            transaction.setTransactionBy(transactionBy);
            
            TransactionController.recordTransaction(transaction);
        }
        
        return SavingsAccount.fromMember(member);
    }
    
    /**
     * Update an existing savings account
     * 
     * @param account The account to update
     * @return True if update was successful, false otherwise
     */
    public static boolean updateAccount(SavingsAccount account) {
        Member member = MemberController.getMemberById(account.getMemberId());
        if (member == null) {
            return false;
        }
        
        // Transfer savings account data to member
        member.setSavingsBalance(account.getBalance());
        member.setInterestEarned(account.getInterestEarned());
        member.setStatus(account.getStatus());
        member.setLastActivityDate(account.getLastActivityDate());
        
        return MemberController.updateMember(member);
    }
    
    /**
     * Process a deposit into a savings account
     * 
     * @param accountId The account ID
     * @param amount The deposit amount
     * @param description Transaction description
     * @param transactionBy User ID of the person making the deposit
     * @return True if deposit was successful, false otherwise
     */
    public static boolean deposit(int accountId, double amount, String description, int transactionBy) {
        if (amount <= 0) {
            return false;
        }
        
        SavingsAccount account = getAccountById(accountId);
        if (account == null) {
            return false;
        }
        
        // Update account balance
        double newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);
        account.setLastActivityDate(new Date());
        
        // If account was dormant, reactivate it
        if (account.isDormant()) {
            account.setStatus(Constants.ACCOUNT_ACTIVE);
        }
        
        // Update the account
        boolean success = updateAccount(account);
        if (!success) {
            return false;
        }
        
        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType("SAVINGS_DEPOSIT");
        transaction.setAmount(amount);
        transaction.setRunningBalance(newBalance);
        transaction.setDescription(description);
        transaction.setTransactionBy(transactionBy);
        
        return TransactionController.recordTransaction(transaction);
    }
    
    /**
     * Process a withdrawal from a savings account
     * 
     * @param accountId The account ID
     * @param amount The withdrawal amount
     * @param description Transaction description
     * @param transactionBy User ID of the person making the withdrawal
     * @return True if withdrawal was successful, false otherwise
     */
    public static boolean withdraw(int accountId, double amount, String description, int transactionBy) {
        if (amount <= 0) {
            return false;
        }
        
        SavingsAccount account = getAccountById(accountId);
        if (account == null) {
            return false;
        }
        
        // Check if sufficient balance
        if (account.getBalance() < amount) {
            return false;
        }
        
        // Update account balance
        double newBalance = account.getBalance() - amount;
        account.setBalance(newBalance);
        account.setLastActivityDate(new Date());
        
        // Update the account
        boolean success = updateAccount(account);
        if (!success) {
            return false;
        }
        
        // Record the transaction
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType("SAVINGS_WITHDRAWAL");
        transaction.setAmount(amount);
        transaction.setRunningBalance(newBalance);
        transaction.setDescription(description);
        transaction.setTransactionBy(transactionBy);
        
        return TransactionController.recordTransaction(transaction);
    }
    
    /**
     * Calculate and apply interest to all active savings accounts
     * 
     * @param interestRatePercentage The interest rate (e.g., 2.5 for 2.5%)
     * @param systemUserId User ID of the system for transaction recording
     * @return Map containing success count and total interest applied
     */
    public static Map<String, Object> calculateAndApplyInterest(double interestRatePercentage, int systemUserId) {
        List<SavingsAccount> activeAccounts = getActiveAccounts();
        int successCount = 0;
        double totalInterestApplied = 0;
        
        for (SavingsAccount account : activeAccounts) {
            // Calculate interest on the current balance
            double interestAmount = account.getBalance() * (interestRatePercentage / 100.0);
            
            // Update the interest earned
            double newInterestEarned = account.getInterestEarned() + interestAmount;
            account.setInterestEarned(newInterestEarned);
            
            // Update the account
            boolean success = updateAccount(account);
            if (success) {
                // Record the interest transaction
                Transaction transaction = new Transaction();
                transaction.setAccountId(account.getId());
                transaction.setTransactionType("INTEREST_EARNED");
                transaction.setAmount(interestAmount);
                transaction.setRunningBalance(account.getBalance() + newInterestEarned);
                transaction.setDescription("Interest earned at " + interestRatePercentage + "% rate");
                transaction.setTransactionBy(systemUserId);
                
                TransactionController.recordTransaction(transaction);
                
                successCount++;
                totalInterestApplied += interestAmount;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("successCount", successCount);
        result.put("totalInterestApplied", totalInterestApplied);
        
        return result;
    }
    
    /**
     * Check and update account status based on last activity date
     * 
     * @param dormancyDays Number of days of inactivity after which account is considered dormant
     * @return Number of accounts marked as dormant
     */
    public static int checkAndUpdateDormantAccounts(int dormancyDays) {
        List<SavingsAccount> activeAccounts = getActiveAccounts();
        int dormantCount = 0;
        
        Date currentDate = new Date();
        long dormancyMillis = dormancyDays * 24L * 60L * 60L * 1000L;
        
        for (SavingsAccount account : activeAccounts) {
            Date lastActivity = account.getLastActivityDate();
            
            if (lastActivity != null) {
                long inactiveMillis = currentDate.getTime() - lastActivity.getTime();
                
                if (inactiveMillis >= dormancyMillis) {
                    account.setStatus(Constants.ACCOUNT_DORMANT);
                    if (updateAccount(account)) {
                        dormantCount++;
                    }
                }
            }
        }
        
        return dormantCount;
    }
    
    /**
     * Close a savings account
     * 
     * @param accountId The account ID
     * @param closureReason The reason for closure
     * @param transactionBy User ID of the person closing the account
     * @return True if closure was successful, false otherwise
     */
    public static boolean closeAccount(int accountId, String closureReason, int transactionBy) {
        SavingsAccount account = getAccountById(accountId);
        if (account == null) {
            return false;
        }
        
        account.setStatus(Constants.ACCOUNT_CLOSED);
        
        boolean success = updateAccount(account);
        if (!success) {
            return false;
        }
        
        // Record the closure in the transaction history
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setTransactionType("ACCOUNT_CLOSURE");
        transaction.setAmount(0.0);
        transaction.setRunningBalance(account.getBalance() + account.getInterestEarned());
        transaction.setDescription("Account closed: " + closureReason);
        transaction.setTransactionBy(transactionBy);
        
        return TransactionController.recordTransaction(transaction);
    }
}