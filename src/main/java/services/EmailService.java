package services;

import models.User;
import utils.session;

public class EmailService {
    private static final String EMAIL = "jbilimohamadrayen@gmail.com";
    private static final String PASSWORD = "dtibphtmvxpxkzcl";
    
    // Use alternative implementation to avoid class loading issues
    public void sendNotification(String toEmail, String subject, String message) {
        if (toEmail == null || toEmail.isEmpty()) {
            System.err.println("Error: Recipient email is null or empty");
            return;
        }
        
        try {
            // Use the other service to send emails
            utils.EmailService emailSender = new utils.EmailService();
            boolean success = emailSender.sendEmail(toEmail, subject, message);
            
            if (success) {
                System.out.println("Email sent successfully to " + toEmail);
            } else {
                System.err.println("Failed to send email to " + toEmail);
            }
        } catch (Exception e) {
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
        String message = "<html><body>" +
                "<h2>Cher " + currentUser.getName() + " " + currentUser.getPrename() + ",</h2>" +
                "<p>Nous vous confirmons la réception de votre commande.</p>" +
                "<h3>Détails de la commande :</h3>" +
                "<pre>" + orderDetails + "</pre>" +
                "<p>Merci de votre confiance.</p>" +
                "<p>Cordialement,<br>" +
                "L'équipe Nutrinet</p>" +
                "</body></html>";

        sendEmailToCurrentUser(subject, message);
    }

    public void sendPasswordReset(String resetLink) {
        User currentUser = session.getCurrentUser();
        if (currentUser == null) {
            System.err.println("Error: No user is currently logged in");
            return;
        }

        String subject = "Réinitialisation de votre mot de passe";
        String message = "<html><body>" +
                "<h2>Cher " + currentUser.getName() + " " + currentUser.getPrename() + ",</h2>" +
                "<p>Vous avez demandé la réinitialisation de votre mot de passe.</p>" +
                "<p>Cliquez sur le lien suivant pour réinitialiser votre mot de passe :</p>" +
                "<p><a href='" + resetLink + "'>" + resetLink + "</a></p>" +
                "<p>Si vous n'avez pas demandé cette réinitialisation, veuillez ignorer cet email.</p>" +
                "<p>Cordialement,<br>" +
                "L'équipe Nutrinet</p>" +
                "</body></html>";

        sendEmailToCurrentUser(subject, message);
    }
} 