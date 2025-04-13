package com.example.propertylistingmanagement;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SellerMainWindowController {
    @FXML private TableView<Property> propertyTable;
    @FXML private TableView<User> buyersTable;

    private int currentUserId;

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
    }

    @FXML
    private void initialize() {
        setupTables();
        if (currentUserId != 0) {
            loadData();
        }
    }

    private void setupTables() {
        // Таблица свойств
        propertyTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        propertyTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("type"));
        propertyTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("squareFeet"));
        propertyTable.getColumns().get(3).setCellValueFactory(new PropertyValueFactory<>("price"));
        propertyTable.getColumns().get(4).setCellValueFactory(new PropertyValueFactory<>("address"));

        // Таблица покупателей
        buyersTable.getColumns().get(0).setCellValueFactory(new PropertyValueFactory<>("id"));
        buyersTable.getColumns().get(1).setCellValueFactory(new PropertyValueFactory<>("name"));
        buyersTable.getColumns().get(2).setCellValueFactory(new PropertyValueFactory<>("surname"));
    }

    private void loadData() {
        propertyTable.getItems().setAll(loadProperties());
        propertyTable.refresh();
        buyersTable.getItems().setAll(loadBuyers());
        buyersTable.refresh();
    }

    @FXML
    private void addProperty() {
        Dialog<Property> dialog = new Dialog<>();
        dialog.setTitle("Add New Property");
        dialog.setHeaderText("Enter property details");

        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField typeField = new TextField();
        TextField squareFeetField = new TextField();
        TextField priceField = new TextField();
        TextField addressField = new TextField();

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeField, 1, 0);
        grid.add(new Label("Square Feet:"), 0, 1);
        grid.add(squareFeetField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                try {
                    return new Property(
                            0,
                            typeField.getText(),
                            Integer.parseInt(squareFeetField.getText()),
                            Double.parseDouble(priceField.getText()),
                            addressField.getText(),
                            currentUserId
                    );
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter valid numbers");
                    return null;
                }
            }
            return null;
        });

        Optional<Property> result = dialog.showAndWait();
        result.ifPresent(property -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO properties (type, square_feet, price, address, seller_id) VALUES (?, ?, ?, ?, ?)")) {

                stmt.setString(1, property.getType());
                stmt.setInt(2, property.getSquareFeet());
                stmt.setDouble(3, property.getPrice());
                stmt.setString(4, property.getAddress());
                stmt.setInt(5, property.getSellerId());
                stmt.executeUpdate();
                loadData();
            } catch (SQLException e) {
                showAlert("Error", "Failed to add property: " + e.getMessage());
            }
        });
    }

    @FXML
    private void updateProperty() {
        Property selectedProperty = propertyTable.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            showAlert("Error", "Please select a property to update");
            return;
        }

        Dialog<Property> dialog = new Dialog<>();
        dialog.setTitle("Update Property");
        dialog.setHeaderText("Update property details");

        ButtonType updateButtonType = new ButtonType("Update", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(updateButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField typeField = new TextField(selectedProperty.getType());
        TextField squareFeetField = new TextField(String.valueOf(selectedProperty.getSquareFeet()));
        TextField priceField = new TextField(String.valueOf(selectedProperty.getPrice()));
        TextField addressField = new TextField(selectedProperty.getAddress());

        grid.add(new Label("Type:"), 0, 0);
        grid.add(typeField, 1, 0);
        grid.add(new Label("Square Feet:"), 0, 1);
        grid.add(squareFeetField, 1, 1);
        grid.add(new Label("Price:"), 0, 2);
        grid.add(priceField, 1, 2);
        grid.add(new Label("Address:"), 0, 3);
        grid.add(addressField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == updateButtonType) {
                try {
                    return new Property(
                            selectedProperty.getId(),
                            typeField.getText(),
                            Integer.parseInt(squareFeetField.getText()),
                            Double.parseDouble(priceField.getText()),
                            addressField.getText(),
                            currentUserId
                    );
                } catch (NumberFormatException e) {
                    showAlert("Error", "Please enter valid numbers");
                    return null;
                }
            }
            return null;
        });

        Optional<Property> result = dialog.showAndWait();
        result.ifPresent(property -> {
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE properties SET type = ?, square_feet = ?, price = ?, address = ? WHERE id = ?")) {

                stmt.setString(1, property.getType());
                stmt.setInt(2, property.getSquareFeet());
                stmt.setDouble(3, property.getPrice());
                stmt.setString(4, property.getAddress());
                stmt.setInt(5, property.getId());
                stmt.executeUpdate();
                loadData();
            } catch (SQLException e) {
                showAlert("Error", "Failed to update property: " + e.getMessage());
            }
        });
    }

    @FXML
    private void deleteProperty() {
        Property selectedProperty = propertyTable.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            showAlert("Error", "Please select a property to delete");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirm Deletion");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Are you sure you want to delete this property?");
        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try (Connection conn = DatabaseConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("DELETE FROM properties WHERE id = ?")) {

                    stmt.setInt(1, selectedProperty.getId());
                    stmt.executeUpdate();
                    loadData();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete property: " + e.getMessage());
                }
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private List<Property> loadProperties() {
        List<Property> properties = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM properties WHERE seller_id = ?")) {

            System.out.println("Current User ID: " + currentUserId);
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                properties.add(new Property(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getInt("square_feet"),
                        rs.getDouble("price"),
                        rs.getString("address"),
                        rs.getInt("seller_id")
                ));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load properties: " + e.getMessage());
        }
        return properties;
    }

    private List<User> loadBuyers() {
        List<User> buyers = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, surname FROM users WHERE role = 'buyer'")) {

            while (rs.next()) {
                buyers.add(new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("surname")
                ));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load buyers: " + e.getMessage());
        }
        return buyers;
    }
    public void loadDataManually() {
        loadData();
    }
}