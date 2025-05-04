package controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.User;
import services.user.log_historyService;
import utils.session;

public class WelcomeController {

    @FXML
    private Label welcomeLabel;
    private final log_historyService logService = new log_historyService();

    @FXML
    public void initialize() {
        User currentUser = session.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getPrename() + "!");
        } else {
            // Handle the case where there is no logged-in user
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
        try {
            // Récupérer l'utilisateur avant de vider la session
            User currentUser = session.getCurrentUser();

            // Ajouter le log de déconnexion
            if (currentUser != null) {
                String details = "User " + currentUser.getName() + " " + currentUser.getPrename() + " logged out";
                logService.addLog(currentUser.getEmail(), "Logout", details);
            }

            // Vider la session
            session.clearSession();

            // Redirection vers la page de connexion
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

            @FXML
            public void naviguateToForum(ActionEvent actionEvent) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/forum/listForum.fxml"));
                    Parent root = loader.load();

                    Stage stage = (Stage) welcomeLabel.getScene().getWindow();
                    stage.setTitle("Forum");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

    @FXML
    private void openSeifProgram() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Programme/AfficherProgrammeFront.fxml"));
            Parent root = loader.load();

            // Récupérer la scène actuelle via welcomeLabel
            Scene currentScene = welcomeLabel.getScene();

            // Remplacer juste le contenu racine
            currentScene.setRoot(root);

            // Ajouter le CSS si besoin
            String css = getClass().getResource("/Programme/modern_list.css").toExternalForm();
            if (!currentScene.getStylesheets().contains(css)) {
                currentScene.getStylesheets().add(css);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        }