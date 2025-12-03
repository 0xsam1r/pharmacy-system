package gui.controllers;

import DB.DBConnection;
import exceptions.ValidationException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import util.ExceptionLogger;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller for Login Screen
 */
public class LoginController implements Initializable {
    
    @FXML
    private TextField usernameField;
    
    @FXML
    private PasswordField passwordField;
    
    @FXML
    private CheckBox rememberMeCheckbox;
    
    @FXML
    private Label errorLabel;
    
    @FXML
    private Button loginButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Add enter key listener for password field
        passwordField.setOnAction(event -> handleLogin());
        
        // Load saved credentials if any (from preferences)
        loadSavedCredentials();
    }
    
    @FXML
    private void handleLogin() {
        try {
            // Validate inputs
            validateInputs();
            
            // Clear previous error
            errorLabel.setVisible(false);
            
            String username = usernameField.getText().trim();
            String password = passwordField.getText();
            
            // Authenticate user
            if (authenticateUser(username, password)) {
                // Save credentials if remember me is checked
                if (rememberMeCheckbox.isSelected()) {
                    saveCredentials(username);
                }
                
                // Log successful login
                ExceptionLogger.logInfo("User logged in successfully: " + username);
                
                // Navigate to dashboard
                navigateToDashboard();
                
            } else {
                showError("Invalid username or password. Please try again.");
                ExceptionLogger.logWarning("Failed login attempt for username: " + username);
            }
            
        } catch (ValidationException e) {
            showError(e.getMessage());
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Login error");
            showError("An error occurred during login. Please try again.");
        }
    }
    
    @FXML
    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Forgot Password");
        alert.setHeaderText("Password Reset");
        alert.setContentText("Please contact your system administrator to reset your password.");
        alert.showAndWait();
    }
    
    private void validateInputs() throws ValidationException {
        if (usernameField.getText() == null || usernameField.getText().trim().isEmpty()) {
            throw new ValidationException("Username is required", "username");
        }
        
        if (passwordField.getText() == null || passwordField.getText().isEmpty()) {
            throw new ValidationException("Password is required", "password");
        }
    }
    
    private boolean authenticateUser(String username, String password) {
        // Join employee with person table to get the name
        // Using Creation2 schema where employee only has Person_ID (no Person_Phone)
        String sql = "SELECT e.User_name, e.Password, e.Person_ID, p.name " +
                    "FROM employee e " +
                    "JOIN person p ON e.Person_ID = p.ID " +
                    "WHERE e.User_name = ? AND e.Password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password); // Note: In production, use hashed passwords!
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                // User authenticated - store session info
                String userId = rs.getString("Person_ID");
                String fullName = rs.getString("name");
                
                // Deduce role from ID prefix
                String role = "Employee"; // Default
                if (userId != null && !userId.isEmpty()) {
                    char prefix = Character.toUpperCase(userId.charAt(0));
                    if (prefix == 'A') {
                        role = "Admin";
                    } else if (prefix == 'M') {
                        role = "Manager";
                    } else if (prefix == 'P') {
                        role = "Pharmacist";
                    }
                }
                
                // Store in session
                util.SessionManager.getInstance().setUserSession(username, fullName, role, userId);
                
                return true;
            }
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Authentication query failed");
            e.printStackTrace(); // Print stack trace for debugging
        }
        
        return false;
    }
    
    private void navigateToDashboard() {
        try {
            System.out.println("Loading dashboard...");
            ExceptionLogger.logInfo("Attempting to load dashboard");
            
            // Load FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/Dashboard.fxml"));
            // FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/SimpleDashboard.fxml"));
            
            if (loader.getLocation() == null) {
                throw new Exception("Dashboard.fxml not found in resources");
            }
            
            System.out.println("FXML loaded from: " + loader.getLocation());
            Parent root = loader.load();
            System.out.println("Dashboard loaded successfully");
            
            Scene scene = new Scene(root);
            
            // Try to load CSS, but don't fail if it's missing
            try {
                URL cssUrl = getClass().getResource("/gui/css/styles.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                    System.out.println("CSS loaded successfully");
                } else {
                    System.out.println("WARNING: CSS file not found, using default styles");
                    ExceptionLogger.logWarning("CSS file not found at /gui/css/styles.css");
                }
            } catch (Exception cssEx) {
                System.out.println("WARNING: Failed to load CSS: " + cssEx.getMessage());
                ExceptionLogger.logException(cssEx, "CSS loading failed - continuing without styles");
                // Continue anyway - CSS is not critical
            }
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
            
            ExceptionLogger.logInfo("Dashboard displayed successfully");
            
        } catch (java.io.IOException e) {
            System.err.println("IOException while loading dashboard: " + e.getMessage());
            e.printStackTrace();
            ExceptionLogger.logException(e, "IOException - Failed to load Dashboard.fxml");
            showError("Failed to load dashboard file. Please check FXML file exists.");
        } catch (Exception e) {
            System.err.println("Error loading dashboard: " + e.getMessage());
            e.printStackTrace();
            ExceptionLogger.logException(e, "Failed to load dashboard");
            showError("Failed to load dashboard: " + e.getMessage() + "\nPlease check the console for details.");
        }
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    
    private void loadSavedCredentials() {
        // TODO: Implement loading from preferences
        // For now, this is just a placeholder
    }
    
    private void saveCredentials(String username) {
        // TODO: Implement saving to preferences
        // For now, this is just a placeholder
    }
}
