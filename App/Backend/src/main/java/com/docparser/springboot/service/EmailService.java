package com.docparser.springboot.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {
    @Value("${magic.link.redirect.url}")
    private String redirectUrl;

    @Autowired
    private JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String token) {
        try {
        MimeMessage messagemimeMessage = emailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(messagemimeMessage, "UTF-8");
        message.setFrom("Accessibilator");
        message.setTo(to);
        message.setSubject("Login to Accessibilator ");
        StringBuilder sb = new StringBuilder();
        sb.append("Hi there!").append("\n\n")
                .append("To log in to Accessibilator, click this link")
                .append("\n\n").append(redirectUrl + "?email=" + to + "&token=" + token)
                .append("\n\n")
                .append("The above link automatically expires in 30 minutes. Copy and paste it into your browser if you're unable to click the link. If you didn't make this request, please ignore this email.")
                .append("\n\n").append("Thanks!").append("\n\n").append("The Accessibilator Team");
              message.setText(sb.toString());
        emailSender.send(messagemimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
