package com.cts.EventManagementSystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface TicketBookingRepository extends CrudRepository<T, ID>, PagingAndSortingRepository<T, ID> {

}
