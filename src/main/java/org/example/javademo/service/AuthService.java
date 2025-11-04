package org.example.javademo.service;

import org.example.javademo.domain.User;
import org.example.javademo.dto.*;
import org.example.javademo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@Service
public class AuthService {
    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final UserRepository users;

    // 先暫存 refresh（之後可換 DB 表）
    private final java.util.concurrent.ConcurrentHashMap<String,String> refreshByEmail = new java.util.concurrent.ConcurrentHashMap<>();

    public AuthService(PasswordEncoder encoder, JwtService jwt, UserRepository users) {
        this.encoder = encoder; this.jwt = jwt; this.users = users;
    }

    public AuthResponse register(RegisterRequest req) {
        String email = req.getEmail().toLowerCase();
        if (users.existsByEmail(email))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        User u = new User(email, encoder.encode(req.getPassword()), req.getDisplayName(), "USER");
        users.save(u);
        return buildTokens(u);
    }

    public AuthResponse login(LoginRequest req) {
        var u = users.findByEmail(req.getEmail().toLowerCase())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!encoder.matches(req.getPassword(), u.getPasswordHash()))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        return buildTokens(u);
    }

    public AuthResponse refresh(RefreshTokenRequest req) {
        String token = req.getRefreshToken();
        if (token == null || token.isBlank())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
        if (!jwt.isRefreshToken(token))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        String email = jwt.getSubject(token);
        String stored = refreshByEmail.get(email);
        if (stored == null || !stored.equals(token))
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not recognized");
        User u = users.findByEmail(email).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        String newAccess = jwt.generateAccessToken(email, java.util.List.of("USER"), Map.of("uid", u.getId()));
        return new AuthResponse(newAccess, stored, new UserDto(u.getId(), u.getEmail(), u.getDisplayName()));
    }

    public void logout(RefreshTokenRequest req) {
        if (req != null && req.getRefreshToken() != null && !req.getRefreshToken().isBlank()) {
            String email = jwt.getSubject(req.getRefreshToken());
            refreshByEmail.remove(email);
        }
    }

    private AuthResponse buildTokens(User u) {
        String access = jwt.generateAccessToken(u.getEmail(), java.util.List.of("USER"), Map.of("uid", u.getId()));
        String refresh = jwt.generateRefreshToken(u.getEmail());
        refreshByEmail.put(u.getEmail(), refresh);
        return new AuthResponse(access, refresh, new UserDto(u.getId(), u.getEmail(), u.getDisplayName()));
    }
}
