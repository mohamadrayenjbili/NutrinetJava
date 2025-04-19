package services.user;

import models.User;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SignInService {

    private Connection getConnection() throws Exception {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/didou", "root", "");
    }

    public User authenticate(String email, String password) throws Exception {
        String query = "SELECT * FROM user WHERE email = ? AND password = ?";
        try (Connection con = getConnection(); PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, email);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setName(rs.getString("name"));
                user.setPrename(rs.getString("prename"));
                user.setEmail(rs.getString("email"));
                user.setPassword(rs.getString("password"));
                user.setAge(rs.getInt("age"));
                user.setPhoneNumber(rs.getString("phone_number"));
                user.setAddress(rs.getString("address"));
                user.setRole(rs.getString("role"));
                user.setIsBanned(rs.getString("is_banned"));
                return user;
            }
        }
        return null;
    }
}
