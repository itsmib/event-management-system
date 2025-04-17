package com.cts.EventManagementSystem.controller;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
 
import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.service.UserRegistrationService;
 
@Controller
@ComponentScan(basePackages = "com.cts.EventManagementSystem.service")
public class UserRegistrationController {
 
    @Autowired
    private UserRegistrationService userService;
 
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new UserRegistration());
        return "register";
    }
 
    @PostMapping("/register")
    public String processRegister(@ModelAttribute UserRegistration user) {
    	userService.save(user);
        return "redirect:/login";
    }
 
    @GetMapping("/login")
    public String loginForm() {
       return "login";
    }

 
    @GetMapping("/home")
    public String home() {
        return "home";
    }
}