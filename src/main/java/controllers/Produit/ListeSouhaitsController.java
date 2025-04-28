package controllers.Produit;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.ListeSouhaits;
import models.Notification;
import models.User;
import services.ListeSouhaitsService;
import services.NotificationService;
import utils.session;

public class ListeSouhaitsController implements Initializable {

    @FXML
    private TableView<ListeSouhaits> tableListeSouhaits;

    @FXML
    private TableColumn<ListeSouhaits, String> colNom;

    @FXML
    private TableColumn<ListeSouhaits, Double> colPrix;

    @FXML
    private TableColumn<ListeSouhaits, Integer> colStock;

    @FXML
    private TableColumn<ListeSouhaits, String> colDateAjout;

    @FXML
    private TableColumn<ListeSouhaits, Void> colAction;

    @FXML
    private Label lblMessage;

    private ListeSouhaitsService listeSouhaitsService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listeSouhaitsService = new ListeSouhaitsService();
        configurerTable();
        chargerListeSouhaits();
        verifierNotifications();
    }

    private void configurerTable() {
        colNom.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getProduit().getNomProduit()));
        colPrix.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(cellData.getValue().getProduit().getPrix()).asObject());
        colStock.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getProduit().getStock()).asObject());
        colDateAjout.setCellValueFactory(cellData -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            return new javafx.beans.property.SimpleStringProperty(sdf.format(cellData.getValue().getDateAjout()));
        });

        // Colonne d'action
        colAction.setCellFactory(col -> new TableCell<>() {
            private final Button btnSupprimer = new Button("Supprimer");
            private final Button btnAjouterPanier = new Button("Ajouter au panier");

            {
                btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                btnAjouterPanier.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");

                btnSupprimer.setOnAction(e -> {
                    ListeSouhaits ls = getTableView().getItems().get(getIndex());
                    supprimerDeListeSouhaits(ls);
                });

                btnAjouterPanier.setOnAction(e -> {
                    ListeSouhaits ls = getTableView().getItems().get(getIndex());
                    ajouterAuPanier(ls);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ListeSouhaits ls = getTableView().getItems().get(getIndex());
                    btnAjouterPanier.setDisable(ls.getProduit().getStock() <= 0);
                    HBox buttons = new HBox(5, btnSupprimer, btnAjouterPanier);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void chargerListeSouhaits() {
        try {
            User loggedUser = session.getCurrentUser();
            if (loggedUser != null) {
                tableListeSouhaits.getItems().clear();
                tableListeSouhaits.getItems().addAll(listeSouhaitsService.getListeSouhaitsByUserId(loggedUser.getId()));
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors du chargement de la liste de souhaits: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void supprimerDeListeSouhaits(ListeSouhaits ls) {
        try {
            User loggedUser = session.getCurrentUser();
            if (loggedUser != null) {
                listeSouhaitsService.supprimerDeListeSouhaits(loggedUser.getId(), ls.getProduitId());
                chargerListeSouhaits();
                showAlert("Succès", "Produit supprimé de la liste de souhaits", Alert.AlertType.INFORMATION);
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void ajouterAuPanier(ListeSouhaits ls) {
        // Implémenter l'ajout au panier
        showAlert("Information", "Fonctionnalité à implémenter", Alert.AlertType.INFORMATION);
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/AfficherProduitFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) tableListeSouhaits.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Nos Produits");
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à la liste des produits", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void verifierNotifications() {
        try {
            User loggedUser = session.getCurrentUser();
            if (loggedUser != null) {
                NotificationService notificationService = new NotificationService();
                List<Notification> notifications = notificationService.getNotificationsByUserId(loggedUser.getId());

                for (Notification notification : notifications) {
                    if (!notification.isLu()) {
                        showAlert("Notification", notification.getMessage(), Alert.AlertType.INFORMATION);
                        notificationService.marquerCommeLu(notification.getId());
                    }
                }
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la vérification des notifications: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}