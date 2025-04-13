package com.example.propertylistingmanagement;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.*;

public class RegistrationController {
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    @FXML
    private void handleRegistration() {
        // Получаем значения из полей ввода
        String username = usernameField.getText();  // Вот объявление username
        String password = passwordField.getText();  // Вот объявление password
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password cannot be empty");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO users (username, password) VALUES (?, ?)")) {

            // Параметры индексируются с 1
            stmt.setString(1, username);  // parameterIndex=1
            stmt.setString(2, password);  // parameterIndex=2
            stmt.executeUpdate();

            openMainWindow();

        } catch (SQLException e) {
            if (e.getSQLState().equals("23505")) {
                showError("Username already exists");
            } else {
                showError("Database error: " + e.getMessage());
            }
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private void openMainWindow() {
        try {
            usernameField.getScene().getWindow().hide();
            new MainWindow().show();
        } catch (Exception e) {
            showError("Failed to open application: " + e.getMessage());
        }
    }
}