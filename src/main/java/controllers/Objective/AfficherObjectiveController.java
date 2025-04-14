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
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Objective;
import services.IObjectiveService;
import services.ObjectiveService;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherObjectiveController implements Initializable {

    @FXML
    private ListView<Objective> listObjectives;

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

    private IObjectiveService objectiveService;
    private ObservableList<Objective> objectivesList;
    private FilteredList<Objective> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        objectiveService = new ObjectiveService();

        // Configuration de la ListView avec une cellule personnalisée
        listObjectives.setCellFactory(param -> new ObjectiveListCell());

        // Charger les objectifs depuis la base de données
        chargerObjectives();

        // Configurer la recherche
        setupSearch();
    }

    private void setupSearch() {
        // Initialiser la liste filtrée
        filteredList = new FilteredList<>(objectivesList, p -> true);

        // Mettre à jour la ListView avec la liste filtrée
        listObjectives.setItems(filteredList);

        // Ajouter un listener pour filtrer automatiquement quand le texte de recherche change
        tfRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(objective -> {
                // Si le champ de recherche est vide, afficher tous les objectifs
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Rechercher par nom
                if (objective.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }

                return false;
            });
        });
    }

    private void chargerObjectives() {
        try {
            List<Objective> objectives = objectiveService.getAllObjectives();
            objectivesList = FXCollections.observableArrayList(objectives);
            listObjectives.setItems(objectivesList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des objectifs : " + e.getMessage());
        }
    }

    @FXML
    private void handleActualiser(ActionEvent event) {
        chargerObjectives();
        tfRecherche.clear();
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterObjective.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ajouter un objectif");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();

            // Rafraîchir la liste après l'ajout
            chargerObjectives();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout : " + e.getMessage());
        }
    }

    @FXML
    private void handleRecherche(ActionEvent event) {
        // La recherche est déjà gérée par le listener du TextField
    }

    private void handleModifier(Objective objective) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierObjective.fxml"));
            Parent root = loader.load();

            ModifierObjectiveController controller = loader.getController();
            controller.initData(objective);

            Stage stage = new Stage();
            stage.setTitle("Modifier un objectif");
            stage.setScene(new Scene(root));
            stage.setResizable(false);

            stage.showAndWait();

            // Rafraîchir la liste après la modification
            chargerObjectives();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
        }
    }

    private void handleSupprimer(Objective objective) {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer l'objectif \"" + objective.getName() + "\" ?");

        ButtonType btnOui = new ButtonType("Oui");
        ButtonType btnNon = new ButtonType("Non");

        confirmation.getButtonTypes().setAll(btnOui, btnNon);

        confirmation.showAndWait().ifPresent(response -> {
            if (response == btnOui) {
                try {
                    objectiveService.deleteObjective(objective.getId());
                    showAlert(Alert.AlertType.INFORMATION, "Succès", "Objectif supprimé avec succès !");
                    chargerObjectives();
                } catch (SQLException e) {
                    e.printStackTrace();
                    showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression de l'objectif : " + e.getMessage());
                }
            }
        });
    }



    @FXML
    private void handleRetour(ActionEvent event) {
        // Fermer la fenêtre actuelle
        ((Stage) btnRetour.getScene().getWindow()).close();
    }

    // Ajouter cette méthode à AfficherObjectiveController
    private void handleViewPerformances(Objective objective) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherPerformance.fxml"));
            Parent root = loader.load();

            AfficherPerformanceController controller = loader.getController();
            controller.initData(objective);

            Stage stage = new Stage();
            stage.setTitle("Performances pour " + objective.getName());
            stage.setScene(new Scene(root));
            stage.setResizable(true);

            stage.show();

            // Fermer la fenêtre actuelle
            ((Stage) listObjectives.getScene().getWindow()).close();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture de la fenêtre des performances : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    // Modifier la classe ObjectiveListCell dans AfficherObjectiveController
    private class ObjectiveListCell extends ListCell<Objective> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        @Override
        protected void updateItem(Objective objective, boolean empty) {
            super.updateItem(objective, empty);

            if (empty || objective == null) {
                setText(null);
                setGraphic(null);
            } else {
                // Création d'un conteneur pour les informations de l'objectif
                VBox vbox = new VBox(5);

                // Titre avec le nom de l'objectif
                Label lblName = new Label(objective.getName());
                lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                // Détails de l'objectif
                Label lblDetails = new Label(String.format("Valeur cible: %.2f %s | Du %s au %s",
                        objective.getTargetValue(),
                        objective.getUnit(),
                        objective.getStartDate().format(formatter),
                        objective.getEndDate().format(formatter)));

                // Boutons d'action
                Button btnModifier = new Button("Modifier");
                btnModifier.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                btnModifier.setOnAction(e -> handleModifier(objective));

                Button btnSupprimer = new Button("Supprimer");
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnSupprimer.setOnAction(e -> handleSupprimer(objective));

                Button btnPerformances = new Button("Voir performances");
                btnPerformances.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                btnPerformances.setOnAction(e -> handleViewPerformances(objective));

                HBox buttons = new HBox(10, btnModifier, btnSupprimer, btnPerformances);
                buttons.setAlignment(Pos.CENTER_RIGHT);

                // Espaceur
                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                // ID
                Label lblId = new Label("ID: " + objective.getId());
                lblId.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                // Conteneur horizontal pour ID et boutons
                HBox bottomBox = new HBox(lblId, spacer, buttons);
                bottomBox.setAlignment(Pos.CENTER_LEFT);

                // Ajouter tous les éléments au conteneur vertical
                vbox.getChildren().addAll(lblName, lblDetails, bottomBox);
                vbox.setStyle("-fx-padding: 5; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0;");

                setGraphic(vbox);
            }
        }
    }
}