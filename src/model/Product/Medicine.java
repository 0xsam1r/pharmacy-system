/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Products;
/*
import java.util.ArrayList;
/**
 *
 * @author mahmoud Elsayed
 */
/*
public class Medicine extends Product{
    private ِArrayList<DosageForm> form  ;

    public Medicine( String Parcode, String Name, int UnitsPerProduct, double Price, Products.Category Category) {
        super(Parcode, Name, UnitsPerProduct, Price ,Category);
        this.form = new ArrayList<>();
    }
    
    public void AddDosageForm(DosageForm Nform) {
        form.add(Nform);
    }
    
    public void RemoveDosageForm(DosageForm Oform) {
        form.remove(Oform);
    }
     public int getFormsCount() {
        return form.size();
    }
     public void displayMedicine() {
        System.out.println("Medicine: " + getName());
        for (DosageForm fo : form) {
            System.out.println(" - " + fo);
        }
}
*/
import java.util.ArrayList;

public class Medicine extends Product {

    private ArrayList<DosageForm> forms; 

    public Medicine(String parcode, String name, int unitsPerProduct, double price) {
        super(parcode, name, unitsPerProduct, price, Category.MEDICINE);
        this.forms = new ArrayList<>(); 
    }
// this new to our structure
    public void addDosageForm(DosageForm form) {
        forms.add(form);
    }
    public void displayMedicine() {
        System.out.println("Medicine Product :  " + getName());
        for (DosageForm form : forms) {
            System.out.println(" - " + form);
        }
    }
}
