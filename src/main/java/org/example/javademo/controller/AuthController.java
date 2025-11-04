package org.example.javademo.controller;

import jakarta.validation.Valid;
import org.example.javademo.dto.AuthResponse;
import org.example.javademo.dto.LoginRequest;
import org.example.javademo.dto.RefreshTokenRequest;
import org.example.javademo.dto.RegisterRequest;
import org.example.javademo.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService auth;

    public AuthController(AuthService auth) {
        this.auth = auth;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(auth.register(req));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(auth.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        return ResponseEntity.ok(auth.refresh(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest req) {
        auth.logout(req);
        return ResponseEntity.noContent().build();
    }
}
