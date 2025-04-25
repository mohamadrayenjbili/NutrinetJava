package services.Commande;

import models.Commande;
import models.Produit;
import java.sql.SQLException;
import java.util.List;

public interface ICommandeService {
    // CREATE - Ajouter une commande
    void ajouterCommande(Commande commande) throws SQLException;
    // READ - Récupérer toutes les commandes
    List<Commande> getAllCommandes() throws SQLException;
    // READ - Trouver une commande par son ID
    Commande getCommandeById(int id) throws SQLException;
    // UPDATE - Mettre à jour une commande
    void updateCommande(Commande commande) throws SQLException;
    // DELETE - Supprimer une commande
    void deleteCommande(int id) throws SQLException;
    // Méthodes pour gérer les produits d'une commande
    // Ajouter un produit à une commande
    void ajouterProduitACommande(int commandeId, int produitId) throws SQLException;
    // Supprimer un produit d'une commande
    void supprimerProduitDeCommande(int commandeId, int produitId) throws SQLException;
    // Récupérer tous les produits d'une commande
    List<Produit> getProduitsByCommandeId(int commandeId) throws SQLException;
}
