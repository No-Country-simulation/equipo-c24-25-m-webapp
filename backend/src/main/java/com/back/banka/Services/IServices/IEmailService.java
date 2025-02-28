package com.back.banka.Services.IServices;

public interface IEmailService {
    void sendEmail(String to, String subject, String body);

}