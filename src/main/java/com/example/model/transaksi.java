package com.hotel.model;

import java.time.LocalDate;

public class Transaksi {

    private String id;
    private String metodePembayaran;
    private LocalDate tanggalPembayaran;
    private double jumlah;
    private String status;
    private String idReservasi;

    public Transaksi(String id, String metodePembayaran,
                     LocalDate tanggalPembayaran,
                     double jumlah, String idReservasi) {

        this.id = id;
        this.metodePembayaran = metodePembayaran;
        this.tanggalPembayaran = tanggalPembayaran;
        this.jumlah = jumlah;
        this.idReservasi = idReservasi;
        this.status = "LUNAS";
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }
}
