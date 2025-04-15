package controllers.reclamation;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
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
        // Charge les réclamations de l'utilisateur
        reclamations = service.getReclamationsByUser(session.getCurrentUser());

        // Si aucune réclamation, afficher le message
        if (reclamations.isEmpty()) {
            aucuneLabel.setVisible(true);
            pagination.setVisible(false); // Masquer la pagination si vide
        } else {
            aucuneLabel.setVisible(false);
            pagination.setVisible(true); // Afficher la pagination
            pagination.setPageCount((reclamations.size() / 2) + (reclamations.size() % 2 == 0 ? 0 : 1)); // 3 réclamations par page
        }
    }

    private VBox createPage(int pageIndex) {
        // Chaque page affiche 5 réclamations max
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
        box.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 8px; -fx-padding: 10; -fx-background-color: #f5f5f5;-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 6, 0, 0, 2); -fx-background-radius: 8px;");

        // Partie gauche : texte
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
        } else {
            reponse = new Label("📬 Aucune réponse");
            reponse.setStyle("-fx-text-fill: #888888;");
        }
        reponse.setStyle("-fx-font-size: 13px;");

        leftContent.getChildren().addAll(sujet, message, status, reponse);
        leftContent.setPrefWidth(500); // ou ajuste selon la taille souhaitée

        // Partie droite : image + boutons
        VBox rightContent = new VBox(10);
        rightContent.setStyle("-fx-alignment: center;");
        rightContent.setPrefWidth(200);

        if (r.getAttachmentFile() != null && !r.getAttachmentFile().isEmpty()) {
            try {
                javafx.scene.image.Image img = new javafx.scene.image.Image(getClass().getResourceAsStream("/" + r.getAttachmentFile()));
                javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(img);
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

        // HBox global : gauche = texte, droite = image + boutons
        HBox mainContent = new HBox(20);
        mainContent.getChildren().addAll(leftContent, rightContent);

        box.getChildren().add(mainContent);
        return box;
    }




    private void supprimerReclamation(Reclamation r) {
        // Affichage d'une alerte de confirmation avant la suppression
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Êtes-vous sûr de vouloir supprimer cette réclamation ?");
        alert.setContentText("Cette action est irréversible.");

        ButtonType buttonTypeOui = new ButtonType("Oui");
        ButtonType buttonTypeNon = new ButtonType("Non");

        alert.getButtonTypes().setAll(buttonTypeOui, buttonTypeNon);

        // Attente de la réponse de l'utilisateur
        alert.showAndWait().ifPresent(response -> {
            if (response == buttonTypeOui) {
                // Appeler le service pour supprimer la réclamation de la base de données
                service.supprimer(r);

                // Retirer la réclamation de la liste affichée
                reclamations.remove(r);

                // Recharger l'affichage pour refléter les changements
                loadReclamations();
            }
        });
    }

    private void modifierReclamation(Reclamation r) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reclamation/ModifierReclamation.fxml"));
            Parent root = loader.load();

            ModifierReclamationController controller = loader.getController();
            controller.initData(r); // Envoie la réclamation à modifier

            ajouterBtn.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
