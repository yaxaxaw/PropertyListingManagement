package com.example.propertylistingmanagement;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class BuyerMainWindow {
    private static Stage stage;
    private int userId;

    public BuyerMainWindow(int userId) {
        this.userId = userId;
    }

    public void show() {
        try {

            if (stage != null && stage.isShowing()) {
                stage.requestFocus();
                return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource("BuyerMainWindow.fxml"));
            Parent root = loader.load();

            BuyerMainWindowController controller = loader.getController();
            controller.setCurrentUserId(userId);

            stage = new Stage();
            stage.setTitle("Buyer Dashboard");
            stage.setScene(new Scene(root, 800, 600));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}