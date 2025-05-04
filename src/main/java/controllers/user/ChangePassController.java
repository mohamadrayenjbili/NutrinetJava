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
        String body = "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }" +
                ".container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px; background-color: #f9f9f9; }" +
                ".header { text-align: center; padding: 10px 0; }" +
                ".header h1 { color: #4CAF50; }" +
                ".content { margin-top: 20px; }" +
                ".content p { margin: 10px 0; }" +
                ".footer { margin-top: 20px; text-align: center; font-size: 12px; color: #777; }" +
                "a { color: #4CAF50; text-decoration: none; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>Réinitialisation de votre mot de passe</h1>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Bonjour,</p>" +
                "<p>Cliquez sur le lien ci-dessous pour réinitialiser votre mot de passe :</p>" +
                "<p><a href='" + resetLink + "'>Réinitialiser mon mot de passe</a></p>" +
                "<p>Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.</p>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>&copy; 2025 Nutrinet. Tous droits réservés.</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";

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