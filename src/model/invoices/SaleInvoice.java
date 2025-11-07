package model.invoices;

import java.time.LocalDateTime;
import model.people.Customer;
import java.util.List;
import model.branch.Branch;

public class SaleInvoice extends Invoice{
    private Customer customer;
    private double discount;
    private List<InvoiceItem> invoiceItems;
    private double supTotal;
    private Branch branch;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public List<InvoiceItem> getInvoiceItems() {
        return invoiceItems;
    }

    public void setInvoiceItems(List<InvoiceItem> invoiceItems) {
        this.invoiceItems = invoiceItems;
    }

    public double getSupTotal() {
        return supTotal;
    }

    public void setSupTotal(double supTotal) {
        this.supTotal = supTotal;
    }

    public String getBranchID() {
        return BranchID;
    }

    public void setBranchID(String BranchID) {
        this.BranchID = BranchID;
    }

    public int getInvoiceID() {
        return InvoiceID;
    }

    public void setInvoiceID(int InvoiceID) {
        this.InvoiceID = InvoiceID;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public SaleInvoice(Customer customer, double discount, List<InvoiceItem> invoiceItems, double supTotal, String BranchID, int InvoiceID, LocalDateTime date, double totalPrice, double totalAmount,Branch branch) {
        super(BranchID, InvoiceID, date, totalPrice, totalAmount);
        this.customer = customer;
        this.discount = discount;
        this.invoiceItems = invoiceItems;
        this.supTotal = supTotal;
        this.branch = branch;
    }

    public SaleInvoice(SaleInvoice s) {
        this.BranchID = s.BranchID;
        this.InvoiceID = s.InvoiceID;
        this.date = s.date;
        this.setTotalPrice(s.getTotalPrice());
        this.totalAmount = s.totalAmount;
        this.customer = s.customer;
        this.discount = s.discount;
        this.invoiceItems = s.invoiceItems;
        this.supTotal = s.supTotal;
        this.branch = s.branch;
    }

    public SaleInvoice() {
        this.BranchID = "";
        this.InvoiceID = 0;
        this.date = null;
        this.setTotalPrice(0);
        this.totalAmount = 0;
        this.customer = null;
        this.discount = 0;
        this.invoiceItems = null;
        this.supTotal = 0;
        this.branch = null;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
    
}
