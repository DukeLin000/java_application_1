package org.example.javademo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 開發期：關 CSRF；未來用表單/Cookie 再開
                .csrf(csrf -> csrf.disable())

                // 關鍵：啟用 CORS（實際規則由 WebConfig#addCorsMappings 提供）
                .cors(Customizer.withDefaults())

                // 無狀態（搭配 Bearer/JWT 比方便；沒有也不影響）
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 路由授權（開發期全部放行；上線再收斂）
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/**",          // 你的 API
                                "/actuator/health", // 若有加 actuator
                                "/health"
                        ).permitAll()
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    // ⚠️ 不要在這裡宣告 CorsConfigurationSource Bean，避免覆蓋你既有的 WebConfig CORS 規則。
}
