package gui.controllers;

import DB.DBConnection;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import util.ExceptionLogger;
import util.SessionManager;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

/**
 * Controller for Settings View
 */
public class SettingsController implements Initializable {

    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ExceptionLogger.logInfo("Settings view initialized");
    }

    @FXML
    private void handleChangePassword() {
        String currentPass = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirmPass = confirmPasswordField.getText();

        if (currentPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            showError("Validation Error", "All fields are required.");
            return;
        }

        if (!newPass.equals(confirmPass)) {
            showError("Validation Error", "New password and confirmation do not match.");
            return;
        }

        String username = SessionManager.getInstance().getUsername();
        if (username == null) {
            showError("Session Error", "No active user session found. Please login again.");
            return;
        }

        try {
            if (verifyCurrentPassword(username, currentPass)) {
                if (updatePassword(username, newPass)) {
                    showSuccess("Password updated successfully!");
                    clearPasswordFields();
                } else {
                    showError("Update Failed", "Failed to update password.");
                }
            } else {
                showError("Authentication Error", "Incorrect current password.");
            }
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error changing password");
            showError("System Error", "An error occurred while changing password.");
        }
    }

    private boolean verifyCurrentPassword(String username, String password) throws SQLException {
        String sql = "SELECT Password FROM employee WHERE User_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String dbPass = rs.getString("Password");
                return dbPass.equals(password); // In production, use hashing!
            }
        }
        return false;
    }

    private boolean updatePassword(String username, String newPassword) throws SQLException {
        String sql = "UPDATE employee SET Password = ? WHERE User_name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            return ps.executeUpdate() > 0;
        }
    }

    @FXML
    private void handleBackup() {
        // Mock backup functionality
        showInfo("Database Backup", "Database backup feature is coming soon.\nCurrently, please use MySQL Workbench to export your database.");
    }

    @FXML
    private void handleRestore() {
        // Mock restore functionality
        showInfo("Database Restore", "Database restore feature is coming soon.\nPlease contact administrator for database restoration.");
    }

    private void clearPasswordFields() {
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
    }

    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
