package model.invoices;
import model.Product.Product ;
import java.util.Date ;
 
 public class Batch {
    private int batchNumber ;
    private double Quantity ;
    private Date expireDate ;
    private double Cost ;
    private Supplier Supplier ;
    public Product products ;

    public Batch(int batchNumber, double Quantity, Date expireDate, double Cost, Supplier Supplier, Product products) {
        this.batchNumber = batchNumber;
        this.Quantity = Quantity;
        this.expireDate = expireDate;
        this.Cost = Cost;
        this.Supplier = Supplier;
        this.products = products;
    }
    public Batch() {
        this.batchNumber = 0;
        this.Quantity = 0;
        this.expireDate = null;
        this.Cost = 0;
        this.Supplier = null;
        this.products = null;
    }

    public int getBatchNumber() {
        return batchNumber;
    }

    public double getQuantity() {
        return Quantity;
    }

    public Date getExpireDate() {
        return expireDate;
    }

    public double getCost() {
        return Cost;
    }

    public Supplier getSupplier() {
        return Supplier;
    }

    public void setBatchNumber(int batchNumber) {
        this.batchNumber = batchNumber;
    }

    public void setQuantity(double Quantity) {
        this.Quantity = Quantity;
    }

    public void setExpireDate(Date expireDate) {
        this.expireDate = expireDate;
    }

    public void setCost(double Cost) {
        this.Cost = Cost;
    }

    public void setSupplier(Supplier Supplier) {
            this.Supplier = Supplier;
    }
}
