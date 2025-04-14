package controllers.user;

import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import models.User;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;
import services.user.UserService;

public class sign_upController implements Initializable {

    @FXML private TextField nomTextField;
    @FXML private TextField prenomTextField;
    @FXML private TextField emailTextField;
    @FXML private PasswordField passwordField;
    @FXML private TextField ageTextField;
    @FXML private TextField phoneTextField;
    @FXML private TextField addressTextField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> roleComboBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.setValue("client");
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.show();
    }

    private void clearFields() {
        nomTextField.clear();
        prenomTextField.clear();
        emailTextField.clear();
        passwordField.clear();
        ageTextField.clear();
        phoneTextField.clear();
        addressTextField.clear();
        roleComboBox.setValue("client");
        datePicker.setValue(null);
    }

    @FXML
    void adduser(ActionEvent event) {
        try {
            String nom = nomTextField.getText().trim();
            String prenom = prenomTextField.getText().trim();
            String email = emailTextField.getText().trim();
            String password = passwordField.getText();
            String ageText = ageTextField.getText().trim();
            String phone = phoneTextField.getText().trim();
            String address = addressTextField.getText().trim();
            LocalDate selectedDate = datePicker.getValue();
            String role = roleComboBox.getValue();

            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || password.isEmpty()
                    || ageText.isEmpty() || phone.isEmpty() || address.isEmpty() || selectedDate == null || role == null) {
                showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs.");
                return;
            }

            if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
                showAlert(Alert.AlertType.WARNING, "Adresse e-mail invalide.");
                return;
            }

            if (password.length() < 6) {
                showAlert(Alert.AlertType.WARNING, "Le mot de passe doit contenir au moins 6 caractères.");
                return;
            }

            int age;
            try {
                age = Integer.parseInt(ageText);
                if (age <= 0) {
                    showAlert(Alert.AlertType.WARNING, "L'âge doit être un nombre positif.");
                    return;
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.WARNING, "Veuillez entrer un âge valide.");
                return;
            }

            if (!phone.matches("\\d{8,15}")) {
                showAlert(Alert.AlertType.WARNING, "Numéro de téléphone invalide (8 à 15 chiffres).");
                return;
            }

            Date sqlDate = Date.valueOf(selectedDate);

            User user = new User(0, nom, prenom, email, password, age, phone, address, role, "0", sqlDate);
            UserService userService = new UserService();
            userService.addUser(user);

            utils.session.setCurrentUser(user);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/welcome.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Bienvenue");
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
        }
    }

    public void goToSignIn(ActionEvent actionEvent) {
        try {
            // Load the sign-up FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
            Parent root = loader.load();

            // Get the current stage from the event
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();

            // Set the new scene
            stage.setScene(new Scene(root));
            stage.setTitle("Sign In");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace(); // You can log this or show an alert
        }
    }
}
