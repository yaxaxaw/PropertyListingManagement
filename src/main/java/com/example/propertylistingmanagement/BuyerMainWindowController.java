package com.example.propertylistingmanagement;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuyerMainWindowController {
    @FXML private TableView<Property> propertyTable;
    private int currentUserId;

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadProperties();
    }

    @FXML
    private void initialize() {
        setupTables();
    }

    private void setupTables() {
        propertyTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        propertyTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("type"));
        propertyTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("squareFeet"));
        propertyTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("price"));
        propertyTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("address"));
    }

    private void loadProperties() {
        List<Property> properties = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM properties")) {

            while (rs.next()) {
                properties.add(new Property(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getInt("square_feet"),
                        rs.getDouble("price"),
                        rs.getString("address")
                ));
            }
            propertyTable.getItems().setAll(properties);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}