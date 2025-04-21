package com.cts.EventManagementSystem.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.EventManagementSystem.model.Notification;
import com.cts.EventManagementSystem.repository.NotificationRepository;

@Service
public class NotificationService {
	@Autowired
	private NotificationRepository notificationRepository;
	
	public void sendNotification(Long userId, Long eventId, String message) {
        Notification notification = new Notification(userId, eventId, message); // Create a new Notification object
        notificationRepository.save(notification); // Save it to the database
        System.out.println("Notification saved: " + notification.getMessage() + " for user " + userId + " and event " + eventId);
        // TODO: Implement actual notification sending (email, SMS, etc.)
    }
	 public List<Notification> getNotificationsForUser(Long userId) {
         return notificationRepository.findAll().stream() // Fetch all notifications
                 .filter(n -> n.getUserId().equals(userId)) // Filter by userId
                 .collect(Collectors.toList()); // Collect the results into a list
     }


}
