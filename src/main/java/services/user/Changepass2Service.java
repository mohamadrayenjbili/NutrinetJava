package services.user;

import controllers.user.sign_upController;
import utils.MaConnexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Changepass2Service {

    public static boolean verifyPassword(int userId, String oldPassword) {
        String hashedPassword = sign_upController.PasswordUtils.hashPassword(oldPassword);
        String query = "SELECT password FROM user WHERE id = ?";
        try (Connection conn = MaConnexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                return storedPassword.equals(hashedPassword); // Comparaison des hachages
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean updatePassword(int userId, String newPassword) {
        String hashedPassword = sign_upController.PasswordUtils.hashPassword(newPassword);
        String query = "UPDATE user SET password = ? WHERE id = ?";
        try (Connection conn = MaConnexion.getInstance().getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, hashedPassword);
            stmt.setInt(2, userId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}