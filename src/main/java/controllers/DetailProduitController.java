package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Produit;
import services.IProduitService;
import services.ProduitService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class DetailProduitController implements Initializable {

    @FXML
    private ListView<Produit> produitsList;

    @FXML
    private ImageView imageView;

    @FXML
    private TextField idField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField prixField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private TextField categorieField;

    @FXML
    private TextField stockField;

    private IProduitService produitService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Initialisation du DetailProduitController...");

        produitService = new ProduitService();

        // Vérifier que la liste n'est pas null
        if (produitsList == null) {
            System.err.println("ERREUR: produitsList est null!");
            return;
        }

        // Configurer l'affichage des éléments dans la ListView
        produitsList.setCellFactory(param -> new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                } else {
                    setText(String.format("%d - %s (%.2f €) - %s - Stock: %d",
                            produit.getId(),
                            produit.getNomProduit(),
                            produit.getPrix(),
                            produit.getCategorie(),
                            produit.getStock()));
                }
            }
        });

        loadAllProduits();
        setupSelectionListener();

        System.out.println("Initialisation terminée");
    }

    private void loadAllProduits() {
        try {
            System.out.println("Chargement des produits en cours...");

            // Effacer d'abord les éléments existants
            produitsList.getItems().clear();

            // Obtenir tous les produits
            List<Produit> produits = produitService.getAllProduits();

            // Déboguer - afficher les produits récupérés
            System.out.println("Nombre de produits récupérés : " + produits.size());
            for (Produit p : produits) {
                System.out.println("Produit: ID=" + p.getId() + ", Nom=" + p.getNomProduit());
            }

            if (produits.isEmpty()) {
                System.out.println("Aucun produit trouvé dans la base de données!");
            } else {
                // Ajouter les produits à la liste
                produitsList.getItems().addAll(produits);
                System.out.println("Liste rafraîchie avec " + produitsList.getItems().size() + " produits");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les produits: " + e.getMessage());
        }
    }

    private void setupSelectionListener() {
        produitsList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showProduitDetails(newSelection);
                    }
                });
    }

    private void showProduitDetails(Produit produit) {
        idField.setText(String.valueOf(produit.getId()));
        nomField.setText(produit.getNomProduit());
        prixField.setText(String.format("%.2f", produit.getPrix()));
        descriptionArea.setText(produit.getDescription());
        categorieField.setText(produit.getCategorie());
        stockField.setText(String.valueOf(produit.getStock()));

        // Charger l'image si elle existe
        if (produit.getImage() != null && !produit.getImage().isEmpty()) {
            try {
                String imagePath = "file:src/main/resources/images/" + produit.getImage();
                Image image = new Image(imagePath);
                imageView.setImage(image);
            } catch (Exception e) {
                imageView.setImage(null);
                System.err.println("Erreur de chargement de l'image: " + e.getMessage());
            }
        } else {
            imageView.setImage(null);
        }
    }
    @FXML
    private void handleModify() {
        Produit selectedProduit = produitsList.getSelectionModel().getSelectedItem();
        if (selectedProduit == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner un produit à modifier");
            return;
        }

        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierProduit.fxml"));
            Parent root = loader.load(); // Use Parent instead of specific pane type

            // Get the controller and pass the selected product
            ModifierProduitController controller = loader.getController();
            controller.loadProduitDirectly(selectedProduit);

            // Set the new scene
            Stage stage = (Stage) produitsList.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Produit");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'interface de modification: " + e.getMessage());
        }
    }

    @FXML
    private void handleReturn() {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("/AjouterProduit.fxml"));
            produitsList.getScene().setRoot(pane);
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de retourner: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void setProduit(Produit produit) {
    }
}