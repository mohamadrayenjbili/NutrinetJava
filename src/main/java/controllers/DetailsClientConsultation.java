package controllers;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Consultation;
import services.ConsultationService;
import utils.WindowUtils;

public class DetailsClientConsultation implements Initializable {

    @FXML
    private ListView<Consultation> consultationsListView;

    @FXML
    private TextField searchField;

    @FXML
    private Button btnAjouterConsultation;
    @FXML
    private Button btnCalendrier;

    @FXML
    private FlowPane flowProduits;

    private ConsultationService consultationService;
    private ObservableList<Consultation> consultationsObservableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/didou", "root", "");
            this.consultationService = new ConsultationService(connection);
            loadConsultations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadConsultations() {
        try {
            List<Consultation> consultations = consultationService.getAllConsultations();
            consultationsObservableList = FXCollections.observableArrayList(consultations);
            consultationsListView.setItems(consultationsObservableList);

            consultationsListView.setCellFactory(param -> new ListCell<>() {
                private final HBox container = new HBox(15);
                private final VBox infoContainer = new VBox(5);
                private final HBox buttonContainer = new HBox(10);
                private final Label nameLabel = new Label();
                private final Label detailsLabel = new Label();
                private final Button btnModifier = new Button("Modifier");
                private final Button btnSupprimer = new Button("Supprimer");
                private final Rectangle separator = new Rectangle(0, 1);

                {
                    container.setStyle("-fx-padding: 15; -fx-background-radius: 8; -fx-background-color: white;");
                    container.setMaxWidth(Double.MAX_VALUE);

                    nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                    detailsLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

                    btnModifier.setStyle("-fx-background-color: #a8d5ba; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10 5 10;");
                    btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10 5 10;");
                    separator.setWidth(600);
                    separator.setFill(Color.valueOf("#ecf0f1"));

                    infoContainer.getChildren().addAll(nameLabel, detailsLabel);
                    container.getChildren().addAll(infoContainer, new Region());

                    btnModifier.setOnAction(event -> {
                        Consultation consultation = getItem();
                        if (consultation != null) {
                            modifierConsultation(consultation);
                        }
                    });

                    btnSupprimer.setOnAction(event -> {
                        Consultation consultation = getItem();
                        if (consultation != null) {
                            supprimerConsultation(consultation);
                        }
                    });
                }

                @Override
                protected void updateItem(Consultation consultation, boolean empty) {
                    super.updateItem(consultation, empty);
                    if (empty || consultation == null) {
                        setGraphic(null);
                    } else {
                        nameLabel.setText(consultation.getNom() + " " + consultation.getPrenom());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        String formattedDate = consultation.getDate().format(formatter);

                        String details = String.format("%s • %s • %s",
                                consultation.getType(),
                                formattedDate,
                                consultation.getStatus());

                        String statusStyle = switch (consultation.getStatus().toLowerCase()) {
                            case "faite" -> "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
                            case "annulé" -> "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
                            case "en attente" -> "-fx-text-fill: #f39c12; -fx-font-weight: bold;";
                            default -> "-fx-text-fill: #7f8c8d;";
                        };

                        detailsLabel.setText(details);
                        detailsLabel.setStyle("-fx-font-size: 13px; " + statusStyle);

                        buttonContainer.getChildren().clear();
                        buttonContainer.getChildren().addAll(btnModifier, btnSupprimer);

                        container.getChildren().remove(buttonContainer);
                        container.getChildren().add(buttonContainer);
                        setGraphic(container);
                    }
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur de chargement des consultations", "Une erreur est survenue lors du chargement des consultations.");
        }
    }

    @FXML
    private void handleAjouterConsultation() {
        try {
            Stage stage = (Stage) btnAjouterConsultation.getScene().getWindow();
            WindowUtils.changeScene(stage, "/AjouterConsultation.fxml", "Ajouter une consultation");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void modifierConsultation(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateConsultation.fxml"));
            Parent root = loader.load();

            UpdateConsultationController controller = loader.getController();
            controller.initData(consultation, consultationService);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modifier la consultation");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHidden(event -> loadConsultations());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'ouverture", "Impossible d'ouvrir le formulaire de modification.");
        }
    }

    private void supprimerConsultation(Consultation consultation) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer la consultation");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer la consultation de " +
                consultation.getNom() + " " + consultation.getPrenom() + " ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                boolean deleted = consultationService.deleteConsultation(consultation.getId());
                if (deleted) {
                    consultationsListView.getItems().remove(consultation);
                    showInfoAlert("Suppression réussie", "La consultation a été supprimée avec succès.");
                } else {
                    showErrorAlert("Erreur de suppression", "La consultation n'a pas pu être supprimée.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Erreur de suppression", "Une erreur est survenue: " + e.getMessage());
            }
        }
    }

    @FXML
    private void rechercherConsultation() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            consultationsListView.setItems(consultationsObservableList);
        } else {
            ObservableList<Consultation> filteredList = consultationsObservableList.filtered(
                    c -> c.getNom().toLowerCase().contains(searchText));
            consultationsListView.setItems(filteredList);
        }
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfoAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void retourAccueil(ActionEvent event) {
        try {
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            WindowUtils.changeScene(stage, "/User/welcome.fxml", "Accueil");
        } catch (IOException ex) {
            showAlert("Erreur", "Erreur lors de la navigation vers l'accueil: " + ex.getMessage(), Alert.AlertType.ERROR);
            ex.printStackTrace();
        }
    }
}
