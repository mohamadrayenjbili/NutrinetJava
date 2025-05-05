package services.user;

import models.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class updateservice {

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/didou", "root", "");
    }

    public void updateUser(User user) throws Exception {
        String query = "UPDATE user SET name = ?, prename = ?, age = ?, phone_number = ?, address = ? WHERE id = ?";

        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getPrename());
            ps.setInt(3, user.getAge());
            ps.setString(4, user.getPhoneNumber());
            ps.setString(5, user.getAddress());
            ps.setInt(6, user.getId());
            ps.executeUpdate();
        }
    }
}