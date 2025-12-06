package exceptions;

 
public class DuplicateEntryException extends Exception {
    
    private String entityType;
    private String identifier;
    
    public DuplicateEntryException(String message) {
        super(message);
    }
    
    public DuplicateEntryException(String entityType, String identifier) {
        super(String.format("Duplicate %s found with identifier: %s", entityType, identifier));
        this.entityType = entityType;
        this.identifier = identifier;
    }
    
    public String getEntityType() {
        return entityType;
    }
    
    public String getIdentifier() {
        return identifier;
    }
}
