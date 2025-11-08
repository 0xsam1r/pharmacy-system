package model.finance;

import DB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * AlertSystem: Fully independent class.
 * Returns List<String> only - NO FILE OUTPUT.
 * Works with actual schema: Quantaty, reordr_level, inventory_has_product.
 */
public class AlertSystem {

    // 1. Check products expiring within 30 days
    public static List<String> checkExpiryDates() {
        List<String> alerts = new ArrayList<>();
        String sql = """
            SELECT p.Name, b.Batch_number, b.expire_date, b.Quantaty
            FROM batch b
            JOIN product p ON b.Product_parcode = p.parcode
            WHERE b.expire_date <= DATE_ADD(CURDATE(), INTERVAL 30 DAY)
              AND b.expire_date >= CURDATE()
              AND b.Quantaty > 0
            ORDER BY b.expire_date
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                alerts.add(String.format("EXPIRY: %s | Batch: %s | Expires: %s | Qty: %d",
                        rs.getString("Name"),
                        rs.getString("Batch_number"),
                        rs.getDate("expire_date"),
                        rs.getInt("Quantaty")));
            }
        } catch (SQLException e) {
            alerts.add("ERROR (Expiry): " + e.getMessage());
        }
        return alerts;
    }

    // 2. Check low stock: Aggregate from batch + compare with inventory_has_product.reordr_level
    public static List<String> checkLowStock() {
        List<String> alerts = new ArrayList<>();
        String sql = """
            SELECT p.Name, p.parcode, i.reordr_level,
                   COALESCE(SUM(b.Quantaty), 0) AS total_qty
            FROM product p
            JOIN inventory_has_product i ON p.parcode = i.Product_parcode
            LEFT JOIN batch b ON p.parcode = b.Product_parcode
            GROUP BY p.parcode, p.Name, i.reordr_level
            HAVING total_qty < i.reordr_level AND i.reordr_level > 0
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                alerts.add(String.format("LOW STOCK: %s | Code: %s | Stock: %d | Reorder: %d",
                        rs.getString("Name"),
                        rs.getString("parcode"),
                        rs.getInt("total_qty"),
                        rs.getInt("reordr_level")));
            }
        } catch (SQLException e) {
            alerts.add("ERROR (Low Stock): " + e.getMessage());
        }
        return alerts;
    }

    // 3. Check unpaid purchase invoices
    public static List<String> checkUnpaidInvoices() {
        List<String> alerts = new ArrayList<>();
        String sql = """
            SELECT i.ID, i.date, pi.remaing_money, s.nane
            FROM invoice i
            JOIN purchase_invoce pi ON i.ID = pi.Invoice_ID
            JOIN supplier s ON pi.Supplier_nane = s.nane AND pi.Supplier_phone = s.phone
            WHERE pi.remaing_money > 0
            ORDER BY i.date
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                alerts.add(String.format("UNPAID: Invoice #%d | Date: %s | Remaining: %.2f | Supplier: %s",
                        rs.getInt("ID"),
                        rs.getDate("date"),
                        rs.getDouble("remaing_money"),
                        rs.getString("nane")));
            }
        } catch (SQLException e) {
            alerts.add("ERROR (Unpaid): " + e.getMessage());
        }
        return alerts;
    }

    // 4. Check all alerts
    public static List<String> checkAll() {
        List<String> all = new ArrayList<>();
        all.add("=== EXPIRY ALERTS ===");
        all.addAll(checkExpiryDates());
        all.add("\n=== LOW STOCK ALERTS ===");
        all.addAll(checkLowStock());
        all.add("\n=== UNPAID INVOICES ===");
        all.addAll(checkUnpaidInvoices());
        return all;
    }
}
/*

*/