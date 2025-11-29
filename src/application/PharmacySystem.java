package application;

import model.branch.Branch;
import model.finance.Transaction;
import model.Product.Cosmetic;
import model.Product.DosageForm;
import model.Product.Inventory;
import model.Product.Medicine;
import model.Product.Product;
import model.finance.AlertSystem;
import model.finance.ReportGenerator;
import model.people.Customer;
import model.people.Employee;
import enums.Category;
import static enums.Role.CASHIER;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import model.invoices.SaleInvoice;
import model.people.UserAccount;
import model.returns.ReturnItem;
import model.returns.SaleReturn;

public class PharmacySystem {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("        STARTING COMPREHENSIVE SYSTEM TEST");
        System.out.println("==========================================");

        // 1. Test Product and Inventory Classes
        testProductAndInventory();

        // 2. Test Sales and Return Cycle
        testSaleCycle();

        // 3. Test Alerts, Reports, and Treasury
        testFinancialUtilities();

        System.out.println("\n" + "=".repeat(50));
        System.out.println("         ALL SYSTEM TESTS COMPLETED");
        System.out.println("==========================================");
    }
    
    private static void testProductAndInventory() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("      1. Testing Product and Inventory Classes");
        System.out.println("=".repeat(50));

        // 1. DosageForm
        DosageForm form1 = new DosageForm(500.0, "Paracetamol");
        System.out.println("DosageForm Created: " + form1);

        // 2. Medicine
        ArrayList<DosageForm> forms = new ArrayList<>();
        forms.add(form1);
        Medicine medicine = new Medicine("622300000001", "Panadol Extra", 10, 25.0, Category.MEDICINE, forms);
        medicine.setQuantityInStock(50.0);
        System.out.println(" Medicine Created: " + medicine.getName() + " | Stock: " + medicine.getQuantityInStock());

        // 3. Cosmetic
        Cosmetic cosmetic = new Cosmetic("622300000004", "Nivea Cream", 1, 60.0, Category.COSMETIC, "Nivea", 'F');
        cosmetic.setQuantityInStock(30.0);
        System.out.println(" Cosmetic Created: " + cosmetic.getName() + " | Stock: " + cosmetic.getQuantityInStock());

        // 4. Inventory
        List<Product> productsList = new ArrayList<>(Arrays.asList(medicine, cosmetic));
        Inventory inventory = new Inventory("INV001", 10.0, (ArrayList<Product>) productsList, new ArrayList<>());
        System.out.println(" Inventory initialized with Panadol Extra and Nivea Cream.");

        // 5. Search
        Product found = inventory.search("Panadol Extra");
        System.out.println(" Search result for Panadol: " + (found != null ? found.getName() : "Not Found"));
    }

    private static void testSaleCycle() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("      2. Testing Sale and Return Cycle Classes");
        System.out.println("=".repeat(50));

        // --- Setup Core Entities ---
        Branch testBranch = new Branch("B001", "Main Branch", "Cairo");
        UserAccount userAccount = new UserAccount("ahmedk", "ahm123", CASHIER);

        String startDate = LocalDate.now().minusMonths(6).toString();
        int salary = 8500;

        // Employee from DB: P001
        Employee employee = new Employee(
            "P001",
            "Ahmed Khaled",
            "01001234567",
            userAccount,
            salary,
            startDate,
            testBranch
        );

        // Customer from DB: P004
        Customer customer = new Customer("P004", "Sarah Abdullah", "01099887766", 80);
        Product testProduct = new Cosmetic("622300000005", "Fair & Lovely", 1, 70.0, Category.COSMETIC, "Fair & Lovely", 'F');
        testProduct.setQuantityInStock(50.0);

        System.out.println(" Core entities (Employee, Customer, Product) set up.");

        // --- Test SaleInvoice ---
        SaleInvoice invoice = createSampleSaleInvoice(employee, customer, testBranch, testProduct);
        System.out.println("\n--- Sale Invoice Test ---");
        System.out.println(" Invoice ID: " + invoice.getInvoiceID() + " | Total: " + invoice.getTotalPrice());

        // --- Test SaleReturn and ReturnItem ---
        testSaleReturn(invoice, employee, customer, testProduct);

        // --- Test Standalone Transaction ---
        System.out.println("\n--- Standalone Transaction Test ---");
        testStandaloneTransaction(employee, testBranch, invoice);
    }

    private static SaleInvoice createSampleSaleInvoice(Employee employee, Customer customer, Branch branch, Product product) {
        SaleInvoice invoice = new SaleInvoice();
        invoice.setInvoiceID(10001);
        invoice.setDate(LocalDateTime.now());
        invoice.setCustomer(customer);
        invoice.setBranch(branch);
        invoice.setTotalPrice(product.getPrice());
        return invoice;
    }

    private static void testSaleReturn(SaleInvoice saleInvoice, Employee employee, Customer customer, Product product) {
        System.out.println("\n--- Sale Return Test ---");

        try {
            ReturnItem returnItem = new ReturnItem();
            returnItem.setProduct(product);
            returnItem.setQuantity(1);
            returnItem.setUnitPrice(product.getPrice());
            System.out.println(" ReturnItem created for " + product.getName() + " | Qty: " + returnItem.getQuantity());

            SaleReturn saleReturn = new SaleReturn(
                5001,
                LocalDateTime.now().plusHours(1),
                employee,
                customer,
                0,
                Arrays.asList(returnItem)
            );
            saleReturn.setTotalRefundOfMoney(returnItem.getUnitPrice() * returnItem.getQuantity());

            saleReturn.processRefund(saleInvoice);

            System.out.println(" SaleReturn ID " + saleReturn.getId() + " processed successfully.");
            System.out.println(" Total Refund Amount: " + saleReturn.getTotalRefundOfMoney());
        } catch (Exception e) {
            System.err.println("️ Warning: SaleReturn.processRefund failed to run. May require Treasury/DB setup.");
            System.err.println("   Exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    private static void testStandaloneTransaction(Employee employee, Branch branch, SaleInvoice invoice) {
        Transaction transaction = new Transaction();
        transaction.setId(9001);
        transaction.setDateAndTime(LocalDateTime.now());
        transaction.setType("PURCHASE");
        transaction.setEmployee(employee);
        transaction.setInvoice(invoice);
        transaction.setAmountOfMoney(500.0);

        System.out.println(" Standalone Transaction ID " + transaction.getId() + " created.");
        System.out.println("    Type: " + transaction.getType() + " | Amount: " + transaction.getAmountOfMoney());
    }

    private static void testFinancialUtilities() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("3. Testing Financial Utilities (Alerts, Reports, Treasury)");
        System.out.println("=".repeat(50));

        System.out.println("\n--- Treasury Test (Calling DB.TestTreasury.main) ---");
        try {
            DB.TestTreasury.main(null);
        } catch (Exception e) {
            System.err.println("️ ERROR: TreasuryTest failed to run. Ensure DB.TestTreasury class exists and DBConnection is configured.");
            System.err.println("   Exception: " + e.getMessage());
        }

        // --- Test AlertSystem ---
        System.out.println("\n--- Alert System Test ---");
        try {
            List<String> alerts = AlertSystem.checkAll();
            if (alerts.isEmpty()) {
                System.out.println(" AlertSystem checked: No critical alerts found.");
            } else {
                System.out.println("Alerts found:");
                alerts.forEach(System.out::println);
            }
        } catch (Exception e) {
            System.err.println("️ ERROR: AlertSystem failed. Ensure database setup is complete.");
        }

        // --- Test ReportGenerator ---
        System.out.println("\n--- Report Generator Test ---");
        String testDate = LocalDate.now().toString();

        ReportGenerator.generateSalesReport(testDate);
        System.out.println(" Sales Report generated. Check 'reports' folder.");

        ReportGenerator.generateProfitGraph(testDate);
        System.out.println(" Profit Graph generated. Check 'reports' folder.");
        
    }
}

