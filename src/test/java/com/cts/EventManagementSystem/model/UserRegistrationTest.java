package com.cts.EventManagementSystem.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UserRegistrationTest {

    @Test
    void testNoArgsConstructor() {
        UserRegistration user = new UserRegistration();
        assertNotNull(user);
    }

    @Test
    void testAllArgsConstructor() {
        Long userId = 1L;
        String name = "John Doe";
        String email = "john.doe@example.com";
        String password = "securePassword";
        String contactNumber = "1234567890";
        String role = "USER";
 
        UserRegistration user = new UserRegistration(userId, name, email, password, contactNumber, role);

        assertEquals(userId, user.getUserId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(contactNumber, user.getContactNumber());
        assertEquals(role, user.getRole());
    }

    @Test
    void testSettersAndGetters() {
        UserRegistration user = new UserRegistration();
        Long userId = 10L;
        String name = "Jane Smith";
        String email = "jane.smith@example.com";
        String password = "anotherPassword";
        String contactNumber = "9876543210";
        String role = "ADMIN";

        user.setUserId(userId);
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setContactNumber(contactNumber);
        user.setRole(role);

        assertEquals(userId, user.getUserId());
        assertEquals(name, user.getName());
        assertEquals(email, user.getEmail());
        assertEquals(password, user.getPassword());
        assertEquals(contactNumber, user.getContactNumber());
        assertEquals(role, user.getRole());
    }
}