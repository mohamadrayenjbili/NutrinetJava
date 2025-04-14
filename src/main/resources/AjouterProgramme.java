package controllers.Programme;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
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
    @FXML private Button btnRetourListe; // ✅ Nouveau bouton

    private ProgrammeService programmeService = new ProgrammeService();
    private Programme programmeEnCours = null;

    private File selectedFile;
    private String imageName = "";

    @FXML
    private void initialize() {
        cbType.getItems().addAll("Régime", "Cohérence cardiaque", "Arrêt du tabac", "Santé Mentale");
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

    @FXML
    private void handleRetourListe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/AfficherProgramme.fxml")); // Modifie le chemin selon ton projet
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) btnRetourListe.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(AlertType.ERROR, "Erreur", "Impossible de charger la liste des programmes.");
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

            File imageFile = new File(getProjectResourceImagePath() + "/" + programme.getImage());
            if (imageFile.exists()) {
                imagePreview.setImage(new Image(imageFile.toURI().toString()));
            }
        }
    }
}
