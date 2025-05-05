package controllers;

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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import models.Produit;
import services.PanierService;
import services.ProduitService;

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

    private ProduitService produitService = new ProduitService();
    private PanierService panierService = PanierService.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
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
        card.setPadding(new Insets(15));
        card.setSpacing(10);
        card.setPrefWidth(220);
        card.setStyle("-fx-background-color: white; -fx-background-radius: 15; -fx-border-radius: 15; -fx-cursor: hand;");
        card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1)));

        // Hover effect
        card.setOnMouseEntered(e -> card.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.25))));
        card.setOnMouseExited(e -> card.setEffect(new DropShadow(5, Color.rgb(0, 0, 0, 0.1))));

        // Image
        ImageView imgProduit = new ImageView();
        try {
            Image image = new Image("file:src/main/resources/images/" + produit.getImage());
            imgProduit.setImage(image);
        } catch (Exception e) {
            Image defaultImage = new Image("file:src/main/resources/images/default-product.png");
            imgProduit.setImage(defaultImage);
            Tooltip.install(imgProduit, new Tooltip("Image non disponible"));
        }

        imgProduit.setFitHeight(130);
        imgProduit.setFitWidth(190);
        imgProduit.setPreserveRatio(false);
        imgProduit.setSmooth(true);

        // Clipping avec coins arrondis
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
        Spinner<Integer> spinnerQuantite = new Spinner<>();
        spinnerQuantite.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, produit.getStock(), 1));
        spinnerQuantite.setPrefWidth(190);
        spinnerQuantite.setStyle("-fx-font-size: 14px;");

        // Boutons d'action
        HBox buttonBox = new HBox(5);
        Button btnAjouter = new Button("Ajouter");
        btnAjouter.setStyle("-fx-background-color: #00b894; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 5; -fx-padding: 5 10;");
        btnAjouter.setPrefWidth(90);

        btnAjouter.setOnAction(e -> {
            int quantite = spinnerQuantite.getValue();
            if (quantite > 0 && quantite <= produit.getStock()) {
                panierService.ajouterAuPanier(produit, quantite);
                spinnerQuantite.getValueFactory().setValue(1);
                showAlert("Succès", produit.getNomProduit() + " ajouté au panier", Alert.AlertType.INFORMATION);
            }
        });

        Button btnDetails = new Button("Détails");
        btnDetails.setStyle("-fx-background-color: #0984e3; -fx-text-fill: white; -fx-font-weight: bold; " +
                "-fx-background-radius: 5; -fx-padding: 5 10;");
        btnDetails.setPrefWidth(90);

        btnDetails.setOnAction(e -> openProductDetails(produit));

        buttonBox.getChildren().addAll(btnAjouter, btnDetails);
        card.getChildren().addAll(imgProduit, lblNom, lblPrix, lblStock, spinnerQuantite, buttonBox);

        return card;
    }

    private void openProductDetails(Produit produit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailProduit.fxml"));
            Parent root = loader.load();

            DetailProduitController controller = loader.getController();
            controller.setProduit(produit);

            // Récupérer la scène actuelle à partir d'un élément de l'interface
            // (par exemple le FlowPane flowProduits ou le bouton de détails)
            Stage stage = (Stage) flowProduits.getScene().getWindow();

            // Remplacer la scène actuelle
            stage.setScene(new Scene(root));
            stage.setTitle("Détails: " + produit.getNomProduit());
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir les détails", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void voirPanier(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Panier.fxml"));
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