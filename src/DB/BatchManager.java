package DB;

import util.ExceptionLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages batch operations with FEFO (First Expire First Out) logic
 * Ensures that products are sold from batches that expire first
 */
public class BatchManager {
    
    /**
     * Represents a batch with its details
     */
    public static class Batch {
        private String batchNumber;
        private String productParcode;
        private int quantity;
        private Date expireDate;
        private double cost;
        
        public Batch(String batchNumber, String productParcode, int quantity, Date expireDate, double cost) {
            this.batchNumber = batchNumber;
            this.productParcode = productParcode;
            this.quantity = quantity;
            this.expireDate = expireDate;
            this.cost = cost;
        }
        
        public String getBatchNumber() { return batchNumber; }
        public String getProductParcode() { return productParcode; }
        public int getQuantity() { return quantity; }
        public Date getExpireDate() { return expireDate; }
        public double getCost() { return cost; }
        public void setQuantity(int quantity) { this.quantity = quantity; }
    }
    
    /**
     * Get all batches for a product using an existing connection
     * This prevents closing the connection prematurely during transactions
     * @param conn Active connection
     * @param productParcode Product barcode
     * @return List of batches
     */
    public static List<Batch> getBatchesForProduct(Connection conn, String productParcode) {
        List<Batch> batches = new ArrayList<>();
        
        String query = "SELECT Batch_number, Quantaty, expire_date, cost " +
                      "FROM batch " +
                      "WHERE Product_parcode = ? AND Quantaty > 0 " +
                      "ORDER BY expire_date ASC"; // FEFO: earliest expiry first
        
        // Note: We use try-with-resources for PreparedStatement and ResultSet, 
        // BUT NOT for Connection, as it is passed from outside
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, productParcode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Batch batch = new Batch(
                        rs.getString("Batch_number"),
                        productParcode,
                        rs.getInt("Quantaty"),
                        rs.getDate("expire_date"),
                        rs.getDouble("cost")
                    );
                    batches.add(batch);
                }
            }
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error fetching batches for product: " + productParcode);
        }
        
        return batches;
    }

    /**
     * Get all batches for a product (Opens new connection - Legacy/Read-only use)
     * Use this ONLY when not in a transaction
     */
    public static List<Batch> getBatchesForProduct(String productParcode) {
        try (Connection conn = DBConnection.getConnection()) {
            return getBatchesForProduct(conn, productParcode);
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error getting connection for batches");
            return new ArrayList<>();
        }
    }

    /**
     * Reduce quantity from batches using FEFO logic (Uses existing connection for transaction safety)
     * @param conn Active database connection
     * @param productParcode Product barcode
     * @param quantityToReduce Quantity to reduce
     * @param inventoryId Inventory ID
     * @return true if successful
     */
    public static boolean reduceQuantityFromBatches(Connection conn, String productParcode, int quantityToReduce, int inventoryId) throws SQLException {
        ExceptionLogger.logInfo(String.format("🔵 FEFO START: Reducing %d units of %s", quantityToReduce, productParcode));

        // Get batches ordered by expiry date (FEFO) using the SAME connection
        List<Batch> batches = getBatchesForProduct(conn, productParcode);
        
        if (batches.isEmpty()) {
            ExceptionLogger.logInfo("❌ No batches found for: " + productParcode);
            return false;
        }
        
        int totalAvailable = batches.stream().mapToInt(Batch::getQuantity).sum();
        if (totalAvailable < quantityToReduce) {
            ExceptionLogger.logInfo(String.format("❌ Insufficient Stock: Need %d, Have %d", quantityToReduce, totalAvailable));
            return false;
        }
        
        int remainingToReduce = quantityToReduce;
        
        // Log batches found
        for (Batch b : batches) {
             ExceptionLogger.logInfo(String.format("   📦 Batch %s (Qty: %d, Exp: %s)", b.getBatchNumber(), b.getQuantity(), b.getExpireDate()));
        }

        for (Batch batch : batches) {
            if (remainingToReduce <= 0) break;
            
            int currentQty = batch.getQuantity();
            int take = Math.min(remainingToReduce, currentQty);
            int newQty = currentQty - take;
            
            String sql = "UPDATE batch SET Quantaty = ? WHERE Batch_number = ? AND Product_parcode = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, newQty);
                ps.setString(2, batch.getBatchNumber());
                ps.setString(3, productParcode);
                ps.executeUpdate();
            }
            
            remainingToReduce -= take;
            ExceptionLogger.logInfo(String.format("✅ Taken %d from batch %s (New Qty: %d)", take, batch.getBatchNumber(), newQty));
        }
        
        // Update Inventory Total
        String sqlInv = "UPDATE inventory_has_product SET Quntaty = Quntaty - ? WHERE Inventory_ID = ? AND Product_parcode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlInv)) {
            ps.setInt(1, quantityToReduce);
            ps.setInt(2, inventoryId);
            ps.setString(3, productParcode);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                 ExceptionLogger.logInfo("❌ Failed to update inventory total");
                 return false;
            }
            ExceptionLogger.logInfo("✅ Inventory total updated");
        }
        
        return true;
    }

    /**
     * Helper wrapper for standalone usage (Creates its own transaction)
     */
    public static boolean reduceQuantityFromBatches(String productParcode, int quantityToReduce, int inventoryId) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            if (reduceQuantityFromBatches(conn, productParcode, quantityToReduce, inventoryId)) {
                conn.commit();
                return true;
            } else {
                conn.rollback();
                return false;
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error in independent batch reduction");
            return false;
        }
    }
    
    /**
     * Add quantity to a specific batch
     */
    public static boolean addQuantityToBatch(String batchNumber, String productParcode, int quantityToAdd, int inventoryId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update batch quantity
            String updateBatchQuery = "UPDATE batch SET Quantaty = Quantaty + ? WHERE Batch_number = ? AND Product_parcode = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBatchQuery)) {
                pstmt.setInt(1, quantityToAdd);
                pstmt.setString(2, batchNumber);
                pstmt.setString(3, productParcode);
                int rowsAffected = pstmt.executeUpdate();
                
                if (rowsAffected == 0) {
                    ExceptionLogger.logInfo("Batch not found");
                    conn.rollback();
                    return false;
                }
            }
            
            // Update total quantity in inventory
            String updateInventoryQuery = "UPDATE inventory_has_product SET Quntaty = Quntaty + ? " +
                                         "WHERE Inventory_ID = ? AND Product_parcode = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateInventoryQuery)) {
                pstmt.setInt(1, quantityToAdd);
                pstmt.setInt(2, inventoryId);
                pstmt.setString(3, productParcode);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            ExceptionLogger.logInfo(String.format("Added %d units to batch %s", quantityToAdd, batchNumber));
            return true;
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error adding quantity to batch");
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ExceptionLogger.logException(ex, "Error rolling back transaction");
                }
            }
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    ExceptionLogger.logException(e, "Error closing connection");
                }
            }
        }
    }
    
    /**
     * Get total available quantity for a product across all batches
     */
    public static int getTotalAvailableQuantity(String productParcode) {
        int total = 0;
        
        String query = "SELECT SUM(Quantaty) as total FROM batch WHERE Product_parcode = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, productParcode);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getInt("total");
                }
            }
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error getting total quantity for product: " + productParcode);
        }
        
        return total;
    }
}
