package DB;

import model.people.Customer;
import model.people.Person;
import java.sql.*;
import model.people.Employee;

public class DB_operation {
    static public boolean isPersonExist(String id) {
        String checkSql = "SELECT COUNT(*) FROM `person` WHERE `ID` = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // Returns true if count > 0
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            System.err.println("Error checking person existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false; // Assume not existing if error occurs
    }
    
    static public boolean isPhoneExist(String phone) {
    String checkSql = "SELECT COUNT(*) FROM `person` WHERE `Phone` = ?";
    try (Connection conn = DBConnection.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(checkSql)) {
        pstmt.setString(1, phone);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        System.err.println("Error checking phone existence: " + e.getMessage());
        e.printStackTrace();
    }
    return false;
}

    /*-------------------Customer------------------*/
    static public boolean add_Customer(Customer c) {
            if (isPersonExist(c.getId())) {
        System.err.println("Error: Person with ID " + c.getId() + " already exists.");
        return false;
    }
    if (isPhoneExist(c.getPhone())) {
        System.err.println("Error: Phone number " + c.getPhone() + " already exists in the system.");
        return false;
    }

        String customerInsertSql = "INSERT INTO `Customer` (`Person_ID`, `points`) VALUES (?, ?)";
        String personInsertSql = "INSERT INTO `person` (`ID`, `Phone`, `name`) VALUES (?, ?, ?) ";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);
            try (PreparedStatement pstmtPerson = conn.prepareStatement(personInsertSql)) {
                pstmtPerson.setString(1, c.getId());
                pstmtPerson.setString(2, c.getPhone());
                pstmtPerson.setString(3, c.getName());
                pstmtPerson.executeUpdate();
            }
            try (PreparedStatement pstmtCustomer = conn.prepareStatement(customerInsertSql)) {
                pstmtCustomer.setString(1, c.getId());
                pstmtCustomer.setDouble(2, c.getPoints());
                pstmtCustomer.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException excepRollback) {
                    System.err.println("Error during rollback: " + excepRollback.getMessage());
                }
            }
            System.err.println("Error adding Customer (Transaction rolled back): " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    System.err.println("Error closing connection: " + e.getMessage());
                }
            }
        }
    }
    /*-------------------Employee------------------*/
    static public boolean add_Employee(Employee e) {
        if (isPersonExist(e.getId())) {
            System.err.println("Error: Employee with ID " + e.getId() + " already exists. Insertion aborted.");
            return false;
        }
        if (isPhoneExist(e.getPhone())) {
            System.err.println("Error: Phone number " + e.getPhone() + " already exists in the system. Insertion aborted.");
            return false;
        }

        String personInsertSql = "INSERT INTO `person` (`ID`, `Phone`, `name`) VALUES (?, ?, ?) ";
        String employeeInsertSql = "INSERT INTO `employee` (`User_name`, `salary`, `StartDate`, `Password`, `Person_ID`, `bransh_ID`) VALUES (?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); 
            try (PreparedStatement pstmtPerson = conn.prepareStatement(personInsertSql)) {
                pstmtPerson.setString(1, e.getId());
                pstmtPerson.setString(2, e.getPhone());
                pstmtPerson.setString(3, e.getName());
                pstmtPerson.executeUpdate();
            }

            try (PreparedStatement pstmtEmployee = conn.prepareStatement(employeeInsertSql)) {
                
                pstmtEmployee.setString(1, e.getAccount().getUsername()); 
                pstmtEmployee.setInt(2, e.getSalary());               
                pstmtEmployee.setString(3, e.getStartDate());       
                pstmtEmployee.setString(4, e.getAccount().getPassword());
                pstmtEmployee.setString(5, e.getId());
                
                if (e.getBranch() != null) {
                    pstmtEmployee.setString(6, e.getBranch().getId());
                } else {
                    pstmtEmployee.setNull(6, Types.INTEGER); 
                }
                
                pstmtEmployee.executeUpdate();
            }

            // 4. تأكيد المعاملة (Commit)
            conn.commit();
            System.out.println("Employee " + e.getName() + " added successfully with integrated account details.");
            return true;

        } catch (SQLException ex) {

            if (conn != null) {
                try {
                    System.err.println("Transaction failed. Rolling back changes for employee " + e.getId());
                    conn.rollback();
                } catch (SQLException exRollback) {
                    System.err.println("Error during rollback: " + exRollback.getMessage());
                }
            }
            System.err.println("Error adding Employee: " + ex.getMessage());
            ex.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException exClose) {
                    System.err.println("Error closing connection: " + exClose.getMessage());
                }
            }
        }
    }
    
}