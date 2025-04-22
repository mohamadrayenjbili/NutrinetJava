package controllers.Programme;
import javafx.animation.FadeTransition;
import javafx.util.Duration;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
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
import java.util.Set;
import java.util.stream.Collectors;

public class AfficherProgrammeFront implements Initializable {

    @FXML
    private FlowPane programmeListContainer;

    @FXML
    private TextField searchField; // Barre de recherche

    @FXML
    private ComboBox<String> filterComboBox; // Filtre par type

    private ProgrammeService programmeService = new ProgrammeService();
    private ObservableList<Programme> allProgrammes; // liste complète

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            List<Programme> programmes = programmeService.getAllProgrammes();
            allProgrammes = FXCollections.observableArrayList(programmes);

            // Remplir le ComboBox avec les types de programmes uniques
            Set<String> types = programmes.stream()
                    .map(Programme::getType)
                    .collect(Collectors.toSet());
            filterComboBox.setItems(FXCollections.observableArrayList(types));
            filterComboBox.getItems().add(0, "Tous"); // Option pour afficher tous
            filterComboBox.getSelectionModel().selectFirst();

            // Listener sur la barre de recherche
            searchField.textProperty().addListener((observable, oldValue, newValue) -> filterAndDisplayProgrammes());

            // Listener sur le filtre (ComboBox)
            filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterAndDisplayProgrammes());

            // Affichage initial de la liste
            filterAndDisplayProgrammes();

        } catch (SQLException e) {
            System.err.println("Erreur lors du chargement des programmes : " + e.getMessage());
        }
    }

    private void filterAndDisplayProgrammes() {
        // Vider le conteneur avant de réafficher
        programmeListContainer.getChildren().clear();

        String searchText = (searchField.getText() != null) ? searchField.getText().toLowerCase() : "";
        String filterType = filterComboBox.getValue();
        if (filterType == null || filterType.equals("Tous")) {
            filterType = "";
        }
        // Création d'une variable finale pour l'utiliser dans la lambda
        final String effectiveFilterType = filterType;

        // Filtrer la liste en fonction du texte de recherche et du type sélectionné
        List<Programme> filtered = allProgrammes.stream()
                .filter(p -> p.getTitre().toLowerCase().contains(searchText))
                .filter(p -> effectiveFilterType.isEmpty() || p.getType().equalsIgnoreCase(effectiveFilterType))
                .collect(Collectors.toList());

        for (Programme p : filtered) {
            // Création d'une variable finale pour utilisation dans la lambda
            final Programme programmeCard = p;

            VBox card = new VBox(10);
            card.setPadding(new Insets(15));
            card.setSpacing(10);
            card.setPrefWidth(250); // Adapté pour 3 cartes par ligne dans une largeur de 800px
            card.setStyle("-fx-background-color: #ffffff; -fx-background-radius: 15; -fx-border-radius: 15; -fx-cursor: hand;");
            card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));

            // Effet hover
            card.setOnMouseEntered(e -> card.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.25))));
            card.setOnMouseExited(e -> card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1))));

            // Utilisation de la variable finale dans la lambda pour l'événement clic
            card.setOnMouseClicked((MouseEvent e) -> openDetails(programmeCard));

            // Gestion de l'image
            Image image;
            try {
                image = new Image("file:src/main/resources/images/" + programmeCard.getImage());
            } catch (Exception ex) {
                image = new Image("file:src/main/resources/images/default_program.png");
            }

            ImageView imageView = new ImageView(image);
            imageView.setFitHeight(160);
            imageView.setFitWidth(220);
            imageView.setPreserveRatio(false);
            imageView.setSmooth(true);

            // Clipping pour obtenir des coins arrondis
            Rectangle clip = new Rectangle(220, 160);
            clip.setArcWidth(20);
            clip.setArcHeight(20);
            imageView.setClip(clip);

            // Titre
            Label titleLabel = new Label(programmeCard.getTitre());
            titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2d3436;");

            // Description
            Label descriptionLabel = new Label(programmeCard.getDescription());
            descriptionLabel.setWrapText(true);
            descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #636e72;");

            card.getChildren().addAll(imageView, titleLabel, descriptionLabel);
            programmeListContainer.getChildren().add(card);
            FadeTransition fade = new FadeTransition(Duration.millis(300), card);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
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