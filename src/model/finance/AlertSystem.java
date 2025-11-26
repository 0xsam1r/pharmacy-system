package model.finance;

import DB.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AlertSystem {

    private static final int CURRENT_BRANCH_ID = 1; 

    public static List<String> checkExpiryDates() {
        List<String> alerts = new ArrayList<>();

        String sql = """
    SELECT p.Name, b.Batch_number, b.expire_date, b.Quantaty
    FROM batch b
    JOIN product p ON b.Product_parcode = p.parcode
    WHERE (b.expire_date < CURDATE() 
           OR b.expire_date BETWEEN CURDATE() AND DATE_ADD(CURDATE(), INTERVAL 30 DAY))
      AND b.Quantaty > 0
    ORDER BY b.expire_date
    """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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

    public static List<String> checkLowStock() {
        List<String> alerts = new ArrayList<>();
        String sql = """
            SELECT p.Name, p.parcode, ihp.reordr_level, ihp.Quntaty AS current_qty
            FROM inventory_has_product ihp
            JOIN inventory i ON ihp.Inventory_ID = i.ID
            JOIN product p ON ihp.Product_parcode = p.parcode
            WHERE i.Bransh_ID = ?
              AND ihp.Quntaty < ihp.reordr_level
              AND ihp.reordr_level > 0
            ORDER BY ihp.Quntaty ASC
            """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, CURRENT_BRANCH_ID);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    alerts.add(String.format("LOW STOCK: %s | Code: %s | Stock: %d | Reorder: %d",
                            rs.getString("Name"),
                            rs.getString("parcode"),
                            rs.getInt("current_qty"),
                            rs.getInt("reordr_level")));
                }
            }
        } catch (SQLException e) {
            alerts.add("ERROR (Low Stock): " + e.getMessage());
        }
        return alerts;
    }

    public static List<String> checkUnpaidInvoices() {
        List<String> alerts = new ArrayList<>();
        String sql = """
            SELECT i.ID, i.date, pi.remaing_money, s.nane
            FROM purchase_invoce pi
            JOIN invoice i ON pi.Invoice_ID = i.ID
            JOIN supplier s ON pi.Supplier_nane = s.nane AND pi.Supplier_phone = s.phone
            WHERE pi.remaing_money > 0
            ORDER BY i.date ASC
            """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {

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
