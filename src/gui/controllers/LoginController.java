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
        String sql = "SELECT COUNT(*) FROM employee WHERE User_name = ? AND Password = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password); // Note: In production, use hashed passwords!
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Authentication query failed");
        }
        
        return false;
    }
    
    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/Dashboard.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/css/styles.css").toExternalForm());
            
            Stage stage = (Stage) loginButton.getScene().getWindow();
            stage.setScene(scene);
            stage.setMaximized(true);
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Failed to load dashboard");
            showError("Failed to load dashboard. Please contact support.");
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
