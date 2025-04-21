package services.reclamation;

import models.Reclamation;
import utils.MaConnexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ModifierReclamationService {

    private final Connection cnx;

    public ModifierReclamationService() throws SQLException {
        cnx = MaConnexion.getInstance().getConnection();
    }

    public boolean modifierReclamation(Reclamation reclamation) {
        String sql = "UPDATE reclamation SET sujet = ?, message = ?, attachment_name = ? WHERE id = ?";

        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, reclamation.getSujet());
            ps.setString(2, reclamation.getMessage());
            ps.setString(3, reclamation.getAttachmentFile());
            ps.setInt(4, reclamation.getId());

            int rowsUpdated = ps.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification : " + e.getMessage());
            return false;
        }
    }
}
