package model.people;

import model.invoices.SaleInvoice;

public class Customer extends Person {
    private double points;

    public Customer(String id, String name, String phone, double points) {
        super(id, name, phone);
        this.points = points;
    }
    public Customer(Customer c) {
        super(c.id, c.name, c.phone);
        this.points = c.points;
    }
    
    public Customer() {
        super();
        this.points = 0;
    }

public double calcPoints(SaleInvoice saleInvoice , double Total) {
        if (saleInvoice != null) {
            double pointsEarned = 0.0;

            if (Total <= 100) {
                pointsEarned = Total / 10.0; 
            } else if (Total <= 500) {
                pointsEarned = 10.0 + (Total - 100) / 8.0;
            } else {
                pointsEarned = 10.0 + 50.0 + (Total - 500) / 5.0;
            }

            this.points += pointsEarned; 
            return this.points;
        }
        return points;
    }

    public double getPoints() { 
        return points; 
    }
    public void setPoints(double points) {
        this.points = points; 
    }
}