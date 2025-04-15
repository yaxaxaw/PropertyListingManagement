package com.example.propertylistingmanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BuyerMainWindowController {
    @FXML private TableView<Property> propertyTable;
    @FXML private Label buyerNameLabel;
    private int currentUserId;

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadProperties();
        loadBuyerName();
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
        propertyTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("status"));
        propertyTable.getColumns().get(5).setCellValueFactory(new PropertyValueFactory<>("address"));
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
                        rs.getString("address"),
                        rs.getInt("seller_id"),
                        rs.getString("status")
                ));
            }
            propertyTable.getItems().setAll(properties);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load properties: " + e.getMessage());
        }
    }

    private void loadBuyerName() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT username FROM users WHERE id = ?")) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                buyerNameLabel.setText(rs.getString("username"));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load buyer name: " + e.getMessage());
        }
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PropertyListing.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) buyerNameLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}