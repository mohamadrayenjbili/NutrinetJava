package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import models.Commande;
import models.Produit;
import services.ICommandeService;
import services.CommandeService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class DetailCommandeController implements Initializable {

    @FXML
    private ListView<Commande> commandesList;

    @FXML
    private ListView<Produit> produitsList;

    @FXML
    private TextField idField;

    @FXML
    private TextField nomField;

    @FXML
    private TextField mailField;

    @FXML
    private TextArea adresseArea;

    @FXML
    private TextField dateField;

    @FXML
    private TextField statusField;

    @FXML
    private TextField paiementField;

    private ICommandeService commandeService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("Initialisation du DetailCommandeController...");

        commandeService = new CommandeService();

        // Configurer l'affichage des commandes
        commandesList.setCellFactory(param -> new ListCell<Commande>() {
            @Override
            protected void updateItem(Commande commande, boolean empty) {
                super.updateItem(commande, empty);
                if (empty || commande == null) {
                    setText(null);
                } else {
                    setText(String.format("%d - %s - %s - %s",
                            commande.getId(),
                            commande.getNomC(),
                            commande.getDateC(),
                            commande.getStatus()));
                }
            }
        });

        // Configurer l'affichage des produits
        produitsList.setCellFactory(param -> new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %.2f €",
                            produit.getNomProduit(),
                            produit.getPrix()));
                }
            }
        });

        loadAllCommandes();
        setupSelectionListener();

        System.out.println("Initialisation terminée");
    }

    private void loadAllCommandes() {
        try {
            System.out.println("Chargement des commandes en cours...");
            commandesList.getItems().clear();

            List<Commande> commandes = commandeService.getAllCommandes();
            System.out.println("Nombre de commandes récupérées : " + commandes.size());

            if (commandes.isEmpty()) {
                System.out.println("Aucune commande trouvée dans la base de données!");
            } else {
                commandesList.getItems().addAll(commandes);
                System.out.println("Liste rafraîchie avec " + commandesList.getItems().size() + " commandes");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les commandes: " + e.getMessage());
        }
    }

    private void setupSelectionListener() {
        commandesList.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        showCommandeDetails(newSelection);
                        loadProduitsForCommande(newSelection.getId());
                    }
                });
    }

    private void showCommandeDetails(Commande commande) {
        idField.setText(String.valueOf(commande.getId()));
        nomField.setText(commande.getNomC());
        mailField.setText(commande.getMail());
        adresseArea.setText(commande.getAdress());
        dateField.setText(commande.getDateC().toString());
        statusField.setText(commande.getStatus());
        paiementField.setText(commande.getMethodeDePaiement());
    }

    private void loadProduitsForCommande(int commandeId) {
        try {
            produitsList.getItems().clear();
            List<Produit> produits = commandeService.getProduitsByCommandeId(commandeId);

            if (produits.isEmpty()) {
                produitsList.getItems().add(new Produit(0, "Aucun produit", 0, "", "", "", 0));
            } else {
                produitsList.getItems().addAll(produits);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger les produits: " + e.getMessage());
        }
    }

    @FXML
    private void handleModify() {
        Commande selectedCommande = commandesList.getSelectionModel().getSelectedItem();
        if (selectedCommande == null) {
            showAlert("Aucune sélection", "Veuillez sélectionner une commande à modifier");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCommande.fxml"));
            Parent root = loader.load();

            ModifierCommandeController controller = loader.getController();
            controller.loadCommandeDirectly(selectedCommande);

            Stage stage = (Stage) commandesList.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Commande");

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir l'interface de modification: " + e.getMessage());
        }
    }

    @FXML
    private void handleReturn() {
        try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource("/AjouterCommande.fxml"));
            commandesList.getScene().setRoot(pane);
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
}