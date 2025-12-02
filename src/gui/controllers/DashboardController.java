package gui.controllers;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.ExceptionLogger;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

/**
 * Controller for Main Dashboard
 */
public class DashboardController implements Initializable {
    
    // Sidebar Buttons
    @FXML private Button btnDashboard;
    @FXML private Button btnProducts;
    @FXML private Button btnInventory;
    @FXML private Button btnSales;
    @FXML private Button btnCustomers;
    @FXML private Button btnEmployees;
    @FXML private Button btnReports;
    @FXML private Button btnSettings;
    
    // User Info
    @FXML private Label userLabel;
    @FXML private Label roleLabel;
    @FXML private Label dateTimeLabel;
    
    // Statistics
    @FXML private Label totalSalesLabel;
    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalCustomersLabel;
    
    // Tables and Lists
    @FXML private TableView<Transaction> recentTransactionsTable;
    @FXML private TableColumn<Transaction, Integer> colInvoiceId;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colCustomer;
    @FXML private TableColumn<Transaction, Integer> colItems;
    @FXML private TableColumn<Transaction, Double> colTotal;
    @FXML private TableColumn<Transaction, String> colStatus;
    
    @FXML private ListView<String> alertsList;
    
    // Content Area
    @FXML private StackPane contentArea;
    
    private Button currentActiveButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Set current active button
            currentActiveButton = btnDashboard;
            
            // Start clock
            startClock();
            
            // Load dashboard data
            loadDashboardData();
            
            // Setup table columns
            setupTableColumns();
            
            // Load recent transactions
            loadRecentTransactions();
            
            // Load system alerts
            loadSystemAlerts();
            
            ExceptionLogger.logInfo("Dashboard initialized successfully");
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing dashboard");
            showErrorAlert("Failed to initialize dashboard", e.getMessage());
        }
    }
    
    private void startClock() {
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, dd MMMM yyyy - HH:mm:ss");
            dateTimeLabel.setText("🕒 " + LocalDateTime.now().format(formatter));
        }), new KeyFrame(Duration.seconds(1)));
        
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();
    }
    
    private void loadDashboardData() {
        try {
            // TODO: Load real data from database
            // For now, using sample data
            totalSalesLabel.setText("$15,240");
            totalProductsLabel.setText("1,234");
            lowStockLabel.setText("23");
            totalCustomersLabel.setText("567");
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error loading dashboard data");
        }
    }
    
    private void setupTableColumns() {
        // TODO: Setup table cell value factories
        // This would map Transaction object properties to table columns
    }
    
    private void loadRecentTransactions() {
        try {
            // TODO: Load from database
            ObservableList<Transaction> transactions = FXCollections.observableArrayList();
            // Sample data for demonstration
            recentTransactionsTable.setItems(transactions);
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error loading recent transactions");
        }
    }
    
    private void loadSystemAlerts() {
        try {
            ObservableList<String> alerts = FXCollections.observableArrayList();
            
            // TODO: Load real alerts from AlertSystem
            alerts.add("⚠️ 5 products are low in stock");
            alerts.add("⚠️ 3 products will expire in 30 days");
            alerts.add("✅ System backup completed successfully");
            
            alertsList.setItems(alerts);
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error loading system alerts");
        }
    }
    
    // Navigation Methods
    @FXML
    private void showDashboardView() {
        setActiveButton(btnDashboard);
        // Dashboard is already showing - just refresh data
        loadDashboardData();
    }
    
    @FXML
    private void showProductsView() {
        setActiveButton(btnProducts);
        loadView("/gui/fxml/ProductsView.fxml");
    }
    
    @FXML
    private void showInventoryView() {
        setActiveButton(btnInventory);
        loadView("/gui/fxml/InventoryView.fxml");
    }
    
    @FXML
    private void showSalesView() {
        setActiveButton(btnSales);
        loadView("/gui/fxml/SalesView.fxml");
    }
    
    @FXML
    private void showCustomersView() {
        setActiveButton(btnCustomers);
        loadView("/gui/fxml/CustomersView.fxml");
    }
    
    @FXML
    private void showEmployeesView() {
        setActiveButton(btnEmployees);
        loadView("/gui/fxml/EmployeesView.fxml");
    }
    
    @FXML
    private void showReportsView() {
        setActiveButton(btnReports);
        loadView("/gui/fxml/ReportsView.fxml");
    }
    
    @FXML
    private void showSettingsView() {
        setActiveButton(btnSettings);
        loadView("/gui/fxml/SettingsView.fxml");
    }
    
    private void setActiveButton(Button button) {
        if (currentActiveButton != null) {
            currentActiveButton.getStyleClass().remove("sidebar-button-active");
        }
        button.getStyleClass().add("sidebar-button-active");
        currentActiveButton = button;
    }
    
    private void loadView(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent view = loader.load();
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error loading view: " + fxmlPath);
            showErrorAlert("Error Loading View", "Could not load the requested view. Please try again.");
        }
    }
    
    // Menu Actions
    @FXML
    private void handleLogout() {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Logout");
            alert.setHeaderText("Confirm Logout");
            alert.setContentText("Are you sure you want to logout?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    ExceptionLogger.logInfo("User logged out");
                    
                    // Navigate back to login screen
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/fxml/LoginScreen.fxml"));
                        Parent root = loader.load();
                        
                        Scene scene = new Scene(root);
                        scene.getStylesheets().add(getClass().getResource("/gui/css/styles.css").toExternalForm());
                        
                        Stage stage = (Stage) btnDashboard.getScene().getWindow();
                        stage.setScene(scene);
                        stage.setMaximized(false);
                        stage.centerOnScreen();
                        
                    } catch (Exception e) {
                        ExceptionLogger.logException(e, "Error during logout");
                    }
                }
            });
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error in logout handler");
        }
    }
    
    @FXML
    private void handleExit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Application");
        alert.setHeaderText("Confirm Exit");
        alert.setContentText("Are you sure you want to exit the application?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                ExceptionLogger.logInfo("Application closed by user");
                System.exit(0);
            }
        });
    }
    
    @FXML
    private void handleAbout() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("About");
        alert.setHeaderText("Pharmacy Management System");
        alert.setContentText("Version 1.0\n\nDeveloped by:\n" +
                "- Samir Ahmed\n" +
                "- MM Bayoumi Taha\n" +
                "- Ziad Ahmed\n" +
                "- Mahmoud Elsayed\n\n" +
                "© 2025 All Rights Reserved");
        alert.showAndWait();
    }
    
    // Quick Actions
    @FXML
    private void createNewSale() {
        showSalesView();
        // TODO: Open new sale dialog
    }
    
    @FXML
    private void addNewProduct() {
        showProductsView();
        // TODO: Open add product dialog
    }
    
    @FXML
    private void addNewCustomer() {
        showCustomersView();
        // TODO: Open add customer dialog
    }
    
    @FXML
    private void generateReport() {
        showReportsView();
    }
    
    @FXML
    private void showAllTransactions() {
        showSalesView();
    }
    
    // Helper Methods
    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Inner class for table data (temporary - should use actual Transaction model)
    public static class Transaction {
        private int invoiceId;
        private String date;
        private String customer;
        private int items;
        private double total;
        private String status;
        
        // Getters and setters
        public int getInvoiceId() { return invoiceId; }
        public void setInvoiceId(int invoiceId) { this.invoiceId = invoiceId; }
        
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        
        public String getCustomer() { return customer; }
        public void setCustomer(String customer) { this.customer = customer; }
        
        public int getItems() { return items; }
        public void setItems(int items) { this.items = items; }
        
        public double getTotal() { return total; }
        public void setTotal(double total) { this.total = total; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
    }
}
