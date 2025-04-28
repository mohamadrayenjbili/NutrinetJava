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
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

    private ProduitService produitService = new ProduitService();
    private PanierService panierService = PanierService.getInstance();
    private ObservableList<Produit> allProduits;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Charger le CSS
        flowProduits.getStylesheets().add(getClass().getResource("/Produit/listdidou.css").toExternalForm());

        try {
            List<Produit> produits = produitService.getAllProduits();
            allProduits = FXCollections.observableArrayList(produits);

            // Remplir le ComboBox avec les catégories uniques
            Set<String> categories = produits.stream()
                    .map(Produit::getCategorie)
                    .collect(Collectors.toSet());
            filterComboBox.setItems(FXCollections.observableArrayList(categories));
            filterComboBox.getItems().add(0, "Toutes les catégories");
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
        VBox card = new VBox();
        card.getStyleClass().add("produit-card");

        // Image
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image("file:src/main/resources/images/" + produit.getImage()));
        } catch (Exception ex) {
            imageView.setImage(new Image("file:src/main/resources/images/default_produit.png"));
        }
        imageView.setFitWidth(280);
        imageView.setFitHeight(180);
        imageView.setPreserveRatio(false);
        imageView.getStyleClass().add("card-image");

        // Contenu
        VBox content = new VBox(10);
        content.getStyleClass().add("card-content");

        Label titleLabel = new Label(produit.getNomProduit());
        titleLabel.getStyleClass().add("card-title");

        Label priceLabel = new Label(String.format("%.2f €", produit.getPrix()));
        priceLabel.getStyleClass().add("card-price");

        Label categoryLabel = new Label("🏷 " + produit.getCategorie());
        categoryLabel.getStyleClass().add("card-category");

        Label stockLabel = new Label(produit.getStock() > 0 ? "📦 En stock: " + produit.getStock() : "❌ Hors stock");
        stockLabel.getStyleClass().add(produit.getStock() > 0 ? "card-stock" : "card-stock-warning");

        // Spinner de quantité
        Spinner<Integer> spinnerQuantite = new Spinner<>();
        if (produit.getStock() > 0) {
            spinnerQuantite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, produit.getStock(), 1));
        } else {
            spinnerQuantite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 0, 0));
        }
        spinnerQuantite.getStyleClass().add("quantity-spinner");
        spinnerQuantite.setDisable(produit.getStock() <= 0);

        // Boutons d'action
        HBox buttonBox = new HBox(10);
        buttonBox.getStyleClass().add("action-buttons");

        Button btnAjouter = new Button("🛒 Ajouter");
        btnAjouter.getStyleClass().add("btn-ajouter");
        btnAjouter.setDisable(produit.getStock() <= 0);

        Button btnDetails = new Button("👁️ Détails");
        btnDetails.getStyleClass().add("btn-details");

        Button btnSouhaits = new Button("❤️ Souhaits");
        btnSouhaits.getStyleClass().add("btn-souhaits");

        buttonBox.getChildren().addAll(btnAjouter, btnDetails, btnSouhaits);

        // Actions des boutons
        btnAjouter.setOnAction(e -> handleAjouterPanier(produit, spinnerQuantite.getValue()));
        btnDetails.setOnAction(e -> openProductDetails(produit));
        btnSouhaits.setOnAction(e -> handleAjouterSouhaits(produit));

        content.getChildren().addAll(titleLabel, priceLabel, categoryLabel, stockLabel, spinnerQuantite, buttonBox);
        card.getChildren().addAll(imageView, content);

        // Animation au survol
        card.setOnMouseEntered(e -> {
            card.setEffect(new DropShadow(20, Color.rgb(0, 0, 0, 0.2)));
            card.setTranslateY(-2);
        });

        card.setOnMouseExited(e -> {
            card.setEffect(new DropShadow(10, Color.rgb(0, 0, 0, 0.1)));
            card.setTranslateY(0);
        });

        return card;
    }

    private void handleAjouterPanier(Produit produit, int quantite) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            showAlert("Connexion requise", "Veuillez vous connecter pour ajouter des produits au panier", Alert.AlertType.WARNING);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) flowProduits.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Connexion");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            return;
        }

        panierService.ajouterAuPanier(produit, quantite);
    }

    private void handleAjouterSouhaits(Produit produit) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            showAlert("Connexion requise", "Veuillez vous connecter pour ajouter des produits à votre liste de souhaits", Alert.AlertType.WARNING);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) flowProduits.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Connexion");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
            showAlert("Erreur", "Erreur lors de l'ajout à la liste de souhaits", Alert.AlertType.ERROR);
        }
    }

    private void openProductDetails(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/DetailProduit.fxml"));
            Parent root = loader.load();

            DetailProduitController controller = loader.getController();
            controller.setProduit(produit);

            Stage stage = (Stage) flowProduits.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Détails: " + produit.getNomProduit());
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir les détails", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void voirPanier(ActionEvent event) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            showAlert("Connexion requise", "Veuillez vous connecter pour accéder au panier", Alert.AlertType.WARNING);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnVoirPanier.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Connexion");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Panier/Panier.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) btnVoirPanier.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Panier");
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir le panier", Alert.AlertType.ERROR);
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