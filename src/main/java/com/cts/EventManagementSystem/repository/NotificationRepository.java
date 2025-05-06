package com.cts.EventManagementSystem.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cts.EventManagementSystem.model.Notification;
import com.cts.EventManagementSystem.model.UserRegistration;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
	List<Notification> findByUser(UserRegistration user);
}