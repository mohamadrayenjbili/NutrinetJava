package services.reclamation;

import models.Reclamation;
import models.User;
import utils.MaConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ReclamationService {

    private Connection getConnection() throws SQLException {
        return MaConnexion.getInstance().getConnection();
    }

    public void ajouter(Reclamation r) {
        String sql = "INSERT INTO reclamation (sujet, message, user_id, status, updated_at, attachment_name) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, r.getSujet());
            pst.setString(2, r.getMessage());
            pst.setInt(3, r.getUser().getId());
            pst.setString(4, r.getStatus());
            pst.setTimestamp(5, Timestamp.valueOf(r.getUpdatedAt()));
            pst.setString(6, r.getAttachmentFile());  // Chemin de l'image attachée

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout de la réclamation : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Reclamation> getReclamationsByUser(User user) {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM reclamation WHERE user_id = ? ORDER BY updated_at DESC";

        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, user.getId());
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Reclamation r = new Reclamation(
                        rs.getInt("id"),
                        rs.getString("sujet"),
                        rs.getString("message"),
                        0,
                        user,
                        rs.getString("status"),
                        rs.getTimestamp("updated_at").toLocalDateTime(),
                        rs.getString("attachment_name"),
                        rs.getString("reponse")
                );
                list.add(r);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des réclamations : " + e.getMessage());
        }

        return list;
    }

    public void supprimer(Reclamation r) {
        String sql = "DELETE FROM reclamation WHERE id = ?";

        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, r.getId());
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de la réclamation : " + e.getMessage());
            e.printStackTrace();
        }
    }

}
