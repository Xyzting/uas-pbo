package com.example.controller;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class LoginController {
    
    @FXML
    private Button loginUserButton;
    
    @FXML
    private Button loginAdminButton;
    
    /**
     * Handler untuk button Login User
     */
    @FXML
    private void handleLoginUser(ActionEvent event) {
        try {
            // Load halaman login user
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/loginUser.fxml"));
            Parent root = loader.load();
            
            // Get stage dari event
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Set scene baru
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login User - Sistem Reservasi Hotel");
            stage.show();
            
            System.out.println("Navigasi ke Login User");
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading loginUser.fxml: " + e.getMessage());
        }
    }
    
    /**
     * Handler untuk button Login Admin
     */
    @FXML
    private void handleLoginAdmin(ActionEvent event) {
        try {
            // Load halaman login admin
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/loginAdmin.fxml"));
            Parent root = loader.load();
            
            // Get stage dari event
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Set scene baru
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login Admin - Sistem Reservasi Hotel");
            stage.show();
            
            System.out.println("Navigasi ke Login Admin");
            
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error loading loginAdmin.fxml: " + e.getMessage());
        }
    }
    
    /**
     * Method initialize - dipanggil otomatis saat FXML di-load
     */
    @FXML
    private void initialize() {
        System.out.println("Login page initialized");
        
        // Setup hover effect untuk buttons
        setupButtonHoverEffect();
    }
    
    /**
     * Setup hover effect untuk buttons
     */
    private void setupButtonHoverEffect() {
        // Hover effect untuk button User
        loginUserButton.setOnMouseEntered(e -> {
            loginUserButton.setStyle(
                "-fx-background-color: #2980b9; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
        });
        
        loginUserButton.setOnMouseExited(e -> {
            loginUserButton.setStyle(
                "-fx-background-color: #3498db; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
        });
        
        // Hover effect untuk button Admin
        loginAdminButton.setOnMouseEntered(e -> {
            loginAdminButton.setStyle(
                "-fx-background-color: #27ae60; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
        });
        
        loginAdminButton.setOnMouseExited(e -> {
            loginAdminButton.setStyle(
                "-fx-background-color: #2ecc71; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 14px; " +
                "-fx-font-weight: bold; " +
                "-fx-background-radius: 8; " +
                "-fx-cursor: hand;"
            );
        });
    }
}