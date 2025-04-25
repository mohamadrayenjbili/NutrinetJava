// 5. Contrôleur pour finaliser la commande (AjouterCommandeController)
package controllers.Commande;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.Commande;
import models.LignePanier;
import models.Panier;
import models.Produit;
import services.Commande.CommandeService;
import services.PanierService;
import services.Produit.ProduitService;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class AjouterCommandeController implements Initializable {

    @FXML
    private TextField txtNom;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtAdresse;

    @FXML
    private ComboBox<String> cbxPaiement;

    @FXML
    private Label lblTotalCommande;

    @FXML
    private Button btnConfirmer;

    @FXML
    private Button btnAnnuler;

    private PanierService panierService;
    private CommandeService commandeService;
    private ProduitService produitService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        commandeService = new CommandeService();
        produitService = new ProduitService();

        // Initialiser la combobox des méthodes de paiement
        cbxPaiement.getItems().addAll("Carte bancaire", "PayPal", "Espèces à la livraison");
        cbxPaiement.getSelectionModel().selectFirst();
    }

    public void setPanierService(PanierService panierService) {
        this.panierService = panierService;
        // Afficher le total de la commande
        lblTotalCommande.setText(String.format("Total de la commande: %.2f €", panierService.getPanier().getTotal()));
    }

    @FXML
    private void confirmerCommande(ActionEvent event) {
        if (!validerFormulaire()) {
            return;
        }

        try {
            // Créer la commande
            Commande commande = new Commande();
            commande.setNomC(txtNom.getText());
            commande.setMail(txtEmail.getText());
            commande.setAdress(txtAdresse.getText());
            commande.setDateC(Date.valueOf(LocalDate.now()));
            commande.setStatus("En attente");
            commande.setMethodeDePaiement(cbxPaiement.getValue());

            // Enregistrer la commande dans la base de données
            commandeService.ajouterCommande(commande);

            // Ajouter les produits à la commande
            Panier panier = panierService.getPanier();
            for (LignePanier ligne : panier.getItemsList()) {
                Produit produit = ligne.getProduit();

                // Ajouter le produit à la commande dans la table de liaison
                for (int i = 0; i < ligne.getQuantite(); i++) {
                    commandeService.ajouterProduitACommande(commande.getId(), produit.getId());
                }

                // Mettre à jour le stock du produit
                produit.setStock(produit.getStock() - ligne.getQuantite());
                produitService.updateProduit(produit);
            }

            // Vider le panier
            panierService.viderPanier();

            // Afficher message de confirmation
            afficherMessage("Commande confirmée", "Votre commande a été enregistrée avec succès!", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre
            Stage stage = (Stage) btnConfirmer.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Erreur lors de l'enregistrement de la commande: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void annulerCommande(ActionEvent event) {
        Stage stage = (Stage) btnAnnuler.getScene().getWindow();
        stage.close();
    }

    private boolean validerFormulaire() {
        StringBuilder erreurs = new StringBuilder();

        if (txtNom.getText().trim().isEmpty()) {
            erreurs.append("- Le nom est obligatoire.\n");
        }

        if (txtEmail.getText().trim().isEmpty()) {
            erreurs.append("- L'email est obligatoire.\n");
        } else if (!txtEmail.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            erreurs.append("- Format d'email invalide.\n");
        }

        if (txtAdresse.getText().trim().isEmpty()) {
            erreurs.append("- L'adresse est obligatoire.\n");
        }

        if (erreurs.length() > 0) {
            afficherMessage("Erreur de validation", "Veuillez corriger les erreurs suivantes:\n" + erreurs.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void afficherMessage(String titre, String message, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(titre);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}