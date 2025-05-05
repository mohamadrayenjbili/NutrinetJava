package services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.Notification;
import models.Produit;
import utils.MaConnexion;

public class NotificationService {
    private Connection getConnection() throws SQLException {
        return MaConnexion.getInstance().getConnection();
    }

    public void creerNotification(int userId, int produitId, String message) throws SQLException {
        String query = "INSERT INTO notifications (user_id, produit_id, message) VALUES (?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            pstmt.setInt(2, produitId);
            pstmt.setString(3, message);
            pstmt.executeUpdate();
        }
    }

    public List<Notification> getNotificationsByUserId(int userId) throws SQLException {
        List<Notification> notifications = new ArrayList<>();
        String query = "SELECT n.*, p.* FROM notifications n " +
                      "JOIN boutique p ON n.produit_id = p.id " +
                      "WHERE n.user_id = ? ORDER BY n.date_notification DESC";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Notification notification = new Notification();
                    notification.setId(rs.getInt("id"));
                    notification.setUserId(rs.getInt("user_id"));
                    notification.setProduitId(rs.getInt("produit_id"));
                    notification.setMessage(rs.getString("message"));
                    notification.setDateNotification(rs.getTimestamp("date_notification"));
                    notification.setLu(rs.getBoolean("lu"));

                    Produit produit = new Produit();
                    produit.setId(rs.getInt("produit_id"));
                    produit.setNomProduit(rs.getString("nom_produit"));
                    produit.setPrix(rs.getDouble("prix"));
                    produit.setDescription(rs.getString("description"));
                    produit.setCategorie(rs.getString("categorie"));
                    produit.setImage(rs.getString("image"));
                    produit.setStock(rs.getInt("stock"));

                    notification.setProduit(produit);
                    notifications.add(notification);
                }
            }
        }
        return notifications;
    }

    public void marquerCommeLu(int notificationId) throws SQLException {
        String query = "UPDATE notifications SET lu = true WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, notificationId);
            pstmt.executeUpdate();
        }
    }

    public int getNombreNotificationsNonLues(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM notifications WHERE user_id = ? AND lu = false";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
} 