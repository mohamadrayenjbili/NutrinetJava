package controllers.reclamation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.Reclamation;
import services.reclamation.ReclamationService;
import utils.session;

import java.io.IOException;
import java.util.List;

public class ListReclamationController {

    @FXML
    private VBox reclamationsContainer;

    @FXML
    private Label aucuneLabel;

    @FXML
    private Button ajouterBtn;

    @FXML
    private Button retourBtn;

    @FXML
    private Pagination pagination;

    private final ReclamationService service = new ReclamationService();

    private List<Reclamation> reclamations;

    @FXML
    public void initialize() {
        loadReclamations();

        ajouterBtn.setOnAction(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/reclamation/AjouterReclamation.fxml"));
                ajouterBtn.getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        retourBtn.setOnAction(e -> {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/user/welcome.fxml"));
                retourBtn.getScene().setRoot(root);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        pagination.setPageFactory(this::createPage);
    }

    private void loadReclamations() {
        reclamations = service.getReclamationsByUser(session.getCurrentUser());

        if (reclamations.isEmpty()) {
            aucuneLabel.setVisible(true);
            pagination.setVisible(false);
        } else {
            aucuneLabel.setVisible(false);
            pagination.setVisible(true);
            pagination.setPageCount((reclamations.size() / 2) + (reclamations.size() % 2 == 0 ? 0 : 1));
        }
    }

    private VBox createPage(int pageIndex) {
        VBox pageBox = new VBox(10);
        int start = pageIndex * 2;
        int end = Math.min(start + 2, reclamations.size());

        for (int i = start; i < end; i++) {
            Reclamation r = reclamations.get(i);
            VBox box = createReclamationBox(r);
            pageBox.getChildren().add(box);
        }

        return pageBox;
    }

    private VBox createReclamationBox(Reclamation r) {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-padding: 10; -fx-background-color: #f5f5f5; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2); -fx-background-radius: 8px;");

        VBox leftContent = new VBox(6);
        Label sujet = new Label("📌 Sujet : " + r.getSujet());
        Label message = new Label("📝 Message : " + r.getMessage());
        Label status = new Label("📍 Statut : " + r.getStatus());

        sujet.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        message.setStyle("-fx-font-size: 13px;");
        status.setStyle("-fx-font-size: 13px; -fx-text-fill: #666;");

        Label reponse;
        if (r.getReponse() != null && !r.getReponse().isEmpty()) {
            reponse = new Label("📬 Réponse : " + r.getReponse());
            reponse.setStyle("-fx-text-fill: green;");

            // Afficher la note en étoiles seulement si une réponse existe
            HBox ratingStars = createRatingStars(r);
            leftContent.getChildren().add(ratingStars);
        } else {
            reponse = new Label("📬 Aucune réponse");
            reponse.setStyle("-fx-text-fill: #888888;");
        }

        reponse.setStyle("-fx-font-size: 13px;");
        leftContent.getChildren().addAll(sujet, message, status, reponse);
        leftContent.setPrefWidth(500);

        VBox rightContent = new VBox(10);
        rightContent.setStyle("-fx-alignment: center;");
        rightContent.setPrefWidth(200);

        if (r.getAttachmentFile() != null && !r.getAttachmentFile().isEmpty()) {
            try {
                Image img = new Image(getClass().getResourceAsStream("/" + r.getAttachmentFile()));
                ImageView imageView = new ImageView(img);
                imageView.setFitWidth(130);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                rightContent.getChildren().add(imageView);
            } catch (Exception e) {
                Label errorImage = new Label("❌ Image introuvable.");
                errorImage.setStyle("-fx-text-fill: red;");
                rightContent.getChildren().add(errorImage);
            }
        }

        Button editButton = new Button("✏️ Modifier");
        editButton.setStyle("-fx-background-color: orange; -fx-text-fill: white; -fx-border-radius: 5px; -fx-font-size: 12px;");
        editButton.setOnAction(event -> modifierReclamation(r));

        Button deleteButton = new Button("🗑️ Supprimer");
        deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-border-radius: 5px; -fx-font-size: 12px;");
        deleteButton.setOnAction(event -> supprimerReclamation(r));

        rightContent.getChildren().addAll(editButton, deleteButton);

        HBox mainContent = new HBox(20);
        mainContent.getChildren().addAll(leftContent, rightContent);

        box.getChildren().add(mainContent);
        return box;
    }


    private HBox createRatingStars(Reclamation reclamation) {
        HBox starBox = new HBox(5);
        starBox.setStyle("-fx-alignment: center-left;");
        int currentNote = reclamation.getNote(); // Note actuelle
        Label[] stars = new Label[5];

        for (int i = 0; i < 5; i++) {
            Label star = new Label();
            star.setStyle("-fx-font-size: 20px; -fx-cursor: hand;");
            final int index = i;

            if (i < currentNote) {
                star.setText("★");
                star.setStyle("-fx-font-size: 20px; -fx-text-fill: gold; -fx-cursor: hand;");
            } else {
                star.setText("☆");
                star.setStyle("-fx-font-size: 20px; -fx-text-fill: grey; -fx-cursor: hand;");
            }

            // ➔ Hover pour montrer visuellement
            star.setOnMouseEntered(event -> {
                for (int j = 0; j < 5; j++) {
                    if (j <= index) {
                        stars[j].setText("★");
                        stars[j].setStyle("-fx-font-size: 20px; -fx-text-fill: gold; -fx-cursor: hand;");
                    } else {
                        stars[j].setText("☆");
                        stars[j].setStyle("-fx-font-size: 20px; -fx-text-fill: grey; -fx-cursor: hand;");
                    }
                }
            });

            // ➔ Quand la souris sort : afficher la vraie note actuelle
            starBox.setOnMouseExited(event -> {
                for (int j = 0; j < 5; j++) {
                    if (j < reclamation.getNote()) {
                        stars[j].setText("★");
                        stars[j].setStyle("-fx-font-size: 20px; -fx-text-fill: gold; -fx-cursor: hand;");
                    } else {
                        stars[j].setText("☆");
                        stars[j].setStyle("-fx-font-size: 20px; -fx-text-fill: grey; -fx-cursor: hand;");
                    }
                }
            });

            // ➔ Quand on clique sur une étoile, on fixe la note dans la BDD
            star.setOnMouseClicked(event -> {
                int newNote = index + 1; // Parce que index commence à 0
                reclamation.setNote(newNote); // Mettre à jour l'objet Reclamation
                service.noterReclamation(reclamation.getId(), newNote); // Sauvegarder en BDD

                // Mettre à jour visuellement les étoiles
                for (int j = 0; j < 5; j++) {
                    if (j < newNote) {
                        stars[j].setText("★");
                        stars[j].setStyle("-fx-font-size: 20px; -fx-text-fill: gold; -fx-cursor: hand;");
                    } else {
                        stars[j].setText("☆");
                        stars[j].setStyle("-fx-font-size: 20px; -fx-text-fill: grey; -fx-cursor: hand;");
                    }
                }
            });

            stars[i] = star;
            starBox.getChildren().add(star);
        }

        return starBox;
    }


    private void supprimerReclamation(Reclamation r) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");
        alert.setContentText("Cette action est irréversible.");

        ButtonType buttonTypeOui = new ButtonType("Oui");
        ButtonType buttonTypeNon = new ButtonType("Non");

        alert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);

        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeOui) {
                service.supprimer(r);
                reclamations.remove(r);
                loadReclamations();
            }
        });
    }

    private void modifierReclamation(Reclamation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reclamation/ModifierReclamation.fxml"));
            Parent root = loader.load();

            ModifierReclamationController controller = loader.getController();
            controller.initData(r);

            ajouterBtn.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAdminButton() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/reclamation/listReclamationAdmin.fxml"));
            Stage stage = (Stage) reclamationsContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
