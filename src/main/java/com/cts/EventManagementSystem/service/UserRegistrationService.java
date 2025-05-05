package com.cts.EventManagementSystem.service;

import java.util.List;

import com.cts.EventManagementSystem.model.UserRegistration;

public interface UserRegistrationService {
    void save(UserRegistration user);
    UserRegistration findByEmail(String email);
    List<UserRegistration> findByRole(String role);
}
