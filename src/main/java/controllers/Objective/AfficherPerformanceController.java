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
import controllers.Objective.AfficherObjectiveController;

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

    private IPerformanceService performanceService;
    private ObservableList<Performance> performancesList;
    private FilteredList<Performance> filteredList;
    private Objective currentObjective;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        performanceService = new PerformanceService();

        // Configuration de la ListView avec une cellule personnalisée
        listPerformances.setCellFactory(param -> new PerformanceListCell());
    }

    public void initData(Objective objective) {
        this.currentObjective = objective;

        // Afficher les informations de l'objectif
        lblObjectiveName.setText(objective.getName());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblObjectiveDetails.setText(String.format("Valeur cible: %.2f %s | Du %s au %s",
                objective.getTargetValue(),
                objective.getUnit(),
                objective.getStartDate().format(formatter),
                objective.getEndDate().format(formatter)));

        // Charger les performances pour cet objectif
        chargerPerformances();

        // Configurer la recherche
        setupSearch();
    }

    private void setupSearch() {
        // Initialiser la liste filtrée
        filteredList = new FilteredList<>(performancesList, p -> true);

        // Mettre à jour la ListView avec la liste filtrée
        listPerformances.setItems(filteredList);

        // Ajouter un listener pour filtrer automatiquement quand le texte de recherche change
        tfRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(performance -> {
                // Si le champ de recherche est vide, afficher toutes les performances
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Rechercher par nom de métrique
                if (performance.getMetricName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                // Rechercher par notes
                if (performance.getNotes() != null && performance.getNotes().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });
    }

    private void chargerPerformances() {
        try {
            List<Performance> performances = performanceService.getPerformancesByObjectiveId(currentObjective.getId());
            performancesList = FXCollections.observableArrayList(performances);
            listPerformances.setItems(performancesList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des performances : " + e.getMessage());
        }
    }

    @FXML
    private void handleActualiser(ActionEvent event) {
        chargerPerformances();
        tfRecherche.clear();
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPerformance.fxml"));
            Parent root = loader.load();

            AjouterPerformanceController controller = loader.getController();
            controller.initData(currentObjective);

            Stage stage = new Stage();
            stage.setTitle("Ajouter une performance");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();

            // Rafraîchir la liste après ajout
            chargerPerformances();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void handleRecherche(ActionEvent event) {
        // La recherche est déjà gérée par le listener, cette méthode peut rester vide
        // ou être utilisée pour des fonctionnalités supplémentaires
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherObjective.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Gestion des Objectifs");
            stage.setScene(new Scene(root));
            stage.setResizable(true);

            stage.show();

            // Fermer la fenêtre actuelle
            ((Stage) btnRetour.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du retour aux objectifs : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Classe interne pour la cellule personnalisée de la ListView des performances
    private class PerformanceListCell extends ListCell<Performance> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        @Override
        protected void updateItem(Performance performance, boolean empty) {
            super.updateItem(performance, empty);

            if (empty || performance == null) {
                setText(null);
                setGraphic(null);
            } else {
                // Création d'un conteneur pour les informations de la performance
                VBox vbox = new VBox(5);

                // Métrique et valeur
                Label lblMetric = new Label(String.format("%s: %.2f %s",
                        performance.getMetricName(),
                        performance.getValue(),
                        performance.getUnit()));
                lblMetric.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                // Date et notes
                Label lblDetails = new Label(String.format("Le %s | %s",
                        performance.getDate().format(formatter),
                        performance.getNotes() != null ? performance.getNotes() : ""));
                lblDetails.setStyle("-fx-font-size: 12px;");

                // Boutons d'action
                Button btnModifier = new Button("Modifier");
                btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                btnModifier.setOnAction(e -> handleModifier(performance));

                Button btnSupprimer = new Button("Supprimer");
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnSupprimer.setOnAction(e -> handleSupprimer(performance));

                HBox buttons = new HBox(10, btnModifier, btnSupprimer);
                buttons.setAlignment(Pos.CENTER_RIGHT);

                // Espaceur
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // ID
                Label lblId = new Label("ID: " + performance.getId());
                lblId.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                // Conteneur horizontal pour ID et boutons
                HBox bottomBox = new HBox(lblId, spacer, buttons);
                bottomBox.setAlignment(Pos.CENTER_LEFT);

                // Ajouter tous les éléments au conteneur vertical
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
                stage.setResizable(false);

                stage.showAndWait();

                // Rafraîchir la liste après modification
                chargerPerformances();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
            }
        }

        private void handleSupprimer(Performance performance) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmation de suppression");
            alert.setHeaderText(null);
            alert.setContentText("Êtes-vous sûr de vouloir supprimer cette performance ?");

            if (alert.showAndWait().get() == ButtonType.OK) {
                try {
                    performanceService.deletePerformance(performance.getId());
                    chargerPerformances();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression : " + e.getMessage());
                }
            }
        }
    }
}