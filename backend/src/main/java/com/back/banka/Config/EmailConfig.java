package com.back.banka.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration

public class EmailConfig {

    @Value("${MAIL_USERNAME}")
    private String mailUsername;
    @Value("${MAIL_PASSWORD}")
    private String mailPassword;

    @Bean
    public JavaMailSender javaMailSender() {
    JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

    // Configurar los parámetros SMTP según el proveedor de correo
    mailSender.setHost("smtp.gmail.com");
    mailSender.setPort(587);  // El puerto SMTP de Gmail
    mailSender.setUsername(mailUsername);  // Tu correo electrónico
    mailSender.setPassword(mailPassword);  // Tu contraseña

    Properties props = mailSender.getJavaMailProperties();
    props.put("mail.transport.protocol", "smtp");
    props.put("mail.smtp.auth", "true");
    props.put("mail.smtp.starttls.enable", "true");
    props.put("mail.debug", "true");

    return mailSender;
}
}