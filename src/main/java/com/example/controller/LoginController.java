package com.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    private Hyperlink forgotPasswordLink;

    @FXML
    public void initialize() {
        // Setup event handlers
        setupEnterKeyLogin();

        // Setup hyperlink actions
        forgotPasswordLink.setOnAction(e -> handleForgotPassword());

        System.out.println("LoginController initialized");
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        // Reset error message
        hideError();

        // Validasi input
        if (username.isEmpty() || password.isEmpty()) {
            showError("Username dan password tidak boleh kosong!");
            return;
        }

        // Login logic (ganti dengan logika database Anda)
        if (validateLogin(username, password)) {
            // Login berhasil
            System.out.println("Login berhasil!");

            // Pindah ke halaman utama
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/main.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) loginButton.getScene().getWindow();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                stage.setTitle("Hotel Management System");
                stage.centerOnScreen();
                stage.show();

            } catch (Exception e) {
                e.printStackTrace();
                showError("Gagal memuat halaman utama!");
            }

        } else {
            // Login gagal
            showError("Username atau password salah!");
            passwordField.clear();
            passwordField.requestFocus();
        }
    }

    private boolean validateLogin(String username, String password) {
        // TODO: Implementasi validasi dengan database
        // Sementara hardcode untuk testing
        return username.equals("admin") && password.equals("admin123");
    }

    private void handleForgotPassword() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Lupa Password");
        alert.setHeaderText("Reset Password");
        alert.setContentText("Silakan hubungi administrator untuk reset password.");
        alert.showAndWait();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        errorLabel.setManaged(true);
    }

    private void hideError() {
        errorLabel.setText("");
        errorLabel.setVisible(false);
        errorLabel.setManaged(false);
    }

    private void setupEnterKeyLogin() {
        // Login saat tekan Enter di password field
        passwordField.setOnAction(e -> handleLogin());
        usernameField.setOnAction(e -> passwordField.requestFocus());
    }
}
