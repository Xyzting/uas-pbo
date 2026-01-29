package com.example.model;

public class Kamar {

    private String id;
    private String nomorKamar;
    private double harga;
    private String status;
    private String jenis;

    public Kamar(String id, String nomorKamar, double harga, String status, String jenis) {
        this.id = id;
        this.nomorKamar = nomorKamar;
        this.harga = harga;
        this.status = status;
        this.jenis = jenis;
    }

    public String getId() {
        return id;
    }

    public String getNomorKamar() {
        return nomorKamar;
    }

    public double getHarga() {
        return harga;
    }

    public String getStatus() {
        return status;
    }

    public String getJenis() {
        return jenis;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
