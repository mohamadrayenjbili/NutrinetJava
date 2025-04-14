package models;

public class Programme {
    private int id;
    private String titre;
    private String type;
    private String videoUrl;
    private String image;
    private String auteur;
    private String description;

    // Constructeurs
    public Programme() {
    }

    public Programme(String titre, String type, String videoUrl, String image, String auteur, String description) {
        this.titre = titre;
        this.type = type;
        this.videoUrl = videoUrl;
        this.image = image;
        this.auteur = auteur;
        this.description = description;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitre() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getAuteur() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Programme{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", type='" + type + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                ", image='" + image + '\'' +
                ", auteur='" + auteur + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
