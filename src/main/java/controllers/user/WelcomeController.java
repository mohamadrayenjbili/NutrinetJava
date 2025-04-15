package controllers.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import utils.session;
import models.User;

public class WelcomeController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        User currentUser = session.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + "!");
        } else {
            welcomeLabel.setText("Welcome!");
        }
    }

    @FXML
    private void goToUserList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/users_list.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("User List");
            stage.setScene(new Scene(root));
            stage.show();

            // Optionally hide welcome window
            welcomeLabel.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        // Clear the current session
        session.clearSession();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
            Parent root = loader.load();

            // Get the current stage from any UI element
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();

            // Set the new scene (sign in) on the same stage
            stage.setTitle("Sign In");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void goToProfile() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/profile.fxml"));
            Parent root = loader.load();

            // Get the current stage from any UI component on the page (e.g., a button or label)
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();  // replace 'someUIElement' with a real fx:id from your FXML

            // Replace the scene with the profile scene
            stage.setScene(new Scene(root));
            stage.setTitle("Profile");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void goToProducts() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AfficherProduitFront.fxml"));
            Parent root = loader.load();

            // Get the current stage
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("Nos Produits");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            // Vous pourriez ajouter une alerte pour informer l'utilisateur en cas d'erreur
        }
    }


}
