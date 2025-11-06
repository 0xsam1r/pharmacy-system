/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Products;

/**
 *
 * @author mahmoud Elsayed
 */
public class DosageForm {
    private double Strength ;
    private String  Active_Ingredient ;

    public DosageForm(double Strength, String Active_Ingredient) {
        this.Strength = Strength;
        this.Active_Ingredient = Active_Ingredient;
    }

    public double getStrength() {
        return Strength;
    }

    public String getActive_Ingredient() {
        return Active_Ingredient;
    }

    @Override
    public String toString() {
        return "DosageForm{" + "Strength=" + Strength + ", Active_Ingredient=" + Active_Ingredient + '}';
    }
  
}
