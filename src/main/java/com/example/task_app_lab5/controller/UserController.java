package com.example.task_app_lab5.controller;
import com.example.task_app_lab5.model.User_table;
import com.example.task_app_lab5.reposiory.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Base64;

@Controller
@RequestMapping("/profile")
public class UserController {
    @Autowired
    private final UserRepo userRepo;
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
    public String editProfile(@AuthenticationPrincipal UserDetails userDetails,@ModelAttribute User_table updated, RedirectAttributes redirectAttributes) {
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        user.setEmail(updated.getEmail());
        user.setUsername(updated.getUsername());
        userRepo.save(user);

        UserDetails updatedUserDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles("USER")
                .build();

        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(
                updatedUserDetails, null, updatedUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully!");
        return "redirect:/profile";
    }

    @PostMapping("/upload-photo")
    public String uploadPhoto(@RequestParam("photo") MultipartFile photo, @AuthenticationPrincipal UserDetails userDetails,  RedirectAttributes redirectAttributes){
        User_table user = userRepo.findByUsername(userDetails.getUsername());
        if (user!= null && !photo.isEmpty()){
            try{
                user.setPhoto(photo.getBytes());
                userRepo.save(user);
                String base64Image = Base64.getEncoder().encodeToString(user.getPhoto());
                redirectAttributes.addFlashAttribute("base64Image", base64Image);
                System.out.println("Base64 Image String: " + base64Image);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Error while uploading the photo");
                e.printStackTrace();
                return "profile";
            }
        }
        redirectAttributes.addFlashAttribute("user", user);
        return "redirect:/profile";
    }


}
