package services.user;

import models.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class UserService {

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/didou", "root", "");
    }

    public void addUser(User user) throws Exception {
        String query = "INSERT INTO user (name, prename, email, password, age, phone_number, address, role, is_banned, date) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPrename());
            ps.setString(3, user.getEmail());
            ps.setString(4, user.getPassword());
            ps.setInt(5, user.getAge());
            ps.setString(6, user.getPhoneNumber());
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getRole());
            ps.setString(9, user.getIsBanned());
            ps.setDate(10, user.getDate());
            ps.executeUpdate();
        }
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
