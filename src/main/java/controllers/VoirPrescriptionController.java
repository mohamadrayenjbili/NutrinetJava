package src.main.java.controllers;

import Models.Prescription;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class VoirPrescriptionController {

    @FXML
    private Label lblNom;

    @FXML
    private Label lblPrenom;

    @FXML
    private Label lblDate;

    @FXML
    private Label lblObjectif;

    @FXML
    private Label lblProgramme;

    @FXML
    private void initialize() {
        System.out.println("VoirPrescriptionController initialisé");
        assert lblNom != null : "lblNom non injecté ! Vérifie fx:id.";
        assert lblPrenom != null : "lblPrenom non injecté ! Vérifie fx:id.";
    }


    public void initData(Prescription prescription) {
        // Vérification si l'objet prescription est null avant de l'utiliser
        if (prescription != null) {
            lblNom.setText(prescription.getNomP() != null ? prescription.getNomP() : "Nom non disponible");
            lblPrenom.setText(prescription.getPrenomP() != null ? prescription.getPrenomP() : "Prénom non disponible");
            lblDate.setText(prescription.getDate() != null ? prescription.getDate().toString() : "Date non disponible");
            lblObjectif.setText(prescription.getObjectif() != null ? prescription.getObjectif() : "Objectif non disponible");
            lblProgramme.setText(prescription.getProgramme() != null ? prescription.getProgramme() : "Programme non disponible");
        } else {
            // Si prescription est null, afficher des valeurs par défaut
            lblNom.setText("Prescription non disponible");
            lblPrenom.setText("Prescription non disponible");
            lblDate.setText("Prescription non disponible");
            lblObjectif.setText("Prescription non disponible");
            lblProgramme.setText("Prescription non disponible");
        }
    }
}
