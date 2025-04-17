package com.cts.EventManagementSystem.repository;
 
import org.springframework.data.jpa.repository.JpaRepository;
import com.cts.EventManagementSystem.model.UserRegistration;
 
public interface UserRegistrationRepository extends JpaRepository<UserRegistration, Long> {
    UserRegistration findByEmail(String email);
}