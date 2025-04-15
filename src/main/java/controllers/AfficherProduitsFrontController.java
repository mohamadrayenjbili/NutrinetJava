package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import models.Produit;
import services.PanierService;
import services.ProduitService;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class AfficherProduitsFrontController implements Initializable {

    @FXML
    private FlowPane flowProduits;

    @FXML
    private Button btnVoirPanier;

    private ProduitService produitService;
    private PanierService panierService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        produitService = new ProduitService();
        panierService = PanierService.getInstance();
        chargerProduits();
    }

    private void chargerProduits() {
        try {
            List<Produit> produits = produitService.getAllProduits();
            flowProduits.getChildren().clear();
            flowProduits.setHgap(20);
            flowProduits.setVgap(20);
            flowProduits.setPadding(new Insets(15));

            for (Produit produit : produits) {
                if (produit.getStock() <= 0) continue;

                VBox cardProduit = createProduitCard(produit);
                flowProduits.getChildren().add(cardProduit);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement des produits: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox createProduitCard(Produit produit) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 0);");
        card.setPrefWidth(220);
        card.setPadding(new Insets(15));
        card.setMaxWidth(220);

        // Configuration de l'image avec coins arrondis
        ImageView imgProduit = createProductImageView(produit);

        // Rectangle pour les coins arrondis
        Rectangle clip = new Rectangle(190, 130);
        clip.setArcWidth(20);
        clip.setArcHeight(20);
        imgProduit.setClip(clip);

        // Informations du produit
        Label lblNom = new Label(produit.getNomProduit());
        lblNom.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #2d3436;");
        lblNom.setWrapText(true);
        lblNom.setMaxWidth(190);

        Label lblPrix = new Label(String.format("%.2f €", produit.getPrix()));
        lblPrix.setStyle("-fx-font-size: 14px; -fx-text-fill: #e17055; -fx-font-weight: bold;");

        Label lblStock = new Label("En stock: " + produit.getStock());
        lblStock.setStyle("-fx-font-size: 12px; -fx-text-fill: #636e72;");

        // Configuration du spinner de quantité
        Spinner<Integer> spinnerQuantite = createQuantitySpinner(produit);

        // Boutons d'action
        HBox buttonBox = new HBox(5);
        Button btnAjouter = createAddToCartButton(produit, spinnerQuantite);
        Button btnDetails = createDetailsButton(produit);
        buttonBox.getChildren().addAll(btnAjouter, btnDetails);

        card.getChildren().addAll(imgProduit, lblNom, lblPrix, lblStock, spinnerQuantite, buttonBox);
        return card;
    }

    private ImageView createProductImageView(Produit produit) {
        ImageView imgView = new ImageView();
        imgView.setFitWidth(190);
        imgView.setFitHeight(130);
        imgView.setPreserveRatio(false);
        imgView.setSmooth(true);

        Image productImage = loadProductImage(produit.getImage());
        if (productImage != null) {
            imgView.setImage(productImage);
        } else {
            // Charger une image par défaut si l'image du produit n'est pas trouvée
            try {
                InputStream stream = getClass().getResourceAsStream("/images/default-product.png");
                if (stream != null) {
                    imgView.setImage(new Image(stream));
                }
            } catch (Exception e) {
                System.err.println("Erreur chargement image par défaut");
            }
            Tooltip.install(imgView, new Tooltip("Image non disponible"));
        }

        return imgView;
    }

    private Spinner<Integer> createQuantitySpinner(Produit produit) {
        Spinner<Integer> spinner = new Spinner<>();
        spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, produit.getStock(), 1));
        spinner.setPrefWidth(190);
        spinner.setStyle("-fx-font-size: 14px;");
        return spinner;
    }

    private Button createAddToCartButton(Produit produit, Spinner<Integer> spinner) {
        Button button = new Button("Ajouter");
        button.setStyle("-fx-background-color: #00b894; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 5; -fx-padding: 5 10;");
        button.setPrefWidth(90);

        button.setOnAction(e -> {
            int quantite = spinner.getValue();
            if (quantite > 0 && quantite <= produit.getStock()) {
                panierService.ajouterAuPanier(produit, quantite);
                spinner.getValueFactory().setValue(1);
                showAlert("Succès", produit.getNomProduit() + " ajouté au panier", Alert.AlertType.INFORMATION);
            }
        });

        return button;
    }

    private Button createDetailsButton(Produit produit) {
        Button button = new Button("Détails");
        button.setStyle("-fx-background-color: #0984e3; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 5; -fx-padding: 5 10;");
        button.setPrefWidth(90);

        button.setOnAction(e -> openProductDetails(produit));
        return button;
    }

    private Image loadProductImage(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return getPlaceholderImage();
        }

        try {
            // Pour les ressources internes
            if (imagePath.startsWith("/")) {
                InputStream stream = getClass().getResourceAsStream(imagePath);
                if (stream != null) return new Image(stream);
            }
            // Pour les URLs web
            else if (imagePath.startsWith("http")) {
                return new Image(imagePath);
            }
            // Pour les fichiers locaux
            else {
                File file = new File(imagePath);
                if (file.exists()) {
                    String formattedPath = file.toURI().toString();
                    return new Image(formattedPath);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur chargement image: " + e.getMessage());
        }

        return getPlaceholderImage();
    }

    private Image getPlaceholderImage() {
        try {
            InputStream stream = getClass().getResourceAsStream("/images/default-product.png");
            if (stream != null) return new Image(stream);
        } catch (Exception e) {
            System.err.println("Erreur chargement image par défaut");
        }
        return null;
    }

    private void openProductDetails(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/DetailProduit.fxml"));
            Parent root = loader.load();

            DetailProduitController controller = loader.getController();
            controller.setProduit(produit);

            Stage stage = new Stage();
            stage.setTitle("Détails: " + produit.getNomProduit());
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir les détails", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void voirPanier(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/views/Panier.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Mon Panier");
            stage.setScene(new Scene(root));
            stage.show();
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