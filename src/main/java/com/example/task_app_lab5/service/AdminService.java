package com.example.task_app_lab5.service;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private JavaMailSender mailSender;

    @PreAuthorize("hasRole('ADMIN')")
    public void sendTaskNotification(String userEmail, String taskDetails) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("New Task Assigned");
        message.setText("You have a new task: " + taskDetails);
        mailSender.send(message);
    }
}
