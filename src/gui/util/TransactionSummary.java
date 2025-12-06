package gui.util;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import util.ExceptionLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

public class TransactionSummary {

    private static final Map<String, ProductState> preStates = new HashMap<>();

    public static class ProductState {
        String barcode;
        String name;
        double inventoryQty;
        // Map<BatchNumber, BatchQty>
        Map<String, Double> batchQuantities = new HashMap<>();
    }

    public static class ChangeRecord {
        private String productName;
        private String batchInfo;
        private String inventoryChange;
        
        public String getProductName() { return productName; }
        public String getBatchInfo() { return batchInfo; }
        public String getInventoryChange() { return inventoryChange; }
        
        public ChangeRecord(String name, String batch, String inv) {
            this.productName = name;
            this.batchInfo = batch;
            this.inventoryChange = inv;
        }
    }

    public static void clearSnapshots() {
        preStates.clear();
    }

    // Step 1: Snapshot Before Transaction
    public static void snapshotState(Connection conn, String barcode) {
        try {
            ProductState state = new ProductState();
            state.barcode = barcode;
            
            // Get Name & Total Inv
            // Note: Inventory ID 1 is default
            String sqlP = "SELECT p.Name, i.Quntaty FROM product p " +
                          "LEFT JOIN inventory_has_product i ON p.parcode = i.Product_parcode AND i.Inventory_ID = 1 " +
                          "WHERE p.parcode = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlP)) {
                ps.setString(1, barcode);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        state.name = rs.getString("Name");
                        state.inventoryQty = rs.getDouble("Quntaty");
                        if (rs.wasNull()) state.inventoryQty = 0.0;
                    }
                }
            }
            
            // Get Batches
            String sqlB = "SELECT Batch_number, Quantaty FROM batch WHERE Product_parcode = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlB)) {
                ps.setString(1, barcode);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        state.batchQuantities.put(rs.getString("Batch_number"), rs.getDouble("Quantaty"));
                    }
                }
            }
            
            preStates.put(barcode, state);
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error snapshotting state for " + barcode);
        }
    }

    // Step 2: Show Dialog After Transaction
    // Pass the SAME connection used in transaction to see uncommitted changes
    public static void showSummary(Connection conn, String title, String financialSummary) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Transaction Breakdown");
        dialog.setHeaderText(title);
        
        TableView<ChangeRecord> table = new TableView<>();
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(300);
        table.setPrefWidth(600);
        
        table.getColumns().add(createCol("Product", "productName", 150));
        table.getColumns().add(createCol("Batch Details (Before -> After)", "batchInfo", 300));
        table.getColumns().add(createCol("Inventory (Before -> After)", "inventoryChange", 150));
        
        // Calculate Changes
        for (String barcode : preStates.keySet()) {
            ProductState oldState = preStates.get(barcode);
            
            try {
                // Get New State
                String name = oldState.name;
                if (name == null) name = "Unknown Product";
                
                double newInvQty = 0;
                
                // Check Inventory (ID 1)
                String sqlInv = "SELECT Quntaty FROM inventory_has_product WHERE Product_parcode = ? AND Inventory_ID = 1";
                try (PreparedStatement ps = conn.prepareStatement(sqlInv)) {
                    ps.setString(1, barcode);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) newInvQty = rs.getDouble("Quntaty");
                    }
                }
                
                StringBuilder batchDiff = new StringBuilder();
                
                // Get All Current Batches (to catch new ones or changed ones)
                // We re-query the batch table. 
                String sqlB = "SELECT Batch_number, Quantaty FROM batch WHERE Product_parcode = ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlB)) {
                    ps.setString(1, barcode);
                    try (ResultSet rs = ps.executeQuery()) {
                        while (rs.next()) {
                            String bNo = rs.getString("Batch_number");
                            double newQty = rs.getDouble("Quantaty");
                            
                            Double oldQty = oldState.batchQuantities.get(bNo);
                            
                            if (oldQty == null) {
                                // New Batch Created
                                batchDiff.append(String.format("[NEW %s]: 0 ➔ %.1f\n", bNo, newQty));
                            } else if (Math.abs(newQty - oldQty) > 0.001) {
                                // Existing Batch Changed
                                batchDiff.append(String.format("[%s]: %.1f ➔ %.1f\n", bNo, oldQty, newQty));
                            }
                        }
                    }
                }
                
                String invDiff = String.format("%.1f ➔ %.1f", oldState.inventoryQty, newInvQty);
                if (batchDiff.length() == 0) batchDiff.append("No Batch Quantity Change");
                
                table.getItems().add(new ChangeRecord(name, batchDiff.toString(), invDiff));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(10));
        
        Label finLabel = new Label(financialSummary);
        finLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        finLabel.setWrapText(true);
        
        content.getChildren().addAll(finLabel, new Separator(), table);
        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        
        dialog.showAndWait();
        clearSnapshots();
    }
    
    private static TableColumn<ChangeRecord, String> createCol(String title, String field, double width) {
        TableColumn<ChangeRecord, String> col = new TableColumn<>(title);
        col.setCellValueFactory(new PropertyValueFactory<>(field));
        col.setPrefWidth(width);
        return col;
    }
}
