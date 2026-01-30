package com.example.controller;

import com.example.App;
import com.example.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class AdminViewController {

    @FXML
    private Button logoutButton;

    private static final String CSS_PATH = "/css/app.css";

    @FXML
    public void initialize() {
        logoutButton.setOnAction(e -> handleLogout());
    }

    @FXML
    private void handleLogout() {
        SessionManager.getInstance().logout();

        try {

            Parent root = FXMLLoader.load(App.class.getResource("login.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            root.getStylesheets().add(
                    App.class.getResource(CSS_PATH).toExternalForm()
            );
            stage.setScene(new Scene(root));
            showAlert(Alert.AlertType.INFORMATION, "Logout Berhasil",
                    "ByeBye!");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Gagal membuka halaman: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        try {
            alert.getDialogPane().getStylesheets().add(
                    App.class.getResource(CSS_PATH).toExternalForm()
            );
        } catch (Exception e) {
            try {
                alert.getDialogPane().getStylesheets().add(
                        App.class.getResource(CSS_PATH).toExternalForm()
                );
            } catch (Exception e2) {
            }
        }

        alert.showAndWait();
    }
}
