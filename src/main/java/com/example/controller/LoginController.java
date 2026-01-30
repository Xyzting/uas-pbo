package com.example.controller;

import java.util.List;

import com.example.App;
import com.example.model.Karyawan;
import com.example.utils.FileUtil;
import com.example.utils.SessionManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Button loginButton;

    private static final String KARYAWAN_FILE = "karyawan.csv";
    private static final String CSS_PATH = "/css/app.css";

    @FXML
    public void initialize() {
        loginButton.setOnAction(e -> handleLogin());

        // Enter key on password field triggers login
        passwordField.setOnAction(e -> handleLogin());

        // Enter key on username field moves to password
        usernameField.setOnAction(e -> passwordField.requestFocus());
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Input tidak lengkap!",
                    "Username dan password harus diisi!");
            return;
        }

        List<Karyawan> karyawanList = FileUtil.load(KARYAWAN_FILE, Karyawan::fromCSV);

        Karyawan found = null;
        for (Karyawan k : karyawanList) {
            if (k.getNama().equalsIgnoreCase(username) || k.getId().equalsIgnoreCase(username)) {
                found = k;
                break;
            }
        }

        if (found == null) {
            showAlert(Alert.AlertType.ERROR, "Login Gagal",
                    "Username tidak ditemukan!");
            return;
        }

        if (!found.verifyPassword(password)) {
            showAlert(Alert.AlertType.ERROR, "Login Gagal",
                    "Password salah!");
            return;
        }

        // Login berhasil - set session
        SessionManager.getInstance().login(found);

        try {
            String fxmlPath;
            if (found.getRole().equals("MANAGER")) {
                fxmlPath = "AdminView.fxml";
            } else {
                fxmlPath = "KasirView.fxml";
            }

            Parent root = FXMLLoader.load(App.class.getResource(fxmlPath));
            Stage stage = (Stage) loginButton.getScene().getWindow();

            // Apply CSS
            try {
                root.getStylesheets().add(
                        App.class.getResource(CSS_PATH).toExternalForm()
                );
            } catch (Exception cssEx) {
                // Try alternate path
                try {
                    root.getStylesheets().add(
                            App.class.getResource(CSS_PATH).toExternalForm()
                    );
                } catch (Exception e2) {
                    // ignore
                }
            }

            stage.setScene(new Scene(root));
            stage.setMaximized(true);
            stage.setTitle("Hotel Management - " + found.getNama());

            showAlert(Alert.AlertType.INFORMATION, "Login Berhasil",
                    "Selamat datang, " + found.getNama() + "!");

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
