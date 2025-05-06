package controllers.Programme;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import models.*;
import services.commentaire.CommentaireLikeService;
import utils.session;
import services.commentaire.CommentaireService;
import javafx.geometry.Insets;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Map;
import java.util.stream.Collectors;

public class DetailsProgramme implements Initializable {

    @FXML
    private VBox rootPane;

    @FXML
    private Label lblTitre, lblType, lblAuteur;
    @FXML
    private Hyperlink lblVideoUrl;

    @FXML
    private TextArea lblDescription;

    @FXML
    private ImageView imgProgramme;

    @FXML
    private TextArea txtCommentaire;
    @FXML
    private Button btnRetour;

    @FXML
    private Button btnEnvoyerCommentaire;

    @FXML
    private Button btnCommencerProgramme; // Le nouveau bouton "Commencer le programme"

    @FXML
    private VBox commentaireContainer;

    private Programme programme;

    private final CommentaireService commentaireService = new CommentaireService();
    private final CommentaireLikeService likeService = new CommentaireLikeService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // On ajoute la feuille de style
        rootPane.getStylesheets().add(getClass().getResource("/Programme/details_style.css").toExternalForm());
        rootPane.getStylesheets().add(getClass().getResource("/Programme/modern_details.css").toExternalForm());
        // Action sur le bouton "Envoyer le commentaire"
        btnEnvoyerCommentaire.setOnAction(event -> {
            String contenu = txtCommentaire.getText().trim();
            if (!contenu.isEmpty() && programme != null) {
                User currentUser = session.getCurrentUser();
                if (currentUser != null) {
                    Commentaire commentaire = new Commentaire();
                    commentaire.setProgrammeId(programme.getId());
                    commentaire.setAuteurId(currentUser.getId());
                    commentaire.setContenu(contenu);
                    commentaire.setCreatedAt(LocalDateTime.now());

                    // Récupérer l'ID généré et le mettre à jour
                    int newId = commentaireService.ajouterCommentaire(commentaire);
                    commentaire.setId(newId);

                    afficherCommentaire(commentaire, commentaireContainer);
                    txtCommentaire.clear();
                } else {
                    System.out.println("Aucun utilisateur connecté !");
                }
            }
        });

        // Bouton pour générer un commentaire AI
        Button btnGenererCommentaire = new Button("🤖 Générer un commentaire");
        btnGenererCommentaire.getStyleClass().add("btn-modifier"); // Style similaire au bouton "Modifier"
        btnGenererCommentaire.setOnAction(event -> onGenerateAIComment());
        commentaireContainer.getChildren().add(0, btnGenererCommentaire); // Ajouter en haut de la section des commentaires


        // Action sur le bouton "Commencer le programme"
        btnCommencerProgramme.setOnAction(event -> {
            if (programme != null) {
                String titreProgramme = programme.getTitre();
                String utilisateurNom = session.getCurrentUser() != null ? session.getCurrentUser().getName() : "Utilisateur inconnu";

                // Envoyer le SMS via Twilio
                //TwilioSMSService.sendSMS("+21656330320", utilisateurNom + " a commencé le programme : " + titreProgramme);

                // Ouvrir la vidéo YouTube dans le navigateur
                String videoUrl = programme.getVideoUrl();
                if (videoUrl != null && !videoUrl.isEmpty()) {
                    try {
                        java.awt.Desktop.getDesktop().browse(java.net.URI.create(videoUrl));
                        System.out.println("La vidéo du programme " + titreProgramme + " est lancée !");
                    } catch (java.io.IOException e) {
                        System.out.println("Erreur lors de l'ouverture de la vidéo : " + e.getMessage());
                    }
                } else {
                    System.out.println("Aucune URL de vidéo associée au programme.");
                }

                System.out.println("Le programme " + titreProgramme + " est lancé !");
            } else {
                System.out.println("Aucun programme sélectionné !");
            }
        });


        // Gestion du bouton retour
        btnRetour.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/Programme/AfficherProgrammeFront.fxml"));
                Parent root = loader.load();

                // Récupérer la scène actuelle
                Scene currentScene = btnRetour.getScene();

                // Remplacer seulement le contenu (pas la scène entière)
                currentScene.setRoot(root);

                // Ajouter ton CSS si besoin
                String css = getClass().getResource("/Programme/modern_list.css").toExternalForm();
                if (!currentScene.getStylesheets().contains(css)) {
                    currentScene.getStylesheets().add(css);
                }

            } catch (IOException e) {
                System.err.println("Erreur lors du retour : " + e.getMessage());
                e.printStackTrace();
            }
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
        // Réorganisation de l'affichage des commentaires
        commentaireContainer.getChildren().clear();
        List<Commentaire> commentaires = commentaireService.getCommentairesByProgramme(p.getId());

        // Créer une map des commentaires par parent ID
        Map<Integer, List<Commentaire>> reponses = commentaires.stream()
                .filter(c -> c.getParentId() != null)
                .collect(Collectors.groupingBy(Commentaire::getParentId));

        // Afficher les commentaires parents avec leurs réponses
        commentaires.stream()
                .filter(c -> c.getParentId() == null)
                .forEach(commentaire -> {
                    // Afficher le commentaire parent
                    VBox parentBlock = new VBox(10);
                    parentBlock.getStyleClass().add("parent-comment-block");

                    afficherCommentaire(commentaire, parentBlock);

                    // Afficher les réponses associées
                    List<Commentaire> reponsesCommentaire = reponses.get(commentaire.getId());
                    if (reponsesCommentaire != null) {
                        VBox reponsesBlock = new VBox(5);
                        reponsesBlock.setPadding(new Insets(0, 0, 0, 50));
                        reponsesBlock.getStyleClass().add("responses-block");

                        reponsesCommentaire.forEach(reponse ->
                                afficherCommentaire(reponse, reponsesBlock)
                        );

                        parentBlock.getChildren().add(reponsesBlock);
                    }

                    commentaireContainer.getChildren().add(parentBlock);
                });



    }

    private void afficherCommentaire(Commentaire c, VBox container) {
        VBox commentBlock = new VBox(10);
        commentBlock.getStyleClass().add("comment-block");

        // Ajouter la classe spécifique pour les réponses
        if (c.getParentId() != null) {
            commentBlock.getStyleClass().add("response-comment");
        }

        // Commentaire principal
        HBox hbox = new HBox(10);
        hbox.getStyleClass().add("comment-card");

        // Configuration de l'image de profil
        ImageView imageView = new ImageView(new Image("file:src/main/resources/images/default-user.png"));
        imageView.setFitWidth(c.getParentId() != null ? 35 : 40);
        imageView.setFitHeight(c.getParentId() != null ? 35 : 40);
        imageView.setPreserveRatio(true);

        VBox vbox = new VBox(5);
        vbox.setMaxWidth(400);

        // En-tête du commentaire
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label nom = new Label("Utilisateur inconnu"); // Valeur par défaut
        try {
            User auteur = services.user.UserService.getUserById(c.getAuteurId());
            if (auteur != null) {
                nom.setText(auteur.getPrename() + " " + auteur.getName());
            }
        } catch (Exception ex) {
            System.err.println("Erreur lors de la récupération de l'auteur : " + ex.getMessage());
        }
        nom.getStyleClass().add("comment-author");

        if (c.getParentId() != null) {
            Label replyIndicator = new Label("↳ Réponse");
            replyIndicator.getStyleClass().add("reply-indicator");
            headerBox.getChildren().addAll(nom, replyIndicator);
        } else {
            headerBox.getChildren().add(nom);
        }

        Label date = new Label("Publié le " + c.getCreatedAt().toLocalDate().toString());
        date.getStyleClass().add("comment-date");

        Label contenu = new Label(c.getContenu());
        contenu.setWrapText(true);
        contenu.getStyleClass().add("comment-content");

        // Boutons d'action
        HBox boutons = new HBox(10);
        Button btnRepondre = new Button("↩ Répondre");
        btnRepondre.getStyleClass().add("btn-repondre");
        /*
        Button btnUtile = new Button("Utile");
        btnUtile.getStyleClass().add("btn-utile");

        Button btnSignaler = new Button("Signaler");
        btnSignaler.getStyleClass().add("btn-signaler"); */

        // Boutons seulement pour l'auteur
        User currentUser = session.getCurrentUser();
        Button btnSupprimer = new Button("Supprimer");
        btnSupprimer.getStyleClass().add("btn-supprimer");

        Button btnModifier = new Button("Modifier");
        btnModifier.getStyleClass().add("btn-modifier");

        if (currentUser != null && currentUser.getId() == c.getAuteurId()) {
            boutons.getChildren().addAll(btnSupprimer, btnModifier);
        }

        // boutons.getChildren().addAll(btnRepondre, btnUtile, btnSignaler);
        boutons.getChildren().addAll(btnRepondre);

        vbox.getChildren().addAll(headerBox, date, contenu, boutons);
        hbox.getChildren().addAll(imageView, vbox);

        // Zone de réponse (initialement cachée)
        VBox replyBox = new VBox(10);
        replyBox.setVisible(false);
        replyBox.getStyleClass().add("reply-box");
        replyBox.setPadding(new Insets(0, 0, 0, 50));

        TextArea replyArea = new TextArea();
        replyArea.setPromptText("Votre réponse...");
        replyArea.getStyleClass().add("reply-input");
        replyArea.setPrefRowCount(2);

        Button btnEnvoyerReponse = new Button("Envoyer");
        btnEnvoyerReponse.getStyleClass().add("submit-button");

        replyBox.getChildren().addAll(replyArea, btnEnvoyerReponse);

        // Event handlers
        btnRepondre.setOnAction(e -> replyBox.setVisible(!replyBox.isVisible()));

        btnEnvoyerReponse.setOnAction(e -> {
            if (!replyArea.getText().trim().isEmpty()) {
                Commentaire reponse = new Commentaire();
                reponse.setProgrammeId(programme.getId());
                reponse.setAuteurId(currentUser.getId());
                reponse.setParentId(c.getId());
                reponse.setContenu(replyArea.getText().trim());
                reponse.setCreatedAt(LocalDateTime.now());

                // Récupérer l'ID généré et le mettre à jour
                int newId = commentaireService.ajouterCommentaire(reponse);
                reponse.setId(newId);

                VBox parentBlock = (VBox) commentBlock.getParent();
                VBox reponsesBlock = parentBlock.getChildren().stream()
                        .filter(node -> node instanceof VBox && ((VBox) node).getStyleClass().contains("responses-block"))
                        .map(node -> (VBox) node)
                        .findFirst()
                        .orElseGet(() -> {
                            VBox newReponsesBlock = new VBox(5);
                            newReponsesBlock.setPadding(new Insets(0, 0, 0, 50));
                            newReponsesBlock.getStyleClass().add("responses-block");
                            parentBlock.getChildren().add(newReponsesBlock);
                            return newReponsesBlock;
                        });

                afficherCommentaire(reponse, reponsesBlock);

                replyArea.clear();
                replyBox.setVisible(false);
            }
        });

        btnSupprimer.setOnAction(e -> {
            if (currentUser != null && currentUser.getId() == c.getAuteurId()) {
                // 1. Suppression en base
                commentaireService.supprimerCommentaire(c.getId());

                // 2. Retrait immédiat du bloc de commentaire de l'UI
                container.getChildren().remove(commentBlock);

                // 3. Si c'était une réponse et que la zone de réponses est vide, on supprime aussi le VBox parent
                if (container.getStyleClass().contains("responses-block") && container.getChildren().isEmpty()) {
                    ((Pane) container.getParent()).getChildren().remove(container);
                }
            }
        });

        btnModifier.setOnAction(e -> {
            if (currentUser != null && currentUser.getId() == c.getAuteurId()) {
                // Ouvre une zone de texte pour modifier le commentaire
                TextInputDialog dialog = new TextInputDialog(c.getContenu());
                dialog.setTitle("Modifier le commentaire");
                dialog.setHeaderText("Modification de votre commentaire");
                dialog.setContentText("Nouveau contenu :");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(newContent -> {
                    if (!newContent.trim().isEmpty()) {
                        c.setContenu(newContent.trim());
                        commentaireService.modifierCommentaire(c); // Attention : il faut avoir une méthode pour modifier en BDD
                        contenu.setText(newContent.trim()); // Actualiser directement l'affichage
                    }
                });
            }
        });
        // Like et Deslike//


// Dans la méthode afficherCommentaire, après la création des autres boutons
        HBox likeBox = new HBox(5);
        likeBox.setAlignment(Pos.CENTER_LEFT);

        Button btnLike = new Button();
        btnLike.getStyleClass().add("btn-like");

// Vérifie si l'utilisateur a déjà liké
        boolean hasLiked = likeService.hasUserLiked(c.getId(), currentUser.getId());
        int likeCount = likeService.getLikeCount(c.getId());

// Mise à jour de l'apparence du bouton
        updateLikeButton(btnLike, hasLiked, likeCount);

        btnLike.setOnAction(e -> {
            // Vérifie si l'utilisateur est connecté
            if (currentUser != null) {
                likeService.toggleLike(c.getId(), currentUser.getId());
                boolean newLikeStatus = likeService.hasUserLiked(c.getId(), currentUser.getId());
                int newLikeCount = likeService.getLikeCount(c.getId());
                updateLikeButton(btnLike, newLikeStatus, newLikeCount);
            } else {
                // Afficher une alerte "Veuillez vous connecter"
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Connexion requise");
                alert.setContentText("Vous devez être connecté pour liker un commentaire.");
                alert.showAndWait();
            }
        });

        likeBox.getChildren().add(btnLike);
        boutons.getChildren().add(0, likeBox); // Ajoute le bouton like en premier

        commentBlock.getChildren().add(hbox);

        if (c.getParentId() == null) {
            commentBlock.getChildren().add(replyBox);
        }

        container.getChildren().add(commentBlock);
    }


    private void updateLikeButton(Button btn, boolean hasLiked, int count) {
        String countText = count > 0 ? " " + count : "";

        if (hasLiked) {
            btn.setText("\u2764" + countText); // Cœur plein Unicode
            btn.getStyleClass().add("liked");
        } else {
            btn.setText("\u2661" + countText); // Cœur vide Unicode
            btn.getStyleClass().remove("liked");
        }

        if (!btn.getStyleClass().contains("btn-like")) {
            btn.getStyleClass().add("btn-like");
        }
    }

    private void refreshLikes(Button btnLike, int commentaireId, int userId) {
        boolean hasLiked = likeService.hasUserLiked(commentaireId, userId);
        int likeCount = likeService.getLikeCount(commentaireId);
        updateLikeButton(btnLike, hasLiked, likeCount);
    }


    @FXML
    private void onGenerateAIComment() {
        new Thread(() -> {
            try {
                String prompt = "Imagine que tu es une personne ordinaire qui a testé ce programme de type "
                        + programme.getType()
                        + ". Écris un commentaire court (3-4 lignes) de manière naturelle et humaine, comme si tu donnais ton avis sincère pour encourager d'autres personnes à essayer. Ne commence pas par 'Commentaire :' ou toute autre indication formelle.\n\n"
                        + "Description du programme :\n"
                        + programme.getDescription()
                        + "\n\n"
                        + "Ton commentaire doit être direct, chaleureux, positif et donner envie d'essayer.";

                String generated = DeepInfra.generateComment(prompt);

                Platform.runLater(() -> {
                    txtCommentaire.setText(generated.trim());
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }



}
