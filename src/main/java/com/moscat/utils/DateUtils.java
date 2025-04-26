package com.moscat.utils;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Utility class for date operations
 */
public class DateUtils {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Gets the current SQL date
     * 
     * @return Current date as java.sql.Date
     */
    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
    
    /**
     * Gets the current SQL timestamp
     * 
     * @return Current timestamp as java.sql.Timestamp
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Formats a date for display
     * 
     * @param date Date to format
     * @return Formatted date string
     */
    public static String formatDateForDisplay(java.util.Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }
    
    /**
     * Formats a date time for display
     * 
     * @param date Date to format
     * @return Formatted date time string
     */
    public static String formatDateTimeForDisplay(java.util.Date date) {
        if (date == null) {
            return "";
        }
        return DATE_TIME_FORMAT.format(date);
    }
    
    /**
     * Formats a SQL date as string
     * 
     * @param date SQL date to format
     * @return Formatted date string
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date);
    }
    
    /**
     * Parses a date string to java.sql.Date
     * 
     * @param dateStr Date string in yyyy-MM-dd format
     * @return SQL date
     * @throws ParseException If date format is invalid
     */
    public static Date parseDate(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        java.util.Date parsed = DATE_FORMAT.parse(dateStr);
        return new Date(parsed.getTime());
    }
    
    /**
     * Calculates age from birth date
     * 
     * @param birthDate Birth date
     * @return Age in years
     */
    public static int calculateAge(java.util.Date birthDate) {
        if (birthDate == null) {
            return 0;
        }
        
        java.util.Calendar birthCal = java.util.Calendar.getInstance();
        birthCal.setTime(birthDate);
        
        java.util.Calendar currentCal = java.util.Calendar.getInstance();
        
        int age = currentCal.get(java.util.Calendar.YEAR) - birthCal.get(java.util.Calendar.YEAR);
        
        // Adjust age if birthday hasn't occurred yet this year
        if (currentCal.get(java.util.Calendar.DAY_OF_YEAR) < birthCal.get(java.util.Calendar.DAY_OF_YEAR)) {
            age--;
        }
        
        return age;
    }
    
    /**
     * Calculates months between two dates
     * 
     * @param startDate Start date
     * @param endDate End date
     * @return Number of months between dates
     */
    public static int getMonthsBetween(java.util.Date startDate, java.util.Date endDate) {
        if (startDate == null || endDate == null) {
            return 0;
        }
        
        java.util.Calendar startCal = java.util.Calendar.getInstance();
        startCal.setTime(startDate);
        
        java.util.Calendar endCal = java.util.Calendar.getInstance();
        endCal.setTime(endDate);
        
        int yearDiff = endCal.get(java.util.Calendar.YEAR) - startCal.get(java.util.Calendar.YEAR);
        int monthDiff = endCal.get(java.util.Calendar.MONTH) - startCal.get(java.util.Calendar.MONTH);
        
        return yearDiff * 12 + monthDiff;
    }
    
    /**
     * Adds days to a date
     * 
     * @param date Base date
     * @param days Number of days to add
     * @return New date with days added
     */
    public static java.util.Date addDays(java.util.Date date, int days) {
        if (date == null) {
            return null;
        }
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.add(java.util.Calendar.DAY_OF_MONTH, days);
        
        return cal.getTime();
    }
    
    /**
     * Adds months to a date
     * 
     * @param date Base date
     * @param months Number of months to add
     * @return New date with months added
     */
    public static java.util.Date addMonths(java.util.Date date, int months) {
        if (date == null) {
            return null;
        }
        
        java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.setTime(date);
        cal.add(java.util.Calendar.MONTH, months);
        
        return cal.getTime();
    }
    
    /**
     * Converts a java.util.Date to java.sql.Date
     * 
     * @param date Java util date
     * @return SQL date
     */
    public static Date toSqlDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime());
    }
}