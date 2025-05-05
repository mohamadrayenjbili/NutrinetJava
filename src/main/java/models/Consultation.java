package models;



import java.time.DateTimeException;
import java.time.LocalDateTime;

public class Consultation {
    private int id;
    private String nom;
    private String prenom;
    private int tel;
    private String mail;
    private String type;
    private  LocalDateTime date;
    private String note;
    private Integer userId;
    private String status;

    // Constructors
    public Consultation() {
    }

    public Consultation(String nom, String prenom, int tel, String mail, String type, LocalDateTime date, String note, Integer userId, String status) {
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.mail = mail;
        this.type = type;
        this.date = date;
        this.note = note;
        this.userId = userId;
        this.status = status;
    }




    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public int getTel() {
        return tel;
    }

    public void setTel(int tel) {
        this.tel = tel;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Consultation{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", tel=" + tel +
                ", mail='" + mail + '\'' +
                ", type='" + type + '\'' +
                ", date=" + date +
                ", note='" + note + '\'' +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                '}';
    }
}
