package com.example.propertylistingmanagement;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SellerMainWindowController {
    @FXML private TableView<Property> propertyTable;
    @FXML private TableView<User> buyersTable;
    @FXML private AnchorPane propertiesPane;
    @FXML private AnchorPane buyersPane;
    @FXML private TextField typeField;
    @FXML private TextField squareFeetField;
    @FXML private TextField priceField;
    @FXML private TextField addressField;
    @FXML private ChoiceBox<String> statusChoiceBox;
    @FXML private Label sellerNameLabel;

    private int currentUserId;
    private File lastPropertyExportFile;
    private File lastBuyerExportFile;

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        loadData();
        loadSellerName();
    }

    @FXML
    private void initialize() {
        setupTables();
        propertyTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                typeField.setText(newSelection.getType());
                squareFeetField.setText(String.valueOf(newSelection.getSquareFeet()));
                priceField.setText(String.valueOf(newSelection.getPrice()));
                addressField.setText(newSelection.getAddress());
                statusChoiceBox.setValue(newSelection.getStatus());
            }
        });
    }

    private void setupTables() {
        propertyTable.getColumns().get(0).setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        propertyTable.getColumns().get(1).setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("type"));
        propertyTable.getColumns().get(2).setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("squareFeet"));
        propertyTable.getColumns().get(3).setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("price"));
        propertyTable.getColumns().get(4).setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("status"));
        propertyTable.getColumns().get(5).setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("address"));

        buyersTable.getColumns().get(0).setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("id"));
        buyersTable.getColumns().get(1).setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));
        buyersTable.getColumns().get(2).setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("surname"));
    }

    private void loadData() {
        List<Property> properties = loadProperties();
        propertyTable.getItems().setAll(properties);
        propertyTable.refresh();
        List<User> buyers = loadBuyers();
        buyersTable.getItems().setAll(buyers);
        buyersTable.refresh();
    }

    private void loadSellerName() {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT username FROM users WHERE id = ?")) {
            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                sellerNameLabel.setText(rs.getString("username"));
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load seller name: " + e.getMessage());
        }
    }

    public void loadDataManually() {
        loadData();
    }

    @FXML
    private void showPropertiesPane() {
        propertiesPane.setVisible(true);
        buyersPane.setVisible(false);
    }

    @FXML
    private void showBuyersPane() {
        propertiesPane.setVisible(false);
        buyersPane.setVisible(true);
    }

    @FXML
    private void addProperty() {
        String type = typeField.getText();
        String squareFeetText = squareFeetField.getText();
        String priceText = priceField.getText();
        String address = addressField.getText();
        String status = statusChoiceBox.getValue();

        if (type.isEmpty() || squareFeetText.isEmpty() || priceText.isEmpty() || address.isEmpty() || status == null) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        try {
            int squareFeet = Integer.parseInt(squareFeetText);
            double price = Double.parseDouble(priceText);

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "INSERT INTO properties (type, square_feet, price, address, status, seller_id) VALUES (?, ?, ?, ?, ?, ?)")) {

                stmt.setString(1, type);
                stmt.setInt(2, squareFeet);
                stmt.setDouble(3, price);
                stmt.setString(4, address);
                stmt.setString(5, status);
                stmt.setInt(6, currentUserId);
                stmt.executeUpdate();
                loadData();
                clearFields();
            } catch (SQLException e) {
                showAlert("Error", "Failed to add property: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for Square Feet and Price");
        }
    }

    @FXML
    private void updateProperty() {
        Property selectedProperty = propertyTable.getSelectionModel().getSelectedItem();
        if (selectedProperty == null) {
            showAlert("Error", "Please select a property to update");
            return;
        }

        String type = typeField.getText();
        String squareFeetText = squareFeetField.getText();
        String priceText = priceField.getText();
        String address = addressField.getText();
        String status = statusChoiceBox.getValue();

        if (type.isEmpty() || squareFeetText.isEmpty() || priceText.isEmpty() || address.isEmpty() || status == null) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        try {
            int squareFeet = Integer.parseInt(squareFeetText);
            double price = Double.parseDouble(priceText);

            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(
                         "UPDATE properties SET type = ?, square_feet = ?, price = ?, address = ?, status = ? WHERE id = ?")) {

                stmt.setString(1, type);
                stmt.setInt(2, squareFeet);
                stmt.setDouble(3, price);
                stmt.setString(4, address);
                stmt.setString(5, status);
                stmt.setInt(6, selectedProperty.getId());
                stmt.executeUpdate();
                loadData();
                clearFields();
                propertyTable.getSelectionModel().clearSelection();
            } catch (SQLException e) {
                showAlert("Error", "Failed to update property: " + e.getMessage());
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter valid numbers for Square Feet and Price");
        }
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
                    clearFields();
                    propertyTable.getSelectionModel().clearSelection();
                } catch (SQLException e) {
                    showAlert("Error", "Failed to delete property: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void clearFields() {
        typeField.clear();
        squareFeetField.clear();
        priceField.clear();
        addressField.clear();
        statusChoiceBox.setValue("For Sale");
        propertyTable.getSelectionModel().clearSelection();
    }

    @FXML
    private void logout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PropertyListing.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) propertyTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Login");
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Failed to logout: " + e.getMessage());
        }
    }

    @FXML
    private void exportPropertiesToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Properties CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        fileChooser.setInitialFileName("properties_export.csv");

        if (lastPropertyExportFile != null) {
            fileChooser.setInitialDirectory(lastPropertyExportFile);
        }

        File file = fileChooser.showSaveDialog(propertyTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        if (propertyTable.getItems().isEmpty()) {
            showAlert("Error", "No properties to export");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("ID,Type,Square Feet,Price,Status,Address\n");
            for (Property property : propertyTable.getItems()) {
                writer.write(String.format("%d,%s,%d,%.2f,%s,%s\n",
                        property.getId(),
                        property.getType(),
                        property.getSquareFeet(),
                        property.getPrice(),
                        property.getStatus(),
                        property.getAddress()));
            }
            writer.flush();
            showAlert("Success", "Properties exported successfully to:\n" + file.getAbsolutePath());
        } catch (IOException e) {
            showAlert("Error", "Failed to export properties: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void exportBuyersToCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Buyers CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

        fileChooser.setInitialFileName("buyers_export.csv");

        if (lastBuyerExportFile != null) {
            fileChooser.setInitialDirectory(lastBuyerExportFile);
        }

        File file = fileChooser.showSaveDialog(buyersTable.getScene().getWindow());
        if (file == null) {
            return;
        }

        if (buyersTable.getItems().isEmpty()) {
            showAlert("Error", "No buyers to export");
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("ID,Name,Surname\n");
            for (User buyer : buyersTable.getItems()) {
                writer.write(String.format("%d,%s,%s\n",
                        buyer.getId(),
                        buyer.getName(),
                        buyer.getSurname()));
            }
            writer.flush();
            showAlert("Success", "Buyers exported successfully to:\n" + file.getAbsolutePath());
        } catch (IOException e) {
            showAlert("Error", "Failed to export buyers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private List<Property> loadProperties() {
        List<Property> properties = new ArrayList<>();
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM properties WHERE seller_id = ?")) {

            stmt.setInt(1, currentUserId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Property property = new Property(
                        rs.getInt("id"),
                        rs.getString("type"),
                        rs.getInt("square_feet"),
                        rs.getDouble("price"),
                        rs.getString("address"),
                        rs.getInt("seller_id"),
                        rs.getString("status")
                );
                properties.add(property);
            }
        } catch (SQLException e) {
            showAlert("Error", "Failed to load properties: " + e.getMessage());
            e.printStackTrace();
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
}