package com.cts.EventManagementSystem.controller;

import com.cts.EventManagementSystem.config.CustomAuthenticationSuccessHandler;
import com.cts.EventManagementSystem.config.SecurityConfig;
import com.cts.EventManagementSystem.model.Event;
import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.service.CustomUserDetailsService;
import com.cts.EventManagementSystem.service.EventService;
import com.cts.EventManagementSystem.service.UserRegistrationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EventRestController.class)
@ContextConfiguration(classes = {EventRestController.class, SecurityConfig.class})
class EventRestControllerTest {
    @Autowired private MockMvc mvc;
    @MockBean private EventService events;
    @MockBean private UserRegistrationService users;
    @MockBean private CustomUserDetailsService details;
    @MockBean private CustomAuthenticationSuccessHandler handler;

    private UserRegistration setupOwner() {
        UserRegistration owner = new UserRegistration();
        owner.setUserId(1L);
        owner.setName("Owner");
        when(users.findByEmail("owner")).thenReturn(owner);
        when(events.save(any())).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            event.setEventId(1L);
            return event;
        });
        return owner;
    }

    private MockMultipartHttpServletRequestBuilder form(HttpMethod method, String url) {
        var request = multipart(method, url);
        request.with(user("owner").roles("ADMIN")).with(csrf())
                .param("name", "Test").param("category", "Music").param("location", "HK")
                .param("eventDate", "2026-12-01").param("eventTime", "14:30");
        return request;
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 10})
    void createsEventWithNonNegativeInventory(int tickets) throws Exception {
        setupOwner();
        mvc.perform(form(HttpMethod.POST, "/api/events").param("totalTickets", String.valueOf(tickets)))
                .andExpect(status().isCreated()).andExpect(jsonPath("$.totalTickets").value(tickets));
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, -5, Integer.MIN_VALUE})
    void rejectsNegativeInventoryWithoutSaving(int tickets) throws Exception {
        mvc.perform(form(HttpMethod.POST, "/api/events").param("totalTickets", String.valueOf(tickets)))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(events, users);
    }

    @Test
    void editPreservesInventoryAndImageWhenNoReplacementIsProvided() throws Exception {
        Event event = new Event();
        event.setEventId(1L);
        event.setOrganizer(setupOwner());
        event.setTotalTickets(7);
        byte[] image = {1, 2};
        event.setImage(image);
        when(events.findById(1L)).thenReturn(Optional.of(event));
        mvc.perform(form(HttpMethod.PUT, "/api/events/1").param("totalTickets", "999"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.totalTickets").value(7));
        assertArrayEquals(image, event.getImage());
        assertEquals("Test", event.getName());
    }

    @Test
    void ownerCanReplaceImage() throws Exception {
        Event event = new Event();
        event.setOrganizer(setupOwner());
        when(events.findById(1L)).thenReturn(Optional.of(event));
        byte[] replacement = {3, 4};
        mvc.perform(form(HttpMethod.PUT, "/api/events/1")
                .file(new MockMultipartFile("imageFile", "event.png", "image/png", replacement)))
                .andExpect(status().isOk());
        assertArrayEquals(replacement, event.getImage());
    }

    @Test
    void rejectsOtherOwnerMutations() throws Exception {
        setupOwner();
        Event event = new Event();
        event.setOrganizer(new UserRegistration());
        when(events.findById(1L)).thenReturn(Optional.of(event));
        mvc.perform(form(HttpMethod.PUT, "/api/events/1")).andExpect(status().isForbidden());
        mvc.perform(delete("/api/events/1").with(user("owner").roles("ADMIN")).with(csrf()))
                .andExpect(status().isForbidden());
        verify(events, never()).save(any());
        verify(events, never()).delete(any());
    }

    @Test
    void blocksUserMutation() throws Exception {
        mvc.perform(delete("/api/events/1").with(user("user").roles("USER")).with(csrf()))
                .andExpect(status().isForbidden());
        verifyNoInteractions(events);
    }

    @Test
    void blocksMissingCsrf() throws Exception {
        mvc.perform(delete("/api/events/1").with(user("owner").roles("ADMIN")))
                .andExpect(status().isForbidden());
        verifyNoInteractions(events);
    }

    @Test
    void missingEventReturns404() throws Exception {
        when(events.findById(1L)).thenReturn(Optional.empty());
        mvc.perform(delete("/api/events/1").with(user("owner").roles("ADMIN")).with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void ownerCanDelete() throws Exception {
        Event event = new Event();
        event.setOrganizer(setupOwner());
        when(events.findById(1L)).thenReturn(Optional.of(event));
        mvc.perform(delete("/api/events/1").with(user("owner").roles("ADMIN")).with(csrf()))
                .andExpect(status().isNoContent());
        verify(events).delete(event);
    }
}
