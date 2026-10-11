package com.cts.EventManagementSystem.service;

import com.cts.EventManagementSystem.model.*;
import com.cts.EventManagementSystem.repository.*;
import java.time.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.context.ApplicationEventPublisher;

@Service
public class BookingWorkflow {
    private final EventRepository events;
    private final BookingRepository bookings;
    private final UserRegistrationRepository users;
    private final ApplicationEventPublisher publisher;
    public BookingWorkflow(EventRepository events, BookingRepository bookings, UserRegistrationRepository users, ApplicationEventPublisher publisher) {
        this.events=events; this.bookings=bookings; this.users=users; this.publisher=publisher;
    }
    public record Confirmation(String attendeeEmail, String organizerEmail, String eventName, Long bookingId, int tickets) {}
    @Transactional
    public Booking book(Long eventId, int tickets, String email) {
        Event event = events.findForUpdate(eventId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Event not found."));
        if (tickets < 1 || tickets > event.getTotalTickets()) throw new ResponseStatusException(HttpStatus.CONFLICT, "Not enough tickets available.");
        if (!LocalDateTime.of(event.getEventDate(), event.getEventTime()).isAfter(LocalDateTime.now()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This event has already started.");
        var user = users.findByEmail(email);
        var booking = new Booking(event, user, LocalDate.now(), tickets);
        event.setTotalTickets(event.getTotalTickets() - tickets);
        bookings.save(booking);
        publisher.publishEvent(new Confirmation(user.getEmail(), event.getOrganizer().getEmail(), event.getName(), booking.getBookingId(), tickets));
        return booking;
    }
    @Transactional
    public void cancel(Long id, String email) {
        Booking initial = bookings.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found."));
        if (!initial.getUser().getEmail().equals(email)) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "This booking belongs to another user.");
        Event event = events.findForUpdate(initial.getEvent().getEventId()).orElseThrow();
        // The first read may have waited on another cancellation; lock and refresh the booking before restoring inventory.
        Booking booking = bookings.findLockedById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking already cancelled."));
        event.setTotalTickets(event.getTotalTickets() + booking.getTotalBooking());
        bookings.delete(booking);
    }
}
