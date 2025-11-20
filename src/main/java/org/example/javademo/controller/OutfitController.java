package org.example.javademo.controller;

import org.example.javademo.dto.OutfitDto;
import org.example.javademo.service.OutfitService;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping("/api/outfits")
@Validated
public class OutfitController {

    private final OutfitService service;

    public OutfitController(OutfitService service) { this.service = service; }

    @PostMapping
    public OutfitDto create(@Valid @RequestBody OutfitDto req, Authentication auth) {
        // 從 Authentication 取得目前登入者的 email
        String email = (String) auth.getPrincipal();
        return service.create(email, req);
    }

    @GetMapping
    public List<OutfitDto> list(Authentication auth) {
        String email = (String) auth.getPrincipal();
        return service.list(email);
    }

    @GetMapping("/{id}")
    public OutfitDto get(@PathVariable("id") @Min(1) long id, Authentication auth) {
        String email = (String) auth.getPrincipal();
        return service.get(email, id);
    }
}