package exceptions;

/**
 * Exception thrown when data validation fails
 */
public class ValidationException extends Exception {
    
    private String fieldName;
    
    public ValidationException(String message) {
        super(message);
    }
    
    public ValidationException(String message, String fieldName) {
        super(message);
        this.fieldName = fieldName;
    }
    
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public String getFieldName() {
        return fieldName;
    }
    
    @Override
    public String toString() {
        if (fieldName != null) {
            return "ValidationException [Field: " + fieldName + "]: " + getMessage();
        }
        return "ValidationException: " + getMessage();
    }
}
