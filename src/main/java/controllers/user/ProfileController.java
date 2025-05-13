package controllers.user;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import models.User;
import utils.WindowUtils;
import utils.session;

public class ProfileController {

    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label roleLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label phoneLabel;

    @FXML
    public void initialize() {
        User user = session.getCurrentUser();
        if (user != null) {
            nameLabel.setText("Name: " + user.getName() + " " + user.getPrename());
            emailLabel.setText("Email: " + user.getEmail());
            roleLabel.setText("Role: " + user.getRole());
            addressLabel.setText("Address: " + user.getAddress());
            phoneLabel.setText("Phone: " + user.getPhoneNumber());
        }
    }

    @FXML
    private void handleBack() {
        try {
            Stage stage = (Stage) nameLabel.getScene().getWindow();
            WindowUtils.changeScene(stage, "/User/welcome.fxml", "Welcome");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigatetoupdate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/update_user.fxml"));
            Parent updateRoot = loader.load();

            // Pass the current user to the update controller
            UpdateUserController updateController = loader.getController();

            User currentUser = session.getCurrentUser(); // <- You must implement this logic
            updateController.setUser(currentUser);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(updateRoot));
            stage.setTitle("Update Profile");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToChangePassword(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/change_pass2.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Change Password");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
