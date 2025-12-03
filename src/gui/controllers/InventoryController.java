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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import util.ExceptionLogger;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for Inventory Management View
 */
public class InventoryController implements Initializable {

    @FXML private TextField searchField;
    @FXML private CheckBox lowStockFilter;
    @FXML private TableView<InventoryItem> inventoryTable;
    @FXML private Label itemCountLabel;
    
    @FXML private TableColumn<InventoryItem, String> colBarcode;
    @FXML private TableColumn<InventoryItem, String> colName;
    @FXML private TableColumn<InventoryItem, Double> colQuantity;
    @FXML private TableColumn<InventoryItem, Integer> colReorderLevel;
    @FXML private TableColumn<InventoryItem, String> colStatus;
    @FXML private TableColumn<InventoryItem, Void> colActions;
    
    private ObservableList<InventoryItem> inventoryList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTableColumns();
            loadInventory();
            ExceptionLogger.logInfo("Inventory view initialized");
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing inventory view");
        }
    }
    
    private void setupTableColumns() {
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colReorderLevel.setCellValueFactory(new PropertyValueFactory<>("reorderLevel"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (item == null || empty) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if (item.equals("Low Stock")) {
                        setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: green;");
                    }
                }
            }
        });
        
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button updateBtn = new Button("🔄 Update");
            
            {
                updateBtn.setOnAction(event -> {
                    InventoryItem item = getTableView().getItems().get(getIndex());
                    handleUpdateStock(item);
                });
                updateBtn.getStyleClass().addAll("btn", "btn-secondary");
                updateBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10;");
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, updateBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void loadInventory() {
        try {
            inventoryList.clear();
            
            // Assuming Inventory_ID = 1 for main branch
            String query = "SELECT p.parcode, p.Name, i.Quntaty, i.reordr_level " +
                          "FROM product p " +
                          "LEFT JOIN inventory_has_product i ON p.parcode = i.Product_parcode " +
                          "WHERE i.Inventory_ID = 1 OR i.Inventory_ID IS NULL";
            
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    InventoryItem item = new InventoryItem();
                    item.setBarcode(rs.getString("parcode"));
                    item.setName(rs.getString("Name"));
                    
                    double qty = rs.getDouble("Quntaty");
                    // Handle NULL quantity (product exists but not in inventory table yet)
                    if (rs.wasNull()) qty = 0;
                    
                    item.setQuantity(qty);
                    item.setReorderLevel(rs.getInt("reordr_level"));
                    
                    inventoryList.add(item);
                }
                
                inventoryTable.setItems(inventoryList);
                updateItemCount();
            }
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Database error loading inventory");
            showError("Database Error", "Failed to load inventory");
        }
    }
    
    @FXML
    private void handleAddStock() {
        // Dialog to add new product to inventory or update existing
        // For simplicity, we'll just show update dialog for now or a simple add dialog
        // Since we are listing ALL products (LEFT JOIN), "Add Stock" is effectively "Update Stock" for 0 qty items
        // But let's make a dialog that allows entering barcode manually
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Add Stock");
        dialog.setHeaderText("Enter Product Barcode");
        dialog.setContentText("Barcode:");
        
        dialog.showAndWait().ifPresent(barcode -> {
            // Check if product exists
            if (DB_operation.isProductExist(barcode)) {
                // Find item in list or create new
                InventoryItem item = inventoryList.stream()
                        .filter(i -> i.getBarcode().equals(barcode))
                        .findFirst()
                        .orElse(null);
                
                if (item != null) {
                    handleUpdateStock(item);
                } else {
                    // Should not happen if we load all products, but just in case
                    showError("Error", "Product found but not in list. Try refreshing.");
                }
            } else {
                showError("Error", "Product not found. Please add product in Products view first.");
            }
        });
    }
    
    private void handleUpdateStock(InventoryItem item) {
        Dialog<Double> dialog = new Dialog<>();
        dialog.setTitle("Update Stock");
        dialog.setHeaderText("Update Stock for: " + item.getName());
        
        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField qtyField = new TextField(String.valueOf(item.getQuantity()));
        TextField reorderField = new TextField(String.valueOf(item.getReorderLevel()));
        
        grid.add(new Label("Quantity:"), 0, 0); grid.add(qtyField, 1, 0);
        grid.add(new Label("Reorder Level:"), 0, 1); grid.add(reorderField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                try {
                    return Double.parseDouble(qtyField.getText());
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
        
        dialog.showAndWait().ifPresent(newQty -> {
            try {
                int newReorder = Integer.parseInt(reorderField.getText());
                
                // Check if record exists in inventory_has_product
                if (recordExists(item.getBarcode())) {
                     if (DB_operation.updateInventoryQuantity(1, item.getBarcode(), newQty)) {
                         // Also update reorder level (custom query needed as DB_operation might not have it)
                         updateReorderLevel(1, item.getBarcode(), newReorder);
                         showSuccess("Stock updated successfully");
                         loadInventory();
                     }
                } else {
                    // Insert new record
                    if (DB_operation.addInventoryProduct(1, item.getBarcode(), newQty, newReorder)) {
                        showSuccess("Stock added successfully");
                        loadInventory();
                    }
                }
            } catch (Exception e) {
                showError("Error", "Invalid input");
            }
        });
    }
    
    private boolean recordExists(String barcode) {
        // Helper to check if inventory record exists
        String sql = "SELECT COUNT(*) FROM inventory_has_product WHERE Inventory_ID = 1 AND Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, barcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error checking inventory record");
        }
        return false;
    }
    
    private void updateReorderLevel(int inventoryId, String barcode, int level) {
        String sql = "UPDATE inventory_has_product SET reordr_level = ? WHERE Inventory_ID = ? AND Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, level);
            ps.setInt(2, inventoryId);
            ps.setString(3, barcode);
            ps.executeUpdate();
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error updating reorder level");
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        filterList(searchText, lowStockFilter.isSelected());
    }
    
    @FXML
    private void handleFilter() {
        handleSearch();
    }
    
    private void filterList(String searchText, boolean lowStockOnly) {
        ObservableList<InventoryItem> filtered = inventoryList.filtered(item -> {
            boolean matchesSearch = searchText.isEmpty() || 
                                  item.getName().toLowerCase().contains(searchText) || 
                                  item.getBarcode().toLowerCase().contains(searchText);
            
            boolean matchesLowStock = !lowStockOnly || item.getQuantity() <= item.getReorderLevel();
            
            return matchesSearch && matchesLowStock;
        });
        
        inventoryTable.setItems(filtered);
        updateItemCount();
    }
    
    @FXML
    private void handleRefresh() {
        searchField.clear();
        lowStockFilter.setSelected(false);
        loadInventory();
    }
    
    private void updateItemCount() {
        itemCountLabel.setText("Total: " + inventoryTable.getItems().size() + " items");
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

    public static class InventoryItem {
        private SimpleStringProperty barcode = new SimpleStringProperty();
        private SimpleStringProperty name = new SimpleStringProperty();
        private SimpleDoubleProperty quantity = new SimpleDoubleProperty();
        private SimpleIntegerProperty reorderLevel = new SimpleIntegerProperty();
        
        public String getBarcode() { return barcode.get(); }
        public void setBarcode(String v) { barcode.set(v); }
        public SimpleStringProperty barcodeProperty() { return barcode; }
        
        public String getName() { return name.get(); }
        public void setName(String v) { name.set(v); }
        public SimpleStringProperty nameProperty() { return name; }
        
        public double getQuantity() { return quantity.get(); }
        public void setQuantity(double v) { quantity.set(v); }
        public SimpleDoubleProperty quantityProperty() { return quantity; }
        
        public int getReorderLevel() { return reorderLevel.get(); }
        public void setReorderLevel(int v) { reorderLevel.set(v); }
        public SimpleIntegerProperty reorderLevelProperty() { return reorderLevel; }
        
        public String getStatus() {
            return getQuantity() <= getReorderLevel() ? "Low Stock" : "OK";
        }
        public SimpleStringProperty statusProperty() {
            return new SimpleStringProperty(getStatus());
        }
    }
}
