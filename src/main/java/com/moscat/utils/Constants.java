package com.moscat.utils;

/**
 * Constants used throughout the application
 */
public class Constants {
    
    // Application constants
    public static final int TEXT_FIELD_HEIGHT = 30;
    public static final int BUTTON_HEIGHT = 35;
    
    // User roles
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_TREASURER = "TREASURER";
    public static final String ROLE_BOOKKEEPER = "BOOKKEEPER";
    
    // Status constants
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_PENDING = "PENDING";
    
    // Account status
    public static final String ACCOUNT_ACTIVE = "ACTIVE";
    public static final String ACCOUNT_DORMANT = "DORMANT";
    public static final String ACCOUNT_CLOSED = "CLOSED";
    
    // Loan status
    public static final String LOAN_PENDING = "PENDING";
    public static final String LOAN_APPROVED = "APPROVED";
    public static final String LOAN_ACTIVE = "ACTIVE";
    public static final String LOAN_REJECTED = "REJECTED";
    public static final String LOAN_COMPLETED = "COMPLETED";
    public static final String LOAN_DEFAULT = "DEFAULT";
    
    // Transaction types
    public static final String TRANSACTION_DEPOSIT = "DEPOSIT";
    public static final String TRANSACTION_WITHDRAWAL = "WITHDRAWAL";
    public static final String TRANSACTION_INTEREST_EARNING = "INTEREST_EARNING";
    public static final String TRANSACTION_LOAN_RELEASE = "LOAN_RELEASE";
    public static final String TRANSACTION_LOAN_PAYMENT = "LOAN_PAYMENT";
    public static final String TRANSACTION_FEE = "FEE";
}