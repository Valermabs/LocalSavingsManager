package com.moscat.utils;

import java.util.regex.Pattern;

/**
 * Utility class for input validation
 */
public class ValidationUtils {
    
    // Regular expressions for validation
    private static final Pattern EMAIL_PATTERN = 
            Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    private static final Pattern PHONE_PATTERN = 
            Pattern.compile("^\\+?[0-9]{10,15}$");
    
    private static final Pattern NUMERIC_PATTERN = 
            Pattern.compile("^[0-9]+(\\.[0-9]+)?$");
    
    private static final Pattern NAME_PATTERN = 
            Pattern.compile("^[A-Za-z\\s'-]+$");
    
    /**
     * Validates an email address
     * 
     * @param email Email to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Validates a phone number
     * 
     * @param phone Phone number to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }
    
    /**
     * Validates if a string is numeric (integer or decimal)
     * 
     * @param value String to validate
     * @return true if numeric, false otherwise
     */
    public static boolean isNumeric(String value) {
        if (value == null || value.trim().isEmpty()) {
            return false;
        }
        return NUMERIC_PATTERN.matcher(value).matches();
    }
    
    /**
     * Validates if a string is a valid name (letters, spaces, hyphens, apostrophes)
     * 
     * @param name Name to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        return NAME_PATTERN.matcher(name).matches();
    }
    
    /**
     * Validates if a value is within a range
     * 
     * @param value Value to check
     * @param min Minimum allowed value
     * @param max Maximum allowed value
     * @return true if within range, false otherwise
     */
    public static boolean isInRange(double value, double min, double max) {
        return value >= min && value <= max;
    }
    
    /**
     * Validates if a string is not empty
     * 
     * @param value String to check
     * @return true if not empty, false otherwise
     */
    public static boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    
    /**
     * Validates if a string has a minimum length
     * 
     * @param value String to check
     * @param minLength Minimum required length
     * @return true if long enough, false otherwise
     */
    public static boolean hasMinLength(String value, int minLength) {
        return value != null && value.length() >= minLength;
    }
    
    /**
     * Converts a string to a double, returns 0 if invalid
     * 
     * @param value String to convert
     * @return double value or 0 if invalid
     */
    public static double parseDoubleOrZero(String value) {
        if (isNumeric(value)) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
    
    /**
     * Converts a string to an integer, returns 0 if invalid
     * 
     * @param value String to convert
     * @return int value or 0 if invalid
     */
    public static int parseIntOrZero(String value) {
        if (isNumeric(value)) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}
