/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Products;
import Products.Category;

/**
 *
 * @author mahmoud Elsayed
 */
public class Product {
    private String Parcode ;
    private String Name ;
    private int UnitsPerProduct ;
    private double Price ;
    private  Category Category  ;

    public Product(String Parcode, String Name, int UnitsPerProduct, double Price, Category Category) {
        this.Parcode = Parcode;
        this.Name = Name;
        this.UnitsPerProduct = UnitsPerProduct;
        this.Price = Price;
        this.Category = Category;
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
