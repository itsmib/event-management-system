package com.cts.EventManagementSystem.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.Id;

@Component
public class UserRegistration {

	@Id
	private int userId;
}
