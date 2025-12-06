package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/pms";
    private static final String USER = "pmsadmin";
    private static final String PASSWORD = "1234";  

    private static Connection connection = null;

     
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connection succ");
            }
        } catch (SQLException e) {
            System.out.println("Connection fill");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println("");
            e.printStackTrace();
        }
        return connection;
    }

     
    public static void main(String[] args) {
        getConnection();
    }
}
