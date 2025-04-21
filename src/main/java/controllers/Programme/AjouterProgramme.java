/*
package controllers.Programme;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert.AlertType;
import models.Programme;
import services.ProgrammeService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class AjouterProgramme {

    @FXML private TextField tfTitre;
    @FXML private TextField tfAuteur;
    @FXML private TextField tfVideoUrl;
    @FXML private ComboBox<String> cbType;
    @FXML private TextField tfImagePath;
    @FXML private Button btnChoisirImage;
    @FXML private TextArea taDescription;
    @FXML private ImageView imagePreview;
    @FXML private Button btnAjouter;

    private ProgrammeService programmeService = new ProgrammeService();
    private Programme programmeEnCours = null;

    private File selectedFile;
    private String imageName = "";

    @FXML
    private void initialize() {
        cbType.getItems().addAll("Régime", "Cohérence cardiaque", "Arrêt du tabac","Santé Mentale");
    }

    @FXML
    private void handleChoisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(btnChoisirImage.getScene().getWindow());

        if (selectedFile != null) {
            imageName = UUID.randomUUID().toString().replaceAll("-", "") + getFileExtension(selectedFile.getName());
            tfImagePath.setText(selectedFile.getAbsolutePath());

            Image image = new Image(selectedFile.toURI().toString());
            imagePreview.setImage(image);
        }
    }

    @FXML
    private void handleAjouter() {
        if (!validateFields()) return;

        Programme programme = (programmeEnCours == null) ? new Programme() : programmeEnCours;
        programme.setTitre(tfTitre.getText());
        programme.setAuteur(tfAuteur.getText());
        programme.setType(cbType.getValue());
        programme.setVideoUrl(tfVideoUrl.getText());
        programme.setDescription(taDescription.getText());

        try {
            if (selectedFile != null) {
                File destDir = new File(getProjectResourceImagePath());
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, imageName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                programme.setImage(imageName);
            }

            if (programmeEnCours == null) {
                programmeService.ajouterProgramme(programme);
                showAlert(AlertType.INFORMATION, "Succès", "Programme ajouté avec succès !");
            } else {
                programmeService.updateProgramme(programme);
                showAlert(AlertType.INFORMATION, "Succès", "Programme mis à jour avec succès !");
            }

            clearForm();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la copie de l'image : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (tfTitre.getText().isEmpty()) errors.append("- Le titre est requis.\n");
        if (tfAuteur.getText().isEmpty()) errors.append("- L'auteur est requis.\n");
        if (cbType.getValue() == null) errors.append("- Le type est requis.\n");
        if (taDescription.getText().isEmpty()) errors.append("- La description est requise.\n");

        if (errors.length() > 0) {
            showAlert(AlertType.ERROR, "Erreur de validation", errors.toString());
            return false;
        }
        return true;
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return (index > 0) ? fileName.substring(index) : ".png";
    }

    private String getProjectResourceImagePath() {
        return System.getProperty("user.dir") + "/src/main/resources/images";
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        tfTitre.clear();
        tfAuteur.clear();
        tfVideoUrl.clear();
        cbType.setValue(null);
        tfImagePath.clear();
        taDescription.clear();
        imagePreview.setImage(null);
        selectedFile = null;
        imageName = "";
        programmeEnCours = null;
    }

    public void setProgrammeToEdit(Programme programme) {
        if (programme != null) {
            this.programmeEnCours = programme;

            tfTitre.setText(programme.getTitre());
            tfAuteur.setText(programme.getAuteur());
            tfVideoUrl.setText(programme.getVideoUrl());
            cbType.setValue(programme.getType());
            tfImagePath.setText(programme.getImage());
            taDescription.setText(programme.getDescription());

            // Charger l'image existante si elle est là
            File imageFile = new File(getProjectResourceImagePath() + "/" + programme.getImage());
            if (imageFile.exists()) {
                imagePreview.setImage(new Image(imageFile.toURI().toString()));
            }
        }
    }
}



<?xml version="1.0" encoding="UTF-8"?>

<?import javafx.geometry.Insets?>
<?import javafx.scene.control.*?>
<?import javafx.scene.image.ImageView?>
<?import javafx.scene.layout.*?>
<?import javafx.scene.text.Font?>

<AnchorPane xmlns="http://javafx.com/javafx"
            xmlns:fx="http://javafx.com/fxml"
            fx:controller="controllers.Programme.AjouterProgramme"
            prefHeight="700.0" prefWidth="550.0"
            stylesheets="@style.css"
            styleClass="root-pane">

    <VBox alignment="TOP_CENTER" spacing="15" AnchorPane.topAnchor="20" AnchorPane.leftAnchor="20" AnchorPane.rightAnchor="20">
        <Label text="Ajouter un programme" styleClass="title"/>

        <HBox spacing="10">
            <TextField fx:id="tfTitre" promptText="Titre" prefWidth="200" styleClass="text-field"/>
            <TextField fx:id="tfAuteur" promptText="Auteur" prefWidth="200" styleClass="text-field"/>
        </HBox>

        <TextField fx:id="tfVideoUrl" promptText="Vidéo (URL)" prefWidth="420" styleClass="text-field"/>

        <ComboBox fx:id="cbType" promptText="Type" prefWidth="420" styleClass="combo-box"/>

        <HBox spacing="10">
            <TextField fx:id="tfImagePath" promptText="Image sélectionnée..." editable="false" prefWidth="320" styleClass="text-field"/>
            <Button fx:id="btnChoisirImage" text="Parcourir..." onAction="#handleChoisirImage" styleClass="btn-secondary"/>
        </HBox>

        <TextArea fx:id="taDescription" promptText="Description" prefWidth="420" prefHeight="100" wrapText="true" styleClass="text-area"/>

        <ImageView fx:id="imagePreview" fitWidth="150" fitHeight="150" styleClass="image-preview"/>

        <Button fx:id="btnAjouter" text="Ajouter" onAction="#handleAjouter" styleClass="btn-primary"/>
    </VBox>
</AnchorPane>





 */

package controllers.Programme;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.scene.control.Alert.AlertType;
import models.Programme;
import services.ProgrammeService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.UUID;

public class AjouterProgramme {

    @FXML private TextField tfTitre;
    @FXML private TextField tfAuteur;
    @FXML private TextField tfVideoUrl;
    @FXML private ComboBox<String> cbType;
    @FXML private TextField tfImagePath;
    @FXML private Button btnChoisirImage;
    @FXML private TextArea taDescription;
    @FXML private ImageView imagePreview;
    @FXML private Button btnAjouter;

    // Labels d'erreur
    @FXML private Label titreErrorLabel;
    @FXML private Label auteurErrorLabel;
    @FXML private Label typeErrorLabel;
    @FXML private Label descriptionErrorLabel;

    private ProgrammeService programmeService = new ProgrammeService();
    private Programme programmeEnCours = null;

    private File selectedFile;
    private String imageName = "";

    @FXML
    private void initialize() {
        cbType.getItems().addAll("Régime", "Cohérence cardiaque", "Arrêt du tabac", "Santé Mentale");

        // Masquer les erreurs au démarrage
        resetErrorLabels();
    }

    @FXML
    private void handleChoisirImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(btnChoisirImage.getScene().getWindow());

        if (selectedFile != null) {
            imageName = UUID.randomUUID().toString().replaceAll("-", "") + getFileExtension(selectedFile.getName());
            tfImagePath.setText(selectedFile.getAbsolutePath());

            Image image = new Image(selectedFile.toURI().toString());
            imagePreview.setImage(image);
        }
    }

    @FXML
    private void handleAjouter() {
        resetErrorLabels();
        if (!validateFields()) return;

        Programme programme = (programmeEnCours == null) ? new Programme() : programmeEnCours;
        programme.setTitre(tfTitre.getText());
        programme.setAuteur(tfAuteur.getText());
        programme.setType(cbType.getValue());
        programme.setVideoUrl(tfVideoUrl.getText());
        programme.setDescription(taDescription.getText());

        try {
            if (selectedFile != null) {
                File destDir = new File(getProjectResourceImagePath());
                if (!destDir.exists()) destDir.mkdirs();

                File destFile = new File(destDir, imageName);
                Files.copy(selectedFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);

                programme.setImage(imageName);
            }

            if (programmeEnCours == null) {
                programmeService.ajouterProgramme(programme);
                showAlert(AlertType.INFORMATION, "Succès", "Programme ajouté avec succès !");
            } else {
                programmeService.updateProgramme(programme);
                showAlert(AlertType.INFORMATION, "Succès", "Programme mis à jour avec succès !");
            }

            clearForm();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la copie de l'image : " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'enregistrement : " + e.getMessage());
        }
    }

    private boolean validateFields() {
        boolean isValid = true;

        if (tfTitre.getText().trim().isEmpty()) {
            titreErrorLabel.setText("Le titre est requis.");
            titreErrorLabel.setVisible(true);
            isValid = false;
        }

        if (tfAuteur.getText().trim().isEmpty()) {
            auteurErrorLabel.setText("L'auteur est requis.");
            auteurErrorLabel.setVisible(true);
            isValid = false;
        }

        if (cbType.getValue() == null) {
            typeErrorLabel.setText("Le type est requis.");
            typeErrorLabel.setVisible(true);
            isValid = false;
        }

        if (taDescription.getText().trim().isEmpty()) {
            descriptionErrorLabel.setText("La description est requise.");
            descriptionErrorLabel.setVisible(true);
            isValid = false;
        }

        return isValid;
    }

    private void resetErrorLabels() {
        titreErrorLabel.setVisible(false);
        auteurErrorLabel.setVisible(false);
        typeErrorLabel.setVisible(false);
        descriptionErrorLabel.setVisible(false);
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        return (index > 0) ? fileName.substring(index) : ".png";
    }

    private String getProjectResourceImagePath() {
        return System.getProperty("user.dir") + "/src/main/resources/images";
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void clearForm() {
        tfTitre.clear();
        tfAuteur.clear();
        tfVideoUrl.clear();
        cbType.setValue(null);
        tfImagePath.clear();
        taDescription.clear();
        imagePreview.setImage(null);
        selectedFile = null;
        imageName = "";
        programmeEnCours = null;
        resetErrorLabels();
    }

    public void setProgrammeToEdit(Programme programme) {
        if (programme != null) {
            this.programmeEnCours = programme;

            tfTitre.setText(programme.getTitre());
            tfAuteur.setText(programme.getAuteur());
            tfVideoUrl.setText(programme.getVideoUrl());
            cbType.setValue(programme.getType());
            tfImagePath.setText(programme.getImage());
            taDescription.setText(programme.getDescription());

            File imageFile = new File(getProjectResourceImagePath() + "/" + programme.getImage());
            if (imageFile.exists()) {
                imagePreview.setImage(new Image(imageFile.toURI().toString()));
            }
        }
    }
}
