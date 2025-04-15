package src.main.java.controllers;

import Models.Consultation;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import src.main.java.services.ConsultationService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class UpdateConsultationController implements Initializable {

    @FXML
    private TextField nomField;
    @FXML
    private TextField prenomField;
    @FXML
    private TextField telField;
    @FXML
    private TextField mailField;
    @FXML
    private ComboBox<String> typeComboBox;
    @FXML
    private DatePicker datePicker;
    @FXML
    private ComboBox<String> heureComboBox;
    @FXML
    private ComboBox<String> statusComboBox;
    @FXML
    private TextArea noteTextArea;

    @FXML
    private Label nomErrorLabel;
    @FXML
    private Label prenomErrorLabel;
    @FXML
    private Label telErrorLabel;
    @FXML
    private Label mailErrorLabel;
    @FXML
    private Label dateErrorLabel;
    @FXML
    private Label heureErrorLabel;
    @FXML
    private Label statusErrorLabel;

    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private Consultation consultation;
    private ConsultationService consultationService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        typeComboBox.getItems().addAll("Consultation médicale", "Examen", "Suivi", "Urgence");

        for (int hour = 8; hour <= 18; hour++) {
            heureComboBox.getItems().add(String.format("%02d:00", hour));
            if (hour < 18) {
                heureComboBox.getItems().add(String.format("%02d:30", hour));
            }
        }

        statusComboBox.getItems().addAll("En attente", "Faite", "Annulé");

        saveButton.setOnAction(event -> saveConsultation());
        cancelButton.setOnAction(event -> closeWindow());
    }

    public void initData(Consultation consultation, ConsultationService service) {
        this.consultation = consultation;
        this.consultationService = service;

        nomField.setText(consultation.getNom());
        prenomField.setText(consultation.getPrenom());
        telField.setText(String.valueOf(consultation.getTel()));
        mailField.setText(consultation.getMail());
        typeComboBox.setValue(consultation.getType());

        LocalDateTime dateTime = consultation.getDate();
        if (dateTime != null) {
            datePicker.setValue(dateTime.toLocalDate());
            String heure = String.format("%02d:%02d", dateTime.getHour(), dateTime.getMinute());
            heureComboBox.setValue(heure);
        }

        statusComboBox.setValue(consultation.getStatus());
        noteTextArea.setText(consultation.getNote());
    }

    private void saveConsultation() {
        resetErrorLabels();
        boolean isValid = true;

        if (nomField.getText().isEmpty()) {
            nomErrorLabel.setText("Nom est obligatoire");
            nomErrorLabel.setVisible(true);
            isValid = false;
        } else if (!nomField.getText().matches("[a-zA-Z]+")) {  // Check for non-numeric input
            nomErrorLabel.setText("Nom ne peut pas contenir de chiffres");
            nomErrorLabel.setVisible(true);
            isValid = false;
        } else {
            nomErrorLabel.setVisible(false);  // Hide error label if valid
        }


        if (prenomField.getText().isEmpty()) {
            prenomErrorLabel.setText("Prénom est obligatoire");
            prenomErrorLabel.setVisible(true);
            isValid = false;
        } else if (!prenomField.getText().matches("[a-zA-Z]+")) {  // Check for non-numeric input
            prenomErrorLabel.setText("Prénom ne peut pas contenir de chiffres");
            prenomErrorLabel.setVisible(true);
            isValid = false;
        } else {
            prenomErrorLabel.setVisible(false);  // Hide error label if valid
        }

        if (telField.getText().isEmpty() || !telField.getText().matches("\\d{8}")) {
            telErrorLabel.setText("Numéro de téléphone obligatoire et doit contenir exactement 8 chiffres");
            telErrorLabel.setVisible(true);
            isValid = false;
        } else {
            telErrorLabel.setVisible(false);  // Hide error label if valid
        }


        if (mailField.getText().isEmpty() || !mailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mailErrorLabel.setText("Email invalide");
            mailErrorLabel.setVisible(true);
            isValid = false;
        }

        if (typeComboBox.getValue() == null) {
            statusErrorLabel.setText("Type de consultation est obligatoire");
            statusErrorLabel.setVisible(true);
            isValid = false;
        }

        if (datePicker.getValue() == null) {
            dateErrorLabel.setText("Date de consultation est obligatoire");
            dateErrorLabel.setVisible(true);
            isValid = false;
        } else {
            LocalDate selectedDate = datePicker.getValue();
            LocalDate currentDate = LocalDate.now();

            // Check if the selected date is in the past
            if (selectedDate.isBefore(currentDate)) {
                dateErrorLabel.setText("La date ne peut pas être dans le passé");
                dateErrorLabel.setVisible(true);
                isValid = false;
            } else {
                dateErrorLabel.setVisible(false);  // Hide error if the date is valid
            }
        }

        if (heureComboBox.getValue() == null) {
            heureErrorLabel.setText("Heure de consultation est obligatoire");
            heureErrorLabel.setVisible(true);
            isValid = false;
        } else {
            String selectedTime = heureComboBox.getValue();  // Get the selected time from the combo box

            // Check if the time is valid and within the range of 8 AM and 6 PM
            LocalTime time = LocalTime.parse(selectedTime);  // Assuming the time format is HH:mm

            if (time.isBefore(LocalTime.of(8, 0)) || time.isAfter(LocalTime.of(18, 0))) {
                heureErrorLabel.setText("L'heure doit être entre 8h et 18h");
                heureErrorLabel.setVisible(true);
                isValid = false;
            } else {
                heureErrorLabel.setVisible(false);  // Hide error if the time is valid
            }
        }

        if (statusComboBox.getValue() == null) {
            statusErrorLabel.setText("Statut est obligatoire");
            statusErrorLabel.setVisible(true);
            isValid = false;
        }

        if (isValid) {
            try {
                int tel = Integer.parseInt(telField.getText());

                LocalDate date = datePicker.getValue();
                String[] heureMinute = heureComboBox.getValue().split(":");
                LocalTime time = LocalTime.of(Integer.parseInt(heureMinute[0]), Integer.parseInt(heureMinute[1]));
                LocalDateTime dateTime = LocalDateTime.of(date, time);

                consultation.setNom(nomField.getText());
                consultation.setPrenom(prenomField.getText());
                consultation.setTel(tel);
                consultation.setMail(mailField.getText());
                consultation.setType(typeComboBox.getValue());
                consultation.setDate(dateTime);
                consultation.setStatus(statusComboBox.getValue());
                consultation.setNote(noteTextArea.getText());

                consultationService.updateConsultation(consultation);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Consultation mise à jour");
                alert.setHeaderText("Mise à jour effectuée avec succès !");
                alert.show();

                closeWindow();

            } catch (SQLException e) {
                showAlert("Erreur SQL", "Erreur lors de la mise à jour: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    private void resetErrorLabels() {
        nomErrorLabel.setVisible(false);
        prenomErrorLabel.setVisible(false);
        telErrorLabel.setVisible(false);
        mailErrorLabel.setVisible(false);
        dateErrorLabel.setVisible(false);
        heureErrorLabel.setVisible(false);
        statusErrorLabel.setVisible(false);
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }
}
