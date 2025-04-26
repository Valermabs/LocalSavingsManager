package com.moscat.utils;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

/**
 * Utility methods for date operations
 */
public class DateUtils {

    /**
     * Get current timestamp
     * 
     * @return Current timestamp
     */
    public static Timestamp getCurrentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
    
    /**
     * Get current date
     * 
     * @return Current date
     */
    public static Date getCurrentDate() {
        return new Date();
    }
    
    /**
     * Get current SQL date
     * 
     * @return Current SQL date
     */
    public static java.sql.Date getCurrentSqlDate() {
        return new java.sql.Date(System.currentTimeMillis());
    }
    
    /**
     * Convert java.util.Date to java.sql.Date
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
     * Format date for display (yyyy-MM-dd)
     * 
     * @param date Date to format
     * @return Formatted date string
     */
    public static String formatDateForDisplay(Date date) {
        return formatDate(date, "yyyy-MM-dd");
    }
    
    /**
     * Parse date string to Date
     * 
     * @param dateString Date string
     * @param format Date format
     * @return Date
     * @throws ParseException If date format is invalid
     */
    public static Date parseDate(String dateString, String format) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.parse(dateString);
    }
    
    /**
     * Format Date to string
     * 
     * @param date Date
     * @param format Date format
     * @return Formatted date string
     */
    public static String formatDate(Date date, String format) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    
    /**
     * Get difference between two dates in days
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return Difference in days
     */
    public static long getDaysDifference(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
    
    /**
     * Get difference between two dates in months (approximate)
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return Difference in months
     */
    public static long getMonthsDifference(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        // Get difference in days and approximate months (assuming 30 days per month)
        long diffInDays = getDaysDifference(date1, date2);
        return diffInDays / 30;
    }
    
    /**
     * Check if a date is after another date
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return true if date1 is after date2, false otherwise
     */
    public static boolean isAfter(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.after(date2);
    }
    
    /**
     * Check if a date is before another date
     * 
     * @param date1 First date
     * @param date2 Second date
     * @return true if date1 is before date2, false otherwise
     */
    public static boolean isBefore(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.before(date2);
    }
    
    /**
     * Add days to a date
     * 
     * @param date Date
     * @param days Days to add
     * @return New date
     */
    public static Date addDays(Date date, int days) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime() + TimeUnit.DAYS.toMillis(days));
    }
    
    /**
     * Add months to a date (accurate)
     * 
     * @param date Date
     * @param months Months to add
     * @return New date
     */
    public static Date addMonths(Date date, int months) {
        if (date == null) {
            return null;
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH, months);
        return calendar.getTime();
    }
}