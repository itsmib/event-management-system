package com.cts.EventManagementSystem.controller;

import java.util.Map;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.mail.MailException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResponseStatusException.class)
    ResponseEntity<Map<String,String>> status(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode()).body(Map.of("message", ex.getReason() == null ? "Request failed." : ex.getReason()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<Map<String,String>> validation(MethodArgumentNotValidException ex) {
        var error = ex.getBindingResult().getFieldErrors().get(0);
        return ResponseEntity.badRequest().body(Map.of("message", error.getField() + " " + error.getDefaultMessage()));
    }
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    ResponseEntity<Map<String,String>> upload() { return ResponseEntity.status(413).body(Map.of("message", "Image must be smaller than 5 MB.")); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    ResponseEntity<Map<String,String>> conflict() { return ResponseEntity.status(409).body(Map.of("message", "This change conflicts with an existing record.")); }
    @ExceptionHandler(org.springframework.dao.OptimisticLockingFailureException.class)
    ResponseEntity<Map<String,String>> changed() { return ResponseEntity.status(409).body(Map.of("message", "This event changed. Refresh and try again.")); }
    @ExceptionHandler(MailException.class)
    ResponseEntity<Map<String,String>> mail() { return ResponseEntity.status(503).body(Map.of("message", "Email is unavailable. Please try again later.")); }
}
