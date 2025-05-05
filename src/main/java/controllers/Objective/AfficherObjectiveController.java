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
import javafx.scene.layout.*;
import javafx.scene.text.*;
import javafx.stage.Stage;
import models.Objective;
import services.IObjectiveService;
import services.ObjectiveService;
import services.QuotableService;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AfficherObjectiveController implements Initializable {
    @FXML private ListView<Objective> listObjectives;
    @FXML private TextField tfRecherche;
    @FXML private Button btnRecherche;
    @FXML private Button btnAjouter;
    @FXML private Button btnActualiser;
    @FXML private Button btnRetour;
    @FXML private Button btnRefreshQuote;
    @FXML private TextFlow quoteTextFlow;
    @FXML private Text quoteContent;
    @FXML private Text quoteAuthor;

    private IObjectiveService objectiveService = new ObjectiveService();
    private QuotableService quotableService = new QuotableService();
    private ObservableList<Objective> objectivesList;
    private FilteredList<Objective> filteredList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listObjectives.setCellFactory(param -> new ObjectiveListCell());
        chargerObjectives();
        setupSearch();
        setupQuote();
    }

    private void setupQuote() {
        quoteTextFlow.setLineSpacing(5);
        quoteTextFlow.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        quoteContent.setFont(Font.font("Arial", FontPosture.ITALIC, 14));
        quoteAuthor.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        loadQuote();
    }

    private void loadQuote() {
        new Thread(() -> {
            try {
                String quote = quotableService.getRandomQuote();
                String[] quoteParts = quote.split(" - ", 2);

                javafx.application.Platform.runLater(() -> {
                    quoteContent.setText("\"" + quoteParts[0].replace("\"", "") + "\"");
                    if (quoteParts.length > 1) {
                        quoteAuthor.setText(" - " + quoteParts[1]);
                    } else {
                        quoteAuthor.setText("");
                    }
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() -> {
                    quoteContent.setText("\"La persévérance est la clé du succès.\"");
                    quoteAuthor.setText(" - Proverbe français");
                });
            }
        }).start();
    }

    private void setupSearch() {
        filteredList = new FilteredList<>(objectivesList, p -> true);
        listObjectives.setItems(filteredList);
        tfRecherche.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredList.setPredicate(objective -> {
                if (newValue == null || newValue.isEmpty()) return true;
                String lowerCaseFilter = newValue.toLowerCase();
                return objective.getName().toLowerCase().contains(lowerCaseFilter);
            });
        });
    }

    private void chargerObjectives() {
        try {
            objectivesList = FXCollections.observableArrayList(objectiveService.getAllObjectives());
            listObjectives.setItems(objectivesList);
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement: " + e.getMessage());
        }
    }

    @FXML
    private void handleRefreshQuote(ActionEvent event) {
        quoteContent.setText("Chargement...");
        quoteAuthor.setText("");
        loadQuote();
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
            chargerObjectives();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    @FXML
    private void handleRecherche(ActionEvent event) {}

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
            chargerObjectives();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
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
                    showAlert("Succès", "Objectif supprimé avec succès !");
                    chargerObjectives();
                } catch (SQLException e) {
                    showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        ((Stage) btnRetour.getScene().getWindow()).close();
    }

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
            ((Stage) listObjectives.getScene().getWindow()).close();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private class ObjectiveListCell extends ListCell<Objective> {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        @Override
        protected void updateItem(Objective objective, boolean empty) {
            super.updateItem(objective, empty);
            if (empty || objective == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox vbox = new VBox(5);
                Label lblName = new Label(objective.getName());
                lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                Label lblDetails = new Label(String.format("Valeur cible: %.2f %s | Du %s au %s",
                        objective.getTargetValue(),
                        objective.getUnit(),
                        objective.getStartDate().format(formatter),
                        objective.getEndDate().format(formatter)));

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

                Region spacer = new Region();
                HBox.setHgrow(spacer, Priority.ALWAYS);

                Label lblId = new Label("ID: " + objective.getId());
                lblId.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

                HBox bottomBox = new HBox(lblId, spacer, buttons);
                bottomBox.setAlignment(Pos.CENTER_LEFT);

                vbox.getChildren().addAll(lblName, lblDetails, bottomBox);
                vbox.setStyle("-fx-padding: 5; -fx-border-color: #ecf0f1; -fx-border-width: 0 0 1 0;");

                setGraphic(vbox);
            }
        }
    }

    public static class AdminObjectiveController {
    }
}