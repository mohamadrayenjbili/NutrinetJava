package controllers.forum;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Forum;
import models.User;
import services.forum.MessageService;
import utils.session;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class AjouterMessageController implements Initializable {

    @FXML private TextArea taMessage;
    @FXML private Button btnAjouter;
    @FXML private Label lblErreur;

    private User userConnecte;

    // Liste des gros mots à interdire
    private static final List<String> GROS_MOTS = List.of("merde", "putain", "connard");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Cacher le label d'erreur au démarrage
        lblErreur.setVisible(false);

        // Récupérer l'utilisateur connecté via la session
        userConnecte = session.getCurrentUser();
    }

    @FXML
    private void handleAjouterMessage(ActionEvent event) {
        String contenuMessage = taMessage.getText().trim();

        // Vérification des gros mots
        if (contientGrosMot(contenuMessage)) {
            lblErreur.setText("Votre message contient des termes inappropriés.");
            lblErreur.setVisible(true);
            return;
        }

        // Validation du message (longueur, etc.)
        if (!validateMessage(contenuMessage)) {
            return;
        }

        try {
            // Créer l'objet Forum
            Forum forumMessage = new Forum();
            forumMessage.setMessage(contenuMessage);
            forumMessage.setUserId(userConnecte.getId()); // Récupérer l'ID de l'utilisateur connecté

            // Ajouter le message via MessageService
            MessageService messageService = new MessageService();
            messageService.addMessage(forumMessage);

            showAlert(Alert.AlertType.INFORMATION, "Succès", "Message ajouté avec succès.");
            taMessage.clear();
            lblErreur.setVisible(false);

            handleRetour(event);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private boolean validateMessage(String message) {
        if (message.isEmpty()) {
            lblErreur.setText("Le message ne peut pas être vide.");
            lblErreur.setVisible(true);
            return false;
        } else if (message.length() < 5) {
            lblErreur.setText("Le message doit contenir au moins 5 caractères.");
            lblErreur.setVisible(true);
            return false;
        } else if (message.length() > 1000) {
            lblErreur.setText("Le message ne peut pas dépasser 1000 caractères.");
            lblErreur.setVisible(true);
            return false;
        }

        lblErreur.setVisible(false);
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleRetour(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/listForum.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) taMessage.getScene().getWindow(); // Obtenez la fenêtre actuelle
            stage.setScene(new Scene(root)); // Changer la scène
            stage.setTitle("Forum"); // Titre de la nouvelle scène
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Fonction de contrôle des gros mots
    private boolean contientGrosMot(String message) {
        for (String mot : GROS_MOTS) {
            if (message.toLowerCase().contains(mot.toLowerCase())) {
                return true; // Si un gros mot est trouvé
            }
        }
        return false; // Aucun gros mot trouvé
    }
}
