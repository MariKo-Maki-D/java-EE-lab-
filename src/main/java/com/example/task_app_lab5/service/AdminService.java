package com.example.task_app_lab5.service;
import com.example.task_app_lab5.model.Tasks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    @Autowired
    private JavaMailSender mailSender;

    @PreAuthorize("hasRole('ADMIN')")
    public void sendTaskNotification(String userEmail, Tasks task) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(userEmail);
        message.setSubject("New Task Assigned");
        message.setText("You have a new task: " + task.getTitle() + " - " +task.getDescription());
        mailSender.send(message);
    }
}
