package model.branch;

import java.util.ArrayList;
import java.util.List;
import model.people.Employee;
import model.Product.Inventory;
//import model.finance.ReportGenerator; // Assuming Report is generated from ReportGenerator or similar; adjust if Report is a separate class

public class Branch {
    private String id;
    private String name;
    private String address;
    private List<Employee> employees;
    private Inventory inventory;

    // Constructor
    public Branch(String id, String name, String address, Inventory inventory) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.employees = new ArrayList<>();
        this.inventory = inventory;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees = employees;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    // Methods
    public void addEmployee(Employee employee) {
        employees.add(employee);
    }

    public void removeEmployee(String employeeId) {
        employees.removeIf(emp -> emp.getId().equals(employeeId));
    }

//    public ReportGenerator.Report getInventoryReport() {
//        // Assuming ReportGenerator has a static method or instance to generate report
//        // Adjust based on actual ReportGenerator implementation
//        return ReportGenerator.generateInventoryReport(this.inventory);
//    }
}