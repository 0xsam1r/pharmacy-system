package DB;

import model.people.Customer;
import model.people.Employee;
import model.people.UserAccount;
import model.Product.Product;
import model.branch.Branch;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DB_operation {

     /* ------------------ Helper existence checks ------------------ */

    public static boolean isPersonExist(String id) {
        String sql = "SELECT COUNT(*) FROM person WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isPersonExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isPhoneExist(String phone) {
        String sql = "SELECT COUNT(*) FROM person WHERE Phone = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isPhoneExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isCustomerExist(String personId) {
        String sql = "SELECT COUNT(*) FROM customer WHERE Person_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isCustomerExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isEmployeeExist(String personId, String userName, int branchId) {
        String sql = "SELECT COUNT(*) FROM employee WHERE Person_ID = ? AND User_name = ? AND bransh_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ps.setString(2, userName);
            ps.setInt(3, branchId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isEmployeeExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isProductExist(String parcode) {
        String sql = "SELECT COUNT(*) FROM product WHERE parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, parcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isProductExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isSupplierExist(String name, String phone) {
        String sql = "SELECT COUNT(*) FROM supplier WHERE nane = ? AND phone = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isSupplierExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isInvoiceExist(int invoiceId) {
        String sql = "SELECT COUNT(*) FROM invoice WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isInvoiceExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isBranchExist(int id) {
        String sql = "SELECT COUNT(*) FROM bransh WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isBranchExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isBatchExist(String batchNumber, String productParcode) {
        String sql = "SELECT COUNT(*) FROM batch WHERE Batch_number = ? AND Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, batchNumber);
            ps.setString(2, productParcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isBatchExist error: " + e.getMessage());
        }
        return false;
    }

    /* ------------------ Person / Customer operations ------------------ */

    public static boolean addPersonIfNotExist(String id, String phone, String name) {
        if (isPersonExist(id)) {
            System.err.println("addPersonIfNotExist: person " + id + " already exists.");
            return false;
        }
        if (isPhoneExist(phone)) {
            System.err.println("addPersonIfNotExist: phone " + phone + " already exists.");
            return false;
        }
        String sql = "INSERT INTO person (ID, Phone, name) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, id);
            ps.setString(2, phone);
            ps.setString(3, name);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addPersonIfNotExist error: " + e.getMessage());
            return false;
        }
    }

    public static Customer searchCustomerById(String personId) {
        String sql = "SELECT p.ID, p.Phone, p.name, c.points FROM person p JOIN customer c ON p.ID = c.Person_ID WHERE p.ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Customer c = new Customer();
                c.setId(rs.getString("ID"));
                c.setPhone(rs.getString("Phone"));
                c.setName(rs.getString("name"));
                c.setPoints(rs.getDouble("points"));
                return c;
            }
        } catch (SQLException e) {
            System.err.println("searchCustomerById error: " + e.getMessage());
        }
        return null;
    }

    public static boolean addCustomer(Customer c) {
        // uses transaction: insert person then customer
        if (isPersonExist(c.getId())) {
            System.err.println("addCustomer: person already exists -> " + c.getId());
            return false;
        }
        if (isPhoneExist(c.getPhone())) {
            System.err.println("addCustomer: phone already exists -> " + c.getPhone());
            return false;
        }
        String personSql = "INSERT INTO person (ID, Phone, name) VALUES (?, ?, ?)";
        String customerSql = "INSERT INTO customer (points, Person_ID) VALUES (?, ?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement psPerson = conn.prepareStatement(personSql);
                 PreparedStatement psCustomer = conn.prepareStatement(customerSql)) {
                psPerson.setString(1, c.getId());
                psPerson.setString(2, c.getPhone());
                psPerson.setString(3, c.getName());
                psPerson.executeUpdate();

                psCustomer.setDouble(1, c.getPoints());
                psCustomer.setString(2, c.getId());
                psCustomer.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException e) {
            System.err.println("addCustomer error (rollback): " + e.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) { /* ignore */ }
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

    public static boolean updateCustomerPoints(String personId, double newPoints) {
        if (!isCustomerExist(personId)) {
            System.err.println("updateCustomerPoints: customer not found -> " + personId);
            return false;
        }
        String sql = "UPDATE customer SET points = ? WHERE Person_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPoints);
            ps.setString(2, personId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateCustomerPoints error: " + e.getMessage());
            return false;
        }
    }

    /* ------------------ Employee operations ------------------ */

    public static boolean addEmployee(Employee e) {
        if (isPersonExist(e.getId())) {
            System.err.println("addEmployee: person already exists -> " + e.getId());
            return false;
        }
        if (isPhoneExist(e.getPhone())) {
            System.err.println("addEmployee: phone already exists -> " + e.getPhone());
            return false;
        }
        if (e.getBranch() == null || e.getBranch().getId() == null) {
            System.err.println("addEmployee: branch is required");
            return false;
        }
        int branchId;
        try {
            branchId = Integer.parseInt(e.getBranch().getId());
        } catch (NumberFormatException ex) {
            System.err.println("addEmployee: invalid branch id");
            return false;
        }
        if (!isBranchExist(branchId)) {
            System.err.println("addEmployee: branch not found -> " + branchId);
            return false;
        }
        if (isEmployeeExist(e.getId(), e.getAccount().getUsername(), branchId)) {
            System.err.println("addEmployee: employee already exists with same keys");
            return false;
        }

        String personSql = "INSERT INTO person (ID, Phone, name) VALUES (?, ?, ?)";
        String empSql = "INSERT INTO employee (User_name, salary, StartDate, Password, Person_ID, bransh_ID) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement psPerson = conn.prepareStatement(personSql);
                 PreparedStatement psEmp = conn.prepareStatement(empSql)) {
                psPerson.setString(1, e.getId());
                psPerson.setString(2, e.getPhone());
                psPerson.setString(3, e.getName());
                psPerson.executeUpdate();

                psEmp.setString(1, e.getAccount().getUsername());
                psEmp.setDouble(2, e.getSalary());
                psEmp.setString(3, e.getStartDate());
                psEmp.setString(4, e.getAccount().getPassword());
                psEmp.setString(5, e.getId());
                psEmp.setInt(6, branchId);
                psEmp.executeUpdate();
            }
            conn.commit();
            return true;
        } catch (SQLException ex) {
            System.err.println("addEmployee error (rollback): " + ex.getMessage());
            if (conn != null) try { conn.rollback(); } catch (SQLException r) { /* ignore */ }
            return false;
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ex) { /* ignore */ }
        }
    }

    public static Employee searchEmployeeByPersonId(String personId) {
        String sql = "SELECT p.ID, p.Phone, p.name, e.User_name, e.salary, e.StartDate, e.Password, e.bransh_ID " +
                "FROM person p JOIN employee e ON p.ID = e.Person_ID WHERE p.ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, personId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Employee emp = new Employee();
                emp.setId(rs.getString("ID"));
                emp.setPhone(rs.getString("Phone"));
                emp.setName(rs.getString("name"));
                UserAccount acc = new UserAccount();
                acc.setUsername(rs.getString("User_name"));
                acc.setPassword(rs.getString("Password"));
                emp.setAccount(acc);
                emp.setSalary((int) rs.getDouble("salary"));
                emp.setStartDate(rs.getString("StartDate"));
                Branch br = new Branch();
                br.setId(String.valueOf(rs.getInt("bransh_ID")));
                emp.setBranch(br);
                return emp;
            }
        } catch (SQLException e) {
            System.err.println("searchEmployeeByPersonId error: " + e.getMessage());
        }
        return null;
    }

    public static boolean updateEmployeeSalary(String personId, int newSalary) {
        String sql = "UPDATE employee SET salary = ? WHERE Person_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newSalary);
            ps.setString(2, personId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateEmployeeSalary error: " + e.getMessage());
            return false;
        }
    }

    /* ------------------ Product / Batch operations ------------------ */

    public static boolean addProduct(String parcode, String name, double price, int units, int categoryId) {
        if (isProductExist(parcode)) {
            System.err.println("addProduct: product exists -> " + parcode);
            return false;
        }
        String sql = "INSERT INTO product (parcode, Name, Price, Uints, Category_ID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, parcode);
            ps.setString(2, name);
            ps.setDouble(3, price);
            ps.setInt(4, units);
            ps.setInt(5, categoryId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addProduct error: " + e.getMessage());
            return false;
        }
    }

    public static boolean addBatch(String batchNumber, double cost, Date expireDate, int quantity, String productParcode) {
        if (!isProductExist(productParcode)) {
            System.err.println("addBatch: product not found -> " + productParcode);
            return false;
        }
        if (isBatchExist(batchNumber, productParcode)) {
            System.err.println("addBatch: batch exists -> " + batchNumber);
            return false;
        }
        String sql = "INSERT INTO batch (Batch_number, cost, expire_date, Quantaty, Product_parcode) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, batchNumber);
            ps.setDouble(2, cost);
            ps.setDate(3, expireDate);
            ps.setInt(4, quantity);
            ps.setString(5, productParcode);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addBatch error: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateProductPrice(String parcode, double newPrice) {
        if (!isProductExist(parcode)) {
            System.err.println("updateProductPrice: product not found -> " + parcode);
            return false;
        }
        String sql = "UPDATE product SET Price = ? WHERE parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setString(2, parcode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateProductPrice error: " + e.getMessage());
            return false;
        }
    }

    public static Product searchProductByParcode(String parcode) {
        String sql = "SELECT parcode, Name, Price, Uints, Category_ID FROM product WHERE parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, parcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = new Product();
                // Product class fields are protected; set via reflection or add setters.
                // Here assuming setters exist for demonstration (if not, adapt to your Product constructors).
                p.setPrice(rs.getDouble("Price"));
                // p.setParcode(rs.getString("parcode")); // adapt if setter exists
                return p;
            }
        } catch (SQLException e) {
            System.err.println("searchProductByParcode error: " + e.getMessage());
        }
        return null;
    }

    /* ------------------ Supplier operations ------------------ */

    public static boolean addSupplier(String name, String phone, String adress) {
        if (isSupplierExist(name, phone)) {
            System.err.println("addSupplier: supplier already exists -> " + name + " / " + phone);
            return false;
        }
        String sql = "INSERT INTO supplier (nane, phone, adress) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, phone);
            ps.setString(3, adress);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addSupplier error: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateSupplierAddress(String name, String phone, String newAddress) {
        if (!isSupplierExist(name, phone)) {
            System.err.println("updateSupplierAddress: supplier not found");
            return false;
        }
        String sql = "UPDATE supplier SET adress = ? WHERE nane = ? AND phone = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newAddress);
            ps.setString(2, name);
            ps.setString(3, phone);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateSupplierAddress error: " + e.getMessage());
            return false;
        }
    }

    /* ------------------ Invoice operations (basic) ------------------ */

    public static boolean addInvoice(int id, Date date, double price, String employeeUser, String employeePersonId, int employeeBranchId) {
        if (isInvoiceExist(id)) {
            System.err.println("addInvoice: invoice exists -> " + id);
            return false;
        }
        if (!isEmployeeExist(employeePersonId, employeeUser, employeeBranchId)) {
            System.err.println("addInvoice: employee not found (can't create invoice)");
            return false;
        }
        String sql = "INSERT INTO invoice (ID, date, price, employee_User_name, employee_Person_ID, employee_bransh_ID) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setDate(2, date);
            ps.setDouble(3, price);
            ps.setString(4, employeeUser);
            ps.setString(5, employeePersonId);
            ps.setInt(6, employeeBranchId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addInvoice error: " + e.getMessage());
            return false;
        }
    }

    public static boolean addSellInvoice(int invoiceId, String customerPersonId, double discount) {
        if (!isInvoiceExist(invoiceId)) {
            System.err.println("addSellInvoice: parent invoice not found -> " + invoiceId);
            return false;
        }
        if (!isCustomerExist(customerPersonId)) {
            System.err.println("addSellInvoice: customer not found -> " + customerPersonId);
            return false;
        }
        String sql = "INSERT INTO sell_invoice (Discount, Invoice_ID, Customer_Person_ID) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, discount);
            ps.setInt(2, invoiceId);
            ps.setString(3, customerPersonId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addSellInvoice error: " + e.getMessage());
            return false;
        }
    }

    public static boolean addPurchaseInvoice(int invoiceId, double moneyPaid, Double remainingMoney, String supplierName, String supplierPhone) {
        if (!isInvoiceExist(invoiceId)) {
            System.err.println("addPurchaseInvoice: parent invoice not found -> " + invoiceId);
            return false;
        }
        if (!isSupplierExist(supplierName, supplierPhone)) {
            System.err.println("addPurchaseInvoice: supplier not found -> " + supplierName + " / " + supplierPhone);
            return false;
        }
        String sql = "INSERT INTO purchase_invoce (money_paid, remaing_money, Invoice_ID, Supplier_nane, Supplier_phone) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, moneyPaid);
            if (remainingMoney != null) ps.setDouble(2, remainingMoney); else ps.setNull(2, Types.DOUBLE);
            ps.setInt(3, invoiceId);
            ps.setString(4, supplierName);
            ps.setString(5, supplierPhone);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addPurchaseInvoice error: " + e.getMessage());
            return false;
        }
    }

    /* ------------------ Inventory operations (basic) ------------------ */

    public static boolean addInventory(int branshId) {
        if (!isBranchExist(branshId)) {
            System.err.println("addInventory: branch not found -> " + branshId);
            return false;
        }
        String sql = "INSERT INTO inventory (Bransh_ID) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, branshId);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addInventory error: " + e.getMessage());
            return false;
        }
    }

    public static boolean addInventoryProduct(int inventoryId, String productParcode, double qty, int reorderLevel) {
        if (!isProductExist(productParcode)) {
            System.err.println("addInventoryProduct: product not found -> " + productParcode);
            return false;
        }
        String sql = "INSERT INTO inventory_has_product (Inventory_ID, Product_parcode, Quntaty, reordr_level) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, inventoryId);
            ps.setString(2, productParcode);
            ps.setDouble(3, qty);
            ps.setInt(4, reorderLevel);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addInventoryProduct error: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateInventoryQuantity(int inventoryId, String productParcode, double newQty) {
        String sql = "UPDATE inventory_has_product SET Quntaty = ? WHERE Inventory_ID = ? AND Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newQty);
            ps.setInt(2, inventoryId);
            ps.setString(3, productParcode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateInventoryQuantity error: " + e.getMessage());
            return false;
        }
    }

    /* ------------------ Utility search helpers ------------------ */

    public static List<String> searchProductsByName(String keyword) {
        List<String> results = new ArrayList<>();
        String sql = "SELECT parcode, Name FROM product WHERE Name LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("parcode") + " - " + rs.getString("Name"));
            }
        } catch (SQLException e) {
            System.err.println("searchProductsByName error: " + e.getMessage());
        }
        return results;
    }

    public static List<String> searchSuppliersByName(String keyword) {
        List<String> results = new ArrayList<>();
        String sql = "SELECT nane, phone FROM supplier WHERE nane LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + keyword + "%");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                results.add(rs.getString("nane") + " / " + rs.getString("phone"));
            }
        } catch (SQLException e) {
            System.err.println("searchSuppliersByName error: " + e.getMessage());
        }
        return results;
    }

    public static boolean isCategoryExistById(int id) {
        String sql = "SELECT COUNT(*) FROM category WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isCategoryExistById error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isCategoryExistByName(String name) {
        String sql = "SELECT COUNT(*) FROM category WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isCategoryExistByName error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isDosageFormExist(int id) {
        String sql = "SELECT COUNT(*) FROM dosage_form WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isDosageFormExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isMedicineExist(String productParcode) {
        String sql = "SELECT COUNT(*) FROM medicine WHERE Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productParcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isMedicineExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isSupplierHasProductExist(String supplierName, String supplierPhone, String productParcode) {
        String sql = "SELECT COUNT(*) FROM supplier_has_product WHERE Supplier_nane = ? AND Supplier_phone = ? AND Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplierName);
            ps.setString(2, supplierPhone);
            ps.setString(3, productParcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isSupplierHasProductExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isMedicineDosageExist(String medicineParcode, int dosageId) {
        String sql = "SELECT COUNT(*) FROM medicine_has_dosage_form WHERE medicine_Product_parcode = ? AND dosage_form_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicineParcode);
            ps.setInt(2, dosageId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isMedicineDosageExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isInvoiceHasProductExist(int invoiceId, String productParcode) {
        String sql = "SELECT COUNT(*) FROM invoice_has_product WHERE Invoice_ID = ? AND Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setString(2, productParcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isInvoiceHasProductExist error: " + e.getMessage());
        }
        return false;
    }

    public static boolean isPurchaseInvoiceHasBatchExist(int invoiceId, String batchNumber, String productParcode) {
        String sql = "SELECT COUNT(*) FROM purchase_invoce_has_batch WHERE purchase_invoce_Invoice_ID = ? AND Batch_Batch_number = ? AND Batch_Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setString(2, batchNumber);
            ps.setString(3, productParcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1) > 0;
        } catch (SQLException e) {
            System.err.println("isPurchaseInvoiceHasBatchExist error: " + e.getMessage());
        }
        return false;
    }

    /* ------------------ category ------------------ */

    public static boolean addCategory(String name) {
        if (isCategoryExistByName(name)) {
            System.err.println("addCategory: category exists -> " + name);
            return false; // strict
        }
        String sql = "INSERT INTO category (name) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, name);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addCategory error: " + e.getMessage());
            return false;
        }
    }

    public static Integer searchCategoryIdByName(String name) {
        String sql = "SELECT ID FROM category WHERE name = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt("ID");
        } catch (SQLException e) {
            System.err.println("searchCategoryIdByName error: " + e.getMessage());
        }
        return null;
    }

    public static boolean updateCategoryName(int id, String newName) {
        if (!isCategoryExistById(id)) {
            System.err.println("updateCategoryName: category not found -> " + id);
            return false;
        }
        if (isCategoryExistByName(newName)) {
            System.err.println("updateCategoryName: new name already exists -> " + newName);
            return false; // strict avoid duplicates
        }
        String sql = "UPDATE category SET name = ? WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newName);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateCategoryName error: " + e.getMessage());
            return false;
        }
    }

    /* ------------------ dosage_form ------------------ */

    public static boolean addDosageForm(int id, String activeIngredient) {
        if (isDosageFormExist(id)) {
            System.err.println("addDosageForm: dosage form exists -> " + id);
            return false;
        }
        String sql = "INSERT INTO dosage_form (ID, active_ing) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setString(2, activeIngredient);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addDosageForm error: " + e.getMessage());
            return false;
        }
    }

    public static String searchDosageFormById(int id) {
        String sql = "SELECT active_ing FROM dosage_form WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("active_ing");
        } catch (SQLException e) {
            System.err.println("searchDosageFormById error: " + e.getMessage());
        }
        return null;
    }

    public static boolean updateDosageForm(int id, String newActiveIng) {
        if (!isDosageFormExist(id)) {
            System.err.println("updateDosageForm: not found -> " + id);
            return false;
        }
        String sql = "UPDATE dosage_form SET active_ing = ? WHERE ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newActiveIng);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateDosageForm error: " + e.getMessage());
            return false;
        }
    }

    /* ------------------ medicine_has_dosage_form ------------------ */
    // strict: refuse if relation exists

    public static boolean addMedicineDosageForm(String medicineParcode, int dosageFormId, double strength) {
        // validate existence of medicine & dosage form
        if (!isProductExist(medicineParcode) || !isMedicineExist(medicineParcode)) {
            System.err.println("addMedicineDosageForm: medicine not found -> " + medicineParcode);
            return false;
        }
        if (!isDosageFormExist(dosageFormId)) {
            System.err.println("addMedicineDosageForm: dosage form not found -> " + dosageFormId);
            return false;
        }
        if (isMedicineDosageExist(medicineParcode, dosageFormId)) {
            System.err.println("addMedicineDosageForm: relation already exists -> " + medicineParcode + " / " + dosageFormId);
            return false; // strict
        }
        String sql = "INSERT INTO medicine_has_dosage_form (medicine_Product_parcode, dosage_form_ID, Strength) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicineParcode);
            ps.setInt(2, dosageFormId);
            ps.setDouble(3, strength);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addMedicineDosageForm error: " + e.getMessage());
            return false;
        }
    }

    public static Double searchMedicineDosageStrength(String medicineParcode, int dosageFormId) {
        String sql = "SELECT Strength FROM medicine_has_dosage_form WHERE medicine_Product_parcode = ? AND dosage_form_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, medicineParcode);
            ps.setInt(2, dosageFormId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("Strength");
        } catch (SQLException e) {
            System.err.println("searchMedicineDosageStrength error: " + e.getMessage());
        }
        return null;
    }

    public static boolean updateMedicineDosageStrength(String medicineParcode, int dosageFormId, double newStrength) {
        if (!isMedicineDosageExist(medicineParcode, dosageFormId)) {
            System.err.println("updateMedicineDosageStrength: relation not found");
            return false;
        }
        String sql = "UPDATE medicine_has_dosage_form SET Strength = ? WHERE medicine_Product_parcode = ? AND dosage_form_ID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newStrength);
            ps.setString(2, medicineParcode);
            ps.setInt(3, dosageFormId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateMedicineDosageStrength error: " + e.getMessage());
            return false;
        }
    }

    /* ------------------ supplier_has_product ------------------ */
    // strict mode — don't add duplicates

    public static boolean addSupplierProduct(String supplierName, String supplierPhone, String productParcode) {
        if (!isSupplierExist(supplierName, supplierPhone)) {
            System.err.println("addSupplierProduct: supplier not found -> " + supplierName + " / " + supplierPhone);
            return false;
        }
        if (!isProductExist(productParcode)) {
            System.err.println("addSupplierProduct: product not found -> " + productParcode);
            return false;
        }
        if (isSupplierHasProductExist(supplierName, supplierPhone, productParcode)) {
            System.err.println("addSupplierProduct: relation exists -> " + supplierName + " / " + productParcode);
            return false; // strict
        }
        String sql = "INSERT INTO supplier_has_product (Supplier_nane, Supplier_phone, Product_parcode) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplierName);
            ps.setString(2, supplierPhone);
            ps.setString(3, productParcode);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addSupplierProduct error: " + e.getMessage());
            return false;
        }
    }

    public static List<String> searchSuppliersForProduct(String productParcode) {
        List<String> res = new ArrayList<>();
        String sql = "SELECT s.nane, s.phone FROM supplier s JOIN supplier_has_product shp ON s.nane = shp.Supplier_nane AND s.phone = shp.Supplier_phone WHERE shp.Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, productParcode);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                res.add(rs.getString("nane") + " / " + rs.getString("phone"));
            }
        } catch (SQLException e) {
            System.err.println("searchSuppliersForProduct error: " + e.getMessage());
        }
        return res;
    }

    public static boolean removeSupplierProductRelation(String supplierName, String supplierPhone, String productParcode) {
        if (!isSupplierHasProductExist(supplierName, supplierPhone, productParcode)) {
            System.err.println("removeSupplierProductRelation: relation not found");
            return false;
        }
        String sql = "DELETE FROM supplier_has_product WHERE Supplier_nane = ? AND Supplier_phone = ? AND Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, supplierName);
            ps.setString(2, supplierPhone);
            ps.setString(3, productParcode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("removeSupplierProductRelation error: " + e.getMessage());
            return false;
        }
    }

    /* ------------------ invoice_has_product ------------------ */
    // strict: refuse if duplicate

    public static boolean addInvoiceHasProduct(int invoiceId, String productParcode, double units) {
        if (!isInvoiceExist(invoiceId)) {
            System.err.println("addInvoiceHasProduct: invoice not found -> " + invoiceId);
            return false;
        }
        if (!isProductExist(productParcode)) {
            System.err.println("addInvoiceHasProduct: product not found -> " + productParcode);
            return false;
        }
        if (isInvoiceHasProductExist(invoiceId, productParcode)) {
            System.err.println("addInvoiceHasProduct: relation exists -> " + invoiceId + " / " + productParcode);
            return false; // strict
        }
        String sql = "INSERT INTO invoice_has_product (Invoice_ID, Product_parcode, units) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setString(2, productParcode);
            ps.setDouble(3, units);
            ps.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("addInvoiceHasProduct error: " + e.getMessage());
            return false;
        }
    }

    public static Double searchInvoiceProductUnits(int invoiceId, String productParcode) {
        String sql = "SELECT units FROM invoice_has_product WHERE Invoice_ID = ? AND Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, invoiceId);
            ps.setString(2, productParcode);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getDouble("units");
        } catch (SQLException e) {
            System.err.println("searchInvoiceProductUnits error: " + e.getMessage());
        }
        return null;
    }

    public static boolean updateInvoiceProductUnits(int invoiceId, String productParcode, double newUnits) {
        if (!isInvoiceHasProductExist(invoiceId, productParcode)) {
            System.err.println("updateInvoiceProductUnits: relation not found");
            return false;
        }
        String sql = "UPDATE invoice_has_product SET units = ? WHERE Invoice_ID = ? AND Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newUnits);
            ps.setInt(2, invoiceId);
            ps.setString(3, productParcode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("updateInvoiceProductUnits error: " + e.getMessage());
            return false;
        }
    }

    /* ------------------ purchase_invoce_has_batch ------------------ */
    // strict mode: refuse duplicate relation

    public static boolean addPurchaseInvoiceHasBatch(int purchaseInvoiceId, String batchNumber, String productParcode, int quantity) {
        if (!isInvoiceExist(purchaseInvoiceId)) {
            System.err.println("addPurchaseInvoiceHasBatch: purchase invoice not found -> " + purchaseInvoiceId);
            return false;
        }
        if (!isBatchExist(batchNumber, productParcode)) {
            System.err.println("addPurchaseInvoiceHasBatch: batch not found -> " + batchNumber + " / " + productParcode);
            return false;
        }
        if (isPurchaseInvoiceHasBatchExist(purchaseInvoiceId, batchNumber, productParcode)) {
            System.err.println("addPurchaseInvoiceHasBatch: relation exists -> " + purchaseInvoiceId + " / " + batchNumber);
            return false; // strict
        }
        String sql = "INSERT INTO purchase_invoce_has_batch (purchase_invoce_Invoice_ID, Batch_Batch_number, Batch_Product_parcode) VALUES (?, ?, ?)";
        // Note: schema has optional column purchase_invoce_has_Batchcol (nullable default NULL) — we skip it
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, purchaseInvoiceId);
            ps.setString(2, batchNumber);
            ps.setString(3, productParcode);
            ps.executeUpdate();
            // If you want to record quantity in another table, do it separately (schema doesn't include quantity column here)
            return true;
        } catch (SQLException e) {
            System.err.println("addPurchaseInvoiceHasBatch error: " + e.getMessage());
            return false;
        }
    }

    public static boolean removePurchaseInvoiceHasBatch(int purchaseInvoiceId, String batchNumber, String productParcode) {
        if (!isPurchaseInvoiceHasBatchExist(purchaseInvoiceId, batchNumber, productParcode)) {
            System.err.println("removePurchaseInvoiceHasBatch: relation not found");
            return false;
        }
        String sql = "DELETE FROM purchase_invoce_has_batch WHERE purchase_invoce_Invoice_ID = ? AND Batch_Batch_number = ? AND Batch_Product_parcode = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, purchaseInvoiceId);
            ps.setString(2, batchNumber);
            ps.setString(3, productParcode);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("removePurchaseInvoiceHasBatch error: " + e.getMessage());
            return false;
        }
    }

}
