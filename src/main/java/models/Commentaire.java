package models;

import java.time.LocalDateTime;

public class Commentaire {
    private int id;
    private int programmeId;
    private int auteurId;
    private Integer parentId; // peut être null
    private String contenu;
    private LocalDateTime createdAt;

    // ✅ Constructeur par défaut
    public Commentaire() {
    }

    public Commentaire(int id, int programmeId, int auteurId, Integer parentId, String contenu, LocalDateTime createdAt) {
        this.id = id;
        this.programmeId = programmeId;
        this.auteurId = auteurId;
        this.parentId = parentId;
        this.contenu = contenu;
        this.createdAt = createdAt;
    }

    public Commentaire(int programmeId, int auteurId, Integer parentId, String contenu, LocalDateTime createdAt) {
        this.programmeId = programmeId;
        this.auteurId = auteurId;
        this.parentId = parentId;
        this.contenu = contenu;
        this.createdAt = createdAt;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getProgrammeId() { return programmeId; }
    public void setProgrammeId(int programmeId) { this.programmeId = programmeId; }

    public int getAuteurId() { return auteurId; }
    public void setAuteurId(int auteurId) { this.auteurId = auteurId; }

    public Integer getParentId() { return parentId; }
    public void setParentId(Integer parentId) { this.parentId = parentId; }

    public String getContenu() { return contenu; }
    public void setContenu(String contenu) { this.contenu = contenu; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}


