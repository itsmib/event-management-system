package com.cts.EventManagementSystem.service;

import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class BookingConfirmationListener {
    private final JavaMailSender mail;
    public BookingConfirmationListener(JavaMailSender mail) { this.mail = mail; }
    @TransactionalEventListener
    public void confirmed(BookingWorkflow.Confirmation booking) {
        for (String recipient : new String[]{booking.attendeeEmail(), booking.organizerEmail()}) {
            try {
                var message = new SimpleMailMessage();
                message.setTo(recipient); message.setSubject("Booking confirmed: " + booking.eventName());
                message.setText("Booking #" + booking.bookingId() + " confirmed for " + booking.tickets() + " ticket(s) at " + booking.eventName() + ".");
                mail.send(message);
            } catch (MailException ex) {
                // The committed reservation must not look failed and cause the attendee to book again.
                LoggerFactory.getLogger(getClass()).warn("Confirmation email failed for booking {}", booking.bookingId());
            }
        }
    }
}
