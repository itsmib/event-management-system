package com.cts.EventManagementSystem.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class BookingTest {

    @Test
    void testNoArgsConstructor() {
        Booking booking = new Booking();
        assertNotNull(booking);
    }

    @Test
    void testConstructorWithEventUserAndDate() {
        Event event = new Event();
        event.setEventId(1L);
        UserRegistration user = new UserRegistration();
        user.setUserId(2L);
        LocalDate bookingDate = LocalDate.now();
        int totalBooking = 20;

        Booking booking = new Booking(event, user, bookingDate,totalBooking);

        assertEquals(event, booking.getEvent());
        assertEquals(user, booking.getUser());
        assertEquals(bookingDate, booking.getBookingDate());
        assertEquals(20, booking.getTotalBooking());
     }

    @Test
    void testAllArgsConstructor() {
        Long bookingId = 3L;
        Event event = new Event();
        event.setEventId(4L);
        UserRegistration user = new UserRegistration();
        user.setUserId(5L);
        LocalDate bookingDate = LocalDate.of(2025, 5, 5);

        Booking booking = new Booking(bookingId, event, user, bookingDate);

        assertEquals(bookingId, booking.getBookingId());
        assertEquals(event, booking.getEvent());
        assertEquals(user, booking.getUser());
        assertEquals(bookingDate, booking.getBookingDate());
    }

    @Test
    void testSettersAndGetters() {
        Booking booking = new Booking();
        Long bookingId = 6L;
        Event event = new Event();
        event.setEventId(7L);
        UserRegistration user = new UserRegistration();
        user.setUserId(8L);
        LocalDate bookingDate = LocalDate.of(2025, 5, 6);

        booking.setBookingId(bookingId);
        booking.setEvent(event);
        booking.setUser(user);
        booking.setBookingDate(bookingDate);

        assertEquals(bookingId, booking.getBookingId());
        assertEquals(event, booking.getEvent());
        assertEquals(user, booking.getUser());
        assertEquals(bookingDate, booking.getBookingDate());
    }

    @Test
    void testEventRelationship() {
        Booking booking = new Booking();
        Event event = new Event();
        event.setEventId(9L);
        event.setName("Test Event");
        booking.setEvent(event);
        assertEquals(event, booking.getEvent());
        assertEquals(9L, booking.getEvent().getEventId());
        assertEquals("Test Event", booking.getEvent().getName());
    }

    @Test
    void testUserRelationship() {
        Booking booking = new Booking();
        UserRegistration user = new UserRegistration();
        user.setUserId(10L);
        user.setEmail("test@user.com");
        booking.setUser(user);
        assertEquals(user, booking.getUser());
        assertEquals(10L, booking.getUser().getUserId());
        assertEquals("test@user.com", booking.getUser().getEmail());
    }

    @Test
    void testDefaultValues() {
        Booking booking = new Booking();
        assertNull(booking.getBookingId());
        assertNull(booking.getEvent());
        assertNull(booking.getUser());
        assertNull(booking.getBookingDate());
    }
}