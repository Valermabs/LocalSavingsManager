package com.moscat.utils;

/**
 * Constants used throughout the application
 */
public class Constants {
    
    // Application settings
    public static final String APP_NAME = "MOSCAT Cooperative";
    public static final String APP_VERSION = "1.0.0";
    
    // Account status
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";
    public static final String STATUS_DORMANT = "Dormant";
    
    // Transaction types
    public static final String TRANSACTION_DEPOSIT = "Deposit";
    public static final String TRANSACTION_WITHDRAWAL = "Withdrawal";
    public static final String TRANSACTION_INTEREST = "Interest";
    public static final String TRANSACTION_INTEREST_EARNING = "Interest Earning";
    public static final String TRANSACTION_INTEREST_WITHDRAWAL = "Interest Withdrawal";
    public static final String TRANSACTION_LOAN_RELEASE = "Loan Release";
    public static final String TRANSACTION_LOAN_PAYMENT = "Loan Payment";
    
    // Loan types
    public static final String LOAN_TYPE_REGULAR = "Regular";
    public static final String LOAN_TYPE_EMERGENCY = "Emergency";
    public static final String LOAN_TYPE_EDUCATIONAL = "Educational";
    public static final String LOAN_TYPE_PETTY_CASH = "Petty Cash";
    public static final String LOAN_TYPE_BONUS = "Bonus";
    
    // Loan status
    public static final String LOAN_STATUS_PENDING = "Pending";
    public static final String LOAN_STATUS_APPROVED = "Approved";
    public static final String LOAN_STATUS_REJECTED = "Rejected";
    public static final String LOAN_STATUS_ACTIVE = "Active";
    public static final String LOAN_STATUS_PAID = "Paid";
    public static final String LOAN_STATUS_DEFAULTED = "Defaulted";
    
    // Loan status aliases (For compatibility with existing code)
    public static final String LOAN_PENDING = "Pending";
    public static final String LOAN_APPROVED = "Approved";
    public static final String LOAN_REJECTED = "Rejected";
    public static final String LOAN_ACTIVE = "Active";
    public static final String LOAN_PAID = "Paid";
    public static final String LOAN_DEFAULTED = "Defaulted";
    
    // Payment status
    public static final String PAYMENT_STATUS_PAID = "Paid";
    public static final String PAYMENT_STATUS_UNPAID = "Unpaid";
    public static final String PAYMENT_STATUS_PARTIAL = "Partial";
    public static final String PAYMENT_STATUS_LATE = "Late";
    
    // Interest computation basis
    public static final String INTEREST_COMPUTATION_DAILY = "Daily";
    public static final String INTEREST_COMPUTATION_MONTHLY = "Monthly";
    
    // Employment status
    public static final String EMPLOYMENT_REGULAR = "Regular";
    public static final String EMPLOYMENT_CONTRACTUAL = "Contractual";
    public static final String EMPLOYMENT_SELF_EMPLOYED = "Self-Employed";
    public static final String EMPLOYMENT_UNEMPLOYED = "Unemployed";
    public static final String EMPLOYMENT_RETIRED = "Retired";
    
    // Date formats
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String MONTH_FORMAT = "yyyy-MM";
    
    // System settings
    public static final int DORMANCY_PERIOD_MONTHS = 12; // 12 months of inactivity marks account as dormant
    
    // Numeric validation patterns
    public static final String NUMERIC_PATTERN = "^[0-9]+(\\.[0-9]{1,2})?$"; // Numbers with up to 2 decimal places
    public static final String INTEGER_PATTERN = "^[0-9]+$"; // Integer numbers only
    
    // Default interest rates
    public static final double DEFAULT_SAVINGS_INTEREST_RATE = 2.5; // 2.5% annual interest
    public static final double DEFAULT_MINIMUM_BALANCE = 500.00; // 500 minimum balance
    
    // Transaction fees
    public static final double TRANSACTION_FEE = 10.00; // Standard transaction fee

    // UI constants
    public static final int TEXT_FIELD_HEIGHT = 25;
    
    // User roles (For backward compatibility as we transition to SuperAdmin-only system)
    public static final String ROLE_SUPER_ADMIN = "Super Administrator";
    public static final String ROLE_TREASURER = "Treasurer";
    public static final String ROLE_BOOKKEEPER = "Bookkeeper";
    
    // User and account status
    public static final String ACCOUNT_ACTIVE = "Active";
    public static final String ACCOUNT_INACTIVE = "Inactive";
    public static final String ACCOUNT_DORMANT = "Dormant";
    public static final String ACCOUNT_CLOSED = "Closed";
    
    // Savings account status (For backward compatibility)
    public static final String SAVINGS_STATUS_ACTIVE = "Active";
    public static final String SAVINGS_STATUS_DORMANT = "Dormant";
    public static final String SAVINGS_STATUS_CLOSED = "Closed";
    
    // Permission types (For backward compatibility)
    public static final String PERMISSION_USER_MANAGEMENT = "User Management";
    public static final String PERMISSION_MEMBER_MANAGEMENT = "Member Management";
    public static final String PERMISSION_ACCOUNT_MANAGEMENT = "Account Management";
    public static final String PERMISSION_TRANSACTION_MANAGEMENT = "Transaction Management";
    public static final String PERMISSION_LOAN_MANAGEMENT = "Loan Management";
    public static final String PERMISSION_REPORT_GENERATION = "Report Generation";
    public static final String PERMISSION_SYSTEM_SETTINGS = "System Settings";
}