package model.invoices;

import model.Product.Inventory;

import java.util.List;

public class PurchaseInvoice {
    private Inventory inventory;
    private double  remainingMoney;
    private double moneyPaid;
    private List<Batch> batches;
    private Supplier supplier ;
}
