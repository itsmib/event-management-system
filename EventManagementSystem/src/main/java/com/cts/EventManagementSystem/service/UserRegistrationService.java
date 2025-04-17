package com.cts.EventManagementSystem.service;

import org.springframework.stereotype.Service;

import com.cts.EventManagementSystem.model.UserRegistration;

public interface UserRegistrationService {
    void save(UserRegistration user);
    UserRegistration findByEmail(String email);
}
