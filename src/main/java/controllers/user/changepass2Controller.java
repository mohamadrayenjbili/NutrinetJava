package controllers.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import models.User;
import services.user.Changepass2Service;
import utils.session;

import java.io.IOException;

public class changepass2Controller {

    @FXML
    private PasswordField oldPasswordField;

    @FXML
    private PasswordField newPasswordField;

    @FXML
    private Label oldPasswordError;

    @FXML
    private Label newPasswordError;

    @FXML
    private void handleChangePassword() {
        // Réinitialiser les messages d'erreur
        oldPasswordError.setText("");
        newPasswordError.setText("");

        String oldPassword = oldPasswordField.getText();
        String newPassword = newPasswordField.getText();

        if (oldPassword.isEmpty()) {
            oldPasswordError.setText("Veuillez entrer l'ancien mot de passe.");
            return;
        }

        if (newPassword.isEmpty()) {
            newPasswordError.setText("Veuillez entrer un nouveau mot de passe.");
            return;
        }

        if (oldPassword.equals(newPassword)) {
            newPasswordError.setText("Le nouveau mot de passe doit être différent de l'ancien.");
            return;
        }

        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            oldPasswordError.setText("Aucun utilisateur connecté.");
            return;
        }

        // Vérifier si l'ancien mot de passe est correct
        boolean isOldPasswordCorrect = Changepass2Service.verifyPassword(currentUser.getId(), oldPassword);
        if (!isOldPasswordCorrect) {
            oldPasswordError.setText("L'ancien mot de passe est incorrect.");
            return;
        }

        boolean isPasswordUpdated = Changepass2Service.updatePassword(currentUser.getId(), newPassword);
        if (isPasswordUpdated) {
            newPasswordError.setText("Mot de passe changé avec succès.");
            redirectToProfile();
        } else {
            newPasswordError.setText("Erreur lors de la mise à jour du mot de passe.");
        }
    }

    private void redirectToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/profile.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer
            Stage stage = (Stage) oldPasswordField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la redirection vers le profil.");
        }
    }

    @FXML
    private void redirectToProfile2() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/profile.fxml"));
            Parent root = loader.load();

            // Obtenir la scène actuelle et la remplacer
            Stage stage = (Stage) oldPasswordField.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profil");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la redirection vers le profil.");
        }
    }



    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }
}