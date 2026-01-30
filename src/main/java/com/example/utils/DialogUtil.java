package com.example.utils;

import java.util.Optional;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;

/**
 * Utility class untuk menampilkan dialog dengan style yang konsisten
 */
public class DialogUtil {

    private static final String CSS_PATH = "/com/example/view/app.css";

    /**
     * Apply stylesheet ke dialog
     */
    public static void applyStyle(Dialog<?> dialog) {
        try {
            dialog.getDialogPane().getStylesheets().add(
                    DialogUtil.class.getResource(CSS_PATH).toExternalForm()
            );
        } catch (Exception e) {
            // CSS not found, ignore
            System.err.println("Warning: CSS file not found at " + CSS_PATH);
        }
    }

    /**
     * Apply stylesheet ke alert
     */
    public static void applyStyle(Alert alert) {
        try {
            alert.getDialogPane().getStylesheets().add(
                    DialogUtil.class.getResource(CSS_PATH).toExternalForm()
            );
        } catch (Exception e) {
            System.err.println("Warning: CSS file not found at " + CSS_PATH);
        }
    }

    /**
     * Tampilkan alert INFO
     */
    public static void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyStyle(alert);
        alert.showAndWait();
    }

    /**
     * Tampilkan alert WARNING
     */
    public static void showWarning(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyStyle(alert);
        alert.showAndWait();
    }

    /**
     * Tampilkan alert ERROR
     */
    public static void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyStyle(alert);
        alert.showAndWait();
    }

    /**
     * Tampilkan alert CONFIRMATION dan return hasilnya
     */
    public static boolean showConfirm(String title, String header, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        applyStyle(alert);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    /**
     * Tampilkan alert CONFIRMATION tanpa header
     */
    public static boolean showConfirm(String title, String message) {
        return showConfirm(title, null, message);
    }

    /**
     * Tampilkan alert dengan type custom
     */
    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        applyStyle(alert);
        alert.showAndWait();
    }

    /**
     * Tampilkan alert dengan header
     */
    public static void showAlert(Alert.AlertType type, String title, String header, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(message);
        applyStyle(alert);
        alert.showAndWait();
    }
}
