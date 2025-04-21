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

public class ModifierPerformanceController implements Initializable {

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
    private Button btnModifier;

    private IPerformanceService performanceService;
    private Objective currentObjective;
    private Performance currentPerformance;

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
    }

    public void initData(Objective objective, Performance performance) {
        this.currentObjective = objective;
        this.currentPerformance = performance;

        // Afficher les informations de l'objectif
        lblObjectiveName.setText(objective.getName());
        lblObjectiveDetails.setText(String.format("Unité cible: %s", objective.getUnit()));

        // Initialiser l'unité avec celle de l'objectif
        cbUnit.setItems(FXCollections.observableArrayList(objective.getUnit()));

        // Remplir les champs avec les données de la performance
        tfMetricName.setText(performance.getMetricName());
        tfValue.setText(String.valueOf(performance.getValue()));
        cbUnit.getSelectionModel().select(performance.getUnit());
        dpDate.setValue(performance.getDate().toLocalDate());
        cbHour.getSelectionModel().select(performance.getDate().getHour());
        cbMinute.getSelectionModel().select(performance.getDate().getMinute());
        taNotes.setText(performance.getNotes());
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        ((Stage) btnAnnuler.getScene().getWindow()).close();
    }

    @FXML
    private void handleModifier(ActionEvent event) {
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

            // Mise à jour de la performance
            currentPerformance.setMetricName(tfMetricName.getText());
            currentPerformance.setValue(value);
            currentPerformance.setUnit(cbUnit.getValue());
            currentPerformance.setDate(dateTime);
            currentPerformance.setNotes(taNotes.getText());

            // Mise à jour en base de données
            performanceService.updatePerformance(currentPerformance);

            // Fermeture de la fenêtre
            ((Stage) btnModifier.getScene().getWindow()).close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification de la performance : " + e.getMessage());
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