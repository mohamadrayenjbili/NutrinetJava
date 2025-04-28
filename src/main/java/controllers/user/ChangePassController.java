package controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import services.user.Changepassservice;
import utils.EmailService;

import java.io.IOException;

public class ChangePassController {

    @FXML
    private TextField emailField;

    @FXML
    private void handleSendResetEmail() {
        String email = emailField.getText();

        if (email == null || email.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Veuillez entrer un email.");
            return;
        }

        // Récupérer l'ID de l'utilisateur à partir de l'email
        int userId = Changepassservice.getUserIdByEmail(email);
        if (userId == -1) {
            showAlert(Alert.AlertType.ERROR, "Aucun utilisateur trouvé avec cet email.");
            return;
        }

        // Générer le lien de réinitialisation
        String resetLink = "http://127.0.0.1:8000/user/update-password/" + userId;
        String subject = "Réinitialisation de votre mot de passe";
        String body = "Bonjour,\n\nCliquez sur le lien suivant pour réinitialiser votre mot de passe :\n" + resetLink + "\n\nCordialement,\nL'équipe MyApp";

        // Envoyer l'email
        boolean emailSent = EmailService.sendEmail(email, subject, body);
        if (emailSent) {
            showAlert(Alert.AlertType.INFORMATION, "Email de réinitialisation envoyé avec succès.");
        } else {
            showAlert(Alert.AlertType.ERROR, "Échec de l'envoi de l'email.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void redirectToProfile(ActionEvent event) {
        try {
            // Charger le fichier FXML de l'écran de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/profile.fxml"));
            Parent root = loader.load();

            // Obtenir la fenêtre actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Remplacer la scène par celle de l'écran de connexion
            stage.setScene(new Scene(root));
            stage.setTitle("Sign In");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'écran de connexion.");
        }
    }
}