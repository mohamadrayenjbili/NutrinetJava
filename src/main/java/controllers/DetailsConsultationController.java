package controllers;



import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Consultation;
import models.Prescription;
import services.ConsultationService;
import services.PrescriptionService;

public class DetailsConsultationController implements Initializable {

    @FXML
    private ListView<Consultation> consultationsListView;

    private ConsultationService consultationService;
    private PrescriptionService prescriptionService;
    @FXML
    private TextField searchField;
    @FXML
    private Button btnAjouterConsultation;
    @FXML
    private Button btnCalendrier;

    private ObservableList<Consultation> consultationsObservableList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/didou", "root", "");
            this.consultationService = new ConsultationService(connection);
            this.prescriptionService = new PrescriptionService(connection);

            loadConsultations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadConsultations() {
        try {
            List<Consultation> consultations = consultationService.getAllConsultations();
            ObservableList<Consultation> consultationsObservableList = FXCollections.observableArrayList(consultations);

            consultationsListView.setItems(consultationsObservableList);

            consultationsListView.setCellFactory(param -> new ListCell<>() {
                private final HBox container = new HBox(15);
                private final VBox infoContainer = new VBox(5);
                private final HBox buttonContainer = new HBox(10);
                private final Label nameLabel = new Label();
                private final Label detailsLabel = new Label();
                private final Button btnModifier = new Button("Modifier");
                private final Button btnSupprimer = new Button("Supprimer");
                private final Button btnAjouterPrescription = new Button("Ajouter Prescription");
                private final Button btnUpdatePrescription = new Button("Modifier Prescription");
                private final Button btnDeletePrescription = new Button("Supprimer Prescription");
                private final Button btnVoirPrescription = new Button("voir Prescription");

                private final Rectangle separator = new Rectangle(0, 1);

                {
                    container.setStyle("-fx-padding: 15; -fx-background-radius: 8; -fx-background-color: white;");
                    container.setMaxWidth(Double.MAX_VALUE);

                    nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                    detailsLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

                    btnModifier.setStyle("-fx-background-color: #a8d5ba; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10 5 10;");
                    btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10 5 10;");
                    btnAjouterPrescription.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10 5 10;");
                    btnUpdatePrescription.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10 5 10;");
                    btnDeletePrescription.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10 5 10;");
                    btnVoirPrescription.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10 5 10;");
                    separator.setWidth(600);
                    separator.setFill(Color.valueOf("#ecf0f1"));

                    infoContainer.getChildren().addAll(nameLabel, detailsLabel);
                    container.getChildren().addAll(infoContainer, new Region());

                    btnModifier.setOnAction(event -> {
                        Consultation consultation = getItem();
                        if (consultation != null) {
                            modifierConsultation(consultation);
                        }
                    });

                    btnSupprimer.setOnAction(event -> {
                        Consultation consultation = getItem();
                        if (consultation != null) {
                            supprimerConsultation(consultation);
                        }
                    });

                    btnAjouterPrescription.setOnAction(event -> {
                        Consultation consultation = getItem();
                        if (consultation != null) {
                            ajouterPrescription(consultation);
                        }
                    });

                    btnUpdatePrescription.setOnAction(event -> {
                        Consultation consultation = getItem();
                        if (consultation != null) {
                            modifierPrescription(consultation);
                        }
                    });

                    btnDeletePrescription.setOnAction(event -> {
                        Consultation consultation = getItem();
                        if (consultation != null) {
                            supprimerPrescription(consultation);
                        }
                    });
                    btnVoirPrescription.setOnAction(event -> {
                        Consultation consultation = getItem(); // Récupère la consultation associée à la ligne actuelle
                        if (consultation != null) {
                            voirPrescription(consultation); // Ouvre la fenêtre pour afficher la prescription liée
                        }
                    });


                }

                @Override
                protected void updateItem(Consultation consultation, boolean empty) {
                    super.updateItem(consultation, empty);
                    if (empty || consultation == null) {
                        setGraphic(null);
                    } else {
                        nameLabel.setText(consultation.getNom() + " " + consultation.getPrenom());
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
                        String formattedDate = consultation.getDate().format(formatter);

                        String details = String.format("%s • %s • %s",
                                consultation.getType(),
                                formattedDate,
                                getStatusStyled(consultation.getStatus()));


                        String statusStyle = switch (consultation.getStatus().toLowerCase()) {
                            case "faite" -> "-fx-text-fill: #27ae60; -fx-font-weight: bold;";
                            case "annulé" -> "-fx-text-fill: #e74c3c; -fx-font-weight: bold;";
                            case "en attente" -> "-fx-text-fill: #f39c12; -fx-font-weight: bold;";
                            default -> "-fx-text-fill: #7f8c8d;";
                        };

                        detailsLabel.setText(details);
                        detailsLabel.setStyle("-fx-font-size: 13px; " + statusStyle);

                        // Check if prescription exists for this consultation
                        buttonContainer.getChildren().clear();
                        buttonContainer.getChildren().addAll(btnModifier, btnSupprimer);

                        try {
                            Prescription prescription = prescriptionService.getPrescriptionByConsultationId(consultation.getId());
                            if (prescription == null) {
                                buttonContainer.getChildren().add(btnAjouterPrescription);
                            } else {
                                Button btnExportPDF = new Button("Exporter PDF");
                                btnExportPDF.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10 5 10;");
                                btnExportPDF.setOnAction(event -> exporterPrescriptionPDF(consultation));
                                buttonContainer.getChildren().addAll(btnUpdatePrescription, btnDeletePrescription , btnExportPDF);
                            }
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        container.getChildren().remove(buttonContainer);
                        container.getChildren().add(buttonContainer);
                        setGraphic(container);
                    }
                }

                private String getStatusStyled(String status) {
                    return status;
                }
            });

        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur de chargement des consultations", "Une erreur est survenue lors du chargement des consultations.");
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
            stage.setOnHidden(event -> loadConsultations());
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'ouverture", "Impossible d'ouvrir le formulaire d'ajout de prescription.");
        }
    }

    private void modifierPrescription(Consultation consultation) {
        try {
            Prescription prescription = prescriptionService.getPrescriptionByConsultationId(consultation.getId());
            if (prescription != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/UpdatePrescription.fxml"));
                Parent root = loader.load();

                UpdatePrescriptionController controller = loader.getController();
                controller.initData(prescription, prescriptionService);

                Scene scene = new Scene(root);
                Stage stage = new Stage();
                stage.setTitle("Modifier la prescription");
                stage.setScene(scene);
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.setOnHidden(event -> loadConsultations());
                stage.show();
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'ouverture", "Impossible d'ouvrir le formulaire de modification de prescription.");
        }
    }

    private void supprimerPrescription(Consultation consultation) {
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText("Supprimer la prescription");
        confirmDialog.setContentText("Êtes-vous sûr de vouloir supprimer la prescription pour " +
                consultation.getNom() + " " + consultation.getPrenom() + " ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                Prescription prescription = prescriptionService.getPrescriptionByConsultationId(consultation.getId());
                if (prescription != null) {
                    boolean deleted = prescriptionService.deletePrescription(prescription.getId());
                    if (deleted) {
                        showInfoAlert("Suppression réussie", "La prescription a été supprimée avec succès.");
                        loadConsultations();
                    } else {
                        showErrorAlert("Erreur de suppression", "La prescription n'a pas pu être supprimée.");
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Erreur de suppression", "Une erreur est survenue: " + e.getMessage());
            }
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
            stage.setOnHidden(event -> loadConsultations());
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
                // First delete associated prescription if exists
                Prescription prescription = prescriptionService.getPrescriptionByConsultationId(consultation.getId());
                if (prescription != null) {
                    prescriptionService.deletePrescription(prescription.getId());
                }

                // Then delete the consultation
                boolean deleted = consultationService.deleteConsultation(consultation.getId());
                if (deleted) {
                    consultationsListView.getItems().remove(consultation);
                    showInfoAlert("Suppression réussie", "La consultation a été supprimée avec succès.");
                } else {
                    showErrorAlert("Erreur de suppression", "La consultation n'a pas pu être supprimée.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                showErrorAlert("Erreur de suppression", "Une erreur est survenue: " + e.getMessage());
            }
        }
    }

    private void voirPrescription(Consultation consultation) {
        try {
            System.out.println("Consultation ID: " + consultation.getId());

            Prescription prescription = prescriptionService.getPrescriptionByConsultationId(consultation.getId());

            if (prescription != null) {
                // Charger le FXML
                URL fxmlLocation = getClass().getResource("/VoirPrescription.fxml");
                if (fxmlLocation == null) {
                    throw new IOException("Le fichier FXML 'VoirPrescription.fxml' est introuvable.");
                }

                FXMLLoader loader = new FXMLLoader(fxmlLocation);

                // Important : charger la vue d'abord
                Parent root = loader.load();

                // Ensuite seulement, récupérer le contrôleur
                /*VoirPrescriptionController controller = loader.getController();
                if (controller == null) {
                    throw new IllegalStateException("Le contrôleur VoirPrescriptionController est null. Vérifie le fx:controller.");
                }*/

                // Initialiser les données
                // controller.initData(prescription);

                // Afficher la fenêtre
                Stage stage = new Stage();
                stage.setTitle("Voir la prescription");
                stage.setScene(new Scene(root));
                stage.initModality(Modality.APPLICATION_MODAL);
                stage.show();
            } else {
                System.out.println("Aucune prescription trouvée pour cette consultation.");
                showErrorAlert("Erreur", "Aucune prescription disponible pour cette consultation.");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'ouverture", "Impossible de charger le fichier FXML : " + e.getMessage());
        } catch (SQLException e) {
            e.printStackTrace();
            showErrorAlert("Erreur d'accès aux données", "Problème lors de l'accès à la base de données.");
        } catch (Exception e) {
            e.printStackTrace();
            showErrorAlert("Erreur inattendue", "Une erreur s'est produite : " + e.getMessage());
        }
    }

    @FXML
    private void handleAddConsultation() {
        // Vous pouvez ouvrir un nouveau formulaire FXML ici
        try {
            // Code pour charger la fenêtre du formulaire d'ajout
            // Exemple : Ouvrir une nouvelle fenêtre avec le formulaire d'ajout

            // Affichage d'une alerte temporaire pour l'exemple
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ajouter Consultation");
            alert.setHeaderText("Formulaire d'ajout de consultation");
            alert.setContentText("Formulaire d'ajout de consultation va s'ouvrir.");
            alert.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
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

    public Button getBtnAjouterConsultation() {
        return btnAjouterConsultation;
    }

    public void setBtnAjouterConsultation(Button btnAjouterConsultation) {
        this.btnAjouterConsultation = btnAjouterConsultation;
    }





    private void exporterPrescriptionPDF(Consultation consultation) {
        try {
            Prescription prescription = prescriptionService.getPrescriptionByConsultationId(consultation.getId());
            if (prescription != null) {
                PDDocument document = new PDDocument();
                PDPage page = new PDPage();
                document.addPage(page);

                PDPageContentStream contentStream = new PDPageContentStream(document, page);

                // Marges de départ
                float marginX = 60;
                float startY = 750;

                // TITRE
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 20);
                contentStream.newLineAtOffset(marginX, startY);
                contentStream.showText("Prescription Médicale");
                contentStream.endText();

                // LIGNE DE SÉPARATION
                contentStream.setLineWidth(1f);
                contentStream.moveTo(marginX, startY - 10);
                contentStream.lineTo(550, startY - 10);
                contentStream.stroke();

                // INFOS PATIENT
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(marginX, startY - 40);
                contentStream.showText("Nom du patient : ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(consultation.getNom() + " " + consultation.getPrenom());

                contentStream.newLineAtOffset(0, -20);
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.showText("Date de consultation : ");
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.showText(String.valueOf(consultation.getDate()));
                contentStream.endText();

                // OBJECTIF
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLineAtOffset(marginX, startY - 100);
                contentStream.showText("Objectif du patient :");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(marginX, startY - 120);
                contentStream.showText(prescription.getObjectif());
                contentStream.endText();

                // PROGRAMME
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.newLineAtOffset(marginX, startY - 160);
                contentStream.showText("Programme prescrit :");
                contentStream.endText();

                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 12);
                contentStream.newLineAtOffset(marginX, startY - 180);
                contentStream.showText(prescription.getProgramme());
                contentStream.endText();

                contentStream.close();

                File pdfFile = File.createTempFile("prescription_", ".pdf");
                document.save(pdfFile);
                document.close();

                if (Desktop.isDesktopSupported()) {
                    Desktop.getDesktop().open(pdfFile);
                } else {
                    showInfoAlert("Export réussi", "PDF généré: " + pdfFile.getAbsolutePath());
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            showErrorAlert("Erreur", "Échec de l'export PDF: " + e.getMessage());
        }
    }

    @FXML
    private void rechercherConsultation() {
        String searchText = searchField.getText().toLowerCase().trim();
        if (searchText.isEmpty()) {
            consultationsListView.setItems(consultationsObservableList);
        } else {
            ObservableList<Consultation> filteredList = consultationsObservableList.filtered(
                    c -> c.getNom().toLowerCase().contains(searchText));
            consultationsListView.setItems(filteredList);
        }
    }
    @FXML
    private void handleCalendrier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/calendrier.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) btnCalendrier.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Mon Calendrier");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToAfficherProduitFront(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Produit/afficherProduitFront.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void openSeifProgram(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Programme/AfficherProgrammeFront.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void navigateToAfficherObjective(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Objective/adminobjective.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToMalek(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/malek.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void naviguateToForum(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Forum/Forum.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void naviguateToReclamation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Reclamation/Reclamation.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void goToProfile(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/profile.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/user/sign_in.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((Button) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
