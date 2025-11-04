package org.example.javademo.controller;

import org.example.javademo.dto.UserProfileDto;
import org.example.javademo.service.ProfileService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/profile")
@Validated
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /** GET /api/profile → 提示可用端點（防呆） */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> index() {
        return Map.of(
                "service", "profile",
                "endpoints", List.of(
                        "GET  /api/profile/ping",
                        "POST /api/profile",
                        "GET  /api/profile/{id}",
                        "GET  /api/profile/me"
                )
        );
    }

    /** 健康檢查 */
    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> ping() {
        return Map.of("ok", true, "ts", System.currentTimeMillis());
    }

    /** 建立/更新目前登入者的 Profile，回 201 並帶 Location */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> save(@Valid @RequestBody UserProfileDto req,
                                                    Authentication auth) {
        String email = (auth != null) ? (String) auth.getPrincipal() : null; // JwtAuthenticationFilter 設的 principal=Email
        var body = profileService.saveForUser(email, req);
        String id = String.valueOf(body.get("id"));
        return ResponseEntity.created(URI.create("/api/profile/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    /** 回讀：/api/profile/{id} */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfileDto> get(@PathVariable Long id) {
        return profileService.get(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /** 回讀目前登入者的 Profile（方便前端） */
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserProfileDto> me(Authentication auth) {
        String email = (auth != null) ? (String) auth.getPrincipal() : null;
        return profileService.getByUserEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
