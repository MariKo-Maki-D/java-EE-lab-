package com.example.task_app_lab5.controller;

import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.reposiory.UserRepo;
import com.example.task_app_lab5.service.PasswordForgotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PasswordController {
    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    @Autowired
    private PasswordForgotService passwordForgotService;
    public PasswordController(UserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/profile/change-password")
    public String changePasswordForm(Model model){
        return "change-password";
    }
    @PostMapping("/profile/change-password")
    public String changePassword(@RequestParam("oldPassword") String oldPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User_table user = userRepo.findByUsername(username);
        if(!passwordEncoder.matches(oldPassword, user.getPassword())){
            model.addAttribute("error", "Old password is incorrect");
            return "change-password";
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
        return "redirect:/profile";
    }
    @PostMapping("/profile/reset-password")
    public String resetPassword(Model model) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User_table user = userRepo.findByUsername(username);
        passwordForgotService.sendNewPassword(user.getEmail());
//        if (user != null) {
//            passwordForgotService.sendNewPassword(user.getEmail());
//            model.addAttribute("message", "A new password has been sent to your email.");
//        } else {
//            model.addAttribute("error", "Failed to reset password. User not found.");
//        }
        return "change-password";
    }
}
