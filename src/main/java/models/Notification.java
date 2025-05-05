package models;

import java.sql.Timestamp;

public class Notification {
    private int id;
    private int userId;
    private int produitId;
    private String message;
    private Timestamp dateNotification;
    private boolean lu;
    private Produit produit; // Pour stocker les détails du produit

    public Notification() {
    }

    public Notification(int userId, int produitId, String message) {
        this.userId = userId;
        this.produitId = produitId;
        this.message = message;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProduitId() {
        return produitId;
    }

    public void setProduitId(int produitId) {
        this.produitId = produitId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Timestamp getDateNotification() {
        return dateNotification;
    }

    public void setDateNotification(Timestamp dateNotification) {
        this.dateNotification = dateNotification;
    }

    public boolean isLu() {
        return lu;
    }

    public void setLu(boolean lu) {
        this.lu = lu;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
} 