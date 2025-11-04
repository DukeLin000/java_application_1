package org.example.javademo.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JwtService {
    private final Algorithm algorithm;
    private final long expMinutes;
    private final long refreshExpMinutes;

    public JwtService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.expMinutes:60}") long expMinutes,
            @Value("${jwt.refreshExpMinutes:43200}") long refreshExpMinutes
    ) {
        this.algorithm = Algorithm.HMAC256(secret);
        this.expMinutes = expMinutes;
        this.refreshExpMinutes = refreshExpMinutes;
    }

    public String generateAccessToken(String subject, List<String> roles, Map<String, Object> extraClaims) {
        Instant now = Instant.now();
        var builder = JWT.create()
                .withSubject(subject)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(Duration.ofMinutes(expMinutes))));

        if (roles != null) builder.withClaim("roles", roles);

        if (extraClaims != null && !extraClaims.isEmpty()) {
            // 避免覆蓋標準註冊宣告
            var filtered = new java.util.HashMap<>(extraClaims);
            filtered.remove("sub"); filtered.remove("exp"); filtered.remove("iat");
            filtered.remove("nbf"); filtered.remove("iss"); filtered.remove("aud");
            filtered.remove("jti");
            builder.withPayload(filtered);
        }
        return builder.sign(algorithm);
    }

    public String generateRefreshToken(String subject) {
        var now = Instant.now();
        return JWT.create()
                .withSubject(subject)
                .withIssuedAt(Date.from(now))
                .withExpiresAt(Date.from(now.plus(Duration.ofMinutes(refreshExpMinutes))))
                .withClaim("typ", "refresh")
                .sign(algorithm);
    }

    public DecodedJWT verify(String token) { return JWT.require(algorithm).build().verify(token); }
    public String getSubject(String token) { return verify(token).getSubject(); }
    public boolean isRefreshToken(String token) {
        var c = verify(token).getClaim("typ");
        return !c.isNull() && "refresh".equalsIgnoreCase(c.asString());
    }
}
