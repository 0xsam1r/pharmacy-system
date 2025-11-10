package model.invoices;



import model.Product.Inventory;
import model.Product.Product;

public class InvoiceItem {
    private Product product;
    private String Units; 
    private int quantity; 
    private Inventory inventory;

    public InvoiceItem(Product product, String Units, int quantity, Inventory inventory) {
        this.product = product;
        this.Units = Units;
        this.quantity = quantity;
        this.inventory = inventory;
    }
    public InvoiceItem(InvoiceItem i) {
        this.product = i.product;
        this.Units = i.Units;
        this.quantity = i.quantity;
        this.inventory = i.inventory;
    }
    public InvoiceItem() {
        this.product = null;
        this.Units = "";
        this.quantity = 0;
        this.inventory = null;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getUnits() {
        return Units;
    }

    public void setUnits(String Units) {
        this.Units = Units;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public double CalcPrice() {

        if ( product.getQuantityInStock() > 0) {
            if (product.getQuantityInStock() < this.quantity) {
                System.out.println("Quantity in stock not enough");
                return 0;
            }
            else
                return ((double) quantity /product.getUnitsPerProduct()) * product.getPrice();
        }
        else {
            System.out.println("Product Doesn't exist");
            return 0;
        }
    }
}

