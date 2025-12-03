package gui.controllers;

import DB.DBConnection;
import exceptions.DatabaseException;
import exceptions.ValidationException;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import util.ExceptionLogger;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for Suppliers Management View
 */
public class SuppliersController implements Initializable {
    
    @FXML private TextField searchField;
    @FXML private TableView<SupplierData> suppliersTable;
    @FXML private Label supplierCountLabel;
    
    // Table Columns
    @FXML private TableColumn<SupplierData, String> colSupplierId;
    @FXML private TableColumn<SupplierData, String> colName;
    @FXML private TableColumn<SupplierData, String> colPhone;
    @FXML private TableColumn<SupplierData, String> colEmail;
    @FXML private TableColumn<SupplierData, String> colAddress;
    @FXML private TableColumn<SupplierData, Double> colTotalDebt;
    @FXML private TableColumn<SupplierData, Void> colActions;
    
    private ObservableList<SupplierData> suppliersList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTableColumns();
            loadSuppliers();
            
            ExceptionLogger.logInfo("Suppliers view initialized");
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing suppliers view");
            showError("Initialization Error", "Failed to load suppliers view");
        }
    }
    
    private void setupTableColumns() {
        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colTotalDebt.setCellValueFactory(new PropertyValueFactory<>("totalDebt"));
        
        // Add action buttons column
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button viewBtn = new Button("👁️ View");
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️");
            
            {
                viewBtn.setOnAction(event -> {
                    SupplierData supplier = getTableView().getItems().get(getIndex());
                    handleViewSupplier(supplier);
                });
                
                editBtn.setOnAction(event -> {
                    SupplierData supplier = getTableView().getItems().get(getIndex());
                    handleEditSupplier(supplier);
                });
                
                deleteBtn.setOnAction(event -> {
                    SupplierData supplier = getTableView().getItems().get(getIndex());
                    handleDeleteSupplier(supplier);
                });
                
                viewBtn.getStyleClass().addAll("btn", "btn-primary");
                viewBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 8;");
                editBtn.getStyleClass().addAll("btn", "btn-secondary");
                editBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 8;");
                deleteBtn.getStyleClass().addAll("btn", "btn-danger");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 8;");
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(3, viewBtn, editBtn, deleteBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void loadSuppliers() {
        suppliersList.clear();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBConnection.getConnection();
            stmt = conn.createStatement();
            
            // Use LEFT JOIN to get suppliers AND their total debt in ONE query
            // This avoids opening a second connection inside the loop which caused the "ResultSet closed" error
            String query;
            try {
                // Try with typo names first (nane, adress)
                query = "SELECT s.nane, s.phone, s.adress, COALESCE(SUM(p.remaing_money), 0) as total_debt " +
                        "FROM supplier s " +
                        "LEFT JOIN purchase_invoce p ON s.nane = p.Supplier_nane AND s.phone = p.Supplier_phone " +
                        "GROUP BY s.nane, s.phone, s.adress";
                rs = stmt.executeQuery(query);
            } catch (SQLException e) {
                // If failed, try with correct spelling (name, address)
                ExceptionLogger.logInfo("Query with typos failed, trying correct spelling...");
                query = "SELECT s.name, s.phone, s.address, COALESCE(SUM(p.remaing_money), 0) as total_debt " +
                        "FROM supplier s " +
                        "LEFT JOIN purchase_invoce p ON s.name = p.Supplier_name AND s.phone = p.Supplier_phone " +
                        "GROUP BY s.name, s.phone, s.address";
                rs = stmt.executeQuery(query);
            }

            while (rs.next()) {
                SupplierData supplier = new SupplierData();
                
                // Handle column names dynamically
                ResultSetMetaData meta = rs.getMetaData();
                String nameCol = "name";
                String addrCol = "address";
                
                for (int i = 1; i <= meta.getColumnCount(); i++) {
                    String col = meta.getColumnName(i).toLowerCase();
                    if (col.equals("nane")) nameCol = "nane";
                    if (col.equals("adress")) addrCol = "adress";
                }
                
                String name = rs.getString(nameCol);
                String phone = rs.getString("phone");
                String address = rs.getString(addrCol);
                double totalDebt = rs.getDouble("total_debt");
                
                supplier.setSupplierId(name);
                supplier.setName(name);
                supplier.setPhone(phone);
                supplier.setEmail("N/A");
                supplier.setAddress(address);
                supplier.setTotalDebt(totalDebt);
                
                suppliersList.add(supplier);
            }
            
            suppliersTable.setItems(suppliersList);
            updateSupplierCount();
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Database error while loading suppliers");
            showError("Database Error", "Failed to load suppliers:\n" + e.getMessage());
        } finally {
            try { if (rs != null) rs.close(); } catch (Exception e) {}
            try { if (stmt != null) stmt.close(); } catch (Exception e) {}
            try { if (conn != null) conn.close(); } catch (Exception e) {}
        }
    }
    
    private double calculateSupplierDebt(String supplierName, String supplierPhone) {
        double totalDebt = 0.0;
        String query = "SELECT COALESCE(SUM(remaing_money), 0) as total_debt " +
                      "FROM purchase_invoce WHERE Supplier_nane = ? AND Supplier_phone = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(query)) {
            
            ps.setString(1, supplierName);
            ps.setString(2, supplierPhone);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                totalDebt = rs.getDouble("total_debt");
            }
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error calculating supplier debt");
        }
        
        return totalDebt;
    }
    
    @FXML
    private void handleAddSupplier() {
        try {
            Dialog<SupplierData> dialog = createSupplierDialog("Add New Supplier", null);
            
            Optional<SupplierData> result = dialog.showAndWait();
            result.ifPresent(supplier -> {
                try {
                    validateSupplier(supplier);
                    
                    // Add to database using nane, phone, adress
                    String sql = "INSERT INTO supplier (nane, phone, adress) VALUES (?, ?, ?)";
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement(sql)) {
                        
                        ps.setString(1, supplier.getName());
                        ps.setString(2, supplier.getPhone());
                        ps.setString(3, supplier.getAddress());
                        
                        int rows = ps.executeUpdate();
                        if (rows > 0) {
                            showSuccess("Supplier added successfully!");
                            loadSuppliers();
                            ExceptionLogger.logInfo("Supplier added: " + supplier.getName());
                        } else {
                            showError("Add Failed", "Failed to add supplier to database");
                        }
                    }
                    
                } catch (ValidationException e) {
                    showError("Validation Error", e.getMessage());
                } catch (Exception e) {
                    ExceptionLogger.logException(e, "Error adding supplier");
                    showError("Error", "Failed to add supplier: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error in add supplier dialog");
            showError("Error", "Failed to open add supplier dialog");
        }
    }
    
    private void handleViewSupplier(SupplierData supplier) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Supplier Details");
        alert.setHeaderText("Supplier Information");
        alert.setContentText(
            "ID: " + supplier.getSupplierId() + "\n" +
            "Name: " + supplier.getName() + "\n" +
            "Phone: " + supplier.getPhone() + "\n" +
            "Email: " + supplier.getEmail() + "\n" +
            "Address: " + supplier.getAddress() + "\n" +
            "Total Debt: $" + String.format("%.2f", supplier.getTotalDebt())
        );
        alert.showAndWait();
    }
    
    private void handleEditSupplier(SupplierData supplier) {
        try {
            Dialog<SupplierData> dialog = createSupplierDialog("Edit Supplier", supplier);
            
            Optional<SupplierData> result = dialog.showAndWait();
            result.ifPresent(editedSupplier -> {
                try {
                    validateSupplier(editedSupplier);
                    
                    // Update in database - need to use old name+phone as key
                    String sql = "UPDATE supplier SET nane = ?, phone = ?, adress = ? WHERE nane = ? AND phone = ?";
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement(sql)) {
                        
                        ps.setString(1, editedSupplier.getName());
                        ps.setString(2, editedSupplier.getPhone());
                        ps.setString(3, editedSupplier.getAddress());
                        ps.setString(4, supplier.getName()); // old name
                        ps.setString(5, supplier.getPhone()); // old phone
                        
                        int rows = ps.executeUpdate();
                        if (rows > 0) {
                            showSuccess("Supplier updated successfully!");
                            loadSuppliers();
                            ExceptionLogger.logInfo("Supplier updated: " + editedSupplier.getName());
                        } else {
                            showError("Update Failed", "Failed to update supplier");
                        }
                    }
                    
                } catch (ValidationException e) {
                    showError("Validation Error", e.getMessage());
                } catch (Exception e) {
                    ExceptionLogger.logException(e, "Error updating supplier");
                    showError("Error", "Failed to update supplier");
                }
            });
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error in edit supplier dialog");
        }
    }
    
    private void handleDeleteSupplier(SupplierData supplier) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Supplier");
            alert.setContentText("Are you sure you want to delete: " + supplier.getName() + "?\n" +
                               "This action cannot be undone.");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        String sql = "DELETE FROM supplier WHERE nane = ? AND phone = ?";
                        try (Connection conn = DBConnection.getConnection();
                             PreparedStatement ps = conn.prepareStatement(sql)) {
                            
                            ps.setString(1, supplier.getName());
                            ps.setString(2, supplier.getPhone());
                            int rows = ps.executeUpdate();
                            
                            if (rows > 0) {
                                showSuccess("Supplier deleted successfully!");
                                loadSuppliers();
                                ExceptionLogger.logInfo("Supplier deleted: " + supplier.getName());
                            } else {
                                showError("Delete Failed", "Failed to delete supplier");
                            }
                        }
                        
                    } catch (SQLException e) {
                        ExceptionLogger.logException(e, "Error deleting supplier");
                        if (e.getMessage().contains("foreign key constraint")) {
                            showError("Cannot Delete", "This supplier has associated purchase invoices and cannot be deleted.");
                        } else {
                            showError("Delete Failed", "Failed to delete supplier");
                        }
                    }
                }
            });
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error in delete supplier");
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        
        if (searchText.isEmpty()) {
            suppliersTable.setItems(suppliersList);
        } else {
            ObservableList<SupplierData> filtered = suppliersList.filtered(supplier ->
                supplier.getName().toLowerCase().contains(searchText) ||
                supplier.getSupplierId().toLowerCase().contains(searchText) ||
                supplier.getPhone().toLowerCase().contains(searchText)
            );
            suppliersTable.setItems(filtered);
        }
        
        updateSupplierCount();
    }
    
    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadSuppliers();
    }
    
    private Dialog<SupplierData> createSupplierDialog(String title, SupplierData existingSupplier) {
        Dialog<SupplierData> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField addressField = new TextField();
        
        if (existingSupplier != null) {
            idField.setText(existingSupplier.getSupplierId());
            idField.setDisable(true);
            nameField.setText(existingSupplier.getName());
            phoneField.setText(existingSupplier.getPhone());
            emailField.setText(existingSupplier.getEmail());
            addressField.setText(existingSupplier.getAddress());
        }
        
        grid.add(new Label("Supplier ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Email:"), 0, 3);
        grid.add(emailField, 1, 3);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                SupplierData supplier = new SupplierData();
                supplier.setSupplierId(idField.getText());
                supplier.setName(nameField.getText());
                supplier.setPhone(phoneField.getText());
                supplier.setEmail(emailField.getText());
                supplier.setAddress(addressField.getText());
                return supplier;
            }
            return null;
        });
        
        return dialog;
    }
    
    private void validateSupplier(SupplierData supplier) throws ValidationException {
        if (supplier.getSupplierId() == null || supplier.getSupplierId().trim().isEmpty()) {
            throw new ValidationException("Supplier ID is required", "supplierId");
        }
        if (supplier.getName() == null || supplier.getName().trim().isEmpty()) {
            throw new ValidationException("Supplier name is required", "name");
        }
    }
    
    private void updateSupplierCount() {
        supplierCountLabel.setText("Total: " + suppliersTable.getItems().size() + " suppliers");
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
    
    // Supplier Data Model
    public static class SupplierData {
        private SimpleStringProperty supplierId = new SimpleStringProperty();
        private SimpleStringProperty name = new SimpleStringProperty();
        private SimpleStringProperty phone = new SimpleStringProperty();
        private SimpleStringProperty email = new SimpleStringProperty();
        private SimpleStringProperty address = new SimpleStringProperty();
        private SimpleDoubleProperty totalDebt = new SimpleDoubleProperty();
        
        public String getSupplierId() { return supplierId.get(); }
        public void setSupplierId(String value) { supplierId.set(value); }
        public SimpleStringProperty supplierIdProperty() { return supplierId; }
        
        public String getName() { return name.get(); }
        public void setName(String value) { name.set(value); }
        public SimpleStringProperty nameProperty() { return name; }
        
        public String getPhone() { return phone.get(); }
        public void setPhone(String value) { phone.set(value); }
        public SimpleStringProperty phoneProperty() { return phone; }
        
        public String getEmail() { return email.get(); }
        public void setEmail(String value) { email.set(value); }
        public SimpleStringProperty emailProperty() { return email; }
        
        public String getAddress() { return address.get(); }
        public void setAddress(String value) { address.set(value); }
        public SimpleStringProperty addressProperty() { return address; }
        
        public double getTotalDebt() { return totalDebt.get(); }
        public void setTotalDebt(double value) { totalDebt.set(value); }
        public SimpleDoubleProperty totalDebtProperty() { return totalDebt; }
    }
}
