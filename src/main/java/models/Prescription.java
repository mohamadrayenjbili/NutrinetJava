package models;



import java.util.Date;

public class Prescription {
    private int id;
    private int consultationId;
    private String nomP;
    private String prenomP;
    private Date date;
    private String objectif;
    private String programme;

    public Prescription(int id, int consultationId, String nomP, String prenomP, Date date, String objectif, String programme) {
        this.id = id;
        this.consultationId = consultationId;
        this.nomP = nomP;
        this.prenomP = prenomP;
        this.date = date;
        this.objectif = objectif;
        this.programme = programme;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getConsultationId() {
        return consultationId;
    }

    public void setConsultationId(int consultationId) {
        this.consultationId = consultationId;
    }

    public String getNomP() {
        return nomP;
    }

    public void setNomP(String nomP) {
        this.nomP = nomP;
    }

    public String getPrenomP() {
        return prenomP;
    }

    public void setPrenomP(String prenomP) {
        this.prenomP = prenomP;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getObjectif() {
        return objectif;
    }

    public void setObjectif(String objectif) {
        this.objectif = objectif;
    }

    public String getProgramme() {
        return programme;
    }

    public void setProgramme(String programme) {
        this.programme = programme;
    }
}
