package com.moscat.utils;

/**
 * Application constants
 */
public class Constants {
    
    // User roles
    public static final String ROLE_SUPER_ADMIN = "SUPER_ADMIN";
    public static final String ROLE_TREASURER = "TREASURER";
    public static final String ROLE_BOOKKEEPER = "BOOKKEEPER";
    
    // Member status
    public static final String STATUS_ACTIVE = "ACTIVE";
    public static final String STATUS_INACTIVE = "INACTIVE";
    public static final String STATUS_PENDING = "PENDING";
    
    // Account status
    public static final String ACCOUNT_ACTIVE = "ACTIVE";
    public static final String ACCOUNT_DORMANT = "DORMANT";
    public static final String ACCOUNT_CLOSED = "CLOSED";
    
    // Loan status
    public static final String LOAN_ACTIVE = "ACTIVE";
    public static final String LOAN_PAID = "PAID";
    public static final String LOAN_DEFAULTED = "DEFAULTED";
    public static final String LOAN_RESTRUCTURED = "RESTRUCTURED";
    
    // Transaction types
    public static final String TRANSACTION_DEPOSIT = "DEPOSIT";
    public static final String TRANSACTION_WITHDRAWAL = "WITHDRAWAL";
    public static final String TRANSACTION_INTEREST_EARNING = "INTEREST";
    public static final String TRANSACTION_LOAN_RELEASE = "LOAN_RELEASE";
    public static final String TRANSACTION_LOAN_PAYMENT = "LOAN_PAYMENT";
    public static final String TRANSACTION_FEE = "FEE";
    
    // UI Constants
    public static final int TEXT_FIELD_HEIGHT = 30;
    
    // Database constants
    public static final String DB_URL = "jdbc:h2:./database/moscat";
    public static final String DB_USER = "sa";
    public static final String DB_PASSWORD = "";
    
    // Application settings
    public static final double DEFAULT_SAVINGS_INTEREST_RATE = 0.025; // 2.5% annual interest
    public static final int DORMANT_ACCOUNT_MONTHS = 6; // Account is dormant after 6 months of inactivity
}