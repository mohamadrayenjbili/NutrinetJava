package controllers;



import models.Prescription;
import services.PrescriptionService;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

public class UpdatePrescriptionController {
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker datePicker;
    @FXML private TextField objectifField;
    @FXML private TextArea programmeArea;

    @FXML private Label dateError;
    @FXML private Label objectifError;
    @FXML private Label programmeError;

    private PrescriptionService prescriptionService;
    private Prescription prescription;

    public void initData(Prescription prescription, PrescriptionService prescriptionService) {
        this.prescription = prescription;
        this.prescriptionService = prescriptionService;

        nomField.setText(prescription.getNomP());
        prenomField.setText(prescription.getPrenomP());
        datePicker.setValue(convertToLocalDate(prescription.getDate()));
        objectifField.setText(prescription.getObjectif());
        programmeArea.setText(prescription.getProgramme());

        nomField.setDisable(true);
        prenomField.setDisable(true);
    }

    private LocalDate convertToLocalDate(Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @FXML
    private void handleUpdatePrescription() {
        clearErrors();

        boolean valid = true;

        if (datePicker.getValue() == null) {
            dateError.setText("La date est requise.");
            valid = false;
        }

        if (objectifField.getText().isEmpty()) {
            objectifError.setText("L’objectif est requis.");
            valid = false;
        }

        if (programmeArea.getText().isEmpty()) {
            programmeError.setText("Le programme est requis.");
            valid = false;
        }

        if (!valid) return;

        try {
            prescription.setDate(Date.from(datePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            prescription.setObjectif(objectifField.getText());
            prescription.setProgramme(programmeArea.getText());

            boolean success = prescriptionService.updatePrescription(prescription);
            if (success) {
                closeWindow();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearErrors() {
        dateError.setText("");
        objectifError.setText("");
        programmeError.setText("");
    }

    @FXML
    private void handleAnnuler() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}

