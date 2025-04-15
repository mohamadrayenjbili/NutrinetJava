package src.main.java.controllers;

import Models.Consultation;
import Models.Prescription;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import src.main.java.services.PrescriptionService;

import java.sql.SQLException;
import java.time.ZoneId;
import java.util.Date;

public class AjouterPrescriptionController {
    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField objectifField;
    @FXML
    private TextArea programmeArea;

    // Error labels
    @FXML
    private Label nomError;
    @FXML
    private Label prenomError;
    @FXML
    private Label dateError;
    @FXML
    private Label objectifError;
    @FXML
    private Label programmeError;

    private PrescriptionService prescriptionService;
    private Consultation consultation;

    public void initData(Consultation consultation, PrescriptionService prescriptionService) {
        this.consultation = consultation;
        this.prescriptionService = prescriptionService;

        // Set default values from consultation
        nomField.setText(consultation.getNom());
        prenomField.setText(consultation.getPrenom());
        nomField.setDisable(true);
        prenomField.setDisable(true);
    }

    @FXML
    private void handleAjouterPrescription() {
        // Reset error messages
        resetErrorMessages();

        boolean isValid = true;

        // Validate nom
        if (nomField.getText().isEmpty()) {
            nomError.setText("Le nom est obligatoire.");
            isValid = false;
        }

        // Validate prenom
        if (prenomField.getText().isEmpty()) {
            prenomError.setText("Le prénom est obligatoire.");
            isValid = false;
        }

        // Validate date
        if (datePicker.getValue() == null) {
            dateError.setText("La date est obligatoire.");
            isValid = false;
        }

        // Validate objectif
        if (objectifField.getText().isEmpty()) {
            objectifError.setText("L'objectif est obligatoire.");
            isValid = false;
        }

        // Validate programme
        if (programmeArea.getText().isEmpty()) {
            programmeError.setText("Le programme est obligatoire.");
            isValid = false;
        }

        // If all validations pass, proceed with the addition
        if (isValid) {
            try {
                Prescription prescription = new Prescription(
                        0, // id will be auto-generated
                        consultation.getId(),
                        nomField.getText(),
                        prenomField.getText(),
                        Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()),
                        objectifField.getText(),
                        programmeArea.getText()
                );

                boolean success = prescriptionService.addPrescription(prescription);
                if (success) {
                    closeWindow();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }

    // Reset all error messages
    private void resetErrorMessages() {
        nomError.setText("");
        prenomError.setText("");
        dateError.setText("");
        objectifError.setText("");
        programmeError.setText("");
    }
}
