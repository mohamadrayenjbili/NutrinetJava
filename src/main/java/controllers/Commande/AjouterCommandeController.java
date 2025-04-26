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
import models.User;
import models.CodePromo;
import services.Commande.CommandeService;
import services.PanierService;
import services.Produit.ProduitService;
import services.CodePromoService;
import services.paiement.StripeService;
import utils.session;

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
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;

public class AjouterCommandeController implements Initializable {

    @FXML private TextField txtNom;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAdresse;
    @FXML private ComboBox<String> cbxPaiement;
    @FXML private Label lblTotalCommande;
    @FXML private Button btnConfirmer;
    @FXML private Button btnAnnuler;
    @FXML private TextField txtCodePromo;
    @FXML private Button btnAppliquerCode;
    @FXML private Label lblRemise;
    @FXML private Label lblTotalApresRemise;

    private PanierService panierService;
    private CommandeService commandeService;
    private ProduitService produitService;
    private StripeService stripeService;
    private CodePromoService codePromoService;

    private double remise = 0.0;
    private CodePromo codePromoApplique = null;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        commandeService = new CommandeService();
        produitService = new ProduitService();
        stripeService = new StripeService();
        codePromoService = new CodePromoService();

        cbxPaiement.getItems().addAll("Carte bancaire", "Espèces à la livraison");
        cbxPaiement.getSelectionModel().selectFirst();

        User currentUser = session.getCurrentUser();
        if (currentUser != null) {
            txtNom.setText(currentUser.getName() + " " + currentUser.getPrename());
            txtEmail.setText(currentUser.getEmail());
            txtAdresse.setText(currentUser.getAddress());
        }

        lblRemise.setVisible(false);
        lblTotalApresRemise.setVisible(false);
    }

    public void setPanierService(PanierService panierService) {
        this.panierService = panierService;
        double total = panierService.getPanier().getTotal();
        lblTotalCommande.setText(String.format("Total de la commande: %.2f €", total));
    }

    @FXML
    private void appliquerCodePromo(ActionEvent event) {
        String code = txtCodePromo.getText().trim();
        if (code.isEmpty()) {
            afficherMessage("Information", "Veuillez entrer un code promo", Alert.AlertType.INFORMATION);
            return;
        }

        try {
            codePromoApplique = codePromoService.getCodePromoByCode(code);
            if (codePromoApplique == null || !codePromoApplique.isActif()) {
                afficherMessage("Code promo invalide", "Le code promo n'existe pas ou n'est plus actif", Alert.AlertType.WARNING);
                reinitialiserRemise();
                return;
            }

            double totalAvantRemise = panierService.getPanier().getTotal();
            remise = totalAvantRemise * (codePromoApplique.getPourcentage() / 100);

            lblRemise.setText(String.format("Remise: %.2f € (-%.0f%%)",
                    remise, codePromoApplique.getPourcentage()));
            lblRemise.setVisible(true);

            lblTotalApresRemise.setText(String.format("Total après remise: %.2f €",
                    (totalAvantRemise - remise)));
            lblTotalApresRemise.setVisible(true);

            afficherMessage("Code promo appliqué",
                    String.format("Une remise de %.0f%% a été appliquée", codePromoApplique.getPourcentage()),
                    Alert.AlertType.INFORMATION);

        } catch (SQLException e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Erreur lors de la vérification du code promo", Alert.AlertType.ERROR);
            reinitialiserRemise();
        }
    }

    private void reinitialiserRemise() {
        remise = 0.0;
        codePromoApplique = null;
        lblRemise.setVisible(false);
        lblTotalApresRemise.setVisible(false);
    }

    @FXML
    private void confirmerCommande(ActionEvent event) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            afficherMessage("Connexion requise", "Veuillez vous connecter pour passer une commande", Alert.AlertType.WARNING);
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) btnConfirmer.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.setTitle("Connexion");
            } catch (IOException e) {
                e.printStackTrace();
                afficherMessage("Erreur", "Impossible d'accéder à la page de connexion", Alert.AlertType.ERROR);
            }
            return;
        }

        if (!validerFormulaire()) {
            return;
        }

        if ("Carte bancaire".equals(cbxPaiement.getValue())) {
            try {
                double total = panierService.getPanier().getTotal() - remise;
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
        instructionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");

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
            erreurs.append("- Numéro de carte invalide\n");
        }
        if (expDate == null || !expDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            erreurs.append("- Date d'expiration invalide\n");
        }
        if (cvc == null || cvc.length() != 3) {
            erreurs.append("- CVC invalide\n");
        }

        if (erreurs.length() > 0) {
            afficherMessage("Erreur de paiement", erreurs.toString(), Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void traiterCommandeAvecPaiementReussi() {
        try {
            Commande commande = creerCommande();
            commande.setStatus("Payée");
            commandeService.ajouterCommande(commande);
            ajouterProduitsACommande(commande);
            panierService.viderPanier();

            afficherMessage("Paiement réussi", "Votre commande a été confirmée.", Alert.AlertType.INFORMATION);
            fermerFenetre();
        } catch (SQLException e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Erreur lors de l'enregistrement de la commande", Alert.AlertType.ERROR);
        }
    }

    private void traiterCommandeSansPaiementEnLigne() {
        try {
            Commande commande = creerCommande();
            commande.setStatus("En attente de paiement");
            commandeService.ajouterCommande(commande);
            ajouterProduitsACommande(commande);
            panierService.viderPanier();

            afficherMessage("Commande enregistrée", "Votre commande est confirmée.", Alert.AlertType.INFORMATION);
            fermerFenetre();
        } catch (SQLException e) {
            e.printStackTrace();
            afficherMessage("Erreur", "Erreur lors de l'enregistrement de la commande", Alert.AlertType.ERROR);
        }
    }

    private Commande creerCommande() {
        Commande commande = new Commande();
        commande.setNomC(txtNom.getText());
        commande.setMail(txtEmail.getText());
        commande.setAdress(txtAdresse.getText());
        commande.setDateC(Date.valueOf(LocalDate.now()));
        commande.setMethodeDePaiement(cbxPaiement.getValue());
        commande.setTotalAvantRemise(panierService.getPanier().getTotal());

        if (codePromoApplique != null) {
            commande.setCodePromo(codePromoApplique.getCode());
            commande.setRemise(remise);
        }

        return commande;
    }

    private void ajouterProduitsACommande(Commande commande) throws SQLException {
        Panier panier = panierService.getPanier();

        for (LignePanier ligne : panier.getItemsList()) {
            Produit produit = ligne.getProduit();

            if (produit.getStock() < ligne.getQuantite()) {
                throw new SQLException("Stock insuffisant pour le produit: " + produit.getNomProduit());
            }

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
            erreurs.append("- Nom requis.\n");
        }
        if (txtEmail.getText().trim().isEmpty() || !txtEmail.getText().matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            erreurs.append("- Email invalide.\n");
        }
        if (txtAdresse.getText().trim().isEmpty()) {
            erreurs.append("- Adresse requise.\n");
        }

        if (erreurs.length() > 0) {
            afficherMessage("Erreur de validation", erreurs.toString(), Alert.AlertType.ERROR);
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