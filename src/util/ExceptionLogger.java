package util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logging utility for exceptions and errors
 */
public class ExceptionLogger {
    
    private static final String LOG_DIRECTORY = "logs";
    private static final String LOG_FILE = "pharmacy_errors.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    static {
        // Create logs directory if it doesn't exist
        File logDir = new File(LOG_DIRECTORY);
        if (!logDir.exists()) {
            logDir.mkdir();
        }
    }
    
    /**
     * Logs an exception to file
     */
    public static void logException(Exception e) {
        logException(e, "");
    }
    
    /**
     * Logs an exception with additional context
     */
    public static void logException(Exception e, String context) {
        try (FileWriter fw = new FileWriter(LOG_DIRECTORY + File.separator + LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("=".repeat(80));
            pw.println("TIME: " + LocalDateTime.now().format(FORMATTER));
            if (context != null && !context.isEmpty()) {
                pw.println("CONTEXT: " + context);
            }
            pw.println("EXCEPTION: " + e.getClass().getName());
            pw.println("MESSAGE: " + e.getMessage());
            pw.println("STACK TRACE:");
            e.printStackTrace(pw);
            pw.println("=".repeat(80));
            pw.println();
            
        } catch (IOException ioException) {
            System.err.println("Failed to write to log file: " + ioException.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Logs an error message
     */
    public static void logError(String message) {
        try (FileWriter fw = new FileWriter(LOG_DIRECTORY + File.separator + LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("[ERROR] " + LocalDateTime.now().format(FORMATTER) + " - " + message);
            
        } catch (IOException ioException) {
            System.err.println("Failed to write to log file: " + ioException.getMessage());
        }
    }
    
    /**
     * Logs a warning message
     */
    public static void logWarning(String message) {
        try (FileWriter fw = new FileWriter(LOG_DIRECTORY + File.separator + LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("[WARNING] " + LocalDateTime.now().format(FORMATTER) + " - " + message);
            
        } catch (IOException ioException) {
            System.err.println("Failed to write to log file: " + ioException.getMessage());
        }
    }
    
    /**
     * Logs an info message
     */
    public static void logInfo(String message) {
        try (FileWriter fw = new FileWriter(LOG_DIRECTORY + File.separator + LOG_FILE, true);
             PrintWriter pw = new PrintWriter(fw)) {
            
            pw.println("[INFO] " + LocalDateTime.now().format(FORMATTER) + " - " + message);
            
        } catch (IOException ioException) {
            System.err.println("Failed to write to log file: " + ioException.getMessage());
        }
    }
}
