package controllers.Produit;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import models.Produit;
import services.ListeSouhaitsService;
import services.PanierService;
import utils.session;

public class DetailProduitController implements Initializable {

    @FXML
    private VBox rootPane;

    @FXML
    private Label lblNom;

    @FXML
    private Label lblPrix;

    @FXML
    private Label lblCategorie;

    @FXML
    private Label lblStock;

    @FXML
    private TextArea lblDescription;

    @FXML
    private ImageView imgProduit;

    @FXML
    private Button btnRetour;

    @FXML
    private Button btnAjouterPanier;

    @FXML
    private Button btnAjouterSouhaits;

    private Produit produit;
    private PanierService panierService = PanierService.getInstance();
    private ListeSouhaitsService listeSouhaitsService = new ListeSouhaitsService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Charger le CSS
        rootPane.getStylesheets().add(getClass().getResource("/Produit/detaildidou.css").toExternalForm());

        // Configurer les actions des boutons
        btnRetour.setOnAction(e -> handleRetour());
        btnAjouterPanier.setOnAction(e -> handleAjouterPanier());
        btnAjouterSouhaits.setOnAction(e -> handleAjouterSouhaits());
    }

    public void setProduit(Produit produit) {
        this.produit = produit;
        if (produit != null) {
            lblNom.setText(produit.getNomProduit());
            lblPrix.setText(String.format("%.2f €", produit.getPrix()));
            lblCategorie.setText(produit.getCategorie());
            lblStock.setText("Stock: " + produit.getStock());
            lblDescription.setText(produit.getDescription());

            // Mettre à jour le style du stock en fonction de sa disponibilité
            if (produit.getStock() <= 0) {
                lblStock.getStyleClass().add("stock-warning");
                btnAjouterPanier.setDisable(true);
            }

            if (produit.getImage() != null && !produit.getImage().isEmpty()) {
                try {
                    Image image = new Image("file:src/main/resources/images/" + produit.getImage());
                    imgProduit.setImage(image);
                } catch (Exception e) {
                    System.err.println("Erreur de chargement de l'image: " + e.getMessage());
                }
            }
        }
    }

    @FXML
    private void handleRetour() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/AfficherProduitFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Nos Produits");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAjouterPanier() {
        if (produit != null) {
            if (session.getCurrentUser() == null) {
                // Rediriger vers la page de connexion
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) btnAjouterPanier.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Connexion");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            panierService.ajouterAuPanier(produit, 1); // Ajouter une quantité de 1 par défaut
        }
    }

    @FXML
    private void handleAjouterSouhaits() {
        if (produit != null) {
            if (session.getCurrentUser() == null) {
                // Rediriger vers la page de connexion
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
                    Parent root = loader.load();
                    Stage stage = (Stage) btnAjouterSouhaits.getScene().getWindow();
                    stage.setScene(new Scene(root));
                    stage.setTitle("Connexion");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }

            try {
                listeSouhaitsService.ajouterAListeSouhaits(session.getCurrentUser().getId(), produit.getId());
                showAlert("Succès", "Produit ajouté à la liste de souhaits", javafx.scene.control.Alert.AlertType.INFORMATION);
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de l'ajout à la liste de souhaits", javafx.scene.control.Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String content, javafx.scene.control.Alert.AlertType type) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}