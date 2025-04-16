package com.cts.EventManagementSystem.repo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import com.cts.EventManagementSystem.model.UserRegistration;

public interface UserRegistrationRepository extends CrudRepository<UserRegistration, Integer>, PagingAndSortingRepository<UserRegistration, Integer> {

}
