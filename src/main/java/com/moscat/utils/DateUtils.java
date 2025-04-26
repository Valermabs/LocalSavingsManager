package com.moscat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Utility methods for working with dates
 */
public class DateUtils {
    
    // Date format patterns
    private static final String DISPLAY_DATE_FORMAT = "MMM dd, yyyy";
    private static final String SQL_DATE_FORMAT = "yyyy-MM-dd";
    private static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
    
    /**
     * Formats a date for display in UI
     * 
     * @param date Date to format
     * @return Formatted date string or empty string if date is null
     */
    public static String formatDateForDisplay(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * Formats a date for SQL operations
     * 
     * @param date Date to format
     * @return Formatted date string or empty string if date is null
     */
    public static String formatDateForSQL(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(SQL_DATE_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * Formats a date and time for SQL operations
     * 
     * @param date Date to format
     * @return Formatted datetime string or empty string if date is null
     */
    public static String formatDateTimeForSQL(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(DATETIME_FORMAT);
        return sdf.format(date);
    }
    
    /**
     * Parses a date string in display format
     * 
     * @param dateStr Date string in display format
     * @return Parsed date or null if parsing fails
     */
    public static Date parseDisplayDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DISPLAY_DATE_FORMAT);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Parses a date string in SQL format
     * 
     * @param dateStr Date string in SQL format
     * @return Parsed date or null if parsing fails
     */
    public static Date parseSQLDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(SQL_DATE_FORMAT);
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Converts a java.util.Date to java.sql.Date
     * 
     * @param date Java date
     * @return SQL date or null if the input is null
     */
    public static java.sql.Date toSqlDate(Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }
    
    /**
     * Gets the current date with time set to 00:00:00
     * 
     * @return Current date at start of day
     */
    public static Date getCurrentDateNoTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * Calculates age based on birth date
     * 
     * @param birthDate Birth date
     * @return Age in years
     */
    public static int calculateAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        }
        
        Calendar birthCal = Calendar.getInstance();
        birthCal.setTime(birthDate);
        
        Calendar nowCal = Calendar.getInstance();
        
        int age = nowCal.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR);
        
        // Check if birthday has occurred this year
        if (nowCal.get(Calendar.MONTH) < birthCal.get(Calendar.MONTH) || 
                (nowCal.get(Calendar.MONTH) == birthCal.get(Calendar.MONTH) && 
                nowCal.get(Calendar.DAY_OF_MONTH) < birthCal.get(Calendar.DAY_OF_MONTH))) {
            age--;
        }
        
        return age;
    }
    
    /**
     * Gets the first day of the current month
     * 
     * @return First day of current month
     */
    public static Date getFirstDayOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * Gets the last day of the current month
     * 
     * @return Last day of current month
     */
    public static Date getLastDayOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    
    /**
     * Gets the first day of the given month and year
     * 
     * @param year Year
     * @param month Month (1-12)
     * @return First day of specified month
     */
    public static Date getFirstDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Calendar months are 0-based
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }
    
    /**
     * Gets the last day of the given month and year
     * 
     * @param year Year
     * @param month Month (1-12)
     * @return Last day of specified month
     */
    public static Date getLastDayOfMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1); // Calendar months are 0-based
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }
    
    /**
     * Adds a number of days to a date
     * 
     * @param date Base date
     * @param days Number of days to add (can be negative)
     * @return New date
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }
    
    /**
     * Gets the current date (today)
     * 
     * @return Current date
     */
    public static Date getCurrentDate() {
        return new Date();
    }
    
    /**
     * Gets the current timestamp for database operations
     * 
     * @return Current timestamp as java.sql.Timestamp
     */
    public static java.sql.Timestamp getCurrentTimestamp() {
        return new java.sql.Timestamp(System.currentTimeMillis());
    }
}