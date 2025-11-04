package org.example.javademo.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.javademo.service.JwtService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 驗證 Authorization: Bearer <token>，
 * 驗證通過後把 Authentication 放到 SecurityContext。
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwt;

    public JwtAuthenticationFilter(JwtService jwt) {
        this.jwt = jwt;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws ServletException, IOException {
        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);

        if (auth != null && auth.startsWith("Bearer ")) {
            String token = auth.substring(7);
            try {
                DecodedJWT decoded = jwt.verify(token);
                String subject = decoded.getSubject();
                var rolesClaim = decoded.getClaim("roles");
                var authorities = rolesClaim.isNull()
                        ? java.util.List.<SimpleGrantedAuthority>of()
                        : rolesClaim.asList(String.class).stream()
                        .map(r -> new SimpleGrantedAuthority("ROLE_" + r))
                        .collect(Collectors.toList());

                var authentication =
                        new UsernamePasswordAuthenticationToken(subject, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                // 無效 token：不設 Authentication，讓後續授權規則去擋
            }
        }

        chain.doFilter(req, res);
    }
}
