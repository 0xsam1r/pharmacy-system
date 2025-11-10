package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import DB.DBConnection;

public class  TestTreasury{
    public static void main(String[] args) {
        System.out.println("Connection succ");
        try (Connection conn = DBConnection.getConnection()) {

            // 1️⃣ إنشاء فاتورة عامة
            String addInvoice = "INSERT INTO invoice (ID, date, price, Treasury_Bransh_ID) VALUES (?, ?, ?, ?)";
            PreparedStatement inv = conn.prepareStatement(addInvoice);
            inv.setInt(1, 88);
            inv.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            inv.setFloat(3, 200);
            inv.setInt(4, 1); // تأكد أن عندك Bransh_ID = 1
            inv.executeUpdate();

            // 2️⃣ إضافة فاتورة بيع مرتبطة بنفس العميل اللي عندك
            String addSell = "INSERT INTO sell_invoice (Discount, Invoice_ID, Customer_Person_ID, Customer_Person_Phone) VALUES (?, ?, ?, ?)";
            PreparedStatement sell = conn.prepareStatement(addSell);
            sell.setFloat(1, 0);
            sell.setInt(2, 88);
            sell.setString(3, "C202");        // موجود بالفعل
            sell.setString(4, "01110000000"); // نفس الرقم اللي في person/customer
            sell.executeUpdate();

            System.out.println(" TreasuryTest executed successfully!");

        } catch (Exception e) {
            System.err.println(" TreasuryTest failed: " + e.getMessage());
        }
    }
}
