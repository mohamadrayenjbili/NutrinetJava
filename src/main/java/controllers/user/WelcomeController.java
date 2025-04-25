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

            welcomeLabel.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout() {
        session.clearSession();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
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
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Profile");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAfficherProduitFront() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/AfficherProduitFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Nos Produits");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
