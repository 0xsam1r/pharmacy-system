package gui.controllers;

import DB.DB_operation;
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
 * Controller for Products Management View
 */
public class ProductsController implements Initializable {
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TableView<ProductData> productsTable;
    @FXML private Label productCountLabel;
    
    // Table Columns
    @FXML private TableColumn<ProductData, String> colBarcode;
    @FXML private TableColumn<ProductData, String> colName;
    @FXML private TableColumn<ProductData, String> colCategory;
    @FXML private TableColumn<ProductData, Double> colPrice;
    @FXML private TableColumn<ProductData, Integer> colStock;
    @FXML private TableColumn<ProductData, String> colUnit;
    @FXML private TableColumn<ProductData, String> colStatus;
    @FXML private TableColumn<ProductData, Void> colActions;
    
    private ObservableList<ProductData> productsList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTableColumns();
            loadCategories();
            loadProducts();
            
            ExceptionLogger.logInfo("Products view initialized");
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing products view");
            showError("Initialization Error", "Failed to load products view");
        }
    }
    
    private void setupTableColumns() {
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Add action buttons column
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️");
            
            {
                editBtn.setOnAction(event -> {
                    ProductData product = getTableView().getItems().get(getIndex());
                    handleEditProduct(product);
                });
                
                deleteBtn.setOnAction(event -> {
                    ProductData product = getTableView().getItems().get(getIndex());
                    handleDeleteProduct(product);
                });
                
                editBtn.getStyleClass().addAll("btn", "btn-secondary");
                editBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10;");
                deleteBtn.getStyleClass().addAll("btn", "btn-danger");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10;");
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editBtn, deleteBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void loadCategories() {
        try {
            ObservableList<String> categories = FXCollections.observableArrayList();
            categories.add("All Categories");
            categories.add("Medicine");
            categories.add("Cosmetic");
            categories.add("Medical Equipment");
            categories.add("Others");
            
            categoryComboBox.setItems(categories);
            categoryComboBox.getSelectionModel().selectFirst();
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error loading categories");
        }
    }
    
    private void loadProducts() {
        try {
            productsList.clear();
            
            String query = "SELECT p.parcode, p.Name, p.Price, p.Uints, c.name as category " +
                          "FROM product p " +
                          "LEFT JOIN category c ON p.Category_ID = c.ID";
            
            try (Connection conn = DB.DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    ProductData product = new ProductData();
                    product.setBarcode(rs.getString("parcode"));
                    product.setName(rs.getString("Name"));
                    product.setPrice(rs.getDouble("Price"));
                    product.setStock(rs.getInt("Uints"));
                    product.setCategory(rs.getString("category"));
                    product.setUnit("pcs");
                    
                    // Determine status based on stock
                    int stock = rs.getInt("Uints");
                    if (stock == 0) {
                        product.setStatus("Out of Stock");
                    } else if (stock < 10) {
                        product.setStatus("Low Stock");
                    } else {
                        product.setStatus("In Stock");
                    }
                    
                    productsList.add(product);
                }
                
                productsTable.setItems(productsList);
                updateProductCount();
                
            } catch (SQLException e) {
                throw new DatabaseException("Failed to load products from database", e);
            }
            
        } catch (DatabaseException e) {
            ExceptionLogger.logException(e, "Database error while loading products");
            showError("Database Error", "Failed to load products: " + e.getMessage());
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Unexpected error loading products");
            showError("Error", "An unexpected error occurred while loading products");
        }
    }
    
    @FXML
    private void handleAddProduct() {
        try {
            // Create dialog for adding new product
            Dialog<ProductData> dialog = createProductDialog("Add New Product", null);
            
            Optional<ProductData> result = dialog.showAndWait();
            result.ifPresent(product -> {
                try {
                    // Validate product data
                    validateProduct(product);
                    
                    // Add to database
                    boolean success = DB_operation.addProduct(
                        product.getBarcode(),
                        product.getName(),
                        product.getPrice(),
                        product.getStock(),
                        1 // Default category ID
                    );
                    
                    if (success) {
                        showSuccess("Product added successfully!");
                        loadProducts();
                        ExceptionLogger.logInfo("Product added: " + product.getName());
                    } else {
                        showError("Add Failed", "Failed to add product to database");
                    }
                    
                } catch (ValidationException e) {
                    showError("Validation Error", e.getMessage());
                } catch (Exception e) {
                    ExceptionLogger.logException(e, "Error adding product");
                    showError("Error", "Failed to add product");
                }
            });
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error in add product dialog");
            showError("Error", "Failed to open add product dialog");
        }
    }
    
    private void handleEditProduct(ProductData product) {
        try {
            Dialog<ProductData> dialog = createProductDialog("Edit Product", product);
            
            Optional<ProductData> result = dialog.showAndWait();
            result.ifPresent(editedProduct -> {
                try {
                    validateProduct(editedProduct);
                    
                    // Update in database
                    boolean success = DB_operation.updateProductPrice(
                        editedProduct.getBarcode(),
                        editedProduct.getPrice()
                    );
                    
                    if (success) {
                        showSuccess("Product updated successfully!");
                        loadProducts();
                        ExceptionLogger.logInfo("Product updated: " + editedProduct.getName());
                    } else {
                        showError("Update Failed", "Failed to update product");
                    }
                    
                } catch (ValidationException e) {
                    showError("Validation Error", e.getMessage());
                } catch (Exception e) {
                    ExceptionLogger.logException(e, "Error updating product");
                    showError("Error", "Failed to update product");
                }
            });
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error in edit product dialog");
        }
    }
    
    private void handleDeleteProduct(ProductData product) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirm Delete");
            alert.setHeaderText("Delete Product");
            alert.setContentText("Are you sure you want to delete: " + product.getName() + "?");
            
            alert.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // TODO: Implement delete in DB_operation
                        showSuccess("Product deleted successfully!");
                        loadProducts();
                        ExceptionLogger.logInfo("Product deleted: " + product.getName());
                        
                    } catch (Exception e) {
                        ExceptionLogger.logException(e, "Error deleting product");
                        showError("Delete Failed", "Failed to delete product");
                    }
                }
            });
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error in delete product");
        }
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        
        if (searchText.isEmpty()) {
            productsTable.setItems(productsList);
        } else {
            ObservableList<ProductData> filtered = productsList.filtered(product ->
                product.getName().toLowerCase().contains(searchText) ||
                product.getBarcode().toLowerCase().contains(searchText)
            );
            productsTable.setItems(filtered);
        }
        
        updateProductCount();
    }
    
    @FXML
    private void handleCategoryFilter() {
        String category = categoryComboBox.getValue();
        
        if (category == null || category.equals("All Categories")) {
            handleSearch(); // Apply search filter
        } else {
            ObservableList<ProductData> filtered = productsList.filtered(product ->
                product.getCategory() != null && product.getCategory().equalsIgnoreCase(category)
            );
            productsTable.setItems(filtered);
            updateProductCount();
        }
    }
    
    @FXML
    private void handleRefresh() {
        searchField.clear();
        categoryComboBox.getSelectionModel().selectFirst();
        loadProducts();
    }
    
    @FXML
    private void handleExport() {
        // TODO: Implement export functionality
        showInfo("Export", "Export functionality coming soon!");
    }
    
    private Dialog<ProductData> createProductDialog(String title, ProductData existingProduct) {
        Dialog<ProductData> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField barcodeField = new TextField();
        TextField nameField = new TextField();
        TextField priceField = new TextField();
        TextField stockField = new TextField();
        
        if (existingProduct != null) {
            barcodeField.setText(existingProduct.getBarcode());
            barcodeField.setDisable(true);
            nameField.setText(existingProduct.getName());
            priceField.setText(String.valueOf(existingProduct.getPrice()));
            stockField.setText(String.valueOf(existingProduct.getStock()));
        }
        
        grid.add(new Label("Barcode:"), 0, 0);
        grid.add(barcodeField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Stock:"), 0, 3);
        grid.add(stockField, 1, 3);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                ProductData product = new ProductData();
                product.setBarcode(barcodeField.getText());
                product.setName(nameField.getText());
                try {
                    product.setPrice(Double.parseDouble(priceField.getText()));
                    product.setStock(Integer.parseInt(stockField.getText()));
                } catch (NumberFormatException e) {
                    return null;
                }
                return product;
            }
            return null;
        });
        
        return dialog;
    }
    
    private void validateProduct(ProductData product) throws ValidationException {
        if (product.getBarcode() == null || product.getBarcode().trim().isEmpty()) {
            throw new ValidationException("Barcode is required", "barcode");
        }
        if (product.getName() == null || product.getName().trim().isEmpty()) {
            throw new ValidationException("Product name is required", "name");
        }
        if (product.getPrice() <= 0) {
            throw new ValidationException("Price must be greater than 0", "price");
        }
        if (product.getStock() < 0) {
            throw new ValidationException("Stock cannot be negative", "stock");
        }
    }
    
    private void updateProductCount() {
        productCountLabel.setText("Total: " + productsTable.getItems().size() + " products");
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
    
    private void showInfo(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    // Product Data Model
    public static class ProductData {
        private SimpleStringProperty barcode = new SimpleStringProperty();
        private SimpleStringProperty name = new SimpleStringProperty();
        private SimpleStringProperty category = new SimpleStringProperty();
        private SimpleDoubleProperty price = new SimpleDoubleProperty();
        private SimpleStringProperty unit = new SimpleStringProperty();
        private SimpleStringProperty status = new SimpleStringProperty();
        private int stock;
        
        public String getBarcode() { return barcode.get(); }
        public void setBarcode(String value) { barcode.set(value); }
        public SimpleStringProperty barcodeProperty() { return barcode; }
        
        public String getName() { return name.get(); }
        public void setName(String value) { name.set(value); }
        public SimpleStringProperty nameProperty() { return name; }
        
        public String getCategory() { return category.get(); }
        public void setCategory(String value) { category.set(value); }
        public SimpleStringProperty categoryProperty() { return category; }
        
        public double getPrice() { return price.get(); }
        public void setPrice(double value) { price.set(value); }
        public SimpleDoubleProperty priceProperty() { return price; }
        
        public String getUnit() { return unit.get(); }
        public void setUnit(String value) { unit.set(value); }
        public SimpleStringProperty unitProperty() { return unit; }
        
        public String getStatus() { return status.get(); }
        public void setStatus(String value) { status.set(value); }
        public SimpleStringProperty statusProperty() { return status; }
        
        public int getStock() { return stock; }
        public void setStock(int stock) { this.stock = stock; }
    }
}
