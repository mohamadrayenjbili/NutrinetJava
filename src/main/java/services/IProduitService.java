package services;

import models.Produit;
import java.sql.SQLException;
import java.util.List;

public interface IProduitService {
    // CREATE - Ajouter un produit
    void ajouterProduit(Produit produit) throws SQLException;
    // READ - Récupérer tous les produits
    List<Produit> getAllProduits() throws SQLException;
    // READ - Trouver un produit par son ID
    Produit getProduitById(int id) throws SQLException;
    // UPDATE - Mettre à jour un produit
    void updateProduit(Produit produit) throws SQLException;
    // DELETE - Supprimer un produit
    void deleteProduit(int id) throws SQLException;

    void closeConnection() throws SQLException;
}



