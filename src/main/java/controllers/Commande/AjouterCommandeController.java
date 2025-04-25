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
import services.paiement.StripeService;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ResourceBundle;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import com.stripe.exception.StripeException;

import javafx.scene.web.WebView;
import javafx.scene.web.WebEngine;
import netscape.javascript.JSObject;
import javafx.application.Platform;

public class AjouterCommandeController implements Initializable {

    @FXML private TextField txtNom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAdresse;
    @FXML private ComboBox<String> cbxPaiement;
    @FXML private Label lblTotalCommande;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;

    private PanierService panierService;
    private CommandeService commandeService;
    private ProduitService produitService;
    private StripeService stripeService;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        commandeService = new CommandeService();
        produitService = new ProduitService();
        stripeService = new StripeService();

        cbxPaiement.getItems().addAll("Carte bancaire", "Espèces à la livraison");
        cbxPaiement.getSelectionModel().selectFirst();
    }

    public void setPanierService(PanierService panierService) {
        this.panierService = panierService;
        lblTotalCommande.setText(String.format("Total de la commande: %.2f €", panierService.getPanier().getTotal()));
    }

    @FXML
    private void confirmerCommande(ActionEvent event) {
        if (!validerFormulaire()) {
            return;
        }

        if ("Carte bancaire".equals(cbxPaiement.getValue())) {
            try {
                double total = panierService.getPanier().getTotal();
                String clientSecret = stripeService.createPaymentIntent(total, "EUR", "Paiement pour la commande");
                afficherInterfacePaiement(clientSecret);
            } catch (StripeException e) {
                e.printStackTrace();
                afficherMessage("Erreur de paiement", "Erreur lors de la création du paiement: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        } else {
            traiterCommandeSansPaiementEnLigne();
        }
    }

    private void afficherInterfacePaiement(String clientSecret) {
        Stage paymentStage = new Stage();
        paymentStage.initModality(Modality.APPLICATION_MODAL);
        paymentStage.setTitle("Paiement par carte");

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        Label instructionLabel = new Label("Veuillez entrer vos informations de paiement:");

        TextField cardNumberField = new TextField();
        cardNumberField.setPromptText("Numéro de carte (4242 4242 4242 4242 pour test)");

        TextField expDateField = new TextField();
        expDateField.setPromptText("MM/AA (12/34 pour test)");

        TextField cvcField = new TextField();
        cvcField.setPromptText("CVC (123 pour test)");

        Label testCardLabel = new Label("Mode test activé - Utilisez les valeurs de test");
        testCardLabel.setStyle("-fx-text-fill: #2ecc71; -fx-font-style: italic;");

        Button payerButton = new Button("Payer");
        payerButton.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
        payerButton.setOnAction(e -> {
            if (validerFormulairePaiement(cardNumberField.getText(), expDateField.getText(), cvcField.getText())) {
                paymentStage.close();
                traiterCommandeAvecPaiementReussi();
            }

        });

        root.getChildren().addAll(instructionLabel, testCardLabel, cardNumberField, expDateField, cvcField, payerButton);

        Scene scene = new Scene(root, 400, 300);
        paymentStage.setScene(scene);
        paymentStage.showAndWait();
    }

    private boolean validerFormulairePaiement(String cardNumber, String expDate, String cvc) {
        StringBuilder erreurs = new StringBuilder();

        if (cardNumber == null || cardNumber.replaceAll("\\s+", "").length() != 16) {
            erreurs.append("- Numéro de carte invalide (16 chiffres requis)\n");
        }

        if (expDate == null || !expDate.matches("(0[1-9]|1[0-2])/[0-9]{2}")) {
            erreurs.append("- Date d'expiration invalide (format MM/AA)\n");
        }

        if (cvc == null || cvc.length() != 3) {
            erreurs.append("- CVC invalide (3 chiffres requis)\n");
        }

        if (erreurs.length() > 0) {
            afficherMessage("Erreur de paiement", "Veuillez corriger les erreurs suivantes:\n" + erreurs.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void traiterCommandeAvecPaiementReussi() {
        try {
            Commande commande = creerCommande();
            commande.setStatus("Payée"); // Statut différent pour les commandes payées
            commandeService.ajouterCommande(commande);
            ajouterProduitsACommande(commande);
            panierService.viderPanier();

            afficherMessage("Paiement réussi",
                    "Votre paiement a été accepté et votre commande est confirmée!\n" +
                            "Un email de confirmation vous a été envoyé.",
                    Alert.AlertType.INFORMATION);

            fermerFenetre();
        } catch (SQLException e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Erreur lors de l'enregistrement de la commande: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void traiterCommandeSansPaiementEnLigne() {
        try {
            Commande commande = creerCommande();
            commandeService.ajouterCommande(commande);
            ajouterProduitsACommande(commande);
            panierService.viderPanier();

            afficherMessage("Commande confirmée",
                    "Votre commande a été enregistrée avec succès!\n" +
                            "Vous paierez en espèces à la livraison.",
                    Alert.AlertType.INFORMATION);

            fermerFenetre();
        } catch (SQLException e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Erreur lors de l'enregistrement de la commande: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private Commande creerCommande() {
        Commande commande = new Commande();
        commande.setNomC(txtNom.getText());
        commande.setMail(txtEmail.getText());
        commande.setAdress(txtAdresse.getText());
        commande.setDateC(Date.valueOf(LocalDate.now()));
        commande.setMethodeDePaiement(cbxPaiement.getValue());
        return commande;
    }

    private void ajouterProduitsACommande(Commande commande) throws SQLException {
        Panier panier = panierService.getPanier();
        for (LignePanier ligne : panier.getItemsList()) {
            Produit produit = ligne.getProduit();

            for (int i = 0; i < ligne.getQuantite(); i++) {
                commandeService.ajouterProduitACommande(commande.getId(), produit.getId());
            }

            produit.setStock(produit.getStock() - ligne.getQuantite());
            produitService.updateProduit(produit);
        }
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

    @FXML
    private void annulerCommande(ActionEvent event) {
        fermerFenetre();
    }

    private void fermerFenetre() {
        Stage stage = (Stage) btnConfirmer.getScene().getWindow();
        stage.close();
    }
}