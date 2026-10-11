package com.cts.EventManagementSystem.controller;

import com.cts.EventManagementSystem.model.Booking;
import com.cts.EventManagementSystem.repository.*;
import com.cts.EventManagementSystem.service.BookingWorkflow;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@PreAuthorize("hasRole('USER')")
public class BookingRestController {
    private final BookingRepository bookings;
    private final UserRegistrationRepository users;
    private final BookingWorkflow workflow;
    public BookingRestController(BookingRepository bookings, UserRegistrationRepository users, BookingWorkflow workflow) {
        this.bookings=bookings; this.users=users; this.workflow=workflow;
    }
    public record BookingRequest(@jakarta.validation.constraints.NotNull Long eventId, @Min(1) int ticketCount) {}
    public record BookingResponse(Long bookingId, LocalDate bookingDate, int ticketCount, EventRestController.EventResponse event) {
        static BookingResponse from(Booking b) { return new BookingResponse(b.getBookingId(), b.getBookingDate(), b.getTotalBooking(), EventRestController.EventResponse.from(b.getEvent())); }
    }
    @GetMapping
    public List<BookingResponse> list(Principal principal) { return bookings.findByUser(users.findByEmail(principal.getName())).stream().map(BookingResponse::from).toList(); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public BookingResponse book(@Valid @RequestBody BookingRequest body, Principal principal) { return BookingResponse.from(workflow.book(body.eventId(), body.ticketCount(), principal.getName())); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancel(@PathVariable Long id, Principal principal) { workflow.cancel(id, principal.getName()); }
}
