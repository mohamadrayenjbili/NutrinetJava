package models;

import java.sql.Timestamp;

public class ListeSouhaits {
    private int id;
    private int userId;
    private int produitId;
    private Timestamp dateAjout;
    private Produit produit; // Pour stocker les détails du produit

    public ListeSouhaits() {
    }

    public ListeSouhaits(int userId, int produitId) {
        this.userId = userId;
        this.produitId = produitId;
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

    public Timestamp getDateAjout() {
        return dateAjout;
    }

    public void setDateAjout(Timestamp dateAjout) {
        this.dateAjout = dateAjout;
    }

    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
    }
} 