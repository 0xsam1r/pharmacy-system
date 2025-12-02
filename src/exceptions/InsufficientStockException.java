package exceptions;

/**
 * Exception thrown when there is insufficient stock for a transaction
 */
public class InsufficientStockException extends Exception {
    
    private String productId;
    private double requestedQuantity;
    private double availableQuantity;
    
    public InsufficientStockException(String message) {
        super(message);
    }
    
    public InsufficientStockException(String productId, double requestedQuantity, double availableQuantity) {
        super(String.format("Insufficient stock for product %s. Requested: %.2f, Available: %.2f", 
              productId, requestedQuantity, availableQuantity));
        this.productId = productId;
        this.requestedQuantity = requestedQuantity;
        this.availableQuantity = availableQuantity;
    }
    
    public String getProductId() {
        return productId;
    }
    
    public double getRequestedQuantity() {
        return requestedQuantity;
    }
    
    public double getAvailableQuantity() {
        return availableQuantity;
    }
}
