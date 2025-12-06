package model.Product;

 

import java.util.ArrayList;
import enums.Category;

public class Medicine extends Product {

    private ArrayList<DosageForm> forms; 

    public Medicine(String parcode, String name, int unitsPerProduct, double price, Category Category, ArrayList<DosageForm> forms) {
        super(parcode, name, unitsPerProduct, price, Category);
        this.forms = forms; 
    }
    public Medicine(Medicine m) {
        this.Parcode = m.Parcode;
        this.Name = m.Name;
        this.UnitsPerProduct = m.UnitsPerProduct;
        this.Price = m.Price;
        this.Category = m.Category;
        this.quantityInStock=0;
        this.forms = m.forms; 
    }
    public Medicine() {
        this.Parcode = "";
        this.Name = "";
        this.UnitsPerProduct = 0;
        this.Price = 0;
        this.Category = null;
        this.quantityInStock=0;
        this.forms = null; 
    }
 
    public void addDosageForm(DosageForm form) {
        forms.add(form);
    }

    public ArrayList<DosageForm> getForms() {
        return forms;
    }

    public void setForms(ArrayList<DosageForm> forms) {
        this.forms = forms;
    }
    
    public void displayMedicine() {
        for (DosageForm form : forms) {
            System.out.println(" - " + form);
        }
    }

    @Override
    public String toString() {
        displayproduct();
        displayMedicine();
        return "";
    }
    
}
