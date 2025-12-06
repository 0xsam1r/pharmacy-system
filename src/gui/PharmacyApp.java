package gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import util.ExceptionLogger;

 
public class PharmacyApp extends Application {
    
    private static Stage primaryStageObj;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            primaryStageObj = primaryStage;
            
             
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LoginScreen.fxml"));
            Parent root = loader.load();
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/gui/css/styles.css").toExternalForm());
            
            primaryStage.setTitle("Pharmacy Management System");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1000);
            primaryStage.setMinHeight(700);
            
             
            try {
                primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/gui/images/icon.png")));
            } catch (Exception e) {
                 
            }
            
             
            primaryStage.setOnCloseRequest(event -> {
                javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
                alert.setTitle("Exit Confirmation");
                alert.setHeaderText("Are you sure you want to exit?");
                alert.setContentText("Click OK to close the Pharmacy Management System.");
                
                java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == javafx.scene.control.ButtonType.OK) {
                    ExceptionLogger.logInfo("Application closed by user");
                } else {
                    event.consume();  
                }
            });
            
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
