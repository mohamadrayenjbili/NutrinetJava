package controllers.Programme;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import javafx.scene.layout.Priority; // Import manquant
import models.Programme;
import services.Programme.ProgrammeService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType; // Import manquant
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional; // Import manquant
import java.util.ResourceBundle;

public class AfficherProgramme implements Initializable {

    @FXML
    private VBox programmeListContainer; // Conteneur VBox dans le FXML pour afficher les programmes

    private ProgrammeService programmeService = new ProgrammeService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Charger la liste des programmes depuis la base de données
            List<Programme> programmes = programmeService.getAllProgrammes();

            // Pour chaque programme, créer un HBox contenant ses informations et les boutons d'action
            for (Programme p : programmes) {
                HBox programmeBox = new HBox(15);
                programmeBox.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2); -fx-padding: 15;");

                VBox detailsBox = new VBox(8);
                detailsBox.setStyle("-fx-padding: 0;");

                Label titreLabel = new Label(p.getTitre());
                titreLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

                Label descriptionLabel = new Label(p.getDescription());
                descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");
                descriptionLabel.setWrapText(true);

                HBox infoBox = new HBox(15);
                Label dureeLabel = new Label("⏱️ " + p.getAuteur() + " heures");
                dureeLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-opacity: 0.8;");
                Label dateLabel = new Label("📅 Créé le " + p.getDescription());
                dateLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; -fx-opacity: 0.8;");
                infoBox.getChildren().addAll(dureeLabel, dateLabel);

                detailsBox.getChildren().addAll(titreLabel, descriptionLabel, infoBox);

                VBox actionsBox = new VBox(10);
                actionsBox.setStyle("-fx-alignment: center;");

                Button detailBtn = new Button("Voir Details 👁");
                detailBtn.setStyle("-fx-background-color: #2980b9; -fx-text-fill: white; -fx-background-radius: 20; -fx-min-width: 36px; -fx-min-height: 36px; -fx-cursor: hand;");
                detailBtn.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Programme/DetailsProgramme.fxml"));
                        Parent root = loader.load();

                        DetailsProgramme controller = loader.getController();
                        controller.setProgrammeDetails(p); // Transfert des données

                        // Utiliser programmeListContainer pour récupérer le Stage actuel
                        Stage currentStage = (Stage) programmeListContainer.getScene().getWindow();

                        // Changer la scène du Stage existant
                        currentStage.setScene(new Scene(root));
                        currentStage.setTitle("Détails du Programme");
                        currentStage.setFullScreen(true);
                        currentStage.setFullScreenExitHint("");
                        currentStage.show();

                    } catch (IOException ex) {
                        System.err.println("Erreur lors de l'ouverture de la vue de détails : " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });


                Button updateBtn = new Button("Modifier✏");
                updateBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-background-radius: 20; -fx-min-width: 36px; -fx-min-height: 36px; -fx-cursor: hand;");
                updateBtn.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Programme/AjouterProgramme.fxml"));
                        Parent root = loader.load();

                        AjouterProgramme controller = loader.getController();
                        controller.setProgrammeToEdit(p); // Envoie le programme sélectionné

                        Stage stage = new Stage();
                        stage.setTitle("Modifier Programme");
                        stage.setScene(new Scene(root));
                        stage.show();

                    } catch (IOException ex) {
                        System.err.println("Erreur lors de l'ouverture du formulaire de modification : " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });

                Button deleteBtn = new Button("Supprimer🗑");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-background-radius: 20; -fx-min-width: 36px; -fx-min-height: 36px; -fx-cursor: hand;");
                deleteBtn.setOnAction(e -> {
                    try {
                        programmeService.deleteProgramme(p.getId());
                        programmeListContainer.getChildren().remove(programmeBox);
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                });

                actionsBox.getChildren().addAll(detailBtn, updateBtn, deleteBtn);

                programmeBox.getChildren().addAll(detailsBox, actionsBox);
                programmeListContainer.getChildren().add(programmeBox);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des programmes : " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAjouterProgramme() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Programme/AjouterProgramme.fxml")); // Correction du chemin
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter un Programme");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture du formulaire d'ajout : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleSupprimerProgramme(Programme programme) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmer la suppression");
        confirmDialog.setHeaderText("Supprimer le programme");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer le programme \"" + programme.getTitre() + "\" ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                programmeService.deleteProgramme(programme.getId());
                loadProgrammes();
            } catch (SQLException e) {
                System.err.println("Erreur lors de la suppression du programme : " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void loadProgrammes() {
        try {
            List<Programme> programmes = programmeService.getAllProgrammes();
            programmeListContainer.getChildren().clear();

            for (Programme programme : programmes) {
                HBox programmeBox = new HBox(20);
                programmeBox.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");
                programmeBox.setPadding(new Insets(10));

                Label programmeDetails = new Label(programme.getTitre());
                programmeDetails.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                programmeListContainer.getChildren().add(programmeBox);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors du rechargement des programmes : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
