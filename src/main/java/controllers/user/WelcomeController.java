package controllers.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import utils.session;
import models.User;

import java.io.IOException;

public class WelcomeController {

    @FXML
    private Label welcomeLabel;
    @FXML
    private VBox roleButtonsContainer;

    @FXML
    public void initialize() {
        User currentUser = session.getCurrentUser();
        if (currentUser != null) {
            welcomeLabel.setText("Welcome, " + currentUser.getName() + "!");
        } else {
            welcomeLabel.setText("Welcome!");
        }

        if (currentUser != null) {
            if (currentUser.getRole().equalsIgnoreCase("doctor")) {
                Button doctorButton = new Button("Ma liste de consultation");
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
                Button clientButton = new Button("Reserver une consultation");
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



}
