package controllers.Programme;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import models.Programme;
import services.ProgrammeService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherProgrammeFront implements Initializable {

    @FXML
    private FlowPane programmeListContainer;

    private ProgrammeService programmeService = new ProgrammeService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            List<Programme> programmes = programmeService.getAllProgrammes();

            for (Programme p : programmes) {
                VBox card = new VBox(10);
                card.setPadding(new Insets(15));
                card.setSpacing(10);
                card.setPrefWidth(250); // Ajusté pour 3 par ligne dans 800px
                card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; -fx-border-radius: 15; -fx-cursor: hand;");
                card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));

                // Hover effect
                card.setOnMouseEntered(e -> card.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.25))));
                card.setOnMouseExited(e -> card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1))));

                card.setOnMouseClicked((MouseEvent e) -> openDetails(p));

                // Image
                Image image;
                try {
                    image = new Image("file:src/main/resources/images/" + p.getImage());
                } catch (Exception e) {
                    image = new Image("file:src/main/resources/images/default_program.png");
                }

                ImageView imageView = new ImageView(image);
                imageView.setFitHeight(160);
                imageView.setFitWidth(220);
                imageView.setPreserveRatio(false);
                imageView.setSmooth(true);

                // Clipping avec coins arrondis
                Rectangle clip = new Rectangle(220, 160);
                clip.setArcWidth(20);
                clip.setArcHeight(20);
                imageView.setClip(clip);

                // Titre
                Label titleLabel = new Label(p.getTitre());
                titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d3436;");

                // Description
                Label descriptionLabel = new Label(p.getDescription());
                descriptionLabel.setWrapText(true);
                descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #636e72;");

                card.getChildren().addAll(imageView, titleLabel, descriptionLabel);
                programmeListContainer.getChildren().add(card);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des programmes : " + e.getMessage());
        }
    }

    private void openDetails(Programme p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsProgramme.fxml"));
            Parent root = loader.load();

            DetailsProgramme controller = loader.getController();
            controller.setProgrammeDetails(p);

            Stage stage = new Stage();
            stage.setTitle("Détails du Programme");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture des détails : " + e.getMessage());
        }
    }
}



