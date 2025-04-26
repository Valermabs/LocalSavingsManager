package com.moscat.utils;

/**
 * Constants used throughout the application
 */
public class Constants {
    
    // Application settings
    public static final String APP_NAME = "MOSCAT Cooperative";
    public static final String APP_VERSION = "1.0.0";
    
    // User roles
    public static final String ROLE_SUPER_ADMIN = "SuperAdmin";
    public static final String ROLE_TREASURER = "Treasurer";
    public static final String ROLE_BOOKKEEPER = "Bookkeeper";
    
    // User status
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";
    public static final String STATUS_SUSPENDED = "Suspended";
    
    // Member status
    public static final String MEMBER_STATUS_ACTIVE = "Active";
    public static final String MEMBER_STATUS_INACTIVE = "Inactive";
    public static final String MEMBER_STATUS_SUSPENDED = "Suspended";
    
    // Account status
    public static final String ACCOUNT_STATUS_ACTIVE = "Active";
    public static final String ACCOUNT_STATUS_DORMANT = "Dormant";
    public static final String ACCOUNT_STATUS_CLOSED = "Closed";
    public static final String ACCOUNT_STATUS_FROZEN = "Frozen";
    
    // Savings account status - aliases for account status
    public static final String SAVINGS_STATUS_ACTIVE = ACCOUNT_STATUS_ACTIVE;
    public static final String SAVINGS_STATUS_DORMANT = ACCOUNT_STATUS_DORMANT;
    public static final String SAVINGS_STATUS_CLOSED = ACCOUNT_STATUS_CLOSED;
    public static final String SAVINGS_STATUS_FROZEN = ACCOUNT_STATUS_FROZEN;
    
    // Account types
    public static final String ACCOUNT_TYPE_REGULAR_SAVINGS = "Regular Savings";
    public static final String ACCOUNT_TYPE_TIME_DEPOSIT = "Time Deposit";
    public static final String ACCOUNT_TYPE_SHARE_CAPITAL = "Share Capital";
    
    // Transaction types
    public static final String TRANSACTION_TYPE_DEPOSIT = "Deposit";
    public static final String TRANSACTION_TYPE_WITHDRAWAL = "Withdrawal";
    public static final String TRANSACTION_TYPE_INTEREST = "Interest";
    public static final String TRANSACTION_TYPE_FEE = "Fee";
    public static final String TRANSACTION_TYPE_TRANSFER = "Transfer";
    public static final String TRANSACTION_TYPE_LOAN_RELEASE = "Loan Release";
    public static final String TRANSACTION_TYPE_LOAN_PAYMENT = "Loan Payment";
    
    // Transaction type aliases - for backward compatibility
    public static final String TRANSACTION_LOAN_RELEASE = TRANSACTION_TYPE_LOAN_RELEASE;
    public static final String TRANSACTION_LOAN_PAYMENT = TRANSACTION_TYPE_LOAN_PAYMENT;
    
    // Loan types
    public static final String LOAN_TYPE_PERSONAL = "Personal";
    public static final String LOAN_TYPE_BUSINESS = "Business";
    public static final String LOAN_TYPE_EMERGENCY = "Emergency";
    public static final String LOAN_TYPE_EDUCATIONAL = "Educational";
    
    // Loan status
    public static final String LOAN_STATUS_PENDING = "Pending";
    public static final String LOAN_STATUS_APPROVED = "Approved";
    public static final String LOAN_STATUS_REJECTED = "Rejected";
    public static final String LOAN_STATUS_DISBURSED = "Disbursed";
    public static final String LOAN_STATUS_ACTIVE = "Active";
    public static final String LOAN_STATUS_PAID = "Paid";
    public static final String LOAN_STATUS_COMPLETED = "Completed";
    public static final String LOAN_STATUS_DEFAULTED = "Defaulted";
    
    // Loan status aliases - for backward compatibility
    public static final String LOAN_PENDING = LOAN_STATUS_PENDING;
    public static final String LOAN_APPROVED = LOAN_STATUS_APPROVED;
    public static final String LOAN_REJECTED = LOAN_STATUS_REJECTED;
    public static final String LOAN_ACTIVE = LOAN_STATUS_ACTIVE;
    public static final String LOAN_PAID = LOAN_STATUS_PAID;
    
    // Payment status
    public static final String PAYMENT_STATUS_PAID = "Paid";
    public static final String PAYMENT_STATUS_UNPAID = "Unpaid";
    public static final String PAYMENT_STATUS_PARTIAL = "Partial";
    public static final String PAYMENT_STATUS_LATE = "Late";
    
    // Payment frequency
    public static final String PAYMENT_FREQUENCY_MONTHLY = "Monthly";
    public static final String PAYMENT_FREQUENCY_BIWEEKLY = "Bi-weekly";
    public static final String PAYMENT_FREQUENCY_WEEKLY = "Weekly";
    
    // Interest calculation methods
    public static final String INTEREST_CALCULATION_DAILY = "Daily";
    public static final String INTEREST_CALCULATION_MONTHLY = "Monthly";
    public static final String INTEREST_CALCULATION_QUARTERLY = "Quarterly";
    
    // Permissions
    public static final String PERMISSION_USER_MANAGEMENT = "USER_MANAGEMENT";
    public static final String PERMISSION_MEMBER_MANAGEMENT = "MEMBER_MANAGEMENT";
    public static final String PERMISSION_ACCOUNT_MANAGEMENT = "ACCOUNT_MANAGEMENT";
    public static final String PERMISSION_TRANSACTION_MANAGEMENT = "TRANSACTION_MANAGEMENT";
    public static final String PERMISSION_LOAN_MANAGEMENT = "LOAN_MANAGEMENT";
    public static final String PERMISSION_REPORT_GENERATION = "REPORT_GENERATION";
    public static final String PERMISSION_SYSTEM_SETTINGS = "SYSTEM_SETTINGS";
    
    // Date format
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    // Dormancy period in days
    public static final int DORMANCY_PERIOD_DAYS = 180; // 6 months
    
    // Default interest rates
    public static final double DEFAULT_SAVINGS_INTEREST_RATE = 0.025; // 2.5%
    public static final double DEFAULT_TIME_DEPOSIT_INTEREST_RATE = 0.04; // 4.0%
    public static final double DEFAULT_SHARE_CAPITAL_INTEREST_RATE = 0.05; // 5.0%
    
    // Default minimum balances
    public static final double DEFAULT_SAVINGS_MINIMUM_BALANCE = 500.0;
    public static final double DEFAULT_TIME_DEPOSIT_MINIMUM_BALANCE = 5000.0;
    public static final double DEFAULT_SHARE_CAPITAL_MINIMUM_BALANCE = 1000.0;
    
    // System settings keys
    public static final String SETTING_INTEREST_RATE = "interest_rate";
    public static final String SETTING_MINIMUM_BALANCE = "minimum_balance";
    public static final String SETTING_DORMANCY_PERIOD = "dormancy_period";
}