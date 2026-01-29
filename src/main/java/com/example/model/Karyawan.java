package com.example.model;

public class Karyawan extends Person {

    private String shift;

    public Karyawan(String id, String nama, String noTelp, String alamat, String shift) {
        super(id, nama, noTelp, alamat);
        this.shift = shift;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    @Override
    public String getInfo() {
        return "Karyawan: " + nama + " | Shift: " + shift;
    }
}
