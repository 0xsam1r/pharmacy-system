package model.invoices;

import java.util.List;

public class Supplier {
    private int id;
    private String name;
    private String phone;
    private List<PurchaseInvoice> purchaseInvoices;

    public Supplier(int id, String name, String phone, List<PurchaseInvoice> purchaseInvoices) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.purchaseInvoices = purchaseInvoices;
    }
    public Supplier(Supplier s) {
        this.id = s.id;
        this.name = s.name;
        this.phone = s.phone;
        this.purchaseInvoices = s.purchaseInvoices;
    }
    public Supplier() {
        this.id = 0;
        this.name = "";
        this.phone = "";
        this.purchaseInvoices = null;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<PurchaseInvoice> getPurchaseInvoices() {
        return purchaseInvoices;
    }

    public void setPurchaseInvoices(List<PurchaseInvoice> purchaseInvoices) {
        this.purchaseInvoices = purchaseInvoices;
    }
    
    
    
}
