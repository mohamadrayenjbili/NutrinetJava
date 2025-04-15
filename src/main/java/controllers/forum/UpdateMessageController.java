package controllers.forum;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Forum;
import services.forum.MessageService;
import services.forum.UpdateMessageService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class UpdateMessageController implements Initializable {

    @FXML private TextArea taMessage;
    @FXML private Label lblErreur;

    private Forum forumMessage;

    private final MessageService messageService = new MessageService();
    private final UpdateMessageService updateMessage = new UpdateMessageService();

    // Liste des gros mots à interdire
    private static final List<String> GROS_MOTS = List.of("merde", "putain", "connard");

    public UpdateMessageController() throws SQLException {
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        lblErreur.setVisible(false);
    }

    public void setForumMessage(Forum message) {
        this.forumMessage = message;
        taMessage.setText(message.getMessage());
    }

    @FXML
    private void handleEnregistrer() {
        String nouveauMessage = taMessage.getText().trim();

        // Vérification de la longueur du message
        if (nouveauMessage.isEmpty() || nouveauMessage.length() < 5) {
            lblErreur.setText("Le message doit contenir au moins 5 caractères.");
            lblErreur.setVisible(true);
            return;
        }

        // Vérification des gros mots
        if (contientGrosMot(nouveauMessage)) {
            lblErreur.setText("Votre message contient des termes inappropriés.");
            lblErreur.setVisible(true);
            return;
        }

        forumMessage.setMessage(nouveauMessage);

        try {
            updateMessage.updateMessage(forumMessage);
            handleAnnuler();
        } catch (Exception e) {
            lblErreur.setText("Erreur lors de la mise à jour du message.");
            lblErreur.setVisible(true);
        }
    }

    @FXML
    private void handleAnnuler() {
        try {
            // Charge la vue de la liste des forums
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/listForum.fxml"));
            Parent root = loader.load();

            // Remplace la scène actuelle par la liste des forums
            Stage stage = (Stage) taMessage.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Forum");
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
