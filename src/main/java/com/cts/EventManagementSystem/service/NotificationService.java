package com.cts.EventManagementSystem.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.cts.EventManagementSystem.model.Notification;

@Service
public interface NotificationService {
	void sendReminderEmails(Long eventId);
	List<Notification> findAll();
}
