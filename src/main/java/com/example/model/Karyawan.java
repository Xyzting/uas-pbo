package com.example.model;

import org.mindrot.jbcrypt.BCrypt;

import com.example.utils.FileSerializable;

public class Karyawan extends Person implements FileSerializable {

    private String jabatan;
    private String shift;
    private String password;
    private String role;

    public Karyawan(String id, String nama, String noTelp, String alamat,
            String jabatan, String shift, String password, String role) {
        super(id, nama, noTelp, alamat);
        this.jabatan = jabatan;
        this.shift = shift;
        this.password = password;
        this.role = role;
    }

    public Karyawan(String id, String nama, String noTelp, String alamat,
            String jabatan, String shift, String plainPassword) {
        super(id, nama, noTelp, alamat);
        this.jabatan = jabatan;
        this.shift = shift;
        this.password = hashPassword(plainPassword);
        this.role = jabatan.equalsIgnoreCase("Manager") ? "MANAGER" : "RESEPSIONIS";
    }

    public String getJabatan() {
        return jabatan;
    }

    public String getShift() {
        return shift;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
        this.role = jabatan.equalsIgnoreCase("Manager") ? "MANAGER" : "RESEPSIONIS";
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public void setPassword(String plainPassword) {
        this.password = hashPassword(plainPassword);
    }

    // ========== PASSWORD HASHING ==========
    /**
     * Hash password menggunakan BCrypt
     *
     * @param plainPassword password asli (plain text)
     * @return password yang sudah di-hash
     */
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    /**
     * Verifikasi password
     *
     * @param plainPassword password yang diinput user
     * @return true jika password cocok, false jika tidak
     */
    public boolean verifyPassword(String plainPassword) {
        try {
            return BCrypt.checkpw(plainPassword, this.password);
        } catch (Exception e) {
            System.err.println("Error verifying password: " + e.getMessage());
            return false;
        }
    }

    // ========== FILE SERIALIZATION ==========
    @Override
    public String toCSV() {
        return id + "," + nama + "," + noTelp + "," + alamat + ","
                + jabatan + "," + shift + "," + password + "," + role;
    }

    public static Karyawan fromCSV(String line) {
        String[] d = line.split(",", -1);

        if (d.length == 6) {
            String defaultPassword = hashPassword("password123");
            String role = d[4].equalsIgnoreCase("Manager") ? "MANAGER" : "RESEPSIONIS";
            return new Karyawan(d[0], d[1], d[2], d[3], d[4], d[5], defaultPassword, role);
        } else {
            return new Karyawan(d[0], d[1], d[2], d[3], d[4], d[5], d[6], d[7]);
        }
    }

    @Override
    public String getInfo() {
        return jabatan + " - " + nama + " (" + shift + ")";
    }

    @Override
    public String toString() {
        return nama + " - " + jabatan;
    }
}
