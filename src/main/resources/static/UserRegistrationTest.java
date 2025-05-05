package com.cts.EventManagementSystem.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class UserRegistrationTest {
	
	@Test
	void userRegistrationTest() {
        UserRegistration user = new UserRegistration();
        user.setName("meghraj");
        
        
        assertEquals("meghraj", user.getName());
        
    }

}
