package org.example.javademo.controller;

import org.example.javademo.dto.UserProfileDto;
import org.example.javademo.service.ProfileService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
@CrossOrigin( // 開發期保險用；之後可移除改用全域 CORS
        origins = "*",
        allowedHeaders = {"*"},
        exposedHeaders = {"Location"},
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS },
        allowCredentials = "false",
        maxAge = 3600
)
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    /** 防呆：有人打 GET /api/profile 就回可用端點，避免 500 */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> index() {
        return Map.of(
                "service", "profile",
                "endpoints", List.of(
                        "GET  /api/profile/ping",
                        "POST /api/profile",
                        "GET  /api/profile/{id}"
                )
        );
    }

    /** 健康檢查（前端 preflight/ping 會打這裡） */
    @GetMapping(value = "/ping", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> ping() {
        return Map.of("ok", true, "ts", System.currentTimeMillis());
    }

    /** 建立/儲存使用者量身資料（回 201 並含 Location） */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, Object>> save(@Valid @RequestBody UserProfileDto req) {
        Map<String, Object> body = profileService.save(req);
        String id = String.valueOf(body.get("id")); // 例如 P-1
        return ResponseEntity
                .created(URI.create("/api/profile/" + id))
                .contentType(MediaType.APPLICATION_JSON)
                .body(body);
    }

    /** 回讀：支援 /api/profile/P-1 或 /api/profile/1 */
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public UserProfileDto get(@PathVariable String id) {
        final long key;
        try {
            key = id.startsWith("P-") ? Long.parseLong(id.substring(2)) : Long.parseLong(id);
        } catch (NumberFormatException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid id format: " + id);
        }
        return profileService.get(key)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Profile not found: " + id));
    }

    // ⚠️ 不要自訂 OPTIONS；交給 Spring 的 CORS Filter 處理即可
}
