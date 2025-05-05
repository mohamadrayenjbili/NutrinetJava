package controllers;



import brevo.ApiClient;
import brevo.ApiException;
import brevo.Configuration;
import brevo.auth.ApiKeyAuth;
import brevoApi.TransactionalEmailsApi;
import brevoModel.CreateSmtpEmail;
import brevoModel.SendSmtpEmail;
import brevoModel.SendSmtpEmailSender;
import brevoModel.SendSmtpEmailTo;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import models.Consultation;
import models.Prescription;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import services.ConsultationService;
import services.PrescriptionService;
import utils.SmsService;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import javax.mail.*;
import javax.mail.internet.*;


public class BackConsultationAjoutController implements Initializable {

    @FXML
    private ListView<Consultation> consultationsListView;

    private ConsultationService consultationService;
    private PrescriptionService prescriptionService;

    @FXML
    private Button btnAjouterConsultation;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/didou", "root", "");
            this.consultationService = new ConsultationService(connection);
            this.prescriptionService = new PrescriptionService(connection);
            SmsService.initialize();

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
                private final Button btnEnvoyerRappel = new Button("Envoyer Rappel");
                private final Rectangle separator = new Rectangle(0, 1);

                {
                    container.setStyle("-fx-padding: 15; -fx-background-radius: 8; -fx-background-color: white;");
                    container.setMaxWidth(Double.MAX_VALUE);

                    nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");
                    detailsLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #7f8c8d;");

                    btnModifier.setStyle("-fx-background-color: #a8d5ba; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10 5 10;");
                    btnSupprimer.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 4; -fx-padding: 5 10 5 10;");
                    btnEnvoyerRappel.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
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

                    btnEnvoyerRappel.setOnAction(event -> {
                        Consultation consultation = getItem();
                        if (consultation != null) {
                            // Confirmation avant envoi
                            Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
                            confirmation.setTitle("Confirmer l'envoi");
                            confirmation.setHeaderText("Envoyer un rappel à " + consultation.getMail());
                            confirmation.setContentText("Êtes-vous sûr de vouloir envoyer ce rappel ?");

                            Optional<ButtonType> result = confirmation.showAndWait();
                            if (result.isPresent() && result.get() == ButtonType.OK) {
                                envoyerRappelSMS(consultation);
                            }
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
                        buttonContainer.getChildren().addAll(btnModifier, btnSupprimer, btnEnvoyerRappel);

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

   /* private void envoyerRappelPatient(Consultation consultation) {
        if (consultation.getMail() == null || consultation.getMail().isEmpty()) {
            showErrorAlert("Erreur", "Aucune adresse email enregistrée pour ce patient.");
            return;
        }

        final String smtpHost = "smtp.gmail.com";
        final int smtpPort = 587;
        final String username = "malek.ayedi@esprit.tn";
        final String password = "mmzf vedq czpx sdlp";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.ssl.trust", "*"); // Solution radicale pour les problèmes SSL
        props.put("mail.smtp.connectiontimeout", "5000"); // 5 secondes timeout
        props.put("mail.smtp.timeout", "5000"); // 5 secondes timeout

        // Activez le debug pour voir les erreurs détaillées
        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
        session.setDebug(true); // IMPORTANT pour le diagnostic

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(username));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(consultation.getMail()));
            message.setSubject("Rappel de rendez-vous médical");

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE dd MMMM yyyy 'à' HH'h'mm", Locale.FRENCH);
            String dateFormatee = consultation.getDate().format(formatter);

            String texteEmail = String.format(
                    "Bonjour %s %s,\n\nCeci est un rappel que vous avez un rendez-vous le :\n📅 %s\n\nCordialement,\nVotre Clinique Médicale",
                    consultation.getPrenom(), consultation.getNom(), dateFormatee
            );

            message.setText(texteEmail);

            new Thread(() -> {
                try {
                    Transport transport = session.getTransport("smtp");
                    transport.connect(smtpHost, smtpPort, username, password); // Connexion explicite
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();

                    Platform.runLater(() ->
                            showInfoAlert("Succès", "Rappel envoyé à:\n" + consultation.getMail()));
                } catch (Exception e) {
                    Platform.runLater(() ->
                            showErrorAlert("Erreur SMTP", getErrorMessage(e)));
                }
            }).start();

        } catch (Exception e) {
            showErrorAlert("Erreur", getErrorMessage(e));
        }
    }

    private String getErrorMessage(Exception e) {
        if (e instanceof AuthenticationFailedException) {
            return "Échec d'authentification:\nVérifiez email/mot de passe";
        } else if (e instanceof MessagingException) {
            return "Erreur SMTP:\n" + e.getMessage();
        }
        return "Erreur inattendue:\n" + e.getMessage();
    }*/



    // Méthode d'envoi de rappel
    private void envoyerRappelSMS(Consultation consultation) {
        // Formatter la date pour un affichage lisible
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
        String dateFormatted = consultation.getDate().format(formatter);

        // Demander confirmation avant d'envoyer le SMS
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation d'envoi de SMS");
        confirmDialog.setHeaderText("Envoyer un rappel de consultation");
        confirmDialog.setContentText("Voulez-vous envoyer un SMS de rappel à " +
                consultation.getNom() + " " + consultation.getPrenom() +
                " pour sa consultation du " + dateFormatted + " ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Initialiser le service SMS si nécessaire
                SmsService.initialize();

                // Envoyer le SMS avec le numéro de téléphone de type int
                SmsService.sendAppointmentReminder(
                        consultation.getTel(),
                        consultation.getPrenom() + " " + consultation.getNom(),
                        dateFormatted,
                        "Didou" // Remplacez par le nom du médecin ou récupérez-le depuis la consultation
                );

            } catch (Exception e) {
                e.printStackTrace();
                showErrorAlert("Erreur d'envoi SMS", "Une erreur est survenue: " + e.getMessage());
            }
        }
    }

}
