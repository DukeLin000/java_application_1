package org.example.javademo.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.javademo.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String[] WHITELIST_PREFIX = {
            "/files/",          // 圖片公開
            "/h2-console",      // H2 console
            "/swagger-ui",      // Swagger UI
            "/v3/api-docs",     // OpenAPI
            "/api/auth"         // 登入/註冊/refresh
    };

    private final JwtService jwt;

    public JwtAuthenticationFilter(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        final String path = req.getRequestURI();

        // 1) CORS Preflight 直通
        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            chain.doFilter(req, res);
            return;
        }

        // 2) 白名單路徑直通（與 SecurityConfig 的 permitAll 對應）
        for (String p : WHITELIST_PREFIX) {
            if (path.startsWith(p)) {
                chain.doFilter(req, res);
                return;
            }
        }

        // 3) 驗證 Bearer token（沒有就交給授權規則處理）
        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                DecodedJWT decoded = jwt.verify(token);
                String subject = decoded.getSubject();                // 你的 email
                var rolesClaim = decoded.getClaim("roles");
                List<SimpleGrantedAuthority> authorities =
                        rolesClaim.isNull()
                                ? List.of()
                                : rolesClaim.asList(String.class).stream()
                                .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                                .collect(Collectors.toList());

                var authentication =
                        new UsernamePasswordAuthenticationToken(subject, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // 無效 token：不設 Authentication，讓後續授權規則決定（受保護路徑會 401）
                SecurityContextHolder.clearContext();
            }
        }

        chain.doFilter(req, res);
    }
}
