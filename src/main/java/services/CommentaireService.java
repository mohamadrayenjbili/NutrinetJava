package services;

import models.Commentaire;
import utils.MaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentaireService implements ICommentaireService {

    private Connection connection;

    public CommentaireService() {
        try {
            connection = MaConnexion.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void ajouterCommentaire(Commentaire commentaire) {
        String sql = "INSERT INTO commentaire (programme_id, auteur_id, parent_id, contenu, created_at) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, commentaire.getProgrammeId());
            stmt.setInt(2, commentaire.getAuteurId());
            if (commentaire.getParentId() != null) {
                stmt.setInt(3, commentaire.getParentId());
            } else {
                stmt.setNull(3, Types.INTEGER);
            }
            stmt.setString(4, commentaire.getContenu());
            stmt.setTimestamp(5, Timestamp.valueOf(commentaire.getCreatedAt()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void supprimerCommentaire(int id) {
        String sql = "DELETE FROM commentaire WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void modifierCommentaire(Commentaire commentaire) {
        String sql = "UPDATE commentaire SET contenu = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, commentaire.getContenu());
            stmt.setInt(2, commentaire.getId());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Commentaire getCommentaireById(int id) {
        String sql = "SELECT * FROM commentaire WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapResultSetToCommentaire(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<Commentaire> getCommentairesByProgramme(int programmeId) {
        List<Commentaire> commentaires = new ArrayList<>();
        String sql = "SELECT * FROM commentaire WHERE programme_id = ? ORDER BY created_at ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, programmeId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                commentaires.add(mapResultSetToCommentaire(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return commentaires;
    }

    private Commentaire mapResultSetToCommentaire(ResultSet rs) throws SQLException {
        return new Commentaire(
                rs.getInt("id"),
                rs.getInt("programme_id"),
                rs.getInt("auteur_id"),
                rs.getObject("parent_id") != null ? rs.getInt("parent_id") : null,
                rs.getString("contenu"),
                rs.getTimestamp("created_at").toLocalDateTime()
        );
    }
}