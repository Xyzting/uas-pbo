package com.example.controller;

import java.util.Optional;
import java.util.UUID;

import com.example.App;
import com.example.model.Pelanggan;
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
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class PelangganController {

    @FXML
    private TableView<Pelanggan> pelangganTableView;
    @FXML
    private TableColumn<Pelanggan, String> idColumn;
    @FXML
    private TableColumn<Pelanggan, String> namaColumn;
    @FXML
    private TableColumn<Pelanggan, String> noTelpColumn;
    @FXML
    private TableColumn<Pelanggan, String> alamatColumn;
    @FXML
    private TableColumn<Pelanggan, String> ktpColumn;

    @FXML
    private TextField idField;
    @FXML
    private TextField namaField;
    @FXML
    private TextField noTelpField;
    @FXML
    private TextArea alamatField;
    @FXML
    private TextField noKtpField;

    @FXML
    private Button tambahButton;
    @FXML
    private Button ubahButton;
    @FXML
    private Button hapusButton;

    private ObservableList<Pelanggan> pelangganList = FXCollections.observableArrayList();
    private static final String FILE = "pelanggan.csv";
    private static final String CSS_PATH = "/css/app.css";

    @FXML
    public void initialize() {
        // Button visibility
        tambahButton.setVisible(true);
        ubahButton.setVisible(false);
        hapusButton.setVisible(false);

        // Table mapping
        idColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getId()));
        namaColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNama()));
        noTelpColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNoTelp()));
        alamatColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAlamat()));
        ktpColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNoKtp()));

        // Load data dari file
        loadData();

        // Klik row → isi form
        pelangganTableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, p) -> {
                    if (p != null) {
                        idField.setText(p.getId());
                        namaField.setText(p.getNama());
                        noTelpField.setText(p.getNoTelp());
                        alamatField.setText(p.getAlamat());
                        noKtpField.setText(p.getNoKtp());
                    }
                });

        // Klik area kosong → clear selection & reset form
        pelangganTableView.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
            Node source = evt.getPickResult().getIntersectedNode();
            while (source != null && !(source instanceof TableRow)) {
                source = source.getParent();
            }
            if (source == null || (source instanceof TableRow && ((TableRow<?>) source).isEmpty())) {
                pelangganTableView.getSelectionModel().clearSelection();
                tambahButton.setVisible(true);
                resetForm();
            }
        });

        // Toggle button visibility based on selection
        pelangganTableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> {
                    hapusButton.setVisible(selected != null);
                    ubahButton.setVisible(selected != null);
                    tambahButton.setVisible(selected == null);
                });

        // Button actions
        tambahButton.setOnAction(e -> tambahData());
        ubahButton.setOnAction(e -> ubahData());
        hapusButton.setOnAction(e -> hapusData());
    }

    private void loadData() {
        pelangganList.clear();
        pelangganList.addAll(FileUtil.load(FILE, Pelanggan::fromCSV));
        pelangganTableView.setItems(pelangganList);
    }

    private boolean validateForm() {
        if (namaField.getText().isEmpty()) {
            showAlert("Nama tidak boleh kosong!");
            return false;
        }

        if (noTelpField.getText().isEmpty()) {
            showAlert("No Telp tidak boleh kosong!");
            return false;
        }

        if (!noTelpField.getText().matches("\\d+")) {
            showAlert("No Telp harus angka!");
            return false;
        }

        if (noKtpField.getText().isEmpty()) {
            showAlert("No KTP tidak boleh kosong!");
            return false;
        }

        if (!noKtpField.getText().matches("\\d{16}")) {
            showAlert("No KTP harus 16 digit angka!");
            return false;
        }

        if (alamatField.getText().isEmpty()) {
            showAlert("Alamat tidak boleh kosong!");
            return false;
        }

        return true;
    }

    // CREATE
    private void tambahData() {
        if (!validateForm()) {
            return;
        }

        String id = "PLG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Pelanggan p = new Pelanggan(
                id,
                namaField.getText(),
                noTelpField.getText(),
                alamatField.getText(),
                noKtpField.getText()
        );

        pelangganList.add(p);
        FileUtil.save(FILE, pelangganList);
        resetForm();
        successAlert("Data pelanggan berhasil ditambahkan!", "Tambah");
    }

    // UPDATE
    private void ubahData() {
        if (!validateForm()) {
            return;
        }

        Pelanggan p = pelangganTableView.getSelectionModel().getSelectedItem();
        if (p == null) {
            return;
        }

        p.setNama(namaField.getText());
        p.setNoTelp(noTelpField.getText());
        p.setAlamat(alamatField.getText());
        p.setNoKtp(noKtpField.getText());

        pelangganTableView.refresh();
        FileUtil.save(FILE, pelangganList);
        resetForm();
        pelangganTableView.getSelectionModel().clearSelection();
        successAlert("Data pelanggan berhasil diubah!", "Ubah");
    }

    // DELETE
    private void hapusData() {
        Pelanggan p = pelangganTableView.getSelectionModel().getSelectedItem();
        if (p == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText(null);
        confirm.setContentText("Yakin ingin menghapus pelanggan " + p.getNama() + "?");
        applyDialogStyle(confirm);

        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            pelangganList.remove(p);
            FileUtil.save(FILE, pelangganList);
            resetForm();
            successAlert("Data pelanggan berhasil dihapus!", "Hapus");
        }
    }

    // RESET
    @FXML
    private void resetForm() {
        idField.clear();
        namaField.clear();
        noTelpField.clear();
        alamatField.clear();
        noKtpField.clear();
    }

    // ==================== DIALOG STYLING ====================
    private static void applyDialogStyle(Dialog<?> dialog) {
        try {
            dialog.getDialogPane().getStylesheets().add(
                    App.class.getResource(CSS_PATH).toExternalForm()
            );
        } catch (Exception e) {
            // CSS not found, ignore
        }
    }

    private void applyDialogStyle(Alert alert) {
        try {
            alert.getDialogPane().getStylesheets().add(
                    App.class.getResource(CSS_PATH).toExternalForm()
            );
        } catch (Exception e) {
            // CSS not found, ignore
        }
    }

    private void showAlert(String msg) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Validasi Error");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        applyDialogStyle(alert);
        alert.showAndWait();
    }

    private void successAlert(String msg, String title) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title + " Pelanggan");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        applyDialogStyle(alert);
        alert.showAndWait();
    }

    // ========== DIALOG FOR RESERVASI CONTROLLER ==========
    /**
     * Method static untuk dipanggil dari ReservasiController Mengembalikan
     * Pelanggan baru atau null jika dibatalkan
     */
    public static Optional<Pelanggan> showPelangganDialog(Pelanggan pelanggan) {
        Dialog<Pelanggan> dialog = new Dialog<>();
        dialog.setTitle(pelanggan == null ? "Tambah Pelanggan Baru" : "Ubah Pelanggan");
        dialog.setHeaderText(pelanggan == null
                ? "Input data pelanggan baru" : "Edit data pelanggan");

        // Apply CSS
        applyDialogStyle(dialog);

        // Buttons
        ButtonType saveButtonType = new ButtonType("Simpan", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        // Form Grid
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField namaField = new TextField();
        namaField.setPromptText("Nama Lengkap");
        namaField.setPrefWidth(300);

        TextField telpField = new TextField();
        telpField.setPromptText("08xxxxxxxxxx");

        TextField ktpField = new TextField();
        ktpField.setPromptText("16 digit");
        ktpField.textProperty().addListener((obs, old, newVal) -> {
            if (!newVal.matches("\\d*")) {
                ktpField.setText(old);
            }
            if (newVal.length() > 16) {
                ktpField.setText(newVal.substring(0, 16));
            }
        });

        TextArea alamatArea = new TextArea();
        alamatArea.setPromptText("Alamat lengkap");
        alamatArea.setPrefRowCount(3);

        // Fill if edit mode
        if (pelanggan != null) {
            namaField.setText(pelanggan.getNama());
            telpField.setText(pelanggan.getNoTelp());
            ktpField.setText(pelanggan.getNoKtp());
            alamatArea.setText(pelanggan.getAlamat());
        }

        grid.add(new Label("Nama Lengkap:"), 0, 0);
        grid.add(namaField, 1, 0);
        grid.add(new Label("No. Telp:"), 0, 1);
        grid.add(telpField, 1, 1);
        grid.add(new Label("No. KTP:"), 0, 2);
        grid.add(ktpField, 1, 2);
        grid.add(new Label("Alamat:"), 0, 3);
        grid.add(alamatArea, 1, 3);

        dialog.getDialogPane().setContent(grid);

        Node saveButton = dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setDisable(true);

        Runnable checkValid = () -> {
            boolean valid = !namaField.getText().trim().isEmpty()
                    && !telpField.getText().trim().isEmpty()
                    && telpField.getText().matches("\\d+")
                    && !ktpField.getText().trim().isEmpty()
                    && ktpField.getText().matches("\\d{16}");
            saveButton.setDisable(!valid);
        };

        namaField.textProperty().addListener((obs, old, newVal) -> checkValid.run());
        telpField.textProperty().addListener((obs, old, newVal) -> checkValid.run());
        ktpField.textProperty().addListener((obs, old, newVal) -> checkValid.run());

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                String id = pelanggan != null
                        ? pelanggan.getId()
                        : "PLG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

                return new Pelanggan(
                        id,
                        namaField.getText().trim(),
                        telpField.getText().trim(),
                        alamatArea.getText().trim(),
                        ktpField.getText().trim()
                );
            }
            return null;
        });

        return dialog.showAndWait();
    }

    public static ObservableList<Pelanggan> getAllPelanggan() {
        return FXCollections.observableArrayList(
                FileUtil.load(FILE, Pelanggan::fromCSV)
        );
    }
}
