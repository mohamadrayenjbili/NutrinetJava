package services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import models.User;
import utils.session;

public class EmailService {
    private static final String EMAIL = "jbilimohamadrayen@gmail.com"; // Replace with your Gmail
    private static final String PASSWORD = "dtibphtmvxpxkzcl"; // Replace with your app password

    public void sendNotification(String toEmail, String subject, String message) {
        if (toEmail == null || toEmail.isEmpty()) {
            System.err.println("Error: Recipient email is null or empty");
            return;
        }

        // Set up mail server properties
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        // Create a session with authentication
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(EMAIL, PASSWORD);
            }
        });

        try {
            // Create a new message
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(EMAIL));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            msg.setSubject(subject);
            msg.setText(message);

            // Send the message
            Transport.send(msg);
            System.out.println("Email sent successfully to " + toEmail);
        } catch (MessagingException e) {
            System.err.println("Error sending email: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendEmailToCurrentUser(String subject, String message) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("Error: No user is currently logged in");
            return;
        }

        String userEmail = currentUser.getEmail();
        if (userEmail == null || userEmail.isEmpty()) {
            System.err.println("Error: User email is null or empty");
            return;
        }

        sendNotification(userEmail, subject, message);
    }

    public void sendOrderConfirmation(String orderDetails) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("Error: No user is currently logged in");
            return;
        }

        String subject = "Confirmation de votre commande";
        String message = "Cher " + currentUser.getName() + " " + currentUser.getPrename() + ",\n\n" +
                        "Nous vous confirmons la réception de votre commande.\n\n" +
                        "Détails de la commande :\n" +
                        orderDetails + "\n\n" +
                        "Merci de votre confiance.\n" +
                        "Cordialement,\n" +
                        "L'équipe Nutrinet";

        sendEmailToCurrentUser(subject, message);
    }

    public void sendPasswordReset(String resetLink) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("Error: No user is currently logged in");
            return;
        }

        String subject = "Réinitialisation de votre mot de passe";
        String message = "Cher " + currentUser.getName() + " " + currentUser.getPrename() + ",\n\n" +
                        "Vous avez demandé la réinitialisation de votre mot de passe.\n" +
                        "Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n" +
                        resetLink + "\n\n" +
                        "Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.\n\n" +
                        "Cordialement,\n" +
                        "L'équipe Nutrinet";

        sendEmailToCurrentUser(subject, message);
    }
} 