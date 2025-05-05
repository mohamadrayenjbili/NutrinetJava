package controllers.Objective;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Objective;
import models.Performance;
import services.IPerformanceService;
import services.PerformanceService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherPerformanceController implements Initializable {

    @FXML
    private Label lblObjectiveName;
    @FXML
    private Label lblObjectiveDetails;
    @FXML
    private ListView<Performance> listPerformances;
    @FXML
    private TextField tfRecherche;
    @FXML
    private Button btnRecherche;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btnActualiser;
    @FXML
    private Button btnRetour;
    @FXML
    private Button btnGraphique;

    private IPerformanceService performanceService;
    private ObservableList<Performance> performancesList;
    private FilteredList<Performance> filteredList;
    private Objective currentObjective;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        performanceService = new PerformanceService();
        listPerformances.setCellFactory(param -> new PerformanceListCell());
    }

    public void initData(Objective objective) {
        this.currentObjective = objective;
        updateObjectiveInfo();
        chargerPerformances();
        setupSearch();
    }



    private void updateObjectiveInfo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblObjectiveName.setText(currentObjective.getName());
        lblObjectiveDetails.setText(String.format("Valeur cible: %.2f %s | Du %s au %s",
                currentObjective.getTargetValue(),
                currentObjective.getUnit(),
                currentObjective.getStartDate().format(formatter),
                currentObjective.getEndDate().format(formatter)))   ;
    }

    private void chargerPerformances() {
        try {
            List<Performance> performances = performanceService.getPerformancesByObjectiveId(currentObjective.getId());
            performancesList = FXCollections.observableArrayList(performances);
            listPerformances.setItems(performancesList);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des performances : " + e.getMessage());
        }
    }

    private void setupSearch() {
        filteredList = new FilteredList<>(performancesList, p -> true);
        listPerformances.setItems(filteredList);

        tfRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(performance -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return performance.getMetricName().toLowerCase().contains(lowerCaseFilter) ||
                        (performance.getNotes() != null && performance.getNotes().toLowerCase().contains(lowerCaseFilter));
            });
        });
    }

    @FXML
    private void handleActualiser(ActionEvent event) {
        chargerPerformances();
        tfRecherche.clear();
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        openPerformanceWindow("/AjouterPerformance.fxml", "Ajouter une performance");
    }

    @FXML
    private void handleRecherche(ActionEvent event) {
        // La recherche est déjà gérée par le listener, cette méthode peut rester vide
        // ou être utilisée pour des fonctionnalités supplémentaires
    }

    @FXML
    private void handleVoirGraphique(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PerformanceChart.fxml"));
            Parent root = loader.load();

            PerformanceChartController controller = loader.getController();
            controller.initData(currentObjective);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Graphique des Performances");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir le graphique");
        }
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherObjective.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Gestion des Objectifs");
            stage.show();

            ((Stage) btnRetour.getScene().getWindow()).close();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour aux objectifs");
        }
    }

    private void openPerformanceWindow(String fxmlFile, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            Parent root = loader.load();

            if (fxmlFile.equals("/AjouterPerformance.fxml")) {
                AjouterPerformanceController controller = loader.getController();
                controller.initData(currentObjective);
            } else if (fxmlFile.equals("/PerformanceChart.fxml")) {
                PerformanceChartController controller = loader.getController();
                controller.initData(currentObjective);
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            chargerPerformances();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private class PerformanceListCell extends ListCell<Performance> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        @Override
        protected void updateItem(Performance performance, boolean empty) {
            super.updateItem(performance, empty);

            if (empty || performance == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox vbox = new VBox(5);

                Label lblMetric = new Label(String.format("%s: %.2f %s",
                        performance.getMetricName(),
                        performance.getValue(),
                        performance.getUnit()));
                lblMetric.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                Label lblDetails = new Label(String.format("Le %s | %s",
                        performance.getDate().format(formatter),
                        performance.getNotes() != null ? performance.getNotes() : ""));
                lblDetails.setStyle("-fx-font-size: 12px;");

                Button btnModifier = new Button("Modifier");
                btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                btnModifier.setOnAction(e -> handleModifier(performance));

                Button btnSupprimer = new Button("Supprimer");
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnSupprimer.setOnAction(e -> handleSupprimer(performance));

                HBox buttons = new HBox(10, btnModifier, btnSupprimer);
                buttons.setAlignment(Pos.CENTER_RIGHT);

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label lblId = new Label("ID: " + performance.getId());
                lblId.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                HBox bottomBox = new HBox(lblId, spacer, buttons);
                bottomBox.setAlignment(Pos.CENTER_LEFT);

                vbox.getChildren().addAll(lblMetric, lblDetails, bottomBox);
                vbox.setStyle("-fx-padding: 5; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0;");

                setGraphic(vbox);
            }
        }

        private void handleModifier(Performance performance) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierPerformance.fxml"));
                Parent root = loader.load();

                ModifierPerformanceController controller = loader.getController();
                controller.initData(currentObjective, performance);

                Stage stage = new Stage();
                stage.setTitle("Modifier une performance");
                stage.setScene(new Scene(root));
                stage.showAndWait();

                chargerPerformances();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre de modification");
            }
        }

        private void handleSupprimer(Performance performance) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette performance ?");

            if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
                try {
                    performanceService.deletePerformance(performance.getId());
                    chargerPerformances();
                } catch (SQLException e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression");
                }
            }
        }
    }
}