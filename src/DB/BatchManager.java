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
        private double quantity;
        private Date expireDate;
        private double cost;
        
        public Batch(String batchNumber, String productParcode, double quantity, Date expireDate, double cost) {
            this.batchNumber = batchNumber;
            this.productParcode = productParcode;
            this.quantity = quantity;
            this.expireDate = expireDate;
            this.cost = cost;
        }
        
        public String getBatchNumber() { return batchNumber; }
        public String getProductParcode() { return productParcode; }
        public double getQuantity() { return quantity; }
        public Date getExpireDate() { return expireDate; }
        public double getCost() { return cost; }
        public void setQuantity(double quantity) { this.quantity = quantity; }
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
        
        ExceptionLogger.logInfo("🔍 Searching batches for parcode: " + productParcode);
        
        // Note: We use try-with-resources for PreparedStatement and ResultSet, 
        // BUT NOT for Connection, as it is passed from outside
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            
            pstmt.setString(1, productParcode.trim());
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Batch batch = new Batch(
                        rs.getString("Batch_number"),
                        productParcode,
                        rs.getDouble("Quantaty"),
                        rs.getDate("expire_date"),
                        rs.getDouble("cost")
                    );
                    batches.add(batch);
                    ExceptionLogger.logInfo(String.format("   ✓ Found batch: %s, Qty: %.2f", 
                        batch.getBatchNumber(), batch.getQuantity()));
                }
            }
            
            ExceptionLogger.logInfo("📦 Total batches found: " + batches.size());
            
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
    public static boolean reduceQuantityFromBatches(Connection conn, String productParcode, double quantityToReduce, int inventoryId) throws SQLException {
        ExceptionLogger.logInfo(String.format("🔵 FEFO START: Reducing %.2f boxes of %s", quantityToReduce, productParcode));

        // Get batches ordered by expiry date (FEFO) using the SAME connection
        List<Batch> batches = getBatchesForProduct(conn, productParcode);
        
        if (batches.isEmpty()) {
            ExceptionLogger.logInfo("❌ No batches found for: " + productParcode);
            return false;
        }
        
        double totalAvailable = batches.stream().mapToDouble(Batch::getQuantity).sum();
        if (totalAvailable < quantityToReduce) {
            ExceptionLogger.logInfo(String.format("❌ Insufficient Stock: Need %.2f, Have %.2f", quantityToReduce, totalAvailable));
            return false;
        }
        
        double remainingToReduce = quantityToReduce;
        
        // Log batches found
        for (Batch b : batches) {
             ExceptionLogger.logInfo(String.format("   📦 Batch %s (Qty: %.2f, Exp: %s)", b.getBatchNumber(), b.getQuantity(), b.getExpireDate()));
        }

        for (Batch batch : batches) {
            if (remainingToReduce <= 0) break;
            
            double currentQty = batch.getQuantity();
            double take = Math.min(remainingToReduce, currentQty);
            double newQty = currentQty - take;
            
            String sql = "UPDATE batch SET Quantaty = ? WHERE Batch_number = ? AND Product_parcode = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setDouble(1, newQty);
                ps.setString(2, batch.getBatchNumber());
                ps.setString(3, productParcode);
                ps.executeUpdate();
            }
            
            remainingToReduce -= take;
            ExceptionLogger.logInfo(String.format("✅ Taken %.2f from batch %s (New Qty: %.2f)", take, batch.getBatchNumber(), newQty));
        }
        
        // Update Inventory Total
        String sqlInv = "UPDATE inventory_has_product SET Quntaty = Quntaty - ? WHERE Inventory_ID = ? AND Product_parcode = ?";
        try (PreparedStatement ps = conn.prepareStatement(sqlInv)) {
            ps.setDouble(1, quantityToReduce);
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
    public static boolean reduceQuantityFromBatches(String productParcode, double quantityToReduce, int inventoryId) {
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
    /**
     * Add quantity to a specific batch (Uses existing connection)
     */
    public static boolean addQuantityToBatch(Connection conn, String batchNumber, String productParcode, double quantityToAdd, int inventoryId) throws SQLException {
        // Update batch quantity
        String updateBatchQuery = "UPDATE batch SET Quantaty = Quantaty + ? WHERE Batch_number = ? AND Product_parcode = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateBatchQuery)) {
            pstmt.setDouble(1, quantityToAdd);
            pstmt.setString(2, batchNumber);
            pstmt.setString(3, productParcode);
            int rowsAffected = pstmt.executeUpdate();
            
            if (rowsAffected == 0) {
                ExceptionLogger.logInfo("Batch not found for adding quantity");
                return false;
            }
        }
        
        // Update total quantity in inventory
        String updateInventoryQuery = "UPDATE inventory_has_product SET Quntaty = Quntaty + ? " +
                                     "WHERE Inventory_ID = ? AND Product_parcode = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateInventoryQuery)) {
            pstmt.setDouble(1, quantityToAdd);
            pstmt.setInt(2, inventoryId);
            pstmt.setString(3, productParcode);
            pstmt.executeUpdate();
        }
        
        ExceptionLogger.logInfo(String.format("Added %.2f boxes to batch %s", quantityToAdd, batchNumber));
        return true;
    }

    /**
     * Add quantity to a specific batch (Standalone - manages connection)
     */
    public static boolean addQuantityToBatch(String batchNumber, String productParcode, double quantityToAdd, int inventoryId) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            
            // Update batch quantity
            String updateBatchQuery = "UPDATE batch SET Quantaty = Quantaty + ? WHERE Batch_number = ? AND Product_parcode = ?";
            try (PreparedStatement pstmt = conn.prepareStatement(updateBatchQuery)) {
                pstmt.setDouble(1, quantityToAdd);
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
                pstmt.setDouble(1, quantityToAdd);
                pstmt.setInt(2, inventoryId);
                pstmt.setString(3, productParcode);
                pstmt.executeUpdate();
            }
            
            conn.commit();
            ExceptionLogger.logInfo(String.format("Added %.2f boxes to batch %s", quantityToAdd, batchNumber));
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
     * Reduce quantity from a specific batch (Uses existing connection)
     * Critical for Purchase Returns where we return specific items from a batch
     */
    public static boolean reduceQuantityFromSpecificBatch(Connection conn, String batchNumber, String productParcode, double quantityToReduce, int inventoryId) throws SQLException {
        // 1. Check if batch has enough quantity
        String  checkQuery = "SELECT Quantaty FROM batch WHERE Batch_number = ? AND Product_parcode = ?";
        double currentQty = 0;
        try (PreparedStatement ps = conn.prepareStatement(checkQuery)) {
            ps.setString(1, batchNumber);
            ps.setString(2, productParcode);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    currentQty = rs.getDouble("Quantaty");
                } else {
                    ExceptionLogger.logInfo("❌ Batch not found: " + batchNumber);
                    return false;
                }
            }
        }
        
        if (currentQty < quantityToReduce) {
            ExceptionLogger.logInfo(String.format("❌ Insufficient Stock in Batch %s: Has %.2f, Trying to remove %.2f", batchNumber, currentQty, quantityToReduce));
            return false;
        }

        // 2. Reduce from Batch
        String updateBatchQuery = "UPDATE batch SET Quantaty = Quantaty - ? WHERE Batch_number = ? AND Product_parcode = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateBatchQuery)) {
            pstmt.setDouble(1, quantityToReduce);
            pstmt.setString(2, batchNumber);
            pstmt.setString(3, productParcode);
            pstmt.executeUpdate();
        }
        
        // 3. Update total quantity in inventory
        String updateInventoryQuery = "UPDATE inventory_has_product SET Quntaty = Quntaty - ? " +
                                     "WHERE Inventory_ID = ? AND Product_parcode = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(updateInventoryQuery)) {
            pstmt.setDouble(1, quantityToReduce);
            pstmt.setInt(2, inventoryId);
            pstmt.setString(3, productParcode);
            pstmt.executeUpdate();
        }
        
        ExceptionLogger.logInfo(String.format("✅ Removed %.2f boxes from batch %s", quantityToReduce, batchNumber));
        return true;
    }
    
    /**
     * Get total available quantity for a product across all batches (Using existing connection)
     */
    public static double getTotalAvailableQuantity(Connection conn, String productParcode) {
        double total = 0;
        String query = "SELECT SUM(Quantaty) as total FROM batch WHERE Product_parcode = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, productParcode.trim());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error getting total quantity for product: " + productParcode);
        }
        return total;
    }

    /**
     * Get total available quantity for a product across all batches (New Connection)
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
