package com.moscat.utils;

import java.text.DecimalFormat;

/**
 * Utility class for string operations
 */
public class StringUtils {
    
    private static final DecimalFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");
    
    /**
     * Converts a double value to a formatted string
     * 
     * @param value The double value
     * @return The formatted string
     */
    public static String formatAmount(double value) {
        return CURRENCY_FORMAT.format(value);
    }
    
    /**
     * Converts a double value to a string
     * 
     * @param value The double value
     * @return The string representation
     */
    public static String toString(double value) {
        return String.valueOf(value);
    }
    
    /**
     * Converts an integer value to a string
     * 
     * @param value The integer value
     * @return The string representation
     */
    public static String toString(int value) {
        return String.valueOf(value);
    }
    
    /**
     * Converts a boolean value to a string
     * 
     * @param value The boolean value
     * @return The string representation
     */
    public static String toString(boolean value) {
        return String.valueOf(value);
    }
    
    /**
     * Checks if a string is null or empty
     * 
     * @param str The string to check
     * @return True if the string is null or empty, false otherwise
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * Truncates a string to a specified length
     * 
     * @param str The string to truncate
     * @param maxLength The maximum length
     * @return The truncated string
     */
    public static String truncate(String str, int maxLength) {
        if (str == null) {
            return null;
        }
        
        if (str.length() <= maxLength) {
            return str;
        }
        
        return str.substring(0, maxLength) + "...";
    }
    
    /**
     * Formats a string as title case
     * 
     * @param str The string to format
     * @return The formatted string
     */
    public static String toTitleCase(String str) {
        if (isEmpty(str)) {
            return str;
        }
        
        StringBuilder result = new StringBuilder(str.length());
        String[] words = str.toLowerCase().split("\\s");
        
        for (String word : words) {
            if (!word.isEmpty()) {
                result.append(Character.toUpperCase(word.charAt(0)))
                      .append(word.substring(1)).append(" ");
            }
        }
        
        return result.toString().trim();
    }
}