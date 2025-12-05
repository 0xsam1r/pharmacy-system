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
        String sql = "INSERT INTO treasury (treasuryid, Bransh_ID, date_and_time, amount_of_money, invoice_ID) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, "TR-" + System.currentTimeMillis()); // Simple ID generation
            ps.setInt(2, 1); // Default Branch
            ps.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            ps.setDouble(4, amount);
            ps.setObject(5, null); // No invoice for manual
            
            ps.executeUpdate();
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error adding treasury transaction");
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
