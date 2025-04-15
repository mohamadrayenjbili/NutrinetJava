package services.user;

import models.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class userlist {

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/user", "root", "");
    }

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM user";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                User user = new User(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("prename"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getInt("age"),
                        rs.getString("phone_number"),
                        rs.getString("address"),
                        rs.getString("role")
                );
                users.add(user);
            }

        } catch (Exception e) {
            e.printStackTrace(); // You can use logging instead
        }

        return users;
    }

    // Delete a user by ID
    public void deleteUser(int id) throws Exception {
        String query = "DELETE FROM user WHERE id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }


}
