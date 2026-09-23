package com.cts.EventManagementSystem.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cts.EventManagementSystem.model.Event;
import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.service.EventService;
import com.cts.EventManagementSystem.service.UserRegistrationService;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

    private final EventService eventService;
    private final UserRegistrationService userService;

    public EventRestController(EventService eventService, UserRegistrationService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping("/upcoming")
    public List<EventResponse> getUpcomingEvents() {
        return eventService.findByEventDateAfterOrderByEventDateAsc(LocalDate.now())
                .stream().map(EventResponse::from).toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEvent(@PathVariable Long id) {
        return eventService.findById(id)
                .map(EventResponse::from)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> createEvent(
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam String location,
            @RequestParam LocalDate eventDate,
            @RequestParam LocalTime eventTime,
            @RequestParam(required = false) String description,
            @RequestParam int totalTickets,
            @RequestParam(required = false) MultipartFile imageFile,
            Principal principal) throws IOException {

        Event event = new Event();
        applyEditableFields(event, name, category, location, eventDate, eventTime, description);
        event.setTotalTickets(totalTickets);

        if (imageFile != null && !imageFile.isEmpty()) {
            event.setImage(imageFile.getBytes());
        }

        event.setOrganizer(userService.findByEmail(principal.getName()));
        Event saved = eventService.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.from(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @RequestParam String name,
            @RequestParam String category,
            @RequestParam String location,
            @RequestParam LocalDate eventDate,
            @RequestParam LocalTime eventTime,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) MultipartFile imageFile,
            Principal principal) throws IOException {

        Event existing = eventService.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        UserRegistration admin = userService.findByEmail(principal.getName());
        if (!existing.getOrganizer().equals(admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        applyEditableFields(existing, name, category, location, eventDate, eventTime, description);
        if (imageFile != null && !imageFile.isEmpty()) {
            existing.setImage(imageFile.getBytes());
        }

        return ResponseEntity.ok(EventResponse.from(eventService.save(existing)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id, Principal principal) {
        Event event = eventService.findById(id).orElse(null);
        if (event == null) {
            return ResponseEntity.notFound().build();
        }

        UserRegistration admin = userService.findByEmail(principal.getName());
        if (!event.getOrganizer().equals(admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        eventService.delete(event);
        return ResponseEntity.noContent().build();
    }

    private void applyEditableFields(Event event, String name, String category, String location,
            LocalDate eventDate, LocalTime eventTime, String description) {
        event.setName(name);
        event.setCategory(category);
        event.setLocation(location);
        event.setEventDate(eventDate);
        event.setEventTime(eventTime);
        event.setDescription(description);
    }

    public record EventResponse(Long eventId, String name, String category, String location,
            LocalDate eventDate, LocalTime eventTime, String description, int totalTickets,
            String organizerName) {
        static EventResponse from(Event event) {
            String organizerName = event.getOrganizer() != null ? event.getOrganizer().getName() : null;
            return new EventResponse(event.getEventId(), event.getName(), event.getCategory(), event.getLocation(),
                    event.getEventDate(), event.getEventTime(), event.getDescription(), event.getTotalTickets(), organizerName);
        }
    }
}
