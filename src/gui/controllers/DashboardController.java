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

import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

 
public class DashboardController implements Initializable {
    
     
    @FXML private Button btnDashboard;
    @FXML private Button btnProducts;
    @FXML private Button btnInventory;
    @FXML private Button btnBatches;
    @FXML private Button btnSales;
    @FXML private Button btnPurchases;  
    @FXML private Button btnCustomers;
    @FXML private Button btnEmployees;
    @FXML private Button btnReports;
    @FXML private Button btnSuppliers;
    @FXML private Button btnSettings;
    @FXML private Button btnTreasury;
    
     
    @FXML private Label userLabel;
    @FXML private Label roleLabel;
    @FXML private Label dateTimeLabel;
    
     
    @FXML private Label totalSalesLabel;
    @FXML private Label totalProductsLabel;
    @FXML private Label lowStockLabel;
    @FXML private Label totalCustomersLabel;
    
     
    @FXML private TableView<Transaction> recentTransactionsTable;
    @FXML private TableColumn<Transaction, Integer> colInvoiceId;
    @FXML private TableColumn<Transaction, String> colDate;
    @FXML private TableColumn<Transaction, String> colCustomer;
    @FXML private TableColumn<Transaction, Integer> colItems;
    @FXML private TableColumn<Transaction, Double> colTotal;
    @FXML private TableColumn<Transaction, String> colStatus;
    
    @FXML private ListView<String> alertsList;
    
     
    @FXML private StackPane contentArea;
    
    private Button currentActiveButton;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
             
            currentActiveButton = btnDashboard;
            
             
            try {
                util.SessionManager session = util.SessionManager.getInstance();
                if (session.isLoggedIn()) {
                    userLabel.setText("Welcome, " + session.getFullName());
                    roleLabel.setText("Role: " + session.getUserRole());
                    applyRoleBaseAccess();  
                } else {
                    userLabel.setText("Welcome, User");
                    roleLabel.setText("Role: Unknown");
                }
            } catch (Exception e) {
                ExceptionLogger.logException(e, "Error loading session info");
                userLabel.setText("Welcome, User");
                roleLabel.setText("Role: Guest");
            }

            ExceptionLogger.logInfo("Dashboard initialized successfully");
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing dashboard");
            showErrorAlert("Failed to initialize dashboard", e.getMessage());
        }
    }

    private void applyRoleBaseAccess() {
        String role = util.SessionManager.getInstance().getUserRole();
        if (role == null) role = "Cashier";  

        if (role.equalsIgnoreCase("Manager")) {
             
            return; 
        } 
        
        if (role.equalsIgnoreCase("Pharmacist")) {
             
            setButtonVisible(btnTreasury, false);
            setButtonVisible(btnReports, false);
            setButtonVisible(btnEmployees, false);
        } 
        else if (role.equalsIgnoreCase("Cashier") || role.equals("Unknown")) {
             
            
             
            setButtonVisible(btnProducts, true);
            setButtonVisible(btnInventory, true);
            setButtonVisible(btnSales, true);
            setButtonVisible(btnCustomers, true);
            
             
            setButtonVisible(btnBatches, false);
            setButtonVisible(btnPurchases, false);
            setButtonVisible(btnEmployees, false);
            setButtonVisible(btnReports, false);
            setButtonVisible(btnSuppliers, false);
            setButtonVisible(btnSettings, false);
            setButtonVisible(btnTreasury, false);
        }
    }
    
    private void setButtonVisible(Button btn, boolean visible) {
        if (btn != null) {
            btn.setVisible(visible);
            btn.setManaged(visible);  
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
             
             
            totalSalesLabel.setText("$15,240");
            totalProductsLabel.setText("1,234");
            lowStockLabel.setText("23");
            totalCustomersLabel.setText("567");
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error loading dashboard data");
        }
    }
    
    private void setupTableColumns() {
         
         
    }
    
    private void loadRecentTransactions() {
        try {
             
            ObservableList<Transaction> transactions = FXCollections.observableArrayList();
             
            recentTransactionsTable.setItems(transactions);
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error loading recent transactions");
        }
    }
    
    private void loadSystemAlerts() {
        try {
            ObservableList<String> alerts = FXCollections.observableArrayList();
            
             
            alerts.add("⚠️ 5 products are low in stock");
            alerts.add("⚠️ 3 products will expire in 30 days");
            alerts.add("✅ System backup completed successfully");
            
            alertsList.setItems(alerts);
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error loading system alerts");
        }
    }
    
     
    @FXML
    private void showDashboardView() {
        setActiveButton(btnDashboard);
         
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
    private void showPurchasesView() {
        setActiveButton(btnPurchases);
        loadView("/gui/fxml/PurchasesView.fxml");
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
    private void showSuppliersView() {
        setActiveButton(btnSuppliers);
        loadView("/gui/fxml/SuppliersView.fxml");
    }
    
    @FXML
    private void showSettingsView() {
        setActiveButton(btnSettings);
        loadView("/gui/fxml/SettingsView.fxml");
    }
    
    @FXML
    private void showBatchView() {
        setActiveButton(btnBatches);
        loadView("/gui/fxml/BatchView.fxml");
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
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("FXML file not found: " + fxmlPath);
            }
            
            FXMLLoader loader = new FXMLLoader(resource);
            Parent view = loader.load();
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
        } catch (IOException e) {
            ExceptionLogger.logException(e, "Error loading view: " + fxmlPath);
            
             
            StringBuilder errorMessage = new StringBuilder("Could not load view: " + fxmlPath + "\n\n");
            errorMessage.append("Reason: ").append(e.getMessage());
            
            if (e.getCause() != null) {
                errorMessage.append("\nCaused by: ").append(e.getCause().getMessage());
            }
            
            showErrorAlert("View Loading Error", errorMessage.toString());
            e.printStackTrace();  
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Unexpected error loading view: " + fxmlPath);
            showErrorAlert("Unexpected Error", "An unexpected error occurred while loading the view.\n" + e.getMessage());
            e.printStackTrace();
        }
    }
    
     
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

    @FXML
    private void showTreasuryView() {
        setActiveButton(btnTreasury);
        loadView("/gui/fxml/TreasuryView.fxml");
    }
    
     
    @FXML
    private void createNewSale() {
        showSalesView();
         
    }
    
    @FXML
    private void addNewProduct() {
        showProductsView();
         
    }
    
    @FXML
    private void addNewCustomer() {
        showCustomersView();
         
    }
    
    @FXML
    private void generateReport() {
        showReportsView();
    }
    
    @FXML
    private void showAllTransactions() {
        showSalesView();
    }
    
     
    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
     
    public static class Transaction {
        private int invoiceId;
        private String date;
        private String customer;
        private int items;
        private double total;
        private String status;
        
         
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
