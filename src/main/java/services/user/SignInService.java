package services.user;

import controllers.user.sign_upController;
import models.User;
import java.sql.*;

public class SignInService {

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/didou", "root", "");
    }

    private final log_historyService logService = new log_historyService();


    public User authenticate(String email, String password) throws Exception {
        String query = "SELECT * FROM user WHERE email = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String storedHashedPassword = rs.getString("password");
                String inputHashedPassword = sign_upController.PasswordUtils.hashPassword(password);

                if (storedHashedPassword.equals(inputHashedPassword)) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setName(rs.getString("name"));
                    user.setPrename(rs.getString("prename"));
                    user.setEmail(rs.getString("email"));
                    user.setPassword(storedHashedPassword);
                    user.setAge(rs.getInt("age"));
                    user.setPhoneNumber(rs.getString("phone_number"));
                    user.setAddress(rs.getString("address"));
                    user.setRole(rs.getString("role"));
                    user.setIsBanned(rs.getString("is_banned"));
                    user.setDate(rs.getDate("date"));

                    // Ajouter une entrée dans l'historique des logs
                    String details = "User " + user.getName() + " " + user.getPrename() + " logged in";
                    logService.addLog(user.getEmail(), "Login", details);

                    return user;
                }
            }
        }
        return null;
    }
}
