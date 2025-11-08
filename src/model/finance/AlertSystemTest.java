package model.finance;

import java.util.List;

public class AlertSystemTest {
    public static void main(String[] args) {
        System.out.println("Running AlertSystem Checks...\n");

        List<String> expiry = AlertSystem.checkExpiryDates();
        System.out.println("EXPIRY ALERTS (" + expiry.size() + "):");
        expiry.forEach(System.out::println);

        List<String> low = AlertSystem.checkLowStock();
        System.out.println("\nLOW STOCK (" + low.size() + "):");
        low.forEach(System.out::println);

        List<String> unpaid = AlertSystem.checkUnpaidInvoices();
        System.out.println("\nUNPAID INVOICES (" + unpaid.size() + "):");
        unpaid.forEach(System.out::println);

        System.out.println("\nALL ALERTS:");
        AlertSystem.checkAll().forEach(System.out::println);
    }
}