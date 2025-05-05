package com.cts.EventManagementSystem.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cts.EventManagementSystem.model.Booking;
import com.cts.EventManagementSystem.model.Event;
import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.repository.BookingRepository;

@Service
public class BookingServiceImpl implements BookingService {
	
	@Autowired
	BookingRepository bookingRepo;

	@Override
	public void save(Booking booking) {
		bookingRepo.save(booking);
	}

	@Override
	public List<Booking> findByUser(UserRegistration user) {
		return bookingRepo.findByUser(user);
	}

	@Override
	public Optional<Booking> findById(Long bookingId) {
		return bookingRepo.findById(bookingId);
	}

	@Override
	public void delete(Booking booking) {
		bookingRepo.delete(booking);
	}

	@Override
	public List<Booking> findByEvent(Event event) {
		return bookingRepo.findByEvent(event);
	}

}
