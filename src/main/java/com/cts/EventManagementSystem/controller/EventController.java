package com.cts.EventManagementSystem.controller;

import java.io.IOException;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import com.cts.EventManagementSystem.model.Event;
import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.service.BookingService;
import com.cts.EventManagementSystem.service.EventService;
import com.cts.EventManagementSystem.service.UserRegistrationService;

@Controller
public class EventController {
	@Autowired
	private EventService eventService;

	@Autowired
	private UserRegistrationService userService;

	@Autowired
	private BookingService bookingService;

	@GetMapping("/user/events")
	public String viewUpcomingEvents(Model model) {
		List<Event> events = eventService.findByEventDateAfterOrderByEventDateAsc(LocalDate.now());
		model.addAttribute("events", events);
		return "user/view_events";
	}

	@GetMapping("/event/image/{id}")
	@ResponseBody
	public ResponseEntity<byte[]> getImage(@PathVariable Long id) {
		Event event = eventService.findById(id).orElse(null);
		if (event != null && event.getImage() != null) {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.IMAGE_JPEG); // or PNG based on your input
			return new ResponseEntity<>(event.getImage(), headers, HttpStatus.OK);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping("/dashboard")
	public String dashboard(Authentication auth) {
		String role = auth.getAuthorities().iterator().next().getAuthority();
		if (role.equals("ROLE_ADMIN")) {
			return "admin_dashboard"; // admin_dashboard.html
		} else if (role.equals("ROLE_USER")) {
			return "user_dashboard"; // user_dashboard.html
		}
		return "home";
	}

	@GetMapping("/user/dashboard")
	public String userDashboard(Model model, Principal principal) {
		UserRegistration user = userService.findByEmail(principal.getName());
		model.addAttribute("username", user.getName());

		return "user/user_dashboard"; // This loads src/main/resources/templates/user/dashboard.html
	}

	@GetMapping("/admin/dashboard")
		@PreAuthorize("hasRole('ADMIN')")
		public String showAdminDashboard(Model model, Principal principal) {
			List<Event> event = eventService.findByOrganizerEmail(principal.getName());
			List<UserRegistration> user = userService.findByRole("USER");
			int bs = 0;
			for(Event ev : event) {
				bs = bs + bookingService.findByEvent(ev).size();
			}
			model.addAttribute("totalEvent", event.size());
			model.addAttribute("totalUsers", user.size());
			model.addAttribute("confirmedBookings", bs);
			return "admin/admin_dashboard"; // Renamed view
		}

	@GetMapping("/admin/create-event")
	@PreAuthorize("hasRole('ADMIN')")
	public String showCreateEventForm(Model model) {
		model.addAttribute("event", new Event());
		return "admin/create_event";
	}

	@PostMapping("/admin/save-event")
	@PreAuthorize("hasRole('ADMIN')")
	public String saveEvent(@RequestParam("imageFile") MultipartFile file, @ModelAttribute("event") Event event,
			Principal principal) throws IOException {
		if (!file.isEmpty()) {
			event.setImage(file.getBytes());
		}
		UserRegistration admin = userService.findByEmail(principal.getName());
		event.setOrganizer(admin);
		eventService.save(event);
		return "redirect:/admin/my-events";
	}

	@GetMapping("/admin/my-events")
	public String viewMyEvents(Model model, Principal principal) {
		// Get currently logged-in admin's email
		String email = principal.getName();
		// Find the admin user
		UserRegistration organizer = userService.findByEmail(email);
		// Fetch events created by this admin
		List<Event> myEvents = eventService.findByOrganizer(organizer);
		// Pass the list to the view
		model.addAttribute("myEvents", myEvents);
		return "admin/manage_events"; // Corresponds to admin_my_events.html
	}

	@GetMapping("/admin/edit-event/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String showEditEventForm(@PathVariable Long id, Model model, Principal principal) {
		Event event = eventService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid event ID"));
		model.addAttribute("event", event);
		return "admin/edit_event";
	}

	@PostMapping("/admin/edit-event/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String updateEvent(@PathVariable Long id, @ModelAttribute("event") Event submitted, Principal principal) {

		// 1. Load the existing event

		Event existing = eventService.findById(id)

				.orElseThrow(() -> new IllegalArgumentException("Invalid event ID"));

		// 2. Security check

		UserRegistration admin = userService.findByEmail(principal.getName());

		if (!existing.getOrganizer().equals(admin)) {

			throw new SecurityException("Not authorized");

		}

		// 3. Copy editable fields

		existing.setName(submitted.getName());

		existing.setCategory(submitted.getCategory());

		existing.setLocation(submitted.getLocation());

		existing.setEventDate(submitted.getEventDate());

		existing.setEventTime(submitted.getEventTime());

		existing.setDescription(submitted.getDescription());

		// 4. Save—organizer stays intact

		eventService.save(existing);

		return "redirect:/admin/my-events";

	}

	@PostMapping("/admin/delete-event/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	public String deleteEvent(@PathVariable Long id, Principal principal) {
		Event event = eventService.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid event ID"));
		UserRegistration admin = userService.findByEmail(principal.getName());
		if (!event.getOrganizer().equals(admin)) {
			throw new SecurityException("Not authorized");
		}
		eventService.delete(event);
		return "redirect:/admin/my-events";
	}
}