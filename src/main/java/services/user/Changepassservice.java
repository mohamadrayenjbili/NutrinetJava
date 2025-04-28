package services.user;

import utils.MaConnexion;
import utils.session;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Changepassservice {

    public static boolean updatePassword(String email, String newPassword) {
        String query = "UPDATE user SET password = ? WHERE email = ?";
        try (Connection conn = MaConnexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, newPassword); // Vous pouvez hacher le mot de passe ici
            stmt.setString(2, email);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getUserIdByEmail(String email) {
        String query = "SELECT id FROM user WHERE email = ?";
        try (Connection conn = MaConnexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Retourne -1 si aucun utilisateur n'est trouvé
    }

    public static String getUserEmail() {
        if (session.getCurrentUser() == null) {
            return null;
        }

        String email = session.getCurrentUser().getEmail();
        return email;
    }
}