package util;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

/**
 * Utility class for showing standardized alerts and dialogs
 */
public class AlertHelper {
    
    /**
     * Shows an error alert
     */
    public static void showError(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Shows an error alert with default title
     */
    public static void showError(String header, String content) {
        showError("Error", header, content);
    }
    
    /**
     * Shows a success alert
     */
    public static void showSuccess(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Shows a success alert with default title
     */
    public static void showSuccess(String message) {
        showSuccess("Success", null, message);
    }
    
    /**
     * Shows a warning alert
     */
    public static void showWarning(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Shows a warning alert with default title
     */
    public static void showWarning(String header, String content) {
        showWarning("Warning", header, content);
    }
    
    /**
     * Shows an info alert
     */
    public static void showInfo(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    /**
     * Shows an info alert with default title
     */
    public static void showInfo(String header, String content) {
        showInfo("Information", header, content);
    }
    
    /**
     * Shows a confirmation dialog
     * @return true if user clicked OK, false otherwise
     */
    public static boolean showConfirmation(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
    
    /**
     * Shows a confirmation dialog with default title
     */
    public static boolean showConfirmation(String header, String content) {
        return showConfirmation("Confirmation", header, content);
    }
    
    /**
     * Shows a yes/no confirmation dialog
     */
    public static boolean showYesNo(String title, String question) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(question);
        
        ButtonType yesButton = new ButtonType("Yes");
        ButtonType noButton = new ButtonType("No");
        alert.getButtonTypes().setAll(yesButton, noButton);
        
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == yesButton;
    }
}
