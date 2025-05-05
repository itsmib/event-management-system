package com.cts.EventManagementSystem.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cts.EventManagementSystem.model.Event;
import com.cts.EventManagementSystem.model.UserRegistration;

@Service
public interface EventService {

	List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDate today);
	Optional<Event> findById(Long id);
	List<Event> findByOrganizerEmail(String email);
	void save(Event event);
	List<Event> findByOrganizer(UserRegistration user);
	void delete(Event event);
	
}
