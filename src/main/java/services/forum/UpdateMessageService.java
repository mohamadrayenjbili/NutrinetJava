package services.forum;

import models.Forum;
import utils.MaConnexion;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class UpdateMessageService {
    private final Connection connection;

    public UpdateMessageService() throws SQLException {
        connection = MaConnexion.getInstance().getConnection();
    }

    public void updateMessage(Forum message) throws SQLException {
        String sql = "UPDATE message_forum SET message = ? WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, message.getMessage());
            ps.setInt(2, message.getId());
            ps.executeUpdate();
        }
    }
}
