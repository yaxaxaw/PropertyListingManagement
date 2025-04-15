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
    private File lastPropertyExportFile; // Для запоминания последнего пути экспорта свойств
    private File lastBuyerExportFile; // Для запоминания последнего пути экспорта покупателей

    public void setCurrentUserId(int userId) {
        this.currentUserId = userId;
        System.out.println("Setting currentUserId to: " + userId);
        loadData();
        loadSellerName();
    }

    @FXML
    private void initialize() {
        setupTables();
        System.out.println("Properties Pane: " + (propertiesPane != null ? "Initialized" : "Not Initialized"));
        System.out.println("Buyers Pane: " + (buyersPane != null ? "Initialized" : "Not Initialized"));

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
        // Убираем Platform.runLater() и загружаем данные синхронно
        List<Property> properties = loadProperties();
        propertyTable.getItems().setAll(properties);
        System.out.println("Loaded " + properties.size() + " properties into table");
        propertyTable.refresh();
        List<User> buyers = loadBuyers();
        buyersTable.getItems().setAll(buyers);
        System.out.println("Loaded " + buyers.size() + " buyers into table");
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
        File file;
        if (lastPropertyExportFile == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Properties CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            file = fileChooser.showSaveDialog(propertyTable.getScene().getWindow());
            if (file != null) {
                lastPropertyExportFile = file;
            }
        } else {
            file = lastPropertyExportFile;
        }

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ID;Type;Square Feet;Price;Status;Address\n");
                List<Property> properties = propertyTable.getItems();
                System.out.println("Exporting " + properties.size() + " properties to CSV");
                for (Property property : properties) {
                    System.out.println("Writing property: ID=" + property.getId() + ", Type=" + property.getType());
                    writer.write(String.format("%d;%s;%d;%.2f;%s;%s\n",
                            property.getId(),
                            property.getType(),
                            property.getSquareFeet(),
                            property.getPrice(),
                            property.getStatus(),
                            property.getAddress()));
                }
                writer.flush(); // Убедимся, что данные записаны
                showAlert("Success", "Properties exported to " + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Error", "Failed to export properties: " + e.getMessage());
                e.printStackTrace(); // Выводим стек ошибки в консоль для отладки
            }
        }
    }

    @FXML
    private void exportBuyersToCSV() {
        File file;
        if (lastBuyerExportFile == null) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Buyers CSV");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            file = fileChooser.showSaveDialog(buyersTable.getScene().getWindow());
            if (file != null) {
                lastBuyerExportFile = file;
            }
        } else {
            file = lastBuyerExportFile;
        }

        if (file != null) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ID;Name;Surname\n");
                List<User> buyers = buyersTable.getItems();
                System.out.println("Exporting " + buyers.size() + " buyers to CSV");
                for (User buyer : buyers) {
                    System.out.println("Writing buyer: ID=" + buyer.getId() + ", Name=" + buyer.getName());
                    writer.write(String.format("%d;%s;%s\n",
                            buyer.getId(),
                            buyer.getName(),
                            buyer.getSurname()));
                }
                writer.flush();
                showAlert("Success", "Buyers exported to " + file.getAbsolutePath());
            } catch (IOException e) {
                showAlert("Error", "Failed to export buyers: " + e.getMessage());
                e.printStackTrace();
            }
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

            System.out.println("Current User ID: " + currentUserId);
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
                System.out.println("Loaded Property: " + property.getId() + ", Seller ID: " + property.getSellerId());
            }
            if (properties.isEmpty()) {
                System.out.println("No properties found for seller_id: " + currentUserId);
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