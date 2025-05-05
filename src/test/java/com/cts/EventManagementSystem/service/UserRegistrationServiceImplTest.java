package com.cts.EventManagementSystem.service;

import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.repository.UserRegistrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserRegistrationServiceImplTest {

    @Mock
    private UserRegistrationRepository repo;

    @InjectMocks
    private UserRegistrationServiceImpl userRegistrationService;

    @Test
    void save_userSuccessfully() {
        // Arrange
        UserRegistration userToSave = new UserRegistration();
        userToSave.setEmail("test@example.com");
        userToSave.setPassword("plainPassword");
        userToSave.setRole("USER");

        // Act
        userRegistrationService.save(userToSave);

        // Assert
        ArgumentCaptor<UserRegistration> userCaptor = ArgumentCaptor.forClass(UserRegistration.class);
        verify(repo, times(1)).save(userCaptor.capture()); // Verify that repo.save was called once

        UserRegistration savedUser = userCaptor.getValue();
        assertEquals("test@example.com", savedUser.getEmail());
        assertTrue(new BCryptPasswordEncoder().matches("plainPassword", savedUser.getPassword())); // Verify password was encoded
        assertEquals("USER", savedUser.getRole());
    }

    @Test
    void findByEmail_userFound() {
        // Arrange
        String emailToFind = "existing@example.com";
        UserRegistration expectedUser = new UserRegistration();
        expectedUser.setEmail(emailToFind);
        expectedUser.setPassword("encodedPassword");
        expectedUser.setRole("ADMIN");

        Mockito.when(repo.findByEmail(emailToFind)).thenReturn(expectedUser);

        // Act
        UserRegistration foundUser = userRegistrationService.findByEmail(emailToFind);

        // Assert
        assertNotNull(foundUser);
        assertEquals(emailToFind, foundUser.getEmail());
        assertEquals("encodedPassword", foundUser.getPassword());
        assertEquals("ADMIN", foundUser.getRole());

        verify(repo, times(1)).findByEmail(emailToFind); // Verify that repo.findByEmail was called once
    }

    @Test
    void findByEmail_userNotFound() {
        // Arrange
        String emailToFind = "nonexistent@example.com";
        Mockito.when(repo.findByEmail(emailToFind)).thenReturn(null);

        // Act
        UserRegistration foundUser = userRegistrationService.findByEmail(emailToFind);

        // Assert
        assertNull(foundUser);

        verify(repo, times(1)).findByEmail(emailToFind); // Verify that repo.findByEmail was called once
    }
}