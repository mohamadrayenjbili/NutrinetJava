package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Produit;
import services.IProduitService;
import services.ProduitService;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class AjouterProduitController implements Initializable {

    private File selectedFile;

    @FXML
    private TextField tfNomProduit;

    @FXML
    private TextField tfPrix;

    @FXML
    private TextArea taDescription;

    @FXML
    private ComboBox<String> cbCategorie;

    @FXML
    private TextField tfImagePath;

    @FXML
    private TextField tfStock;

    @FXML
    private Button btnAjouter;

    @FXML
    private Button btnAnnuler;

    @FXML
    private Button btnChoisirImage;

    @FXML
    private Button btnVoirDetails; // Nouveau bouton ajouté

    @FXML
    private ImageView imagePreview;

    private IProduitService produitService;
    private String imagePath = "";



    @Override
    public void initialize(URL url, ResourceBundle rb) {
        produitService = new ProduitService();
        cbCategorie.getItems().addAll("Whey", "Creatine", "Pré-workout", "Post-workout", "Vitamines");
    }

    @FXML
    private void handleChoisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            imagePath = selectedFile.getAbsolutePath();
            tfImagePath.setText(imagePath);
            Image image = new Image(selectedFile.toURI().toString());
            imagePreview.setImage(image);
        }
    }

    private String getFileExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return (index > 0 && index < filename.length() - 1)
                ? filename.substring(index + 1)
                : "png";
    }

    private String getProjectResourceImagePath() {
        return System.getProperty("user.dir") + "/src/main/resources/images";
    }

    @FXML
    private void handleAjouter(ActionEvent event) {
        try {
            if (validateFields()) {
                Produit produit = new Produit();
                produit.setNomProduit(tfNomProduit.getText());
                produit.setPrix(Double.parseDouble(tfPrix.getText()));
                produit.setDescription(taDescription.getText());
                produit.setCategorie(cbCategorie.getValue());
                produit.setStock(Integer.parseInt(tfStock.getText()));

                if (selectedFile != null) {
                    String extension = getFileExtension(selectedFile.getName());
                    String uniqueName = java.util.UUID.randomUUID().toString().replaceAll("-", "") + "." + extension;

                    String destDirPath = getProjectResourceImagePath();
                    File destDir = new File(destDirPath);
                    if (!destDir.exists()) {
                        destDir.mkdirs();
                    }

                    File destFile = new File(destDir, uniqueName);
                    Files.copy(selectedFile.toPath(), destFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                    produit.setImage(uniqueName);
                } else {
                    produit.setImage(null);
                }

                produitService.ajouterProduit(produit);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit ajouté avec succès!");
                clearFields();
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout du produit: " + e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        clearFields();
    }

    // Nouvelle méthode pour gérer la navigation vers DetailProduit.fxml
    @FXML
    private void handleVoirDetails(ActionEvent event) {
        try {
            System.out.println("Chargement de la vue DetailProduit...");

            // Charger le FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailProduit.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur et s'assurer qu'il est initialisé
            DetailProduitController controller = loader.getController();

            // Note: il n'est pas nécessaire d'appeler manuellement initialize() car
            // JavaFX l'appelle automatiquement lors du chargement

            // Créer une nouvelle scène
            Scene scene = new Scene(root);

            // Obtenir la fenêtre actuelle et changer sa scène
            Stage stage = (Stage) btnVoirDetails.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Produits");

            System.out.println("Navigation vers la liste des produits réussie");

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher la liste: " + e.getMessage());
        }
    }



    private boolean validateFields() {
        StringBuilder errors = new StringBuilder();

        if (tfNomProduit.getText().isEmpty()) {
            errors.append("- Le nom du produit est requis.\n");
        }

        if (tfPrix.getText().isEmpty()) {
            errors.append("- Le prix est requis.\n");
        } else {
            try {
                double prix = Double.parseDouble(tfPrix.getText());
                if (prix <= 0) {
                    errors.append("- Le prix doit être supérieur à 0.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Le prix doit être un nombre valide.\n");
            }
        }

        if (taDescription.getText().isEmpty()) {
            errors.append("- La description est requise.\n");
        }

        if (cbCategorie.getValue() == null) {
            errors.append("- La catégorie est requise.\n");
        }

        if (tfStock.getText().isEmpty()) {
            errors.append("- Le stock est requis.\n");
        } else {
            try {
                int stock = Integer.parseInt(tfStock.getText());
                if (stock < 0) {
                    errors.append("- Le stock ne peut pas être négatif.\n");
                }
            } catch (NumberFormatException e) {
                errors.append("- Le stock doit être un nombre entier.\n");
            }
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errors.toString());
            return false;
        }

        return true;
    }

    private void clearFields() {
        tfNomProduit.clear();
        tfPrix.clear();
        taDescription.clear();
        cbCategorie.setValue(null);
        tfImagePath.clear();
        tfStock.clear();
        imagePath = "";
        imagePreview.setImage(null);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
