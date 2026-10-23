package com.cts.EventManagementSystem.controller;

import com.cts.EventManagementSystem.repository.*;
import com.cts.EventManagementSystem.service.NotificationService;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRestController {
    private final EventRepository events;
    private final BookingRepository bookings;
    private final NotificationRepository notifications;
    private final NotificationService reminders;
    public AdminRestController(EventRepository events, BookingRepository bookings, NotificationRepository notifications, NotificationService reminders) {
        this.events=events; this.bookings=bookings; this.notifications=notifications; this.reminders=reminders;
    }
    @GetMapping("/events")
    public List<EventRestController.EventResponse> events(Principal p) { return events.findByOrganizerEmail(p.getName()).stream().map(EventRestController.EventResponse::from).toList(); }
    @GetMapping("/summary")
    public Map<String,Integer> summary(Principal p) {
        var owned = events.findByOrganizerEmail(p.getName());
        int count=0, tickets=0;
        for (var event:owned) for(var booking:bookings.findByEvent(event)) { count++; tickets+=booking.getTotalBooking(); }
        return Map.of("events", owned.size(), "bookings", count, "tickets", tickets);
    }
    @PostMapping("/events/{id}/reminders") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void remind(@PathVariable Long id, Principal p) {
        var event=events.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        if(event.getOrganizer()==null || !event.getOrganizer().getEmail().equals(p.getName())) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not own this event.");
        reminders.sendReminderEmails(id);
    }
    public record Reminder(Long id, String eventName, String message, LocalDateTime sentAt) {}
    @GetMapping("/reminders")
    public List<Reminder> reminders(Principal p) { return notifications.findByEventOrganizerEmailOrderBySentAtDesc(p.getName()).stream().map(n -> new Reminder(n.getId(), n.getEvent().getName(), n.getMessage(), n.getSentAt())).toList(); }
}
