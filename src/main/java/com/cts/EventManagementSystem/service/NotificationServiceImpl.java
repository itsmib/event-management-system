package com.cts.EventManagementSystem.service;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.cts.EventManagementSystem.model.Booking;
import com.cts.EventManagementSystem.model.Event;
import com.cts.EventManagementSystem.model.Notification;
import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.repository.BookingRepository;
import com.cts.EventManagementSystem.repository.EventRepository;
import com.cts.EventManagementSystem.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private NotificationRepository notificationRepository;
	@Autowired
	private JavaMailSender mailSender;

	@Override
	@Transactional
	public void sendReminderEmails(Long eventId) {
		Event event = eventRepository.findById(eventId).orElseThrow(() -> new RuntimeException("Event not found"));
		List<Booking> bookings = bookingRepository.findByEvent(event);
		for (Booking booking : bookings) {
			UserRegistration user = booking.getUser();
			String subject = "Reminder: " + event.getName();
			String message = "Dear " + user.getName() + ",\n\n" + "This is a reminder for the event: " + event.getName()
					+ "\nScheduled Date: " + event.getEventDate() + "\n\nThanks,\nEvent Management Team";
			sendEmail(user.getEmail(), subject, message);
			Notification notification = new Notification();
			notification.setUser(user);
			notification.setEvent(event);
			notification.setMessage("Reminder sent for event: " + event.getName());
			notification.setSentAt(LocalDateTime.now());
			notificationRepository.save(notification);
		}
	}

	private void sendEmail(String to, String subject, String text) {
		SimpleMailMessage mail = new SimpleMailMessage();
		mail.setTo(to);
		mail.setSubject(subject);
		mail.setText(text);
		mailSender.send(mail);
	}

	@Override
	public List<Notification> findAll() {
		return notificationRepository.findAll();
	}
}