package com.moscat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for date operations
 */
public class DateUtils {
    
    private static final SimpleDateFormat DISPLAY_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    private static final SimpleDateFormat DB_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    
    /**
     * Formats a date for display
     * 
     * @param date Date to format
     * @return Formatted date string
     */
    public static String formatDateForDisplay(Date date) {
        if (date == null) {
            return "";
        }
        return DISPLAY_FORMAT.format(date);
    }
    
    /**
     * Formats a date for database storage
     * 
     * @param date Date to format
     * @return Formatted date string
     */
    public static String formatDateForDatabase(Date date) {
        if (date == null) {
            return null;
        }
        return DB_FORMAT.format(date);
    }
    
    /**
     * Formats a timestamp for display
     * 
     * @param timestamp Timestamp to format
     * @return Formatted timestamp string
     */
    public static String formatTimestampForDisplay(Date timestamp) {
        if (timestamp == null) {
            return "";
        }
        return TIMESTAMP_FORMAT.format(timestamp);
    }
    
    /**
     * Parses a date string from the display format
     * 
     * @param dateStr Date string in display format
     * @return Parsed date
     * @throws ParseException if the date string cannot be parsed
     */
    public static Date parseDisplayDate(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return DISPLAY_FORMAT.parse(dateStr);
    }
    
    /**
     * Parses a date string from the database format
     * 
     * @param dateStr Date string in database format
     * @return Parsed date
     * @throws ParseException if the date string cannot be parsed
     */
    public static Date parseDatabaseDate(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        return DB_FORMAT.parse(dateStr);
    }
    
    /**
     * Checks if a string is a valid date in display format
     * 
     * @param dateStr Date string to check
     * @return true if valid, false otherwise
     */
    public static boolean isValidDisplayDate(String dateStr) {
        try {
            if (dateStr == null || dateStr.isEmpty()) {
                return false;
            }
            parseDisplayDate(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    
    /**
     * Converts a java.util.Date to java.sql.Date
     * 
     * @param date Date to convert
     * @return SQL date
     */
    public static java.sql.Date toSqlDate(Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }
    
    /**
     * Gets the current date at midnight
     * 
     * @return Current date at midnight
     */
    public static Date getCurrentDate() {
        try {
            // Get current date
            Date now = new Date();
            // Format and parse to remove time component
            String dateStr = DISPLAY_FORMAT.format(now);
            return DISPLAY_FORMAT.parse(dateStr);
        } catch (ParseException e) {
            return new Date(); // Fallback
        }
    }
}