package com.example.model;

import com.example.utils.FileSerializable;

public class Pelanggan extends Person implements FileSerializable {

    private String noKtp;

    public Pelanggan(String id, String nama, String noTelp, String alamat, String noKtp) {
        super(id, nama, noTelp, alamat);
        this.noKtp = noKtp;
    }

    public String getNoKtp() {
        return noKtp;
    }

    public void setNoKtp(String noKtp) {
        this.noKtp = noKtp;
    }

    @Override
    public String getInfo() {
        return "Pelanggan: " + nama + " | KTP: " + noKtp;
    }

    @Override
    public String toCSV() {
        return id + "," + nama + "," + noTelp + "," + alamat + "," + noKtp;
    }

    public static Pelanggan fromCSV(String line) {
        String[] d = line.split(",");
        return new Pelanggan(d[0], d[1], d[2], d[3], d[4]);
    }

    @Override
    public String toString() {
        return nama + " - " + noTelp;
    }
}
