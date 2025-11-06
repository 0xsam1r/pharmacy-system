package model.invoices;

// @ Written By Samir Ahmed (0xsam1r)

import model.Product.Inventory;
import model.Product.Product;

public class InvoiceItem {
    private Product product;
    private String Units; // The measurement unit (e.g. “box”, “tablet”, “bottle”)
    private int quantity; // How many of those units
    private Inventory inventory;

    // =============================================================
    // @ methods
    // =============================================================
    // handler 0 product not exist ---
    public double CalcPrice() {

        // what for units !?
        if (validProduct(inventory)) {
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

    public boolean validProduct(Inventory inventory) {
        return product.getQuantityInStock() > 0;
    }
}
