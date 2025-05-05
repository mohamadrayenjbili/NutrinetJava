package controllers.Produit;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import models.Produit;
import models.User;
import services.Produit.IProduitService;
import services.Produit.ProduitService;
import services.user.log_historyService;
import utils.session;

public class AjouterProduitController implements Initializable {

    private File selectedFile;

    @FXML private TextField tfNomProduit;
    @FXML private TextField tfPrix;
    @FXML private TextArea taDescription;
    @FXML private ComboBox<String> cbCategorie;
    @FXML private TextField tfImagePath;
    @FXML private TextField tfStock;
    @FXML private Button btnAjouter;
    @FXML private Button btnAnnuler;
    @FXML private Button btnChoisirImage;
    @FXML private Button btnVoirDetails;
    @FXML private ImageView imagePreview;
    @FXML private Label lblNomError;
    @FXML private Label lblPrixError;
    @FXML private Label lblDescError;
    @FXML private Label lblStockError;
    @FXML private Label lblCategorieError;
    @FXML private Button btnCodePromo;
    @FXML private Button btnModifierProduit;

    @FXML private VBox sidebar;
    @FXML private Button toggleSidebarBtn;
    @FXML private VBox mainContent;

    private IProduitService produitService;
    private String imagePath = "";
    private final log_historyService logService = new log_historyService();
    private boolean isSidebarVisible = true;
    private TranslateTransition sidebarTransition;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        produitService = new ProduitService();
        cbCategorie.getItems().addAll("Whey", "Creatine", "Pré-workout", "Post-workout", "Vitamines");
        setupInputValidation();
        hideErrorLabels();
        
        // Ensure toggle button is on top
        toggleSidebarBtn.toFront();
        
        // Initialize the sidebar transition
        sidebarTransition = new TranslateTransition(Duration.millis(300), sidebar);
        sidebarTransition.setFromX(0);
        sidebarTransition.setToX(-220);
        
        // Add listener to update main content position
        sidebarTransition.setOnFinished(event -> {
            if (!isSidebarVisible) {
                AnchorPane.setLeftAnchor(mainContent, 20.0);
            } else {
                AnchorPane.setLeftAnchor(mainContent, 240.0);
            }
            // Keep button on top after animation
            toggleSidebarBtn.toFront();
        });
    }

    private void setupInputValidation() {
        // Validation du nom (3-50 caractères)
        tfNomProduit.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 50) {
                tfNomProduit.setText(oldVal);
            }
            validateNom();
        });

        // Validation du prix (format numérique)
        tfPrix.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*(\\.\\d{0,2})?")) {
                tfPrix.setText(oldVal);
            }
            validatePrix();
        });

        // Validation du stock (entier positif)
        tfStock.textProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal.matches("\\d*")) {
                tfStock.setText(oldVal);
            }
            validateStock();
        });

        // Limite la description (500 caractères max)
        taDescription.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 500) {
                taDescription.setText(oldVal);
            }
            validateDescription();
        });

        // Validation catégorie
        cbCategorie.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateCategorie();
        });
    }

    private void hideErrorLabels() {
        lblNomError.setVisible(false);
        lblPrixError.setVisible(false);
        lblDescError.setVisible(false);
        lblStockError.setVisible(false);
        lblCategorieError.setVisible(false);
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

    @FXML
    private void handleAjouter(ActionEvent event) {
        if (validateAllFields()) {
            try {
                Produit produit = createProduitFromInput();
                produitService.ajouterProduit(produit);

                showAlert(Alert.AlertType.INFORMATION, "Succès", "Produit ajouté avec succès!");
                clearFields();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ajout: " + e.getMessage());
            }
        }
    }

    private Produit createProduitFromInput() throws IOException {
        Produit produit = new Produit();
        produit.setNomProduit(tfNomProduit.getText());
        produit.setPrix(Double.parseDouble(tfPrix.getText()));
        produit.setDescription(taDescription.getText());
        produit.setCategorie(cbCategorie.getValue());
        produit.setStock(Integer.parseInt(tfStock.getText()));

        if (selectedFile != null) {
            String uniqueName = saveImageToResources();
            produit.setImage(uniqueName);
        }
        return produit;
    }

    private String saveImageToResources() throws IOException {
        String extension = getFileExtension(selectedFile.getName());
        String uniqueName = java.util.UUID.randomUUID().toString().replaceAll("-", "") + "." + extension;
        String destDirPath = System.getProperty("user.dir") + "/src/main/resources/images";

        File destDir = new File(destDirPath);
        if (!destDir.exists()) destDir.mkdirs();

        File destFile = new File(destDir, uniqueName);
        Files.copy(selectedFile.toPath(), destFile.toPath());
        return uniqueName;
    }

    private boolean validateAllFields() {
        boolean isValid = true;

        if (!validateNom()) isValid = false;
        if (!validatePrix()) isValid = false;
        if (!validateDescription()) isValid = false;
        if (!validateStock()) isValid = false;
        if (!validateCategorie()) isValid = false;

        return isValid;
    }

    private boolean validateNom() {
        if (tfNomProduit.getText().isEmpty()) {
            showError(lblNomError, "Le nom est requis");
            return false;
        } else if (tfNomProduit.getText().length() < 3) {
            showError(lblNomError, "Minimum 3 caractères");
            return false;
        }
        hideError(lblNomError);
        return true;
    }

    private boolean validatePrix() {
        if (tfPrix.getText().isEmpty()) {
            showError(lblPrixError, "Le prix est requis");
            return false;
        }

        try {
            double prix = Double.parseDouble(tfPrix.getText());
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
        if (taDescription.getText().isEmpty()) {
            showError(lblDescError, "Description requise");
            return false;
        } else if (taDescription.getText().length() < 10) {
            showError(lblDescError, "Minimum 10 caractères");
            return false;
        }
        hideError(lblDescError);
        return true;
    }

    private boolean validateStock() {
        if (tfStock.getText().isEmpty()) {
            showError(lblStockError, "Stock requis");
            return false;
        }

        try {
            int stock = Integer.parseInt(tfStock.getText());
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
        if (cbCategorie.getValue() == null) {
            showError(lblCategorieError, "Catégorie requise");
            return false;
        }
        hideError(lblCategorieError);
        return true;
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    private void hideError(Label label) {
        label.setVisible(false);
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void handleVoirDetails(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/DetailProduit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnVoirDetails.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Produits");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    private void clearFields() {
        tfNomProduit.clear();
        tfPrix.clear();
        taDescription.clear();
        cbCategorie.setValue(null);
        tfStock.clear();
        tfImagePath.clear();
        imagePreview.setImage(null);
        selectedFile = null;
        hideErrorLabels();
    }

    private String getFileExtension(String filename) {
        int index = filename.lastIndexOf('.');
        return (index > 0 && index < filename.length() - 1)
                ? filename.substring(index + 1)
                : "png";
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void handleCodePromo(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CodePromo.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnCodePromo.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Gérer les Codes Promo");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir CodePromo.fxml : " + e.getMessage());
        }
    }

    @FXML
    public void toggleSidebar() {
        if (isSidebarVisible) {
            // Hide sidebar
            sidebarTransition.setFromX(0);
            sidebarTransition.setToX(-220);
            sidebarTransition.play();
            isSidebarVisible = false;
        } else {
            // Show sidebar
            sidebar.setTranslateX(-220); // Reset position before showing
            sidebarTransition.setFromX(-220);
            sidebarTransition.setToX(0);
            sidebarTransition.play();
            isSidebarVisible = true;
        }
        // Keep button on top after toggle
        toggleSidebarBtn.toFront();
    }

    public void showUsersList(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/users_list.fxml"));
            Scene usersListScene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(usersListScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleAjouterProduit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/AjouterProduit.fxml"));
            Scene ajouterProduitScene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(ajouterProduitScene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void handleLogout(ActionEvent actionEvent) {
        try {
            // Récupérer l'utilisateur avant de vider la session
            User currentUser = session.getCurrentUser();

            // Ajouter le log de déconnexion
            if (currentUser != null) {
                String details = "User " + currentUser.getName() + " " + currentUser.getPrename() + " logged out";
                logService.addLog(currentUser.getEmail(), "Logout", details);
            }

            // Vider la session
            session.clearSession();

            // Redirection vers la page de connexion
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/sign_in.fxml"));
            Scene loginScene = new Scene(loader.load());

            Stage stage = (Stage) ((javafx.scene.Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(loginScene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void handleModifierProduit(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/ModifierProduit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnModifierProduit.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modification Des Produits");
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'ouvrir Produit/ModifierProduit.fxml : " + e.getMessage());
        }
    }

}