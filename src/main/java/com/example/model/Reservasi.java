package com.example.model;

import java.time.LocalDate;

public class Reservasi {

    private String id;
    private Pelanggan tamu;
    private Kamar kamar;
    private LocalDate tanggalCheckIn;
    private LocalDate tanggalCheckOut;
    private String status;
    private double totalHarga;

    public Reservasi(String id, Pelanggan tamu, Kamar kamar,
                     LocalDate checkIn, LocalDate checkOut) {

        this.id = id;
        this.tamu = tamu;
        this.kamar = kamar;
        this.tanggalCheckIn = checkIn;
        this.tanggalCheckOut = checkOut;

        long lamaInap = java.time.temporal.ChronoUnit.DAYS
                .between(checkIn, checkOut);

        this.totalHarga = lamaInap * kamar.getHarga();
        this.status = "AKTIF";
    }

    public String getId() {
        return id;
    }

    public Pelanggan getTamu() {
        return tamu;
    }

    public Kamar getKamar() {
        return kamar;
    }

    public double getTotalHarga() {
        return totalHarga;
    }

    public String getStatus() {
        return status;
    }

    public void batalkan() {
        status = "DIBATALKAN";
        kamar.setStatus("TERSEDIA");
    }
}

