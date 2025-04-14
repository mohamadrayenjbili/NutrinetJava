package services.user;

import models.User;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class updateservice {

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/user", "root", "");
    }

    public void updateUser(User user) throws Exception {
        String query = "UPDATE user SET name = ?, prename = ?, email = ?, password = ?, age = ?, phone_number = ?, address = ?, role = ?, is_banned = ? WHERE id = ?";

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
            ps.setInt(10, user.getId());
            ps.executeUpdate();
        }
    }
}
