package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.time.LocalDate;
import DB.DBConnection;

public class  TestTreasury{
    public static void main(String[] args) {
        System.out.println("Connection succ");
        try (Connection conn = DBConnection.getConnection()) {

            String addInvoice = "INSERT INTO invoice (ID, date, price, Treasury_Bransh_ID) VALUES (?, ?, ?, ?)";
            PreparedStatement inv = conn.prepareStatement(addInvoice);
            inv.setInt(1, 133);
            inv.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
            inv.setFloat(3, 200);
            inv.setInt(4, 1);
            inv.executeUpdate();

            String addSell = "INSERT INTO sell_invoice (Discount, Invoice_ID, Customer_Person_ID, Customer_Person_Phone) VALUES (?, ?, ?, ?)";
            PreparedStatement sell = conn.prepareStatement(addSell);
            sell.setFloat(1, 0);
            sell.setInt(2, 133);
            sell.setString(3, "C202");        
            sell.setString(4, "01110000000"); 
            sell.executeUpdate();

            System.out.println(" TreasuryTest executed successfully!");

        } catch (Exception e) {
            System.err.println(" TreasuryTest failed: " + e.getMessage());
        }
    }
}
