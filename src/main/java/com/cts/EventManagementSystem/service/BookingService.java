package com.cts.EventManagementSystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cts.EventManagementSystem.model.Booking;
import com.cts.EventManagementSystem.model.UserRegistration;

@Service
public interface BookingService {
	
	void save(Booking booking);
	
	List<Booking> findByUser(UserRegistration user);
	
	Optional<Booking> findById(Long bookingId);
	
	void delete(Booking booking);

}
