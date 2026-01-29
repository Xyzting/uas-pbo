package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class ReservasiController {

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll(
                "Menunggu", "Dikonfirmasi", "Check-in",
                "Check-out", "Dibatalkan"
        );
    }
}
