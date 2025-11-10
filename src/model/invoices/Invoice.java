package model.invoices;
import java.time.LocalDateTime;


public class Invoice {
    protected String BranchID; 
    protected int InvoiceID;
    protected LocalDateTime date;
    private double totalPrice;
    protected double totalAmount;

    
    public Invoice(String BranchID, int InvoiceID, LocalDateTime date, double totalPrice, double totalAmount) {
        this.BranchID = BranchID;
        this.InvoiceID = InvoiceID;
        this.date = date;
        this.totalPrice = totalPrice;
        this.totalAmount = totalAmount;
    }
    public Invoice(Invoice i) {
        this.BranchID = i.BranchID;
        this.InvoiceID = i.InvoiceID;
        this.date = i.date;
        this.totalPrice = i.totalPrice;
        this.totalAmount = i.totalAmount;
    }
    public Invoice() {
        this.BranchID = "";
        this.InvoiceID = 0;
        this.date = null;
        this.totalPrice = 0;
        this.totalAmount = 0;
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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

}
