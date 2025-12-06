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

 
public class SuppliersController implements Initializable {
    
    @FXML private TextField searchField;
    @FXML private TableView<SupplierData> suppliersTable;
    @FXML private Label supplierCountLabel;
    
     
    @FXML private TableColumn<SupplierData, String> colName;
    @FXML private TableColumn<SupplierData, String> colPhone;
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
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colTotalDebt.setCellValueFactory(new PropertyValueFactory<>("totalDebt"));
        
         
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button payBtn = new Button("💸 Pay");
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️");
            
            {
                payBtn.setOnAction(event -> {
                    SupplierData supplier = getTableView().getItems().get(getIndex());
                    handlePayDebt(supplier);
                });

                editBtn.setOnAction(event -> {
                    SupplierData supplier = getTableView().getItems().get(getIndex());
                    handleEditSupplier(supplier);
                });
                
                deleteBtn.setOnAction(event -> {
                    SupplierData supplier = getTableView().getItems().get(getIndex());
                    handleDeleteSupplier(supplier);
                });
                
                payBtn.getStyleClass().addAll("btn", "btn-warning");
                payBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 8;");
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
                    HBox buttons = new HBox(3, payBtn, editBtn, deleteBtn);
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
            
             
             
            String query;
            try {
                 
                query = "SELECT s.nane, s.phone, s.adress, COALESCE(SUM(p.remaing_money), 0) as total_debt " +
                        "FROM supplier s " +
                        "LEFT JOIN purchase_invoce p ON s.nane = p.Supplier_nane AND s.phone = p.Supplier_phone " +
                        "GROUP BY s.nane, s.phone, s.adress";
                rs = stmt.executeQuery(query);
            } catch (SQLException e) {
                 
                ExceptionLogger.logInfo("Query with typos failed, trying correct spelling...");
                query = "SELECT s.name, s.phone, s.address, COALESCE(SUM(p.remaing_money), 0) as total_debt " +
                        "FROM supplier s " +
                        "LEFT JOIN purchase_invoce p ON s.name = p.Supplier_name AND s.phone = p.Supplier_phone " +
                        "GROUP BY s.name, s.phone, s.address";
                rs = stmt.executeQuery(query);
            }

            while (rs.next()) {
                SupplierData supplier = new SupplierData();
                
                 
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
                
                supplier.setName(name);
                supplier.setPhone(phone);
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
    
    private void handlePayDebt(SupplierData supplier) {
        if (supplier.getTotalDebt() <= 0) {
            showError("No Debt", "This supplier has no outstanding debt.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Repay Debt");
        dialog.setHeaderText("Repay Debt for " + supplier.getName());
        dialog.setContentText(String.format("Total Debt: $%.2f\nEnter Amount:", supplier.getTotalDebt()));
        
        dialog.showAndWait().ifPresent(amtStr -> {
            try {
                double amount = Double.parseDouble(amtStr);
                if (amount <= 0 || amount > supplier.getTotalDebt()) {
                    showError("Invalid Amount", "Please enter a valid amount (Max: " + supplier.getTotalDebt() + ")");
                    return;
                }
                processDebtPayment(supplier, amount);
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a numeric amount.");
            }
        });
    }

    private void processDebtPayment(SupplierData supplier, double amount) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
             
            int invoiceId = 0;
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(ID) FROM invoice")) {
                if (rs.next()) invoiceId = rs.getInt(1) + 1;
                else invoiceId = 1;
            }
            
             
            util.SessionManager session = util.SessionManager.getInstance();
            String username = session.getUsername() != null ? session.getUsername() : "admin";
            String userId = session.getUserId() != null ? session.getUserId() : "1";
            
            String insertInvoice = "INSERT INTO invoice (ID, date, price, employee_User_name, employee_Person_ID, employee_bransh_ID) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertInvoice)) {
                ps.setInt(1, invoiceId);
                ps.setDate(2, java.sql.Date.valueOf(java.time.LocalDate.now()));
                ps.setDouble(3, amount);  
                ps.setString(4, username);
                ps.setString(5, userId);
                ps.setInt(6, 1);  
                ps.executeUpdate();
            }
            
             
            String sqlTreasury = "INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) VALUES (?, ?, NOW(), ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTreasury)) {
                ps.setString(1, "TR-PAY-" + System.currentTimeMillis());
                ps.setInt(2, 1);
                 
                ps.setDouble(3, -amount); 
                ps.setInt(4, invoiceId);  
                ps.executeUpdate();
            }
            
             
            String sqlGetInv = "SELECT Invoice_ID, remaing_money, money_paid FROM purchase_invoce WHERE Supplier_nane = ? AND Supplier_phone = ? AND remaing_money > 0.01 ORDER BY Invoice_ID ASC";
            
            double remainingToPay = amount;
            
            try (PreparedStatement psGet = conn.prepareStatement(sqlGetInv)) {
                psGet.setString(1, supplier.getName());
                psGet.setString(2, supplier.getPhone());
                ResultSet rs = psGet.executeQuery();
                
                String sqlUpdateInv = "UPDATE purchase_invoce SET remaing_money = ?, money_paid = ? WHERE Invoice_ID = ?";
                try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdateInv)) {
                    while (rs.next() && remainingToPay > 0.01) {
                         int pId = rs.getInt("Invoice_ID");
                         double invDebt = rs.getDouble("remaing_money");
                         double invPaid = rs.getDouble("money_paid");
                         
                         double payNow = Math.min(remainingToPay, invDebt);
                         
                         psUpdate.setDouble(1, invDebt - payNow);
                         psUpdate.setDouble(2, invPaid + payNow);
                         psUpdate.setInt(3, pId);
                         psUpdate.addBatch();
                         
                         remainingToPay -= payNow;
                    }
                    psUpdate.executeBatch();
                }
            }
            
            conn.commit();
            showSuccess("Payment of $" + amount + " processed successfully.");
            loadSuppliers();
            
        } catch (SQLException e) {
            try { if(conn!=null) conn.rollback(); } catch(SQLException ex){}
            ExceptionLogger.logException(e, "Error paying debt");
            showError("Payment Failed", e.getMessage());
        } finally {
             try { if(conn!=null) { conn.setAutoCommit(true); conn.close(); } } catch(SQLException ex){}
        }
    }

    @FXML
    private void handleAddSupplier() {
        try {
            Dialog<SupplierData> dialog = createSupplierDialog("Add New Supplier", null);
            
            Optional<SupplierData> result = dialog.showAndWait();
            result.ifPresent(supplier -> {
                try {
                    validateSupplier(supplier);
                    
                     
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
            "Name: " + supplier.getName() + "\n" +
            "Phone: " + supplier.getPhone() + "\n" +
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
                    
                     
                    String sql = "UPDATE supplier SET nane = ?, phone = ?, adress = ? WHERE nane = ? AND phone = ?";
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement(sql)) {
                        
                        ps.setString(1, editedSupplier.getName());
                        ps.setString(2, editedSupplier.getPhone());
                        ps.setString(3, editedSupplier.getAddress());
                        ps.setString(4, supplier.getName());  
                        ps.setString(5, supplier.getPhone());  
                        
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
        
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField addressField = new TextField();
        
        if (existingSupplier != null) {
            nameField.setText(existingSupplier.getName());
            phoneField.setText(existingSupplier.getPhone());
            addressField.setText(existingSupplier.getAddress());
        }
        
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2);
        grid.add(phoneField, 1, 2);
        grid.add(new Label("Address:"), 0, 4);
        grid.add(addressField, 1, 4);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                SupplierData supplier = new SupplierData();
                supplier.setName(nameField.getText());
                supplier.setPhone(phoneField.getText());
                supplier.setAddress(addressField.getText());
                return supplier;
            }
            return null;
        });
        
        return dialog;
    }
    
    private void validateSupplier(SupplierData supplier) throws ValidationException {
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
    
     
    public static class SupplierData {
        private SimpleStringProperty name = new SimpleStringProperty();
        private SimpleStringProperty phone = new SimpleStringProperty();
        private SimpleStringProperty address = new SimpleStringProperty();
        private SimpleDoubleProperty totalDebt = new SimpleDoubleProperty();
        
        public String getName() { return name.get(); }
        public void setName(String value) { name.set(value); }
        public SimpleStringProperty nameProperty() { return name; }
        
        public String getPhone() { return phone.get(); }
        public void setPhone(String value) { phone.set(value); }
        public SimpleStringProperty phoneProperty() { return phone; }
        
        public String getAddress() { return address.get(); }
        public void setAddress(String value) { address.set(value); }
        public SimpleStringProperty addressProperty() { return address; }
        
        public double getTotalDebt() { return totalDebt.get(); }
        public void setTotalDebt(double value) { totalDebt.set(value); }
        public SimpleDoubleProperty totalDebtProperty() { return totalDebt; }
    }
}
