package controllers.user;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Parent;
import models.User;
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/welcome.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Welcome");
            stage.setScene(new Scene(root));
            stage.show();

            // Close current window
            nameLabel.getScene().getWindow().hide();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
