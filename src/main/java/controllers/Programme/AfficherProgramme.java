package controllers.Programme;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Insets;
import models.Programme;
import services.ProgrammeService;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherProgramme implements Initializable {

    @FXML
    private VBox programmeListContainer;  // Conteneur VBox dans le FXML pour afficher les programmes

    private ProgrammeService programmeService = new ProgrammeService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // Charger la liste des programmes depuis la base de données
            List<Programme> programmes = programmeService.getAllProgrammes();

            // Pour chaque programme, créer un HBox contenant ses informations et les boutons d'action
            for (Programme p : programmes) {
                // Créer un HBox pour le programme
                HBox programmeBox = new HBox(20);
                programmeBox.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 10;");
                programmeBox.setPadding(new Insets(10));

                // Créer le label affichant le titre, le type et l'auteur
                Label programmeDetails = new Label(p.getTitre());
                programmeDetails.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                // Créer les boutons d'action avec des emojis et tooltips
                Button updateBtn = new Button("Modifier");
                updateBtn.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                updateBtn.setTooltip(new Tooltip("Modifier"));

                Button deleteBtn = new Button("🗑Supprimer");
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                deleteBtn.setTooltip(new Tooltip("Supprimer"));

                Button detailBtn = new Button("Details");
                detailBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
                detailBtn.setTooltip(new Tooltip("Détails"));


                // Action du bouton supprimer :
                deleteBtn.setOnAction(e -> {
                    try {
                        // Appeler la méthode du service pour supprimer le programme de la base de données
                        programmeService.deleteProgramme(p.getId());
                        // Supprimer l'élément visuellement dans l'interface
                        programmeListContainer.getChildren().remove(programmeBox);
                    } catch (SQLException ex) {
                        System.err.println("Erreur lors de la suppression du programme : " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });

                //Action du bouton de modification
                updateBtn.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterProgramme.fxml"));
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


                //Action du bouton details
                detailBtn.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsProgramme.fxml"));
                        Parent root = loader.load();

                        DetailsProgramme controller = loader.getController();
                        controller.setProgrammeDetails(p); // Transfert des données

                        Stage stage = new Stage();
                        stage.setTitle("Détails du Programme");
                        stage.setScene(new Scene(root));
                        stage.show();

                    } catch (IOException ex) {
                        System.err.println("Erreur lors de l'ouverture de la vue de détails : " + ex.getMessage());
                        ex.printStackTrace();
                    }
                });
                // Ajouter le label et les boutons dans le HBox
                programmeBox.getChildren().addAll(programmeDetails, updateBtn, deleteBtn, detailBtn);
                // Ajouter le HBox au conteneur VBox
                programmeListContainer.getChildren().add(programmeBox);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des programmes : " + e.getMessage());
            e.printStackTrace();
        }
    }
}



