package util;

import exceptions.ValidationException;

/**
 * Utility class for input validation
 */
public class Validator {
    
    /**
     * Validates that a string is not null or empty
     */
    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty", fieldName);
        }
    }
    
    /**
     * Validates that a number is positive
     */
    public static void validatePositive(double value, String fieldName) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero", fieldName);
        }
    }
    
    /**
     * Validates that a number is non-negative
     */
    public static void validateNonNegative(double value, String fieldName) throws ValidationException {
        if (value < 0) {
            throw new ValidationException(fieldName + " cannot be negative", fieldName);
        }
    }
    
    /**
     * Validates phone number format (basic)
     */
    public static void validatePhoneNumber(String phone, String fieldName) throws ValidationException {
        validateNotEmpty(phone, fieldName);
        
        // Remove spaces and dashes
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        
        // Check if it contains only digits
        if (!cleanPhone.matches("\\d+")) {
            throw new ValidationException(fieldName + " must contain only numbers", fieldName);
        }
        
        // Check length (assuming Egyptian phone numbers)
        if (cleanPhone.length() < 10 || cleanPhone.length() > 15) {
            throw new ValidationException(fieldName + " must be between 10 and 15 digits", fieldName);
        }
    }
    
    /**
     * Validates ID format
     */
    public static void validateID(String id, String fieldName) throws ValidationException {
        validateNotEmpty(id, fieldName);
        
        if (id.length() < 3) {
            throw new ValidationException(fieldName + " must be at least 3 characters long", fieldName);
        }
    }
    
    /**
     * Validates price
     */
    public static void validatePrice(double price) throws ValidationException {
        validatePositive(price, "Price");
    }
    
    /**
     * Validates quantity
     */
    public static void validateQuantity(int quantity) throws ValidationException {
        if (quantity < 0) {
            throw new ValidationException("Quantity cannot be negative", "quantity");
        }
    }
    
    /**
     * Validates barcode format
     */
    public static void validateBarcode(String barcode) throws ValidationException {
        validateNotEmpty(barcode, "Barcode");
        
        if (!barcode.matches("\\d+")) {
            throw new ValidationException("Barcode must contain only numbers", "barcode");
        }
        
        if (barcode.length() < 8) {
            throw new ValidationException("Barcode must be at least 8 digits long", "barcode");
        }
    }
    
    /**
     * Validates email format (basic)
     */
    public static void validateEmail(String email) throws ValidationException {
        validateNotEmpty(email, "Email");
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid email format", "email");
        }
    }
    
    /**
     * Validates that a value is within a range
     */
    public static void validateRange(double value, double min, double max, String fieldName) 
            throws ValidationException {
        if (value < min || value > max) {
            throw new ValidationException(
                fieldName + " must be between " + min + " and " + max, 
                fieldName
            );
        }
    }
    
    /**
     * Validates password strength (basic)
     */
    public static void validatePassword(String password) throws ValidationException {
        validateNotEmpty(password, "Password");
        
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long", "password");
        }
    }
}
