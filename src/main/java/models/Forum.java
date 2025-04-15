package models;

import java.util.Date;

public class Forum {

    private int id;
    private String message;
    private Date date;
    private int likes;
    private int userId;

    public Forum() {
        this.date = new Date();
        this.likes = 0;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            throw new IllegalArgumentException("Le message ne peut pas être vide.");
        }
        if (message.length() < 5) {
            throw new IllegalArgumentException("Le message doit contenir au moins 5 caractères.");
        }
        if (message.length() > 1000) {
            throw new IllegalArgumentException("Le message ne peut pas dépasser 1000 caractères.");
        }
        this.message = message;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getLikes() {
        return likes;
    }

    public void like() {
        this.likes++;
    }

    public void unlike() {
        if (this.likes > 0) {
            this.likes--;
        }
    }

    public void toggleLike() {
        if (this.likes > 0) {
            this.likes--;
        } else {
            this.likes++;
        }
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setLikes(int likes) {
        if (likes < 0) {
            throw new IllegalArgumentException("Le nombre de likes ne peut pas être négatif.");
        }
        this.likes = likes;
    }

}
