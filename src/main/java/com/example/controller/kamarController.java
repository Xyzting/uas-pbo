package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class KamarController {

    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    public void initialize() {
        statusComboBox.getItems().addAll("Tersedia", "Terisi", "Maintenance");
    }
}
