package src.main.java.controllers;


import Models.Consultation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import src.main.java.services.ConsultationService;
import utils.MaConnexion;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AjouterConsultationController {

    @FXML
    private TextField nomTextField;
    @FXML
    private TextField prenomTextField;
    @FXML
    private TextField telTextField;
    @FXML
    private TextField mailTextField;
    @FXML
    private ComboBox<String> type;

    @FXML
    private DatePicker date;
    @FXML
    private ComboBox<String> heureComboBox;
    @FXML
    private TextField noteTextField;
    @FXML
    private TextField statusTextField;

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

    private Connection connection;
    private ConsultationService consultationService;

    public AjouterConsultationController() throws SQLException {
        this.connection = MaConnexion.getInstance().getConnection();
        this.consultationService = new ConsultationService(connection);
    }

    @FXML
    private void initialize() {
        if (type != null) {
            type.getItems().addAll("en ligne", "présentiel");
        }

        for (int hour = 8; hour < 18; hour++) {
            for (int minute : new int[]{0, 30}) {
                String time = String.format("%02d:%02d", hour, minute);
                heureComboBox.getItems().add(time);
            }
        }
    }

    @FXML
    void ajouterConsultationAction(ActionEvent event) {
        resetErrorLabels();
        boolean isValid = true;

        LocalDate selectedDate = date.getValue();
        String selectedTime = heureComboBox.getValue();

        if (nomTextField.getText().isEmpty() || !nomTextField.getText().matches("[a-zA-Z]+")) {
            nomErrorLabel.setText("ne doit contenir que des lettres");
            nomErrorLabel.setVisible(true);
            isValid = false;
        }

        if (prenomTextField.getText().isEmpty() || !prenomTextField.getText().matches("[a-zA-Z]+")) {
            prenomErrorLabel.setText("ne doit contenir que des lettres");
            prenomErrorLabel.setVisible(true);
            isValid = false;
        }


        if (!telTextField.getText().matches("\\d{8}")) {
            telErrorLabel.setText("Le numéro doit contenir exactement 8 chiffres");
            telErrorLabel.setVisible(true);
            isValid = false;
        }

        if (mailTextField.getText().isEmpty() || !mailTextField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            mailErrorLabel.setText("Email invalide");
            mailErrorLabel.setVisible(true);
            isValid = false;
        }

        if (type.getValue() == null) {
            statusErrorLabel.setText("Type de consultation est obligatoire");
            statusErrorLabel.setVisible(true);
            isValid = false;
        }

        if (selectedDate == null) {
            dateErrorLabel.setText("Date est obligatoire");
            dateErrorLabel.setVisible(true);
            isValid = false;
        } else if (selectedDate.isBefore(LocalDate.now())) {
            dateErrorLabel.setText("La date ne peut pas être dans le passé");
            dateErrorLabel.setVisible(true);
            isValid = false;
        }

        if (selectedTime == null) {
            heureErrorLabel.setText("Heure est obligatoire");
            heureErrorLabel.setVisible(true);
            isValid = false;
        }

        LocalDateTime dateTime = null;
        if (selectedDate != null && selectedTime != null) {
            LocalTime time = LocalTime.parse(selectedTime);
            dateTime = LocalDateTime.of(selectedDate, time);
        }

        if (statusTextField.getText().isEmpty()) {
            statusErrorLabel.setText("Statut est obligatoire");
            statusErrorLabel.setVisible(true);
            isValid = false;
        }

        if (isValid) {
            try {
                int tel = Integer.parseInt(telTextField.getText());

                Consultation consultation = new Consultation(
                        nomTextField.getText(),
                        prenomTextField.getText(),
                        tel,
                        mailTextField.getText(),
                        type.getValue(),
                        dateTime,
                        noteTextField.getText(),
                        null,
                        statusTextField.getText()
                );

                consultationService.addConsultation(consultation);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Consultation ajoutée");
                alert.setHeaderText("Ajout effectué avec succès !");
                alert.show();

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("DetailsConsultation.fxml"));
                    Parent root = loader.load();
                    nomTextField.getScene().setRoot(root);
                } catch (IOException e) {
                    System.out.println("Erreur lors du chargement de la vue DetailsConsultation.fxml : " + e.getMessage());
                }

            } catch (SQLException e) {
                showAlert("Erreur SQL", "Erreur lors de l'ajout: " + e.getMessage(), Alert.AlertType.ERROR);
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

    private void clearFields() {
        nomTextField.clear();
        prenomTextField.clear();
        telTextField.clear();
        mailTextField.clear();
        type.setValue(null);
        date.setValue(null);
        noteTextField.clear();
        statusTextField.clear();
    }

    @FXML
    private void goToDetailsConsultation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("DetailsConsultation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void AjouterPrescriptionController(ActionEvent actionEvent) {
        // Méthode à compléter si nécessaire
    }

    @FXML
    private void goToBackConsultation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("BackConsultation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.out.println("Erreur lors du chargement de la vue BackConsultation.fxml : " + e.getMessage());
        }
    }


}
