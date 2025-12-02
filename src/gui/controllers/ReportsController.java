package gui.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import model.finance.AlertSystem;
import model.finance.ReportGenerator;
import util.ExceptionLogger;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for Reports and Analytics View
 */
public class ReportsController implements Initializable {
    
    @FXML private DatePicker salesReportDate;
    @FXML private DatePicker profitReportDate;
    @FXML private ListView<String> recentReportsList;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Set default dates to today
            salesReportDate.setValue(LocalDate.now());
            profitReportDate.setValue(LocalDate.now());
            
            loadRecentReports();
            
            ExceptionLogger.logInfo("Reports view initialized");
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing reports view");
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
            ReportGenerator.generateSalesReport(dateStr);
            
            showSuccess("Sales report generated successfully!\nCheck the 'reports' folder.");
            ExceptionLogger.logInfo("Sales report generated for date: " + dateStr);
            
            loadRecentReports();
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error generating sales report");
            showError("Report Generation Failed", "Failed to generate sales report: " + e.getMessage());
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
            
            showSuccess("Profit graph generated successfully!\nCheck the 'reports' folder.");
            ExceptionLogger.logInfo("Profit graph generated for date: " + dateStr);
            
            loadRecentReports();
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error generating profit graph");
            showError("Graph Generation Failed", "Failed to generate profit graph: " + e.getMessage());
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
            
            if (alerts.isEmpty()) {
                showInfo("System Alerts", "No alerts found. All systems operating normally.");
            } else {
                StringBuilder alertText = new StringBuilder("System Alerts:\n\n");
                for (String alert : alerts) {
                    alertText.append("• ").append(alert).append("\n");
                }
                
                Alert alertDialog = new Alert(Alert.AlertType.WARNING);
                alertDialog.setTitle("System Alerts");
                alertDialog.setHeaderText("Active Alerts (" + alerts.size() + ")");
                alertDialog.setContentText(alertText.toString());
                alertDialog.showAndWait();
            }
            
            ExceptionLogger.logInfo("Alerts checked: " + alerts.size() + " found");
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error checking alerts");
            showError("Alert Check Failed", "Failed to check system alerts");
        }
    }
    
    private void loadRecentReports() {
        try {
            ObservableList<String> reports = FXCollections.observableArrayList();
            
            // TODO: Load from file system or database
            reports.add("📊 Sales Report - " + LocalDate.now());
            reports.add("📈 Profit Graph - " + LocalDate.now().minusDays(1));
            reports.add("📦 Inventory Report - " + LocalDate.now().minusDays(2));
            
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
