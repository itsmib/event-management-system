package com.cts.EventManagementSystem.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

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
	@GetMapping("/logout")
	public String logout() {
		return "home";
	}

	@Autowired
	private JavaMailSender mailSender;
	private final Map<String, String> otpStorage = new HashMap<>();

	// --- Existing methods (register, login, home) ---
	@GetMapping("/forgot-password")
	public String showForgotForm(Model model) {
		model.addAttribute("email", "");
		return "forgot-password";
	}

	@PostMapping("/send-otp")
	public String sendOtp(@RequestParam String email, Model model) {
		UserRegistration user = userService.findByEmail(email);
		if (user == null) {
			model.addAttribute("error", "Email not registered");
			return "forgot-password";
		}
		String otp = String.valueOf(new Random().nextInt(900000) + 100000);
		otpStorage.put(email, otp);
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(email);
		message.setSubject("OTP for Password Reset");
		message.setText("Your OTP for password reset is: " + otp);
		mailSender.send(message);
		model.addAttribute("email", email);
		return "enter-otp";
	}

	@PostMapping("/verify-otp")
	public String verifyOtp(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword,
			Model model) {
		String storedOtp = otpStorage.get(email);
		if (storedOtp != null && storedOtp.equals(otp)) {
			UserRegistration user = userService.findByEmail(email);
			user.setPassword(newPassword);
			userService.save(user);
			otpStorage.remove(email);
			return "redirect:/login?reset";
		} else {
			model.addAttribute("email", email);
			model.addAttribute("error", "Invalid OTP");
			return "enter-otp";
		}
	}
	
	@GetMapping("/admin/edit_profile")
	@PreAuthorize("hasRole('ADMIN')")
	public String edit_profile(Principal principal,Model model) {
		UserRegistration user = userService.findByEmail(principal.getName());
		model.addAttribute("user",user);
		return "admin/edit_profile";
	}
	
	@PostMapping("/admin/update_profile")
	@PreAuthorize("hasRole('ADMIN')")
	public String processUpdate(@ModelAttribute("user") UserRegistration usersubmitted) {
		userService.save(usersubmitted);
		return "redirect:/admin/dashboard";
	}
	
	@GetMapping("/user/edit_profile")
	@PreAuthorize("hasRole('USER')")
	public String user_edit_profile(Principal principal,Model model) {
		UserRegistration user = userService.findByEmail(principal.getName());
		model.addAttribute("user",user);
		return "user/edit_profile";
	}
	
	@PostMapping("/user/update_profile")
	@PreAuthorize("hasRole('USER')")
	public String processUpdateUser(@ModelAttribute("user") UserRegistration usersubmitted) {
		userService.save(usersubmitted);
		return "redirect:/user/dashboard";
	}

}