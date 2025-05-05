package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Consultation;
import models.Prescription;
import services.ConsultationService;
import services.PrescriptionService;
import utils.SmsService;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CalendrierController implements Initializable {

    @FXML
    private Label monthYearLabel;

    @FXML
    private GridPane calendarGrid;

    @FXML
    private Button prevMonthButton;

    @FXML
    private Button nextMonthButton;

    @FXML
    private Button todayButton;

    private YearMonth currentYearMonth;
    private ConsultationService consultationService;
    private PrescriptionService prescriptionService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Connexion à la base de données
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/didou", "root", "");
            this.consultationService = new ConsultationService(connection);
            this.prescriptionService = new PrescriptionService(connection);

            // Initialiser avec la date actuelle
            currentYearMonth = YearMonth.now();

            // Configuration des boutons de navigation
            setupNavigationButtons();

            // Chargement initial du calendrier
            updateCalendarView();

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur de connexion", "Impossible de se connecter à la base de données");
        }
    }

    private void setupNavigationButtons() {
        prevMonthButton.setOnAction(event -> {
            currentYearMonth = currentYearMonth.minusMonths(1);
            updateCalendarView();
        });

        nextMonthButton.setOnAction(event -> {
            currentYearMonth = currentYearMonth.plusMonths(1);
            updateCalendarView();
        });

        todayButton.setOnAction(event -> {
            currentYearMonth = YearMonth.now();
            updateCalendarView();
        });
    }

    private void updateCalendarView() {
        // Mise à jour du titre
        monthYearLabel.setText(currentYearMonth.getMonth().toString() + " " + currentYearMonth.getYear());

        // Effacer le calendrier existant
        calendarGrid.getChildren().clear();

        // Ajouter les entêtes des jours
        String[] daysOfWeek = {"Lun", "Mar", "Mer", "Jeu", "Ven", "Sam", "Dim"};
        for (int i = 0; i < 7; i++) {
            Label dayLabel = new Label(daysOfWeek[i]);
            dayLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
            dayLabel.setAlignment(Pos.CENTER);
            dayLabel.setPrefWidth(Double.MAX_VALUE);
            dayLabel.setStyle("-fx-background-color: #f0f0f0; -fx-padding: 5;");
            calendarGrid.add(dayLabel, i, 0);
        }

        // Calculer le premier jour du mois
        LocalDate firstOfMonth = currentYearMonth.atDay(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue(); // 1 = Lundi, 7 = Dimanche

        // Charger les consultations pour ce mois
        Map<LocalDate, List<Consultation>> consultationsByDate = loadConsultationsForMonth();

        // Remplir le calendrier
        int daysInMonth = currentYearMonth.lengthOfMonth();
        int day = 1;
        int row = 1;

        LocalDate today = LocalDate.now();

        // Déterminer le nombre de semaines à afficher
        int rowCount = (daysInMonth + dayOfWeek - 1) / 7 + 1;

        for (int i = 1; i < rowCount * 7 + 1; i++) {
            int column = (i - 1) % 7;

            if (i < dayOfWeek || day > daysInMonth) {
                // Cellules vides avant le premier jour et après le dernier jour
                calendarGrid.add(createEmptyCell(), column, row);
            } else {
                // Cellules avec date et consultations
                LocalDate date = LocalDate.of(currentYearMonth.getYear(), currentYearMonth.getMonthValue(), day);
                boolean isToday = date.equals(today);

                List<Consultation> dayConsultations = consultationsByDate.getOrDefault(date, Collections.emptyList());
                calendarGrid.add(createDayCell(date, dayConsultations, isToday), column, row);

                day++;
            }

            // Nouvelle ligne après 7 colonnes
            if (column == 6) {
                row++;
            }
        }
    }

    private AnchorPane createEmptyCell() {
        AnchorPane cell = new AnchorPane();
        cell.setStyle("-fx-background-color: #f8f9fa; -fx-border-color: #e9ecef;");
        cell.setPrefSize(100, 100);
        return cell;
    }
    private VBox createDayCell(LocalDate date, List<Consultation> consultations, boolean isToday) {
        VBox cell = new VBox(5);
        cell.setPadding(new Insets(10));
        cell.setPrefSize(200, 120); // Dimensions fixes
        cell.setMaxSize(200, 120);
        cell.setMinSize(200, 120);
        cell.getStyleClass().add("day-cell");

        if (date.equals(LocalDate.now())) {
            cell.getStyleClass().add("today-cell");
        }

        Label dayLabel = new Label(String.valueOf(date.getDayOfMonth()));
        dayLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        cell.getChildren().add(dayLabel);

        // Correction pour limiter le nombre de consultations affichées
        int consultationsToShow = Math.min(consultations.size(), 3); // Affiche max 3 consultations

        if (consultationsToShow > 0) {
            VBox consultationsBox = new VBox(3);
            consultationsBox.setPadding(new Insets(5, 0, 0, 0));

            for (int i = 0; i < consultationsToShow; i++) {
                Consultation consultation = consultations.get(i);
                HBox consultationItem = createConsultationItem(consultation);
                consultationsBox.getChildren().add(consultationItem);
            }

            // Si plus de 3 consultations, ajouter un indicateur
            if (consultations.size() > 3) {
                Label moreLabel = new Label("+" + (consultations.size() - 3) + " de plus...");
                moreLabel.setStyle("-fx-text-fill: #7f8c8d; -fx-font-size: 10;");
                consultationsBox.getChildren().add(moreLabel);
            }

            cell.getChildren().add(consultationsBox);
        }

        return cell;
    }
    private HBox createConsultationItem(Consultation consultation) {
        HBox item = new HBox();
        item.setPadding(new Insets(3));
        item.setSpacing(5);

        // Couleur en fonction du statut
        String bgColor;
        switch (consultation.getStatus().toLowerCase()) {
            case "faite":
                bgColor = "#a8d5ba";
                break;
            case "annulé":
                bgColor = "#e74c3c";
                break;
            case "en attente":
                bgColor = "#f39c12";
                break;
            default:
                bgColor = "#95a5a6";
        }

        item.setStyle("-fx-background-color: " + bgColor + "; -fx-background-radius: 3; -fx-padding: 3;");

        // Nom du patient
        Label nameLabel = new Label(consultation.getNom() + " " + consultation.getPrenom());
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 10px;");
        nameLabel.setMaxWidth(Double.MAX_VALUE);
        item.getChildren().add(nameLabel);

        // Action au clic
        item.setOnMouseClicked(event -> showConsultationDetails(consultation));

        return item;
    }

    private Map<LocalDate, List<Consultation>> loadConsultationsForMonth() {
        Map<LocalDate, List<Consultation>> result = new HashMap<>();

        try {
            // Charger toutes les consultations
            List<Consultation> allConsultations = consultationService.getAllConsultations();

            // Filtrer pour ce mois et organiser par date
            for (Consultation consultation : allConsultations) {
                try {
                    LocalDate consultDate = consultation.getDate().toLocalDate();


                    // Vérifier si la date est dans le mois courant
                    if (consultDate.getYear() == currentYearMonth.getYear() &&
                            consultDate.getMonthValue() == currentYearMonth.getMonthValue()) {

                        // Ajouter à la liste pour cette date
                        if (!result.containsKey(consultDate)) {
                            result.put(consultDate, new ArrayList<>());
                        }
                        result.get(consultDate).add(consultation);
                    }
                } catch (Exception e) {
                    System.err.println("Erreur de parsing de date: " + consultation.getDate());
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur de chargement", "Impossible de charger les consultations");
        }

        return result;
    }

    private void showConsultationDetails(Consultation consultation) {
        try {
            // Créer une boîte de dialogue pour les détails
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Détails de la consultation");
            dialog.setHeaderText(null);

            // Contenu de la boîte de dialogue
            VBox content = new VBox(10);
            content.setPadding(new Insets(20));

            Label titleLabel = new Label(consultation.getNom() + " " + consultation.getPrenom());
            titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));

            GridPane detailsGrid = new GridPane();
            detailsGrid.setHgap(10);
            detailsGrid.setVgap(5);
            detailsGrid.setPadding(new Insets(10));

            // Ajouter les détails
            int row = 0;

            detailsGrid.add(new Label("Date:"), 0, row);
            detailsGrid.add(new Label(), 1, row++);

            detailsGrid.add(new Label("Type:"), 0, row);
            detailsGrid.add(new Label(consultation.getType()), 1, row++);

            detailsGrid.add(new Label("Statut:"), 0, row);
            Label statusLabel = new Label(consultation.getStatus());
            setStatusStyle(statusLabel, consultation.getStatus());
            detailsGrid.add(statusLabel, 1, row++);

            // Boutons d'action
            HBox buttonsBox = new HBox(10);
            buttonsBox.setAlignment(Pos.CENTER);

            Button modifierBtn = new Button("Modifier");
            modifierBtn.setStyle("-fx-background-color: #a8d5ba; -fx-text-fill: white; -fx-font-weight: bold;");
            modifierBtn.setOnAction(event -> {
                dialog.close();
                modifierConsultation(consultation);
            });

            // Vérifier si une prescription existe
            boolean hasPrescription = false;
            try {
                Prescription prescription = prescriptionService.getPrescriptionByConsultationId(consultation.getId());
                hasPrescription = (prescription != null);
            } catch (SQLException e) {
                e.printStackTrace();
            }

            Button prescriptionBtn = new Button(hasPrescription ? "Voir Prescription" : "Ajouter Prescription");
            prescriptionBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
            boolean finalHasPrescription = hasPrescription;
            prescriptionBtn.setOnAction(event -> {
                dialog.close();
                if (finalHasPrescription) {
                    voirPrescription(consultation);
                } else {
                    ajouterPrescription(consultation);
                }
            });

            Button supprimerBtn = new Button("Supprimer");
            supprimerBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold;");
            supprimerBtn.setOnAction(event -> {
                dialog.close();
                supprimerConsultation(consultation);
            });

            buttonsBox.getChildren().addAll(modifierBtn, prescriptionBtn, supprimerBtn);

            content.getChildren().addAll(titleLabel, detailsGrid, buttonsBox);
            dialog.getDialogPane().setContent(content);

            // Bouton pour fermer
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            dialog.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Une erreur est survenue: " + e.getMessage());
        }
    }

    private void setStatusStyle(Label label, String status) {
        switch (status.toLowerCase()) {
            case "faite":
                label.setTextFill(Color.valueOf("#27ae60"));
                label.setFont(Font.font("System", FontWeight.BOLD, 12));
                break;
            case "annulé":
                label.setTextFill(Color.valueOf("#e74c3c"));
                label.setFont(Font.font("System", FontWeight.BOLD, 12));
                break;
            case "en attente":
                label.setTextFill(Color.valueOf("#f39c12"));
                label.setFont(Font.font("System", FontWeight.BOLD, 12));
                break;
            default:
                label.setTextFill(Color.valueOf("#7f8c8d"));
        }
    }

    private void modifierConsultation(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdateConsultation.fxml"));
            Parent root = loader.load();

            UpdateConsultationController controller = loader.getController();
            controller.initData(consultation, consultationService);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modifier la consultation");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHidden(event -> updateCalendarView());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'ouverture", "Impossible d'ouvrir le formulaire de modification.");
        }
    }

    private void supprimerConsultation(Consultation consultation) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer la consultation");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer la consultation de " +
                consultation.getNom() + " " + consultation.getPrenom() + " ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer d'abord la prescription associée si elle existe
                Prescription prescription = prescriptionService.getPrescriptionByConsultationId(consultation.getId());
                if (prescription != null) {
                    prescriptionService.deletePrescription(prescription.getId());
                }

                // Puis supprimer la consultation
                boolean deleted = consultationService.deleteConsultation(consultation.getId());
                if (deleted) {
                    showInfoAlert("Suppression réussie", "La consultation a été supprimée avec succès.");
                    updateCalendarView();
                } else {
                    showErrorAlert("Erreur de suppression", "La consultation n'a pas pu être supprimée.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Erreur de suppression", "Une erreur est survenue: " + e.getMessage());
            }
        }
    }

    private void ajouterPrescription(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPrescription.fxml"));
            Parent root = loader.load();

            AjouterPrescriptionController controller = loader.getController();
            controller.initData(consultation, prescriptionService);

            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Ajouter une prescription");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setOnHidden(event -> updateCalendarView());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'ouverture", "Impossible d'ouvrir le formulaire d'ajout de prescription.");
        }
    }

    private void voirPrescription(Consultation consultation) {
        try {
            Prescription prescription = prescriptionService.getPrescriptionByConsultationId(consultation.getId());

            if (prescription != null) {
                // Charger le FXML pour voir la prescription
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterPrescription.fxml"));
                Parent root = loader.load();

                /*VoirPrescriptionController controller = loader.getController();
                controller.initData(prescription);*/

                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("Détails de la prescription");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } else {
                showInfoAlert("Information", "Aucune prescription trouvée pour cette consultation.");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Impossible d'afficher la prescription: " + e.getMessage());
        }
    }

    private void showErrorAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void showInfoAlert(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }



    // Ajoutez cette méthode dans votre classe CalendrierController


}