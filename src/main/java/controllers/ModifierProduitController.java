package controllers;

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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import models.Produit;
import services.ProduitService;

public class ModifierProduitController implements Initializable {

    @FXML private ListView<Produit> listViewProduits;
    @FXML private Label lblId;

    @FXML private TextField txtNom;
    @FXML private TextField txtPrix;
    @FXML private TextArea txtDescription;
    @FXML private ComboBox<String> comboCategorie;
    @FXML private TextField txtImage;
    @FXML private TextField txtStock;

    @FXML private Button btnModifier;
    @FXML private Button btnAnnuler;
    @FXML private Button btnSupprimer;
    @FXML private Button btnVoirDetails;

    private ProduitService produitService;
    private ObservableList<Produit> produitsList;
    private Produit produitSelectionne;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        produitService = new ProduitService();
        produitsList = FXCollections.observableArrayList();

        // Configuration des boutons
        configurerBoutons();

        // Charger les catégories
        chargerCategories();

        // Charger les produits
        chargerProduits();

        // Désactiver les boutons initialement
        desactiverBoutons();

        // Configuration de la ListView pour afficher le nom des produits
        listViewProduits.setCellFactory(lv -> new javafx.scene.control.ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                setText(empty ? null : produit.getNomProduit() + " (" + produit.getPrix() + "€)");
            }
        });

        // Gestion de la sélection dans la liste
        listViewProduits.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        selectionnerProduit(newSelection);
                    } else {
                        deselectionnerProduit();
                    }
                });
    }

    private void configurerBoutons() {
        // Style des boutons
        btnModifier.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white;");
        btnModifier.setFont(Font.font("System Bold", 14));

        btnAnnuler.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white;");
        btnAnnuler.setFont(Font.font("System Bold", 14));

        btnSupprimer.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white;");
        btnSupprimer.setFont(Font.font("System Bold", 14));
    }

    private void chargerCategories() {
        comboCategorie.getItems().addAll(
                "Électronique",
                "Vêtements",
                "Alimentation",
                "Maison",
                "Sport"
        );
    }

    private void chargerProduits() {
        try {
            List<Produit> produits = produitService.getAllProduits();
            produitsList.setAll(produits);
            listViewProduits.setItems(produitsList);
        } catch (SQLException e) {
            afficherAlerte(AlertType.ERROR, "Erreur",
                    "Erreur lors du chargement des produits: " + e.getMessage());
        }
    }

    private void selectionnerProduit(Produit produit) {
        this.produitSelectionne = produit;

        // Remplir les champs avec les données du produit
        lblId.setText(String.valueOf(produit.getId()));
        txtNom.setText(produit.getNomProduit());
        txtPrix.setText(String.valueOf(produit.getPrix()));
        txtDescription.setText(produit.getDescription());
        comboCategorie.setValue(produit.getCategorie());
        txtImage.setText(produit.getImage());
        txtStock.setText(String.valueOf(produit.getStock()));

        // Activer les boutons
        btnModifier.setDisable(false);
        btnSupprimer.setDisable(false);
    }

    private void deselectionnerProduit() {
        this.produitSelectionne = null;

        // Vider les champs
        lblId.setText("");
        txtNom.clear();
        txtPrix.clear();
        txtDescription.clear();
        comboCategorie.getSelectionModel().clearSelection();
        txtImage.clear();
        txtStock.clear();

        // Désactiver les boutons
        desactiverBoutons();
    }

    private void desactiverBoutons() {
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }

    @FXML
    private void handleModifier(ActionEvent event) {
        if (produitSelectionne == null) return;

        try {
            // Mettre à jour l'objet produit
            produitSelectionne.setNomProduit(txtNom.getText());
            produitSelectionne.setPrix(Double.parseDouble(txtPrix.getText()));
            produitSelectionne.setDescription(txtDescription.getText());
            produitSelectionne.setCategorie(comboCategorie.getValue());
            produitSelectionne.setImage(txtImage.getText());
            produitSelectionne.setStock(Integer.parseInt(txtStock.getText()));

            // Mettre à jour dans la base
            produitService.updateProduit(produitSelectionne);

            // Rafraîchir la liste
            chargerProduits();

            afficherAlerte(AlertType.INFORMATION, "Succès",
                    "Produit modifié avec succès !");

        } catch (NumberFormatException e) {
            afficherAlerte(AlertType.ERROR, "Erreur",
                    "Veuillez entrer des valeurs numériques valides pour le prix et le stock");
        } catch (SQLException e) {
            afficherAlerte(AlertType.ERROR, "Erreur",
                    "Erreur lors de la modification: " + e.getMessage());
        }
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        if (produitSelectionne == null) return;

        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Suppression du produit");
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le produit: "
                + produitSelectionne.getNomProduit() + " ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                try {
                    produitService.deleteProduit(produitSelectionne.getId());
                    chargerProduits();
                    deselectionnerProduit();
                    afficherAlerte(AlertType.INFORMATION, "Succès",
                            "Produit supprimé avec succès !");
                } catch (SQLException e) {
                    afficherAlerte(AlertType.ERROR, "Erreur",
                            "Erreur lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleVoirDetails(ActionEvent event) {
        try {
            System.out.println("Chargement de la vue DetailProduit...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DetailProduit.fxml"));
            Parent root = loader.load();

            DetailProduitController controller = loader.getController();

            Scene scene = new Scene(root);
            Stage stage = (Stage) btnVoirDetails.getScene().getWindow();
            stage.setScene(scene);
            stage.setTitle("Liste des Produits");

            System.out.println("Navigation vers la liste des produits réussie");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        deselectionnerProduit();
        listViewProduits.getSelectionModel().clearSelection();
    }

    private void afficherAlerte(AlertType type, String titre, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loadProduitDirectly(Produit produit) {
        this.produitSelectionne = produit;
        // Update the UI fields directly
        lblId.setText(String.valueOf(produit.getId()));
        txtNom.setText(produit.getNomProduit());
        txtPrix.setText(String.valueOf(produit.getPrix()));
        txtDescription.setText(produit.getDescription());
        comboCategorie.setValue(produit.getCategorie());
        txtImage.setText(produit.getImage());
        txtStock.setText(String.valueOf(produit.getStock()));
        btnModifier.setDisable(false);
        btnSupprimer.setDisable(false);
    }
}