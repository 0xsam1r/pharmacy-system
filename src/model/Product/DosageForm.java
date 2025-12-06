package model.Product;

 
public class DosageForm {
    private double Strength ;
    private String  Active_Ingredient ;

    public DosageForm(double Strength, String Active_Ingredient) {
        this.Strength = Strength;
        this.Active_Ingredient = Active_Ingredient;
    }
    public DosageForm(DosageForm d) {
        this.Strength = d.Strength;
        this.Active_Ingredient = d.Active_Ingredient;
    }
    public DosageForm() {
        this.Strength = 0;
        this.Active_Ingredient = "";
    }

    public double getStrength() {
        return Strength;
    }

    public String getActive_Ingredient() {
        return Active_Ingredient;
    }

    public void setStrength(double Strength) {
        this.Strength = Strength;
    }

    public void setActive_Ingredient(String Active_Ingredient) {
        this.Active_Ingredient = Active_Ingredient;
    }

    @Override
    public String toString() {
        return "DosageForm{" + "Strength=" + Strength + ", Active_Ingredient=" + Active_Ingredient + '}';
    }
  
}
