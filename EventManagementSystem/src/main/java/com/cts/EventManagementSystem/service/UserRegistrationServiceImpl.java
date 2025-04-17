package com.cts.EventManagementSystem.service;
 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.repository.UserRegistrationRepository;
 
@Service
public class UserRegistrationServiceImpl implements UserRegistrationService {
 
    @Autowired
    private UserRegistrationRepository repo;
 
    @Override
    public void save(UserRegistration user) {
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setRole("USER");
        repo.save(user);
    }
 
    @Override
    public UserRegistration findByEmail(String email) {
        return repo.findByEmail(email);
    }
    
}