package models;

import java.time.LocalDateTime;

public class Reclamation {
    private int id;
    private String sujet;
    private String message;
    private int note;
    private User user;
    private String reponse;
    private String status;
    private LocalDateTime updatedAt;
    private String attachmentFile;  // Nouveau champ pour stocker le chemin de l'image attachée

    public Reclamation() {}

    public Reclamation(String sujet, String message, int note, User user, String status, LocalDateTime updatedAt, String attachmentFile) {
        this.sujet = sujet;
        this.message = message;
        this.note = note;
        this.user = user;
        this.status = status;
        this.updatedAt = updatedAt;
        this.attachmentFile = attachmentFile;
    }

    public Reclamation(int id, String sujet, String message, int note, User user, String status, LocalDateTime updatedAt, String attachmentFile, String reponse) {
        this(sujet, message, note, user, status, updatedAt, attachmentFile);
        this.id = id;
        this.reponse = reponse;
    }
    // Getters et Setters

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public String getSujet() { return sujet; }

    public void setSujet(String sujet) { this.sujet = sujet; }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public int getNote() { return note; }

    public void setNote(int note) { this.note = note; }

    public User getUser() { return user; }

    public void setUser(User user) { this.user = user; }

    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getAttachmentFile() { return attachmentFile; }

    public String getReponse() {
        return reponse;
    }

    public void setReponse(String reponse) {
        this.reponse = reponse;
    }

    public void setAttachmentFile(String attachmentFile) { this.attachmentFile = attachmentFile; }
}
