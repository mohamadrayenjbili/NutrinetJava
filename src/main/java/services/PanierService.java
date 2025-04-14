// 3. Service de gestion du panier
package services;

import javafx.scene.control.Alert;
import models.Panier;
import models.Produit;

public class PanierService {
    // Instance unique pour le modèle Singleton
    private static PanierService instance;

    // Le panier stocké en session
    private Panier panier;

    private PanierService() {
        // Initialisation du panier
        panier = new Panier();
    }

    // Pattern Singleton pour garantir une seule instance du panier
    public static PanierService getInstance() {
        if (instance == null) {
            instance = new PanierService();
        }
        return instance;
    }

    public Panier getPanier() {
        return panier;
    }

    // Ajouter un produit au panier
    public void ajouterAuPanier(Produit produit, int quantite) {
        if (produit.getStock() >= quantite) {
            panier.ajouterProduit(produit, quantite);
            afficherMessage("Produit ajouté", "Le produit a été ajouté au panier avec succès.", Alert.AlertType.INFORMATION);
        } else {
            afficherMessage("Stock insuffisant", "Il n'y a pas assez de stock disponible pour ce produit.", Alert.AlertType.WARNING);
        }
    }

    // Supprimer un produit du panier
    public void supprimerDuPanier(int produitId) {
        panier.enleverProduit(produitId);
    }

    // Modifier la quantité d'un produit
    public void modifierQuantite(int produitId, int nouvelleQuantite) {
        panier.modifierQuantite(produitId, nouvelleQuantite);
    }

    // Vider le panier
    public void viderPanier() {
        panier.viderPanier();
    }

    // Afficher un message
    private void afficherMessage(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Réinitialiser l'instance (utile pour les tests)
    public static void resetInstance() {
        instance = null;
    }
}
