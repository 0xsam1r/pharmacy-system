package model.Product;
import enums.Category;

// @author mahmoud Elsayed
public class Product {
    private String Parcode ;
    private String Name ;
    private int UnitsPerProduct ;
    private double Price ;
    private  Category Category  ;
    private double quantityInStock;// comment => that to handel Qu in inventory  

    public Product(String Parcode, String Name, int UnitsPerProduct, double Price, Category Category) {
        this.Parcode = Parcode;
        this.Name = Name;
        this.UnitsPerProduct = UnitsPerProduct;
        this.Price = Price;
        this.Category = Category;
        this.quantityInStock=0;
    }

    public void setUnitsPerProduct(int UnitsPerProduct) {
        this.UnitsPerProduct = UnitsPerProduct;
    }

    public void setQuantityInStock(double quantityInStock) {
        this.quantityInStock = quantityInStock;
    }

    public double getQuantityInStock() {
        return quantityInStock;
    }

    public String getParcode() {
        return Parcode;
    }
    public String getName() {
        return Name;
    }
    public int getUnitsPerProduct() {
        return UnitsPerProduct;
    }
    public double getPrice() {
        return Price;
    }
    public Category getCategory() {
        return Category;
    }
    public void setPrice(double Price) {
        this.Price = Price;
    }
    
    
    
}
