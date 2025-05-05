package com.cts.EventManagementSystem.model;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

public class EventTest {

    @Test
    void testNoArgsConstructor() {
        assertNotNull(new Event());
    }

    @Test
    void testAllArgsConstructor() {
        Long eventId = 1L;
        String name = "Tech Conference";
        String category = "Technology";
        String location = "Convention Center";
        LocalDate eventDate = LocalDate.of(2025, 6, 15);
        String description = "Annual tech conference";
        LocalTime eventTime = LocalTime.of(9, 0);
        byte[] image = new byte[]{1, 2, 3};
        UserRegistration organizer = new UserRegistration();
        organizer.setUserId(2L);

        Event event = new Event(eventId, name, category, location, eventDate, description, eventTime, image, organizer);

        assertEquals(eventId, event.getEventId());
        assertEquals(name, event.getName());
        assertEquals(category, event.getCategory());
        assertEquals(location, event.getLocation());
        assertEquals(eventDate, event.getEventDate());
        assertEquals(description, event.getDescription());
        assertEquals(eventTime, event.getEventTime());
        assertArrayEquals(image, event.getImage());
        assertEquals(organizer, event.getOrganizer());
    }

    @Test
    void testSettersAndGetters() {
        Event event = new Event();

        // Test eventId
        Long eventId = 2L;
        event.setEventId(eventId);
        assertEquals(eventId, event.getEventId());

        // Test name
        String name = "Music Festival";
        event.setName(name);
        assertEquals(name, event.getName());

        // Test category
        String category = "Music";
        event.setCategory(category);
        assertEquals(category, event.getCategory());

        // Test location
        String location = "City Park";
        event.setLocation(location);
        assertEquals(location, event.getLocation());

        // Test eventDate
        LocalDate eventDate = LocalDate.of(2025, 7, 20);
        event.setEventDate(eventDate);
        assertEquals(eventDate, event.getEventDate());

        // Test description
        String description = "Summer music festival";
        event.setDescription(description);
        assertEquals(description, event.getDescription());

        // Test eventTime
        LocalTime eventTime = LocalTime.of(14, 0);
        event.setEventTime(eventTime);
        assertEquals(eventTime, event.getEventTime());

        // Test image
        byte[] image = new byte[]{4, 5, 6, 7};
        event.setImage(image);
        assertArrayEquals(image, event.getImage());

        // Test organizer
        UserRegistration organizer = new UserRegistration();
        organizer.setUserId(3L);
        event.setOrganizer(organizer);
        assertEquals(organizer, event.getOrganizer());
    }

    @Test
    void testOrganizerRelationship() {
        Event event = new Event();
        UserRegistration organizer = new UserRegistration();
        organizer.setUserId(5L);
        organizer.setEmail("organizer@example.com");
        event.setOrganizer(organizer);
        assertEquals(organizer, event.getOrganizer());
        assertEquals(5L, event.getOrganizer().getUserId());
        assertEquals("organizer@example.com", event.getOrganizer().getEmail());
    }

    @Test
    void testDefaultValues() {
        Event event = new Event();
        assertNull(event.getEventId());
        assertNull(event.getName());
        assertNull(event.getCategory());
        assertNull(event.getLocation());
        assertNull(event.getEventDate());
        assertNull(event.getDescription());
        assertNull(event.getEventTime());
        assertNull(event.getImage());
        assertNull(event.getOrganizer());
    }
}