package com.example.task_app_lab5.controller;

import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.reposiory.UserRepo;
import com.example.task_app_lab5.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/profile")
public class UserController {
    @Autowired
    private final UserRepo userRepo;
    @Autowired
    private UserService userService;
    public UserController(UserRepo userRepo){
        this.userRepo = userRepo;
    }

    @GetMapping
    public String viewProfile(Model model, @AuthenticationPrincipal UserDetails userDetails){
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found!");
        }
        model.addAttribute("user", user);
        return "profile";
    }
    @GetMapping("/edit")
    public String editProfileForm(@AuthenticationPrincipal UserDetails userDetails, Model model){
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        model.addAttribute("user", user);
        return "editProfile";
    }
    @PostMapping("/edit")
    public String editProfile(@AuthenticationPrincipal UserDetails userDetails, @ModelAttribute User_table updated, Model model){
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        user.setEmail(updated.getEmail());
        user.setUsername(updated.getUsername());
        userRepo.save(user);
        model.addAttribute("user", user);
        System.out.println("Updated User: " + user);
        return "redirect:/profile";
    }

    @PostMapping("/upload-photo")
    public String uploadPhoto(@RequestParam("photo") MultipartFile photo, @AuthenticationPrincipal UserDetails userDetails, Model model){
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        if (user!= null && !photo.isEmpty()){
            try{
                user.setPhoto(photo.getBytes());
                userRepo.save(user);
            } catch (IOException e) {
                model.addAttribute("errorMessage", "Error while uploading the photo");
                e.printStackTrace();
                return "profile";
            }
        }
        return "redirect:/profile";
    }
}
