package controllers.Produit;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Produit;
import models.User;
import services.ListeSouhaitsService;
import services.PanierService;
import services.Produit.ProduitService;
import utils.session;

public class AfficherProduitsFrontController implements Initializable {

    @FXML
    private FlowPane flowProduits;

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> filterComboBox;

    @FXML
    private Button btnVoirPanier;

    @FXML
    private Button btnListeSouhaits;

    private ProduitService produitService = new ProduitService();
    private PanierService panierService = PanierService.getInstance();
    private ObservableList<Produit> allProduits;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Charger le CSS
        String cssPath = getClass().getResource("/Produit/listdidou.css").toExternalForm();
        flowProduits.getStylesheets().add(cssPath);
        
        // Ajouter le CSS de FontAwesome
        String fontAwesomePath = getClass().getResource("/styles/fontawesome.css").toExternalForm();
        flowProduits.getStylesheets().add(fontAwesomePath);
        
        // Appliquer le CSS à la scène entière
        flowProduits.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                if (!newValue.getStylesheets().contains(cssPath)) {
                    newValue.getStylesheets().add(cssPath);
                }
                if (!newValue.getStylesheets().contains(fontAwesomePath)) {
                    newValue.getStylesheets().add(fontAwesomePath);
                }
            }
        });

        try {
            List<Produit> produits = produitService.getAllProduits();
            allProduits = FXCollections.observableArrayList(produits);

            // Remplir le ComboBox avec les catégories uniques
            Set<String> categories = produits.stream()
                    .map(Produit::getCategorie)
                    .collect(Collectors.toSet());
            ObservableList<String> categoriesList = FXCollections.observableArrayList(categories);
            categoriesList.add(0, "Toutes les catégories");
            filterComboBox.setItems(categoriesList);
            filterComboBox.getSelectionModel().selectFirst();

            // Listener sur la barre de recherche
            searchField.textProperty().addListener((observable, oldValue, newValue) -> filterAndDisplayProduits());

            // Listener sur le filtre (ComboBox)
            filterComboBox.valueProperty().addListener((observable, oldValue, newValue) -> filterAndDisplayProduits());

            // Affichage initial de la liste
            filterAndDisplayProduits();

        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des produits: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void filterAndDisplayProduits() {
        flowProduits.getChildren().clear();

        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase() : "";
        String filterCategory = filterComboBox.getValue();
        final String effectiveFilterCategory = (filterCategory == null || filterCategory.equals("Toutes les catégories")) ? "" : filterCategory;

        List<Produit> filtered = allProduits.stream()
                .filter(p -> p.getNomProduit().toLowerCase().contains(searchText))
                .filter(p -> effectiveFilterCategory.isEmpty() || p.getCategorie().equalsIgnoreCase(effectiveFilterCategory))
                .collect(Collectors.toList());

        for (Produit p : filtered) {
            VBox card = createProduitCard(p);
            flowProduits.getChildren().add(card);
        }
    }

    private VBox createProduitCard(Produit produit) {
        // Container principal
        StackPane card = new StackPane();
        card.getStyleClass().add("produit-card");

        // Image container
        VBox imageContainer = new VBox();
        imageContainer.getStyleClass().add("image-container");
        imageContainer.setAlignment(javafx.geometry.Pos.CENTER);

        // Image
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image("file:src/main/resources/images/" + produit.getImage()));
        } catch (Exception ex) {
            imageView.setImage(new Image("file:src/main/resources/images/default_produit.png"));
        }
        imageView.setFitWidth(300);
        imageView.setFitHeight(300);
        imageView.setPreserveRatio(false);
        imageView.getStyleClass().add("produit-image");

        imageContainer.getChildren().add(imageView);

        // Overlay container pour les boutons (initialement invisible)
        VBox overlayContent = new VBox(10);
        overlayContent.getStyleClass().add("overlay-content");
        overlayContent.setAlignment(javafx.geometry.Pos.CENTER);
        overlayContent.setVisible(false);

        // Prix
        Label priceLabel = new Label(String.format("%.2f €", produit.getPrix()));
        priceLabel.getStyleClass().add("overlay-prix");

        // Boutons
        VBox buttonBox = new VBox(8);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setSpacing(10);

        // Bouton Ajouter
        Button btnAjouter = new Button("🛒");
        btnAjouter.getStyleClass().add("overlay-btn-ajouter");
        btnAjouter.setDisable(produit.getStock() <= 0);

        // Boutons secondaires
        HBox buttonsContainer = new HBox(10);
        buttonsContainer.setAlignment(javafx.geometry.Pos.CENTER);

        Button btnSouhaiter = new Button("♥");
        btnSouhaiter.getStyleClass().add("overlay-btn-souhaiter");

        Button btnDetails = new Button("👁");
        btnDetails.getStyleClass().add("overlay-btn-details");

        buttonsContainer.getChildren().addAll(btnSouhaiter, btnDetails);
        buttonBox.getChildren().addAll(btnAjouter, buttonsContainer);
        overlayContent.getChildren().addAll(priceLabel, buttonBox);

        // Ajouter les conteneurs au StackPane
        card.getChildren().addAll(imageContainer, overlayContent);

        // Événements de survol
        card.setOnMouseEntered(e -> {
            overlayContent.setVisible(true);
        });

        card.setOnMouseExited(e -> {
            overlayContent.setVisible(false);
        });

        // Actions des boutons
        btnAjouter.setOnAction(e -> handleAjouterPanier(produit, 1));
        btnSouhaiter.setOnAction(e -> handleAjouterSouhaits(produit));
        btnDetails.setOnAction(e -> navigateToDetailProduit(produit));

        return new VBox(card);
    }

    private void handleAjouterPanier(Produit produit, int quantite) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            showAlert("Connexion requise", "Veuillez vous connecter pour ajouter des produits au panier", Alert.AlertType.WARNING);
            navigateToLogin();
            return;
        }

        try {
            panierService.ajouterAuPanier(produit, quantite);
            showAlert("Succès", "Produit ajouté au panier avec succès!", Alert.AlertType.INFORMATION);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout au panier: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void handleAjouterSouhaits(Produit produit) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            showAlert("Connexion requise", "Veuillez vous connecter pour ajouter des produits à votre liste de souhaits", Alert.AlertType.WARNING);
            navigateToLogin();
            return;
        }

        try {
            ListeSouhaitsService listeSouhaitsService = new ListeSouhaitsService();
            if (listeSouhaitsService.estDansListeSouhaits(currentUser.getId(), produit.getId())) {
                showAlert("Information", "Ce produit est déjà dans votre liste de souhaits", Alert.AlertType.INFORMATION);
            } else {
                listeSouhaitsService.ajouterAListeSouhaits(currentUser.getId(), produit.getId());
                showAlert("Succès", "Produit ajouté à votre liste de souhaits", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException ex) {
            showAlert("Erreur", "Erreur lors de l'ajout à la liste de souhaits: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) flowProduits.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Connexion");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @FXML
    private void navigateToDetailProduit(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/DetailProduit.fxml"));
            Parent root = loader.load();

            DetailProduitController controller = loader.getController();
            controller.setProduit(produit);

            Stage stage = (Stage) flowProduits.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails: " + produit.getNomProduit());
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir les détails: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void navigateToPanier(ActionEvent event) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            showAlert("Connexion requise", "Veuillez vous connecter pour accéder au panier", Alert.AlertType.WARNING);
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Panier/Panier.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVoirPanier.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Panier");
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le panier: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void navigateToListeSouhaits(ActionEvent event) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            showAlert("Connexion requise", "Veuillez vous connecter pour accéder à votre liste de souhaits", Alert.AlertType.WARNING);
            navigateToLogin();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/ListeSouhaits.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnListeSouhaits.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Ma Liste de Souhaits");
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la liste de souhaits: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}