package services.reclamation;

import models.Reclamation;
import models.User;
import utils.MaConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import services.user.UserService;

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

    public void repondre(int idReclamation, String reponse) {
        String sql = "UPDATE reclamation SET reponse = ?, status = ? WHERE id = ?";

        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, reponse);
            pst.setString(2, "Traité"); // Par exemple, tu peux changer si besoin
            pst.setInt(3, idReclamation);

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'envoi de la réponse : " + e.getMessage());
            e.printStackTrace();
        }
    }

    public List<Reclamation> getAllReclamations() {
        List<Reclamation> list = new ArrayList<>();
        String sql = "SELECT * FROM reclamation ORDER BY updated_at DESC";

        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                User user = UserService.getUserById(userId); // Utilise ta méthode pour récupérer l'utilisateur

                Reclamation r = new Reclamation(
                        rs.getInt("id"),
                        rs.getString("sujet"),
                        rs.getString("message"),
                        0, // Note (non utilisée ici)
                        user,
                        rs.getString("status"),
                        rs.getTimestamp("updated_at").toLocalDateTime(),
                        rs.getString("attachment_name"),
                        rs.getString("reponse")
                );
                list.add(r);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération de toutes les réclamations : " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return list;
    }


    public void noterReclamation(int idReclamation, int note) {
        String sql = "UPDATE reclamation SET note = ? WHERE id = ?";

        try (Connection con = getConnection(); PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, note);
            pst.setInt(2, idReclamation);

            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Erreur lors de la notation de la réclamation : " + e.getMessage());
            e.printStackTrace();
        }
    }




}
