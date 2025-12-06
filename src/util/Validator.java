package util;

import exceptions.ValidationException;

 
public class Validator {
    
     
    public static void validateNotEmpty(String value, String fieldName) throws ValidationException {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be empty", fieldName);
        }
    }
    
     
    public static void validatePositive(double value, String fieldName) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be greater than zero", fieldName);
        }
    }
    
     
    public static void validateNonNegative(double value, String fieldName) throws ValidationException {
        if (value < 0) {
            throw new ValidationException(fieldName + " cannot be negative", fieldName);
        }
    }
    
     
    public static void validatePhoneNumber(String phone, String fieldName) throws ValidationException {
        validateNotEmpty(phone, fieldName);
        
         
        String cleanPhone = phone.replaceAll("[\\s-]", "");
        
         
        if (!cleanPhone.matches("\\d+")) {
            throw new ValidationException(fieldName + " must contain only numbers", fieldName);
        }
        
         
        if (cleanPhone.length() < 10 || cleanPhone.length() > 15) {
            throw new ValidationException(fieldName + " must be between 10 and 15 digits", fieldName);
        }
    }
    
     
    public static void validateID(String id, String fieldName) throws ValidationException {
        validateNotEmpty(id, fieldName);
        
        if (id.length() < 3) {
            throw new ValidationException(fieldName + " must be at least 3 characters long", fieldName);
        }
    }
    
     
    public static void validatePrice(double price) throws ValidationException {
        validatePositive(price, "Price");
    }
    
     
    public static void validateQuantity(int quantity) throws ValidationException {
        if (quantity < 0) {
            throw new ValidationException("Quantity cannot be negative", "quantity");
        }
    }
    
     
    public static void validateBarcode(String barcode) throws ValidationException {
        validateNotEmpty(barcode, "Barcode");
        
        if (!barcode.matches("\\d+")) {
            throw new ValidationException("Barcode must contain only numbers", "barcode");
        }
        
        if (barcode.length() < 8) {
            throw new ValidationException("Barcode must be at least 8 digits long", "barcode");
        }
    }
    
     
    public static void validateEmail(String email) throws ValidationException {
        validateNotEmpty(email, "Email");
        
        if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Invalid email format", "email");
        }
    }
    
     
    public static void validateRange(double value, double min, double max, String fieldName) 
            throws ValidationException {
        if (value < min || value > max) {
            throw new ValidationException(
                fieldName + " must be between " + min + " and " + max, 
                fieldName
            );
        }
    }
    
     
    public static void validatePassword(String password) throws ValidationException {
        validateNotEmpty(password, "Password");
        
        if (password.length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long", "password");
        }
    }
}
