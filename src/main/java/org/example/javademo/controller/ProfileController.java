package org.example.javademo.controller;

import org.example.javademo.dto.UserProfileDto;
import org.example.javademo.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
                        "GET  /api/profile/me",
                        "PUT  /api/profile/{id}"
                )
        );
    }

    /** 健康檢查 */
    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> ping() {
        return Map.of("ok", true, "ts", System.currentTimeMillis());
    }

    /** 建立/更新目前登入者的 Profile（upsert），回 201 並帶 Location */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> save(@Valid @RequestBody UserProfileDto req,
                                                    Authentication auth) {
        String email = (auth != null) ? (String) auth.getPrincipal() : null; // JwtAuthenticationFilter 設為 Email
        var body = profileService.saveForUser(email, req);
        String id = String.valueOf(body.get("id"));
        return ResponseEntity.created(URI.create("/api/profile/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    /** 回讀指定 id，但必須是「本人」的 Profile（避免越權） */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileDto get(@PathVariable Long id, Authentication auth) {
        String email = (auth != null) ? (String) auth.getPrincipal() : null;
        return profileService.getByIdForOwner(email, id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
    }

    /** 回讀目前登入者的 Profile（不存在則 404，走全域錯誤格式） */
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileDto me(Authentication auth) {
        String email = (auth != null) ? (String) auth.getPrincipal() : null;
        return profileService.getMine(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found"));
    }

    /** 更新指定 id（僅本人；部分更新：只覆蓋非 null 欄位） */
    @PutMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileDto update(@PathVariable Long id,
                                 @Valid @RequestBody UserProfileDto req,
                                 Authentication auth) {
        String email = (auth != null) ? (String) auth.getPrincipal() : null;
        return profileService.update(email, id, req);
    }
}
