// 2. Classe LignePanier
package models;

import java.io.Serializable;

public class LignePanier implements Serializable {
    private Produit produit;
    private int quantite;
    private double sousTotal;

    public LignePanier(Produit produit, int quantite) {
        this.produit = produit;
        this.quantite = quantite;
        this.calculerSousTotal();
    }

    private void calculerSousTotal() {
        this.sousTotal = this.produit.getPrix() * this.quantite;
    }

    // Getters et setters
    public Produit getProduit() {
        return produit;
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
        calculerSousTotal();
    }

    public int getQuantite() {
        return quantite;
    }

    public void setQuantite(int quantite) {
        this.quantite = quantite;
        calculerSousTotal();
    }

    public double getSousTotal() {
        return sousTotal;
    }
}