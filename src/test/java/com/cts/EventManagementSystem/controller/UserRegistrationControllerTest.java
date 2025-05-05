package com.cts.EventManagementSystem.controller;

import com.cts.EventManagementSystem.repository.UserRegistrationRepository;
import com.cts.EventManagementSystem.service.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(UserRegistrationController.class)
public class UserRegistrationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRegistrationService userService;

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private UserRegistrationRepository userRegistrationRepository;

    @Test
    void showRegisterForm_addsUserToModel() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/register"))
               .andExpect(MockMvcResultMatchers.status().isOk())
               .andExpect(MockMvcResultMatchers.model().attributeExists("user"));
    }
}