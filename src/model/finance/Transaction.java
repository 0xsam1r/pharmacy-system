package model.finance;

import model.people.Employee;
import model.invoices.Invoice;

import java.time.LocalDateTime;
import model.invoices.Invoice;

public class Transaction {
    private int id;
    private LocalDateTime dateAndTime;
    private String type;
    private Employee employee;
    private Invoice invoice;
    private double amountOfMoney;

    public Transaction(int id, LocalDateTime dateAndTime, String type, Employee employee, Invoice invoice, double amountOfMoney) {
        this.id = id;
        this.dateAndTime = dateAndTime;
        this.type = type;
        this.employee = employee;
        this.invoice = invoice;
        this.amountOfMoney = amountOfMoney;
    }
    
    public Transaction(Transaction t) {
        this.id = t.id;
        this.dateAndTime = t.dateAndTime;
        this.type = t.type;
        this.employee = t.employee;
        this.invoice = t.invoice;
        this.amountOfMoney = t.amountOfMoney;
    }
    
    public Transaction() {
        this.id = 0;
        this.dateAndTime = null;
        this.type = "";
        this.employee = new Employee();
        this.invoice = null;
        this.amountOfMoney = 0;
    }

     
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public LocalDateTime getDateAndTime() { return dateAndTime; }
    public void setDateAndTime(LocalDateTime dateAndTime) { this.dateAndTime = dateAndTime; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }
    public Invoice getInvoice() { return invoice; }
    public void setInvoice(Invoice invoice) { this.invoice = invoice; }
    public double getAmountOfMoney() { return amountOfMoney; }
    public void setAmountOfMoney(double amountOfMoney) { this.amountOfMoney = amountOfMoney; }
}