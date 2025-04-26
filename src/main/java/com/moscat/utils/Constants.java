package com.moscat.utils;

/**
 * Application constants
 */
public class Constants {
    
    // User roles
    public static final String ROLE_SUPER_ADMIN = "Super Admin";
    public static final String ROLE_TREASURER = "Treasurer";
    public static final String ROLE_BOOKKEEPER = "Bookkeeper";
    
    // Member status
    public static final String MEMBER_STATUS_ACTIVE = "Active";
    public static final String MEMBER_STATUS_INACTIVE = "Inactive";
    public static final String MEMBER_STATUS_SUSPENDED = "Suspended";
    
    // Savings account types
    public static final String SAVINGS_TYPE_REGULAR = "Regular";
    public static final String SAVINGS_TYPE_TIME_DEPOSIT = "Time Deposit";
    public static final String SAVINGS_TYPE_EDUCATION = "Education";
    
    // Savings account status
    public static final String SAVINGS_STATUS_ACTIVE = "Active";
    public static final String SAVINGS_STATUS_DORMANT = "Dormant";
    public static final String SAVINGS_STATUS_CLOSED = "Closed";
    
    // Generic status constants
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";
    public static final String ACCOUNT_ACTIVE = "Active";
    
    // Transaction types
    public static final String TRANSACTION_TYPE_DEPOSIT = "Deposit";
    public static final String TRANSACTION_TYPE_WITHDRAWAL = "Withdrawal";
    public static final String TRANSACTION_TYPE_TRANSFER = "Transfer";
    public static final String TRANSACTION_TYPE_INTEREST = "Interest";
    public static final String TRANSACTION_TYPE_FEE = "Fee";
    
    // Shorthand transaction types for code use
    public static final String TRANSACTION_DEPOSIT = "Deposit";
    public static final String TRANSACTION_WITHDRAWAL = "Withdrawal";
    public static final String TRANSACTION_INTEREST_EARNING = "Interest";
    public static final String TRANSACTION_LOAN_RELEASE = "Loan Release";
    public static final String TRANSACTION_LOAN_PAYMENT = "Loan Payment";
    
    // Loan types
    public static final String LOAN_TYPE_PERSONAL = "Personal";
    public static final String LOAN_TYPE_BUSINESS = "Business";
    public static final String LOAN_TYPE_EMERGENCY = "Emergency";
    public static final String LOAN_TYPE_EDUCATIONAL = "Educational";
    
    // Loan status
    public static final String LOAN_STATUS_PENDING = "Pending";
    public static final String LOAN_STATUS_APPROVED = "Approved";
    public static final String LOAN_STATUS_REJECTED = "Rejected";
    public static final String LOAN_STATUS_ACTIVE = "Active";
    public static final String LOAN_STATUS_COMPLETED = "Completed";
    public static final String LOAN_STATUS_DEFAULTED = "Defaulted";
    public static final String LOAN_STATUS_RESTRUCTURED = "Restructured";
    
    // Payment frequencies
    public static final String PAYMENT_FREQUENCY_DAILY = "Daily";
    public static final String PAYMENT_FREQUENCY_WEEKLY = "Weekly";
    public static final String PAYMENT_FREQUENCY_BIWEEKLY = "Bi-weekly";
    public static final String PAYMENT_FREQUENCY_MONTHLY = "Monthly";
    public static final String PAYMENT_FREQUENCY_QUARTERLY = "Quarterly";
    
    // Payment status
    public static final String PAYMENT_STATUS_PAID = "Paid";
    public static final String PAYMENT_STATUS_UNPAID = "Unpaid";
    public static final String PAYMENT_STATUS_PARTIAL = "Partial";
    public static final String PAYMENT_STATUS_LATE = "Late";
    
    // Database table names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_MEMBERS = "members";
    public static final String TABLE_SAVINGS_ACCOUNTS = "savings_accounts";
    public static final String TABLE_TRANSACTIONS = "transactions";
    public static final String TABLE_LOANS = "loans";
    public static final String TABLE_LOAN_PAYMENTS = "loan_payments";
    
    // Default interest rates
    public static final double DEFAULT_SAVINGS_INTEREST_RATE = 0.025; // 2.5%
    public static final double DEFAULT_LOAN_INTEREST_RATE = 0.12; // 12%
    
    // System settings
    public static final String SETTING_SAVINGS_INTEREST_RATE = "savings_interest_rate";
    public static final String SETTING_LOAN_INTEREST_RATE = "loan_interest_rate";
    public static final String SETTING_MEMBERSHIP_FEE = "membership_fee";
    public static final String SETTING_MINIMUM_BALANCE = "minimum_balance";
    public static final String SETTING_DORMANT_DAYS = "dormant_days";
}