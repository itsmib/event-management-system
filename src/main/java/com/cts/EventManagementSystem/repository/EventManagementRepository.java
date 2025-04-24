package com.cts.EventManagementSystem.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.cts.EventManagementSystem.model.EventManagement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;

@Repository
public interface EventManagementRepository extends JpaRepository<EventManagement, Long> {

    List<EventManagement> findByCategory(String category);

    List<EventManagement> findByDate(Date date);

    List<EventManagement> findByLocation(String location);

    List<EventManagement> findByDateBetween(Date startDate, Date endDate);
}