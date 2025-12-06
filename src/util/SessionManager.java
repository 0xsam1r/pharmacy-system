package util;

/**
 * Session Manager to keep track of logged-in user information
 */
public class SessionManager {
    private static SessionManager instance;
    
    private String username;
    private String userRole;
    private String userId;
    private String fullName;
    private int branchId;
    
    private SessionManager() {
        // Private constructor for singleton
    }
    
    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
       return instance;
    }
    
    public void setUserSession(String username, String fullName, String role, String userId, int branchId) {
        this.username = username;
        this.fullName = fullName;
        this.userRole = role;
        this.userId = userId;
        this.branchId = branchId;
        ExceptionLogger.logInfo("Session started for user: " + username + " (Role: " + role + ", Branch: " + branchId + ")");
    }
    
    public void clearSession() {
        ExceptionLogger.logInfo("Session cleared for user: " + username);
        this.username = null;
        this.fullName = null;
        this.userRole = null;
        this.userId = null;
        this.branchId = 0;
    }
    
    public boolean isLoggedIn() {
        return username != null;
    }
    
    public String getUsername() {
        return username;
    }
    
    public String getUserRole() {
        return userRole;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public String getFullName() {
        return fullName != null ? fullName : username;
    }
    
    public int getBranchId() {
        return branchId;
    }
    
    public boolean hasRole(String role) {
        return userRole != null && userRole.equalsIgnoreCase(role);
    }
    
    public boolean isAdmin() {
        return hasRole("ADMIN") || hasRole("MANAGER");
    }
}
