package com.cts.EventManagementSystem.controller;

import com.cts.EventManagementSystem.model.Booking;

import com.cts.EventManagementSystem.model.Event;

import com.cts.EventManagementSystem.model.UserRegistration;

import com.cts.EventManagementSystem.repository.BookingRepository;

import com.cts.EventManagementSystem.repository.EventRepository;

import com.cts.EventManagementSystem.repository.UserRegistrationRepository;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.util.List;

import java.util.stream.Collectors;

@Controller

@RequestMapping("/user")

public class BookingController {

	@Autowired
	private EventRepository eventRepo;

	@Autowired
	private BookingRepository bookingRepo;

	@Autowired
	private UserRegistrationRepository userRepo;
	

	@GetMapping("/event-details/{eventId}")
	public String viewEventDetails(@PathVariable Long eventId, Model model) {
		// Fetch the event using the ID
		Event event = eventRepo.findById(eventId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid event ID"));
		// Add event details to the model
		model.addAttribute("event", event);
		return "user/event_details"; // Show event details page
	}
	
	@PostMapping("/book-event/{eventId}")
	public String bookEvent(@PathVariable Long eventId, Principal principal) {
	   // Fetch the event using the ID
	   Event event = eventRepo.findById(eventId)
	           .orElseThrow(() -> new IllegalArgumentException("Invalid event ID"));
	   // Fetch the user who is booking the event
	   UserRegistration user = userRepo.findByEmail(principal.getName());
	   // Create a new booking (you may want to create a Booking entity for this)
	   Booking booking = new Booking();
	   booking.setEvent(event);
	   booking.setUser(user);
	   booking.setBookingDate(LocalDate.now()); // Store today's date as booking date
	   // Save the booking
	   bookingRepo.save(booking);
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