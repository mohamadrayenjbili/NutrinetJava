package models;

import java.sql.Date;

public class Commande {
    private int id;
    private String nomC;
    private String mail;
    private String adress;
    private Date dateC;
    private String status;
    private String methodeDePaiement;
    private String transactionId;


    // Constructeurs
    public Commande() {
    }

    public Commande(String nomC, String mail, String adress, Date dateC,
                    String status, String methodeDePaiement) {
        this.nomC = nomC;
        this.mail = mail;
        this.adress = adress;
        this.dateC = dateC;
        this.status = status;
        this.methodeDePaiement = methodeDePaiement;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomC() {
        return nomC;
    }

    public void setNomC(String nomC) {
        this.nomC = nomC;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAdress() {
        return adress;
    }

    public void setAdress(String adress) {
        this.adress = adress;
    }

    public Date getDateC() {
        return dateC;
    }

    public void setDateC(Date dateC) {
        this.dateC = dateC;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMethodeDePaiement() {
        return methodeDePaiement;
    }

    public void setMethodeDePaiement(String methodeDePaiement) {
        this.methodeDePaiement = methodeDePaiement;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    @Override
    public String toString() {
        return "Commande{" +
                "id=" + id +
                ", nomC='" + nomC + '\'' +
                ", mail='" + mail + '\'' +
                ", status='" + status + '\'' +
                ", methodeDePaiement='" + methodeDePaiement + '\'' +
                '}';
    }
}