package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;

public class KaryawanController {
    @FXML
    private ComboBox<String> jabatanComboBox;
    @FXML
    private ComboBox<String> shiftComboBox;

    @FXML
    public void initialize() {
        jabatanComboBox.getItems().addAll(
            "Manager", "Resepsionis", "Housekeeping", 
            "Keamanan", "Maintenance", "Chef", "Waiter"
        );
        
        shiftComboBox.getItems().addAll(
            "Pagi (07:00 - 15:00)",
            "Siang (15:00 - 23:00)",
            "Malam (23:00 - 07:00)"
        );
    }
}
