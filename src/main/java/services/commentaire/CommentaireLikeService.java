package services.commentaire;
import utils.MaConnexion;
import java.sql.*;

public class CommentaireLikeService implements ICommentaireLikeService {
    private Connection connection;

    public CommentaireLikeService() {
        try {
            connection = MaConnexion.getInstance().getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasUserLiked(int commentaireId, int userId) {
        String query = "SELECT * FROM commentaire_likes WHERE commentaire_id = ? AND user_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, commentaireId);
            pstmt.setInt(2, userId);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void toggleLike(int commentaireId, int userId) {
        if (hasUserLiked(commentaireId, userId)) {
            String query = "DELETE FROM commentaire_likes WHERE commentaire_id = ? AND user_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, commentaireId);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            String query = "INSERT INTO commentaire_likes (commentaire_id, user_id) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(query)) {
                pstmt.setInt(1, commentaireId);
                pstmt.setInt(2, userId);
                pstmt.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public int getLikeCount(int commentaireId) {
        String query = "SELECT COUNT(*) FROM commentaire_likes WHERE commentaire_id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, commentaireId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}