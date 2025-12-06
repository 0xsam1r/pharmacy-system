package gui.controllers;

import DB.DBConnection;
import DB.DB_operation;
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
import javafx.util.converter.IntegerStringConverter;
import javafx.scene.control.cell.TextFieldTableCell;
import model.Product.Product;
import util.ExceptionLogger;
import util.SessionManager;
import gui.util.TransactionSummary;

import java.net.URL;
import java.sql.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

 
public class SalesController implements Initializable {

    @FXML private TextField barcodeField;
    @FXML private TableView<CartItem> cartTable;
    @FXML private Label subtotalLabel;
    @FXML private TextField discountField;
    @FXML private Label totalLabel;
    @FXML private TextField customerField;
    
    @FXML private TableColumn<CartItem, String> colName;
    @FXML private TableColumn<CartItem, Double> colPrice;
    @FXML private TableColumn<CartItem, Integer> colQty;
    @FXML private TableColumn<CartItem, Double> colTotal;
    @FXML private TableColumn<CartItem, Void> colAction;
    
    private ObservableList<CartItem> cartList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            ensureSchemaColumns();  
            setupTableColumns();
            cartTable.setItems(cartList);
            cartTable.setEditable(true);
            
            discountField.textProperty().addListener((obs, oldVal, newVal) -> updateTotals());
            
             
            
            ExceptionLogger.logInfo("Sales view initialized");
            
            setupAutocompletion();
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing sales view");
        }
    }
    
    private void setupAutocompletion() {
        ContextMenu suggestionsPopup = new ContextMenu();
        suggestionsPopup.setAutoHide(true);
        suggestionsPopup.setPrefWidth(barcodeField.getPrefWidth());  
        
         
        barcodeField.textProperty().addListener((observable, oldValue, newValue) -> {
             
            if (newValue == null || newValue.trim().isEmpty()) {
                suggestionsPopup.hide();
                return;
            }
            
             
            if (newValue.matches("\\d+")) {
                suggestionsPopup.hide();
                return;
            }

             
             
             
            try (Connection conn = DBConnection.getConnection()) {
                 
                String sql = "SELECT Name, parcode FROM product WHERE Name LIKE ? LIMIT 10";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, "%" + newValue + "%");
                    
                    List<MenuItem> items = new ArrayList<>();
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            final String pName = rs.getString("Name");
                            final String pBarcode = rs.getString("parcode");
                            
                            MenuItem item = new MenuItem(pName);
                            item.setStyle("-fx-font-size: 14px; -fx-padding: 5;");
                            item.setOnAction(e -> {
                                 
                                 
                                barcodeField.setText(pName); 
                                barcodeField.positionCaret(pName.length());
                                suggestionsPopup.hide();
                                
                                 
                                 
                            });
                            items.add(item);
                        }
                    }
                    
                    if (!items.isEmpty()) {
                        javafx.application.Platform.runLater(() -> {
                            suggestionsPopup.getItems().setAll(items);
                            if (!suggestionsPopup.isShowing()) {
                                suggestionsPopup.show(barcodeField, javafx.geometry.Side.BOTTOM, 0, 0);
                            }
                        });
                    } else {
                        javafx.application.Platform.runLater(suggestionsPopup::hide);
                    }
                }
            } catch (SQLException e) {
                 
            }
        });

         
        barcodeField.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) {
                 
                 
                 
            }
        });
    }

    private void ensureSchemaColumns() {
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement()) {
            try {
                stmt.execute("ALTER TABLE sell_invoice ADD COLUMN points_used DOUBLE DEFAULT 0");
            } catch (SQLException e) {}  
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error checking DB schema");
        }
    }
    
    private void setupTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colQty.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        colQty.setOnEditCommit(event -> {
            CartItem item = event.getRowValue();
            item.setQuantity(event.getNewValue());
            cartTable.refresh();
            updateTotals();
        });

        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button removeBtn = new Button("❌");
            
            {
                removeBtn.setOnAction(event -> {
                    CartItem item = getTableView().getItems().get(getIndex());
                    cartList.remove(item);
                    updateTotals();
                });
                removeBtn.getStyleClass().addAll("btn", "btn-danger");
                removeBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10;");
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(removeBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }
    
    @FXML
    private void handleAddItem() {
        String query = barcodeField.getText().trim();
        if (query.isEmpty()) return;
        
        try {
             
             
            
            Product p = null;
            String barcode = null;
            
             
            if (query.matches("\\d+")) {
                p = DB_operation.searchProductByParcode(query);
                if (p != null) barcode = query;
            }
            
             
            if (p == null) {
                p = searchProductByNameOrActiveIngredient(query);
                if (p != null) barcode = p.getParcode();  
            }
            
            if (p != null) {
                 
                 
                 
                 
                 
                 
                
                 
                 
                if (barcode == null) {
                      
                      
                      
                     barcode = getBarcodeForProduct(p);  
                }
                
                 
                for (CartItem item : cartList) {
                    if (item.getBarcode().equals(barcode)) {
                        item.setQuantity(item.getQuantity() + 1);
                        cartTable.refresh();
                        updateTotals();
                        barcodeField.clear();
                        return;
                    }
                }

                String name = getProductName(barcode);
                
                 
                int unitsPerBox = p.getUnitsPerProduct();
                 
                 
                 
                
                int defaultQty;
                double unitPrice;
                
                if (unitsPerBox > 1) {
                    defaultQty = unitsPerBox;
                    unitPrice = p.getPrice() / unitsPerBox;
                } else {
                    defaultQty = 1;
                    unitPrice = p.getPrice();
                }
                 
                
                CartItem item = new CartItem();
                item.setBarcode(barcode);
                item.setName(name != null ? name : "Unknown Product");
                item.setPrice(unitPrice);
                item.setQuantity(defaultQty);
                
                cartList.add(item);
                updateTotals();
                barcodeField.clear();
            } else {
                showError("Not Found", "Product not found: " + query);
            }
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error adding item to cart");
        }
    }

    private Product searchProductByNameOrActiveIngredient(String query) {
         
        String sql = "SELECT p.* FROM product p " +
                     "LEFT JOIN medicine m ON p.parcode = m.Product_parcode " +
                     "LEFT JOIN medicine_has_dosage_form md ON m.Product_parcode = md.medicine_Product_parcode " +
                     "LEFT JOIN dosage_form d ON md.dosage_form_ID = d.ID " +
                     "WHERE p.Name LIKE ? OR d.active_ing LIKE ? LIMIT 1";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + query + "%");
            ps.setString(2, "%" + query + "%");
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                 
                return new Product(
                    rs.getString("parcode"),
                    rs.getString("Name"),
                    rs.getInt("Uints"),
                    rs.getDouble("Price"),
                    null  
                );
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error searching product by name/active ingredient");
        }
        return null;
    }
    
    private String getBarcodeForProduct(Product p) {
        return p.getParcode();
    }
    
    
    private String getProductName(String barcode) {
        String sql = "SELECT Name FROM product WHERE parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("Name");
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error fetching product name");
        }
        return null;
    }
    
     
    private int getUnitsPerProduct(Connection conn, String barcode) {
        String sql = "SELECT Uints FROM product WHERE parcode = ?";
         
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int units = rs.getInt("Uints");
                    return units > 0 ? units : 1;
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error fetching units per product");
        }
        return 1;  
    }
    
    private void updateTotals() {
        double subtotal = cartList.stream().mapToDouble(CartItem::getTotal).sum();
        subtotalLabel.setText(String.format("$%.2f", subtotal));
        
        double discount = 0;
        try {
            discount = Double.parseDouble(discountField.getText());
        } catch (NumberFormatException e) {
             
        }
        
        double total = Math.max(0, subtotal - discount);
        totalLabel.setText(String.format("$%.2f", total));
    }
    
    @FXML
    private void handleCheckout() {
        if (cartList.isEmpty()) {
            showError("Empty Cart", "Please add items to the cart first.");
            return;
        }
        
         
        String customerId = customerField.getText().trim();
        // Initialize variables
        double discountToApply;
        boolean usePoints = false;
        double pointsToDeduct = 0;

        // Initialize discount with manual entry if present
        try {
            discountToApply = Double.parseDouble(discountField.getText());
        } catch (NumberFormatException e) {
            discountToApply = 0; 
        }

        if (!customerId.isEmpty()) {
            // ... (Customer logic remains the same up to dialog choice) ...
            try (Connection checkConn = DBConnection.getConnection()) {
                double totalSpent = 0;
                double customerPoints = 0;
                
                String actualPersonId = customerId;
                String findCustomerSql = "SELECT c.Person_ID FROM customer c JOIN person p ON c.Person_ID = p.ID WHERE c.Person_ID = ? OR p.Phone = ? LIMIT 1";
                try (PreparedStatement psFind = checkConn.prepareStatement(findCustomerSql)) {
                    psFind.setString(1, customerId);
                    psFind.setString(2, customerId);
                    ResultSet rsFind = psFind.executeQuery();
                    if (rsFind.next()) {
                        actualPersonId = rsFind.getString("Person_ID");
                    } else {
                        showError("Customer Not Found", "No customer found with ID/Phone: " + customerId);
                        return;
                    }
                }
                
                String sql = "SELECT COALESCE(SUM(i.price), 0) as total_spent, " +
                             "COALESCE((SELECT points FROM customer WHERE Person_ID = ?), 0) as points " +
                             "FROM invoice i " +
                             "JOIN sell_invoice si ON i.ID = si.Invoice_ID " +
                             "WHERE si.Customer_Person_ID = ?";
                
                try (PreparedStatement ps = checkConn.prepareStatement(sql)) {
                    ps.setString(1, actualPersonId);
                    ps.setString(2, actualPersonId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        totalSpent = rs.getDouble("total_spent");
                        customerPoints = rs.getDouble("points");
                    }
                }
                
                double currentSubtotal = cartList.stream().mapToDouble(CartItem::getTotal).sum();
                
                final double pointsDiscountPercent = Math.min(customerPoints / 10.0, 50);
                final double pointsDiscountAmount = currentSubtotal * (pointsDiscountPercent / 100.0);
                final double pointsNeeded = pointsDiscountPercent * 10;
                
                if (pointsDiscountAmount > 0) {
                    Alert discountChoice = new Alert(Alert.AlertType.CONFIRMATION);
                    discountChoice.setTitle("Customer Discount");
                    discountChoice.setHeaderText("Choose Discount Type for Customer: " + customerId);
                    
                    StringBuilder content = new StringBuilder();
                    content.append(String.format("Total Spending History: $%.2f\n", totalSpent));
                    content.append(String.format("Available Points: %.0f points\n\n", customerPoints));
                    content.append("Available options:\n");
                    content.append(String.format("• Use Points: $%.2f (%.0f%% off - Uses %.0f points)\n", pointsDiscountAmount, pointsDiscountPercent, pointsNeeded));
                    content.append(String.format("• Manual/Current Discount: $%.2f\n", discountToApply));

                    discountChoice.setContentText(content.toString());
                    
                    ButtonType pointsBtn = new ButtonType("Use Points");
                    ButtonType keepManualBtn = new ButtonType("Keep Manual");
                    
                    discountChoice.getButtonTypes().setAll(pointsBtn, keepManualBtn);
                    
                    Optional<ButtonType> choice = discountChoice.showAndWait();
                    if (choice.isPresent()) {
                         if (choice.get() == pointsBtn) {
                            discountToApply = pointsDiscountAmount;
                            usePoints = true;
                            pointsToDeduct = pointsNeeded;
                            discountField.setText(String.format("%.2f", discountToApply)); // Update field if system discount chosen
                        }
                        // If keepManualBtn, we do nothing
                    } else {
                        return; // Cancelled
                    }
                }
            } catch (SQLException e) {
                ExceptionLogger.logException(e, "Error checking customer discount");
            }
        }
        
        // Ensure the discount field reflects the final decision (though handled above for overrides, good to sync)
        if(Math.abs(Double.parseDouble(discountField.getText()) - discountToApply) > 0.01) {
             discountField.setText(String.format("%.2f", discountToApply));
        }
        updateTotals();
        
         
        TransactionSummary.clearSnapshots();
        try (Connection preConn = DBConnection.getConnection()) {
            for (CartItem item : cartList) {
                TransactionSummary.snapshotState(preConn, item.getBarcode().trim());
            }
        } catch (SQLException e) {
             ExceptionLogger.logException(e, "Error snapshotting pre-sale state");
        }
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);  

            int invoiceId = generateInvoiceId(conn);
            String totalText = totalLabel.getText().replaceAll("[^\\d.]", "");
            double total = totalText.isEmpty() ? 0 : Double.parseDouble(totalText);
            
            String username = SessionManager.getInstance().getUsername();
            String userId = SessionManager.getInstance().getUserId();
            int branchId = SessionManager.getInstance().getBranchId(); 
            
             
            if (branchId == 0) branchId = 1; 
            
            if (username == null) username = "admin";
            if (userId == null) userId = "1";

             
            String sqlInv = "INSERT INTO invoice (ID, date, price, employee_User_name, employee_Person_ID, employee_bransh_ID) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlInv)) {
                ps.setInt(1, invoiceId);
                ps.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                ps.setDouble(3, total);
                ps.setString(4, username);
                ps.setString(5, userId);
                ps.setInt(6, branchId);
                ps.executeUpdate();
            }

             
            double pointsBefore = 0;
            boolean customerExists = false;
            String actualCustomerId = customerId;
            if (!customerId.isEmpty()) {
                 
                String findCust = "SELECT c.Person_ID, c.points FROM customer c JOIN person p ON c.Person_ID = p.ID WHERE c.Person_ID = ? OR p.Phone = ? LIMIT 1";
                try (PreparedStatement ps = conn.prepareStatement(findCust)) {
                    ps.setString(1, customerId);
                    ps.setString(2, customerId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        customerExists = true;
                        actualCustomerId = rs.getString("Person_ID");
                        pointsBefore = rs.getDouble("points");
                    }
                }
                
                if (customerExists) {
                    String sqlSell = "INSERT INTO sell_invoice (Discount, Invoice_ID, Customer_Person_ID, points_used) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlSell)) {
                        ps.setDouble(1, discountToApply);
                        ps.setInt(2, invoiceId);
                        ps.setString(3, actualCustomerId);
                        ps.setDouble(4, usePoints ? pointsToDeduct : 0);
                        ps.executeUpdate();
                    }
                    
                    if (usePoints) {
                         
                        String sqlDeductPoints = "UPDATE customer SET points = points - ? WHERE Person_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlDeductPoints)) {
                            ps.setDouble(1, pointsToDeduct);
                            ps.setString(2, actualCustomerId);
                            ps.executeUpdate();
                        }
                    }
                    
                     
                    double pointsEarned = total / 10.0;
                    if (pointsEarned > 0) {
                        String sqlPoints = "UPDATE customer SET points = points + ? WHERE Person_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlPoints)) {
                            ps.setDouble(1, pointsEarned);
                            ps.setString(2, actualCustomerId);
                            ps.executeUpdate();
                        }
                    }
                }
            }

             
            String sqlItems = "INSERT INTO invoice_has_product (Invoice_ID, Product_parcode, units) VALUES (?, ?, ?)";
            
            try (PreparedStatement psItems = conn.prepareStatement(sqlItems)) {
                
                for (CartItem item : cartList) {
                     
                    String cleanBarcode = item.getBarcode().trim();
                    
                     
                    psItems.setInt(1, invoiceId);
                    psItems.setString(2, cleanBarcode);
                    psItems.setDouble(3, item.getQuantity());
                    psItems.addBatch();
                    
                     
                    int unitsPerBox = getUnitsPerProduct(conn, cleanBarcode);
                    if (unitsPerBox <= 0) unitsPerBox = 1;  
                    
                     
                     
                    double boxesToDeduct = (double) item.getQuantity() / unitsPerBox;
                    
                     
                    ExceptionLogger.logInfo(String.format(
                        "🛒 CHECKOUT: Product=%s, Barcode='%s' (Len:%d), CartQty=%d strips, UnitsPerBox=%d, BoxesToDeduct=%.3f",
                        item.getName(), cleanBarcode, cleanBarcode.length(), item.getQuantity(), unitsPerBox, boxesToDeduct
                    ));
                    
                     
                    boolean reduced = BatchManager.reduceQuantityFromBatches(
                        conn,  
                        cleanBarcode,
                        boxesToDeduct,  
                        1  
                    );
                    
                    if (!reduced) {
                        double totalAvailable = BatchManager.getTotalAvailableQuantity(conn, cleanBarcode);
                        
                        String msg = String.format(
                            "Oops! Not enough stock for: %s\nAvailable: %.2f\nRequired: %.2f\nPlease update inventory or reduce quantity.", 
                            item.getName(), totalAvailable, boxesToDeduct
                        );
                        throw new SQLException(msg);
                    }
                }
                psItems.executeBatch();
            }

             
            String sqlTreasury = "INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTreasury)) {
                ps.setString(1, "TR-" + System.currentTimeMillis());
                ps.setInt(2, branchId);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.setDouble(4, total);
                ps.setInt(5, invoiceId);
                ps.executeUpdate();
            }

             
            double pointsAfter = pointsBefore;
            if (usePoints) pointsAfter -= pointsToDeduct;
            pointsAfter += (total / 10.0);
            
            String pointsMsg = customerExists ? String.format("\nPoints: %.0f -> %.0f", pointsBefore, pointsAfter) : "";
            
            String finSummary = String.format("Invoice: #%d\nTotal Amount: $%.2f\nCustomer: %s%s\nDiscount: $%.2f", 
                                              invoiceId, total, customerId.isEmpty() ? "Walk-in" : actualCustomerId, pointsMsg, discountToApply);
            TransactionSummary.showSummary(conn, "Sales Transaction Complete", finSummary);

            conn.commit();
            showSuccess("Sale Completed! Invoice #" + invoiceId);
            handleCancel();

        } catch (Exception e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            ExceptionLogger.logException(e, "Error during checkout");
            showError("Checkout Error", "Transaction failed: " + e.getMessage());
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    private int generateInvoiceId(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT MAX(ID) FROM invoice")) {
            if (rs.next()) return rs.getInt(1) + 1;
        }
        return 1001;
    }

    @FXML
    private void handleCancel() {
        cartList.clear();
        barcodeField.clear();
        discountField.setText("0");
        customerField.clear();
        customerField.setUserData(null);  
        updateTotals();
    }

    @FXML
    private void handleReturn() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Return Items");
        dialog.setHeaderText("Enter Invoice ID to Return");
        dialog.setContentText("Invoice ID:");
        
        dialog.showAndWait().ifPresent(invoiceIdStr -> {
            try {
                int invoiceId = Integer.parseInt(invoiceIdStr);
                processReturn(invoiceId);
            } catch (NumberFormatException e) {
                showError("Invalid Input", "Please enter a valid numeric Invoice ID.");
            }
        });
    }

    private void processReturn(int invoiceId) {
         
        ObservableList<CartItem> returnableItems = FXCollections.observableArrayList();
        
         
        double originalDiscount = 0;
        double originalTotalPaid = 0;
        double originalPointsUsed = 0;
        
        String sqlHead = "SELECT i.price, s.Discount, s.points_used FROM invoice i JOIN sell_invoice s ON i.ID = s.Invoice_ID WHERE i.ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlHead)) {
             ps.setInt(1, invoiceId);
             ResultSet rs = ps.executeQuery();
             if (rs.next()) {
                 originalTotalPaid = rs.getDouble("price");
                 originalDiscount = rs.getDouble("Discount");
                  
                 try { originalPointsUsed = rs.getDouble("points_used"); } catch(Exception e) { originalPointsUsed = 0; }
             }
        } catch (SQLException e) { ExceptionLogger.logException(e, "Error fetching invoice header"); }
        
        double originalListPriceTotal = originalTotalPaid + originalDiscount;
         
        double paidRatio = (originalListPriceTotal > 0) ? (originalTotalPaid / originalListPriceTotal) : 1.0;
        
         
        final double fPointsUsed = originalPointsUsed;
        final double fDiscount = originalDiscount;

        String sql = "SELECT ip.Product_parcode, ip.units, p.Name, p.Price, p.Uints as UnitsPerBox " +
                     "FROM invoice_has_product ip " +
                     "JOIN product p ON ip.Product_parcode = p.parcode " +
                     "WHERE ip.Invoice_ID = ?";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CartItem item = new CartItem();
                item.setBarcode(rs.getString("Product_parcode"));
                item.setName(rs.getString("Name"));
                
                double boxPrice = rs.getDouble("Price");
                int unitsPerBox = rs.getInt("UnitsPerBox");
                if (unitsPerBox <= 0) unitsPerBox = 1;
                
                double unitListPrice = boxPrice / unitsPerBox;
                
                 
                 
                item.setPrice(unitListPrice * paidRatio);
                
                 
                 
                
                item.setQuantity((int)rs.getDouble("units"));
                returnableItems.add(item);
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error fetching invoice items");
            showError("Error", "Database error.");
            return;
        }

        if (returnableItems.isEmpty()) {
            showError("Not Found", "No items found for Invoice ID: " + invoiceId);
            return;
        }

         
        Dialog<List<CartItem>> dialog = new Dialog<>();
        dialog.setTitle("Select Return Items");
        dialog.setHeaderText("Select items and quantity to return from Invoice #" + invoiceId + "\n" +
                           String.format("(Refunds adjusted for original discount: %.1f%%)", (1-paidRatio)*100));
        
        TableView<CartItem> table = new TableView<>(returnableItems);
        table.setEditable(true);
        
        TableColumn<CartItem, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<CartItem, Double> priceCol = new TableColumn<>("Refund Rate/Unit");
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        
        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Qty to Return");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        qtyCol.setOnEditCommit(e -> e.getRowValue().setQuantity(e.getNewValue()));
        
        table.getColumns().addAll(nameCol, priceCol, qtyCol);
        
        DialogPane pane = dialog.getDialogPane();
        pane.setContent(table);
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) return new ArrayList<>(table.getItems());
            return null;
        });
        
        dialog.showAndWait().ifPresent(itemsToReturn -> {
            performReturnTransaction(invoiceId, itemsToReturn, fPointsUsed, fDiscount);
        });
    }

    private void performReturnTransaction(int invoiceId, List<CartItem> items, double originalPointsUsed, double originalTotalDiscount) {
        
         
        TransactionSummary.clearSnapshots();
        try (Connection preConn = DBConnection.getConnection()) {
            for (CartItem item : items) {
                if (item.getQuantity() > 0)
                    TransactionSummary.snapshotState(preConn, item.getBarcode().trim());
            }
        } catch (SQLException e) { e.printStackTrace(); }

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

             
            for (CartItem item : items) {
                if (item.getQuantity() > 0) {
                     
                    int unitsPerBox = getUnitsPerProduct(conn, item.getBarcode());
                    if (unitsPerBox <= 0) unitsPerBox = 1;
                    
                     
                    double boxesToReturn = (double) item.getQuantity() / unitsPerBox;
                    
                     
                    List<BatchManager.Batch> batches = BatchManager.getBatchesForProduct(conn, item.getBarcode());
                    
                    if (!batches.isEmpty()) {
                         
                        BatchManager.Batch nearestBatch = batches.get(0);
                         
                        boolean added = BatchManager.addQuantityToBatch(
                            conn,
                            nearestBatch.getBatchNumber(),
                            item.getBarcode(),
                            boxesToReturn, 
                            1  
                        );
                        
                        if (!added) {
                            throw new SQLException("Failed to add quantity back to batch for: " + item.getName());
                        }
                        
                        if (!added) {
                            throw new SQLException("Failed to add quantity back to batch for: " + item.getName());
                        }
                    } else {
                         
                        String sqlInv = "UPDATE inventory_has_product SET Quntaty = Quntaty + ? WHERE Product_parcode = ? AND Inventory_ID = 1";
                        try (PreparedStatement ps = conn.prepareStatement(sqlInv)) {
                            ps.setDouble(1, boxesToReturn);  
                            ps.setString(2, item.getBarcode());
                            ps.executeUpdate();
                        }
                    }
                }
            }

             
            String sqlCust = "SELECT Customer_Person_ID FROM sell_invoice WHERE Invoice_ID = ?";
            String custId = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlCust)) {
                ps.setInt(1, invoiceId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) custId = rs.getString("Customer_Person_ID");
            }

            double pointsChange = 0;
            double pointsBefore = 0;
            if (custId != null) {
                  
                try (PreparedStatement ps = conn.prepareStatement("SELECT points FROM customer WHERE Person_ID = ?")) {
                    ps.setString(1, custId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) pointsBefore = rs.getDouble("points");
                }
                double totalRefundPaid = items.stream().mapToDouble(CartItem::getTotal).sum();
                
                 
                double earnedToReclaim = totalRefundPaid / 10.0;
                
                 
                 
                 
                 
                 
                 
                 
                
                double pointsToRefund = 0;
                if (originalTotalDiscount > 0 && originalPointsUsed > 0) {
                      
                      
                      
                     double paidRatio = 1.0; 
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                      
                     
                      
                      
                      
                      
                     
                      
                      
                      
                      
                }
                
                 
                 
                
                 
                 
                 
                 
                double originalInvoicePrice = 0;
                 try (PreparedStatement ps2 = conn.prepareStatement("SELECT price FROM invoice WHERE ID=?")) {
                     ps2.setInt(1, invoiceId);
                     ResultSet rs2 = ps2.executeQuery();
                     if (rs2.next()) originalInvoicePrice = rs2.getDouble(1);
                 }
                 
                 if (originalInvoicePrice > 0) {
                     double returnRatio = totalRefundPaid / originalInvoicePrice;
                     pointsToRefund = originalPointsUsed * returnRatio;
                 }
                 
                 pointsChange = pointsToRefund - earnedToReclaim;
                 
                 if (Math.abs(pointsChange) > 0.1) {
                     String sqlUpdatePts = "UPDATE customer SET points = points + ? WHERE Person_ID = ?";
                     try (PreparedStatement ps = conn.prepareStatement(sqlUpdatePts)) {
                         ps.setDouble(1, pointsChange);  
                         ps.setString(2, custId);
                         ps.executeUpdate();
                     }
                 }
            }
            
             
            double totalRefund = items.stream().mapToDouble(CartItem::getTotal).sum();
            String sqlTreasury = "INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) VALUES (?, ?, NOW(), ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTreasury)) {
                ps.setString(1, "REF-" + System.currentTimeMillis());
                ps.setInt(2, 1);
                ps.setDouble(3, -totalRefund);  
                ps.setInt(4, invoiceId);
                ps.executeUpdate();
            }

             
            String ptsMsg = (custId != null) ? String.format("\nPoints: %.0f -> %.0f (%+.0f)", pointsBefore, pointsBefore + pointsChange, pointsChange) : "";
            
            String finSummary = String.format("Return Invoice: #%d\nRefund Amount: $%.2f%s", 
                                              invoiceId, totalRefund, ptsMsg);
            TransactionSummary.showSummary(conn, "Return Transaction Complete", finSummary);

            conn.commit();
            showSuccess("Return Processed. Inventory updated and points deducted.");

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            ExceptionLogger.logException(e, "Error processing return");
            showError("Return Failed", e.getMessage());
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
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

    public static class CartItem {
        private SimpleStringProperty barcode = new SimpleStringProperty();
        private SimpleStringProperty name = new SimpleStringProperty();
        private SimpleDoubleProperty price = new SimpleDoubleProperty();
        private SimpleIntegerProperty quantity = new SimpleIntegerProperty();
        
        public String getBarcode() { return barcode.get(); }
        public void setBarcode(String v) { barcode.set(v); }
        
        public String getName() { return name.get(); }
        public void setName(String v) { name.set(v); }
        public SimpleStringProperty nameProperty() { return name; }
        
        public double getPrice() { return price.get(); }
        public void setPrice(double v) { price.set(v); }
        public SimpleDoubleProperty priceProperty() { return price; }
        
        public int getQuantity() { return quantity.get(); }
        public void setQuantity(int v) { quantity.set(v); }
        public SimpleIntegerProperty quantityProperty() { return quantity; }
        
        public double getTotal() { return getPrice() * getQuantity(); }
        public SimpleDoubleProperty totalProperty() { return new SimpleDoubleProperty(getTotal()); }
    }
}
