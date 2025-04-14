// 4. Contrôleur pour le panier
package controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Commande;
import models.LignePanier;
import models.Panier;
import models.Produit;
import services.CommandeService;
import services.PanierService;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class PanierController implements Initializable {

    @FXML
    private TableView<LignePanier> tablePanier;

    @FXML
    private TableColumn<LignePanier, String> colNom;

    @FXML
    private TableColumn<LignePanier, Double> colPrix;

    @FXML
    private TableColumn<LignePanier, Integer> colQuantite;

    @FXML
    private TableColumn<LignePanier, Double> colSousTotal;

    @FXML
    private Label lblTotal;

    @FXML
    private Button btnViderPanier;

    @FXML
    private Button btnCommander;

    private PanierService panierService;
    private CommandeService commandeService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        panierService = PanierService.getInstance();
        commandeService = new CommandeService();

        // Initialiser les colonnes de la table
        colNom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getProduit().getNomProduit()));
        colPrix.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(
                cellData.getValue().getProduit().getPrix()).asObject());
        colQuantite.setCellValueFactory(new PropertyValueFactory<>("quantite"));
        colSousTotal.setCellValueFactory(new PropertyValueFactory<>("sousTotal"));

        // Ajouter une colonne pour le bouton supprimer
        TableColumn<LignePanier, Void> colAction = new TableColumn<>("Action");
        colAction.setCellFactory(param -> new TableCell<>() {
            private final Button btnSupprimer = new Button("Supprimer");

            {
                btnSupprimer.setOnAction(event -> {
                    LignePanier ligne = getTableView().getItems().get(getIndex());
                    panierService.supprimerDuPanier(ligne.getProduit().getId());
                    mettreAJourTablePanier();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnSupprimer);
                }
            }
        });

        tablePanier.getColumns().add(colAction);

        // Mettre à jour l'affichage du panier
        mettreAJourTablePanier();
    }

    private void mettreAJourTablePanier() {
        Panier panier = panierService.getPanier();
        ObservableList<LignePanier> items = FXCollections.observableArrayList(panier.getItemsList());
        tablePanier.setItems(items);
        lblTotal.setText(String.format("Total: %.2f €", panier.getTotal()));
    }

    @FXML
    private void viderPanier(ActionEvent event) {
        panierService.viderPanier();
        mettreAJourTablePanier();
    }

    @FXML
    private void passerCommande(ActionEvent event) {
        if (panierService.getPanier().getItems().isEmpty()) {
            afficherMessage("Panier vide", "Votre panier est vide. Ajoutez des produits avant de commander.", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Ouvrir la fenêtre pour finaliser la commande
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/User/AjouterCommande.fxml"));
            Parent root = loader.load();

            AjouterCommandeController controller = loader.getController();
            controller.setPanierService(panierService);

            Stage stage = new Stage();
            stage.setTitle("Finaliser la commande");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Erreur lors de l'ouverture de la fenêtre de commande: " + e.getMessage(), Alert.AlertType.ERROR);
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