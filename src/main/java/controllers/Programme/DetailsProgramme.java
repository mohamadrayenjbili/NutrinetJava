package controllers.Programme;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import models.Commentaire;
import models.Programme;
import services.CommentaireService;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ResourceBundle;

public class DetailsProgramme implements Initializable {

    @FXML
    private VBox rootPane;

    @FXML
    private Label lblTitre, lblType, lblAuteur, lblVideoUrl;

    @FXML
    private TextArea lblDescription;

    @FXML
    private ImageView imgProgramme;

    @FXML
    private TextArea txtCommentaire;

    @FXML
    private Button btnEnvoyerCommentaire;

    @FXML
    private Button btnCommencerProgramme; // Le nouveau bouton "Commencer le programme"

    @FXML
    private VBox commentaireContainer;

    private Programme programme;

    private final CommentaireService commentaireService = new CommentaireService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // On ajoute la feuille de style
        rootPane.getStylesheets().add(getClass().getResource("/details_style.css").toExternalForm());

        // Action sur le bouton "Envoyer le commentaire"
        btnEnvoyerCommentaire.setOnAction(event -> {
            String contenu = txtCommentaire.getText().trim();
            if (!contenu.isEmpty() && programme != null) {
                Commentaire commentaire = new Commentaire();
                commentaire.setProgrammeId(programme.getId());
                commentaire.setAuteurId(1); // Remplacer par l’ID de l’utilisateur connecté
                commentaire.setContenu(contenu);
                commentaire.setCreatedAt(LocalDateTime.now());

                commentaireService.ajouterCommentaire(commentaire);
                afficherCommentaire(commentaire);

                txtCommentaire.clear();
            }
        });

        // Action sur le bouton "Commencer le programme"
        btnCommencerProgramme.setOnAction(event -> {
            // Ici, vous pouvez définir l’action qui lance le programme.
            // Par exemple, ouvrir un lecteur vidéo avec l’URL, naviguer vers un autre écran, etc.
            System.out.println("Le programme " + (programme != null ? programme.getTitre() : "") + " est lancé !");
            // TODO: Implémentez la logique pour démarrer ou lancer le programme
        });
    }

    public void setProgrammeDetails(Programme p) {
        this.programme = p;

        lblTitre.setText(p.getTitre());
        lblType.setText(p.getType());
        lblAuteur.setText(p.getAuteur());
        lblDescription.setText(p.getDescription());
        lblVideoUrl.setText(p.getVideoUrl());

        if (p.getImage() != null && !p.getImage().isEmpty()) {
            // Vous pouvez adapter le chemin vers l'image selon votre organisation de projet
            Image image = new Image("file:src/main/resources/images/" + p.getImage());
            imgProgramme.setImage(image);
        }

        // Afficher les commentaires du programme
        commentaireContainer.getChildren().clear();
        List<Commentaire> commentaires = commentaireService.getCommentairesByProgramme(p.getId());
        for (Commentaire commentaire : commentaires) {
            afficherCommentaire(commentaire);
        }
    }

    private void afficherCommentaire(Commentaire c) {
        HBox hbox = new HBox(10);
        hbox.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 10; -fx-background-radius: 10;");
        hbox.setAlignment(Pos.TOP_LEFT);

        ImageView imageView = new ImageView(new Image("file:src/main/resources/images/default-user.png"));
        imageView.setFitWidth(40);
        imageView.setFitHeight(40);
        imageView.setPreserveRatio(true);

        VBox vbox = new VBox(5);
        vbox.setMaxWidth(400); // Limite la largeur du contenu
        vbox.setStyle("-fx-padding: 5;");

        Label nom = new Label("Utilisateur #" + c.getAuteurId());
        nom.setStyle("-fx-font-weight: bold;");

        Label date = new Label("Publié le " + c.getCreatedAt().toLocalDate().toString());
        date.setStyle("-fx-font-size: 10; -fx-text-fill: gray;");

        Label contenu = new Label(c.getContenu());
        contenu.setWrapText(true);
        contenu.setStyle("-fx-font-size: 13;");

        HBox boutons = new HBox(10);
        Button btnUtile = new Button("Utile");
        btnUtile.getStyleClass().add("btn-utile");

        Button btnSignaler = new Button("Signaler");
        btnSignaler.getStyleClass().add("btn-signaler");

        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.getStyleClass().add("btn-supprimer");

        // Suppression du commentaire au clic sur "Supprimer"
        btnSupprimer.setOnAction(e -> {
            commentaireService.supprimerCommentaire(c.getId());
            commentaireContainer.getChildren().remove(hbox);
        });

        boutons.getChildren().addAll(btnUtile, btnSignaler, btnSupprimer);
        vbox.getChildren().addAll(nom, date, contenu, boutons);
        hbox.getChildren().addAll(imageView, vbox);

        commentaireContainer.getChildren().add(hbox);
    }
}