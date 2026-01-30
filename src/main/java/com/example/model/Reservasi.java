package com.example.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.example.utils.FileSerializable;

public class Reservasi implements FileSerializable {

    private String idReservasi;
    private String idPelanggan;
    private String namaPelanggan;
    private String idKamar;
    private String namaKamar;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private int durasi; // dalam hari
    private double totalHarga;
    private String status; // "BOOKING", "CHECK_IN", "CHECK_OUT", "BATAL"
    private String idKasir;
    private String namaKasir;
    private LocalDate tanggalDibuat;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    // Constructor lengkap
    public Reservasi(String idReservasi, String idPelanggan, String namaPelanggan,
            String idKamar, String namaKamar, LocalDate checkIn, LocalDate checkOut,
            int durasi, double totalHarga, String status,
            String idKasir, String namaKasir, LocalDate tanggalDibuat) {
        this.idReservasi = idReservasi;
        this.idPelanggan = idPelanggan;
        this.namaPelanggan = namaPelanggan;
        this.idKamar = idKamar;
        this.namaKamar = namaKamar;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.durasi = durasi;
        this.totalHarga = totalHarga;
        this.status = status;
        this.idKasir = idKasir;
        this.namaKasir = namaKasir;
        this.tanggalDibuat = tanggalDibuat;
    }

    // Constructor untuk create baru (auto hitung durasi)
    public Reservasi(String idReservasi, Pelanggan pelanggan, Kamar kamar,
            LocalDate checkIn, LocalDate checkOut, String idKasir, String namaKasir) {
        this.idReservasi = idReservasi;
        this.idPelanggan = pelanggan.getId();
        this.namaPelanggan = pelanggan.getNama();
        this.idKamar = kamar.getId();
        this.namaKamar = kamar.getNomorKamar() + " - " + kamar.getTipeKamar();
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.durasi = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        this.totalHarga = durasi * kamar.getHargaPerMalam();
        this.status = "BOOKING";
        this.idKasir = idKasir;
        this.namaKasir = namaKasir;
        this.tanggalDibuat = LocalDate.now();
    }

    // Getters
    public String getIdReservasi() {
        return idReservasi;
    }

    public String getIdPelanggan() {
        return idPelanggan;
    }

    public String getNamaPelanggan() {
        return namaPelanggan;
    }

    public String getIdKamar() {
        return idKamar;
    }

    public String getNamaKamar() {
        return namaKamar;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public int getDurasi() {
        return durasi;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public String getStatus() {
        return status;
    }

    public String getIdKasir() {
        return idKasir;
    }

    public String getNamaKasir() {
        return namaKasir;
    }

    public LocalDate getTanggalDibuat() {
        return tanggalDibuat;
    }

    // Setters
    public void setCheckIn(LocalDate checkIn) {
        this.checkIn = checkIn;
        recalculateDuration();
    }

    public void setCheckOut(LocalDate checkOut) {
        this.checkOut = checkOut;
        recalculateDuration();
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setTotalHarga(double totalHarga) {
        this.totalHarga = totalHarga;
    }

    private void recalculateDuration() {
        if (checkIn != null && checkOut != null) {
            this.durasi = (int) ChronoUnit.DAYS.between(checkIn, checkOut);
        }
    }

    // Formatted getters untuk display
    public String getCheckInFormatted() {
        return checkIn != null ? checkIn.format(DATE_FORMATTER) : "";
    }

    public String getCheckOutFormatted() {
        return checkOut != null ? checkOut.format(DATE_FORMATTER) : "";
    }

    public String getTotalHargaFormatted() {
        return String.format("Rp %,.0f", totalHarga);
    }

    public String getDurasiText() {
        return durasi + " hari";
    }

    // File Serialization
    @Override
    public String toCSV() {
        return String.join(",",
                idReservasi,
                idPelanggan,
                namaPelanggan,
                idKamar,
                namaKamar,
                checkIn.format(DATE_FORMATTER),
                checkOut.format(DATE_FORMATTER),
                String.valueOf(durasi),
                String.valueOf(totalHarga),
                status,
                idKasir,
                namaKasir,
                tanggalDibuat.format(DATE_FORMATTER)
        );
    }

    public static Reservasi fromCSV(String line) {
        String[] d = line.split(",", -1);
        return new Reservasi(
                d[0], // idReservasi
                d[1], // idPelanggan
                d[2], // namaPelanggan
                d[3], // idKamar
                d[4], // namaKamar
                LocalDate.parse(d[5], DATE_FORMATTER), // checkIn
                LocalDate.parse(d[6], DATE_FORMATTER), // checkOut
                Integer.parseInt(d[7]), // durasi
                Double.parseDouble(d[8]), // totalHarga
                d[9], // status
                d[10], // idKasir
                d[11], // namaKasir
                LocalDate.parse(d[12], DATE_FORMATTER) // tanggalDibuat
        );
    }

    @Override
    public String toString() {
        return idReservasi + " - " + namaPelanggan + " (" + status + ")";
    }
}
