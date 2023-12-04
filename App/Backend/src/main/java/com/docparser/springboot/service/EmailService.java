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
        message.setSubject("Hi there! Please click on the link below to login to Accessibilator");
        message.setText(redirectUrl + "?email=" + to + "&token=" + token);
        emailSender.send(messagemimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}
