package com.example.controller;

import java.util.Optional;
import java.util.UUID;

import com.example.App;
import com.example.model.Kamar;
import com.example.utils.FileUtil;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class KamarController {

    @FXML
    private TableView<Kamar> kamarTableView;
    @FXML
    private TableColumn<Kamar, String> idKamarColumn;
    @FXML
    private TableColumn<Kamar, String> tipeColumn;
    @FXML
    private TableColumn<Kamar, String> hargaColumn;
    @FXML
    private TableColumn<Kamar, String> statusColumn;

    @FXML
    private TextField idKamarField;
    @FXML
    private ComboBox<String> tipeComboBox;
    @FXML
    private TextField hargaField;
    @FXML
    private ComboBox<String> statusComboBox;

    @FXML
    private Button tambahButton;
    @FXML
    private Button ubahButton;
    @FXML
    private Button hapusButton;
    @FXML
    private Button resetButton;

    private ObservableList<Kamar> kamarList = FXCollections.observableArrayList();
    private static final String FILE = "kamar.csv";

    // Options
    private static final String[] STATUS_OPTIONS = {"TERSEDIA", "TERISI", "MAINTENANCE"};
    private static final String[] TIPE_OPTIONS = {"Standard", "Deluxe", "Suite"};

    private static final String CSS_PATH = "/css/app.css";

    @FXML
    public void initialize() {
        // Button visibility
        tambahButton.setVisible(true);
        ubahButton.setVisible(false);
        hapusButton.setVisible(false);

        // Setup ComboBox
        statusComboBox.setItems(FXCollections.observableArrayList(STATUS_OPTIONS));
        statusComboBox.setValue("TERSEDIA");

        tipeComboBox.setItems(FXCollections.observableArrayList(TIPE_OPTIONS));
        tipeComboBox.setValue("Standard");

        // Table mapping
        idKamarColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNomorKamar()));
        tipeColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getTipeKamar()));
        hargaColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getHargaFormatted()));
        statusColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getStatus()));

        // Load data dari file
        loadData();

        // Klik row → isi form
        kamarTableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, k) -> {
                    if (k != null) {
                        idKamarField.setText(k.getNomorKamar());
                        tipeComboBox.setValue(k.getTipeKamar());
                        hargaField.setText(String.valueOf((int) k.getHargaPerMalam()));
                        statusComboBox.setValue(k.getStatus());
                    }
                });

        // Klik area kosong → clear selection & reset form
        kamarTableView.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
            Node source = evt.getPickResult().getIntersectedNode();
            while (source != null && !(source instanceof TableRow)) {
                source = source.getParent();
            }
            if (source == null || (source instanceof TableRow && ((TableRow<?>) source).isEmpty())) {
                kamarTableView.getSelectionModel().clearSelection();
                tambahButton.setVisible(true);
                resetForm();
            }
        });

        // Toggle button visibility based on selection
        kamarTableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> {
                    hapusButton.setVisible(selected != null);
                    ubahButton.setVisible(selected != null);
                    tambahButton.setVisible(selected == null);
                });

        // Input filter: harga hanya angka
        hargaField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                hargaField.setText(old);
            }
        });

        // Button actions
        tambahButton.setOnAction(e -> tambahData());
        ubahButton.setOnAction(e -> ubahData());
        hapusButton.setOnAction(e -> hapusData());
        resetButton.setOnAction(e -> resetForm());
    }

    private void loadData() {
        kamarList.clear();
        kamarList.addAll(FileUtil.load(FILE, Kamar::fromCSV));
        kamarTableView.setItems(kamarList);
    }

    private boolean validateForm() {
        if (idKamarField.getText().trim().isEmpty()) {
            showAlert("Nomor Kamar tidak boleh kosong!");
            return false;
        }

        if (tipeComboBox.getValue() == null) {
            showAlert("Tipe Kamar harus dipilih!");
            return false;
        }

        if (hargaField.getText().trim().isEmpty()) {
            showAlert("Harga tidak boleh kosong!");
            return false;
        }

        if (!hargaField.getText().matches("\\d+")) {
            showAlert("Harga harus berupa angka!");
            return false;
        }

        double harga = Double.parseDouble(hargaField.getText());
        if (harga <= 0) {
            showAlert("Harga harus lebih dari 0!");
            return false;
        }

        if (statusComboBox.getValue() == null) {
            showAlert("Status harus dipilih!");
            return false;
        }

        // Cek duplikat nomor kamar (untuk tambah data)
        Kamar selected = kamarTableView.getSelectionModel().getSelectedItem();
        String nomorKamar = idKamarField.getText().trim();

        for (Kamar k : kamarList) {
            if (k.getNomorKamar().equalsIgnoreCase(nomorKamar)) {
                // Kalau edit, skip pengecekan untuk kamar yang sama
                if (selected != null && selected.getId().equals(k.getId())) {
                    continue;
                }
                showAlert("Nomor Kamar sudah ada!");
                return false;
            }
        }

        return true;
    }

    // CREATE
    private void tambahData() {
        if (!validateForm()) {
            return;
        }

        String id = "KMR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Kamar k = new Kamar(
                id,
                idKamarField.getText().trim(),
                tipeComboBox.getValue(),
                Double.parseDouble(hargaField.getText()),
                statusComboBox.getValue(),
                "",
                2
        );

        kamarList.add(k);
        FileUtil.save(FILE, kamarList);
        resetForm();
        successAlert("Data kamar berhasil ditambahkan!", "Tambah");
    }

    // UPDATE
    private void ubahData() {
        if (!validateForm()) {
            return;
        }

        Kamar k = kamarTableView.getSelectionModel().getSelectedItem();
        if (k == null) {
            return;
        }

        k.setNomorKamar(idKamarField.getText().trim());
        k.setTipeKamar(tipeComboBox.getValue());
        k.setHargaPerMalam(Double.parseDouble(hargaField.getText()));
        k.setStatus(statusComboBox.getValue());

        kamarTableView.refresh();
        FileUtil.save(FILE, kamarList);
        resetForm();
        kamarTableView.getSelectionModel().clearSelection();
        successAlert("Data kamar berhasil diubah!", "Ubah");
    }

    // DELETE
    private void hapusData() {
        Kamar k = kamarTableView.getSelectionModel().getSelectedItem();
        if (k == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Yakin ingin menghapus kamar " + k.getNomorKamar() + "?");

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
            kamarList.remove(k);
            FileUtil.save(FILE, kamarList);
            resetForm();
            successAlert("Data kamar berhasil dihapus!", "Hapus");
        }
    }

    // RESET
    private void resetForm() {
        idKamarField.clear();
        tipeComboBox.setValue("Standard");
        hargaField.clear();
        statusComboBox.setValue("TERSEDIA");
        kamarTableView.getSelectionModel().clearSelection();
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validasi Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        // Apply CSS
        try {
            alert.getDialogPane().getStylesheets().add(
                    App.class.getResource(CSS_PATH).toExternalForm()
            );
        } catch (Exception e) {
            // ignore
        }

        alert.showAndWait();
    }

    private void successAlert(String msg, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title + " Kamar");
        alert.setHeaderText(null);
        alert.setContentText(msg);

        // Apply CSS
        try {
            alert.getDialogPane().getStylesheets().add(
                    App.class.getResource(CSS_PATH).toExternalForm()
            );
        } catch (Exception e) {
            // ignore
        }

        alert.showAndWait();
    }

    // ========== STATIC METHODS FOR RESERVASI CONTROLLER ==========
    public static Optional<Kamar> showKamarDialog(Kamar kamar) {
        Dialog<Kamar> dialog = new Dialog<>();
        dialog.setTitle(kamar == null ? "Tambah Kamar Baru" : "Ubah Kamar");
        dialog.setHeaderText(kamar == null
                ? "Input data kamar baru" : "Edit data kamar");

        // Apply CSS
        try {
            dialog.getDialogPane().getStylesheets().add(
                    KamarController.class.getResource("/com/example/view/app.css").toExternalForm()
            );
        } catch (Exception e) {
            // ignore
        }

        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField nomorField = new TextField();
        nomorField.setPromptText("Contoh: 101, 102, A1");
        nomorField.setPrefWidth(250);

        ComboBox<String> tipeCombo = new ComboBox<>();
        tipeCombo.setItems(FXCollections.observableArrayList(TIPE_OPTIONS));
        tipeCombo.setPromptText("Pilih Tipe Kamar");
        tipeCombo.setPrefWidth(250);

        TextField hargaField = new TextField();
        hargaField.setPromptText("Harga per malam");
        hargaField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                hargaField.setText(old);
            }
        });

        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.setItems(FXCollections.observableArrayList(STATUS_OPTIONS));
        statusCombo.setValue("TERSEDIA");
        statusCombo.setPrefWidth(250);

        TextArea fasilitasArea = new TextArea();
        fasilitasArea.setPromptText("AC, TV, WiFi, dll");
        fasilitasArea.setPrefRowCount(2);

        Spinner<Integer> kapasitasSpinner = new Spinner<>();
        kapasitasSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 2));
        kapasitasSpinner.setEditable(true);
        kapasitasSpinner.setPrefWidth(100);

        if (kamar != null) {
            nomorField.setText(kamar.getNomorKamar());
            tipeCombo.setValue(kamar.getTipeKamar());
            hargaField.setText(String.valueOf((int) kamar.getHargaPerMalam()));
            statusCombo.setValue(kamar.getStatus());
            fasilitasArea.setText(kamar.getFasilitas());
            kapasitasSpinner.getValueFactory().setValue(kamar.getKapasitas());
        }

        grid.add(new Label("Nomor Kamar:"), 0, 0);
        grid.add(nomorField, 1, 0);
        grid.add(new Label("Tipe Kamar:"), 0, 1);
        grid.add(tipeCombo, 1, 1);
        grid.add(new Label("Harga/Malam (Rp):"), 0, 2);
        grid.add(hargaField, 1, 2);
        grid.add(new Label("Status:"), 0, 3);
        grid.add(statusCombo, 1, 3);
        grid.add(new Label("Fasilitas:"), 0, 4);
        grid.add(fasilitasArea, 1, 4);
        grid.add(new Label("Kapasitas:"), 0, 5);
        grid.add(kapasitasSpinner, 1, 5);

        dialog.getDialogPane().setContent(grid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        Runnable checkValid = () -> {
            boolean valid = !nomorField.getText().trim().isEmpty()
                    && tipeCombo.getValue() != null
                    && !hargaField.getText().trim().isEmpty()
                    && hargaField.getText().matches("\\d+")
                    && Double.parseDouble(hargaField.getText()) > 0
                    && statusCombo.getValue() != null;
            saveButton.setDisable(!valid);
        };

        nomorField.textProperty().addListener((obs, old, newVal) -> checkValid.run());
        tipeCombo.valueProperty().addListener((obs, old, newVal) -> checkValid.run());
        hargaField.textProperty().addListener((obs, old, newVal) -> checkValid.run());
        statusCombo.valueProperty().addListener((obs, old, newVal) -> checkValid.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String id = kamar != null
                        ? kamar.getId()
                        : "KMR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                return new Kamar(
                        id,
                        nomorField.getText().trim(),
                        tipeCombo.getValue(),
                        Double.parseDouble(hargaField.getText()),
                        statusCombo.getValue(),
                        fasilitasArea.getText().trim(),
                        kapasitasSpinner.getValue()
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static ObservableList<Kamar> getAllKamar() {
        return FXCollections.observableArrayList(
                FileUtil.load(FILE, Kamar::fromCSV)
        );
    }

    public static ObservableList<Kamar> getAvailableKamar() {
        ObservableList<Kamar> available = FXCollections.observableArrayList();
        for (Kamar k : getAllKamar()) {
            if ("TERSEDIA".equals(k.getStatus())) {
                available.add(k);
            }
        }
        return available;
    }

    public static void updateKamarStatus(String kamarId, String newStatus) {
        ObservableList<Kamar> allKamar = getAllKamar();
        for (Kamar k : allKamar) {
            if (k.getId().equals(kamarId)) {
                k.setStatus(newStatus);
                break;
            }
        }
        FileUtil.save(FILE, allKamar);
    }
}
