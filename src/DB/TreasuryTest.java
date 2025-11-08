// src/test/TreasuryTest.java
package DB;

import DB.DBConnection;
import model.branch.Branch;
import model.finance.Treasury;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class TreasuryTest {

    public static void main(String[] args) {
        // 1. نضمن إن الداتا بيز شغالة
        testConnection();

        // 2. نضيف بيانات وهمية للاختبار
        insertTestData();

        // 3. نجرب الخزنة
        testTreasury();
    }

    private static void testConnection() {
        try (Connection conn = DBConnection.getConnection()) {
            System.out.println("✅ الاتصال بالداتا بيز ناجح!");
        } catch (SQLException e) {
            System.err.println("❌ فشل الاتصال!");
            e.printStackTrace();
        }
    }

    private static void insertTestData() {
        String sqlInvoice = "INSERT INTO invoice (ID, date, price, Treasury_Bransh_ID) VALUES (?, CURDATE(), ?, ?)";
        String sqlSale = "INSERT INTO sell_invoice (Invoice_ID, Customer_Person_ID, Customer_Person_Phone, Discount) VALUES (?, 'C001', '01012345678', 0)";
        String sqlPurchase = "INSERT INTO purchase_invoce (Invoice_ID, money_paid, remaing_money, Supplier_nane, Supplier_phone) VALUES (?, 500, 0, 'SupplierX', '01111111111')";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            // فاتورة بيع
            try (PreparedStatement ps1 = conn.prepareStatement(sqlInvoice);
                 PreparedStatement ps2 = conn.prepareStatement(sqlSale)) {
                ps1.setInt(1, 1001);
                ps1.setDouble(2, 250.0);
                ps1.setInt(3, 1); // فرع 1
                ps1.executeUpdate();

                ps2.setInt(1, 1001);
                ps2.executeUpdate();
            }

            // فاتورة شراء
            try (PreparedStatement ps1 = conn.prepareStatement(sqlInvoice);
                 PreparedStatement ps3 = conn.prepareStatement(sqlPurchase)) {
                ps1.setInt(1, 2001);
                ps1.setDouble(2, -500.0); // سالب = شراء
                ps1.setInt(3, 1);
                ps1.executeUpdate();

                ps3.setInt(1, 2001);
                ps3.executeUpdate();
            }

            conn.commit();
            System.out.println("✅ تم إدخال بيانات الاختبار بنجاح!");
        } catch (SQLException e) {
            System.err.println("❌ خطأ في إدخال البيانات: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void testTreasury() {
        // جلب الفرع
        Branch branch = Branch.getBranchById("1");
        if (branch == null) {
            System.out.println("❌ الفرع غير موجود! تأكد من وجوده في الـ DB أو في الـ static map");
            return;
        }

        Treasury treasury = new Treasury();
        treasury.setBranch(branch);

        System.out.println("\n" + "=".repeat(50));
        System.out.println("        نتائج الخزنة لفرع: " + branch.getName());
        System.out.println("=".repeat(50));

        System.out.println("💰 إجمالي المبيعات اليوم: " + treasury.getTotalDailySales() + " ج.م");
        System.out.println("🛒 إجمالي المشتريات اليوم: " + treasury.getTotalDailyPurchases() + " ج.م");
        System.out.println("↩️ مرتجعات البيع: " + treasury.getTotalDailySaleReturns() + " ج.م");
        System.out.println("↩️ مرتجعات الشراء: " + treasury.getTotalDailyPurchaseReturns() + " ج.م");

        System.out.println("\n📜 سجل المعاملات اليوم:");
        treasury.getTransactionsHistory().forEach(t ->
            System.out.println("   • " + t.getType() + " | المبلغ: " + t.getAmountOfMoney() + " | الفاتورة: #" + t.getInvoice().getInvoiceID())
        );
    }
}