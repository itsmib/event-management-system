package com.cts.EventManagementSystem.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.cts.EventManagementSystem.model.Event;
import com.cts.EventManagementSystem.model.Notification;
import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.service.NotificationService;
import com.cts.EventManagementSystem.service.UserRegistrationService;

@Controller
@RequestMapping("/admin")
public class NotificationController {
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private UserRegistrationService userService;

	@PostMapping("/send-reminders/{eventId}")
	public String sendReminders(@PathVariable Long eventId) {
		notificationService.sendReminderEmails(eventId);
		return "redirect:/admin/my-events?success=remindersent";
	}
	
	@GetMapping("/reminder")
	public String viewNotification(Model model, Principal principal) {
		// Get currently logged-in admin's email
		String email = principal.getName();
		// Find the admin user
		UserRegistration organizer = userService.findByEmail(email);
		// Fetch events created by this admin
		List<Notification> notification = notificationService.findAll();
		// Pass the list to the view
		model.addAttribute("myNotification", notification);
		return "admin/reminder"; // Corresponds to admin_my_events.html
	}
}