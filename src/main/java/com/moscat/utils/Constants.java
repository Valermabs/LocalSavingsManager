package com.moscat.utils;

/**
 * Constants used throughout the application
 */
public class Constants {
    
    // User roles
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_TREASURER = "TREASURER";
    public static final String ROLE_BOOKKEEPER = "BOOKKEEPER";
    
    // User status
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    
    // Account status
    public static final String ACCOUNT_ACTIVE = "ACTIVE";
    public static final String ACCOUNT_DORMANT = "DORMANT";
    public static final String ACCOUNT_CLOSED = "CLOSED";
    
    // Transaction types
    public static final String TRANSACTION_DEPOSIT = "DEPOSIT";
    public static final String TRANSACTION_WITHDRAWAL = "WITHDRAWAL";
    public static final String TRANSACTION_LOAN_PAYMENT = "LOAN_PAYMENT";
    public static final String TRANSACTION_LOAN_RELEASE = "LOAN_RELEASE";
    public static final String TRANSACTION_INTEREST_EARNING = "INTEREST_EARNING";
    
    // Loan status
    public static final String LOAN_PENDING = "PENDING";
    public static final String LOAN_APPROVED = "APPROVED";
    public static final String LOAN_REJECTED = "REJECTED";
    public static final String LOAN_ACTIVE = "ACTIVE";
    public static final String LOAN_PAID = "PAID";
    
    // Loan types
    public static final String LOAN_TYPE_REGULAR = "REGULAR";
    public static final String LOAN_TYPE_PETTY_CASH = "PETTY_CASH";
    public static final String LOAN_TYPE_BONUS = "BONUS";
    
    // Employment status
    public static final String EMPLOYMENT_REGULAR = "REGULAR";
    public static final String EMPLOYMENT_CONTRACT = "CONTRACT OF SERVICE";
    public static final String EMPLOYMENT_JOB_ORDER = "JOB ORDER";
    
    // Interest computation methods
    public static final String INTEREST_MONTHLY = "MONTHLY";
    public static final String INTEREST_DAILY = "DAILY";
    
    // RLPF rate
    public static final double RLPF_RATE = 1.0; // Php 1.00 per Php 1,000.00
    
    // Default decimal format for currency
    public static final String CURRENCY_FORMAT = "#,##0.00";
    
    // Default date format
    public static final String DATE_FORMAT = "yyyy-MM-dd";
    public static final String DISPLAY_DATE_FORMAT = "MMMM dd, yyyy";
    
    // Default page sizes
    public static final int PAGE_SIZE = 20;
    
    // UI Constants
    public static final int PADDING = 10;
    public static final int BUTTON_HEIGHT = 30;
    public static final int TEXT_FIELD_HEIGHT = 25;
    public static final int LABEL_HEIGHT = 20;
}
