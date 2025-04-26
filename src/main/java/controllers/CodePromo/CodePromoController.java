package controllers.CodePromo;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import models.CodePromo;
import services.CodePromoService;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class CodePromoController implements Initializable {

    @FXML private TextField txtCode;
    @FXML private TextField txtPourcentage;
    @FXML private CheckBox chkActif;
    @FXML private Label lblMessage;
    @FXML private Button btnAjouter;
    @FXML private Button btnSupprimer;
    @FXML private Button btnModifier;
    @FXML private Button btnAfficherTous;  // Nouveau bouton pour afficher tous les codes promo
    @FXML private TableView<CodePromo> tblCodePromo;
    @FXML private TableColumn<CodePromo, String> colCode;
    @FXML private TableColumn<CodePromo, Double> colPourcentage;
    @FXML private TableColumn<CodePromo, Boolean> colActif;

    private CodePromoService codePromoService;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        codePromoService = new CodePromoService();
        chkActif.setSelected(true);

        // Initialisation de la table pour afficher les données
        colCode.setCellValueFactory(cellData -> cellData.getValue().codeProperty());
        colPourcentage.setCellValueFactory(cellData -> cellData.getValue().pourcentageProperty().asObject());
        colActif.setCellValueFactory(cellData -> cellData.getValue().actifProperty());
    }

    @FXML
    private void ajouterCodePromo(ActionEvent event) {
        lblMessage.setText("");
        if (!validerFormulaire(true)) return;

        String code = txtCode.getText().trim();
        double pourcentage = Double.parseDouble(txtPourcentage.getText().trim());
        boolean actif = chkActif.isSelected();

        CodePromo cp = new CodePromo(0, code, pourcentage, actif);
        try {
            codePromoService.ajouterCodePromo(cp);
            afficherMessage("✅ Code promo ajouté !");
            viderChamps();
            afficherTousLesCodePromo();  // Actualiser la table après ajout
        } catch (SQLException e) {
            afficherMessage("❌ Erreur SQL : " + e.getMessage());
        }
    }

    @FXML
    private void supprimerCodePromo(ActionEvent event) {
        lblMessage.setText("");
        String code = txtCode.getText().trim();

        if (code.isEmpty()) {
            afficherMessage("⚠️ Veuillez entrer un code promo.");
            return;
        }

        try {
            CodePromo promo = codePromoService.getCodePromoByCode(code);
            if (promo != null) {
                codePromoService.deleteCodePromo(promo.getId());
                afficherMessage("🗑️ Code promo supprimé.");
                viderChamps();
                afficherTousLesCodePromo();  // Actualiser la table après suppression
            } else {
                afficherMessage("⚠️ Code promo introuvable.");
            }
        } catch (SQLException e) {
            afficherMessage("❌ Erreur SQL : " + e.getMessage());
        }
    }

    @FXML
    private void modifierCodePromo(ActionEvent event) {
        lblMessage.setText("");
        if (!validerFormulaire(false)) return;

        String code = txtCode.getText().trim();
        double pourcentage = Double.parseDouble(txtPourcentage.getText().trim());
        boolean actif = chkActif.isSelected();

        try {
            CodePromo existing = codePromoService.getCodePromoByCode(code);
            if (existing != null) {
                existing.setPourcentage(pourcentage);
                existing.setActif(actif);
                codePromoService.updateCodePromo(existing);
                afficherMessage("✏️ Code promo modifié avec succès !");
                viderChamps();
                afficherTousLesCodePromo();  // Actualiser la table après modification
            } else {
                afficherMessage("⚠️ Code promo introuvable.");
            }
        } catch (SQLException e) {
            afficherMessage("❌ Erreur SQL : " + e.getMessage());
        }
    }

    @FXML
    private void afficherTousLesCodePromo() {
        try {
            List<CodePromo> allPromoCodes = codePromoService.getAllCodesPromo();
            tblCodePromo.getItems().clear();  // Clear the current data
            tblCodePromo.getItems().addAll(allPromoCodes);  // Add all fetched data to the table
        } catch (SQLException e) {
            afficherMessage("❌ Erreur lors de la récupération des codes promo.");
        }
    }

    private boolean validerFormulaire(boolean pourAjout) {
        StringBuilder erreurs = new StringBuilder();
        String code = txtCode.getText().trim();
        String pourcentageStr = txtPourcentage.getText().trim();

        if (code.isEmpty()) {
            erreurs.append("- Le code promo est obligatoire.\n");
        }

        try {
            double val = Double.parseDouble(pourcentageStr);
            if (val <= 0 || val > 100) {
                erreurs.append("- Le pourcentage doit être entre 1 et 100.\n");
            }
        } catch (NumberFormatException e) {
            erreurs.append("- Pourcentage invalide (nombre attendu).\n");
        }

        if (pourAjout) {
            try {
                CodePromo existing = codePromoService.getCodePromoByCode(code);
                if (existing != null) {
                    erreurs.append("- Ce code promo existe déjà. Utilisez un autre code.\n");
                }
            } catch (SQLException e) {
                erreurs.append("- Erreur lors de la vérification du code promo.\n");
            }
        }

        if (erreurs.length() > 0) {
            afficherMessage("❌ Erreurs :\n" + erreurs);
            return false;
        }

        return true;
    }

    private void afficherMessage(String msg) {
        lblMessage.setText(msg);
    }

    private void viderChamps() {
        txtCode.clear();
        txtPourcentage.clear();
        chkActif.setSelected(true);
    }

    @FXML
    private void fermerFenetre(ActionEvent event) {
        Stage stage = (Stage) btnAjouter.getScene().getWindow();
        stage.close();
    }
}
