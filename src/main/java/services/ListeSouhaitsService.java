package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.ListeSouhaits;
import models.Produit;
import models.User;
import utils.MaConnexion;

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

    public void notifierClientsProduitEnStock(int produitId) throws SQLException {
        // Récupérer tous les utilisateurs qui ont ce produit dans leur liste de souhaits
        String query = "SELECT u.* FROM user u " +
                      "JOIN liste_souhaits ls ON u.id = ls.user_id " +
                      "JOIN boutique p ON ls.produit_id = p.id " +
                      "WHERE p.id = ? AND p.stock > 0";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, produitId);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<User> usersToNotify = new ArrayList<>();
                while (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setPrename(rs.getString("prename"));
                    user.setEmail(rs.getString("email"));
                    usersToNotify.add(user);
                }

                // Récupérer les informations du produit
                String produitQuery = "SELECT * FROM boutique WHERE id = ?";
                try (PreparedStatement produitStmt = conn.prepareStatement(produitQuery)) {
                    produitStmt.setInt(1, produitId);
                    try (ResultSet produitRs = produitStmt.executeQuery()) {
                        if (produitRs.next()) {
                            String nomProduit = produitRs.getString("nom_produit");
                            double prix = produitRs.getDouble("prix");
                            int stock = produitRs.getInt("stock");

                            // Envoyer un email à chaque utilisateur
                            EmailService emailService = new EmailService();
                            for (User user : usersToNotify) {
                                String subject = "Produit de nouveau en stock !";
                                String message = "Cher " + user.getName() + " " + user.getPrename() + ",\n\n" +
                                               "Nous sommes heureux de vous informer que le produit suivant est de nouveau en stock :\n\n" +
                                               "Produit : " + nomProduit + "\n" +
                                               "Prix : " + prix + "€\n" +
                                               "Stock disponible : " + stock + "\n\n" +
                                               "N'hésitez pas à visiter notre site pour passer commande !\n\n" +
                                               "Cordialement,\n" +
                                               "L'équipe Nutrinet";

                                emailService.sendNotification(user.getEmail(), subject, message);
                            }
                        }
                    }
                }
            }
        }
    }
} 