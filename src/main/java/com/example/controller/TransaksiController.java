package com.example.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.example.App;
import com.example.model.Reservasi;
import com.example.model.Transaksi;
import com.example.utils.FileUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.print.PrinterJob;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class TransaksiController {

    // TableView & Columns
    @FXML
    private TableView<Transaksi> transaksiTableView;
    @FXML
    private TableColumn<Transaksi, String> idTransaksiColumn;
    @FXML
    private TableColumn<Transaksi, String> reservasiColumn;
    @FXML
    private TableColumn<Transaksi, String> pelangganColumn;
    @FXML
    private TableColumn<Transaksi, String> kamarColumn;
    @FXML
    private TableColumn<Transaksi, String> totalBayarColumn;
    @FXML
    private TableColumn<Transaksi, String> metodeColumn;
    @FXML
    private TableColumn<Transaksi, String> tanggalColumn;
    @FXML
    private TableColumn<Transaksi, String> statusColumn;

    // Info Reservasi Labels
    @FXML
    private ComboBox<Reservasi> reservasiComboBox;
    @FXML
    private Label pelangganLabel;
    @FXML
    private Label kamarLabel;
    @FXML
    private Label durasiLabel;

    // Detail Pembayaran
    @FXML
    private TextField idTransaksiField;
    @FXML
    private Label subtotalLabel;
    @FXML
    private Label pajakLabel;
    @FXML
    private TextField diskonField;
    @FXML
    private Label diskonNominalLabel;
    @FXML
    private Label totalBayarLabel;
    @FXML
    private ComboBox<String> metodeComboBox;
    @FXML
    private Label statusLabel;

    // Buttons
    @FXML
    private Button bayarButton;
    @FXML
    private Button cetakStrukButton;
    @FXML
    private Button resetButton;

    private ObservableList<Transaksi> transaksiList = FXCollections.observableArrayList();
    private FilteredList<Transaksi> filteredData;
    private SortedList<Transaksi> sortedData;
    private static final String TRANSAKSI_FILE = "transaksi.csv";
    private static final String RESERVASI_FILE = "reservasi.csv";

    private static final String[] METODE_PEMBAYARAN = {"CASH", "DEBIT", "CREDIT", "TRANSFER"};
    private static final String CSS_PATH = "/css/app.css";

    @FXML
    public void initialize() {
        idTransaksiColumn.setCellValueFactory(new PropertyValueFactory<>("idTransaksi"));
        reservasiColumn.setCellValueFactory(new PropertyValueFactory<>("idReservasi"));
        pelangganColumn.setCellValueFactory(new PropertyValueFactory<>("namaPelanggan"));
        kamarColumn.setCellValueFactory(new PropertyValueFactory<>("namaKamar"));
        totalBayarColumn.setCellValueFactory(new PropertyValueFactory<>("totalBayarFormatted"));
        metodeColumn.setCellValueFactory(new PropertyValueFactory<>("metodePembayaran"));
        tanggalColumn.setCellValueFactory(new PropertyValueFactory<>("tanggalFormatted"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        setupMetodeComboBox();
        loadReservasiComboBox();
        loadTransaksiData();
        setupFilteringAndSorting();
        setupTableSelection();

        reservasiComboBox.valueProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                fillInfoFromReservasi(selected);
                checkExistingTransaction(selected);
            }
        });

        if (diskonField != null) {
            diskonField.textProperty().addListener((obs, old, newVal) -> {
                if (!newVal.matches("\\d*\\.?\\d*")) {
                    diskonField.setText(old);
                }
                calculateTotal();
            });
        }

        bayarButton.setOnAction(e -> handleBayar());
        cetakStrukButton.setOnAction(e -> handleCetakStruk());
        resetButton.setOnAction(e -> resetForm());

        bayarButton.setDisable(true);
        cetakStrukButton.setDisable(true);
    }

    private void setupFilteringAndSorting() {
        filteredData = new FilteredList<>(transaksiList, p -> true);
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(transaksiTableView.comparatorProperty());
        transaksiTableView.setItems(sortedData);
    }

    private void setupMetodeComboBox() {
        metodeComboBox.getItems().addAll(METODE_PEMBAYARAN);
        metodeComboBox.setValue("CASH");
    }

    private void loadReservasiComboBox() {
        List<Reservasi> allReservasi = FileUtil.load(RESERVASI_FILE, Reservasi::fromCSV);

        // Filter reservasi yang CHECK_IN (sudah bayar) atau BOOKING (belum bayar)
        List<Reservasi> validReservasi = allReservasi.stream()
                .filter(r -> "CHECK_IN".equals(r.getStatus()) || "BOOKING".equals(r.getStatus()))
                .collect(Collectors.toList());

        reservasiComboBox.setItems(FXCollections.observableArrayList(validReservasi));

        reservasiComboBox.setCellFactory(lv -> new ListCell<Reservasi>() {
            @Override
            protected void updateItem(Reservasi item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getIdReservasi() + " - " + item.getNamaPelanggan());
                }
            }
        });

        reservasiComboBox.setButtonCell(new ListCell<Reservasi>() {
            @Override
            protected void updateItem(Reservasi item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Pilih Reservasi");
                } else {
                    setText(item.getIdReservasi() + " - " + item.getNamaPelanggan());
                }
            }
        });
    }

    private void loadTransaksiData() {
        transaksiList.clear();
        transaksiList.addAll(FileUtil.load(TRANSAKSI_FILE, Transaksi::fromCSV));
        transaksiTableView.setItems(transaksiList);
    }

    private void setupTableSelection() {
        transaksiTableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> {
                    if (selected != null) {
                        fillFormFromTransaksi(selected);
                    }
                });

        transaksiTableView.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
            Node source = evt.getPickResult().getIntersectedNode();
            while (source != null && !(source instanceof TableRow)) {
                source = source.getParent();
            }
            if (source == null || (source instanceof TableRow && ((TableRow<?>) source).isEmpty())) {
                transaksiTableView.getSelectionModel().clearSelection();
                resetForm();
            }
        });
    }

    private void fillInfoFromReservasi(Reservasi r) {
        pelangganLabel.setText(r.getNamaPelanggan());
        kamarLabel.setText(r.getNamaKamar());
        durasiLabel.setText(r.getDurasiText());

        double subtotal = r.getTotalHarga();
        double pajak = subtotal * 0.10;

        subtotalLabel.setText(String.format("Rp %,.0f", subtotal));
        pajakLabel.setText(String.format("Rp %,.0f", pajak));

        if (diskonField != null) {
            diskonField.setText("0");
        }
        if (diskonNominalLabel != null) {
            diskonNominalLabel.setText("(Rp 0)");
        }

        totalBayarLabel.setText(String.format("Rp %,.0f", subtotal + pajak));
    }

    private void checkExistingTransaction(Reservasi r) {
        Optional<Transaksi> existing = transaksiList.stream()
                .filter(t -> t.getIdReservasi().equals(r.getIdReservasi()))
                .findFirst();

        if (existing.isPresent()) {
            fillFormFromTransaksi(existing.get());
            bayarButton.setDisable(true);
            cetakStrukButton.setDisable(false);
            statusLabel.setText("LUNAS");
            statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

            // Select di table
            transaksiTableView.getSelectionModel().select(existing.get());
        } else {
            // Belum ada transaksi - bisa bayar
            idTransaksiField.setText("");
            bayarButton.setDisable(false);
            cetakStrukButton.setDisable(true);
            statusLabel.setText("Siap untuk pembayaran");
            statusLabel.setStyle("-fx-text-fill: #3498db;");
        }
    }

    private void fillFormFromTransaksi(Transaksi t) {
        reservasiComboBox.getItems().stream()
                .filter(r -> r.getIdReservasi().equals(t.getIdReservasi()))
                .findFirst()
                .ifPresent(reservasiComboBox.getSelectionModel()::select);

        idTransaksiField.setText(t.getIdTransaksi());
        pelangganLabel.setText(t.getNamaPelanggan());
        kamarLabel.setText(t.getNamaKamar());
        subtotalLabel.setText(t.getSubtotalFormatted());
        pajakLabel.setText(t.getPajakFormatted());

        if (diskonField != null) {
            diskonField.setText(String.valueOf((int) t.getDiskon()));
            diskonField.setDisable(true);
        }
        if (diskonNominalLabel != null) {
            diskonNominalLabel.setText(t.getDiskonNominalFormatted());
        }

        totalBayarLabel.setText(t.getTotalBayarFormatted());
        metodeComboBox.setValue(t.getMetodePembayaran());
        metodeComboBox.setDisable(true);

        statusLabel.setText(t.getStatus());
        statusLabel.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;");

        bayarButton.setDisable(true);
        cetakStrukButton.setDisable(false);
    }

    private void calculateTotal() {
        Reservasi selected = reservasiComboBox.getValue();
        if (selected == null) {
            return;
        }

        try {
            double subtotal = selected.getTotalHarga();
            double pajak = subtotal * 0.10;
            double diskon = 0;

            if (diskonField != null && !diskonField.getText().isEmpty()) {
                diskon = Double.parseDouble(diskonField.getText());
                if (diskon < 0) {
                    diskon = 0;
                }
                if (diskon > 100) {
                    diskon = 100;
                }
            }

            double afterPajak = subtotal + pajak;
            double potongan = afterPajak * (diskon / 100.0);
            double total = afterPajak - potongan;

            if (diskonNominalLabel != null) {
                diskonNominalLabel.setText(String.format("(Rp %,.0f)", potongan));
            }
            totalBayarLabel.setText(String.format("Rp %,.0f", total));

        } catch (NumberFormatException e) {
            // ignore
        }
    }

    // ==================== ACTIONS ====================
    private void handleBayar() {
        // Pembayaran seharusnya dilakukan di tab Reservasi saat Check-in
        // Tapi kalau ada kasus khusus, bisa tetap bayar di sini

        showAlert(Alert.AlertType.INFORMATION, "Info",
                "Pembayaran dilakukan saat proses Check-in di tab Reservasi.\n\n"
                + "Silakan pilih reservasi dengan status BOOKING, lalu klik tombol Check-in.");
    }

    private void handleCetakStruk() {
        Transaksi selected = transaksiTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Peringatan", "Pilih transaksi yang ingin dicetak!");
            return;
        }

        // Buat struk
        VBox struk = createStruk(selected);

        // Print dialog
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cetak Struk");
        confirm.setHeaderText("Cetak struk transaksi?");
        confirm.setContentText("ID: " + selected.getIdTransaksi());

        // Apply CSS
        try {
            confirm.getDialogPane().getStylesheets().add(
                    App.class.getResource(CSS_PATH).toExternalForm()
            );
        } catch (Exception e) {
            // ignore
        }

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            printStruk(struk);
        }
    }

    private VBox createStruk(Transaksi t) {
        VBox struk = new VBox(5);
        struk.setStyle("-fx-padding: 20; -fx-font-family: 'Courier New';");

        // Header
        Label header = new Label("================================");
        Label hotelName = new Label("      HOTEL MANAGEMENT");
        Label subHeader = new Label("         STRUK PEMBAYARAN");
        Label line1 = new Label("================================");

        header.setFont(Font.font("Courier New", 12));
        hotelName.setFont(Font.font("Courier New", 14));
        subHeader.setFont(Font.font("Courier New", 12));
        line1.setFont(Font.font("Courier New", 12));

        // Info
        String info = String.format(
                "No. Transaksi : %s\n"
                + "Tanggal       : %s\n"
                + "Waktu         : %s\n"
                + "Kasir         : %s\n"
                + "--------------------------------\n"
                + "No. Reservasi : %s\n"
                + "Pelanggan     : %s\n"
                + "Kamar         : %s\n"
                + "--------------------------------\n"
                + "Subtotal      : %s\n"
                + "Pajak (10%%)   : %s\n"
                + "Diskon (%s)  : -%s\n"
                + "--------------------------------\n"
                + "TOTAL BAYAR   : %s\n"
                + "Metode        : %s\n"
                + "Status        : %s\n"
                + "================================\n"
                + "    Terima kasih telah\n"
                + "      menginap di hotel kami!\n"
                + "================================",
                t.getIdTransaksi(),
                t.getTanggalFormatted(),
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                t.getNamaKaryawan(),
                t.getIdReservasi(),
                t.getNamaPelanggan(),
                t.getNamaKamar(),
                t.getSubtotalFormatted(),
                t.getPajakFormatted(),
                t.getDiskonFormatted(),
                t.getDiskonNominalFormatted(),
                t.getTotalBayarFormatted(),
                t.getMetodePembayaran(),
                t.getStatus()
        );

        Label infoLabel = new Label(info);
        infoLabel.setFont(Font.font("Courier New", 11));

        struk.getChildren().addAll(header, hotelName, subHeader, line1, infoLabel);

        return struk;
    }

    private void printStruk(VBox struk) {
        PrinterJob job = PrinterJob.createPrinterJob();
        if (job != null) {
            boolean showDialog = job.showPrintDialog(null);
            if (showDialog) {
                boolean success = job.printPage(struk);
                if (success) {
                    job.endJob();
                    showAlert(Alert.AlertType.INFORMATION, "Sukses", "Struk berhasil dicetak!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Error", "Gagal mencetak struk!");
                }
            }
        } else {
            // Fallback: tampilkan struk di dialog
            showStrukDialog(struk);
        }
    }

    private void showStrukDialog(VBox struk) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Preview Struk");
        dialog.setHeaderText("Struk Pembayaran");
        dialog.getDialogPane().setContent(struk);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        try {
            dialog.getDialogPane().getStylesheets().add(
                    App.class.getResource(CSS_PATH).toExternalForm()
            );
        } catch (Exception e) {
        }

        dialog.showAndWait();
    }

    private void resetForm() {
        reservasiComboBox.setValue(null);
        idTransaksiField.clear();
        pelangganLabel.setText("-");
        kamarLabel.setText("-");
        durasiLabel.setText("-");
        subtotalLabel.setText("Rp 0");
        pajakLabel.setText("Rp 0");

        if (diskonField != null) {
            diskonField.setText("0");
            diskonField.setDisable(false);
        }
        if (diskonNominalLabel != null) {
            diskonNominalLabel.setText("(Rp 0)");
        }

        totalBayarLabel.setText("Rp 0");
        metodeComboBox.setValue("CASH");
        metodeComboBox.setDisable(false);
        statusLabel.setText("Siap untuk pembayaran");
        statusLabel.setStyle("-fx-text-fill: #3498db;");

        bayarButton.setDisable(true);
        cetakStrukButton.setDisable(true);

        transaksiTableView.getSelectionModel().clearSelection();
        loadReservasiComboBox();
        loadTransaksiData();
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
        }

        alert.showAndWait();
    }

    public static ObservableList<Transaksi> getAllTransaksi() {
        return FXCollections.observableArrayList(
                FileUtil.load(TRANSAKSI_FILE, Transaksi::fromCSV)
        );
    }

    public static Optional<Transaksi> getTransaksiByReservasi(String idReservasi) {
        return getAllTransaksi().stream()
                .filter(t -> t.getIdReservasi().equals(idReservasi))
                .findFirst();
    }
}
