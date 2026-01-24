package com.example.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class TransaksiController {
    
    @FXML
    private TableView<?> transaksiTableView;
    
    @FXML
    private TableColumn<?, ?> idTransaksiColumn;
    @FXML
    private TableColumn<?, ?> reservasiColumn;
    @FXML
    private TableColumn<?, ?> pelangganColumn;
    @FXML
    private TableColumn<?, ?> kamarColumn;
    @FXML
    private TableColumn<?, ?> totalBayarColumn;
    @FXML
    private TableColumn<?, ?> metodeColumn;
    @FXML
    private TableColumn<?, ?> tanggalColumn;
    @FXML
    private TableColumn<?, ?> statusColumn;
    
    @FXML
    private ComboBox<String> reservasiComboBox;
    @FXML
    private ComboBox<String> metodeComboBox;
    
    @FXML
    private Label pelangganLabel;
    @FXML
    private Label kamarLabel;
    @FXML
    private Label durasiLabel;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label pajakLabel;
    @FXML
    private Label diskonRpLabel;
    @FXML
    private Label totalBayarLabel;
    @FXML
    private Label statusPembayaranLabel;
    
    @FXML
    private TextField idTransaksiField;
    @FXML
    private TextField diskonField;
    
    @FXML
    private Button bayarButton;
    @FXML
    private Button cetakButton;
    @FXML
    private Button resetButton;
    
    @FXML
    public void initialize() {
        // Isi ComboBox Metode Pembayaran
        metodeComboBox.getItems().addAll(
            "Tunai", 
            "Transfer Bank", 
            "Kartu Kredit", 
            "Kartu Debit", 
            "E-Wallet (OVO)", 
            "E-Wallet (GoPay)", 
            "E-Wallet (Dana)", 
            "QRIS"
        );

        reservasiComboBox.getItems().addAll("Reservasi 1", "Reservasi 2", "Reservasi 3");
        
        System.out.println("TransaksiController initialized");
    }

    @FXML
    public void handleDiskonChange() {
        
        
        System.out.println("Hello");
    }
}