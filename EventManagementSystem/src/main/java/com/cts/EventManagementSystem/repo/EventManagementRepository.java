package com.cts.EventManagementSystem.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface EventManagementRepository extends CrudRepository<T, ID>, PagingAndSortingRepository<T, ID> {

}
