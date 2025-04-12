package services;

import models.Produit;
import utils.MaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProduitService implements IProduitService {
    // Utiliser la classe MaConnexion pour gérer la connexion à la base de données
    private Connection getConnection() throws SQLException {
        return MaConnexion.getInstance().getConnection();
    }

    @Override
    public void ajouterProduit(Produit produit) throws SQLException {
        String query = "INSERT INTO boutique (nom_produit, prix, description, categorie, image, stock) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, produit.getNomProduit());
            pstmt.setDouble(2, produit.getPrix());
            pstmt.setString(3, produit.getDescription());
            pstmt.setString(4, produit.getCategorie());
            pstmt.setString(5, produit.getImage()); // ✅ here
            pstmt.setInt(6, produit.getStock());

            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produit.setId(generatedKeys.getInt(1));
                }
            }
        }
    }


    @Override
    public List<Produit> getAllProduits() throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT * FROM boutique";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Produit p = new Produit();
                p.setId(rs.getInt("id"));
                p.setNomProduit(rs.getString("nom_produit"));
                p.setPrix(rs.getDouble("prix"));
                p.setDescription(rs.getString("description"));
                p.setCategorie(rs.getString("categorie"));
                p.setImage(rs.getString("image"));
                p.setStock(rs.getInt("stock"));

                produits.add(p);
            }
        }
        return produits;
    }

    @Override
    public Produit getProduitById(int id) throws SQLException {
        String query = "SELECT * FROM boutique WHERE id = ?";
        Produit produit = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    produit = new Produit();
                    produit.setId(rs.getInt("id"));
                    produit.setNomProduit(rs.getString("nom_produit"));
                    produit.setPrix(rs.getDouble("prix"));
                    produit.setDescription(rs.getString("description"));
                    produit.setCategorie(rs.getString("categorie"));
                    produit.setImage(rs.getString("image"));
                    produit.setStock(rs.getInt("stock"));
                }
            }
        }
        return produit;
    }

    @Override
    public void updateProduit(Produit produit) throws SQLException {
        String query = "UPDATE boutique SET nom_produit = ?, prix = ?, description = ?, " +
                "categorie = ?, image = ?, stock = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, produit.getNomProduit());
            pstmt.setDouble(2, produit.getPrix());
            pstmt.setString(3, produit.getDescription());
            pstmt.setString(4, produit.getCategorie());
            pstmt.setString(5, produit.getImage());
            pstmt.setInt(6, produit.getStock());
            pstmt.setInt(7, produit.getId());

            pstmt.executeUpdate();
        }
    }

    @Override
    public void deleteProduit(int id) throws SQLException {
        String query = "DELETE FROM boutique WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}