package controllers.Objective;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import models.Objective;
import services.IObjectiveService;
import services.ObjectiveService;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.stage.Stage;

public class ModifierObjectiveController implements Initializable {

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
    private Button btnModifier;

    @FXML
    private Button btnAnnuler;

    private IObjectiveService objectiveService;
    private Objective objective;
    private LocalDate originalStartDate; // Pour garder la date de début originale

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        objectiveService = new ObjectiveService();

        // Unités possibles, à adapter selon ton projet
        cbUnit.getItems().addAll("kg", "km", "minutes", "heures", "calories", "autre");
    }

    public void initData(Objective objective) {
        this.objective = objective;
        this.originalStartDate = objective.getStartDate(); // Sauvegarder la date de début originale

        // Remplir les champs avec les données de l'objectif
        tfName.setText(objective.getName());
        tfTargetValue.setText(String.valueOf(objective.getTargetValue()));
        cbUnit.setValue(objective.getUnit());
        dpStartDate.setValue(objective.getStartDate());
        dpEndDate.setValue(objective.getEndDate());

        // Configurer la validation des dates
        setupDateValidation();
    }

    private void setupDateValidation() {
        LocalDate today = LocalDate.now();

        // Pour la modification, on autorise de garder la date de début originale même si elle est dans le passé
        // mais on empêche de la changer à une date antérieure à aujourd'hui
        dpStartDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                // On permet la date originale ou les dates futures
                boolean isOriginalDate = date.isEqual(originalStartDate);
                setDisable(empty || (!isOriginalDate && date.isBefore(today)));
            }
        });

        // La date de fin doit être après la date de début
        dpEndDate.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (dpStartDate.getValue() != null) {
                    setDisable(empty || date.isBefore(dpStartDate.getValue()));
                } else {
                    setDisable(empty);
                }
            }
        });

        // Mettre à jour les dates de fin disponibles quand la date de début change
        dpStartDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                // Si la date de fin est avant la nouvelle date de début, réinitialiser la date de fin
                if (dpEndDate.getValue() != null && dpEndDate.getValue().isBefore(newValue)) {
                    dpEndDate.setValue(newValue);
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
    private void handleModifier(ActionEvent event) {
        try {
            if (validateFields()) {
                objective.setName(tfName.getText());
                objective.setTargetValue(Double.parseDouble(tfTargetValue.getText()));
                objective.setUnit(cbUnit.getValue());
                objective.setStartDate(dpStartDate.getValue());
                objective.setEndDate(dpEndDate.getValue());

                objectiveService.updateObjective(objective);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Objectif modifié avec succès !");

                // Fermer la fenêtre
                ((Stage) btnModifier.getScene().getWindow()).close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification de l'objectif : " + e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        // Fermer la fenêtre
        ((Stage) btnAnnuler.getScene().getWindow()).close();
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
                if (val <= 0) {
                    errors.append("- La valeur cible doit être supérieure à 0.\n");
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
        } else {
            // Pour la modification, on vérifie si la date de début a été changée
            if (!dpStartDate.getValue().isEqual(originalStartDate) && dpStartDate.getValue().isBefore(LocalDate.now())) {
                errors.append("- Si vous modifiez la date de début, elle ne peut pas être antérieure à aujourd'hui.\n");
            }
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

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}