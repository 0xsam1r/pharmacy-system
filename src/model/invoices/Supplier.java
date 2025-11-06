package model.invoices;

import java.util.List;

public class Supplier {
    private int id;
    private String name;
    private String phone;
    List<PurchaseInvoice> purchaseInvoices;
}
