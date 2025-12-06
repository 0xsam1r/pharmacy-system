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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.layout.GridPane;

 
public class ProductsController implements Initializable {
    
    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryComboBox;
    @FXML private TableView<ProductData> productsTable;
    @FXML private Label productCountLabel;
    
     
    @FXML private TableColumn<ProductData, String> colBarcode;
    @FXML private TableColumn<ProductData, String> colName;
    @FXML private TableColumn<ProductData, String> colCategory;
    @FXML private TableColumn<ProductData, Double> colPrice;
    @FXML private TableColumn<ProductData, Integer> colUnit;
    @FXML private TableColumn<ProductData, String> colDosage;
    @FXML private TableColumn<ProductData, Void> colActions;
    
    private ObservableList<ProductData> productsList = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTableColumns();
            loadCategories();
            loadProducts();
            
             
            if (categoryComboBox != null) {
                categoryComboBox.setOnAction(event -> handleCategoryFilter());
            }
            
        productCountLabel.setText("Total: 0 products");

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty()) {
                handleSearch();   
            } else {
                 productsTable.setItems(productsList);  
                 updateProductCount();
            }
        });
        
        setupAutocompletion();

        ExceptionLogger.logInfo("Products view initialized");
    } catch (Exception e) {
        ExceptionLogger.logException(e, "Error initializing products view");
        showError("Initialization Error", "Failed to load products view");
    }
}

private void setupAutocompletion() {
    ContextMenu suggestionsPopup = new ContextMenu();
    
    searchField.textProperty().addListener((observable, oldValue, newValue) -> {
        if (newValue == null || newValue.length() < 2) {
            suggestionsPopup.hide();
            return;
        }

        List<String> matches = new ArrayList<>();
         
        for (ProductData p : productsList) {
            if (p.getName().toLowerCase().contains(newValue.toLowerCase())) {
                matches.add(p.getName());
            }
        }
        
        if (!matches.isEmpty()) {
            suggestionsPopup.getItems().clear();
             
            for (int i = 0; i < Math.min(matches.size(), 10); i++) {
                String match = matches.get(i);
                MenuItem item = new MenuItem(match);
                item.setOnAction(e -> {
                    searchField.setText(match);
                    handleSearch();
                    suggestionsPopup.hide();
                });
                suggestionsPopup.getItems().add(item);
            }
            if (!suggestionsPopup.isShowing()) {
                suggestionsPopup.show(searchField, javafx.geometry.Side.BOTTOM, 0, 0);
            }
        } else {
            suggestionsPopup.hide();
        }
    });

     
    searchField.focusedProperty().addListener((obs, oldVal, newVal) -> {
        if (!newVal) suggestionsPopup.hide();
    });
}
    
    private void setupTableColumns() {
        colBarcode.setCellValueFactory(new PropertyValueFactory<>("barcode"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));
        colDosage.setCellValueFactory(new PropertyValueFactory<>("dosage"));
        
         
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Edit");
            private final Button deleteBtn = new Button("Delete");
            
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
            
             
            String query = "SELECT name FROM category ORDER BY name";
            try (Connection conn = DB.DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    String categoryName = rs.getString("name");
                    if (categoryName != null && !categoryName.trim().isEmpty()) {
                        categories.add(categoryName);
                    }
                }
            } catch (SQLException e) {
                ExceptionLogger.logException(e, "Error fetching categories from database");
                 
                categories.add("Medicine");
                categories.add("Cosmetic");
            }
            
            categoryComboBox.setItems(categories);
            categoryComboBox.getSelectionModel().selectFirst();
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error loading categories");
        }
    }
    
    private void loadProducts() {
        try {
            productsList.clear();
            
            String query = "SELECT p.parcode, p.Name, p.Price, p.Uints, c.name as category, " +
                          "(SELECT d.active_ing FROM dosage_form d " +
                          " JOIN medicine_has_dosage_form md ON d.ID = md.dosage_form_ID " +
                          " JOIN medicine m ON md.medicine_Product_parcode = m.Product_parcode " +
                          " WHERE m.Product_parcode = p.parcode LIMIT 1) as dosage " +
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
                    product.setUnit(rs.getInt("Uints"));
                    product.setCategory(rs.getString("category"));
                    product.setDosage(rs.getString("dosage"));
                    
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
             
            Dialog<ProductData> dialog = createProductDialog("Add New Product", null);
            
            Optional<ProductData> result = dialog.showAndWait();
            result.ifPresent(product -> {
                try {
                     
                    validateProduct(product);
                    
                     
                    int categoryId = getCategoryIdByName(product.getCategory());
                    if (categoryId == -1) {
                        showError("Category Error", "Invalid category selected");
                        return;
                    }
                    
                     
                    boolean success = DB_operation.addProduct(
                        product.getBarcode(),
                        product.getName(),
                        product.getPrice(),
                        product.getUnit(),
                        categoryId
                    );
                    
                    if (success) {
                        // Handle Dosage Form linking
                        String dosage = product.getDosage();
                        if (dosage != null && !dosage.isEmpty()) {
                            try (Connection conn = DB.DBConnection.getConnection()) {
                                // 1. Ensure Medicine record
                                String checkMed = "SELECT Product_parcode FROM medicine WHERE Product_parcode = ?";
                                try (PreparedStatement ps = conn.prepareStatement(checkMed)) {
                                    ps.setString(1, product.getBarcode());
                                    if (!ps.executeQuery().next()) {
                                        try (PreparedStatement startMed = conn.prepareStatement("INSERT INTO medicine (Product_parcode) VALUES (?)")) {
                                            startMed.setString(1, product.getBarcode());
                                            startMed.executeUpdate();
                                        }
                                    }
                                }
                                
                                // 2. Get Dosage ID
                                int dosageId = -1;
                                try (PreparedStatement psDos = conn.prepareStatement("SELECT ID FROM dosage_form WHERE active_ing = ?")) {
                                    psDos.setString(1, dosage);
                                    ResultSet rsDos = psDos.executeQuery();
                                    if (rsDos.next()) dosageId = rsDos.getInt("ID");
                                }
                                
                                // 3. Link
                                if (dosageId != -1) {
                                    String linkSql = "INSERT INTO medicine_has_dosage_form (medicine_Product_parcode, dosage_form_ID, Strength) VALUES (?, ?, ?)";
                                    try (PreparedStatement psLink = conn.prepareStatement(linkSql)) {
                                        psLink.setString(1, product.getBarcode());
                                        psLink.setInt(2, dosageId);
                                        psLink.setDouble(3, 0); // Default strength
                                        psLink.executeUpdate();
                                    }
                                }
                            } catch (SQLException e) {
                                ExceptionLogger.logException(e, "Error linking dosage form");
                            }
                        }
                    
                        showSuccess("Product and Dosage Info added successfully!");
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
                Connection conn = null;
                try {
                    validateProduct(editedProduct);
                    
                    // Resolve Category ID
                    int categoryId = getCategoryIdByName(editedProduct.getCategory());
                    if (categoryId == -1) {
                        showError("Category Error", "Invalid category selected");
                        return;
                    }

                    conn = DB.DBConnection.getConnection();
                    conn.setAutoCommit(false);
                    
                    String barcode = editedProduct.getBarcode();
                    
                    // 1. Update Product Basic Info
                    String updateProductSql = "UPDATE product SET Name = ?, Price = ?, Uints = ?, Category_ID = ? WHERE parcode = ?";
                    try (PreparedStatement ps = conn.prepareStatement(updateProductSql)) {
                        ps.setString(1, editedProduct.getName());
                        ps.setDouble(2, editedProduct.getPrice());
                        ps.setInt(3, editedProduct.getUnit());
                        ps.setInt(4, categoryId);
                        ps.setString(5, barcode);
                        ps.executeUpdate();
                    }
                    
                    // 2. Handle Dosage Form Update
                    String newDosage = editedProduct.getDosage();
                    if (newDosage != null && !newDosage.isEmpty()) {
                        // Ensure it's treated as Medicine
                        try (PreparedStatement ps = conn.prepareStatement("INSERT IGNORE INTO medicine (Product_parcode) VALUES (?)")) {
                            ps.setString(1, barcode);
                            ps.executeUpdate();
                        }
                        
                        // Get Dosage ID
                        int dosageId = -1;
                        try (PreparedStatement ps = conn.prepareStatement("SELECT ID FROM dosage_form WHERE active_ing = ?")) {
                            ps.setString(1, newDosage);
                            ResultSet rs = ps.executeQuery();
                            if (rs.next()) dosageId = rs.getInt("ID");
                        }
                        
                        if (dosageId != -1) {
                            // Remove old dosage links for this medicine
                            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM medicine_has_dosage_form WHERE medicine_Product_parcode = ?")) {
                                ps.setString(1, barcode);
                                ps.executeUpdate();
                            }
                            
                            // Add new link
                            try (PreparedStatement ps = conn.prepareStatement("INSERT INTO medicine_has_dosage_form (medicine_Product_parcode, dosage_form_ID, Strength) VALUES (?, ?, ?)")) {
                                ps.setString(1, barcode);
                                ps.setInt(2, dosageId);
                                ps.setDouble(3, 0); // Default strength
                                ps.executeUpdate();
                            }
                        }
                    } else {
                        // If dosage cleared/empty, maybe remove from medicine? 
                        // For now, just remove dosage link
                         try (PreparedStatement ps = conn.prepareStatement("DELETE FROM medicine_has_dosage_form WHERE medicine_Product_parcode = ?")) {
                            ps.setString(1, barcode);
                            ps.executeUpdate();
                        }
                    }

                    conn.commit();
                    showSuccess("Product updated successfully!");
                    loadProducts();
                    ExceptionLogger.logInfo("Product updated: " + editedProduct.getName());
                    
                } catch (ValidationException e) {
                    showError("Validation Error", e.getMessage());
                } catch (SQLException e) {
                    if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
                    ExceptionLogger.logException(e, "Error updating product");
                    showError("Update Error", "Failed to update product details: " + e.getMessage());
                } finally {
                    if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
                }
            });
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error in edit product dialog");
        }
    }
    
    private void handleDeleteProduct(ProductData product) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Product");
        alert.setContentText("Are you sure you want to delete: " + product.getName() + "?\n\n" +
                           "This will remove the product and all its inventory/batch records if no sales exist.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Resolve Category ID BEFORE opening the transaction connection
                // This prevents 'getCategoryIdByName' from closing the connection if it uses the same singleton/helper
                int catId = getCategoryIdByName(product.getCategory());
                
                Connection conn = null;
                try {
                    conn = DB.DBConnection.getConnection();
                    conn.setAutoCommit(false);
                    
                    String barcode = product.getBarcode().trim();
                    
                    // 1. Check Sales/Purchase History (Blocking)
                    try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM invoice_has_product WHERE Product_parcode = ? LIMIT 1")) {
                        ps.setString(1, barcode);
                        if (ps.executeQuery().next()) {
                            showError("Cannot Delete", "This product has sales history (Invoices). Cannot delete.");
                            return;
                        }
                    }
                    try (PreparedStatement ps = conn.prepareStatement("SELECT 1 FROM customer_buy_product WHERE Product_parcode = ? LIMIT 1")) {
                        ps.setString(1, barcode);
                        if (ps.executeQuery().next()) {
                            showError("Cannot Delete", "This product has customer purchase history. Cannot delete.");
                            return;
                        }
                    }
                    
                    // 2. Clear Dependencies
                    
                    // Medicine / Dosage Link
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM medicine_has_dosage_form WHERE medicine_Product_parcode = ?")) {
                        ps.setString(1, barcode);
                        ps.executeUpdate();
                    }
                    // Medicine / Cosmetic
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM medicine WHERE Product_parcode = ?")) {
                        ps.setString(1, barcode);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM cosmetic WHERE Product_parcode = ?")) {
                        ps.setString(1, barcode);
                        ps.executeUpdate();
                    }
                    
                    // Supplier / Branch / Inventory Links
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM supplier_has_product WHERE Product_parcode = ?")) {
                        ps.setString(1, barcode);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM bransh_has_product WHERE Product_parcode = ?")) {
                        ps.setString(1, barcode);
                        ps.executeUpdate();
                    }
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM inventory_has_product WHERE Product_parcode = ?")) {
                        ps.setString(1, barcode);
                        ps.executeUpdate();
                    }
                    
                    // Batches
                    try (PreparedStatement ps = conn.prepareStatement("DELETE FROM batch WHERE Product_parcode = ?")) {
                        ps.setString(1, barcode);
                        ps.executeUpdate();
                    }
                    
                    // 3. Delete Product (Using Composite Key if possible, else Barcode)
                    int rows = 0;
                    
                    if (catId != -1) {
                        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM product WHERE parcode = ? AND Category_ID = ?")) {
                            ps.setString(1, barcode);
                            ps.setInt(2, catId);
                            rows = ps.executeUpdate();
                        }
                    }
                    
                    // Fallback: If Category mismatch or generic delete needed
                    if (rows == 0) {
                        try (PreparedStatement ps = conn.prepareStatement("DELETE FROM product WHERE parcode = ?")) {
                             ps.setString(1, barcode);
                             rows = ps.executeUpdate();
                        }
                    }
                    
                    if (rows > 0) {
                        conn.commit();
                        showSuccess("Product deleted successfully!");
                        
                        // Force UI refresh on JavaFX thread
                        javafx.application.Platform.runLater(() -> {
                            handleRefresh();
                        });
                        
                        ExceptionLogger.logInfo("Product deleted: " + product.getName());
                    } else {
                        conn.rollback();
                        showError("Delete Failed", "Could not delete product record (Row not found).");
                    }
                    
                } catch (SQLException e) {
                    if(conn!=null) try { conn.rollback(); } catch(SQLException ex){}
                    ExceptionLogger.logException(e, "Error deleting product");
                    if (e.getMessage().contains("constraint")) {
                         showError("Cannot Delete", "Product has associated records preventing deletion (e.g. constraints).");
                    } else {
                         showError("Delete Failed", "Database Error: " + e.getMessage());
                    }
                } finally {
                    if(conn!=null) try { conn.setAutoCommit(true); conn.close(); } catch(SQLException ex){}
                }
            }
        });
    }
    
    @FXML private TextField activeIngredientSearchField;  

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
    private void handleActiveIngredientSearch() {
        String ingredient = activeIngredientSearchField.getText().trim();
        if (ingredient.isEmpty()) {
            productsTable.setItems(productsList);
            updateProductCount();
            return;
        }
        
        try {
            ObservableList<ProductData> ingredientMatches = FXCollections.observableArrayList();
            
            String sql = "SELECT p.parcode, p.Name, p.Price, p.Uints, c.name as category, d.active_ing as dosage " +
                         "FROM product p " +
                         "LEFT JOIN category c ON p.Category_ID = c.ID " +
                         "JOIN medicine m ON p.parcode = m.Product_parcode " +
                         "JOIN medicine_has_dosage_form md ON m.Product_parcode = md.medicine_Product_parcode " +
                         "JOIN dosage_form d ON md.dosage_form_ID = d.ID " +
                         "WHERE d.active_ing LIKE ?";
                         
            try (Connection conn = DB.DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, "%" + ingredient + "%");
                ResultSet rs = ps.executeQuery();
                 while (rs.next()) {
                    ProductData product = new ProductData();
                    product.setBarcode(rs.getString("parcode"));
                    product.setName(rs.getString("Name"));
                    product.setPrice(rs.getDouble("Price"));
                    product.setUnit(rs.getInt("Uints"));
                    product.setCategory(rs.getString("category"));
                    product.setDosage(rs.getString("dosage"));
                    
                    // Avoid duplicates if multiple dosage forms match (though unlikely with this query struct per row)
                    boolean exists = false;
                    for(ProductData existing : ingredientMatches) {
                        if(existing.getBarcode().equals(product.getBarcode())) { exists = true; break; }
                    }
                    if(!exists) ingredientMatches.add(product);
                }
            }
            
            productsTable.setItems(ingredientMatches);
            updateProductCount();
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error searching by active ingredient");
            showError("Search Error", "Failed to search by active ingredient");
        }
    }
    
    @FXML
    private void handleCategoryFilter() {
        String category = categoryComboBox.getValue();
        
        if (category == null || category.equals("All Categories")) {
            handleSearch();  
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
        TextField unitField = new TextField();
        ComboBox<String> categoryCombo = new ComboBox<>();
        
         
        try {
            ObservableList<String> categories = FXCollections.observableArrayList();
            String query = "SELECT ID, name FROM category ORDER BY name";
            try (Connection conn = DB.DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    String categoryName = rs.getString("name");
                    if (categoryName != null && !categoryName.trim().isEmpty()) {
                        categories.add(categoryName);
                    }
                }
            }
            categoryCombo.setItems(categories);
            if (!categories.isEmpty()) {
                categoryCombo.getSelectionModel().selectFirst();
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error loading categories for dialog");
        }
        
        if (existingProduct != null) {
            barcodeField.setText(existingProduct.getBarcode());
            barcodeField.setDisable(true);
            nameField.setText(existingProduct.getName());
            priceField.setText(String.valueOf(existingProduct.getPrice()));
            unitField.setText(String.valueOf(existingProduct.getUnit()));
            if (existingProduct.getCategory() != null) {
                categoryCombo.setValue(existingProduct.getCategory());
            }
        }
        
        grid.add(new Label("Barcode:"), 0, 0);
        grid.add(barcodeField, 1, 0);
        grid.add(new Label("Name:"), 0, 1);
        grid.add(nameField, 1, 1);
        grid.add(new Label("Category:"), 0, 2);
        grid.add(categoryCombo, 1, 2);
        grid.add(new Label("Price:"), 0, 3);
        grid.add(priceField, 1, 3);
        grid.add(new Label("Units Per Product:"), 0, 4);
        grid.add(unitField, 1, 4);

        HBox dosageBox = new HBox(10);
        ComboBox<String> dosageCombo = new ComboBox<>();
        Button addDosageBtn = new Button("+");
        addDosageBtn.setOnAction(e -> {
            TextInputDialog newDosageDialog = new TextInputDialog();
            newDosageDialog.setTitle("Add Dosage Form");
            newDosageDialog.setHeaderText("Create New Dosage Form");
            newDosageDialog.setContentText("Name (e.g., Tablet, Syrup):");
            
            newDosageDialog.showAndWait().ifPresent(newName -> {
                if (newName.trim().isEmpty()) return;
                
                try (Connection conn = DB.DBConnection.getConnection()) {
                    // Check existence
                    try (PreparedStatement check = conn.prepareStatement("SELECT 1 FROM dosage_form WHERE active_ing = ?")) {
                        check.setString(1, newName.trim());
                        if (check.executeQuery().next()) {
                            showError("Duplicate", "Dosage form already exists.");
                            return;
                        }
                    }
                    
                    // Insert
                    // Get Max ID
                    int nextId = 1;
                    try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery("SELECT MAX(ID) FROM dosage_form")) {
                        if (rs.next()) nextId = rs.getInt(1) + 1;
                    }
                    
                    try (PreparedStatement insert = conn.prepareStatement("INSERT INTO dosage_form (ID, active_ing) VALUES (?, ?)")) {
                        insert.setInt(1, nextId);
                        insert.setString(2, newName.trim());
                        insert.executeUpdate();
                    }
                    
                    showSuccess("Dosage Form '" + newName + "' added.");
                    dosageCombo.getItems().add(newName.trim());
                    dosageCombo.setValue(newName.trim());
                    
                } catch (SQLException ex) {
                    ExceptionLogger.logException(ex, "Error adding dosage form");
                    showError("Error", "Failed to add dosage form: " + ex.getMessage());
                }
            });
        });
        
        try (Connection conn = DB.DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT active_ing FROM dosage_form ORDER BY active_ing")) {
             while (rs.next()) dosageCombo.getItems().add(rs.getString("active_ing"));
        } catch(SQLException e) {}
        
        dosageBox.getChildren().addAll(dosageCombo, addDosageBtn);
        grid.add(new Label("Dosage Form:"), 0, 5);
        grid.add(dosageBox, 1, 5);
        
        if (existingProduct != null && existingProduct.getDosage() != null) {
            dosageCombo.setValue(existingProduct.getDosage());
        }
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                ProductData product = new ProductData();
                product.setBarcode(barcodeField.getText());
                product.setName(nameField.getText());
                product.setCategory(categoryCombo.getValue());
                try {
                    product.setPrice(Double.parseDouble(priceField.getText()));
                    product.setUnit(Integer.parseInt(unitField.getText()));
                } catch (NumberFormatException e) {
                    return null;
                }
                product.setDosage(dosageCombo.getValue());
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
        if (product.getCategory() == null || product.getCategory().trim().isEmpty()) {
            throw new ValidationException("Category is required", "category");
        }
        if (product.getPrice() <= 0) {
            throw new ValidationException("Price must be greater than 0", "price");
        }
        if (product.getUnit() <= 0) {
            throw new ValidationException("Units per product must be greater than 0", "unit");
        }
    }

    private int getCategoryIdByName(String categoryName) {
        if (categoryName == null || categoryName.trim().isEmpty()) {
            return -1;
        }
        
        try {
            String query = "SELECT ID FROM category WHERE name = ?";
            try (Connection conn = DB.DBConnection.getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(query)) {
                
                pstmt.setString(1, categoryName);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("ID");
                    }
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error getting category ID for: " + categoryName);
        }
        
        return -1;
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
    
     
    public static class ProductData {
        private SimpleStringProperty barcode = new SimpleStringProperty();
        private SimpleStringProperty name = new SimpleStringProperty();
        private SimpleStringProperty category = new SimpleStringProperty();
        private SimpleDoubleProperty price = new SimpleDoubleProperty();
        private int unit;  
        
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
        
        public int getUnit() { return unit; }
        public void setUnit(int value) { unit = value; }
        
        private SimpleStringProperty dosage = new SimpleStringProperty();
        public String getDosage() { return dosage.get(); }
        public void setDosage(String value) { dosage.set(value); }
        public SimpleStringProperty dosageProperty() { return dosage; }
    }
}
