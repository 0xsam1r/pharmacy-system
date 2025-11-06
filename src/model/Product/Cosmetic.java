
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
    public void displayCosmetics() {
        System.out.println("Cosmetic Product :" + getName());
        System.out.println("Brand: " + brand);
        System.out.println("Gender: " + gender);
    }
    
    
    
}
