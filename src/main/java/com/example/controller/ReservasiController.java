package com.example.controller;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.example.App;
import com.example.model.Kamar;
import com.example.model.Pelanggan;
import com.example.model.Reservasi;
import com.example.model.Transaksi;
import com.example.utils.FileUtil;
import com.example.utils.SessionManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ReservasiController {

    // TableView & Columns
    @FXML
    private TableView<Reservasi> reservasiTableView;
    @FXML
    private TableColumn<Reservasi, String> idReservasiColumn;
    @FXML
    private TableColumn<Reservasi, String> pelangganColumn;
    @FXML
    private TableColumn<Reservasi, String> kamarColumn;
    @FXML
    private TableColumn<Reservasi, String> checkInColumn;
    @FXML
    private TableColumn<Reservasi, String> checkOutColumn;
    @FXML
    private TableColumn<Reservasi, String> durasiColumn;
    @FXML
    private TableColumn<Reservasi, String> statusColumn;

    // Form Fields
    @FXML
    private TextField idReservasiField;
    @FXML
    private ComboBox<Pelanggan> pelangganComboBox;
    @FXML
    private ComboBox<Kamar> kamarComboBox;
    @FXML
    private DatePicker checkInDatePicker;
    @FXML
    private DatePicker checkOutDatePicker;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private Label durasiLabel;
    @FXML
    private Label totalHargaLabel;

    // Buttons
    @FXML
    private Button tambahButton;
    @FXML
    private Button ubahButton;
    @FXML
    private Button batalkanButton;
    @FXML
    private Button resetButton;
    @FXML
    private Button checkInButton;
    @FXML
    private Button checkOutButton;
    @FXML
    private Button refreshKamarButton;
    @FXML
    private Button pelangganBaruButton;

    // Data
    private ObservableList<Reservasi> reservasiList = FXCollections.observableArrayList();

    private FilteredList<Reservasi> filteredData;
    private SortedList<Reservasi> sortedData;
    private static final String RESERVASI_FILE = "reservasi.csv";
    private static final String PELANGGAN_FILE = "pelanggan.csv";
    private static final String KAMAR_FILE = "kamar.csv";
    private static final String TRANSAKSI_FILE = "transaksi.csv";

    // Payment methods
    private static final String[] METODE_PEMBAYARAN = {"CASH", "DEBIT", "CREDIT", "TRANSFER"};
    private static final String CSS_PATH = "/css/app.css";

    @FXML
    public void initialize() {
        tambahButton.setVisible(true);
        ubahButton.setVisible(false);
        batalkanButton.setVisible(false);
        checkInButton.setVisible(false);
        checkOutButton.setVisible(false);

        idReservasiColumn.setCellValueFactory(new PropertyValueFactory<>("idReservasi"));
        pelangganColumn.setCellValueFactory(new PropertyValueFactory<>("namaPelanggan"));
        kamarColumn.setCellValueFactory(new PropertyValueFactory<>("namaKamar"));
        checkInColumn.setCellValueFactory(new PropertyValueFactory<>("checkInFormatted"));
        checkOutColumn.setCellValueFactory(new PropertyValueFactory<>("checkOutFormatted"));
        durasiColumn.setCellValueFactory(new PropertyValueFactory<>("durasiText"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        setupStatusComboBox();
        loadPelangganComboBox();
        loadKamarComboBox();
        setupDatePickers();
        loadReservasiData();
        setupFilteringAndSorting();
        setupTableSelection();

        tambahButton.setOnAction(e -> handleTambah());
        ubahButton.setOnAction(e -> handleUbah());
        batalkanButton.setOnAction(e -> handleBatalkan());
        resetButton.setOnAction(e -> resetForm());
        checkInButton.setOnAction(e -> handleCheckIn());
        checkOutButton.setOnAction(e -> handleCheckOut());
        refreshKamarButton.setOnAction(e -> loadKamarComboBox());

        if (pelangganBaruButton != null) {
            pelangganBaruButton.setOnAction(e -> handlePelangganBaru());
        }

        checkInDatePicker.valueProperty().addListener((obs, old, newVal) -> calculateDurasiAndTotal());
        checkOutDatePicker.valueProperty().addListener((obs, old, newVal) -> calculateDurasiAndTotal());
        kamarComboBox.valueProperty().addListener((obs, old, newVal) -> calculateDurasiAndTotal());
    }

    private void setupFilteringAndSorting() {
        filteredData = new FilteredList<>(reservasiList, p -> true);
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(reservasiTableView.comparatorProperty());
        reservasiTableView.setItems(sortedData);
    }

    private void setupStatusComboBox() {
        statusComboBox.getItems().addAll("BOOKING", "CHECK_IN", "CHECK_OUT", "BATAL");
        statusComboBox.setValue("BOOKING");

        statusComboBox.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setDisable(false);
                    setStyle("");
                } else {
                    setText(item);
                    if ("BATAL".equals(item) || "CHECK_OUT".equals(item)) {
                        setDisable(true);
                        setStyle("-fx-opacity: 0.5; -fx-text-fill: #9ca3af;");
                    } else {
                        setDisable(false);
                        setStyle("");
                    }
                }
            }
        });

        statusComboBox.setButtonCell(new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText("Pilih Status");
                } else {
                    setText(item);
                    // Style berdasarkan status
                    switch (item) {
                        case "BOOKING":
                            setStyle("-fx-text-fill: #2563eb; -fx-font-weight: bold;");
                            break;
                        case "CHECK_IN":
                            setStyle("-fx-text-fill: #16a34a; -fx-font-weight: bold;");
                            break;
                        case "CHECK_OUT":
                            setStyle("-fx-text-fill: #6b7280; -fx-font-weight: bold;");
                            break;
                        case "BATAL":
                            setStyle("-fx-text-fill: #dc2626; -fx-font-weight: bold;");
                            break;
                        default:
                            setStyle("");
                    }
                }
            }
        });
    }

    private void loadPelangganComboBox() {
        List<Pelanggan> pelangganList = FileUtil.load(PELANGGAN_FILE, Pelanggan::fromCSV);
        pelangganComboBox.setItems(FXCollections.observableArrayList(pelangganList));

        pelangganComboBox.setCellFactory(lv -> new ListCell<Pelanggan>() {
            @Override
            protected void updateItem(Pelanggan item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        pelangganComboBox.setButtonCell(new ListCell<Pelanggan>() {
            @Override
            protected void updateItem(Pelanggan item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Pilih Pelanggan" : item.getNama());
            }
        });
    }

    private void loadKamarComboBox() {
        List<Kamar> kamarList = FileUtil.load(KAMAR_FILE, Kamar::fromCSV);

        List<Kamar> kamarTersedia = kamarList.stream()
                .filter(k -> "TERSEDIA".equals(k.getStatus()))
                .collect(Collectors.toList());

        kamarComboBox.setItems(FXCollections.observableArrayList(kamarTersedia));

        kamarComboBox.setCellFactory(lv -> new ListCell<Kamar>() {
            @Override
            protected void updateItem(Kamar item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
            }
        });
        kamarComboBox.setButtonCell(new ListCell<Kamar>() {
            @Override
            protected void updateItem(Kamar item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Pilih Kamar"
                        : item.getNomorKamar() + " - " + item.getTipeKamar());
            }
        });
    }

    private void setupDatePickers() {
        checkInDatePicker.setValue(LocalDate.now());
        checkOutDatePicker.setValue(LocalDate.now().plusDays(1));

        checkInDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(LocalDate.now()));
            }
        });

        checkOutDatePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate checkIn = checkInDatePicker.getValue();
                setDisable(empty || date.isBefore(LocalDate.now())
                        || (checkIn != null && date.isBefore(checkIn.plusDays(1))));
            }
        });
    }

    private void loadReservasiData() {
        reservasiList.clear();
        reservasiList.addAll(FileUtil.load(RESERVASI_FILE, Reservasi::fromCSV));
        reservasiTableView.setItems(reservasiList);
    }

    private void setupTableSelection() {
        reservasiTableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> {
                    if (selected != null) {
                        fillFormFromReservasi(selected);
                        updateButtonVisibility(selected);
                    }
                });

        reservasiTableView.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
            Node source = evt.getPickResult().getIntersectedNode();
            while (source != null && !(source instanceof TableRow)) {
                source = source.getParent();
            }
            if (source == null || (source instanceof TableRow && ((TableRow<?>) source).isEmpty())) {
                reservasiTableView.getSelectionModel().clearSelection();
                resetForm();
            }
        });
    }

    private void fillFormFromReservasi(Reservasi r) {
        idReservasiField.setText(r.getIdReservasi());

        pelangganComboBox.getItems().stream()
                .filter(p -> p.getId().equals(r.getIdPelanggan()))
                .findFirst()
                .ifPresent(pelangganComboBox.getSelectionModel()::select);

        List<Kamar> allKamar = FileUtil.load(KAMAR_FILE, Kamar::fromCSV);
        kamarComboBox.setItems(FXCollections.observableArrayList(allKamar));
        allKamar.stream()
                .filter(k -> k.getId().equals(r.getIdKamar()))
                .findFirst()
                .ifPresent(kamarComboBox.getSelectionModel()::select);

        checkInDatePicker.setValue(r.getCheckIn());
        checkOutDatePicker.setValue(r.getCheckOut());
        statusComboBox.setValue(r.getStatus());
        durasiLabel.setText(r.getDurasiText());
        totalHargaLabel.setText(r.getTotalHargaFormatted());
    }

    private void updateButtonVisibility(Reservasi r) {
        boolean isSelected = r != null;
        String status = isSelected ? r.getStatus() : "";

        tambahButton.setVisible(!isSelected);
        ubahButton.setVisible(isSelected && "BOOKING".equals(status));
        batalkanButton.setVisible(isSelected && !"BATAL".equals(status) && !"CHECK_OUT".equals(status) && !"CHECK_IN".equals(status));
        checkInButton.setVisible(isSelected && "BOOKING".equals(status));
        checkOutButton.setVisible(isSelected && "CHECK_IN".equals(status));
    }

    private void calculateDurasiAndTotal() {
        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();
        Kamar kamar = kamarComboBox.getValue();

        if (checkIn != null && checkOut != null && checkOut.isAfter(checkIn)) {
            int durasi = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
            durasiLabel.setText(durasi + " hari");

            if (kamar != null) {
                double total = durasi * kamar.getHargaPerMalam();
                totalHargaLabel.setText(String.format("Rp %,.0f", total));
            } else {
                totalHargaLabel.setText("Rp 0");
            }
        } else {
            durasiLabel.setText("- hari");
            totalHargaLabel.setText("Rp 0");
        }
    }

    private void handleTambah() {
        if (!validateForm()) {
            return;
        }

        SessionManager session = SessionManager.getInstance();
        if (!session.isLoggedIn()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Anda harus login terlebih dahulu!");
            return;
        }

        String id = "RSV-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String selectedStatus = statusComboBox.getValue();

        Reservasi reservasi = new Reservasi(
                id,
                pelangganComboBox.getValue(),
                kamarComboBox.getValue(),
                checkInDatePicker.getValue(),
                checkOutDatePicker.getValue(),
                session.getCurrentUserId(),
                session.getCurrentUserName()
        );

        if ("CHECK_IN".equals(selectedStatus)) {
            Optional<Transaksi> result = showPaymentDialog(reservasi);

            if (!result.isPresent()) {
                return;
            }

            List<Transaksi> transaksiList = FileUtil.load(TRANSAKSI_FILE, Transaksi::fromCSV);
            transaksiList.add(result.get());
            FileUtil.save(TRANSAKSI_FILE, transaksiList);

            reservasi.setStatus("CHECK_IN");

            updateKamarStatus(kamarComboBox.getValue().getId(), "TERISI");

            reservasiList.add(reservasi);
            FileUtil.save(RESERVASI_FILE, reservasiList);

            resetForm();
            loadKamarComboBox();
            showAlert(Alert.AlertType.INFORMATION, "Sukses",
                    "Reservasi + Check-in berhasil!\n\n"
                    + "ID Reservasi: " + id + "\n"
                    + "ID Transaksi: " + result.get().getIdTransaksi() + "\n"
                    + "Total Bayar: " + result.get().getTotalBayarFormatted() + "\n\n"
                    + "Selamat menginap!");

        } else {
            reservasi.setStatus("BOOKING");

            updateKamarStatus(kamarComboBox.getValue().getId(), "TERISI");

            reservasiList.add(reservasi);
            FileUtil.save(RESERVASI_FILE, reservasiList);

            resetForm();
            loadKamarComboBox();
            showAlert(Alert.AlertType.INFORMATION, "Sukses",
                    "Reservasi berhasil ditambahkan!\nID: " + id);
        }
    }

    private void handleUbah() {
        Reservasi selected = reservasiTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        if (!validateForm()) {
            return;
        }

        selected.setCheckIn(checkInDatePicker.getValue());
        selected.setCheckOut(checkOutDatePicker.getValue());
        selected.setTotalHarga(Double.parseDouble(
                totalHargaLabel.getText().replace("Rp ", "").replace(",", "").trim()
        ));

        reservasiTableView.refresh();
        FileUtil.save(RESERVASI_FILE, reservasiList);

        resetForm();
        reservasiTableView.getSelectionModel().clearSelection();
        showAlert(Alert.AlertType.INFORMATION, "Sukses", "Reservasi berhasil diubah!");
    }

    private void handleBatalkan() {
        Reservasi selected = reservasiTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.getDialogPane().getStylesheets().add(
                App.class.getResource(CSS_PATH).toExternalForm()
        );
        confirm.setTitle("Konfirmasi Pembatalan");
        confirm.setHeaderText("Batalkan reservasi ini?");
        confirm.setContentText("Reservasi: " + selected.getIdReservasi()
                + "\nPelanggan: " + selected.getNamaPelanggan());

        if (confirm.showAndWait().get() == ButtonType.OK) {
            selected.setStatus("BATAL");
            updateKamarStatus(selected.getIdKamar(), "TERSEDIA");

            reservasiTableView.refresh();
            FileUtil.save(RESERVASI_FILE, reservasiList);

            resetForm();
            loadKamarComboBox();
            showAlert(Alert.AlertType.INFORMATION, "Sukses", "Reservasi dibatalkan!");
        }
    }

    private void handleCheckIn() {
        Reservasi selected = reservasiTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Optional<Transaksi> result = showPaymentDialog(selected);

        result.ifPresent(transaksi -> {
            List<Transaksi> transaksiList = FileUtil.load(TRANSAKSI_FILE, Transaksi::fromCSV);
            transaksiList.add(transaksi);
            FileUtil.save(TRANSAKSI_FILE, transaksiList);

            selected.setStatus("CHECK_IN");
            reservasiTableView.refresh();
            FileUtil.save(RESERVASI_FILE, reservasiList);

            resetForm();
            showAlert(Alert.AlertType.INFORMATION, "Check-in Berhasil",
                    "Pembayaran diterima!\n\n"
                    + "ID Transaksi: " + transaksi.getIdTransaksi() + "\n"
                    + "Total Bayar: " + transaksi.getTotalBayarFormatted() + "\n"
                    + "Metode: " + transaksi.getMetodePembayaran() + "\n\n"
                    + "Selamat menginap!");
        });
    }

    private Optional<Transaksi> showPaymentDialog(Reservasi reservasi) {
        Dialog<Transaksi> dialog = new Dialog<>();
        dialog.setTitle("Pembayaran Check-in");
        dialog.setHeaderText("Proses pembayaran untuk " + reservasi.getNamaPelanggan());

        dialog.getDialogPane().getStylesheets().add(
                App.class.getResource(CSS_PATH).toExternalForm()
        );

        ButtonType bayarButtonType = new ButtonType("Bayar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(bayarButtonType, ButtonType.CANCEL);

        // Form
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 20, 10, 20));

        // Info Reservasi
        Label infoLabel = new Label("INFORMASI RESERVASI");
        infoLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        grid.add(infoLabel, 0, 0, 2, 1);

        grid.add(new Label("ID Reservasi:"), 0, 1);
        grid.add(new Label(reservasi.getIdReservasi()), 1, 1);

        grid.add(new Label("Pelanggan:"), 0, 2);
        grid.add(new Label(reservasi.getNamaPelanggan()), 1, 2);

        grid.add(new Label("Kamar:"), 0, 3);
        grid.add(new Label(reservasi.getNamaKamar()), 1, 3);

        grid.add(new Label("Durasi:"), 0, 4);
        grid.add(new Label(reservasi.getDurasiText()), 1, 4);

        // Separator
        Separator sep = new Separator();
        grid.add(sep, 0, 5, 2, 1);

        // Detail Pembayaran
        Label payLabel = new Label("DETAIL PEMBAYARAN");
        payLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        grid.add(payLabel, 0, 6, 2, 1);

        double subtotal = reservasi.getTotalHarga();
        double pajak = subtotal * 0.10;

        grid.add(new Label("Subtotal:"), 0, 7);
        Label subtotalLabel = new Label(String.format("Rp %,.0f", subtotal));
        grid.add(subtotalLabel, 1, 7);

        grid.add(new Label("Pajak (10%):"), 0, 8);
        Label pajakLabel = new Label(String.format("Rp %,.0f", pajak));
        grid.add(pajakLabel, 1, 8);

        grid.add(new Label("Diskon (%):"), 0, 9);
        TextField diskonField = new TextField("0");
        diskonField.setPrefWidth(80);
        diskonField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*\\.?\\d*")) {
                diskonField.setText(old);
            }
        });

        Label diskonNominalLabel = new Label("(Rp 0)");
        HBox diskonBox = new HBox(10, diskonField, diskonNominalLabel);
        grid.add(diskonBox, 1, 9);

        grid.add(new Label("Total Bayar:"), 0, 10);
        Label totalLabel = new Label(String.format("Rp %,.0f", subtotal + pajak));
        totalLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #e74c3c;");
        grid.add(totalLabel, 1, 10);

        grid.add(new Label("Metode Bayar:"), 0, 11);
        ComboBox<String> metodeCombo = new ComboBox<>();
        metodeCombo.getItems().addAll(METODE_PEMBAYARAN);
        metodeCombo.setValue("CASH");
        metodeCombo.setPrefWidth(150);
        grid.add(metodeCombo, 1, 11);

        // Update total saat diskon berubah
        diskonField.textProperty().addListener((obs, old, newVal) -> {
            try {
                double diskon = newVal.isEmpty() ? 0 : Double.parseDouble(newVal);
                if (diskon < 0) {
                    diskon = 0;
                }
                if (diskon > 100) {
                    diskon = 100;
                }

                double afterPajak = subtotal + pajak;
                double potongan = afterPajak * (diskon / 100.0);
                double total = afterPajak - potongan;

                diskonNominalLabel.setText(String.format("(Rp %,.0f)", potongan));
                totalLabel.setText(String.format("Rp %,.0f", total));
            } catch (NumberFormatException e) {
                // ignore
            }
        });

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().setPrefWidth(400);

        Node bayarButton = dialog.getDialogPane().lookupButton(bayarButtonType);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == bayarButtonType) {
                SessionManager session = SessionManager.getInstance();
                String idTransaksi = "TRX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                double diskon = 0;
                try {
                    diskon = Double.parseDouble(diskonField.getText());
                    if (diskon < 0) {
                        diskon = 0;
                    }
                    if (diskon > 100) {
                        diskon = 100;
                    }
                } catch (NumberFormatException e) {
                    diskon = 0;
                }

                return new Transaksi(
                        idTransaksi,
                        reservasi.getIdReservasi(),
                        reservasi.getNamaPelanggan(),
                        reservasi.getNamaKamar(),
                        subtotal,
                        diskon,
                        metodeCombo.getValue(),
                        session.getCurrentUserId(),
                        session.getCurrentUserName()
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #6b7280; -fx-font-weight: normal;");
        return label;
    }

    private Label createValueLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: #111827; -fx-font-weight: 600;");
        return label;
    }

    private void handleCheckOut() {
        Reservasi selected = reservasiTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.getDialogPane().getStylesheets().add(
                App.class.getResource(CSS_PATH).toExternalForm()
        );
        confirm.setTitle("Konfirmasi Check-out");
        confirm.setHeaderText("Proses check-out untuk:");
        confirm.setContentText("Pelanggan: " + selected.getNamaPelanggan()
                + "\nKamar: " + selected.getNamaKamar());

        if (confirm.showAndWait().get() == ButtonType.OK) {
            selected.setStatus("CHECK_OUT");
            updateKamarStatus(selected.getIdKamar(), "TERSEDIA");

            reservasiTableView.refresh();
            FileUtil.save(RESERVASI_FILE, reservasiList);

            resetForm();
            loadKamarComboBox();
            showAlert(Alert.AlertType.INFORMATION, "Sukses",
                    "Check-out berhasil!\nTerima kasih telah menginap!");
        }
    }

    private void handlePelangganBaru() {
        Optional<Pelanggan> result = PelangganController.showPelangganDialog(null);

        result.ifPresent(pelanggan -> {
            List<Pelanggan> pelangganList = FileUtil.load(PELANGGAN_FILE, Pelanggan::fromCSV);
            pelangganList.add(pelanggan);
            FileUtil.save(PELANGGAN_FILE, pelangganList);

            loadPelangganComboBox();
            pelangganComboBox.getSelectionModel().select(pelanggan);

            showAlert(Alert.AlertType.INFORMATION, "Sukses",
                    "Pelanggan baru berhasil ditambahkan!");
        });
    }

    private void updateKamarStatus(String kamarId, String newStatus) {
        List<Kamar> kamarList = FileUtil.load(KAMAR_FILE, Kamar::fromCSV);

        kamarList.stream()
                .filter(k -> k.getId().equals(kamarId))
                .findFirst()
                .ifPresent(k -> k.setStatus(newStatus));

        FileUtil.save(KAMAR_FILE, kamarList);
    }

    private boolean validateForm() {
        if (pelangganComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Pilih pelanggan!");
            return false;
        }

        if (kamarComboBox.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Pilih kamar!");
            return false;
        }

        LocalDate checkIn = checkInDatePicker.getValue();
        LocalDate checkOut = checkOutDatePicker.getValue();

        if (checkIn == null || checkOut == null) {
            showAlert(Alert.AlertType.WARNING, "Validasi", "Pilih tanggal check-in dan check-out!");
            return false;
        }

        if (!checkOut.isAfter(checkIn)) {
            showAlert(Alert.AlertType.WARNING, "Validasi",
                    "Tanggal check-out harus setelah check-in!");
            return false;
        }

        return true;
    }

    private void resetForm() {
        idReservasiField.clear();
        pelangganComboBox.setValue(null);
        kamarComboBox.setValue(null);
        checkInDatePicker.setValue(LocalDate.now());
        checkOutDatePicker.setValue(LocalDate.now().plusDays(1));
        statusComboBox.setValue("BOOKING");
        durasiLabel.setText("- hari");
        totalHargaLabel.setText("Rp 0");

        tambahButton.setVisible(true);
        ubahButton.setVisible(false);
        batalkanButton.setVisible(false);
        checkInButton.setVisible(false);
        checkOutButton.setVisible(false);

        loadKamarComboBox();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        alert.getDialogPane().getStylesheets().add(
                App.class.getResource(CSS_PATH).toExternalForm()
        );

        alert.showAndWait();
    }
}
