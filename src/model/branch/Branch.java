package model.branch;

import DB.DBConnection;
import com.sun.jdi.connect.spi.Connection;
import java.util.ArrayList;
import java.util.List;
import model.people.Employee;
import model.Product.Inventory;
import model.finance.Transaction;
import model.finance.Treasury;
//import model.finance.ReportGenerator; // Assuming Report is generated from ReportGenerator or similar; adjust if Report is a separate class

public class Branch {
    private String id;
    private String name;
    private String address;
    private List<Employee> employees;
    private Inventory inventory;
    private Treasury treasury;
    private List<Transaction> transactions;
    // Constructor
    public Branch(String id, String name, String address,List<Employee> employees , Inventory inventory, Treasury treasury, List<Transaction> transaction) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.employees = employees;
        this.inventory = inventory;
        this.treasury = treasury;
        this.transactions = transaction;
    }
    public Branch(Branch b) {
        this.id = b.id;
        this.name = b.name;
        this.address = b.address;
        this.employees = b.employees;
        this.inventory = b.inventory;
        this.treasury = b.treasury;
        this.transactions = b.transactions;
    }
    public Branch() {
        this.id = "";
        this.name = "";
        this.address = "";
        this.employees = null;
        this.inventory = null;
        this.treasury = null;
        this.transactions = null;
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

    public Treasury getTreasury() {
        return treasury;
    }

    public void setTreasury(Treasury treasury) {
        this.treasury = treasury;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<Transaction> transactions) {
        this.transactions = transactions;
    }
    public static Branch getBranchById(String branchId) {
        String sql = "SELECT ID, Name, Adress FROM bransh WHERE ID = ?";

        try (Connection conn = (Connection) DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, Integer.parseInt(branchId));
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return new Branch(
                    String.valueOf(rs.getInt("ID")),
                    rs.getString("Name"),
                    rs.getString("Adress")
                );
            }
        } catch (SQLException e) {
            System.err.println("Error fetching branch by ID: " + e.getMessage());
            e.printStackTrace();
        }
        return null; // لو مش موجود
    }
}