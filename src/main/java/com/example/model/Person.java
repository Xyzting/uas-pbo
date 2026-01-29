package com.example.model;

public abstract class Person {

    protected String id;
    protected String nama;
    protected String noTelp;
    protected String alamat;

    public Person(String id, String nama, String noTelp, String alamat) {
        this.id = id;
        this.nama = nama;
        this.noTelp = noTelp;
        this.alamat = alamat;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public String getNoTelp() {
        return noTelp;
    }

    public String getAlamat() {
        return alamat;
    }

    public abstract String getInfo();
}

