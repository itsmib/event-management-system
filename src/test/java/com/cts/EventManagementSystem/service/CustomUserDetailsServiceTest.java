package com.cts.EventManagementSystem.service;

import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.repository.UserRegistrationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class CustomUserDetailsServiceTest {

    @Mock
    private UserRegistrationRepository repo;

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Test
    void loadUserByUsername_userFound() {
        // Arrange
        String email = "test@example.com";
        UserRegistration user = new UserRegistration();
        user.setEmail(email);
        user.setPassword("password");
        user.setRole("USER");

        Mockito.when(repo.findByEmail(email)).thenReturn(user);

        // Act
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        // Assert
        assertNotNull(userDetails);
        assertEquals(email, userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertEquals(1, userDetails.getAuthorities().size());
        assertEquals("ROLE_USER", userDetails.getAuthorities().iterator().next().getAuthority());

        // Verify that the repository method was called
        Mockito.verify(repo).findByEmail(email);
    }

    @Test
    void loadUserByUsername_userNotFound() {
        // Arrange
        String email = "nonexistent@example.com";
        Mockito.when(repo.findByEmail(email)).thenReturn(null);

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        // Verify that the repository method was called
        Mockito.verify(repo).findByEmail(email);
    }
}