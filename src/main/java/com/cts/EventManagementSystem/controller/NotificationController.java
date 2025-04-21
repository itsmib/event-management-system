package com.cts.EventManagementSystem.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cts.EventManagementSystem.service.NotificationService;

@RestController
@RequestMapping("/api/notification")
public class NotificationController {
	@Autowired
	private NotificationService notificationService;
	
	@PostMapping("/send")
    public ResponseEntity<String> sendNewNotification(
            @RequestParam Long userId, // Get userId from request parameter
            @RequestParam Long eventId, // Get eventId from request parameter
            @RequestParam String message) { // Get message from request parameter
        notificationService.sendNotification(userId, eventId, message); // Call the service to send the notification
        return new ResponseEntity<>("Notification sent and saved.", HttpStatus.CREATED); // Return success response
    }
	@GetMapping("/user/{userId}")
    public ResponseEntity<List<com.cts.EventManagementSystem.model.Notification>> getNotificationsForUser(
            @PathVariable Long userId) { // Get userId from path variable
        List<com.cts.EventManagementSystem.model.Notification> notifications = notificationService.getNotificationsForUser(userId); // Call the service to get notifications
        return new ResponseEntity<>(notifications, HttpStatus.OK); // Return success response with the list of notifications
    }

}
