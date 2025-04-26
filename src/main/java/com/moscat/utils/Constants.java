package com.moscat.utils;

/**
 * Constants used throughout the application
 */
public class Constants {
    
    // User roles
    public static final String ROLE_SUPER_ADMIN = "SuperAdmin";
    public static final String ROLE_TREASURER = "Treasurer";
    public static final String ROLE_BOOKKEEPER = "Bookkeeper";
    
    // User status
    public static final String STATUS_ACTIVE = "Active";
    public static final String STATUS_INACTIVE = "Inactive";
    public static final String STATUS_LOCKED = "Locked";
    public static final String STATUS_PENDING = "Pending";
    
    // Account status
    public static final String ACCOUNT_ACTIVE = "Active";
    public static final String ACCOUNT_DORMANT = "Dormant";
    public static final String ACCOUNT_CLOSED = "Closed";
    public static final String ACCOUNT_SUSPENDED = "Suspended";
    
    // Savings status (aliases for account status)
    public static final String SAVINGS_STATUS_ACTIVE = ACCOUNT_ACTIVE;
    public static final String SAVINGS_STATUS_DORMANT = ACCOUNT_DORMANT;
    public static final String SAVINGS_STATUS_CLOSED = ACCOUNT_CLOSED;
    public static final String SAVINGS_STATUS_SUSPENDED = ACCOUNT_SUSPENDED;
    
    // Transaction types
    public static final String TRANSACTION_DEPOSIT = "Deposit";
    public static final String TRANSACTION_WITHDRAWAL = "Withdrawal";
    public static final String TRANSACTION_TRANSFER = "Transfer";
    public static final String TRANSACTION_INTEREST = "Interest";
    public static final String TRANSACTION_FEE = "Fee";
    public static final String TRANSACTION_LOAN_PAYMENT = "Loan Payment";
    public static final String TRANSACTION_LOAN_RELEASE = "Loan Release";
    public static final String TRANSACTION_INTEREST_EARNING = "Interest Earning";
    public static final String TRANSACTION_INTEREST_WITHDRAWAL = "Interest Withdrawal";
    
    // Loan status
    public static final String LOAN_PENDING = "Pending";
    public static final String LOAN_APPROVED = "Approved";
    public static final String LOAN_REJECTED = "Rejected";
    public static final String LOAN_DISBURSED = "Disbursed";
    public static final String LOAN_ACTIVE = "Active";
    public static final String LOAN_COMPLETED = "Completed";
    public static final String LOAN_DEFAULTED = "Defaulted";
    
    // Loan status codes (used in controller logic)
    public static final String LOAN_STATUS_PENDING = LOAN_PENDING;
    public static final String LOAN_STATUS_APPROVED = LOAN_APPROVED;
    public static final String LOAN_STATUS_REJECTED = LOAN_REJECTED;
    public static final String LOAN_STATUS_DISBURSED = LOAN_DISBURSED;
    public static final String LOAN_STATUS_ACTIVE = LOAN_ACTIVE;
    public static final String LOAN_STATUS_COMPLETED = LOAN_COMPLETED;
    public static final String LOAN_STATUS_DEFAULTED = LOAN_DEFAULTED;
    
    // Payment status
    public static final String PAYMENT_PAID = "Paid";
    public static final String PAYMENT_UNPAID = "Unpaid";
    public static final String PAYMENT_LATE = "Late";
    public static final String PAYMENT_PARTIAL = "Partial";
    
    // Loan payment status (aliases for payment status)
    public static final String LOAN_PAID = PAYMENT_PAID;
    
    // Account types
    public static final String ACCOUNT_SAVINGS = "Savings";
    public static final String ACCOUNT_TIME_DEPOSIT = "Time Deposit";
    public static final String ACCOUNT_SHARE_CAPITAL = "Share Capital";
    
    // Loan types
    public static final String LOAN_PERSONAL = "Personal Loan";
    public static final String LOAN_BUSINESS = "Business Loan";
    public static final String LOAN_EMERGENCY = "Emergency Loan";
    public static final String LOAN_HOUSING = "Housing Loan";
    public static final String LOAN_EDUCATIONAL = "Educational Loan";
    
    // Loan type codes (used in controller logic)
    public static final String LOAN_TYPE_PERSONAL = "PERSONAL";
    public static final String LOAN_TYPE_BUSINESS = "BUSINESS";
    public static final String LOAN_TYPE_EMERGENCY = "EMERGENCY";
    public static final String LOAN_TYPE_HOUSING = "HOUSING";
    public static final String LOAN_TYPE_EDUCATIONAL = "EDUCATIONAL";
    
    // Interest calculation methods
    public static final String INTEREST_DAILY = "Daily";
    public static final String INTEREST_MONTHLY = "Monthly";
    public static final String INTEREST_QUARTERLY = "Quarterly";
    public static final String INTEREST_ANNUALLY = "Annually";
    
    // Payment frequencies
    public static final String FREQUENCY_MONTHLY = "Monthly";
    public static final String FREQUENCY_QUARTERLY = "Quarterly";
    public static final String FREQUENCY_SEMI_ANNUALLY = "Semi-Annually";
    public static final String FREQUENCY_ANNUALLY = "Annually";
    
    // Date formats
    public static final String DATE_FORMAT_DISPLAY = "yyyy-MM-dd";
    public static final String DATE_FORMAT_DATABASE = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_SHORT = "MM/dd/yyyy";
    
    // Dormancy period in months
    public static final int DORMANCY_PERIOD_MONTHS = 12;
    
    // Minimum balance for savings account
    public static final double MINIMUM_BALANCE_SAVINGS = 500.0;
    
    // Default interest rates
    public static final double DEFAULT_INTEREST_RATE_SAVINGS = 0.025; // 2.5%
    public static final double DEFAULT_INTEREST_RATE_TIME_DEPOSIT = 0.045; // 4.5%
    public static final double DEFAULT_INTEREST_RATE_SHARE_CAPITAL = 0.07; // 7%
    
    // Aliases for interest rates
    public static final double DEFAULT_SAVINGS_INTEREST_RATE = DEFAULT_INTEREST_RATE_SAVINGS;
    
    // Default loan interest rates
    public static final double DEFAULT_INTEREST_RATE_PERSONAL = 0.12; // 12%
    public static final double DEFAULT_INTEREST_RATE_BUSINESS = 0.15; // 15%
    public static final double DEFAULT_INTEREST_RATE_EMERGENCY = 0.10; // 10%
    public static final double DEFAULT_INTEREST_RATE_HOUSING = 0.09; // 9%
    public static final double DEFAULT_INTEREST_RATE_EDUCATIONAL = 0.08; // 8%
    
    // Maximum loan terms in years
    public static final int MAX_TERM_PERSONAL = 3;
    public static final int MAX_TERM_BUSINESS = 5;
    public static final int MAX_TERM_EMERGENCY = 1;
    public static final int MAX_TERM_HOUSING = 15;
    public static final int MAX_TERM_EDUCATIONAL = 5;
    
    // Maximum loan amounts
    public static final double MAX_AMOUNT_PERSONAL = 500000.0;
    public static final double MAX_AMOUNT_BUSINESS = 1000000.0;
    public static final double MAX_AMOUNT_EMERGENCY = 100000.0;
    public static final double MAX_AMOUNT_HOUSING = 3000000.0;
    public static final double MAX_AMOUNT_EDUCATIONAL = 300000.0;
    
    // Currency format
    public static final String CURRENCY_SYMBOL = "₱";
    public static final String CURRENCY_FORMAT = "###,##0.00";
    
    // ID number prefixes
    public static final String MEMBER_ID_PREFIX = "MEM";
    public static final String ACCOUNT_ID_PREFIX = "ACC";
    public static final String LOAN_ID_PREFIX = "LOAN";
    public static final String TRANSACTION_ID_PREFIX = "TXN";
    
    // Permission modules
    public static final String MODULE_MEMBERS = "MEMBERS";
    public static final String MODULE_SAVINGS = "SAVINGS";
    public static final String MODULE_LOANS = "LOANS";
    public static final String MODULE_REPORTS = "REPORTS";
    public static final String MODULE_ADMIN = "ADMIN";
    
    // Permission codes for Treasurer role
    public static final String[] TREASURER_PERMISSIONS = {
        "MEMBER_VIEW", "MEMBER_ADD", "MEMBER_EDIT",
        "SAVINGS_VIEW", "SAVINGS_CREATE", "SAVINGS_DEPOSIT", "SAVINGS_WITHDRAW",
        "LOAN_VIEW", "LOAN_CREATE", "LOAN_APPROVE", "LOAN_DISBURSE", "LOAN_PAYMENT",
        "REPORT_VIEW", "REPORT_MEMBER", "REPORT_SAVINGS", "REPORT_LOAN"
    };
    
    // Permission codes for Bookkeeper role
    public static final String[] BOOKKEEPER_PERMISSIONS = {
        "MEMBER_VIEW",
        "SAVINGS_VIEW",
        "LOAN_VIEW",
        "REPORT_VIEW", "REPORT_MEMBER", "REPORT_SAVINGS", "REPORT_LOAN", "REPORT_FINANCIAL"
    };
    
    // UI components dimensions
    public static final int TEXT_FIELD_HEIGHT = 30;
    public static final int BUTTON_HEIGHT = 35;
    public static final int TABLE_ROW_HEIGHT = 25;
    public static final int PANEL_SPACING = 10;
}