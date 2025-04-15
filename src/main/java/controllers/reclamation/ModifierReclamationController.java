package controllers.reclamation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import models.Reclamation;
import services.reclamation.ModifierReclamationService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ModifierReclamationController {

    @FXML
    private TextField sujetField;

    @FXML
    private TextArea messageArea;

    @FXML
    private Button attacherBtn;

    @FXML
    private Label imageLabel;

    private Reclamation reclamation;

    private String attachmentPath = null;

    private final ModifierReclamationService modifierService = new ModifierReclamationService();
    private final List<String> grosMots = Arrays.asList("merde", "con", "putain", "salop", "chiant");

    public ModifierReclamationController() throws SQLException {
    }

    public void initData(Reclamation r) {
        this.reclamation = r;
        sujetField.setText(r.getSujet());
        messageArea.setText(r.getMessage());

        if (r.getAttachmentFile() != null && !r.getAttachmentFile().isEmpty()) {
            imageLabel.setText("Image actuelle : " + r.getAttachmentFile().substring(r.getAttachmentFile().lastIndexOf("/") + 1));
            attachmentPath = r.getAttachmentFile();
        }
    }

    @FXML
    private void handleUpdate() {
        String sujet = sujetField.getText().trim();
        String message = messageArea.getText().trim();

        if (sujet.isEmpty() || message.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "❌ Tous les champs sont obligatoires.");
            return;
        }

        if (contientGrosMot(sujet) || contientGrosMot(message)) {
            showAlert(Alert.AlertType.ERROR, "❌ Sujet ou message contient un mot inapproprié.");
            return;
        }

        reclamation.setSujet(sujet);
        reclamation.setMessage(message);
        reclamation.setAttachmentFile(attachmentPath);

        boolean success = modifierService.modifierReclamation(reclamation);

        if (success) {
            showAlert(Alert.AlertType.INFORMATION, "✅ Réclamation modifiée avec succès.");
            retour();
        } else {
            showAlert(Alert.AlertType.ERROR, "❌ Échec de la modification.");
        }
    }

    @FXML
    public void ajouterImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                String targetDir = "src/main/resources/images/";
                File directory = new File(targetDir);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                File targetFile = new File(targetDir + selectedFile.getName());
                Files.copy(selectedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                attachmentPath = "images/" + selectedFile.getName();
                imageLabel.setText("Image attachée: " + selectedFile.getName());
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout de l'image.");
            }
        }
    }

    @FXML
    private void handleRetour() {
        retour();
    }

    private void retour() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/reclamation/ListReclamation.fxml"));
            sujetField.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean contientGrosMot(String texte) {
        String lower = texte.toLowerCase();
        return grosMots.stream().anyMatch(lower::contains);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }
}
