package model.Product;

// * @author mahmoud Elsayed
import model.invoices.Batch ;
import java.util.ArrayList ;
public class Inventory {
    
    private String ID;
    private ArrayList<Product> products;
    private ArrayList<PurchaseInvoice> purchaseInvoices;
    private double reorderLevel;

    public Inventory(String ID, double reorderLevel) {
    this.ID = ID;
    this.reorderLevel = reorderLevel;
    this.products = new ArrayList<>();
    this.purchaseInvoices = new ArrayList<>();
    }
    
    public void addProduct(Product p) {
        products.add(p);
    }
    public void removeProduct(Product p) {
        products.remove(p);
    }
    public boolean containProduct(Product p) {
       return products.contains(p);
    }
    
    public void addPurchaseInvoice(PurchaseInvoice invoice) {
        purchaseInvoices.add(invoice);
    }
    
     public void modifyQuantaty(Batch batch) {

        Product p = batch.products;

        if (p != null) {
            double newQty = p.getQuantityInStock() + batch.getQuantity();
            p.setQuantityInStock(newQty);       
        }
    }
    
    public void modifyQuantaty(SaleReturn saleReturn) {
        for (ReturnItems item : saleReturn.getReturnItems()) {
            Product p = item.getProduct();
            double qty = p.getQuantityInStock()+item.getQuantity();
            p.setQuantityInStock(qty);
        }
    }
     
    public void modifyQuantaty(PurchaseReturn purchaseReturn) {
        for (ReturnItems item : purchaseReturn.getReturnItems()) {
            Product p = item.getProduct();
            double qty = p.getQuantityInStock()-item.getQuantity();
            p.setQuantityInStock(qty);
        }
    }
    
    public void modifyQuantaty(SaleInvoice saleInvoice) {
        for (InvoiceItem item : saleInvoice.getInvoiceItems()) {
            Product p = item.getProduct();
            double qty = p.getQuantityInStock()-item.getQuantity();
            p.setQuantityInStock(qty);
        }
    }
    
    public Product search (String key)
    {
        for(Product  p :products)
        {
            if(p.getName().equalsIgnoreCase(key)||p.getParcode().equalsIgnoreCase(key) )
                return p ;
        }
        return null ;
    }
    
     public void displayInventory() {
        System.out.println("Inventory ID: " + ID);
        for (Product p : products) {
            System.out.println("- " + p.getName() + ", Qty: " + p.getQuantityInStock());
        }
    }

}
