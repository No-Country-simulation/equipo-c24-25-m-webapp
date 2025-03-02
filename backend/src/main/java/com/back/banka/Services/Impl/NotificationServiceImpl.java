package com.back.banka.Services.Impl;

import com.back.banka.Dtos.RequestDto.NotificationRequestDto;
import com.back.banka.Enums.NotificationsType;
import com.back.banka.Model.Notifications;
import com.back.banka.Repository.INotificationRepository;
import com.back.banka.Services.IServices.INotificationService;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final INotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Override
    //Se crea y guarda la notificación DB
    public Notifications createAndNotify(NotificationRequestDto request) {
        if (request.getTitle() == null || request.getBody() == null) {
            throw new RuntimeException("El título y el cuerpo no pueden estar vacíos");
        }

        Notifications notification = Notifications.builder()
                .title(request.getTitle())
                .body(request.getBody())
                .date(LocalDateTime.now())
                .user(request.getUser())
                .type(request.getType())
                .build();

        Notifications savedNotification = notificationRepository.save(notification);

        if (request.getUser() != null && request.getUser().getEmail() != null) {
            sendEmailNotification(
                    request.getUser().getEmail(),
                    request.getTitle(),
                    request.getBody()
            );
        }

        return savedNotification;
    }

    private void sendEmailNotification(String to, String subject, String content) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(content, true);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error al enviar correo: " + e.getMessage());
        }
    }

}

