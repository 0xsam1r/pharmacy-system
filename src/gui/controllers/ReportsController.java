package gui.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
 
import javafx.util.Duration;
import model.finance.AlertSystem;
import model.finance.ReportGenerator;
import util.ExceptionLogger;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

 
public class ReportsController implements Initializable {
    
    @FXML private DatePicker salesReportDate;
    @FXML private DatePicker profitReportDate;
    @FXML private ListView<String> recentReportsList;
    @FXML private TextArea reportOutputArea;
    @FXML private Button checkAlertsButton;
    @FXML private Label lastAlertCheckLabel;
    
    private Timeline alertCheckTimeline;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
             
            salesReportDate.setValue(LocalDate.now());
            profitReportDate.setValue(LocalDate.now());
            
            loadRecentReports();
            
             
            checkAlertsAutomatically();
            
             
            setupPeriodicAlertCheck();
            
            ExceptionLogger.logInfo("Reports view initialized");
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing reports view");
        }
    }
    
    private void setupPeriodicAlertCheck() {
         
        alertCheckTimeline = new Timeline(new KeyFrame(Duration.hours(6), event -> {
            checkAlertsAutomatically();
        }));
        alertCheckTimeline.setCycleCount(Timeline.INDEFINITE);
        alertCheckTimeline.play();
        
        ExceptionLogger.logInfo("Periodic alert check started (every 6 hours)");
    }
    
    private void checkAlertsAutomatically() {
        try {
            List<String> alerts = AlertSystem.checkAll();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lastAlertCheckLabel.setText("Last checked: " + LocalDateTime.now().format(formatter));
            
             
            if (alerts.isEmpty()) {
                checkAlertsButton.setText("🔔 Check System Alerts (0)");
            } else {
                checkAlertsButton.setText("🔔 Check System Alerts (" + alerts.size() + ")");
                checkAlertsButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            }
            
            ExceptionLogger.logInfo("Automatic alert check completed: " + alerts.size() + " alerts found");
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error in automatic alert check");
        }
    }
    
    @FXML
    private void handleGenerateSalesReport() {
        try {
            LocalDate date = salesReportDate.getValue();
            if (date == null) {
                showError("No Date Selected", "Please select a date for the sales report");
                return;
            }
            
            String dateStr = date.toString();
            
            try {
                ReportGenerator.generateSalesReport(dateStr);
                showSuccess("Sales report generated successfully!\nCheck the 'reports' folder.");
                ExceptionLogger.logInfo("Sales report generated for date: " + dateStr);
                
                loadRecentReports();
                displayReportOutput("sales_report_" + dateStr + ".txt");
            } catch (Exception e) {
                ExceptionLogger.logException(e, "Error in ReportGenerator.generateSalesReport");
                showError("Report Generation Error", 
                    "Could not generate sales report.\n\n" +
                    "Possible reasons:\n" +
                    "• No sales data for this date\n" +
                    "• Database connection issue\n" +
                    "• Reports folder not accessible\n\n" +
                    "Error: " + e.getMessage());
            }
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error generating sales report");
            showError("Report Generation Failed", "An unexpected error occurred.");
        }
    }
    

    
    @FXML
    private void handleGenerateProfitGraph() {
        try {
            LocalDate date = profitReportDate.getValue();
            if (date == null) {
                showError("No Date Selected", "Please select a date for the profit graph");
                return;
            }
            
            String dateStr = date.toString();
            
             
            ReportGenerator.generateProfitGraph(dateStr);
            
             
            String imagePath = "reports/profit_graph_" + dateStr + ".png";
            File imageFile = new File(imagePath);
            
            if (imageFile.exists()) {
                javafx.scene.image.Image image = new javafx.scene.image.Image(imageFile.toURI().toString());
                 
                javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(image);
                imageView.setPreserveRatio(true);
                imageView.setFitWidth(800);
                imageView.setFitHeight(600);
                
                StackPane root = new StackPane(imageView);
                root.setPadding(new javafx.geometry.Insets(10));
                
                javafx.scene.Scene scene = new javafx.scene.Scene(root);
                javafx.stage.Stage stage = new javafx.stage.Stage();
                stage.setTitle("Profit Graph - " + dateStr);
                stage.setScene(scene);
                stage.show();
                
                showSuccess("Graph generated successfully!");
                loadRecentReports();
            } else {
                showError("Error", "Graph image file was not created.");
            }
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error generating profit graph");
            showError("Graph Generation Failed", "An unexpected error occurred: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleInventoryReport() {
        showInfo("Inventory Report", "Inventory report feature coming soon!");
    }
    
    @FXML
    private void handleViewAlerts() {
        try {
            List<String> alerts = AlertSystem.checkAll();
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            lastAlertCheckLabel.setText("Last checked: " + LocalDateTime.now().format(formatter));
            
            if (alerts.isEmpty()) {
                showInfo("System Alerts", "No alerts found. All systems operating normally.");
                checkAlertsButton.setText("🔔 Check System Alerts (0)");
                checkAlertsButton.setStyle("");
            } else {
                StringBuilder alertText = new StringBuilder("System Alerts:\n\n");
                for (String alert : alerts) {
                    alertText.append("• ").append(alert).append("\n");
                }
                
                 
                reportOutputArea.setText(alertText.toString());
                
                 
                Alert alertDialog = new Alert(Alert.AlertType.WARNING);
                alertDialog.setTitle("System Alerts");
                alertDialog.setHeaderText("Active Alerts (" + alerts.size() + ")");
                alertDialog.setContentText(alertText.toString());
                alertDialog.showAndWait();
                
                checkAlertsButton.setText("🔔 Check System Alerts (" + alerts.size() + ")");
                checkAlertsButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
            }
            
            ExceptionLogger.logInfo("Alerts checked: " + alerts.size() + " found");
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error checking alerts");
            showError("Alert Check Failed", "Failed to check system alerts");
        }
    }
    
    @FXML
    private void handleClearOutput() {
        reportOutputArea.clear();
    }
    
    private void displayReportOutput(String fileName) {
        try {
            File reportFile = new File("reports/" + fileName);
            if (reportFile.exists()) {
                String content = Files.readString(reportFile.toPath());
                reportOutputArea.setText(content);
            } else {
                reportOutputArea.setText("Report file not found: " + fileName);
            }
        } catch (IOException e) {
            ExceptionLogger.logException(e, "Error reading report file");
            reportOutputArea.setText("Error reading report file: " + e.getMessage());
        }
    }
    
    private void loadRecentReports() {
        try {
            ObservableList<String> reports = FXCollections.observableArrayList();
            
             
            File reportsDir = new File("reports");
            if (reportsDir.exists() && reportsDir.isDirectory()) {
                File[] reportFiles = reportsDir.listFiles((dir, name) -> 
                    name.endsWith(".txt") || name.endsWith(".png") || name.endsWith(".pdf"));
                
                if (reportFiles != null && reportFiles.length > 0) {
                    for (File file : reportFiles) {
                        String icon = file.getName().contains("sales") ? "📊" : 
                                    file.getName().contains("profit") ? "📈" : "📄";
                        reports.add(icon + " " + file.getName());
                    }
                } else {
                    reports.add("📊 Sales Report - " + LocalDate.now());
                    reports.add("📈 Profit Graph - " + LocalDate.now().minusDays(1));
                    reports.add("📦 Inventory Report - " + LocalDate.now().minusDays(2));
                }
            } else {
                reports.add("📊 Sales Report - " + LocalDate.now());
                reports.add("📈 Profit Graph - " + LocalDate.now().minusDays(1));
                reports.add("📦 Inventory Report - " + LocalDate.now().minusDays(2));
            }
            
            recentReportsList.setItems(reports);
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error loading recent reports");
        }
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
