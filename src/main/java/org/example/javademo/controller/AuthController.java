package org.example.javademo.controller;

import org.example.javademo.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest req) {
        // 假資料回傳
        String access = "dummy-access-" + UUID.randomUUID();
        String refresh = "dummy-refresh-" + UUID.randomUUID();
        UserDto user = new UserDto(1L, req.getEmail(), req.getDisplayName());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new AuthResponse(access, refresh, user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest req) {
        String access = "dummy-access-" + UUID.randomUUID();
        String refresh = "dummy-refresh-" + UUID.randomUUID();
        UserDto user = new UserDto(1L, req.getEmail(), "User");
        return ResponseEntity.ok(new AuthResponse(access, refresh, user));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshTokenRequest req) {
        String access = "dummy-access-" + UUID.randomUUID();
        String refresh = "dummy-refresh-" + UUID.randomUUID();
        // 假設用戶資訊固定
        UserDto user = new UserDto(1L, "user@example.com", "User");
        return ResponseEntity.ok(new AuthResponse(access, refresh, user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@Valid @RequestBody RefreshTokenRequest req) {
        // Sprint 0 無狀態，直接回 204
        return ResponseEntity.noContent().build();
    }
}
