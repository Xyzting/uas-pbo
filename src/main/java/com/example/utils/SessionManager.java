package com.example.utils;

import com.example.model.Karyawan;

public class SessionManager {

    private static SessionManager instance;
    private Karyawan currentUser;

    private SessionManager() {
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    public void login(Karyawan karyawan) {
        this.currentUser = karyawan;
    }

    public void logout() {
        this.currentUser = null;
    }

    public Karyawan getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public boolean isManager() {
        return currentUser != null && "MANAGER".equals(currentUser.getRole());
    }

    public boolean isResepsionis() {
        return currentUser != null && "RESEPSIONIS".equals(currentUser.getRole());
    }

    public String getCurrentUserName() {
        return currentUser != null ? currentUser.getNama() : "Guest";
    }

    public String getCurrentUserId() {
        return currentUser != null ? currentUser.getId() : null;
    }
}
