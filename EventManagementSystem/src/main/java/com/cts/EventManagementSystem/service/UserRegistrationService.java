package com.cts.EventManagementSystem.service;

import com.cts.EventManagementSystem.model.UserRegistration;

public interface UserRegistrationService {
    void save(UserRegistration user);
    UserRegistration findByEmail(String email);
}
