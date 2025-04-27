package services;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

public class EmailService {
    private static final String EMAIL = "oueslatighassen5201@gmail.com"; // Replace with your Gmail
    private static final String PASSWORD = "marcbolialydwvun"; // Replace with your app password

    public void sendNotification(String toEmail, String subject, String message) {
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
} 