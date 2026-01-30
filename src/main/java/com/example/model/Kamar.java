package com.example.model;

import com.example.utils.FileSerializable;

public class Kamar implements FileSerializable {

    private String id;
    private String nomorKamar;
    private String tipeKamar; // "Standard", "Deluxe", "Suite"
    private double hargaPerMalam;
    private String status; // "TERSEDIA", "TERISI", "MAINTENANCE"
    private String fasilitas;
    private int kapasitas;

    public Kamar(String id, String nomorKamar, String tipeKamar,
            double hargaPerMalam, String status, String fasilitas, int kapasitas) {
        this.id = id;
        this.nomorKamar = nomorKamar;
        this.tipeKamar = tipeKamar;
        this.hargaPerMalam = hargaPerMalam;
        this.status = status;
        this.fasilitas = fasilitas;
        this.kapasitas = kapasitas;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getNomorKamar() {
        return nomorKamar;
    }

    public String getTipeKamar() {
        return tipeKamar;
    }

    public double getHargaPerMalam() {
        return hargaPerMalam;
    }

    public String getStatus() {
        return status;
    }

    public String getFasilitas() {
        return fasilitas;
    }

    public int getKapasitas() {
        return kapasitas;
    }

    // Setters
    public void setNomorKamar(String nomorKamar) {
        this.nomorKamar = nomorKamar;
    }

    public void setTipeKamar(String tipeKamar) {
        this.tipeKamar = tipeKamar;
    }

    public void setHargaPerMalam(double hargaPerMalam) {
        this.hargaPerMalam = hargaPerMalam;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setFasilitas(String fasilitas) {
        this.fasilitas = fasilitas;
    }

    public void setKapasitas(int kapasitas) {
        this.kapasitas = kapasitas;
    }

    public String getHargaFormatted() {
        return String.format("Rp %,.0f", hargaPerMalam);
    }

    @Override
    public String toCSV() {
        return id + "," + nomorKamar + "," + tipeKamar + ","
                + hargaPerMalam + "," + status + "," + fasilitas + "," + kapasitas;
    }

    public static Kamar fromCSV(String line) {
        String[] d = line.split(",", -1);
        return new Kamar(d[0], d[1], d[2],
                Double.parseDouble(d[3]), d[4], d[5], Integer.parseInt(d[6]));
    }

    @Override
    public String toString() {
        return nomorKamar + " - " + tipeKamar + " (" + getHargaFormatted() + "/malam)";
    }
}
