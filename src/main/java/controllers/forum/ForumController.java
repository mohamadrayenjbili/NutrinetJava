package controllers.forum;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import models.Forum;
import models.User;
import services.forum.MessageService;
import services.user.UserService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ForumController implements Initializable {

    @FXML
    private VBox messageContainer;

    private final MessageService messageService = new MessageService();
    private final UserService userService = new UserService();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        chargerMessages();
    }



    private void chargerMessages() {
        try {
            List<Forum> messages = messageService.getAllMessages();
            messageContainer.getChildren().clear();

            for (Forum message : messages) {
                VBox bulle = creerBulleMessage(message);
                messageContainer.getChildren().add(bulle);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private VBox creerBulleMessage(Forum message) {
        VBox bulle = new VBox();
        bulle.setSpacing(8);
        bulle.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-background-radius: 10px; -fx-border-color: #ccc; -fx-border-radius: 10px;");
        bulle.setPrefWidth(600);

        // Barre du haut : nom, prénom, date
        HBox header = new HBox();
        header.setSpacing(20);
        header.setAlignment(Pos.CENTER_LEFT);

        try {
            User auteur = userService.getUserById(message.getUserId());
            String nomPrenom = auteur.getName() + " " + auteur.getPrename();
            Label nomLabel = new Label("👤 " + nomPrenom);
            nomLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #2c3e50;");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            Label dateLabel = new Label("🕒 " + sdf.format(message.getDate()));
            dateLabel.setStyle("-fx-text-fill: gray;");

            header.getChildren().addAll(nomLabel, dateLabel);

            // Vérifie si l'utilisateur connecté est l'auteur du message
            User currentUser = utils.session.getCurrentUser();
            if (currentUser != null && currentUser.getId() == message.getUserId()) {
                Label menuIcon = new Label("⋮");
                menuIcon.setStyle("-fx-font-size: 18px; -fx-cursor: hand; -fx-text-fill: #888;");
                header.getChildren().add(menuIcon);

                // Crée un menu contextuel
                ContextMenu contextMenu = new ContextMenu();

                // Bouton Modifier
                Label editLabel = new Label("✏️ Modifier");
                editLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #3498db;");
                CustomMenuItem editItem = new CustomMenuItem(editLabel);
                editItem.setHideOnClick(true);

                editItem.setOnAction(e -> {
                    // Charge la scène de mise à jour sans popup
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/UpdateMessage.fxml"));
                        Parent root = loader.load();

                        UpdateMessageController controller = loader.getController();
                        controller.setForumMessage(message);

                        // Remplace la scène actuelle par celle de mise à jour
                        Stage stage = (Stage) messageContainer.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Modifier le Message");
                        stage.show();

                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });

                // Bouton Supprimer
                Label deleteLabel = new Label("🗑 Supprimer");
                deleteLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: #e74c3c;");
                CustomMenuItem deleteItem = new CustomMenuItem(deleteLabel);
                deleteItem.setHideOnClick(true);

                deleteItem.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText(null);
                    alert.setContentText("Voulez-vous vraiment supprimer ce message ?");

                    Optional<ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        try {
                            messageService.deleteMessage(message.getId());
                            chargerMessages();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        }
                    }
                });

                contextMenu.getItems().addAll(editItem, deleteItem);
                menuIcon.setOnMouseClicked(e -> contextMenu.show(menuIcon, e.getScreenX(), e.getScreenY()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Contenu du message
        Text contenu = new Text(message.getMessage());
        contenu.setWrappingWidth(550);
        contenu.setStyle("-fx-font-size: 14px; -fx-fill: #333;");

        // Likes
        HBox likeBox = new HBox();
        likeBox.setSpacing(5);
        likeBox.setAlignment(Pos.CENTER_LEFT);

        Label likeIcon = new Label("❤️");
        likeIcon.setStyle("-fx-cursor: hand;");
        Label likeCount = new Label(String.valueOf(message.getLikes()));

        likeIcon.setOnMouseClicked(event -> {
            try {
                messageService.incrementLikes(message.getId());
                message.setLikes(message.getLikes() + 1);
                likeCount.setText(String.valueOf(message.getLikes()));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

        likeBox.getChildren().addAll(likeIcon, likeCount);
        bulle.getChildren().addAll(header, contenu, likeBox);

        return bulle;
    }





    @FXML
    private void handleAjouterMessage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/AjouterMessage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) messageContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter un Message");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/welcome.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) messageContainer.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Accueil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
