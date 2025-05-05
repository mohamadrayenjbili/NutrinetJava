package utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import javafx.application.Platform;
import javafx.scene.control.Alert;

public class SmsService {
    // Remplacez par vos identifiants Twilio
    private static final String ACCOUNT_SID = "AC311eca359ee0cbfbf5cf0c53239a7145";
    private static final String AUTH_TOKEN = "2fa57777c7f7bdd01e1b961b53f8bab0";
    private static final String TWILIO_PHONE = "+16204592879"; // Votre numéro Twilio vérifié
    private static boolean isInitialized = false;

    /**
     * Initialise le client Twilio avec les identifiants fournis
     */
    public static void initialize() {
        if (!isInitialized) {
            try {
                Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
                isInitialized = true;
                System.out.println("Twilio client initialized successfully");
            } catch (Exception e) {
                System.err.println("Error initializing Twilio client: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    /**
     * Envoie un SMS de rappel de rendez-vous
     *
     * @param patientPhone Numéro de téléphone du patient (entier)
     * @param patientName Nom du patient
     * @param appointmentDate Date et heure du rendez-vous
     * @param doctorName Nom du médecin
     */
    public static void sendAppointmentReminder(int patientPhone,
                                               String patientName,
                                               String appointmentDate,
                                               String doctorName) {
        // S'assurer que Twilio est initialisé
        if (!isInitialized) {
            initialize();
        }

        // Exécuter l'envoi dans un thread séparé pour ne pas bloquer l'UI
        new Thread(() -> {
            try {
                String formattedPhone = formatPhoneNumber(patientPhone);
                String messageText = String.format(
                        "Bonjour %s,\nRappel: Consultation avec Dr %s le %s.\nClinique Médicale",
                        patientName, doctorName, appointmentDate);

                System.out.println("Envoi SMS à " + formattedPhone);
                System.out.println("Message: " + messageText);

                Message message = Message.creator(
                        new PhoneNumber(formattedPhone),  // À: numéro du patient
                        new PhoneNumber(TWILIO_PHONE),    // De: votre numéro Twilio
                        messageText                       // Corps du message
                ).create();

                System.out.println("SMS envoyé avec succès! SID: " + message.getSid());

                // Afficher alerte dans le thread UI
                Platform.runLater(() ->
                        showAlert("Succès", "SMS envoyé à " + patientName + " (" + formattedPhone + ")",
                                Alert.AlertType.INFORMATION));

            } catch (Exception e) {
                System.err.println("Erreur lors de l'envoi du SMS: " + e.getMessage());
                e.printStackTrace();

                // Afficher l'erreur dans le thread UI
                Platform.runLater(() ->
                        showAlert("Erreur", "Échec d'envoi SMS: " + e.getMessage(),
                                Alert.AlertType.ERROR));
            }
        }).start();
    }

    /**
     * Formate le numéro de téléphone au format international requis par Twilio
     *
     * @param phone Numéro de téléphone (entier) à formater
     * @return Numéro formaté en chaîne de caractères avec indicatif international
     */
    private static String formatPhoneNumber(int phone) {
        // Convertir l'entier en chaîne
        String phoneStr = String.valueOf(phone);

        // Déterminer le format en fonction de la longueur et du début du numéro
        if (phoneStr.length() == 8) {
            // Format tunisien standard à 8 chiffres (sans indicatif)
            return "+216" + phoneStr;
        } else if (phoneStr.startsWith("216") && phoneStr.length() == 11) {
            // Le numéro contient déjà l'indicatif tunisien
            return "+" + phoneStr;
        } else if (phoneStr.length() > 8) {
            // Pour des numéros internationaux, on suppose qu'ils sont complets
            // mais manquent juste le +
            return "+" + phoneStr;
        } else {
            // Format par défaut - ajouter indicatif tunisien
            return "+216" + phoneStr;
        }
    }

    /**
     * Affiche une alerte dans l'interface utilisateur
     */
    private static void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}