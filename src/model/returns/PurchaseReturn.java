package model.returns;

import java.time.LocalDate;
import model.people.Employee;
import model.invoices.Supplier;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Product.Product;
import model.finance.Transaction;
import model.finance.Treasury;
import model.invoices.Batch;
import model.invoices.Supplier;

public class PurchaseReturn {

    private int id;
    private LocalDateTime returnDate;
    private Employee returnInitiatedBy;
    private List<ReturnItem> returnItems = new ArrayList<>();
    private Supplier supplier;
    private double totalCreditOfMoney;

    public PurchaseReturn(int id, LocalDateTime returnDate, Employee returnInitiatedBy, Supplier supplier, double totalCreditOfMoney) {
        this.id = id;
        this.returnDate = returnDate;
        this.returnInitiatedBy = returnInitiatedBy;
        this.supplier = supplier;
        this.totalCreditOfMoney = totalCreditOfMoney;
    }
    
    public PurchaseReturn(PurchaseReturn p) {
        this.id = p.id;
        this.returnDate = p.returnDate;
        this.returnInitiatedBy = p.returnInitiatedBy;
        this.supplier = p.supplier;
        this.totalCreditOfMoney = p.totalCreditOfMoney;
    }
    
    public PurchaseReturn() {
        this.id = 0;
        this.returnDate = null;
        this.returnInitiatedBy = null;
        this.supplier = null;
        this.totalCreditOfMoney = 0;
    }

    public void processCredit() {
        double total = 0.0;

        for (ReturnItem item : returnItems) {
            double refund = item.calcReturnMoney();
            total += refund;
        }

        totalCreditOfMoney = total;
        for (ReturnItem item : returnItems) {
            Product product = item.getProduct();
            Batch batch = item.getBatch();
            double returnedQty = item.getQuantity();

            double newQuantity = batch.getQuantity() - returnedQty;

            if (newQuantity < 0) {
                newQuantity = 0;
            }

            batch.setQuantity(newQuantity);

            returnInitiatedBy.getBranch().getInventory().modifyQuantaty(batch);

            System.out.println("📦 تم خصم " + returnedQty + " من المنتج " + product.getName() + " من المخزون.");
        }

        // 3️⃣ نحدّث الخزنة (الفلوس بتزيد لأن المورد رجعها)
        Treasury treasury = returnInitiatedBy.getBranch().getTreasury();
        double currentBalance = treasury.getCurrentBalance();
        double newBalance = currentBalance + totalCreditOfMoney;

        treasury.setCurrentBalance(newBalance);
        treasury.setLastUpdatedDate(LocalDate.now());

        System.out.println("🏦 تم إضافة " + totalCreditOfMoney + " إلى الخزنة. الرصيد الجديد = " + newBalance);

        // 4️⃣ نسجّل العملية في سجل المعاملات (Transaction)
        Transaction transaction = new Transaction();
        transaction.setDateAndTime(LocalDateTime.now());
        transaction.setType("PURCHASE_RETURN");
        transaction.setEmployee(returnInitiatedBy);
        transaction.setInvoice(null); // مفيش فاتورة بيع هنا، دي عملية مستقلة
        transaction.setAmountOfMoney(totalCreditOfMoney);

        // نضيف العملية دي لسجل المعاملات بتاع الفرع
        returnInitiatedBy.getBranch().getTransactions().add(transaction);
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDateTime returnDate) {
        this.returnDate = returnDate;
    }

    public Employee getReturnInitiatedBy() {
        return returnInitiatedBy;
    }

    public void setReturnInitiatedBy(Employee returnInitiatedBy) {
        this.returnInitiatedBy = returnInitiatedBy;
    }

    public List<ReturnItem> getReturnItems() {
        return returnItems;
    }

    public void setReturnItems(List<ReturnItem> returnItems) {
        this.returnItems = returnItems;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }

    public double getTotalCreditOfMoney() {
        return totalCreditOfMoney;
    }

    public void setTotalCreditOfMoney(double totalCreditOfMoney) {
        this.totalCreditOfMoney = totalCreditOfMoney;
    }
}
