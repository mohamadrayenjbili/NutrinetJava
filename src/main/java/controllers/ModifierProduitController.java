//package Controllers;
//
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.*;
//import javafx.stage.FileChooser;
//import javafx.stage.Stage;
//import models.Produit;
//import services.IProduitService;
//import services.ProduitService;
//
//import java.io.File;
//import java.net.URL;
//import java.sql.SQLException;
//import java.util.ResourceBundle;
//
//public class ModifierProduitController implements Initializable {
//
//    @FXML
//    private TextField tfNomProduit;
//
//    @FXML
//    private TextField tfPrix;
//
//    @FXML
//    private TextArea taDescription;
//
//    @FXML
//    private ComboBox<String> cbCategorie;
//
//    @FXML
//    private TextField tfImagePath;
//
//    @FXML
//    private TextField tfStock;
//
//    @FXML
//    private Button btnModifier;
//
//    @FXML
//    private Button btnAnnuler;
//
//    @FXML
//    private Button btnChoisirImage;
//
//    private IProduitService produitService;
//    private Produit produitActuel;
//    private String imagePath = "";
//
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        // Initialiser le service
//        produitService = new ProduitService();
//
//        // Charger les catégories dans le ComboBox
//        cbCategorie.getItems().addAll("Électronique", "Vêtements", "Alimentation", "Maison", "Loisirs", "Autres");
//    }
//
//    // Méthode pour charger les détails du produit à modifier
//    public void setProduit(Produit produit) {
//        this.produitActuel = produit;
//
//        // Remplir les champs avec les données du produit
//        tfNomProduit.setText(produit.getNomProduit());
//        tfPrix.setText(String.valueOf(produit.getPrix()));
//        taDescription.setText(produit.getDescription());
//        cbCategorie.setValue(produit.getCategorie());
//        tfImagePath.setText(produit.getImage());
//        tfStock.setText(String.valueOf(produit.getStock()));
//
//        imagePath = produit.getImage();
//    }
//
//    @FXML
//    private void handleChoisirImage(ActionEvent event) {
//        FileChooser fileChooser = new FileChooser();
//        fileChooser.setTitle("Sélectionner une image");
//        fileChooser.getExtensionFilters().addAll(
//                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
//        );
//
//        File selectedFile = fileChooser.showOpenDialog(new Stage());
//        if (selectedFile != null) {
//            imagePath = selectedFile.getAbsolutePath();
//            tfImagePath.setText(imagePath);
//        }
//    }
//
//    @FXML
//    private void handleModifier(ActionEvent event) {
//        try {
//            // Validation des champs
//            if (validateFields()) {
//                // Mettre à jour les données du produit
//                produitActuel.setNomProduit(tfNomProduit.getText());
//                produitActuel.setPrix(Double.parseDouble(tfPrix.getText()));
//                produitActuel.setDescription(taDescription.getText());
//                produitActuel.setCategorie(cbCategorie.getValue());
//                produitActuel.setImage(imagePath);
//                produitActuel.setStock(Integer.parseInt(tfStock.getText()));
//
//                // Mettre à jour dans la base de données
//                produitService.updateProduit(produitActuel);
//
//                // Afficher un message de succès
//                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit modifié avec succès!");
//
//                // Fermer la fenêtre
//                ((Stage) btnModifier.getScene().getWindow()).close();
//            }
//        } catch (SQLException e) {
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la modification du produit: " + e.getMessage());
//        } catch (NumberFormatException e) {
//            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir des valeurs numériques valides pour le prix et le stock.");
//        }
//    }
//
//    @FXML
//    private void handleAnnuler(ActionEvent event) {
//        // Fermer la fenêtre
//        ((Stage) btnAnnuler.getScene().getWindow()).close();
//    }
//
//    private boolean validateFields() {
//        StringBuilder errors = new StringBuilder();
//
//        if (tfNomProduit.getText().isEmpty()) {
//            errors.append("- Le nom du produit est requis.\n");
//        }
//
//        if (tfPrix.getText().isEmpty()) {
//            errors.append("- Le prix est requis.\n");
//        } else {
//            try {
//                double prix = Double.parseDouble(tfPrix.getText());
//                if (prix <= 0) {
//                    errors.append("- Le prix doit être supérieur à 0.\n");
//                }
//            } catch (NumberFormatException e) {
//                errors.append("- Le prix doit être un nombre valide.\n");
//            }
//        }
//
//        if (taDescription.getText().isEmpty()) {
//            errors.append("- La description est requise.\n");
//        }
//
//        if (cbCategorie.getValue() == null) {
//            errors.append("- La catégorie est requise.\n");
//        }
//
//        if (tfStock.getText().isEmpty()) {
//            errors.append("- Le stock est requis.\n");
//        } else {
//            try {
//                int stock = Integer.parseInt(tfStock.getText());
//                if (stock < 0) {
//                    errors.append("- Le stock ne peut pas être négatif.\n");
//                }
//            } catch (NumberFormatException e) {
//                errors.append("- Le stock doit être un nombre entier.\n");
//            }
//        }
//
//        if (errors.length() > 0) {
//            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errors.toString());
//            return false;
//        }
//
//        return true;
//    }
//
//    private void showAlert(Alert.AlertType type, String title, String content) {
//        Alert alert = new Alert(type);
//        alert.setTitle(title);
//        alert.setHeaderText(null);
//        alert.setContentText(content);
//        alert.showAndWait();
//    }
//}