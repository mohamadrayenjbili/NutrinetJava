package controllers.Objective;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Objective;
import services.IObjectiveService;
import services.ObjectiveService;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AjouterObjectiveController implements Initializable {

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfTargetValue;

    @FXML
    private ComboBox<String> cbUnit;

    @FXML
    private DatePicker dpStartDate;

    @FXML
    private DatePicker dpEndDate;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnAnnuler;

    private IObjectiveService objectiveService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        objectiveService = new ObjectiveService();

        // Unités possibles, à adapter selon ton projet
        cbUnit.getItems().addAll("kg", "km", "minutes", "heures", "calories", "autre");

        // Ajouter les contrôles de validation pour les dates
        setupDateValidation();
    }

    private void setupDateValidation() {
        // Définir la date minimale comme aujourd'hui
        LocalDate today = LocalDate.now();

        // Empêcher de sélectionner des dates antérieures à aujourd'hui
        dpStartDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                setDisable(empty || date.isBefore(today));
            }
        });

        // Empêcher de sélectionner des dates antérieures à la date de début
        dpEndDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                boolean disableDate = empty || date.isBefore(today);
                if (dpStartDate.getValue() != null && !disableDate) {
                    disableDate = date.isBefore(dpStartDate.getValue());
                }

                setDisable(disableDate);
            }
        });

        // Mettre à jour les dates de fin disponibles quand la date de début change
        dpStartDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Si la date de fin est avant la nouvelle date de début, réinitialiser la date de fin
                if (dpEndDate.getValue() != null && dpEndDate.getValue().isBefore(newValue)) {
                    dpEndDate.setValue(null);
                }

                // Mettre à jour le contrôle de la date de fin
                dpEndDate.setDayCellFactory(picker -> new DateCell() {
                    @Override
                    public void updateItem(LocalDate date, boolean empty) {
                        super.updateItem(date, empty);
                        setDisable(empty || date.isBefore(newValue));
                    }
                });
            }
        });
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            if (validateFields()) {
                Objective objective = new Objective();
                objective.setName(tfName.getText());
                objective.setTargetValue(Double.parseDouble(tfTargetValue.getText()));
                objective.setUnit(cbUnit.getValue());
                objective.setStartDate(dpStartDate.getValue());
                objective.setEndDate(dpEndDate.getValue());

                objectiveService.ajouterObjective(objective);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Objectif ajouté avec succès !");
                clearFields();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout de l'objectif : " + e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        clearFields();
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (tfName.getText().isEmpty()) {
            errors.append("- Le nom est requis.\n");
        }

        if (tfTargetValue.getText().isEmpty()) {
            errors.append("- La valeur cible est requise.\n");
        } else {
            try {
                double val = Double.parseDouble(tfTargetValue.getText());
                if (val < 0) {
                    errors.append("- La valeur cible doit être Positive.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- La valeur cible doit être un nombre.\n");
            }
        }

        if (cbUnit.getValue() == null) {
            errors.append("- L'unité est requise.\n");
        }

        if (dpStartDate.getValue() == null) {
            errors.append("- La date de début est requise.\n");
        } else if (dpStartDate.getValue().isBefore(LocalDate.now())) {
            errors.append("- La date de début ne peut pas être antérieure à aujourd'hui.\n");
        }

        if (dpEndDate.getValue() == null) {
            errors.append("- La date de fin est requise.\n");
        } else if (dpStartDate.getValue() != null && dpEndDate.getValue().isBefore(dpStartDate.getValue())) {
            errors.append("- La date de fin doit être après la date de début.\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errors.toString());
            return false;
        }

        return true;
    }

    private void clearFields() {
        tfName.clear();
        tfTargetValue.clear();
        cbUnit.setValue(null);
        dpStartDate.setValue(null);
        dpEndDate.setValue(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}