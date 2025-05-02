package com.cts.EventManagementSystem.controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.stereotype.Controller;

@Controller
public class HomeController {

    private static final Logger logger = LoggerFactory.getLogger(HomeController.class);

    @GetMapping("/")
    public String home(HttpSession session) {
        logger.info("Accessing home page");

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            logger.info("User is authenticated: {}", auth.getName());
        } else {
            logger.info("User is NOT authenticated");
        }

        logger.info("Session ID: {}", session.getId()); // Log session ID

        return "home"; // Return the name of your Thymeleaf template
    }
    
    @GetMapping("/about")
    public String getAbout() {
    	return "about";
    }

    // ... your other controllers
}