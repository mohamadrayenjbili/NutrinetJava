package controllers.user;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import services.user.GoogleAuthService;
import services.user.SignInService;
import utils.session;

import java.io.IOException;

public class Sign_InController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;


    @FXML
    private void handleGoogleSignIn() {
        try {
            GoogleAuthService googleAuth = new GoogleAuthService();
            String authCode = googleAuth.getAuthorizationCode();

            // Authentifier l'utilisateur avec le code
            User user = googleAuth.authenticateWithGoogle(authCode);
            handleGoogleAuthSuccess(user);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'authentification Google : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void handleGoogleAuthSuccess(User user) {
        if (user == null) {
            showAlert(Alert.AlertType.ERROR, "Échec de l'authentification Google");
            return;
        }

        try {
            session.setCurrentUser(user);

            // Vérifier si l'utilisateur est banni
            if (user.getIsBanned() != null && user.getIsBanned().equals("1")) {
                loadScene("/user/banned.fxml", "Compte Banni");
                return;
            }

            // Rediriger en fonction du rôle
            String fxmlPath;
            String title;

            if ("admin".equalsIgnoreCase(user.getRole())) {
                fxmlPath = "/user/dashboard.fxml";
                title = "Dashboard";
            } else {
                fxmlPath = "/user/welcome.fxml";
                title = "Welcome";
            }

            loadScene(fxmlPath, title);

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la redirection : " + e.getMessage());
        }
    }

    private void loadScene(String fxmlPath, String title) throws IOException {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(title);
                stage.show();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de la page : " + e.getMessage());
            }
        });
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Veuillez saisir l'email et le mot de passe.");
            return;
        }

        try {
            SignInService service = new SignInService();
            User user = service.authenticate(email, password);

            if (user != null) {
                session.setCurrentUser(user);

                // Vérifier si l'utilisateur est banni
                if (user.getIsBanned() != null && user.getIsBanned().equals("1")) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/banned.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) emailField.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Compte Banni");
                    stage.show();
                    return;
                }

                String fxmlPath;
                String title;

                fxmlPath = "/User/welcome.fxml";
                title = "Welcome";

                FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
                Parent root = loader.load();
                Stage stage = (Stage) emailField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle(title);
                stage.show();

            } else {
                showAlert(Alert.AlertType.ERROR, "Email ou mot de passe incorrect.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur système lors de la connexion.");
        }
    }


    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }

    public void goToSignUp(ActionEvent actionEvent) {
        try {
            // Load the sign-up FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_up.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("Sign Up");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // You can log this or show an alert
        }
    }


    @FXML
    private void goToForgotPassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/change_pass.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            stage.setScene(new Scene(root));
            stage.setTitle("Forgot Password");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de la page de réinitialisation.");
        }
    }
}
