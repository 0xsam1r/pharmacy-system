package model.invoices;
import model.Product.Product ;
import java.util.Date ;
 // @author mahmoud Elsayed
 
public class Batch {
    private int batchNumber ;
    private double Quantity ;
    private Date expireDate ;
    private double Cost ;
    private double Supplier ;
    public Product products ;

    public Batch(int batchNumber, double Quantity, Date expireDate, double Cost, double Supplier, Product products) {
        this.batchNumber = batchNumber;
        this.Quantity = Quantity;
        this.expireDate = expireDate;
        this.Cost = Cost;
        this.Supplier = Supplier;
        this.products = products;
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

    public double getSupplier() {
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

    public void setSupplier(double Supplier) {
        this.Supplier = Supplier;
    }
    
    

 }
