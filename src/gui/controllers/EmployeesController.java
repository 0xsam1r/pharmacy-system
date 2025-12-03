package gui.controllers;

import DB.DBConnection;
import DB.DB_operation;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import model.branch.Branch;
import model.people.Employee;
import model.people.UserAccount;
import util.ExceptionLogger;

import java.net.URL;
import java.sql.*;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * Controller for Employees Management View
 */
public class EmployeesController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<EmployeeData> employeesTable;
    @FXML private Label employeeCountLabel;
    
    @FXML private TableColumn<EmployeeData, String> colId;
    @FXML private TableColumn<EmployeeData, String> colName;
    @FXML private TableColumn<EmployeeData, String> colPhone;
    @FXML private TableColumn<EmployeeData, String> colUsername;
    @FXML private TableColumn<EmployeeData, String> colRole;
    @FXML private TableColumn<EmployeeData, Double> colSalary;
    @FXML private TableColumn<EmployeeData, String> colStartDate;
    @FXML private TableColumn<EmployeeData, Void> colActions;
    
    private ObservableList<EmployeeData> employeesList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            setupTableColumns();
            loadEmployees();
            ExceptionLogger.logInfo("Employees view initialized");
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error initializing employees view");
        }
    }
    
    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colRole.setCellValueFactory(new PropertyValueFactory<>("role"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));
        colStartDate.setCellValueFactory(new PropertyValueFactory<>("startDate"));
        
        colActions.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("✏️ Edit");
            private final Button deleteBtn = new Button("🗑️");
            
            {
                editBtn.setOnAction(event -> {
                    EmployeeData employee = getTableView().getItems().get(getIndex());
                    handleEditEmployee(employee);
                });
                
                deleteBtn.setOnAction(event -> {
                    EmployeeData employee = getTableView().getItems().get(getIndex());
                    handleDeleteEmployee(employee);
                });
                
                editBtn.getStyleClass().addAll("btn", "btn-secondary");
                editBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10;");
                deleteBtn.getStyleClass().addAll("btn", "btn-danger");
                deleteBtn.setStyle("-fx-font-size: 10px; -fx-padding: 5 10;");
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editBtn, deleteBtn);
                    buttons.setAlignment(Pos.CENTER);
                    setGraphic(buttons);
                }
            }
        });
    }
    
    private void loadEmployees() {
        try {
            employeesList.clear();
            
            String query = "SELECT p.ID, p.name, p.Phone, e.User_name, e.salary, e.StartDate " +
                          "FROM person p JOIN employee e ON p.ID = e.Person_ID";
            
            try (Connection conn = DBConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery(query)) {
                
                while (rs.next()) {
                    EmployeeData emp = new EmployeeData();
                    emp.setId(rs.getString("ID"));
                    emp.setName(rs.getString("name"));
                    emp.setPhone(rs.getString("Phone"));
                    emp.setUsername(rs.getString("User_name"));
                    emp.setSalary(rs.getDouble("salary"));
                    emp.setStartDate(rs.getString("StartDate"));
                    emp.setRole("Employee"); // Default role
                    
                    employeesList.add(emp);
                }
                
                employeesTable.setItems(employeesList);
                updateEmployeeCount();
            }
            
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Database error loading employees");
            showError("Database Error", "Failed to load employees");
        }
    }
    
    @FXML
    private void handleAddEmployee() {
        try {
            Dialog<EmployeeData> dialog = createEmployeeDialog("Add New Employee", null);
            
            Optional<EmployeeData> result = dialog.showAndWait();
            result.ifPresent(data -> {
                try {
                    Employee emp = new Employee();
                    emp.setId(data.getId());
                    emp.setName(data.getName());
                    emp.setPhone(data.getPhone());
                    emp.setSalary((int)data.getSalary());
                    emp.setStartDate(java.time.LocalDate.now().toString());
                    
                    UserAccount acc = new UserAccount();
                    acc.setUsername(data.getUsername());
                    acc.setPassword(data.getPassword()); // Should be hashed
                    emp.setAccount(acc);
                    
                    Branch branch = new Branch();
                    branch.setId("1"); // Default branch 1
                    emp.setBranch(branch);
                    
                    if (DB_operation.addEmployee(emp)) {
                        showSuccess("Employee added successfully!");
                        loadEmployees();
                    } else {
                        showError("Add Failed", "Failed to add employee. ID or Username might already exist.");
                    }
                    
                } catch (Exception e) {
                    ExceptionLogger.logException(e, "Error adding employee");
                    showError("Error", "Failed to add employee: " + e.getMessage());
                }
            });
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error showing add employee dialog");
        }
    }
    
    private void handleEditEmployee(EmployeeData employee) {
        try {
            Dialog<EmployeeData> dialog = createEmployeeDialog("Edit Employee", employee);
            
            Optional<EmployeeData> result = dialog.showAndWait();
            result.ifPresent(data -> {
                try {
                    // Only salary update is supported by DB_operation currently
                    if (DB_operation.updateEmployeeSalary(data.getId(), (int)data.getSalary())) {
                        showSuccess("Employee updated successfully!");
                        loadEmployees();
                    } else {
                        showError("Update Failed", "Failed to update employee");
                    }
                } catch (Exception e) {
                    ExceptionLogger.logException(e, "Error updating employee");
                }
            });
            
        } catch (Exception e) {
            ExceptionLogger.logException(e, "Error showing edit employee dialog");
        }
    }
    
    private void handleDeleteEmployee(EmployeeData employee) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Delete");
        alert.setHeaderText("Delete Employee");
        alert.setContentText("Are you sure you want to delete " + employee.getName() + "?");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    // Need to implement delete manually as DB_operation doesn't have it
                    String sql = "DELETE FROM employee WHERE Person_ID = ?";
                    try (Connection conn = DBConnection.getConnection();
                         PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, employee.getId());
                        int rows = ps.executeUpdate();
                        if (rows > 0) {
                            showSuccess("Employee deleted successfully");
                            loadEmployees();
                        } else {
                            showError("Delete Failed", "Failed to delete employee");
                        }
                    }
                } catch (SQLException e) {
                    ExceptionLogger.logException(e, "Error deleting employee");
                    showError("Delete Failed", "Cannot delete employee. They may have associated records.");
                }
            }
        });
    }
    
    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            employeesTable.setItems(employeesList);
        } else {
            ObservableList<EmployeeData> filtered = employeesList.filtered(emp -> 
                emp.getName().toLowerCase().contains(searchText) ||
                emp.getId().toLowerCase().contains(searchText) ||
                emp.getUsername().toLowerCase().contains(searchText)
            );
            employeesTable.setItems(filtered);
        }
        updateEmployeeCount();
    }
    
    @FXML
    private void handleRefresh() {
        searchField.clear();
        loadEmployees();
    }
    
    private void updateEmployeeCount() {
        employeeCountLabel.setText("Total: " + employeesTable.getItems().size() + " employees");
    }
    
    private Dialog<EmployeeData> createEmployeeDialog(String title, EmployeeData existing) {
        Dialog<EmployeeData> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(title);
        
        ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));
        
        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField phoneField = new TextField();
        TextField usernameField = new TextField();
        PasswordField passwordField = new PasswordField();
        TextField salaryField = new TextField();
        
        if (existing != null) {
            idField.setText(existing.getId());
            idField.setDisable(true);
            nameField.setText(existing.getName());
            nameField.setDisable(true); // Editing name not supported yet
            phoneField.setText(existing.getPhone());
            phoneField.setDisable(true);
            usernameField.setText(existing.getUsername());
            usernameField.setDisable(true);
            passwordField.setDisable(true);
            salaryField.setText(String.valueOf(existing.getSalary()));
        }
        
        grid.add(new Label("ID:"), 0, 0); grid.add(idField, 1, 0);
        grid.add(new Label("Name:"), 0, 1); grid.add(nameField, 1, 1);
        grid.add(new Label("Phone:"), 0, 2); grid.add(phoneField, 1, 2);
        grid.add(new Label("Username:"), 0, 3); grid.add(usernameField, 1, 3);
        if (existing == null) {
            grid.add(new Label("Password:"), 0, 4); grid.add(passwordField, 1, 4);
        }
        grid.add(new Label("Salary:"), 0, 5); grid.add(salaryField, 1, 5);
        
        dialog.getDialogPane().setContent(grid);
        
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                EmployeeData data = new EmployeeData();
                data.setId(idField.getText());
                data.setName(nameField.getText());
                data.setPhone(phoneField.getText());
                data.setUsername(usernameField.getText());
                data.setPassword(passwordField.getText());
                try {
                    data.setSalary(Double.parseDouble(salaryField.getText()));
                } catch (NumberFormatException e) {
                    data.setSalary(0);
                }
                return data;
            }
            return null;
        });
        
        return dialog;
    }
    
    private void showError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class EmployeeData {
        private SimpleStringProperty id = new SimpleStringProperty();
        private SimpleStringProperty name = new SimpleStringProperty();
        private SimpleStringProperty phone = new SimpleStringProperty();
        private SimpleStringProperty username = new SimpleStringProperty();
        private SimpleStringProperty role = new SimpleStringProperty();
        private SimpleDoubleProperty salary = new SimpleDoubleProperty();
        private SimpleStringProperty startDate = new SimpleStringProperty();
        private String password; // Not a property, just for transport
        
        public String getId() { return id.get(); }
        public void setId(String v) { id.set(v); }
        public SimpleStringProperty idProperty() { return id; }
        
        public String getName() { return name.get(); }
        public void setName(String v) { name.set(v); }
        public SimpleStringProperty nameProperty() { return name; }
        
        public String getPhone() { return phone.get(); }
        public void setPhone(String v) { phone.set(v); }
        public SimpleStringProperty phoneProperty() { return phone; }
        
        public String getUsername() { return username.get(); }
        public void setUsername(String v) { username.set(v); }
        public SimpleStringProperty usernameProperty() { return username; }
        
        public String getRole() { return role.get(); }
        public void setRole(String v) { role.set(v); }
        public SimpleStringProperty roleProperty() { return role; }
        
        public double getSalary() { return salary.get(); }
        public void setSalary(double v) { salary.set(v); }
        public SimpleDoubleProperty salaryProperty() { return salary; }
        
        public String getStartDate() { return startDate.get(); }
        public void setStartDate(String v) { startDate.set(v); }
        public SimpleStringProperty startDateProperty() { return startDate; }
        
        public String getPassword() { return password; }
        public void setPassword(String v) { password = v; }
    }
}
