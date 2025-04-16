package com.cts.EventManagementSystem.model;

import org.springframework.stereotype.Component;

import jakarta.persistence.Id;

@Component
public class Notification {
	@Id
	private int userId;

}
