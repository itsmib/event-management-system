package com.cts.EventManagementSystem.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "notification")
public class Notification {
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "notification_seq")
	@SequenceGenerator(name = "notification_seq", sequenceName = "NOTIFICATION_SEQ", allocationSize = 1) // or 50, to match the DB
	private Long notificationId;
	private Long userId;
	private Long eventId;
	private String message;
	private LocalDateTime sentTimestamp;

	public Notification() {
	}

	// Constructor with fields (optional, for easier object creation)
	public Notification(Long userId, Long eventId, String message) {
		this.userId = userId;
		this.eventId = eventId;
		this.message = message;
		this.sentTimestamp = LocalDateTime.now(); // Set timestamp on creation
	}

	public Long getNotificationId() {
		return notificationId;
	}

	public void setNotificationId(Long notificationId) {
		this.notificationId = notificationId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LocalDateTime getSentTimestamp() {
		return sentTimestamp;
	}

	public void setSentTimestamp(LocalDateTime sentTimestamp) {
		this.sentTimestamp = sentTimestamp;
	}

}
