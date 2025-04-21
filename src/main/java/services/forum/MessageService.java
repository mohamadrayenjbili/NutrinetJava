package services.forum;

import models.Forum;
import utils.MaConnexion;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MessageService {

    private Connection getConnection() throws SQLException {
        return MaConnexion.getInstance().getConnection();
    }

    public void addMessage(Forum message) throws SQLException {
        String query = "INSERT INTO message_forum (message, date, likes, user_id) VALUES (?, ?, ?, ?)";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, message.getMessage());
            ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now())); // Date actuelle
            ps.setInt(3, 0); // Nombre de likes par défaut
            ps.setInt(4, message.getUserId()); // ID de l'utilisateur connecté

            ps.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'ajout du message : " + e.getMessage());
        }
    }

    public List<Forum> getAllMessages() throws SQLException {
        String query = "SELECT * FROM message_forum ORDER BY date DESC";
        List<Forum> messages = new ArrayList<>();

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Forum msg = new Forum();
                msg.setId(rs.getInt("id"));
                msg.setMessage(rs.getString("message"));
                msg.setDate(rs.getTimestamp("date"));
                msg.setUserId(rs.getInt("user_id"));
                messages.add(msg);
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la récupération des messages : " + e.getMessage());
        }

        return messages;
    }

    public void incrementLikes(int id) throws SQLException {
        String query = "UPDATE message_forum SET likes = likes + 1 WHERE id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new SQLException("Aucun message trouvé avec l'ID : " + id);
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'incrémentation des likes : " + e.getMessage());
        }
    }

    public void deleteMessage(int id) throws SQLException {
        String query = "DELETE FROM message_forum WHERE id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows == 0) {
                throw new SQLException("Aucun message trouvé avec l'ID " + id);
            }
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de la suppression du message : " + e.getMessage());
        }
    }



}
