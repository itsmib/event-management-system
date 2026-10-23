package com.cts.EventManagementSystem.config;

import com.cts.EventManagementSystem.model.*;
import com.cts.EventManagementSystem.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import java.time.*;

/** Explicit opt-in fixtures for the disposable local H2 profile only. */
@Configuration
@Profile("local")
@ConditionalOnProperty(name="app.demo-data", havingValue="true")
public class LocalDemoData {
    @Bean
    CommandLineRunner demoData(UserRegistrationRepository users, EventRepository events, BCryptPasswordEncoder encoder,
        @Value("${app.demo-password}") String password) {
        return args -> {
            if (users.count() != 0) return;
            if (password.length() < 8) throw new IllegalArgumentException("Demo password must have at least 8 characters");
            var organizer = users.save(new UserRegistration(null,"Alex Morgan","organizer@example.test",encoder.encode(password),"","ADMIN"));
            users.save(new UserRegistration(null,"Jamie Lee","member@example.test",encoder.encode(password),"","USER"));
            String[][] samples={{"The Sunday listening room","Music","The Greenhouse","An intimate afternoon of live music, new discoveries, and good company."},
                {"Build something together","Technology","Studio 04","Bring an idea and leave with a new perspective. A hands-on session for curious minds."},
                {"Clay, coffee & conversation","Workshops","The Makers Studio","Slow down, get your hands a little messy, and make something you can call your own."}};
            for(int i=0;i<samples.length;i++) {
                var event=new Event();event.setName(samples[i][0]);event.setCategory(samples[i][1]);event.setLocation(samples[i][2]);event.setDescription(samples[i][3]);
                event.setOrganizer(organizer);event.setEventDate(LocalDate.now().plusDays(5+i*3));event.setEventTime(LocalTime.of(14+i,0));event.setTotalTickets(30+i*12);events.save(event);
            }
        };
    }
}
