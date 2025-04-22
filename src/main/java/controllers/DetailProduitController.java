package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import models.Produit;

import java.net.URL;
import java.util.ResourceBundle;

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

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Charger le CSS si nécessaire
        // rootPane.getStylesheets().add(getClass().getResource("/details_style.css").toExternalForm());
    }

    public void setProduit(Produit produit) {
        if (produit != null) {
            lblNom.setText(produit.getNomProduit());
            lblPrix.setText(String.format("%.2f €", produit.getPrix()));
            lblCategorie.setText(produit.getCategorie());
            lblStock.setText("Stock: " + produit.getStock());
            lblDescription.setText(produit.getDescription());

            if (produit.getImage() != null && !produit.getImage().isEmpty()) {
                try {
                    Image image = new Image("file:src/main/resources/images/" + produit.getImage());
                    imgProduit.setImage(image);
                } catch (Exception e) {
                    System.err.println("Erreur de chargement de l'image: " + e.getMessage());
                    // Charger une image par défaut si nécessaire
                }
            }
        }
    }
}