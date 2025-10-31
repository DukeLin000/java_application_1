package org.example.javademo.controller;

import org.example.javademo.dto.OutfitDto;
import org.example.javademo.service.OutfitService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.Collection;

@RestController
@RequestMapping("/api/outfits")
@Validated
public class OutfitController {

    private final OutfitService service;

    public OutfitController(OutfitService service) { this.service = service; }

    @PostMapping
    public OutfitDto create(@Valid @RequestBody OutfitDto req) {
        return service.create(req);
    }

    @GetMapping
    public Collection<OutfitDto> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    public OutfitDto get(@PathVariable("id") @Min(1) long id) {
        return service.get(id);
    }
}
