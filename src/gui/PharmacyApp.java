package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.ExceptionLogger;

/**
 * Main JavaFX Application Entry Point
 */
public class PharmacyApp extends Application {
    
    private static Stage primaryStageObj;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStageObj = primaryStage;
            
            // Load login screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LoginScreen.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/css/styles.css").toExternalForm());
            
            primaryStage.setTitle("Pharmacy Management System");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            
            // Set icon (optional)
            try {
                primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/gui/images/icon.png")));
            } catch (Exception e) {
                // Icon not found, continue without it
            }
            
            primaryStage.show();
            
            ExceptionLogger.logInfo("Application started successfully");
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Failed to start application");
            e.printStackTrace();
        }
    }
    
    public static Stage getPrimaryStage() {
        return primaryStageObj;
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}
