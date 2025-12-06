package gui.controllers;

import DB.DB_operation;
import exceptions.ValidationException;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.people.Customer;
import util.ExceptionLogger;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

 
public class CustomersController implements Initializable {
    
    @FXML private TextField searchField;
    @FXML private TableView<CustomerData> customersTable;
    @FXML private Label customerCountLabel;
    
    @FXML private TableColumn<CustomerData, String> colCustomerId;
    @FXML private TableColumn<CustomerData, String> colName;
    @FXML private TableColumn<CustomerData, String> colPhone;
    @FXML private TableColumn<CustomerData, Double> colPoints;
    @FXML private TableColumn<CustomerData, Double> colPurchases;
    @FXML private TableColumn<CustomerData, Void> colActions;
    
    private ObservableList<CustomerData> customersList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTableColumns();
            loadCustomers();
            ExceptionLogger.logInfo("Customers view initialized");
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing customers view");
        }
    }
    
    private void setupTableColumns() {
        colCustomerId.setCellValueFactory(new PropertyValueFactory<>("customerId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPoints.setCellValueFactory(new PropertyValueFactory<>("points"));
        colPurchases.setCellValueFactory(new PropertyValueFactory<>("totalPurchases"));
        
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button viewBtn = new Button("👁️ View");
            
            {
                editBtn.setOnAction(event -> {
                    CustomerData customer = getTableView().getItems().get(getIndex());
                    handleEditCustomer(customer);
                });
                
                viewBtn.setOnAction(event -> {
                    CustomerData customer = getTableView().getItems().get(getIndex());
                    handleViewCustomer(customer);
                });
                
                editBtn.getStyleClass().addAll("btn", "btn-secondary");
                editBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10;");
                viewBtn.getStyleClass().addAll("btn", "btn-primary");
                viewBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10;");
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editBtn, viewBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void loadCustomers() {
        try {
            customersList.clear();
            
            String query = "SELECT p.ID, p.name, p.Phone, c.points FROM person p " +
                          "JOIN customer c ON p.ID = c.Person_ID";
            
            try (Connection conn = DB.DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    CustomerData customer = new CustomerData();
                    customer.setCustomerId(rs.getString("ID"));
                    customer.setName(rs.getString("name"));
                    customer.setPhone(rs.getString("Phone"));
                    customer.setPoints(rs.getDouble("points"));
                    customer.setTotalPurchases(0.0);  
                    
                    customersList.add(customer);
                }
                
                customersTable.setItems(customersList);
                updateCustomerCount();
                
            }
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Database error loading customers");
            showError("Database Error", "Failed to load customers");
        }
    }
    
    @FXML
    private void handleAddCustomer() {
        try {
            Dialog<CustomerData> dialog = createCustomerDialog("Add New Customer", null);
            
            Optional<CustomerData> result = dialog.showAndWait();
            result.ifPresent(customer -> {
                try {
                    validateCustomer(customer);
                    
                    Customer c = new Customer();
                    c.setId(customer.getCustomerId());
                    c.setName(customer.getName());
                    c.setPhone(customer.getPhone());
                    c.setPoints(customer.getPoints());
                    
                    if (DB_operation.addCustomer(c)) {
                        showSuccess("Customer added successfully!");
                        loadCustomers();
                        ExceptionLogger.logInfo("Customer added: " + customer.getName());
                    } else {
                        showError("Add Failed", "Failed to add customer");
                    }
                    
                } catch (ValidationException e) {
                    showError("Validation Error", e.getMessage());
                } catch (Exception e) {
                    ExceptionLogger.logException(e, "Error adding customer");
                    showError("Error", "Failed to add customer");
                }
            });
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error in add customer dialog");
        }
    }
    
    private void handleEditCustomer(CustomerData customer) {
        try {
            Dialog<CustomerData> dialog = createCustomerDialog("Edit Customer", customer);
            
            Optional<CustomerData> result = dialog.showAndWait();
            result.ifPresent(editedCustomer -> {
                try {
                    validateCustomer(editedCustomer);
                    
                    if (DB_operation.updateCustomerPoints(editedCustomer.getCustomerId(), editedCustomer.getPoints())) {
                        showSuccess("Customer updated successfully!");
                        loadCustomers();
                        ExceptionLogger.logInfo("Customer updated: " + editedCustomer.getName());
                    } else {
                        showError("Update Failed", "Failed to update customer");
                    }
                    
                } catch (ValidationException e) {
                    showError("Validation Error", e.getMessage());
                } catch (Exception e) {
                    ExceptionLogger.logException(e, "Error updating customer");
                }
            });
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error in edit customer");
        }
    }
    
    private void handleViewCustomer(CustomerData customer) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Customer Details");
        alert.setHeaderText("Customer: " + customer.getName());
        alert.setContentText(
            "ID: " + customer.getCustomerId() + "\n" +
            "Phone: " + customer.getPhone() + "\n" +
            "Points: " + customer.getPoints() + "\n" +
            "Total Purchases: $" + customer.getTotalPurchases()
        );
        alert.showAndWait();
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        
        if (searchText.isEmpty()) {
            customersTable.setItems(customersList);
        } else {
            ObservableList<CustomerData> filtered = customersList.filtered(customer ->
                customer.getName().toLowerCase().contains(searchText) ||
                customer.getPhone().contains(searchText) ||
                customer.getCustomerId().toLowerCase().contains(searchText)
            );
            customersTable.setItems(filtered);
        }
        
        updateCustomerCount();
    }
    
    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadCustomers();
    }
    
    private Dialog<CustomerData> createCustomerDialog(String title, CustomerData existingCustomer) {
        Dialog<CustomerData> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField pointsField = new TextField();
        pointsField.setText("0");
        
        if (existingCustomer != null) {
            idField.setText(existingCustomer.getCustomerId());
            idField.setDisable(true);
            nameField.setText(existingCustomer.getName());
            phoneField.setText(existingCustomer.getPhone());
            pointsField.setText(String.valueOf(existingCustomer.getPoints()));
        }
        
        grid.add(new Label("Customer ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Points:"), 0, 3);
        grid.add(pointsField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                CustomerData customer = new CustomerData();
                customer.setCustomerId(idField.getText());
                customer.setName(nameField.getText());
                customer.setPhone(phoneField.getText());
                try {
                    customer.setPoints(Double.parseDouble(pointsField.getText()));
                } catch (NumberFormatException e) {
                    customer.setPoints(0);
                }
                return customer;
            }
            return null;
        });
        
        return dialog;
    }
    
    private void validateCustomer(CustomerData customer) throws ValidationException {
        if (customer.getCustomerId() == null || customer.getCustomerId().trim().isEmpty()) {
            throw new ValidationException("Customer ID is required", "customerId");
        }
        if (customer.getName() == null || customer.getName().trim().isEmpty()) {
            throw new ValidationException("Customer name is required", "name");
        }
        if (customer.getPhone() == null || customer.getPhone().trim().isEmpty()) {
            throw new ValidationException("Phone number is required", "phone");
        }
    }
    
    private void updateCustomerCount() {
        customerCountLabel.setText("Total: " + customersTable.getItems().size() + " customers");
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
    
     
    public static class CustomerData {
        private SimpleStringProperty customerId = new SimpleStringProperty();
        private SimpleStringProperty name = new SimpleStringProperty();
        private SimpleStringProperty phone = new SimpleStringProperty();
        private SimpleDoubleProperty points = new SimpleDoubleProperty();
        private SimpleDoubleProperty totalPurchases = new SimpleDoubleProperty();
        
        public String getCustomerId() { return customerId.get(); }
        public void setCustomerId(String value) { customerId.set(value); }
        
        public String getName() { return name.get(); }
        public void setName(String value) { name.set(value); }
        
        public String getPhone() { return phone.get(); }
        public void setPhone(String value) { phone.set(value); }
        
        public double getPoints() { return points.get(); }
        public void setPoints(double value) { points.set(value); }
        
        public double getTotalPurchases() { return totalPurchases.get(); }
        public void setTotalPurchases(double value) { totalPurchases.set(value); }
    }
}
