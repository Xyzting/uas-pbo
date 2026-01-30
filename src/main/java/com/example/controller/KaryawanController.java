package com.example.controller;

import java.util.Optional;
import java.util.UUID;

import com.example.App;
import com.example.model.Karyawan;
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
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class KaryawanController {

    @FXML
    private TableView<Karyawan> karyawanTableView;
    @FXML
    private TableColumn<Karyawan, String> idColumn;
    @FXML
    private TableColumn<Karyawan, String> namaColumn;
    @FXML
    private TableColumn<Karyawan, String> noHpColumn;
    @FXML
    private TableColumn<Karyawan, String> alamatColumn;
    @FXML
    private TableColumn<Karyawan, String> jabatanColumn;
    @FXML
    private TableColumn<Karyawan, String> shiftColumn;

    @FXML
    private TextField idField;
    @FXML
    private TextField namaField;
    @FXML
    private TextField noHpField;
    @FXML
    private TextArea alamatField;
    @FXML
    private ComboBox<String> jabatanComboBox;
    @FXML
    private ComboBox<String> shiftComboBox;
    @FXML
    private Button tambahButton;
    @FXML
    private Button ubahButton;
    @FXML
    private Button hapusButton;
    @FXML
    private Button resetPasswordButton;

    private ObservableList<Karyawan> karyawanList = FXCollections.observableArrayList();
    private static final String FILE = "karyawan.csv";
    private static final String CSS_PATH = "/css/app.css";

    @FXML
    public void initialize() {
        tambahButton.setVisible(true);
        ubahButton.setVisible(false);
        hapusButton.setVisible(false);
        resetPasswordButton.setVisible(false);

        jabatanComboBox.getItems().addAll("Manager", "Resepsionis");
        shiftComboBox.getItems().addAll("Pagi", "Malam");

        idColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getId()));
        namaColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNama()));
        noHpColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getNoTelp()));
        alamatColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getAlamat()));
        jabatanColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getJabatan()));
        shiftColumn.setCellValueFactory(d
                -> new javafx.beans.property.SimpleStringProperty(d.getValue().getShift()));

        karyawanList.addAll(FileUtil.load(FILE, Karyawan::fromCSV));
        karyawanTableView.setItems(karyawanList);

        karyawanTableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, k) -> {
                    if (k != null) {
                        idField.setText(k.getId());
                        namaField.setText(k.getNama());
                        noHpField.setText(k.getNoTelp());
                        alamatField.setText(k.getAlamat());
                        jabatanComboBox.setValue(k.getJabatan());
                        shiftComboBox.setValue(k.getShift());
                    }
                });

        karyawanTableView.addEventFilter(MouseEvent.MOUSE_CLICKED, evt -> {
            Node source = evt.getPickResult().getIntersectedNode();
            while (source != null && !(source instanceof TableRow)) {
                source = source.getParent();
            }
            if (source == null || (source instanceof TableRow && ((TableRow<?>) source).isEmpty())) {
                karyawanTableView.getSelectionModel().clearSelection();
                tambahButton.setVisible(true);
                resetForm();
            }
        });

        karyawanTableView.getSelectionModel().selectedItemProperty()
                .addListener((obs, old, selected) -> {
                    hapusButton.setVisible(selected != null);
                    ubahButton.setVisible(selected != null);
                    resetPasswordButton.setVisible(selected != null);
                    tambahButton.setVisible(selected == null);
                });

        // Button Actions
        tambahButton.setOnAction(e -> tambahData());
        ubahButton.setOnAction(e -> ubahData());
        hapusButton.setOnAction(e -> hapusData());
        resetPasswordButton.setOnAction(e -> handleResetPassword());
    }

    private boolean validateForm() {
        if (namaField.getText().isEmpty()) {
            showAlert("Nama tidak boleh kosong!");
            return false;
        }
        if (noHpField.getText().isEmpty()) {
            showAlert("No HP tidak boleh kosong!");
            return false;
        }
        if (!noHpField.getText().matches("\\d+")) {
            showAlert("No HP harus angka!");
            return false;
        }
        if (alamatField.getText().isEmpty()) {
            showAlert("Alamat tidak boleh kosong!");
            return false;
        }
        if (jabatanComboBox.getValue() == null) {
            showAlert("Pilih jabatan!");
            return false;
        }
        if (shiftComboBox.getValue() == null) {
            showAlert("Pilih shift!");
            return false;
        }
        return true;
    }

    // CREATE - DENGAN PASSWORD DIALOG
    private void tambahData() {
        if (!validateForm()) {
            return;
        }

        // Dialog untuk input password
        Optional<String> passwordResult = showPasswordDialog(null);
        if (!passwordResult.isPresent()) {
            return; // User cancel
        }

        String id = "KRY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Karyawan k = new Karyawan(
                id,
                namaField.getText(),
                noHpField.getText(),
                alamatField.getText(),
                jabatanComboBox.getValue(),
                shiftComboBox.getValue(),
                passwordResult.get()
        );

        karyawanList.add(k);
        FileUtil.save(FILE, karyawanList);
        resetForm();
        successAlert("Karyawan berhasil ditambahkan!\nPassword: " + passwordResult.get(), "Tambah");
    }

    // UPDATE
    private void ubahData() {
        if (!validateForm()) {
            return;
        }

        Karyawan k = karyawanTableView.getSelectionModel().getSelectedItem();
        if (k == null) {
            return;
        }

        k.setNama(namaField.getText());
        k.setNoTelp(noHpField.getText());
        k.setAlamat(alamatField.getText());
        k.setJabatan(jabatanComboBox.getValue());
        k.setShift(shiftComboBox.getValue());

        karyawanTableView.refresh();
        FileUtil.save(FILE, karyawanList);
        resetForm();
        karyawanTableView.getSelectionModel().clearSelection();
        successAlert("Data karyawan berhasil diubah!", "Ubah");
    }

    // DELETE
    private void hapusData() {
        Karyawan k = karyawanTableView.getSelectionModel().getSelectedItem();
        if (k == null) {
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Konfirmasi Hapus");
        confirm.setHeaderText("Hapus karyawan " + k.getNama() + "?");
        confirm.setContentText("User login juga akan terhapus!");
        applyDialogStyle(confirm);

        if (confirm.showAndWait().get() == ButtonType.OK) {
            karyawanList.remove(k);
            FileUtil.save(FILE, karyawanList);
            resetForm();
            successAlert("Karyawan berhasil dihapus!", "Hapus");
        }
    }

    // RESET PASSWORD
    private void handleResetPassword() {
        Karyawan k = karyawanTableView.getSelectionModel().getSelectedItem();
        if (k == null) {
            return;
        }

        Optional<String> newPassword = showPasswordDialog(k.getNama());
        if (!newPassword.isPresent()) {
            return;
        }

        k.setPassword(newPassword.get()); // Auto hash di setter
        FileUtil.save(FILE, karyawanList);

        successAlert("Password untuk " + k.getNama() + " berhasil direset!\n"
                + "Password baru: " + newPassword.get(), "Reset Password");
    }

    // PASSWORD DIALOG
    private Optional<String> showPasswordDialog(String karyawanNama) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle(karyawanNama == null ? "Set Password" : "Reset Password");
        dialog.setHeaderText(karyawanNama == null
                ? "Buat password untuk karyawan baru"
                : "Reset password untuk: " + karyawanNama);

        applyDialogStyle(dialog);

        ButtonType okButton = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okButton, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setPrefWidth(200);

        PasswordField confirmField = new PasswordField();
        confirmField.setPromptText("Konfirmasi Password");
        confirmField.setPrefWidth(200);

        Label warningLabel = new Label();
        warningLabel.setStyle("-fx-text-fill: #dc2626; -fx-font-weight: normal;");

        grid.add(new Label("Password:"), 0, 0);
        grid.add(passwordField, 1, 0);
        grid.add(new Label("Konfirmasi:"), 0, 1);
        grid.add(confirmField, 1, 1);
        grid.add(warningLabel, 1, 2);

        dialog.getDialogPane().setContent(grid);

        Node okBtn = dialog.getDialogPane().lookupButton(okButton);
        okBtn.setDisable(true);

        // Validation
        Runnable validate = () -> {
            String pass = passwordField.getText();
            String conf = confirmField.getText();

            if (pass.isEmpty()) {
                warningLabel.setText("Password tidak boleh kosong");
                okBtn.setDisable(true);
            } else if (pass.length() < 4) {
                warningLabel.setText("Password minimal 4 karakter");
                okBtn.setDisable(true);
            } else if (!pass.equals(conf)) {
                warningLabel.setText("Password tidak cocok");
                okBtn.setDisable(true);
            } else {
                warningLabel.setText("");
                okBtn.setDisable(false);
            }
        };

        passwordField.textProperty().addListener((obs, old, val) -> validate.run());
        confirmField.textProperty().addListener((obs, old, val) -> validate.run());

        dialog.setResultConverter(btn -> {
            if (btn == okButton) {
                return passwordField.getText();
            }
            return null;
        });

        return dialog.showAndWait();
    }

    @FXML
    private void resetForm() {
        idField.clear();
        namaField.clear();
        noHpField.clear();
        alamatField.clear();
        jabatanComboBox.setValue(null);
        shiftComboBox.setValue(null);
    }

    // ==================== DIALOG STYLING ====================
    private void applyDialogStyle(Dialog<?> dialog) {
        try {
            dialog.getDialogPane().getStylesheets().add(
                    App.class.getResource(CSS_PATH).toExternalForm()
            );
        } catch (Exception e) {
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
        alert.setTitle(title + " Karyawan");
        alert.setHeaderText(null);
        alert.setContentText(msg);
        applyDialogStyle(alert);
        alert.showAndWait();
    }
}
