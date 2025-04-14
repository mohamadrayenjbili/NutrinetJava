////package controllers;
//
//import java.net.URL;
//import java.sql.SQLException;
//import java.util.List;
//import java.util.ResourceBundle;
//import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Alert.AlertType;
//import javafx.scene.control.Button;
//import javafx.scene.control.TableColumn;
//import javafx.scene.control.TableView;
//import javafx.scene.control.cell.PropertyValueFactory;
//import javafx.scene.text.Font;
//import models.Produit;
//import services.ProduitService;
//
//public class SupprimerProduitController implements Initializable {
//
//    @FXML
//    private TableView<Produit> tableViewProduits;
//
//    @FXML
//    private TableColumn<Produit, Integer> colId;
//
//    @FXML
//    private TableColumn<Produit, String> colNom;
//
//    @FXML
//    private TableColumn<Produit, Double> colPrix;
//
//    @FXML
//    private TableColumn<Produit, String> colDescription;
//
//    @FXML
//    private TableColumn<Produit, String> colCategorie;
//
//    @FXML
//    private TableColumn<Produit, Integer> colStock;
//
//    @FXML
//    private Button btnSupprimer;
//
//    private ProduitService produitService;
//    private ObservableList<Produit> produitsList;
//
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        produitService = new ProduitService();
//        produitsList = FXCollections.observableArrayList();
//
//        // Configuration des colonnes
//        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
//        colNom.setCellValueFactory(new PropertyValueFactory<>("nomProduit"));
//        colPrix.setCellValueFactory(new PropertyValueFactory<>("prix"));
//        colDescription.setCellValueFactory(new PropertyValueFactory<>("description"));
//        colCategorie.setCellValueFactory(new PropertyValueFactory<>("categorie"));
//        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
//
//        // Configuration du style du bouton
//        btnSupprimer.setStyle("-fx-background-color: #5bc0de; -fx-text-fill: white;");
//        btnSupprimer.setFont(Font.font("System Bold", 14));
//
//        // Chargement des données
//        chargerProduits();
//
//        // Désactiver le bouton supprimer si aucun produit n'est sélectionné
//        btnSupprimer.setDisable(true);
//
//        // Activer le bouton supprimer quand un produit est sélectionné
//        tableViewProduits.getSelectionModel().selectedItemProperty().addListener(
//                (observable, oldValue, newValue) -> btnSupprimer.setDisable(newValue == null));
//    }
//
//    @FXML
//    private void handleSupprimer(ActionEvent event) {
//        Produit produitSelectionne = tableViewProduits.getSelectionModel().getSelectedItem();
//
//        if (produitSelectionne != null) {
//            try {
//                // Afficher une confirmation
//                Alert confirmation = new Alert(AlertType.CONFIRMATION);
//                confirmation.setTitle("Confirmation de suppression");
//                confirmation.setHeaderText("Suppression du produit");
//                confirmation.setContentText("Êtes-vous sûr de vouloir supprimer le produit : "
//                        + produitSelectionne.getNomProduit() + " ?");
//
//                confirmation.showAndWait().ifPresent(response -> {
//                    if (response == javafx.scene.control.ButtonType.OK) {
//                        try {
//                            // Supprimer le produit
//                            produitService.deleteProduit(produitSelectionne.getId());
//
//                            // Actualiser la liste
//                            chargerProduits();
//
//                            // Afficher un message de succès
//                            afficherAlerte(AlertType.INFORMATION, "Succès",
//                                    "Produit supprimé avec succès !");
//                        } catch (SQLException e) {
//                            afficherAlerte(AlertType.ERROR, "Erreur",
//                                    "Erreur lors de la suppression : " + e.getMessage());
//                        }
//                    }
//                });
//
//            } catch (Exception e) {
//                afficherAlerte(AlertType.ERROR, "Erreur",
//                        "Une erreur est survenue : " + e.getMessage());
//            }
//        } else {
//            afficherAlerte(AlertType.WARNING, "Attention",
//                    "Veuillez sélectionner un produit à supprimer.");
//        }
//    }
//
//    @FXML
//    private void handleRefresh(ActionEvent event) {
//        chargerProduits();
//    }
//
//    private void chargerProduits() {
//        try {
//            List<Produit> produits = produitService.getAllProduits();
//            produitsList.clear();
//            produitsList.addAll(produits);
//            tableViewProduits.setItems(produitsList);
//        } catch (SQLException e) {
//            afficherAlerte(AlertType.ERROR, "Erreur",
//                    "Erreur lors du chargement des produits : " + e.getMessage());
//        }
//    }
//
//    private void afficherAlerte(AlertType type, String titre, String message) {
//        Alert alert = new Alert(type);
//        alert.setTitle(titre);
//        alert.setHeaderText(null);
//        alert.setContentText(message);
//        alert.showAndWait();
//    }
//}