package controllers.Programme;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.animation.FadeTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Programme;
import services.Programme.ProgrammeService;

public class AfficherProgrammeFront implements Initializable {

    @FXML
    private FlowPane programmeListContainer;

    @FXML
    private TextField searchField; // Barre de recherche
    @FXML
    private Button btnRetour1;

    @FXML
    private ComboBox<String> filterComboBox; // Filtre par type

    private ProgrammeService programmeService = new ProgrammeService();
    private ObservableList<Programme> allProgrammes; // liste complète



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        programmeListContainer.getStylesheets().add(getClass().getResource("/Programme/modern_list.css").toExternalForm());
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

// Gestion du bouton retour
        btnRetour1.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/welcome.fxml"));
                Parent root = loader.load();

                // Récupérer la scène actuelle
                Scene currentScene = btnRetour1.getScene();

                // Remplacer seulement le contenu (pas la scène entière)
                currentScene.setRoot(root);

                // Ajouter ton CSS si besoin
                /*String css = getClass().getResource("/user/welcome.css").toExternalForm();
                if (!currentScene.getStylesheets().contains(css)) {
                    currentScene.getStylesheets().add(css);
                } */

            } catch (IOException e) {
                System.err.println("Erreur lors du retour à la page de bienvenue : " + e.getMessage());
                e.printStackTrace();
            }
        });

    }
    private void filterAndDisplayProgrammes() {
        programmeListContainer.getChildren().clear();

        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        String filterType = filterComboBox.getValue();
        final String effectiveFilterType = (filterType == null || filterType.equals("Tous")) ? "" : filterType;

        List<Programme> filtered = allProgrammes.stream()
                .filter(p -> p.getTitre().toLowerCase().contains(searchText))
                .filter(p -> effectiveFilterType.isEmpty() || p.getType().equalsIgnoreCase(effectiveFilterType))
                .collect(Collectors.toList());

        for (Programme p : filtered) {
            VBox card = new VBox();
            card.getStyleClass().add("programme-card");

            // Image
            ImageView imageView = new ImageView();
            try {
                imageView.setImage(new Image("file:C:\\Users\\jbili\\OneDrive\\Bureau\\Nos Backups\\D+G+K+B+M+S +bahamarokhra\\public\\uploads\\images\\" + p.getImage()));
            } catch (Exception ex) {
                imageView.setImage(new Image("file:src/main/resources/images/default_program.png"));
            }
            imageView.setFitWidth(280);
            imageView.setFitHeight(180);
            imageView.setPreserveRatio(false);
            imageView.getStyleClass().add("card-image");

            // Contenu
            VBox content = new VBox(10);
            content.getStyleClass().add("card-content");

            Label titleLabel = new Label(p.getTitre());
            titleLabel.getStyleClass().add("card-title");

            Label typeLabel = new Label("🏷 " + p.getType());
            typeLabel.getStyleClass().add("card-type");

            Label descLabel = new Label(p.getDescription());
            descLabel.getStyleClass().add("card-description");
            descLabel.setWrapText(true);
            descLabel.setMaxHeight(60);

            content.getChildren().addAll(titleLabel, typeLabel, descLabel);
            card.getChildren().addAll(imageView, content);

            // Animation et interaction
            card.setOnMouseClicked(e -> openDetails(p));

            FadeTransition fade = new FadeTransition(Duration.millis(300), card);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();

            programmeListContainer.getChildren().add(card);
        }
    }


    private void openDetails(Programme p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Programme/DetailsProgramme.fxml"));
            Parent root = loader.load();

            DetailsProgramme controller = loader.getController();
            controller.setProgrammeDetails(p);

            // Utiliser programmeListContainer pour récupérer le Stage actuel
            Stage currentStage = (Stage) programmeListContainer.getScene().getWindow();



            // Changer seulement la scène du Stage existant
            currentStage.setScene(new Scene(root));
            currentStage.setTitle("Détails du Programme");
            currentStage.setFullScreen(true);
            currentStage.setFullScreenExitHint("");
            currentStage.show();

        } catch (IOException e) {
            System.err.println("Erreur lors de l'ouverture des détails : " + e.getMessage());
        }
    }

}
