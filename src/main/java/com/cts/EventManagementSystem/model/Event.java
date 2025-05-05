package com.cts.EventManagementSystem.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
public class Event {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long eventId;
	private String name;
	private String category;
	private String location;
	private LocalDate eventDate;
	private String description;
	private LocalTime eventTime;
	@Lob
	private byte[] image;
	@ManyToOne
	@JoinColumn(name = "organizer_Id", referencedColumnName = "userId")
	private UserRegistration organizer;

	// Getters and Setters
	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public LocalDate getEventDate() {
		return eventDate;
	}

	public void setEventDate(LocalDate eventDate) {
		this.eventDate = eventDate;
	}

	public UserRegistration getOrganizer() {
		return organizer;
	}

	public void setOrganizer(UserRegistration organizer) {
		this.organizer = organizer;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public LocalTime getEventTime() {
		return eventTime;
	}

	public void setEventTime(LocalTime eventTime) {
		this.eventTime = eventTime;
	}

	public byte[] getImage() {
		return image;
	}

	public void setImage(byte[] image) {
		this.image = image;
	}
	
	

	public Event() {
		
	}

	public Event(Long eventId, String name, String category, String location, LocalDate eventDate, String description,
			LocalTime eventTime, byte[] image, UserRegistration organizer) {
		super();
		this.eventId = eventId;
		this.name = name;
		this.category = category;
		this.location = location;
		this.eventDate = eventDate;
		this.description = description;
		this.eventTime = eventTime;
		this.image = image;
		this.organizer = organizer;
	}
	

}