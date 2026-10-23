package com.cts.EventManagementSystem;

import com.cts.EventManagementSystem.model.*;
import com.cts.EventManagementSystem.repository.*;
import com.cts.EventManagementSystem.service.BookingWorkflow;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.mockito.ArgumentCaptor;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:api;MODE=Oracle;DB_CLOSE_DELAY=-1", "spring.datasource.driver-class-name=org.h2.Driver", "spring.datasource.username=sa", "spring.datasource.password=", "spring.jpa.hibernate.ddl-auto=create-drop"})
@AutoConfigureMockMvc
class ApiWorkflowIntegrationTest {
    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired UserRegistrationRepository users;
    @Autowired EventRepository events;
    @Autowired BookingRepository bookings;
    @Autowired NotificationRepository notifications;
    @Autowired BookingWorkflow workflow;
    @Autowired BCryptPasswordEncoder encoder;
    @MockBean JavaMailSender mail;
    UserRegistration owner, member;
    Event event;
    @BeforeEach void seed() {
        notifications.deleteAll(); bookings.deleteAll(); events.deleteAll(); users.deleteAll();
        owner=users.save(new UserRegistration(null,"Organizer","owner@example.test",encoder.encode("password123"),"","ADMIN"));
        member=users.save(new UserRegistration(null,"Member","member@example.test",encoder.encode("password123"),"","USER"));
        event=new Event(); event.setName("Workshop");event.setCategory("Technology");event.setLocation("Studio");event.setEventDate(LocalDate.now().plusDays(2));event.setEventTime(LocalTime.NOON);event.setTotalTickets(3);event.setOrganizer(owner);event=events.save(event);
    }
    @Test void anonymousResponsesAreJsonAndMutationsNeedCsrf() throws Exception {
        mvc.perform(get("/api/auth/me")).andExpect(status().isUnauthorized()).andExpect(content().contentTypeCompatibleWith("application/json"));
        mvc.perform(get("/api/events/upcoming")).andExpect(status().isOk()).andExpect(jsonPath("$[0].name").value("Workshop"));
        mvc.perform(post("/api/auth/register").contentType("application/json").content("{}")) .andExpect(status().isForbidden());
        mvc.perform(get("/user/dashboard")).andExpect(status().isUnauthorized());
    }
    @Test void browserCsrfLoginSessionRotationAndLogout() throws Exception {
        var tokenResponse=mvc.perform(get("/api/auth/csrf")).andExpect(status().isOk()).andReturn();
        var session=(MockHttpSession)tokenResponse.getRequest().getSession();
        String oldSession=session.getId();
        var token=mapper.readTree(tokenResponse.getResponse().getContentAsString());
        mvc.perform(post("/api/auth/login").session(session).header(token.get("headerName").asText(),token.get("token").asText()).param("username",member.getEmail()).param("password","password123")) .andExpect(status().isNoContent());
        assertNotEquals(oldSession,session.getId());
        mvc.perform(get("/api/auth/me").session(session)).andExpect(status().isOk()).andExpect(jsonPath("$.role").value("USER")).andExpect(jsonPath("$.password").doesNotExist());
        var renewed=mvc.perform(get("/api/auth/csrf").session(session)).andReturn();
        var fresh=mapper.readTree(renewed.getResponse().getContentAsString());
        mvc.perform(post("/api/auth/logout").session(session).header(fresh.get("headerName").asText(),fresh.get("token").asText())).andExpect(status().isNoContent());
        assertTrue(session.isInvalid());
    }
    @Test void registrationCannotAssignAdminAndProfilePreservesPassword() throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType("application/json").content("{\"name\":\"New member\",\"email\":\"new@example.test\",\"password\":\"password123\",\"role\":\"ADMIN\"}"))
            .andExpect(status().isCreated()).andExpect(jsonPath("$.role").value("USER")).andExpect(jsonPath("$.password").doesNotExist());
        String password=member.getPassword();
        mvc.perform(put("/api/profile").with(user(member.getEmail())).with(csrf()).contentType("application/json").content("{\"name\":\"Updated\",\"contactNumber\":\"123\",\"userId\":"+owner.getUserId()+",\"role\":\"ADMIN\"}")) .andExpect(status().isOk());
        var updated=users.findByEmail(member.getEmail());assertEquals("Updated",updated.getName());assertEquals("USER",updated.getRole());assertEquals(password,updated.getPassword());assertEquals("Organizer",users.findByEmail(owner.getEmail()).getName());
    }
    @Test void registrationRejectsInvalidOrDuplicateInput() throws Exception {
        mvc.perform(post("/api/auth/register").with(csrf()).contentType("application/json").content("{\"name\":\"\",\"email\":\"bad\",\"password\":\"x\"}")) .andExpect(status().isBadRequest());
        mvc.perform(post("/api/auth/register").with(csrf()).contentType("application/json").content("{\"name\":\"New\",\"email\":\"member@example.test\",\"password\":\"password123\"}")) .andExpect(status().isConflict());
    }
    @Test void bookingRejectsInvalidCountAndRestoresInventoryOnlyOnce() throws Exception {
        mvc.perform(post("/api/bookings").with(user(member.getEmail())).with(csrf()).contentType("application/json").content("{\"eventId\":"+event.getEventId()+",\"ticketCount\":0}")) .andExpect(status().isBadRequest());
        mvc.perform(post("/api/bookings").with(user(member.getEmail())).with(csrf()).contentType("application/json").content("{\"eventId\":"+event.getEventId()+",\"ticketCount\":4}")) .andExpect(status().isConflict());
        var result=mvc.perform(post("/api/bookings").with(user(member.getEmail())).with(csrf()).contentType("application/json").content("{\"eventId\":"+event.getEventId()+",\"ticketCount\":2}")) .andExpect(status().isCreated()).andReturn();
        long id=mapper.readTree(result.getResponse().getContentAsString()).get("bookingId").asLong();
        assertEquals(1,events.findById(event.getEventId()).orElseThrow().getTotalTickets());
        mvc.perform(delete("/api/bookings/"+id).with(user("other@example.test")).with(csrf())).andExpect(status().isForbidden());
        mvc.perform(delete("/api/bookings/"+id).with(user(member.getEmail())).with(csrf())).andExpect(status().isNoContent());
        mvc.perform(delete("/api/bookings/"+id).with(user(member.getEmail())).with(csrf())).andExpect(status().isNotFound());
        assertEquals(3,events.findById(event.getEventId()).orElseThrow().getTotalTickets());
    }
    @Test void concurrentBookingsCannotOversell() throws Exception {
        var pool=Executors.newFixedThreadPool(2);var start=new CountDownLatch(1);
        try {
            Callable<Boolean> task=()->{start.await();try{workflow.book(event.getEventId(),2,member.getEmail());return true;}catch(org.springframework.web.server.ResponseStatusException ex){assertEquals(409,ex.getStatusCode().value());return false;}};
            var a=pool.submit(task);var b=pool.submit(task);start.countDown();
            assertNotEquals(a.get(10,TimeUnit.SECONDS),b.get(10,TimeUnit.SECONDS));
            assertEquals(1,events.findById(event.getEventId()).orElseThrow().getTotalTickets());assertEquals(1,bookings.count());
        } finally {pool.shutdownNow();}
    }
    @Test void concurrentCancellationDoesNotDuplicateInventory() throws Exception {
        long id=workflow.book(event.getEventId(),2,member.getEmail()).getBookingId();
        var pool=Executors.newFixedThreadPool(2);var start=new CountDownLatch(1);
        try {
            Callable<Boolean> task=()->{start.await();try{workflow.cancel(id,member.getEmail());return true;}catch(org.springframework.web.server.ResponseStatusException ex){assertEquals(404,ex.getStatusCode().value());return false;}};
            var a=pool.submit(task);var b=pool.submit(task);start.countDown();assertNotEquals(a.get(10,TimeUnit.SECONDS),b.get(10,TimeUnit.SECONDS));
            assertEquals(3,events.findById(event.getEventId()).orElseThrow().getTotalTickets());
        } finally {pool.shutdownNow();}
    }
    @Test void adminAndReminderAccessIsScopedToOwner() throws Exception {
        mvc.perform(get("/api/admin/events").with(user(member.getEmail()))).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/events").with(user("other@example.test").roles("ADMIN"))).andExpect(jsonPath("$.length()").value(0));
        mvc.perform(post("/api/admin/events/"+event.getEventId()+"/reminders").with(user("other@example.test").roles("ADMIN")).with(csrf())).andExpect(status().isForbidden());
        mvc.perform(get("/api/admin/summary").with(user(owner.getEmail()).roles("ADMIN"))).andExpect(jsonPath("$.events").value(1));
        verifyNoInteractions(mail);
    }
    @Test void passwordResetIsSingleUseAndSessionBound() throws Exception {
        var session=new MockHttpSession();
        mvc.perform(post("/api/auth/password/request").session(session).with(csrf()).contentType("application/json").content("{\"email\":\"member@example.test\"}")) .andExpect(status().isAccepted());
        var capture=ArgumentCaptor.forClass(SimpleMailMessage.class);verify(mail).send(capture.capture());
        String code=capture.getValue().getText().replaceAll("[^0-9]", "").substring(0,6);
        String body="{\"otp\":\""+code+"\",\"newPassword\":\"new-password123\"}";
        mvc.perform(post("/api/auth/password/confirm").with(csrf()).contentType("application/json").content(body)).andExpect(status().isBadRequest());
        mvc.perform(post("/api/auth/password/confirm").session(session).with(csrf()).contentType("application/json").content(body)).andExpect(status().isNoContent());
        assertTrue(encoder.matches("new-password123",users.findByEmail(member.getEmail()).getPassword()));
        mvc.perform(post("/api/auth/password/confirm").session(session).with(csrf()).contentType("application/json").content(body)).andExpect(status().isBadRequest());
    }
}
