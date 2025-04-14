package controllers.user;

import models.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import services.user.updateservice;
import javafx.scene.Node;
import java.io.IOException;

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
        // Pré-remplir les champs avec les données de l'utilisateur
        nomTextField.setText(user.getName());
        prenomTextField.setText(user.getPrename());
        ageTextField.setText(String.valueOf(user.getAge()));
        phoneTextField.setText(user.getPhoneNumber());
        addressTextField.setText(user.getAddress());
    }

    @FXML
    private void handleUpdateUser(ActionEvent event) {
        // Mettre à jour les valeurs
        userToUpdate.setName(nomTextField.getText());
        userToUpdate.setPrename(prenomTextField.getText());
        userToUpdate.setAge(Integer.parseInt(ageTextField.getText()));
        userToUpdate.setPhoneNumber(phoneTextField.getText());
        userToUpdate.setAddress(addressTextField.getText());

        try {
            updateservice.updateUser(userToUpdate);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/users_list.fxml"));

            // Créer la nouvelle scène
            Scene scene = new Scene(loader.load());

            // Charger le contrôleur et mettre à jour la liste des utilisateurs
            controllers.user.userlistcontroller controller = loader.getController();
            controller.loadUsers();

            // Récupérer la fenêtre actuelle et changer la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Users List");
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de la vue de la liste des utilisateurs : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la mise à jour de l'utilisateur.");
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
