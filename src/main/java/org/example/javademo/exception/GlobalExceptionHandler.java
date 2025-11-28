package org.example.javademo.exception;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // ✅ 務必引入這個
import jakarta.validation.ConstraintViolationException;

import java.util.*;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // ✅ 新增：專門處理 ResponseStatusException (例如 401 Unauthorized, 404 Not Found)
    // 這樣 AuthService 拋出的錯誤才能正確傳給前端，而不會被當成 500
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<?> handleResponseStatus(ResponseStatusException ex) {
        return ResponseEntity.status(ex.getStatusCode())
                .body(Map.of(
                        "error", ex.getStatusCode().toString(),
                        "message", ex.getReason() != null ? ex.getReason() : "Error"
                ));
    }

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
        ex.printStackTrace(); // 印出錯誤堆疊，方便開發除錯
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error","INTERNAL_ERROR","message", ex.getMessage() != null ? ex.getMessage() : "Unknown error"));
    }
}