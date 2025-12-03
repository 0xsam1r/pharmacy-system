package gui.controllers;

import DB.DBConnection;
import DB.DB_operation;
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
import model.Product.Product;
import util.ExceptionLogger;
import util.SessionManager;

import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
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
            
            discountField.textProperty().addListener((obs, oldVal, newVal) -> updateTotals());
            
            ExceptionLogger.logInfo("Sales view initialized");
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing sales view");
        }
    }
    
    private void setupTableColumns() {
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
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
        String barcode = barcodeField.getText().trim();
        if (barcode.isEmpty()) return;
        
        try {
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
            
            // Search in DB
            Product p = DB_operation.searchProductByParcode(barcode);
            if (p != null) {
                // We need the name, but searchProductByParcode returns a Product object which might not have name populated depending on implementation
                // Let's do a quick custom query to get name if needed, or trust DB_operation
                // DB_operation.searchProductByParcode selects Name, Price... so we should be good if we can access it.
                // However, Product class fields are protected. Let's assume we can access them or use a custom query.
                
                // Custom query to be safe and get Name
                String name = getProductName(barcode);
                
                CartItem item = new CartItem();
                item.setBarcode(barcode);
                item.setName(name != null ? name : "Unknown Product");
                item.setPrice(p.getPrice());
                item.setQuantity(1);
                
                cartList.add(item);
                updateTotals();
                barcodeField.clear();
            } else {
                showError("Not Found", "Product not found with barcode: " + barcode);
            }
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error adding item to cart");
        }
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
        
        try {
            int invoiceId = generateInvoiceId();
            // Remove any non-numeric characters (except decimal point) to handle currency symbols like $
            String totalText = totalLabel.getText().replaceAll("[^\\d.]", "");
            double total = totalText.isEmpty() ? 0 : Double.parseDouble(totalText);
            double discount = 0;
            try { discount = Double.parseDouble(discountField.getText()); } catch (Exception e) {}
            
            String username = SessionManager.getInstance().getUsername();
            String userId = SessionManager.getInstance().getUserId();
            // Assuming branch ID 1 for now
            int branchId = 1; 
            
            if (username == null) {
                showError("Session Error", "Please login again.");
                return;
            }
            
            // 1. Create Invoice
            if (DB_operation.addInvoice(invoiceId, java.sql.Date.valueOf(LocalDate.now()), total, username, userId, branchId)) {
                
                // 2. Create Sell Invoice (if customer provided, else use default/null)
                String customerId = customerField.getText();
                if (customerId != null && !customerId.isEmpty()) {
                    // Check if customer exists
                    if (DB_operation.isCustomerExist(customerId)) {
                        DB_operation.addSellInvoice(invoiceId, customerId, discount);
                    } else {
                        // Warn but proceed? Or fail? Let's proceed without linking customer for now or show error
                        // For simplicity, let's just log it
                        ExceptionLogger.logWarning("Customer ID not found: " + customerId);
                    }
                }
                
                // 3. Add items to invoice_has_product and update inventory
                addInvoiceItems(invoiceId);
                
                showSuccess("Sale Completed! Invoice #" + invoiceId);
                handleCancel(); // Clear cart
            } else {
                showError("Checkout Failed", "Could not create invoice.");
            }
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error during checkout");
            showError("Checkout Error", "An error occurred: " + e.getMessage());
        }
    }
    
    private int generateInvoiceId() {
        String sql = "SELECT MAX(ID) FROM invoice";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1) + 1;
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error generating invoice ID");
        }
        return 1001; // Default start ID
    }
    
    private void addInvoiceItems(int invoiceId) {
        String sql = "INSERT INTO invoice_has_product (Invoice_ID, Product_parcode, Quntaty, Total_price) VALUES (?, ?, ?, ?)";
        String updateInventorySql = "UPDATE inventory_has_product SET Quntaty = Quntaty - ? WHERE Product_parcode = ? AND Inventory_ID = 1";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             PreparedStatement psInv = conn.prepareStatement(updateInventorySql)) {
            
            for (CartItem item : cartList) {
                // Add to invoice_has_product
                ps.setInt(1, invoiceId);
                ps.setString(2, item.getBarcode());
                ps.setInt(3, item.getQuantity());
                ps.setDouble(4, item.getTotal());
                ps.addBatch();
                
                // Update Inventory
                psInv.setDouble(1, item.getQuantity());
                psInv.setString(2, item.getBarcode());
                psInv.addBatch();
            }
            
            ps.executeBatch();
            psInv.executeBatch();
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error adding invoice items");
        }
    }
    
    @FXML
    private void handleCancel() {
        cartList.clear();
        barcodeField.clear();
        discountField.setText("0");
        customerField.clear();
        updateTotals();
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
