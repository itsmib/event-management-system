package com.cts.EventManagementSystem.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

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
        if (event == null || event.getImage() == null) {
            return ResponseEntity.notFound().build();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_JPEG);
        return new ResponseEntity<>(event.getImage(), headers, HttpStatus.OK);
    }

    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {
        String role = auth.getAuthorities().iterator().next().getAuthority();
        if ("ROLE_ADMIN".equals(role)) {
            return "admin_dashboard";
        }
        if ("ROLE_USER".equals(role)) {
            return "user_dashboard";
        }
        return "home";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(Model model, Principal principal) {
        UserRegistration user = userService.findByEmail(principal.getName());
        model.addAttribute("username", user.getName());
        return "user/user_dashboard";
    }

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAdminDashboard(Model model, Principal principal) {
        List<Event> events = eventService.findByOrganizerEmail(principal.getName());
        List<UserRegistration> users = userService.findByRole("USER");

        int confirmedBookings = events.stream()
                .mapToInt(event -> bookingService.findByEvent(event).size())
                .sum();

        model.addAttribute("totalEvent", events.size());
        model.addAttribute("totalUsers", users.size());
        model.addAttribute("confirmedBookings", confirmedBookings);
        return "admin/admin_dashboard";
    }

    @GetMapping("/admin/create-event")
    @PreAuthorize("hasRole('ADMIN')")
    public String showCreateEventForm(Model model) {
        model.addAttribute("event", new Event());
        return "admin/create_event";
    }

    @GetMapping("/admin/my-events")
    @PreAuthorize("hasRole('ADMIN')")
    public String viewMyEvents(Model model, Principal principal) {
        UserRegistration organizer = userService.findByEmail(principal.getName());
        model.addAttribute("myEvents", eventService.findByOrganizer(organizer));
        return "admin/manage_events";
    }

    @GetMapping("/admin/edit-event/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditEventForm(@PathVariable Long id, Model model, Principal principal) {
        Event event = eventService.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid event ID"));

        UserRegistration admin = userService.findByEmail(principal.getName());
        if (!event.getOrganizer().equals(admin)) {
            throw new SecurityException("Not authorized");
        }

        model.addAttribute("event", event);
        return "admin/edit_event";
    }
}
