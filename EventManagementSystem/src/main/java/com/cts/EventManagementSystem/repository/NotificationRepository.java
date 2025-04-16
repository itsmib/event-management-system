package com.cts.EventManagementSystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.cts.EventManagementSystem.model.Notification;

public interface NotificationRepository extends CrudRepository<Notification, Integer>, PagingAndSortingRepository<Notification, Integer> {

}
