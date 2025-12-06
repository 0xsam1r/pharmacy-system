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
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.util.converter.DoubleStringConverter;
import util.ExceptionLogger;
import util.SessionManager;
import gui.util.TransactionSummary;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class PurchasesController implements Initializable {

    @FXML private ComboBox<String> supplierCombo;
    @FXML private DatePicker invoiceDate;
    
    @FXML private TextField barcodeField;
    @FXML private ComboBox<String> productNameCombo;  
    @FXML private ComboBox<String> batchCombo;  
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
    private boolean isReturnMode = false;
    private double returnPaidRatio = 1.0;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadSuppliers();
        setupListeners();
        setupProductSearch();
        setupProductSearch();
        
        invoiceDate.setValue(LocalDate.now());
        paidField.setText("0");
    }
    
    private void setupTable() {
        purchaseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);  
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
                    updateSuggestedRefund();
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
        
        colQty.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        colQty.setOnEditCommit(event -> {
            PurchaseItem item = event.getRowValue();
            double newQty = event.getNewValue();
            if (newQty <= 0) {
                 showError("Invalid Quantity", "Quantity must be positive.");
                 purchaseTable.refresh();  
                 return;
            }
            item.setQuantity(newQty);
             
            purchaseTable.refresh();
            updateSuggestedRefund();
            updateTotals();
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
        String sql = "SELECT nane FROM supplier";  
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
            if (!newVal) fetchProductName();  
        });
        
        barcodeField.setOnAction(e -> fetchProductName());
        
        paidField.textProperty().addListener((obs, old, newVal) -> updateTotals());
        
         
        batchCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.startsWith("B-")) {  
                  
                 fetchBatchDetails(newVal);
            }
        });
        
         
        productNameCombo.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.isEmpty()) {
                 fetchBarcodeByName(newVal);
            }
        });
    }
    
    private void fetchBatchDetails(String batchNo) {
        String barcode = barcodeField.getText().trim();
        if (barcode.isEmpty()) return;
        
         
        String sql = "SELECT expire_date, cost, Quantaty FROM batch WHERE Batch_number = ? AND Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, batchNo);
            ps.setString(2, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Date exp = rs.getDate("expire_date");
                    if (exp != null) expiryDate.setValue(exp.toLocalDate());
                    
                     
                    double cost = rs.getDouble("cost");
                    costField.setText(String.valueOf(cost));
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error loading batch details");
        }
    }
    
    private void setupProductSearch() {
        productNameCombo.getEditor().textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.length() < 2) return;
            
             
             
            if (productNameCombo.getSelectionModel().getSelectedItem() != null && 
                productNameCombo.getSelectionModel().getSelectedItem().equals(newVal)) return;

            String sql = "SELECT Name FROM product WHERE Name LIKE ? LIMIT 10";
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + newVal + "%");
                try (ResultSet rs = ps.executeQuery()) {
                    ObservableList<String> suggestions = FXCollections.observableArrayList();
                    while (rs.next()) {
                        suggestions.add(rs.getString("Name"));
                    }
                    
                     
                     
                     
                    if (!suggestions.isEmpty()) {
                        productNameCombo.setItems(suggestions);
                        productNameCombo.show();
                    }
                }
            } catch (SQLException e) {
                 
            }
        });
    }

    private void fetchBarcodeByName(String name) {
        String sql = "SELECT parcode FROM product WHERE Name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String barcode = rs.getString("parcode");
                    if (!barcode.equals(barcodeField.getText())) {
                        barcodeField.setText(barcode);
                         
                        loadProductBatches(barcode);
                        generateBatchNumber();
                    }
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error fetching barcode");
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
                    String name = rs.getString("Name");
                    productNameCombo.setValue(name);  
                    productNameCombo.getEditor().setText(name);
                    
                    loadProductBatches(barcode);  
                    generateBatchNumber();  
                    expiryDate.requestFocus();  
                } else {
                    productNameCombo.setValue(null);
                    productNameCombo.getEditor().clear();
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error fetching product name");
        }
    }
    
    private void generateBatchNumber() {
         
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyMMdd-HHmm");
        String batchNo = "B-" + java.time.LocalDateTime.now().format(dtf);
        
         
        batchCombo.getEditor().setText(batchNo);
         
    }
    
    @FXML
    private void handleAddItem() {
        try {
            String barcode = barcodeField.getText().trim();
            String name = productNameCombo.getValue();
            if (name == null || name.isEmpty()) name = productNameCombo.getEditor().getText();
             
            String batch = batchCombo.getSelectionModel().getSelectedItem();
            if (batch == null || batch.isEmpty()) {
                batch = batchCombo.getEditor().getText().trim();
            }
            LocalDate expiry = expiryDate.getValue();
            double qty = Double.parseDouble(quantityField.getText());
            double unitCost = Double.parseDouble(costField.getText());  
            double totalCost = unitCost * qty;  
            
            if (barcode.isEmpty() || batch.isEmpty() || expiry == null || qty <= 0 || unitCost < 0) {
                showError("Invalid Input", "Please fill all item fields correctly.");
                return;
            }
            
            PurchaseItem item = new PurchaseItem(barcode, name, batch, expiry, qty, totalCost);
            itemList.add(item);
            updateTotals();
            
             
            barcodeField.clear();
            productNameCombo.setValue(null);
            productNameCombo.getEditor().clear();
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
    
    private void updateSuggestedRefund() {
        if (isReturnMode) {
            double total = itemList.stream().mapToDouble(PurchaseItem::getTotalCost).sum();
            double suggestedRefund = total * returnPaidRatio;
            
            // Only update if the difference is significant to avoid fighting manual edits during minor rounding
            // But here we want to offer the suggestion whenever the cart content changes significantly
            paidField.setText(String.format("%.2f", suggestedRefund));
        }
    }
    
    @FXML
    private void handleConfirmPurchase() {
        if (itemList.isEmpty() || supplierCombo.getValue() == null) {
            showError("Missing Info", "Please ensure items are added and a Supplier is selected.");
            return;
        }
        
         
         
        TransactionSummary.clearSnapshots();
        double initialDebt = 0;
        try (Connection preConn = DBConnection.getConnection()) {
            for (PurchaseItem item : itemList) {
                TransactionSummary.snapshotState(preConn, item.getBarcode());
            }
            initialDebt = getSupplierDebt(preConn, supplierCombo.getValue());
        } catch (SQLException e) { ExceptionLogger.logException(e, "Error snapshotting purchase state"); }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
             
            int invoiceId = generateInvoiceId(conn);
            String supplierName = supplierCombo.getValue();
            double totalAmount = Double.parseDouble(totalLabel.getText());
            double paidAmount = Double.parseDouble(paidField.getText());
            double remaining = totalAmount - paidAmount;
            
             
            String username = SessionManager.getInstance().getUsername();
            String userId = SessionManager.getInstance().getUserId();
            if (username == null) username = "admin";  
            if (userId == null) userId = "1";
            
             
             
            String sqlInv = "INSERT INTO invoice (ID, date, price, employee_User_name, employee_Person_ID, employee_bransh_ID) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInv)) {
                 ps.setInt(1, invoiceId);
                 ps.setDate(2, java.sql.Date.valueOf(invoiceDate.getValue()));
                 ps.setDouble(3, totalAmount);
                 ps.setString(4, username);
                 ps.setString(5, userId);
                 ps.setInt(6, 1);  
                 ps.executeUpdate();
            }
            
             
             
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
            
             
            String sqlBatchLink = "INSERT INTO purchase_invoce_has_batch (purchase_invoce_Invoice_ID, Batch_Batch_number, Batch_Product_parcode, purchase_invoce_has_Batchcol) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement psLink = conn.prepareStatement(sqlBatchLink)) {
                for (PurchaseItem item : itemList) {
                     
                    boolean batchExists = checkBatchExists(conn, item.getBatchNumber(), item.getBarcode());
                    
                    if (batchExists) {
                         
                        BatchManager.addQuantityToBatch(conn, item.getBatchNumber(), item.getBarcode(), item.getQuantity(), 1);
                    } else {
                         
                        String sqlNewBatch = "INSERT INTO batch (Batch_number, cost, expire_date, Quantaty, Product_parcode) VALUES (?, ?, ?, ?, ?)";
                        try (PreparedStatement psBatch = conn.prepareStatement(sqlNewBatch)) {
                            psBatch.setString(1, item.getBatchNumber());
                            psBatch.setDouble(2, item.getUnitCost());
                            psBatch.setDate(3, java.sql.Date.valueOf(item.getExpiryDate()));
                            psBatch.setDouble(4, item.getQuantity());
                            psBatch.setString(5, item.getBarcode());
                            psBatch.executeUpdate();
                        }
                        
                          
                          String updateInv = "UPDATE inventory_has_product SET Quntaty = Quntaty + ? WHERE Inventory_ID = ? AND Product_parcode = ?";
                          try (PreparedStatement psInv = conn.prepareStatement(updateInv)) {
                              psInv.setDouble(1, item.getQuantity());
                              psInv.setInt(2, 1); 
                              psInv.setString(3, item.getBarcode());
                              int rowsInv = psInv.executeUpdate();
                              
                              if (rowsInv == 0) {
                                   
                                  String insertInv = "INSERT INTO inventory_has_product (Inventory_ID, Product_parcode, Quntaty, reordr_level) VALUES (?, ?, ?, ?)";
                                  try (PreparedStatement psInsert = conn.prepareStatement(insertInv)) {
                                      psInsert.setInt(1, 1);
                                      psInsert.setString(2, item.getBarcode());
                                      psInsert.setDouble(3, item.getQuantity());
                                      psInsert.setInt(4, 10);  
                                      psInsert.executeUpdate();
                                  }
                              }
                          }
                    }
                    
                     
                    psLink.setInt(1, invoiceId);
                    psLink.setString(2, item.getBatchNumber());
                    psLink.setString(3, item.getBarcode());
                    psLink.setString(4, "Standard Purchase");
                    psLink.addBatch();
                }
                psLink.executeBatch();
            }
            
             
             
            if (paidAmount > 0.001) {  
                 String sqlTreasury = "INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) VALUES (?, ?, NOW(), ?, ?)";
                 try (PreparedStatement ps = conn.prepareStatement(sqlTreasury)) {
                      
                     String treasId = "TR-PUR-" + java.util.UUID.randomUUID().toString().substring(0, 8);
                     ps.setString(1, treasId);
                     ps.setInt(2, 1);
                     ps.setDouble(3, -paidAmount);  
                     ps.setInt(4, invoiceId);
                     int rows = ps.executeUpdate();
                     if (rows == 0) throw new SQLException("Treasury insert failed, no rows affected.");
                 }
            }  

             
            double finalDebt = getSupplierDebt(conn, supplierName);
            String finSummary = String.format("Purchase Invoice: #%d\nTotal: $%.2f\nPaid: $%.2f\nDebt Added: $%.2f\nSupplier: %s\nTotal Debt: $%.2f ➔ $%.2f", 
                                              invoiceId, totalAmount, paidAmount, remaining, supplierName, initialDebt, finalDebt);
            TransactionSummary.showSummary(conn, "Purchase Transaction Complete", finSummary);

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
        return "00000000000";  
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
        return 20001;  
    }
    
    @FXML
    private void handleClear() {
        itemList.clear();
        barcodeField.clear();
        barcodeField.clear();
        productNameCombo.setValue(null);
        productNameCombo.getEditor().clear();
        batchCombo.getItems().clear();
        batchCombo.getEditor().clear();
        batchCombo.setValue(null);
        quantityField.clear();
        costField.clear();
        paidField.setText("0");
        isReturnMode = false;
        returnPaidRatio = 1.0;
        updateTotals();
    }
    
    @FXML
    private void handlePurchaseReturn() {
        if (itemList.isEmpty() || supplierCombo.getValue() == null) {
            showError("Missing Info", "Please ensure items are added and a Supplier is selected.");
            return;
        }

         
        TransactionSummary.clearSnapshots();
        double initialDebt = 0;
        try (Connection preConn = DBConnection.getConnection()) {
             for (PurchaseItem item : itemList) {
                 TransactionSummary.snapshotState(preConn, item.getBarcode());
             }
             initialDebt = getSupplierDebt(preConn, supplierCombo.getValue());
        } catch (Exception e) {}

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

             
            int invoiceId = generateInvoiceId(conn);
            String supplierName = supplierCombo.getValue();
            double totalAmount = Double.parseDouble(totalLabel.getText());
            double paidAmount = Double.parseDouble(paidField.getText());
            double remaining = totalAmount - paidAmount;

             
            String username = SessionManager.getInstance().getUsername();
            String userId = SessionManager.getInstance().getUserId();
            if (username == null) username = "admin";
            if (userId == null) userId = "1";

             
            String sqlInv = "INSERT INTO invoice (ID, date, price, employee_User_name, employee_Person_ID, employee_bransh_ID) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInv)) {
                 ps.setInt(1, invoiceId);
                 ps.setDate(2, java.sql.Date.valueOf(invoiceDate.getValue()));
                 ps.setDouble(3, -totalAmount);  
                 ps.setString(4, username);
                 ps.setString(5, userId);
                 ps.setInt(6, 1);
                 ps.executeUpdate();
            }

             
            String suppPhone = getSupplierPhone(conn, supplierName);
            String sqlPurch = "INSERT INTO purchase_invoce (money_paid, remaing_money, Invoice_ID, Supplier_nane, Supplier_phone) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlPurch)) {
                ps.setDouble(1, -paidAmount);  
                ps.setDouble(2, -remaining);   
                ps.setInt(3, invoiceId);
                ps.setString(4, supplierName);
                ps.setString(5, suppPhone);
                ps.executeUpdate();
            }

             
            String sqlBatchLink = "INSERT INTO purchase_invoce_has_batch (purchase_invoce_Invoice_ID, Batch_Batch_number, Batch_Product_parcode, purchase_invoce_has_Batchcol) VALUES (?, ?, ?, ?)";
            
            try (PreparedStatement psLink = conn.prepareStatement(sqlBatchLink)) {
                for (PurchaseItem item : itemList) {
                     
                    boolean reduced = BatchManager.reduceQuantityFromSpecificBatch(
                        conn, item.getBatchNumber(), item.getBarcode(), item.getQuantity(), 1
                    );
                    
                    if (!reduced) {
                        throw new SQLException("Insufficient stock or invalid batch: " + item.getBatchNumber());
                    }

                     
                    psLink.setInt(1, invoiceId);
                    psLink.setString(2, item.getBatchNumber());
                    psLink.setString(3, item.getBarcode());
                    psLink.setString(4, "Purchase Return");
                    psLink.addBatch();
                }
                psLink.executeBatch();
            }

             
            if (paidAmount > 0.001) {
                 String sqlTreasury = "INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) VALUES (?, ?, NOW(), ?, ?)";
                 try (PreparedStatement ps = conn.prepareStatement(sqlTreasury)) {
                      
                     String treasId = "TR-PR-" + java.util.UUID.randomUUID().toString().substring(0, 8);
                     ps.setString(1, treasId);
                     ps.setInt(2, 1);  
                     ps.setDouble(3, paidAmount);  
                     ps.setInt(4, invoiceId);
                     int rows = ps.executeUpdate();
                     if (rows == 0) throw new SQLException("Treasury return insert failed.");
                 }
            }  
            
             
            double finalDebt = getSupplierDebt(conn, supplierName);
            String finSummary = String.format("Return Invoice: #%d\nReturn Value: $%.2f\nRefund Recvd: $%.2f\nDebt Reduced: $%.2f\nSupplier: %s\nTotal Debt: $%.2f ➔ $%.2f", 
                                              invoiceId, totalAmount, paidAmount, remaining, supplierName, initialDebt, finalDebt);
            TransactionSummary.showSummary(conn, "Return Transaction Complete", finSummary);

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
        
         
        String sqlHeader = "SELECT i.price, pi.money_paid FROM invoice i " +
                           "JOIN purchase_invoce pi ON i.ID = pi.Invoice_ID " +
                           "WHERE i.ID = ?";
                           
        try (Connection conn = DBConnection.getConnection()) {
            try (PreparedStatement psH = conn.prepareStatement(sqlHeader)) {
                psH.setInt(1, invoiceId);
                try (ResultSet rsH = psH.executeQuery()) {
                   if (rsH.next()) {
                       double origTotal = rsH.getDouble("price");
                        
                        
                       origTotal = Math.abs(origTotal);
                       
                       double origPaid = rsH.getDouble("money_paid");
                        
                       
                       if (origTotal != 0) {
                           returnPaidRatio = origPaid / origTotal;
                       } else {
                           returnPaidRatio = 1.0;
                       }
                       isReturnMode = true;
                   }
                }
            } catch (SQLException e) {
                 ExceptionLogger.logException(e, "Error fetching invoice header");
            }
            
             
            String sql = "SELECT bLink.Batch_Product_parcode as Parcode, " +
                         "       p.Name, " +
                         "       bLink.Batch_Batch_number as BatchNo, " +
                         "       batch.expire_date, " +
                         "       batch.cost, " +
                         "       batch.Quantaty " + 
                         "FROM purchase_invoce_has_batch bLink " +
                         "JOIN product p ON bLink.Batch_Product_parcode = p.parcode " +
                         "JOIN batch ON batch.Batch_number = bLink.Batch_Batch_number AND batch.Product_parcode = bLink.Batch_Product_parcode " +
                         "WHERE bLink.purchase_invoce_Invoice_ID = ?";
                         
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
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
                    
                    double currentQty = rs.getDouble("Quantaty");
                    
                    LocalDate exp = (expDate != null) ? expDate.toLocalDate() : LocalDate.now();
                    double total = currentQty * cost; 
                    
                    itemList.add(new PurchaseItem(barcode, name, batchNo, exp, currentQty, total));
                }
                
                if (found) {
                    updateTotals();  
                    showSuccess("Invoice Loaded. Quantity shown is CURRENT batch stock.");
                } else {
                    showError("Not Found", "No items found for Invoice ID: " + invoiceId);
                }
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
    
     
    public static class PurchaseItem {
        private String barcode;
        private String productName;
        private String batchNumber;
        private LocalDate expiryDate;
        private double quantity;
        private double unitCost; // Store unit cost explicitly
        private double totalCost;
        
        public PurchaseItem(String barcode, String productName, String batchNumber, LocalDate expiryDate, double quantity, double totalCost) {
            this.barcode = barcode;
            this.productName = productName;
            this.batchNumber = batchNumber;
            this.expiryDate = expiryDate;
            this.quantity = quantity;
            this.totalCost = totalCost;
            this.unitCost = (quantity != 0) ? totalCost / quantity : 0;
        }
        
        public String getBarcode() { return barcode; }
        public String getProductName() { return productName; }
        public String getBatchNumber() { return batchNumber; }
        public LocalDate getExpiryDate() { return expiryDate; }
        
        public void setQuantity(double quantity) { 
            this.quantity = quantity; 
            // Recalculate total based on fixed unit cost
            this.totalCost = this.unitCost * quantity;
        }
        
        public void setTotalCost(double totalCost) { 
            this.totalCost = totalCost; 
            // Recalculate unit cost if total changes (e.g. manual edit of total, though not exposed in UI currently for that column specific logic)
            if (this.quantity != 0) this.unitCost = totalCost / this.quantity;
        }
        
        public double getQuantity() { return quantity; }
        public double getTotalCost() { return totalCost; }
        public double getUnitCost() { return unitCost; }
    }
    private double getSupplierDebt(Connection conn, String supplierName) {
        String sql = "SELECT SUM(remaing_money) FROM purchase_invoce WHERE Supplier_nane = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplierName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getDouble(1);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error calculating supplier debt");
        }
        return 0.0;
    }
}
