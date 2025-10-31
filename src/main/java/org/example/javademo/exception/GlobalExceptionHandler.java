package org.example.javademo.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.ConstraintViolationException;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> notFound(NoSuchElementException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error","NOT_FOUND","message",ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> badRequest(MethodArgumentNotValidException ex) {
        var details = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField()+": "+fe.getDefaultMessage()).collect(Collectors.toList());
        return ResponseEntity.badRequest()
                .body(Map.of("error","VALIDATION_ERROR","details",details));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> constraint(ConstraintViolationException ex) {
        var details = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath()+": "+v.getMessage()).collect(Collectors.toList());
        return ResponseEntity.badRequest()
                .body(Map.of("error","CONSTRAINT_VIOLATION","details",details));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> other(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error","INTERNAL_ERROR","message",ex.getMessage()));
    }
}
