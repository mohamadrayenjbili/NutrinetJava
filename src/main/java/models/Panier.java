// 1. Classe Panier
package models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Panier implements Serializable {
    private Map<Integer, LignePanier> items;
    private double total;

    public Panier() {
        this.items = new HashMap<>();
        this.total = 0.0;
    }

    // Ajouter un produit au panier
    public void ajouterProduit(Produit produit, int quantite) {
        int produitId = produit.getId();

        if (items.containsKey(produitId)) {
            // Le produit existe déjà dans le panier, augmenter la quantité
            LignePanier ligne = items.get(produitId);
            ligne.setQuantite(ligne.getQuantite() + quantite);
        } else {
            // Nouveau produit à ajouter au panier
            LignePanier ligne = new LignePanier(produit, quantite);
            items.put(produitId, ligne);
        }

        // Recalculer le total
        calculerTotal();
    }

    // Enlever un produit du panier
    public void enleverProduit(int produitId) {
        if (items.containsKey(produitId)) {
            items.remove(produitId);
            calculerTotal();
        }
    }

    // Modifier la quantité d'un produit
    public void modifierQuantite(int produitId, int quantite) {
        if (items.containsKey(produitId)) {
            if (quantite <= 0) {
                // Si la quantité est 0 ou négative, supprimer l'article
                items.remove(produitId);
            } else {
                // Sinon mettre à jour la quantité
                LignePanier ligne = items.get(produitId);
                ligne.setQuantite(quantite);
            }
            calculerTotal();
        }
    }

    // Calculer le total du panier
    private void calculerTotal() {
        total = 0.0;
        for (LignePanier ligne : items.values()) {
            total += ligne.getSousTotal();
        }
    }

    // Vider le panier
    public void viderPanier() {
        items.clear();
        total = 0.0;
    }

    // Getters et setters
    public Map<Integer, LignePanier> getItems() {
        return items;
    }

    public List<LignePanier> getItemsList() {
        return new ArrayList<>(items.values());
    }

    public double getTotal() {
        return total;
    }

    public int getNombreArticles() {
        int count = 0;
        for (LignePanier ligne : items.values()) {
            count += ligne.getQuantite();
        }
        return count;
    }
}