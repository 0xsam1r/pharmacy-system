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
import java.util.ResourceBundle;

public class BatchController implements Initializable {

    @FXML private TextField searchField;
    @FXML private TableView<BatchData> batchTable;
    @FXML private TableColumn<BatchData, String> colProduct;
    @FXML private TableColumn<BatchData, String> colBatch;
    @FXML private TableColumn<BatchData, String> colExpire;
    @FXML private TableColumn<BatchData, Integer> colQty;
    @FXML private TableColumn<BatchData, Double> colCost;

    private ObservableList<BatchData> batchList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTable();
        loadBatches();
    }

    private void setupTable() {
        colProduct.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colBatch.setCellValueFactory(new PropertyValueFactory<>("batchNumber"));
        colExpire.setCellValueFactory(new PropertyValueFactory<>("expireDate"));
        colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colCost.setCellValueFactory(new PropertyValueFactory<>("cost"));
    }

    private void loadBatches() {
        batchList.clear();
        String sql = "SELECT b.Batch_number, b.cost, b.expire_date, b.Quantaty, p.Name " +
                     "FROM batch b JOIN product p ON b.Product_parcode = p.parcode";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                batchList.add(new BatchData(
                        rs.getString("Name"),
                        rs.getString("Batch_number"),
                        rs.getString("expire_date"),
                        rs.getInt("Quantaty"),
                        rs.getDouble("cost")
                ));
            }
            batchTable.setItems(batchList);
        } catch (SQLException e) {
            ExceptionLogger.logException(e, "Error loading batches");
        }
    }

    @FXML
    private void handleSearch() {
        String query = searchField.getText().toLowerCase();
        if (query.isEmpty()) {
            batchTable.setItems(batchList);
        } else {
            ObservableList<BatchData> filtered = batchList.filtered(b -> 
                b.getProductName().toLowerCase().contains(query) || 
                b.getBatchNumber().toLowerCase().contains(query)
            );
            batchTable.setItems(filtered);
        }
    }

    public static class BatchData {
        private SimpleStringProperty productName;
        private SimpleStringProperty batchNumber;
        private SimpleStringProperty expireDate;
        private SimpleIntegerProperty quantity;
        private SimpleDoubleProperty cost;

        public BatchData(String prod, String batch, String exp, int qty, double cost) {
            this.productName = new SimpleStringProperty(prod);
            this.batchNumber = new SimpleStringProperty(batch);
            this.expireDate = new SimpleStringProperty(exp);
            this.quantity = new SimpleIntegerProperty(qty);
            this.cost = new SimpleDoubleProperty(cost);
        }

        public String getProductName() { return productName.get(); }
        public String getBatchNumber() { return batchNumber.get(); }
        public String getExpireDate() { return expireDate.get(); }
        public int getQuantity() { return quantity.get(); }
        public double getCost() { return cost.get(); }
    }
}
