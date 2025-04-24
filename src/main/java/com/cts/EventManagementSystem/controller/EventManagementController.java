package com.cts.EventManagementSystem.controller;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.cts.EventManagementSystem.model.EventManagement;
import com.cts.EventManagementSystem.repository.EventManagementRepository;
import com.cts.EventManagementSystem.service.EventManagementService;


@Controller
@RequestMapping("/events")
public class EventManagementController {
	
	
	@Autowired
	EventManagementRepository eventRepo;
	
	@PostMapping("/save")
	public String saveEvent(@ModelAttribute("event") EventManagement event, Model model) {
		eventRepo.save(event);
		return "redirect:/events";
	}
	

    @Autowired
    private EventManagementService eventService;

    @GetMapping
    public String getAllEvents(Model model) {
        List<EventManagement> events = eventService.getAllEvents();
        model.addAttribute("events", events);
        return "events/list";
    }

    @GetMapping("/{id}")
    public String getEventById(@PathVariable Long id, Model model) {
        Optional<EventManagement> event = eventService.getEventById(id);
        if (event.isPresent()) {
            model.addAttribute("event", event.get());
            return "events/details";
        } else {
            return "error"; // Or redirect to a not-found page
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new EventManagement());
        return "events/new";
    }

    @PostMapping
    public String createEvent(@ModelAttribute EventManagement event) {
        eventService.createEvent(event);
        return "redirect:/events";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<EventManagement> event = eventService.getEventById(id);
        if (event.isPresent()) {
            model.addAttribute("event", event.get());
            return "events/edit";
        } else {
            return "error";
        }
    }

    @PostMapping("/edit/{id}")
    public String updateEvent(@PathVariable Long id, @ModelAttribute EventManagement event) {
        eventService.updateEvent(id, event);
        return "redirect:/events";
    }

    @GetMapping("/delete/{id}")
    public String deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return "redirect:/events";
    }

    @GetMapping("/filter")
    public String filterEvents(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "date", required = false) String date,
            Model model) {

        List<EventManagement> events = eventService.getAllEvents(); // Default to all events

        if (category != null && !category.isEmpty()) {
            events = eventService.getEventsByCategory(category);
        } else if (location != null && !location.isEmpty()) {
            events = eventService.getEventsByLocation(location);
        } else if (date != null && !date.isEmpty()) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date eventDate = dateFormat.parse(date);
                events = eventService.getEventsByDate(eventDate);
            } catch (ParseException e) {
                // Handle date parsing error
                model.addAttribute("error", "Invalid date format. Please use yyyy-MM-dd.");
                return "events/list";
            }
        }

        model.addAttribute("events", events);
        return "events/list";
    }
}