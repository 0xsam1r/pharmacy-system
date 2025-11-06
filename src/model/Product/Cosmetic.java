/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Products;

/**
 *
 * @author mahmoud Elsayed
 */
public class Cosmetic extends Product {
    private String brand;
    private char gender;
    public Cosmetic(String Parcode, String Name,  int UnitsPerProduct, double Price,String brand, char gender) {
        super(Parcode, Name, UnitsPerProduct, Price, Category.COSMETIC);
        this.brand = brand;
        this.gender = gender;
    }
    public void displayCosmetics() {
        System.out.println("Cosmetic Product :" + getName());
        System.out.println("Brand: " + brand);
        System.out.println("Gender: " + gender);
    }
    
    
    
}
