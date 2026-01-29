package com.example.model;

public class Pelanggan extends Person {

    private String noKtp;

    public Pelanggan(String id, String nama, String noTelp, String alamat, String noKtp) {
        super(id, nama, noTelp, alamat);
        this.noKtp = noKtp;
    }

    public String getNoKtp() {
        return noKtp;
    }

    @Override
    public String getInfo() {
        return "Pelanggan: " + nama + " | KTP: " + noKtp;
    }
}

