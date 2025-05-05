package com.cts.EventManagementSystem.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
public class Booking {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long bookingId;
	@ManyToOne
	@JoinColumn(name = "event_id", nullable = false)
	private Event event;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private UserRegistration user;
	private LocalDate bookingDate;
	
	private int totalBooking;
	
	

	
	
	public Booking() {
	}

	public Booking(Event event, UserRegistration user, LocalDate bookingDate, int totalBooking) {
		super();
		this.event = event;
		this.user = user;
		this.bookingDate = bookingDate;
		this.totalBooking = totalBooking;
	}

	public int getTotalBooking() {
		return totalBooking;
	}

	public void setTotalBooking(int totalBooking) {
		this.totalBooking = totalBooking;
	}

	public Long getBookingId() {
		return bookingId;
	}

	public void setBookingId(Long bookingId) {
		this.bookingId = bookingId;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public UserRegistration getUser() {
		return user;
	}

	public void setUser(UserRegistration user) {
		this.user = user;
	}

	public LocalDate getBookingDate() {
		return bookingDate;
	}

	public void setBookingDate(LocalDate bookingDate) {
		this.bookingDate = bookingDate;
	}
	

	public Booking(Long bookingId, Event event, UserRegistration user, LocalDate bookingDate) {
		super();
		this.bookingId = bookingId;
		this.event = event;
		this.user = user;
		this.bookingDate = bookingDate;
	}
	
	

}