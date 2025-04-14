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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Produit;
import services.PanierService;
import services.ProduitService;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

class AfficherProduitsFrontController implements Initializable {

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

            for (Produit produit : produits) {
                // Ne pas afficher les produits sans stock
                if (produit.getStock() <= 0) {
                    continue;
                }

                // Créer une carte pour chaque produit
                VBox cardProduit = createProduitCard(produit);
                flowProduits.getChildren().add(cardProduit);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Erreur lors du chargement des produits: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private VBox createProduitCard(Produit produit) {
        VBox card = new VBox(10);
        card.setStyle("-fx-border-color: #CCCCCC; -fx-border-radius: 5; -fx-background-color: white;");
        card.setPrefWidth(200);
        card.setPadding(new Insets(10));

        // Image du produit
        ImageView imgProduit = new ImageView();
        imgProduit.setFitWidth(180);
        imgProduit.setFitHeight(120);
        imgProduit.setPreserveRatio(true);

        try {
            if (produit.getImage() != null && !produit.getImage().isEmpty()) {
                Image image = new Image(produit.getImage());
                imgProduit.setImage(image);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors du chargement de l'image: " + e.getMessage());
        }

        // Informations du produit
        Label lblNom = new Label(produit.getNomProduit());
        lblNom.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label lblPrix = new Label(String.format("%.2f €", produit.getPrix()));
        lblPrix.setStyle("-fx-font-size: 12px;");

        Label lblStock = new Label("Stock: " + produit.getStock());
        lblStock.setStyle("-fx-font-size: 12px;");

        // Spinner pour la quantité
        Spinner<Integer> spinnerQuantite = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, produit.getStock(), 1);
        spinnerQuantite.setValueFactory(valueFactory);
        spinnerQuantite.setPrefWidth(180);

        // Bouton d'ajout au panier
        Button btnAjouter = new Button("Ajouter au panier");
        btnAjouter.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        btnAjouter.setPrefWidth(180);

        // Action du bouton
        btnAjouter.setOnAction(event -> {
            int quantite = spinnerQuantite.getValue();
            if (quantite > 0 && quantite <= produit.getStock()) {
                panierService.ajouterAuPanier(produit, quantite);
                // Mettre à jour le spinner après ajout
                SpinnerValueFactory<Integer> newValueFactory =
                        new SpinnerValueFactory.IntegerSpinnerValueFactory(1, produit.getStock(), 1);
                spinnerQuantite.setValueFactory(newValueFactory);
            }
        });

        // Bouton pour voir les détails
        Button btnDetails = new Button("Voir détails");
        btnDetails.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
        btnDetails.setPrefWidth(180);

        // Action du bouton détails
        btnDetails.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/DetailProduit.fxml"));
                Parent root = loader.load();

                DetailProduitController controller = loader.getController();
                controller.setProduit(produit);

                Stage stage = new Stage();
                stage.setTitle("Détails du produit");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                e.printStackTrace();
                afficherMessage("Erreur", "Erreur lors de l'ouverture des détails: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        });

        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(imgProduit, lblNom, lblPrix, lblStock, spinnerQuantite, btnAjouter, btnDetails);

        return card;
    }

    @FXML
    private void voirPanier(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/Panier.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Mon Panier");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Erreur lors de l'ouverture du panier: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void afficherMessage(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}