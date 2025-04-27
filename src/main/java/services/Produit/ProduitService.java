package services.Produit;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Produit;
import services.NotificationService;
import utils.MaConnexion;

public class ProduitService implements IProduitService {

    // Méthode helper pour obtenir une nouvelle connexion
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
            pstmt.setString(5, produit.getImage());
            pstmt.setInt(6, produit.getStock());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de l'ajout du produit, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    produit.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Échec de la récupération de l'ID généré.");
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

        if (produit == null) {
            throw new SQLException("Produit non trouvé avec l'ID: " + id);
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

            // Vérifier si le stock a été mis à jour et envoyer des notifications
            if (produit.getStock() > 0) {
                envoyerNotificationsStockDisponible(produit);
            }
        }
    }

    private void envoyerNotificationsStockDisponible(Produit produit) throws SQLException {
        String query = "SELECT user_id FROM liste_souhaits WHERE produit_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, produit.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                NotificationService notificationService = new NotificationService();
                while (rs.next()) {
                    int userId = rs.getInt("user_id");
                    String message = "Le produit " + produit.getNomProduit() + " est maintenant disponible en stock !";
                    notificationService.creerNotification(userId, produit.getId(), message);
                }
            }
        }
    }

    @Override
    public void deleteProduit(int id) throws SQLException {
        String query = "DELETE FROM boutique WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de la suppression, produit non trouvé avec l'ID: " + id);
            }
        }
    }
    @Override
    public void closeConnection() throws SQLException {
        // Si MaConnexion gère un pool de connexions, cette méthode peut être vide
        // Ou implémentez la logique de fermeture si nécessaire
    }
}