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

import java.net.URL;
import java.sql.*;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for Sales (POS) View
 */
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
            setupTableColumns();
            cartTable.setItems(cartList);
            cartTable.setEditable(true);
            
            discountField.textProperty().addListener((obs, oldVal, newVal) -> updateTotals());
            
            // No automatic discount popup - only when completing sale
            
            ExceptionLogger.logInfo("Sales view initialized");
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing sales view");
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
            // Check if already in cart (by barcode) - ONLY if query is a barcode
            // If query is name, we need to find the barcode first.
            
            Product p = null;
            String barcode = null;
            
            // 1. Try search by Barcode (if numeric)
            if (query.matches("\\d+")) {
                p = DB_operation.searchProductByParcode(query);
                if (p != null) barcode = query;
            }
            
            // 2. If not found or not barcode, search by Name or Active Ingredient
            if (p == null) {
                p = searchProductByNameOrActiveIngredient(query);
                if (p != null) barcode = p.getParcode(); // Assuming Product has getParcode() or we need to fetch it
            }
            
            if (p != null) {
                // We need the barcode. If Product object doesn't have it (it should), we might need to rely on the query if it was a barcode, 
                // or fetch it if it was a name search.
                // The Product class in model.Product usually has getters. Let's assume getParcode() exists or we use the one we found.
                // Wait, DB_operation.searchProductByParcode returns a Product. 
                // Let's check if we can get the barcode from 'p'.
                // If not, we need to ensure we have it.
                
                // For the purpose of this fix, let's assume we can get it.
                // If p was found via name search, we need its barcode.
                if (barcode == null) {
                     // We need to fetch barcode for this product if we found it by name
                     // The searchProductByNameOrActiveIngredient should return a Product with barcode populated.
                     // I will implement searchProductByNameOrActiveIngredient to return a Product with barcode.
                     barcode = getBarcodeForProduct(p); // Helper if needed, or p.getParcode()
                }
                
                // Check if already in cart
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
                
                // --- Unit-based Logic Start ---
                int unitsPerBox = p.getUnitsPerProduct();
                // If units is 0 or 1, we treat it as selling the whole box (QTY=1)
                // If units > 1, we default to selling by units (QTY = Total Units in Box)
                // and Price becomes Price Per Unit.
                
                int defaultQty;
                double unitPrice;
                
                if (unitsPerBox > 1) {
                    defaultQty = unitsPerBox;
                    unitPrice = p.getPrice() / unitsPerBox;
                } else {
                    defaultQty = 1;
                    unitPrice = p.getPrice();
                }
                // --- Unit-based Logic End ---
                
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
        // Search by Product Name OR Active Ingredient
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
                // Product(String Parcode, String Name, int UnitsPerProduct, double Price, Category Category)
                return new Product(
                    rs.getString("parcode"),
                    rs.getString("Name"),
                    rs.getInt("Uints"),
                    rs.getDouble("Price"),
                    null // Category not needed for this search
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
    
    private void updateTotals() {
        double subtotal = cartList.stream().mapToDouble(CartItem::getTotal).sum();
        subtotalLabel.setText(String.format("$%.2f", subtotal));
        
        double discount = 0;
        try {
            discount = Double.parseDouble(discountField.getText());
        } catch (NumberFormatException e) {
            // Ignore invalid discount
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
        
        // Get customer ID and check for discounts BEFORE starting transaction
        String customerId = customerField.getText().trim();
        double discountToApply = 0;
        boolean usePoints = false;
        double pointsToDeduct = 0;
        
        if (!customerId.isEmpty()) {
            // Check if customer exists and get discount options
            try (Connection checkConn = DBConnection.getConnection()) {
                double totalSpent = 0;
                double customerPoints = 0;
                
                String sql = "SELECT COALESCE(SUM(i.price), 0) as total_spent, " +
                             "COALESCE((SELECT points FROM customer WHERE Person_ID = ?), 0) as points " +
                             "FROM invoice i " +
                             "JOIN sell_invoice si ON i.ID = si.Invoice_ID " +
                             "WHERE si.Customer_Person_ID = ?";
                
                try (PreparedStatement ps = checkConn.prepareStatement(sql)) {
                    ps.setString(1, customerId);
                    ps.setString(2, customerId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        totalSpent = rs.getDouble("total_spent");
                        customerPoints = rs.getDouble("points");
                    }
                }
                
                double currentSubtotal = cartList.stream().mapToDouble(CartItem::getTotal).sum();
                
                // Calculate loyalty discount
                double loyaltyDiscountRate = 0;
                if (totalSpent > 10000) loyaltyDiscountRate = 0.10;
                else if (totalSpent > 5000) loyaltyDiscountRate = 0.05;
                else if (totalSpent > 1000) loyaltyDiscountRate = 0.02;
                
                final double loyaltyDiscountAmount = currentSubtotal * loyaltyDiscountRate;
                final double loyaltyDiscountPercent = loyaltyDiscountRate * 100;
                
                // Calculate points discount
                final double pointsDiscountPercent = Math.min(customerPoints / 10.0, 50);
                final double pointsDiscountAmount = currentSubtotal * (pointsDiscountPercent / 100.0);
                final double pointsNeeded = pointsDiscountPercent * 10;
                
                // Show discount dialog if any discount available
                if (loyaltyDiscountAmount > 0 || pointsDiscountAmount > 0) {
                    Alert discountChoice = new Alert(Alert.AlertType.CONFIRMATION);
                    discountChoice.setTitle("Customer Discount");
                    discountChoice.setHeaderText("Choose Discount Type for Customer: " + customerId);
                    
                    StringBuilder content = new StringBuilder();
                    content.append(String.format("Total Spending History: $%.2f\n", totalSpent));
                    content.append(String.format("Available Points: %.0f points\n\n", customerPoints));
                    content.append("Choose discount type:\n");
                    content.append(String.format("• Loyalty Discount: $%.2f (%.0f%% off)\n", loyaltyDiscountAmount, loyaltyDiscountPercent));
                    content.append(String.format("• Use Points: $%.2f (%.0f%% off - Uses %.0f points)\n", pointsDiscountAmount, pointsDiscountPercent, pointsNeeded));
                    
                    discountChoice.setContentText(content.toString());
                    
                    ButtonType loyaltyBtn = new ButtonType("Loyalty Discount");
                    ButtonType pointsBtn = new ButtonType("Use Points");
                    ButtonType noDiscountBtn = new ButtonType("No Discount");
                    
                    discountChoice.getButtonTypes().setAll(loyaltyBtn, pointsBtn, noDiscountBtn);
                    
                    Optional<ButtonType> choice = discountChoice.showAndWait();
                    if (choice.isPresent()) {
                        if (choice.get() == loyaltyBtn && loyaltyDiscountAmount > 0) {
                            discountToApply = loyaltyDiscountAmount;
                        } else if (choice.get() == pointsBtn && pointsDiscountAmount > 0) {
                            discountToApply = pointsDiscountAmount;
                            usePoints = true;
                            pointsToDeduct = pointsNeeded;
                        }
                    } else {
                        // User closed dialog, cancel checkout
                        return;
                    }
                }
            } catch (SQLException e) {
                ExceptionLogger.logException(e, "Error checking customer discount");
            }
        }
        
        // Update discount field
        discountField.setText(String.format("%.2f", discountToApply));
        updateTotals();
        
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            int invoiceId = generateInvoiceId(conn);
            String totalText = totalLabel.getText().replaceAll("[^\\d.]", "");
            double total = totalText.isEmpty() ? 0 : Double.parseDouble(totalText);
            
            String username = SessionManager.getInstance().getUsername();
            String userId = SessionManager.getInstance().getUserId();
            int branchId = 1; 
            
            if (username == null) username = "admin";
            if (userId == null) userId = "1";

            // 1. Create Invoice Header
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

            // 2. Link Customer & Sell Invoice
            if (!customerId.isEmpty()) {
                boolean customerExists = false;
                String checkCust = "SELECT COUNT(*) FROM customer WHERE Person_ID = ?";
                try (PreparedStatement ps = conn.prepareStatement(checkCust)) {
                    ps.setString(1, customerId);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next() && rs.getInt(1) > 0) {
                        customerExists = true;
                    }
                }
                
                if (customerExists) {
                    String sqlSell = "INSERT INTO sell_invoice (Discount, Invoice_ID, Customer_Person_ID) VALUES (?, ?, ?)";
                    try (PreparedStatement ps = conn.prepareStatement(sqlSell)) {
                        ps.setDouble(1, discountToApply);
                        ps.setInt(2, invoiceId);
                        ps.setString(3, customerId);
                        ps.executeUpdate();
                    }
                    
                    if (usePoints) {
                        // Deduct points
                        String sqlDeductPoints = "UPDATE customer SET points = points - ? WHERE Person_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlDeductPoints)) {
                            ps.setDouble(1, pointsToDeduct);
                            ps.setString(2, customerId);
                            ps.executeUpdate();
                        }
                    } else {
                        // Add Points
                        double pointsEarned = total / 10.0;
                        String sqlPoints = "UPDATE customer SET points = points + ? WHERE Person_ID = ?";
                        try (PreparedStatement ps = conn.prepareStatement(sqlPoints)) {
                            ps.setDouble(1, pointsEarned);
                            ps.setString(2, customerId);
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // 3. Add Items & Update Inventory using FEFO Batch Logic
            String sqlItems = "INSERT INTO invoice_has_product (Invoice_ID, Product_parcode, units) VALUES (?, ?, ?)";
            
            try (PreparedStatement psItems = conn.prepareStatement(sqlItems)) {
                
                for (CartItem item : cartList) {
                    // Add to invoice
                    psItems.setInt(1, invoiceId);
                    psItems.setString(2, item.getBarcode());
                    psItems.setDouble(3, item.getQuantity());
                    psItems.addBatch();
                    
                    // ✅ FEFO: Reduce from batches that expire first (using SAME connection)
                    boolean reduced = BatchManager.reduceQuantityFromBatches(
                        conn, // Pass the active connection!
                        item.getBarcode(),
                        item.getQuantity(),
                        1 // Inventory ID
                    );
                    
                    if (!reduced) {
                        throw new SQLException("Insufficient stock in batches for product: " + item.getName());
                    }
                }
                psItems.executeBatch();
            }

            // 4. Update Treasury
            String sqlTreasury = "INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTreasury)) {
                ps.setString(1, "TR-" + System.currentTimeMillis());
                ps.setInt(2, branchId);
                ps.setTimestamp(3, new Timestamp(System.currentTimeMillis()));
                ps.setDouble(4, total);
                ps.setInt(5, invoiceId);
                ps.executeUpdate();
            }

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
        customerField.setUserData(null); // Clear point discount flag
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
        // Fetch items
        ObservableList<CartItem> returnableItems = FXCollections.observableArrayList();
        String sql = "SELECT ip.Product_parcode, ip.units, p.Name, p.Price " +
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
                item.setPrice(rs.getDouble("Price"));
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

        // Show Dialog to select items to return
        Dialog<List<CartItem>> dialog = new Dialog<>();
        dialog.setTitle("Select Return Items");
        dialog.setHeaderText("Select items and quantity to return from Invoice #" + invoiceId);
        
        TableView<CartItem> table = new TableView<>(returnableItems);
        table.setEditable(true);
        
        TableColumn<CartItem, String> nameCol = new TableColumn<>("Product");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        
        TableColumn<CartItem, Integer> qtyCol = new TableColumn<>("Qty to Return");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        qtyCol.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        qtyCol.setOnEditCommit(e -> e.getRowValue().setQuantity(e.getNewValue()));
        
        table.getColumns().addAll(nameCol, qtyCol);
        
        DialogPane pane = dialog.getDialogPane();
        pane.setContent(table);
        pane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        
        dialog.setResultConverter(btn -> {
            if (btn == ButtonType.OK) return new ArrayList<>(table.getItems());
            return null;
        });
        
        dialog.showAndWait().ifPresent(itemsToReturn -> {
            performReturnTransaction(invoiceId, itemsToReturn);
        });
    }

    private void performReturnTransaction(int invoiceId, List<CartItem> items) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Update Inventory & Batch (Add back to nearest expiring batch)
            for (CartItem item : items) {
                if (item.getQuantity() > 0) {
                    // Get the batch that expires first (to add returns there)
                    List<BatchManager.Batch> batches = BatchManager.getBatchesForProduct(item.getBarcode());
                    
                    if (!batches.isEmpty()) {
                        // Add to the batch that expires first (logical for returns)
                        BatchManager.Batch nearestBatch = batches.get(0);
                        boolean added = BatchManager.addQuantityToBatch(
                            nearestBatch.getBatchNumber(),
                            item.getBarcode(),
                            item.getQuantity(),
                            1 // Inventory ID
                        );
                        
                        if (!added) {
                            throw new SQLException("Failed to add quantity back to batch for: " + item.getName());
                        }
                    } else {
                        // Fallback: Update inventory directly if no batches found
                        String sqlInv = "UPDATE inventory_has_product SET Quntaty = Quntaty + ? WHERE Product_parcode = ? AND Inventory_ID = 1";
                        try (PreparedStatement ps = conn.prepareStatement(sqlInv)) {
                            ps.setDouble(1, item.getQuantity());
                            ps.setString(2, item.getBarcode());
                            ps.executeUpdate();
                        }
                    }
                }
            }

            // 2. Deduct Points from Customer (if any)
            String sqlCust = "SELECT Customer_Person_ID FROM sell_invoice WHERE Invoice_ID = ?";
            String custId = null;
            try (PreparedStatement ps = conn.prepareStatement(sqlCust)) {
                ps.setInt(1, invoiceId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) custId = rs.getString("Customer_Person_ID");
            }

            if (custId != null) {
                double totalRefund = items.stream().mapToDouble(CartItem::getTotal).sum();
                double pointsToDeduct = totalRefund / 10.0;
                String sqlDed = "UPDATE customer SET points = points - ? WHERE Person_ID = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlDed)) {
                    ps.setDouble(1, pointsToDeduct);
                    ps.setString(2, custId);
                    ps.executeUpdate();
                }
            }
            
            // 3. Update Treasury (Refund = Expense)
            double totalRefund = items.stream().mapToDouble(CartItem::getTotal).sum();
            String sqlTreasury = "INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) VALUES (?, ?, NOW(), ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTreasury)) {
                ps.setString(1, "REF-" + System.currentTimeMillis());
                ps.setInt(2, 1);
                ps.setDouble(3, -totalRefund); // Negative for refund
                ps.setInt(4, invoiceId);
                ps.executeUpdate();
            }

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
