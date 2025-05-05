package com.cts.EventManagementSystem.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.EventManagementSystem.model.Event;
import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.repository.EventRepository;

@Service
public class EventServiceImpl implements EventService {
	
	@Autowired
	EventRepository repo;

	@Override
	public List<Event> findByEventDateAfterOrderByEventDateAsc(LocalDate today) {
		return repo.findByEventDateAfterOrderByEventDateAsc(today);
	}

	@Override
	public Optional<Event> findById(Long Id) {
		return repo.findByEventId(Id);
	}

	@Override
	public List<Event> findByOrganizerEmail(String email) {
		return repo.findByOrganizerEmail(email);
	}

	@Override
	public void save(Event event) {
		repo.save(event);
	}

	@Override
	public List<Event> findByOrganizer(UserRegistration user) {
		return repo.findByOrganizer(user);
	}

	@Override
	public void delete(Event event) {
		repo.delete(event);
	}

}
