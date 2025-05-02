package models;

public class CommentaireLike {
    private int id;
    private int commentaireId;
    private int userId;

    public CommentaireLike() {
    }

    public CommentaireLike(int commentaireId, int userId) {
        this.commentaireId = commentaireId;
        this.userId = userId;
    }

    // Getters
    public int getId() {
        return id;
    }

    public int getCommentaireId() {
        return commentaireId;
    }

    public int getUserId() {
        return userId;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCommentaireId(int commentaireId) {
        this.commentaireId = commentaireId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}