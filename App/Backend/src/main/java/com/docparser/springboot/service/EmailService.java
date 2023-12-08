package com.docparser.springboot.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

// Service annotation indicates that this class is a Spring service component.
@Service
public class EmailService {
    // This injects the value for 'magic.link.redirect.url' from the application
    // properties.
    @Value("${magic.link.redirect.url}")
    private String redirectUrl;

    // Autowired annotation is used to auto-wire the JavaMailSender bean from Spring
    // context.
    @Autowired
    private JavaMailSender emailSender;

    // Method for sending a simple email message.
    public void sendSimpleMessage(String to, String token) {
        try {
            // Creating a MIME message using the JavaMailSender.
            MimeMessage messagemimeMessage = emailSender.createMimeMessage();
            // Helper class to simplify the creation of MIME messages.
            MimeMessageHelper message = new MimeMessageHelper(messagemimeMessage, "UTF-8");
            // Setting the sender's email.
            message.setFrom("Accessibilator");
            // Setting the recipient's email.
            message.setTo(to);
            // Setting the subject of the email.
            message.setSubject("Login to Accessibilator");
            // StringBuilder to construct the body of the email.
            StringBuilder sb = new StringBuilder();
            sb.append("Hi there!").append("\n\n")
                    .append("To log in to Accessibilator, click this link")
                    .append("\n\n").append(redirectUrl + "?email=" + to + "&token=" + token)
                    .append("\n\n")
                    .append("The above link automatically expires in 30 minutes. Copy and paste it into your browser if you're unable to click the link. If you didn't make this request, please ignore this email.")
                    .append("\n\n").append("Thanks!").append("\n\n").append("The Accessibilator Team");
            // Setting the text of the email using the StringBuilder content.
            message.setText(sb.toString());
            // Sending the email.
            emailSender.send(messagemimeMessage);
        } catch (MessagingException e) {
            // RuntimeException is thrown if there's an issue with sending the email.
            throw new RuntimeException(e);
        }
    }
}
