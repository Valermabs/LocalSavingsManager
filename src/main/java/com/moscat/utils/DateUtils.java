package com.moscat.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Utility class for date operations
 */
public class DateUtils {
    
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final SimpleDateFormat DISPLAY_DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy");
    private static final SimpleDateFormat DISPLAY_DATE_TIME_FORMAT = new SimpleDateFormat("MMM dd, yyyy hh:mm a");

    private static final DateTimeFormatter LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final DateTimeFormatter DISPLAY_LOCAL_DATE_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy");
    private static final DateTimeFormatter DISPLAY_LOCAL_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm a");
    
    /**
     * Formats a java.util.Date object for database storage
     * 
     * @param date The date to format
     * @return Formatted date string (yyyy-MM-dd)
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }
    
    /**
     * Formats a java.time.LocalDate object for database storage
     * 
     * @param date The date to format
     * @return Formatted date string (yyyy-MM-dd)
     */
    public static String formatLocalDate(LocalDate date) {
        if (date == null) {
            return null;
        }
        return date.format(LOCAL_DATE_FORMATTER);
    }
    
    /**
     * Formats a java.time.LocalDateTime object for database storage
     * 
     * @param dateTime The date/time to format
     * @return Formatted date/time string (yyyy-MM-dd HH:mm:ss)
     */
    public static String formatLocalDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(LOCAL_DATE_TIME_FORMATTER);
    }
    
    /**
     * Formats a java.util.Date object for display
     * 
     * @param date The date to format
     * @return Formatted date string (MMM dd, yyyy)
     */
    public static String formatDateForDisplay(Date date) {
        if (date == null) {
            return "";
        }
        return DISPLAY_DATE_FORMAT.format(date);
    }
    
    /**
     * Formats a java.time.LocalDate object for display
     * 
     * @param date The date to format
     * @return Formatted date string (MMM dd, yyyy)
     */
    public static String formatLocalDateForDisplay(LocalDate date) {
        if (date == null) {
            return "";
        }
        return date.format(DISPLAY_LOCAL_DATE_FORMATTER);
    }
    
    /**
     * Formats a java.time.LocalDateTime object for display
     * 
     * @param dateTime The date/time to format
     * @return Formatted date/time string (MMM dd, yyyy hh:mm a)
     */
    public static String formatDateForDisplay(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DISPLAY_LOCAL_DATE_TIME_FORMATTER);
    }
    
    /**
     * Parses a date string to a java.util.Date object
     * 
     * @param dateStr The date string (yyyy-MM-dd)
     * @return The parsed Date object
     * @throws ParseException If the string cannot be parsed as a date
     */
    public static Date parseDate(String dateStr) throws ParseException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return DATE_FORMAT.parse(dateStr);
    }
    
    /**
     * Parses a date string to a java.time.LocalDate object
     * 
     * @param dateStr The date string (yyyy-MM-dd)
     * @return The parsed LocalDate object
     */
    public static LocalDate parseLocalDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        return LocalDate.parse(dateStr, LOCAL_DATE_FORMATTER);
    }
    
    /**
     * Parses a date/time string to a java.time.LocalDateTime object
     * 
     * @param dateTimeStr The date/time string (yyyy-MM-dd HH:mm:ss)
     * @return The parsed LocalDateTime object
     */
    public static LocalDateTime parseLocalDateTime(String dateTimeStr) {
        if (dateTimeStr == null || dateTimeStr.trim().isEmpty()) {
            return null;
        }
        return LocalDateTime.parse(dateTimeStr, LOCAL_DATE_TIME_FORMATTER);
    }
    
    /**
     * Converts a java.util.Date to java.time.LocalDate
     * 
     * @param date The java.util.Date to convert
     * @return The corresponding LocalDate
     */
    public static LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
    
    /**
     * Converts a java.util.Date to java.time.LocalDateTime
     * 
     * @param date The java.util.Date to convert
     * @return The corresponding LocalDateTime
     */
    public static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    /**
     * Converts a java.time.LocalDate to java.util.Date
     * 
     * @param localDate The LocalDate to convert
     * @return The corresponding java.util.Date
     */
    public static Date convertToDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Converts a java.time.LocalDateTime to java.util.Date
     * 
     * @param localDateTime The LocalDateTime to convert
     * @return The corresponding java.util.Date
     */
    public static Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
    
    /**
     * Get the current date as a java.util.Date
     * 
     * @return The current date
     */
    public static Date getCurrentDate() {
        return new Date();
    }
    
    /**
     * Get the current date as a java.time.LocalDate
     * 
     * @return The current date
     */
    public static LocalDate getCurrentLocalDate() {
        return LocalDate.now();
    }
    
    /**
     * Get the current date and time as a java.time.LocalDateTime
     * 
     * @return The current date and time
     */
    public static LocalDateTime getCurrentLocalDateTime() {
        return LocalDateTime.now();
    }
    
    /**
     * Converts a java.util.Date to java.sql.Date for database operations
     * 
     * @param date The java.util.Date to convert
     * @return The corresponding java.sql.Date
     */
    public static java.sql.Date toSqlDate(java.util.Date date) {
        if (date == null) {
            return null;
        }
        return new java.sql.Date(date.getTime());
    }
    
    /**
     * Converts a java.time.LocalDate to java.sql.Date for database operations
     * 
     * @param localDate The LocalDate to convert
     * @return The corresponding java.sql.Date
     */
    public static java.sql.Date toSqlDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return java.sql.Date.valueOf(localDate);
    }
    
    /**
     * Converts a java.time.LocalDateTime to java.sql.Timestamp for database operations
     * 
     * @param localDateTime The LocalDateTime to convert
     * @return The corresponding java.sql.Timestamp
     */
    public static java.sql.Timestamp toSqlTimestamp(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return java.sql.Timestamp.valueOf(localDateTime);
    }
}