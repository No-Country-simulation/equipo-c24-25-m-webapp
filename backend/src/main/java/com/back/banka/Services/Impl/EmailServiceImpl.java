package com.back.banka.Services.Impl;

import com.back.banka.Services.IServices.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.Map;


@Service
public class EmailServiceImpl implements IEmailService {

    private  final JavaMailSender javaMailSender;

    @Value("${MAIL_USERNAME}")
    private String mailUsername;

    public EmailServiceImpl(JavaMailSender javaMailSender){
        this.javaMailSender = javaMailSender;
    }

    @Override
    public void sendEmail(String to, String subject, String body) {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true,StandardCharsets.UTF_8.name());

            //Construcción del correo
            helper.setFrom(mailUsername);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);

            javaMailSender.send(message);
        } catch (MessagingException e){
            throw new RuntimeException("Hubo un error al enviar el correo", e);
        }
    }


}