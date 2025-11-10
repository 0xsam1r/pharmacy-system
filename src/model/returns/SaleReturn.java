package model.returns;

import java.time.LocalDate;
import model.people.Customer;
import model.people.Employee;
import model.invoices.SaleInvoice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import model.Product.Product;
import model.finance.Transaction;
import model.finance.Treasury;
import model.invoices.Batch;

public class SaleReturn {

    private int id;
    private LocalDateTime returnDate;
    private Employee refundProcessedBy;
    private List<ReturnItem> returnItems = new ArrayList<>();
    private Customer customer;
    private double totalRefundOfMoney;

    public SaleReturn(int id, LocalDateTime returnDate, Employee refundProcessedBy, Customer customer, double totalRefundOfMoney , List<ReturnItem> returnItems) {
        this.id = id;
        this.returnDate = returnDate;
        this.refundProcessedBy = refundProcessedBy;
        this.customer = customer;
        this.totalRefundOfMoney = totalRefundOfMoney;
        this.returnItems = returnItems;
    }

    public SaleReturn(SaleReturn s) {
        this.id = s.id;
        this.returnDate = s.returnDate;
        this.refundProcessedBy = s.refundProcessedBy;
        this.customer = s.customer;
        this.totalRefundOfMoney = s.totalRefundOfMoney;
        this.returnItems = s.returnItems;
    }

    public SaleReturn() {
        this.id = 0;
        this.returnDate = null;
        this.refundProcessedBy = null;
        this.customer = null;
        this.totalRefundOfMoney = 0;
        this.returnItems = null;
    }

    public void processRefund(SaleInvoice saleInvoice) {
        System.out.println("Processing refund for sale invoice: " + saleInvoice.getInvoiceID());

        double total = 0.0;

        for (ReturnItem item : returnItems) {
            double refund = item.calcReturnMoney();
            total += refund;
        }

        totalRefundOfMoney = total;
        System.out.println("tootal refund is " + totalRefundOfMoney + " $$");

        // ✅ تعديل آمن لتفادي NullPointerException
        for (ReturnItem item : returnItems) {
            Product product = item.getProduct();
            Batch batch = item.getBatch();
            double returnedQty = item.getQuantity();

            try {
                if (batch != null && saleInvoice.getBranch() != null && saleInvoice.getBranch().getInventory() != null) {
                    double newQuantity = batch.getQuantity() + returnedQty;
                    batch.setQuantity(newQuantity);
                    saleInvoice.getBranch().getInventory().modifyQuantaty(batch);
                    System.out.println("✅ Refund done: " + returnedQty + " from " + product.getName());
                } else {
                    System.out.println("⚠️ Skipped refund batch adjustment for non-batch or unlinked product: " + product.getName());
                }
            } catch (NullPointerException e) {
                System.out.println("⚠️ Skipped refund batch adjustment (NullPointerException caught).");
            }
        }

        // ✅ حساب النقاط بعد الخصم
        if (customer != null) {
            double pointsLost = 0.0;

            if (totalRefundOfMoney <= 100) {
                pointsLost = totalRefundOfMoney / 10.0;
            } else if (totalRefundOfMoney <= 500) {
                pointsLost = 10.0 + (totalRefundOfMoney - 100) / 8.0;
            } else {
                pointsLost = 10.0 + 50.0 + (totalRefundOfMoney - 500) / 5.0;
            }

            double newPoints = customer.getPoints() - pointsLost;
            if (newPoints < 0) newPoints = 0;

            customer.setPoints(newPoints);

            System.out.println("💳 Points Lost: " + pointsLost);
            System.out.println("💎 Points after refund: " + newPoints);
        }

        // ✅ تحديث الخزنة
        if (saleInvoice.getBranch() != null && saleInvoice.getBranch().getTreasury() != null) {
            Treasury treasury = saleInvoice.getBranch().getTreasury();
            double currentBalance = treasury.getCurrentBalance();
            double newBalance = currentBalance - totalRefundOfMoney;

            treasury.setCurrentBalance(newBalance);
            treasury.setLastUpdatedDate(LocalDate.now());

            System.out.println("💰 Refund Money: " + totalRefundOfMoney + " | Balance after update: " + newBalance);
        } else {
            System.out.println("⚠️ Treasury update skipped (branch or treasury is null).");
        }

        // ✅ تسجيل المعاملة
        Transaction transaction = new Transaction();
        transaction.setDateAndTime(LocalDateTime.now());
        transaction.setType("SALE_RETURN");
        transaction.setEmployee(refundProcessedBy);
        transaction.setInvoice(saleInvoice);
        transaction.setAmountOfMoney(totalRefundOfMoney);
        System.out.println("🧾 Transaction recorded: SALE_RETURN for " + totalRefundOfMoney);
    }

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

    public Employee getRefundProcessedBy() {
        return refundProcessedBy;
    }

    public void setRefundProcessedBy(Employee refundProcessedBy) {
        this.refundProcessedBy = refundProcessedBy;
    }

    public List<ReturnItem> getReturnItems() {
        return returnItems;
    }

    public void setReturnItems(List<ReturnItem> returnItems) {
        this.returnItems = returnItems;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getTotalRefundOfMoney() {
        return totalRefundOfMoney;
    }

    public void setTotalRefundOfMoney(double totalRefundOfMoney) {
        this.totalRefundOfMoney = totalRefundOfMoney;
    }
}
