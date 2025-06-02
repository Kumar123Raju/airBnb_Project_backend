package com.rajukumar.project.airBnbApp.service;

public interface EmailSenderService {

    void sendEmail(String toEmail,String subject,String body);
    void  sendEmail(String toEmail[],String subject,String body);
}
