// model/finance/Treasury.java
package model.finance;

import DB.DBConnection;
import model.branch.Branch;
import model.invoices.PurchaseInvoice;
import model.invoices.SaleInvoice;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Treasury {

    private int id;
    private double currentBalance;
    private LocalDate lastUpdatedDate;
    private Branch branch;

    // Constructors
    public Treasury(int id, double currentBalance, LocalDate lastUpdatedDate, Branch branch) {
        this.id = id;
        this.currentBalance = currentBalance;
        this.lastUpdatedDate = lastUpdatedDate;
        this.branch = branch;
    }

    public Treasury() {
        this.id = 0;
        this.currentBalance = 0;
        this.lastUpdatedDate = null;
        this.branch = null;
    }

    public Treasury(Treasury t) {
        this.id = t.id;
        this.currentBalance = t.currentBalance;
        this.lastUpdatedDate = t.lastUpdatedDate;
        this.branch = t.branch;
    }

    // ====================== REAL DATABASE FUNCTIONS ======================
    
    public double getTotalDailyPurchaseReturns() {
        if (branch == null) {
            return 0.0;
        }

        String sql = """
            SELECT COALESCE(SUM(pi.remaing_money), 0) AS total_returns
            FROM purchase_invoce pi
            JOIN invoice i ON pi.Invoice_ID = i.ID
            WHERE i.Treasury_Bransh_ID = ?
              AND i.date = CURDATE()
              AND pi.remaing_money > 0
            """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(branch.getId()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_returns");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching daily purchase returns: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

   
    public double getTotalDailySaleReturns() {
        if (branch == null) {
            return 0.0;
        }

        String sql = """
            SELECT COALESCE(SUM(i.price), 0) AS total_returns
            FROM sell_invoice si
            JOIN invoice i ON si.Invoice_ID = i.ID
            WHERE i.Treasury_Bransh_ID = ?
              AND i.date = CURDATE()
              AND i.price < 0  -- افتراض: المرتجعات تُسجل كـ negative price
            """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(branch.getId()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Math.abs(rs.getDouble("total_returns")); // نرجّع القيمة الموجبة
            }
        } catch (SQLException e) {
            System.err.println("Error fetching daily sale returns: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    
    public double getTotalDailySales() {
        if (branch == null) {
            return 0.0;
        }

        String sql = """
            SELECT COALESCE(SUM(i.price), 0) AS total_sales
            FROM sell_invoice si
            JOIN invoice i ON si.Invoice_ID = i.ID
            WHERE i.Treasury_Bransh_ID = ?
              AND i.date = CURDATE()
              AND i.price > 0
            """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(branch.getId()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_sales");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching daily sales: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    
    public double getTotalDailyPurchases() {
        if (branch == null) {
            return 0.0;
        }

        String sql = """
            SELECT COALESCE(SUM(pi.money_paid), 0) AS total_purchases
            FROM purchase_invoce pi
            JOIN invoice i ON pi.Invoice_ID = i.ID
            WHERE i.Treasury_Bransh_ID = ?
              AND i.date = CURDATE()
            """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(branch.getId()));
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("total_purchases");
            }
        } catch (SQLException e) {
            System.err.println("Error fetching daily purchases: " + e.getMessage());
            e.printStackTrace();
        }
        return 0.0;
    }

    
    public List<Transaction> getTransactionsHistory() {
    List<Transaction> transactions = new ArrayList<>();
    if (branch == null) return transactions;

    String sql = """
        SELECT
            i.ID AS invoice_id,
            i.date,
            i.price,
            'SALE' AS type
        FROM sell_invoice si
        JOIN invoice i ON si.Invoice_ID = i.ID
        WHERE i.Treasury_Bransh_ID = ?
          AND i.date = CURDATE()

        UNION ALL

        SELECT
            i.ID,
            i.date,
            pi.money_paid * -1,
            'PURCHASE' AS type
        FROM purchase_invoce pi
        JOIN invoice i ON pi.Invoice_ID = i.ID
        WHERE i.Treasury_Bransh_ID = ?
          AND i.date = CURDATE()

        ORDER BY date DESC
        """;

    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {

        int branchId = Integer.parseInt(branch.getId());
        pstmt.setInt(1, branchId);
        pstmt.setInt(2, branchId);

        ResultSet rs = pstmt.executeQuery();
        while (rs.next()) {
            Transaction t = new Transaction();
            LocalDateTime dateTime = rs.getDate("date").toLocalDate().atStartOfDay();
            t.setDateAndTime(dateTime);
            t.setType(rs.getString("type"));
            double amount = Math.abs(rs.getDouble("price"));
            t.setAmountOfMoney(amount);

            int invoiceId = rs.getInt("invoice_id");
            Branch branchObj = this.branch; // أو this.branch

            if ("SALE".equals(t.getType())) {
                t.setInvoice(new SaleInvoice(invoiceId, dateTime, amount, branchObj));
            } else {
                t.setInvoice(new PurchaseInvoice(invoiceId, dateTime, amount, branchObj));
            }

            transactions.add(t);
        }
    } catch (SQLException e) {
        System.err.println("Error fetching transaction history: " + e.getMessage());
        e.printStackTrace();
    }
    return transactions;
}

    // ====================== GETTERS & SETTERS ======================
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public LocalDate getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(LocalDate lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }

    public void addIncome() {
        System.out.println("Adding income to treasury.");
    }
}
