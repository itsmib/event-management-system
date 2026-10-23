package com.cts.EventManagementSystem.repository;
import com.cts.EventManagementSystem.model.Event;
import com.cts.EventManagementSystem.model.UserRegistration;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public interface EventRepository extends JpaRepository<Event, Long> {
   @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
   @org.springframework.data.jpa.repository.Query("select e from Event e where e.eventId = :id")
   Optional<Event> findForUpdate(@org.springframework.data.repository.query.Param("id") Long id);
   List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDate today);
   List<Event> findByOrganizerEmail(String email);
   List<Event> findByOrganizer(UserRegistration organizer);
   Optional<Event> findByEventId(Long eventId);
}