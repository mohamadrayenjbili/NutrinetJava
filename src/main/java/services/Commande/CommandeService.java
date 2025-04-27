package services.Commande;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import models.Commande;
import models.Produit;


public class CommandeService implements ICommandeService {
    private static final String URL = "jdbc:mysql://localhost:3306/didou";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    // Méthode pour établir la connexion
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // CREATE - Ajouter une commande
    public void ajouterCommande(Commande commande) throws SQLException {
        String query = "INSERT INTO commande (nom_c, mail, adress, date_c, status, methode_de_paiement) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, commande.getNomC());
            pstmt.setString(2, commande.getMail());
            pstmt.setString(3, commande.getAdress());
            pstmt.setDate(4, commande.getDateC());
            pstmt.setString(5, commande.getStatus());
            pstmt.setString(6, commande.getMethodeDePaiement());

            pstmt.executeUpdate();

            // Récupérer l'ID généré
            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    commande.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    // READ - Récupérer toutes les commandes
    public List<Commande> getAllCommandes() throws SQLException {
        List<Commande> commandes = new ArrayList<>();
        String query = "SELECT * FROM commande";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Commande c = new Commande();
                c.setId(rs.getInt("id"));
                c.setNomC(rs.getString("nom_c"));
                c.setMail(rs.getString("mail"));
                c.setAdress(rs.getString("adress"));
                c.setDateC(rs.getDate("date_c"));
                c.setStatus(rs.getString("status"));
                c.setMethodeDePaiement(rs.getString("methode_de_paiement"));

                commandes.add(c);
            }
        }
        return commandes;
    }

    // UPDATE - Mettre à jour une commande
    public void updateCommande(Commande commande) throws SQLException {
        String query = "UPDATE commande SET nom_c = ?, mail = ?, adress = ?, " +
                "date_c = ?, status = ?, methode_de_paiement = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, commande.getNomC());
            pstmt.setString(2, commande.getMail());
            pstmt.setString(3, commande.getAdress());
            pstmt.setDate(4, commande.getDateC());
            pstmt.setString(5, commande.getStatus());
            pstmt.setString(6, commande.getMethodeDePaiement());
            pstmt.setInt(7, commande.getId());

            pstmt.executeUpdate();
        }
    }

    // DELETE - Supprimer une commande
    public void deleteCommande(int id) throws SQLException {
        String query = "DELETE FROM commande WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }

    // Méthode supplémentaire: Trouver une commande par son ID
    public Commande getCommandeById(int id) throws SQLException {
        String query = "SELECT * FROM commande WHERE id = ?";
        Commande commande = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    commande = new Commande();
                    commande.setId(rs.getInt("id"));
                    commande.setNomC(rs.getString("nom_c"));
                    commande.setMail(rs.getString("mail"));
                    commande.setAdress(rs.getString("adress"));
                    commande.setDateC(rs.getDate("date_c"));
                    commande.setStatus(rs.getString("status"));
                    commande.setMethodeDePaiement(rs.getString("methode_de_paiement"));
                }
            }
        }
        return commande;
    }

    // Méthode pour ajouter un produit à une commande
    public void ajouterProduitACommande(int commandeId, int produitId) throws SQLException {
        String query = "INSERT INTO commande_boutique (commande_id, boutique_id, quantite) VALUES (?, ?, 1) " +
                      "ON DUPLICATE KEY UPDATE quantite = quantite + 1";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, commandeId);
            pstmt.setInt(2, produitId);

            pstmt.executeUpdate();
        }
    }

    // Méthode pour supprimer un produit d'une commande
    public void supprimerProduitDeCommande(int commandeId, int produitId) throws SQLException {
        String query = "DELETE FROM commande_boutique WHERE commande_id = ? AND boutique_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, commandeId);
            pstmt.setInt(2, produitId);

            pstmt.executeUpdate();
        }
    }

    // Méthode pour récupérer tous les produits d'une commande
    public List<Produit> getProduitsByCommandeId(int commandeId) throws SQLException {
        List<Produit> produits = new ArrayList<>();
        String query = "SELECT b.* FROM boutique b " +
                "JOIN commande_boutique cb ON b.id = cb.boutique_id " +
                "WHERE cb.commande_id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, commandeId);
            try (ResultSet rs = pstmt.executeQuery()) {
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
        }
        return produits;
    }



}