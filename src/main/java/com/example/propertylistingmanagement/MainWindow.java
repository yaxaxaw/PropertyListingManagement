package com.example.propertylistingmanagement;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindow {
    public void show() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MainWindow.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Property Management System");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();

        } catch (Exception e) {
            System.err.println("Error opening MainWindow: " + e.getMessage());
            e.printStackTrace();
        }
    }
}