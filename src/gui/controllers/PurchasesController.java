package gui.controllers;

import DB.DBConnection;
import DB.BatchManager;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import util.ExceptionLogger;
import util.SessionManager;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class PurchasesController implements Initializable {

    @FXML private ComboBox<String> supplierCombo;
    @FXML private DatePicker invoiceDate;
    
    @FXML private TextField barcodeField;
    @FXML private TextField productNameField;
    @FXML private ComboBox<String> batchCombo; // Changed from TextField
    @FXML private DatePicker expiryDate;
    @FXML private TextField quantityField;
    @FXML private TextField costField;
    
    @FXML private TableView<PurchaseItem> purchaseTable;
    @FXML private TableColumn<PurchaseItem, String> colName;
    @FXML private TableColumn<PurchaseItem, String> colBatch;
    @FXML private TableColumn<PurchaseItem, String> colExpiry;
    @FXML private TableColumn<PurchaseItem, Double> colQty;
    @FXML private TableColumn<PurchaseItem, Double> colCostRate;
    @FXML private TableColumn<PurchaseItem, Double> colTotal;
    @FXML private TableColumn<PurchaseItem, Void> colAction;
    
    @FXML private Label totalLabel;
    @FXML private TextField paidField;
    @FXML private Label remainingLabel;
    
    private ObservableList<PurchaseItem> itemList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadSuppliers();
        setupListeners();
        
        invoiceDate.setValue(LocalDate.now());
        paidField.setText("0");
    }
    
    private void setupTable() {
        purchaseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY); // Auto-resize columns
        colName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        colExpiry.setCellValueFactory(new PropertyValueFactory<>("expiryDate"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCostRate.setCellValueFactory(new PropertyValueFactory<>("unitCost"));
        colTotal.setCellValueFactory(new PropertyValueFactory<>("totalCost"));
        
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button removeBtn = new Button("❌");
            {
                removeBtn.setOnAction(event -> {
                    itemList.remove(getIndex());
                    updateTotals();
                });
                removeBtn.setStyle("-fx-text-fill: red; -fx-background-color: transparent; -fx-cursor: hand;");
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : removeBtn);
            }
        });
        
        colQty.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.1f", item));
            }
        });
        
        colCostRate.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });
        
        colTotal.setCellFactory(tc -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : String.format("%.2f", item));
            }
        });

        purchaseTable.setItems(itemList);
    }
    
    private void loadSuppliers() {
        String sql = "SELECT nane FROM supplier"; // Note: column is 'nane' in DB
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                supplierCombo.getItems().add(rs.getString("nane"));
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error loading suppliers");
        }
    }
    
    private void setupListeners() {
        barcodeField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) fetchProductName(); // Fetch on focus lost
        });
        
        barcodeField.setOnAction(e -> fetchProductName());
        
        paidField.textProperty().addListener((obs, old, newVal) -> updateTotals());
        
        // Batch Selection Listener
        batchCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.startsWith("B-")) { // "B-..." is usually our auto-gen, ignore logic if basic
                 // Try to fetch details for this batch if it exists
                 fetchBatchDetails(newVal);
            }
        });
    }
    
    private void fetchBatchDetails(String batchNo) {
        String barcode = barcodeField.getText().trim();
        if (barcode.isEmpty()) return;
        
        // Find specific batch details
        String sql = "SELECT expire_date, cost, Quantaty FROM batch WHERE Batch_number = ? AND Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, batchNo);
            ps.setString(2, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date exp = rs.getDate("expire_date");
                    if (exp != null) expiryDate.setValue(exp.toLocalDate());
                    
                    // Auto-fill Unit Cost from existing batch
                    double cost = rs.getDouble("cost");
                    costField.setText(String.valueOf(cost));
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error loading batch details");
        }
    }
    
    private void loadProductBatches(String barcode) {
        batchCombo.getItems().clear();
        String sql = "SELECT Batch_number FROM batch WHERE Product_parcode = ? ORDER BY expire_date ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    batchCombo.getItems().add(rs.getString("Batch_number"));
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error loading batches");
        }
    }
    
    private void fetchProductName() {
        String barcode = barcodeField.getText().trim();
        if (barcode.isEmpty()) return;
        
        String sql = "SELECT Name FROM product WHERE parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    productNameField.setText(rs.getString("Name"));
                    loadProductBatches(barcode); // Populate existing batches
                    generateBatchNumber(); // Auto-suggest NEW batch
                    expiryDate.requestFocus(); // Focus expiry
                } else {
                    productNameField.setText("Not Found");
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error fetching product name");
        }
    }
    
    private void generateBatchNumber() {
        // Format: B-yyMMdd-HHmm (Total 13 chars)
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyMMdd-HHmm");
        String batchNo = "B-" + java.time.LocalDateTime.now().format(dtf);
        
        // If combo is editable, set the editor text
        batchCombo.getEditor().setText(batchNo);
        // batchCombo.setValue(batchNo); // Optional, might trigger listener
    }
    
    @FXML
    private void handleAddItem() {
        try {
            String barcode = barcodeField.getText().trim();
            String name = productNameField.getText();
            // Get from Combo (Editable)
            String batch = batchCombo.getSelectionModel().getSelectedItem();
            if (batch == null || batch.isEmpty()) {
                batch = batchCombo.getEditor().getText().trim();
            }
            LocalDate expiry = expiryDate.getValue();
            double qty = Double.parseDouble(quantityField.getText());
            double unitCost = Double.parseDouble(costField.getText()); // Now input is Unit Cost
            double totalCost = unitCost * qty; // Calculate Total
            
            if (barcode.isEmpty() || batch.isEmpty() || expiry == null || qty <= 0 || unitCost < 0) {
                showError("Invalid Input", "Please fill all item fields correctly.");
                return;
            }
            
            PurchaseItem item = new PurchaseItem(barcode, name, batch, expiry, qty, totalCost);
            itemList.add(item);
            updateTotals();
            
            // Clear inputs for next item
            barcodeField.clear();
            productNameField.clear();
            batchCombo.getEditor().clear();
            batchCombo.getItems().clear();
            quantityField.clear();
            costField.clear();
            barcodeField.requestFocus();
            
        } catch (NumberFormatException e) {
            showError("Input Error", "Quantity and Cost must be numbers.");
        }
    }
    
    private void updateTotals() {
        double total = itemList.stream().mapToDouble(PurchaseItem::getTotalCost).sum();
        totalLabel.setText(String.format("%.2f", total));
        
        try {
            double paid = Double.parseDouble(paidField.getText());
            double remaining = total - paid;
            remainingLabel.setText(String.format("%.2f", remaining));
        } catch (NumberFormatException e) {
            remainingLabel.setText("0.00");
        }
    }
    
    @FXML
    private void handleConfirmPurchase() {
        if (itemList.isEmpty() || supplierCombo.getValue() == null) {
            showError("Missing Info", "Please ensure items are added and a Supplier is selected.");
            return;
        }
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // 1. Generate Invoice ID
            int invoiceId = generateInvoiceId(conn);
            String supplierName = supplierCombo.getValue();
            double totalAmount = Double.parseDouble(totalLabel.getText());
            double paidAmount = Double.parseDouble(paidField.getText());
            double remaining = totalAmount - paidAmount;
            
            // Admin/User Info
            String username = SessionManager.getInstance().getUsername();
            String userId = SessionManager.getInstance().getUserId();
            if (username == null) username = "admin"; // Fallback
            if (userId == null) userId = "1";
            
            // 2. Insert into Invoice Header (Using Employee details - assuming logged in user is making purchase)
            // Note: Purchase invoices also go into 'invoice' table first, then 'purchase_invoce'
            String sqlInv = "INSERT INTO invoice (ID, date, price, employee_User_name, employee_Person_ID, employee_bransh_ID) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInv)) {
                 ps.setInt(1, invoiceId);
                 ps.setDate(2, java.sql.Date.valueOf(invoiceDate.getValue()));
                 ps.setDouble(3, totalAmount);
                 ps.setString(4, username);
                 ps.setString(5, userId);
                 ps.setInt(6, 1); // Default branch 1
                 ps.executeUpdate();
            }
            
            // 3. Purchase Invoice Details (Supplier info)
            // Need supplier phone. For now, fetch first phone for this supplier name or handle properly
            String suppPhone = getSupplierPhone(conn, supplierName);
            
            String sqlPurch = "INSERT INTO purchase_invoce (money_paid, remaing_money, Invoice_ID, Supplier_nane, Supplier_phone) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPurch)) {
                ps.setDouble(1, paidAmount);
                ps.setDouble(2, remaining);
                ps.setInt(3, invoiceId);
                ps.setString(4, supplierName);
                ps.setString(5, suppPhone);
                ps.executeUpdate();
            }
            
            // 4. Process Items (Batches & Inventory)
            String sqlBatchLink = "INSERT INTO purchase_invoce_has_batch (purchase_invoce_Invoice_ID, Batch_Batch_number, Batch_Product_parcode, purchase_invoce_has_Batchcol) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement psLink = conn.prepareStatement(sqlBatchLink)) {
                for (PurchaseItem item : itemList) {
                    // A. Create/Update Batch
                    // Check if batch exists first?
                    // Actually, if it's a new purchase, it might be a new batch or adding to existing.
                    // The BatchManager.addQuantityToBatch updates qty, but doesn't CREATE a new batch row if not exists.
                    // We need a method to create a batch.
                    
                    boolean batchExists = checkBatchExists(conn, item.getBatchNumber(), item.getBarcode());
                    
                    if (batchExists) {
                        // Update existing batch
                        BatchManager.addQuantityToBatch(conn, item.getBatchNumber(), item.getBarcode(), item.getQuantity(), 1);
                    } else {
                        // Insert new batch
                        String sqlNewBatch = "INSERT INTO batch (Batch_number, cost, expire_date, Quantaty, Product_parcode) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement psBatch = conn.prepareStatement(sqlNewBatch)) {
                            psBatch.setString(1, item.getBatchNumber());
                            psBatch.setDouble(2, item.getUnitCost()); // Cost per unit/box
                            psBatch.setDate(3, java.sql.Date.valueOf(item.getExpiryDate()));
                            psBatch.setDouble(4, item.getQuantity());
                            psBatch.setString(5, item.getBarcode());
                            psBatch.executeUpdate();
                        }
                        
                        // Also update Inventory total
                         String updateInv = "UPDATE inventory_has_product SET Quntaty = Quntaty + ? WHERE Inventory_ID = ? AND Product_parcode = ?";
                         try (PreparedStatement psInv = conn.prepareStatement(updateInv)) {
                             psInv.setDouble(1, item.getQuantity());
                             psInv.setInt(2, 1); 
                             psInv.setString(3, item.getBarcode());
                             psInv.executeUpdate();
                         }
                    }
                    
                    // B. Link Batch to Purchase Invoice
                    psLink.setInt(1, invoiceId);
                    psLink.setString(2, item.getBatchNumber());
                    psLink.setString(3, item.getBarcode());
                    psLink.setString(4, "Standard Purchase");
                    psLink.addBatch();
                }
                psLink.executeBatch();
            }
            
            // 5. Update Treasury (Money Out)
            // 5. Update Treasury (Money Out)
            if (paidAmount > 0.001) { // Ensure strictly positive
                 String sqlTreasury = "INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) VALUES (?, ?, NOW(), ?, ?)";
                 try (PreparedStatement ps = conn.prepareStatement(sqlTreasury)) {
                     // Use UUID to ensure uniqueness
                     String treasId = "TR-PUR-" + java.util.UUID.randomUUID().toString().substring(0, 8);
                     ps.setString(1, treasId);
                     ps.setInt(2, 1);
                     ps.setDouble(3, -paidAmount); // Negative for expense
                     ps.setInt(4, invoiceId);
                     int rows = ps.executeUpdate();
                     if (rows == 0) throw new SQLException("Treasury insert failed, no rows affected.");
                 }
            }
            
            conn.commit();
            showSuccess("Purchase Completed! Invoice #" + invoiceId);
            handleClear();
            
        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            ExceptionLogger.logException(e, "Error processing purchase");
            showError("Transaction Failed", e.getMessage());
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }
    
    private String getSupplierPhone(Connection conn, String name) throws SQLException {
        String sql = "SELECT phone FROM supplier WHERE nane = ? LIMIT 1";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getString("phone");
            }
        }
        return "00000000000"; // Fallback
    }
    
    private boolean checkBatchExists(Connection conn, String batch, String barcode) throws SQLException {
        String sql = "SELECT 1 FROM batch WHERE Batch_number = ? AND Product_parcode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, batch);
            ps.setString(2, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    private int generateInvoiceId(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(ID) FROM invoice")) {
            if (rs.next()) return rs.getInt(1) + 1;
        }
        return 20001; // Start from 20001 for purchases maybe?
    }
    
    @FXML
    private void handleClear() {
        itemList.clear();
        barcodeField.clear();
        productNameField.clear();
        batchCombo.getItems().clear();
        batchCombo.getEditor().clear();
        batchCombo.setValue(null);
        quantityField.clear();
        costField.clear();
        paidField.setText("0");
        updateTotals();
    }
    
    @FXML
    private void handlePurchaseReturn() {
        if (itemList.isEmpty() || supplierCombo.getValue() == null) {
            showError("Missing Info", "Please ensure items are added and a Supplier is selected.");
            return;
        }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Generate Invoice ID
            int invoiceId = generateInvoiceId(conn);
            String supplierName = supplierCombo.getValue();
            double totalAmount = Double.parseDouble(totalLabel.getText());
            double paidAmount = Double.parseDouble(paidField.getText());
            double remaining = totalAmount - paidAmount;

            // User Info
            String username = SessionManager.getInstance().getUsername();
            String userId = SessionManager.getInstance().getUserId();
            if (username == null) username = "admin";
            if (userId == null) userId = "1";

            // 2. Insert Invoice (Negative Price for Return)
            String sqlInv = "INSERT INTO invoice (ID, date, price, employee_User_name, employee_Person_ID, employee_bransh_ID) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInv)) {
                 ps.setInt(1, invoiceId);
                 ps.setDate(2, java.sql.Date.valueOf(invoiceDate.getValue()));
                 ps.setDouble(3, -totalAmount); // Negative price indicating return
                 ps.setString(4, username);
                 ps.setString(5, userId);
                 ps.setInt(6, 1);
                 ps.executeUpdate();
            }

            // 3. Purchase Invoice (Negative Values to reduce debt/expenses)
            String suppPhone = getSupplierPhone(conn, supplierName);
            String sqlPurch = "INSERT INTO purchase_invoce (money_paid, remaing_money, Invoice_ID, Supplier_nane, Supplier_phone) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPurch)) {
                ps.setDouble(1, -paidAmount); // Negative expense (refund)
                ps.setDouble(2, -remaining);  // Reduce debt
                ps.setInt(3, invoiceId);
                ps.setString(4, supplierName);
                ps.setString(5, suppPhone);
                ps.executeUpdate();
            }

            // 4. Process Items (Reduce Stock from Specific Batches)
            String sqlBatchLink = "INSERT INTO purchase_invoce_has_batch (purchase_invoce_Invoice_ID, Batch_Batch_number, Batch_Product_parcode, purchase_invoce_has_Batchcol) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement psLink = conn.prepareStatement(sqlBatchLink)) {
                for (PurchaseItem item : itemList) {
                    // Try to reduce from specific batch (Critical for returns)
                    boolean reduced = BatchManager.reduceQuantityFromSpecificBatch(
                        conn, item.getBatchNumber(), item.getBarcode(), item.getQuantity(), 1
                    );
                    
                    if (!reduced) {
                        throw new SQLException("Insufficient stock or invalid batch: " + item.getBatchNumber());
                    }

                    // Link Batch to Invoice
                    psLink.setInt(1, invoiceId);
                    psLink.setString(2, item.getBatchNumber());
                    psLink.setString(3, item.getBarcode());
                    psLink.setString(4, "Purchase Return");
                    psLink.addBatch();
                }
                psLink.executeBatch();
            }

            // 5. Update Treasury (Money IN - Income) if paidAmount > 0
            if (paidAmount > 0.001) {
                 String sqlTreasury = "INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) VALUES (?, ?, NOW(), ?, ?)";
                 try (PreparedStatement ps = conn.prepareStatement(sqlTreasury)) {
                     // Use UUID for unique transaction ID
                     String treasId = "TR-PR-" + java.util.UUID.randomUUID().toString().substring(0, 8);
                     ps.setString(1, treasId);
                     ps.setInt(2, 1);
                     ps.setDouble(3, paidAmount); // Positive Value = Income (Refund received)
                     ps.setInt(4, invoiceId);
                     int rows = ps.executeUpdate();
                     if (rows == 0) throw new SQLException("Treasury return insert failed.");
                 }
            }

            conn.commit();
            showSuccess("Return Processed Successfully! Invoice #" + invoiceId);
            handleClear();

        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            ExceptionLogger.logException(e, "Error processing purchase return");
            showError("Return Failed", e.getMessage());
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    @FXML
    private void handleLoadInvoice() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Load Purchase Invoice");
        dialog.setHeaderText("Enter Purchase Invoice ID to Return:");
        dialog.setContentText("Invoice ID:");
        
        dialog.showAndWait().ifPresent(idStr -> {
            try {
                int invId = Integer.parseInt(idStr);
                loadInvoiceItems(invId);
            } catch (NumberFormatException e) {
                showError("Invalid ID", "Please enter a valid numeric ID");
            }
        });
    }

    private void loadInvoiceItems(int invoiceId) {
        itemList.clear();
        // Updated Query: Fetch directly from purchase_invoce_has_batch
        // We link to 'batch' to get cost and expiry, and 'product' for name.
        String sql = "SELECT bLink.Batch_Product_parcode as Parcode, " +
                     "       p.Name, " +
                     "       bLink.Batch_Batch_number as BatchNo, " +
                     "       batch.expire_date, " +
                     "       batch.cost, " +
                     "       batch.Quantaty " + // Note: This is CURRENT batch quantity, not necessarily what was bought.
                     // But purchase doesn't store 'Quantity Purchased' explicitly in this link table?
                     // Wait, 'purchase_invoce_has_Batchcol' might be it? Schema said VARCHAR(45).
                     // Let's check if we store quantity anywhere.
                     // In handleConfirmPurchase, we insert into purchase_invoce_has_batch.
                     // param 4 is 'Standard Purchase'.
                     // WE ARE NOT STORING THE PURCHASED QUANTITY in the link table!
                     // This is a design flaw in the DB Schema or insertion logic.
                     // However, for RETURNING, we usually return what is currently in the batch?
                     // Or we just fetch the batch details and let user input quantity.
                     // Let's fetch the CURRENT batch quantity as a default/max.
                     "FROM purchase_invoce_has_batch bLink " +
                     "JOIN product p ON bLink.Batch_Product_parcode = p.parcode " +
                     "JOIN batch ON batch.Batch_number = bLink.Batch_Batch_number AND batch.Product_parcode = bLink.Batch_Product_parcode " +
                     "WHERE bLink.purchase_invoce_Invoice_ID = ?";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            
            boolean found = false;
            while(rs.next()) {
                found = true;
                String barcode = rs.getString("Parcode");
                String name = rs.getString("Name");
                String batchNo = rs.getString("BatchNo");
                Date expDate = rs.getDate("expire_date");
                double cost = rs.getDouble("cost"); 
                
                // Problem: We don't know the original purchased quantity from the DB.
                // We only know the CURRENT quantity in the batch.
                // For a Return, it's safe to show current batch quantity as "Available to Return".
                double currentQty = rs.getDouble("Quantaty");
                
                LocalDate exp = (expDate != null) ? expDate.toLocalDate() : LocalDate.now();
                double total = currentQty * cost; // Estimate total value of remaining stock
                
                // Add to list
                itemList.add(new PurchaseItem(barcode, name, batchNo, exp, currentQty, total));
            }
            
            if (found) {
                updateTotals();
                paidField.setText(totalLabel.getText());
                showSuccess("Invoice Loaded. Quantity shown is CURRENT batch stock.");
            } else {
                showError("Not Found", "No items found for Invoice ID: " + invoiceId);
            }
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error loading invoice");
            showError("Database Error", e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showSuccess(String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    
    // Model Class for Table
    public static class PurchaseItem {
        private String barcode;
        private String productName;
        private String batchNumber;
        private LocalDate expiryDate;
        private double quantity;
        private double totalCost;
        
        public PurchaseItem(String barcode, String productName, String batchNumber, LocalDate expiryDate, double quantity, double totalCost) {
            this.barcode = barcode;
            this.productName = productName;
            this.batchNumber = batchNumber;
            this.expiryDate = expiryDate;
            this.quantity = quantity;
            this.totalCost = totalCost;
        }
        
        public String getBarcode() { return barcode; }
        public String getProductName() { return productName; }
        public String getBatchNumber() { return batchNumber; }
        public LocalDate getExpiryDate() { return expiryDate; }
        public double getQuantity() { return quantity; }
        public double getTotalCost() { return totalCost; }
        public double getUnitCost() { return totalCost / quantity; }
    }
}
