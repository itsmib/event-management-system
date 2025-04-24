package com.cts.EventManagementSystem.service;


import com.cts.EventManagementSystem.model.EventManagement;
import com.cts.EventManagementSystem.repository.EventManagementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.Date;

@Service
public class EventManagementService {

    @Autowired
    private EventManagementRepository eventManagementRepository;

    public List<EventManagement> getAllEvents() {
        return eventManagementRepository.findAll();
    }

    public Optional<EventManagement> getEventById(Long id) {
        return eventManagementRepository.findById(id);
    }

    public EventManagement createEvent(EventManagement event) {
        return eventManagementRepository.save(event);
    }

    public EventManagement updateEvent(Long id, EventManagement event) {
        Optional<EventManagement> eventOptional = eventManagementRepository.findById(id);
        if (eventOptional.isPresent()) {
        	EventManagement existingEvent = eventOptional.get();
            existingEvent.setName(event.getName());
            existingEvent.setCategory(event.getCategory());
            existingEvent.setLocation(event.getLocation());
            existingEvent.setDate(event.getDate());
            existingEvent.setOrganizerId(event.getOrganizerId());
            return eventManagementRepository.save(existingEvent);
        }
        return null; // Or throw an exception
    }

    public void deleteEvent(Long id) {
    	eventManagementRepository.deleteById(id);
    }

    public List<EventManagement> getEventsByCategory(String category) {
        return eventManagementRepository.findByCategory(category);
    }

    public List<EventManagement> getEventsByDate(Date date) {
        return eventManagementRepository.findByDate(date);
    }

    public List<EventManagement> getEventsByLocation(String location) {
        return eventManagementRepository.findByLocation(location);
    }

     public List<EventManagement> getEventsByDateRange(Date startDate, Date endDate) {
        return eventManagementRepository.findByDateBetween(startDate, endDate);
    }
}