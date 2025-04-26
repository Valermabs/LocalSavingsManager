package com.moscat.utils;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

/**
 * Utility class for date operations
 */
public class DateUtils {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy");
    
    /**
     * Converts a string date to a java.sql.Date object
     * 
     * @param dateString Date string in yyyy-MM-dd format
     * @return java.sql.Date object
     * @throws ParseException If the date string cannot be parsed
     */
    public static Date parseDate(String dateString) throws ParseException {
        java.util.Date parsed = DATE_FORMAT.parse(dateString);
        return new Date(parsed.getTime());
    }
    
    /**
     * Formats a java.sql.Date to a display string (Month dd, yyyy)
     * 
     * @param date Date to format
     * @return Formatted date string
     */
    public static String formatDateForDisplay(Date date) {
        if (date == null) return "";
        return DISPLAY_DATE_FORMAT.format(date);
    }
    
    /**
     * Formats a java.sql.Date to a string in yyyy-MM-dd format
     * 
     * @param date Date to format
     * @return Formatted date string
     */
    public static String formatDate(Date date) {
        if (date == null) return "";
        return DATE_FORMAT.format(date);
    }
    
    /**
     * Gets the current date as a java.sql.Date
     * 
     * @return Current date
     */
    public static Date getCurrentDate() {
        return new Date(System.currentTimeMillis());
    }
    
    /**
     * Calculates age from birthdate
     * 
     * @param birthDate Birthdate as java.sql.Date
     * @return Age in years
     */
    public static int calculateAge(Date birthDate) {
        if (birthDate == null) return 0;
        
        LocalDate birthLocalDate = birthDate.toLocalDate();
        LocalDate currentDate = LocalDate.now();
        
        return Period.between(birthLocalDate, currentDate).getYears();
    }
    
    /**
     * Adds months to a date
     * 
     * @param date Base date
     * @param months Number of months to add
     * @return New date with added months
     */
    public static Date addMonths(Date date, int months) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MONTH, months);
        return new Date(cal.getTimeInMillis());
    }
    
    /**
     * Adds years to a date
     * 
     * @param date Base date
     * @param years Number of years to add
     * @return New date with added years
     */
    public static Date addYears(Date date, int years) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.YEAR, years);
        return new Date(cal.getTimeInMillis());
    }
    
    /**
     * Checks if an account is dormant (no activity for 12 months)
     * 
     * @param lastActivityDate Date of last activity
     * @return true if account is dormant, false otherwise
     */
    public static boolean isDormant(Date lastActivityDate) {
        if (lastActivityDate == null) return false;
        
        LocalDate lastActivity = lastActivityDate.toLocalDate();
        LocalDate currentDate = LocalDate.now();
        
        // Calculate months between dates
        Period period = Period.between(lastActivity, currentDate);
        int months = period.getYears() * 12 + period.getMonths();
        
        return months >= 12;
    }
    
    /**
     * Gets the first day of the current month
     * 
     * @return Date representing the first day of the current month
     */
    public static Date getFirstDayOfMonth() {
        LocalDate now = LocalDate.now();
        LocalDate firstDay = LocalDate.of(now.getYear(), now.getMonth(), 1);
        return Date.valueOf(firstDay);
    }
    
    /**
     * Gets the last day of the current month
     * 
     * @return Date representing the last day of the current month
     */
    public static Date getLastDayOfMonth() {
        LocalDate now = LocalDate.now();
        LocalDate lastDay = LocalDate.of(now.getYear(), now.getMonth(), 
                now.getMonth().length(now.isLeapYear()));
        return Date.valueOf(lastDay);
    }
    
    /**
     * Gets the number of days in a month
     * 
     * @param year Year
     * @param month Month (1-12)
     * @return Number of days in the month
     */
    public static int getDaysInMonth(int year, int month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month - 1);
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
    }
    
    /**
     * Gets the number of days in a year
     * 
     * @param year Year
     * @return Number of days in the year
     */
    public static int getDaysInYear(int year) {
        return LocalDate.of(year, 1, 1).isLeapYear() ? 366 : 365;
    }
}
