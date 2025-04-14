package controllers.Objective;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Objective;
import models.Performance;
import services.IPerformanceService;
import services.PerformanceService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AjouterPerformanceController implements Initializable {

    @FXML
    private Label lblObjectiveName;
    @FXML
    private Label lblObjectiveDetails;
    @FXML
    private TextField tfMetricName;
    @FXML
    private TextField tfValue;
    @FXML
    private ComboBox<String> cbUnit;
    @FXML
    private DatePicker dpDate;
    @FXML
    private ComboBox<Integer> cbHour;
    @FXML
    private ComboBox<Integer> cbMinute;
    @FXML
    private TextArea taNotes;
    @FXML
    private Button btnAnnuler;
    @FXML
    private Button btnAjouter;

    private IPerformanceService performanceService;
    private Objective currentObjective;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        performanceService = new PerformanceService();

        // Initialiser les ComboBox pour l'heure
        for (int i = 0; i < 24; i++) {
            cbHour.getItems().add(i);
        }
        for (int i = 0; i < 60; i++) {
            cbMinute.getItems().add(i);
        }

        // Sélectionner l'heure actuelle par défaut
        cbHour.getSelectionModel().select(LocalTime.now().getHour());
        cbMinute.getSelectionModel().select(LocalTime.now().getMinute());

        // Sélectionner la date actuelle par défaut
        dpDate.setValue(LocalDate.now());
    }

    public void initData(Objective objective) {
        this.currentObjective = objective;

        // Afficher les informations de l'objectif
        lblObjectiveName.setText(objective.getName());
        lblObjectiveDetails.setText(String.format("Unité cible: %s", objective.getUnit()));

        // Initialiser l'unité avec celle de l'objectif
        cbUnit.setItems(FXCollections.observableArrayList(objective.getUnit()));
        cbUnit.getSelectionModel().selectFirst();
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        ((Stage) btnAnnuler.getScene().getWindow()).close();
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            // Validation des champs
            if (tfMetricName.getText().isEmpty()) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Le nom de la métrique est obligatoire");
                return;
            }

            double value;
            try {
                value = Double.parseDouble(tfValue.getText());
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La valeur doit être un nombre");
                return;
            }

            if (dpDate.getValue() == null) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "La date est obligatoire");
                return;
            }

            // Création de la date et heure
            LocalDate date = dpDate.getValue();
            LocalTime time = LocalTime.of(cbHour.getValue(), cbMinute.getValue());
            LocalDateTime dateTime = LocalDateTime.of(date, time);

            // Création de la performance
            Performance performance = new Performance(
                    currentObjective.getId(),
                    dateTime,
                    taNotes.getText(),
                    tfMetricName.getText(),
                    value,
                    cbUnit.getValue()
            );

            // Sauvegarde en base de données
            performanceService.ajouterPerformance(performance);

            // Fermeture de la fenêtre
            ((Stage) btnAjouter.getScene().getWindow()).close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de la performance : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}