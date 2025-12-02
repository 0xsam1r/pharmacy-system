package util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date formatting
 */
public class DateFormatter {
    
    // Common date formatters
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter DISPLAY_DATE_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    public static final DateTimeFormatter DISPLAY_DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    public static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    
    /**
     * Formats LocalDate to database format (yyyy-MM-dd)
     */
    public static String toDatabaseFormat(LocalDate date) {
        if (date == null) return null;
        return date.format(DATE_FORMAT);
    }
    
    /**
     * Formats LocalDateTime to database format (yyyy-MM-dd HH:mm:ss)
     */
    public static String toDatabaseFormat(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(DATETIME_FORMAT);
    }
    
    /**
     * Formats LocalDate to display format (dd/MM/yyyy)
     */
    public static String toDisplayFormat(LocalDate date) {
        if (date == null) return "";
        return date.format(DISPLAY_DATE_FORMAT);
    }
    
    /**
     * Formats LocalDateTime to display format (dd/MM/yyyy HH:mm)
     */
    public static String toDisplayFormat(LocalDateTime dateTime) {
        if (dateTime == null) return "";
        return dateTime.format(DISPLAY_DATETIME_FORMAT);
    }
    
    /**
     * Parses date string from database format
     */
    public static LocalDate parseDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) return null;
        return LocalDate.parse(dateString, DATE_FORMAT);
    }
    
    /**
     * Parses datetime string from database format
     */
    public static LocalDateTime parseDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.trim().isEmpty()) return null;
        return LocalDateTime.parse(dateTimeString, DATETIME_FORMAT);
    }
    
    /**
     * Gets current date in database format
     */
    public static String getCurrentDate() {
        return LocalDate.now().format(DATE_FORMAT);
    }
    
    /**
     * Gets current datetime in database format
     */
    public static String getCurrentDateTime() {
        return LocalDateTime.now().format(DATETIME_FORMAT);
    }
    
    /**
     * Converts java.sql.Date to LocalDate
     */
    public static LocalDate toLocalDate(java.sql.Date sqlDate) {
        if (sqlDate == null) return null;
        return sqlDate.toLocalDate();
    }
    
    /**
     * Converts LocalDate to java.sql.Date
     */
    public static java.sql.Date toSqlDate(LocalDate localDate) {
        if (localDate == null) return null;
        return java.sql.Date.valueOf(localDate);
    }
}
