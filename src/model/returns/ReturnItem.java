package model.returns;

import model.Product.Product;

import model.Product.Product;

import model.invoices.Batch;

public class ReturnItem {
    private Product product;
    private Batch batch;
    private double unitPrice;
    private double quantity;

    public ReturnItem(Product product, Batch batch, double unitPrice, double quantity) {
        this.product = product;
        this.batch = batch;
        this.unitPrice = unitPrice;
        this.quantity = quantity;
    }
    public ReturnItem(ReturnItem r) {
        this.product = r.product;
        this.batch = r.batch;
        this.unitPrice = r.unitPrice;
        this.quantity = r.quantity;
    }
    public ReturnItem() {
        this.product = null;
        this.batch = null;
        this.unitPrice = 0;
        this.quantity = 0;
    }

    public double calcReturnMoney() {
        return unitPrice * quantity;
    }

    // Getters and setters
    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }
    public Batch getBatch() { return batch; }
    public void setBatch(Batch batch) { this.batch = batch; }
    public double getUnitPrice() { return unitPrice; }
    public void setUnitPrice(double unitPrice) { this.unitPrice = unitPrice; }
    public double getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}