package controllers.Panier;

import controllers.Commande.AjouterCommandeController;
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
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.LignePanier;
import models.Panier;
import models.User;
import services.Commande.CommandeService;
import services.PanierService;
import utils.session;

import java.io.IOException;
import java.net.URL;
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

    @FXML
    private Button btnRetour;

    private PanierService panierService;
    private CommandeService commandeService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        panierService = PanierService.getInstance();
        commandeService = new CommandeService();

        colNom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getProduit().getNomProduit()));
        colPrix.setCellValueFactory(cellData -> new javafx.beans.property.SimpleDoubleProperty(
                cellData.getValue().getProduit().getPrix()).asObject());
        colQuantite.setCellValueFactory(cellData -> cellData.getValue().quantiteProperty().asObject());
        colSousTotal.setCellValueFactory(cellData -> cellData.getValue().sousTotalProperty().asObject());


        // Colonne action (supprimer + ajouter/diminuer)
        TableColumn<LignePanier, Void> colAction = new TableColumn<>("Action");
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnMoins = new Button("-");
            private final Button btnPlus = new Button("+");
            private final Button btnSupprimer = new Button("Supprimer");
            private final HBox hbox = new HBox(5, btnMoins, btnPlus, btnSupprimer);

            {
                btnMoins.setStyle("-fx-background-color: #f1c40f; -fx-text-fill: white;");
                btnPlus.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white;");
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");

                btnMoins.setOnAction(e -> {
                    LignePanier ligne = getTableView().getItems().get(getIndex());
                    if (ligne.getQuantite() > 1) {
                        panierService.modifierQuantite(ligne.getProduit().getId(), ligne.getQuantite() - 1);
                        mettreAJourTablePanier();
                    }
                });

                btnPlus.setOnAction(e -> {
                    LignePanier ligne = getTableView().getItems().get(getIndex());
                    if (ligne.getQuantite() < ligne.getProduit().getStock()) {
                        panierService.modifierQuantite(ligne.getProduit().getId(), ligne.getQuantite() + 1);
                        mettreAJourTablePanier();
                    } else {
                        showAlert("Stock insuffisant", "Quantité maximale atteinte", Alert.AlertType.WARNING);
                    }
                });

                btnSupprimer.setOnAction(e -> {
                    LignePanier ligne = getTableView().getItems().get(getIndex());
                    panierService.supprimerDuPanier(ligne.getProduit().getId());
                    mettreAJourTablePanier();
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : hbox);
            }
        });

        tablePanier.getColumns().add(colAction);
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
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            showAlert("Connexion requise", "Veuillez vous connecter pour passer une commande", Alert.AlertType.WARNING);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnCommander.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Connexion");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        if (panierService.getPanier().getItems().isEmpty()) {
            showAlert("Panier vide", "Votre panier est vide. Ajoutez des produits avant de commander.", Alert.AlertType.WARNING);
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Commande/AjouterCommande.fxml"));
            Parent root = loader.load();

            AjouterCommandeController controller = loader.getController();
            controller.setPanierService(panierService);

            Stage stage = new Stage();
            stage.setTitle("Finaliser la commande");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de commande: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void retourProduits(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/AfficherProduitFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnRetour.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Nos Produits");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la liste des produits", Alert.AlertType.ERROR);
        }
    }
}
