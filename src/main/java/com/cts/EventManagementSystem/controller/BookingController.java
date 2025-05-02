package com.cts.EventManagementSystem.controller;

import com.cts.EventManagementSystem.model.Booking;

import com.cts.EventManagementSystem.model.Event;

import com.cts.EventManagementSystem.model.UserRegistration;

import com.cts.EventManagementSystem.repository.BookingRepository;

import com.cts.EventManagementSystem.repository.EventRepository;

import com.cts.EventManagementSystem.repository.UserRegistrationRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Controller

@RequestMapping("/user")

public class BookingController {

	@Autowired
	JavaMailSender mailSender;

	@Autowired
	private EventRepository eventRepo;

	@Autowired
	private BookingRepository bookingRepo;

	@Autowired
	private UserRegistrationRepository userRepo;

	@GetMapping("/event-details/{eventId}")
	public String viewEventDetails(@PathVariable Long eventId, Model model, Principal principal) {
		// Fetch the event using the ID
		Event event = eventRepo.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Invalid event ID"));
		UserRegistration user = userRepo.findByEmail(principal.getName());
		model.addAttribute("user", user);
		// Add event details to the model
		model.addAttribute("event", event);
		return "user/event_details"; // Show event details page
	}

	@PostMapping("/book-event/{eventId}")
	public String bookEvent(@PathVariable Long eventId, Principal principal) {
		// Fetch the event using the ID
		Event event = eventRepo.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Invalid event ID"));
		// Fetch the user who is booking the event
		UserRegistration user = userRepo.findByEmail(principal.getName());
		UserRegistration admin = userRepo.findByEmail(event.getOrganizer().getEmail());
		// Create a new booking (you may want to create a Booking entity for this)

		Booking booking = new Booking();
		booking.setEvent(event);
		booking.setUser(user);
		booking.setBookingDate(LocalDate.now()); // Store today's date as booking date
		bookingRepo.save(booking);
		event.setTotalTickets(event.getTotalTickets() - 1);
		String emailContent = "Dear " + user.getName() + ",\n\n" +

				"Thank you for booking your spot at " + event.getName() + "!\n\n" +

				"We are excited to confirm your booking for the event. Below are the details of your booking:\n\n" +

				"Event Details:\n" +

				"- Event Name: " + event.getName() + "\n" +

				"- Date: " + event.getEventDate() + "\n" +

				"- Time: " + event.getEventTime() + "\n" +

				"- Venue: " + event.getLocation() + "\n" +

				"- Booking ID: " + booking.getBookingId() + "\n\n" +

				"We look forward to seeing you at the event! If you have any questions or need further assistance, feel free to reach out to us at "
				+ admin.getEmail() + " or call us at " + admin.getContactNumber() + ".\n\n" +

				"Thank you for choosing " + "evm" + ".\n\n" +

				"Best regards,\n" +

				admin.getName() + "\n";
		SimpleMailMessage message = new SimpleMailMessage();
		message.setTo(user.getEmail());
		message.setSubject("Event Booking Confirmation");
		message.setText(emailContent);
		mailSender.send(message);

		emailContent = "Hello " + admin.getName() + ",\n\n" + "Good news! A new booking has been made for your event: "
				+ event.getName() + ".\n\n" + "Booking Details:\n" + "- User: " + user.getName() + " ("
				+ user.getEmail() + ")\n" + "- Booking ID: " + booking.getBookingId() + "\n" + "- Event Date: "
				+ event.getEventId() + "\n" + "- Time: " + event.getEventTime() + "\n" + "- Venue: "
				+ event.getLocation() + "\n\n" + "You can view all bookings from your admin panel for more details.\n\n"
				+ "Regards,\n" + "evm Team";
		message.setTo(admin.getEmail());
		message.setSubject("New Booking Confirmed");
		message.setText(emailContent);
		mailSender.send(message);
		// Save the booking
		return "redirect:/user/events"; // Redirect back to the events list
	}

	@GetMapping("/bookings")
	public String viewMyBookings(Model model, Principal principal) {
		UserRegistration user = userRepo.findByEmail(principal.getName());
		List<Booking> bookings = bookingRepo.findByUser(user);
		model.addAttribute("bookings", bookings);
		return "user/my_bookings";
	}

	@PostMapping("/cancel/{id}")
	public String cancelBooking(@PathVariable("id") Long bookingId, Principal principal) {
		// Fetch the booking by ID
		Booking booking = bookingRepo.findById(bookingId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid booking ID: " + bookingId));
		// Ensure only the user who made the booking can cancel it
		String userEmail = principal.getName();
		if (!booking.getUser().getEmail().equals(userEmail)) {
			throw new SecurityException("You are not authorized to cancel this booking.");
		}
		// Delete the booking
		bookingRepo.delete(booking);
		// Redirect back to the bookings page
		return "redirect:/user/bookings";
	}

}