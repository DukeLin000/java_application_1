package org.example.javademo.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import org.example.javademo.domain.RefreshToken;
import org.example.javademo.domain.User;
import org.example.javademo.dto.*;
import org.example.javademo.repository.RefreshTokenRepository;
import org.example.javademo.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AuthService {

    private final PasswordEncoder encoder;
    private final JwtService jwt;
    private final UserRepository users;
    private final RefreshTokenRepository refreshTokens;

    public AuthService(PasswordEncoder encoder,
                       JwtService jwt,
                       UserRepository users,
                       RefreshTokenRepository refreshTokens) {
        this.encoder = encoder;
        this.jwt = jwt;
        this.users = users;
        this.refreshTokens = refreshTokens;
    }

    // ========= Public APIs =========

    public AuthResponse register(RegisterRequest req) {
        String email = normalize(req.getEmail());
        if (users.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User u = new User(email, encoder.encode(req.getPassword()), req.getDisplayName(), "USER");
        u = users.save(u);

        return issueTokens(u);
    }

    public AuthResponse login(LoginRequest req) {
        String email = normalize(req.getEmail());

        User u = users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!encoder.matches(req.getPassword(), u.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return issueTokens(u);
    }

    public AuthResponse refresh(RefreshTokenRequest req) {
        String token = req.getRefreshToken();
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
        }
        if (!jwt.isRefreshToken(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid refresh token");
        }

        // 先驗簽與解碼
        DecodedJWT decoded = jwt.verify(token);
        String email = normalize(decoded.getSubject());

        // 驗 DB 是否存在、未撤銷、未過期
        RefreshToken rt = refreshTokens.findByToken(token)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token not found"));

        if (rt.isRevoked() || rt.getExpiresAt() < System.currentTimeMillis()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Refresh token expired or revoked");
        }

        User u = users.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));

        // P0：先不旋轉 refresh（保留相同 refresh）。如要旋轉可在此撤銷舊的並發新 refresh。
        String newAccess = jwt.generateAccessToken(u.getEmail(), rolesFromCsv(u.getRoles()), Map.of("uid", u.getId()));
        return new AuthResponse(newAccess, token, new UserDto(u.getId(), u.getEmail(), u.getDisplayName()));
    }

    public void logout(RefreshTokenRequest req) {
        String token = (req == null) ? null : req.getRefreshToken();
        if (token == null || token.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Refresh token is required");
        }

        refreshTokens.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokens.save(rt);
        });
    }

    // ========= Helpers =========

    private AuthResponse issueTokens(User u) {
        List<String> roles = rolesFromCsv(u.getRoles());

        String access = jwt.generateAccessToken(u.getEmail(), roles, Map.of("uid", u.getId()));
        String refresh = jwt.generateRefreshToken(u.getEmail());

        // 解析 refresh 到期時間並存 DB（可支援多裝置登入，這裡不強制唯一）
        long expMillis = jwt.verify(refresh).getExpiresAt().getTime();

        RefreshToken rt = new RefreshToken();
        rt.setUser(u);
        rt.setToken(refresh);
        rt.setExpiresAt(expMillis);
        rt.setRevoked(false);
        refreshTokens.save(rt);

        return new AuthResponse(access, refresh, new UserDto(u.getId(), u.getEmail(), u.getDisplayName()));
    }

    private String normalize(String email) {
        if (email == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email required");
        return email.trim().toLowerCase();
    }

    private List<String> rolesFromCsv(String csv) {
        if (csv == null || csv.isBlank()) return List.of();
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toUpperCase)
                .distinct()
                .toList();
    }
}
