package controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import models.User;
import services.user.updateservice;

import java.io.IOException;
import java.util.Objects;

public class UpdateUserController {

    @FXML private TextField nomTextField;
    @FXML private TextField prenomTextField;
    @FXML private TextField ageTextField;
    @FXML private TextField phoneTextField;
    @FXML private TextField addressTextField;

    private final updateservice updateservice = new updateservice();
    private User userToUpdate;

    public void setUser(User user) {
        this.userToUpdate = user;
        nomTextField.setText(user.getName());
        prenomTextField.setText(user.getPrename());
        ageTextField.setText(String.valueOf(user.getAge()));
        phoneTextField.setText(user.getPhoneNumber());
        addressTextField.setText(user.getAddress());
    }

    @FXML
    private void handleUpdateUser(ActionEvent event) {
        userToUpdate.setName(nomTextField.getText());
        userToUpdate.setPrename(prenomTextField.getText());
        userToUpdate.setAge(Integer.parseInt(ageTextField.getText()));
        userToUpdate.setPhoneNumber(phoneTextField.getText());
        userToUpdate.setAddress(addressTextField.getText());

        try {
            updateservice.updateUser(userToUpdate);

            User currentUser = utils.session.getCurrentUser();
            String fxmlPath = (currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole()))
                    ? "/user/users_list.fxml"
                    : "/user/profile.fxml";

            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole())) {
                userlistcontroller controller = loader.getController();
                controller.loadUsers();
            }

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole()) ? "Users List" : "Profile");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de la vue : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace(); // Log l'exception complète
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la mise à jour de l'utilisateur : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void handleCancel(ActionEvent event) throws IOException {
        // Get the current user from session
        User currentUser = utils.session.getCurrentUser();

        String fxmlPath;
        if (currentUser != null && "admin".equalsIgnoreCase(currentUser.getRole())) {
            // If the user is admin, redirect to users_list
            fxmlPath = "/user/users_list.fxml";
        } else {
            // Else redirect to profile
            fxmlPath = "/user/profile.fxml";
        }

        // Load the correct scene
        Parent root = FXMLLoader.load(Objects.requireNonNull(getClass().getResource(fxmlPath)));
        Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

}
