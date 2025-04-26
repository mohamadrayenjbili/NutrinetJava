package models;

import javafx.beans.property.*;

public class LignePanier {
    private final ObjectProperty<Produit> produit;
    private final IntegerProperty quantite;
    private final DoubleProperty sousTotal;

    public LignePanier(Produit produit, int quantite) {
        this.produit = new SimpleObjectProperty<>(produit);
        this.quantite = new SimpleIntegerProperty(quantite);
        this.sousTotal = new SimpleDoubleProperty(produit.getPrix() * quantite);

        // Lorsque la quantité change, mettre à jour le sous-total
        this.quantite.addListener((obs, oldVal, newVal) -> {
            this.sousTotal.set(this.produit.get().getPrix() * newVal.intValue());
        });
    }

    // Getters pour les propriétés
    public ObjectProperty<Produit> produitProperty() {
        return produit;
    }

    public IntegerProperty quantiteProperty() {
        return quantite;
    }

    public DoubleProperty sousTotalProperty() {
        return sousTotal;
    }

    // Getters classiques
    public Produit getProduit() {
        return produit.get();
    }

    public int getQuantite() {
        return quantite.get();
    }

    public double getSousTotal() {
        return sousTotal.get();
    }

    // Setters
    public void setQuantite(int quantite) {
        this.quantite.set(quantite);
    }

    public void setProduit(Produit produit) {
        this.produit.set(produit);
        this.sousTotal.set(produit.getPrix() * this.quantite.get());
    }
}
