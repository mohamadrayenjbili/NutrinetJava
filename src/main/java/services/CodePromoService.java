package services;

import models.CodePromo;
import utils.MaConnexion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CodePromoService implements services.CodePromo.ICodePromoService {

    // Retourner la connexion à la base de données
    private Connection getConnection() throws SQLException {
        return MaConnexion.getInstance().getConnection();
    }

    @Override
    public void ajouterCodePromo(CodePromo codePromo) throws SQLException {
        String query = "INSERT INTO code_promo (code, pourcentage, actif) VALUES (?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, codePromo.getCode());
            pstmt.setDouble(2, codePromo.getPourcentage());
            pstmt.setBoolean(3, codePromo.isActif());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de l'ajout du code promo, aucune ligne affectée.");
            }

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    codePromo.setId(generatedKeys.getInt(1));
                }
            }
        }
    }

    @Override
    public List<CodePromo> getAllCodesPromo() throws SQLException {
        List<CodePromo> codes = new ArrayList<>();
        String query = "SELECT * FROM code_promo"; // Assurez-vous que la table s'appelle bien 'code_promo'

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            // Parcourir les résultats et ajouter chaque code promo à la liste
            while (rs.next()) {
                CodePromo c = new CodePromo();
                c.setId(rs.getInt("id"));
                c.setCode(rs.getString("code"));
                c.setPourcentage(rs.getDouble("pourcentage"));
                c.setActif(rs.getBoolean("actif"));
                codes.add(c);
            }
        }

        return codes; // Retourne la liste des codes promo
    }

    @Override
    public CodePromo getCodePromoByCode(String code) throws SQLException {
        String query = "SELECT * FROM code_promo WHERE code = ? AND actif = TRUE";
        CodePromo promo = null;

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, code);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    promo = new CodePromo();
                    promo.setId(rs.getInt("id"));
                    promo.setCode(rs.getString("code"));
                    promo.setPourcentage(rs.getDouble("pourcentage"));
                    promo.setActif(rs.getBoolean("actif"));
                }
            }
        }

        return promo;
    }

    @Override
    public void updateCodePromo(CodePromo codePromo) throws SQLException {
        String query = "UPDATE code_promo SET code = ?, pourcentage = ?, actif = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, codePromo.getCode());
            pstmt.setDouble(2, codePromo.getPourcentage());
            pstmt.setBoolean(3, codePromo.isActif());
            pstmt.setInt(4, codePromo.getId());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de la mise à jour, code promo non trouvé avec ID: " + codePromo.getId());
            }
        }
    }

    @Override
    public void deleteCodePromo(int id) throws SQLException {
        String query = "DELETE FROM code_promo WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Échec de la suppression, code promo non trouvé avec ID: " + id);
            }
        }
    }

    @Override
    public void closeConnection() throws SQLException {
        // Ici, vous pouvez implémenter une logique pour fermer la connexion si vous utilisez un pool de connexions.
    }
}
