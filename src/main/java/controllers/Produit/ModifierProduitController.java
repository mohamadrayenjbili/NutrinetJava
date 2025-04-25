package controllers.Produit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import models.Produit;
import services.Produit.ProduitService;

public class ModifierProduitController implements Initializable {

    @FXML private ListView<Produit> listViewProduits;
    @FXML private Label lblId;
    @FXML private Label lblNomError;
    @FXML private Label lblPrixError;
    @FXML private Label lblDescError;
    @FXML private Label lblStockError;
    @FXML private Label lblCategorieError;
    @FXML private Label lblImageError;

    @FXML private TextField txtNom;
    @FXML private TextField txtPrix;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<String> comboCategorie;
    @FXML private TextField txtImage;
    @FXML private TextField txtStock;
    @FXML private ImageView imagePreview;

    @FXML private Button btnModifier;
    @FXML private Button btnAnnuler;
    @FXML private Button btnSupprimer;
    @FXML private Button btnVoirDetails;
    @FXML private Button btnChoisirImage;

    private ProduitService produitService;
    private ObservableList<Produit> produitsList;
    private Produit produitSelectionne;
    private File selectedImageFile;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        produitService = new ProduitService();
        produitsList = FXCollections.observableArrayList();

        configurerInterface();
        chargerCategories();
        chargerProduits();
        setupInputValidation();

        // ✅ Ajouter le listener de sélection ici
        listViewProduits.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectionnerProduit(newSelection);
            }
        });
    }

    private void configurerInterface() {
        // Configuration des boutons
        btnModifier.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAnnuler.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white; -fx-font-weight: bold;");
        btnSupprimer.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold;");
        btnVoirDetails.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");
        btnChoisirImage.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white; -fx-font-weight: bold;");

        // Configuration de la ListView
        listViewProduits.setCellFactory(lv -> new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    Label nomLabel = new Label(produit.getNomProduit());
                    Label prixLabel = new Label(String.format("%.2f€", produit.getPrix()));
                    Label stockLabel = new Label("Stock: " + produit.getStock());

                    nomLabel.setStyle("-fx-font-weight: bold;");
                    prixLabel.setStyle("-fx-text-fill: #2ecc71;");
                    stockLabel.setStyle("-fx-text-fill: #3498db;");

                    hbox.getChildren().addAll(nomLabel, prixLabel, stockLabel);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void setupInputValidation() {
        // Validation du nom
        txtNom.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 50) txtNom.setText(oldVal);
            validateNom();
        });

        // Validation du prix
        txtPrix.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) txtPrix.setText(oldVal);
            validatePrix();
        });

        // Validation du stock
        txtStock.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) txtStock.setText(oldVal);
            validateStock();
        });

        // Limite la description
        txtDescription.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 500) txtDescription.setText(oldVal);
            validateDescription();
        });

        // Validation catégorie
        comboCategorie.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateCategorie();
        });

        // Validation image
        txtImage.textProperty().addListener((obs, oldVal, newVal) -> {
            validateImage();
        });
    }

    private void chargerCategories() {
        comboCategorie.getItems().addAll(
                "Whey", "Creatine", "Pré-workout", "Post-workout", "Vitamines"
        );
    }

    private void chargerProduits() {
        try {
            List<Produit> produits = produitService.getAllProduits();
            produitsList.setAll(produits);
            listViewProduits.setItems(produitsList);
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors du chargement des produits: " + e.getMessage());
        }
    }

    @FXML
    private void handleChoisirImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Sélectionner une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        selectedImageFile = fileChooser.showOpenDialog(new Stage());
        if (selectedImageFile != null) {
            txtImage.setText(selectedImageFile.getAbsolutePath());
            imagePreview.setImage(new Image(selectedImageFile.toURI().toString()));
            validateImage();
        }
    }

    @FXML
    private void handleModifier(ActionEvent event) {
        if (produitSelectionne == null || !validateAllFields()) return;

        try {
            updateProduitFromFields();
            produitService.updateProduit(produitSelectionne);
            chargerProduits();
            showAlert(AlertType.INFORMATION, "Succès", "Produit modifié avec succès!");
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void updateProduitFromFields() {
        produitSelectionne.setNomProduit(txtNom.getText());
        produitSelectionne.setPrix(Double.parseDouble(txtPrix.getText()));
        produitSelectionne.setDescription(txtDescription.getText());
        produitSelectionne.setCategorie(comboCategorie.getValue());
        produitSelectionne.setStock(Integer.parseInt(txtStock.getText()));

        if (selectedImageFile != null) {
            // Ici vous pourriez implémenter la logique pour sauvegarder la nouvelle image
            produitSelectionne.setImage(selectedImageFile.getName());
        }
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        if (produitSelectionne == null) return;

        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer le produit: " + produitSelectionne.getNomProduit());
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer ce produit ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    produitService.deleteProduit(produitSelectionne.getId());
                    chargerProduits();
                    deselectionnerProduit();
                    showAlert(AlertType.INFORMATION, "Succès", "Produit supprimé avec succès!");
                } catch (SQLException e) {
                    showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleVoirDetails(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Produit/DetailProduit.fxml"));
            Stage stage = (Stage) btnVoirDetails.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Produits");
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        deselectionnerProduit();
    }

    private void selectionnerProduit(Produit produit) {
        this.produitSelectionne = produit;

        lblId.setText(String.valueOf(produit.getId()));
        txtNom.setText(produit.getNomProduit());
        txtPrix.setText(String.format("%.2f", produit.getPrix()));
        txtDescription.setText(produit.getDescription());
        comboCategorie.setValue(produit.getCategorie());
        txtImage.setText(produit.getImage());
        txtStock.setText(String.valueOf(produit.getStock()));

        if (produit.getImage() != null && !produit.getImage().isEmpty()) {
            try {
                Image image = new Image("file:" + produit.getImage());
                imagePreview.setImage(image);
            } catch (Exception e) {
                imagePreview.setImage(null);
            }
        } else {
            imagePreview.setImage(null);
        }

        btnModifier.setDisable(false);
        btnSupprimer.setDisable(false);
    }

    private void deselectionnerProduit() {
        this.produitSelectionne = null;
        listViewProduits.getSelectionModel().clearSelection();

        lblId.setText("");
        txtNom.clear();
        txtPrix.clear();
        txtDescription.clear();
        comboCategorie.getSelectionModel().clearSelection();
        txtImage.clear();
        txtStock.clear();
        imagePreview.setImage(null);
        selectedImageFile = null;

        hideAllErrors();
        desactiverBoutons();
    }

    private void desactiverBoutons() {
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }

    private boolean validateAllFields() {
        boolean valid = true;
        if (!validateNom()) valid = false;
        if (!validatePrix()) valid = false;
        if (!validateDescription()) valid = false;
        if (!validateCategorie()) valid = false;
        if (!validateStock()) valid = false;
        return valid;
    }

    private boolean validateNom() {
        if (txtNom.getText().isEmpty()) {
            showError(lblNomError, "Le nom est requis");
            return false;
        } else if (txtNom.getText().length() < 3) {
            showError(lblNomError, "Minimum 3 caractères");
            return false;
        }
        hideError(lblNomError);
        return true;
    }

    private boolean validatePrix() {
        if (txtPrix.getText().isEmpty()) {
            showError(lblPrixError, "Le prix est requis");
            return false;
        }

        try {
            double prix = Double.parseDouble(txtPrix.getText());
            if (prix <= 0) {
                showError(lblPrixError, "Doit être > 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError(lblPrixError, "Format invalide");
            return false;
        }

        hideError(lblPrixError);
        return true;
    }

    private boolean validateDescription() {
        if (txtDescription.getText().isEmpty()) {
            showError(lblDescError, "Description requise");
            return false;
        } else if (txtDescription.getText().length() < 10) {
            showError(lblDescError, "Minimum 10 caractères");
            return false;
        }
        hideError(lblDescError);
        return true;
    }

    private boolean validateStock() {
        if (txtStock.getText().isEmpty()) {
            showError(lblStockError, "Stock requis");
            return false;
        }

        try {
            int stock = Integer.parseInt(txtStock.getText());
            if (stock < 0) {
                showError(lblStockError, "Doit être ≥ 0");
                return false;
            }
        } catch (NumberFormatException e) {
            showError(lblStockError, "Nombre entier");
            return false;
        }

        hideError(lblStockError);
        return true;
    }

    private boolean validateCategorie() {
        if (comboCategorie.getValue() == null) {
            showError(lblCategorieError, "Catégorie requise");
            return false;
        }
        hideError(lblCategorieError);
        return true;
    }

    private boolean validateImage() {
        // Validation optionnelle de l'image
        hideError(lblImageError);
        return true;
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    private void hideError(Label label) {
        label.setVisible(false);
    }

    private void hideAllErrors() {
        hideError(lblNomError);
        hideError(lblPrixError);
        hideError(lblDescError);
        hideError(lblStockError);
        hideError(lblCategorieError);
        hideError(lblImageError);
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loadProduitDirectly(Produit produit) {
        selectionnerProduit(produit);
        listViewProduits.getSelectionModel().select(produit);
    }
}