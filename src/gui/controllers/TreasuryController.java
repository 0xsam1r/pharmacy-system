package gui.controllers;

import DB.DBConnection;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import util.ExceptionLogger;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class TreasuryController implements Initializable {

    @FXML private Label balanceLabel;
    @FXML private TableView<TreasuryTransaction> treasuryTable;
    @FXML private TableColumn<TreasuryTransaction, String> colId;
    @FXML private TableColumn<TreasuryTransaction, String> colDate;
    @FXML private TableColumn<TreasuryTransaction, Double> colAmount;
    @FXML private TableColumn<TreasuryTransaction, Integer> colInvoiceId;
    @FXML private TextField searchField;
    @FXML private DatePicker fromDate;
    @FXML private DatePicker toDate;

    private ObservableList<TreasuryTransaction> transactionList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadTreasuryData(null, null);
        calculateBalance();
    }

    private void setupTable() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDate.setCellValueFactory(new PropertyValueFactory<>("date"));
        colAmount.setCellValueFactory(new PropertyValueFactory<>("amount"));
        colInvoiceId.setCellValueFactory(new PropertyValueFactory<>("invoiceId"));
        
        treasuryTable.setItems(transactionList);
    }

    private void loadTreasuryData(LocalDate from, LocalDate to) {
        transactionList.clear();
        StringBuilder sql = new StringBuilder("SELECT * FROM treasury WHERE 1=1");
        
        if (from != null) {
            sql.append(" AND DATE(date_and_time) >= ?");
        }
        if (to != null) {
            sql.append(" AND DATE(date_and_time) <= ?");
        }
        sql.append(" ORDER BY date_and_time DESC");
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            
            int paramIndex = 1;
            if (from != null) {
                ps.setDate(paramIndex++, java.sql.Date.valueOf(from));
            }
            if (to != null) {
                ps.setDate(paramIndex++, java.sql.Date.valueOf(to));
            }
            
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                transactionList.add(new TreasuryTransaction(
                        rs.getString("treasuryid"),
                        rs.getString("date_and_time"),
                        rs.getDouble("amount_of_money"),
                        rs.getInt("invoice_ID")
                ));
            }
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error loading treasury data");
        }
    }

    private void calculateBalance() {
        double balance = transactionList.stream().mapToDouble(TreasuryTransaction::getAmount).sum();
        balanceLabel.setText(String.format("$%.2f", balance));
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            treasuryTable.setItems(transactionList);
        } else {
            ObservableList<TreasuryTransaction> filtered = transactionList.filtered(t -> 
                t.getId().toLowerCase().contains(query) || 
                String.valueOf(t.getInvoiceId()).contains(query)
            );
            treasuryTable.setItems(filtered);
        }
    }

    @FXML
    private void handleDateSearch() {
        LocalDate from = fromDate.getValue();
        LocalDate to = toDate.getValue();
        loadTreasuryData(from, to);
        calculateBalance();
    }

    @FXML
    private void handleClearDates() {
        fromDate.setValue(null);
        toDate.setValue(null);
        loadTreasuryData(null, null);
        calculateBalance();
    }

    @FXML
    private void handleAddTransaction() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Manual Transaction");
        dialog.setHeaderText("Add Manual Transaction");
        dialog.setContentText("Enter Amount (positive for income, negative for expense):");

        dialog.showAndWait().ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                addTransactionToDB(amount);
                loadTreasuryData(fromDate.getValue(), toDate.getValue());
                calculateBalance();
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Invalid Amount");
                alert.show();
            }
        });
    }

    private void addTransactionToDB(double amount) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false); // Start Transaction

            // 1. Gather Required Data (Auto-Fill Missing Values)
            String username = util.SessionManager.getInstance().getUsername();
            String userId = util.SessionManager.getInstance().getUserId();
            int branchId = util.SessionManager.getInstance().getBranchId(); // Assuming added in previous steps
            
            // Fallbacks if session is empty (e.g. during dev/testing)
            if (username == null) username = "admin";
            if (userId == null) {
                // Try to find a valid Person_ID for "admin" or first available employee
                try (java.sql.Statement st = conn.createStatement();
                     ResultSet rs = st.executeQuery("SELECT Person_ID FROM employee LIMIT 1")) {
                     if (rs.next()) userId = rs.getString(1);
                     else userId = "1"; // Ultimate fallback, might fail FK
                }
            }
            if (branchId == 0) branchId = 1;

            // 2. Generate New Invoice ID
            int invoiceId = 1;
            try (java.sql.Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(ID) FROM invoice")) {
                if (rs.next()) invoiceId = rs.getInt(1) + 1;
            }
            // Ensure unique range for manual transactions if needed? 
            // Just incrementing MAX is fine for now, assuming single-threaded usage or DB lock.

            // 3. Create Supporting Invoice Record (Required by FK constraint)
            String sqlInv = "INSERT INTO invoice (ID, date, price, employee_User_name, employee_Person_ID, employee_bransh_ID) VALUES (?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psInv = conn.prepareStatement(sqlInv)) {
                psInv.setInt(1, invoiceId);
                psInv.setDate(2, java.sql.Date.valueOf(LocalDate.now()));
                psInv.setDouble(3, amount); // The amount of the manual transaction
                psInv.setString(4, username);
                psInv.setString(5, userId);
                psInv.setInt(6, branchId);
                psInv.executeUpdate();
            }

            // 4. Insert Treasury Record
            String sqlTreasury = "INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(sqlTreasury)) {
                
                String treasuryId = "TR-MAN-" + System.currentTimeMillis(); // Distinct ID for manual
                
                ps.setString(1, treasuryId);
                ps.setInt(2, branchId);
                ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
                ps.setDouble(4, amount);
                ps.setInt(5, invoiceId); // Linked to the manual invoice created above
                
                ps.executeUpdate();
            }

            conn.commit();
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Transaction Added Successfully!");
            alert.show();

        } catch (SQLException e) {
            if (conn != null) try { conn.rollback(); } catch (SQLException ex) {}
            ExceptionLogger.logException(e, "Error adding treasury transaction");
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to add transaction: " + e.getMessage());
            alert.show();
        } finally {
            if (conn != null) try { conn.setAutoCommit(true); conn.close(); } catch (SQLException e) {}
        }
    }

    public static class TreasuryTransaction {
        private SimpleStringProperty id;
        private SimpleStringProperty date;
        private SimpleDoubleProperty amount;
        private SimpleIntegerProperty invoiceId;

        public TreasuryTransaction(String id, String date, double amount, int invoiceId) {
            this.id = new SimpleStringProperty(id);
            this.date = new SimpleStringProperty(date);
            this.amount = new SimpleDoubleProperty(amount);
            this.invoiceId = new SimpleIntegerProperty(invoiceId);
        }

        public String getId() { return id.get(); }
        public String getDate() { return date.get(); }
        public double getAmount() { return amount.get(); }
        public int getInvoiceId() { return invoiceId.get(); }
    }
}
