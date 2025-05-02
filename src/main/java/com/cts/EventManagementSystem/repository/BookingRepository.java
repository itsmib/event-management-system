package com.cts.EventManagementSystem.repository;
import com.cts.EventManagementSystem.model.Booking;
import com.cts.EventManagementSystem.model.Event;
import com.cts.EventManagementSystem.model.UserRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
public interface BookingRepository extends JpaRepository<Booking, Long> {
   Optional<Booking> findByEventAndUser(Event event, UserRegistration user);
   List<Booking> findByUser(UserRegistration user);
   List<Booking> findByEvent(Event event);
}