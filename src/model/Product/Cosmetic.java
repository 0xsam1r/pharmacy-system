
package model.Product;
import enums.Category;
// @author mahmoud Elsayed

 public class Cosmetic extends Product {
    private String brand;
    private char gender;
    public Cosmetic(String Parcode, String Name,  int UnitsPerProduct, double Price,Category Category,String brand, char gender) {
        super(Parcode, Name, UnitsPerProduct, Price, Category );
        this.brand = brand;
        this.gender = gender;
    }
    public Cosmetic(Cosmetic c) {
        this.Parcode = c.Parcode;
        this.Name = c.Name;
        this.UnitsPerProduct = c.UnitsPerProduct;
        this.Price = c.Price;
        this.Category = c.Category;
        this.quantityInStock=0;
        this.brand = c.brand;
        this.gender = c.gender;
    }
    public Cosmetic() {
        this.Parcode = "";
        this.Name = "";
        this.UnitsPerProduct = 0;
        this.Price = 0;
        this.Category = null;
        this.quantityInStock=0;
        this.brand = "";
        this.gender = 0;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }
    
    public void displayCosmetics() {
        System.out.println("Cosmetic Product :" + getName());
        System.out.println("Brand: " + brand);
        System.out.println("Gender: " + gender);
    }
    
    
    
}
