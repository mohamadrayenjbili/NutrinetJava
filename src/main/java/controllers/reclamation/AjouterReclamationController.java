package controllers.reclamation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import models.Reclamation;
import models.User;
import services.reclamation.ReclamationService;
import utils.session;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class AjouterReclamationController {

    @FXML
    private TextField sujetField;

    @FXML
    private TextArea messageArea;

    @FXML
    private Button attacherBtn;

    @FXML
    private Label imageLabel;

    @FXML
    private Button envoyerBtn;

    @FXML
    private Button retourBtn;


    private String attachmentPath = null;
    private final ReclamationService service = new ReclamationService();

    private final List<String> grosMots = Arrays.asList("merde", "con", "putain", "salop", "chiant");

    @FXML
    public void initialize() {
        envoyerBtn.setOnAction(e -> ajouterReclamation());
        retourBtn.setOnAction(e -> retour());
    }

    @FXML
    public void ajouterImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif"));
        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                // Dossier de destination pour les images
                String targetDir = "src/main/resources/images/";

                // Vérification si le dossier existe, sinon création
                File directory = new File(targetDir);
                if (!directory.exists()) {
                    directory.mkdirs(); // Crée les répertoires nécessaires
                }

                // Création du fichier cible dans le dossier de destination
                File targetFile = new File(targetDir + selectedFile.getName());

                // Copie du fichier dans le dossier de destination
                Files.copy(selectedFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                // Mettre à jour le chemin de l'image attachée
                attachmentPath = "images/" + selectedFile.getName();

                // Mise à jour du label pour afficher le nom de l'image attachée
                imageLabel.setText("Image attachée: " + selectedFile.getName());

            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ajout de l'image. Veuillez réessayer.");
            }
        }
    }

    private void ajouterReclamation() {
        String sujet = sujetField.getText().trim();
        String message = messageArea.getText().trim();

        if (sujet.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "❌ Le champ sujet est obligatoire.");
            return;
        }

        if (message.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "❌ Le champ message est obligatoire.");
            return;
        }

        if (contientGrosMot(sujet)) {
            showAlert(Alert.AlertType.ERROR, "❌ Le sujet contient un mot inapproprié.");
            return;
        }

        if (contientGrosMot(message)) {
            showAlert(Alert.AlertType.ERROR, "❌ Le message contient un mot inapproprié.");
            return;
        }

        User currentUser = session.getCurrentUser();
        Reclamation r = new Reclamation(
                sujet,
                message,
                0, // note retirée
                currentUser,
                "En attente",
                LocalDateTime.now(),
                attachmentPath
        );
        service.ajouter(r);
        showAlert(Alert.AlertType.INFORMATION, "✅ Réclamation envoyée !");
        sujetField.clear();
        messageArea.clear();
        attachmentPath = null;
        imageLabel.setText("Aucune image sélectionnée");
        retour();
    }



    private boolean contientGrosMot(String texte) {
        String lower = texte.toLowerCase();
        return grosMots.stream().anyMatch(lower::contains);
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message);
        alert.showAndWait();
    }

    private void retour() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/Reclamation/listReclamation.fxml")); // ⚠️ change le chemin si besoin
            javafx.scene.Parent root = loader.load();
            sujetField.getScene().setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
