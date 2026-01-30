module com.example {
    requires javafx.controls;
    requires javafx.fxml;
    
    // BCrypt untuk password hashing
    requires jbcrypt;
    
    // Opens untuk JavaFX FXML
    opens com.example to javafx.fxml;
    opens com.example.controller to javafx.fxml;
    opens com.example.model to javafx.base; 

    // Exports
    exports com.example;
    exports com.example.controller;
    exports com.example.model;
    exports com.example.utils;
}
