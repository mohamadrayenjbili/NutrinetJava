package controllers.Commande;

import java.io.IOException;
import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Commande;
import models.Produit;
import services.Commande.CommandeService;
import services.Commande.ICommandeService;

public class ModifierCommandeController implements Initializable {

    @FXML private ListView<Commande> listViewCommandes;
    @FXML private Label lblId;
    @FXML private Label lblNomError;
    @FXML private Label lblMailError;
    @FXML private Label lblAdresseError;
    @FXML private Label lblDateError;
    @FXML private Label lblStatusError;
    @FXML private Label lblPaiementError;

    @FXML private TextField txtNom;
    @FXML private TextField txtMail;
    @FXML private TextArea txtAdresse;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> comboStatus;
    @FXML private ComboBox<String> comboPaiement;
    @FXML private ListView<Produit> listViewProduits;

    @FXML private Button btnModifier;
    @FXML private Button btnAnnuler;
    @FXML private Button btnSupprimer;
    @FXML private Button btnVoirDetails;

    private ICommandeService commandeService;
    private ObservableList<Commande> commandesList;
    private ObservableList<Produit> produitsList;
    private Commande commandeSelectionnee;


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        commandeService = new CommandeService();
        commandesList = FXCollections.observableArrayList();
        produitsList = FXCollections.observableArrayList();

        configurerInterface();
        chargerStatus();
        chargerMethodesPaiement();
        chargerCommandes();
        setupInputValidation();

        listViewCommandes.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectionnerCommande(newSelection);
            }
        });
    }

    private void configurerInterface() {
        btnModifier.setStyle("-fx-background-color: #5cb85c; -fx-text-fill: white; -fx-font-weight: bold;");
        btnAnnuler.setStyle("-fx-background-color: #f0ad4e; -fx-text-fill: white; -fx-font-weight: bold;");
        btnSupprimer.setStyle("-fx-background-color: #d9534f; -fx-text-fill: white; -fx-font-weight: bold;");
        btnVoirDetails.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold;");

        listViewCommandes.setCellFactory(lv -> new ListCell<Commande>() {
            @Override
            protected void updateItem(Commande commande, boolean empty) {
                super.updateItem(commande, empty);
                if (empty || commande == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    HBox hbox = new HBox(10);
                    Label idLabel = new Label("#" + commande.getId());
                    Label nomLabel = new Label(commande.getNomC());
                    Label dateLabel = new Label(commande.getDateC().toString());
                    Label statusLabel = new Label(commande.getStatus());

                    idLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #3498db;");
                    nomLabel.setStyle("-fx-font-weight: bold;");
                    dateLabel.setStyle("-fx-text-fill: #7f8c8d;");

                    switch(commande.getStatus()) {
                        case "Payée": statusLabel.setStyle("-fx-text-fill: #2ecc71;"); break;
                        case "En attente": statusLabel.setStyle("-fx-text-fill: #f39c12;"); break;
                        case "Annulée": statusLabel.setStyle("-fx-text-fill: #e74c3c;"); break;
                        default: statusLabel.setStyle("-fx-text-fill: #34495e;");
                    }

                    hbox.getChildren().addAll(idLabel, nomLabel, dateLabel, statusLabel);
                    setGraphic(hbox);
                }
            }
        });

        listViewProduits.setCellFactory(lv -> new ListCell<Produit>() {
            @Override
            protected void updateItem(Produit produit, boolean empty) {
                super.updateItem(produit, empty);
                if (empty || produit == null) {
                    setText(null);
                } else {
                    setText(String.format("%s - %.2f€", produit.getNomProduit(), produit.getPrix()));
                }
            }
        });
    }

    private void setupInputValidation() {
        txtNom.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.length() > 50) txtNom.setText(oldVal);
            validateNom();
        });

        txtMail.textProperty().addListener((obs, oldVal, newVal) -> {
            validateMail();
        });

        txtAdresse.textProperty().addListener((obs, oldVal, newVal) -> {
            validateAdresse();
        });

        datePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateDate();
        });

        comboStatus.valueProperty().addListener((obs, oldVal, newVal) -> {
            validateStatus();
        });

        comboPaiement.valueProperty().addListener((obs, oldVal, newVal) -> {
            validatePaiement();
        });
    }

    private void chargerStatus() {
        comboStatus.getItems().addAll(
                "En attente", "Payée", "Expédiée", "Annulée"
        );
    }

    private void chargerMethodesPaiement() {
        comboPaiement.getItems().addAll(
                "Carte bancaire", "PayPal", "Virement", "Livraison"
        );
    }

    private void chargerCommandes() {
        try {
            List<Commande> commandes = commandeService.getAllCommandes();
            commandesList.setAll(commandes);
            listViewCommandes.setItems(commandesList);
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors du chargement des commandes: " + e.getMessage());
        }
    }

    private void chargerProduitsPourCommande(int commandeId) {
        try {
            produitsList.clear();
            List<Produit> produits = commandeService.getProduitsByCommandeId(commandeId);
            produitsList.setAll(produits);
            listViewProduits.setItems(produitsList);
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors du chargement des produits: " + e.getMessage());
        }
    }

    @FXML
    private void handleModifier(ActionEvent event) {
        if (commandeSelectionnee == null || !validateAllFields()) return;

        try {
            updateCommandeFromFields();
            commandeService.updateCommande(commandeSelectionnee);
            chargerCommandes();
            showAlert(AlertType.INFORMATION, "Succès", "Commande modifiée avec succès!");
        } catch (SQLException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la modification: " + e.getMessage());
        }
    }

    private void updateCommandeFromFields() {
        commandeSelectionnee.setNomC(txtNom.getText());
        commandeSelectionnee.setMail(txtMail.getText());
        commandeSelectionnee.setAdress(txtAdresse.getText());
        commandeSelectionnee.setDateC(Date.valueOf(datePicker.getValue()));
        commandeSelectionnee.setStatus(comboStatus.getValue());
        commandeSelectionnee.setMethodeDePaiement(comboPaiement.getValue());
    }

    @FXML
    private void handleSupprimer(ActionEvent event) {
        if (commandeSelectionnee == null) return;

        Alert confirmation = new Alert(AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText("Supprimer la commande #" + commandeSelectionnee.getId());
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cette commande ?");

        confirmation.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    commandeService.deleteCommande(commandeSelectionnee.getId());
                    chargerCommandes();
                    deselectionnerCommande();
                    showAlert(AlertType.INFORMATION, "Succès", "Commande supprimée avec succès!");
                } catch (SQLException e) {
                    showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
                }
            }
        });
    }

    @FXML
    private void handleVoirDetails(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/Commande/DetailCommande.fxml"));
            Stage stage = (Stage) btnVoirDetails.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Liste des Commandes");
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", e.getMessage());
        }
    }

    @FXML
    private void handleAnnuler(ActionEvent event) {
        deselectionnerCommande();
    }

    private void selectionnerCommande(Commande commande) {
        this.commandeSelectionnee = commande;

        lblId.setText(String.valueOf(commande.getId()));
        txtNom.setText(commande.getNomC());
        txtMail.setText(commande.getMail());
        txtAdresse.setText(commande.getAdress());
        datePicker.setValue(commande.getDateC().toLocalDate());
        comboStatus.setValue(commande.getStatus());
        comboPaiement.setValue(commande.getMethodeDePaiement());

        chargerProduitsPourCommande(commande.getId());

        btnModifier.setDisable(false);
        btnSupprimer.setDisable(false);
    }

    private void deselectionnerCommande() {
        this.commandeSelectionnee = null;
        listViewCommandes.getSelectionModel().clearSelection();

        lblId.setText("");
        txtNom.clear();
        txtMail.clear();
        txtAdresse.clear();
        datePicker.setValue(null);
        comboStatus.getSelectionModel().clearSelection();
        comboPaiement.getSelectionModel().clearSelection();
        produitsList.clear();

        hideAllErrors();
        desactiverBoutons();
    }

    private void desactiverBoutons() {
        btnModifier.setDisable(true);
        btnSupprimer.setDisable(true);
    }

    private boolean validateAllFields() {
        boolean valid = true;
        if (!validateNom()) valid = false;
        if (!validateMail()) valid = false;
        if (!validateAdresse()) valid = false;
        if (!validateDate()) valid = false;
        if (!validateStatus()) valid = false;
        if (!validatePaiement()) valid = false;
        return valid;
    }

    private boolean validateNom() {
        if (txtNom.getText().isEmpty()) {
            showError(lblNomError, "Le nom est requis");
            return false;
        } else if (txtNom.getText().length() < 3) {
            showError(lblNomError, "Minimum 3 caractères");
            return false;
        }
        hideError(lblNomError);
        return true;
    }

    private boolean validateMail() {
        if (txtMail.getText().isEmpty()) {
            showError(lblMailError, "L'email est requis");
            return false;
        } else if (!txtMail.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$")) {
            showError(lblMailError, "Format email invalide");
            return false;
        }
        hideError(lblMailError);
        return true;
    }

    private boolean validateAdresse() {
        if (txtAdresse.getText().isEmpty()) {
            showError(lblAdresseError, "L'adresse est requise");
            return false;
        }
        hideError(lblAdresseError);
        return true;
    }

    private boolean validateDate() {
        if (datePicker.getValue() == null) {
            showError(lblDateError, "La date est requise");
            return false;
        }
        hideError(lblDateError);
        return true;
    }

    private boolean validateStatus() {
        if (comboStatus.getValue() == null) {
            showError(lblStatusError, "Le statut est requis");
            return false;
        }
        hideError(lblStatusError);
        return true;
    }

    private boolean validatePaiement() {
        if (comboPaiement.getValue() == null) {
            showError(lblPaiementError, "La méthode est requise");
            return false;
        }
        hideError(lblPaiementError);
        return true;
    }

    private void showError(Label label, String message) {
        label.setText(message);
        label.setVisible(true);
    }

    private void hideError(Label label) {
        label.setVisible(false);
    }

    private void hideAllErrors() {
        hideError(lblNomError);
        hideError(lblMailError);
        hideError(lblAdresseError);
        hideError(lblDateError);
        hideError(lblStatusError);
        hideError(lblPaiementError);
    }

    private void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void loadCommandeDirectly(Commande commande) {
        selectionnerCommande(commande);
        listViewCommandes.getSelectionModel().select(commande);
    }
}
