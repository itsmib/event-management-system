package com.cts.EventManagementSystem.controller;

import java.io.IOException;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

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
        return eventService.findByEventDateAfterOrderByEventDateAsc(LocalDate.now().minusDays(1))
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
            @RequestParam @NotBlank @Size(max=100) String name,
            @RequestParam @NotBlank @Size(max=60) String category,
            @RequestParam @NotBlank @Size(max=200) String location,
            @RequestParam LocalDate eventDate,
            @RequestParam LocalTime eventTime,
            @RequestParam(required = false) @Size(max=255) String description,
            @RequestParam @PositiveOrZero int totalTickets,
            @RequestParam(required = false) MultipartFile imageFile,
            Principal principal) throws IOException {

        Event event = new Event();
        applyEditableFields(event, name, category, location, eventDate, eventTime, description);
        event.setTotalTickets(totalTickets);

        if (imageFile != null && !imageFile.isEmpty()) {
            event.setImage(readImage(imageFile));
        }

        event.setOrganizer(userService.findByEmail(principal.getName()));
        Event saved = eventService.save(event);
        return ResponseEntity.status(HttpStatus.CREATED).body(EventResponse.from(saved));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EventResponse> updateEvent(
            @PathVariable Long id,
            @RequestParam @NotBlank @Size(max=100) String name,
            @RequestParam @NotBlank @Size(max=60) String category,
            @RequestParam @NotBlank @Size(max=200) String location,
            @RequestParam LocalDate eventDate,
            @RequestParam LocalTime eventTime,
            @RequestParam(required = false) @Size(max=255) String description,
            @RequestParam(required = false) MultipartFile imageFile,
            Principal principal) throws IOException {

        Event existing = eventService.findById(id).orElse(null);
        if (existing == null) {
            return ResponseEntity.notFound().build();
        }

        UserRegistration admin = userService.findByEmail(principal.getName());
        if (!owns(existing, admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        applyEditableFields(existing, name, category, location, eventDate, eventTime, description);
        if (imageFile != null && !imageFile.isEmpty()) {
            existing.setImage(readImage(imageFile));
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
        if (!owns(event, admin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        eventService.delete(event);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> image(@PathVariable Long id) {
        Event event = eventService.findById(id).orElse(null);
        if (event == null || event.getImage() == null || event.getImage().length == 0) return ResponseEntity.notFound().build();
        byte[] data = event.getImage();
        String type = data.length >= 8 && data[0] == (byte)0x89 ? "image/png" : "image/jpeg";
        return ResponseEntity.ok().header("Content-Type", type).header("X-Content-Type-Options", "nosniff").body(data);
    }

    private boolean owns(Event event, UserRegistration user) {
        return event.getOrganizer() != null && user != null && user.getUserId() != null
            && user.getUserId().equals(event.getOrganizer().getUserId());
    }

    private byte[] readImage(MultipartFile file) throws IOException {
        if (file.getSize() > 5 * 1024 * 1024) throw new ResponseStatusException(HttpStatus.PAYLOAD_TOO_LARGE, "Image must be smaller than 5 MB.");
        byte[] bytes = file.getBytes();
        if (javax.imageio.ImageIO.read(new java.io.ByteArrayInputStream(bytes)) == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a valid PNG or JPEG image.");
        boolean png = bytes.length >= 8 && bytes[0] == (byte)0x89 && bytes[1] == 0x50 && bytes[2] == 0x4e && bytes[3] == 0x47;
        boolean jpeg = bytes.length >= 3 && bytes[0] == (byte)0xff && bytes[1] == (byte)0xd8 && bytes[2] == (byte)0xff;
        if (!png && !jpeg) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Upload a PNG or JPEG image.");
        return bytes;
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
            String organizerName, boolean hasImage) {
        static EventResponse from(Event event) {
            String organizerName = event.getOrganizer() != null ? event.getOrganizer().getName() : null;
            return new EventResponse(event.getEventId(), event.getName(), event.getCategory(), event.getLocation(),
                    event.getEventDate(), event.getEventTime(), event.getDescription(), event.getTotalTickets(), organizerName, event.getImage() != null && event.getImage().length > 0);
        }
    }
}
