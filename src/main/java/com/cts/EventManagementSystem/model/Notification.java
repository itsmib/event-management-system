package com.cts.EventManagementSystem.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String message;
	private LocalDateTime sentAt;
	@ManyToOne
	@JoinColumn(name = "user_id")
	private UserRegistration user;
	@ManyToOne
	@JoinColumn(name = "event_id")
	private Event event;
	private String type; // For example, 'Booking Confirmation', 'Event Reminder', etc.
	// Getters and setters

	public Notification(String message, UserRegistration user, Event event, String type) {
		this.message = message;
		this.user = user;
		this.event = event;
		this.type = type;
		this.sentAt = LocalDateTime.now();
	}
}