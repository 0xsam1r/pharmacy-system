package model.invoices;

import model.Product.Inventory;

import java.util.List;

public class PurchaseInvoice {
    private Inventory inventory;
    private double  remainingMoney;
    private double moneyPaid;
    private List<Batch> batches;
    private Supplier supplier ;

    public PurchaseInvoice(Inventory inventory, double remainingMoney, double moneyPaid, List<Batch> batches, Supplier supplier) {
        this.inventory = inventory;
        this.remainingMoney = remainingMoney;
        this.moneyPaid = moneyPaid;
        this.batches = batches;
        this.supplier = supplier;
    }
    public PurchaseInvoice(PurchaseInvoice p) {
        this.inventory = p.inventory;
        this.remainingMoney = p.remainingMoney;
        this.moneyPaid = p.moneyPaid;
        this.batches = p.batches;
        this.supplier = p.supplier;
    }
    public PurchaseInvoice() {
        this.inventory = null;
        this.remainingMoney = 0;
        this.moneyPaid = 0;
        this.batches = null;
        this.supplier = null;
        
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public double getRemainingMoney() {
        return remainingMoney;
    }

    public void setRemainingMoney(double remainingMoney) {
        this.remainingMoney = remainingMoney;
    }

    public double getMoneyPaid() {
        return moneyPaid;
    }

    public void setMoneyPaid(double moneyPaid) {
        this.moneyPaid = moneyPaid;
    }

    public List<Batch> getBatches() {
        return batches;
    }

    public void setBatches(List<Batch> batches) {
        this.batches = batches;
    }

    public Supplier getSupplier() {
        return supplier;
    }

    public void setSupplier(Supplier supplier) {
        this.supplier = supplier;
    }
    
    
}
