package controllers.user;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.User;
import services.user.UserService;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ResourceBundle;

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

    @FXML private Label nomErrorLabel;
    @FXML private Label prenomErrorLabel;
    @FXML private Label emailErrorLabel;
    @FXML private Label passwordErrorLabel;
    @FXML private Label ageErrorLabel;
    @FXML private Label phoneErrorLabel;
    @FXML private Label addressErrorLabel;
    @FXML private Label dateErrorLabel;
    @FXML private Label roleErrorLabel;
    @FXML private Label generalErrorLabel;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        roleComboBox.setValue("client");
        clearErrorLabels();

        nomTextField.textProperty().addListener((observable, oldValue, newValue) -> nomErrorLabel.setText(""));
        prenomTextField.textProperty().addListener((observable, oldValue, newValue) -> prenomErrorLabel.setText(""));
        emailTextField.textProperty().addListener((observable, oldValue, newValue) -> emailErrorLabel.setText(""));
        passwordField.textProperty().addListener((observable, oldValue, newValue) -> passwordErrorLabel.setText(""));
        ageTextField.textProperty().addListener((observable, oldValue, newValue) -> ageErrorLabel.setText(""));
        phoneTextField.textProperty().addListener((observable, oldValue, newValue) -> phoneErrorLabel.setText(""));
        addressTextField.textProperty().addListener((observable, oldValue, newValue) -> addressErrorLabel.setText(""));
        datePicker.valueProperty().addListener((observable, oldValue, newValue) -> dateErrorLabel.setText(""));
        roleComboBox.valueProperty().addListener((observable, oldValue, newValue) -> roleErrorLabel.setText(""));
    }

    private void clearErrorLabels() {
        nomErrorLabel.setText("");
        prenomErrorLabel.setText("");
        emailErrorLabel.setText("");
        passwordErrorLabel.setText("");
        ageErrorLabel.setText("");
        phoneErrorLabel.setText("");
        addressErrorLabel.setText("");
        dateErrorLabel.setText("");
        roleErrorLabel.setText("");
        generalErrorLabel.setText("");
    }

    @FXML
    void clearFields() {
        nomTextField.clear();
        prenomTextField.clear();
        emailTextField.clear();
        passwordField.clear();
        ageTextField.clear();
        phoneTextField.clear();
        addressTextField.clear();
        roleComboBox.setValue("client");
        datePicker.setValue(null);
        clearErrorLabels();
    }

    private void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.show();
    }

    @FXML
    void goToSignIn(ActionEvent event) {
        try {
            // Charger le fichier FXML de l'écran de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/sign_in.fxml"));
            Parent root = loader.load();

            // Obtenir la fenêtre actuelle à partir de l'événement
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Remplacer la scène par celle de l'écran de connexion
            stage.setScene(new Scene(root));
            stage.setTitle("Sign In");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors du chargement de l'écran de connexion.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    void adduser(ActionEvent event) {
        clearErrorLabels();
        boolean hasErrors = false;

        String nom = nomTextField.getText().trim();
        String prenom = prenomTextField.getText().trim();
        String email = emailTextField.getText().trim();
        String password = passwordField.getText();
        String ageText = ageTextField.getText().trim();
        String phone = phoneTextField.getText().trim();
        String address = addressTextField.getText().trim();
        LocalDate selectedDate = datePicker.getValue();
        String role = roleComboBox.getValue();

        UserService userService = new UserService();

        if (nom.isEmpty()) {
            nomErrorLabel.setText("Le nom est obligatoire");
            hasErrors = true;
        }

        if (prenom.isEmpty()) {
            prenomErrorLabel.setText("Le prénom est obligatoire");
            hasErrors = true;
        }

        if (email.isEmpty()) {
            emailErrorLabel.setText("L'adresse e-mail est obligatoire");
            hasErrors = true;
        } else if (!email.matches("^[\\w.-]+@[\\w.-]+\\.\\w{2,}$")) {
            emailErrorLabel.setText("Format d'adresse e-mail invalide");
            hasErrors = true;
        } else if (userService.emailExists(email)) {
            emailErrorLabel.setText("Cet e-mail est déjà utilisé");
            hasErrors = true;
        }

        if (password.isEmpty()) {
            passwordErrorLabel.setText("Le mot de passe est obligatoire");
            hasErrors = true;
        } else if (password.length() < 6) {
            passwordErrorLabel.setText("Le mot de passe doit contenir au moins 6 caractères");
            hasErrors = true;
        }

        if (ageText.isEmpty()) {
            ageErrorLabel.setText("L'âge est obligatoire");
            hasErrors = true;
        } else {
            try {
                int age = Integer.parseInt(ageText);
                if (age <= 0) {
                    ageErrorLabel.setText("L'âge doit être un nombre positif");
                    hasErrors = true;
                }
            } catch (NumberFormatException e) {
                ageErrorLabel.setText("Veuillez entrer un âge valide");
                hasErrors = true;
            }
        }

        if (phone.isEmpty()) {
            phoneErrorLabel.setText("Le numéro de téléphone est obligatoire");
            hasErrors = true;
        } else if (!phone.matches("\\d{8,15}")) {
            phoneErrorLabel.setText("Numéro de téléphone invalide (8 à 15 chiffres)");
            hasErrors = true;
        }

        if (address.isEmpty()) {
            addressErrorLabel.setText("L'adresse est obligatoire");
            hasErrors = true;
        }

        if (selectedDate == null) {
            dateErrorLabel.setText("La date de naissance est obligatoire");
            hasErrors = true;
        }

        if (role == null) {
            roleErrorLabel.setText("Le rôle est obligatoire");
            hasErrors = true;
        }

        if (hasErrors) {
            return;
        }

        try {
            int age = Integer.parseInt(ageText);
            Date sqlDate = Date.valueOf(selectedDate);

            User user = new User(0, nom, prenom, email, password, age, phone, address, role, "0", sqlDate);
            userService.addUser(user);

            showSuccessAlert("Utilisateur enregistré avec succès !");
            clearFields();

        } catch (Exception e) {
            e.printStackTrace();
            generalErrorLabel.setText("Erreur: " + e.getMessage());
        }
    }
}
