package com.example.propertylistingmanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;

public class PropertyListingController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;

    private static int currentUserId;
    private boolean isLoggingIn = false;

    @FXML
    private void initialize() {
        DatabaseConnection.initializeDatabase();
        roleComboBox.getItems().clear();
        roleComboBox.getItems().addAll("Buyer", "Seller");
        roleComboBox.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleLogin() {

        if (isLoggingIn) return; // Защита от двойного вызова
        isLoggingIn = true;

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String selectedRole = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert("Error", "Please enter both username and password");
            return;
        }
        if (selectedRole == null) {
            showAlert("Error", "Please select a role");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT id, password, role FROM users WHERE username = ?")) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("id");
                String storedPassword = rs.getString("password");
                String userRole = rs.getString("role").toLowerCase();

                if (!password.equals(storedPassword)) {
                    showAlert("Error", "Invalid password");
                    return;
                }

                if (!selectedRole.equalsIgnoreCase(userRole)) {
                    showAlert("Error", "You don't have permissions for this role. Your role: " + userRole);
                    return;
                }

                Stage currentStage = (Stage) usernameField.getScene().getWindow();
                if ("seller".equalsIgnoreCase(userRole)) {
                    SellerMainWindow sellerWindow = new SellerMainWindow(currentUserId);
                    sellerWindow.show();
                } else {
                    BuyerMainWindow buyerWindow = new BuyerMainWindow(currentUserId);
                    buyerWindow.show();
                }
                currentStage.close();

            } else {
                showAlert("Error", "User not found");
            }
        } catch (SQLException e) {
            showAlert("Database Error", "Failed to login: " + e.getMessage());
            e.printStackTrace();
        } finally {
            isLoggingIn = false; // Сбрасываем флаг
        }
    }

    @FXML
    private void showRegistrationWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Registration.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registration");
            stage.setScene(new Scene(root, 300, 400));
            stage.show();
        } catch (IOException e) {
            showAlert("Error", "Cannot open registration window: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static int getCurrentUserId() {
        return currentUserId;
    }
}