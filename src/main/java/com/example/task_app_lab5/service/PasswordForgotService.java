package com.example.task_app_lab5.service;

import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.reposiory.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class PasswordForgotService {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public  void sendNewPassword(String email){
        User_table user = userRepo.findByEmail(email);
        if (user!=null){
            String newPassword = "newRandom";
            user.setPassword(passwordEncoder.encode(newPassword));
            userRepo.save(user);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("password recovery");
            message.setText("Your new password is:" + newPassword);
            mailSender.send(message);
        }

    }
}
