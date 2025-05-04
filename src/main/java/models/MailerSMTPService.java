/*

package models;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class MailerSendService {

    // Ton token API MailerSend (garde-le privé dans une vraie app !)
    private static final String API_TOKEN = "mlsn.a809438982a2b140bd2676ddd4ce9a4258fc3bca2a9c607dfed196b64ef8f706";

    // Email "from" (doit être validé chez MailerSend)
    private static final String FROM_EMAIL = "noreply@test-z0vklo66jq7l7qrx.mlsender.net";


    // Email "to" (admin qui reçoit)
    private static final String TO_EMAIL = "timosims4@gmail.com";

    public static void sendEmail(String subject, String messageContent) {
        try {
            URL url = new URL("https://api.mailersend.com/v1/email");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + API_TOKEN);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String jsonInputString = "{"
                    + "\"from\": {\"email\": \"" + FROM_EMAIL + "\"},"
                    + "\"to\": [{\"email\": \"" + TO_EMAIL + "\"}],"
                    + "\"subject\": \"" + subject + "\","
                    + "\"text\": \"" + messageContent + "\""
                    + "}";

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int code = conn.getResponseCode();
            if (code == 202) {
                System.out.println("Email envoyé avec succès !");
            } else {
                System.err.println("Erreur lors de l'envoi de l'email, code: " + code);

                InputStream errorStream = conn.getErrorStream();
                if (errorStream != null) {
                    String errorResponse = new String(errorStream.readAllBytes(), StandardCharsets.UTF_8);
                    System.err.println("Erreur détaillée : " + errorResponse);
                }
            }


        } catch (IOException e) {
            System.err.println("Erreur d'envoi d'email : " + e.getMessage());
        }
    }
} */
package models;
import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class MailerSMTPService {

    public static void sendEmailSMTP(String subject, String content) {
        final String username = "MS_ueOUZN@test-z0vklo66jq7l7qrx.mlsender.net"; // depuis ton interface MailerSend
        final String password = "mssp.QB4ZFlv.vywj2lp79dpl7oqz.zbv7vnd";        // depuis ton interface aussi

        String from = username;
        String to = "timosims4@gmail.com"; // destinataire

        // Configuration SMTP
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // TLS
        props.put("mail.smtp.host", "smtp.mailersend.net");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);

            System.out.println("Email envoyé avec succès via SMTP !");
        } catch (MessagingException e) {
            e.printStackTrace();
            System.err.println("Erreur SMTP : " + e.getMessage());
        }
    }
}

