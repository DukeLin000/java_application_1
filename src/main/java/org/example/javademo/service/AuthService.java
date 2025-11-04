package org.example.javademo.service;

import org.example.javademo.dto.AuthResponse;
import org.example.javademo.dto.LoginRequest;
import org.example.javademo.dto.RefreshTokenRequest;
import org.example.javademo.dto.RegisterRequest;
import org.example.javademo.dto.UserDto;
import org.example.javademo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AuthService {

    private final PasswordEncoder encoder;
    private final JwtService jwt;

    // 簡易 In-Memory 使用者與 refresh token 儲存（P0-2 再換 JPA/DB）
    private final Map<String, User> usersByEmail = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);
    private final Map<String, String> refreshByEmail = new ConcurrentHashMap<>();

    public AuthService(PasswordEncoder encoder, JwtService jwt) {
        this.encoder = encoder;
        this.jwt = jwt;
    }

    public AuthResponse register(RegisterRequest req) {
        String email = req.getEmail().toLowerCase();
        if (usersByEmail.containsKey(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }
        String hash = encoder.encode(req.getPassword());
        User user = new User(idGen.getAndIncrement(), email, hash, req.getDisplayName(), List.of("USER"));
        usersByEmail.put(email, user);

        return buildTokens(user);
    }

    public AuthResponse login(LoginRequest req) {
        String email = req.getEmail().toLowerCase();
        User user = Optional.ofNullable(usersByEmail.get(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));
        if (!encoder.matches(req.getPassword(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }
        return buildTokens(user);
    }

    public AuthResponse refresh(RefreshTokenRequest req) {
        String token = req.getRefreshToken();
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
        }
        if (!jwt.isRefreshToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }
        String email = jwt.getSubject(token);
        String stored = refreshByEmail.get(email);
        if (stored == null || !stored.equals(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not recognized");
        }
        User user = Optional.ofNullable(usersByEmail.get(email))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
        // 只換 Access，Refresh 照舊（也可一併輪替）
        String newAccess = jwt.generateAccessToken(email, user.getRoles(), Map.of("uid", user.getId()));
        return new AuthResponse(newAccess, stored, toDto(user));
    }

    public void logout(RefreshTokenRequest req) {
        if (req != null && req.getRefreshToken() != null && !req.getRefreshToken().isBlank()) {
            String email = jwt.getSubject(req.getRefreshToken());
            refreshByEmail.remove(email);
        }
    }

    private AuthResponse buildTokens(User user) {
        String access = jwt.generateAccessToken(user.getEmail(), user.getRoles(), Map.of("uid", user.getId()));
        String refresh = jwt.generateRefreshToken(user.getEmail());
        refreshByEmail.put(user.getEmail(), refresh);
        return new AuthResponse(access, refresh, toDto(user));
    }

    private UserDto toDto(User u) {
        return new UserDto(u.getId(), u.getEmail(), u.getDisplayName());
    }
}
