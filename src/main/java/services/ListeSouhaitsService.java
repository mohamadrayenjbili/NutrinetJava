package services;

import models.ListeSouhaits;
import models.Produit;
import utils.MaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ListeSouhaitsService {
    private Connection getConnection() throws SQLException {
        return MaConnexion.getInstance().getConnection();
    }

    public void ajouterAListeSouhaits(int userId, int produitId) throws SQLException {
        String query = "INSERT INTO liste_souhaits (user_id, produit_id) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, produitId);
            pstmt.executeUpdate();
        }
    }

    public void supprimerDeListeSouhaits(int userId, int produitId) throws SQLException {
        String query = "DELETE FROM liste_souhaits WHERE user_id = ? AND produit_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, produitId);
            pstmt.executeUpdate();
        }
    }

    public List<ListeSouhaits> getListeSouhaitsByUserId(int userId) throws SQLException {
        List<ListeSouhaits> liste = new ArrayList<>();
        String query = "SELECT ls.*, p.* FROM liste_souhaits ls " +
                      "JOIN boutique p ON ls.produit_id = p.id " +
                      "WHERE ls.user_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    ListeSouhaits ls = new ListeSouhaits();
                    ls.setId(rs.getInt("id"));
                    ls.setUserId(rs.getInt("user_id"));
                    ls.setProduitId(rs.getInt("produit_id"));
                    ls.setDateAjout(rs.getTimestamp("date_ajout"));

                    Produit produit = new Produit();
                    produit.setId(rs.getInt("produit_id"));
                    produit.setNomProduit(rs.getString("nom_produit"));
                    produit.setPrix(rs.getDouble("prix"));
                    produit.setDescription(rs.getString("description"));
                    produit.setCategorie(rs.getString("categorie"));
                    produit.setImage(rs.getString("image"));
                    produit.setStock(rs.getInt("stock"));

                    ls.setProduit(produit);
                    liste.add(ls);
                }
            }
        }
        return liste;
    }

    public boolean estDansListeSouhaits(int userId, int produitId) throws SQLException {
        String query = "SELECT COUNT(*) FROM liste_souhaits WHERE user_id = ? AND produit_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, produitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }
} 