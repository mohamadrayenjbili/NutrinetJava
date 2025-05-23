package controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.User;
import services.user.log_historyService;
import utils.session;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private VBox roleButtonsContainer;
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

        if (currentUser != null) {
            if (currentUser.getRole().equalsIgnoreCase("doctor")) {
                Button doctorButton = new Button("");
                doctorButton.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailsConsultation.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) roleButtonsContainer.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Consultations du docteur");
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                roleButtonsContainer.getChildren().add(doctorButton);

            } else if (currentUser.getRole().equalsIgnoreCase("client")) {
                Button clientButton = new Button("");
                clientButton.setOnAction(e -> {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterConsultation.fxml"));
                        Parent root = loader.load();
                        Stage stage = (Stage) roleButtonsContainer.getScene().getWindow();
                        stage.setScene(new Scene(root));
                        stage.setTitle("Mes consultations");
                        stage.show();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                });
                roleButtonsContainer.getChildren().add(clientButton);
            }


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

    @FXML
    public void naviguateToReclamation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/reclamation/listReclamation.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setTitle("Reclamation");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void navigateToAfficherObjective(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AdminObjective.fxml"));
            Scene afficherObjectiveScene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(afficherObjectiveScene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    private void goToMalek() {
        try {
            User currentUser = session.getCurrentUser();
            String role = currentUser.getRole().toLowerCase();

            String fxmlPath;
            String title;

            switch (role) {
                case "doctor":
                    fxmlPath = "/DetailsConsultation.fxml";
                    title = "Espace Docteur - Malek";
                    break;
                case "client":
                    fxmlPath = "/DetailsClient.fxml";
                    title = "Espace Client - Malek";
                    break;
                case "ADMIN":
                    fxmlPath = "/BackConsultation.fxml";
                    title = "Espace Admin - Malek";
                    break;
                default:
                    System.out.println("Rôle non reconnu : " + role);
                    return;
            }

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


