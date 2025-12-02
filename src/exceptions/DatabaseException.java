package exceptions;

/**
 * Custom exception for database-related errors
 */
public class DatabaseException extends Exception {
    
    private String errorCode;
    
    public DatabaseException(String message) {
        super(message);
    }
    
    public DatabaseException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public DatabaseException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public DatabaseException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    @Override
    public String toString() {
        if (errorCode != null) {
            return "DatabaseException [" + errorCode + "]: " + getMessage();
        }
        return "DatabaseException: " + getMessage();
    }
}
