package com.cts.EventManagementSystem.controller;

import com.cts.EventManagementSystem.model.UserRegistration;
import com.cts.EventManagementSystem.repository.UserRegistrationRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.security.Principal;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api")
public class AccountRestController {
    private final UserRegistrationRepository users;
    private final BCryptPasswordEncoder encoder;
    private final JavaMailSender mail;
    public AccountRestController(UserRegistrationRepository users, BCryptPasswordEncoder encoder, JavaMailSender mail) {
        this.users = users; this.encoder = encoder; this.mail = mail;
    }
    public record Profile(Long userId, String name, String email, String contactNumber, String role) {
        static Profile from(UserRegistration u) { return new Profile(u.getUserId(), u.getName(), u.getEmail(), u.getContactNumber(), u.getRole()); }
    }
    public record Registration(@NotBlank @Size(max=100) String name, @NotBlank @Email @Size(max=254) String email,
        @NotBlank @Size(min=8,max=72) String password, @Size(max=30) String contactNumber) {}
    public record ProfileUpdate(@NotBlank @Size(max=100) String name, @Size(max=30) String contactNumber) {}
    public record ResetRequest(@NotBlank @Email String email) {}
    public record ResetConfirm(@NotBlank String otp, @NotBlank @Size(min=8,max=72) String newPassword) {}
    private record Challenge(String email, String code, Instant expires, int attempts) implements java.io.Serializable {}

    @GetMapping("/auth/csrf")
    public Map<String,String> csrf(CsrfToken token) { return Map.of("token", token.getToken(), "headerName", token.getHeaderName()); }
    @GetMapping("/auth/me")
    public Profile me(Principal principal) { return Profile.from(current(principal)); }
    @PostMapping("/auth/register") @ResponseStatus(HttpStatus.CREATED)
    public Profile register(@Valid @RequestBody Registration body) {
        String email = body.email().trim().toLowerCase(java.util.Locale.ROOT);
        if (users.findByEmail(email) != null) throw new ResponseStatusException(HttpStatus.CONFLICT, "This email is already registered.");
        var user = new UserRegistration();
        user.setName(body.name().trim()); user.setEmail(email); user.setPassword(encoder.encode(body.password()));
        user.setContactNumber(body.contactNumber()); user.setRole("USER");
        return Profile.from(users.save(user));
    }
    @PutMapping("/profile")
    public Profile update(@Valid @RequestBody ProfileUpdate body, Principal principal) {
        var user = current(principal);
        user.setName(body.name().trim()); user.setContactNumber(body.contactNumber());
        return Profile.from(users.save(user));
    }
    @PostMapping("/auth/password/request") @ResponseStatus(HttpStatus.ACCEPTED)
    public void requestReset(@Valid @RequestBody ResetRequest body, HttpSession session) {
        synchronized (session) {
            Instant now = Instant.now();
            Instant last = (Instant)session.getAttribute("resetRequestedAt");
            if (last != null && last.plusSeconds(60).isAfter(now)) throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "Wait a minute before requesting another code.");
            session.setAttribute("resetRequestedAt", now);
            session.removeAttribute("passwordReset");
            String email = body.email().trim().toLowerCase(java.util.Locale.ROOT);
            if (users.findByEmail(email) == null) return;
            String code = String.format("%06d", new SecureRandom().nextInt(1000000));
            var message = new SimpleMailMessage();
            message.setTo(email); message.setSubject("Reset your Event Management password");
            message.setText("Your reset code is " + code + ". It expires in 10 minutes.");
            mail.send(message);
            session.setAttribute("passwordReset", new Challenge(email, code, now.plusSeconds(600), 0));
        }
    }
    @PostMapping("/auth/password/confirm") @ResponseStatus(HttpStatus.NO_CONTENT)
    public void confirmReset(@Valid @RequestBody ResetConfirm body, HttpSession session) {
        synchronized (session) {
            Challenge challenge = (Challenge)session.getAttribute("passwordReset");
            if (challenge == null || challenge.expires().isBefore(Instant.now()) || challenge.attempts() >= 5) {
                session.removeAttribute("passwordReset");
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Code expired. Request a new code.");
            }
            session.setAttribute("passwordReset", new Challenge(challenge.email(), challenge.code(), challenge.expires(), challenge.attempts()+1));
            if (!java.security.MessageDigest.isEqual(challenge.code().getBytes(java.nio.charset.StandardCharsets.UTF_8), body.otp().getBytes(java.nio.charset.StandardCharsets.UTF_8)))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid code.");
            var user = users.findByEmail(challenge.email());
            user.setPassword(encoder.encode(body.newPassword())); users.save(user);
            session.removeAttribute("passwordReset");
        }
    }
    private UserRegistration current(Principal principal) {
        var user = users.findByEmail(principal.getName());
        if (user == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Please sign in again.");
        return user;
    }
}
