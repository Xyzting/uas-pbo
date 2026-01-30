package com.example.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.example.utils.FileSerializable;

public class Transaksi implements FileSerializable {

    private String idTransaksi;
    private String idReservasi;
    private String namaPelanggan;
    private String namaKamar;
    private double subtotal;
    private double pajak;          // 10% dari subtotal
    private double diskon;         // dalam persen (0-100)
    private double totalBayar;
    private String metodePembayaran; // "CASH", "DEBIT", "CREDIT", "TRANSFER"
    private LocalDate tanggalBayar;
    private String status;         // "LUNAS", "BATAL"
    private String idKaryawan;
    private String namaKaryawan;

    // Constructor untuk pembayaran baru
    public Transaksi(String idTransaksi, String idReservasi, String namaPelanggan,
            String namaKamar, double subtotal, double diskon,
            String metodePembayaran, String idKaryawan, String namaKaryawan) {
        this.idTransaksi = idTransaksi;
        this.idReservasi = idReservasi;
        this.namaPelanggan = namaPelanggan;
        this.namaKamar = namaKamar;
        this.subtotal = subtotal;
        this.pajak = subtotal * 0.10; // 10%
        this.diskon = diskon;
        this.totalBayar = calculateTotal();
        this.metodePembayaran = metodePembayaran;
        this.tanggalBayar = LocalDate.now();
        this.status = "LUNAS";
        this.idKaryawan = idKaryawan;
        this.namaKaryawan = namaKaryawan;
    }

    // Constructor lengkap untuk load dari CSV
    public Transaksi(String idTransaksi, String idReservasi, String namaPelanggan,
            String namaKamar, double subtotal, double pajak, double diskon,
            double totalBayar, String metodePembayaran, LocalDate tanggalBayar,
            String status, String idKaryawan, String namaKaryawan) {
        this.idTransaksi = idTransaksi;
        this.idReservasi = idReservasi;
        this.namaPelanggan = namaPelanggan;
        this.namaKamar = namaKamar;
        this.subtotal = subtotal;
        this.pajak = pajak;
        this.diskon = diskon;
        this.totalBayar = totalBayar;
        this.metodePembayaran = metodePembayaran;
        this.tanggalBayar = tanggalBayar;
        this.status = status;
        this.idKaryawan = idKaryawan;
        this.namaKaryawan = namaKaryawan;
    }

    private double calculateTotal() {
        double afterPajak = subtotal + pajak;
        double potongan = afterPajak * (diskon / 100.0);
        return afterPajak - potongan;
    }

    // Getters
    public String getIdTransaksi() {
        return idTransaksi;
    }

    public String getIdReservasi() {
        return idReservasi;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public String getNamaKamar() {
        return namaKamar;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getPajak() {
        return pajak;
    }

    public double getDiskon() {
        return diskon;
    }

    public double getTotalBayar() {
        return totalBayar;
    }

    public String getMetodePembayaran() {
        return metodePembayaran;
    }

    public LocalDate getTanggalBayar() {
        return tanggalBayar;
    }

    public String getStatus() {
        return status;
    }

    public String getIdKaryawan() {
        return idKaryawan;
    }

    public String getNamaKaryawan() {
        return namaKaryawan;
    }

    // Formatted getters untuk TableView
    public String getSubtotalFormatted() {
        return String.format("Rp %,.0f", subtotal);
    }

    public String getPajakFormatted() {
        return String.format("Rp %,.0f", pajak);
    }

    public String getDiskonFormatted() {
        return String.format("%.0f%%", diskon);
    }

    public String getDiskonNominalFormatted() {
        double potongan = (subtotal + pajak) * (diskon / 100.0);
        return String.format("Rp %,.0f", potongan);
    }

    public String getTotalBayarFormatted() {
        return String.format("Rp %,.0f", totalBayar);
    }

    public String getTanggalFormatted() {
        return tanggalBayar.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
    }

    // Setters
    public void setStatus(String status) {
        this.status = status;
    }

    public void setDiskon(double diskon) {
        this.diskon = diskon;
        this.totalBayar = calculateTotal();
    }

    // CSV Serialization
    @Override
    public String toCSV() {
        return String.join(",",
                idTransaksi,
                idReservasi,
                namaPelanggan,
                namaKamar,
                String.valueOf(subtotal),
                String.valueOf(pajak),
                String.valueOf(diskon),
                String.valueOf(totalBayar),
                metodePembayaran,
                tanggalBayar.toString(),
                status,
                idKaryawan,
                namaKaryawan
        );
    }

    public static Transaksi fromCSV(String line) {
        String[] d = line.split(",", -1);
        return new Transaksi(
                d[0], // idTransaksi
                d[1], // idReservasi
                d[2], // namaPelanggan
                d[3], // namaKamar
                Double.parseDouble(d[4]), // subtotal
                Double.parseDouble(d[5]), // pajak
                Double.parseDouble(d[6]), // diskon
                Double.parseDouble(d[7]), // totalBayar
                d[8], // metodePembayaran
                LocalDate.parse(d[9]), // tanggalBayar
                d[10], // status
                d[11], // idKaryawan
                d[12] // namaKaryawan
        );
    }

    @Override
    public String toString() {
        return idTransaksi + " - " + namaPelanggan + " (" + getTotalBayarFormatted() + ")";
    }
}
