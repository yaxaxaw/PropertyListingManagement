package com.example.propertylistingmanagement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PropertyListingController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void initialize() {
        DatabaseConnection.initializeDatabase();
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT password FROM users WHERE username = ?")) {

            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedPassword = rs.getString("password");
                // Прямое сравнение паролей без хеширования
                if (password.equals(storedPassword)) {
                    new MainWindow().show();
                    ((Stage) usernameField.getScene().getWindow()).close();
                } else {
                    showAlert("Error", "Invalid password");
                }
            } else {
                showAlert("Error", "User not found");
            }
        } catch (SQLException e) {
            showAlert("Database Error", e.getMessage());
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

    private void openMainWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Property Management System");
            stage.setScene(new Scene(root, 800, 600));

            ((Stage) usernameField.getScene().getWindow()).close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
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